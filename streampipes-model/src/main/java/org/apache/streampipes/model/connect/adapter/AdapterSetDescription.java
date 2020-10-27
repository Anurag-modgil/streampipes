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

package org.apache.streampipes.model.connect.adapter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.fogsy.empire.annotations.Namespaces;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.SpDataSet;

import javax.persistence.Entity;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:AdapterSetDescription")
@Entity
@JsonSubTypes({
        @JsonSubTypes.Type(GenericAdapterSetDescription.class),
        @JsonSubTypes.Type(SpecificAdapterSetDescription.class)
})
public abstract class AdapterSetDescription extends AdapterDescription {

    public AdapterSetDescription() {
    }

    public AdapterSetDescription(String uri, String name, String description) {
        super(uri, name, description);
    }

    public AdapterSetDescription(AdapterSetDescription other) {
        super(other);
        if (other.getDataSet() != null) this.setDataSet(new SpDataSet(other.getDataSet()));
    }

    @RdfProperty("sp:hasDataSet")
    private SpDataSet dataSet;

    @RdfProperty("sp:stopPipeline")
    private boolean stopPipeline;

    public SpDataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(SpDataSet dataSet) {
        this.dataSet = dataSet;
    }

    public boolean isStopPipeline() {
        return stopPipeline;
    }

    public void setStopPipeline(boolean stopPipeline) {
        this.stopPipeline = stopPipeline;
    }
}
