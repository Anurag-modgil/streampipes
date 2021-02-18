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
package org.apache.streampipes.client;

import org.apache.streampipes.client.api.*;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;

public class StreamPipesClient implements SupportsPipelineApi,
        SupportsPipelineElementTemplateApi,
        SupportsDataSinkApi,
        SupportsDataStreamApi,
        SupportsDataProcessorApi {

  private static final Integer SP_DEFAULT_PORT = 80;

  private StreamPipesClientConfig config;

  /**
   * Create a new StreamPipes API client with default port and custom HTTPS settings
   * @param streamPipesHost The hostname of the StreamPipes instance without scheme
   * @param credentials The credentials object
   * @param httpsDisabled Set true if the instance is not served over HTTPS
   */
  public static StreamPipesClient create(String streamPipesHost,
                                         StreamPipesCredentials credentials,
                                         boolean httpsDisabled) {
    return new StreamPipesClient(streamPipesHost, SP_DEFAULT_PORT, credentials, httpsDisabled);
  }

  /**
   * Create a new StreamPipes API client with default port 80 and HTTPS settings (HTTPS enabled)
   * @param streamPipesHost The hostname of the StreamPipes instance without scheme
   * @param credentials The credentials object
   */
  public static StreamPipesClient create(String streamPipesHost,
                                         StreamPipesCredentials credentials) {
    return new StreamPipesClient(streamPipesHost, SP_DEFAULT_PORT, credentials, false);
  }

  /**
   * Create a new StreamPipes API client with custom port and default HTTPS settings
   * @param streamPipesHost The hostname of the StreamPipes instance without scheme
   * @param streamPipesPort The port of the StreamPipes instance
   * @param credentials The credentials object
   */
  public static StreamPipesClient create(String streamPipesHost,
                                         Integer streamPipesPort,
                                         StreamPipesCredentials credentials) {
    return new StreamPipesClient(streamPipesHost, streamPipesPort, credentials, false);
  }

  /**
   * Create a new StreamPipes API client with custom port and HTTPS settings
   * @param streamPipesHost The hostname of the StreamPipes instance without scheme
   * @param streamPipesPort The port of the StreamPipes instance
   * @param credentials The credentials object
   * @param httpsDisabled Set true if the instance is not served over HTTPS
   */
  public static StreamPipesClient create(String streamPipesHost,
                                         Integer streamPipesPort,
                                         StreamPipesCredentials credentials,
                                         boolean httpsDisabled) {
    return new StreamPipesClient(streamPipesHost, streamPipesPort, credentials, httpsDisabled);
  }

  private StreamPipesClient(String streamPipesHost,
                            Integer streamPipesPort,
                            StreamPipesCredentials credentials,
                            boolean httpsDisabled) {
    this.config = new StreamPipesClientConfig(credentials, streamPipesHost, streamPipesPort, httpsDisabled);
    this.registerDataFormat(new JsonDataFormatFactory());
    this.registerDataFormat(new FstDataFormatFactory());
    this.registerDataFormat(new CborDataFormatFactory());
  }

  /**
   * Register a new data format that is used by the live API
   * @param spDataFormatFactory The data format factory
   */
  public void registerDataFormat(SpDataFormatFactory spDataFormatFactory) {
    this.config.addDataFormat(spDataFormatFactory);
  }

  public StreamPipesCredentials getCredentials() {
    return config.getCredentials();
  }

  public StreamPipesClientConfig getConfig() {
    return config;
  }

  /**
   * Get API to work with pipelines
   * @return {@link org.apache.streampipes.client.api.PipelineApi}
   */
  @Override
  public PipelineApi pipelines() {
    return new PipelineApi(config);
  }

  /**
   * Get API to work with pipline element templates
   * @return {@link org.apache.streampipes.client.api.PipelineElementTemplateApi}
   */
  @Override
  public PipelineElementTemplateApi pipelineElementTemplates() {
    return new PipelineElementTemplateApi(config);
  }

  /**
   * Get API to work with data sinks
   * @return {@link org.apache.streampipes.client.api.DataSinkApi}
   */
  @Override
  public DataSinkApi sinks() {
    return new DataSinkApi(config);
  }

  /**
   * Get API to work with data streams
   * @return {@link org.apache.streampipes.client.api.DataStreamApi}
   */
  @Override
  public DataStreamApi streams() {
    return new DataStreamApi(config);
  }

  /**
   * Get API to work with data processors
   * @return {@link org.apache.streampipes.client.api.DataProcessorApi}
   */
  @Override
  public DataProcessorApi processors() {
    return new DataProcessorApi(config);
  }
}
