package com.adopt.apigw.modules.integrations.NexgeVoice.service;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.mapper.ClientServiceMapper;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.integrations.NexgeVoice.constants.NexgeVoiceProvisionConstants;
import com.adopt.apigw.modules.integrations.NexgeVoice.model.*;
import com.adopt.apigw.modules.subscriber.model.CustomerPlansModel;
import com.adopt.apigw.modules.subscriber.model.CustomerVoiceDetailsDTO;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class NexgeVoiceProvisionService {

    @Autowired
    private ClientServiceRepository clientServiceRepository;
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private PostpaidPlanService planService;
    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    private ClientServiceMapper clientServiceMapper;

    public static final String MODULE = " [VoiceProvisionService] ";

    private String performAuthProcess(String apiKey, String authUserId) throws Exception {

        String returnStr;
        String authKeyStr = "";
        String authIpStr = "";


        //Get Auth API
        ClientService authKey = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_AUTH_RESPONSE_KEY).get(0), new CycleAvoidingMappingContext());
        //Get Auth IP
        ClientService authIp = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_AUTH_RESPONSE_IP_KEY).get(0), new CycleAvoidingMappingContext());
        //Get L1 Password
        ClientService l1password = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_L_ONE_PASSWORD).get(0), new CycleAvoidingMappingContext());
        //Get L2 Password
        ClientService l2password = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_L_TWO_PASSWORD).get(0), new CycleAvoidingMappingContext());


        //If Auth Key or Auth IP are null or expired
        // Perform Auth API call and get those parameters
        String authRequestUrl = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_SERVER_KEY).get(0), new CycleAvoidingMappingContext())
                .getValue().toString()
                + NexgeVoiceProvisionConstants.NEXG_BASE_URL
                + NexgeVoiceProvisionConstants.NEXG_AUTH_URL;

        ApplicationLogger.logger.info("Sending Auth request on: " + authRequestUrl);

        OkHttpClient authReqClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();

        String bodyStr = "{\"" +
                NexgeVoiceProvisionConstants.NEXG_JSON_AUTH_USERID_KEY +
                "\":\"" +
                authUserId +
                "\"}";

        RequestBody authRequestBody = RequestBody.create(MediaType.parse("application/json"), bodyStr);

        Request authRequest = new Request.Builder()
                .url(authRequestUrl)
                .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_X_API_KEY, apiKey.toString())
                .post(authRequestBody)
                .build();
        ApplicationLogger.logger.info("Auth Req: " + authRequest.toString());
        Response authResponse = authReqClient.newCall(authRequest).execute();
