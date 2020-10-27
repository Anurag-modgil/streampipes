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

import {Component, Input, NgZone, OnInit,} from "@angular/core";
import {JsplumbBridge} from "../../services/jsplumb-bridge.service";
import {PipelinePositioningService} from "../../services/pipeline-positioning.service";
import {PipelineValidationService} from "../../services/pipeline-validation.service";
import {JsplumbService} from "../../services/jsplumb.service";
import {ShepherdService} from "../../../services/tour/shepherd.service";
import {PipelineElementConfig, PipelineElementUnion} from "../../model/editor.model";
import {ObjectProvider} from "../../services/object-provider.service";
import {PanelType} from "../../../core-ui/dialog/base-dialog/base-dialog.model";
import {SavePipelineComponent} from "../../dialog/save-pipeline/save-pipeline.component";
import {DialogService} from "../../../core-ui/dialog/base-dialog/base-dialog.service";
import {ConfirmDialogComponent} from "../../../core-ui/dialog/confirm-dialog/confirm-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {EditorService} from "../../services/editor.service";
import {PipelineService} from "../../../platform-services/apis/pipeline.service";


@Component({
    selector: 'pipeline-assembly',
    templateUrl: './pipeline-assembly.component.html',
    styleUrls: ['./pipeline-assembly.component.scss']
})
export class PipelineAssemblyComponent implements OnInit {

    PipelineEditorService: any;
    DialogBuilder: any;
    currentMouseOverElement: any;
    currentZoomLevel: any;
    preview: any;

    @Input()
    rawPipelineModel: PipelineElementConfig[];
    selectMode: any;
    currentPipelineName: any;
    currentPipelineDescription: any;

    @Input()
    currentModifiedPipelineId: any;

    @Input()
    allElements: PipelineElementUnion[];

    errorMessagesDisplayed: any = false;

    pipelineValid: boolean = false;

    pipelineCacheRunning: boolean = false;
    pipelineCached: boolean = false;


    constructor(private JsplumbBridge: JsplumbBridge,
                private PipelinePositioningService: PipelinePositioningService,
                private ObjectProvider: ObjectProvider,
                public EditorService: EditorService,
                public PipelineValidationService: PipelineValidationService,
                private PipelineService: PipelineService,
                private JsplumbService: JsplumbService,
                private ShepherdService: ShepherdService,
                private dialogService: DialogService,
                private dialog: MatDialog,
                private ngZone: NgZone) {

        this.selectMode = true;
        this.currentZoomLevel = 1;

    }

    ngOnInit(): void {
        if (this.currentModifiedPipelineId) {
            this.displayPipelineById();
        } else {
            this.checkAndDisplayCachedPipeline();
        }
    }

    ngAfterViewInit() {
        ($("#assembly") as any).panzoom({
            disablePan: true,
            increment: 0.25,
            minScale: 0.5,
            maxScale: 1.5,
            contain: 'invert'
        });

        $("#assembly").on('panzoomzoom', (e, panzoom, scale) => {
            this.currentZoomLevel = scale;
            this.JsplumbBridge.setZoom(scale);
            this.JsplumbBridge.repaintEverything();
        });
    }

    autoLayout() {
        this.PipelinePositioningService.layoutGraph("#assembly", "span[id^='jsplumb']", 110, false);
        this.JsplumbBridge.repaintEverything();
    }

    toggleSelectMode() {
        if (this.selectMode) {
            ($("#assembly") as any).panzoom("option", "disablePan", false);
            this.selectMode = false;
        }
        else {
            ($("#assembly") as any).panzoom("option", "disablePan", true);
            this.selectMode = true;
        }
    }

    zoomOut() {
        this.doZoom(true);
    }

    zoomIn() {
        this.doZoom(false);
    }

    doZoom(zoomOut) {
        ($("#assembly") as any).panzoom("zoom", zoomOut);
    }

    showClearAssemblyConfirmDialog(event: any) {
        let dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                "title": "Do you really want to delete the current pipeline?",
                "subtitle": "This cannot be undone.",
                "cancelTitle": "No",
                "okTitle": "Yes",
                "confirmAndCancel": true
            },
        });
        dialogRef.afterClosed().subscribe(ev => {
            if (ev) {
                if (this.currentModifiedPipelineId) {
                    this.currentModifiedPipelineId = undefined;
                }
                this.clearAssembly();
                this.EditorService.makePipelineAssemblyEmpty(true);
            }
        });
    };

    /**
     * clears the Assembly of all elements
     */
    clearAssembly() {
        //$('#assembly').children().not('#clear, #submit').remove();
        this.JsplumbBridge.deleteEveryEndpoint();
        this.rawPipelineModel = [];
        ($("#assembly") as any).panzoom("reset", {
            disablePan: true,
            increment: 0.25,
            minScale: 0.5,
            maxScale: 1.5,
            contain: 'invert'
        });
        this.currentZoomLevel = 1;
        this.JsplumbBridge.setZoom(this.currentZoomLevel);
        this.JsplumbBridge.repaintEverything();
        this.EditorService.removePipelineFromCache().subscribe(msg => {
            this.pipelineCached = false;
            this.pipelineCacheRunning = false;
        });
    };

    /**
     * Sends the pipeline to the server
     */
    submit() {
        var pipeline = this.ObjectProvider.makeFinalPipeline(this.rawPipelineModel);

        pipeline.name = this.currentPipelineName;
        pipeline.description = this.currentPipelineDescription;
        if (this.currentModifiedPipelineId) {
            pipeline._id = this.currentModifiedPipelineId;
        }

        const dialogRef = this.dialogService.open(SavePipelineComponent,{
            panelType: PanelType.SLIDE_IN_PANEL,
            title: "Save pipeline",
            data: {
                "pipeline": pipeline,
                "currentModifiedPipelineId": this.currentModifiedPipelineId
            }
        });
    }

    checkAndDisplayCachedPipeline() {
        this.EditorService.getCachedPipeline().subscribe(msg => {
            if (msg) {
                this.rawPipelineModel = msg;
                this.displayPipelineInEditor(true);
            }
        });
    }

    displayPipelineById() {
        this.PipelineService.getPipelineById(this.currentModifiedPipelineId)
            .subscribe((msg) => {
                let pipeline = msg;
                this.currentPipelineName = pipeline.name;
                this.currentPipelineDescription = pipeline.description;
                this.rawPipelineModel = this.JsplumbService.makeRawPipeline(pipeline, false);
                this.displayPipelineInEditor(true);
            });
    };

    displayPipelineInEditor(autoLayout) {
        setTimeout(() => {
            this.PipelinePositioningService.displayPipeline(this.rawPipelineModel, "#assembly", false, autoLayout);
            this.EditorService.makePipelineAssemblyEmpty(false);
            this.ngZone.run(() => {
                this.pipelineValid = this.PipelineValidationService
                    .isValidPipeline(this.rawPipelineModel.filter(pe => !(pe.settings.disabled)));
            });
        });
    }

    toggleErrorMessagesDisplayed() {
        this.errorMessagesDisplayed = !(this.errorMessagesDisplayed);
    }

    isPipelineAssemblyEmpty() {
        return this.rawPipelineModel.length === 0 || this.rawPipelineModel.every(pe => pe.settings.disabled);
    }

}