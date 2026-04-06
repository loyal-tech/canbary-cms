package com.adopt.apigw.pojo.api;

import com.adopt.apigw.pojo.CreditDoc.CreditDocChargeRelDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ViewAdjustedInvoicePojo {


    private LocalDateTime billdate;

    private String docnumber;

    private Double totalamount;

    private Double adjustedAmount;

    private String invoiceNumber;

    private List<CreditDocChargeRelDTO> creditDocChargeRelDTOList;

}
