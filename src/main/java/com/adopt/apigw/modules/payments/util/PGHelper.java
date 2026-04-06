package com.adopt.apigw.modules.payments.util;

import java.io.IOException;
import java.util.Map;

import com.adopt.apigw.modules.placeOrder.model.OrderResponseModel;
import com.adopt.apigw.modules.purchaseDetails.model.PGResponseModel;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.pojo.api.PartnerPojo;

public interface PGHelper {

    OrderResponseModel generateFormData(CustomersPojo customersPojo, PurchaseDetailsDTO purchaseDetailsDTO, PartnerPojo partnerPojo) throws IOException;

    PGResponseModel generatePGResponse(Map<String, Object> response);

}
