package com.asu.adapter;
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Sample.com consumer.
 */
public class SharepointComponentConsumer extends ScheduledPollConsumer {
    private Logger LOG = LoggerFactory.getLogger(SharepointComponentConsumer.class);

    private final SharepointComponentEndpoint endpoint;


    public SharepointComponentConsumer(final SharepointComponentEndpoint endpoint, final Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = endpoint.createExchange();

         // create a message body
        String greetingsMessage = endpoint.getGreetingsMessage();
        Date now = new Date();
        if(greetingsMessage == null || greetingsMessage.isEmpty()){
        	LOG.error("The message is empty! Default one will be used");
        	greetingsMessage = " Hello There!! ";
        }
        StringBuilder builder = new StringBuilder(greetingsMessage);
        builder.append(" (Consumer) Now it is ");
        builder.append(now.toString());
        
        exchange.getIn().setBody(builder.toString());

        try {
            // send message to next processor in the route
            getProcessor().process(exchange);
            return 1; // number of messages polled
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }
}
