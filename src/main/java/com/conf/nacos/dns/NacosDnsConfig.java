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

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class NacosDnsConfig {
    
    private String loadBalancer;
    
    private String backendDns;
    
    private int bufferSize = 4096;
    
    private NamingResolverConfig resolver;
    
    private NacosClientConfig nacosConfig;
    
    public String getLoadBalancer() {
        return loadBalancer;
    }
    
    public void setLoadBalancer(String loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
    
    public String getBackendDns() {
        return backendDns;
    }
    
    public void setBackendDns(String backendDns) {
        this.backendDns = backendDns;
    }
    
    public int getBufferSize() {
        return bufferSize;
    }
    
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    public NamingResolverConfig getResolver() {
        return resolver;
    }
    
    public void setResolver(NamingResolverConfig resolver) {
        this.resolver = resolver;
    }
    
    public NacosClientConfig getNacosConfig() {
        return nacosConfig;
    }
    
    public void setNacosConfig(NacosClientConfig nacosConfig) {
        this.nacosConfig = nacosConfig;
    }
}
