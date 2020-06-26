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

package com.conf.nacos.dns.pojo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.conf.nacos.dns.utils.BinaryOperator;
import com.google.common.base.Joiner;

/**
 * <pre>
 *     0  1  2  3  4  5  6  7  0  1  2  3  4  5  6  7
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                     ...                       |
 *   |                    QNAME                      |
 *   |                     ...                       |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    QTYPE                      |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    QCLASS                     |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class Question {

	private String qName;

	private QType qType;

	private int qClass;

	public static enum QType {

		/**
		 * for IPv4
		 */
		A(1, "A"),

		/**
		 * for IPv6
		 */
		AAAA(28, "AAAA"),

		/**
		 * for name server
		 */
		NS(2, "NS"),

		/**
		 * The canonical name defines the alias for the official name of the host
		 */
		CNAME(5, "CNAME"),

		/**
		 * Start authorization marks the beginning of a zone
		 */
		SOA(6, "SOA"),

		/**
		 * Be familiar with the network services provided by the service definition host
		 */
		WKS(11, "WKS"),

		/**
		 * Pointers convert IP addresses to domain names
		 */
		PTR(12, "PTR"),

		/**
		 * Host information gives a description of the hardware and operating system used
		 * by the host
		 */
		HINFO(13, "HINFO"),

		/**
		 * Mail exchange rerouts mail to mail servers
		 */
		MX(15, "MX"),

		/**
		 * Send requests for the entire block
		 */
		AXFR(252, "AXFR"),

		/**
		 * Requests for all records
		 */
		ANY(255, "ANY");

		private final int type;
		private final String name;

		QType(int type, String name) {
			this.type = type;
			this.name = name;
		}

		public static QType sourceOf(int type) {
			for (QType q : values()) {
				if (q.type == type) {
					return q;
				}
			}
			return null;
		}

		public int getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "QType{" + "type=" + type + ", name='" + name + '\'' + '}';
		}
	}

	Question(ByteBuffer buffer) {
		int currentPosition = buffer.position();
		buffer.position(buffer.limit() - 4);
		initQType(buffer);
		initQClass(buffer);

		buffer.position(currentPosition);
		initQName(buffer);
	}

	public Question(String qName, QType qType, int qClass) {
		this.qName = qName;
		this.qType = qType;
		this.qClass = qClass;
	}

	private void initQType(ByteBuffer buffer) {
		byte[] qTypeArr = new byte[2];
		buffer.get(qTypeArr, 0, 2);

		this.qType = QType.sourceOf(BinaryOperator.byteArrayToInt(qTypeArr));
	}

	private void initQClass(ByteBuffer buffer) {
		byte[] qClassArr = new byte[2];
		buffer.get(qClassArr, 0, 2);

		this.qClass = BinaryOperator.byteArrayToInt(qClassArr);
	}

	private void initQName(ByteBuffer buffer) {
		byte[] qNameArr = new byte[buffer.limit() - 4 - 12 + 1];
		buffer.get(qNameArr);

		List<String> list = new ArrayList<>();
		ByteBuffer cache = ByteBuffer.allocate(qNameArr.length);
		for (int i = 0; i < qNameArr.length && qNameArr[i] != 0x00;) {
			byte len = qNameArr[i];
			int j = i + 1;
			for (; j <= i + len; j++) {
				cache.put(qNameArr[j]);
			}
			i = i + 1 + len;
			cache.flip();
			byte[] b = new byte[cache.limit()];
			cache.get(b);
			list.add(new String(b));
			cache.clear();
		}
		qName = Joiner.on(".").join(list);
	}

	public String getQName() {
		return qName;
	}

	public QType getQType() {
		return qType;
	}

	public int getQClass() {
		return qClass;
	}

	@Override
	public String toString() {
		return "Question{" + "qName='" + qName + '\'' + ", qType=" + qType + ", qClass="
				+ qClass + '}';
	}
}
