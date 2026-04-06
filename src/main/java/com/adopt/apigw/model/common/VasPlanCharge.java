package com.adopt.apigw.model.common;

import com.adopt.apigw.model.postpaid.Charge;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "tblmvasplanchargerel")
public class VasPlanCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @DiffIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "CHARGEID", referencedColumnName = "CHARGEID")
    private Charge charge;

    @Column(name = "billingcycle")
    private Double billingCycle;

    @DiffIgnore
    @CreationTimestamp
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private LocalDateTime createdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vasplanid", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private VasPlan vasPlan;

    @Column(name = "chargeprice")
    private Double chargePrice;

}
