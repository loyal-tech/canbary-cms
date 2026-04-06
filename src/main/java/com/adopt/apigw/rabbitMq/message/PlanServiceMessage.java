package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.PlanService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanServiceMessage {

    private Integer id;

    private String name;

    private String icname;

    private String iccode;

    private Integer mvnoId;

    private Long buId;

    private Boolean isQoSV;

    private String expiry;

    private String ledgerId;

    private Boolean is_dtv;

    public PlanServiceMessage(PlanService message) {
        this.id = message.getId();
        this.name = message.getName();
        this.icname = message.getIcname();
        this.iccode = message.getIccode();
        this.mvnoId = message.getMvnoId();
        this.buId = message.getBuId();
        this.isQoSV = message.getIsQoSV();
        this.expiry = message.getExpiry();
        this.ledgerId = message.getLedgerId();
        this.is_dtv = message.getIs_dtv();
    }

}
