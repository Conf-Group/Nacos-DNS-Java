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
import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.conf.nacos.dns.constants.Constants;
import com.conf.nacos.dns.loadbalancer.RandomLoadBalancer;
import com.conf.nacos.dns.pojo.InstanceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class NacosDnsCore {
    
    private static final Logger logger = LoggerFactory.getLogger(NacosDnsCore.class);
    
    private final Map<String, LoadBalancer> serviceCache = new ConcurrentHashMap<>(32);
    
    private final LoadBalancer defaultLoadBalancer = new RandomLoadBalancer();
    
    private final Map<String, LoadBalancer> balancers = new HashMap<>(4);
    
    private final NamingService nacosClient;
    
    private Supplier<LoadBalancer> supplier;
    
    // It can change dynamically
    
    private volatile String balancerName = Constants.RANDOM_LOAD_BALANCER;
    
    private volatile Map<String, Pattern> patternMap = new HashMap<>();
    
    private volatile Map<String, String> serviceToGroup;
    
    public NacosDnsCore(NacosDnsConfig config) throws Throwable {
        nacosClient = NacosFactory.createNamingService(config.getNacosConfig().toNacosClientProperties());
        serviceToGroup = Collections.unmodifiableMap(config.getResolver().getServiceGroupMatch());
        serviceToGroup.forEach((serviceRegx, group) -> {
            patternMap.put(serviceRegx, Pattern.compile(serviceRegx));
        });
        
        balancerName = config.getLoadBalancer();
        
        supplier = () -> {
            ServiceLoader<LoadBalancer> loader = ServiceLoader.load(LoadBalancer.class);
            loader.forEach(loadBalancer -> balancers.put(loadBalancer.name(), loadBalancer));
            return balancers.getOrDefault(balancerName, defaultLoadBalancer);
        };
    }
    
    private static List<InstanceRecord> parseToInstanceRecord(List<Instance> instances) {
        int maxSize = 10_000;
        Stream<Instance> stream;
        if (instances.size() < maxSize) {
            stream = instances.stream();
        } else {
            stream = instances.parallelStream();
        }
        
        return stream.map(instance -> InstanceRecord.builder().ip(instance.getIp()).port(instance.getPort())
                .healthy(instance.isHealthy()).enabled(instance.isEnabled()).weight(instance.getWeight())
                .metadata(instance.getMetadata()).build())
                .collect(CopyOnWriteArrayList::new, CopyOnWriteArrayList::add, CopyOnWriteArrayList::addAll);
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
            
            AtomicReference<String> targetGroup = new AtomicReference<>(Constants.DEFAULT_GROUP);
            for (Map.Entry<String, Pattern> e : patternMap.entrySet()) {
                final Pattern p = e.getValue();
                Matcher matcher = p.matcher(serviceName);
                if (matcher.matches()) {
                    targetGroup.set(serviceToGroup.get(e.getKey()));
                }
            }
            
            try {
                String domain = serviceName.substring(0, serviceName.length() - 1);
                logger.debug("domain info : {}, serviceName : {}, groupName : {}", domain, domain, targetGroup.get());
                List<Instance> instances = nacosClient.getAllInstances(domain, targetGroup.get());
                registerInstanceChangeObserver(serviceName);
                balancer.recordChange(parseToInstanceRecord(instances));
            } catch (Throwable ex) {
                logger.error("An error occurred querying the service instance remotely : {}",
                        ExceptionUtil.getStackTrace(ex));
            }
            return balancer;
        });
    }
    
    private void registerInstanceChangeObserver(final String serviceName) throws NacosException {
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
