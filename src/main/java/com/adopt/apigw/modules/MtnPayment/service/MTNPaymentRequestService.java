package com.adopt.apigw.modules.MtnPayment.service;

import com.adopt.apigw.devCode.ToolService;
import com.adopt.apigw.modules.MtnPayment.model.URL;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class MTNPaymentRequestService {

    @Autowired
    private MtnPaymentService mtnPaymentService;

    @Autowired
    private ToolService toolService;







    public String requestToMTN(String amount , String transactionId , String currency){
        StringBuilder response = new StringBuilder();
        Integer exitCode = -1;
        try {
            String url = "curl --tlsv1.2 -ivk  -H 'Content-Type: application/xml'  -v -s -k -u wifi.admin:Wifiadmin@12345 -H --cacert /home/adoptminss/mTLS_Certs/certificate_chain2.cer --cert /home/adoptminss/mTLS_Certs/mtnsshotspot.cer  --key /home/adoptminss/mTLS_Certs/adopt_server.key -H 'X-lwac-execute-as: ID:490001/ID' -d";

            String soapRequest = "'<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:debitrequest xmlns:ns2=\"http://www.ericsson.com/em/emm/financial/v1_1\"><fromfri>FRI:211926212088/MSISDN</fromfri><tofri>FRI:490001/ID</tofri><amount><amount>"+amount+"</amount><currency>"+currency+"</currency></amount><externaltransactionid>"+transactionId+"</externaltransactionid></ns2:debitrequest>' -X POST "+mtnPaymentService.getURL();
            String curlCommand = url.concat(soapRequest);
            System.out.println(curlCommand);
            ProcessBuilder processBuilder = new ProcessBuilder(curlCommand.split("\\s+"));

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
             response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                response.append(line);
            }
             exitCode = process.waitFor();

            // Print the response

            System.out.println(processBuilder);
            System.out.println("Response: " + response.toString());

            // Print the exit code
            System.out.println("Exit Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();


        }
        return response.toString();
    }

    public String requestToGroovy(String amount , String transactionId , String currency , String debitfri){

        JSONObject json = new JSONObject();
        File tempFile = new File(mtnPaymentService.getGroovyPath() + "/request.groovy");
        System.out.println("FILE PATH: "+mtnPaymentService.getGroovyPath() + "/request.groovy");
        if (tempFile.exists()) {
            json = toolService.runWithGroovyClassLoader("requestToMTN",
                    mtnPaymentService.getGroovyPath() + "/request.groovy", amount, transactionId, currency,debitfri);

            if (json != null) {
                return json.toString();
            }
        }
        else{
            System.out.println("FILE PATH: "+mtnPaymentService.getGroovyPath() + "/request.groovy");
            throw new RuntimeException("File not found in given path");
        }
        return "";
    }

    public String requestToGroovyForBalance(String fri){
        JSONObject json = new JSONObject();
        File tempFile = new File(mtnPaymentService.getGroovyPath() + "/request.groovy");
        System.out.println("FILE PATH: "+mtnPaymentService.getGroovyPath() + "/request.groovy");
        if (tempFile.exists()) {
            json = toolService.runWithGroovyClassLoader("requestToGetBalance",
                    mtnPaymentService.getGroovyPath() + "/request.groovy", fri, "", "","");

            if (json != null) {
                return json.toString();
            }
        }
        else{
            System.out.println("FILE PATH: "+mtnPaymentService.getGroovyPath() + "/request.groovy");
            throw new RuntimeException("File not found in given path");
        }
        return "";
    }

    public String requestToGetMTNUSSDPAY(String mobileNumber , String offeringId , String transactionId){
        File tempFile = new File(mtnPaymentService.getGroovyPath() + "/request.groovy");
        System.out.println("FILE PATH: "+mtnPaymentService.getGroovyPath() + "/request.groovy");
        if (tempFile.exists()) {
            String s = toolService.runWithGroovyClassLoaderWithString("requestToMTNUSSDPAY",
                    mtnPaymentService.getGroovyPath() + "/request.groovy",mobileNumber , offeringId,transactionId,"");
            if (s != null) {
                return s;
            }
        }
        else{
            System.out.println("FILE PATH: "+mtnPaymentService.getGroovyPath() + "/request.groovy");
            throw new RuntimeException("File not found in given path");
        }
        return null;
    }


}
