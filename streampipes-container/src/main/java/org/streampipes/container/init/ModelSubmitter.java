package org.streampipes.container.init;

import org.streampipes.container.model.PeConfig;

public abstract class ModelSubmitter {
////        return "http://localhost:8080/stream-story/api/v1.1.1/";

    public abstract void init(PeConfig peConfig);

}
