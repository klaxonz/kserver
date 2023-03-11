package com.klaxon.kserver.entity.dto;


import com.klaxon.kserver.common.BaseParam;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import javax.validation.constraints.NotBlank;

@Data
public class WebPageDto extends BaseParam {

    @URL
    @NotBlank(message = "资源路径不能为空")
    private String url;
    private String source;
    private Long groupId;
    private String type;

}
