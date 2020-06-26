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

package com.conf.nacos.dns.pojo;

import org.junit.Test;

public class MessageTest {

	private static byte[] createDnsRequest() {
		return new byte[] { (byte) 0x9a, (byte) 0xaa, 0x01, 0x00, 0x00, 0x01, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x0a, 0x74, 0x69, 0x6d, 0x65, 0x2d, 0x6d, 0x61,
				0x63, 0x6f, 0x73, 0x05, 0x61, 0x70, 0x70, 0x6c, 0x65, 0x03, 0x63, 0x6f,
				0x6d, 0x00, 0x00, 0x01, 0x00, 0x01 };
	}

	public static byte[] domainBytes() {
		return new byte[] { 0x0a, 0x74, 0x69, 0x6d, 0x65, 0x2d, 0x6d, 0x61, 0x63, 0x6f,
				0x73, 0x05, 0x61, 0x70, 0x70, 0x6c, 0x65, 0x03, 0x63, 0x6f, 0x6d };
	}

	@Test
	public void testDnsRequestAnalyze() {
		Message message = new Message(createDnsRequest());
		System.out.println(message);
	}

}
