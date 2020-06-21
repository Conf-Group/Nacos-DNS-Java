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

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public enum Code {
    
    SUCCESS(0, "success"),
    
    /**
     * 4000 - 4999 is nacos
     */
    
    CREATE_NACOS_NAMING_FAILED(4000, "create nacos-naming client failed"),
    
    /**
     * 5000 ~ 5999 is dns
     */
    
    CREATE_DNS_SERVER_FAILED(5000, "create dns-server failed"),
    
    ;
    
    private final int code;
    
    private final String msg;
    
    Code(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    @Override
    public String toString() {
        return "Code{" + "code=" + code + ", msg='" + msg + '\'' + '}';
    }
}
