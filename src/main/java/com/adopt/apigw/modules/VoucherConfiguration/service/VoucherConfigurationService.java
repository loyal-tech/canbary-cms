package com.adopt.apigw.modules.VoucherConfiguration.service;


import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.VoucherConfiguration.domain.UpdateVoucherConfigDto;
import com.adopt.apigw.modules.VoucherConfiguration.domain.VoucherConfiguration;
import com.adopt.apigw.modules.VoucherConfiguration.module.VoucherConfigDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VoucherConfigurationService 
{
	VoucherConfiguration save(VoucherConfigDto voucherConfigDto, Long mvnoId);
	VoucherConfiguration update(UpdateVoucherConfigDto voucherConfigDto, Long mvnoId);


	VoucherConfiguration findById(Long id, Long mvnoId);

	VoucherConfiguration getByID(Long id);

	PageableResponse<VoucherConfiguration> getAll(Long mvnoId, String name, Long locationId, PaginationDTO paginationDTO);

	Page<VoucherConfiguration> findByName(String name, Long mvnoId, PaginationDTO paginationDTO);

	void deleteById(Long id, Long mvnoId);
	String updateVoucherConfigStatus(Long id, String status,Long mvnoId);
	List<VoucherConfiguration> findVoucher(String voucherName, String voucherCodeFormat,Long mvnoId);
	List<VoucherConfiguration> findVoucherConfigurations(Long planId, Long mvnoId);
	Integer countByPlanIdAndMvnoId(Long planId, Long mvnoId);
}
