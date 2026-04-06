package com.adopt.apigw.modules.fieldMapping;

import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class FieldsDTO {
    private Long id;
    private String fieldname;
    private String name;
    private String dataType;
    private String fieldType;
    private String endpoint;
    private String dependantfieldName;
    private String backendrequired;
    private Boolean isdependant = false;
    private Boolean isdostrequest = false;
    private String regex;
    @Transient
    private Boolean isBounded = false;
    private List<FieldsDTO> child;
    private Long indexing;

    public FieldsDTO(){}
    public FieldsDTO(ScreenFieldMapping screenFieldMapping) {
        setId(screenFieldMapping.getFields().getId());
        setFieldname(screenFieldMapping.getFields().getFieldname());
        setName(screenFieldMapping.getFields().getName());
        setDataType(screenFieldMapping.getFields().getDataType());
        setIndexing(screenFieldMapping.getIndexing());
        setFieldType(screenFieldMapping.getFieldType());
        setEndpoint(screenFieldMapping.getEndpoint());
        setDependantfieldName(screenFieldMapping.getDependantfieldName());
        setBackendrequired(screenFieldMapping.getBackendrequired());
        setIsdependant(screenFieldMapping.getIsdependant());
        setIsdostrequest(screenFieldMapping.getIspostrequest());
        setRegex(screenFieldMapping.getRegex());
    }
}
