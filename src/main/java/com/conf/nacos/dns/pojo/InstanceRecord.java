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
        return "InstanceRecord{" + "lastAccessTime=" + lastAccessTime + ", ip='" + ip + '\'' + ", port=" + port
                + ", weight=" + weight + ", healthy=" + healthy + ", enabled=" + enabled + ", metadata=" + metadata
                + '}';
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
