package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.PlanService;
import lombok.Data;

@Data
public class PlanServiceMessageKpi {

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

    private Boolean feasibility;

    private Boolean poc;

    private Boolean installation;

    private Boolean provisioning;

    private Boolean isPriceEditable;

    private Long feasibilityTeamId;

    private Long pocTeamId;

    private Long installationTeamId;

    private Long provisioningTeamId;

    public PlanServiceMessageKpi(PlanService message) {
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
        this.feasibility = message.getFeasibility();
        this.poc = message.getPoc();
        this.installation = message.getInstallation();
        this.provisioning = message.getProvisioning();
        this.isPriceEditable = message.getIsPriceEditable();
        this.feasibilityTeamId = message.getFeasibilityTeamId();
        this.pocTeamId = message.getPocTeamId();
        this.installationTeamId = message.getInstallationTeamId();
        this.provisioningTeamId = message.getProvisioningTeamId();

    }
}
