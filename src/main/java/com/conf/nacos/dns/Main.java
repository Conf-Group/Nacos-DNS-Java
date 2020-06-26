/*
 * Copyright (c) 2020, Conf-Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
