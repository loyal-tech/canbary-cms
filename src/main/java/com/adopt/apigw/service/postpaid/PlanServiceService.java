package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.PlanServiceInventoryMapper;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.BusinessUnit.service.BusinessUnitService;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategory;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategoryRepository;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategoryService;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.ServiceParameterMapping.domain.QServiceParamMapping;
import com.adopt.apigw.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import com.adopt.apigw.modules.ServiceParameterMapping.mapper.ServiceParamMappingMapper;
import com.adopt.apigw.modules.ServiceParameterMapping.repository.ServiceParamMappingRepository;
import com.adopt.apigw.modules.ServiceParameters.repository.ServcieParametersRepository;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.pojo.api.PlanPojo;
import com.adopt.apigw.pojo.api.PlanServiceCustomDto;
import com.adopt.apigw.pojo.api.ServiceParamMappingDto;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.PlanServiceForIntegrationMessage;
import com.adopt.apigw.rabbitMq.message.PlanServiceMessage;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.PlanServiceInventoryRepository;
import com.adopt.apigw.repository.postpaid.PlanServiceRepository;
import com.adopt.apigw.repository.postpaid.PostPaidPlanServiceAreaMappingRepo;
import com.adopt.apigw.repository.postpaid.ServiceChargeMappingRepository;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanServiceService extends AbstractService<PlanService, PlanPojo, Integer> {

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private CommitPropertiesProvider commitPropertiesProvider;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private BusinessUnitService businessUnitService;

    @Autowired
    private PlanServiceRepository entityRepository;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private PlanServiceInventoryRepository planServiceInventoryRepository;

    @Autowired
    private PlanServiceInventoryMapper planServiceInventoryMapper;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductCategoryService productCategoryService;

     @Autowired
     private ServiceChargeMappingRepository serviceChargeMappingRepository;
    @Autowired
    MessageSender messageSender;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    private PlanServiceRepository planServiceRepository;

     @Autowired
     private ServcieParametersRepository servcieParametersRepository;

     @Autowired
     private ServiceParamMappingRepository serviceParamMappings;

     @Autowired
     private ServiceParamMappingMapper serviceParamMappingMapper;

    @Autowired
    private ServiceParamMappingRepository serviceParamMappingRepository;

    @Autowired
    private PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepo;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CacheService cacheService;
    @Autowired
    private ServiceRepository serviceRepository;

    public String getModuleNameForLog() {
        return "[PlanServiceService]";
    }

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<PlanService, Integer> getRepository() {
        return entityRepository;
    }


    public Page<PlanService> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.searchEntity(searchText, pageRequest, getMvnoIdFromCurrentStaff(null));
    }

    @Override
    public Page<PlanService> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if (filterList == null || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAll(pageRequest, Arrays.asList(mvnoId, 1));
            else
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAll(pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
    }

    public List<PlanService> getAllServices() {
        List<PlanService> planServiceList = new ArrayList<>();
        QPlanService service = QPlanService.planService;
        BooleanExpression expression = service.isNotNull();
        expression = expression.and(service.isDeleted.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        if(getLoggedInMvnoId(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            expression = expression.and(service.mvnoId.in(getLoggedInMvnoId(null), 1));
        }
        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
            expression = expression.and(service.buId.in(getBUIdsFromCurrentStaff()));
        }
        planServiceList = (List<PlanService>) entityRepository.findAll(expression);
        return planServiceList;
    }


    public List<PlanServiceCustomDto> getCustomPlanServiceList(Integer mvnoId) {
        // TODO: pass mvnoID manually 6/5/2025
//        Integer mvnoId   = getLoggedInMvnoId(null);
        List<Integer> mvnoIds = (mvnoId != 1)
                ? Arrays.asList(mvnoId, 1)
                : Collections.singletonList(1);
        List<Long> buIds = getBUIdsFromCurrentStaff();

        List<Object[]> rows;
        if(buIds!= null && buIds.size()!=0){
            rows = entityRepository.fetchFlatPlanServiceData(
                    mvnoId, mvnoIds, buIds);
        }else {
            rows = entityRepository.fetchFlatPlanServiceData(
                    mvnoId, mvnoIds, null);
        }

        Map<Integer, PlanServiceCustomDto> dtoMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Integer    pId         = (Integer) row[0];
            String     name        = (String)  row[1];
            String     displayName = (String)  row[2];
            Integer    pMvnoId     = (Integer) row[3];

            Long       spmId       = (Long)    row[4];
            Long       serviceId   = (Long)    row[5];
            String     paramName   = (String)  row[6];
            Long       paramId     = (Long)    row[7];
            String     mvnoName    = (String)  row[8];

            // get or create the parent DTO
            PlanServiceCustomDto parentDto = dtoMap.get(pId);
            if (parentDto == null) {
                parentDto = new PlanServiceCustomDto();
                parentDto.setId(pId);
                parentDto.setName(name);
                parentDto.setDisplayName(displayName);
                parentDto.setDisplayId(pId);
                parentDto.setMvnoId(pMvnoId);
                parentDto.setMvnoName(mvnoName);
                parentDto.setServiceParamMappingList(new ArrayList<>());
                dtoMap.put(pId, parentDto);
            }

            if (spmId != null) {
                ServiceParamMappingDto child = new ServiceParamMappingDto();
                child.setId(spmId);
                child.setServiceid(serviceId);
                child.setServiceParamName(paramName);
                child.setServiceParamId(paramId);

                parentDto.getServiceParamMappingList().add(child);
            }
        }

        return new ArrayList<>(dtoMap.values());
    }


    @Transactional
    public void deletePlan(Integer id) throws Exception {
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.serviceId.eq(id));
        if (IterableUtils.toList(postpaidPlanService.getRepository().findAll(booleanExpression)).size() > 0) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, messagesProperty.get("api.plan.service.deleted.not.allowed"), null);
        } else {
               QServiceChargeMapping qServiceChargeMapping= QServiceChargeMapping.serviceChargeMapping;
               BooleanExpression booleanExpression1 = qServiceChargeMapping.isNotNull().and(qServiceChargeMapping.services.id.eq(id.longValue()));
               if (IterableUtils.toList(serviceChargeMappingRepository.findAll(booleanExpression1)).size() > 0)
               {
                   throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, messagesProperty.get("api.plan.service.deleted.not.allowed"), null);
               }
               else{
                   entityRepository.deleteById(id);
               }

        }
    }
    @Transactional
    public PlanService savePlanService(PlanService planService) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
        if (planService.getMvnoId() != null) {
            // TODO: pass mvnoID manually 6/5/2025
            planService.setMvnoId(planService.getMvnoId());
            // TODO: pass mvnoID manually 6/5/2025
            planService.setMvnoName(mvnoRepository.findMvnoNameById(planService.getMvnoId().longValue()));
        }
        PlanService save = entityRepository.save(planService);
        String cacheKey = cacheKeys.PLAN_SERVICES + save.getId();
        cacheService.saveOrUpdateInCacheAsync(save,cacheKey);
        PlanServiceMessage planServiceMessage = new PlanServiceMessage(save);
        //messageSender.send(planServiceMessage, RabbitMqConstants.QUEUE_PLAN_SERVICE_SUCCESS,RabbitMqConstants.QUEUE_PLAN_SERVICE_KPI);
        PlanServiceForIntegrationMessage message = new PlanServiceForIntegrationMessage(save);
        //messageSender.send(message ,RabbitMqConstants.QUEUE_SERVICE_FOR_INTEGRATION);
        return save;
    }

    public PlanService getPlanServiceForAdd() {
        return new PlanService();
    }

    public PlanService getPlanServiceForEdit(Integer id) {
        return entityRepository.getOne(id);
    }

    @Transactional
    public PlanPojo save(PlanPojo pojo,Integer mvnoId) throws Exception {
        PlanService oldObj = null;
        if (pojo.getId() != null) {
            oldObj = planServiceRepository.findById(pojo.getId()).get();
        }
        // TODO: pass mvnoID manually 6/5/2025
        pojo.setMvnoId(mvnoId);
        PlanService obj = convertPlanServicePojoToPlanServiceModel(pojo);
        if (getBUIdsFromCurrentStaff().size() == 1)
            obj.setBuId(getBUIdsFromCurrentStaff().get(0));
        if(pojo.getPcategoryId() != null){
            obj = getProductInventoryMappingId(pojo, obj);
        }
        if(oldObj!=null) {
            log.info("Planservice update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
        }
        obj = savePlanService(obj);
        commitPropertiesProvider.provideForCommittedObject(staffUserRepository.findById(getLoggedInUserId()).get());
        pojo = convertPlanServiceModelToPlanServicePojo(obj);
        String cacheKey = cacheKeys.PLANSERVICE + obj.getId();
        cacheService.putInCache(cacheKey, obj);
        return pojo;
    }

    private PlanService getProductInventoryMappingId(PlanPojo pojo, PlanService planService) {
           PlanServiceInventoryMapping planServiceInventoryMapping =new PlanServiceInventoryMapping();
           List<Long> productCategoryList = pojo.getPcategoryId();
           List<ProductCategory> productCategories = productCategoryRepository.findAllById(productCategoryList);
           if(productCategories != null){
               planService.setProductCategories(productCategories);
           }
           return planService;
    }

    public PlanService convertPlanServicePojoToPlanServiceModel(PlanPojo pojo) throws Exception {
        PlanService planService = null;
        if (pojo != null) {
            planService = new PlanService();
            if (pojo.getId() != null) {
                planService.setId(pojo.getId());
            }
            if(pojo.getDisplayName() != null) {
                planService.setDisplayName(pojo.getDisplayName());
            } else {
                planService.setDisplayName(pojo.getName());
            }
            planService.setName(pojo.getName());
            planService.setIcname(pojo.getIcname());
            planService.setIccode(pojo.getIccode());
            planService.setExpiry(pojo.getExpiry());
            planService.setLedgerId(pojo.getLedgerId());
            planService.setIs_dtv(pojo.getis_dtv());
            planService.setInvestmentid(pojo.getInvestmentid());
            planService.setServiceParamMappingList(serviceParamMappingMapper.dtoToDomain(pojo.getServiceParamMappingList(), new CycleAvoidingMappingContext()));
            List<Long> serviceParmIds = planService.getServiceParamMappingList().stream().map(ServiceParamMapping::getServiceParamId).collect(Collectors.toList());
            if (serviceParmIds.contains(1L)) {
                planService.setIsQoSV(true);
            } else if (!serviceParmIds.contains(1L)) {
                planService.setIsQoSV(false);
            }
            planService.setFeasibility(pojo.getFeasibility());
            planService.setInstallation(pojo.getInstallation());
            planService.setIsPriceEditable(pojo.getIsPriceEditable());
            planService.setPoc(pojo.getPoc());
            planService.setProvisioning(pojo.getProvisioning());
            planService.setCreatedById(pojo.getCreatedById());
            planService.setLastModifiedById(pojo.getLastModifiedById());
            planService.setIsServiceThroughLead(pojo.getIsServiceThroughLead());
            planService.setMvnoId(pojo.getMvnoId());

            return planService;
        }
        return planService;
    }



    public PlanPojo convertPlanServiceModelToPlanServicePojo(PlanService planService) throws Exception {
        PlanPojo planPojo = null;
        List<Long> longs = new ArrayList<>();
        if (planService != null) {
            planPojo = new PlanPojo();
            planPojo.setId(planService.getId());
            planPojo.setName(planService.getName());
            planPojo.setIcname(planService.getIcname());
            planPojo.setIccode(planService.getIccode());
            planPojo.setIsQoSV(planService.getIsQoSV());
            planPojo.setUpdatedate(planService.getUpdatedate());
            planPojo.setCreatedate(planService.getCreatedate());
            planPojo.setCreatedById(planService.getCreatedById());
            planPojo.setCreatedByName(planService.getCreatedByName());
            planPojo.setLastModifiedById(planService.getLastModifiedById());
            planPojo.setLastModifiedByName(planService.getLastModifiedByName());
            planPojo.setExpiry(planService.getExpiry());
            planPojo.setLedgerId(planService.getLedgerId());
            planPojo.setis_dtv(planService.getIs_dtv());
            planPojo.setInvestmentid(planService.getInvestmentid());
            planPojo.setProductCategory(planService.getProductCategories());
            planPojo.setPcategoryId(planPojo.getProductCategory().stream().map(x->x.getId()).collect(Collectors.toList()));
            planPojo.setDisplayId(planService.getId());
            if(planService.getDisplayName() != null)
                planPojo.setDisplayName(planService.getDisplayName());
            else
                planPojo.setDisplayName(planService.getName());
            planPojo.setFeasibility(planService.getFeasibility());
            planPojo.setInstallation(planService.getInstallation());
            planPojo.setPoc(planService.getPoc());
            planPojo.setProvisioning(planService.getProvisioning());
            planPojo.setIsPriceEditable(planService.getIsPriceEditable());
            planPojo.setIsServiceThroughLead(planService.getIsServiceThroughLead());
            planPojo.setMvnoName(planService.getMvnoName());
            planPojo.setMvnoId(planService.getMvnoId());

//            for (int i=0;i<planService.getServiceParamMappings().size();i++) {
//                longs.add(planService.getServiceParamMappings().get(i).getId());
//            }
            planPojo.setServiceParamMappingList(serviceParamMappingMapper.domainToDTO(planService.getServiceParamMappingList(), new CycleAvoidingMappingContext()));
        }
        return planPojo;
    }

    public List<PlanPojo> convertResponseModelIntoPojo(List<PlanService> planServiceList) throws Exception {
        List<PlanPojo> pojoListRes = new ArrayList<PlanPojo>();
        if (planServiceList != null && planServiceList.size() > 0) {
            pojoListRes.addAll(planServiceList.stream().map(planService -> {
                try {
                    return convertPlanServiceModelToPlanServicePojo(planService);
                }catch (Exception e){
                    ApplicationLogger.logger.error("convertResponseModelIntoPojo" + e.getMessage(), e);
                    throw new RuntimeException();
                }
            }).collect(Collectors.toList()));
//            for (PlanService planService : planServiceList) {
//                pojoListRes.add(convertPlanServiceModelToPlanServicePojo(planService));
//            }
        }
        return pojoListRes;
    }

    public void validateRequest(PlanPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Plan Service");
        List<PlanPojo> planPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, PlanPojo.class, planPojoList, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                PlanPojo.class.getDeclaredField("id"),
                PlanPojo.class.getDeclaredField("name"),
        };
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<PlanPojo> planPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, PlanPojo.class, planPojoList, getFields());
    }

     @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
         // TODO: pass mvnoID manually 6/5/2025
        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().isEmpty())
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }


    @Override
    public boolean duplicateVerifyAtSave(String name,Integer mvnoId) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
