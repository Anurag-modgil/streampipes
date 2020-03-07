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

import {RdfId} from '../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {UnnamedStreamPipesEntity} from "./UnnamedStreamPipesEntity";

@RdfsClass('sp:Option')
export class Option extends UnnamedStreamPipesEntity {
  @RdfId
  public id: string;

  @RdfProperty('sp:elementName')
  public elementName: string;

  @RdfProperty('sp:hasName')
  public name: string;

  @RdfProperty('sp:internalName')
  public internalName: string;

  @RdfProperty('sp:isSelected')
  public selected: boolean;

  constructor() {
    super();
  }
}
