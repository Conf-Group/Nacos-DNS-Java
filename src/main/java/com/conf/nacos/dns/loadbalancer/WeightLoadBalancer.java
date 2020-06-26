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

import java.util.List;
import java.util.PriorityQueue;

import com.conf.nacos.dns.LoadBalancer;
import com.conf.nacos.dns.pojo.InstanceRecord;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class WeightLoadBalancer implements LoadBalancer {

	private volatile PriorityQueue<InstanceRecord> records = new PriorityQueue<>(
			(o1, o2) -> (int) (o2.getWeight() - o1.getWeight()));

	@Override
	public void recordChange(List<InstanceRecord> recordList) {
		PriorityQueue<InstanceRecord> old = records;

		PriorityQueue<InstanceRecord> copy = new PriorityQueue<>(
				(o1, o2) -> (int) (o2.getWeight() - o1.getWeight()));
		copy.addAll(recordList);

		records = copy;
		old.clear();
	}

	@Override
	public InstanceRecord selectOne() {
		return records.peek();
	}

	@Override
	public String name() {
		return "WeightLoadBalancer";
	}
}