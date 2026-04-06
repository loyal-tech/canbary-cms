package com.adopt.apigw.modules.PlanQosMapping;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tbltplanqosmapping")
public class PlanQosMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planid")
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PostpaidPlan postpaidPlan;

   @JoinColumn(name="qosid" , referencedColumnName = "id")
   @OneToOne
   private QOSPolicy qosPolicy;

    @Column(name = "from_percentage",  length = 40)
    private Double frompercentage;

    @Column(name = "to_percentage", length = 40)
    private Double topercentage;

    @Column(name = "isdelete")
    private Boolean isdelete;

    @Override
    public String toString() {
        return "PlanQosMappingEntity{" +
                "id=" + id +
                ", frompercentage=" + frompercentage +
                ", topercentage=" + topercentage +
                ", isdelete=" + isdelete +
                '}';
    }
}
