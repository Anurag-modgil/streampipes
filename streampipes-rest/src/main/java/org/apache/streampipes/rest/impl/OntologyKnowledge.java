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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.repository.RepositoryException;

import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.client.ontology.Concept;
import org.apache.streampipes.model.client.ontology.Instance;
import org.apache.streampipes.model.client.ontology.Namespace;
import org.apache.streampipes.model.client.ontology.Property;
import org.apache.streampipes.model.client.ontology.Resource;
import org.apache.streampipes.rest.api.IOntologyKnowledge;
import org.apache.streampipes.storage.management.StorageManager;

@Path("/v2/ontology")
public class OntologyKnowledge extends AbstractRestInterface implements IOntologyKnowledge {

	@Path("/types")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getTypeHiearchy() {
		try {
			return ok(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getClassHierarchy());
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			return null;
		}
		
	}

	@Path("/properties")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getPropertyHierarchy() {
		try {
			return ok(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getPropertyHierarchy());
		} catch (RepositoryException | MalformedQueryException
				| QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Path("/properties/{propertyId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getProperty(@PathParam("propertyId") String propertyId) {
		try {
			return ok(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getProperty(propertyId));
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			return ok(Notifications.error("Could not load details"));
		}
	}

	@Path("/types/{typeId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getType(@PathParam("typeId") String typeId) {
		try {
			return ok(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getConcept(typeId));
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok(typeId);
	}

	@Override
	@Path("/namespaces")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNamespaces() {
		try {
			return ok(StorageManager
					.INSTANCE
					.getBackgroundKnowledgeStorage()
					.getNamespaces());
		} catch (RepositoryException e) {
			e.printStackTrace();
			return ok(Notifications.error("Could not connect to Sesame storage"));
		}
	}
	
	@Override
	@Path("/namespaces")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNamespace(Namespace namespace) {
		boolean success = StorageManager
				.INSTANCE
				.getBackgroundKnowledgeStorage()
				.addNamespace(namespace);
		if (success) {
			return ok(Notifications.success("Namespace successfully added."));
		}
		else {
			return ok(Notifications.error("Could not add namespace. "));
		}
	}

	@Override
	@Path("/namespaces/{prefix}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNamespace(@PathParam("prefix") String prefix) {
		boolean success = StorageManager
				.INSTANCE
				.getBackgroundKnowledgeStorage()
				.deleteNamespace(prefix);
		if (success) {
			return ok(Notifications.success("Namespace successfully removed."));
		}
		else {
			return ok(Notifications.error("Could not remove namespace. "));
		}
	}

	@Override
	@Path("/properties")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addProperty(Resource elementData) {
		boolean success = StorageManager
                .INSTANCE
                .getBackgroundKnowledgeStorage()
                .addProperty(elementData);
		if (success) {
			return ok(Notifications.success("Property successfully added."));
		}
		else {
			return ok(Notifications.error("Could not add property. "));
		}
	}

	@Override
	@Path("/types")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addType(Resource elementData) {
		boolean success = StorageManager
				.INSTANCE
				.getBackgroundKnowledgeStorage()
				.addConcept(elementData);
		if (success) {
			return ok(Notifications.success("Concept successfully added."));
		}
		else {
			return ok(Notifications.error("Could not add concept. "));
		}
	}

	@Override
	@Path("/instances")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addInstance(Resource elementData) {
		boolean success = StorageManager
				.INSTANCE
				.getBackgroundKnowledgeStorage()
				.addIndividual(elementData);
		if (success) {
			return ok(Notifications.success("Instance successfully added."));
		}
		else {
			return ok(Notifications.error("Could not add instance. "));
		}
	}

	@Override
	@Path("/properties/{propertyId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProperty(@PathParam("propertyId") String propertyId, Property propertyData) {
		boolean success = StorageManager
				.INSTANCE
				.getBackgroundKnowledgeStorage()
				.updateProperty(propertyData);
		if (success) {
			return ok(Notifications.success("Property successfully updated."));
		}
		else {
			return ok(Notifications.error("Could not update property. "));
		}
		
	}

	@Override
	@Path("/types/{typeId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateType(@PathParam("typeId") String typeId, Concept concept) {
		boolean success = StorageManager
				.INSTANCE
				.getBackgroundKnowledgeStorage()
				.updateConcept(concept);
		if (success) {
			return ok(Notifications.success("Concept successfully updated."));
		}
		else {
			return ok(Notifications.error("Could not update concept. "));
		}
	}
	
	@Path("/instances/{instanceId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getInstance(@PathParam("instanceId") String instanceId) {
		try {
			return ok(StorageManager
					.INSTANCE
					.getBackgroundKnowledgeStorage()
					.getInstance(instanceId));
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			return ok(Notifications.error("Instance not found"));
		}
	}

	@Override
	@Path("/instances/{instanceId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInstance(@PathParam("instanceId") String instanceId, Instance instance) {
		boolean success = StorageManager
				.INSTANCE
				.getBackgroundKnowledgeStorage()
				.updateInstance(instance);
		if (success) return ok(Notifications.success("Instance successfully updated."));
		else return ok(Notifications.error("Could not update instance. "));
	}

	@Override
	@Path("/properties/{propertyId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProperty(@PathParam("propertyId") String propertyId) {
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().deleteResource(propertyId);
		if (success) return ok(Notifications.success("Property successfully deleted."));
		else return ok(Notifications.error("Could not delete property. "));
	}

	@Override
	@Path("/types/{typeId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteType(@PathParam("typeId") String typeId) {
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().deleteResource(typeId);
		if (success) return ok(Notifications.success("Concept successfully deleted."));
		else return ok(Notifications.error("Could not delete concept. "));
	}

	@Override
	@Path("/instances/{instanceId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInstance(@PathParam("instanceId") String instanceId) {
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().deleteResource(instanceId);
		if (success) return ok(Notifications.success("Instance successfully deleted."));
		else return ok(Notifications.error("Could not delete instance. "));
	}

}
