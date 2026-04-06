package com.adopt.apigw.service.common;

import com.adopt.apigw.MicroSeviceDataShare.MessageSender.ChangeVasPackMessage;
import com.adopt.apigw.MicroSeviceDataShare.MessageSender.VasPackDTO;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveMvnoSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.controller.common.VasPlan.VasPlanResponseDTO;
import com.adopt.apigw.controller.common.VasPlan.VasPlanUpdateDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.BooleanWithMessage;
import com.adopt.apigw.model.common.QVasPlan;
import com.adopt.apigw.model.common.VasPlan;
import com.adopt.apigw.model.common.VasPlanCharge;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.ChangePlanDTOs.ChangePlanNotification;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.pojo.NewCustPojos.NewCustPlanMappingPojo;
import com.adopt.apigw.rabbitMq.message.CustomMessage;
import com.adopt.apigw.repository.common.VasPlanChargeRepository;
import com.adopt.apigw.repository.common.VasPlanRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.postpaid.ChargeService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UpdateDiffFinder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.joda.time.Hours;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.adopt.apigw.core.utillity.log.ApplicationLogger.logger;

@Service
public class VasPlanService extends AbstractService<VasPlan, VasPlanPojo, Integer>{
    @Autowired
    VasPlanRepository vasPlanRepository;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    VasPlanChargeRepository vasPlanChargeRepository;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    private CustomerChargeHistoryRepo customerChargeHistoryRepo;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustChargeInstallmentRepository custChargeInstallmentRepository;
    public static final String MODULE = "[VasPlanService]";
    @Override
    protected JpaRepository<VasPlan, Integer> getRepository() {
        return vasPlanRepository;
    }


    public VasPlanPojo save(VasPlanPojo pojo) throws Exception {
        try {

            VasPlan vasPlanModel = convertVasPlanPojoToVasModel(pojo);
            vasPlanModel = saveVasplan(vasPlanModel);

            if (pojo.getChargeList() != null && !pojo.getChargeList().isEmpty()) {
                List<VasPlanChargePojo> savedCharges = new ArrayList<>();
                List<VasPlanCharge> chargeEntities = new ArrayList<>();
                for (VasPlanChargePojo chargePojo : pojo.getChargeList()) {
                    VasPlanCharge chargeEntity = convertVasPlanChargePojoToEntity(chargePojo);
                    chargeEntity.setVasPlan(vasPlanModel);
                    chargeEntity = vasPlanChargeRepository.save(chargeEntity);
                    chargeEntities.add(chargeEntity);
                    savedCharges.add(convertVasPlanChargeEntityToPojo(chargeEntity));
                }
                pojo.setChargeList(savedCharges);
                vasPlanModel.setChargeList(chargeEntities);
            }

            vasPlanModel = vasPlanRepository.findById(vasPlanModel.getId())
                    .orElseThrow(() -> new RuntimeException("VasPlan not found"));

            pojo = convertVasPlanModelToVasPlanPojo(vasPlanModel);
            createDataSharedService.sendEntitySaveDataForAllMicroService(vasPlanModel);

            return pojo;

        } catch (Exception ex) {
            ApplicationLogger.logger.error("Error saving VasPlanPojo", ex);
            throw ex;
        }
    }


