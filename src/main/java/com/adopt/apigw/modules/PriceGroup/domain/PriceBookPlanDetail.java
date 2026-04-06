package com.adopt.apigw.modules.PriceGroup.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.postpaid.PlanGroup;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblpricebookplandtls")
@NoArgsConstructor
public class PriceBookPlanDetail implements IBaseData<Long> {

    @Id
    @Column(name = "pbdetailid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DiffIgnore
    private Long id;

    private Double offerprice;
    private Double partnerofficeprice;
    private String revsharen = "No";
    private String registration = "No";
    private String renewal = "No";

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "bookid")
    private PriceBook priceBook;

    @ManyToOne
    @DiffIgnore
    @JoinColumn(name = "planid")
    private PostpaidPlan postpaidPlan;
    
    @Column(name = "revenue_share_percentage", length = 10)
    private String revenueSharePercentage;
    
    @Column(name = "is_tax_included", columnDefinition = "Boolean default false")
    private Boolean isTaxIncluded = false;


    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }

    @ManyToOne
    @JoinColumn(name = "planbundleid")
    public PlanGroup planGroup;

    @Transient
    private Integer planGroupId;
    @Transient
    private Integer postpaidplanid;
    @Transient
    private Long pricebookid;

    public PriceBookPlanDetail(PriceBookPlanDetail priceBookPlanDetail) {
        this.id = priceBookPlanDetail.getId();
        this.offerprice = priceBookPlanDetail.getOfferprice();
        this.partnerofficeprice = priceBookPlanDetail.getPartnerofficeprice();
        this.revsharen = priceBookPlanDetail.getRevsharen();
        this.registration = priceBookPlanDetail.getRegistration();
        this.renewal = priceBookPlanDetail.getRenewal();
        this.isDeleted = priceBookPlanDetail.getIsDeleted();
        this.revenueSharePercentage = priceBookPlanDetail.getRevenueSharePercentage();
        this.isTaxIncluded = priceBookPlanDetail.getIsTaxIncluded();
        if(priceBookPlanDetail.getPlanGroup()!=null)
            this.planGroupId = priceBookPlanDetail.getPlanGroup().getPlanGroupId();
        if(priceBookPlanDetail.getPostpaidPlan()!=null)
            this.postpaidplanid = priceBookPlanDetail.getPostpaidPlan().getId();
        if(priceBookPlanDetail.getPriceBook()!=null){
            this.pricebookid = priceBookPlanDetail.getPriceBook().getId();
        }
        if(priceBookPlanDetail.getPostpaidPlan()!=null) {
            this.postpaidplanid = priceBookPlanDetail.getPostpaidPlan().getId();
        }
        this.pricebookid = priceBookPlanDetail.getPriceBook().getId();
    }
}
