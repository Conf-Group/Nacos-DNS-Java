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

import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.conf.nacos.dns.constants.Code;
import com.conf.nacos.dns.exception.NacosDnsException;
import com.conf.nacos.dns.pojo.InstanceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Message;
import org.xbill.DNS.NULLRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class DnsServer extends Thread {

	private static final ThreadLocal<ByteBuffer> THREAD_LOCAL = ThreadLocal.withInitial(() -> ByteBuffer.allocate(1024));

	private static final Logger LOGGER = LoggerFactory.getLogger(DnsServer.class);

	private static NacosDnsCore nacosDnsCore;

	private static DatagramChannel serverChannel;
	private static Selector selector = null;

	private static final Executor executor = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors(), new ThreadFactory() {

				AtomicInteger workerId = new AtomicInteger();

				@Override
				public Thread newThread(Runnable r) {
					final Thread t = new Thread(r);
					t.setName("com.conf.nacos.dns.worker-" + workerId.getAndIncrement());
					t.setDaemon(true);
					return t;
				}
			});

	public DnsServer() throws NacosDnsException {
		try {
			init();
			nacosDnsCore = new NacosDnsCore();
		} catch (Throwable ex) {
			throw new NacosDnsException(Code.CREATE_DNS_SERVER_FAILED, ex);
		}
	}

	private void init() throws Exception {
		AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
			try {
				// 192.168.31.1
				selector = Selector.open();
				serverChannel = DatagramChannel.open();
				serverChannel.socket().bind(new InetSocketAddress("127.0.0.1", 53));
				serverChannel.configureBlocking(false);
				serverChannel.register(selector, SelectionKey.OP_READ);
				System.out.println("finished dns-server open");
			} catch (Throwable ex) {
				throw new NacosDnsException(Code.CREATE_DNS_SERVER_FAILED, ex);
			}
			return null;
		});
	}

	@Override
	public void run() {
		LOGGER.info("DNS-Server starting");
		System.out.println("DNS-Server starting");

		final ByteBuffer buffer = ByteBuffer.allocate(1024);

		for ( ; ; ) {
			try {
				selector.select();
				for (SelectionKey key : selector.selectedKeys()) {
					if (key.isReadable()) {
						buffer.clear();
						SocketAddress client = serverChannel.receive(buffer);
						buffer.flip();
						byte[] requestData = new byte[buffer.limit()];
						buffer.get(requestData, 0, requestData.length);
						executor.execute(() -> {
							try {
								handler(requestData, client);
							} finally {
								ByteBuffer byteBuffer = THREAD_LOCAL.get();
								byteBuffer.clear();
							}
						});
					}
				}
			} catch (Throwable ex) {
				LOGGER.error("handler client request has error : {}", ExceptionUtil.getStackTrace(ex));
			}
		}
	}

	private void handler(final byte[] data, final SocketAddress client) {
		try {
			final ByteBuffer buffer = THREAD_LOCAL.get();
			final Message message = new Message(data);
			final Record question = message.getQuestion();
			final String domain = question.getName().toString();
			final Record response = createRecordByQuery(question, domain);
			final Message out = message.clone();
			out.addRecord(response, Section.ANSWER);
			buffer.put(out.toWire());
			buffer.flip();
			serverChannel.send(buffer, client);
		} catch (Throwable ex) {
			LOGGER.error("response to client has error : {}", ExceptionUtil.getStackTrace(ex));
		}
	}

	private static Record createRecordByQuery(final Record request, final String domain) {
		InstanceRecord record = nacosDnsCore.selectOne(domain);
		if (record == null) {
			return NULLRecord.newRecord(request.getName(), request.getType(), request.getDClass());
		}
		InetSocketAddress address = new InetSocketAddress(record.getIp(), record.getPort());
		return new ARecord(request.getName(), request.getDClass(), request.getTTL(), address.getAddress());
	}
}