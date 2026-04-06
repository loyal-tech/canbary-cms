package com.adopt.apigw.modules.PurchaseOrder.Domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltpurchaseorder")
@EntityListeners(AuditableListener.class)
public class PurchaseOrder extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "purchaseorder_number")
    private String ponumber;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "custid",referencedColumnName = "custid")
    private Customers customer;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buid;

    @Column(name = "filename")
    private String filename;
    @Column(name = "uniquename")
    private String uniquename;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
}
