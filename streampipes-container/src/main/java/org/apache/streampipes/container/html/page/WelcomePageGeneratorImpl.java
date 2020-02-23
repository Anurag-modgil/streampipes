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

package org.apache.streampipes.container.html.page;

import org.apache.streampipes.container.declarer.*;
import org.apache.streampipes.container.html.model.DataSourceDescriptionHtml;
import org.apache.streampipes.model.container.PeContainerElementDescription;
import org.apache.streampipes.container.locales.LabelGenerator;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataSinkDescription;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WelcomePageGeneratorImpl extends WelcomePageGenerator<Declarer> {


    public WelcomePageGeneratorImpl(String baseUri, Collection<Declarer> declarers) {
        super(baseUri, declarers);
    }

    @Override
    public List<PeContainerElementDescription> buildUris() {
        List<PeContainerElementDescription> descriptions = new ArrayList<>();

        for (Declarer declarer : declarers) {
            if (declarer instanceof InvocableDeclarer) {
                descriptions.add(getDescription((InvocableDeclarer) declarer));
            } else if (declarer instanceof SemanticEventProducerDeclarer) {
                descriptions.add(getDescription((SemanticEventProducerDeclarer) declarer));
            } else if (declarer instanceof PipelineTemplateDeclarer) {
                descriptions.add(getDescription(declarer));
            }
        }
        return descriptions;
    }

    private PeContainerElementDescription getDescription(Declarer declarer) {
        PeContainerElementDescription desc = new PeContainerElementDescription();
        // TODO remove after full internationalization support has been implemented
        updateLabel(declarer.declareModel(), desc);
        desc.setType(getType(declarer));
        String uri = baseUri;
        if (declarer instanceof SemanticEventConsumerDeclarer) {
            uri += "sec/";
        } else if (declarer instanceof SemanticEventProcessingAgentDeclarer) {
            uri += "sepa/";
        } else if (declarer instanceof PipelineTemplateDeclarer) {
            uri += "template/";
        }
        desc.setUri(URI.create(uri +declarer.declareModel().getUri().replaceFirst("[a-zA-Z]{4}://[a-zA-Z\\.]+:\\d+/", "")));
        return desc;
    }

    private String getType(Declarer declarer) {
        if (declarer.declareModel() instanceof DataSinkDescription) return "action";
        else return "sepa";
    }

    private PeContainerElementDescription getDescription(SemanticEventProducerDeclarer declarer) {
        List<PeContainerElementDescription> streams = new ArrayList<>();
        DataSourceDescriptionHtml desc = new DataSourceDescriptionHtml();
        updateLabel(declarer.declareModel(), desc);
//        desc.setName(declarer.declareModel().getName());
//        desc.setDescription(declarer.declareModel().getDescription());
        desc.setUri(URI.create(baseUri + "sep/" + declarer.declareModel().getUri()));
        desc.setType("source");
        for (DataStreamDeclarer streamDeclarer : declarer.getEventStreams()) {
            PeContainerElementDescription ad = new PeContainerElementDescription();
//            ad.setDescription(streamDeclarer.declareModel(declarer.declareModel()).getDescription());
//            ad.setName(streamDeclarer.declareModel(declarer.declareModel()).getName());
            updateLabel(streamDeclarer.declareModel(declarer.declareModel()), ad);
            ad.setUri(URI.create(baseUri +"stream/" + streamDeclarer.declareModel(declarer.declareModel()).getUri()));
            ad.setType("stream");
            streams.add(ad);
        }
        desc.setStreams(streams);
        return desc;
    }

    private void updateLabel(NamedStreamPipesEntity entity, PeContainerElementDescription desc) {
        if (!entity.isIncludesLocales()) {
            desc.setName(entity.getName());
            desc.setDescription(entity.getDescription());
        } else {
            LabelGenerator lg = new LabelGenerator(entity);
            try {
                desc.setName(lg.getElementTitle());
                desc.setDescription(lg.getElementDescription());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
