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

import java.nio.ByteBuffer;

import com.conf.nacos.dns.utils.BinaryOperator;

/**
 * DNS requests total 12 bytes
 * 
 * <pre>
 *     0  1  2  3  4  5  6  7  0  1  2  3  4  5  6  7
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                      ID                       |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |QR|  opcode   |AA|TC|RD|RA|   Z    |   RCODE   |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    QDCOUNT                    |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    ANCOUNT                    |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    NSCOUNT                    |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    ARCOUNT                    |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 *
 * <p>
 * ID: 2 byte
 * <p>
 * // 1 byte start QR: 1 bit opcode: 4bit AA: 1bit TC: 1bit RD: 1bit // 1 byte end
 * <p>
 * // 1 byte start RA: 1bit Z : 3bit RCODE: 4bit // 1 byte end
 * <p>
 * // 8 byte start QDCOUNT: 2 byte ANCOUNT: 2 byte NSCOUNT: 2 byte ARCOUNT: 2 byte // 8
 * byte end
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class Header {

	enum QREnum {
		Query((short) 0),

		Answer((short) 1);

		private final short code;

		QREnum(short code) {
			this.code = code;
		}
	}

	enum OpCodeEnum {
		StandQuery((short) 0),

		ReverseQuery((short) 1),

		ServerStatusRequest((short) 2);

		private final short code;

		OpCodeEnum(short code) {
			this.code = code;
		}
	}

	enum RCodeEnum {
		OK((short) 0),

		FormatError((short) 1),

		ServerFailure((short) 2),

		NameError((short) 3),

		NotImplemented((short) 4),

		Refused((short) 5),

		;

		private final short code;

		RCodeEnum(short code) {
			this.code = code;
		}
	}

	/**
	 * ID field. The client will resolve the DNS reply message returned by the server and
	 * get the ID value to compare with the ID value set by the request message. If the ID
	 * value is the same, it is considered to be the same DNS session.
	 */
	private int id;

	/**
	 * 0 represents query message, 1 represents response message;
	 */
	private short qr;

	/**
	 * Typically the value is 0 (standard query), the other values are 1 (reverse query)
	 * and 2 (server status request),[3,15] reserved;
	 */
	private short opcode;

	/**
	 * Would its meaning be meaningful in the reply, indicating that the server answering
	 * the answer would be the authoritative resolution server that would query the domain
	 * name;
	 */
	private short aa;

	/**
	 * Truncated - indicates that a message is longer than permitted, resulting in
	 * truncation
	 */
	private short tc;

	/**
	 * Represents Recursion Desired -- This bit is set by the request, and the same value
	 * is returned when the response is made. If RD is set, recursive resolution is
	 * recommended for the domain name server. Recursive query support is optional.
	 */
	private short rd;

	/**
	 * Recursion Available - This bit is set or cancelled in the reply to indicate whether
	 * the server supports recursive queries.
	 */
	private short ra;

	/**
	 * Reserved value, not yet used;
	 */
	private short z;

	/**
	 * Response Code
	 */
	private short rCode;

	/**
	 * The unsigned 16bit integer represents the number of problem records in the request
	 * segment of the message.
	 */
	private int qdCount;

	/**
	 * The unsigned 16bit integer represents the number of answer records in the message
	 * answer segment.
	 */
	private int anCount;

	/**
	 * The unsigned 16bit integer represents the number of authorized records in the
	 * authorization section of a message.
	 */
	private int nsCount;

	/**
	 * The unsigned 16bit integer represents the number of additional records in the
	 * additional segment of the message.
	 */
	private int arCount;

	Header(ByteBuffer buffer) {
		// set id value
		initID(buffer);

		// set flags value
		initFlags(buffer);

		// set all count value
		initCounts(buffer);
	}

	private void initID(ByteBuffer buffer) {
		byte[] idArr = new byte[2];
		buffer.get(idArr, 0, idArr.length);
		// shot size is tow byte
		// (high level) 0000 (low level) 0000
		this.id = BinaryOperator.byteArrayToInt(idArr);
	}

	private void initFlags(ByteBuffer buffer) {
		byte[] flagsArr = new byte[2];
		buffer.get(flagsArr, 0, flagsArr.length);

		// flags part one
		final byte flagPartOne = flagsArr[0];
		qr = (short) BinaryOperator.extractTargetValue(flagPartOne, 0, 1);
		opcode = (short) BinaryOperator.extractTargetValue(flagPartOne, 1, 4);
		aa = (short) BinaryOperator.extractTargetValue(flagPartOne, 5, 1);
		tc = (short) BinaryOperator.extractTargetValue(flagPartOne, 6, 1);
		rd = (short) BinaryOperator.extractTargetValue(flagPartOne, 7, 1);

		// flags part two
		final byte flagPartTwo = flagsArr[1];
		ra = (short) BinaryOperator.extractTargetValue(flagPartTwo, 0, 1);
		z = (short) BinaryOperator.extractTargetValue(flagPartTwo, 1, 3);
		rCode = (short) BinaryOperator.extractTargetValue(flagPartTwo, 4, 4);
	}

	private void initCounts(ByteBuffer buffer) {
		// set qdCount value
		byte[] qdCountArr = new byte[2];
		buffer.get(qdCountArr, 0, qdCountArr.length);
		this.qdCount = BinaryOperator.byteArrayToInt(qdCountArr);

		// set anCount value
		byte[] anCountArr = new byte[2];
		buffer.get(anCountArr, 0, anCountArr.length);
		this.anCount = BinaryOperator.byteArrayToInt(anCountArr);

		// set nsCount value
		byte[] nsCountArr = new byte[2];
		buffer.get(nsCountArr, 0, nsCountArr.length);
		this.nsCount = BinaryOperator.byteArrayToInt(nsCountArr);

		// set arCount value
		byte[] arCountArr = new byte[2];
		buffer.get(arCountArr, 0, arCountArr.length);
		this.arCount = BinaryOperator.byteArrayToInt(arCountArr);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public short getQr() {
		return qr;
	}

	public void setQr(short qr) {
		this.qr = qr;
	}

	public short getOpcode() {
		return opcode;
	}

	public void setOpcode(short opcode) {
		this.opcode = opcode;
	}

	public short getAa() {
		return aa;
	}

	public void setAa(short aa) {
		this.aa = aa;
	}

	public short getTc() {
		return tc;
	}

	public void setTc(short tc) {
		this.tc = tc;
	}

	public short getRd() {
		return rd;
	}

	public void setRd(short rd) {
		this.rd = rd;
	}

	public short getRa() {
		return ra;
	}

	public void setRa(short ra) {
		this.ra = ra;
	}

	public short getZ() {
		return z;
	}

	public void setZ(short z) {
		this.z = z;
	}

	public short getRCode() {
		return rCode;
	}

	public void setRCode(short rCode) {
		this.rCode = rCode;
	}

	public int getQdCount() {
		return qdCount;
	}

	public void setQdCount(int qdCount) {
		this.qdCount = qdCount;
	}

	public int getAnCount() {
		return anCount;
	}

	public void setAnCount(int anCount) {
		this.anCount = anCount;
	}

	public int getNsCount() {
		return nsCount;
	}

	public void setNsCount(int nsCount) {
		this.nsCount = nsCount;
	}

	public int getArCount() {
		return arCount;
	}

	public void setArCount(int arCount) {
		this.arCount = arCount;
	}

	@Override
	public String toString() {
		return "Header{" + "id=" + id + ", qr=" + qr + ", opcode=" + opcode + ", aa=" + aa
				+ ", tc=" + tc + ", rd=" + rd + ", ra=" + ra + ", z=" + z + ", rCode="
				+ rCode + ", qdCount=" + qdCount + ", anCount=" + anCount + ", nsCount="
				+ nsCount + ", arCount=" + arCount + '}';
	}
}
