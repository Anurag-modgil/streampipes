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

import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FlexLayoutModule} from '@angular/flex-layout';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

import {ConnectComponent} from './connect.component';
import {NewAdapterComponent} from './components/new-adapter/new-adapter.component';

import {FormatFormComponent} from './components/format-form/format-form.component';

import {EditEventPropertyPrimitiveComponent} from './dialog/edit-event-property/components/edit-event-property-primitive/edit-event-property-primitive.component';
import {EventSchemaComponent} from './components/schema-editor/event-schema/event-schema.component';

import {CustomMaterialModule} from '../CustomMaterial/custom-material.module';

import {RestService} from './services/rest.service';

import {MatInputModule} from '@angular/material/input';
import {DragulaModule} from 'ng2-dragula';
import {AdapterStartedDialog} from './dialog/adapter-started/adapter-started-dialog.component';
import {DataTypesService} from './services/data-type.service';
import {StaticPropertyUtilService} from '../core-ui/static-properties/static-property-util.service';
import {TransformationRuleService} from './services/transformation-rule.service';
import {ConnectService} from './services/connect.service';
import {AdapterDescriptionComponent} from './components/data-marketplace/adapter-description/adapter-description.component';
import {DataMarketplaceComponent} from './components/data-marketplace/data-marketplace.component';
import {DataMarketplaceService} from './services/data-marketplace.service';
import {FormatComponent} from './components/format-component/format.component';
import {FormatListComponent} from './components/format-list-component/format-list.component';
import {IconService} from './services/icon.service';
import {UnitProviderService} from './services/unit-provider.service';


import {FilterPipe} from './filter/filter.pipe';
import {AdapterExportDialog} from './dialog/adapter-export/adapter-export-dialog.component';
import {AdapterUploadDialog} from './dialog/adapter-upload/adapter-upload-dialog.component';
import {EditEventPropertyListComponent} from './dialog/edit-event-property/components/edit-event-property-list/edit-event-property-list.component';
import {TimestampPipe} from './filter/timestamp.pipe';
import {MatChipsModule} from '@angular/material/chips';
import {MatSliderModule} from '@angular/material/slider';
import {TreeModule} from 'angular-tree-component';
import {xsService} from '../NS/XS.service';
import {EditDataTypeComponent} from './dialog/edit-event-property/components/edit-data-type/edit-data-type.component';
import {EditTimestampPropertyComponent} from './dialog/edit-event-property/components/edit-timestamp-property/edit-timestamp-property.component';
import {EditUnitTransformationComponent} from './dialog/edit-event-property/components/edit-unit-transformation/edit-unit-transformation.component';
import {EditEventPropertyComponent} from './dialog/edit-event-property/edit-event-property.component';
import {PipelineElementRuntimeInfoComponent} from './components/runtime-info/pipeline-element-runtime-info.component';
import {EventPropertyRowComponent} from './components/schema-editor/event-property-row/event-property-row.component';
import {EventSchemaPreviewComponent} from './components/schema-editor/event-schema-preview/event-schema-preview.component';
import {CoreUiModule} from "../core-ui/core-ui.module";
import {EditCorrectionValueComponent} from './dialog/edit-event-property/components/edit-correction-value/edit-correction-value.component';

@NgModule({
    imports: [
        CoreUiModule,
        FormsModule,
        ReactiveFormsModule,
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        CustomMaterialModule,
        DragulaModule,
        MatProgressSpinnerModule,
        MatChipsModule,
        MatInputModule,
        MatFormFieldModule,
        MatSliderModule,
        TreeModule.forRoot(),
    ],
    exports: [
        PipelineElementRuntimeInfoComponent
    ],
    declarations: [
        AdapterDescriptionComponent,
        AdapterExportDialog,
        AdapterStartedDialog,
        AdapterUploadDialog,
        ConnectComponent,
        DataMarketplaceComponent,
        EventSchemaComponent,
        EditEventPropertyPrimitiveComponent,
        EditEventPropertyComponent,
        EventPropertyRowComponent,
        EditEventPropertyListComponent,
        EditUnitTransformationComponent,
        EditTimestampPropertyComponent,
        EditDataTypeComponent,
        EventSchemaPreviewComponent,
        FilterPipe,
        FormatComponent,
        FormatFormComponent,
        FormatListComponent,
        NewAdapterComponent,
        PipelineElementRuntimeInfoComponent,
        TimestampPipe,
        EditCorrectionValueComponent
    ],
    providers: [
        RestService,
        ConnectService,
        DataTypesService,
        TransformationRuleService,
        StaticPropertyUtilService,
        DataMarketplaceService,
        IconService,
        UnitProviderService,
        TimestampPipe,
        xsService,
    ],
    entryComponents: [ConnectComponent, AdapterStartedDialog, AdapterExportDialog, AdapterUploadDialog, EditEventPropertyComponent],
})
export class ConnectModule { }
