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
package org.apache.camel.component.tika;

import java.io.File;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class TikaParseTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Test
    public void testDocumentParse() throws Exception {

        File document = new File("src/test/resources/test.doc");
        template.sendBody("direct:start", document);

        resultEndpoint.setExpectedMessageCount(1);

        resultEndpoint.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                Object body = exchange.getIn().getBody(String.class);
                Map<String, Object> headerMap = exchange.getIn().getHeaders();
                assertThat(body, instanceOf(String.class));
                assertThat((String) body, containsString("test"));
                assertThat(headerMap.get("TikaContent-Type"), equalTo("application/msword"));
                return true;
            }
        });
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testImageParse() throws Exception {
        File document = new File("src/test/resources/testGIF.gif");
        template.sendBody("direct:start", document);

        resultEndpoint.setExpectedMessageCount(1);

        resultEndpoint.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                Object body = exchange.getIn().getBody(String.class);
                Map<String, Object> headerMap = exchange.getIn().getHeaders();
                assertThat(body, instanceOf(String.class));
                assertThat((String) body, containsString("<body/>"));
                assertThat(headerMap.get("TikaContent-Type"), equalTo("image/gif"));
                return true;
            }
        });
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testEmptyConfigDocumentParse() throws Exception {
        File document = new File("src/test/resources/test.doc");
        template.sendBody("direct:start3", document);

        resultEndpoint.setExpectedMessageCount(1);

        resultEndpoint.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                Object body = exchange.getIn().getBody(String.class);
                Map<String, Object> headerMap = exchange.getIn().getHeaders();
                assertThat(body, instanceOf(String.class));
                assertThat((String) body, containsString("<body/>"));
                assertThat(headerMap.get("TikaContent-Type"), equalTo("application/msword"));
                return true;
            }
        });
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testRegistryConfigDocumentParse() throws Exception {
        File document = new File("src/test/resources/test.doc");
        template.sendBody("direct:start3", document);

        resultEndpoint.setExpectedMessageCount(1);

        resultEndpoint.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                Object body = exchange.getIn().getBody(String.class);
                Map<String, Object> headerMap = exchange.getIn().getHeaders();
                assertThat(body, instanceOf(String.class));
                assertThat((String) body, containsString("<body/>"));
                assertThat(headerMap.get("TikaContent-Type"), equalTo("application/msword"));
                return true;
            }
        });
        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to("tika:parse").to("mock:result");
                from("direct:start2").to("tika:parse?tikaConfigUri=src/test/resources/tika-empty.xml")
                        .to("mock:result");
                from("direct:start3").to("tika:parse?tikaConfig=#testConfig").to("mock:result");
            }
        };
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry reg = super.createRegistry();
        reg.bind("testConfig", new TikaEmptyConfig());
        return reg;
    }
}