package com.adopt.apigw.model.postpaid;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class CustomerLedgerDtlsPojo {
    private Integer id;
    private Integer custId;
    private String paymentMode;
    private String bank;
    private String branch;
    private String paymentRefNo;
    private String description;
    private String transtype;
    private String transcategory;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate CREATE_DATE;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate END_DATE;
    private Integer refNo;
    private Double balAmount;
    private Double amount;
    private Boolean isDelete=false;
    private Boolean isVoid=false;
    private List<String> invoiceNo;
    private String receiptNo;
    private String category;
    private String remarks;

    public void setCustomerId(Integer i) {
    }
}
