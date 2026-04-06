package com.adopt.apigw.modules.BankManagement.service;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveBankManagementSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateBankManagementSharedDataMessage;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.QCreditDocument;
import com.adopt.apigw.modules.BankManagement.domain.BankManagement;
import com.adopt.apigw.modules.BankManagement.domain.QBankManagement;
import com.adopt.apigw.modules.BankManagement.mapper.BankManagementMapper;
import com.adopt.apigw.modules.BankManagement.model.BankManagementDTO;
import com.adopt.apigw.modules.BankManagement.repository.BankManagementRepository;
import com.adopt.apigw.repository.postpaid.CreditDocRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BankManagementService  extends ExBaseAbstractService<BankManagementDTO , BankManagement , Long> {

    public BankManagementService(BankManagementRepository repository, BankManagementMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;

    }


    @Autowired
    private BankManagementRepository bankManagementRepository;

    @Autowired
    private CreditDocRepository creditDocRepository;


//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<BankManagement> paginationList = null;
//        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
//        if(getMvnoIdFromCurrentStaff() == 1)
//            paginationList = bankManagementRepository.findAll(pageRequest);
//        else
//            paginationList = bankManagementRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
//
//    //Save Bank
//    @Override
//    public boolean duplicateVerifyAtSave(String accountnum) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (accountnum != null) {
//            accountnum = accountnum.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = bankManagementRepository.duplicateVerifyAtSave(accountnum);
//            else count = bankManagementRepository.duplicateVerifyAtSave(accountnum, mvnoIds);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    public boolean deleteVerify(Long id)
//    {
//        boolean flag = false;
//        QBankManagement qBankManagement = QBankManagement.bankManagement;
//        QCreditDocument qCreditDocument =QCreditDocument.creditDocument;
////        BooleanExpression expression = qBankManagement.isNotNull().and(qBankManagement.isDeleted.eq(false));
//        BooleanExpression expression = qCreditDocument.isNotNull().and(qCreditDocument.isDelete.eq(false));
//        expression = expression.and((qCreditDocument.bankManagement.in(id)).or(qCreditDocument.destinationBank.in(id)));
//        expression = expression.and(qCreditDocument.status.notEqualsIgnoreCase("rejected"));
//
//        boolean count = creditDocRepository.exists(expression);
//        if (count == false) {
//            flag = true;
//        }
//        return flag;
//    }
//
//    public boolean duplicateVerifyAtEdit(String accountnum, Long id,String bankType) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (accountnum != null) {
//            accountnum  = accountnum.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = bankManagementRepository.duplicateVerifyAtSave(accountnum);
//            else count = bankManagementRepository.duplicateVerifyAtSave(accountnum, mvnoIds);
//            if(count > 1 ){
//                List<BankManagement> bankManagementList = bankManagementRepository.findByAccountnumAndMvnoIdIn(accountnum,mvnoIds);
//                for(BankManagement bankManagement:bankManagementList){
//                    if(bankManagement.getBanktype().equalsIgnoreCase(bankType)){
//                        return false;
//                    }
//                }
//            }
//
//            if (count >= 1) {
//                Integer countEdit;
//                if(getMvnoIdFromCurrentStaff() == 1) countEdit = bankManagementRepository.duplicateVerifyAtEdit(accountnum, id);
//                else countEdit = bankManagementRepository.duplicateVerifyAtEdit(accountnum, id, mvnoIds);
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//
////    public boolean deleteVerification(Long id)throws Exception {
////        boolean flag = false;
////        Integer count = bankManagementRepository.deleteVerify(id);
////        if(count==0){
////            flag=true;
////        }
////        return flag;
////    }
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            QBankManagement qBankManagement = QBankManagement.bankManagement;
//            BooleanExpression exp = qBankManagement.isNotNull();
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (!searchModel.getFilterValue().isEmpty()) {
//                        String s = searchModel.getFilterValue();
//                        exp = exp.and(qBankManagement.accountnum.containsIgnoreCase(s)
//                                      .or(qBankManagement.bankname.containsIgnoreCase(s))
//                        .or(qBankManagement.bankholdername.containsIgnoreCase(s))
//                        .or(qBankManagement.ifsccode.containsIgnoreCase(s))
//                                .or(qBankManagement.banktype.containsIgnoreCase(s))
//                        .or(qBankManagement.status.equalsIgnoreCase(s)));
//
//                        exp = exp.and(qBankManagement.isDeleted.eq(false));
//
//
//
//                        if(getMvnoIdFromCurrentStaff() != 1)
//                            exp = exp.and(qBankManagement.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//                        GenericDataDTO genericDataDTO = new GenericDataDTO();
//                        Page<BankManagement> bankList = null;
//                        bankList = bankManagementRepository.findAll(exp, pageRequest);
//                        if (null != bankList && 0 < bankList.getSize()) {
//                            makeGenericResponse(genericDataDTO, bankList);
//                        }
//                        return genericDataDTO;
//
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
    public BankManagement validateBankByName(String name) {
        try {

             QBankManagement qBankManagement = QBankManagement.bankManagement;
            BooleanExpression boolExp = qBankManagement.isNotNull();
            Long i = Long.parseLong(name);
            boolExp = boolExp.and(qBankManagement.id.eq(i));

            Optional<BankManagement> bankManagement = bankManagementRepository.findOne(boolExp);
            if (!bankManagement.isPresent()) {
                throw new IllegalArgumentException(
                        "No record found with accoun num " + name + " Please enter valid account no");
            }
            return bankManagement.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //Get data from the common  microservice


    public void saveBank(SaveBankManagementSharedDataMessage message){
        BankManagement bankManagement = new BankManagement();
        bankManagement.setId(message.getId());
        bankManagement.setStatus(message.getStatus());
        bankManagement.setAccountnum(message.getAccountnum());
        bankManagement.setBankname(message.getBankname());
        bankManagement.setBankcode(message.getBankcode());
        bankManagement.setBankholdername(message.getBankholdername());
        bankManagement.setMvnoId(message.getMvnoId());
        bankManagement.setIsDeleted(message.getIsDeleted());
        bankManagement.setIfsccode(message.getIfsccode());
        bankManagement.setBanktype(message.getBanktype());
        bankManagement.setCreatedById(message.getCreatedById());
        bankManagement.setLastModifiedById(message.getLastModifiedById());
        bankManagement.setCreatedByName(message.getCreatedByName());
        bankManagement.setLastModifiedByName(message.getLastModifiedByName());
        bankManagement.setCreatedate(LocalDateTime.now());

        bankManagementRepository.save(bankManagement);
    }

    public void updateBank(UpdateBankManagementSharedDataMessage message){
        BankManagement bankManagement = bankManagementRepository.findById(message.getId()).orElse(null);
        if(bankManagement!=null){
            bankManagement.setStatus(message.getStatus());
            bankManagement.setAccountnum(message.getAccountnum());
            bankManagement.setBankname(message.getBankname());
            bankManagement.setBankcode(message.getBankcode());
            bankManagement.setBankholdername(message.getBankholdername());
            bankManagement.setMvnoId(message.getMvnoId());
            bankManagement.setIsDeleted(message.getIsDeleted());
            bankManagement.setIfsccode(message.getIfsccode());
            bankManagement.setBanktype(message.getBanktype());
            bankManagement.setLastModifiedById(message.getLastModifiedById());
            bankManagement.setCreatedByName(message.getCreatedByName());
            bankManagement.setLastModifiedByName(message.getLastModifiedByName());
            bankManagementRepository.save(bankManagement);
        } else {
            BankManagement bankManagement1 = new BankManagement();
            bankManagement1.setId(message.getId());
            bankManagement1.setStatus(message.getStatus());
            bankManagement1.setAccountnum(message.getAccountnum());
            bankManagement1.setBankname(message.getBankname());
            bankManagement1.setBankcode(message.getBankcode());
            bankManagement1.setBankholdername(message.getBankholdername());
            bankManagement1.setMvnoId(message.getMvnoId());
            bankManagement1.setIsDeleted(message.getIsDeleted());
            bankManagement1.setIfsccode(message.getIfsccode());
            bankManagement1.setBanktype(message.getBanktype());
            bankManagement.setLastModifiedById(message.getLastModifiedById());
            bankManagement.setCreatedByName(message.getCreatedByName());
            bankManagement.setLastModifiedByName(message.getLastModifiedByName());
            bankManagementRepository.save(bankManagement1);
        }
    }

}


