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

#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config;

import org.apache.streampipes.config.SpConfig;
import org.apache.streampipes.container.model.PeConfig;

public enum Config implements PeConfig {

  INSTANCE;

  private SpConfig config;
  public static final String JAR_FILE = "./streampipes-processing-element-container.jar";

  private final static String SERVICE_ID = "pe/${package}";

  Config() {
    config = SpConfig.getSpConfig(SERVICE_ID);

    config.register(ConfigKeys.HOST, "${artifactId}", "Hostname for the pe source component");
    config.register(ConfigKeys.PORT, 8090, "Port for the pe source component");
    config.register(ConfigKeys.ICON_HOST, "backend", "Hostname for the icon host");
    config.register(ConfigKeys.ICON_PORT, 80, "Port for the icons in nginx");

    config.register(ConfigKeys.SERVICE_NAME, "${packageName}", "The name of the service");

  }

  @Override
  public String getHost() {
    return config.getString(ConfigKeys.HOST);
  }

  @Override
  public int getPort() {
    return config.getInteger(ConfigKeys.PORT);
  }

  public static final String iconBaseUrl = "http://" + Config.INSTANCE.getIconHost() + ":" +
          Config.INSTANCE.getIconPort() + "/assets/img/pe_icons";

  public static final String getIconUrl(String pictureName) {
    return iconBaseUrl + "/" + pictureName + ".png";
  }

  public String getIconHost() {
    return config.getString(ConfigKeys.ICON_HOST);
  }

  public int getIconPort() {
    return config.getInteger(ConfigKeys.ICON_PORT);
  }

  @Override
  public String getId() {
    return SERVICE_ID;
  }

  @Override
  public String getName() {
    return config.getString(ConfigKeys.SERVICE_NAME);
  }

}
