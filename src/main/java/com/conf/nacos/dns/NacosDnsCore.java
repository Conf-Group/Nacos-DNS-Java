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

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.utils.NamingUtils;
import com.alibaba.nacos.common.utils.MapUtils;
import com.conf.nacos.dns.constants.Constants;
import com.conf.nacos.dns.loadbalancer.LastAccessLoadBalancer;
import com.conf.nacos.dns.loadbalancer.RandomLoadBalancer;
import com.conf.nacos.dns.loadbalancer.RoundRobinLoadBalancer;
import com.conf.nacos.dns.pojo.InstanceRecord;
import org.jctools.maps.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class NacosDnsCore {

	private static final Logger logger = LoggerFactory.getLogger(NacosDnsCore.class);

	private static final Map<String, List<InstanceRecord>> serviceMap = new NonBlockingHashMap<>(
			32);

	private static final LoadBalancer[] balancers = new LoadBalancer[3];

	private static NamingService nacosClient;

	public NacosDnsCore() throws Throwable {
		try (InputStream stream = NacosDnsCore.class.getClassLoader()
				.getResourceAsStream("nacos-dns.properties")) {
			Properties properties = new Properties();
			properties.load(stream);

			Properties config = new Properties();
			for (String[] keys : Constants.NACOS_PEOPERTIES_KEY) {
				MapUtils.putIfValNoNull(config, keys[0], properties.getProperty(keys[1]));
			}

			nacosClient = NacosFactory.createNamingService(config);
			initLoadBalancer();
		}
	}

	private void initLoadBalancer() {
		balancers[0] = new RoundRobinLoadBalancer();
		balancers[1] = new LastAccessLoadBalancer();
		balancers[2] = new RandomLoadBalancer();
	}


	public InstanceRecord selectOne(final String domain) {
		List<InstanceRecord> list = findAllInstanceByServiceName(domain);
		if (list.isEmpty()) {
			return null;
		}
		return balancers[2].selectOne(list);
	}

	private List<InstanceRecord> findAllInstanceByServiceName(final String serviceName) {
		if (!serviceMap.containsKey(serviceName)) {
			obtainServiceFromRemoteServer(serviceName);
		}
		return serviceMap.getOrDefault(serviceName, Collections.emptyList());
	}

	private static void obtainServiceFromRemoteServer(final String serviceName) {
		serviceMap.computeIfAbsent(serviceName, name -> {
			try {
				String domain = serviceName.substring(0, serviceName.length() - 1).replace("\\@\\@", "@@");
				final String _serviceName = NamingUtils.getServiceName(domain);
				final String _groupName = NamingUtils.getGroupName(domain);
				List<Instance> instances = nacosClient.getAllInstances(_serviceName, _groupName);
				System.out.println(instances);
				registerInstanceChangeObserver(serviceName);
				return parseToInstanceRecord(instances);
			}
			catch (Throwable ex) {
				logger.error(
						"An error occurred querying the service instance remotely : {}",
						ex);
				ex.printStackTrace();
				return Collections.emptyList();
			}
		});
	}

	private static void registerInstanceChangeObserver(final String serviceName)
			throws NacosException {
		nacosClient.subscribe(serviceName, event -> {
			NamingEvent namingEvent = (NamingEvent) event;
			final String name = namingEvent.getServiceName();
			final List<Instance> newInstances = namingEvent.getInstances();
			serviceMap.computeIfPresent(name, (s, instanceRecords) -> {
				instanceRecords.clear();
				return parseToInstanceRecord(newInstances);
			});
		});
	}

	private static List<InstanceRecord> parseToInstanceRecord(List<Instance> instances) {
		int maxSize = 10_000;
		Stream<Instance> stream;
		if (instances.size() < maxSize) {
			stream = instances.stream();
		}
		else {
			stream = instances.parallelStream();
		}

		return stream.map(instance -> InstanceRecord.builder().ip(instance.getIp())
				.port(instance.getPort()).healthy(instance.isHealthy())
				.enabled(instance.isEnabled()).weight(instance.getWeight())
				.metadata(instance.getMetadata()).build())
				.collect(CopyOnWriteArrayList::new, CopyOnWriteArrayList::add,
						CopyOnWriteArrayList::addAll);
	}

}
