package com.adopt.apigw.modules.fieldMapping;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleWiseFieldsDto {

    private String moduleName;
    private List<FieldsDetailsDTO> fields = new ArrayList<>();

}
