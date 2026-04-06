package com.adopt.apigw.modules.fieldMapping;

import com.adopt.apigw.modules.ServiceParameterMapping.model.ServiceParamMappingDTO;
import lombok.Data;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Data
public class FieldsDetailsDTO {
    private Long id;
    private String fieldname;
    private String name;
    private String dataType;
    private String screen;
    private Boolean isBounded = false;
    private String module;
    private Boolean isMandatory;
    private String fieldType;
    private String endpoint;
    private String backendrequired;
    private String dependantfieldName;
    private Boolean isdependant = false;
    private Boolean isdostrequest = false;
    private String regex;
    private List<FieldsDTO> child = new ArrayList<>();
    private Long indexing;

    private String defaultValue;

    private Boolean mandatoryFlag = false ;
    private Boolean defaultMandatory;

    public FieldsDetailsDTO(FieldsDTO fieldsDTO){
        this.id = fieldsDTO.getId();
        this.fieldname = fieldsDTO.getFieldname();
        this.name = fieldsDTO.getName();
        this.dataType = fieldsDTO.getDataType();
        this.isBounded = fieldsDTO.getIsBounded();
        this.fieldType = fieldsDTO.getFieldType();
        this.endpoint = fieldsDTO.getEndpoint();
        this.backendrequired = fieldsDTO.getBackendrequired();
        this.dependantfieldName = fieldsDTO.getDependantfieldName();
        this.isdependant = fieldsDTO.getIsdependant();
        this.isdostrequest = fieldsDTO.getIsdostrequest();
        this.regex = fieldsDTO.getRegex();
        this.child = fieldsDTO.getChild();
        this.indexing = fieldsDTO.getIndexing();
//        this.module = fieldsDTO.getModule();
//        this.isMandatory = fieldsDTO.getIsMandatory();
    }

    public FieldsDetailsDTO(ServiceParamMappingDTO serviceParamMappingDTO) {
        this.setDefaultValue(serviceParamMappingDTO.getValue());
        this.setMandatoryFlag(serviceParamMappingDTO.getIsMandatory());
    }
    public FieldsDetailsDTO() {
    }


}
