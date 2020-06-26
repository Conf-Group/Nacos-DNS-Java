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

package com.conf.nacos.dns.utils;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class BinaryOperator {

	public static int extractTargetValue(byte b, int offset, int length) {
		return (b >>> (7 - offset + length - 1)) & ~(0xff << length);
	}

	public static int byteArrayToInt(byte[] bytes) {
		return ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
	}

	public static long byteArrayToLong(byte[] bytes) {
		return ((bytes[0] & 0xffff) << 24) | ((bytes[1] & 0xffff) << 16)
				| ((bytes[2] & 0xffff) << 8) | (bytes[3] & 0xffff);
	}

	public static String byteArrayToString(byte[] bytes) {
		return new String(bytes);
	}

}
