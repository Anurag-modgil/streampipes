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

package org.apache.streampipes.model.connect.rules.schema;

import io.fogsy.empire.annotations.Namespaces;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@Namespaces({StreamPipes.NS_PREFIX, StreamPipes.NS})
@RdfsClass(StreamPipes.RENAME_RULE_DESCRIPTION)
@Entity
public class RenameRuleDescription extends SchemaTransformationRuleDescription {

    @RdfProperty(StreamPipes.OLD_RUNTIME_NAME)
    private String oldRuntimeKey;

    @RdfProperty(StreamPipes.NEW_RUNTIME_NAME)
    private String newRuntimeKey;

    public RenameRuleDescription() {
        super();
    }

    public RenameRuleDescription(String oldRuntimeKey, String newRuntimeKey) {
        super();
        this.oldRuntimeKey = oldRuntimeKey;
        this.newRuntimeKey = newRuntimeKey;
    }

    public RenameRuleDescription(RenameRuleDescription other) {
        super(other);
        this.oldRuntimeKey = other.getOldRuntimeKey();
        this.newRuntimeKey = other.getNewRuntimeKey();
    }

    public String getOldRuntimeKey() {
        return oldRuntimeKey;
    }

    public void setOldRuntimeKey(String oldRuntimeKey) {
        this.oldRuntimeKey = oldRuntimeKey;
    }

    public String getNewRuntimeKey() {
        return newRuntimeKey;
    }

    public void setNewRuntimeKey(String newRuntimeKey) {
        this.newRuntimeKey = newRuntimeKey;
    }
}
