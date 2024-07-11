package com.klaxon.kserver.module.media.subscribe.resource;

import com.klaxon.kserver.module.media.subscribe.task.SubscribeTask;

public class CuppasoResourceProvider implements ResourceProvider {

    @Override
    public String getName() {
        return "Cuppaso";
    }

    @Override
    public SubscribeTask.ExtractInfo parse(String name, Integer year) {



        return null;
    }

}
