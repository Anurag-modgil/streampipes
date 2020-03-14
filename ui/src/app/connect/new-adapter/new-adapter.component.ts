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

///<reference path="../model/connect/AdapterDescription.ts"/>
import { Component, EventEmitter, Input, OnInit, Output, PipeTransform, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { ShepherdService } from '../../services/tour/shepherd.service';
import { Logger } from '../../shared/logger/default-log.service';
import { ConnectService } from '../connect.service';
import { TimestampPipe } from '../filter/timestamp.pipe';
import { AdapterDescription } from '../model/connect/AdapterDescription';
import { GenericAdapterSetDescription } from '../model/connect/GenericAdapterSetDescription';
import { GenericAdapterStreamDescription } from '../model/connect/GenericAdapterStreamDescription';
import { FormatDescription } from '../model/connect/grounding/FormatDescription';
import { EventRateTransformationRuleDescription } from '../model/connect/rules/EventRateTransformationRuleDescription';
import { RemoveDuplicatesRuleDescription } from '../model/connect/rules/RemoveDuplicatesRuleDescription';
import { TransformationRuleDescription } from '../model/connect/rules/TransformationRuleDescription';
import { SpecificAdapterSetDescription } from '../model/connect/SpecificAdapterSetDescription';
import { SpecificAdapterStreamDescription } from '../model/connect/SpecificAdapterStreamDescription';
import { ConfigurationInfo } from '../model/message/ConfigurationInfo';
import { RestService } from '../rest.service';
import { EventSchemaComponent } from '../schema-editor/event-schema/event-schema.component';
import { EventProperty } from '../schema-editor/model/EventProperty';
import { EventSchema } from '../schema-editor/model/EventSchema';
import { TransformationRuleService } from '../transformation-rule.service';
import { AdapterStartedDialog } from './component/adapter-started-dialog.component';
import { IconService } from './icon.service';

@Component({
    selector: 'sp-new-adapter',
    templateUrl: './new-adapter.component.html',
    styleUrls: ['./new-adapter.component.css'],
})
export class NewAdapterComponent implements OnInit {



    constructor(
        private logger: Logger,
        private restService: RestService,
        private transformationRuleService: TransformationRuleService,
        public dialog: MatDialog,
        private ShepherdService: ShepherdService,
        private connectService: ConnectService,
        private _formBuilder: FormBuilder,
        private iconService: IconService,
        private timestampPipe: TimestampPipe,
    ) { }


    selectedUploadFile: File;
    fileName;
    isGenericAdapter = false;
    isDataSetDescription = false;
    isDataStreamDescription = false;


    @Input()
    adapter: AdapterDescription;

    @Output()
    removeSelectionEmitter: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    updateAdapterEmitter: EventEmitter<void> = new EventEmitter<void>();

    @ViewChild('stepper', { static: true }) myStepper: MatStepper;

    allFormats: FormatDescription[] = [];

    protocolConfigurationValid: boolean;
    formatConfigurationValid: boolean;

    removeDuplicates = false;
    removeDuplicatesTime: number;

    eventRateReduction = false;
    eventRateTime: number;
    eventRateMode = 'none';

    saveInDataLake = false;
    dataLakeTimestampField: string;

    startAdapterFormGroup: FormGroup;

    eventSchema: EventSchema;
    oldEventSchema: EventSchema;

    timestampPropertiesInSchema: EventProperty[] = [];

    hasInput: boolean[];

    // indicates whether user uses a template or not
    fromTemplate = false;

    // deactivates all edit functions when user starts a template
    isEditable = true;

    @ViewChild(EventSchemaComponent, { static: true })
    private eventSchemaComponent: EventSchemaComponent;

    isSetAdapter = false;

    completedStaticProperty: ConfigurationInfo;

    isPreviewEnabled = false;

    handleFileInput(files: any) {
        this.selectedUploadFile = files[0];
        this.fileName = this.selectedUploadFile.name;

        this.iconService.toBase64(this.selectedUploadFile)
            .then(
                data => {
                    this.adapter.icon = (data as string);
                }
            );
    }

    ngOnInit() {

        this.isGenericAdapter = this.connectService.isGenericDescription(this.adapter);
        this.isDataSetDescription = this.connectService.isDataSetDescription(this.adapter);
        this.isDataStreamDescription = this.connectService.isDataStreamDescription(this.adapter);
        this.formatConfigurationValid = false;

        if (this.adapter instanceof GenericAdapterSetDescription) {
            if ((this.adapter as GenericAdapterSetDescription).format != undefined) {
                this.formatConfigurationValid = true;
            }
        }

        if (this.adapter instanceof GenericAdapterStreamDescription) {
            if ((this.adapter as GenericAdapterStreamDescription).format != undefined) {
                this.formatConfigurationValid = true;
            }
        }

        this.restService.getFormats().subscribe(x => {
            this.allFormats = x.list;
        });

        this.startAdapterFormGroup = this._formBuilder.group({
            startAdapterFormCtrl: ['', Validators.required]
        });

        this.protocolConfigurationValid = false;

        this.eventSchema = this.getEventSchema(this.adapter);

        if (this.eventSchema.eventProperties.length > 0) {

            // Timeout is needed for stepper to work correctly. Without the stepper is frozen when initializing with
            // step 2. Can be removed when a better solution is founf.
            setTimeout(() => {
                this.goForward(this.myStepper);
                this.goForward(this.myStepper);
            }, 1);

            this.fromTemplate = true;
            this.isEditable = false;
            this.oldEventSchema = this.eventSchema;


        }
    }

    public showPreview(isPreviewEnabled) {
        this.isPreviewEnabled = isPreviewEnabled;
    }

    public triggerDialog(storeAsTemplate: boolean) {
        if (this.removeDuplicates) {
            this.adapter.rules.push(new RemoveDuplicatesRuleDescription(this.removeDuplicatesTime));
        }
        if (this.eventRateReduction) {
            this.adapter.rules.push(new EventRateTransformationRuleDescription(this.eventRateTime, this.eventRateMode));
        }

        const dialogRef = this.dialog.open(AdapterStartedDialog, {
            width: '70%',
            data: {
                adapter: this.adapter,
                storeAsTemplate,
                saveInDataLake: this.saveInDataLake,
                dataLakeTimestampField: this.dataLakeTimestampField
            },
            panelClass: 'sp-no-padding-dialog'
        });

        this.ShepherdService.trigger('button-startAdapter');

        dialogRef.afterClosed().subscribe(result => {
            this.updateAdapterEmitter.emit();
            this.removeSelectionEmitter.emit();
        });
    }

    public saveTemplate() {
        this.triggerDialog(true);
    }

    public startAdapter() {
        this.triggerDialog(false);
    }

    validateProtocol(valid) {
        this.protocolConfigurationValid = valid;
    }

    validateFormat(valid) {
        this.formatConfigurationValid = valid;
    }

    removeSelection() {
        this.removeSelectionEmitter.emit();
    }

    clickProtocolSettingsNextButton(stepper: MatStepper) {
        this.ShepherdService.trigger('specific-settings-next-button');
        this.goForward(stepper);
    }

    clickSpecificSettingsNextButton(stepper: MatStepper) {
        this.ShepherdService.trigger('specific-settings-next-button');
        this.guessEventSchema();
        this.goForward(stepper);
    }

    clickEventSchemaNextButtonButton(stepper: MatStepper) {
        if (this.isEditable) {
            this.setSchema();
        }

        // Auto selection of timestamp field for datalake
        this.timestampPropertiesInSchema = this.timestampPipe.transform(this.eventSchema.eventProperties, '');
        if (this.timestampPropertiesInSchema.length > 0) {
            this.dataLakeTimestampField = this.timestampPropertiesInSchema[0].runtimeName;
        }

        this.ShepherdService.trigger('event-schema-next-button');
        this.goForward(stepper);

        if (this.adapter instanceof GenericAdapterSetDescription || this.adapter instanceof SpecificAdapterSetDescription) {
            this.isSetAdapter = true;
        }

    }

    clickFormatSelectionNextButton(stepper: MatStepper) {
        this.ShepherdService.trigger('format-selection-next-button');
        this.guessEventSchema();
        this.goForward(stepper);
    }

    guessEventSchema() {
        const eventSchema: EventSchema = this.getEventSchema(this.adapter);
        if (eventSchema.eventProperties.length === 0) {
            this.eventSchemaComponent.guessSchema();
        } else {
            this.oldEventSchema = eventSchema;
        }
    }

    getEventSchema(adapter: AdapterDescription): EventSchema {
        let eventSchema: EventSchema;

        if (adapter instanceof GenericAdapterSetDescription) {
            eventSchema = (adapter as GenericAdapterSetDescription).dataSet.eventSchema;
        } else if (adapter instanceof SpecificAdapterSetDescription) {
            eventSchema = (adapter as SpecificAdapterSetDescription).dataSet.eventSchema;
        } else if (adapter instanceof GenericAdapterStreamDescription) {
            eventSchema = (adapter as GenericAdapterStreamDescription).dataStream.eventSchema;
        } else if (adapter instanceof SpecificAdapterStreamDescription) {
            eventSchema = (adapter as SpecificAdapterStreamDescription).dataStream.eventSchema;
        } else {
            return new EventSchema();
        }

        if (eventSchema && eventSchema.eventProperties && eventSchema.eventProperties.length > 0) {
            return eventSchema;
        } else {
            return new EventSchema();
        }
    }

    public setSchema() {

        if (this.adapter instanceof  GenericAdapterSetDescription) {
            (this.adapter as GenericAdapterSetDescription).dataSet.eventSchema = this.eventSchema;
        } else if (this.adapter instanceof SpecificAdapterSetDescription) {
            (this.adapter as SpecificAdapterSetDescription).dataSet.eventSchema = this.eventSchema;
        } else if (this.adapter instanceof GenericAdapterStreamDescription) {
            (this.adapter as GenericAdapterStreamDescription).dataStream.eventSchema = this.eventSchema;
        } else if (this.adapter instanceof SpecificAdapterStreamDescription) {
            (this.adapter as SpecificAdapterStreamDescription).dataStream.eventSchema = this.eventSchema;
        } else {
            console.log('Error: Adapter type is unknown');
        }


        this.transformationRuleService.setOldEventSchema(this.oldEventSchema);

        this.transformationRuleService.setNewEventSchema(this.eventSchema);
        const transformationRules: TransformationRuleDescription[] = this.transformationRuleService.getTransformationRuleDescriptions();
        this.adapter.rules = transformationRules;
    }

    formatSelected(selectedFormat) {
        if (
            this.adapter instanceof GenericAdapterSetDescription ||
            this.adapter instanceof GenericAdapterStreamDescription
        ) {
            this.adapter.format = selectedFormat;
            if (selectedFormat.config.length === 0) {
                this.validateFormat(true);
            }
        }
    }

    goBack(stepper: MatStepper) {
        this.myStepper.selectedIndex = this.myStepper.selectedIndex - 1;
    }
    goForward(stepper: MatStepper) {
        this.myStepper.selectedIndex = this.myStepper.selectedIndex + 1;
    }

    triggerUpdate(configurationInfo: ConfigurationInfo) {
        this.completedStaticProperty = {...configurationInfo};
    }
}