//            Response authResponse = new Response.Builder().build();

        if (authResponse != null) {
            String responseBody = authResponse.body().string();

            ApplicationLogger.logger.info("performAuthProcess() response:" + responseBody);

            HashMap<String, Object> responseMap = (HashMap<String, Object>) UtilsCommon.convertJsonToHashMap(responseBody);
            authKeyStr = String.valueOf(responseMap.get(NexgeVoiceProvisionConstants.NEXG_JSON_RESP_API_KEY));
            authIpStr = String.valueOf(responseMap.get(NexgeVoiceProvisionConstants.NEXG_JSON_RESP_IP_KEY));

            //Save them in DB
            ClientService authKeySaveObj;
            if (null == authKey)
                authKeySaveObj = new ClientService();
            else
                authKeySaveObj = authKey;
            authKeySaveObj.setName(NexgeVoiceProvisionConstants.NEXG_AUTH_RESPONSE_KEY);
            authKeySaveObj.setValue(authKeyStr);
            clientServiceRepository.save(authKeySaveObj);

            ClientService authIpSaveObj;
            if (null == authIp)
                authIpSaveObj = new ClientService();
            else
                authIpSaveObj = authIp;
            authIpSaveObj.setName(NexgeVoiceProvisionConstants.NEXG_AUTH_RESPONSE_IP_KEY);
            authIpSaveObj.setValue(authIpStr);
            clientServiceRepository.save(authIpSaveObj);

        } else {
            ApplicationLogger.logger.info("performAuthProcess() response is null");
        }


        String authPassword = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_AUTH_USER_PASSWORD_KEY)
                .get(0), new CycleAvoidingMappingContext()).getValue();
        // SHA
        String levelOnePlainText = authUserId + ":" + authIpStr + ":" + authPassword;

        ApplicationLogger.logger.info("Level 1 Plain Text: " + levelOnePlainText);

        String levelOneSha256 = Hashing.sha256().hashString(levelOnePlainText, StandardCharsets.UTF_8).toString();

        ApplicationLogger.logger.info("Level 1 Hash: " + levelOneSha256);

        String levelTwoPlainText = levelOneSha256 + ":" + authKeyStr;

        ApplicationLogger.logger.info("Level 2 Plain Text: " + levelTwoPlainText);

        String levelTwoSha256 = Hashing.sha256().hashString(levelTwoPlainText, StandardCharsets.UTF_8).toString();

        ApplicationLogger.logger.info("Level 2 Hash : " + levelTwoSha256);
        returnStr = levelTwoSha256;

        // Save Passwords in DB
        ClientService levelOneSaveObj;
        if (null == l1password)
            levelOneSaveObj = new ClientService();
        else
            levelOneSaveObj = l1password;
        levelOneSaveObj.setName(NexgeVoiceProvisionConstants.NEXG_L_ONE_PASSWORD);
        levelOneSaveObj.setValue(levelOneSha256);
        clientServiceRepository.save(levelOneSaveObj);

        ClientService levelTwoSaveObj;
        if (null == l2password)
            levelTwoSaveObj = new ClientService();
        else
            levelTwoSaveObj = l2password;
        levelTwoSaveObj.setName(NexgeVoiceProvisionConstants.NEXG_L_TWO_PASSWORD);
        levelTwoSaveObj.setValue(levelTwoSha256);
        clientServiceRepository.save(levelTwoSaveObj);

        return returnStr;
    }

    private String performLogin(String apiKey, Boolean tokenExpired) throws Exception {

        String retStr = "";
        //Get password from DB
        String password = "";
        ClientService authUserId = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_AUTH_USER_ID_KEY)
                .get(0), new CycleAvoidingMappingContext());

        if (null == authUserId) {
            throw new Exception("Auth User ID is NULL");
        }
        ClientService passwordObj = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_L_TWO_PASSWORD)
                .get(0), new CycleAvoidingMappingContext());


        //If password NULL or expired
        //Perform Auth
        if (null == passwordObj || tokenExpired) {
            password = performAuthProcess(apiKey, authUserId.getValue());
        } else {
            password = passwordObj.getValue();
        }

        //perform login
        String loginUrl = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_SERVER_KEY)
                .get(0), new CycleAvoidingMappingContext()).getValue()
                + NexgeVoiceProvisionConstants.NEXG_BASE_URL
                + NexgeVoiceProvisionConstants.NEXG_LOGIN_URL;

        ApplicationLogger.logger.info("Sending Login request on: " + loginUrl);


        OkHttpClient loginClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();

        StringBuilder bodyBuilder = new StringBuilder();
        String bodyStr = bodyBuilder.append("{\"")
                .append(NexgeVoiceProvisionConstants.NEXG_JSON_AUTH_USERID_KEY)
                .append("\":\"")
                .append(authUserId.getValue())
                .append("\",\"")
                .append(NexgeVoiceProvisionConstants.NEXG_JSON_AUTH_PASSWORD_KEY)
                .append("\":\"")
                .append(password)
                .append("\"}").toString();

        RequestBody loginRequestBody = RequestBody.create(MediaType.parse("application/json"), bodyStr);

        Request loginRequest = new Request.Builder()
                .url(loginUrl)
                .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_X_API_KEY, apiKey)
                .post(loginRequestBody)
                .build();

        ApplicationLogger.logger.info("Login Req: " + loginRequest.toString());
        Response loginResponse = loginClient.newCall(loginRequest).execute();

        if (loginResponse != null) {
            String responseBody = loginResponse.body().string();

            ApplicationLogger.logger.info("performLogin() response:" + responseBody);

            HashMap<String, Object> responseMap = (HashMap<String, Object>) UtilsCommon.convertJsonToHashMap(responseBody);
            String tokenStr = String.valueOf(responseMap.get(NexgeVoiceProvisionConstants.NEXG_JSON_LOGIN_RESP_TOKEN_KEY));
            String expiryStr = String.valueOf(responseMap.get(NexgeVoiceProvisionConstants.NEXG_JSON_LOGIN_RESP_EXPIRY_KEY));
            retStr = tokenStr;

            //Save JWT in DB
            ClientService jwtSaveObj;
            if (tokenExpired) {
                ClientService jwt = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_LOGIN_TOKEN)
                        .get(0), new CycleAvoidingMappingContext());
                jwtSaveObj = jwt;
            } else
                jwtSaveObj = new ClientService();
            jwtSaveObj.setName(NexgeVoiceProvisionConstants.NEXG_LOGIN_TOKEN);
            jwtSaveObj.setValue(tokenStr);
            clientServiceRepository.save(jwtSaveObj);


            ClientService jwtExpirySaveObj;
            if (tokenExpired) {
                ClientService jwt = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_LOGIN_TOKEN_EXPIRY)
                        .get(0), new CycleAvoidingMappingContext());
                jwtExpirySaveObj = jwt;
            } else
                jwtExpirySaveObj = new ClientService();
            jwtExpirySaveObj.setName(NexgeVoiceProvisionConstants.NEXG_LOGIN_TOKEN_EXPIRY);
            jwtExpirySaveObj.setValue(expiryStr);
            clientServiceRepository.save(jwtExpirySaveObj);


        } else {
            ApplicationLogger.logger.info("performLogin() response is null");
        }

        //return JWT
        return retStr;
    }

    private void performTokenRefresh() {

    }

    private String getLoginToken(String apiKey) throws Exception {

        String retStr = "";
        // Get token from DB
        ClientService token = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_LOGIN_TOKEN)
                .get(0), new CycleAvoidingMappingContext());
        ClientService tokenExpiry = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_LOGIN_TOKEN_EXPIRY)
                .get(0), new CycleAvoidingMappingContext());

        // IF null perform Login
        if (null == token) {
            retStr = performLogin(apiKey, false);
        } else if (tokenExpiry != null
                && LocalDateTime.now().isAfter(
                LocalDateTime.parse(
                        tokenExpiry.getValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")))) {
            retStr = performLogin(apiKey, true);
        } else if (tokenExpiry != null
                && LocalDateTime.now().plusDays(1).isAfter(
                LocalDateTime.parse(
                        tokenExpiry.getValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")))) {
            performTokenRefresh();
        } else {
            retStr = token.getValue();
        }


        //return token
        retStr = CommonConstants.AUTHORIZATION_TOKEN_PREFIX + " " + retStr;
        return retStr;
    }

    public NexgeVoiceResponseDTO setNexgeProvisionRequest(Customers customers, String param) throws Exception {
        NexgeVoiceResponseDTO dto = new NexgeVoiceResponseDTO();
        dto.setId(NexgeVoiceProvisionConstants.NEXG_BSS + customers.getId().toString());
        dto.setAuthUserId(dto.getId());
        dto.setWebDomain(null);
        dto.setWebAuthPassword(null);
        dto.setParent(clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_PARENT_NAME_KEY)
                .get(0), new CycleAvoidingMappingContext()).getValue());
        dto.setStatus(NexgeVoiceProvisionConstants.NEXG_SUB_STATUS_ACTIVE);
        dto.setFirstName(customers.getFirstname());
        dto.setMiddleName(null);
        dto.setLastName(customers.getLastname());
        dto.setEmailId(customers.getEmail());

        for (int i = 0; i < customers.getAddressList().size(); i++) {
            if (customers.getAddressList().get(i).getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)) {
                dto.setCountry(customers.getAddressList().get(i).getCountry().getName());
                dto.setState(customers.getAddressList().get(i).getState().getName());
                dto.setCity(customers.getAddressList().get(i).getCity().getName());
                dto.setPincode(customers.getAddressList().get(i).getPincode().getPincode());
                dto.setLocalArea(customers.getAddressList().get(i).getLandmark());
            }
        }
        List<VoiceAccountDTO> voiceAccountDTOList = new ArrayList<>();
        VoiceAccountDTO voiceAccountDTO = new VoiceAccountDTO();
        voiceAccountDTO.setId(customers.getAcctno());

        voiceAccountDTO.setCreditLimit(0.0);
        if (customers.getVoicesrvtype().equalsIgnoreCase(SubscriberConstants.PHONE_LINE)) {
            List<DIDProfile> proDTO = new ArrayList<>();
            DIDProfile didProfile = new DIDProfile();
            didProfile.setDIDNumber(customers.getDidno());
            proDTO.add(didProfile);
            voiceAccountDTO.setAccountType(NexgeVoiceProvisionConstants.NEXG_SUB_ACC_PHONELINE);
            voiceAccountDTO.setBillType(NexgeVoiceProvisionConstants.NEXG_PLAN_TYPE_POSTPAID);

            voiceAccountDTO.setServicePlanId(param);
            voiceAccountDTO.setDIDProfile(proDTO);
        }
        if (customers.getVoicesrvtype().equalsIgnoreCase(SubscriberConstants.SHIP_TRUNK)) {
            List<DIDProfile> proDTO = new ArrayList<>();
            DIDProfile didProfile = new DIDProfile();

            NumberBlockSIPDTO numberBlockSIPDTO = new NumberBlockSIPDTO();
            numberBlockSIPDTO.setCustomerPilotNumber(customers.getDidno());

            List<String> DIDNumbers = Arrays.stream(customers.getChilddidno().split(",")).collect(Collectors.toList());


            numberBlockSIPDTO.setDIDNumbers(DIDNumbers);
            didProfile.setNumberBlock(numberBlockSIPDTO);
            proDTO.add(didProfile);
            voiceAccountDTO.setAccountType(NexgeVoiceProvisionConstants.NEXG_SUB_ACC_SIPTRUNK);
            voiceAccountDTO.setBillType(NexgeVoiceProvisionConstants.NEXG_PLAN_TYPE_PREPAID);
            voiceAccountDTO.setServicePlanId(param);
            voiceAccountDTO.setDIDProfile(proDTO);
        }
        if (customers.getVoicesrvtype().equalsIgnoreCase(SubscriberConstants.INTERCOM)) {
            voiceAccountDTO.setAccountType(NexgeVoiceProvisionConstants.NEXG_SUB_ACC_INTERCOM);
            voiceAccountDTO.setIntercomGroup(customers.getIntercomgrp());
            voiceAccountDTO.setIntercomNumber(customers.getIntercomno());
        }
        voiceAccountDTOList.add(voiceAccountDTO);
        dto.setAccounts(voiceAccountDTOList);
        dto.setEmailId(customers.getEmail());
        dto.setAlternatePhoneNumber(customers.getAltmobile());

        ApplicationLogger.logger.info("Customer Provision JSON : " + dto);
        return dto;
    }

    public Boolean performCustomerProvision(Customers customers) {
        String SUBMODULE = MODULE + " [performCustomerProvision] ";
        Boolean result = false;
        String planId = null;
        try {
            if (null != customers.getPlanMappingList() && 0 < customers.getPlanMappingList().size()) {
                List<CustomerPlansModel> activePLanList = subscriberService.getActivePlanList(customers.getId(), false);
                if (null != activePLanList && 0 < activePLanList.size()) {
                    List<CustomerPlansModel> dataPlanList = activePLanList.stream().filter(data -> null != data.getService() && data.getService()
                            .equalsIgnoreCase(SubscriberConstants.SERVICE_DATA) && null != data.getPlanId()).collect(Collectors.toList());
                    if (null != dataPlanList && 0 < dataPlanList.size()) {
                        for (CustomerPlansModel plansModel : dataPlanList) {
                            PostpaidPlan plan = planService.get(plansModel.getPlanId(),customers.getMvnoId());
                            if (null != plan && null != plan.getParam1()) {
                                planId = plan.getParam1();
                                break;
                            }
                        }
                    }
                }
            }
            if (planId == null) {
                ApplicationLogger.logger.info(SUBMODULE + " PlanId Not Found! ");
                return false;
            }

            NexgeVoiceResponseDTO payload = setNexgeProvisionRequest(customers, planId);
            ClientService apiKey = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_AUTH_API_KEY)
                    .get(0), new CycleAvoidingMappingContext());

            if (null == apiKey) {
                throw new Exception("API KEY not found");
            }
            //Get JWT Token
            String jwtToken = getLoginToken(apiKey.getValue());

            // Set data
            payload.setWebDomain(clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_AUTH_RESPONSE_IP_KEY)
                    .get(0), new CycleAvoidingMappingContext()).getValue());
            payload.setWebAuthPassword(clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_L_ONE_PASSWORD)
                    .get(0), new CycleAvoidingMappingContext()).getValue());

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String payloadStr = mapper.writeValueAsString(payload);

            //Perform Provision
            String saveRequestUrl = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_SERVER_KEY)
                    .get(0), new CycleAvoidingMappingContext()).getValue()
                    + NexgeVoiceProvisionConstants.NEXG_BASE_URL
                    + NexgeVoiceProvisionConstants.NEXG_SAVE_CUSTOMER_URL;

            OkHttpClient saveClient = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();

            ApplicationLogger.logger.info("Payload : " + payloadStr);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), payloadStr);

            Request saveRequest = new Request.Builder()
                    .url(saveRequestUrl)
                    .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_X_API_KEY, apiKey.getValue())
                    .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_AUTHORIZATION, jwtToken)
                    .post(body)
                    .build();

            ApplicationLogger.logger.info("Save Req: " + saveRequest);
            Response saveResponse = saveClient.newCall(saveRequest).execute();
