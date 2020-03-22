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

package org.apache.streampipes.rest.notifications;

import org.apache.streampipes.config.backend.BackendConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class NotificationListener implements ServletContextListener {

  private static final String internalNotificationTopic = "org.apache.streampipes.notifications.>";


  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
  }

  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    if (BackendConfig.INSTANCE.isConfigured()) {
      try {
        new Thread(new StreamPipesNotificationSubscriber(internalNotificationTopic)).start();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
