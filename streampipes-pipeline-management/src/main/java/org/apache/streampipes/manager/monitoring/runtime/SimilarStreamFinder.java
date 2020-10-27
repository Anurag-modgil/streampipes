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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.model.quality.MeasurementCapability;
import org.apache.streampipes.model.quality.MeasurementObject;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.storage.management.StorageManager;

import java.util.ArrayList;
import java.util.List;

public class SimilarStreamFinder {

	private Pipeline pipeline;
	
	private List<SpDataStream> similarStreams;
	
	public SimilarStreamFinder(String pipelineId) {
		this.pipeline = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().getPipeline(pipelineId);
		this.similarStreams = new ArrayList<>();
	}
	
	public boolean isReplacable() {
		if (pipeline.getStreams().size() > 1 || pipeline.getStreams().size() == 0) return false;
		else {
			return isSimilarStreamAvailable();
		}
	}

	private boolean isSimilarStreamAvailable() {
		
		List<DataSourceDescription> seps = StorageManager.INSTANCE.getPipelineElementStorage().getAllDataSources();
		List<SpDataStream> streams = getEventStreams(seps);
		
		SpDataStream pipelineInputStream = getStream();
		List<MeasurementCapability> pipelineInputStreamCapabilities = pipelineInputStream.getMeasurementCapability();
		List<MeasurementObject> pipelineInputStreamMeasurementObject = pipelineInputStream.getMeasurementObject();
		
		for(SpDataStream stream : streams) {
			if (!stream.getElementId().equals(pipelineInputStream.getElementId())) {
				if (matchesStream(pipelineInputStreamCapabilities, pipelineInputStreamMeasurementObject, stream.getMeasurementCapability(), stream.getMeasurementObject())) {
					similarStreams.add(stream);
				}
			}
		}
		
		return similarStreams.size() > 0;
	}
	
	private boolean matchesStream(
			List<MeasurementCapability> pipelineInputStreamCapabilities,
			List<MeasurementObject> pipelineInputStreamMeasurementObject,
			List<MeasurementCapability> measurementCapability,
			List<MeasurementObject> measurementObject) {
		return matchesCapability(pipelineInputStreamCapabilities, measurementCapability) &&
				matchesObject(pipelineInputStreamMeasurementObject, measurementObject);
	}

	private boolean matchesObject(
			List<MeasurementObject> pipelineInputStreamMeasurementObject,
			List<MeasurementObject> measurementObject) {
		if (pipelineInputStreamMeasurementObject == null | measurementObject == null) return false;
		else return pipelineInputStreamMeasurementObject.stream().allMatch(p -> measurementObject.stream().anyMatch(mc -> mc.getMeasuresObject().toString().equals(p.getMeasuresObject().toString())));
	}

	private boolean matchesCapability(
			List<MeasurementCapability> pipelineInputStreamCapabilities,
			List<MeasurementCapability> measurementCapability) {
		if (pipelineInputStreamCapabilities == null || measurementCapability == null) return false;
		else return pipelineInputStreamCapabilities.stream().allMatch(p -> measurementCapability.stream().anyMatch(mc -> mc.getCapability().toString().equals(p.getCapability().toString())));
	}

	private SpDataStream getStream() {
		String streamId = pipeline.getStreams().get(0).getElementId();
		
		return StorageManager.INSTANCE.getPipelineElementStorage().getEventStreamById(streamId);
	}
	
	private List<SpDataStream> getEventStreams(List<DataSourceDescription> seps) {
		List<SpDataStream> result = new ArrayList<>();
		for(DataSourceDescription sep : seps) {
			result.addAll(sep.getSpDataStreams());
		}
		return result;
	}

	public List<SpDataStream> getSimilarStreams() {
		return similarStreams;
	}
	
	
}
