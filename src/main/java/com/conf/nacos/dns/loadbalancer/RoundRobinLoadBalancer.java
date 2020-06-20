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
package com.conf.nacos.dns.loadbalancer;

import com.conf.nacos.dns.LoadBalancer;
import com.conf.nacos.dns.pojo.InstanceRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
	
	AtomicInteger index = new AtomicInteger(0);
	
	private volatile List<InstanceRecord> instances = new ArrayList<>(100);
	
	@Override
	public void recordChange(List<InstanceRecord> recordList) {
		List<InstanceRecord> old = instances;
		instances = recordList;
		old.clear();
	}
	
	@Override
	public InstanceRecord selectOne() {
		List<InstanceRecord> copy = instances;
		int currentIndex = index.getAndIncrement();
		if (index.get() == copy.size() - 1) {
			index.lazySet(0);
		}
		return copy.get(currentIndex);
	}

	@Override
	public String name() {
		return "RoundRobinLoadBalancer";
	}

}
