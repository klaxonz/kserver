package com.klaxon.kserver.module.media.model.rsp;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MediaLibraryDetailRsp {

    private Long id;
    private String name;
    private String url;
    private String username;
    private String password;
    private List<String> paths;

}
