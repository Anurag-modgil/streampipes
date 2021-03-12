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
package org.apache.streampipes.wrapper.standalone.runtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.context.RuntimeContext;
import org.apache.streampipes.wrapper.params.binding.BindingParams;
import org.apache.streampipes.wrapper.params.runtime.RuntimeParams;
import org.apache.streampipes.wrapper.routing.RawDataProcessor;
import org.apache.streampipes.wrapper.routing.SpInputCollector;
import org.apache.streampipes.wrapper.runtime.PipelineElement;
import org.apache.streampipes.wrapper.runtime.PipelineElementRuntime;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;
import org.apache.streampipes.wrapper.standalone.routing.StandaloneReconfigurationSpInputCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class StandalonePipelineElementRuntime<B extends BindingParams<I>,
        I extends InvocableStreamPipesEntity,
        RP extends RuntimeParams<B, I, RC>,
        RC extends RuntimeContext,
        P extends PipelineElement<B, I>>
        extends PipelineElementRuntime implements RawDataProcessor {

  protected RP params;
  protected final P engine;

  public StandalonePipelineElementRuntime(Supplier<P> supplier, RP runtimeParams) {
    super();
    this.engine = supplier.get();
    this.params = runtimeParams;
  }

  public P getEngine() {
    return engine;
  }

  public void discardEngine() throws SpRuntimeException {
    engine.onDetach();
  }

  public List<SpInputCollector> getInputCollectors() throws SpRuntimeException {
    List<SpInputCollector> inputCollectors = new ArrayList<>();
    for (SpDataStream is : params.getBindingParams().getGraph().getInputStreams()) {
      inputCollectors.add(ProtocolManager.findInputCollector(is.getEventGrounding()
                      .getTransportProtocol(), is.getEventGrounding().getTransportFormats().get(0),
              params.isSingletonEngine()));
    }
    return inputCollectors;
  }

  public SpInputCollector getReconfigurationInputCollector() throws SpRuntimeException{
    ObjectMapper mapper = new ObjectMapper();
    InvocableStreamPipesEntity graph = params.getBindingParams().getGraph();
    try {
      TransportProtocol tp = mapper.readValue(mapper.writeValueAsString(graph.getInputStreams().get(0)
              .getEventGrounding().getTransportProtocol()), graph.getInputStreams().get(0)
              .getEventGrounding().getTransportProtocol().getClass());
      tp.getTopicDefinition().setActualTopicName("org.apache.streampipes.control.event.reconfigure."
              + graph.getDeploymentRunningInstanceId());

      return ProtocolManager.findReconfigurationInputCollector(tp,
              graph.getInputStreams().get(0).getEventGrounding().getTransportFormats().get(0), true);
    } catch (JsonProcessingException e) {
      throw new SpRuntimeException(e);
    }
  }

  public abstract void bindEngine() throws SpRuntimeException;


}
