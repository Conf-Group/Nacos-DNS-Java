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

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class InstanceRecord {

	/**
	 * instance ip
	 */
	private final String ip;

	/**
	 * instance port
	 */
	private final int port;

	/**
	 * The last time the instance was accessed
	 */
	private long lastAccessTime;

	/**
	 * instance weight
	 */
	private double weight = 1.0D;

	/**
	 * instance health status
	 */
	private boolean healthy = true;

	/**
	 * If instance is enabled to accept request
	 */
	private boolean enabled = true;

	/**
	 * user extended attributes
	 */
	private Map<String, String> metadata = new HashMap<String, String>();

	public InstanceRecord(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public static RecordBuilder builder() {
		return new RecordBuilder();
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public double getWeight() {
		return weight;
	}

	public boolean isHealthy() {
		return healthy;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	@Override
	public String toString() {
		return "InstanceRecord{" + "lastAccessTime=" + lastAccessTime + ", ip='" + ip
				+ '\'' + ", port=" + port + ", weight=" + weight + ", healthy=" + healthy
				+ ", enabled=" + enabled + ", metadata=" + metadata + '}';
	}

	public static final class RecordBuilder {

		private long lastAccessTime;

		private String ip;

		private int port;

		private double weight = 1.0D;

		private boolean healthy = true;

		private boolean enabled = true;

		private Map<String, String> metadata = new HashMap<String, String>();

		private RecordBuilder() {
		}

		public RecordBuilder lastAccessTime(long lastAccessTime) {
			this.lastAccessTime = lastAccessTime;
			return this;
		}

		public RecordBuilder ip(String ip) {
			this.ip = ip;
			return this;
		}

		public RecordBuilder port(int port) {
			this.port = port;
			return this;
		}

		public RecordBuilder weight(double weight) {
			this.weight = weight;
			return this;
		}

		public RecordBuilder healthy(boolean healthy) {
			this.healthy = healthy;
			return this;
		}

		public RecordBuilder enabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public RecordBuilder metadata(Map<String, String> metadata) {
			this.metadata = metadata;
			return this;
		}

		public InstanceRecord build() {
			InstanceRecord instanceRecord = new InstanceRecord(ip, port);
			instanceRecord.lastAccessTime = this.lastAccessTime;
			instanceRecord.healthy = this.healthy;
			instanceRecord.enabled = this.enabled;
			instanceRecord.metadata = this.metadata;
			instanceRecord.weight = this.weight;
			return instanceRecord;
		}
	}
}
