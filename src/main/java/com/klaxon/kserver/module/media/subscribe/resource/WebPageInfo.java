package com.klaxon.kserver.module.media.subscribe.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebPageInfo {
    @JsonProperty("header")
    private String header;
    @JsonProperty("body")
    private String body;
}
