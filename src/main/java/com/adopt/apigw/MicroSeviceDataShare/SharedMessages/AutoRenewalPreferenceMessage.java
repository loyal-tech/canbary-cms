package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class AutoRenewalPreferenceMessage {
    private String message;
    private Long buId;
    private Map<String,Object> customerData = new HashMap<>();

    // Constructor
    public AutoRenewalPreferenceMessage(String message, String username, Integer customerId, Integer mvnoId, String email,
                                        Long buId, Boolean renewalPreference, String phone) {
        this.message = message;
        this.buId = buId;

        customerData.put("userName",username);
        customerData.put("customerId",customerId);
        customerData.put("mvnoId",mvnoId);
        customerData.put("emailId",email);
        customerData.put("mobileNumber",phone);
        customerData.put("Auto-RenewalPreference",renewalPreference);
        customerData.put("isCustomer",true);


    }

}
