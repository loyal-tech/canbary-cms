package com.adopt.apigw.modules.payments.util;

import org.springframework.stereotype.Component;

import com.adopt.apigw.constants.PGConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.placeOrder.model.OrderResponseModel;
import com.adopt.apigw.modules.purchaseDetails.model.PGResponseModel;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.pojo.api.PartnerPojo;
import com.adopt.apigw.utils.PropertyReaderUtil;
import com.adopt.apigw.utils.RSAUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class CCAvenueHelper implements PGHelper {

    public static final String MODULE = " [CCAvenueHelper()] ";

    @Override
    public OrderResponseModel generateFormData(CustomersPojo customersPojo, PurchaseDetailsDTO purchaseDetailsDTO, PartnerPojo partnerPojo) throws IOException {
        String SUBMODULE = MODULE + " [generateFormData()] ";
        OrderResponseModel orderResponseModel = new OrderResponseModel();
        Map<String, Object> map = new HashMap<>();
        try {
            StringBuffer vEncVal = new StringBuffer("");
            Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
            vEncVal.append(RSAUtility.addToPostParams(PGConstants.CCAVENUE_MAP_MERCHANT_ID_KEY,properties.getProperty(PGConstants.PG_CONFIG_CCAVENUE_MERCHANT_KEY)));
            vEncVal.append(RSAUtility.addToPostParams(PGConstants.CCAVENUE_MAP_ORDER_ID_KEY, purchaseDetailsDTO.getTransid()));
            vEncVal.append(RSAUtility.addToPostParams(PGConstants.CCAVENUE_MAP_CURRENCY_KEY, "INR"));
            vEncVal.append(RSAUtility.addToPostParams(PGConstants.CCAVENUE_MAP_AMOUNT_KEY, purchaseDetailsDTO.getAmount().toString()));
            vEncVal.append(RSAUtility.addToPostParams(PGConstants.CCAVENUE_MAP_RI_DIRECT_URL_KEY, properties.getProperty(PGConstants.PG_CONFIG_CCAVENUE_RI_DIRECT_URL)));
            vEncVal.append(RSAUtility.addToPostParams(PGConstants.CCAVENUE_MAP_CANCEL_URL_KEY, properties.getProperty(PGConstants.PG_CONFIG_CCAVENUE_CANCEL_URL)));
            vEncVal.append(RSAUtility.addToPostParams(PGConstants.CCAVENUE_MAP_LANGUAGE_KEY, "EN"));
            vEncVal.append(RSAUtility.addToPostParams(PGConstants.CCAVENUE_MAP_ACCESS_CODE_KEY, properties.getProperty(PGConstants.PG_CONFIG_CCAVENUE_ACCESS_CODE)));
            vEncVal.append(RSAUtility.addToPostParams(PGConstants.CCAVENUE_MAP_ENC_KEY, properties.getProperty(PGConstants.PG_CONFIG_CCAVENUE_ENC_KEY)));
            vEncVal.append(RSAUtility.addToPostParams(PGConstants.CCAVENUE_MAP_SUBMIT_URL_KEY, properties.getProperty(PGConstants.PG_CONFIG_CCAVENUE_SUBMIT_URL)));
            map.put(PGConstants.CCAVENUE_MAP_ACCESS_CODE_KEY, properties.getProperty(PGConstants.PG_CONFIG_CCAVENUE_ACCESS_CODE));
            map.put(PGConstants.CCAVENUE_MAP_ENC_REQUEST_KEY, RSAUtility.encrypt(properties.getProperty(PGConstants.PG_CONFIG_CCAVENUE_ENC_KEY), vEncVal.substring(0, vEncVal.length() - 1)));
            orderResponseModel.setPgDetails(map);
            orderResponseModel.setAmount(purchaseDetailsDTO.getAmount());
            orderResponseModel.setUniqueId(purchaseDetailsDTO.getTransid());
            orderResponseModel.setSubmitUrl(properties.getProperty(PGConstants.PG_CONFIG_CCAVENUE_SUBMIT_URL));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return orderResponseModel;
    }

    @Override
    public PGResponseModel generatePGResponse(Map<String, Object> response) {
        try {
            Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
            response = RSAUtility.decrypt(properties.getProperty(PGConstants.PG_CONFIG_CCAVENUE_ENC_KEY),response.get("encResp").toString());
            return new PGResponseModel
                    (response.get(PGConstants.CCAVENUE_RESPONSE_STATUS).toString()
                            , response.get(PGConstants.CCAVENUE_RESPONSE_TRACKING_ID).toString()
                            , Double.parseDouble(response.get(PGConstants.CCAVENUE_RESPONSE_AMOUNT).toString())
                            , response.get(PGConstants.CCAVENUE_RESPONSE_ORDER_ID).toString());
        }
        catch (Exception ex){
            return null;
        }
    }
}
