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

import {Component, OnInit} from "@angular/core";
import {BasicProfileSettings} from "../basic-profile-settings";
import {RawUserApiToken, UserApiToken} from "../../../core-model/gen/streampipes-model-client";
import {MatTableDataSource} from "@angular/material/table";

@Component({
  selector: 'token-management-settings',
  templateUrl: './token-management-settings.component.html',
  styleUrls: ['./token-management-settings.component.scss']
})
export class TokenManagementSettingsComponent extends BasicProfileSettings implements OnInit {

  newTokenName: string;
  newTokenCreated: boolean = false;
  newlyCreatedToken: RawUserApiToken;

  displayedColumns: string[] = ['name', 'action'];
  apiKeyDataSource: MatTableDataSource<UserApiToken>;

  ngOnInit(): void {
    this.receiveUserData();
  }

  requestNewKey() {
    let baseToken: RawUserApiToken = this.makeBaseToken();
    this.profileService.requestNewApiToken(baseToken).subscribe(result => {
      this.newlyCreatedToken = result;
      this.newTokenCreated = true;
      this.newTokenName = "";
      this.receiveUserData();
    });
  }

  makeBaseToken(): RawUserApiToken {
    let baseToken = new RawUserApiToken();
    baseToken.tokenName = this.newTokenName;
    return baseToken;
  }

  revokeApiKey(apiKey: UserApiToken) {
    var removeIndex = this.userData.userApiTokens.map(token => token.tokenId).indexOf(apiKey.tokenId);
    this.userData.userApiTokens.splice(removeIndex, 1);
    this.profileService.updateUserProfile(this.userData).subscribe(response => {
      this.receiveUserData();
    })
  }

  onUserDataReceived() {
    this.apiKeyDataSource = new MatTableDataSource<UserApiToken>(this.userData.userApiTokens);
  }

}
