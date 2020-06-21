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

import com.conf.nacos.dns.utils.BinaryOperator;
import com.google.common.base.Joiner;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     0  1  2  3  4  5  6  7  0  1  2  3  4  5  6  7
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                     ...                       |
 *   |                    QNAME                      |
 *   |                     ...                       |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    QTYPE                      |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    QCLASS                     |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class Question {
    
    private String qName;
    
    private QType qType;
    
    private int qClass;
    
    public static enum QType {
        
        /**
         * for IPv4
         */
        A(1, "A"),
        
        /**
         * for IPv6
         */
        AAAA(28, "AAAA"),
        
        /**
         * for name server
         */
        NS(2, "NS"),
        
        /**
         * The canonical name defines the alias for the official name of the host
         */
        CNAME(5, "CNAME"),
        
        /**
         * Start authorization marks the beginning of a zone
         */
        SOA(6, "SOA"),
        
        /**
         * Be familiar with the network services provided by the service definition host
         */
        WKS(11, "WKS"),
        
        /**
         * Pointers convert IP addresses to domain names
         */
        PTR(12, "PTR"),
        
        /**
         * Host information gives a description of the hardware and operating system used by the host
         */
        HINFO(13, "HINFO"),
        
        /**
         * Mail exchange rerouts mail to mail servers
         */
        MX(15, "MX"),
        
        /**
         * Send requests for the entire block
         */
        AXFR(252, "AXFR"),
        
        /**
         * Requests for all records
         */
        ANY(255, "ANY");
        
        private final int type;
        private final String name;
        
        QType(int type, String name) {
            this.type = type;
            this.name = name;
        }
        
        public static QType sourceOf(int type) {
            for (QType q : values()) {
                if (q.type == type) {
                    return q;
                }
            }
            return null;
        }
    
        public int getType() {
            return type;
        }
    
        public String getName() {
            return name;
        }
    
        @Override
        public String toString() {
            return "QType{" + "type=" + type + ", name='" + name + '\'' + '}';
        }
    }
    
    Question(ByteBuffer buffer) {
        int currentPosition = buffer.position();
        buffer.position(buffer.limit() - 4);
        initQType(buffer);
        initQClass(buffer);
        
        buffer.position(currentPosition);
        initQName(buffer);
    }
    
    public Question(String qName, QType qType, int qClass) {
        this.qName = qName;
        this.qType = qType;
        this.qClass = qClass;
    }
    
    private void initQType(ByteBuffer buffer) {
        byte[] qTypeArr = new byte[2];
        buffer.get(qTypeArr, 0, 2);
        
        this.qType = QType.sourceOf(BinaryOperator.byteArrayToInt(qTypeArr));
    }
    
    private void initQClass(ByteBuffer buffer) {
        byte[] qClassArr = new byte[2];
        buffer.get(qClassArr, 0, 2);
        
        this.qClass = BinaryOperator.byteArrayToInt(qClassArr);
    }
    
    private void initQName(ByteBuffer buffer) {
        byte[] qNameArr = new byte[buffer.limit() - 4 - 12 + 1];
        buffer.get(qNameArr);
        
        List<String> list = new ArrayList<>();
        ByteBuffer cache = ByteBuffer.allocate(qNameArr.length);
        for (int i = 0; i < qNameArr.length && qNameArr[i] != 0x00; ) {
            byte len = qNameArr[i];
            int j = i + 1;
            for (; j <= i + len; j++) {
                cache.put(qNameArr[j]);
            }
            i = i + 1 + len;
            cache.flip();
            byte[] b = new byte[cache.limit()];
            cache.get(b);
            list.add(new String(b));
            cache.clear();
        }
        qName = Joiner.on(".").join(list);
    }
    
    public String getQName() {
        return qName;
    }
    
    public QType getQType() {
        return qType;
    }
    
    public int getQClass() {
        return qClass;
    }
    
    @Override
    public String toString() {
        return "Question{" + "qName='" + qName + '\'' + ", qType=" + qType + ", qClass=" + qClass + '}';
    }
}
