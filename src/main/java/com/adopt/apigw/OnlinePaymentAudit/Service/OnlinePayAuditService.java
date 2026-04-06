package com.adopt.apigw.OnlinePaymentAudit.Service;



import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.pojo.api.CustomerPaymentDto;
import com.adopt.apigw.repository.common.CustomerPaymentRepository;
import com.adopt.apigw.service.radius.AbstractService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OnlinePayAuditService extends AbstractService<CustomerPayment, CustomerPaymentDto,Long>  {

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    private final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository getRepository() {
        return null;
    }




    public Page<CustomerPayment> getOnlinePayAuditList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder,
                                                       List<GenericSearchModel> filterList, Integer mvnoId){
        pageRequest = generatePageRequest(pageNumber,customPageSize,sortBy,sortOrder);

        // TODO: pass mvnoID manually 6/5/2025
        if(mvnoId==1) {
            return customerPaymentRepository.findAll(pageRequest);
        }
        if (null == filterList || 0 == filterList.size()){
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                return customerPaymentRepository.findAll(pageRequest, Arrays.asList(1, mvnoId));
          }
    else {
            for (GenericSearchModel searchModel : filterList) {
                if (null == searchModel.getFilterColumn() || searchModel.getFilterValue().isEmpty()) {
                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        return customerPaymentRepository.findAll(pageRequest, Arrays.asList(1, mvnoId));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        return customerPaymentRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());

                } else {
                    return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
                }
            }
        }

        return null;
    }

    public List<CustomerPayment> getOnlinePayAuditListByCustId( Integer custId){
        try{
            List<CustomerPayment> onlinePayAudits = customerPaymentRepository.findCustomerPaymentByCustId(custId);
            log.info("List of payment for the customer fetch successfully");
            return onlinePayAudits;

        }catch (Exception e){
            log.error("List of payment for the customer fetch failed : "+e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<CustomerPayment> getOnlinePayAuditListByPartner( Integer partnerId){
        try{
            List<CustomerPayment> onlinePayAudits = customerPaymentRepository.findCustomerPaymentByPartnerId(partnerId);
            log.info("List of payment for the partner fetch successfully");
            return onlinePayAudits;

        }catch (Exception e){
            log.error("List of payment for the partner fetch failed : "+e.getMessage());
        }
        return new ArrayList<>();
    }


    @Override
    public Page<CustomerPayment> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy,
                            Integer sortOrder,Integer mvnoId) {
        //String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        if(searchModel.getFilterDataType().equals("customerUsername")){
                            return getAuditByCustomerName(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }else if(searchModel.getFilterDataType().equals("status")){
                            return getAuditBySatus(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }else if(searchModel.getFilterDataType().equals("orderid")){
                            return getAuditByOrderid(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }else if(searchModel.getFilterDataType().equals("merchantName")){
                            return getAuditByMerchant(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }else if(searchModel.getFilterDataType().equals("pgTransactionId")){
                            return getAuditByPgTransactionId(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }
                        else if(searchModel.getFilterDataType().equals("accountNumber")){
                            return getAuditByAccountNumber(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }
                        else{
                            return customerPaymentRepository.findAll(pageRequest);
                        }
                    }
                } else
                    throw new RuntimeException("Please Provide Search Column!");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Online Payment Audit" + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }


    public Page<CustomerPayment> getAuditByCustomerName(String s1, String s2, String dataType, PageRequest pageRequest) {

        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return customerPaymentRepository.findAllByCustomerUsername(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByCustomerUsernameAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByCustomerUsernameAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
    }

    public Page<CustomerPayment> getAuditBySatus(String s1, String s2, String dataType, PageRequest pageRequest) {

        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return customerPaymentRepository.findAllByStatus(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByStatusAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByStatusAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
    }


    public Page<CustomerPayment> getAuditByOrderid(String s1, String s2, String dataType, PageRequest pageRequest) {

        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return customerPaymentRepository.findAllByOrderid(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByOrderidAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByOrderidAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
    }


    public Page<CustomerPayment> getAuditByMerchant(String s1, String s2, String dataType, PageRequest pageRequest) {

        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return customerPaymentRepository.findAllByMerchantName(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByMerchantNameAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByMerchantNameAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
    }


    public Page<CustomerPayment> getAuditByPgTransactionId(String s1, String s2, String dataType, PageRequest pageRequest) {

        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return customerPaymentRepository.findAllByPgTransactionIdWithSearch(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByPgTransactionIdWithSearchAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByPgTransactionIdWithSearchAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
    }

    public Page<CustomerPayment> getAuditByAccountNumber(String s1, String s2, String dataType, PageRequest pageRequest) {

        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return customerPaymentRepository.findAllByAccountNumberWithSearch(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByAccountNumberWithSearchAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return customerPaymentRepository.findAllByAccountNumberWithSearchAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
    }








}
