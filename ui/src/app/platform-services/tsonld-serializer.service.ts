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

import {Injectable} from '@angular/core';
import {AnyStaticProperty} from '../connect/model/AnyStaticProperty';
import {OneOfStaticProperty} from '../connect/model/OneOfStaticProperty';
import {ProtocolDescription} from '../connect/model/connect/grounding/ProtocolDescription';
import {ProtocolDescriptionList} from '../connect/model/connect/grounding/ProtocolDescriptionList';
import {FreeTextStaticProperty} from '../connect/model/FreeTextStaticProperty';
import {FileStaticProperty} from '../connect/model/FileStaticProperty';
import {FormatDescriptionList} from '../connect/model/connect/grounding/FormatDescriptionList';
import {FormatDescription} from '../connect/model/connect/grounding/FormatDescription';
import {AdapterDescriptionList} from '../connect/model/connect/AdapterDescriptionList';
import {Option} from '../connect/model/Option';
import {AdapterDescription} from '../connect/model/connect/AdapterDescription';
import {AdapterSetDescription} from '../connect/model/connect/AdapterSetDescription';
import {AdapterStreamDescription} from '../connect/model/connect/AdapterStreamDescription';
import {DataSetDescription} from '../connect/model/DataSetDescription';
import {EventSchema} from '../connect/schema-editor/model/EventSchema';
import {EventProperty} from '../connect/schema-editor/model/EventProperty';
import {EventPropertyNested} from '../connect/schema-editor/model/EventPropertyNested';
import {EventPropertyList} from '../connect/schema-editor/model/EventPropertyList';
import {EventPropertyPrimitive} from '../connect/schema-editor/model/EventPropertyPrimitive';
import {DomainPropertyProbability} from '../connect/schema-editor/model/DomainPropertyProbability';
import {DomainPropertyProbabilityList} from '../connect/schema-editor/model/DomainPropertyProbabilityList';
import {GuessSchema} from '../connect/schema-editor/model/GuessSchema';
import {URI} from '../connect/model/URI';
import {RenameRuleDescription} from '../connect/model/connect/rules/RenameRuleDescription';
import {DeleteRuleDescription} from '../connect/model/connect/rules/DeleteRuleDescription';
import {AddNestedRuleDescription} from '../connect/model/connect/rules/AddNestedRuleDescription';
import {RemoveDuplicatesRuleDescription} from '../connect/model/connect/rules/RemoveDuplicatesRuleDescription';
import {AddTimestampRuleDescription} from '../connect/model/connect/rules/AddTimestampRuleDescription';
import {AddValueTransformationRuleDescription} from '../connect/model/connect/rules/AddValueTransformationRuleDescription';
import {MoveRuleDescription} from '../connect/model/connect/rules/MoveRuleDesctiption';
import {UnitTransformRuleDescription} from '../connect/model/connect/rules/UnitTransformRuleDescription';
import {TsonLd} from './tsonld/index';
import {Observable} from 'rxjs';
import {TransformationRuleDescription} from '../connect/model/connect/rules/TransformationRuleDescription';
import {GenericAdapterSetDescription} from '../connect/model/connect/GenericAdapterSetDescription';
import {SpecificAdapterSetDescription} from '../connect/model/connect/SpecificAdapterSetDescription';
import {GenericAdapterStreamDescription} from '../connect/model/connect/GenericAdapterStreamDescription';
import {SpecificAdapterStreamDescription} from '../connect/model/connect/SpecificAdapterStreamDescription';
import {MessageLd} from '../connect/model/message/MessageLd';
import {NotificationLd} from '../connect/model/message/NotificationLd';
import {SuccessMessageLd} from '../connect/model/message/SuccessMessage';
import {ErrorMessageLd} from '../connect/model/message/ErrorMessage';
import {TimestampTransformationRuleDescription} from '../connect/model/connect/rules/TimestampTransformationRuleDescription';
import {DataStreamDescription} from '../connect/model/DataStreamDescription';
import {PipelineTemplateInvocation} from '../connect/model/PipelineTemplateInvocation';
import {MappingPropertyUnary} from '../connect/model/MappingPropertyUnary';
import {DataProcessorInvocation} from '../connect/model/DataProcessorInvocation';
import {AppendOutputStrategy} from '../connect/model/output/AppendOutputStrategy';
import {CustomOutputStrategy} from '../connect/model/output/CustomOutputStrategy';
import {CustomTransformOutputStrategy} from '../connect/model/output/CustomTransformOutputStrategy';
import {FixedOutputStrategy} from '../connect/model/output/FixedOutputStrategy';
import {KeepOutputStrategy} from '../connect/model/output/KeepOutputStrategy';
import {ListOutputStrategy} from '../connect/model/output/ListOutputStrategy';
import {OutputStrategy} from '../connect/model/output/OutputStrategy';
import {PropertyRenameRule} from '../connect/model/output/PropertyRenameRule';
import {TransformOutputStrategy} from '../connect/model/output/TransformOutputStrategy';
import {TransformOperation} from '../connect/model/output/TransformOperation';
import {DataStreamContainer} from '../connect/model/DataStreamContainer';
import {Enumeration} from '../connect/schema-editor/model/Enumeration';
import {QuantitativeValue} from '../connect/schema-editor/model/QuantitativeValue';
import {PipelineTemplateDescriptionContainer} from '../connect/model/PipelineTemplateDescriptionContainer';
import {PipelineTemplateDescription} from '../connect/model/PipelineTemplateDescription';
import {BoundPipelineElement} from '../connect/model/BoundPipelineElement';
import {DataSinkInvocation} from '../connect/model/DataSinkInvocation';
import {RuntimeResolvableOneOfStaticProperty} from "../connect/model/RuntimeResolvableOneOfStaticProperty";
import {RuntimeResolvableAnyStaticProperty} from "../connect/model/RuntimeResolvableAnyStaticProperty";
import {RuntimeOptionsRequest} from "../connect/model/connect/runtime/RuntimeOptionsRequest";
import {RuntimeOptionsResponse} from "../connect/model/connect/runtime/RuntimeOptionsResponse";
import {EventRateTransformationRuleDescription} from '../connect/model/connect/rules/EventRateTransformationRuleDescription';
import {SecretStaticProperty} from "../connect/model/SecretStaticProperty";
import {AlternativesStaticProperty} from '../connect/model/AlternativesStaticProperty';
import {AlternativeStaticProperty} from '../connect/model/AlternativeStaticProperty';
import {GroupStaticProperty} from '../connect/model/GroupStaticProperty';
import {CollectionStaticProperty} from "../connect/model/CollectionStaticProperty";
import {VisualizablePipeline} from "../core-model/dashboard/VisualizablePipeline";
import {DashboardWidget} from "../core-model/dashboard/DashboardWidget";
import {DashboardWidgetSettings} from "../core-model/dashboard/DashboardWidgetSettings";
import {ColorPickerStaticProperty} from "../connect/model/ColorPickerStaticProperty";
import {MappingPropertyNary} from "../connect/model/MappingPropertyNary";


