package com.adopt.apigw.modules.RvenueClient;

import com.adopt.apigw.model.postpaid.CustomerLedgerDtlsPojo;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.TrialDebitDocument;
import com.adopt.apigw.modules.DebitDocumentInventoryRel.DebitDocNumberMappingPojo;
import com.adopt.apigw.pojo.CustomPeriodInvoiceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class RevenueClientFallback implements RevenueClient {
    @Override
    public ResponseEntity<?> getCafWalletAmount(String token, CustomerLedgerDtlsPojo pojo) {
        return null;
    }

    @Override
    public List<DebitDocNumberMappingPojo> getDebitDocNumber(String token) {
        return Collections.emptyList();
    }

    @Override
    public List<DebitDocument> getDebitDocumentByCustId(Integer custId, String token) {
        return Collections.emptyList();
    }

    @Override
    public List<TrialDebitDocument> getTrailDebitDocumentByCustId(Integer custId, String token) {
        return Collections.emptyList();
    }

    @Override
    public ResponseEntity<Map<String, Object>> getWalletAmount(CustomerLedgerDtlsPojo pojo, String token) {
        return null;
    }

    @Override
    public ResponseEntity<Map<String, Object>> getWalletAmounts(@RequestBody List<CustomerLedgerDtlsPojo> pojoList, @RequestHeader("Authorization") String token) {
        return null;
    }

    @Override
    public ResponseEntity<Map<Integer, List<Double>>>getTaxPercentagesByCustomers(
            @RequestHeader("Authorization") String token,
            @RequestBody List<Integer> customerIds) {
        return null;
    }

    @Override
    public ResponseEntity<Map<Integer, List<String>>>getDebitDocNumber(
            @RequestHeader("Authorization") String token,
            @RequestBody List<Integer> debitdocIds) {
        return null;
    }

    @Override
    public ResponseEntity<Map<Integer, List<String>>>getInvoiceNumber(
            @RequestHeader("Authorization") String token,
            @RequestBody List<Integer> debitdocumentIds) {
        return null;
    }

    @Override
    public ResponseEntity<?> createPostpaidInvoiceForCustomPeriod(@RequestBody CustomPeriodInvoiceDTO requestDTO, @RequestParam String status, @RequestHeader("Authorization") String token){
        return null;
    }
}
