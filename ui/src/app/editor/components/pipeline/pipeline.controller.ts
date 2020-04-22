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

import * as angular from "angular";

import {PipelineValidationService} from "../../services/pipeline-validation.service";
import {RestApi} from "../../../services/rest-api.service";

export class PipelineController {

    $timeout: any;
    JsplumbService: any;
    PipelineEditorService: any;
    PipelineValidationService: PipelineValidationService;
    JsplumbBridge: any;
    ObjectProvider: any;
    DialogBuilder: any;
    plumbReady: any;
    objectProvider: any;
    EditorDialogManager: any;
    currentMouseOverElement: any;
    currentPipelineModel: any;
    idCounter: any;
    currentZoomLevel: any;
    canvasId: any;
    preview: any;
    rawPipelineModel: any;
    TransitionService: any;
    ShepherdService: any;
    $rootScope: any;
    RestApi: RestApi;

    pipelineCacheRunning: boolean;
    pipelineCached: boolean;

    pipelineValid: boolean = false;

    constructor($timeout, JsplumbService, PipelineEditorService, JsplumbBridge, ObjectProvider, DialogBuilder,
                EditorDialogManager, TransitionService, ShepherdService, $rootScope, PipelineValidationService, RestApi) {
        this.plumbReady = false;
        this.JsplumbBridge = JsplumbBridge;
        this.JsplumbService = JsplumbService;
        this.PipelineEditorService = PipelineEditorService;
        this.$timeout = $timeout;
        this.objectProvider = ObjectProvider;
        this.DialogBuilder = DialogBuilder;
        this.EditorDialogManager = EditorDialogManager;
        this.currentMouseOverElement = "";
        this.TransitionService = TransitionService;
        this.ShepherdService = ShepherdService;
        this.$rootScope = $rootScope;
        this.PipelineValidationService = PipelineValidationService;
        this.RestApi = RestApi;

        this.currentPipelineModel = {};
        this.idCounter = 0;

        this.currentZoomLevel = 1;
    }

    $onInit() {
        this.JsplumbBridge.setContainer(this.canvasId);
        this.initAssembly();
        this.initPlumb();
        //this.validatePipeline();
    }

    validatePipeline() {
        this.$timeout(() => {
            this.pipelineValid = this.PipelineValidationService.isValidPipeline(this.rawPipelineModel);
        }, 200);
    }

    $onDestroy() {
        this.JsplumbBridge.deleteEveryEndpoint();
        //this.JsplumbBridge.reset();
        this.plumbReady = false;
    }

    updateMouseover(elementId) {
        this.currentMouseOverElement = elementId;
    }

    updateOptionsClick(elementId) {
        if (this.currentMouseOverElement == elementId) {
            this.currentMouseOverElement = "";
        } else {
            this.currentMouseOverElement = elementId;
        }
    }

    getElementCss(currentPipelineElementSettings) {
        return "position:absolute;"
            + (this.preview ? "width:75px;" : "width:110px;")
            + (this.preview ? "height:75px;" : "height:110px;")
            + "left: " + currentPipelineElementSettings.position.x + "px; "
            + "top: " + currentPipelineElementSettings.position.y + "px; "
    }

    getElementCssClasses(currentPipelineElement) {
        return currentPipelineElement.type + " " + (currentPipelineElement.settings.openCustomize ? "" : "")
            + currentPipelineElement.settings.connectable + " "
            + currentPipelineElement.settings.displaySettings;
    }

    isStreamInPipeline() {
        return this.isInPipeline('stream');
    }

    isSetInPipeline() {
        return this.isInPipeline('set');
    }

    isInPipeline(type) {
        return this.rawPipelineModel.some(x => (x.type == type && !(x.settings.disabled)));
    }

    showMixedStreamAlert() {
        this.EditorDialogManager.showMixedStreamAlert();
    }

