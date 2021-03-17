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

import {jsPlumbInstance} from 'jsplumb'

export class JsplumbBridge {

    constructor(private jsPlumbInstance: jsPlumbInstance) {
    }

    activateEndpoint(endpointId: string, endpointEnabled: boolean) {
        let endpoint = this.getEndpointById(endpointId);
        endpoint.setEnabled(endpointEnabled);
    }

    activateEndpointWithType(endpointId: string, endpointEnabled: boolean, endpointType: string) {
        this.activateEndpoint(endpointId, endpointEnabled);
        this.setEndpointType(endpointId, endpointType);
    }

    setEndpointType(endpointId: string, endpointType: string) {
        let endpoint = this.getEndpointById(endpointId);
        // @ts-ignore
        endpoint.setType(endpointType);
    }

    getEndpointById(endpointId: string) {
        return this.jsPlumbInstance.getEndpoint(endpointId);
    }

    setZoom(scale) {
        this.jsPlumbInstance.setZoom(scale);
    }

    repaintEverything() {
        this.jsPlumbInstance.repaintEverything(true);
    }

    deleteEveryEndpoint() {
        this.jsPlumbInstance.deleteEveryEndpoint();
    }

    setContainer(container) {
        this.jsPlumbInstance.setContainer(container);
    }

    unbind(element) {
        this.jsPlumbInstance.unbind(element);
    }

    bind(event, fn) {
        return this.jsPlumbInstance.bind(event, fn);
    }

    // TODO: Overloading Functions?
    selectEndpoints(endpoint?) {
        if (endpoint === undefined) {
            // @ts-ignore
            return this.jsPlumbInstance.selectEndpoints();
        }
        // @ts-ignore
        return this.jsPlumbInstance.selectEndpoints(endpoint);
    }

    selectEndpointsById(id) {
        // @ts-ignore
        return this.jsPlumbInstance.selectEndpoints({source: id});
    }

    getSourceEndpoint(id) {
        // @ts-ignore
        return this.jsPlumbInstance.selectEndpoints({source: id});
    }

    getTargetEndpoint(id) {
        // @ts-ignore
        return this.jsPlumbInstance.selectEndpoints({target: id});
    }

    getEndpointCount(id) {
        // @ts-ignore
        return this.jsPlumbInstance.selectEndpoints({element: id}).length;
    }

    detach(connection) {
        this.jsPlumbInstance.deleteConnection(connection);
    }

    getConnections(filter) {
        return this.jsPlumbInstance.getConnections(filter, {});
    }

    addEndpoint(element, options) {
        return this.jsPlumbInstance.addEndpoint(element, options);
    }

    connect(connection) {
        this.jsPlumbInstance.connect(connection);
    }

    removeAllEndpoints(element) {
        this.jsPlumbInstance.removeAllEndpoints(element);
    }

    registerEndpointTypes(typeInfo) {
        this.jsPlumbInstance.registerEndpointTypes(typeInfo);
    }

    draggable(element, option) {
        this.jsPlumbInstance.draggable(element, option);
    }

    // TODO: Overloading Functions?
    setSuspendDrawing(bool1, bool2?) {
        if (bool2 === undefined) {
            this.jsPlumbInstance.setSuspendDrawing(bool1);
        } else {
            this.jsPlumbInstance.setSuspendDrawing(bool1, bool2);
        }
    }

    getAllConnections() {
        return this.jsPlumbInstance.getAllConnections();
    }

    reset() {
        this.jsPlumbInstance.reset();
    }
}

//JsplumbBridge.$inject = [];
