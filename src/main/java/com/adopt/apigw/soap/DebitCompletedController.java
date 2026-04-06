package com.adopt.apigw.soap;

import com.adopt.apigw.modules.MtnPayment.service.MtnPaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/debitcompleted")
public class DebitCompletedController {

    @Autowired
    private MtnPaymentService mtnPaymentService;

    @PostMapping(consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> handleCustomDebitCompletedRequest(@RequestBody String requestBody) throws Exception {
        String responseXml = "";
        JSONObject json = XML.toJSONObject(requestBody);
        String jsonString = json.toString(4);
        Map<String, LinkedHashMap<String , Object>> mapData = new ObjectMapper().readValue(jsonString, HashMap.class);
        LinkedHashMap<String, Object> mapping = mapData.get("ns0:debitcompletedrequest");
        String response =  mtnPaymentService.debitcompleted(mapping.get("externaltransactionid").toString() , mapping.get("transactionid").toString() , mapping.get("status").toString());
        if (response.equalsIgnoreCase("Success")) {
                   System.out.println("Successfully payment response came and plan is bind");
                    responseXml = "<ns0:debitcompletedresponse xmlns:ns0=\"http://www.ericsson.com/em/emm/callback/v1_2\"/>";
                }
        else if (response.equalsIgnoreCase("customernotfound")) {
                   System.out.println("Transaction is not found in Given Request Transaction Id and Ecternal Transaction Id");
                    responseXml = "<ns0:debitcompletedresponse xmlns:ns0=\"http://www.ericsson.com/em/emm/callback/v1_2\"/>" +
                            "<Status>452</Status>" +
                            "<Message>TRANSACTION NOT FOUND GIVEN TRANSACTION AND EXTERNAL TRANSACTION ID</Message>"+
                            "<ns0:debitcompletedresponse>";
                 }
        else if (response.equalsIgnoreCase("IntiatePayment")) {
            System.out.println("No pending payment found");
            responseXml = "<ns0:debitcompletedresponse xmlns:ns0=\"http://www.ericsson.com/em/emm/callback/v1_2\"/>" +
                    "<Status>453</Status>" +
                    "<Message>NO PENDING PAYMENT FOUND</Message>"+
                    "<ns0:debitcompletedresponse>";
        }
        else if (response.equalsIgnoreCase("paymentdonewithsuccess")) {
            System.out.println("Payment Already done Request Transaction Id and External Transaction Id");
            responseXml = "<ns0:debitcompletedresponse xmlns:ns0=\"http://www.ericsson.com/em/emm/callback/v1_2\"/>" +
                    "<Status>454</Status>" +
                    "<Message>PAYMENT ALREADY DONE WITH TRANSACTIONAL AND EXTERNALTRANSACTION ID</Message>"+
                    "<ns0:debitcompletedresponse>";
        }
        else{
            System.out.println("Error processing debit request");
            responseXml = "<ns0:debitcompletedresponse xmlns:ns0=\"http://www.ericsson.com/em/emm/callback/v1_2\"/>" +
                    "<Status>500</Status>" +
                    "<Message>ERROR PROCESSING DEBIT REQUEST</Message>"+
                    "<ns0:debitcompletedresponse>";
        }
        String finalResponse = processCustomDebitCompletedRequest(responseXml);

        return ResponseEntity.ok(finalResponse);
    }
    private String processCustomDebitCompletedRequest(String requestBody) {
        // Implement your logic to process the custom SOAP request and generate a response
        // For simplicity, let's return a hard-coded response
        String responseXml  = requestBody;
        return responseXml;
    }
}