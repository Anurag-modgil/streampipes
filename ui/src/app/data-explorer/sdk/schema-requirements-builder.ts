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

import {CollectedSchemaRequirements} from "./collected-schema-requirements";
import {
    EventPropertyUnion, MappingPropertyNary,
    MappingPropertyUnary,
    StaticPropertyUnion
} from "../../core-model/gen/streampipes-model";

export class SchemaRequirementsBuilder {

    private requiredEventProperties: Array<EventPropertyUnion>;
    private staticProperties: Array<StaticPropertyUnion>;

    private constructor() {
        this.requiredEventProperties = [];
        this.staticProperties = [];
    }

    static create(): SchemaRequirementsBuilder {
        return new SchemaRequirementsBuilder();
    }

    requiredPropertyWithUnaryMapping(internalId: string, label: string, description: string, eventProperty: EventPropertyUnion): SchemaRequirementsBuilder {
        eventProperty.runtimeName = internalId;
        let mp = this.makeMappingProperty(internalId, label, description, new MappingPropertyUnary());

        this.staticProperties.push(mp);
        this.requiredEventProperties.push(eventProperty);

        return this;
    }

    requiredPropertyWithNaryMapping(internalId: string, label: string, description: string, eventProperty: EventPropertyUnion): SchemaRequirementsBuilder {
        eventProperty.runtimeName = internalId;
        let mp = this.makeMappingProperty(internalId, label, description, new MappingPropertyNary());

        this.staticProperties.push(mp);
        this.requiredEventProperties.push(eventProperty);

        return this;
    }

    makeMappingProperty(internalId: string, label: string, description: string, sp: StaticPropertyUnion): StaticPropertyUnion {
        sp.internalName = internalId;
        sp.label = label;
        sp.description = description;
        return sp;
    }

    build() {
        return new CollectedSchemaRequirements(this.requiredEventProperties, this.staticProperties);
    }
}