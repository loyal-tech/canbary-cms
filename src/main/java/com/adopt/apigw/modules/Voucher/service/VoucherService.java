package com.adopt.apigw.modules.Voucher.service;


import com.adopt.apigw.modules.Voucher.domain.Voucher;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface VoucherService {


    @Transactional
    void generateBatch(Long batchId, Long configId, Long mvnoId);

    ResponseEntity<Map<String, Object>> validateVoucher(String code, Long mvnoId);

    ResponseEntity<Map<String, Object>> verifyVoucher(String code, Long mvnoId);

    Page<Voucher> getAllVouchers(Long mvnoId, PaginationDTO paginationDTO, Long resellerId);

    Page<Voucher> findVouchers(String batchName, String status, Long mvnoId, PaginationDTO paginationDTO);

    String changeStatusToActive(List<Long> voucherIdList, Long mvnoId);

    String changeStatusToBlock(List<Long> voucherIdList, Long mvnoId);

    String changeStatusToUnblock(List<Long> voucherIdList, Long mvnoId);

    String changeStatusToScrap(List<Long> voucherIdList, Long mvnoId);

    void addVoucherId(Long id);

    Page<Voucher> findVouchersByBatchId(Long batchId, Long mvnoId, PaginationDTO paginationDTO);

    void sendSms(Long id, String countryCode, String mobileNo, String code, Long mvnoId);


    Integer countByBatchId(Long batchId);

    List<Map<String,String>>dataToExport(String batchName, String status,Long mvnoId);

    Voucher getVoucher(String voucherCode, Integer mvnoId);

    ResponseEntity<Map<String, Object>> changeStatus(Long voucherId, Long aLong);
}
