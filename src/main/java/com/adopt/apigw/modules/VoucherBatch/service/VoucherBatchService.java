package com.adopt.apigw.modules.VoucherBatch.service;

import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.VoucherBatch.domain.BSSVoucherBatch;
import com.adopt.apigw.modules.VoucherBatch.module.UpdateVoucherBatchDto;
import com.adopt.apigw.modules.VoucherBatch.module.VoucherBatchDto;
import com.adopt.apigw.modules.VoucherBatch.module.VoucherBatchInfoDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VoucherBatchService {

	VoucherBatchDto saveVoucherBatch(VoucherBatchDto voucherBatchDto, Long mvnoId);
	void generateBatchAndVouchers(VoucherBatchDto voucherBatchDto, Long mvnoId);
    UpdateVoucherBatchDto updateVoucherBatch(UpdateVoucherBatchDto voucherBatchDto, Long mvnoId);




	void deleteVoucherBatchById(Long voucherBatchId, Long mvnoId);



	List<VoucherBatchInfoDto> findAllVoucherBatch(Long mvnoId);

	PageableResponse<VoucherBatchInfoDto> getAllVoucherBatch(Long mvnoId, Long resellerId, String batchName,
															 PaginationDTO paginationDto);





	List<BSSVoucherBatch> searchVoucherBatch(String batchName, Long mvnoId);
	List<BSSVoucherBatch> findAllVoucherBatchByPlanId(Long planId, Long mvnoId);
	BSSVoucherBatch findVoucherBatchById(Long voucherBatchId, Long mvnoId);
	List<BSSVoucherBatch> findVoucherBatcheWithoutReseller(Long mvnoId);



	List<VoucherBatchDto> searchByDate(Long mvnoId, LocalDateTime createDate);
	void assignResellerToVoucherBatch(Long voucherBatchId, Long resellerId, Long mvnoId);
	Integer countByPlanIdAndMvnoId(Long planId, Long mvnoId);
	Integer countByResellerIdAndMvnoId(Long reseller, Long mvnoId);

	void updateExpiryDate(Long voucherBatchId, String expiryDate, Long mvnoId, String lastModifiedBy);

	boolean isPartnerBalanceInsufficient(VoucherBatchDto voucherBatchDto, Integer loggedInUserPartnerId);

	public void shareVoucherBatchData(VoucherBatchDto voucherBatchDto,Integer partnerId);
}
