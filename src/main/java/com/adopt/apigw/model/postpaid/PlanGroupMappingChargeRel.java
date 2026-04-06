package com.adopt.apigw.model.postpaid;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tbltplangroupmappingchargerel")
public class PlanGroupMappingChargeRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @JoinColumn(name = "plan_group_mappingid" , referencedColumnName = "plangroupmappingid")
    @ManyToOne(targetEntity = PlanGroupMapping.class,cascade = CascadeType.ALL)
    private PlanGroupMapping planGroupMapping;

    @JoinColumn(name="chargeid" , referencedColumnName = "CHARGEID")
    @OneToOne
    private Charge charge;

    @Column(name = "price", nullable = false, length = 40)
    private double price;

    @Column(name = "chargename")
    private String chargeName;

    @Column(name ="planid")
    private Integer planId;

    @Column(name = "isdelete")
    private Boolean isdelete = false;

    @Transient
    private Integer planGroupMappingId;

    @Transient
    private Integer chargeid;

    public PlanGroupMappingChargeRel() {
    }

    public PlanGroupMappingChargeRel(PlanGroupMappingChargeRel planGroupMappingChargeRel){
        this.id = planGroupMappingChargeRel.getId();
        this.chargeid = planGroupMappingChargeRel.getCharge().getId();
        this.price = planGroupMappingChargeRel.getPrice();
        this.chargeName = planGroupMappingChargeRel.getChargeName();
        this.planId = planGroupMappingChargeRel.getPlanId();
        this.isdelete = planGroupMappingChargeRel.getIsdelete();
        this.planGroupMappingId = planGroupMappingChargeRel.getPlanGroupMapping().getPlanGroupMappingId();
    }

}
