package com.klaxon.kserver.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class WebPageTagVo {

    @NotBlank(message = "请求参数有误，请核对后重试")
    private Long id;
    private List <WebPageTagItem> tags;

    @Data
    public static class WebPageTagItem {
        private Long tagId;
        private String tagName;
    }
}