    initAssembly() {
        ($('#assembly') as any).droppable({
            tolerance: "fit",
            drop: (element, ui) => {
                if (ui.draggable.hasClass('draggable-icon')) {
                    this.TransitionService.makePipelineAssemblyEmpty(false);
                    var pipelineElementConfig = this.JsplumbService.createNewPipelineElementConfig(ui.draggable.data("JSON"), this.PipelineEditorService.getCoordinates(ui, this.currentZoomLevel), false);
                    if ((this.isStreamInPipeline() && pipelineElementConfig.type == 'set') ||
                        this.isSetInPipeline() && pipelineElementConfig.type == 'stream') {
                        this.showMixedStreamAlert();
                    } else {
                        this.rawPipelineModel.push(pipelineElementConfig);
                        if (ui.draggable.hasClass('set')) {
                            this.$timeout(() => {
                                this.$timeout(() => {
                                    this.JsplumbService.setDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
                                });
                            });
                        }
                        else if (ui.draggable.hasClass('stream')) {
                            this.checkTopicModel(pipelineElementConfig);
                        } else if (ui.draggable.hasClass('sepa')) {
                            this.$timeout(() => {
                                this.$timeout(() => {
                                    this.JsplumbService.sepaDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
                                });
                            });
                            //Droppable Actions
                        } else if (ui.draggable.hasClass('action')) {
                             this.$timeout(() => {
                                 this.$timeout(() => {
                                    this.JsplumbService.actionDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
                                 });
                             });
                        }
                        if (this.ShepherdService.isTourActive()) {
                            this.ShepherdService.trigger("drop-" +pipelineElementConfig.type);
                        }
                    }
                }
                this.JsplumbBridge.repaintEverything();
                this.validatePipeline();
                this.triggerPipelineCacheUpdate();
            }

        }); //End #assembly.droppable()
        ($("#assembly") as any)
            .selectable({
                selected: function (event, ui) {
                },
                filter: ".connectable.stream,.connectable.sepa:not('.disabled')",
                delay: 150

            })
    }
    ;

    checkTopicModel(pipelineElementConfig) {
        this.$timeout(() => {
            this.$timeout(() => {
                this.JsplumbService.streamDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
            });
        });

        var streamDescription = pipelineElementConfig.payload;
        if (streamDescription
                .eventGrounding
                .transportProtocols[0]
                .properties.topicDefinition
                .type === "org.apache.streampipes.model.grounding.WildcardTopicDefinition") {
            this.EditorDialogManager.showCustomizeStreamDialog(streamDescription);
        }
    }

    handleDeleteOption(pipelineElement) {
        this.JsplumbBridge.removeAllEndpoints(pipelineElement.payload.DOM);
        angular.forEach(this.rawPipelineModel, pe => {
           if (pe.payload.DOM == pipelineElement.payload.DOM) {
               pe.settings.disabled = true;
           }
        });
        if (this.rawPipelineModel.every(pe => pe.settings.disabled)) {
            this.TransitionService.makePipelineAssemblyEmpty(true);
        }
        this.JsplumbBridge.repaintEverything();
        this.RestApi.updateCachedPipeline(this.rawPipelineModel);
    }

