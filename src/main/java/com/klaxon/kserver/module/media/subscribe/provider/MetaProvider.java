package com.klaxon.kserver.module.media.subscribe.provider;

import java.util.List;

public interface MetaProvider {

    Boolean isSupport();

    List<MovieItem> parse();

}
