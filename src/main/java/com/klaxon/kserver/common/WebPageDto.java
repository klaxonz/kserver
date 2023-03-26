package com.klaxon.kserver.common;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class WebPageDto extends BaseParam {

    private String type;

}
