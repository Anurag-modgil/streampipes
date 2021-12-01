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

package org.apache.streampipes.wrapper.spark;

import org.apache.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public abstract class SparkDataProcessorDeclarer<B extends EventProcessorBindingParams>
    extends AbstractSparkDeclarer<DataProcessorDescription, DataProcessorInvocation, SparkDataProcessorRuntime> implements SemanticEventProcessingAgentDeclarer {
    //If not overwritten elements are regarded as stateless
    //TODO: Assess if this class is needed in its current form as it is not used in extensions atm
    @Override
    public void setState(String state) {
    }

    @Override
    public String getState() {
        return null;
    }
}
