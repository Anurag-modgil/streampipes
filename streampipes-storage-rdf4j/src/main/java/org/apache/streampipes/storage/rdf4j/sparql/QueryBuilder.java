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

package org.apache.streampipes.storage.rdf4j.sparql;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.model.client.ontology.Concept;
import org.apache.streampipes.model.client.ontology.Instance;
import org.apache.streampipes.model.client.ontology.OntologyQueryItem;
import org.apache.streampipes.model.client.ontology.PrimitiveRange;
import org.apache.streampipes.model.client.ontology.Property;
import org.apache.streampipes.model.client.ontology.QuantitativeValueRange;
import org.apache.streampipes.model.client.ontology.RangeType;
import org.apache.streampipes.storage.rdf4j.util.BackgroundKnowledgeUtils;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.StreamPipes;

import java.util.List;

public class QueryBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(QueryBuilder.class);

  public static final String RDFS_SUBCLASS_OF = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
  public static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
  public static final String RDFS_CLASS = "http://www.w3.org/2000/01/rdf-schema#Class";
  public static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
  public static final String RDFS_DESCRIPTION = "http://www.w3.org/2000/01/rdf-schema#description";
  public static final String RDFS_RANGE = "http://www.w3.org/2000/01/rdf-schema#range";
  public static final String RDFS_DOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
  public static final String SP = StreamPipes.NS;

  public static final String SO_DOMAIN_INCLUDES = "http://schema.org/domainIncludes";
  public static final String SO_RANGE_INCLUDES = "http://schema.org/rangeIncludes";

  public static String buildListDataStreamQuery() {
    return "where { ?result rdf:type sp:DataStream. }";
  }

  public static String buildListDataSetQuery() {
    return "where { ?result rdf:type sp:DataSet. }";
  }

  private static String getPrefix() {
    StringBuilder builder = new StringBuilder();
    builder.append("PREFIX sp:<https://streampipes.org/vocabulary/v1/>\n")
            .append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");

    return builder.toString();
  }

  public static String getMatching() {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix())
            .append("select ?d where {?a rdf:type sp:EventProperty. ?b rdf:type sp:EventSchema. ?b " +
                    "sp:hasEventProperty ?a. ?a sp:hasPropertyName ?d}");
    return builder.toString();
  }

  public static String getAllStatements() {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix()).append("select ?a where { ?a ?b ?c }");

    return builder.toString();
  }

  public static String buildListSEPAQuery() {
    StringBuilder builder = new StringBuilder();
    builder.append("where { ?result rdf:type sp:DataProcessorDescription. }");

    return builder.toString();
  }

  public static String buildListSECQuery() {
    StringBuilder builder = new StringBuilder();
    builder.append("where { ?result rdf:type sp:DataSinkDescription }");

    return builder.toString();
  }

  public static String getClasses() {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?result where {?result rdf:type <http://www.w3.org/2000/01/rdf-schema#Class>}");
    return builder.toString();
  }

  public static String getEventProperties() {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?result where {?result rdf:type <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>}");
    return builder.toString();
  }

  public static String getTypeDetails(String typeId) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?label ?description ?domainPropertyId where { "
            + getLabelDescriptionQueryPart(typeId)
            + " ?domainPropertyId <" + SO_DOMAIN_INCLUDES + "> <" + typeId + "> . }");
    return builder.toString();
  }

  public static String getInstanceDetails(String instanceId) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?label ?description ?property where { "
            + getLabelDescriptionQueryPart(instanceId)
            + " <" + instanceId + "> ?property ?o ."
            + " }");

    return builder.toString();
  }

  public static String getRdfType(String instanceId) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?typeOf where { "
            + " <" + instanceId + "> <" + RDF_TYPE + "> " + " ?typeOf . }");
    return builder.toString();
  }

  public static String getSubclasses(String className) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?s where {?s <" + RDFS_SUBCLASS_OF + "> <" + className + ">}");
    return builder.toString();
  }

  public static String getInstances(String className) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?s where {?s <" + RDF_TYPE + "> <" + className + ">}");
    return builder.toString();
  }

  public static String getAutocompleteSuggestion(String propertyName) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?label ?value where { "//?s <" +RDF_TYPE +"> sepa:DomainConcept ."
            + "?s <" + propertyName + "> ?value ."
            + "?s rdfs:label ?label ."
            + "}"
    );
    return builder.toString();
  }

  public static String getProperty(String propertyId) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?label ?description ?range ?rangeType where {"
            + getLabelDescriptionQueryPart(propertyId)
            + "<" + propertyId + "> <" + SO_RANGE_INCLUDES + "> " + " ?range ."
            + "?range <" + RDF_TYPE + "> " + " ?rangeType ."
            + "}");
    LOG.info(builder.toString());
    return builder.toString();
  }

  public static String getQuantitativeValueRange(String rangeId) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?minValue ?maxValue ?unitCode where {"
            + "<" + rangeId + "> <" + RDF_TYPE + "> " + " <http://schema.org/QuantitativeValue> ."
            + "<" + rangeId + "> <" + SO.MinValue + "> " + " ?minValue ."
            + "<" + rangeId + "> <" + SO.MaxValue + "> " + " ?maxValue ."
            + "<" + rangeId + "> <" + SO.UnitCode + "> " + " ?unitCode ."
            + "}");

    return builder.toString();
  }

  public static String deletePropertyDetails(String propertyId) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " DELETE { ?s ?p ?o }"
            + " WHERE { ?s ?p ?o ."
            + " FILTER (?s  = <" + propertyId + "> && ("
            + " ?p = <" + RDFS_LABEL + "> || "
            + " ?p = <" + RDFS_DESCRIPTION + "> || "
            + " ?p = <" + SO_RANGE_INCLUDES + "> )) } ");

    return builder.toString();
  }

  public static String deleteConceptDetails(String conceptId) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " DELETE { ?s ?p ?o }"
            + " WHERE { ?s ?p ?o ."
            + " FILTER ( (?s  = <" + conceptId + "> && ("
            + " ?p = <" + RDFS_LABEL + "> || "
            + " ?p = <" + RDFS_DESCRIPTION + ">) || "
            + " ( ?o = <" + conceptId + "> && ?p = <" + SO_DOMAIN_INCLUDES + ">) )) } ");

    return builder.toString();
  }

  public static String addConceptDetails(Concept concept) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " INSERT DATA {"
            + "<" + concept.getElementHeader().getId() + "> <" + RDFS_LABEL + "> '" + concept.getRdfsLabel() + "' ."
            + "<" + concept.getElementHeader().getId() + "> <" + RDFS_DESCRIPTION + "> '" + concept.getRdfsDescription() + "' .");

    concept.getDomainProperties().forEach(dp -> builder.append("<" + dp.getElementHeader().getId() + "> <" + SO_DOMAIN_INCLUDES + "> <" + concept.getElementHeader().getId() + "> ."));

    builder.append(" }");
    return builder.toString();

  }

  public static String addPropertyDetails(Property property) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " INSERT DATA {"
            + "<" + property.getElementHeader().getId() + "> <" + RDFS_LABEL + "> '" + property.getRdfsLabel() + "' ."
            + "<" + property.getElementHeader().getId() + "> <" + RDFS_DESCRIPTION + "> '" + property.getRdfsDescription() + "' .");

    if (property.getRange().getRangeType() == RangeType.PRIMITIVE) {
      builder.append("<" + property.getElementHeader().getId() + "> <" + SO_RANGE_INCLUDES + "> <" + ((PrimitiveRange) property.getRange()).getRdfsDatatype() + "> .");
    } else if (property.getRange().getRangeType() == RangeType.QUANTITATIVE_VALUE) {
      builder.append("<" + property.getElementHeader().getId() + "> <" + SO_RANGE_INCLUDES + "> <" + SO.QuantitativeValue + "> .");
    } else if (property.getRange().getRangeType() == RangeType.ENUMERATION) {
      builder.append("");
    }

    builder.append(" }");
    return builder.toString();

  }

  public static String deleteInstanceDetails(String id) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " DELETE { ?s ?p ?o }"
            + " WHERE { ?s ?p ?o ."
            + " FILTER ((?s  = <" + id + "> && ("
            + " ?p != <" + RDF_TYPE + "> )) || (?s = <" + id + "-qv>) || (?o = <" + id + "-qv>)) } ");

    return builder.toString();
  }

  public static String addInstanceDetails(Instance instance) {

    StringBuilder builder = new StringBuilder();
    String id = instance.getElementHeader().getId();

    builder.append(getPrefix() + " INSERT DATA {"
            + "<" + instance.getElementHeader().getId() + "> <" + RDFS_LABEL + "> '" + instance.getRdfsLabel() + "' ."
            + "<" + instance.getElementHeader().getId() + "> <" + RDFS_DESCRIPTION + "> '" + instance.getRdfsDescription() + "' .");

    for (Property property : instance.getDomainProperties()) {
      if (property.getRange().getRangeType() == RangeType.PRIMITIVE) {
        String value = ((PrimitiveRange) property.getRange()).getValue();
        String rangeTypeRdfs = ((PrimitiveRange) property.getRange()).getRdfsDatatype();
        try {
          builder.append("<" + id + "> <" + property.getElementHeader().getId() + "> " + BackgroundKnowledgeUtils.parse(value, rangeTypeRdfs) + " .");
        } catch (RepositoryException | IllegalArgumentException e) {
          e.printStackTrace();
        }
      } else if (property.getRange().getRangeType() == RangeType.QUANTITATIVE_VALUE) {
        builder.append("<" + id + "-qv> <" + RDF_TYPE + "> <" + SO.QuantitativeValue + "> .");
        builder.append("<" + id + "> <" + SO.QuantitativeValue + "> <" + id + "-qv> .");
        builder.append("<" + id + "-qv> <" + SO.MinValue + "> \"" + ((QuantitativeValueRange) property.getRange()).getMinValue() + "\" .");
        builder.append("<" + id + "-qv> <" + SO.MaxValue + "> \"" + ((QuantitativeValueRange) property.getRange()).getMaxValue() + "\" .");
        builder.append("<" + id + "-qv> <" + SO.UnitCode + "> \"" + ((QuantitativeValueRange) property.getRange()).getUnitCode() + "\" .");
      }
    }

    builder.append(" }");
    return builder.toString();
  }

  public static String getPrimitivePropertyValue(String instanceId,
                                                 String propertyId) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?value where {"
            + "<" + instanceId + "> <" + propertyId + "> " + " ?value ."
            + "}");

    return builder.toString();
  }

  public static String deleteResource(String resourceId) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " DELETE { ?s ?p ?o }"
            + " WHERE { ?s ?p ?o ."
            + " FILTER ((?s  = <" + resourceId + ">) || (?p = <" + resourceId + ">) || (?o = <" + resourceId + ">))  } ");

    return builder.toString();
  }

  private static String getLabelDescriptionQueryPart(String subject) {
    StringBuilder builder = new StringBuilder();
    builder.append(" OPTIONAL { <" + subject + "> <" + RDFS_LABEL + "> " + " ?label . }"
            + " OPTIONAL { <" + subject + "> <" + RDFS_DESCRIPTION + "> " + " ?description . }");
    return builder.toString();
  }

  public static String getPropertyDetails(String requiredClass,
                                          String propertyId, List<OntologyQueryItem> requiredProperties) {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " select ?label ?description ?propertyValue where {"
            + " OPTIONAL { ?s <" + RDFS_LABEL + "> ?label . }"
            + " OPTIONAL { ?s <" + RDFS_DESCRIPTION + "> ?description . }"
            + " ?s <" + propertyId + "> ?propertyValue . ");

    for (OntologyQueryItem item : requiredProperties) {
      builder.append(" ?s <" + item.getPropertyId() + "> ?" + RandomStringUtils.randomAlphabetic(5) + " .");
    }

    if (requiredClass != null) {
      builder.append(" ?s <" + RDF_TYPE + "> <" + requiredClass + "> .");
    }

    builder.append(" }");
    LOG.info(builder.toString());
    return builder.toString();

  }

  public static String addRequiredTriples() {
    StringBuilder builder = new StringBuilder();
    builder.append(getPrefix() + " INSERT DATA {"
            + "sp:domainProperty <" + RDFS_RANGE + "> " + "rdf:Property ."
            + " }");
    return builder.toString();
  }

}
