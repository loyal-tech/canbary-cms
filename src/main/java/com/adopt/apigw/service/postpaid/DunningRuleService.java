package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.BranchServiceAreaMapping;
import com.adopt.apigw.model.postpaid.DunningRule;
import com.adopt.apigw.model.postpaid.DunningRuleAction;
import com.adopt.apigw.model.postpaid.QDunningRule;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.DunningRuleBranchMapping.domain.DunningRuleBranchMapping;
import com.adopt.apigw.modules.DunningRuleBranchMapping.model.DunningRuleBranchMappingPojo;
import com.adopt.apigw.modules.DunningRuleBranchMapping.repository.DunningRuleBranchMappingRepository;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.pojo.api.DunningRuleActionPojo;
import com.adopt.apigw.pojo.api.DunningRulePojo;
import com.adopt.apigw.repository.common.BranchServiceAreaMappingRepository;
import com.adopt.apigw.repository.postpaid.DunningRuleRepository;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.repository.postpaid.PartnerServiceAreaMappingRepo;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DunningRuleService extends AbstractService<DunningRule, DunningRulePojo, Integer> {

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private DunningRuleRepository entityRepository;

    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;

    @Autowired
    private DunningRuleBranchMappingRepository dunningRuleBranchMappingRepository;

    @Autowired
    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private BranchRepository branchRepository;


    @Autowired
    private MvnoRepository  mvnoRepository;


    public static final String MODULE = "[DunningRuleSerprivate com.adopt.apigw.repository.postpaid.DunningRuleActionRepository dunningRuleActionRepository;vice]";

    @Override
    protected JpaRepository<DunningRule, Integer> getRepository() {
        return entityRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(APIController.class);


    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '1')")
    public Page<DunningRule> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        // TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) == 1)
            return entityRepository.searchEntity(searchText, pageRequest);
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.searchEntity(searchText, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '1')")
    public List<DunningRule> getAllActiveEntities() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findByStatus("Y").stream().filter(dunningRule -> dunningRule.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || dunningRule.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '1')")
    public List<DunningRule> getAllEntities() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAll().stream().filter(dunningRule -> dunningRule.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || dunningRule.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '2')")
    public DunningRule getDunningRuleForAdd() {
        return new DunningRule();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '2')")
    public DunningRule getDunningRuleForEdit(Integer id,Integer mvnoId) {
        if(Objects.isNull(mvnoId)) {
            mvnoId=getMvnoIdFromCurrentStaff(null);
        }
        return getEntityForUpdateAndDelete(id,mvnoId);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '2')")
    public DunningRule saveDunningRule(DunningRule dunningRule) {
        // TODO: pass mvnoID manually 6/5/2025
        dunningRule.setMvnoId(getMvnoIdFromCurrentStaff(null));
        for (DunningRuleAction item : dunningRule.getActionList()) {
            item.setDrule(dunningRule);
        }
        // TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) != null) {
            // TODO: pass mvnoID manually 6/5/2025
        	dunningRule.setMvnoId(getMvnoIdFromCurrentStaff(null));
    	}
        if(getLoggedInUser().getLco())
            dunningRule.setLcoId(getLoggedInUser().getPartnerId());
        else
            dunningRule.setLcoId(null);

        DunningRule save = entityRepository.save(dunningRule);
        return save;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '4')")
    public void deleteDunningRule(Integer id,Integer mvnoId) {
        getEntityForUpdateAndDelete(id,mvnoId);
        entityRepository.deleteById(id);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '2')")
    public DunningRulePojo save(DunningRulePojo pojo) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
        pojo.setMvnoId(getMvnoIdFromCurrentStaff(null));
        if(getLoggedInUser().getLco())
            pojo.setLcoId(getLoggedInUser().getPartnerId());
        else
            pojo.setLcoId(null);
        DunningRule obj = convertDunningRulePojoToDunningRuleModel(pojo);
        obj = saveDunningRule(obj);
        if(!Objects.isNull(obj.getId())) {
            List<DunningRuleBranchMapping> dunningRuleBranchMappingList1 = dunningRuleBranchMappingRepository.findAllByByDunningId(obj.getId());
            dunningRuleBranchMappingRepository.deleteAll(dunningRuleBranchMappingList1);
        }
        if(pojo.getBranchIds() != null && !pojo.getBranchIds().isEmpty()){
            List<DunningRuleBranchMapping> dunningRuleBranchMappingList =  new ArrayList<>();
            for(int i =0 ;i<pojo.getBranchIds().size();i++) {
                List<Integer> serviceAreaIds = branchServiceAreaMappingRepository.getAllServiceAreaIdsWithBranchId(pojo.getBranchIds().get(i).intValue());
              for(Integer serviceAreaId:serviceAreaIds) {
                  DunningRuleBranchMapping dunningRuleBranchMapping1 = new DunningRuleBranchMapping();
                  dunningRuleBranchMapping1.setBranchId(pojo.getBranchIds().get(i));
                  dunningRuleBranchMapping1.setDunningRuleId(obj.getId());
                  dunningRuleBranchMapping1.setServiceAreaId(serviceAreaId.longValue());
                  dunningRuleBranchMappingList.add(dunningRuleBranchMapping1);
              }
            }

            dunningRuleBranchMappingRepository.saveAll(dunningRuleBranchMappingList);

        }
        if(pojo.getPartnerIds() != null && !pojo.getPartnerIds().isEmpty()){
            List<DunningRuleBranchMapping> dunningRuleBranchMappingList =  new ArrayList<>();
            for(int i =0 ;i<pojo.getPartnerIds().size();i++) {
                DunningRuleBranchMapping dunningRuleBranchMapping1 = new DunningRuleBranchMapping();
                dunningRuleBranchMapping1.setPartnerId(pojo.getPartnerIds().get(i).longValue());
                dunningRuleBranchMapping1.setDunningRuleId(obj.getId());
                List<Long> serviceAreaIds = partnerServiceAreaMappingRepo.serviceAreaIdWherePartnerIsNotBind(Math.toIntExact(pojo.getPartnerIds().get(i)));
                dunningRuleBranchMapping1.setServiceAreaId(serviceAreaIds.get(0));
                dunningRuleBranchMappingList.add(dunningRuleBranchMapping1);
            }
            dunningRuleBranchMappingRepository.saveAll(dunningRuleBranchMappingList);

        }
        pojo = convertDunningRuleModelToDunningRulePojo(obj);
        return pojo;
    }

    public DunningRule convertDunningRulePojoToDunningRuleModel(DunningRulePojo dunningRulePojo) throws Exception {
        DunningRule dunningRule = null;
        if (dunningRulePojo != null) {
            dunningRule = new DunningRule();
            if (dunningRulePojo.getId() != null) {
                dunningRule.setId(dunningRulePojo.getId());
            }
            dunningRule.setStatus(dunningRulePojo.getStatus());
            dunningRule.setIsGeneratepaymentLink(dunningRulePojo.getIsGeneratepaymentLink());
            dunningRule.setName(dunningRulePojo.getName());
            if(dunningRulePojo.getCcemail() != null) {
                dunningRule.setCcemail(dunningRulePojo.getCcemail());
            }
            if(dunningRulePojo.getMobile() != null){
                dunningRule.setMobile(dunningRulePojo.getMobile());
            }
            dunningRule.setCreditclass(dunningRulePojo.getCreditclass());
            dunningRule.setCustomerType(dunningRulePojo.getCustomerType());
            dunningRule.setDunningType(dunningRulePojo.getDunningType());
            dunningRule.setDunningSector(dunningRulePojo.getDunningSector());
            dunningRule.setDunningSubType(dunningRulePojo.getDunningSubType());
            dunningRule.setDunningSubSector(dunningRulePojo.getDunningSubSector());
            dunningRule.setCustomerPayType(dunningRulePojo.getCustomerPayType());
            dunningRule.setDunningFor(dunningRulePojo.getDunningFor());
            if(dunningRulePojo.getMvnoId() != null) {
            	dunningRule.setMvnoId(dunningRulePojo.getMvnoId());
            }
            if (dunningRulePojo.getDunningRuleActionPojoList() != null && dunningRulePojo.getDunningRuleActionPojoList().size() > 0) {
                List<DunningRuleAction> dunningRuleActionsList = new ArrayList<DunningRuleAction>();
                for (DunningRuleActionPojo actionPojo : dunningRulePojo.getDunningRuleActionPojoList()) {
                    DunningRuleAction dunningRuleAction = new DunningRuleAction();
                    if (actionPojo.getId() != null) {
                        dunningRuleAction.setId(actionPojo.getId());
                    }
                    dunningRuleAction.setAction(actionPojo.getAction());
                    dunningRuleAction.setDays(actionPojo.getDays());
                    if (actionPojo.getDunningRuleId() != null) {
                        dunningRuleAction.setDrule(entityRepository.getOne(actionPojo.getDunningRuleId()));
                    }
                    dunningRuleActionsList.add(dunningRuleAction);
                }
                dunningRule.setActionList(dunningRuleActionsList);
            }
        }
        return dunningRule;
    }

    public DunningRulePojo convertDunningRuleModelToDunningRulePojo(DunningRule dunningRule) throws Exception {
        DunningRulePojo dunningRulePojo = null;
        if (dunningRule != null) {
            dunningRulePojo = new DunningRulePojo();
            if (dunningRule.getId() != null) {
                dunningRulePojo.setId(dunningRule.getId());
            }
            dunningRulePojo.setStatus(dunningRule.getStatus());
            dunningRulePojo.setName(dunningRule.getName());
            if(dunningRule.getCcemail() != null){
                dunningRulePojo.setCcemail(dunningRule.getCcemail());
            }
            if(dunningRule.getMobile() != null){
                dunningRulePojo.setMobile(dunningRule.getMobile());
            }
            dunningRulePojo.setCreditclass(dunningRule.getCreditclass());
            dunningRulePojo.setCustomerType(dunningRule.getCustomerType());
            dunningRulePojo.setDunningType(dunningRule.getDunningType());
            dunningRulePojo.setDunningSector(dunningRule.getDunningSector());
            dunningRulePojo.setCreatedate(dunningRule.getCreatedate());
            dunningRulePojo.setUpdatedate(dunningRule.getUpdatedate());
            dunningRulePojo.setCreatedById(dunningRule.getCreatedById());
            dunningRulePojo.setCreatedByName(dunningRule.getCreatedByName());
            dunningRulePojo.setLastModifiedById(dunningRule.getLastModifiedById());
            dunningRulePojo.setLastModifiedByName(dunningRule.getLastModifiedByName());
            dunningRulePojo.setDunningSector(dunningRule.getDunningSector());
            dunningRulePojo.setDunningSubType(dunningRule.getDunningSubType());
            dunningRulePojo.setDunningSubSector(dunningRule.getDunningSubSector());
            dunningRulePojo.setCustomerPayType(dunningRule.getCustomerPayType());
            dunningRulePojo.setIsGeneratepaymentLink(dunningRule.getIsGeneratepaymentLink());
            dunningRulePojo.setDunningFor(dunningRule.getDunningFor());
            dunningRulePojo.setPartnerIds(dunningRuleBranchMappingRepository.findAllPartnerIdByDunningId(dunningRule.getId()).stream().filter(aLong -> aLong != null).distinct().collect(Collectors.toList()));
            dunningRulePojo.setBranchIds(dunningRuleBranchMappingRepository.findAllBranchIdByDunningId(dunningRule.getId()).stream().filter(aLong -> aLong != null).distinct().collect(Collectors.toList()));
            dunningRulePojo.setServiceAreaIds(dunningRuleBranchMappingRepository.findAllServiceAreaByDunningId(dunningRule.getId()).stream().distinct().collect(Collectors.toList()));
            if(!dunningRulePojo.getPartnerIds().isEmpty()) {
                dunningRulePojo.setPartnerNames(partnerRepository.getAllPartnerNamesByPartnerIds(dunningRulePojo.getPartnerIds().stream().map(aLong -> aLong.intValue()).collect(Collectors.toList())));
            }
            if(!dunningRulePojo.getBranchIds().isEmpty()) {
                dunningRulePojo.setBranchNames(branchRepository.getAllBranchNamesByBranchIds(dunningRulePojo.getBranchIds()));
            }
            if(dunningRule.getMvnoId() != null) {
            	dunningRulePojo.setMvnoId(dunningRule.getMvnoId());
            }
            if (dunningRule.getActionList() != null && dunningRule.getActionList().size() > 0) {
                List<DunningRuleActionPojo> dunningRuleActionsPojoList = new ArrayList<DunningRuleActionPojo>();

                for (DunningRuleAction dunningRuleAction : dunningRule.getActionList()) {
                    DunningRuleActionPojo actionPojo = new DunningRuleActionPojo();
                    if (dunningRuleAction.getId() != null) {
                        actionPojo.setId(dunningRuleAction.getId());
                    }
                    actionPojo.setAction(dunningRuleAction.getAction());
                    actionPojo.setDays(dunningRuleAction.getDays());
                    if (dunningRuleAction.getDrule() != null) {
                        actionPojo.setDunningRuleId(dunningRuleAction.getDrule().getId());
                    }
                    dunningRuleActionsPojoList.add(actionPojo);
                }
                dunningRulePojo.setDunningRuleActionPojoList(dunningRuleActionsPojoList);
            }
        }
        return dunningRulePojo;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '1')")
    public List<DunningRulePojo> convertResponseModelIntoPojo(List<DunningRule> dunningRuleList) throws Exception {
        List<DunningRulePojo> pojoListRes = new ArrayList<DunningRulePojo>();
        if (dunningRuleList != null && dunningRuleList.size() > 0) {
            for (DunningRule dunningRule : dunningRuleList) {
                pojoListRes.add(convertDunningRuleModelToDunningRulePojo(dunningRule));
            }
        }
        return pojoListRes;
    }

    public Page<DunningRule> getDunningList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        Page<DunningRule> dunningRules = null;
        pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
        Integer lcoId=null;
        if(getLoggedInUser().getLco()) {
            lcoId = getLoggedInUser().getPartnerId();
            if (null == filterList || 0 == filterList.size()) {
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1){
                    dunningRules= entityRepository.findAll(pageRequest,lcoId);
                    dunningRules = setMvnoName(dunningRules);
                }
                // TODO: pass mvnoID manually 6/5/2025
                dunningRules= entityRepository.findAll(pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(null), 1),lcoId);
                dunningRules = setMvnoName(dunningRules);
                return dunningRules;
            }
            else {
                return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,getMvnoIdFromCurrentStaff(null));
            }
        }
        else
        {
            if (null == filterList || 0 == filterList.size()) {
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1){
                    dunningRules = entityRepository.findAll(pageRequest);
                    dunningRules = setMvnoName(dunningRules);
                    return dunningRules;
                }
                // TODO: pass mvnoID manually 6/5/2025
                dunningRules = entityRepository.findAll(pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                dunningRules = setMvnoName(dunningRules);
                return dunningRules;
            }
            else {
                return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,getMvnoIdFromCurrentStaff(null));
            }
        }

        //return null;
    }
    @Override
    public Page<DunningRule> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        Page<DunningRule> dunningRules = null;
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        dunningRules = getDunningRuleByNameOrCreditClass(searchModel.getFilterValue(), pageRequest);
                        for(DunningRule  dunningRule : dunningRules){
                            dunningRule.setMvnoName(mvnoRepository.findMvnoNameById(dunningRule.getMvnoId().longValue()));
                        }
                        return dunningRules;
                    }
                } else
                    throw new RuntimeException("Please Provide Search Column!");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public Page<DunningRule> getDunningRuleByNameOrCreditClass(String s1, PageRequest pageRequest) {
        if(getLoggedInUser().getLco())
        {
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1)
                return entityRepository.findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, pageRequest,getLoggedInUser().getPartnerId());
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1),getLoggedInUser().getPartnerId());
        }
        else
        {
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1)
                return entityRepository.findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, pageRequest);
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        }
    }

    public void validateRequest(DunningRulePojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getDunningRuleActionPojoList() == null || pojo.getDunningRuleActionPojoList().size() == 0) {
                throw new CustomValidationException(APIConstants.FAIL, "Dunning rule action list is required", null);
            }
        }
        if (!(pojo.getStatus().equalsIgnoreCase("Y") || pojo.getStatus().equalsIgnoreCase("N"))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Dunning Rule");
        List<DunningRulePojo> dunningRulePojoList =  new ArrayList<>();
        List<DunningRule> dunningRuleList = entityRepository.findAll();
        for(DunningRule dunningRule : dunningRuleList)
            dunningRulePojoList.add(convertDunningRuleModelToDunningRulePojo(dunningRule));
        createExcel(workbook, sheet, DunningRulePojo.class, dunningRulePojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<DunningRulePojo> dunningRulePojoList =  new ArrayList<>();
        List<DunningRule> dunningRuleList = entityRepository.findAll();
        for(DunningRule dunningRule : dunningRuleList)
            dunningRulePojoList.add(convertDunningRuleModelToDunningRulePojo(dunningRule));
        createPDF(doc, DunningRulePojo.class, dunningRulePojoList, null);
    }

    @Override
    public DunningRule get(Integer id,Integer mvnoId) {
        DunningRule dunningRule = super.get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (dunningRule != null && (mvnoId == 1 || (dunningRule.getMvnoId() == mvnoId || dunningRule.getMvnoId() == 1)))
            return dunningRule;
        return null;
    }

    public DunningRule getEntityForUpdateAndDelete(Integer id,Integer mvnoId) {
        DunningRule dunningRule = get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if(dunningRule == null || !(mvnoId == 1 || mvnoId.intValue() == dunningRule.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return dunningRule;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
                // TODO: pass mvnoID manually 6/5/2025
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
                // TODO: pass mvnoID manually 6/5/2025
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public Page<DunningRule> setMvnoName(Page<DunningRule> dunningRules){
        for (DunningRule dunningRule : dunningRules){
            dunningRule.setMvnoName(mvnoRepository.findMvnoNameById(dunningRule.getMvnoId().longValue()));
        }
        return dunningRules;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
                // TODO: pass mvnoID manually 6/5/2025
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(mvnoId == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(mvnoId, 1));
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
    public Page<DunningRule> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if (null == filterList || 0 == filterList.size()) {
            if(mvnoId!=1){
            return  setMvnoName(entityRepository.findAll(pageRequest,Arrays.asList(1,mvnoId)));
            }
            return setMvnoName(getRepository().findAll(pageRequest));
        }
        else {
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder, mvnoId);
        }
    }
}
