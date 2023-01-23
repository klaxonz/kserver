package com.klaxon.kserver.entity.domain;

import lombok.Data;

@Data
public class OnlineUser {

    private Long id;
    private String username;

    public OnlineUser(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
