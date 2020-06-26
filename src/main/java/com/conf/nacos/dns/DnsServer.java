/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.conf.nacos.dns;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;
import java.util.concurrent.Executor;

import com.alibaba.nacos.common.executor.ExecutorFactory;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.conf.nacos.dns.constants.Code;
import com.conf.nacos.dns.constants.Constants;
import com.conf.nacos.dns.exception.NacosDnsException;
import com.conf.nacos.dns.pojo.InstanceRecord;
import com.conf.nacos.dns.utils.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Message;
import org.xbill.DNS.NULLRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

/**
 * DNS server
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class DnsServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DnsServer.class);

	private final NacosDnsCore nacosDnsCore;

	private final String backendDnsServer;

	private final Executor executor = ExecutorFactory.newFixExecutorService(
			DnsServer.class.getCanonicalName(),
			Runtime.getRuntime().availableProcessors(),
			new NameThreadFactory("com.conf.nacos.dns.worker"));

	private final int bufferSize;

	private final ThreadLocal<ByteBuffer> bufferPool;

	private DatagramChannel serverChannel;

	private Selector selector = null;

	private boolean perfIPv6 = false;

	private volatile boolean shutdown = false;

	private DnsServer(NacosDnsConfig config) throws NacosDnsException {
		try {
			long startTime = System.currentTimeMillis();
			this.bufferSize = config.getBufferSize();
			this.bufferPool = ThreadLocal
					.withInitial(() -> ByteBuffer.allocate(bufferSize));
			this.backendDnsServer = config.getBackendDns();
			this.init();
			this.perfIPv6 = config.isPerfIPv6();
			nacosDnsCore = new NacosDnsCore(config);
			LOGGER.info("dns-server already initialized, spend {} ms",
					(System.currentTimeMillis() - startTime));
		}
		catch (Throwable ex) {
			throw new NacosDnsException(Code.CREATE_DNS_SERVER_FAILED, ex);
		}
	}

	public static DnsServer create(NacosDnsConfig config) throws NacosDnsException {
		return new DnsServer(config);
	}

	private void init() {
		AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
			try {
				// 192.168.31.1
				selector = Selector.open();
				serverChannel = DatagramChannel.open();
				serverChannel.socket()
						.bind(new InetSocketAddress("127.0.0.1", Constants.DNS_PORT));
				serverChannel.configureBlocking(false);
				serverChannel.register(selector, SelectionKey.OP_READ);
			}
			catch (Throwable ex) {
				throw new NacosDnsException(Code.CREATE_DNS_SERVER_FAILED, ex);
			}
			return null;
		});
	}

	public void start() {
		final ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		for (;;) {

			if (shutdown) {
				return;
			}

			try {
				selector.select();
				for (SelectionKey key : selector.selectedKeys()) {
					if (key.isReadable()) {
						buffer.clear();
						SocketAddress client = serverChannel.receive(buffer);
						buffer.flip();
						byte[] requestData = new byte[buffer.limit()];
						buffer.get(requestData, 0, requestData.length);
						executor.execute(() -> handler(requestData, client));
					}
				}
			}
			catch (Throwable ex) {
				LOGGER.error("handler client request has error : {}",
						ExceptionUtil.getStackTrace(ex));
			}
		}
	}

	private void handler(final byte[] data, final SocketAddress client) {
		final ByteBuffer buffer = bufferPool.get();
		buffer.clear();
		try {
			final Message message = new Message(data);
			final Record question = message.getQuestion();
			final String domain = question.getName().toString();
			final Optional<Message> result = findFromNacos(message.clone(), question,
					domain);
			Message resp = result.orElseGet(
					() -> findRecordFromBackend(data, message.clone(), question));
			buffer.put(resp.toWire());
			buffer.flip();
			serverChannel.send(buffer, client);
		}
		catch (Throwable ex) {
			LOGGER.error("response to client has error : {}",
					ExceptionUtil.getStackTrace(ex));
		}
		finally {
			bufferPool.set(buffer);
		}
	}

	private Optional<Message> findFromNacos(final Message message, final Record request,
			final String domain) {
		Optional<InstanceRecord> optional = nacosDnsCore.selectOne(domain);
		if (!optional.isPresent()) {
			return Optional.empty();
		}
		InstanceRecord record = optional.get();
		final String ip = record.getIp();
		Record r = null;
		InetSocketAddress address = new InetSocketAddress(record.getIp(),
				record.getPort());
		if (perfIPv6 && IPUtils.isIPv6(ip)) {
			r = new AAAARecord(request.getName(), request.getDClass(), request.getTTL(),
					address.getAddress());
		}
		else {
			r = new ARecord(request.getName(), request.getDClass(), request.getTTL(),
					address.getAddress());
		}
		message.addRecord(r, Section.ANSWER);
		return Optional.of(message);
	}

	private Message findRecordFromBackend(final byte[] data, final Message message,
			final Record request) {
		try {
			DatagramChannel channel = DatagramChannel.open();
			channel.configureBlocking(true);
			channel.connect(new InetSocketAddress(backendDnsServer, Constants.DNS_PORT));

			ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
			buffer.put(data);
			buffer.flip();
			channel.write(buffer);
			buffer.clear();

			channel.receive(buffer);
			buffer.flip();
			byte[] receive = new byte[buffer.limit()];
			buffer.get(receive, 0, receive.length);
			return new Message(receive);
		}
		catch (Throwable ex) {
			LOGGER.warn("Domain name resolution failed through upper DNS Server : {}",
					ExceptionUtil.getStackTrace(ex));
			message.addRecord(NULLRecord.newRecord(request.getName(), request.getType(),
					request.getDClass()), Section.ANSWER);
			return message;
		}

	}

	public void shutdown() throws Exception {
		shutdown = true;
		nacosDnsCore.shutdown();
		selector.close();
		serverChannel.close();
	}
}