@Injectable()
export class TsonLdSerializerService {

    private getTsonLd(): any {
        const tsonld = new TsonLd();
        tsonld.addClassMapping(ProtocolDescription);
        tsonld.addClassMapping(ProtocolDescriptionList);
        tsonld.addClassMapping(FreeTextStaticProperty);
        tsonld.addClassMapping(SecretStaticProperty);
        tsonld.addClassMapping(FileStaticProperty);
        tsonld.addClassMapping(FormatDescriptionList);
        tsonld.addClassMapping(FormatDescription);
        tsonld.addClassMapping(AdapterDescriptionList);
        tsonld.addClassMapping(AdapterDescription);
        tsonld.addClassMapping(AdapterSetDescription);
        tsonld.addClassMapping(SpecificAdapterSetDescription);
        tsonld.addClassMapping(GenericAdapterSetDescription);
        tsonld.addClassMapping(AdapterStreamDescription);
        tsonld.addClassMapping(GenericAdapterStreamDescription);
        tsonld.addClassMapping(SpecificAdapterStreamDescription);
        tsonld.addClassMapping(DataSetDescription);
        tsonld.addClassMapping(DataStreamDescription);
        tsonld.addClassMapping(EventSchema);
        tsonld.addClassMapping(EventProperty);
        tsonld.addClassMapping(EventPropertyNested);
        tsonld.addClassMapping(PipelineTemplateInvocation);
        tsonld.addClassMapping(MappingPropertyUnary);
        tsonld.addClassMapping(MappingPropertyNary);
        tsonld.addClassMapping(EventPropertyList);
        tsonld.addClassMapping(EventPropertyPrimitive);
        tsonld.addClassMapping(DomainPropertyProbability);
        tsonld.addClassMapping(DomainPropertyProbabilityList);
        tsonld.addClassMapping(GuessSchema);
        tsonld.addClassMapping(URI);
        tsonld.addClassMapping(RenameRuleDescription);
        tsonld.addClassMapping(DeleteRuleDescription);
        tsonld.addClassMapping(AddNestedRuleDescription);
        tsonld.addClassMapping(TransformationRuleDescription);
        tsonld.addClassMapping(RemoveDuplicatesRuleDescription);
        tsonld.addClassMapping(AddTimestampRuleDescription);
        tsonld.addClassMapping(AddValueTransformationRuleDescription);
        tsonld.addClassMapping(EventRateTransformationRuleDescription);
        tsonld.addClassMapping(MoveRuleDescription);
        tsonld.addClassMapping(UnitTransformRuleDescription);
        tsonld.addClassMapping(DataStreamContainer);
        tsonld.addClassMapping(Option);
        tsonld.addClassMapping(AnyStaticProperty);
        tsonld.addClassMapping(OneOfStaticProperty);
        tsonld.addClassMapping(TimestampTransformationRuleDescription);
        tsonld.addClassMapping(NotificationLd);
        tsonld.addClassMapping(MessageLd);
        tsonld.addClassMapping(SuccessMessageLd);
        tsonld.addClassMapping(ErrorMessageLd);

        tsonld.addClassMapping(Enumeration);
        tsonld.addClassMapping(QuantitativeValue);

        tsonld.addClassMapping(PipelineTemplateDescriptionContainer);
        tsonld.addClassMapping(PipelineTemplateDescription);
        tsonld.addClassMapping(DataProcessorInvocation);
        tsonld.addClassMapping(DataSinkInvocation);
        tsonld.addClassMapping(BoundPipelineElement);

        tsonld.addClassMapping(AppendOutputStrategy);
        tsonld.addClassMapping(CustomOutputStrategy);
        tsonld.addClassMapping(CustomTransformOutputStrategy);
        tsonld.addClassMapping(FixedOutputStrategy);
        tsonld.addClassMapping(KeepOutputStrategy);
        tsonld.addClassMapping(ListOutputStrategy);
        tsonld.addClassMapping(OutputStrategy);
        tsonld.addClassMapping(PropertyRenameRule);
        tsonld.addClassMapping(TransformOperation);
        tsonld.addClassMapping(TransformOutputStrategy);

        tsonld.addClassMapping(RuntimeResolvableAnyStaticProperty);
        tsonld.addClassMapping(RuntimeResolvableOneOfStaticProperty);

        tsonld.addClassMapping(RuntimeOptionsRequest);
        tsonld.addClassMapping(RuntimeOptionsResponse);
        tsonld.addClassMapping(RuntimeOptionsResponse);

        tsonld.addClassMapping(GroupStaticProperty);
        tsonld.addClassMapping(AlternativesStaticProperty);
        tsonld.addClassMapping(AlternativeStaticProperty);
        tsonld.addClassMapping(CollectionStaticProperty);

        tsonld.addClassMapping(DashboardWidget);
        tsonld.addClassMapping(DashboardWidgetSettings);

        tsonld.addClassMapping(VisualizablePipeline);
        tsonld.addClassMapping(ColorPickerStaticProperty);

        tsonld.addContext('sp', 'https://streampipes.org/vocabulary/v1/');
        tsonld.addContext('spi', 'urn:streampipes.org:spi:');
        tsonld.addContext('foaf', 'http://xmlns.com/foaf/0.1/');


        return tsonld;
    }

    public toJsonLd(o: any): Observable<{}> {
        return this.getTsonLd().toflattenJsonLd(o);
    }

    public fromJsonLd(o: any, type: string): any {
        return this.getTsonLd().fromJsonLdType(o, type);
    }

    public fromJsonLdContainer(o: any, type:string): any {
        return this.getTsonLd().fromJsonLdContainer(o, type);
    }

}