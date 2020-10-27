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

package org.apache.streampipes.sdk.extractor;

import com.github.drapostolos.typeparser.TypeParser;
import org.apache.http.client.fluent.Request;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.constants.PropertySelectorConstants;
import org.apache.streampipes.model.schema.*;
import org.apache.streampipes.model.staticproperty.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractParameterExtractor<T extends InvocableStreamPipesEntity> {

  protected T sepaElement;
  private TypeParser typeParser;

  public AbstractParameterExtractor(T sepaElement) {
    this.sepaElement = sepaElement;
    this.typeParser = TypeParser.newBuilder().build();
  }

  public String measurementUnit(String runtimeName, Integer streamIndex) {
    return sepaElement
            .getInputStreams()
            .get(streamIndex)
            .getEventSchema()
            .getEventProperties()
            .stream()
            .filter(ep -> ep.getRuntimeName().equals(runtimeName))
            .map(ep -> (EventPropertyPrimitive) ep)
            .findFirst()
            .get()
            .getMeasurementUnit()
            .toString();
  }

  public String inputTopic(Integer streamIndex) {
    return sepaElement
            .getInputStreams()
            .get(streamIndex)
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicDefinition()
            .getActualTopicName();
  }

  public <V> V singleValueParameter(String internalName, Class<V> targetClass) {
    return typeParser.parse(getStaticPropertyByName(internalName, FreeTextStaticProperty.class)
            .getValue(), targetClass);
  }

  public String textParameter(String internalName) {
    return singleValueParameter(internalName, String.class);
  }

  public String secretValue(String internalName) {
    return (getStaticPropertyByName(internalName, SecretStaticProperty.class)
            .getValue());
  }

  public String codeblockValue(String internalName) {
    return getStaticPropertyByName(internalName,CodeInputStaticProperty.class).getValue();
  }

  public String selectedColor(String internalName) {
    return getStaticPropertyByName(internalName, ColorPickerStaticProperty.class).getSelectedColor();
  }

  public String fileContentsAsString(String internalName) throws IOException {
    String filename = selectedFilename(internalName);
    return Request.Get(makeFileRequestPath(filename)).execute().returnContent().asString();
  }

  public byte[] fileContentsAsByteArray(String internalName) throws IOException {
    String filename = selectedFilename(internalName);
    return Request.Get(makeFileRequestPath(filename)).execute().returnContent().asBytes();
  }

  public InputStream fileContentsAsStream(String internalName) throws IOException {
    String filename = selectedFilename(internalName);
    return Request.Get(makeFileRequestPath(filename)).execute().returnContent().asStream();
  }

  public String selectedFilename(String internalName) {
    return getStaticPropertyByName(internalName, FileStaticProperty.class).getLocationPath();
  }

  public String selectedFileFetchUrl(String internalName) {
    return makeFileRequestPath(selectedFilename(internalName));
  }

  private String makeFileRequestPath(String filename) {
    return BackendConfig.INSTANCE.getBackendUrl()
            + "/streampipes-backend/api/v2/noauth"
            + "/files/"
            + filename;
  }

  private <V, T extends SelectionStaticProperty> V selectedSingleValue(String internalName, Class<V> targetClass, Class<T> oneOfStaticProperty) {
    return typeParser.parse(getStaticPropertyByName(internalName, oneOfStaticProperty)
            .getOptions()
            .stream()
            .filter(Option::isSelected)
            .findFirst()
            .get()
            .getName(), targetClass);
  }


  /**
   * @deprecated Use {@link #selectedSingleValue(String, Class)} instead
   */
  @Deprecated
  public <V> V selectedSingleValueFromRemote(String internalName, Class<V> targetClass) {
    return selectedSingleValue(internalName, targetClass);
  }

  public <V> V selectedSingleValue(String internalName, Class<V> targetClass) {
    return selectedSingleValue(internalName, targetClass, OneOfStaticProperty.class);
  }

  public <V> V selectedSingleValueInternalName(String internalName, Class<V> targetClass) {
    return typeParser.parse(getStaticPropertyByName(internalName, OneOfStaticProperty.class)
            .getOptions()
            .stream()
            .filter(Option::isSelected)
            .findFirst()
            .get()
            .getInternalName(), targetClass);
  }

  public List<StaticPropertyGroup> collectionMembersAsGroup(String internalName) {
    return getStaticPropertyByName(internalName, CollectionStaticProperty.class)
            .getMembers()
            .stream()
            .map(sp -> (StaticPropertyGroup) sp)
            .sorted(Comparator.comparingInt(StaticProperty::getIndex))
            .collect(Collectors.toList());
  }

  public StaticProperty extractGroupMember(String internalName, StaticPropertyGroup group) {
    return getStaticPropertyByName(group.getStaticProperties(), internalName);
  }

  public <V> List<V> singleValueParameterFromCollection(String internalName, Class<V> targetClass) {
    CollectionStaticProperty collection = getStaticPropertyByName(internalName, CollectionStaticProperty.class);
    return collection
            .getMembers()
            .stream()
            .sorted(Comparator.comparingInt(StaticProperty::getIndex))
            .map(sp -> (FreeTextStaticProperty) sp)
            .map(FreeTextStaticProperty::getValue)
            .map(v -> typeParser.parse(v, targetClass))
            .collect(Collectors.toList());
  }

  public <V> List<V> selectedMultiValues(String internalName, Class<V> targetClass) {
    return getStaticPropertyByName(internalName, AnyStaticProperty.class)
            .getOptions()
            .stream()
            .filter(Option::isSelected)
            .map(Option::getName)
            .map(o -> typeParser.parse(o, targetClass))
            .collect(Collectors.toList());
  }

  private <S extends StaticProperty> S getStaticPropertyByName(String internalName, Class<S>
          spType) {
    return spType.cast(getStaticPropertyByName(internalName));
  }

  public StaticProperty getStaticPropertyByName(String name) {
    return getStaticPropertyByName(sepaElement.getStaticProperties(), name);
  }

  private StaticProperty getStaticPropertyByName(List<StaticProperty> staticProperties,
                                                 String name) {
    for (StaticProperty p : staticProperties) {
      if (p.getInternalName().equals(name)) {
        return p;
      } else if (p.getStaticPropertyType() == StaticPropertyType.StaticPropertyGroup) {
        return getStaticPropertyByName(((StaticPropertyGroup) p).getStaticProperties(), name);
      } else if (p.getStaticPropertyType() == StaticPropertyType.StaticPropertyAlternatives) {
        StaticProperty tmp = getStaticPropertyFromSelectedAlternative((StaticPropertyAlternatives) p);
        if (tmp != null) {
          tmp = getStaticPropertyByName(Collections.singletonList(tmp), name);
          if (tmp != null && tmp.getInternalName().equals(name)) {
            return tmp;
          }
        }
      }
    }
    return null;
  }

  private StaticProperty getStaticPropertyFromSelectedAlternative(StaticPropertyAlternatives sp) {
    return sp.getAlternatives()
            .stream()
            .filter(StaticPropertyAlternative::getSelected)
            .findFirst()
            .get()
            .getStaticProperty();
  }

  public String mappingPropertyValue(String staticPropertyName) {
    return getPropertySelectorFromUnaryMapping(staticPropertyName);
  }

  public List<String> getUnaryMappingsFromCollection(String collectionStaticPropertyName) {
    CollectionStaticProperty collection = getStaticPropertyByName(collectionStaticPropertyName, CollectionStaticProperty.class);
    return collection
            .getMembers()
            .stream()
            .sorted(Comparator.comparingInt(StaticProperty::getIndex))
            .map(sp -> (MappingPropertyUnary) sp)
            .map(MappingPropertyUnary::getSelectedProperty)
            .collect(Collectors.toList());
  }

  public List<String> mappingPropertyValues(String staticPropertyName) {
    return getPropertySelectorsFromNaryMapping(staticPropertyName);
  }

  public String propertyDatatype(String runtimeName) {
    List<EventProperty> eventProperties = new ArrayList<>();
    for (SpDataStream is : sepaElement.getInputStreams()) {
      eventProperties.addAll(is.getEventSchema().getEventProperties());
    }

    Optional<EventProperty> matchedProperty = eventProperties
            .stream()
            .filter(ep -> ep.getRuntimeName().equals
                    (runtimeName))
            .findFirst();

    if (matchedProperty.isPresent()) {
      EventProperty p = matchedProperty.get();
      if (p instanceof EventPropertyPrimitive) {
        return ((EventPropertyPrimitive) p).getRuntimeType();
      } else if (p instanceof EventPropertyList) {
        EventProperty listProperty = ((EventPropertyList) p).getEventProperty();
        if (listProperty instanceof EventPropertyPrimitive) {
          return ((EventPropertyPrimitive) listProperty).getRuntimeType();
        }
      }
    }
    // TODO exceptions
    return null;
  }

  public <V> V supportedOntologyPropertyValue(String domainPropertyInternalId, String
          propertyId, Class<V> targetClass) {
    DomainStaticProperty dsp = getStaticPropertyByName(domainPropertyInternalId,
            DomainStaticProperty.class);

    return typeParser.parse(dsp
            .getSupportedProperties()
            .stream()
            .filter(sp -> sp.getPropertyId().equals(propertyId))
            .findFirst()
            .map(SupportedProperty::getValue)
            .get(), targetClass);

  }

  public List<EventProperty> getEventPropertiesBySelector(List<String> selectors) throws
          SpRuntimeException {
    List<EventProperty> properties = new ArrayList<>();
    for (String selector : selectors) {
      properties.add(getEventPropertyBySelector(selector));
    }
    return properties;
  }

  public EventProperty getEventPropertyBySelector(String selector) throws SpRuntimeException {
    SpDataStream input = getStreamBySelector(selector);

    List<EventProperty> matchedProperties = getEventProperty(selector, getStreamSelector
            (selector), input.getEventSchema().getEventProperties());

    if (matchedProperties.size() > 0) {
      return matchedProperties.get(0);
    } else {
      throw new SpRuntimeException("Could not find property with selector " + selector);
    }
  }

  public String getEventPropertyTypeBySelector(String selector) throws SpRuntimeException {

    EventProperty eventProperty = getEventPropertyBySelector(selector);
    if (eventProperty instanceof EventPropertyPrimitive) {
      return ((EventPropertyPrimitive) eventProperty).getRuntimeType();
    } else {
      throw new SpRuntimeException("Property with selector " + selector + " is not an EventPropertyPrimitive");
    }

  }

  private List<EventProperty> getEventProperty(String selector, String currentPointer,
                                               List<EventProperty> properties) {
    for (EventProperty property : properties) {
      if (makePropertyWithSelector(currentPointer, property.getRuntimeName()).equals(selector)) {
        return Collections.singletonList(property);
      } else if (EventPropertyNested.class.isInstance(property)) {
        return getEventProperty(selector, makePropertyWithSelector(currentPointer, property
                .getRuntimeName()), ((EventPropertyNested) property).getEventProperties());
      }
    }

    return Collections.emptyList();
  }

  private String makePropertyWithSelector(String currentPointer, String runtimeName) {
    return currentPointer + PropertySelectorConstants.PROPERTY_DELIMITER + runtimeName;
  }

  private SpDataStream getStreamBySelector(String selector) {
    String streamId = getStreamSelector(selector).substring(1);
    return sepaElement.getInputStreams().get(Integer.parseInt(streamId));
  }

  private String getStreamSelector(String selector) {
    return selector.split(PropertySelectorConstants.PROPERTY_DELIMITER)[0];
  }

  public List<EventProperty> getNoneInputStreamEventPropertySubset(List<String> propertySelectors) {
    List<EventProperty> properties = new ArrayList<>();
    for (SpDataStream stream : sepaElement.getInputStreams()) {
      properties.addAll(getNoneInputStreamEventPropertySubset(propertySelectors, sepaElement.getInputStreams().indexOf(stream)));
    }
    return properties;
  }

  private List<EventProperty> getNoneInputStreamEventPropertySubset(List<String> propertySelectors, Integer streamIndex) {
    return sepaElement
            .getInputStreams()
            .get(streamIndex)
            .getEventSchema()
            .getEventProperties()
            .stream()
            .filter(ep -> propertySelectors
                    .stream()
                    .noneMatch(ps -> getBySelector(ep.getRuntimeName(), streamIndex).equals(ps)))
            .collect(Collectors.toList());
  }


  public List<EventProperty> getInputStreamEventPropertySubset(List<String> propertySelectors) {
    List<EventProperty> properties = new ArrayList<>();
    for (SpDataStream stream : sepaElement.getInputStreams()) {
      properties.addAll(getInputStreamEventPropertySubset(propertySelectors, sepaElement.getInputStreams().indexOf(stream)));
    }
    return properties;
  }

  private List<EventProperty> getInputStreamEventPropertySubset(List<String> propertySelectors, Integer streamIndex) {
    return sepaElement
            .getInputStreams()
            .get(streamIndex)
            .getEventSchema()
            .getEventProperties()
            .stream()
            .filter(ep -> propertySelectors
                    .stream()
                    .anyMatch(ps -> getBySelector(ep.getRuntimeName(), streamIndex).equals(ps)))
            .collect(Collectors.toList());
  }

  private String getBySelector(String runtimeName, Integer streamIndex) {
    return getStreamIndex(streamIndex) + PropertySelectorConstants.PROPERTY_DELIMITER + runtimeName;
  }

  private String getStreamIndex(Integer streamIndex) {
    return "s" + streamIndex;
  }

  private String getPropertySelectorFromUnaryMapping(String staticPropertyName) {
    Optional<MappingPropertyUnary> property = sepaElement.getStaticProperties().stream()
            .filter(p -> p instanceof MappingPropertyUnary)
            .map((p -> (MappingPropertyUnary) p))
            .filter(p -> p.getInternalName().equals(staticPropertyName))
            .findFirst();

    return property.map(MappingPropertyUnary::getSelectedProperty).orElse(null);
  }

  private List<String> getPropertySelectorsFromNaryMapping(String staticPropertyName) {
    Optional<MappingPropertyNary> property = sepaElement.getStaticProperties().stream()
            .filter(p -> p instanceof MappingPropertyNary)
            .map((p -> (MappingPropertyNary) p))
            .filter(p -> p.getInternalName().equals(staticPropertyName))
            .findFirst();

    return property.map(MappingPropertyNary::getSelectedProperties).orElse(new ArrayList<>());
  }

  public String selectedAlternativeInternalId(String alternativesInternalId) {
    StaticPropertyAlternatives alternatives = getStaticPropertyByName(alternativesInternalId,
            StaticPropertyAlternatives.class);

    return alternatives
            .getAlternatives()
            .stream()
            .filter(StaticPropertyAlternative::getSelected)
            .map(StaticProperty::getInternalName)
            .findFirst().get();
  }

  public List<String> getEventPropertiesRuntimeNamesByScope(PropertyScope scope) {
    List<String> propertiesSelector = new ArrayList<>();

    List<EventProperty> properties = new ArrayList<>();
    for (SpDataStream stream : sepaElement.getInputStreams()) {
      int streamIndex = sepaElement.getInputStreams().indexOf(stream);
      getEventPropertiesByScope(scope, streamIndex)
              .stream()
              .forEach(ep -> propertiesSelector.add(ep.getRuntimeName()));

      properties.addAll(getEventPropertiesByScope(scope, sepaElement.getInputStreams().indexOf(stream)));
    }
    return propertiesSelector;
  }

  public List<String> getEventPropertiesSelectorByScope(PropertyScope scope) {
    List<String> propertiesSelector = new ArrayList<>();

    List<EventProperty> properties = new ArrayList<>();
    for (SpDataStream stream : sepaElement.getInputStreams()) {
      int streamIndex = sepaElement.getInputStreams().indexOf(stream);
      getEventPropertiesByScope(scope, streamIndex)
              .stream()
              .forEach(ep -> propertiesSelector.add(getBySelector(ep.getRuntimeName(), streamIndex)));

      properties.addAll(getEventPropertiesByScope(scope, sepaElement.getInputStreams().indexOf(stream)));
    }
    return propertiesSelector;
  }

  public List<EventProperty> getEventPropertiesByScope(PropertyScope scope) {
    List<EventProperty> properties = new ArrayList<>();
    for (SpDataStream stream : sepaElement.getInputStreams()) {
      properties.addAll(getEventPropertiesByScope(scope, sepaElement.getInputStreams().indexOf(stream)));
    }
    return properties;
  }

  private List<EventProperty> getEventPropertiesByScope(PropertyScope scope, Integer streamIndex) {
    return sepaElement
            .getInputStreams()
            .get(streamIndex)
            .getEventSchema()
            .getEventProperties()
            .stream()
            .filter(ep ->
                    ep.getPropertyScope() != null && ep.getPropertyScope().equals(scope.name()))
            .collect(Collectors.toList());
  }
}
