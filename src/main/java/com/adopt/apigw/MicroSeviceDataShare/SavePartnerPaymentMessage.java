package com.adopt.apigw.MicroSeviceDataShare;

import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerPaymentDTO;
import lombok.Data;

@Data
public class SavePartnerPaymentMessage {
    private PartnerPayment partnerPayment;
    private PartnerPaymentDTO partnerPaymentDTO;
}
