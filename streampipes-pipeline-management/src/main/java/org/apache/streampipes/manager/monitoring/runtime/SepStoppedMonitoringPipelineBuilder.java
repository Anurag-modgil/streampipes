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

package org.apache.streampipes.manager.monitoring.runtime;

import org.apache.streampipes.commons.exceptions.NoMatchingFormatException;
import org.apache.streampipes.commons.exceptions.NoMatchingProtocolException;
import org.apache.streampipes.commons.exceptions.NoMatchingSchemaException;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.manager.matching.PipelineVerificationHandler;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.model.staticproperty.DomainStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.SupportedProperty;
import org.apache.streampipes.storage.management.StorageManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SepStoppedMonitoringPipelineBuilder {

	// TODO make ULSs dynamic
//	private final String RATE_SEPA_URI = "http://frosch.fzi.de:8090/sepa/streamStopped";
//	private final String KAFKA_SEC_URI = "http://frosch.fzi.de:8091/kafka";

	private final String RATE_SEPA_URI = "http://ipe-koi05.perimeter.fzi.de:8090/sepa/streamStopped";
	private final String KAFKA_SEC_URI = "http://ipe-koi04.perimeter.fzi.de:8091/kafka";
	private final String OUTPUT_TOPIC = "internal.streamepipes.sec.stopped";

	private SpDataStream stream;
	private final String outputTopic;

	private DataSourceDescription dataSourceDescription;

	private DataProcessorDescription streamStoppedDataProcessorDescription;
	private DataSinkDescription kafkaDataSinkDescription;
	private String streamUri;

	public SepStoppedMonitoringPipelineBuilder(String sepUri, String streamUri) throws URISyntaxException {
		this.outputTopic = OUTPUT_TOPIC;
		this.streamUri = streamUri;
		DataSourceDescription desc = StorageManager.INSTANCE.getPipelineElementStorage().getDataSourceById(sepUri);
		this.stream = StorageManager.INSTANCE.getPipelineElementStorage().getEventStreamById(streamUri);
		this.dataSourceDescription = desc;
		this.streamStoppedDataProcessorDescription = getStreamStoppedEpa();
		this.kafkaDataSinkDescription = getKafkaPublisherEc();
	}

	public Pipeline buildPipeline()
			throws NoMatchingFormatException, NoMatchingSchemaException, NoMatchingProtocolException, Exception {
		DataProcessorInvocation rateSepaClient = new DataProcessorInvocation(streamStoppedDataProcessorDescription);
		SpDataStream streamClient = stream instanceof SpDataStream ? new SpDataStream(stream) : new SpDataSet(
						(SpDataSet) stream);
		DataSinkInvocation kafkaActionClient = new DataSinkInvocation(kafkaDataSinkDescription);

		List<NamedStreamPipesEntity> elements = new ArrayList<>();
		elements.add(streamClient);

		rateSepaClient.setConnectedTo(Arrays.asList("stream"));
		streamClient.setDOM("stream");
		rateSepaClient.setDOM("rate");
		kafkaActionClient.setDOM("kafka");

		Pipeline pipeline = new Pipeline();
		pipeline.setStreams(Arrays.asList(streamClient));

		pipeline.setSepas(Arrays.asList(rateSepaClient));

		PipelineModificationMessage message = new PipelineVerificationHandler(pipeline).validateConnection()
				.computeMappingProperties().getPipelineModificationMessage();

		DataProcessorInvocation updatedSepa = updateStreamStoppedSepa(rateSepaClient, message);
		pipeline.setSepas(Arrays.asList(updatedSepa));

		kafkaActionClient.setConnectedTo(Arrays.asList("rate"));
		pipeline.setActions(Arrays.asList(kafkaActionClient));

		message = new PipelineVerificationHandler(pipeline).validateConnection().computeMappingProperties()
				.getPipelineModificationMessage();

		pipeline.setActions(Arrays.asList(updateKafkaSec(kafkaActionClient, message)));

		pipeline.setPipelineId(UUID.randomUUID().toString());
		pipeline.setName("Monitoring - " + stream.getName());

		return pipeline;
	}

	private DataSinkDescription getKafkaPublisherEc() throws URISyntaxException {
		return StorageManager.INSTANCE.getPipelineElementStorage().getDataSinkById(KAFKA_SEC_URI);
	}

	private DataProcessorDescription getStreamStoppedEpa() throws URISyntaxException {
		return StorageManager.INSTANCE.getPipelineElementStorage().getDataProcessorById(RATE_SEPA_URI);
	}

	private DataSinkInvocation updateKafkaSec(DataSinkInvocation actionClient, PipelineModificationMessage message) {
		List<StaticProperty> properties = message.getPipelineModifications().get(0).getStaticProperties();
		List<StaticProperty> newStaticProperties = new ArrayList<>();
		for (StaticProperty p : properties) {
			if (p instanceof FreeTextStaticProperty ||p instanceof DomainStaticProperty) {
				if (p instanceof FreeTextStaticProperty) {
					if (p.getInternalName().equals("topic"))
						((FreeTextStaticProperty) p).setValue(outputTopic);
				}
				else if (p instanceof DomainStaticProperty) {
					for(SupportedProperty sp : ((DomainStaticProperty) p).getSupportedProperties()) {
						if (sp.getPropertyId().equals("http://schema.org/kafkaHost"))
							sp.setValue(String
								.valueOf(BackendConfig.INSTANCE.getKafkaHost()));
						else if (sp.getPropertyId().equals("http://schema.org/kafkaPort"))
							sp.setValue(String
									.valueOf(BackendConfig.INSTANCE.getKafkaPort()));
					}
				}
					
			}
			newStaticProperties.add(p);
		}
		actionClient.setStaticProperties(newStaticProperties);
		return actionClient;
	}

	private DataProcessorInvocation updateStreamStoppedSepa(DataProcessorInvocation newSEPA, PipelineModificationMessage message) {
		List<StaticProperty> properties = message.getPipelineModifications().get(0).getStaticProperties();
		List<StaticProperty> newStaticProperties = new ArrayList<>();
		for (StaticProperty p : properties) {
			if (p instanceof FreeTextStaticProperty) {

					if (p.getInternalName().equals("topic"))
						((FreeTextStaticProperty) p).setValue(String.valueOf(streamUri));

			}
			newStaticProperties.add(p);
		}
		newSEPA.setStaticProperties(newStaticProperties);
		return newSEPA;
	}

	public static void main(String[] args) throws URISyntaxException {

		String SEP_URI = "http://frosch.fzi.de:8089//source-wunderbar";
		String STREAM_URI = "http://frosch.fzi.de:8089//source-wunderbar/accelerometer";

		SepStoppedMonitoringPipelineBuilder pc = new SepStoppedMonitoringPipelineBuilder(SEP_URI, STREAM_URI);

		try {
			Pipeline pipeline = pc.buildPipeline();
			Operations.startPipeline(pipeline, false, false, false);

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String s = br.readLine();

			Operations.stopPipeline(pipeline, false, false, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
