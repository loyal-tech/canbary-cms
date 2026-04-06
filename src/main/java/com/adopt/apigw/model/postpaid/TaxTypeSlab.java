package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.pojo.api.TaxTypeSlabPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "TBLMSLABTAX")
public class TaxTypeSlab {

	/*
CREATE TABLE TBLMSLABTAX
  (
    SLABTAXID serial,
    NAME      VARCHAR(64) NOT NULL,
    RANGEFROM NUMERIC(16,4),
    RANGEUPTO NUMERIC(16,4),
    RATE      NUMERIC(10,2),
    TAXID     bigint UNSIGNED,
    PRIMARY KEY (SLABTAXID),
    FOREIGN KEY (TAXID) REFERENCES TBLMTAX (TAXID)
  );
 
	 */

    public TaxTypeSlab() {
        super();
    }

    public TaxTypeSlab(TaxTypeSlabPojo pojo, Tax tax) {
        this.id = pojo.getId();
        this.name = pojo.getName();
        this.rangeFrom = pojo.getRangeFrom();
        this.rangeUpTo = pojo.getRangeUpTo();
        this.rate = pojo.getRate();
        this.tax = tax;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SLABTAXID", nullable = false, length = 40)
    private Integer id;


    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "RANGEFROM", nullable = false, length = 40)
    private Double rangeFrom;

    @Column(name = "RANGEUPTO", nullable = false, length = 40)
    private Double rangeUpTo;

    @Column(name = "RATE", nullable = false, length = 40)
    private Double rate;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "TAXID")
    @ToString.Exclude
    private Tax tax;

    @Column(name = "before_discount")
    private Boolean beforeDiscount = false;

}
