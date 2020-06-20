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

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.common.utils.ThreadUtils;
import com.conf.nacos.dns.pojo.InstanceRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;


public class NacosDnsCoreTest {

	@Test
	public void testSelectService() throws Throwable {
		NamingService namingService = NacosFactory.createNamingService("127.0.0.1:8848");

		namingService.registerInstance("nacos.test.1", "1.1.1.1", 80);

		NacosDnsCore nacosDnsCore = new NacosDnsCore();
		Optional<InstanceRecord> record = nacosDnsCore.selectOne("DEFAULT_GROUP@@nacos.test.1");
		Assert.assertNotNull(record);
	}

	@Test
	public void testMultiClientRegister() throws Throwable {
		Set<String> ipSet = new HashSet<>();
		Set<NamingService> namingServices = new HashSet<>();
		for (int i = 0; i < 10; i ++) {
			namingServices.add(NacosFactory.createNamingService("127.0.0.1:8846"));
		}

		for (NamingService namingService : namingServices) {
			for (int j = 0; j < 10; j ++) {
				final String serviceName = randomDomainName();
				for (int i = 0; i < 100; i++) {
					String ip = null;
					for (; ; ) {
						ip = getRandomIp();
						if (!ipSet.contains(ip)) {
							break;
						}
					}

					ipSet.add(ip);

					ThreadUtils.sleep(i);
					try {
						namingService.registerInstance(serviceName, ip, 80);
					}
					catch (Throwable ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		CountDownLatch latch = new CountDownLatch(1);
		latch.await();

	}

	private static Random rd = new Random();

	public static String randomDomainName() {
		StringBuilder sb = new StringBuilder();
		sb.append("jinhan");
		for (int i = 0; i < 2; i++) {
			sb.append(getStringWithNumAndCha(5));
			sb.append(".");
		}
		int i = getIntegerBetween(0, 2);
		if (i == 0) {
			sb.append("com");
		} else {
			sb.append("net");
		}
		return sb.toString();
	}

	public static String getStringWithNumAndCha(int n) {
		int[] arg = new int[]{97, 123, 65, 91, 48, 58};
		return getString(n, arg);
	}

	private static char getChar(int[] arg) {
		int size = arg.length;
		int c = rd.nextInt(size / 2);
		c *= 2;
		return (char)getIntegerBetween(arg[c], arg[c + 1]);
	}

	private static String getString(int n, int[] arg) {
		StringBuilder res = new StringBuilder();

		for(int i = 0; i < n; ++i) {
			res.append(getChar(arg));
		}

		return res.toString();
	}

	public static int getIntegerBetween(int n, int m) {
		if (m == n) {
			return n;
		} else {
			int res = getIntegerMoreThanZero();
			return n + res % (m - n);
		}
	}

	public static int getIntegerMoreThanZero() {
		int res;
		for(res = rd.nextInt(); res <= 0; res = rd.nextInt()) {
		}

		return res;
	}


	public static String getRandomIp() {

		// ip范围
		int[][] range = { { 607649792, 608174079 }, // 36.56.0.0-36.63.255.255
				{ 1038614528, 1039007743 }, // 61.232.0.0-61.237.255.255
				{ 1783627776, 1784676351 }, // 106.80.0.0-106.95.255.255
				{ 2035023872, 2035154943 }, // 121.76.0.0-121.77.255.255
				{ 2078801920, 2079064063 }, // 123.232.0.0-123.235.255.255
				{ -1950089216, -1948778497 }, // 139.196.0.0-139.215.255.255
				{ -1425539072, -1425014785 }, // 171.8.0.0-171.15.255.255
				{ -1236271104, -1235419137 }, // 182.80.0.0-182.92.255.255
				{ -770113536, -768606209 }, // 210.25.0.0-210.47.255.255
				{ -569376768, -564133889 }, // 222.16.0.0-222.95.255.255
		};

		Random random = ThreadLocalRandom.current();
		int index = random.nextInt(10);
		return num2ip(
				range[index][0] + random.nextInt(range[index][1] - range[index][0]));
	}

	/*
	 * 将十进制转换成IP地址
	 */
	public static String num2ip(int ip) {
		int[] b = new int[4];
		String ipStr = "";
		b[0] = (ip >> 24) & 0xff;
		b[1] = (ip >> 16) & 0xff;
		b[2] = (ip >> 8) & 0xff;
		b[3] = ip & 0xff;
		ipStr = b[0] + "." + b[1] + "." + b[2] + "." + b[3];

		return ipStr;
	}
}