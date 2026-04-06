package com.adopt.apigw.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DebitDocDetailDTO {

    String chargeType;
    Double totalAmount;
    Integer debitDocDetailId;
    String debitDocNumber;
    String custName;

    Integer chargeId;
    Integer taxId;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime billdate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fromDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime toDate;
    private Integer mvnoId;
    private Boolean isInvoiceVoid;
    List<Integer> debitDocDetailIds = new ArrayList<>();

    public DebitDocDetailDTO(String chargeType, Double totalAmount) {
        this.chargeType = chargeType;
        this.totalAmount = totalAmount;
    }

    public DebitDocDetailDTO(String chargeType, Double totalAmount, Integer debitDocDetailId, String debitDocNumber, String custName, LocalDateTime billdate,Integer chargeId,Integer taxId) {
        this.chargeType = chargeType;
        this.totalAmount = totalAmount;
        this.debitDocDetailId = debitDocDetailId;
        this.debitDocNumber = debitDocNumber;
        this.custName = custName;
        this.billdate = billdate;
        this.chargeId=chargeId;
        this.taxId =taxId;
    }

    public DebitDocDetailDTO(String chargeType, double totalAmount, List<Integer> debitDocDetailIds,Integer chargeId) {
        this.chargeType = chargeType;
        this.totalAmount = totalAmount;
        this.debitDocDetailIds = debitDocDetailIds;
        this.chargeId=chargeId;
    }
}
