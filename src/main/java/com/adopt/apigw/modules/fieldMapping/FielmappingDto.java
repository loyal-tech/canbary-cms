package com.adopt.apigw.modules.fieldMapping;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class FielmappingDto implements IBaseDto {
    private Long id;

    @NotNull(message = "fieldId"+ Constants.MANDATORY_NOT_NULL_MSG)
    private Long fieldId;

    @JsonIgnore
    private Long buid;

    private Boolean isMandatory = false;

    @NotBlank(message = "screen" + Constants.MANDATORY_NOT_NULL_MSG)
    private String screen;

    @NotBlank(message = "module" + Constants.MANDATORY_NOT_NULL_MSG)
    private String module;

    private Boolean isDeleted = false;

    @NotBlank(message = "fieldName" + Constants.MANDATORY_NOT_NULL_MSG)
    private String fieldName;

    @NotBlank(message = "dataType" + Constants.MANDATORY_NOT_NULL_MSG)
    private String dataType;

    private Boolean defaultMandatory;

    @Override
    @JsonIgnore
    public Long getIdentityKey() {
        return id;
    }

    @Override
    @JsonIgnore
    public Integer getMvnoId() {
        return null;
    }

    @Override
    @JsonIgnore
    public void setMvnoId(Integer mvnoId) {

    }
}
