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
import {JsplumbSettings} from "../model/jsplumb.model";

@Injectable()
export class JsplumbConfigService {

    constructor() {
    }

    getEditorConfig() {
        return this.makeConfig(this.makeSettings(12, 5, 30, 30, 2, 80));
    }

    getPreviewConfig() {
        return this.makeConfig(this.makeSettings(6, 2, 15, 15, 1, 40));
    }

    makeConfig(settings) {
        let config = {} as any;
        config.streamEndpointOptions = this.makeStreamEndpointOptions(settings);
        config.sepaEndpointOptions = this.makeSepaEndpointOptions(settings);
        config.leftTargetPointOptions = this.makeLeftTargetPointOptions(settings);
        return config;
    }

    makeSettings(dotRadius: number,
                 lineWidth: number,
                 arrowWidth: number,
                 arrowLength: number,
                 arrowLineWidth: number,
                 curviness: number) {
        let settings = {} as JsplumbSettings;
        settings.dotRadius = dotRadius;
        settings.lineWidth = lineWidth;
        settings.arrowWidth = arrowWidth;
        settings.arrowLength = arrowLength;
        settings.arrowLineWidth = arrowLineWidth;
        settings.curviness = curviness;
        return settings;
    }

    makeStreamEndpointOptions(settings: JsplumbSettings) {
        return {
            endpoint: ["Dot", {radius: settings.dotRadius}],
            connectorStyle: {stroke: "#BDBDBD", outlineStroke: "#BDBDBD", strokeWidth: settings.lineWidth},
            connector: ["Bezier", {curviness: settings.curviness}],
            isSource: true,
            maxConnections: -1,
            anchor: "Right",
            type: "token",
            connectorOverlays: [
                ["Arrow", {
                    width: settings.arrowWidth, length: settings.arrowLength, location: 0.5, id: "arrow", paintStyle: {
                        fillStyle: "#BDBDBD",
                        stroke: "#9E9E9E",
                        strokeWidth: settings.arrowLineWidth
                    }
                }],
            ]
        }
    }

    makeSepaEndpointOptions(settings) {
        return {
            endpoint: ["Dot", {radius: settings.dotRadius}],
            connectorStyle: {
                stroke: "#BDBDBD", outlineStroke: "#9E9E9E", strokeWidth: settings.lineWidth
            },
            connector: ["Bezier", {curviness: settings.curviness}],
            isSource: true,
            maxConnections: -1,
            anchor: "Right",
            type: "empty",
            connectorOverlays: [
                ["Arrow", {
                    width: settings.arrowWidth, length: settings.arrowLength, location: 0.5, id: "arrow", paintStyle: {
                        fill: "#BDBDBD",
                        stroke: "#9E9E9E",
                        strokeWidth: settings.arrowLineWidth
                    }
                }],
            ],
            parameters: {
                endpointType: "output"
            }
        }
    }

    makeLeftTargetPointOptions(settings) {
        return {
            endpoint: ["Dot", {radius: settings.dotRadius}],
            type: "empty",
            anchor: "Left",
            isTarget: true
        }
    }

}
