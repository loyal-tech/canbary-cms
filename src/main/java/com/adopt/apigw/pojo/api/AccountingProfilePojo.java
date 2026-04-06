package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class AccountingProfilePojo extends Auditable {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String status;

    @NotNull
    private String checkitem;

    @NotNull
    private String enablecdr;

    @NotNull
    private String managesession;

    @NotNull
    private Integer priority;

    @NotNull
    private Integer mappingmasterid;

    private Boolean isDelete = false;

    @JsonIgnore
    @Override
    public String toString() {
        return "AccountingProfilePojo [id=" + id + ", name=" + name + ", status=" + status + ", checkitem=" + checkitem
                + ", enablecdr=" + enablecdr + ", managesession=" + managesession + ", priority=" + priority
                + ", mappingmasterid=" + mappingmasterid + ", createdate=" + getCreatedate() + ", updatedate=" + getUpdatedate()
                + "]";
    }
}
