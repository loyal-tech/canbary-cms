package com.adopt.apigw.modules.fieldMapping;

import lombok.Data;

import java.util.List;

@Data
public class ScreenFieldMappingDto {

    private Long id;
    private Long screenid;
    private Long fieldid;
    private Long parentfieldsid;
    private Long indexing;
    private String fieldType;
    private String endpoint;
    private String dependantfieldName;
    private String backendrequired;
    private Boolean isdependant = false;
    private Boolean isdostrequest = false;
    private String regex;
    private List<FieldsDTO> child;
    private String name;
    private String datatype;
    private Boolean isBounded = false;
}
