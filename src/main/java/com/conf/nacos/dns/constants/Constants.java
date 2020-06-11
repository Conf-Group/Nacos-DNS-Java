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
package com.conf.nacos.dns.constants;

import com.alibaba.nacos.api.PropertyKeyConst;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class Constants {

	public static final String PREFIX = "nacos.dns.";

	public static final String NACOS_SERVER_ADDRS = PREFIX + "server-addrs";

	public static final String NACOS_USERNAME = PREFIX + "username";

	public static final String NACOS_PASSWORD = PREFIX + "password";

	public static final String[][] NACOS_PEOPERTIES_KEY = new String[][] {
			new String[] { PropertyKeyConst.SERVER_ADDR, NACOS_SERVER_ADDRS },
			new String[] { PropertyKeyConst.USERNAME, NACOS_USERNAME },
			new String[] { PropertyKeyConst.PASSWORD, NACOS_PASSWORD },
	};

	public static final String RANDOM_LOAD_BALANCER = "RandomLoadBalancer";

	public static final String ROUND_ROBIN_LOAD_BALANCER = "RoundRobinLoadBalancer";

	public static final String WEIGHT_LOAD_BALANCER = "WeightLoadBalancer";

	public static final String BYTE_BUFFER_SIZE = "byte-buffer.size";

}
