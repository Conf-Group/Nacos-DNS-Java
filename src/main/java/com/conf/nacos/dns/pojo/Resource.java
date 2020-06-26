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

/**
 * The resource record section appears only in the DNS response packet
 * 
 * <pre>
 *     0  1  2  3  4  5  6  7  0  1  2  3  4  5  6  7
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    NAME                       |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    TYPE                       |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    CLASS                      |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    TTL                        |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    RDLENGTH                   |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    RDATA                      |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
class Resource {

	private String name;

	private String type;

	private String NClass;

	private long ttl;

	private int rdLength;

	private String rData;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNClass() {
		return NClass;
	}

	public void setNClass(String NClass) {
		this.NClass = NClass;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}

	public int getRdLength() {
		return rdLength;
	}

	public void setRdLength(int rdLength) {
		this.rdLength = rdLength;
	}

	public String getrData() {
		return rData;
	}

	public void setrData(String rData) {
		this.rData = rData;
	}

	public static ResourceBuilder builder() {
		return new ResourceBuilder();
	}

	public static final class ResourceBuilder {

		private String name;

		private String type;

		private String NClass;

		private long ttl;

		private int rdLength;

		private String rData;

		private ResourceBuilder() {
		}

		public ResourceBuilder name(String name) {
			this.name = name;
			return this;
		}

		public ResourceBuilder type(String type) {
			this.type = type;
			return this;
		}

		public ResourceBuilder NClass(String NClass) {
			this.NClass = NClass;
			return this;
		}

		public ResourceBuilder ttl(long ttl) {
			this.ttl = ttl;
			return this;
		}

		public ResourceBuilder rdLength(int rdLength) {
			this.rdLength = rdLength;
			return this;
		}

		public ResourceBuilder rData(String rData) {
			this.rData = rData;
			return this;
		}

		public Resource build() {
			Resource resource = new Resource();
			resource.setName(name);
			resource.setType(type);
			resource.setNClass(NClass);
			resource.setTtl(ttl);
			resource.setRdLength(rdLength);
			resource.rData = this.rData;
			return resource;
		}
	}
}
