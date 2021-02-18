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

import org.apache.streampipes.model.client.user.RawUserApiToken;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.user.management.service.TokenService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/v2/users/{email}")
public class User extends AbstractRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserDetails(@PathParam("email") String email) {
        org.apache.streampipes.model.client.user.User user = getUser(email);
        user.setPassword("");

        if (user != null) {
            return ok(user);
        } else {
            return statusMessage(Notifications.error("User not found"));
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserDetails(org.apache.streampipes.model.client.user.User user) {
        if (user != null) {
            org.apache.streampipes.model.client.user.User existingUser = getUser(user.getEmail());
            user.setPassword(existingUser.getPassword());
            user.setUserApiTokens(existingUser
                    .getUserApiTokens()
                    .stream()
                    .filter(existingToken -> user.getUserApiTokens()
                            .stream()
                            .anyMatch(updatedToken -> existingToken
                                    .getTokenId()
                                    .equals(updatedToken.getTokenId())))
                    .collect(Collectors.toList()));
            user.setRev(existingUser.getRev());
            getUserStorage().updateUser(user);
            return ok(Notifications.success("User updated"));
        } else {
            return statusMessage(Notifications.error("User not found"));
        }
    }

    private org.apache.streampipes.model.client.user.User getUser(String email) {
        return getUserStorage().getUser(email);
    }

    @POST
    @Path("tokens")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    public Response createNewApiToken(@PathParam("email") String email, RawUserApiToken rawToken) {
        RawUserApiToken generatedToken = new TokenService().createAndStoreNewToken(email, rawToken);
        return ok(generatedToken);
    }
}
