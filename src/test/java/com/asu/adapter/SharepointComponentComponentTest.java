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
package com.asu.adapter;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;

public class SharepointComponentComponentTest extends CamelTestSupport {

    @Test
    public void testSample() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
//        mock.expectedMinimumMessageCount(1);
//
//        assertMockEndpointsSatisfied();
//
//        assertMockEndpointsSatisfied();
//        String finalResultFromProducer = mock.getExchanges().get(0).getIn().getBody(String.class);
//        System.out.println(finalResultFromProducer);
//        String expected = "HELLO WORLD2";
//        Assert.assertTrue("Did not get expected result", finalResultFromProducer.contains(expected));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("sap-sample://foo?greetingsMessage=Hello world1")
                  .to("sap-sample://bar?greetingsMessage=Hello world2")
                  .to("mock:result");
            }
        };
    }
}
