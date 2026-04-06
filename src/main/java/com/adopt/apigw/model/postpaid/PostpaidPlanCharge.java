package com.adopt.apigw.model.postpaid;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "TBLMPOSTPAIDPLANCHARGEREL")
@NoArgsConstructor
public class PostpaidPlanCharge implements Serializable {
	/*

CREATE TABLE TBLMPOSTPAIDPLANCHARGEREL
  (
	POSTPAIDPLANCHARGERELID serial,
	CHARGEID BIGINT UNSIGNED NOT NULL,
	BILLINGCYCLE NUMERIC(2,0),
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	POSTPAIDPLANID bigint UNSIGNED,
	PRIMARY KEY (POSTPAIDPLANCHARGERELID),
	FOREIGN KEY (POSTPAIDPLANID) REFERENCES TBLMPOSTPAIDPLAN (POSTPAIDPLANID),
	FOREIGN KEY (CHARGEID) REFERENCES TBLCHARGES(CHARGEID)
  );


	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSTPAIDPLANCHARGERELID", nullable = false, length = 40)
    private Integer id;

    @DiffIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "CHARGEID")
    private Charge charge;

    @Column(name = "BILLINGCYCLE", nullable = false, length = 40)
    private Integer billingCycle;

    @DiffIgnore
    @CreationTimestamp
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private LocalDateTime createdate;

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "POSTPAIDPLANID")
//    @ToString.Exclude
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    private PostpaidPlan plan;

    @Column(name = "chargeprice")
    private Double chargeprice;

    @Column(name = "chargename")
    private String chargeName;

    @Transient
    @DiffIgnore
    private Integer planId;

    @Transient
    @DiffIgnore
    private Integer chargeId;

    public PostpaidPlanCharge(PostpaidPlanCharge postpaidPlanCharge) {
        this.id = postpaidPlanCharge.getId();
        this.charge = new Charge(postpaidPlanCharge.getCharge());
        this.billingCycle = postpaidPlanCharge.getBillingCycle();
        this.chargeprice = postpaidPlanCharge.getChargeprice();
        this.chargeName = postpaidPlanCharge.getChargeName();
        this.planId = postpaidPlanCharge.getPlanId();
        this.chargeId = postpaidPlanCharge.getCharge().getId();
    }

    @Override
    public String toString() {
        return "PostpaidPlanCharge toString Override :" + chargeName;
    }
}
