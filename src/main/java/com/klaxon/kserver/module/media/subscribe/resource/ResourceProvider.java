package com.klaxon.kserver.module.media.subscribe.resource;

import com.klaxon.kserver.module.media.subscribe.task.SubscribeTask;

public interface ResourceProvider {

    SubscribeTask.ExtractInfo parse(String name, Integer year);

    String getName();

}
