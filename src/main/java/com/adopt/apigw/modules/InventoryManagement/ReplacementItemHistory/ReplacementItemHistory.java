package com.adopt.apigw.modules.InventoryManagement.ReplacementItemHistory;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltreplacementitemhistory")
@EntityListeners(AuditableListener.class)
public class ReplacementItemHistory extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "customerid")
    private Long customerId;

    @Column(name = "oldcustomerinventoryid")
    private Long oldcustomerinventoryId;

    @Column(name = "newcustomerinventoryid")
    private Long newcustomerinventoryId;

    @Column(name = "olditemid")
    private Long olditemId;
    @Column(name = "newitemid")
    private Long newitemId;

    @Column(name = "oldmac")
    private String oldmac;

    @Column(name = "newmac")
    private String newmac;

    @Column(name = "olderialnumber")
    private String oldserialNumber;

    @Column(name = "newerialnumber")
    private String newserialNumber;

    @Column(name = "raisedrequeststaffid")
    private Long raisedrequeststaffId;

    @Column(name = "status")
    private String status;

    @Override
    public Long getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
