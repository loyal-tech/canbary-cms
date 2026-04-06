package com.adopt.apigw.pojo.api;

import lombok.Data;

import javax.validation.constraints.NotNull;

import com.adopt.apigw.model.common.Auditable2;

@Data
public class CountryPojo extends Auditable2 {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String status;

    private Boolean isDelete = false;
    
//    private Integer mvnoId;

    private Integer displayId;
    private String displayName;

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

}
