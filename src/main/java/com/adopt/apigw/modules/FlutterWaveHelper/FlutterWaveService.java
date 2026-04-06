package com.adopt.apigw.modules.FlutterWaveHelper;


import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.QCreditDocument;
import com.adopt.apigw.modules.CreditTransactionMapping.CreditTansactionMappingRepository;
import com.adopt.apigw.modules.CreditTransactionMapping.CreditTransactionMapping;
import com.adopt.apigw.pojo.api.CreditDebitDataPojo;
import com.adopt.apigw.pojo.api.CreditDebitMappingPojo;
import com.adopt.apigw.repository.postpaid.CreditDocRepository;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.service.postpaid.DebitDocService;
import com.flutterwave.rave.java.entry.transValidation;
import com.flutterwave.rave.java.payload.transverifyPayload;
import com.flutterwave.rave.java.service.verificationServices;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class FlutterWaveService {

    @Autowired
    private FlutterWaveAuthentication flutterWaveAuthentication;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private CreditTansactionMappingRepository creditTansactionMappingRepository;


    @Transactional
    public String validateTransactionFromFlutterWave(String txref , String transactionId) {

        transValidation transValidation = new transValidation();
        transverifyPayload transverifyPayload = new transverifyPayload();
        transverifyPayload.setTxref(txref);
        transverifyPayload.setSECKEY(flutterWaveAuthentication.getKey());
        String response = bvnvalidate(transverifyPayload);
        return response;
    }

    @Transactional
    public String validateTransactionFromFlutterWaveWithPaymentGateway(String txref , String transactionId , String secretKey) {

        transValidation transValidation = new transValidation();
        transverifyPayload transverifyPayload = new transverifyPayload();
        transverifyPayload.setTxref(txref);
        transverifyPayload.setSECKEY(secretKey);
        String response = bvnvalidate(transverifyPayload);
        return response;
    }

    public void validateTransaction(String txref , String transactionId){
        String response = validateTransactionFromFlutterWave(txref ,transactionId);
        if(response.contains("\"status\":\"success\"")){
            System.out.println("******payment validate Successfully******");
        }
        else {
            throw new RuntimeException("Payment failed to verify from flutterwave");
        }

    }

    public String bvnvalidate(transverifyPayload transverifyPayload) {
        verificationServices verificationServices = new verificationServices();

        String payload = new JSONObject(transverifyPayload).toString();
        String response = dotransverify(payload, transverifyPayload);

        return new JSONObject(response).toString();
    }

    public String dotransverify(String params, transverifyPayload transverifyPayload) {
        StringBuilder result = new StringBuilder();
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            HttpPost post  = new HttpPost(flutterWaveAuthentication.getURL());


          //  LOG.info("dotransverify response ::: " + params);
            //System.out.println("params ===>" + params);

            StringEntity input = new StringEntity(params);
            input.setContentType("application/json");
            //System.out.println("input ===>" + input);
            post.setEntity(input);
            HttpResponse response = client.execute(post);

          //  LOG.info("dotransverify response code ::: " + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
           // LOG.info("dotransverify request" + result.toString());
            if (!String.valueOf(response.getStatusLine().getStatusCode()).startsWith("2") && !response.getEntity().getContentType().getValue().contains("json")) {
                return null;
            }
            if (response.getStatusLine().getStatusCode() == 500) {
                return "there is an error with the data";
            } else {
                return result.toString();
            }

        } catch (UnsupportedEncodingException ex) {
          //  LOG.error(Arrays.toString(ex.getStackTrace()));
        } catch (IOException ex) {
           // LOG.error(Arrays.toString(ex.getStackTrace()));
        }
        return null;
    }

    public void PaymentAdjustForFlutterWave(String creditDocumentId) {

        QCreditDocument qCreditDocument =  QCreditDocument.creditDocument;
        Integer creditdocId =  Integer.parseInt(creditDocumentId);
        BooleanExpression credBoolexp = qCreditDocument.isNotNull().and(qCreditDocument.paytype.eq("Payment").and(qCreditDocument.id.eq(creditdocId)));
//         creditDocumentList
        List<Integer> creditDocIdList = new ArrayList<>();
        creditDocIdList.add(creditdocId);
        Optional<CreditDocument> creditDocument1 = creditDocRepository.findById(creditdocId);
        //Page<CreditDocument> response = (Page<CreditDocument>) creditDocRepository.findAll(credBoolexp);
        List<CreditDocument> list = creditDocRepository.findAllByIdIn(creditDocIdList);
        list.forEach(creditDocument -> {
            CreditDebitMappingPojo creditDebitMappingPojo = new CreditDebitMappingPojo();
            CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
            List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
            List<DebitDocument> debitDocumentList = debitDocRepository.findAllByCustomer(creditDocument.getCustomer());
            Optional<DebitDocument> debitDocument1=  debitDocumentList.stream().sorted((o1, o2) -> Long.compare(o2.getId(), o1.getId())).filter(debitDocument -> !debitDocument.getBillrunstatus().equalsIgnoreCase("Cancelled") && !debitDocument.getBillrunstatus().equalsIgnoreCase("Void")).findFirst();
            if(debitDocument1.isPresent()) {
                //  if (debitDocument1.get().getAdjustedAmount() < debitDocument1.get().getTotalamount()) {
                creditDebitMappingPojo.setInvoiceId(debitDocument1.get().getId());
                creditDebitDataPojo.setAmount(creditDocument.getAmount());
                creditDebitDataPojo.setId(creditDocument.getId());
                creditDebitDataPojoList.add(creditDebitDataPojo);
                creditDebitMappingPojo.setCreditDocumentList(creditDebitDataPojoList);

            }
            try {
                debitDocService.InvoicePaymentDone(creditDebitMappingPojo);
                CreditTransactionMapping creditTransactionMapping = new CreditTransactionMapping();
                creditTransactionMapping.setTransactionno(creditDocument1.get().getPaydetails4());
                creditTransactionMapping.setCreditdocumentid(creditDocument1.get().getId());
                creditTransactionMapping.setAdjustedamount(creditDocument1.get().getAmount());
                creditTransactionMapping.setDebitdocumentid(debitDocument1.get().getId());
                creditTransactionMapping.setCustid(creditDocument.getCustomer().getId());
                creditTansactionMappingRepository.save(creditTransactionMapping);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }



        });
    }

    public void PaymentAdjustForCustomerPay(Integer creditDocId) {
        List<Integer> creditDocIdList = new ArrayList<>();
        creditDocIdList.add(creditDocId);
        Optional<CreditDocument> creditDocument1 = creditDocRepository.findById(creditDocId);
        //Page<CreditDocument> response = (Page<CreditDocument>) creditDocRepository.findAll(credBoolexp);
        List<CreditDocument> list = creditDocRepository.findAllByIdIn(creditDocIdList);
        list.forEach(creditDocument -> {
            CreditDebitMappingPojo creditDebitMappingPojo = new CreditDebitMappingPojo();
            CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
            List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
            List<DebitDocument> debitDocumentList = debitDocRepository.findAllByCustomer(creditDocument.getCustomer());
            debitDocumentList.stream().filter(debitDocument -> !debitDocument.getBillrunstatus().equalsIgnoreCase("Cancelled") && !debitDocument.getBillrunstatus().equalsIgnoreCase("Void")).forEach(debitDocument -> {
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(0.00);
                }
                if (debitDocument.getAdjustedAmount() < debitDocument.getTotalamount()) {
                    creditDebitMappingPojo.setInvoiceId(debitDocument.getId());
                    creditDebitDataPojo.setAmount(creditDocument.getAmount());
                    creditDebitDataPojo.setId(creditDocument.getId());
                    creditDebitDataPojoList.add(creditDebitDataPojo);
                    creditDebitMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
                    try {
                        debitDocService.InvoicePaymentDone(creditDebitMappingPojo);
                        CreditTransactionMapping creditTransactionMapping = new CreditTransactionMapping();
                        creditTransactionMapping.setTransactionno(creditDocument1.get().getRemarks());
                        creditTransactionMapping.setCreditdocumentid(creditDocument1.get().getId());
                        creditTransactionMapping.setAdjustedamount(debitDocument.getAdjustedAmount());
                        creditTransactionMapping.setDebitdocumentid(debitDocument.getId());
                        creditTransactionMapping.setCustid(creditDocument.getCustomer().getId());
                        creditTansactionMappingRepository.save(creditTransactionMapping);


                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            });

        });
    }









}
