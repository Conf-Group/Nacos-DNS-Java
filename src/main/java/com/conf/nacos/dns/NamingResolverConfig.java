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

import java.util.HashMap;
import java.util.Map;

import com.conf.nacos.dns.constants.Constants;

/**
 * In which group does the service of the query fall.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class NamingResolverConfig {

	private String defaultGroup = Constants.DEFAULT_GROUP;

	private Map<String, String> serviceGroupMatch = new HashMap<>();

	public NamingResolverConfig(String defaultGroup) {
		this.defaultGroup = defaultGroup;
	}

	public String getDefaultGroup() {
		return defaultGroup;
	}

	public void setDefaultGroup(String defaultGroup) {
		this.defaultGroup = defaultGroup;
	}

	public Map<String, String> getServiceGroupMatch() {
		return serviceGroupMatch;
	}

	public void setServiceGroupMatch(Map<String, String> serviceGroupMatch) {
		this.serviceGroupMatch = serviceGroupMatch;
	}
}