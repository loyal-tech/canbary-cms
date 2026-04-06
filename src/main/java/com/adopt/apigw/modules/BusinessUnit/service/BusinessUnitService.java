package com.adopt.apigw.modules.BusinessUnit.service;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveBusinessUnitSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateBusinessUnitSharedDataMessage;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.domain.QBusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.mapper.BusinessUnitMapper;
import com.adopt.apigw.modules.BusinessUnit.model.BusinessUnitDTO;
import com.adopt.apigw.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.adopt.apigw.modules.InvestmentCodeBUmapping.IcNameBuMapping;
import com.adopt.apigw.modules.InvestmentCodeBUmapping.IcNameBuMappingRepo;
import com.adopt.apigw.modules.InvestmentCodeBUmapping.QIcNameBuMapping;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessUnitService extends ExBaseAbstractService<BusinessUnitDTO, BusinessUnit, Long> {
//    public BusinessUnitService(BusinessUnitRepository repository, BusinessUnitMapper mapper) {
//        super(repository, mapper);
//    }
//
////    @Autowired
////    BusinessUnitMapper mapper;
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[BusinessUnitService]";
//    }
//
//    @Autowired
//    private BusinessUnitRepository businessUnitRepository;
//
//    @Autowired
//    private IcNameBuMappingRepo icNameBuMappingRepo;
//
//    @Autowired
//    private BusinessUnitMapper mapper;
//
//    //Get All Business Unit with Pagination
//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<BusinessUnit> paginationList = null;
//        //PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
//        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
//        if (getMvnoIdFromCurrentStaff() == 1) paginationList = businessUnitRepository.findAll(pageRequest);
//        else
//            paginationList = businessUnitRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
//
//    //Save Business Unit
//    @Override
//    public boolean duplicateVerifyAtSave(String buname) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (buname != null) {
//            buname = buname.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = businessUnitRepository.duplicateVerifyAtSave(buname);
//            else count = businessUnitRepository.duplicateVerifyAtSave(buname, mvnoIds);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    //Update Business Unit
//    public boolean duplicateVerifyAtEdit(String buname, Long id) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (buname != null) {
//            buname = buname.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = businessUnitRepository.duplicateVerifyAtSave(buname);
//            else count = businessUnitRepository.duplicateVerifyAtSave(buname, mvnoIds);
//            if (count >= 1) {
//                Integer countEdit;
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    countEdit = businessUnitRepository.duplicateVerifyAtEdit(buname, id);
//                else countEdit = businessUnitRepository.duplicateVerifyAtEdit(buname, id, mvnoIds);
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
//    //Delete Business Unit
//    @Override
//    public boolean deleteVerification(Integer id) throws Exception {
//        boolean flag = false;
//        Integer count = businessUnitRepository.deleteVerify(id);
//        if (count == 0) {
//            flag = true;
//        }
//        return flag;
//    }
//
//    public boolean deleteVerificationForSubBusinessunit(Integer id) throws Exception {
//        boolean flag = false;
//        Integer count = businessUnitRepository.deleteVerifyForSubBusinessunit(id);
//        if (count == 0) {
//            flag = true;
//        }
//        return flag;
//    }
//
//    //Search Business Unit
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getBusinessUnitByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    public GenericDataDTO getBusinessUnitByName(String buname, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            QBusinessUnit qBusinessUnit = QBusinessUnit.businessUnit;
//            Page<BusinessUnit> businessUnitList = null;
//
//            BooleanExpression booleanExpression = qBusinessUnit.isNotNull()
//
//                    .and(qBusinessUnit.buname.likeIgnoreCase("%" + buname + "%")).or(qBusinessUnit.bucode.likeIgnoreCase("%" + buname + "%")).and(qBusinessUnit.isDeleted.eq(false)).or(qBusinessUnit.status.equalsIgnoreCase(buname));
//            if (getMvnoIdFromCurrentStaff() == 1) {
//                //businessUnitList = businessUnitRepository.findAllBybunameContainingIgnoreCaseAndIsDeletedIsFalse(buname, pageRequest);
//                businessUnitList = businessUnitRepository.findAll(booleanExpression, pageRequest);
//            } else {
//                //businessUnitList = businessUnitRepository.findAllBybunameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(buname, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                booleanExpression = booleanExpression.and(qBusinessUnit.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                businessUnitList = businessUnitRepository.findAll(booleanExpression, pageRequest);
//            }
//            if (null != businessUnitList && 0 < businessUnitList.getSize()) {
//                makeGenericResponse(genericDataDTO, businessUnitList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
    public BusinessUnit getById(Long id) {
        return businessUnitRepository.findById(id).get();
    }
//
//
//    public boolean duplicateVerifyAtSaveUcode(String bucode) {
//        // TODO Auto-generated method stub
//        boolean flagforUcode = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (bucode != null) {
//            bucode = bucode.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = businessUnitRepository.duplicateVerifyAtSaveUcode(bucode);
//            else count = businessUnitRepository.duplicateVerifyAtSaveUcode(bucode, mvnoIds);
//            if (count == 0) {
//                flagforUcode = true;
//            }
//        }
//        return flagforUcode;
//    }
//
//    public boolean duplicateVerifyUcodeAtEdit(String bucode, Long id) throws Exception {
//        boolean flagforUcode = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (bucode != null) {
//            bucode = bucode.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = businessUnitRepository.duplicateVerifyAtSaveUcode(bucode);
//            else count = businessUnitRepository.duplicateVerifyAtSaveUcode(bucode, mvnoIds);
//            if (count >= 1) {
//                Integer countEdit;
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    countEdit = businessUnitRepository.duplicateVerifyUcodeAtEdit(bucode, id);
//                else countEdit = businessUnitRepository.duplicateVerifyUcodeAtEdit(bucode, id, mvnoIds);
//                if (countEdit == 1) {
//                    flagforUcode = true;
//                }
//            } else {
//                flagforUcode = true;
//            }
//        }
//        return flagforUcode;
//    }
//
//    public GenericDataDTO getBUFromStaff() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        QBusinessUnit businessUnit = QBusinessUnit.businessUnit;
//        BooleanExpression booleanExpression = businessUnit.isDeleted.eq(false).and(businessUnit.status.equalsIgnoreCase("Active"));
//        if (getBUIdsFromCurrentStaff().size() > 0) {
//            booleanExpression = businessUnit.id.in(getBUIdsFromCurrentStaff());
//        }
//        genericDataDTO.setDataList(IterableUtils.toList(businessUnitRepository.findAll(booleanExpression)));
//        return genericDataDTO;
//    }
//
//
//    public void deleteIcNameBumapping(Long Id) {
//        QIcNameBuMapping qIcNameBuMapping = QIcNameBuMapping.icNameBuMapping;
//        BooleanExpression exp = qIcNameBuMapping.isDeleted.eq(false).and(qIcNameBuMapping.businessUnitid.id.eq(Id));
//        List<IcNameBuMapping> icNameBuMappings = IterableUtils.toList(icNameBuMappingRepo.findAll(exp));
//        for (int i = 0; i < icNameBuMappings.size(); i++) {
//            icNameBuMappings.get(i).setIsDeleted(true);
//            icNameBuMappingRepo.saveAll(icNameBuMappings);
//        }
//    }
//
//    public BusinessUnitDTO convertBumodeltoPojo(Optional<BusinessUnit> businessUnit) {
//        BusinessUnitDTO businessUnitDTO = new BusinessUnitDTO();
//        try {
//            if (businessUnitDTO.getId() != null) {
//                businessUnit.get().setId(businessUnitDTO.getId());
//            }
//
//            businessUnitDTO.setBucode(businessUnit.get().getBucode());
//            businessUnitDTO.setBuname(businessUnit.get().getBuname());
//            businessUnitDTO.setStatus(businessUnit.get().getStatus());
//            return businessUnitDTO;
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//    public GenericDataDTO getBUFromCurrentStaff() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        QBusinessUnit businessUnit = QBusinessUnit.businessUnit;
//
//        if (getBUIdsFromCurrentStaff().size() > 0) {
//            BooleanExpression booleanExpression = businessUnit.isDeleted.eq(false).and(businessUnit.status.equalsIgnoreCase("Active"));
//            booleanExpression = businessUnit.id.in(getBUIdsFromCurrentStaff());
//            genericDataDTO.setDataList(IterableUtils.toList(businessUnitRepository.findAll(booleanExpression)));
//        }
//        return genericDataDTO;
//    }
//
////    public void createPartnerbusinessUnit(Partner partner) {
////        BusinessUnitDTO businessUnit = new BusinessUnitDTO();
////        businessUnit.setBuname(partner.getName());
////        businessUnit.setBucode(partner.getPrcode());
////        businessUnit.setId(Long.valueOf(partner.getId()));
////        businessUnit.setStatus(CommonConstants.ACTIVE_STATUS);
////        businessUnit.setMvnoId(partner.getMvnoId());
////        businessUnit.setIsDeleted(partner.getIsDelete());
////    //    BusinessUnitDTO businessUnitDTO =  mapper.domainToDTO(businessUnit);
////    }
//
public BusinessUnitService(BusinessUnitRepository repository, BusinessUnitMapper mapper) {
    super(repository, mapper);
}
    private static Log log = LogFactory.getLog(BusinessUnitService.class);
    @Override
    public String getModuleNameForLog() {
        return "[BusinessUnitService]";
    }

    @Autowired
    BusinessUnitRepository businessUnitRepository;



    //getting data from common microservice

    @Transactional
    public void saveBusineeUnit(SaveBusinessUnitSharedDataMessage message){
        try {
            BusinessUnit businessUnit = new BusinessUnit();

            businessUnit.setId(message.getId());
            businessUnit.setBuname(message.getBuname());
            businessUnit.setBucode(message.getBucode());
            businessUnit.setStatus(message.getStatus());
            businessUnit.setPlanBindingType(message.getPlanBindingType());
            businessUnit.setIsDeleted(message.getIsDeleted());
            businessUnit.setMvnoId(message.getMvnoId());
            businessUnit.setCreatedById(message.getCreatedById());
            businessUnit.setLastModifiedById(message.getLastModifiedById());
            businessUnit.setCreatedByName(message.getCreatedByName());
            businessUnit.setLastModifiedByName(message.getLastModifiedByName());
            businessUnitRepository.save(businessUnit);
        }catch (Exception e){
            log.error("Unable to create Business Unit with name"+message.getBuname()+""+e.getMessage());
        }
    }

    @Transactional
    public void updateBusinessUnit(UpdateBusinessUnitSharedDataMessage message) {
        try {
            if(message.getId()!=null) {
                BusinessUnit businessUnit = businessUnitRepository.findById(message.getId()).orElse(null);
                if (businessUnit!=null) {
                    businessUnit.setBuname(message.getBuname());
                    businessUnit.setBucode(message.getBucode());
                    businessUnit.setStatus(message.getStatus());
                    businessUnit.setPlanBindingType(message.getPlanBindingType());
                    businessUnit.setIsDeleted(message.getIsDeleted());
                    businessUnit.setMvnoId(message.getMvnoId());
                    businessUnit.setCreatedById(message.getCreatedById());
                    businessUnit.setLastModifiedById(message.getLastModifiedById());
                    businessUnit.setCreatedByName(message.getCreatedByName());
                    businessUnit.setLastModifiedByName(message.getLastModifiedByName());
                    businessUnitRepository.save(businessUnit);
                } else{
//                    log.error("No Data Found");
                    BusinessUnit businessUnit1 = new BusinessUnit();
                    businessUnit1.setId(message.getId());
                    businessUnit1.setBuname(message.getBuname());
                    businessUnit1.setBucode(message.getBucode());
                    businessUnit1.setStatus(message.getStatus());
                    businessUnit1.setPlanBindingType(message.getPlanBindingType());
                    businessUnit1.setIsDeleted(message.getIsDeleted());
                    businessUnit1.setMvnoId(message.getMvnoId());
                    businessUnit1.setCreatedById(message.getCreatedById());
                    businessUnit1.setLastModifiedById(message.getLastModifiedById());
                    businessUnit1.setCreatedByName(message.getCreatedByName());
                    businessUnit1.setLastModifiedByName(message.getLastModifiedByName());
                    businessUnitRepository.save(businessUnit1);
                }
            }
        } catch (Exception e) {
            log.error("Unable to create Business Unit with name"+message.getBuname()+""+e.getMessage());
        }
    }
}
