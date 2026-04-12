package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.mapper.postpaid.DiscountMappingMapper;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.QCustomerServiceMapping;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.pojo.api.DiscountMappingPojo;
import com.adopt.apigw.pojo.api.DiscountPlanMappingPojo;
import com.adopt.apigw.pojo.api.DiscountPojo;
import com.adopt.apigw.repository.postpaid.DiscountMappingRepository;
import com.adopt.apigw.repository.postpaid.DiscountPlanMappingRepo;
import com.adopt.apigw.repository.postpaid.DiscountRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.itextpdf.text.Document;

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

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiscountService extends AbstractService<Discount, DiscountPojo, Integer> {

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private DiscountRepository entityRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private DiscountMappingRepository discountMappingRepository;

    @Autowired
    private DiscountPlanMappingRepo discountPlanMappingRepo;
    @Autowired
    private CreateDataSharedService createDataSharedService;

    @Autowired
    private DiscountMappingMapper discountMappingMapper;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    public DiscountService() {
        sortColMap.put("id","discountid");
    }

    public static final String MODULE = "[DiscountService]";

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<Discount, Integer> getRepository() {
        return entityRepository;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Discount', '1')")
    public Page<Discount> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.searchEntity(searchText, pageRequest,getMvnoIdFromCurrentStaff(null));
    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
    public List<Discount> getAllActiveEntities() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findByStatus(CommonConstants.YES_STATUS).stream().filter(discount -> discount.getMvnoId() == getMvnoIdFromCurrentStaff(null) || discount.getMvnoId() == null).collect(Collectors.toList());
    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
    public List<Discount> getAllEntities(Integer mvnoId) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAll().stream().filter(discount -> (discount.getMvnoId() == mvnoId.intValue() || discount.getMvnoId() == 1 || mvnoId == 1)
            && (discount.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(discount.getBuId()))).collect(Collectors.toList());
    }

   // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_DELETE + "\")")
    public void deleteDiscount(Integer id) throws Exception {
        Discount discount = entityRepository.getOne(id);
        discount.setIsDelete(true);
        entityRepository.save(discount);
    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
    public Discount getDiscountForAdd() {
        return new Discount();
    }

 //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
    public Discount getDiscountForEdit(Integer id) throws Exception {
        return entityRepository.getOne(id);
    }

 //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
    public DiscountMapping getDiscountMapping() {
        return new DiscountMapping();
    }

 //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
    public List<DiscountMapping> getDiscountMappingList() {
        return new ArrayList<>();
    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_DELETE + "\")")
    public Discount deleteSlab(Discount discount, int index) {
        discount.getDiscMappingList().remove(index);
        return discount;
    }

 //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
    public DiscountPlanMapping getDiscountPlanMapping() {
        return new DiscountPlanMapping();
    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
    public List<DiscountPlanMapping> getDiscountPlanMappingList() {
        return new ArrayList<>();
    }

 //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_DELETE + "\")")
    public Discount deleteTier(Discount discount, int index) {
        discount.getPlanMappingList().remove(index);
        return discount;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_ADD + "\")")
    public Discount saveDiscount(Discount discount) throws Exception {
        String SUBMODULE = MODULE + "[saveDiscount()]";
        try {
            for (DiscountMapping item : discount.getDiscMappingList()) {
                item.setDiscount(discount);
            }
            for (DiscountPlanMapping item : discount.getPlanMappingList()) {
                item.setDiscount(discount);
            }
            Discount save = entityRepository.save(discount);
            return save;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public DiscountPojo save(DiscountPojo pojo) throws Exception {
        String SUBMODULE = MODULE + "[save()]";
//        Discount oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
        try {
            // TODO: pass mvnoID manually 6/5/2025
            pojo.setMvnoId(pojo.getMvnoId());
            // TODO: pass mvnoID manually 6/5/2025
            pojo.setMvnoName(mvnoRepository.findMvnoNameById(pojo.getMvnoId().longValue()));
            Discount obj = covertDiscountPojoToDiscountModel(pojo);
            if(getBUIdsFromCurrentStaff().size() == 1)
                obj.setBuId(getBUIdsFromCurrentStaff().get(0));
//            if(oldObj!=null) {
//                log.info("Discount update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
//            }
            obj = saveDiscount(obj);
            createDataSharedService.sendEntitySaveDataForAllMicroService(obj);
            pojo = covertDiscountModelToDiscountPojo(obj);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public Discount covertDiscountPojoToDiscountModel(DiscountPojo discountPojo) throws Exception {
        String SUBMODULE = MODULE + "[covertDiscountPojoToDiscountModel()]";
        Discount discount = null;
        try {
            if (discountPojo != null) {
                discount = new Discount();
                if (discountPojo.getId() != null) {
                    discount.setId(discountPojo.getId());
                }
                discount.setName(discountPojo.getName());
                discount.setDesc(discountPojo.getDesc());
                if(discountPojo.getMvnoId() != null) {
                	discount.setMvnoId(discountPojo.getMvnoId());
                    discount.setMvnoName(discountPojo.getMvnoName());
            	}
                discount.setStatus(discountPojo.getStatus());

                if (discountPojo.getDiscoundMappingList() != null && discountPojo.getDiscoundMappingList().size() > 0) {
                    List<DiscountMapping> discountMappingsList = new ArrayList<DiscountMapping>();
                    DiscountMapping discountMapping = null;
                    List<DiscountMapping> oldDiscountMappingList=discountMappingRepository.findByDiscountId(discountPojo.getId());
                    List<DiscountMappingPojo> newDiscountMappingList=discountPojo.getDiscoundMappingList().stream().filter(discountMappingPojo -> Objects.isNull(discountMappingPojo.getId())).collect(Collectors.toList());
                    List<DiscountPlanMapping> exstingPlan=discountPlanMappingRepo.findByDiscountId(discountPojo.getId());
                    List<DiscountPlanMappingPojo> discountPlanMappings=discountPojo.getDiscoundPlanMappingList().stream().filter(discountPlanMappingPojo -> Objects.isNull(discountPlanMappingPojo.getId())).collect(Collectors.toList());

                    for(DiscountMapping oldMapping : oldDiscountMappingList){
                        for(DiscountMappingPojo newDisc: newDiscountMappingList){

                            if(oldMapping.getDiscountType().equals(newDisc.getDiscountType()) && oldMapping.getAmount().equals(newDisc.getAmount()) && oldMapping.getValidFrom().equals(newDisc.getValidFrom()) && oldMapping.getValidUPTO().equals(newDisc.getValidUpto())){
                                throw new IllegalArgumentException("Unable save, Found duplicate mapping entry under Discount Mapping.");
                            }

                        }
                    }

                    for(DiscountPlanMapping discountPlanMapping: exstingPlan){
                        for(DiscountPlanMappingPojo discountPlanMapping1:discountPlanMappings){
                            if(discountPlanMapping.getPlanId()==discountPlanMapping1.getPlanId()){
                                throw new IllegalArgumentException("Unable Save, Found duplicate mapping entry under Discount Plan Mapping.");
                            }
                        }
                    }

                    for (DiscountMappingPojo element : discountPojo.getDiscoundMappingList()) {
                        discountMapping = new DiscountMapping(element, discount);
                        discountMappingsList.add(discountMapping);
                    }
                    discount.setDiscMappingList(discountMappingsList);
                }

                if (discountPojo.getDiscoundPlanMappingList() != null && discountPojo.getDiscoundPlanMappingList().size() > 0) {
                    List<DiscountPlanMapping> discountPlanMappingsList = new ArrayList<DiscountPlanMapping>();
                    DiscountPlanMapping discountPlanMapping = null;
                    for (DiscountPlanMappingPojo element : discountPojo.getDiscoundPlanMappingList()) {
                        discountPlanMapping = new DiscountPlanMapping(element, discount);
                        discountPlanMappingsList.add(discountPlanMapping);
                    }
                    discount.setPlanMappingList(discountPlanMappingsList);
                }

                return discount;
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return discount;
    }

    public DiscountPojo covertDiscountModelToDiscountPojo(Discount discount) throws Exception {

        String SUBMODULE = MODULE + "[covertDiscountModelToDiscountPojo()]";

        DiscountPojo pojo = null;
        try {
            if (discount != null) {
                pojo = new DiscountPojo();
                pojo.setId(discount.getId());
                pojo.setName(discount.getName());
                pojo.setDesc(discount.getDesc());
                pojo.setStatus(discount.getStatus());
                pojo.setCreatedById(discount.getCreatedById());
                pojo.setCreatedate(discount.getCreatedate());
                pojo.setCreatedByName(discount.getCreatedByName());
                pojo.setLastModifiedById(discount.getLastModifiedById());
                pojo.setUpdatedate(discount.getUpdatedate());
                pojo.setLastModifiedByName(discount.getLastModifiedByName());
                if(discount.getMvnoId() != null) {
                	pojo.setMvnoId(discount.getMvnoId());
                    pojo.setMvnoName(discount.getMvnoName());
            	}
                if (discount.getDiscMappingList() != null && discount.getDiscMappingList().size() > 0) {
                    List<DiscountMappingPojo> discoundMappingList = new ArrayList<>();
                    for (DiscountMapping discountMapping : discount.getDiscMappingList()) {
                        DiscountMappingPojo discountMappingPojo = new DiscountMappingPojo(discountMapping);
                        discountMappingPojo.setId(discountMapping.getId());
                        discoundMappingList.add(discountMappingPojo);
                    }
                    pojo.setDiscoundMappingList(discoundMappingList);
                }

                if (discount.getPlanMappingList() != null && discount.getPlanMappingList().size() > 0) {
                    List<DiscountPlanMappingPojo> discoundPlanMappingList = new ArrayList<>();
                    for (DiscountPlanMapping discountPlanMapping : discount.getPlanMappingList()) {
                        DiscountPlanMappingPojo discountPlanMappingPojo = new DiscountPlanMappingPojo(discountPlanMapping);
                        discountPlanMappingPojo.setId(discountPlanMapping.getId());
                        discoundPlanMappingList.add(discountPlanMappingPojo);
                    }
                    pojo.setDiscoundPlanMappingList(discoundPlanMappingList);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
    public List<DiscountPojo> convertResponseModelIntoPojo(List<Discount> discountList) throws Exception {
        String SUBMODULE = MODULE + "[convertResponseModelIntoPojo()]";
        List<DiscountPojo> pojoListRes = new ArrayList<>();
        try {
            if (discountList != null && discountList.size() > 0) {
                for (Discount discount : discountList) {
                    pojoListRes.add(covertDiscountModelToDiscountPojo(discount));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    public void validateRequest(DiscountPojo pojo, Integer operation) {
        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.YES_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.NO_STATUS))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE))
                && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Discount");
        List<DiscountPojo> discountPojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, DiscountPojo.class, discountPojos, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                DiscountPojo.class.getDeclaredField("id"),
                DiscountPojo.class.getDeclaredField("name"),
                DiscountPojo.class.getDeclaredField("status"),
        };
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<DiscountPojo> discountPojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, DiscountPojo.class, discountPojos, getFields());
    }

    @Override
    public Page<Discount> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getDiscountByName(searchModel.getFilterValue(), pageRequest);
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

    public Page<Discount> getDiscountByName(String s1, PageRequest pageRequest) {
        if(getMvnoIdFromCurrentStaff(null) == 1)// TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findAllByNameAndIsDeleteIsFalse(s1, pageRequest);
        if(getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findAllByNameAndIsDeleteIsFalse(s1, pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findAllByNameAndIsDeleteIsFalse(s1, pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
    }


    @Override
    public boolean duplicateVerifyAtSave(String name,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
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
    public boolean duplicateVerifyAtEdit(String name, Integer id,Integer mvnoId) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
//        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(mvnoId == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(mvnoId, 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, mvnoId, getBUIdsFromCurrentStaff());
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
    public Page<Discount> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder,
                                List<GenericSearchModel> filterList,Integer mvnoId) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if(mvnoId == 1)    // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findAll(pageRequest);
        if (null == filterList || 0 == filterList.size())
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findAll(pageRequest, Arrays.asList(1, mvnoId));
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
    }

    @Override
    public Discount get(Integer id,Integer mvnoId) {
        Discount discount = super.get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId == 1 || ((discount.getMvnoId() == mvnoId || discount.getMvnoId() == 1) && (discount.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(discount.getBuId()))))
            return discount;
        return null;
    }

    public Discount getEntityForUpdateAndDelete(Integer id,Integer mvnoId) {
        Discount discount = get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if(discount == null || (!(mvnoId == 1 || mvnoId.intValue() == discount.getMvnoId().intValue()) && (discount.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(discount.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return discount;
    }
    public List<DiscountMappingPojo> getDiscountMappingsByPlanId(Integer planId) {
        List<DiscountMapping> entities = discountMappingRepository.findMappingsByPlanId(planId);
        List<DiscountMappingPojo> result = new ArrayList<>();
        for (DiscountMapping dm : entities) {
            DiscountMappingPojo pojo = new DiscountMappingPojo();
            pojo.setId(dm.getId());
            pojo.setValidFrom(dm.getValidFrom());
            pojo.setValidUpto(dm.getValidUPTO());
            pojo.setDiscountType(dm.getDiscountType());
            pojo.setAmount(dm.getAmount());
            pojo.setName(dm.getDiscount().getName());
            result.add(pojo);
        }
        return result;
    }
    public Map<String, Object> shouldShowDiscountPopup(Long custId, Long serviceMappingId) {

        Map<String, Object> result = new HashMap<>();
        if (custId == null || serviceMappingId == null) {
            result.put("showPopup", true);
            result.put("message", "Bad request.Customer Id is missing.");
            result.put("code", APIConstants.FAIL);
            return result;
        }

        try {
            Integer servicemapId = serviceMappingId.intValue();
            Optional<CustomerServiceMapping> optional = customerServiceMappingRepository.findById(servicemapId);
            if (optional.isPresent()) {
                result.put("showPopup", true);
                result.put("message", "Service mapping not found.");
                result.put("code", APIConstants.NOT_FOUND);
                return result;
            }
            CustomerServiceMapping mapping = optional.get();
            if (mapping.getDiscountType() == null) {
                result.put("showPopup", true);
                result.put("message", "Discount type is not available.");
                result.put("code", APIConstants.FAIL);
                return result;
            }
            if (CommonConstants.DISCOUNT_TYPE.ONE_TIME.equalsIgnoreCase(mapping.getDiscountType())) {
                result.put("showPopup", true);
                result.put("message", "This is a one-time discount and cannot be changed. To update, select a recurring discount.");
                result.put("code", APIConstants.NO_CONTENT);
                return result;
            }
            if (mapping.getDiscount() == null || mapping.getDiscount() <= 0) {
                result.put("showPopup", true);
                result.put("message", "No discount is currently applied. Please update the discount to continue.");
                result.put("code", APIConstants.NO_CONTENT);
                return result;
            }
            if (CommonConstants.DISCOUNT_TYPE.RECURRING.equalsIgnoreCase(mapping.getDiscountType()) && mapping.getDiscount() > 0) {
                LocalDate expiryDate = mapping.getDiscountExpiryDate();
                if (expiryDate != null) {
                    if (!expiryDate.isAfter(LocalDate.now())) {
                        result.put("showPopup", true);
                        result.put("message", "Your discount has expired. Please update it with a future expiry date to continue.");
                        result.put("code", APIConstants.NO_CONTENT);
                        return result;
                    } else {
                        result.put("showPopup", false);
                        result.put("message", "Recurring discount still active.");
                        result.put("code", APIConstants.SUCCESS);
                        return result;
                    }
                }
                result.put("showPopup", true);
                result.put("message", "No expiry date found.");
                result.put("code", APIConstants.FAIL);
                return result;
            }
            result.put("showPopup", true);
            result.put("message", "Something went wrong.");
            result.put("code", APIConstants.INTERNAL_SERVER_ERROR);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("showPopup", true);
            result.put("message", "Internal error");
            result.put("code", APIConstants.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

}
