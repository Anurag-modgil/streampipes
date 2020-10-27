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

package org.apache.streampipes.model.connect.grounding;

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
@RdfsClass("sp:FormatDescriptionList")
@Entity
@TsModel
public class FormatDescriptionList extends NamedStreamPipesEntity {


    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:list")
    private List<FormatDescription> list;

    public FormatDescriptionList() {
        super("http://bla.de#2", "", "");
        list = new ArrayList<>();
    }

    public FormatDescriptionList(List<FormatDescription> formatDescriptions) {
        super("http://bla.de#2", "", "");
        list = formatDescriptions;
    }

    public FormatDescriptionList(FormatDescriptionList other) {
        super(other.getUri(), other.getName(), other.getName());
        if (other.getList() != null) {
            for (FormatDescription fd : other.getList()) {
                this.list.add(new FormatDescription(fd));
            }

        }
    }

    public void addDesctiption(FormatDescription formatDescription) {
        list.add(formatDescription);
    }

    public List<FormatDescription> getList() {
        return list;
    }

    public void setList(List<FormatDescription> list) {
        this.list = list;
    }
}