    public VasPlan convertVasPlanPojoToVasModel(VasPlanPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [convertVasPlanPojoToVasModel()] ";
        VasPlan vasPlan = null;
        try {
            if (pojo != null) {
                vasPlan = new VasPlan();
                if (pojo.getId() != null) {
                    vasPlan.setId(pojo.getId());
                }
                vasPlan.setName(pojo.getName());
                vasPlan.setPauseDaysLimit(pojo.getPauseDaysLimit());
                vasPlan.setPauseTimeLimit(pojo.getPauseTimeLimit());
                vasPlan.setTatId(pojo.getTatId());
                vasPlan.setInventoryReplaceAfterYears(pojo.getInventoryReplaceAfterYears());
                vasPlan.setInventoryPaidMonths(pojo.getInventoryPaidMonths());
                vasPlan.setInventoryCount(pojo.getInventoryCount());
                vasPlan.setShiftLocationYears(pojo.getShiftLocationYears());
                vasPlan.setShiftLocationMonths(pojo.getShiftLocationMonths());
                vasPlan.setShiftLocationCount(pojo.getShiftLocationCount());
                vasPlan.setPaymentType(pojo.getPaymentType());
                vasPlan.setVasAmount(pojo.getVasAmount());
                vasPlan.setValidity(pojo.getValidity());
                vasPlan.setUnitsOfValidity(pojo.getUnitsOfValidity());
                if(pojo.getMvnoId() != null) {
                    vasPlan.setMvnoId(pojo.getMvnoId());
                }
                if(pojo.getIsdefault() != null){
                    vasPlan.setIsdefault(pojo.getIsdefault());
                }
                else {
                    vasPlan.setIsdefault(false);
                }
                return vasPlan;
            }
        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public VasPlan saveVasplan(VasPlan vasPlan) throws Exception {
        try{
//            if(getMvnoIdFromCurrentStaff() != null) {
//                vasPlan.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
            VasPlan save = vasPlanRepository.save(vasPlan);
            return save;
        }catch (Exception e){
            logger.error("While creating VasPlan, throws an error : "+e.getMessage());
            e.printStackTrace();
        }
        return null;

    }
    public VasPlanPojo convertVasPlanModelToVasPlanPojo(VasPlan vasPlan) throws Exception {
        String SUBMODULE = MODULE + " [convertVasPlanModelToVasPlanPojo()] ";
        VasPlanPojo pojo = null;
        try {
            if (vasPlan != null) {
                pojo = new VasPlanPojo();
                pojo.setId(vasPlan.getId());
                pojo.setName(vasPlan.getName());
                pojo.setPauseDaysLimit(vasPlan.getPauseDaysLimit());
                pojo.setPauseTimeLimit(vasPlan.getPauseTimeLimit());
                pojo.setTatId(vasPlan.getTatId());
                pojo.setInventoryReplaceAfterYears(vasPlan.getInventoryReplaceAfterYears());
                pojo.setInventoryPaidMonths(vasPlan.getInventoryPaidMonths());
                pojo.setInventoryCount(vasPlan.getInventoryCount());
                pojo.setShiftLocationYears(vasPlan.getShiftLocationYears());
                pojo.setShiftLocationMonths(vasPlan.getShiftLocationMonths());
                pojo.setShiftLocationCount(vasPlan.getShiftLocationCount());
                pojo.setPaymentType(vasPlan.getPaymentType());
                pojo.setVasAmount(vasPlan.getVasAmount());
                pojo.setValidity(vasPlan.getValidity());
                pojo.setUnitsOfValidity(vasPlan.getUnitsOfValidity());
                if(vasPlan.getMvnoId() != null) {
                    pojo.setMvnoId(vasPlan.getMvnoId());
                }
                List<VasPlanCharge> charges = vasPlanChargeRepository.findByVasPlanId(vasPlan.getId());
                List<VasPlanChargePojo> chargePojos = new ArrayList<>();
                for (VasPlanCharge charge : charges) {
                    chargePojos.add(convertVasPlanChargeEntityToPojo(charge));
                }
                pojo.setChargeList(chargePojos);
            }
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) {
                count = vasPlanRepository.duplicateVerifyAtSave(name);
            } else {
                count = vasPlanRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;

        if (name != null) {
            name = name.trim();
            Integer count;

            if (getMvnoIdFromCurrentStaff() == 1) {
                count = vasPlanRepository.duplicateVerifyAtSave(name);
            } else {
                count = vasPlanRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }

            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1) {
                    countEdit = vasPlanRepository.duplicateVerifyAtEdit(name, id);
                } else {
                    countEdit = vasPlanRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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

    public VasPlanPojo updateCustomerVASplan(VasPlanPojo pojo, HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        String SUBMODULE = MODULE + "update()";
        Optional<VasPlan> vasPlans = vasPlanRepository.findById(pojo.getId());
        try {

//            pojo.setMvnoId(pojo.getMvnoId());
            VasPlan obj = convertVasPlanPojoToVasModel(pojo);
            getVasPlanForUpdateAndDelete(obj.getId(),obj.getMvnoId());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update VasPlan" + LogConstants.LOG_BY_NAME+pojo.getName()  + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + " , Updated VasPlan Details " + UpdateDiffFinder.getUpdatedDiff(vasPlans,obj)+pojo.getName()+ LogConstants.LOG_STATUS+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS  );
            obj = saveVasplan(obj);

            if (pojo.getChargeList() != null) {
                List<VasPlanChargePojo> updatedCharges = new ArrayList<>();
                List<VasPlanCharge> chargeEntities = new ArrayList<>();
                for (VasPlanChargePojo chargePojo : pojo.getChargeList()) {
                    VasPlanCharge chargeEntity = convertVasPlanChargePojoToEntity(chargePojo);
                    chargeEntity.setVasPlan(obj);
                    chargeEntity = vasPlanChargeRepository.save(chargeEntity);
                    chargeEntities.add(chargeEntity);
                    updatedCharges.add(convertVasPlanChargeEntityToPojo(chargeEntity));
                }

                pojo.setChargeList(updatedCharges);
                obj.setChargeList(chargeEntities);
            }
            pojo = convertVasPlanModelToVasPlanPojo(obj);

            createDataSharedService.updateEntityDataForAllMicroService(obj);

        } catch (Exception ex) {
//            LOGGER.error("Request From : "+ req.getHeader("requestFrom")+", Request for : "+", Request to update country "+LogConstants.LOG_BY_NAME+pojo.getName()+LogConstants.REQUEST_BY+ getLoggedInUser().getFirstName()+LogConstants.LOG_STATUS+ LogConstants.LOG_ERROR +APIConstants.ERROR_MESSAGE+ex.getMessage() +LogConstants.LOG_STATUS_CODE +APIConstants.FAIL);
            throw ex;
        }
        return pojo;
    }
    public VasPlan getVasPlanForUpdateAndDelete(Integer id,Integer mvnoId) {
        VasPlan vasPlan = get(id,mvnoId);
        return vasPlan;
    }


    public void deleteVasPlan(Integer id) throws CustomValidationException {

        Optional<VasPlan> optionalPlan;
        if (getMvnoIdFromCurrentStaff() == 1){
            optionalPlan = vasPlanRepository.findByIdAndIsdeleteFalse(id);
        }else {
            optionalPlan = vasPlanRepository.findByIdAndIsdeleteFalseAndMvnoidIn(id,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        }
        if (!optionalPlan.isPresent()) {
            throw new CustomValidationException(404,"id is not found given id",null);
        }
        if (vasPlanChargeRepository.existsByVasPlan_Id(id)) {
            throw new CustomValidationException(400, "VAS Plan is bind with customer", null);
        }
        VasPlan vasPlan = optionalPlan.get();
        vasPlan.setIsdelete(true);
       vasPlanRepository.save(vasPlan);
        createDataSharedService.updateEntityDataForAllMicroService(vasPlan);
    }

    public List<VasPlan> getAllActiveVasPlans() {
        List<VasPlan> vasPlans = new ArrayList<>();
        if(getMvnoIdFromCurrentStaff() == 1){
            vasPlans =  vasPlanRepository.findAllByIsdeleteFalseAndIsdefaultFalse();
        }else {
            vasPlans = vasPlanRepository.findAllByIsdeleteFalseAndIsdefaultFalseAndMvnoidIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        }
        return vasPlans;
    }

    public List<VasPlan> getAllActiveVasPlansBasedCurrency(String currency,Integer mvnoId) {
        List<VasPlan> vasPlans = new ArrayList<>();
        if(mvnoId == 1){
            vasPlans = vasPlanRepository.findAllByIsdeleteFalseAndIsdefaultFalse();
        }else {
            vasPlans = vasPlanRepository.findAllByIsdeleteFalseAndIsdefaultFalseAndMvnoidInAndBaseCurrency(Arrays.asList(mvnoId, 1), currency);
        }
        return vasPlans;
    }

    public Page<VasPlan> getAllActiveVasPlansWithPagination(PaginationRequestDTO paginationRequestDTO,Integer mvnoId) {
        Pageable pageable = PageRequest.of(paginationRequestDTO.getPage() - 1, paginationRequestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        Page<VasPlan> vasPlans = null;
        try {
            if (mvnoId == 1) {
                vasPlans = vasPlanRepository.findAllByIsdeleteFalse(pageable);
            } else {
                vasPlans = vasPlanRepository.findAllByIsdeleteFalseAndMvnoidIn(
                        Arrays.asList(mvnoId, 1),
                        pageable
                );
            }
        }catch (Exception e){
            logger.info(e.getMessage());
        }
        return vasPlans;
    }
    public Page<VasPlan> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getVasPlanByName(searchModel.getFilterValue(), pageRequest);
                    }
                } else
                    throw new RuntimeException("Please Provide Search Column!");
            }
        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }
    public Page<VasPlan> getVasPlanByName(String s1, PageRequest pageRequest) {
        Page<VasPlan> vasPlanList = null;
        QVasPlan qVasPlan = QVasPlan.vasPlan;
        BooleanExpression booleanExpression = qVasPlan.isNotNull()
                .and(qVasPlan.isdelete.eq(false))
                .and(qVasPlan.name.likeIgnoreCase("%" + s1 + "%"));
        if(getMvnoIdFromCurrentStaff() == 1) {
            return vasPlanRepository.findAll(booleanExpression, pageRequest);
        }else {
            booleanExpression = booleanExpression.and(qVasPlan.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            return vasPlanRepository.findAll(booleanExpression, pageRequest);
        }
    }

//    public VasPlanChargePojo Saveplancharge(VasPlanChargePojo chargePojo) throws Exception {
//        try {
//            VasPlanCharge chargeEntity = convertVasPlanChargePojoToEntity(chargePojo);
//            chargeEntity = vasPlanChargeRepository.save(chargeEntity);
//
//            VasPlanChargePojo savedPojo = convertVasPlanChargeEntityToPojo(chargeEntity);
//
//            return savedPojo;
//
//        } catch (Exception e) {
//            // Optionally log the error
//            // ApplicationLogger.logger.error("Error saving charge: " + e.getMessage(), e);
//            throw e;
//        }
//    }
    private VasPlanCharge convertVasPlanChargePojoToEntity(VasPlanChargePojo pojo) {
        VasPlanCharge entity = new VasPlanCharge();
        Optional<Charge> charge = chargeRepository.findById(pojo.getChargeId());
        entity.setId(pojo.getId());
        entity.setCharge(charge.orElse(null));
        entity.setBillingCycle(pojo.getBillingCycle());
        entity.setChargePrice(pojo.getChargePrice());
        entity.setCreatedate(pojo.getCreateDate());
        return entity;
    }
    private VasPlanChargePojo convertVasPlanChargeEntityToPojo(VasPlanCharge entity) {
        VasPlanChargePojo pojo = new VasPlanChargePojo();

        pojo.setId(entity.getId());
        pojo.setChargeId(entity.getCharge().getId());
        pojo.setBillingCycle(entity.getBillingCycle());
        pojo.setChargePrice(entity.getChargePrice());
        pojo.setCreateDate(entity.getCreatedate());
        pojo.setVasPlanId(entity.getVasPlan().getId());
        return pojo;
    }
    public List<Charge> getByListType(Integer mvnoId) {

        List<Charge> charges = new ArrayList<>();
        try {
            if(mvnoId==1) charges = chargeRepository.findByChargeCategory(CommonConstants.VAS_CHARGE);
            else charges = chargeRepository.findByChargeCategoryAndMvnoIdIn(CommonConstants.VAS_CHARGE,Arrays.asList(mvnoId,1));
        }catch (Exception e){
           e.printStackTrace();
        }
        return charges;
    }

    public VasPlanPojo saveDefaultVasForMvno(Long mvnoId) {
        try {
            VasPlanPojo vasPlanPojo = new VasPlanPojo();
            vasPlanPojo.setName("isdefault");
            vasPlanPojo.setPauseDaysLimit(0);
            vasPlanPojo.setPauseTimeLimit(0);
            vasPlanPojo.setTatId(0);
            vasPlanPojo.setInventoryReplaceAfterYears(0);
            vasPlanPojo.setInventoryPaidMonths(0);
            vasPlanPojo.setInventoryCount(0);
            vasPlanPojo.setShiftLocationYears(0);
            vasPlanPojo.setShiftLocationMonths(0);
            vasPlanPojo.setShiftLocationCount(0);
            vasPlanPojo.setVasAmount(0);
            vasPlanPojo.setMvnoId(mvnoId != null ? mvnoId.intValue() : 0);
            vasPlanPojo.setValidity(0);
            vasPlanPojo.setUnitsOfValidity("years");
            vasPlanPojo.setIsdefault(true);

            VasPlan vasPlan = convertVasPlanPojoToVasModel(vasPlanPojo);
            vasPlan = vasPlanRepository.save(vasPlan);
            createDataSharedService.sendEntitySaveDataForAllMicroService(vasPlan);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public BooleanWithMessage checkShiftLocation(Integer custId){
        Boolean flag = false;
        List<NewCustPlanMappingPojo>  planList = custPlanMappingRepository.findAllCurrentPlansByCustId(custId);
        Integer mvnoId = getMvnoIdFromCurrentStaff(custId);
        List<VasPlan> vasPlanList = vasPlanRepository.findDefaultVasPlansByMvnoIdIn(Arrays.asList(mvnoId,1));
        if(vasPlanList.isEmpty()){
            return new BooleanWithMessage(false , CommonConstants.BooleanMessages.DEFAULT_VAS_NOT_FOUND);
        }

        if(planList.isEmpty() ){
            VasPlan vasPlan = vasPlanList.get(0);
            Boolean limitFlag = checkShiftLocationLimit(custId,vasPlan).isAllowed();
            Boolean planInvoiceCheck = checkInvoicePaymentLimit(custId,vasPlan , planList).isAllowed();
            if(limitFlag && planInvoiceCheck){
                return new BooleanWithMessage(true , CommonConstants.BooleanMessages.VAS_ELIGIBLATITY_SUCCESS);
            }
            else{
                return new BooleanWithMessage(false , CommonConstants.BooleanMessages.VAS_ELIGIBLATITY_FAILED);
            }
        }
        Optional<Integer> vasPackId = planList.stream()
                .map(NewCustPlanMappingPojo::getVasPackId)
                .filter(Objects::nonNull)
                .findFirst();
        VasPlan vasPlan = null;
        vasPlan = vasPackId.map(integer -> vasPlanRepository.findById(integer).get()).orElseGet(() -> vasPlanList.get(0));
        BooleanWithMessage limitFlag = checkShiftLocationLimit(custId,vasPlan);
        BooleanWithMessage planInvoiceCheck = checkInvoicePaymentLimit(custId,vasPlan , planList);
        if(!limitFlag.isAllowed()){
            return new BooleanWithMessage(false , limitFlag.getMessage());
        }
        if(!planInvoiceCheck.isAllowed()){
            return new BooleanWithMessage(false , planInvoiceCheck.getMessage());
        }
        return new BooleanWithMessage(true , CommonConstants.BooleanMessages.VAS_ELIGIBLATITY_SUCCESS);
    }

    public BooleanWithMessage checkShiftLocationLimit(Integer custId, VasPlan vasPlan){
       if(vasPlan.getShiftLocationCount() == 0){
           return new BooleanWithMessage(false , CommonConstants.BooleanMessages.VAS_ELIGIBLATITY_FAILED);
       }
       else{
           LocalDateTime startDate = LocalDateTime.now()
                   .minusYears(vasPlan.getShiftLocationYears() == null ? 0 : vasPlan.getShiftLocationYears());
           long count  = customerAddressRepository.countActiveShiftedAddressesBySubscriberIdWithinPeriod(custId,startDate);
            if(count < vasPlan.getShiftLocationCount()){
                return new BooleanWithMessage(true,CommonConstants.BooleanMessages.VAS_ELIGIBLATITY_SUCCESS);
            }
            else{
                return  new BooleanWithMessage(false , CommonConstants.BooleanMessages.SHIFT_LOCATION_NOT_ALLOWED);
            }
       }
    }

    public BooleanWithMessage checkInvoicePaymentLimit(Integer custId,VasPlan vasPlan, List<NewCustPlanMappingPojo>  planList){
        if(vasPlan.getShiftLocationMonths() == 0){
            return new BooleanWithMessage(false, CommonConstants.BooleanMessages.VAS_ELIGIBLATITY_FAILED);
        }
        else{
            List<NewCustPlanMappingPojo> custPlanMappingPojos = planList;
            if(custPlanMappingPojos.isEmpty()) {
                planList = custPlanMappingRepository.findAllCurrentPlansByCustIdByVasnot(custId);
            }
            long count  = debitDocRepository.countFullyPaidInvoicesNative(custId,planList.get(0).getPlanId());
            if(count < vasPlan.getShiftLocationCount()){
                return  new BooleanWithMessage(false ,CommonConstants.BooleanMessages.SHIFT_LOCATION_PAYMENT_LIMIT_NOT_ALLOWED);
            }
            else{
                return  new BooleanWithMessage(true,CommonConstants.BooleanMessages.VAS_ELIGIBLATITY_SUCCESS);
            }
        }
    }

    public void vasPlanUpdate(VasPlanUpdateDTO vasPlanUpdateDTO){
        List<CustPlanMappping> custPlanMapppings = new ArrayList<>();

        VasPlan vasPlan = vasPlanRepository.findById(vasPlanUpdateDTO.getNewVasId()).orElse(null);
        if(vasPlanUpdateDTO.getOldVasId() != null) {
            custPlanMapppings = custPlanMappingRepository.findAllCurrentPlansByCustIdAndVasId(vasPlanUpdateDTO.getCustId(), vasPlanUpdateDTO.getOldVasId());
        List<Integer> custPlanMappingIds = new ArrayList<>();
        List<CustPlanMappping> newMappingList = new ArrayList<>();
        List<Integer> oldCustPackIds = new ArrayList<>();
        for (CustPlanMappping oldMapping : custPlanMapppings) {
            oldCustPackIds.add(oldMapping.getId());
            oldMapping.setExpiryDate(LocalDateTime.now());
            oldMapping.setCustPlanStatus(CommonConstants.STOP_STATUS);
            custPlanMappingRepository.save(oldMapping);

            CustPlanMappping newMapping = new CustPlanMappping();

            BeanUtils.copyProperties(oldMapping, newMapping, "id", "vasId", "expiryDate", "endDate", "startDate","istrialplan","custPlanStatus");

            newMapping.setVasId(vasPlanUpdateDTO.getNewVasId());
            newMapping.setStartDate(LocalDateTime.now());
            newMapping.setExpiryDate(calculateExpiryDate(LocalDateTime.now(), vasPlan.getUnitsOfValidity(), vasPlan.getValidity()));
            newMapping.setEndDate(calculateExpiryDate(LocalDateTime.now(),vasPlan.getUnitsOfValidity(),vasPlan.getValidity()));
            newMapping.setQuotaList(null);
            newMapping.setIstrialplan(false);
            newMapping.setCustPlanStatus("Active");
            CustPlanMappping savedMapping = custPlanMappingRepository.save(newMapping);
            custPlanMappingIds.add(savedMapping.getId());
            newMappingList.add(savedMapping);

            Customers customers = savedMapping.getCustomer();
            customers.getPlanMappingList().add(savedMapping);
            customers = customersRepository.save(customers);
            List<CustPlanMapppingPojo> planmappingPojoList = customerMapper.mapCustPlanMapListToCustPlanMapPojoList(newMappingList , new CycleAvoidingMappingContext());
            planmappingPojoList.forEach(pojo -> {
                pojo.setInstallmentFrequency(vasPlanUpdateDTO.getInstallmentFrequency());
                pojo.setInstallmentNo(vasPlanUpdateDTO.getInstallment_no());
                pojo.setTotalInstallments(vasPlanUpdateDTO.getTotalInstallments());
            });
            customersService.saveCustomerChargeHistoryForVasPlan(planmappingPojoList, customerMapper.domainToDTO(customers,new CycleAvoidingMappingContext()),savedMapping, null, false, false, null, null);


        }

            Set<CustomerChargeHistory> customerChargeHistories =
                    customerChargeHistoryRepo.findByCustPlanMapppingIdIn(custPlanMappingIds);

            List<Integer> historyIds = customerChargeHistories.stream()
                    .map(CustomerChargeHistory::getId)
                    .collect(Collectors.toList());
            List<CustChargeInstallment> custChargeInstallments = custChargeInstallmentRepository.findByCustChargeHistoryIdsFetch(historyIds);
            Set<CustomerChargeHistory> customerChargeHistoryList = customerChargeHistoryRepo.findByCustPlanMapppingIdIn(custPlanMappingIds);
        createDataSharedService.sendChangePlanForAllMicroService(newMappingList,custPlanMapppings,customerChargeHistoryList,"Add ServicePack",custPlanMapppings.get(0).getRenewalId(),null,null,null,"", null,null, null,false,false,null,null,custChargeInstallments,null);
            ChangeVasPackMessage changeVasPackMessage =  new ChangeVasPackMessage();
            changeVasPackMessage.setOldVasPackId(oldCustPackIds);
            VasPackDTO vasPackDTO = new VasPackDTO();
            vasPackDTO.setNewVasId(vasPlanUpdateDTO.getNewVasId());
            vasPackDTO.setStartdate(newMappingList.get(0).getStartDate());
            vasPackDTO.setEnddate(newMappingList.get(0).getEndDate());
            vasPackDTO.setExpirydate(newMappingList.get(0).getExpiryDate());
            vasPackDTO.setMaincprId(custPlanMapppings.get(0).getId());
            vasPackDTO.setNewCprId(newMappingList.get(0).getId());
            vasPackDTO.setOldCprId(custPlanMapppings.get(0).getId());
            changeVasPackMessage.setNewVasPackdto(vasPackDTO);
            kafkaMessageSender.send(new KafkaMessageData(changeVasPackMessage, ChangeVasPackMessage.class.getSimpleName()));
        if(vasPlan == null){
            return;
        }
        for(CustPlanMappping custPlanMappping : custPlanMapppings){
            expireVasPlan(custPlanMappping.getId());
        }
        }
        else{
            if(vasPlan == null){
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Vas plan is null ",null);
            }
            List<CustPlanMappping> custPlanMapppingList1 = custPlanMappingRepository.findAllByCustomerId(vasPlanUpdateDTO.getCustId());
            if(custPlanMapppingList1.isEmpty()){
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "No customer plan mapping found for customer cant add vas.",null);
            }
            CustPlanMappping oldCustPlanMapping = custPlanMapppingList1.get(0);
            CustPlanMappping newMapping = new CustPlanMappping();

            BeanUtils.copyProperties(oldCustPlanMapping, newMapping, "id", "vasId", "expiryDate", "startDate","endDate","planId","istrialplan","custPlanStatus");

            newMapping.setVasId(vasPlanUpdateDTO.getNewVasId());
            newMapping.setStartDate(LocalDateTime.now());
            newMapping.setExpiryDate(calculateExpiryDate(LocalDateTime.now(), vasPlan.getUnitsOfValidity(), vasPlan.getValidity()));
            newMapping.setEndDate(calculateExpiryDate(LocalDateTime.now(), vasPlan.getUnitsOfValidity(), vasPlan.getValidity()));
            newMapping.setQuotaList(null);
            newMapping.setPlanId(null);
            newMapping.setIstrialplan(false);
            newMapping.setCustPlanStatus("Active");
            newMapping = custPlanMappingRepository.save(newMapping);
            ChangeVasPackMessage changeVasPackMessage =  new ChangeVasPackMessage();
            changeVasPackMessage.setOldVasPackId(new ArrayList<>());
            VasPackDTO vasPackDTO = new VasPackDTO();
            vasPackDTO.setNewVasId(vasPlanUpdateDTO.getNewVasId());
            vasPackDTO.setStartdate(newMapping.getStartDate());
            vasPackDTO.setEnddate(newMapping.getEndDate());
            vasPackDTO.setExpirydate(newMapping.getExpiryDate());
            vasPackDTO.setMaincprId(custPlanMapppingList1.get(0).getId());
            vasPackDTO.setNewCprId(newMapping.getId());
            changeVasPackMessage.setNewVasPackdto(vasPackDTO);
            kafkaMessageSender.send(new KafkaMessageData(changeVasPackMessage, ChangeVasPackMessage.class.getSimpleName()));
            Customers customers = newMapping.getCustomer();
            List<CustPlanMappping> newMappingList = new ArrayList<>();
            List<Integer> custPlanMappingIds = new ArrayList<>();
            newMappingList.add(newMapping);
            custPlanMappingIds.add(newMapping.getId());
            List<CustPlanMapppingPojo> planmappingPojoList = customerMapper.mapCustPlanMapListToCustPlanMapPojoList(newMappingList , new CycleAvoidingMappingContext());
            planmappingPojoList.forEach(pojo -> {
                pojo.setInstallmentFrequency(vasPlanUpdateDTO.getInstallmentFrequency());
                pojo.setInstallmentNo(vasPlanUpdateDTO.getInstallment_no());
                pojo.setTotalInstallments(vasPlanUpdateDTO.getTotalInstallments());
            });
            customersService.saveCustomerChargeHistoryForVasPlan(planmappingPojoList, customerMapper.domainToDTO(customers,new CycleAvoidingMappingContext()),newMapping, null, false, false, null, null);
            Set<CustomerChargeHistory> customerChargeHistories =
                    customerChargeHistoryRepo.findByCustPlanMapppingIdIn(custPlanMappingIds);

            List<Integer> historyIds = customerChargeHistories.stream()
                    .map(CustomerChargeHistory::getId)
                    .collect(Collectors.toList());
            List<CustChargeInstallment> custChargeInstallments = custChargeInstallmentRepository.findByCustChargeHistoryIdsFetch(historyIds);
            Set<CustomerChargeHistory> customerChargeHistoryList = customerChargeHistoryRepo.findByCustPlanMapppingIdIn(custPlanMappingIds);
            createDataSharedService.sendChangePlanForAllMicroService(newMappingList,custPlanMapppings,customerChargeHistoryList,"Add ServicePack",!custPlanMapppings.isEmpty() ? custPlanMapppings.get(0).getRenewalId() : null,null,null,null,"", null,null, null,false,false,null,null,custChargeInstallments,null);
        }

    }

    public void expireVasPlan(Integer custPackId){
        LocalDateTime oneHourBeforeNow = LocalDateTime.now().minusHours(1);
        custPlanMappingRepository.updateExpiryAndStatus(custPackId, oneHourBeforeNow);
    }

    public  LocalDateTime calculateExpiryDate(LocalDateTime startDate, String unit, int value) {
        if (startDate == null || unit == null || value < 0) {
            throw new IllegalArgumentException("Invalid input for expiry calculation");
        }

        switch (unit.toLowerCase()) {
            case "hours":
                return startDate.plusHours(value);

            case "days":
                return startDate.plusDays(value);

            case "months":
                return startDate.plusMonths(value);

            case "years":
                return startDate.plusYears(value);

            default:
                throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
    }

    public VasPlan getCustVasPlan(Integer custId){
        List<NewCustPlanMappingPojo> newCustPlanMappingPojo = custPlanMappingRepository.findActivePlansWithVasFilter(custId);
        if(!newCustPlanMappingPojo.isEmpty()) {
            VasPlan vasPlan = vasPlanRepository.findById(newCustPlanMappingPojo.get(0).getVasPackId()).get();
            return vasPlan;
        }
        else{
            return  null;
        }

    }

    public List<VasPlanResponseDTO> getCustVasDetails(Integer custId) {
        List<Object[]> rows = custPlanMappingRepository.findVasPlansByCustomerId(custId);
        if (rows.isEmpty()) {
            return Collections.emptyList();
        }
        List<VasPlanResponseDTO> responseList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Object[] row : rows) {
            VasPlanResponseDTO dto = new VasPlanResponseDTO();
            dto.setVasName((String) row[0]);
            dto.setVasOfferPrice(row[1] != null ? new Integer(row[1].toString()) : null);
            dto.setPauseDaysLimit((Integer) row[2]);
            dto.setPauseTimeLimit((Integer) row[3]);
            dto.setTatId((Integer) row[4]);
            dto.setInventoryReplaceAfterYears((Integer) row[5]);
            dto.setInventoryPaidMonths((Integer) row[6]);
            dto.setInventoryCount((Integer) row[7]);
            dto.setShiftLocationYears((Integer) row[8]);
            dto.setShiftLocationMonths((Integer) row[9]);
            dto.setShiftLocationCount((Integer) row[10]);
            dto.setValidity((Integer) row[11]);
            dto.setUnitsOfValidity((String) row[12]);
            dto.setStartDate((row[13] != null) ? ((java.sql.Timestamp) row[13]).toLocalDateTime() : null);
            dto.setEndDate((row[14] != null) ? ((java.sql.Timestamp) row[14]).toLocalDateTime() : null);
            dto.setExpiryDate((row[15] != null) ? ((java.sql.Timestamp) row[15]).toLocalDateTime() : null);
            dto.setInstallmentType((String) row[16]);
            dto.setTotalInstallments((Integer) row[17]);
            dto.setInstallmentStartDate((row[18] != null) ? ((java.sql.Date) row[18]).toLocalDate() : null);
            dto.setInstallmentEndDate((row[19] != null) ? ((java.sql.Date) row[19]).toLocalDate(): null);
            dto.setInstallmentNo((Integer) row[20]);
            dto.setAmountPerInstallment((BigDecimal) row[21]);
            dto.setInstallmentNextDate((row[22] != null) ? ((java.sql.Date) row[22]).toLocalDate() : null);
            dto.setInstallmentEnabled((Boolean) row[23]);

            if (dto.getStartDate() != null && dto.getEndDate() != null
                    && !now.isBefore(dto.getStartDate())
                    && !now.isAfter(dto.getEndDate())) {
                dto.setIsActive(true);
            } else {
                dto.setIsActive(false);
            }
            responseList.add(dto);
        }
        return responseList;
    }
    public boolean checkDefualtVas(VasPlanPojo vasPlanPojo){
         boolean flag = vasPlanRepository.existsDefaultPlanByMvnoId(vasPlanPojo.getMvnoId());
         return flag;
    }

    public VasPlan getVasPlanByCustId(Integer custId){
        Integer mvnoId = getMvnoIdFromCurrentStaff(custId);
        List<VasPlan> vasPlan = vasPlanRepository.findDefaultVasPlansByMvnoIdIn(Arrays.asList(mvnoId, 1));
        if(vasPlan.isEmpty()){
            return  null;
        }
        else{
            return  vasPlan.get(0);
        }
    }

    public void vasPlanValidation( VasPlanPojo pojo){
       if(pojo.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)){
           throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Vas Plan validity can't be in hours.",null);
       }
       LocalDateTime validityExpiry = calculateExpiryDate(LocalDateTime.now() , pojo.getUnitsOfValidity() , pojo.getValidity());
       LocalDateTime inventoryExpiry = LocalDateTime.now().plusMonths(pojo.getInventoryReplaceAfterYears());
       if(inventoryExpiry.isAfter(validityExpiry)){
           throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Inventory replace after month is greater than vas expiry date.Please change the condition",null);
       }
       LocalDateTime shiftLocationExpiry = LocalDateTime.now().plusYears(pojo.getShiftLocationYears());
       if(shiftLocationExpiry.isAfter(validityExpiry)){
           throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Shift Location year is greater than vas exipry date.Please change the condition",null);
       }
    }
}