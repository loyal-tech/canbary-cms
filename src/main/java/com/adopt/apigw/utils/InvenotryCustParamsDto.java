package com.adopt.apigw.utils;

import lombok.Data;

import java.util.List;

@Data
public class InvenotryCustParamsDto {

    List<Long> serializedItemIds;

    List<ProductParameterDefaultValueMappingDTO> parameters;
}
