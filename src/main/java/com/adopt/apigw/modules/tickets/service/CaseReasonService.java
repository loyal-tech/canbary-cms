//package com.adopt.apigw.modules.tickets.service;
//
//import com.adopt.apigw.constants.SearchConstants;
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.dto.GenericSearchModel;
//import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
//import com.adopt.apigw.core.service.ExBaseAbstractService;
//import com.adopt.apigw.core.service.ExBaseAbstractService2;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.modules.ResolutionReasons.domain.ResolutionReasons;
//import com.adopt.apigw.modules.tickets.domain.CaseReason;
//import com.adopt.apigw.modules.tickets.domain.QCaseReason;
//import com.adopt.apigw.modules.tickets.mapper.CaseReasonMapper;
//import com.adopt.apigw.modules.tickets.model.CaseReasonDTO;
//import com.adopt.apigw.modules.tickets.repository.CaseReasonRepository;
//import com.itextpdf.text.Document;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import org.apache.commons.collections4.IterableUtils;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//
//import java.lang.reflect.Field;
//import java.util.Arrays;
//import java.util.List;
//
//@Service
//public class CaseReasonService extends ExBaseAbstractService2<CaseReasonDTO, CaseReason, Long> {
//
//    @Autowired
//    private CaseReasonRepository caseReasonRepository;
//
//    @Autowired
//    private CaseReasonMapper caseReasonMapper;
//
//    public CaseReasonService(CaseReasonRepository repository, CaseReasonMapper mapper) {
//        super(repository, mapper);
//        sortColMap.put("id","reason_id");
//        sortColMap.put("tatConsideration","tat_consideration");
//    }
//
//    public CaseReasonDTO getCaseReasonByReasonName(String reasonName) {
//        CaseReason caseReason = null;
//        if(getMvnoIdFromCurrentStaff() == 1)
//            caseReason = caseReasonRepository.findCaseReasonByName(reasonName);
//        else
//            caseReason = caseReasonRepository.findCaseReasonByNameAndMvnoIdIn(reasonName, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        return caseReasonMapper.domainToDTO(caseReason, new CycleAvoidingMappingContext());
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[CaseReasonService]";
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
//        Sheet sheet = workbook.createSheet("CaseReason");
//        createExcel(workbook, sheet, CaseReasonDTO.class, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{
//                CaseReasonDTO.class.getDeclaredField("reasonId"),
//                CaseReasonDTO.class.getDeclaredField("name"),
//                CaseReasonDTO.class.getDeclaredField("status"),
//                CaseReasonDTO.class.getDeclaredField("tatConsideration"),
//        };
//    }
//
//    @Override
//    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
//        createPDF(doc, CaseReasonDTO.class, getFields());
//    }
//
//    public GenericDataDTO getReasonByName(String name, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getReasonByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            Page<CaseReason> caseReasonList = null;
//            if(getMvnoIdFromCurrentStaff() == 1)
//                caseReasonList = caseReasonRepository.findAllByNameContainingIgnoreCaseOrTatConsiderationContainingIgnoreCaseAndIsDeleteIsFalse(name, name, pageRequest);
//            else
//            if(getBUIdsFromCurrentStaff().size() == 0)
//                caseReasonList = caseReasonRepository.findAllByNameContainingIgnoreCaseOrTatConsiderationContainingIgnoreCaseAndIsDeleteIsFalse(name , name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            else
//                caseReasonList = caseReasonRepository.findAllByNameContainingIgnoreCaseOrTatConsiderationContainingIgnoreCaseAndIsDeleteIsFalse(name , name , pageRequest , Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getBUIdsFromCurrentStaff());
//
//            if (null != caseReasonList && 0 < caseReasonList.getSize()) {
//                makeGenericResponse(genericDataDTO, caseReasonList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList  && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getReasonByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
////	public CaseReason getById(Long caseReasonId) throws Exception {
////		return caseReasonMapper.dtoToDomain(getEntityById(caseReasonId), new CycleAvoidingMappingContext());
////	}
//
//    @Override
//    public boolean duplicateVerifyAtSave(String name) throws Exception {
//        boolean flag = false;
//        Integer mvnoId = getMvnoIdFromCurrentStaff();
//        if (name != null) {
//        	name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = caseReasonRepository.duplicateVerifyAtSave(name);
//            else {
//                if(getBUIdsFromCurrentStaff().size() == 0)
//                    count = caseReasonRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                else
//                    count = caseReasonRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getBUIdsFromCurrentStaff());
//            }
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//
//    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//        	name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = caseReasonRepository.duplicateVerifyAtSave(name);
//            else {
//                if(getBUIdsFromCurrentStaff().size() == 0)
//                    count = caseReasonRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                else
//                    count = caseReasonRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getBUIdsFromCurrentStaff());
//            }
//            if (count >= 1) {
//                QCaseReason qCaseReason = QCaseReason.caseReason;
//                BooleanExpression booleanExpression = qCaseReason.isNotNull().and(qCaseReason.name.eq(name))
//                        .and(qCaseReason.reasonId.eq(id)).and(qCaseReason.isDelete.eq(false));
//                if(getMvnoIdFromCurrentStaff() != 1)
//                    booleanExpression =booleanExpression.and(qCaseReason.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//
//                int countEdit = IterableUtils.toList(caseReasonRepository.findAll(booleanExpression)).size();
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
//    public boolean isSameStaff(String name) throws Exception {
//   	 boolean flag = true;
//        Integer userId = getLoggedInUserId();
//        Integer mvnoId = getMvnoIdFromCurrentStaff();
//        if (name != null) {
//            name = name.trim();
//            Integer createdById = caseReasonRepository.getCreatedBy(name, mvnoId);
//            if(createdById != userId) {
//           	 flag = false;
//            }
//        }
//            return flag;
//   }
//
//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<CaseReason> paginationList = null;
//        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
//        if(getMvnoIdFromCurrentStaff() == 1)
//            paginationList = caseReasonRepository.findAll(pageRequest);
//        else
//        if(null == filterList || 0 == filterList.size())
//            if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
//                paginationList =  caseReasonRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()));
//            else
//                paginationList = caseReasonRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()), getBUIdsFromCurrentStaff());
//
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
//}
