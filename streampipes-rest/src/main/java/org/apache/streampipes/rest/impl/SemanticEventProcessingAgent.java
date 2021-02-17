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

package org.apache.streampipes.rest.impl;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.api.IPipelineElement;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.util.SpMediaType;
import org.apache.streampipes.storage.rdf4j.filter.Filter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/users/{username}/sepas")
public class SemanticEventProcessingAgent extends AbstractRestResource implements IPipelineElement {

	@GET
	@Path("/available")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getAvailable(@PathParam("username") String username) {
		List<DataProcessorDescription> sepas = Filter.byUri(getPipelineElementRdfStorage().getAllDataProcessors(),
				getUserService().getAvailableSepaUris(username));
		return ok(sepas);
	}
	
	@GET
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getFavorites(@PathParam("username") String username) {
		List<DataProcessorDescription> sepas = Filter.byUri(getPipelineElementRdfStorage().getAllDataProcessors(),
				getUserService().getFavoriteSepaUris(username));
		return ok(sepas);
	}

	@GET
	@Path("/own")
	@RequiresAuthentication
	@JacksonSerialized
	@Produces({MediaType.APPLICATION_JSON, SpMediaType.JSONLD})
	@Override
	public Response getOwn(@PathParam("username") String username) {
		List<DataProcessorDescription> sepas = Filter.byUri(getPipelineElementRdfStorage().getAllDataProcessors(),
				getUserService().getOwnSepaUris(username));
		List<DataProcessorInvocation> si = sepas
						.stream()
						.map(s -> new DataProcessorInvocation(new DataProcessorInvocation(s)))
						.collect(Collectors.toList());

		return ok(si);
	}

	@POST
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response addFavorite(@PathParam("username") String username, @FormParam("uri") String elementUri) {
		getUserService().addSepaAsFavorite(username, decode(elementUri));
		return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}

	@DELETE
	@Path("/favorites/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response removeFavorite(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		getUserService().removeSepaFromFavorites(username, decode(elementUri));
		return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}
	
	@DELETE
	@Path("/own/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response removeOwn(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		getUserService().deleteOwnSepa(username, elementUri);
		getPipelineElementRdfStorage().deleteDataProcessor(getPipelineElementRdfStorage().getDataProcessorById(elementUri));
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}

	@Path("/{elementUri}/jsonld")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public String getAsJsonLd(@PathParam("elementUri") String elementUri) {
		return toJsonLd(getPipelineElementRdfStorage().getDataProcessorById(elementUri));
	}

	@Path("/{elementUri}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getElement(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		// TODO Access rights
		return ok(new DataProcessorInvocation(new DataProcessorInvocation(getPipelineElementRdfStorage().getDataProcessorById(elementUri))));
	}

}
