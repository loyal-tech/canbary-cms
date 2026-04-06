package com.adopt.apigw.modules.ResolutionReasons.service;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.ResolutionReasons.domain.QResolutionReasons;
import com.adopt.apigw.modules.tickets.domain.QResoSubCategoryMapping;
import com.adopt.apigw.modules.tickets.domain.ResoSubCategoryMapping;
import com.adopt.apigw.modules.tickets.repository.ResoSubCategoryMappingRepo;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.ResolutionReasons.domain.ResolutionReasons;
import com.adopt.apigw.modules.ResolutionReasons.mapper.ResolutionReasonsMapper;
import com.adopt.apigw.modules.ResolutionReasons.model.ResolutionReasonsDTO;
import com.adopt.apigw.modules.ResolutionReasons.repository.ResolutionReasonsRepository;
import com.itextpdf.text.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ResolutionReasonsService extends ExBaseAbstractService2<ResolutionReasonsDTO, ResolutionReasons,Long> {
    public ResolutionReasonsService(ResolutionReasonsRepository repository, ResolutionReasonsMapper mapper) {
        super(repository, mapper);
    }
    @Autowired
    private ResolutionReasonsRepository resolutionReasonsRepository;
    @Autowired
    private ResoSubCategoryMappingRepo resoSubCategoryMappingRepo;
    @Override
    public String getModuleNameForLog() {
        return "[ResolutionReasons Service]";
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("ResolutionReasons");
        createExcel(workbook, sheet, ResolutionReasonsDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, ResolutionReasonsDTO.class, null,mvnoId);
    }



    @Override
    public boolean duplicateVerifyAtSave(String name,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (name != null) {
        	name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = resolutionReasonsRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = resolutionReasonsRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = resolutionReasonsRepository.duplicateVerifyAtSave(name , getMvnoIdFromCurrentStaff(null) , getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }
    
    
    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        if (name != null) {
        	name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = resolutionReasonsRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = resolutionReasonsRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = resolutionReasonsRepository.duplicateVerifyAtSave(name , getMvnoIdFromCurrentStaff(null) , getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = resolutionReasonsRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = resolutionReasonsRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = resolutionReasonsRepository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public ResolutionReasonsDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        QResolutionReasons qResolutionReasons=QResolutionReasons.resolutionReasons;
        BooleanExpression expression=qResolutionReasons.isNotNull().and(qResolutionReasons.isDeleted.eq(false));
        if(getLoggedInUser().getLco())
            expression=expression.and(qResolutionReasons.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            expression=expression.and(qResolutionReasons.lcoId.isNull());

        Page<ResolutionReasons> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        // TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) == 1)
            paginationList = resolutionReasonsRepository.findAll(expression,pageRequest);
        else {
            if (null == filterList || 0 == filterList.size())
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    expression=expression.and(qResolutionReasons.mvnoId.in(1,getMvnoIdFromCurrentStaff(null)));
                    paginationList = resolutionReasonsRepository.findAll(expression,pageRequest);
                }
                else {
                    // TODO: pass mvnoID manually 6/5/2025
                    expression=expression.and(qResolutionReasons.mvnoId.in(getMvnoIdFromCurrentStaff(null)));
                    expression=expression.and(qResolutionReasons.buId.in(getBUIdsFromCurrentStaff()));
                    paginationList = resolutionReasonsRepository.findAll(expression,pageRequest);
                }
        }


        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    public List<ResolutionReasons> findByStatus()
    {
        List<ResolutionReasons> resolutionReasonsList = new ArrayList<>();
        // TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) == 1)
            resolutionReasonsList = resolutionReasonsRepository.findAllByStatus();
        else
            if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                resolutionReasonsList =  resolutionReasonsRepository.findAllByStatus(Arrays.asList(1, getMvnoIdFromCurrentStaff(null)));
            else
                // TODO: pass mvnoID manually 6/5/2025
                resolutionReasonsList = resolutionReasonsRepository.findAllByStatus(getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());

        return resolutionReasonsList;
    }

    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);

            QResolutionReasons qResolutionReasons = QResolutionReasons.resolutionReasons;

            BooleanExpression booleanExpression = qResolutionReasons.isNotNull().and(qResolutionReasons.isDeleted.eq(false));

            if(getLoggedInUser().getLco())
                booleanExpression=booleanExpression.and(qResolutionReasons.lcoId.eq(getLoggedInUser().getPartnerId()));
            else
                booleanExpression=booleanExpression.and(qResolutionReasons.lcoId.isNull());

            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (getBUIdsFromCurrentStaff().size() != 0)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qResolutionReasons.mvnoId.eq(1).or(qResolutionReasons.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qResolutionReasons.buId.in(getBUIdsFromCurrentStaff()))));
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                    booleanExpression = booleanExpression.and(qResolutionReasons.name.contains(searchModel.getFilterValue()));
                }
            }
            Page<ResolutionReasons> resolutionReasonsPage = resolutionReasonsRepository.findAll(booleanExpression, pageRequest);
            genericDataDTO.setDataList(resolutionReasonsPage.getContent());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(resolutionReasonsPage.getTotalElements());
            genericDataDTO.setPageRecords(resolutionReasonsPage.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(resolutionReasonsPage.getNumber() + 1);
            genericDataDTO.setTotalPages(resolutionReasonsPage.getTotalPages());
            return genericDataDTO;

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
    public List<ResolutionReasons> findByResoReasons(Long id) {
        try{
            QResoSubCategoryMapping qResoSubCategoryMapping = QResoSubCategoryMapping.resoSubCategoryMapping;
            BooleanExpression exp=qResoSubCategoryMapping.isNotNull().and(qResoSubCategoryMapping.subcateId.eq(id));
            List<ResoSubCategoryMapping> resoSubCategoryMappingList= (List<ResoSubCategoryMapping>) resoSubCategoryMappingRepo.findAll(exp);
           List<Long> number=new ArrayList<>();
            if (resoSubCategoryMappingList.size()>0){
                for (ResoSubCategoryMapping ids:resoSubCategoryMappingList){
                    Long num = ids.getResId();
                    number.add(num);
                }
            }
            QResolutionReasons qResolutionReasons = QResolutionReasons.resolutionReasons;
            BooleanExpression exp1 = qResolutionReasons.isNotNull().and(qResolutionReasons.isDeleted.eq(false).and(qResolutionReasons.id.in(number)));
            List<ResolutionReasons> resolutionReasonsList = (List<ResolutionReasons>) resolutionReasonsRepository.findAll(exp1);
            List<ResolutionReasons> resolutionReasons = new ArrayList<>();
            if(resolutionReasonsList.size()>0){
                for(ResolutionReasons reasons : resolutionReasonsList){
                    ResolutionReasons reasons1 = reasons;
                    resolutionReasons.add(reasons1);
                }
            }
                return resolutionReasons;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

