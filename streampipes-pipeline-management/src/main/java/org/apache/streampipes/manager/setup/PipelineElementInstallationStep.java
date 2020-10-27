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

import org.apache.streampipes.manager.endpoint.EndpointItemParser;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.client.endpoint.RdfEndpoint;
import org.apache.streampipes.model.client.endpoint.RdfEndpointItem;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PipelineElementInstallationStep implements InstallationStep {

  private RdfEndpoint endpoint;
  private String userEmail;

  public PipelineElementInstallationStep(RdfEndpoint endpoint, String userEmail) {
    this.endpoint = endpoint;
    this.userEmail = userEmail;
  }

  @Override
  public List<Message> install() {
    List<Message> statusMessages = new ArrayList<>();
    List<RdfEndpointItem> items = Operations.getEndpointUriContents(Collections.singletonList(endpoint));
    for(RdfEndpointItem item : items) {
      statusMessages.add(new EndpointItemParser().parseAndAddEndpointItem(item.getUri(),
              userEmail, true, false));
    }

    Message installMessage;
    if (statusMessages.stream().allMatch(Message::isSuccess)) {
      installMessage = Notifications.success(getTitle());
    } else {
      installMessage = Notifications.error(getTitle());
    }

    return Collections.singletonList(installMessage);
  }

  @Override
  public String getTitle() {
    return "Installing pipeline elements from " +endpoint.getEndpointUrl();
  }
}
