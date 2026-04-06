package com.adopt.apigw.modules.SubBusinessUnit.Service;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveSubBusinessUnitSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateSubBusinessUnitSharedDataMessage;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.domain.QBranch;
import com.adopt.apigw.modules.SubBusinessUnit.Domain.QSubBusinessUnit;
import com.adopt.apigw.modules.SubBusinessUnit.Domain.QSubBusinessUnit;
import com.adopt.apigw.modules.SubBusinessUnit.Domain.SubBusinessUnit;
import com.adopt.apigw.modules.SubBusinessUnit.Model.SubBusinessUnitDTO;
import com.adopt.apigw.modules.SubBusinessUnit.Repo.SubBusinessUnitRepository;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class SubBusinessUnitService extends ExBaseAbstractService<SubBusinessUnitDTO, SubBusinessUnit, Long> {

    @Autowired
    SubBusinessUnitRepository subBusinessUnitRepository;

    public SubBusinessUnitService(JpaRepository<SubBusinessUnit, Long> repository, IBaseMapper<SubBusinessUnitDTO, SubBusinessUnit> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[SubBusinessUnitService]";
    }
//
//    @Override
//    public boolean duplicateVerifyAtSave(String subbuname) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (subbuname != null) {
//            subbuname = subbuname.trim();
////            QSubBusinessUnit qSubBusinessUnit = QSubBusinessUnit.subBusinessUnit;
////            BooleanExpression exp = qSubBusinessUnit.isNotNull();
////            if (getMvnoIdFromCurrentStaff() == 2) {
////                exp = exp.and(qSubBusinessUnit.subBuName.equalsIgnoreCase(subBuName));
////                if (exp.count().equals(null)) {
////                    flag = true;
////                }
////            }
////            pincodeRepository.duplicateVerifyAtSaveWithPincodeAndCityID(pincode, cityId);
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1)
//                count = subBusinessUnitRepository.duplicateVerifyAtSaveWithName(subbuname);
//            else count = subBusinessUnitRepository.duplicateVerifyAtSaveWithName(subbuname, mvnoIds);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    public String getSubBUName(String subbuname) {
//        QSubBusinessUnit qSubBusinessUnit = QSubBusinessUnit.subBusinessUnit;
//        BooleanExpression expression = qSubBusinessUnit.isNotNull();
//        expression.and(qSubBusinessUnit.subbuname.equalsIgnoreCase(subbuname));
//
//        return subBusinessUnitRepository.findAll(expression).toString();
//    }
//
////    @Override
////    public boolean duplicateVerifyAtEdit(String subbuname) throws Exception {
////        duplicateVerifyAtEdit
////    }
//
//    public boolean duplicateVerifyAtEdit(String subbuname, Long id) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (subbuname != null) {
//            subbuname = subbuname.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) {
//                count = subBusinessUnitRepository.duplicateVerifyAtSaveWithName(subbuname);
//            } else {
//                count = subBusinessUnitRepository.duplicateVerifyAtSaveWithName(subbuname, mvnoIds);
//            }
//            if (count >= 1) {
//                Integer countEdit;
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    countEdit = subBusinessUnitRepository.duplicateVerifyAtEdit(subbuname, id);
//                else countEdit = subBusinessUnitRepository.duplicateVerifyAtEdit(subbuname, id, mvnoIds);
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
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getSubBusinessUnitByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    public GenericDataDTO getSubBusinessUnitByName(String name, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getSubBusinessUnitByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            Page<SubBusinessUnit> subBusinessUnitList = null;
////            QBranch qBranch = QBranch.branch;
//            QSubBusinessUnit qSubBusinessUnit = QSubBusinessUnit.subBusinessUnit;
//            BooleanExpression booleanExpression = qSubBusinessUnit.isNotNull()
//                    .and(qSubBusinessUnit.isDeleted.eq(false))
//                        .and(qSubBusinessUnit.subbuname.likeIgnoreCase("%" + name + "%").or(qSubBusinessUnit.subbucode.likeIgnoreCase("%" + name + "%").or(qSubBusinessUnit.status.equalsIgnoreCase(name))));
//            if (getMvnoIdFromCurrentStaff() == 1) {
//                //branchList = branchRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
//                subBusinessUnitList = subBusinessUnitRepository.findAll(booleanExpression, pageRequest);
//            } else {
//                //branchList = branchRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                booleanExpression = booleanExpression.and(qSubBusinessUnit.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                subBusinessUnitList = subBusinessUnitRepository.findAll(booleanExpression, pageRequest);
//            }
//            if (null != subBusinessUnitList && 0 < subBusinessUnitList.getSize()) {
//                makeGenericResponse(genericDataDTO, subBusinessUnitList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//        }
//        return null;
//    }


    /* Get data from the common microservice  */


    public void saveSubBU(SaveSubBusinessUnitSharedDataMessage message){
        SubBusinessUnit subBusinessUnit = new SubBusinessUnit();
        subBusinessUnit.setId(message.getId());
        subBusinessUnit.setStatus(message.getStatus());
        subBusinessUnit.setMvnoId(message.getMvnoId());
        subBusinessUnit.setIsDeleted(message.getIsDeleted());
        subBusinessUnit.setSubbucode(message.getSubbucode());
        subBusinessUnit.setBusinessunitid(message.getBusinessunitid());
        subBusinessUnit.setSubbuname(message.getSubbuname());
        subBusinessUnit.setCreatedById(message.getCreatedById());
        subBusinessUnit.setLastModifiedById(message.getLastModifiedById());
        subBusinessUnit.setCreatedByName(message.getCreatedByName());
        subBusinessUnit.setLastModifiedByName(message.getLastModifiedByName());
        subBusinessUnit.setCreatedate(LocalDateTime.now());
        subBusinessUnitRepository.save(subBusinessUnit);
    }
    public void updateSubBU(UpdateSubBusinessUnitSharedDataMessage message){
        SubBusinessUnit subBusinessUnit = subBusinessUnitRepository.findById(message.getId()).orElse(null);
        if (subBusinessUnit != null) {
            subBusinessUnit.setStatus(message.getStatus());
            subBusinessUnit.setMvnoId(message.getMvnoId());
            subBusinessUnit.setIsDeleted(message.getIsDeleted());
            subBusinessUnit.setSubbucode(message.getSubbucode());
            subBusinessUnit.setBusinessunitid(message.getBusinessunitid());
            subBusinessUnit.setSubbuname(message.getSubbuname());
            subBusinessUnit.setCreatedById(message.getCreatedById());
            subBusinessUnit.setLastModifiedById(message.getLastModifiedById());
            subBusinessUnit.setCreatedByName(message.getCreatedByName());
            subBusinessUnit.setLastModifiedByName(message.getLastModifiedByName());
            subBusinessUnitRepository.save(subBusinessUnit);
        } else {
            SubBusinessUnit subBusinessUnit1 = new SubBusinessUnit();
            subBusinessUnit1.setId(message.getId());
            subBusinessUnit1.setStatus(message.getStatus());
            subBusinessUnit1.setMvnoId(message.getMvnoId());
            subBusinessUnit1.setIsDeleted(message.getIsDeleted());
            subBusinessUnit1.setSubbucode(message.getSubbucode());
            subBusinessUnit1.setBusinessunitid(message.getBusinessunitid());
            subBusinessUnit1.setSubbuname(message.getSubbuname());
            subBusinessUnit1.setCreatedById(message.getCreatedById());
            subBusinessUnit1.setLastModifiedById(message.getLastModifiedById());
            subBusinessUnit1.setCreatedByName(message.getCreatedByName());
            subBusinessUnit1.setLastModifiedByName(message.getLastModifiedByName());
            subBusinessUnitRepository.save(subBusinessUnit1);
        }
    }

}
