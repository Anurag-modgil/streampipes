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

import {AbstractStaticPropertyRenderer} from "../base/abstract-static-property";
import {
  RuntimeOptionsRequest,
  RuntimeOptionsResponse,
  RuntimeResolvableAnyStaticProperty,
  RuntimeResolvableOneOfStaticProperty
} from "../../../core-model/gen/streampipes-model";
import {RuntimeResolvableService} from "./runtime-resolvable.service";
import {Observable} from "rxjs";
import {Directive, Input, OnChanges, SimpleChanges} from "@angular/core";
import {ConfigurationInfo} from "../../../connect/model/ConfigurationInfo";

@Directive()
export abstract class BaseRuntimeResolvableInput<T extends RuntimeResolvableAnyStaticProperty | RuntimeResolvableOneOfStaticProperty>
    extends AbstractStaticPropertyRenderer<T>
    implements OnChanges {

  @Input()
  completedStaticProperty: ConfigurationInfo;

  showOptions: boolean = false;
  loading: boolean = false;
  dependentStaticProperties: any = new Map();

  constructor(private RuntimeResolvableService: RuntimeResolvableService) {
    super();
  }

  onInit() {
    if (this.staticProperty.options.length == 0 && (!this.staticProperty.dependsOn || this.staticProperty.dependsOn.length == 0)) {
      this.loadOptionsFromRestApi();
    } else if (this.staticProperty.options.length > 0) {
      this.showOptions = true;
    }

    if (this.staticProperty.dependsOn && this.staticProperty.dependsOn.length > 0) {
      this.staticProperty.dependsOn.forEach(dp => {
        this.dependentStaticProperties.set(dp, false);
      });
    }
  }

  loadOptionsFromRestApi() {
    var resolvableOptionsParameterRequest = new RuntimeOptionsRequest();
    resolvableOptionsParameterRequest.staticProperties = this.staticProperties;
    resolvableOptionsParameterRequest.requestId = this.staticProperty.internalName;

    if (this.pipelineElement) {
      resolvableOptionsParameterRequest.inputStreams = this.pipelineElement.inputStreams;
      resolvableOptionsParameterRequest.appId = this.pipelineElement.appId;
      resolvableOptionsParameterRequest.belongsTo = this.pipelineElement.belongsTo;
    }

    this.showOptions = false;
    this.loading = true;
    let observable: Observable<RuntimeOptionsResponse> = this.adapterId ?
        this.RuntimeResolvableService.fetchRemoteOptionsForAdapter(resolvableOptionsParameterRequest, this.adapterId) :
        this.RuntimeResolvableService.fetchRemoteOptionsForPipelineElement(resolvableOptionsParameterRequest);
    observable.subscribe(msg => {
      this.staticProperty.options = msg.options;
      this.afterOptionsLoaded();
      this.loading = false;
      this.showOptions = true;
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['completedStaticProperty']) {
      if (this.completedStaticProperty != undefined) {
        this.dependentStaticProperties.set(this.completedStaticProperty.staticPropertyInternalName, this.completedStaticProperty.configured);
        if (Array.from(this.dependentStaticProperties.values()).every(v => v === true)) {
          this.loadOptionsFromRestApi();
        }
      }
    }
  }

  abstract afterOptionsLoaded();

}