package com.adopt.apigw.model.postpaid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "TBLTTRIALDEBITDOCUMENTDETAIL")
public class TrialDebitDocumentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trialdebitdocaddrid")
    private Integer debitdocdetailid;

    @Column(name = "trialdebitdocumentid")
    private Integer debitdocumentid;
    @Column(name = "chargeid")
    private Integer chargeid;
    @Column(name = "chargename")
    private String chargename;
    @Column(name = "description")
    private String description;
    @Column(name = "chargetype")
    private String chargetype;
    @Column(name = "chargecycle")
    private String chargecycle;
    @Column(name = "subtotal")
    private Double subtotal;
    @Column(name = "tax")
    private Double tax;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "totalamount")
    private Double totalamount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "startdate")
    private LocalDateTime startdate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "enddate")
    private LocalDateTime enddate;
    @Column(name = "prorationtype")
    private String prorationtype;
    @Column(name = "noofcycle")
    private Integer noofcycle;

}