//        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().isEmpty())
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
         // TODO: pass mvnoID manually 6/5/2025
        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().isEmpty())
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name,Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name,  getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff().isEmpty())
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name,  id, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
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
    public boolean duplicateVerifyAtEdit(String name, Integer id,Integer mvnoId) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
//        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().isEmpty())
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name,Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name,  mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (mvnoId == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff().isEmpty())
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(mvnoId, 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name,  id,mvnoId, getBUIdsFromCurrentStaff());
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


    /*  @Override
    public PlanService get(Integer id) {
        PlanService planService = super.get(id);
        if (getMvnoIdFromCurrentStaff() == 1 || ((planService.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || planService.getMvnoId() == 1) && (planService.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(planService.getBuId()))))
            return planService;
        return null;
    }*/
  @Override
  public PlanService get(Integer id,Integer mvnoId) {
      String cacheKey = cacheKeys.PLANSERVICE + id;

      try {
          PlanService planService = (PlanService) cacheService.getFromCache(cacheKey, PlanService.class);

          if (planService == null) {
              planService = super.get(id,mvnoId);

              if (planService == null) {
                  return null;
              }

              // Cache the retrieved PlanService
              cacheService.putInCache(cacheKey, planService);
          }

          // Perform the condition checks before returning
          // TODO: pass mvnoID manually 6/5/2025
          if (mvnoId== 1 ||
                  ((planService.getMvnoId() == mvnoId || planService.getMvnoId() == 1) &&
                          (planService.getMvnoId() == 1 || getBUIdsFromCurrentStaff().isEmpty() || getBUIdsFromCurrentStaff().contains(planService.getBuId())))) {
              return planService;
          }
      } catch (Exception e) {
          e.printStackTrace();
      }

      return null;
  }

    public PlanService getEntityForUpdateAndDelete(Integer id,Integer mvnoId) {
        PlanService planService = planServiceRepository.findById(id).get();
        // TODO: pass mvnoID manually 6/5/2025
        if (planService == null || (!(mvnoId == 1 || mvnoId.intValue() == planService.getMvnoId().intValue()) && (planService.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(planService.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return planService;
    }

    public boolean duplicateVerifyAtSaveICCode(String iccode, Integer mvnoId) {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
//        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
       if(iccode!=null && !iccode.trim().isEmpty()){
            iccode = iccode.trim();
            Integer count;
           // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = entityRepository.duplicateVerifyAtSaveICCode(iccode,Arrays.asList(1));
            else {
                if (getBUIdsFromCurrentStaff().isEmpty())
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSaveICCode(iccode, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSaveICCode(iccode, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
       else  {
           flag=true;
        }
        return flag;
    }

    public boolean duplicateVerifyAtSaveICName(String icname, Integer mvnoId) {
        boolean flag = false;

        // TODO: pass mvnoID manually 6/5/2025
//        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        if (icname!=null && !icname.trim().isEmpty()) {
            icname = icname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = entityRepository.duplicateVerifyAtSaveICName(icname,Arrays.asList(1));
            else {
                if (getBUIdsFromCurrentStaff().isEmpty())
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSaveICName(icname, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSaveICName(icname, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        else  {
            flag=true;
        }
        return flag;

    }

    //duplicate verification at edit at ic code
    public boolean duplicateVerifyAtEditICName(String icname, Integer id, Integer mvnoId) {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
//        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
       if (icname != null && !icname.trim().isEmpty() ) {
            icname = icname.trim();
            Integer count;
           // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = entityRepository.duplicateVerifyAtSaveICName(icname);
            else {
                if(getBUIdsFromCurrentStaff().isEmpty())
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSaveICName(icname, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSaveICName(icname, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(mvnoId == 1) countEdit = entityRepository.duplicateVerifyAtEditICName(icname, id);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEditICName(icname, id, Arrays.asList(mvnoId, 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEditICName(icname, id, mvnoId, getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }else{
           flag=true;
       }
        return flag;


    }



    public boolean duplicateVerifyAtEditICCode(String iccode, Integer id,Integer mvnoId) {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
//        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        if (iccode != null && !iccode.trim().isEmpty() ) {
            iccode = iccode.trim();


            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = entityRepository.duplicateVerifyAtSaveICCode(iccode);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSaveICCode(iccode, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSaveICCode(iccode, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(mvnoId == 1) countEdit = entityRepository.duplicateVerifyAtEditICCode(iccode, id);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEditICCode(iccode, id, Arrays.asList(mvnoId, 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEditICCode(iccode, id, mvnoId, getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        else {
            flag=true;
        }
        return flag;
    }

    public List<PlanService> getAllServicesforIPCharge(){
        QPlanService qPlanService = QPlanService.planService;
        QServiceParamMapping qServiceParamMapping = QServiceParamMapping.serviceParamMapping;
        BooleanExpression booleanExpression = qServiceParamMapping.isNotNull().and(qServiceParamMapping.serviceParamId.eq(1L).and(qServiceParamMapping.serviceid.isNotNull()));
        List<ServiceParamMapping> serviceParamMappingList = IterableUtils.toList(serviceParamMappingRepository.findAll(booleanExpression));
        List<Long> serviceIdList = serviceParamMappingList.stream().map(ServiceParamMapping::getServiceid).collect(Collectors.toList());
        List<Integer> intIds =  serviceIdList.stream().map(Long::intValue)
                .collect(Collectors.toList());
        BooleanExpression expression = qPlanService.isNotNull().and(qPlanService.id.in(intIds));
        // TODO: pass mvnoID manually 6/5/2025
        List<PlanService> planServices = IterableUtils.toList(entityRepository.findAll(expression)).stream().filter(service -> (service.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || service.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (service.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(service.getBuId()))).collect(Collectors.toList());
        return planServices;
    }

    public void sendCreatedDataShared(PlanPojo pojo, Integer operation) throws Exception {
        try {
            PlanService planServiceEntity = convertPlanServicePojoToPlanServiceModel(pojo);
            if(pojo.getPcategoryId() != null) {
                List<ProductCategory> productCategories = new ArrayList<>();
                for (Long pcId : pojo.getPcategoryId()) {
                    ProductCategory productCategory = new ProductCategory();
                    productCategory.setId(pcId);
                    productCategories.add(productCategory);
                }
                planServiceEntity.setProductCategories(productCategories);
            }
            // TODO: pass mvnoID manually 6/5/2025
            planServiceEntity.setMvnoId(pojo.getMvnoId());
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                planServiceEntity.setIsDeleted(false);
                createDataSharedService.sendEntitySaveDataForAllMicroService(planServiceEntity);
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                planServiceEntity.setIsDeleted(false);
                createDataSharedService.updateEntityDataForAllMicroService(planServiceEntity);
            } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
                planServiceEntity.setIsDeleted(true);
                createDataSharedService.deleteEntityDataForAllMicroService(planServiceEntity);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    public Services getServices(Integer id, Integer mvnoId) {
        String cacheKey = cacheKeys.SERVICES + id;

        try {
            // Check if Service exists in cache
            Services service = (Services) cacheService.getFromCache(cacheKey, Services.class);

            if (service == null) {
                Optional<Services> optionalService =  serviceRepository.findById(id.longValue());

                if (!optionalService.isPresent()) {
                    return null;
                }

                service = optionalService.get();

                // Cache the retrieved Service
                cacheService.putInCache(cacheKey, service);
            }
// TODO: pass mvnoID manually 6/5/2025
            // Perform the condition checks before returning
            if (mvnoId == 1 ||
                    ((service.getMvnoId() == mvnoId || service.getMvnoId() == 1) &&
                            (service.getMvnoId() == mvnoId || getBUIdsFromCurrentStaff().isEmpty()))) {
                return service;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public GenericDataDTO serviceSearch(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [serviceSearch()] ";
        try {
            QPlanService qPlanService = QPlanService.planService;
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            BooleanExpression booleanExpression = qPlanService.isNotNull().and(qPlanService.isDeleted.eq(false));
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (filterList.size() > 0) {
                for (GenericSearchModel genericSearchModel : filterList) {
                    booleanExpression = booleanExpression.and(qPlanService.name.containsIgnoreCase(genericSearchModel.getFilterValue()));
                }
            }
            if (mvnoId != 1)
                booleanExpression = booleanExpression.and(qPlanService.mvnoId.in(1, mvnoId));
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                booleanExpression = booleanExpression.and(qPlanService.mvnoId.eq(1).or(qPlanService.mvnoId.eq(mvnoId).and(qPlanService.buId.in(getBUIdsFromCurrentStaff()))));
            }
            Page<PlanService> services = entityRepository.findAll(booleanExpression, pageRequest);
            genericDataDTO.setDataList(services.getContent());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setTotalRecords(services.getTotalElements());
            genericDataDTO.setPageRecords(services.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(services.getNumber() + 1);
            genericDataDTO.setTotalPages(services.getTotalPages());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public Page<PlanServiceCustomDto> getCustomPlanServiceList(Integer mvnoId, Pageable pageable) {
        List<Integer> mvnoIds = (mvnoId != 1)
                ? Arrays.asList(mvnoId, 1)
                : Collections.singletonList(1);
        List<Long> buIds = getBUIdsFromCurrentStaff();

        List<Object[]> rows;
        if(buIds!= null && buIds.size()!=0){
            rows = entityRepository.fetchFlatPlanServiceData(mvnoId, mvnoIds, buIds);
        } else {
            rows = entityRepository.fetchFlatPlanServiceData(mvnoId, mvnoIds, null);
        }

        Map<Integer, PlanServiceCustomDto> dtoMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Integer    pId         = (Integer) row[0];
            String     name        = (String)  row[1];
            String     displayName = (String)  row[2];
            Integer    pMvnoId     = (Integer) row[3];
            Long       spmId       = (Long)    row[4];
            Long       serviceId   = (Long)    row[5];
            String     paramName   = (String)  row[6];
            Long       paramId     = (Long)    row[7];
            String     mvnoName    = (String)  row[8];

            PlanServiceCustomDto parentDto = dtoMap.get(pId);
            if (parentDto == null) {
                parentDto = new PlanServiceCustomDto();
                parentDto.setId(pId);
                parentDto.setName(name);
                parentDto.setDisplayName(displayName);
                parentDto.setDisplayId(pId);
                parentDto.setMvnoId(pMvnoId);
                parentDto.setMvnoName(mvnoName);
                parentDto.setServiceParamMappingList(new ArrayList<>());
                dtoMap.put(pId, parentDto);
            }

            if (spmId != null) {
                ServiceParamMappingDto child = new ServiceParamMappingDto();
                child.setId(spmId);
                child.setServiceid(serviceId);
                child.setServiceParamName(paramName);
                child.setServiceParamId(paramId);
                parentDto.getServiceParamMappingList().add(child);
            }
        }

        List<PlanServiceCustomDto> dtoList = new ArrayList<>(dtoMap.values())
                .stream()
                .sorted(Comparator.comparing(PlanServiceCustomDto::getId).reversed())
                .collect(Collectors.toList());


        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());
        List<PlanServiceCustomDto> pagedList = dtoList.subList(start, end);

        return new PageImpl<>(pagedList, pageable, dtoList.size());
    }


}
