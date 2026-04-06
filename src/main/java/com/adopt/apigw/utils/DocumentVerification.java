package com.adopt.apigw.utils;

import com.adopt.apigw.constants.DocumentConstants;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.pojo.DocumentDto;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.sf.ehcache.search.parser.CustomParseException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class DocumentVerification {

    @Autowired
    private CustomerDocDetailsService customerDocDetailsService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public HashMap authenticateAndVerifyDoc(DocumentDto documentDto) {
        HashMap<String, Object> resp = new HashMap<>();
        String msg = DocumentConstants.VERIFICATION_FAILED;
        try {
            okhttp3.MediaType JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();

            String sandboxKey = clientServiceSrv.getByName("document_verify_sandbox_key").getValue();
            String sandboxSecret = clientServiceSrv.getByName("document_verify_sandbox_secret").getValue();

            Request request = new Request.Builder()
                    .url("https://api.sandbox.co.in/authenticate")
                    .post(okhttp3.RequestBody.create(JSON, ""))
                    .addHeader("Accept", "application/json")
                    .addHeader("x-api-version", "1.0")
                    .addHeader("x-api-secret", sandboxSecret)
                    .addHeader("x-api-key", sandboxKey)
                    .build();
            Response response = client.newCall(request).execute();

            JsonObject jobj = new Gson().fromJson(response.body().string(), JsonObject.class);
            if (jobj.has("access_token")) {
                String token = jobj.get("access_token").getAsString();

                if (documentDto.getDocumentType().equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || documentDto.getDocumentType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD))
                    return verifyAadhar(documentDto.getDocId(), documentDto.getDocumentNumber(), token, sandboxKey);
                else if (documentDto.getDocumentType().equalsIgnoreCase(DocumentConstants.PAN_CARD))
                    return verifyPan(documentDto.getDocId(), documentDto.getDocumentNumber(), token, sandboxKey);
                else if (documentDto.getDocumentType().equalsIgnoreCase(DocumentConstants.GST_NUMBER))
                    return verifyGst(documentDto.getDocId(), documentDto.getDocumentNumber(), token, sandboxKey);
            }
            else if(jobj.has("message")){
                resp.put(DocumentConstants.MESSAGE, jobj.get("message").getAsString());
                resp.put(DocumentConstants.STATUS_CODE, jobj.get(DocumentConstants.STATUS_CODE));
                return resp;
            }
            resp.put(DocumentConstants.MESSAGE, "Please enter valid document type");
            resp.put(DocumentConstants.STATUS_CODE, HttpStatus.NOT_FOUND.value());
            return resp;
        } catch (CustomValidationException ce){

            throw new CustomValidationException(401,DocumentConstants.SUBSCRIPTION_HAS_EXPIRED,null);
        }
        catch (Exception e) {
            throw new RuntimeException(DocumentConstants.SUBSCRIPTION_HAS_EXPIRED);
        }
    }

    private HashMap verifyAadhar(Long docId, String aadharNumber, String token, String key) {
        HashMap<String, Object> resp = new HashMap<>();
        String msg = DocumentConstants.VERIFICATION_FAILED;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"aadhaar_number\":\"" + aadharNumber + "\"}");
            Request request = new Request.Builder()
                    .url("https://api.sandbox.co.in/aadhaar/verify")
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", token)
                    .addHeader("x-api-key", key)
                    .addHeader("x-api-version", "1.0")
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            JsonObject jobj = new Gson().fromJson(response.body().string(), JsonObject.class);
            Integer statusCode = jobj.has(DocumentConstants.STATUS_CODE) ? jobj.get(DocumentConstants.STATUS_CODE).getAsInt() : 404;
            if (statusCode == HttpStatus.OK.value()) {
                if (jobj.has(DocumentConstants.DATA) && jobj.get(DocumentConstants.DATA).getAsJsonObject().has("aadhaar_exists") && jobj.get(DocumentConstants.DATA).getAsJsonObject().get("aadhaar_exists").getAsBoolean()) {
                    msg = DocumentConstants.VERIFIED_SUCCESSFULLY;
                    customerDocDetailsService.approveCustDoc(docId, DocumentConstants.VERIFIED);
                } else {
                    statusCode = HttpStatus.UNPROCESSABLE_ENTITY.value();
                    msg = DocumentConstants.INVALID_AADHAAR_NUMBER;
                }
            } else if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value())
                msg = jobj.has(DocumentConstants.DATA) ? jobj.get(DocumentConstants.DATA).getAsString() : DocumentConstants.INVALID_AADHAAR_NUMBER;
            else
                msg = jobj.has(DocumentConstants.MESSAGE) ? jobj.get(DocumentConstants.MESSAGE).getAsString() : DocumentConstants.VERIFICATION_FAILED;
            resp.put(DocumentConstants.MESSAGE, msg);
            resp.put(DocumentConstants.STATUS_CODE, statusCode);
            return resp;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private HashMap verifyPan(Long docId, String panNumber, String token, String key) {
        HashMap<String, Object> resp = new HashMap<>();
        String msg = DocumentConstants.VERIFICATION_FAILED;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.sandbox.co.in/pans/" + panNumber + "/verify?consent=y&reason=For_KYC_of_User_By_Adopt_NetTech")
                    .get()
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", token)
                    .addHeader("x-api-key", key)
                    .addHeader("x-api-version", "1.0")
                    .build();

            Response response = client.newCall(request).execute();
            JsonObject jobj = new Gson().fromJson(response.body().string(), JsonObject.class);
            Integer statusCode = jobj.has(DocumentConstants.STATUS_CODE) ? jobj.get(DocumentConstants.STATUS_CODE).getAsInt() : 404;
            if (statusCode == HttpStatus.OK.value()) {
                if (jobj.has(DocumentConstants.DATA) && jobj.get(DocumentConstants.DATA).getAsJsonObject().has("status") && jobj.get(DocumentConstants.DATA).getAsJsonObject().get("status").getAsString().equalsIgnoreCase("Valid")) {
                    customerDocDetailsService.approveCustDoc(docId, DocumentConstants.VERIFIED);
                    msg = DocumentConstants.VERIFIED_SUCCESSFULLY;
                } else {
                    statusCode = HttpStatus.UNPROCESSABLE_ENTITY.value();
                    msg = DocumentConstants.INVALID_AADHAAR_NUMBER;
                }
            } else
                msg = jobj.has(DocumentConstants.MESSAGE) ? jobj.get(DocumentConstants.MESSAGE).getAsString() : DocumentConstants.VERIFICATION_FAILED;
            resp.put(DocumentConstants.MESSAGE, msg);
            resp.put(DocumentConstants.STATUS_CODE, statusCode);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private HashMap verifyGst(Long docId, String gstNumber, String token, String key) {
        HashMap<String, Object> resp = new HashMap<>();
        String msg = DocumentConstants.VERIFICATION_FAILED;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.sandbox.co.in/gsp/public/gstin/" + gstNumber)
                    .get()
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", token)
                    .addHeader("x-api-key", key)
                    .addHeader("x-api-version", "1.0")
                    .build();

            Response response = client.newCall(request).execute();
            JsonObject jobj = new Gson().fromJson(response.body().string(), JsonObject.class);
            Integer statusCode = jobj.get(DocumentConstants.STATUS_CODE).getAsInt();
            if (statusCode == HttpStatus.OK.value()) {
                if (jobj.has(DocumentConstants.DATA) && jobj.get(DocumentConstants.DATA).getAsJsonObject().has("sts") && jobj.get(DocumentConstants.DATA).getAsJsonObject().get("sts").getAsString().equalsIgnoreCase("Active")) {
                    msg = DocumentConstants.VERIFIED_SUCCESSFULLY;
                    customerDocDetailsService.approveCustDoc(docId, DocumentConstants.VERIFIED);
                } else {
                    statusCode = HttpStatus.UNPROCESSABLE_ENTITY.value();
                    msg = DocumentConstants.INVALID_GST_NUMBER;
                }
            } else
                msg = jobj.has(DocumentConstants.MESSAGE) ? jobj.get(DocumentConstants.MESSAGE).getAsString() : DocumentConstants.VERIFICATION_FAILED;
            resp.put(DocumentConstants.MESSAGE, msg);
            resp.put(DocumentConstants.STATUS_CODE, statusCode);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}

