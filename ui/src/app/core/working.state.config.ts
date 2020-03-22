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

import {HomeComponent} from '../home/home.component';
import {ConfigurationComponent} from '../configuration/configuration.component';
import {AppContainerComponent} from '../app-container/app-container.component';
import {ConnectComponent} from '../connect/connect.component';

export default function stateConfig($stateProvider, $urlRouterProvider) {
  //	    $urlRouterProvider.otherwise( function($injector, $location) {
  //            var $state = $injector.get("$state");
  //            $state.go("streampipes");
  //        });

  $urlRouterProvider.otherwise('/streampipes');

  var spNavbar = {
    templateUrl: '../../assets/templates/navbar.html',
    controller: 'AppCtrl',
  };

  var spIconBar = {
    templateUrl: '../../assets/templates/iconbar.html',
    controller: 'AppCtrl',
  };

  var container = {
    templateUrl: '../../assets/templates/streampipes.html',
    controller: 'AppCtrl',
  };

  $stateProvider
    .state('streampipes', {
      url: '/streampipes',
      views: {
        container: container,
        'spNavbar@streampipes': spNavbar,
        'spIconBar@streampipes': spIconBar,
        'spMain@streampipes': {
          component: HomeComponent,
        },
      },
      resolve: {
        authData: function (AuthService) {
          return AuthService.authenticate();
        },
      },
    })
    .state('streampipes.pipelineDetails', {
      url: '/pipelines/:pipeline/details',
      views: {
        'spMain@streampipes': {
          templateUrl: '../pipeline-details/pipeline-details.html',
          controller: 'PipelineDetailsCtrl',
        },
      },
    })
    .state('login', {
      url: '/login/:target?session',
      params: { target: null },
      views: {
        container: {
          templateUrl: '../../assets/templates/login.html',
          controller: 'LoginCtrl',
        },
      },
    })
    /*
        .state('streampipes.applinks', {
            url: '/applinks',
            views: {
                "spMain@streampipes": {
                    templateUrl: 'app/applinks/applinks.tmpl.html',
                    controller: 'AppLinksCtrl'
                }
            }
        })
        */
    .state('register', {
      url: '/register',
      views: {
        container: {
          templateUrl: '../../assets/templates/register.html',
          controller: 'RegisterCtrl',
        },
      },
    })
    .state('setup', {
      url: '/setup',
      views: {
        container: {
          templateUrl: '../../assets/templates/setup.html',
          controller: 'SetupCtrl',
        },
      },
    })
    .state('streampipes.error', {
      url: '/error',
      views: {
        'spMain@streampipes': {
          templateUrl: '../../assets/templates/error.html',
          controller: 'SetupCtrl',
        },
      },
    })
    .state('streampipes.notifications', {
      url: '/notifications',
      views: {
        'spMain@streampipes': {
          templateUrl: '../notifications/notifications.component.html',
          controller: 'NotificationsCtrl',
        },
      },
    })
    .state('streampipes.editor', {
      url: '/editor/:pipeline',
      params: { pipeline: null },
      views: {
        'spMain@streampipes': {
          templateUrl: '../editor/editor.html',
          controller: 'EditorCtrl',
        },
      },
    })
    .state('streampipes.pipelines', {
      url: '/pipelines/:pipeline',
      params: { pipeline: null },
      views: {
        'spMain@streampipes': {
          templateUrl: '../pipelines/pipelines.html',
          controller: 'PipelineCtrl',
        },
      },
    })
    .state('streampipes.dashboard', {
      url: '/dashboard',
      views: {
        'spMain@streampipes': {
          templateUrl: '../dashboard/dashboard.html',
          controller: 'DashboardCtrl',
        },
      },
    })
    .state('streampipes.sensors', {
      url: '',
      views: {
        'spMain@streampipes': {
          templateUrl: '../sensors/sensors.html',
          controller: 'SensorsCtrl',
        },
      },
    })
    .state('streampipes.add', {
      url: '/add',
      views: {
        'spMain@streampipes': {
          templateUrl: '../add/add.html',
          controller: 'AddCtrl',
        },
      },
    })
    .state('streampipes.myelements', {
      url: '/myelements',
      views: {
        'spMain@streampipes': {
          templateUrl: '../myelements/myelements.html',
          controller: 'MyElementsCtrl',
        },
      },
    })
    .state('streampipes.configuration', {
      url: '/configuration',
      views: {
        'spMain@streampipes': {
          component: ConfigurationComponent,
        },
      },
    })
    .state('streampipes.appcontainer', {
      url: '/appcontainer',
      views: {
        'spMain@streampipes': {
          component: AppContainerComponent,
        },
      },
    })
    .state('streampipes.connect', {
      url: '/connect',
      views: {
        'spMain@streampipes': {
          component: ConnectComponent,
        },
      },
    });
}
