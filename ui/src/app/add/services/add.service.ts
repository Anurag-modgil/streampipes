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

import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {AuthStatusService} from "../../services/auth-status.service";
import {PlatformServicesCommons} from "../../platform-services/apis/commons.service";
import {Observable} from "rxjs";

@Injectable()
export class AddService {


  constructor(private http: HttpClient,
              private authStatusService: AuthStatusService,
              private platformServicesCommons: PlatformServicesCommons) {
  }

  getRdfEndpoints(): Observable<any> {
    return this.http.get(this.platformServicesCommons.authUserBasePath() + "/rdfendpoints");
  }

  getRdfEndpointItems(): Observable<any> {
    return this.http.get(this.platformServicesCommons.authUserBasePath() + "/rdfendpoints/items");
  }

  addRdfEndpoint(rdfEndpoint): Observable<any> {
    return this.http.post(this.platformServicesCommons.authUserBasePath() + "/rdfendpoints", rdfEndpoint);
  }

  removeRdfEndpoint(rdfEndpointId): Observable<any> {
    return this.http.delete(this.platformServicesCommons.authUserBasePath() + "/rdfendpoints/" + rdfEndpointId);
  }
}