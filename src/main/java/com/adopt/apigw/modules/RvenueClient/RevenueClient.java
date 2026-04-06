package com.adopt.apigw.modules.RvenueClient;

import com.adopt.apigw.model.postpaid.CustomerLedgerDtlsPojo;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.TrialDebitDocument;
import com.adopt.apigw.modules.DebitDocumentInventoryRel.DebitDocNumberMappingPojo;
import com.adopt.apigw.modules.Integration.Pojo.CdataCustDetailsPojo;
import com.adopt.apigw.pojo.CustomPeriodInvoiceDTO;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FeignClient(name = "ADOPTREVENUEMANAGEMENT-SERVICE",contextId = "AdoptRevenuemanagementMicroService",fallback = RevenueClientFallback.class)
public interface RevenueClient {
    @PostMapping("/api/v1/Revenue/cafWallet")
  public   ResponseEntity<?> getCafWalletAmount(@RequestHeader("Authorization") String token, @RequestBody CustomerLedgerDtlsPojo pojo);
    @Retryable(
            value = FeignException.class,
            maxAttempts = 1,
            backoff = @Backoff(delay = 1000)
    )
    @GetMapping("/api/v1/Revenue/getIspDebitdocNumbers")
    public List<DebitDocNumberMappingPojo> getDebitDocNumber(@RequestHeader("Authorization") String token);

  @GetMapping("/api/v1/Revenue/getDebitDocument/{custId}")
  public List<DebitDocument> getDebitDocumentByCustId(@PathVariable Integer custId, @RequestHeader("Authorization") String token);

  @GetMapping("/api/v1/Revenue/getTrailDebitDocument/{custId}")
  public List<TrialDebitDocument> getTrailDebitDocumentByCustId(@PathVariable Integer custId, @RequestHeader("Authorization") String token);

  @PostMapping("/api/v1/Revenue/wallet")
  public ResponseEntity<Map<String, Object>> getWalletAmount(@RequestBody CustomerLedgerDtlsPojo pojo, @RequestHeader("Authorization") String token);

//  @GetMapping("/api/v1/Revenue/getCurrentWalletAmountByCustId/{customerId}")
//  public Double getWalletBalanceByCustId(@PathVariable Integer customerId, @RequestHeader("Authorization") String token);

  @PostMapping("/api/v1/Revenue/wallets/list")
  ResponseEntity<Map<String, Object>> getWalletAmounts(
          @RequestBody List<CustomerLedgerDtlsPojo> pojoList,
          @RequestHeader("Authorization") String token
  );
  @PostMapping("/api/v1/Revenue/taxPercentage")
  ResponseEntity<Map<Integer, List<Double>>> getTaxPercentagesByCustomers(
          @RequestHeader("Authorization") String token,
          @RequestBody List<Integer> customerIds
  );

  @PostMapping("/api/v1/Revenue/getDebitdocNumbers")
  ResponseEntity<Map<Integer, List<String>>> getDebitDocNumber(
          @RequestHeader("Authorization") String token,
          @RequestBody List<Integer> debitdocIds
  );
  @PostMapping("/api/v1/Revenue/getInvoiceNumber")
  ResponseEntity<Map<Integer, List<String>>> getInvoiceNumber(
          @RequestHeader("Authorization") String token,
          @RequestBody List<Integer> debitdocumentIds

  );

  @PostMapping("/api/v1/Revenue/postpaid/period-invoice")
 ResponseEntity<?> createPostpaidInvoiceForCustomPeriod(@RequestBody CustomPeriodInvoiceDTO requestDTO, @RequestParam String status, @RequestHeader("Authorization") String token);


}
