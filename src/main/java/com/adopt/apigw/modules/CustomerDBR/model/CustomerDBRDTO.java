package com.adopt.apigw.modules.CustomerDBR.model;

import com.adopt.apigw.core.dto.IBaseDto2;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CustomerDBRDTO implements IBaseDto2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbrid;
    private Long cusid;
    private Long planid;
    private String custname;
    private String planname;
    private String status;
    private String custtype;
    private Integer validity_days;
    private Long dbr;
    private Double offer_price;
    private LocalDate startdate;
    private LocalDate enddate;
    private Double pendingamt;
    private Long cprid;
    private Double cumm_revenue;
    private List<Double> pending_amt = new ArrayList<>();
    private Long partnerId;

    @Override
    public Long getIdentityKey() {
        return dbrid;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
    }

    @Override
    public Long getBuId() {
        return null;
    }
}
