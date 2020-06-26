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

package com.conf.nacos.dns;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.conf.nacos.dns.constants.Constants;
import com.conf.nacos.dns.loadbalancer.RandomLoadBalancer;
import com.conf.nacos.dns.pojo.InstanceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class NacosDnsCore {

	private static final Logger logger = LoggerFactory.getLogger(NacosDnsCore.class);

	private final Map<String, LoadBalancer> serviceCache = new ConcurrentHashMap<>(32);

	private final LoadBalancer defaultLoadBalancer = new RandomLoadBalancer();

	private final Map<String, LoadBalancer> balancers = new HashMap<>(4);

	private final NamingService nacosClient;
	private final NamingResolverConfig resolverConfig;

	// It can change dynamically
	private Supplier<LoadBalancer> supplier;
	private volatile String balancerName = Constants.RANDOM_LOAD_BALANCER;
	private volatile Map<String, Pattern> patternMap = new HashMap<>();
	private volatile Map<String, String> serviceToGroup;

	public NacosDnsCore(NacosDnsConfig config) throws Throwable {
		this.nacosClient = NacosFactory
				.createNamingService(config.getNacosConfig().toNacosClientProperties());
		this.serviceToGroup = Collections
				.unmodifiableMap(config.getResolver().getServiceGroupMatch());
		this.serviceToGroup.forEach((serviceRegx, group) -> {
			this.patternMap.put(serviceRegx, Pattern.compile(serviceRegx));
		});

		this.balancerName = config.getLoadBalancer();

		this.supplier = () -> {
			ServiceLoader<LoadBalancer> loader = ServiceLoader.load(LoadBalancer.class);
			loader.forEach(loadBalancer -> this.balancers.put(loadBalancer.name(),
					loadBalancer));
			return balancers.getOrDefault(this.balancerName, this.defaultLoadBalancer);
		};

		this.resolverConfig = config.getResolver();
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

		return stream
				.map(instance -> InstanceRecord.builder().ip(instance.getIp())
						.port(instance.getPort()).healthy(instance.isHealthy())
						.enabled(instance.isEnabled()).weight(instance.getWeight())
						.metadata(instance.getMetadata()).build())
				.collect(CopyOnWriteArrayList::new, CopyOnWriteArrayList::add,
						CopyOnWriteArrayList::addAll);
	}

	public Optional<InstanceRecord> selectOne(final String domain) {
		if (!serviceCache.containsKey(domain)) {
			obtainServiceFromRemoteServer(domain);
		}

		final LoadBalancer balancer = serviceCache.get(domain);
		if (Objects.isNull(balancer)) {
			return Optional.empty();
		}
		return Optional.ofNullable(balancer.selectOne());
	}

	private void obtainServiceFromRemoteServer(final String serviceName) {
		serviceCache.computeIfAbsent(serviceName, name -> {
			final LoadBalancer balancer = supplier.get();

			AtomicReference<String> targetGroup = new AtomicReference<>(
					resolverConfig.getDefaultGroup());
			for (Map.Entry<String, Pattern> e : patternMap.entrySet()) {
				final Pattern p = e.getValue();
				Matcher matcher = p.matcher(serviceName);
				if (matcher.matches()) {
					targetGroup.set(serviceToGroup.get(e.getKey()));
				}
			}

			try {
				String domain = serviceName.substring(0, serviceName.length() - 1);
				logger.debug("domain info : {}, serviceName : {}, groupName : {}", domain,
						domain, targetGroup.get());
				List<Instance> instances = nacosClient.getAllInstances(domain,
						targetGroup.get());
				registerInstanceChangeObserver(serviceName);
				balancer.recordChange(parseToInstanceRecord(instances));
			}
			catch (Throwable ex) {
				logger.error(
						"An error occurred querying the service instance remotely : {}",
						ExceptionUtil.getStackTrace(ex));
			}
			return balancer;
		});
	}

	private void registerInstanceChangeObserver(final String serviceName)
			throws NacosException {
		nacosClient.subscribe(serviceName, event -> {
			NamingEvent namingEvent = (NamingEvent) event;
			final String name = namingEvent.getServiceName();
			final List<Instance> newInstances = namingEvent.getInstances();
			serviceCache.computeIfPresent(name, (s, balancer) -> {
				balancer.recordChange(parseToInstanceRecord(newInstances));
				return balancer;
			});
		});
	}

	public void shutdown() {
		serviceCache.clear();
		serviceToGroup.clear();
		patternMap.clear();
	}

}
