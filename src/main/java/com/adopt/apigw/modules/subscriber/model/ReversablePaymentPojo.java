package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import com.adopt.apigw.pojo.api.CreditDocumentPojo;

@Data
public class ReversablePaymentPojo {
    List<CommonListDTO> paymentModeCommonList;
    List<CreditDocumentPojo> creditDocumentPojo;
}
