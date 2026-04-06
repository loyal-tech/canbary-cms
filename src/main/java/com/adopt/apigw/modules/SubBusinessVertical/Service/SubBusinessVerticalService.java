package com.adopt.apigw.modules.SubBusinessVertical.Service;


import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveSubBusinessUnitSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveSubBusinessVerticalsSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateBusinessVerticalSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateSubBusinessVerticalsSharedDataMessage;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.SubBusinessVertical.Domain.QSubBusinessVertical;
import com.adopt.apigw.modules.SubBusinessVertical.Domain.SubBusinessVertical;
import com.adopt.apigw.modules.SubBusinessVertical.Mapper.SubBusinessVerticalMapper;
import com.adopt.apigw.modules.SubBusinessVertical.Model.SubBusinessVerticalDTO;
import com.adopt.apigw.modules.SubBusinessVertical.Repository.SubBusinessVerticalRepository;
import com.adopt.apigw.utils.APIConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class SubBusinessVerticalService  extends ExBaseAbstractService<SubBusinessVerticalDTO, SubBusinessVertical,Long> {


    @Autowired
    SubBusinessVerticalMapper subBusinessVerticalMapper;

    @Autowired
    private SubBusinessVerticalRepository subBusinessVerticalRepository;

    public SubBusinessVerticalService(SubBusinessVerticalRepository repository, SubBusinessVerticalMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
//
//    @Override
//    public boolean duplicateVerifyAtSave(String sbvname) throws Exception {
//        boolean flag = false;
//        List mvnoId = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (sbvname != null) {
//            sbvname = sbvname.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = subBusinessVerticalRepository.duplicateVerifyAtSave(sbvname);
//            else count = subBusinessVerticalRepository.duplicateVerifyAtSave(sbvname, mvnoId);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    public boolean duplicateVerifyAtEdit(String sbvname, Long id) throws Exception {
//        boolean flag = false;
//        List mvnoId = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (sbvname != null) {
//            sbvname = sbvname.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) {
//                count = subBusinessVerticalRepository.duplicateVerifyAtSaveWithName(sbvname);
//            } else {
//                count = subBusinessVerticalRepository.duplicateVerifyAtSaveWithName(sbvname, mvnoId);
//            }
//            if (count >= 1) {
//                Integer countEdit;
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    countEdit = subBusinessVerticalRepository.duplicateVerifyAtEdit(sbvname, id);
//                else countEdit = subBusinessVerticalRepository.duplicateVerifyAtEdit(sbvname, id, mvnoId);
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
//    public SubBusinessVerticalDTO getEntityForUpdateAndDelete(Long id) throws Exception {
//
//        SubBusinessVerticalDTO subBusinessVerticalDTO = subBusinessVerticalMapper.domainToDTO(subBusinessVerticalRepository.findById(id).get(), new CycleAvoidingMappingContext());
//        if(getMvnoIdFromCurrentStaff() != null) {
//            subBusinessVerticalDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        if(subBusinessVerticalDTO == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == subBusinessVerticalDTO.getMvnoId().intValue()))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        return subBusinessVerticalDTO;
//    }
//
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getSubBusinessVerticalByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    public GenericDataDTO getSubBusinessVerticalByName(String sbvname, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getSubBusinessVerticalByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            QSubBusinessVertical qSubBusinessVertical = QSubBusinessVertical.subBusinessVertical;
//            BooleanExpression exp = qSubBusinessVertical.isNotNull();
//            exp = exp.and(qSubBusinessVertical.sbvname.containsIgnoreCase(sbvname)).and(qSubBusinessVertical.isDeleted.eq(false));
//            Page<SubBusinessVertical> subBusinessVertical = null;
//            if (getMvnoIdFromCurrentStaff() == 1) {
//                subBusinessVertical = subBusinessVerticalRepository.findAll(exp, pageRequest);
//            } else {
//                exp = exp.and(qSubBusinessVertical.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                subBusinessVertical = subBusinessVerticalRepository.findAll(exp, pageRequest);
//            }
//            if (null != subBusinessVertical && 0 < subBusinessVertical.getSize()) {
//                makeGenericResponse(genericDataDTO, subBusinessVertical);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }


    /* getting data from the common service */

    public void saveSubBuVertical(SaveSubBusinessVerticalsSharedDataMessage message){
        SubBusinessVertical subBusinessVertical = new SubBusinessVertical();
        subBusinessVertical.setId(message.getId());
        subBusinessVertical.setBusinessVerticalId(message.getBusinessVerticalId());
        subBusinessVertical.setStatus(message.getStatus());
        subBusinessVertical.setSbvname(message.getSbvname());
        subBusinessVertical.setMvnoId(message.getMvnoId());
        subBusinessVertical.setIsDeleted(message.getIsDeleted());
        subBusinessVertical.setCreatedById(message.getCreatedById());
        subBusinessVertical.setLastModifiedById(message.getLastModifiedById());
        subBusinessVertical.setCreatedByName(message.getCreatedByName());
        subBusinessVertical.setLastModifiedByName(message.getLastModifiedByName());
        subBusinessVerticalRepository.save(subBusinessVertical);
    }


    public void updateSubBuVertical(UpdateSubBusinessVerticalsSharedDataMessage message){

        SubBusinessVertical subBusinessVertical = subBusinessVerticalRepository.findById(message.getId()).orElse(null);
        if(subBusinessVertical!=null){
            subBusinessVertical.setBusinessVerticalId(message.getBusinessVerticalId());
            subBusinessVertical.setStatus(message.getStatus());
            subBusinessVertical.setSbvname(message.getSbvname());
            subBusinessVertical.setMvnoId(message.getMvnoId());
            subBusinessVertical.setIsDeleted(message.getIsDeleted());
            subBusinessVertical.setCreatedById(message.getCreatedById());
            subBusinessVertical.setLastModifiedById(message.getLastModifiedById());
            subBusinessVertical.setCreatedByName(message.getCreatedByName());
            subBusinessVertical.setLastModifiedByName(message.getLastModifiedByName());
            subBusinessVerticalRepository.save(subBusinessVertical);
        } else {
            SubBusinessVertical subBusinessVertical1 = new SubBusinessVertical();
            subBusinessVertical1.setId(message.getId());
            subBusinessVertical1.setBusinessVerticalId(message.getBusinessVerticalId());
            subBusinessVertical1.setStatus(message.getStatus());
            subBusinessVertical1.setSbvname(message.getSbvname());
            subBusinessVertical1.setMvnoId(message.getMvnoId());
            subBusinessVertical1.setIsDeleted(message.getIsDeleted());
            subBusinessVertical.setCreatedById(message.getCreatedById());
            subBusinessVertical.setLastModifiedById(message.getLastModifiedById());
            subBusinessVertical.setCreatedByName(message.getCreatedByName());
            subBusinessVertical.setLastModifiedByName(message.getLastModifiedByName());
            subBusinessVerticalRepository.save(subBusinessVertical1);
        }
    }
}
