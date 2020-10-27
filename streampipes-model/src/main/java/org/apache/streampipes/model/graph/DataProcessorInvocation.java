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

package org.apache.streampipes.model.graph;

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.model.util.RdfIdGenerator;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.DATA_PROCESSOR_INVOCATION)
@Entity
public class DataProcessorInvocation extends InvocableStreamPipesEntity implements Serializable {

  private static final long serialVersionUID = 865870355944824186L;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.PRODUCES)
  private SpDataStream outputStream;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_OUTPUT_STRATEGY)
  private List<OutputStrategy> outputStrategies;

  private String pathName;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_EPA_TYPE)
  private List<String> category;

  public DataProcessorInvocation(DataProcessorDescription sepa) {
    super();
    this.setName(sepa.getName());
    this.setDescription(sepa.getDescription());
    this.setIconUrl(sepa.getIconUrl());
    this.setInputStreams(sepa.getSpDataStreams());
    this.setSupportedGrounding(sepa.getSupportedGrounding());
    this.setStaticProperties(sepa.getStaticProperties());
    this.setOutputStrategies(sepa.getOutputStrategies());
    this.setBelongsTo(sepa.getElementId());
    this.category = sepa.getCategory();
    this.setStreamRequirements(sepa.getSpDataStreams());
    this.setAppId(sepa.getAppId());
    this.setIncludesAssets(sepa.isIncludesAssets());
    this.setElementId(RdfIdGenerator.makeRdfId(this));
    //this.setUri(belongsTo +"/" +getElementId());
  }

  public DataProcessorInvocation(DataProcessorInvocation other) {
    super(other);
    this.outputStrategies = new Cloner().strategies(other.getOutputStrategies());
    if (other.getOutputStream() != null) {
      this.outputStream = new Cloner().stream(other.getOutputStream());
    }
    this.pathName = other.getPathName();
    this.category = new Cloner().epaTypes(other.getCategory());
  }

  public DataProcessorInvocation(DataProcessorDescription sepa, String domId) {
    this(sepa);
    this.DOM = domId;
  }

  public DataProcessorInvocation() {
    super();
    inputStreams = new ArrayList<>();
  }

  public DataProcessorInvocation(String uri, String name, String description, String iconUrl, String pathName, List<SpDataStream> spDataStreams, List<StaticProperty> staticProperties) {
    super(uri, name, description, iconUrl);
    this.pathName = pathName;
    this.inputStreams = spDataStreams;
    this.staticProperties = staticProperties;
  }

  public DataProcessorInvocation(String uri, String name, String description, String iconUrl, String pathName) {
    super(uri, name, description, iconUrl);
    this.pathName = pathName;
    inputStreams = new ArrayList<>();
    staticProperties = new ArrayList<>();
  }

  public boolean addInputStream(SpDataStream spDataStream) {
    return inputStreams.add(spDataStream);
  }


  public String getPathName() {
    return pathName;
  }

  public void setPathName(String pathName) {
    this.pathName = pathName;
  }

  public SpDataStream getOutputStream() {
    return outputStream;
  }

  public void setOutputStream(SpDataStream outputStream) {
    this.outputStream = outputStream;
  }

  public List<OutputStrategy> getOutputStrategies() {
    return outputStrategies;
  }

  public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
    this.outputStrategies = outputStrategies;
  }

  public List<String> getCategory() {
    return category;
  }

  public void setCategory(List<String> category) {
    this.category = category;
  }

}
