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

import java.nio.ByteBuffer;

/**
 * document : http://www.023wg.com/message/message/cd_feature_dns_message_format.html
 *
 * <pre>
 *   +--+--+--+--+--+--+--+
 *   |        Header      |
 *   +--+--+--+--+--+--+--+
 *   |      Question      |
 *   +--+--+--+--+--+--+--+
 *   |      Answer        |
 *   +--+--+--+--+--+--+--+
 *   |      Authority     |
 *   +--+--+--+--+--+--+--+
 *   |      Additional    |
 *   +--+--+--+--+--+--+--+
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class Message {
    
    //
    private Header header;
    private Question question;
    
    // It only appears on return
    private Answer answer;
    private Authority authority;
    private Additional additional;
    
    public Message(final byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        header = new Header(buffer);
        question = new Question(buffer);
    }
    
    public Header getHeader() {
        return header;
    }
    
    public void setHeader(Header header) {
        this.header = header;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public Answer getAnswer() {
        return answer;
    }
    
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
    
    public Authority getAuthority() {
        return authority;
    }
    
    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
    
    public Additional getAdditional() {
        return additional;
    }
    
    public void setAdditional(Additional additional) {
        this.additional = additional;
    }
    
    @Override
    public String toString() {
        return new StringBuilder()
                .append("header=").append(header)
                .append('\n')
                .append("question=").append(question)
                .append('\n')
                .append("answer=").append(answer)
                .append('\n')
                .append("authority=").append(authority)
                .append('\n')
                .append("additional=").append(additional)
                .append('\n')
                .toString();
    }
}
