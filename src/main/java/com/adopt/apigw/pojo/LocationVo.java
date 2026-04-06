package com.adopt.apigw.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LocationVo {

    private String latitude;
    private String longitude;
}
