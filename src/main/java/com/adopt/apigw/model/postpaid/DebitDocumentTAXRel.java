package com.adopt.apigw.model.postpaid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbltdebitdocumenttaxrel")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebitDocumentTAXRel  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debitdoctaxid")
    private Integer debitdoctaxid;

    @Column(name = "debitdocumentid")
    private Integer debitdocumentid;

    @Column(name = "taxid")
    private Integer taxid;

    @Column(name = "taxname")
    private String taxname;

    @Column(name = "description")
    private String description;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "taxlevel")
    private Double taxlevel;

    @Column(name="startdate")
    private LocalDateTime startdate;
    @Column(name="enddate")
    private LocalDateTime enddate;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "chargeid")
    private String chargeid;

    @Column(name = "tax_ledger_id")
    private String taxLedgerId;


    public DebitDocumentTAXRel(Double taxlevel,String chargeid,String taxName, Double percentage, Double amount){
        this.taxname=taxName;
        this.percentage=percentage;
        this.amount=amount;
        this.chargeid=chargeid;
        this.taxlevel=taxlevel;
    }

    public DebitDocumentTAXRel(String taxName,Double amount, String taxLedgerId){
        this.taxname=taxName;
        this.amount=amount;
        this.taxLedgerId=taxLedgerId;
    }

    public DebitDocumentTAXRel() {
    }
}