//            Response loginResponse = new Response.Builder().build();

            if (saveResponse != null) {
                String responseBody = saveResponse.body().string();

                ApplicationLogger.logger.info("performCustomerProvision() response:" + responseBody);
                if (saveResponse.code() == HttpStatus.CREATED.value()) {
                    ApplicationLogger.logger.info("Provision successful");
                    result = true;
                } else {
                    ApplicationLogger.logger.info("Error in provisioning customer");
                }
            } else {
                ApplicationLogger.logger.info("performCustomerProvision() response is null");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private Boolean removeCustDidNumbers(Integer custId, String acctId, String didNos) {
        Boolean result = false;
        try {
            ClientService apiKey = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_AUTH_API_KEY)
                    .get(0), new CycleAvoidingMappingContext());

            if (null == apiKey) {
                throw new Exception("API KEY not found");
            }
            //Get JWT Token
            String jwtToken = getLoginToken(apiKey.getValue());

            StringBuilder DIDNumbers = new StringBuilder();
            for (String str : didNos.split(",")) {
                DIDNumbers.append("\"").append(str).append("\",");
            }
            DIDNumbers.deleteCharAt(DIDNumbers.length() - 1);
            StringBuilder payloadStr = new StringBuilder();
            payloadStr.append("{\"DIDNumbers\" : [").append(DIDNumbers.toString()).append("]}");

            ApplicationLogger.logger.info("Payload : " + payloadStr.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), payloadStr.toString());

            //Perform Deletion
            String removeAccountUrl = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_SERVER_KEY)
                    .get(0), new CycleAvoidingMappingContext()).getValue()
                    + NexgeVoiceProvisionConstants.NEXG_BASE_URL
                    + NexgeVoiceProvisionConstants.NEXG_SAVE_CUSTOMER_URL
                    + "/" + NexgeVoiceProvisionConstants.NEXG_BSS + custId.toString()
                    + NexgeVoiceProvisionConstants.NEXG_ACCOUNT_URL
                    + "/" + acctId
                    + NexgeVoiceProvisionConstants.NEXG_DID_URL
                    + NexgeVoiceProvisionConstants.NEXG_DELETE_URL;


            OkHttpClient removeAccountClient = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();


            Request removeRequest = new Request.Builder()
                    .url(removeAccountUrl)
                    .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_X_API_KEY, apiKey.getValue())
                    .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_AUTHORIZATION, jwtToken)
                    .delete(body)
                    .build();

            ApplicationLogger.logger.info("Remove Req: " + removeRequest);
            Response removeResponse = removeAccountClient.newCall(removeRequest).execute();

            if (removeResponse != null) {
                String responseBody = removeResponse.body().string();

                ApplicationLogger.logger.info("removeCustDidNumbers() response:" + responseBody);
                if (removeResponse.code() == HttpStatus.OK.value()) {
                    ApplicationLogger.logger.info("Deletion successful: " + removeResponse.code());
                    result = true;
                } else {
                    ApplicationLogger.logger.info("Error in deleting DID" + removeResponse.code());
                }
            } else {
                ApplicationLogger.logger.info("removeCustDidNumbers() response is null");
            }
