package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable2;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DepartmentPojo extends Auditable2 {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String status;

    private Boolean isDelete = false;
    
//    private Integer mvnoId;

    private Integer displayId;
    private String displayName;
    private List<Integer> planIds;
    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

}