    initPlumb() {

        this.JsplumbService.prepareJsplumb();

        this.JsplumbBridge.unbind("connection");

        this.JsplumbBridge.bind("connectionMoved", (info, originalEvent) => {
            var pe = this.objectProvider.findElement(info.newTargetEndpoint.elementId, this.rawPipelineModel);
            var oldPe = this.objectProvider.findElement(info.originalTargetEndpoint.elementId, this.rawPipelineModel);
            oldPe.payload.configured = false;
            pe.payload.configured = false;
        });

        this.JsplumbBridge.bind("connectionDetached", (info, originalEvent) => {
            var pe = this.objectProvider.findElement(info.targetEndpoint.elementId, this.rawPipelineModel);
            pe.payload.configured = false;
            pe.settings.openCustomize = true;
            info.targetEndpoint.setType("empty");
            this.validatePipeline();
        });

        this.JsplumbBridge.bind("connectionDrag", connection => {
            this.JsplumbBridge.selectEndpoints().each(function (endpoint) {
                if (endpoint.isTarget && endpoint.connections.length === 0) {
                    endpoint.setType("highlight");
                }
            });

        });
        this.JsplumbBridge.bind("connectionAborted", connection => {
            this.JsplumbBridge.selectEndpoints().each(endpoint => {
                if (endpoint.isTarget && endpoint.connections.length === 0) {
                    endpoint.setType("empty");
                }
            });
        })

        this.JsplumbBridge.bind("connection", (info, originalEvent) => {
            var pe = this.objectProvider.findElement(info.target.id, this.rawPipelineModel);
            if (pe.settings.openCustomize) {
                this.currentPipelineModel = this.objectProvider.makePipeline(this.rawPipelineModel);
                pe.settings.loadingStatus = true;
                this.objectProvider.updatePipeline(this.currentPipelineModel)
                    .then(msg => {
                        let data = msg.data;
                        pe.settings.loadingStatus = false;
                        if (data.success) {
                            info.targetEndpoint.setType("token");
                            this.validatePipeline();
                            this.modifyPipeline(data.pipelineModifications);
                            var sourceEndpoint = this.JsplumbBridge.selectEndpoints({element: info.targetEndpoint.elementId});
                            if (this.PipelineEditorService.isFullyConnected(pe)) {
                                if ((pe.payload.staticProperties && pe.payload.staticProperties.length > 0) || this.isCustomOutput(pe)) {
                                    this.EditorDialogManager.showCustomizeDialog($("#" +pe.payload.DOM), sourceEndpoint, pe.payload, false)
                                        .then(() => {
                                            this.JsplumbService.activateEndpoint(pe.payload.DOM, !pe.payload.uncompleted);
                                        }, () => {
                                            this.JsplumbService.activateEndpoint(pe.payload.DOM, !pe.payload.uncompleted);
                                        });
                                } else {
                                    this.$rootScope.$broadcast("SepaElementConfigured", pe.payload.DOM);
                                    pe.payload.configured = true;
                                }
                            }
                        } else {
                            this.JsplumbBridge.detach(info.connection);
                                    this.EditorDialogManager.showMatchingErrorDialog(data);
                        }
                    });
            }
        });

        window.onresize = (event) => {
            this.JsplumbBridge.repaintEverything();
        };

        this.$timeout(() => {
            this.plumbReady = true;
        }, 100);
    }

    modifyPipeline(pipelineModifications) {
        for (var i = 0, modification; modification = pipelineModifications[i]; i++) {
            var id = modification.domId;
            if (id !== "undefined") {
                var pe = this.objectProvider.findElement(id, this.rawPipelineModel);
                pe.payload.staticProperties = modification.staticProperties;
                pe.payload.outputStrategies = modification.outputStrategies;
                pe.payload.inputStreams = modification.inputStreams;
            }
        }
    }

    isCustomOutput(pe) {
        var custom = false;
        angular.forEach(pe.payload.outputStrategies, strategy => {
            if (strategy.type == 'org.apache.streampipes.model.output.CustomOutputStrategy') {
                custom = true;
            }
        });
        return custom;
    }

    triggerPipelineCacheUpdate() {
        this.pipelineCacheRunning = true;
        this.RestApi.updateCachedPipeline(this.rawPipelineModel).then(msg => {
           this.pipelineCacheRunning = false;
           this.pipelineCached = true;
        });
    }


}

PipelineController.$inject = ['$timeout',
    'JsplumbService',
    'PipelineEditorService',
    'JsplumbBridge',
    'ObjectProvider',
    'DialogBuilder',
    'EditorDialogManager',
    'TransitionService',
    'ShepherdService',
    '$rootScope',
    'PipelineValidationService',
    'RestApi'];