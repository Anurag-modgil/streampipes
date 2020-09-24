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
package org.apache.streampipes.rest.shared.serializer;

import org.apache.commons.io.IOUtils;
import org.apache.streampipes.rest.shared.util.JsonLdUtils;
import org.apache.streampipes.rest.shared.util.SpMediaType;
import org.apache.streampipes.serializers.jsonld.JsonLdTransformer;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(SpMediaType.JSONLD)
@Consumes(SpMediaType.JSONLD)
public class JsonLdProvider implements MessageBodyWriter<Object>,
        MessageBodyReader<Object> {

  private final String UTF8 = "UTF-8";

  @Override
  public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return jsonLdSerialized(mediaType);
  }

  @Override
  public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
    StringWriter writer = new StringWriter();
    IOUtils.copy(entityStream, writer, UTF8);
    String bodyContent = writer.toString();

    return new JsonLdTransformer().fromJsonLd(bodyContent, type);
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return jsonLdSerialized(mediaType);
  }

  @Override
  public long getSize(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

    try (OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF8)) {
      writer.write(JsonLdUtils.toJsonLD(o));
    }
  }

  protected boolean jsonLdSerialized(MediaType mediaType) {
    return mediaType.getType().equals(SpMediaType.JSONLD_TYPE.getType()) &&
            mediaType.getSubtype().equals(SpMediaType.JSONLD_TYPE.getSubtype());
  }
}
