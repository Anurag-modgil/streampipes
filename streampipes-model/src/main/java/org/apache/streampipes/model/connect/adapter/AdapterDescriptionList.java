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

package org.apache.streampipes.model.connect.adapter;

import io.fogsy.empire.annotations.Namespaces;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.shared.annotation.TsModel;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:AdapterDescriptionList")
@Entity
@TsModel
public class AdapterDescriptionList extends NamedStreamPipesEntity {
    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:list")
    private List<AdapterDescription> list;

    public AdapterDescriptionList() {
        super("http://streampipes.org/adapterlist", "", "");
        list = new ArrayList<>();
    }

    public AdapterDescriptionList(List<AdapterDescription> adapterDescriptions) {
        super("http://streampipes.org/adapterlist", "", "");
        list = adapterDescriptions;
    }

    public List<AdapterDescription> getList() {
        return list;
    }

    public void setList(List<AdapterDescription> list) {
        this.list = list;
    }
}
