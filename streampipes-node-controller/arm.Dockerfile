# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

ARG BASE_IMAGE=arm32v7/openjdk:11-jre-slim

FROM arm32v7/ubuntu:18.04 as build-dev
RUN apt -y update; \
    apt -y --no-install-recommends install qemu-user-static

FROM $BASE_IMAGE
MAINTAINER dev@streampipes.apache.org

EXPOSE 7077
ENV CONSUL_LOCATION consul

COPY --from=build-dev /usr/bin/qemu-arm-static /usr/bin
RUN set -ex; \
    #apt -y update; \
        apt update && apt upgrade; \
        apt -y install gnupg2; \
        apt-key adv --recv-key --keyserver keyserver.ubuntu.com 648ACFD622F3D138; \
        apt-key adv --recv-key --keyserver keyserver.ubuntu.com 0E98404D386FA1D9; \
    apt -y --no-install-recommends install libjffi-jni curl; \
    apt clean; \
    rm -rf /tmp/apache-* /var/lib/apt/lists/*

COPY target/streampipes-node-controller.jar  /streampipes-node-controller.jar

ENTRYPOINT ["java", "-jar", "/streampipes-node-controller.jar"]
