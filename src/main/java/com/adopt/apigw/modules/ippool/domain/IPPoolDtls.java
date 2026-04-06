package com.adopt.apigw.modules.ippool.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblippooldtls")
public class IPPoolDtls extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long poolDetailsId;
    private Long poolId;
    private String ipAddress;
    private String status;
    private Boolean isDelete = false;
    @Column(name = "allocated_id")
    private Long allocatedId;
    @Column(name = "unblock_time")
    private LocalDateTime unblockTime;
    @Column(name = "block_by_cust_id")
    private Long blockByCustId;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return poolDetailsId;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }
}
