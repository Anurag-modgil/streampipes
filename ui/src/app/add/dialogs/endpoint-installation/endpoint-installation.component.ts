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

import {Component, Input} from "@angular/core";
import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {PipelineElementEndpointService} from "../../../platform-services/apis/pipeline-element-endpoint.service";

@Component({
  selector: 'endpoint-installation-dialog',
  templateUrl: './endpoint-installation.component.html',
  styleUrls: ['./endpoint-installation.component.scss']
})
export class EndpointInstallationComponent {

  endpointItems: any;

  @Input()
  install: boolean;

  @Input()
  endpointItemsToInstall: any;

  installedItemsChanged: boolean = false;

  installationStatus: any;
  installationFinished: boolean;
  page: string;
  nextButton: string;
  installationRunning: boolean;

  constructor(private DialogRef: DialogRef<EndpointInstallationComponent>,
              private PipelineElementEndpointService: PipelineElementEndpointService) {
    this.installationStatus = [];
    this.installationFinished = false;
    this.page = "preview";
    this.nextButton = "Next";
    this.installationRunning = false;
  }

  close() {
    this.DialogRef.close(this.installedItemsChanged);
  }

  next() {
    if (this.page === "installation") {
      this.close();
    } else {
      this.page = "installation";
      this.initiateInstallation(this.endpointItemsToInstall[0], 0);
    }
  }

  initiateInstallation(endpointUri, index) {
    this.installationRunning = true;
    this.installationStatus.push({"name": endpointUri.name, "id": index, "status": "waiting"});
    if (this.install) {
      this.installElement(endpointUri, index);
    } else {
      this.uninstallElement(endpointUri, index);
    }
  }

  installElement(endpointUri, index) {
    endpointUri = encodeURIComponent(endpointUri.uri);

    this.PipelineElementEndpointService.add(endpointUri, true)
        .subscribe(data => {
          if (data.success) {
            this.installationStatus[index].status = "success";
          } else {
            this.installationStatus[index].status = "error";
            this.installationStatus[index].details = data.notifications[0].additionalInformation;
          }
        }, data => {
          this.installationStatus[index].status = "error";
        })
        .add(() => {
          if (index < this.endpointItemsToInstall.length - 1) {
            index++;
            this.initiateInstallation(this.endpointItemsToInstall[index], index);
          } else {
            this.installedItemsChanged = true;
            this.nextButton = "Close";
            this.installationRunning = false;
          }
        });
  }

  uninstallElement(endpointUri, index) {
    this.PipelineElementEndpointService.del(endpointUri.uri)
        .subscribe(data => {
          if (data.success) {
            this.installationStatus[index].status = "success";
          } else {
            this.installationStatus[index].status = "error";
          }
        }, data => {
          this.installationStatus[index].status = "error";
        })
        .add(() => {
          if (index < this.endpointItemsToInstall.length - 1) {
            index++;
            this.initiateInstallation(this.endpointItemsToInstall[index], index);
          } else {
            this.nextButton = "Close";
            this.installationRunning = false;
            this.installedItemsChanged = true;
          }
        });
  }
}