package com.adopt.apigw.modules.linkacceptance.service;

import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.modules.linkacceptance.domain.LinkAcceptance;
import com.adopt.apigw.modules.linkacceptance.mapper.LinkAcceptanceMapper;
import com.adopt.apigw.modules.linkacceptance.model.LinkAcceptanceDTO;
import com.adopt.apigw.modules.linkacceptance.repository.LinkAcceptanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkAcceptanceService  extends ExBaseAbstractService2<LinkAcceptanceDTO, LinkAcceptance, Long> {


    public LinkAcceptanceService(LinkAcceptanceRepository repository, LinkAcceptanceMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return  "[LinkAcceptanceService]";
    }

    @Autowired
    LinkAcceptanceMapper linkAcceptanceMapper;

    @Autowired
    LinkAcceptanceRepository linkAcceptanceRepository;

    @Override
    public LinkAcceptanceDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }

   /* @Override
    public boolean duplicateVerifyAtSave(String circuitname) throws Exception {
        boolean flag = false;
        List mvnoId = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (circuitname != null) {
            circuitname = circuitname.trim();
            Integer count;
           if (getMvnoIdFromCurrentStaff() == 1) count = linkAcceptanceRepository.duplicateVerifyAtSave(circuitname);
            else count = linkAcceptanceRepository.duplicateVerifyAtSave(circuitname, mvnoId);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateVerifyAtEdit(String circuitname, Long id) throws Exception {
        boolean flag = false;
        List mvnoId = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (circuitname != null) {
            circuitname = circuitname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) {
                count = linkAcceptanceRepository.duplicateVerifyAtSaveWithName(circuitname);
            } else {
                count = linkAcceptanceRepository.duplicateVerifyAtSaveWithName(circuitname, mvnoId);
            }
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = linkAcceptanceRepository.duplicateVerifyAtEdit(circuitname, id);
                else countEdit = linkAcceptanceRepository.duplicateVerifyAtEdit(circuitname, id, mvnoId);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }


    public LinkAcceptanceDTO getEntityForUpdateAndDelete(Long id) throws Exception {

        LinkAcceptanceDTO linkAcceptanceDTO = linkAcceptanceMapper.domainToDTO(linkAcceptanceRepository.findById(id).get(), new CycleAvoidingMappingContext());
        if(getMvnoIdFromCurrentStaff() != null) {
            linkAcceptanceDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        if(linkAcceptanceDTO == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == linkAcceptanceDTO.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return linkAcceptanceDTO;
    }
*/
    /*public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getLinkAcceptanceByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }*/

    /*public GenericDataDTO getLinkAcceptanceByName(String circuitname, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getSubBusinessVerticalByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QSubBusinessVertical qSubBusinessVertical = QSubBusinessVertical.subBusinessVertical;
            BooleanExpression exp = qSubBusinessVertical.isNotNull();
            exp = exp.and(qSubBusinessVertical.sbvname.containsIgnoreCase(circuitname)).and(qSubBusinessVertical.isDeleted.eq(false));
            Page<SubBusinessVertical> subBusinessVertical = null;
            if (getMvnoIdFromCurrentStaff() == 1) {
                subBusinessVertical = linkAcceptanceRepository.findAll(exp, pageRequest);
            } else {
                exp = exp.and(qSubBusinessVertical.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                subBusinessVertical = linkAcceptanceRepository.findAll(exp, pageRequest);
            }
            if (null != subBusinessVertical && 0 < subBusinessVertical.getSize()) {
                makeGenericResponse(genericDataDTO, subBusinessVertical);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }*/

}
