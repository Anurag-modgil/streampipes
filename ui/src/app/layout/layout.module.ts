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

import * as angular from 'angular';
import ngCookies from 'angular-cookies';
import 'npm/angular-notification-icons';

import spServices from '../services/services.module'
import {AppCtrl} from './app.controller'
import {ToolbarController} from "./toolbar.controller";
import {FeedbackComponent} from "./components/feedback.component";
import {NotificationCountService} from "../services/notification-count-service";

export default angular.module('sp.layout', [spServices, ngCookies, 'angular-notification-icons', 'ngAnimate'])
	.component('spFeedback', FeedbackComponent)
	.controller('AppCtrl', AppCtrl)
	.controller('ToolbarController', ToolbarController)
	.name;
