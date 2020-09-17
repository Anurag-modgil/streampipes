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

package org.apache.streampipes.manager.matching;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphHelpers;
import org.apache.streampipes.manager.matching.output.OutputSchemaFactory;
import org.apache.streampipes.manager.matching.output.OutputSchemaGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InvocationGraphBuilder {

  private PipelineGraph pipelineGraph;
  private String pipelineId;
  private Integer uniquePeIndex = 0;

  private List<InvocableStreamPipesEntity> graphs;

  public InvocationGraphBuilder(PipelineGraph pipelineGraph, String pipelineId) {
    this.graphs = new ArrayList<>();
    this.pipelineGraph = pipelineGraph;
    this.pipelineId = pipelineId;

  }

  public List<InvocableStreamPipesEntity> buildGraphs() {

    List<SpDataStream> streams = PipelineGraphHelpers.findStreams(pipelineGraph);

    for (SpDataStream stream : streams) {
      Set<InvocableStreamPipesEntity> connectedElements = getConnections(stream);
      configure(stream, connectedElements);
    }

    return graphs;
  }

  private void configure(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {

    EventGrounding inputGrounding = new GroundingBuilder(source, targets)
            .getEventGrounding();

    if (source instanceof InvocableStreamPipesEntity) {
      if (source instanceof DataProcessorInvocation && ((DataProcessorInvocation) source).isConfigured()) {

        DataProcessorInvocation dataProcessorInvocation = (DataProcessorInvocation) source;
        Tuple2<EventSchema, ? extends OutputStrategy> outputSettings;
        OutputSchemaGenerator<?> schemaGenerator = new OutputSchemaFactory(dataProcessorInvocation)
                .getOuputSchemaGenerator();

        if (((DataProcessorInvocation) source).getInputStreams().size() == 1) {
          outputSettings = schemaGenerator.buildFromOneStream(dataProcessorInvocation
                  .getInputStreams()
                  .get(0));
        } else if (graphExists(dataProcessorInvocation.getDOM())) {
          DataProcessorInvocation existingInvocation = (DataProcessorInvocation) find(dataProcessorInvocation.getDOM());

          outputSettings = schemaGenerator.buildFromTwoStreams(existingInvocation
                  .getInputStreams().get(0), dataProcessorInvocation.getInputStreams().get(1));
          graphs.remove(existingInvocation);
        } else {
          outputSettings = new Tuple2<>(new EventSchema(), dataProcessorInvocation
                  .getOutputStrategies().get(0));
        }

        SpDataStream outputStream = new SpDataStream();
        outputStream.setEventGrounding(inputGrounding);
        dataProcessorInvocation.setOutputStrategies(Collections.singletonList(outputSettings.b));
        outputStream.setEventSchema(outputSettings.a);
        ((DataProcessorInvocation) source).setOutputStream(outputStream);
      }

      if (!graphExists(source.getDOM())) {
        graphs.add((InvocableStreamPipesEntity) source);
      }
    }

    targets.forEach(t -> {
      t.getInputStreams()
              .get(getIndex(source.getDOM(), t))
              .setEventGrounding(inputGrounding);

      t.getInputStreams()
              .get(getIndex(source.getDOM(), t))
              .setEventSchema(getInputSchema(source));

      String elementIdentifier = makeElementIdentifier(pipelineId, inputGrounding
              .getTransportProtocol().getTopicDefinition().getActualTopicName(), t.getName());

      t.setElementId(t.getBelongsTo() + "/" + elementIdentifier);
      t.setCorrespondingPipeline(pipelineId);
      t.setStatusInfoSettings(makeStatusInfoSettings(elementIdentifier));

      uniquePeIndex++;

      configure(t, getConnections(t));

    });

  }

  private ElementStatusInfoSettings makeStatusInfoSettings(String elementIdentifier) {
    ElementStatusInfoSettings statusSettings = new ElementStatusInfoSettings();
    statusSettings.setKafkaHost(BackendConfig.INSTANCE.getKafkaHost());
    statusSettings.setKafkaPort(BackendConfig.INSTANCE.getKafkaPort());
    statusSettings.setErrorTopic(elementIdentifier + ".error");
    statusSettings.setStatsTopic(elementIdentifier + ".stats");
    statusSettings.setElementIdentifier(elementIdentifier);

    return statusSettings;
  }

  private String makeElementIdentifier(String pipelineId, String topic, String elementName) {
    return pipelineId
            + "-"
            + topic
            + "-"
            + elementName.replaceAll(" ", "").toLowerCase()
            + "-"
            + uniquePeIndex;
  }

  private EventSchema getInputSchema(NamedStreamPipesEntity source) {
    if (source instanceof SpDataStream) {
      return ((SpDataStream) source).getEventSchema();
    } else if (source instanceof DataProcessorInvocation) {
      return ((DataProcessorInvocation) source)
              .getOutputStream()
              .getEventSchema();
    } else {
      throw new IllegalArgumentException();
    }
  }

  private Set<InvocableStreamPipesEntity> getConnections(NamedStreamPipesEntity source) {
    Set<String> outgoingEdges = pipelineGraph.outgoingEdgesOf(source);
    return outgoingEdges
            .stream()
            .map(o -> pipelineGraph.getEdgeTarget(o))
            .map(g -> (InvocableStreamPipesEntity) g)
            .collect(Collectors.toSet());

  }

  private Integer getIndex(String sourceDomId, InvocableStreamPipesEntity targetElement) {
    return targetElement.getConnectedTo().indexOf(sourceDomId);
  }

  private boolean graphExists(String domId) {
    return graphs
            .stream()
            .anyMatch(g -> g.getDOM().equals(domId));
  }

  private InvocableStreamPipesEntity find(String domId) {
    return graphs
            .stream()
            .filter(g -> g.getDOM().equals(domId))
            .findFirst()
            .get();
  }

}