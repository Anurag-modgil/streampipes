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

package org.apache.streampipes.manager.pipeline;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.serializers.json.Utils;

import java.io.IOException;
import java.net.URL;

public class TestPipelineStorage {

    public static void main(String[] args) {
        URL url = Resources.getResource("pipeline.json");
        try {
            String pipelineString = Resources.toString(url, Charsets.UTF_8);
            Pipeline pipeline = Utils.getGson().fromJson(pipelineString, Pipeline.class);
            Operations.storePipeline(pipeline);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
