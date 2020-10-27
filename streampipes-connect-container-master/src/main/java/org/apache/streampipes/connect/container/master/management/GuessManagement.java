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

package org.apache.streampipes.connect.container.master.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.exception.WorkerAdapterException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.message.ErrorMessage;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GuessManagement {

    private static Logger LOG = LoggerFactory.getLogger(GuessManagement.class);

    public GuessManagement() {

    }

    public GuessSchema guessSchema(AdapterDescription adapterDescription) throws AdapterException, ParseException, WorkerAdapterException {
        String workerUrl = new Utils().getWorkerUrl(adapterDescription);

        workerUrl = workerUrl + "api/v1/admin@streampipes.de/worker/guess/schema";

        ObjectMapper mapper = JacksonSerializer.getObjectMapper();

        try {
            String ad = mapper.writeValueAsString(adapterDescription);
            LOG.info("Guess schema at: " + workerUrl);
            Response requestResponse = Request.Post(workerUrl)
                    .bodyString(ad, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute();

            HttpResponse httpResponse = requestResponse.returnResponse();
            String responseString = EntityUtils.toString(httpResponse.getEntity());

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                GuessSchema guessSchema = mapper.readValue(responseString, GuessSchema.class);
                return guessSchema;
            }  else {
                    ErrorMessage errorMessage = mapper.readValue(responseString, ErrorMessage.class);

                    LOG.error(errorMessage.getElementName());
                    throw new WorkerAdapterException(errorMessage);
            }

        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new AdapterException("Error in connect worker: " + workerUrl, e);
        }
    }

}
