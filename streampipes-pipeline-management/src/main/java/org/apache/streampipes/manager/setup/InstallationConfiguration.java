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

package org.apache.streampipes.manager.setup;

import org.apache.streampipes.manager.endpoint.EndpointFetcher;
import org.apache.streampipes.model.client.endpoint.RdfEndpoint;
import org.apache.streampipes.model.client.setup.InitialSettings;

import java.util.ArrayList;
import java.util.List;

public class InstallationConfiguration {

	public static List<InstallationStep> getInstallationSteps(InitialSettings settings)
	{
		List<InstallationStep> steps = new ArrayList<>();

		steps.add(new CouchDbInstallationStep());
		steps.add(new UserRegistrationInstallationStep(settings.getAdminEmail(), settings.getAdminPassword()));
    steps.add(new EmpireInitializerInstallationStep());

		if (settings.getInstallPipelineElements()) {
			for(RdfEndpoint endpoint : new EndpointFetcher().getEndpoints()) {
				steps.add(new PipelineElementInstallationStep(endpoint, settings.getAdminEmail()));
			}
		}

		steps.add(new CacheInitializationInstallationStep());
		
		return steps;
	}
}
