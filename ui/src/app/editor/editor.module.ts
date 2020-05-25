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

import 'angular-ui-sortable';
import 'angular-ui-bootstrap';

import spServices from '../services/services.module';

import 'jquery.panzoom';
import 'npm/bootstrap';
import 'npm/angular-trix';
import 'npm/angular-datatables';
import 'npm/angular-sanitize';
import 'npm/ng-showdown';

import {EditorCtrl} from './editor.controller';
import myDataBind from './my-data-bind.directive';
import imageBind from './image-bind.directive';
import displayRecommendedFilter from './filter/display-recommended.filter';

import {AnyComponent} from './components/any/any.component';
import {CustomOutputComponent} from './components/customoutput/customoutput.component';
import {DomainConceptComponent} from './components/domainconcept/domainconcept.component';
import {FreeTextComponent} from './components/freetext/freetext.component';
import {MappingUnaryComponent} from './components/mappingunary/mappingunary.component';
import {MappingNaryComponent} from './components/mappingnary/mappingnary.component';
import {MatchingPropertyComponent} from './components/matchingproperty/matchingproperty.component';
import {OneOfComponent} from './components/oneof/oneof.component';
import {ReplaceOutputComponent} from './components/replaceoutput/replaceoutput.component';
import {MultipleValueInputComponent} from './components/multivalue/multiple-value-input.component';
import {PipelineElementOptionsComponent} from './components/pipeline-element-options/pipeline-element-options.component';
import {CollectionComponent} from './components/collection/collection.component';
import {CustomizeDialogComponent} from './components/customize/customize-dialog.component';
import {TopicSelectionDialogComponent} from './components/topic/topic-selection-dialog.component';
import {PipelineComponent} from './components/pipeline/pipeline.component';
import {EditorDialogManager} from './services/editor-dialog-manager.service';
import {PipelineElementComponent} from './components/pipeline-element/pipeline-element.component';
import {PipelineElementRecommendationComponent} from "./components/pipeline-element-recommendation/pipeline-element-recommendation.component";
import {PipelineElementRecommendationService} from "./services/pipeline-element-recommendation.service";
import {PipelineAssemblyComponent} from "./components/pipeline-assembly/pipeline-assembly.component";
import {PipelineElementIconStandComponent} from './components/pipeline-element-icon-stand/pipeline-element-icon-stand.component';
import {PipelineValidationService} from "./services/pipeline-validation.service";
import {OneOfRemoteComponent} from "./components/oneof-remote/oneof-remote.component";

import {TextValidatorDirective} from "./validator/text/text-validator.directive";

import selectFilter from './filter/select.filter';
import elementNameFilter from './filter/element-name.filter';
import {PropertySelectionComponent} from "./components/customoutput/propertyselection/property-selection.component";
import {PipelineElementDocumentationComponent} from "./components/pipeline-element-documentation/pipeline-element-documentation.component";
import {CustomOutputValidatorDirective} from "./validator/text/custom-output-validator.directive";
import {AlternativeComponent} from "./components/alternative/alternative.component";
import {GroupComponent} from "./components/group/group.component";
import {SecretComponent} from "./components/secret/secret.component";
import {FileUploadComponent} from "./components/fileupload/fileupload.component";
import {AnyRemoteComponent} from "./components/any-remote/any-remote.component";
import {CodeInputComponent} from "./components/code/code.component";
import {CodeEditorDirective} from "./components/code/code-editor.directive";
import {UserDefinedOutputComponent} from "./components/userdefinedoutput/user-defined-output.component";


export default angular.module('sp.editor', [spServices, 'ngSanitize', 'angularTrix', 'ngAnimate', 'datatables', 'ng-showdown'])
    .controller('EditorCtrl', EditorCtrl)
    .directive('myDataBind', myDataBind)
    .directive('imageBind', imageBind)
    .directive("textValidator", () => new TextValidatorDirective())
    .directive("customOutputValidator", () => new CustomOutputValidatorDirective())
    .filter('displayRecommendedFilter', displayRecommendedFilter)
    .filter('selectFilter', selectFilter)
    .filter('elementNameFilter', elementNameFilter)
    .component('any', AnyComponent)
    .component('anyRemote', AnyRemoteComponent)
    .component('customOutput', CustomOutputComponent)
    .component('userDefinedOutput', UserDefinedOutputComponent)
    .component('domainConceptInput', DomainConceptComponent)
    .component('freetext', FreeTextComponent)
    .component('secret', SecretComponent)
    .component('mappingPropertyNary', MappingNaryComponent)
    .component('mappingPropertyUnary', MappingUnaryComponent)
    .component('matchingProperty', MatchingPropertyComponent)
    .component('oneof', OneOfComponent)
    .component('oneofRemote', OneOfRemoteComponent)
    .component('propertySelection', PropertySelectionComponent)
    .component('replaceOutput', ReplaceOutputComponent)
    .component('multipleValueInput', MultipleValueInputComponent)
    .component('collectionStaticProperty', CollectionComponent)
    .component('customizeDialog', CustomizeDialogComponent)
    .component('topicSelectionDialog', TopicSelectionDialogComponent)
    .component('pipeline', PipelineComponent)
    .component('pipelineElement', PipelineElementComponent)
    .component('pipelineElementRecommendation', PipelineElementRecommendationComponent)
    .component('pipelineAssembly', PipelineAssemblyComponent)
    .component('pipelineElementIconStand', PipelineElementIconStandComponent)
    .component('pipelineElementOptions', PipelineElementOptionsComponent)
    .component('pipelineElementDocumentation', PipelineElementDocumentationComponent)
    .component('alternative', AlternativeComponent)
    .component('group', GroupComponent)
    .component('fileStaticProperty', FileUploadComponent)
    .component('codeInput', CodeInputComponent)
    .service('EditorDialogManager', EditorDialogManager)
    .service('PipelineElementRecommendationService', PipelineElementRecommendationService)
    .service('PipelineValidationService', PipelineValidationService)
    .directive('codeEditor', () => new CodeEditorDirective())
    .name;
