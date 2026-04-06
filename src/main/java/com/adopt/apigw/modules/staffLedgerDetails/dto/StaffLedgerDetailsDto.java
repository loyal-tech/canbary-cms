package com.adopt.apigw.modules.staffLedgerDetails.dto;

import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.model.common.StaffUser;
import lombok.*;


import javax.persistence.Column;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StaffLedgerDetailsDto implements IBaseDto2 {

    private Integer id;

    StaffUser staffUser;

    private Long custId;

    private Long creditDocId;

    private String paymentMode;

    private String transactionType;

    private Double amount;

    private String action;

    private Long bankId;

    private String bankName;
    private String remarks;

    private LocalDate date;

    private List<Integer> ledgerIds = new ArrayList<>();

    private List<Double> amountList = new ArrayList<>();

    private String status;

    private LocalDate chequedate;

    private String chequeno; //ChequeNo

    private String currency;

    private Double totalCollected;
    private Double totalWithdraw;

    private String custName;

    private String debitDocumentNumber;

    public StaffLedgerDetailsDto(Double totalCollected, Double totalWithdraw) {
        this.totalCollected = totalCollected != null ? totalCollected : 0.0;
        this.totalWithdraw = totalWithdraw != null ? totalWithdraw : 0.0;
    }


    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

    @Override
    public Long getBuId() {
        return null;
    }

    public String staffLedgerDetailsname() {
        return staffLedgerDetailsname();
    }

    public void setBuId(Long aLong) {
    }
}
