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

/**
 * The resource record section appears only in the DNS response packet
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
