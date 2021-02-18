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
package org.apache.streampipes.client.api;

import org.apache.streampipes.client.http.*;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.serializer.ListSerializer;
import org.apache.streampipes.client.serializer.ObjectSerializer;
import org.apache.streampipes.client.serializer.Serializer;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import java.util.List;

public abstract class AbstractClientApi<T> {

  protected StreamPipesClientConfig clientConfig;
  private Class<T> targetClass;

  public AbstractClientApi(StreamPipesClientConfig clientConfig, Class<T> targetClass) {
    this.clientConfig = clientConfig;
    this.targetClass = targetClass;
  }

  protected List<T> getAll(StreamPipesApiPath apiPath) throws SpRuntimeException {
    ListSerializer<Void, T> serializer = new ListSerializer<>();
    return new GetRequest<>(clientConfig, apiPath, targetClass, serializer).executeRequest();
  }

  protected T getSingle(StreamPipesApiPath apiPath) throws SpRuntimeException {
    ObjectSerializer<Void, T> serializer = new ObjectSerializer<>();
    return new GetRequest<>(clientConfig, apiPath, targetClass, serializer).executeRequest();
  }

  protected <O> O getSingle(StreamPipesApiPath apiPath, Class<O> targetClass) throws SpRuntimeException {
    ObjectSerializer<Void, O> serializer = new ObjectSerializer<>();
    return new GetRequest<>(clientConfig, apiPath, targetClass, serializer).executeRequest();
  }

  protected <O> O post(StreamPipesApiPath apiPath, T object, Class<O> responseClass) {
    ObjectSerializer<T, O> serializer = new ObjectSerializer<>();
    return new PostRequestWithPayloadResponse<>(clientConfig, apiPath, serializer, object, responseClass)
            .executeRequest();
  }

  protected <O> O post(StreamPipesApiPath apiPath, Class<O> responseClass) {
    ObjectSerializer<Void, O> serializer = new ObjectSerializer<>();
    return new PostRequestWithPayloadResponse<>(clientConfig, apiPath, serializer, responseClass).executeRequest();
  }

  protected void post(StreamPipesApiPath apiPath, T object) {
    ObjectSerializer<T, Void> serializer = new ObjectSerializer<>();
    new PostRequestWithoutPayloadResponse<>(clientConfig, apiPath, serializer, object).executeRequest();
  }

  protected <O> O delete(StreamPipesApiPath apiPath, Class<O> responseClass) {
    Serializer<Void, O, O> serializer = new ObjectSerializer<>();
    return new DeleteRequest<>(clientConfig, apiPath, responseClass, serializer).executeRequest();
  }

  protected abstract StreamPipesApiPath getBaseResourcePath();
}
