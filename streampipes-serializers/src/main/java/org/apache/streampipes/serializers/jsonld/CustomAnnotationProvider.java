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

package org.apache.streampipes.serializers.jsonld;

import io.fogsy.empire.core.empire.util.EmpireAnnotationProvider;
import org.apache.streampipes.model.ApplicationLink;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamContainer;
import org.apache.streampipes.model.base.StreamPipesJsonLdContainer;
import org.apache.streampipes.model.client.messages.ErrorMessageLd;
import org.apache.streampipes.model.client.messages.MessageLd;
import org.apache.streampipes.model.client.messages.NotificationLd;
import org.apache.streampipes.model.client.messages.SuccessMessageLd;
import org.apache.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.connect.grounding.FormatDescriptionList;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescriptionList;
import org.apache.streampipes.model.connect.guess.DomainPropertyProbability;
import org.apache.streampipes.model.connect.guess.DomainPropertyProbabilityList;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.rules.Schema.CreateNestedRuleDescription;
import org.apache.streampipes.model.connect.rules.Schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.Schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.Schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.Stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.Stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.model.dashboard.DashboardWidgetModel;
import org.apache.streampipes.model.dashboard.VisualizablePipeline;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.grounding.WildcardTopicDefinition;
import org.apache.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.apache.streampipes.model.output.AppendOutputStrategy;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.output.CustomTransformOutputStrategy;
import org.apache.streampipes.model.output.FixedOutputStrategy;
import org.apache.streampipes.model.output.KeepOutputStrategy;
import org.apache.streampipes.model.output.ListOutputStrategy;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.output.TransformOperation;
import org.apache.streampipes.model.output.TransformOutputStrategy;
import org.apache.streampipes.model.quality.Accuracy;
import org.apache.streampipes.model.quality.EventPropertyQualityRequirement;
import org.apache.streampipes.model.quality.EventStreamQualityRequirement;
import org.apache.streampipes.model.quality.Frequency;
import org.apache.streampipes.model.quality.Latency;
import org.apache.streampipes.model.quality.MeasurementCapability;
import org.apache.streampipes.model.quality.MeasurementObject;
import org.apache.streampipes.model.quality.MeasurementProperty;
import org.apache.streampipes.model.quality.MeasurementRange;
import org.apache.streampipes.model.quality.Precision;
import org.apache.streampipes.model.quality.Resolution;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.model.schema.Enumeration;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.QuantitativeValue;
import org.apache.streampipes.model.staticproperty.AnyStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty;
import org.apache.streampipes.model.staticproperty.DomainStaticProperty;
import org.apache.streampipes.model.staticproperty.FileStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyNary;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.MatchingStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RemoteOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.model.staticproperty.SupportedProperty;
import org.apache.streampipes.model.template.BoundPipelineElement;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.model.template.PipelineTemplateDescriptionContainer;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomAnnotationProvider implements EmpireAnnotationProvider {


  @Override
  public Collection<Class<?>> getClassesWithAnnotation(
          Class<? extends Annotation> arg0) {
    if (arg0.getName().equals("io.fogsy.empire.annotations.RdfsClass")) {
      return getAnnotatedClasses();
    } else {
      return Collections.emptyList();
    }
  }


  /**
   * Do not register abstract classes!!!
   * Just register classes with a default constructor
   */
  private List<Class<?>> getAnnotatedClasses() {
    return Arrays.asList(
            Accuracy.class,
            ColorPickerStaticProperty.class,
            CustomOutputStrategy.class,
            DataSinkDescription.class,
            DataProcessorInvocation.class,
            EventPropertyList.class,
            EventPropertyNested.class,
            EventPropertyPrimitive.class,
            EventSchema.class,
            ListOutputStrategy.class,
            MappingPropertyUnary.class,
            MappingPropertyNary.class,
            MatchingStaticProperty.class,
            FixedOutputStrategy.class,
            AppendOutputStrategy.class,
            SpDataStream.class,
            SpDataSet.class,
            EventPropertyQualityRequirement.class,
            EventStreamQualityRequirement.class,
            Frequency.class,
            Latency.class,
            MeasurementProperty.class,
            MeasurementRange.class,
            Precision.class,
            Resolution.class,
            EventGrounding.class,
            DataSourceDescription.class,
            DataProcessorDescription.class,
            KeepOutputStrategy.class,
            OneOfStaticProperty.class,
            RemoteOneOfStaticProperty.class,
            AnyStaticProperty.class,
            FreeTextStaticProperty.class,
            FileStaticProperty.class,
            Option.class,
            MappingProperty.class,
            DataSinkInvocation.class,
            TransportFormat.class,
            JmsTransportProtocol.class,
            KafkaTransportProtocol.class,
            MqttTransportProtocol.class,
            TransportProtocol.class,
            DomainStaticProperty.class,
            SupportedProperty.class,
            CollectionStaticProperty.class,
            MeasurementCapability.class,
            MeasurementObject.class,
            Enumeration.class,
            QuantitativeValue.class,
            ApplicationLink.class,
            ElementStatusInfoSettings.class,
            WildcardTopicDefinition.class,
            SimpleTopicDefinition.class,
            RuntimeResolvableAnyStaticProperty.class,
            RuntimeResolvableOneOfStaticProperty.class,
            TransformOutputStrategy.class,
            TransformOperation.class,
            CustomTransformOutputStrategy.class,
            SpecificAdapterSetDescription.class,
            SpecificAdapterStreamDescription.class,
            GenericAdapterStreamDescription.class,
            GenericAdapterSetDescription.class,
            AdapterDescriptionList.class,
            FormatDescription.class,
            FormatDescriptionList.class,
            DomainPropertyProbability.class,
            DomainPropertyProbabilityList.class,
            GuessSchema.class,
            ProtocolDescription.class,
            ProtocolDescriptionList.class,
            PipelineTemplateDescription.class,
            PipelineTemplateInvocation.class,
            BoundPipelineElement.class,
            SpDataStreamContainer.class,
            PipelineTemplateDescriptionContainer.class,
            DeleteRuleDescription.class,
            CreateNestedRuleDescription.class,
            MoveRuleDescription.class,
            RenameRuleDescription.class,
            UnitTransformRuleDescription.class,
            RemoveDuplicatesTransformationRuleDescription.class,
            AddValueTransformationRuleDescription.class,
            AddValueTransformationRuleDescription.class,
            PropertyRenameRule.class,
            ErrorMessageLd.class,
            SuccessMessageLd.class,
            MessageLd.class,
            NotificationLd.class,
            AddTimestampRuleDescription.class,
            PropertyRenameRule.class,
            TimestampTranfsformationRuleDescription.class,
            RuntimeOptionsRequest.class,
            RuntimeOptionsResponse.class,
            StaticPropertyAlternative.class,
            StaticPropertyAlternatives.class,
            StaticPropertyGroup.class,
            ConnectWorkerContainer.class,
            RuntimeOptionsResponse.class,
            EventRateTransformationRuleDescription.class,
            SecretStaticProperty.class,
            DashboardWidgetModel.class,
            VisualizablePipeline.class,
            StreamPipesJsonLdContainer.class

    );
  }
}
