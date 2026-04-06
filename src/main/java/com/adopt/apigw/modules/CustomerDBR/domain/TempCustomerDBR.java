package com.adopt.apigw.modules.CustomerDBR.domain;


import com.adopt.apigw.core.data.IBaseData2;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tbltmpcustomerdbr")
public class TempCustomerDBR implements IBaseData2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dbr_id",length = 20)
    Long dbrid;
    @Column(name = "custid",length = 20)
    Long custid;
    @Column(name = "planid",length = 20)
    Long planid;
    @Column(name = "custname",length = 50)
    String custname;
    @Column(name = "planname",length = 50)
    String planname;
    @Column(name = "validity_days",length = 10)
    Integer validity_days;
    @Column(name = "offer_price",length = 40)
    Double offer_price;
    @Column(name = "status",length = 150)
    String status;
    @Column(name = "custtype",length = 150)
    String custtype;
    @Column(name = "dbr",length = 40)
    Double dbr;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date")
    LocalDate startdate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
    LocalDate enddate;
    @Column(name = "pending_amt")
    Double pendingamt;
    @Column(name = "cprid",length = 20)
    Long cprid;

    @Column(name = "is_direct_charge",length = 20)
    Boolean isDirectCharge;

    @Column(name = "cumm_revenue",length = 20)
    Double cumm_revenue ;

    @Column(name = "invoiceid",length = 20)
    Long invoiceId;

    @Column(name = "partner_id",length = 20)
    Long partnerId;

    @Column(name = "service_id",length = 20)
    Long serviceId;

    @Column(name = "remark")
    String remark="";

    @Column(name = "service_area")
    Long serviceArea;

    @Column(name = "buid")
    Long buId;

    @Column(name = "mvnoid")
    Integer mvnoId;


    @Override
    public Serializable getPrimaryKey() {
        return dbrid;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag){
    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    public void setBuId(Long buId) {
        this.buId=buId;
    }
}