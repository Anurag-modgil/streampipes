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

export class HelpDialogController {

    $mdDialog: any;
    pipelineElement: any;
    selectedTab = 0;
    RestApi: any;
    latestMeasurements: any;
    $timeout: any;
    pollingActive;
    error: any;
    ctrl: any;

    dtOptions = {paging: false, searching: false};
    nsPrefix = "http://www.w3.org/2001/XMLSchema#";
    tabs = [
        {
            title: 'Fields',
            type: 'fields',
            targets: ['set', 'stream']
        },
        {
            title: 'Values',
            type: 'values',
            targets: ['set', 'stream']
        },
        {
            title: 'Raw',
            type: 'raw',
            targets: ['set', 'stream']
        },
        {
            title: 'Documentation',
            type: 'documentation',
            targets: ['set', 'stream', 'sepa', 'action']
        }
    ];

    constructor($mdDialog, pipelineElement, RestApi, $timeout) {
        this.$mdDialog = $mdDialog;
        this.pipelineElement = pipelineElement;
        this.RestApi = RestApi;
        this.$timeout = $timeout;
        this.pollingActive = true;
        this.error = false;
    }

    $onInit() {
        if (this.pipelineElement.type == 'stream') {
            this.loadCurrentData();
        }
    }

    setSelectedTab(type) {
        this.selectedTab = type;
    }

    getFriendlyRuntimeType(runtimeType) {
        if (this.isNumber(runtimeType)) {
            return "Number";
        } else if (this.isBoolean(runtimeType)) {
            return "Boolean";
        } else {
            return "Text";
        }
    }

    isNumber(runtimeType) {
        return (runtimeType == (this.nsPrefix + "float")) ||
            (runtimeType == (this.nsPrefix + "integer")) ||
            (runtimeType == (this.nsPrefix + "long")) ||
            (runtimeType == (this.nsPrefix + "double"));
    }

    isBoolean(runtimeType) {
        return runtimeType == this.nsPrefix + "boolean";
    }

    hide() {
        this.pollingActive = false;
        this.$mdDialog.hide();
    };

    cancel() {
        this.pollingActive = false;
        this.$mdDialog.cancel();
    };

    loadCurrentData() {
        this.RestApi.getRuntimeInfo(this.pipelineElement).then(msg => {
            let data = msg.data;
            if (!data.notifications) {
                this.error = false;
                this.latestMeasurements = data;
            } else {
                this.error = true;
            }
            if (this.pollingActive) {
                this.$timeout(() => {
                    this.loadCurrentData();
                }, 1000)
            }
        });
    }

    filterTab(tab) {
        return tab.targets.indexOf(this.ctrl.pipelineElement.type) != -1;
    }


}

HelpDialogController.$inject = ['$mdDialog', 'pipelineElement', 'RestApi', '$timeout'];