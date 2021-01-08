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

package org.apache.streampipes.manager.verification;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.storage.UserManagementService;
import org.apache.streampipes.manager.storage.UserService;
import org.apache.streampipes.manager.verification.messages.VerificationError;
import org.apache.streampipes.manager.verification.messages.VerificationResult;
import org.apache.streampipes.manager.verification.structure.GeneralVerifier;
import org.apache.streampipes.manager.verification.structure.Verifier;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.message.*;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;
import org.apache.streampipes.storage.management.StorageManager;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class ElementVerifier<T extends NamedStreamPipesEntity> {

  protected static final Logger logger = Logger.getAnonymousLogger();

  private String graphData;
  private Class<T> elementClass;
  protected T elementDescription;

  protected List<VerificationResult> validationResults;
  protected List<Verifier> validators;

  protected IPipelineElementDescriptionStorageCache storageApi =
          StorageManager.INSTANCE.getPipelineElementStorage();
  protected UserService userService = UserManagementService.getUserService();

  public ElementVerifier(String graphData, Class<T> elementClass) {
    this.elementClass = elementClass;
    this.graphData = graphData;
    this.validators = new ArrayList<>();
    this.validationResults = new ArrayList<>();
  }

  protected void collectValidators() {
    validators.add(new GeneralVerifier<>(elementDescription));
  }

  protected abstract StorageState store(String username, boolean publicElement,
                                        boolean refreshCache);

  protected abstract void update(String username);

  protected void verify() {
    collectValidators();
    validators.forEach(validator -> validationResults.addAll(validator.validate()));
  }

  public Message verifyAndAdd(String username, boolean publicElement, boolean refreshCache) throws SepaParseException {
    try {
      this.elementDescription = transform();
    } catch (RDFParseException | UnsupportedRDFormatException
            | RepositoryException | IOException e) {
      return new ErrorMessage(NotificationType.UNKNOWN_ERROR.uiNotification());
    }
    verify();
    if (isVerifiedSuccessfully()) {
      StorageState state = store(username, publicElement, refreshCache);
      if (state == StorageState.STORED) {
        try {
          storeAssets();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return successMessage();
      } else if (state == StorageState.ALREADY_IN_SESAME) {
        return addedToUserSuccessMessage();
      } else {
        return skippedSuccessMessage();
      }
    } else {
      return errorMessage();
    }

  }

  public Message verifyAndUpdate(String username) throws SepaParseException {
    try {
      this.elementDescription = transform();
    } catch (JsonProcessingException e) {
      return new ErrorMessage(NotificationType.UNKNOWN_ERROR.uiNotification());
    }
    verify();
    if (isVerifiedSuccessfully()) {
      update(username);
      try {
        updateAssets();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return successMessage();
    } else {
      return errorMessage();
    }

  }

  protected abstract void storeAssets() throws IOException;

  protected abstract void updateAssets() throws IOException;

  private Message errorMessage() {
    return new ErrorMessage(elementDescription.getName(), collectNotifications());
  }

  private Message successMessage() {
    List<Notification> notifications = collectNotifications();
    notifications.add(NotificationType.STORAGE_SUCCESS.uiNotification());
    return new SuccessMessage(elementDescription.getName(), notifications);
  }

  private Message skippedSuccessMessage() {
    List<Notification> notifications = collectNotifications();
    notifications.add(new Notification("Already exists", "This element is already in your list of elements, skipped."));
    return new SuccessMessage(elementDescription.getName(), notifications);
  }

  private Message addedToUserSuccessMessage() {
    List<Notification> notifications = collectNotifications();
    notifications.add(new Notification("Already stored", "Element description already stored, added element to user"));
    return new SuccessMessage(elementDescription.getName(), notifications);
  }

  private List<Notification> collectNotifications() {
    List<Notification> notifications = new ArrayList<>();
    validationResults.forEach(vr -> notifications.add(vr.getNotification()));
    return notifications;
  }

  private boolean isVerifiedSuccessfully() {
    return !validationResults.stream().anyMatch(validator -> (validator instanceof VerificationError));
  }

  protected T transform() throws JsonProcessingException {
    return JacksonSerializer.getObjectMapper().readValue(graphData, elementClass);
  }
}
