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

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.couchdb.impl.AdapterStorageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class WorkerRestClient {

    private static final Logger logger = LoggerFactory.getLogger(WorkerRestClient.class);


    public static void invokeStreamAdapter(String baseUrl, AdapterStreamDescription adapterStreamDescription) throws AdapterException {

        String url = baseUrl + "worker/stream/invoke";

        startAdapter(url, adapterStreamDescription);
    }

    public static void stopStreamAdapter(String baseUrl, AdapterStreamDescription adapterStreamDescription) throws AdapterException {
        String url = baseUrl + "worker/stream/stop";

        AdapterDescription ad = getAdapterDescriptionById(new AdapterStorageImpl(), adapterStreamDescription.getUri());

        stopAdapter(adapterStreamDescription.getId(), ad, url);
    }

    public static void invokeSetAdapter(String baseUrl, AdapterSetDescription adapterSetDescription) throws AdapterException {
        String url = baseUrl + "worker/set/invoke";

        startAdapter(url, adapterSetDescription);
    }

    public static void stopSetAdapter(String baseUrl, AdapterSetDescription adapterSetDescription) throws AdapterException {
        String url = baseUrl + "worker/set/stop";

        stopAdapter(adapterSetDescription.getUri(), adapterSetDescription, url);
    }

    public static void startAdapter(String url, AdapterDescription ad) throws AdapterException {
        try {
            logger.info("Trying to start adpater on endpoint: " + url);

            // this ensures that all adapters have a valid uri otherwise the json-ld serializer fails
            if (ad.getUri() == null) {
                ad.setUri("https://streampipes.org/adapter/" + UUID.randomUUID());
            }

            String adapterDescription = JacksonSerializer.getObjectMapper().writeValueAsString(ad);

            String responseString = Request.Post(url)
                    .bodyString(adapterDescription, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            logger.info("Adapter started on endpoint: " + url + " with Response: " + responseString);

        } catch (IOException e) {
            logger.error("Adapter did not start", e);
            throw new AdapterException("Adapter with URL: " + url + " did not start");
        }
    }


    public static void stopAdapter(String adapterId, AdapterDescription ad, String url) throws AdapterException {

        // Stop execution of adatper
        try {
            logger.info("Trying to stopAdapter adpater on endpoint: " + url);

            String adapterDescription = JacksonSerializer.getObjectMapper().writeValueAsString(ad);

            // TODO change this to a delete request
            String responseString = Request.Post(url)
                    .bodyString(adapterDescription, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            logger.info("Adapter stopped on endpoint: " + url + " with Response: " + responseString);

        } catch (IOException e) {
            logger.error("Adapter was not stopped successfully", e);
            throw new AdapterException("Adapter was not stopped successfully with url: " + url);
        }

    }

    public static RuntimeOptionsResponse getConfiguration(String workerEndpoint, String elementId, String username, RuntimeOptionsRequest runtimeOptionsRequest) throws AdapterException {
        String element = encodeValue(elementId);
        String url = workerEndpoint + "api/v1/" + username + "/worker/resolvable/" + element + "/configurations";

        try {
            String payload = JacksonSerializer.getObjectMapper().writeValueAsString(runtimeOptionsRequest);
            String responseString = Request.Post(url)
                       .bodyString(payload, ContentType.APPLICATION_JSON)
                       .connectTimeout(1000)
                       .socketTimeout(100000)
                       .execute().returnContent().asString();

            return JacksonSerializer.getObjectMapper().readValue(responseString, RuntimeOptionsResponse.class);

        } catch (IOException e) {
            e.printStackTrace();
            throw new AdapterException("Could not resolve runtime configurations from " + url);
        }

    }

    public static String saveFileAtWorker(String baseUrl, InputStream inputStream, String fileName) throws AdapterException {
        String url = baseUrl + "worker/file";
        logger.info("Trying to start save file on endpoint: " + url);


        MultipartEntity httpEntity = new MultipartEntity();
        httpEntity.addPart("file_upload", new InputStreamBody(inputStream, fileName));

        try {
            String responseString = Request.Post(url)
                    .body(httpEntity)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            logger.info("File saved successfully at worker");
            return responseString;
        } catch (IOException e) {
            e.printStackTrace();
            throw new AdapterException("Could not save file on endpoint " + url);
        }
    }

    public static InputStream getFileFromWorker(String baseUrl, String fileName) throws AdapterException {
        String url = baseUrl + "worker/file/" + fileName;
        logger.info("Trying to get file from endpoint: " + url);

        try {
            InputStream inputStream = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asStream();

            logger.info("Got File from worker successfully from worker");
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
            throw new AdapterException("Could not get file from endpoint " + url);
        }
    }

    public static List<String> getAllFilePathsFromWorker(java.lang.String baseUrl) throws AdapterException {
        String url = baseUrl + "worker/file";
        logger.info("Trying to get file paths from endpoint: " + url);

        try {
            String stringResponse = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();
            List<String> paths = JacksonSerializer.getObjectMapper().readValue(stringResponse, List.class);

            logger.info("Got File paths successfully");
            return paths;
        } catch (IOException e) {
            e.printStackTrace();
            throw new AdapterException("Could not get file from endpoint " + url);
        }
    }

    public static void deleteFileFromWorker(String baseUrl, String fileName) throws AdapterException {
        String url = baseUrl + "worker/file/" + fileName;
        logger.info("Trying to delete file from endpoint: " + url);

        try {
            int statusCode = Request.Delete(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnResponse().getStatusLine().getStatusCode();

            if (statusCode == 200) {
                logger.info("Deleted File successfully");
            } else {
                throw new AdapterException("Could not delete file from endpoint " + url);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new AdapterException("Could not delete file from endpoint " + url);
        }
    }

    public static String getAdapterAssets(String baseUrl,  AdapterDescription ad) throws AdapterException {
        return getAssets(baseUrl + "worker/adapters", ad.getAppId());
    }

    public static String getProtocolAssets(String baseUrl,  ProtocolDescription ad) throws AdapterException {
        return getAssets(baseUrl + "worker/protocol", ad.getAppId());
    }

    private static String getAssets(String baseUrl,  String  appId) throws AdapterException {
        String url = baseUrl + "/" + appId + "/assets";
        logger.info("Trying to Assets from endpoint: " + url + " for adapter: " + appId);

        try {
            String responseString = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();
            return responseString;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new AdapterException("Could not get assets endpoint: " + url + " for adapter: " + appId);
        }

    }

    public static byte[] getAdapterIconAsset(String baseUrl,  AdapterDescription ad) throws AdapterException {
        return getIconAsset(baseUrl + "worker/adapters", ad.getAppId());
    }

    public static byte[] getProtocolIconAsset(String baseUrl,  ProtocolDescription ad) throws AdapterException {
        return getIconAsset(baseUrl + "worker/protocols", ad.getAppId());

    }

    private static byte[] getIconAsset(String baseUrl,  String appId) throws AdapterException {
        String url = baseUrl + "/" + appId + "/assets/icon";
        logger.info("Trying to get icon from endpoint: " + url);

        try {
            byte[] responseString = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asBytes();
            return responseString;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new AdapterException("Could not get icon endpoint: " + url);
        }
    }

    public static String getAdapterDocumentationAsset(String baseUrl,  AdapterDescription ad) throws AdapterException {
        return getDocumentationAsset(baseUrl + "worker/adapters", ad.getAppId());
    }

    public static String getProtocolDocumentationAsset(String baseUrl,  ProtocolDescription ad) throws AdapterException {
        return getDocumentationAsset(baseUrl + "worker/protocols", ad.getAppId());
    }

    private static String getDocumentationAsset(String baseUrl,  String appId) throws AdapterException  {
        String url = baseUrl + "/" + appId + "/assets/documentation";
        logger.info("Trying to documentation from endpoint: " + url);

        try {
            String responseString = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();
            return responseString;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new AdapterException("Could not get documentation endpoint: " + url + " for adapter: " + appId);
        }
    }


    private static AdapterDescription getAdapterDescriptionById(AdapterStorageImpl adapterStorage, String id) {
        AdapterDescription adapterDescription = null;
        List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();
        for (AdapterDescription a : allAdapters) {
            if (a.getUri().endsWith(id)) {
                adapterDescription = a;
            }
        }

        return adapterDescription;
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }




}

