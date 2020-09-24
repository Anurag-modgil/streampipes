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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamContainer;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.model.template.PipelineTemplateDescriptionContainer;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;
import org.apache.streampipes.rest.api.IPipelineTemplate;
import org.apache.streampipes.rest.shared.util.SpMediaType;
import org.apache.streampipes.serializers.jsonld.JsonLdTransformer;
import org.apache.streampipes.vocabulary.StreamPipes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/pipeline-templates")
public class PipelineTemplate extends AbstractRestInterface implements IPipelineTemplate {

  @GET
  @Path("/streams")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getAvailableDataStreams() {
    List<DataSourceDescription> sources = getPipelineElementRdfStorage().getAllDataSources();
    List<SpDataStream> datasets = new ArrayList<>();

    for (DataSourceDescription source : sources) {
      source
              .getSpDataStreams()
              .stream()
              .filter(stream -> !(stream instanceof SpDataSet))
              .map(SpDataStream::new)
              .forEach(datasets::add);
    }

    return ok(toJsonLd(new SpDataStreamContainer(datasets)));
  }

  @GET
  @Path("/sets")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getAvailableDataSets() {

    List<DataSourceDescription> sources = getPipelineElementRdfStorage().getAllDataSources();
    List<SpDataStream> datasets = new ArrayList<>();

    for (DataSourceDescription source : sources) {
      source
              .getSpDataStreams()
              .stream()
              .filter(stream -> stream instanceof SpDataSet)
              .map(stream -> new SpDataSet((SpDataSet) stream))
              .forEach(set -> datasets.add((SpDataSet) set));
    }

    return ok(toJsonLd(new SpDataStreamContainer(datasets)));
  }

  @GET
  @Produces(SpMediaType.JSONLD)
  @Override
  public Response getPipelineTemplates(@QueryParam("streamId") String streamId) {
    if (streamId != null) {
      return ok(new PipelineTemplateDescriptionContainer(Operations.getCompatiblePipelineTemplates(streamId)));
    } else {
      PipelineTemplateDescriptionContainer container = new PipelineTemplateDescriptionContainer(Operations.getAllPipelineTemplates());
      return ok(container);
    }
  }

  @GET
  @Path("/invocation")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getPipelineTemplateInvocation(@QueryParam("streamId") String streamId,
                                                @QueryParam("templateId") String pipelineTemplateId) {
    if (pipelineTemplateId != null) {
      SpDataStream dataStream = getDataStream(streamId);
      PipelineTemplateDescription pipelineTemplateDescription = getPipelineTemplateDescription(pipelineTemplateId);
      PipelineTemplateInvocation invocation = Operations.getPipelineInvocationTemplate(dataStream, pipelineTemplateDescription);
      PipelineTemplateInvocation clonedInvocation = new PipelineTemplateInvocation(invocation);
      return ok(toJsonLd(new PipelineTemplateInvocation(clonedInvocation)));
    } else {
      return fail();
    }
  }

  private PipelineTemplateDescription getPipelineTemplateDescription(String pipelineTemplateId) {
    return Operations
            .getAllPipelineTemplates()
            .stream()
            .filter(pt -> pt.getAppId().equals(pipelineTemplateId))
            .findFirst()
            .get();
  }

  private List<SpDataStream> getAllDataStreams() {
    List<DataSourceDescription> sources = getPipelineElementRdfStorage().getAllDataSources();
    List<SpDataStream> datasets = new ArrayList<>();

    for (DataSourceDescription source : sources) {
      datasets.addAll(source
              .getSpDataStreams());
    }

    return datasets;
  }

  private SpDataStream getDataStream(String streamId) {
    return getAllDataStreams()
            .stream()
            .filter(sp -> sp.getElementId().equals(streamId))
            .findFirst()
            .get();
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response generatePipeline(@PathParam("username") String username,
                                   String pipelineTemplateInvocationString) {
    try {
      PipelineTemplateInvocation pipelineTemplateInvocation =
              new JsonLdTransformer(StreamPipes.PIPELINE_TEMPLATE_INVOCATION)
                      .fromJsonLd(pipelineTemplateInvocationString, PipelineTemplateInvocation.class);

      PipelineOperationStatus status = Operations
              .handlePipelineTemplateInvocation(username, pipelineTemplateInvocation);

      return ok(status);

    } catch (IOException e) {
      e.printStackTrace();
      return fail();
    }
  }
}
