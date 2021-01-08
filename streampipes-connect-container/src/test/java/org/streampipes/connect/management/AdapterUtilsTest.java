/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.connect.management;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.apache.streampipes.connect.Mock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.*;

public class AdapterUtilsTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(Mock.PORT);


    @Test
    public void stopPipeline() {
        String expected = "";

        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(expected)));


        String result = AdapterUtils.stopPipeline(Mock.HOST + "/");

        assertEquals(expected, result);

        verify(getRequestedFor(urlMatching("/")));
    }

    @Test
    public void getUrlTest() {
        String expected = "http://host:80/api/v2/pipelines/1/stopAdapter";

        String result = AdapterUtils.getUrl("host:80/", "1");

        assertEquals(expected, result);
    }
}