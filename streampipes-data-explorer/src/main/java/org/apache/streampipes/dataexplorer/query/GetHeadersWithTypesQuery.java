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
package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.dataexplorer.param.QueryParams;
import org.influxdb.dto.QueryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetHeadersWithTypesQuery extends ParameterizedDataExplorerQuery<QueryParams, Map<String, String>> {

  public GetHeadersWithTypesQuery(QueryParams queryParams) {
    super(queryParams);
  }

  @Override
  protected void getQuery(DataExplorerQueryBuilder queryBuilder) {
    queryBuilder.add("SHOW FIELD KEYS FROM " + params.getIndex());
  }

  @Override
  protected Map<String, String> postQuery(QueryResult result) {
    Map<String, String> headerTypes = new HashMap<>();
    for (List<Object> element : result.getResults().get(0).getSeries().get(0).getValues()) {
      headerTypes.put(element.get(0).toString(), element.get(1).toString());
    }
    return headerTypes;
  }
}
