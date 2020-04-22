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

import * as angular from 'angular';
import {ShepherdService} from "../../../services/tour/shepherd.service";

export class CustomizeController {

    selectedElement: any;
    selection: any;
    matchingSelectionLeft: any;
    matchingSelectionRight: any;
    invalid: any;
    helpDialogVisible: any;
    currentStaticProperty: any;
    validationErrors: any;
    configVisible: any;
    displayRecommended: boolean;
    sourceEndpoint: any;
    $mdDialog: any;
    $rootScope: any;
    sepa: any;
    customizeForm: any;
    ShepherdService: ShepherdService;
    showDocumentation: boolean = false;
    restrictedEditMode: boolean;

    constructor($rootScope, $mdDialog, elementData, sourceEndpoint, sepa, restrictedEditMode, ShepherdService) {
        this.selectedElement = sepa;
        this.selection = [];
        this.matchingSelectionLeft = [];
        this.matchingSelectionRight = [];
        this.invalid = false;
        this.helpDialogVisible = false;
        this.validationErrors = [];
        this.configVisible = false;
        this.displayRecommended = true;
        this.sourceEndpoint = sourceEndpoint;
        this.$mdDialog = $mdDialog;
        this.$rootScope = $rootScope;
        this.ShepherdService = ShepherdService;
        this.restrictedEditMode = restrictedEditMode;
    }

    $onInit() {
        if (this.ShepherdService.isTourActive()) {
            this.ShepherdService.trigger("customize-" +this.selectedElement.type);
        }
    }

    toggleHelpDialog() {
        this.helpDialogVisible = !this.helpDialogVisible;
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        if (this.customizeForm.$invalid) {
            this.selectedElement.uncompleted = true;
        }
        this.$mdDialog.cancel();
    };

    setSelectValue(c, q) {
        angular.forEach(q, function (item) {
            item.selected = false;
        });

        c.selected = true;
    };

    /**
     * saves the parameters in the current element's data with key "options"
     */
    saveProperties() {
        if (this.validate()) {
            this.selectedElement.uncompleted = false;
            this.$rootScope.$broadcast("SepaElementConfigured", this.selectedElement.DOM);
            this.selectedElement.configured = true;
            this.hide();
            if (this.sourceEndpoint) {
                this.sourceEndpoint.setType("token");
            }
            if (this.ShepherdService.isTourActive()) {
                this.ShepherdService.trigger("save-" +this.selectedElement.type);
            }
        } else {
            this.invalid = true;
        }

    }

    validate() {
        this.validationErrors = [];
        var valid = true;

        angular.forEach(this.selectedElement.staticProperties, staticProperty => {
            if (staticProperty.properties.staticPropertyType === 'OneOfStaticProperty' ||
                staticProperty.properties.staticPropertyType === 'AnyStaticProperty') {
                var anyOccurrence = false;
                angular.forEach(staticProperty.properties.options, option => {
                    if (option.selected) anyOccurrence = true;
                });
                if (!anyOccurrence) valid = false;
            } else if (staticProperty.properties.staticPropertyType === 'MappingPropertyUnary') {
                if (!staticProperty.properties.selectedProperty) {
                    valid = false;
                }

            } else if (staticProperty.properties.staticPropertyType === 'MappingPropertyNary') {
                if (staticProperty.properties.valueRequired) {
                    if (!staticProperty.properties.selectedProperties ||
                        !(staticProperty.properties.selectedProperties.length > 0)) {
                        valid = false;
                    }
                }
            }
        });

        angular.forEach(this.selectedElement.outputStrategies, strategy => {
            if (strategy.type == 'org.apache.streampipes.model.output.CustomOutputStrategy') {
                if (!strategy.properties.selectedPropertyKeys && !(strategy.properties.selectedPropertyKeys.length > 0)) {
                    valid = false;
                }
            }
            // TODO add replace output strategy
            // TODO add support for replace output strategy
        });

        return valid;
    }
}

CustomizeController.$inject = ['$rootScope', '$mdDialog', 'elementData', 'sourceEndpoint', 'sepa', 'restrictedEditMode', 'ShepherdService'];