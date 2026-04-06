package com.adopt.apigw.modules.childcustomer.service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.modules.childcustomer.dto.ChangePasswordPojo;
import com.adopt.apigw.modules.childcustomer.dto.ChildCustPojo;
import com.adopt.apigw.modules.childcustomer.entity.ChildCustomer;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public interface ChildCustomerService {
    ResponseEntity<?> create(ChildCustPojo pojo, HttpServletRequest req);

    List<ChildCustomer> getChildCustomer();

    void delete(Long id, HttpServletRequest req);

    GenericDataDTO getchildCustByParentID(Long parentId);
    ResponseEntity<?> updatechildCustByParentID(ChildCustPojo pojo);

    Page<ChildCustomer> getAllChildCustomer(Integer page, Integer pagesize, Integer mvnoId);

    ChildCustomer getchildCustByID(Long id);

    Page<ChildCustomer> getchildCustByID(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder, String status);

    Page<ChildCustomer> getChildByParentCustId(Long id,Integer page , Integer pageSize);

    List<ChildCustomer> getchildCustByMobileNumber(String mobileNumber,Integer parentId,Long mvnoId);


    ResponseEntity<?> updateChildPassword(ChangePasswordPojo pojo, Integer mvnoId);

    GenericDataDTO  getChildCustomerByMobileNumberAndUserName(String username,String mobileNumber);

    GenericDataDTO  getChildCustomerByUserName(String username,Integer mvnoId);

}
