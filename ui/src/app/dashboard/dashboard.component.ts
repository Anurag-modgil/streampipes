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
import {Dashboard} from "./models/dashboard.model";
import {DashboardService} from "./services/dashboard.service";
import {RefreshDashboardService} from "./services/refresh-dashboard.service";
import {Tuple2} from "../core-model/base/Tuple2";

@Component({
    selector: 'dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

    selectedDashboard: Dashboard;
    selectedIndex: number = 0;
    dashboardsLoaded: boolean = false;
    dashboardTabSelected: boolean = false;

    editMode: boolean = false;

    dashboards: Array<Dashboard>;

    constructor(private dashboardService: DashboardService,
                private refreshDashboardService: RefreshDashboardService) {}

    public ngOnInit() {
        this.getDashboards();
        this.refreshDashboardService.refreshSubject.subscribe(currentDashboardId => {
            this.getDashboards(currentDashboardId);
        });

    }

    openDashboard(data: Tuple2<Dashboard, boolean>) {
        let index = this.dashboards.indexOf(data.a);
        this.editMode = data.b;
        this.selectDashboard((index + 1));
    }

    selectDashboard(index: number) {
        this.selectedIndex = index;
        if (index == 0) {
            this.dashboardTabSelected = false;
        } else {
            this.dashboardTabSelected = true;
            this.selectedDashboard = this.dashboards[(index - 1)];
        }
    }

    protected getDashboards(currentDashboardId?: string) {
        this.dashboardsLoaded = false;
        this.dashboardService.getDashboards().subscribe(data => {
            this.dashboards = data;
            if (currentDashboardId) {
                let currentDashboard = this.dashboards.find(d => d._id === currentDashboardId);
                this.selectDashboard(this.dashboards.indexOf(currentDashboard) + 1);
            } else {
                this.selectedIndex = 0;
            }
            this.dashboardsLoaded = true;
        });
    }

    toggleEditMode() {
        this.editMode = ! (this.editMode);
    }
}
