package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.pojo.api.DiscountPlanMappingPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "TBLMDISCOUNTPOSTPAIDPLANREL")
public class DiscountPlanMapping {
	
	
	/*
CREATE TABLE TBLMDISCOUNTPOSTPAIDPLANREL
  (
    DISCOUNTPLANRELID serial,
  	DISCOUNTID     bigint UNSIGNED,
    POSTPAIDPLANID bigint UNSIGNED,
    PRIMARY KEY (DISCOUNTPLANRELID),
    FOREIGN KEY (DISCOUNTID) REFERENCES TBLMDISCOUNT (DISCOUNTID),
    FOREIGN KEY (POSTPAIDPLANID) REFERENCES TBLMPOSTPAIDPLAN (POSTPAIDPLANID)
  );
 
	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISCOUNTPLANRELID", nullable = false, length = 40)
    private Integer id;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "DISCOUNTID")
    private Discount discount;

    @DiffIgnore
    @Column(name = "POSTPAIDPLANID", nullable = false, length = 40)
    private Integer planId;

    public DiscountPlanMapping() {
    }

    public DiscountPlanMapping(DiscountPlanMappingPojo discountPlanMappingPojo, Discount discount) {
        this.id = discountPlanMappingPojo.getId();
        this.discount = discount;
        this.planId = discountPlanMappingPojo.getPlanId();
    }


}
