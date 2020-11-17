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
package org.apache.streampipes.wrapper.siddhi.engine.generator;

import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.model.EventPropertyDef;
import org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.StringJoiner;

public class SiddhiAppGenerator<B extends EventProcessorBindingParams> {

  private static final Logger LOG = LoggerFactory.getLogger(SiddhiAppGenerator.class);

  private final SiddhiProcessorParams<B> siddhiParams;
  private final String fromStatement;
  private final String selectStatement;

  private final StringBuilder siddhiAppString;

  public SiddhiAppGenerator(SiddhiProcessorParams<B> siddhiParams,
                            String fromStatement,
                            String selectStatement) {
    this.siddhiParams = siddhiParams;
    this.fromStatement = fromStatement;
    this.selectStatement = selectStatement;
    this.siddhiAppString = new StringBuilder();
  }

  public String generateSiddhiApp() {
    LOG.info("Configuring event types for graph " + this.siddhiParams.getParams().getGraph().getName());

    this.siddhiParams.getEventTypeInfo().forEach(this::registerEventType);
    registerStatements(fromStatement, selectStatement, SiddhiUtils.getOutputTopicName(this.siddhiParams.getParams()));

    return this.siddhiAppString.toString();
  }

  private void registerEventType(String eventTypeName, List<EventPropertyDef> eventSchema) {
    String defineStreamPrefix = "define stream " + SiddhiUtils.prepareName(eventTypeName);
    StringJoiner joiner = new StringJoiner(",");

    eventSchema.forEach(typeInfo -> {
      joiner.add(typeInfo.getSelectorPrefix() + typeInfo.getFieldName() + " " + typeInfo.getFieldType());
    });

    this.siddhiAppString
            .append(defineStreamPrefix)
            .append("(")
            .append(joiner.toString())
            .append(");\n");
  }

  private void registerStatements(String fromStatement, String selectStatement, String outputStream) {
    this.siddhiAppString
            .append(fromStatement)
            .append("\n")
            .append(selectStatement)
            .append("\n")
            .append("insert into ")
            .append(SiddhiUtils.prepareName(outputStream))
            .append(";");

    LOG.info("Registering statement: \n" + this.siddhiAppString.toString());

  }
}
