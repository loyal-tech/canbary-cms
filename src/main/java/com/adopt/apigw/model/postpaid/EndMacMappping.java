package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblendmacmapping")
@EntityListeners(AuditableListener.class)
public class EndMacMappping extends Auditable implements IBaseData<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custmacmapid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "macaddress", length = 100)
    private String macAddress;

//    @JsonBackReference
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "custid")
//    private Customers customer;

    @Column(name = "ownerId", length = 100)
    private Integer ownerId;

    @Column(name = "ownerType", length = 100)
    private String ownerType;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Override
    public Integer getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    public EndMacMappping() {
    }

    public EndMacMappping(EndMacMappping endMacMapping) {
        this.id = endMacMapping.getId();
        this.macAddress = endMacMapping.getMacAddress();
        this.ownerId = endMacMapping.getOwnerId();
        this.ownerType = endMacMapping.getOwnerType();;
        this.isDeleted = endMacMapping.getIsDeleted();
    }
}
