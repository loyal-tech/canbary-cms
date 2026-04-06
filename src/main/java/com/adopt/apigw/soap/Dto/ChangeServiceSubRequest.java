package com.adopt.apigw.soap.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeServiceSubRequest {
    private String userName;
    private List<Override> overrides;
    private String serviceId;

}