//
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private Boolean removeCustomer(Integer customerId) {
        Boolean result = false;
        try {
            ClientService apiKey = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_AUTH_API_KEY)
                    .get(0), new CycleAvoidingMappingContext());

            if (null == apiKey) {
                throw new Exception("API KEY not found");
            }
            //Get JWT Token
            String jwtToken = getLoginToken(apiKey.getValue());

            //Perform Provision
            String removeCustomerUrl = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_SERVER_KEY)
                    .get(0), new CycleAvoidingMappingContext()).getValue()
                    + NexgeVoiceProvisionConstants.NEXG_BASE_URL
                    + NexgeVoiceProvisionConstants.NEXG_SAVE_CUSTOMER_URL
                    + "/" + NexgeVoiceProvisionConstants.NEXG_BSS + customerId.toString();

            OkHttpClient removeClient = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();


            Request removeRequest = new Request.Builder()
                    .url(removeCustomerUrl)
                    .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_X_API_KEY, apiKey.getValue())
                    .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_AUTHORIZATION, jwtToken)
                    .delete()
                    .build();

            ApplicationLogger.logger.info("Remove Req: " + removeRequest);
            Response removeResponse = removeClient.newCall(removeRequest).execute();

            if (removeResponse != null) {
                String responseBody = removeResponse.body().string();
//
                ApplicationLogger.logger.info("removeCustomer() response:" + responseBody);
//
                if (removeResponse.code() == HttpStatus.OK.value()) {
                    ApplicationLogger.logger.info("Customer Deletion successful");
                    result = true;
                } else {
                    ApplicationLogger.logger.info("Error in deleting customer");
                }

            } else {
                ApplicationLogger.logger.info("removeCustomer() response is null");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private Boolean performAddDidNumbers(Integer custId, String acctId, NexgeAddDIDDTO addDIDDTO) {
        Boolean result = false;
        try {
            ClientService apiKey = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_AUTH_API_KEY)
                    .get(0), new CycleAvoidingMappingContext());

            if (null == apiKey) {
                throw new Exception("API KEY not found");
            }
            //Get JWT Token
            String jwtToken = getLoginToken(apiKey.getValue());


            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String payloadStr = mapper.writeValueAsString(addDIDDTO);

            //Perform Provision
            String saveRequestUrl = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_SERVER_KEY)
                    .get(0), new CycleAvoidingMappingContext()).getValue()
                    + NexgeVoiceProvisionConstants.NEXG_BASE_URL
                    + NexgeVoiceProvisionConstants.NEXG_SAVE_CUSTOMER_URL
                    + "/" + NexgeVoiceProvisionConstants.NEXG_BSS + custId.toString()
                    + NexgeVoiceProvisionConstants.NEXG_ACCOUNT_URL
                    + "/" + acctId
                    + NexgeVoiceProvisionConstants.NEXG_DID_URL;

            OkHttpClient saveClient = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();

            ApplicationLogger.logger.info("Payload : " + payloadStr);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), payloadStr);

            Request saveRequest = new Request.Builder()
                    .url(saveRequestUrl)
                    .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_X_API_KEY, apiKey.getValue())
                    .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_AUTHORIZATION, jwtToken)
                    .post(body)
                    .build();

            ApplicationLogger.logger.info("Save Req: " + saveRequest);
            Response saveResponse = saveClient.newCall(saveRequest).execute();

            if (saveResponse != null) {
                String responseBody = saveResponse.body().string();

                ApplicationLogger.logger.info("performAddDidNumbers() response:" + responseBody);
                if (saveResponse.code() == HttpStatus.CREATED.value()) {
                    ApplicationLogger.logger.info("Update DID successful");
                    result = true;
                } else {
                    ApplicationLogger.logger.info("Error in updating DID" + saveResponse.code());
                }

            } else {
                ApplicationLogger.logger.info("performAddDidNumbers() response is null");

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    public Boolean performUpdate(CustomerVoiceDetailsDTO voiceDetails, Customers customers) throws Exception {
        Boolean result = false;
        if (voiceDetails.getVoicesrvtype().equalsIgnoreCase(SubscriberConstants.PHONE_LINE)) {

            removeCustDidNumbers(customers.getId(), customers.getAcctno(), customers.getDidno());

            NexgeAddDIDDTO addDIDDTO = new NexgeAddDIDDTO();
            DIDProfile newDidProfile = new DIDProfile();
            newDidProfile.setDIDNumber(voiceDetails.getDidno());
            addDIDDTO.getDIDProfile().add(newDidProfile);
            result = performAddDidNumbers(customers.getId(), customers.getAcctno(), addDIDDTO);

        } else if (voiceDetails.getVoicesrvtype().equalsIgnoreCase(SubscriberConstants.SHIP_TRUNK)) {

            if (!voiceDetails.getDidno().equalsIgnoreCase(customers.getDidno())) {
                String childStr = Arrays.stream(customers.getChilddidno()
                                .split(","))
                        .filter(data -> !data.equalsIgnoreCase(customers.getDidno())).collect(Collectors.joining(","));
                result = removeCustDidNumbers(customers.getId(), customers.getAcctno(), childStr);
                NexgeAddDIDDTO addDIDDTO = new NexgeAddDIDDTO();
                DIDProfile newDidProfile = new DIDProfile();
                NumberBlockSIPDTO numberBlockSIPDTO = new NumberBlockSIPDTO();
                numberBlockSIPDTO.setCustomerPilotNumber(voiceDetails.getDidno());
                numberBlockSIPDTO.setDIDNumbers(Arrays.stream(voiceDetails.getChilddidno().split(",")).collect(Collectors.toList()));
                newDidProfile.setNumberBlock(numberBlockSIPDTO);
                addDIDDTO.getDIDProfile().add(newDidProfile);
                if (result)
                    result = performAddDidNumbers(customers.getId(), customers.getAcctno(), addDIDDTO);
                if (result)
                    result = removeCustDidNumbers(customers.getId(), customers.getAcctno(), customers.getDidno());
            } else {
                String childStr = Arrays.stream(customers.getChilddidno()
                                .split(","))
                        .filter(data -> !data.equalsIgnoreCase(customers.getDidno())).collect(Collectors.joining(","));
                result = removeCustDidNumbers(customers.getId(), customers.getAcctno(), childStr);

                NexgeAddDIDDTO addDIDDTO = new NexgeAddDIDDTO();
                DIDProfile newDidProfile = new DIDProfile();
                NumberBlockSIPDTO numberBlockSIPDTO = new NumberBlockSIPDTO();
                numberBlockSIPDTO.setDIDNumbers(Arrays.stream(voiceDetails.getChilddidno().split(","))
                        .filter(data -> !data.equalsIgnoreCase(voiceDetails.getDidno()))
                        .collect(Collectors.toList()));
                newDidProfile.setNumberBlock(numberBlockSIPDTO);
                addDIDDTO.getDIDProfile().add(newDidProfile);
                if (result)
                    result = performAddDidNumbers(customers.getId(), customers.getAcctno(), addDIDDTO);

            }

        }
        return result;
    }

    public Boolean performServicePlanUpdate(String custId, String acctId, String planId) throws Exception {
        Boolean result = false;

        try {
            ClientService apiKey = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_AUTH_API_KEY)
                    .get(0), new CycleAvoidingMappingContext());

            if (null == apiKey) {
                throw new Exception("API KEY not found");
            }
            //Get JWT Token
            String jwtToken = getLoginToken(apiKey.getValue());


            StringBuilder bodyBuilder = new StringBuilder();
            String bodyStr = bodyBuilder.append("{\"")
                    .append(NexgeVoiceProvisionConstants.NEXG_JSON_SERVICE_PLAN_ID_KEY)
                    .append("\":\"")
                    .append(planId)
                    .append("\"}").toString();

            //Perform Patch
            String patchRequestUrl = clientServiceMapper.dtoToDomain(clientServiceSrv.getClientSrvByName(NexgeVoiceProvisionConstants.NEXG_SERVER_KEY)
                    .get(0), new CycleAvoidingMappingContext()).getValue()
                    + NexgeVoiceProvisionConstants.NEXG_BASE_URL
                    + NexgeVoiceProvisionConstants.NEXG_SAVE_CUSTOMER_URL
                    + "/" + NexgeVoiceProvisionConstants.NEXG_BSS + custId
                    + NexgeVoiceProvisionConstants.NEXG_ACCOUNT_URL
                    + "/" + acctId;

            OkHttpClient saveClient = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), bodyStr);

            Request patchRequest = new Request.Builder()
                    .url(patchRequestUrl)
                    .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_X_API_KEY, apiKey.getValue())
                    .addHeader(NexgeVoiceProvisionConstants.NEXG_HEADER_AUTHORIZATION, jwtToken)
                    .patch(body)
                    .build();

            ApplicationLogger.logger.info("Save Req: " + patchRequest);
            Response patchResponse = saveClient.newCall(patchRequest).execute();

            if (patchResponse != null) {
                String responseBody = patchResponse.body().string();

                ApplicationLogger.logger.info("performServicePlanUpdate() response:" + responseBody);
                if (patchResponse.code() == HttpStatus.OK.value()) {
                    ApplicationLogger.logger.info("Update Service Plan successful");
                    result = true;
                } else {
                    ApplicationLogger.logger.info("Error in updating Service Plan" + patchResponse.code());
                }

            } else {
                ApplicationLogger.logger.info("performAddDidNumbers() response is null");

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }
}
