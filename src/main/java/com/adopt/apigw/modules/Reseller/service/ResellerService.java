package com.adopt.apigw.modules.Reseller.service;


import com.adopt.apigw.modules.Reseller.domain.Reseller;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Reseller.module.ResellerChangePasswordDto;
import com.adopt.apigw.modules.Reseller.module.ResellerDto;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;

import javax.transaction.Transactional;
import java.util.List;

public interface ResellerService {


	String changeStatus(Long resellerId,String status,Long mvnoId);

//	void validateLogoutUser(String userName,Long mvnoId);


    Reseller findResellerById(Long id, Long mvnoId, Boolean isUpdate);

    List<Reseller> searchResellers(String name, Long mvnoId);

    @Transactional
    void deleteResellerById(Long id, Long mvnoId);

    Reseller saveReseller(ResellerDto resellerDto, Long mvnoId);

//    @Transactional
//    void saveManageBalance(Reseller manageBalance, String remark, Long mvnoId);

//    @Transactional
//    void saveAddBalance(AddBalance manageBalance, Long mvnoId);

    @Transactional
    Reseller updateReseller(Reseller reseller, Long mvnoId);

    //Reseller validateLoginUser(LoginDto loginDto, Long mvnoId);

    //Reseller validateLoginUser(LoginDto loginDto, Long mvnoId, String cid, String mac);

    List<Reseller> searchResellersByLocationId(Long locationId, Long mvnoId);

    void changePassword(ResellerChangePasswordDto passwordDto, Long mvnoId);

    @SuppressWarnings("unchecked")
    PageableResponse<Reseller> getAllReseller(Long mvnoId, PaginationDTO paginationDTO, String resellerName);

    Integer countByLocationId(Long locationId);

    List<Reseller> findAllResellers(Long mvnoId, Long locationId);

    //Pagination

}
