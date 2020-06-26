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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;

import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.alibaba.nacos.common.utils.Objects;
import com.alibaba.nacos.common.utils.ShutdownUtils;
import com.conf.nacos.dns.constants.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static DnsServer server;

	public static void main(String[] args) {
		try {
			NacosDnsConfig config = resourceInit();
			server = DnsServer.create(config);
			server.start();
			registerShutdownHook();
		}
		catch (Throwable ex) {
			LOGGER.error("nacos-dns-server start failed : {}",
					ExceptionUtil.getStackTrace(ex));
		}
	}

	private static void registerShutdownHook() {
		ShutdownUtils.addShutdownHook(() -> {
			try {
				if (Objects.nonNull(server)) {
					server.shutdown();
				}
			}
			catch (Throwable ex) {
				LOGGER.error("dns-server shutdown occur error : {}",
						ExceptionUtil.getStackTrace(ex));
			}
		});
	}

	private static NacosDnsConfig resourceInit() throws Exception {
		final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		final String path = System.getProperty("nacos.dns.home");
		final File file = Paths.get(path, "conf", Constants.CONFIG_FILE_NAME).toFile();
		InputStream is;
		try {
			is = new FileInputStream(file);
		}
		catch (FileNotFoundException ex) {
			is = Main.class.getClassLoader().getResourceAsStream("nacos-dns.yaml");
		}
		return mapper.readValue(is, NacosDnsConfig.class);
	}

}
