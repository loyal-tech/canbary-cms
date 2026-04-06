package com.adopt.apigw.modules.payments.util;

import org.springframework.stereotype.Component;

import com.adopt.apigw.constants.PGConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.placeOrder.Util.PaymentUtil;
import com.adopt.apigw.modules.placeOrder.model.OrderResponseModel;
import com.adopt.apigw.modules.purchaseDetails.model.PGResponseModel;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.pojo.api.PartnerPojo;
import com.adopt.apigw.utils.PropertyReaderUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class PayuPGHelper implements PGHelper {

    public static final String MODULE = " [PayuPGHelper()] ";
    DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public OrderResponseModel generateFormData(CustomersPojo customersPojo, PurchaseDetailsDTO purchaseDTO, PartnerPojo partnerPojo) throws IOException {
        String SUBMODULE = MODULE + " [generateFormData()] ";
        OrderResponseModel orderResponseModel = new OrderResponseModel();
        if (purchaseDTO.getAmount() == 0.0 && purchaseDTO.getPaymentstatus().equalsIgnoreCase(PGConstants.SUCCESSFUL_STATUS)) {
            orderResponseModel.setIsPaymentRemaining(false);
        } else {
            orderResponseModel.setIsPaymentRemaining(true);
        }
        if (purchaseDTO.getPurchaseStatus().equalsIgnoreCase(PGConstants.SUCCESSFUL_STATUS)) {
            orderResponseModel.setIsPurchased(true);
        } else {
            orderResponseModel.setIsPurchased(false);
        }
        Map<String, String> basicDetailMap = new HashMap<>();
        if (customersPojo == null && partnerPojo != null) {
            if (partnerPojo.getEmail() != null) {
                basicDetailMap.put(PGConstants.EMAIL, partnerPojo.getEmail());
            } else {
                basicDetailMap.put(PGConstants.EMAIL, "demo@gmail.com");
            }
            if (partnerPojo.getMobile() != null) {
                basicDetailMap.put(PGConstants.MOBILE, partnerPojo.getMobile());
            } else {
                basicDetailMap.put(PGConstants.MOBILE, "123123123");
            }
            if (partnerPojo.getName() != null) {
                basicDetailMap.put(PGConstants.NAME, partnerPojo.getName());
            } else {
                basicDetailMap.put(PGConstants.NAME, "demo");
            }
            basicDetailMap.put(PGConstants.SURL, PGConstants.PG_CONFIG_PAYU_PARTNER_SURL);
            basicDetailMap.put(PGConstants.FURL, PGConstants.PG_CONFIG_PAYU_PARTNER_FURL);
        }
        if (customersPojo != null && partnerPojo == null) {
            if (customersPojo.getEmail() != null) {
                basicDetailMap.put(PGConstants.EMAIL, customersPojo.getEmail());
            } else {
                basicDetailMap.put(PGConstants.EMAIL, "demo@gmail.com");
            }
            if (customersPojo.getMobile() != null) {
                basicDetailMap.put(PGConstants.MOBILE, customersPojo.getMobile());
            } else {
                basicDetailMap.put(PGConstants.MOBILE, "123123123");
            }
            if (customersPojo.getFirstname() != null) {
                basicDetailMap.put(PGConstants.NAME, customersPojo.getFirstname());
            } else {
                basicDetailMap.put(PGConstants.NAME, "demo");
            }
            basicDetailMap.put(PGConstants.SURL, PGConstants.PG_CONFIG_PAYU_CUSTOMER_SURL);
            basicDetailMap.put(PGConstants.FURL, PGConstants.PG_CONFIG_PAYU_CUSTOMER_FURL);
        }

        Map<String, Object> map = new HashMap<>();
        try {
            Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
            map.put(PGConstants.PAYU_MAP_KEY, properties.getProperty(PGConstants.PG_CONFIG_PAYU_MERCHANT_KEY)); //Merchant Key
            map.put(PGConstants.PAYU_MAP_AMOUNT, df.format(purchaseDTO.getAmount()));
            map.put(PGConstants.PAYU_MAP_FIRSTNAME, basicDetailMap.get(PGConstants.NAME));
            map.put(PGConstants.PAYU_MAP_PHONE, basicDetailMap.get(PGConstants.MOBILE));
            map.put(PGConstants.PAYU_MAP_PRODUCT_INFO, "Plan purchase");
            map.put(PGConstants.PAYU_MAP_EMAIL, basicDetailMap.get(PGConstants.EMAIL));
            map.put(PGConstants.PAYU_MAP_TXN_ID, purchaseDTO.getTransid());
            map.put(PGConstants.PAYU_MAP_SURL, properties.getProperty(basicDetailMap.get(PGConstants.SURL))); //Success Url
            map.put(PGConstants.PAYU_MAP_FURL, properties.getProperty(basicDetailMap.get(PGConstants.FURL))); // Failure Url
            map.put(PGConstants.PAYU_MAP_SALT, properties.getProperty(PGConstants.PG_CONFIG_PAYU_MERCHANT_SALT)); //Merchant Payu Salt
            map.put(PGConstants.PAYU_MAP_HEADER, properties.getProperty(PGConstants.PG_CONFIG_PAYU_MERCHANT_AUTH_HEADER)); //Merchant Payu Form Submit Header
//            map.put(PGConstants.PAYU_MAP_UDF1, null); // Extra  Param1
//            map.put(PGConstants.PAYU_MAP_UDF2, null); // Extra  Param2
//            map.put(PGConstants.PAYU_MAP_UDF3, null); // Extra  Param3
            map.put(PGConstants.PAYU_MAP_SUBURL, properties.getProperty(PGConstants.PG_CONFIG_PAYU_MERCHANT_SUBMIT_URL)); //Submit URL
            map.put(PGConstants.PAYU_MAP_HASH, PaymentUtil.payUTransformPayment(map));
            orderResponseModel.setPgDetails(map);
            orderResponseModel.setAmount(purchaseDTO.getAmount());
            orderResponseModel.setUniqueId(purchaseDTO.getTransid());
            orderResponseModel.setSubmitUrl(properties.getProperty(PGConstants.PG_CONFIG_PAYU_MERCHANT_SUBMIT_URL));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return orderResponseModel;
    }

    @Override
    public PGResponseModel generatePGResponse(Map<String, Object> response) {
        return new PGResponseModel
                (response.get(PGConstants.PAYU_RESPONSE_STATUS).toString()
                        , response.get(PGConstants.PAYU_RESPONSE_MIHPAYID).toString()
                        , Double.parseDouble(response.get(PGConstants.PAYU_RESPONSE_AMOUNT)
                        .toString())
                        , response.get(PGConstants.PAYU_RESPONSE_TXNID).toString());
    }
}
