package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.adopt.apigw.modules.CommonList.domain.CommonList;
import com.adopt.apigw.modules.CommonList.repository.CommonListRepository;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.repository.ServiceChargemappingRepo;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.pojo.api.ChargePojo;
import com.adopt.apigw.pojo.api.TaxDetailCountReqDTO;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.ChargeMessage;
import com.adopt.apigw.rabbitMq.message.InventoryChargeMessage;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.postpaid.ChargeRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanChargeRepo;
import com.adopt.apigw.repository.postpaid.TaxRepository;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class ChargeService extends AbstractService<Charge, ChargePojo, Integer> {

    private static final ModelMapper modelMapper = new ModelMapper();

    public ChargeService() {
        sortColMap.put("chargeName", "chargename");
        sortColMap.put("chargeType", "chargetype");
        sortColMap.put("chargeGroup", "chargegroup");
        sortColMap.put("id", "chargeid");
        sortColMap.put("price", "price");
        sortColMap.put("id1", "id");
    }

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private ChargeRepository entityRepository;

    @Autowired
    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    private ChargeMapper chargeMapper;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private TaxService taxService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceChargemappingRepo serviceChargemappingRepo;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    NotificationTemplateRepository templateRepository;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    CommonListRepository commonListRepository;

    @Autowired
    TaxRepository taxRepository;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplates;
    @Autowired
    private CacheService cacheService;


    ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    public static final String MODULE = "[ChargeService]";
    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<Charge, Integer> getRepository() {
        return entityRepository;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Charge");
        List<ChargePojo> chargePojoList = entityRepository.findAll().stream()
                .map(data -> chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, ChargePojo.class, chargePojoList, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                ChargePojo.class.getDeclaredField("id"),
                ChargePojo.class.getDeclaredField("name"),
                ChargePojo.class.getDeclaredField("chargetype"),
                ChargePojo.class.getDeclaredField("chargecategory"),
                ChargePojo.class.getDeclaredField("price"),
        };
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<ChargePojo> chargePojoList = entityRepository.findAll().stream()
                .map(data -> chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, ChargePojo.class, chargePojoList, getFields());
    }

    public Page<Charge> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.searchEntity(searchText, pageRequest, getMvnoIdFromCurrentStaff(null));
    }

    public List<Charge> getAllByChargeType(String chargeType) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findByChargetype(chargeType).stream().filter(charge -> charge.getMvnoId() == getMvnoIdFromCurrentStaff(null) || charge.getMvnoId() == null && charge.getIsDelete().equals(false)).collect(Collectors.toList());
    }

    public List<ChargePojo> getAllChargePojoByChargeType(String chargeType, Integer mvnoID) {
        List<ChargePojo> chargePojoList = new ArrayList<>();
        BusinessUnit businessUnit = new BusinessUnit();
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
            // TODO: pass mvnoID manually 6/5/2025
            chargePojoList = entityRepository.findAllByChargetypeAndIsDeleteIsFalse(chargeType).stream().map(data ->
                            chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(charge -> (charge.getMvnoId() == mvnoID || charge.getMvnoId() == 1 || mvnoID == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId())) &&
                            (Objects.isNull(charge.getBusinessType()) || (CommonConstants.RETAIL).equalsIgnoreCase(charge.getBusinessType()))).collect(Collectors.toList());
        } else if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)) {
            // TODO: pass mvnoID manually 6/5/2025
            chargePojoList = entityRepository.findAllByChargetypeAndIsDeleteIsFalse(chargeType).stream().map(data ->
                            chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(charge -> (charge.getMvnoId() == mvnoID|| charge.getMvnoId() == 1 || mvnoID == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId())) &&
                            (CommonConstants.ENTERPRISE).equalsIgnoreCase(charge.getBusinessType())).collect(Collectors.toList());
        }
        return chargePojoList;
    }

    public Integer getChargeByPlan(Integer planId) throws Exception {
        Integer chargeId = postpaidPlanChargeRepo.getchargeByPlan(planId);
        return chargeId;
    }

    public List<Integer> getchargelistByPlan(Integer planId) throws Exception {
        return postpaidPlanChargeRepo.getchargelistByPlan(planId);
    }

    public List<ChargePojo> getAllChargePojoByChargeCategory(List<String> category) {
        List<ChargePojo> chargePojoList = new ArrayList<>();
        BusinessUnit businessUnit = new BusinessUnit();
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
            // TODO: pass mvnoID manually 6/5/2025
            chargePojoList = entityRepository.findAllByChargecategoryInAndIsDeleteIsFalse(category).stream().map(data ->
                            chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(charge -> (charge.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || charge.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId())) &&
                            (Objects.isNull(charge.getBusinessType()) || (CommonConstants.RETAIL).equalsIgnoreCase(charge.getBusinessType()))).collect(Collectors.toList());
        } else if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)) {
            // TODO: pass mvnoID manually 6/5/2025
            chargePojoList = entityRepository.findAllByChargecategoryInAndIsDeleteIsFalse(category).stream().map(data ->
                            chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(charge -> (charge.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || charge.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId())) &&
                            (CommonConstants.ENTERPRISE).equalsIgnoreCase(charge.getBusinessType())).collect(Collectors.toList());
        }
        return chargePojoList;
    }

    public List<ChargePojo> getAllChargePojoByChargeTypeAndChargeCategory(String type, String category) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAllByChargetypeAndChargecategory(type, category).stream().map(data ->
                        chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                .stream().filter(charge -> (charge.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || charge.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId()))).collect(Collectors.toList());
    }

    public List<ChargePojo> getAllChargePojoByChargeCategory(String category) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAllByChargecategoryInAndIsDeleteIsFalse(Collections.singletonList(category)).stream().map(data ->
                        chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                .stream().filter(charge -> (charge.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || charge.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId()))).collect(Collectors.toList());
    }

    public List<ChargePojo> getAllCharge() {
        List<ChargePojo> chargePojoList = new ArrayList<>();
        BusinessUnit businessUnit = new BusinessUnit();

        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
            Integer currentMvnoId = getMvnoIdFromCurrentStaff();
            List<Long> currentBuIds = getBUIdsFromCurrentStaff();
            String businessType = CommonConstants.RETAIL;

            chargePojoList = entityRepository.findFilteredCharges(
                    currentMvnoId,
                    currentBuIds,
                    currentBuIds.size(),
                    businessType
            );

        }
       else if(businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)){
//            chargePojoList = entityRepository.findAll().stream()
//                    .map(data -> chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).sorted(Comparator.comparing(ChargePojo::getId).reversed()).collect(Collectors.toList())
//                    .stream().filter(chargePojo ->
//                            (chargePojo.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || chargePojo.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()) &&
//                                    (chargePojo.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(chargePojo.getBuId())) &&
//                                    ((CommonConstants.ENTERPRISE).equalsIgnoreCase(chargePojo.getBusinessType()))).collect(Collectors.toList());

            Integer currentMvnoId = getMvnoIdFromCurrentStaff();
            List<Long> currentBuIds = getBUIdsFromCurrentStaff();
            String businessType = CommonConstants.ENTERPRISE;

            chargePojoList = entityRepository.findFilteredCharges(
                    currentMvnoId,
                    currentBuIds,
                    currentBuIds.size(),
                    businessType
            );


        }
        return chargePojoList;
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = entityRepository.deleteVerify(id);
        Integer count2 = entityRepository.deleteVerifyForPlan(id);
        if (count == 0 && count2 == 0) {
            flag = true;
        }
        return flag;
    }

    public void deleteCharge(Integer id) throws Exception {
        String SUBMODULE = MODULE + "[deleteCharge()]";
        try {
            Boolean flag = this.deleteVerification(id);
            if (flag == true) {
                Charge charge = entityRepository.getOne(id);
                if (charge != null) {
                    charge.setIsDelete(true);
                }
                entityRepository.save(charge);
            } else {
                throw new RuntimeException(DeleteContant.CHARGE_DELETE_EXIST);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Charge getChargeForAdd() {
        return new Charge();
    }

    public Charge getChargeForEdit(Integer id) {
        return entityRepository.getOne(id);
    }

    public Charge saveCharge(Charge charge,Integer mvnoId) {
        // TODO: pass mvnoID manually 6/5/2025
    	if(mvnoId != null) {
            // TODO: pass mvnoID manually 6/5/2025
    		charge.setMvnoId(mvnoId);
            // TODO: pass mvnoID manually 6/5/2025
            charge.setMvnoName(mvnoRepository.findMvnoNameById(mvnoId.longValue()));
    	}
        return entityRepository.save(charge);
    }

    public ChargePojo save(ChargePojo pojo) throws Exception {
        String SUBMODULE = MODULE + "[save()]";
//        Charge oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff(null));

            Charge obj = chargeMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext());

            if(getBUIdsFromCurrentStaff().size() == 1)
                obj.setBuId(getBUIdsFromCurrentStaff().get(0));
            if(pojo.getServicesid() !=null || pojo.getServiceid()!=null) {
                obj = getServicesMappingId(pojo, obj);
            }

            if(pojo.getChargetype().equalsIgnoreCase(ChargeConstants.CHARGE_TYPE_ADVANCE) || pojo.getChargetype().equalsIgnoreCase(ChargeConstants.CHARGE_TYPE_RECURRING)){
                obj.setRoyalty_payable(pojo.getRoyalty_payable());
            }
            BusinessUnit businessUnit = new BusinessUnit();
            if(getBUIdsFromCurrentStaff().size()==1) {
                businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
            }
            if(getBUIdsFromCurrentStaff().size()!=0) {
                if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED)) {
                    obj.setBusinessType(CommonConstants.RETAIL);
                } else if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)) {
                    obj.setBusinessType(CommonConstants.ENTERPRISE);
                }
            }
//            if(oldObj!=null) {
//                log.info("Charge update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
//            }
            obj = saveCharge(obj,pojo.getMvnoId());
            String cacheKey = cacheKeys.CHARGE + obj.getId();
            cacheService.saveOrUpdateInCacheAsync(obj,cacheKey);
            TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO();
            taxDetailCountReqDTO.setChargeId(obj.getId());
            ClientService clientService = clientServiceSrv.getByNameAndMvnoId(ChargeConstants.CLIENT_SERVICE_LOCATION_ID,pojo.getMvnoId());
            if (clientService != null) {
                taxDetailCountReqDTO.setLocationId(Integer.parseInt(clientService.getValue()));
            }
           // obj.setTaxamount(taxService.taxCalculationByCharge(taxDetailCountReqDTO));

//            update(obj);
            pojo = chargeMapper.domainToDTO(obj, new CycleAvoidingMappingContext());
//            ChargeMessage customMessage = new ChargeMessage(obj);
   //         kafkaMessageSender.send(new KafkaMessageData(customMessage,ChargeMessage.class.getSimpleName()));
//            messageSender.send(customMessage, RabbitMqConstants.QUEUE_CHARGE_MGMTN_SUCCESS);
            return pojo;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }

    }

    private Charge getServicesMappingId(ChargePojo pojo, Charge charge) {
        ServiceChargeMapping serviceChargeMapping = new ServiceChargeMapping();
        List<Long> serviceChargeMappingList = pojo.getServiceid();
        List<Services> services = serviceRepository.findAllById(serviceChargeMappingList);
        if(services != null){
            charge.setServiceList(services);
        }
        return charge;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name,Integer mvnoId) {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                // TODO: pass mvnoID manually 6/5/2025
                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                    // TODO: pass mvnoID manually 6/5/2025
                else count = entityRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
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
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
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
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(mvnoId == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
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


    public Charge convertChargePojoToChargeModel(ChargePojo chargePojo) {
        String SUBMODULE = MODULE + "[convertChargePojoToChargeModel()]";
        Charge charge = new Charge();
        try {
            if (chargePojo.getId() != null) {
                charge.setId(chargePojo.getId());
            }
            charge = chargeMapper.dtoToDomain(chargePojo, new CycleAvoidingMappingContext());
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) != null) {
            	charge.setMvnoId(chargePojo.getMvnoId());
        	}
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return charge;
    }

    public ChargePojo convertChargeModelToChargePojo(Charge charge) {
        String SUBMODULE = MODULE + "[convertChargeModelToChargePojo()]";
        ChargePojo chargePojo = new ChargePojo();
        try {
            if (chargePojo.getId() != null) {
                charge.setId(chargePojo.getId());
            }


            chargePojo = chargeMapper.domainToDTO(charge, new CycleAvoidingMappingContext());
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) != null) {
            	chargePojo.setMvnoId(charge.getMvnoId());
        	}
            if(charge.getServiceList() !=null && charge.getServiceList().size()>0){
                List<Integer> serviceIds = new ArrayList<>();
                List<String> servicelist = new ArrayList<>();
                for(Services services : charge.getServiceList()){
                    serviceIds.add(Math.toIntExact(services.getId()));
                    servicelist.add(services.getServiceName());
                }
                chargePojo.setServicesid(serviceIds);
                chargePojo.setServiceNameList(servicelist);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return chargePojo;
    }

    public List<ChargePojo> convertResponseModelIntoPojo(List<Charge> chargeList) {
        String SUBMODULE = MODULE + "[convertResponseModelIntoPojo()]";
        List<ChargePojo> pojoListRes = new ArrayList<>();
        try {
            if (chargeList != null && chargeList.size() > 0) {
                for (Charge charge : chargeList) {
                    if (charge != null) {
                        Double taxAmount = taxService.getTaxAmountFromCharge(charge, null);
                        DecimalFormat df = new DecimalFormat("0.00");
                        taxAmount = Double.parseDouble(df.format(taxAmount));
                        charge.setTaxamount(taxAmount);
                    }
                    pojoListRes.add(convertChargeModelToChargePojo(charge));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    public void validateRequest(ChargePojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (null != pojo) {
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                if (pojo.getId() != null)
                    throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
            if (null == pojo.getChargetype() || pojo.getChargetype().isEmpty()) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.charge.charge.type.not.empty"), null);
            }
            if (!(pojo.getChargetype().equalsIgnoreCase(ChargeConstants.CHARGE_TYPE_ADVANCE)
                    || pojo.getChargetype().equalsIgnoreCase(ChargeConstants.CHARGE_TYPE_RECURRING)
                    || pojo.getChargetype().equalsIgnoreCase(ChargeConstants.CHARGE_TYPE_NON_RECURRING)
                    || pojo.getChargetype().equalsIgnoreCase(ChargeConstants.CHARGE_TYPE_REFUNDABLE)
                    || pojo.getChargetype().equalsIgnoreCase(ChargeConstants.CHARGE_TYPE_CUSTOMER_DIRECT)
                    || pojo.getChargetype().equalsIgnoreCase(ChargeConstants.CHARGE_TYPE_ADVANCE_RECURRING )
                    || pojo.getChargetype().equalsIgnoreCase(ChargeConstants.CHARGE_TYPE_VAS_CHARGE)
                    )) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.charge.type"), null);
            }
            if (null == pojo.getChargecategory() || pojo.getChargecategory().isEmpty()) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.charge.charge.category.not.empty"), null);
            }
            if (null == pojo.getName() || pojo.getName().isEmpty()) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.charge.charge.name.not.empty"), null);
            }
//            if (0 == pojo.getPrice()) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.charge.price.not.empty"), null);
//            }
            if ((operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE))
                    && pojo.getId() == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
            }
            if (null == pojo.getDesc() || pojo.getDesc().isEmpty() || pojo.getDesc().length() == 0) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.charge.desc.not.empty"), null);
            }
        }
    }

    public List<Charge> findAllByChargetype(String chargeTypeCustomerDirect) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAllByChargetype(chargeTypeCustomerDirect)
                .stream().filter(charge -> (charge.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || charge.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId()))).collect(Collectors.toList());
    }

    public List<ChargePojo> findAllByChargeCategories(List<String> catList) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAllByChargecategoryInAndIsDeleteIsFalse(catList).stream().map(data ->
                chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                .stream().filter(charge -> (charge.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || charge.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId()))).collect(Collectors.toList());
    }

    @Override
    public Page<Charge> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize,sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        pageRequest = generatePageRequest(page, pageSize,"id1", sortOrder);
                        return getChargeByNameOrTypeOrCategory(searchModel.getFilterValue(), pageRequest,mvnoId);
                    }
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.CHARGE_NAME)) {
                        return getChargeByName(searchModel.getFilterValue(), pageRequest,mvnoId);
                    }
                    else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.CHARGE_CATEGORY)) {
                        return getChargeByCategory(searchModel.getFilterValue(), pageRequest,mvnoId);
                    }
                    else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.CHARGE_TYPE)) {
                        return getChargeByType(searchModel.getFilterValue(), pageRequest,mvnoId);
                    }
                    else{
                        throw new RuntimeException("Please Provide Search Column!");
                    }
                }

            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public Page<Charge> getChargeByNameOrTypeOrCategory(String s1, PageRequest pageRequest,Integer mvnoId) {
        BusinessUnit businessUnit = new BusinessUnit();
        Page<Charge> charges = null;
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }

        QCharge qCharge=QCharge.charge;
        BooleanExpression isDeleted=qCharge.isDelete.eq(false);
        // TODO: pass mvnoID manually 6/5/2025
        BooleanExpression mvnoIds=qCharge.mvnoId.in(Arrays.asList(mvnoId, 1));
        BooleanExpression chargecategory=qCharge.chargecategory.toLowerCase().contains(s1.toLowerCase());
        BooleanExpression taxName=qCharge.tax.name.toLowerCase().contains(s1.toLowerCase());
        BooleanExpression chargeName=qCharge.name.toLowerCase().contains(s1.toLowerCase());
        BooleanExpression status=qCharge.status.toLowerCase().contains(s1.toLowerCase());
        BooleanExpression businessTypeRetail=qCharge.businessType.isNull().or(qCharge.businessType.equalsIgnoreCase("Retail"));
        BooleanExpression businessTypeEnterprise=qCharge.businessType.equalsIgnoreCase("Enterprise");

        BooleanExpression chargeAmount=qCharge.price.stringValue().contains(s1);
        List<CommonList> commonLists=commonListRepository.findAllByType("chargeType");
        commonLists=commonLists.stream().filter(x->x.getText().toLowerCase().contains(s1.toLowerCase())).collect(Collectors.toList());
        BooleanExpression chargeType=qCharge.chargetype.in(commonLists.stream().map(x->x.getValue()).collect(Collectors.toList()));

        BooleanExpression searchExpr = chargeName.or(chargecategory).or(chargeType).or(taxName).or(chargeAmount).or(status);
        BooleanExpression predicate = isDeleted.and(searchExpr);
        if (mvnoId != 1)  predicate = predicate.and(mvnoIds);

        if(getBUIdsFromCurrentStaff() != null && !getBUIdsFromCurrentStaff().isEmpty() && !Objects.isNull(businessUnit.getPlanBindingType())) {
            BooleanExpression mvnoIDs = qCharge.mvnoId.eq(mvnoId);
            BooleanExpression buids = qCharge.buId.in(getBUIdsFromCurrentStaff());
            predicate = predicate.and(mvnoIDs).and(buids);

            if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED)))   predicate = predicate.and(businessTypeRetail);

            if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED)))   predicate = predicate.and(businessTypeEnterprise);
        }
        charges = entityRepository.findAll(predicate, pageRequest);
        return charges;
    }
    public Page<Charge> getChargeByName(String s1, PageRequest pageRequest,Integer mvnoID) {
        BusinessUnit businessUnit = new BusinessUnit();
        Page<Charge> charges = null;
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        // TODO: pass mvnoID manually 6/5/2025
        if(mvnoID == 1) {
            charges = entityRepository.findAllByName(s1, pageRequest);
            return charges;
        }
        if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0 || Objects.isNull(businessUnit.getPlanBindingType())) {
            // TODO: pass mvnoID manually 6/5/2025
            charges= entityRepository.findAllByNameAndMvnoIdIn(s1, pageRequest, Arrays.asList(mvnoID , 1));
        } else if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED))) {
            // TODO: pass mvnoID manually 6/5/2025
            charges= entityRepository.findAllByNameAndMvnoIdInAndRetail(s1, pageRequest, mvnoID, getBUIdsFromCurrentStaff());
        }
        else if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND))) {
            // TODO: pass mvnoID manually 6/5/2025
            charges= entityRepository.findAllByNameAndMvnoIdInAndEnterprise(s1, pageRequest,mvnoID, getBUIdsFromCurrentStaff());
        }
        return charges;
    }

    public Page<Charge> getChargeByType(String s1, PageRequest pageRequest,Integer mvnoId) {
        BusinessUnit businessUnit = new BusinessUnit();
        Page<Charge> charges = null;
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        // TODO: pass mvnoID manually 6/5/2025
        if(mvnoId == 1) {
            charges = entityRepository.findAllByChargetype(s1, pageRequest);
            return charges;
        }
        if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0 || Objects.isNull(businessUnit.getPlanBindingType())) {
            // TODO: pass mvnoID manually 6/5/2025
            charges= entityRepository.findAllByChargetypeAndMvnoIdIn(s1, pageRequest, Arrays.asList(mvnoId , 1));
        } else if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED))) {
            // TODO: pass mvnoID manually 6/5/2025
            charges= entityRepository.findAllByChargetypeAndMvnoIdInAndRetail(s1, pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        }
        else if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND))) {
            // TODO: pass mvnoID manually 6/5/2025
            charges= entityRepository.findAllByChargetypeAndMvnoIdInAndEnterprise(s1, pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        }
        return charges;
    }

    public Page<Charge> getChargeByCategory(String s1, PageRequest pageRequest,Integer mvnoId) {
        BusinessUnit businessUnit = new BusinessUnit();
        Page<Charge> charges = null;
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        // TODO: pass mvnoID manually 6/5/2025
        if(mvnoId == 1) {
            charges = entityRepository.findAllByChargecategory(s1, pageRequest);
            return charges;
        }
        if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0 || Objects.isNull(businessUnit.getPlanBindingType())) {
        // TODO: pass mvnoID manually 6/5/2025
            charges= entityRepository.findAllByChargecategoryAndMvnoIdIn(s1, pageRequest, Arrays.asList(mvnoId , 1));
        } else if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED))) {
            // TODO: pass mvnoID manually 6/5/2025
            charges= entityRepository.findAllByChargecategoryAndMvnoIdInAndRetail(s1, pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        }
        else if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND))) {
            // TODO: pass mvnoID manually 6/5/2025
            charges= entityRepository.findAllByChargecategoryAndMvnoIdInAndEnterprise(s1, pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        }
        return charges;
    }

    public List<Charge> findAllByTaxId(Integer taxId) {
        QCharge qCharge = QCharge.charge;
        QPostpaidPlanCharge qPostpaidPlanCharge = QPostpaidPlanCharge.postpaidPlanCharge;
        JPAQuery<PostpaidPlanCharge> jpaQuery = new JPAQuery(entityManager);
        BooleanExpression booleanExpression = qCharge.isNotNull().and(qCharge.tax.id.eq(taxId))
                .and(qCharge.id.in(jpaQuery.select(qPostpaidPlanCharge.charge.id).from(qPostpaidPlanCharge).where(qPostpaidPlanCharge.charge.id.eq(qCharge.id))));
        // TODO: pass mvnoID manually 6/5/2025
        return IterableUtils.toList(entityRepository.findAll(booleanExpression))
                .stream().filter(charge -> (charge.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || charge.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId()))).collect(Collectors.toList());
    }

    @Override
    public Page<Charge> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder,
                             List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        Page<Charge> charges = null;
        BusinessUnit businessUnit = new BusinessUnit();
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            charges = entityRepository.findAll(pageRequest);
        if (null == filterList || 0 == filterList.size()) {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0 || Objects.isNull(businessUnit.getPlanBindingType())) {
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) != 1){
                    // TODO: pass mvnoID manually 6/5/2025
                    charges = entityRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff(null)));
                }else{
                    charges = entityRepository.findAllWithoutMvno(pageRequest);
                }

            } else if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED))) {
                // TODO: pass mvnoID manually 6/5/2025
                charges = entityRepository.findAllByRetail(pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            } else if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND))) {
                // TODO: pass mvnoID manually 6/5/2025
                charges = entityRepository.findAllByEnterprise(pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
        } else {
            charges = search(filterList, pageNumber, customPageSize, sortBy, sortOrder,getLoggedInMvnoId());
        }
        return  charges;
    }
    @Override
    public Page<Charge> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder,
                                List<GenericSearchModel> filterList,Integer mvnoId) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        Page<Charge> charges = null;
        BusinessUnit businessUnit = new BusinessUnit();
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId == 1)
            charges = entityRepository.findAll(pageRequest);
        if (null == filterList || 0 == filterList.size()) {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0 || Objects.isNull(businessUnit.getPlanBindingType())) {
                // TODO: pass mvnoID manually 6/5/2025
                if(mvnoId != 1){
                    // TODO: pass mvnoID manually 6/5/2025
                    charges = entityRepository.findAll(pageRequest, Arrays.asList(1, mvnoId));
                }else{
                    charges = entityRepository.findAllWithoutMvno(pageRequest);
                }

            } else if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED))) {
                // TODO: pass mvnoID manually 6/5/2025
                charges = entityRepository.findAllByRetail(pageRequest, mvnoId, getBUIdsFromCurrentStaff());
            } else if ((businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND))) {
                // TODO: pass mvnoID manually 6/5/2025
                charges = entityRepository.findAllByEnterprise(pageRequest, mvnoId, getBUIdsFromCurrentStaff());
            }
        } else {
            charges = search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
        }
        return  charges;
    }

    @Override
    public Charge get(Integer id,Integer mvnoId) {
        //TODO Redis
        String cacheKey = cacheKeys.CHARGE + id;
        Charge charge = null;
//        long startTime = System.currentTimeMillis();
        try {
            // Try to get data from the cache
            charge = (Charge) cacheService.getFromCache(cacheKey, Charge.class);
//            long endTimeCache = System.currentTimeMillis();
//            log.warn("Total time to get Charge from cache: " + (endTimeCache - startTime) + "ms");

            if (charge != null) {
                // Return the cached data if available
                return charge;
            }

            // Fetch from DB if not found in cache
            charge = super.get(id,mvnoId);

            if (charge != null) {
                // TODO: pass mvnoID manually 6/5/2025
//                Integer mvnoId = getMvnoIdFromCurrentStaff(null);

                if (mvnoId == 1 ||
                        ((charge.getMvnoId() == mvnoId || charge.getMvnoId() == 1) &&
                                (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().isEmpty() ||
                                        getBUIdsFromCurrentStaff().contains(charge.getBuId())))) {

                    // Cache the fetched data for future use
                    cacheService.putInCache(cacheKey, charge);
                    return charge;
                }
            }
        } catch (Exception e) {
            log.error("Error while fetching Charge: ", e);
        }

//        long endTime = System.currentTimeMillis();
//        log.warn("Total execution time for Charge.get: " + (endTime - startTime) + "ms");
        return null;
    }

    public Charge getEntityForUpdateAndDelete(Integer id,Integer mvnoId) {
        Charge charge = get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if(charge == null || (!(mvnoId == 1 || mvnoId == charge.getMvnoId().intValue())&& (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return charge;
    }
//    public List<ServiceChargeMapping> getchargeByService(String service) {
//        QCharge qCharge = QCharge.charge;
//        BooleanExpression exp = qCharge.isNotNull().and(qCharge.isDelete.eq(false));
//        exp = exp.and(qCharge.status.equalsIgnoreCase("active")).and(qCharge.service.in(service).or(qCharge.service.equalsIgnoreCase("null")));
////        List<Charge> list = entityRepository.getchargeByService(service);
//        List<Charge> list = (List<Charge>) entityRepository.findAll(exp);
//
//           List<ChargePojo> chargePojoList = getAllCharge();
//        chargePojoList.removeIf(x->!x.getStatus().equalsIgnoreCase("active"));
//        List<Charge> chargeList = chargeMapper.dtoToDomain(chargePojoList, new CycleAvoidingMappingContext());
//        List<Integer> chargeIdList = chargePojoList.stream().map(ChargePojo::getId).collect(Collectors.toList());
//                QServiceChargeMapping qServiceChargeMapping=QServiceChargeMapping.serviceChargeMapping;
//        BooleanExpression expression=qServiceChargeMapping.isNotNull();
//        expression=expression.and(qServiceChargeMapping.services.id.eq(Long.valueOf(service))).and(qServiceChargeMapping.Charge.isDelete.eq(false).and(qServiceChargeMapping.Charge.id.in(chargeIdList)));
//        List<ServiceChargeMapping> mappingList= (List<ServiceChargeMapping>) serviceChargemappingRepo.findAll(expression);
//
////        List<String> mappingList1 = new ArrayList<>();
////        List<String> chargeList = new ArrayList<>();
////        for (int i=0;i<mappingList.size();i++) {
////            mappingList1.add(mappingList.get(i).getCharge().getName());
////        }
//        return mappingList;
//    }

    public List<ServiceChargeMapping> getchargeByService(String serviceId, Integer mvnoId) {
        List<Integer> chargeIdList = getFilteredChargeIds(mvnoId);
        if (chargeIdList.isEmpty()) {
            return Collections.emptyList();
        }
        QServiceChargeMapping qServiceChargeMapping = QServiceChargeMapping.serviceChargeMapping;
        BooleanExpression expression = qServiceChargeMapping.services.id.eq(Long.valueOf(serviceId))
                .and(qServiceChargeMapping.Charge.isDelete.eq(false))
                .and(qServiceChargeMapping.Charge.id.in(chargeIdList));

        List<ServiceChargeMapping> mappingList =(List<ServiceChargeMapping>) serviceChargemappingRepo.findAll(expression);
        return mappingList;
    }

    public List<Integer> getFilteredChargeIds(Integer mvnoId) {
        List<Long> buIds = getBUIdsFromCurrentStaff();
        int buSize = buIds.size();
        // TODO: pass mvnoID manually 6/5/2025
//        Integer mvnoId = getMvnoIdFromCurrentStaff(null);

        BusinessUnit bu = null;
        if (buSize == 1) {
            bu = businessUnitRepository.findById(buIds.get(0)).orElse(null);
        }
        String planBindingType = (bu == null) ? null : bu.getPlanBindingType();

        return entityRepository.findFilteredChargeIds(planBindingType, mvnoId, buIds, buSize);
    }


    private void sendCustPaymentLinkMessage(String message, String customerName, Double paymentAmount, String url, Integer mvnoId, String countryCode, String mobileNumber, String emailId) {
        try {
            {
                String currencySymbol = String.valueOf(clientServiceRepository.findValueByNameandMvnoId("CURRENCY_SYMBOL",mvnoId));
                if (!currencySymbol.isEmpty()) {
                    Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_PAYMENT_LINK);
                    if (optionalTemplate.isPresent()) {
                        if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                            ChargeMessage chargeMessage=new ChargeMessage();
                            // Set message in queue to send notification after opt generated successfully.
                           // PaymentLinkMessage paymentLinkMessage = new PaymentLinkMessage(message, customerName, currencySymbol, paymentAmount, url, mvnoId, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, countryCode, mobileNumber, emailId);
                            Gson gson = new Gson();
                            gson.toJson(chargeMessage);
//                            messageSender.send(chargeMessage, RabbitMqConstants.QUEUE_CHARGE_MGMTN_SUCCESS);
                            kafkaMessageSender.send(new KafkaMessageData(chargeMessage,ChargeMessage.class.getSimpleName()));
                        }
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public List<ChargePojo> getAllCustomerDirectChargesByServiceId(Integer serviceId, Integer mvnoId) {
        List<ChargePojo> chargePojoList = new ArrayList<>();
        BusinessUnit businessUnit = new BusinessUnit();
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        QServiceChargeMapping qServiceChargeMapping = QServiceChargeMapping.serviceChargeMapping;
        BooleanExpression booleanExpression = qServiceChargeMapping.isNotNull().and(qServiceChargeMapping.isDeleted.eq(false).and(qServiceChargeMapping.services.id.eq(Long.valueOf(serviceId))));
        List<ServiceChargeMapping> serviceChargeMappingList = IterableUtils.toList(serviceChargemappingRepo.findAll(booleanExpression));
        List<Charge> chargesList = serviceChargeMappingList.stream().map(ServiceChargeMapping::getCharge).collect(Collectors.toList());
        List<Integer> chargeIdsList = chargesList.stream().map(Charge::getId).collect(Collectors.toList());
        QCharge qCharge = QCharge.charge;
        BooleanExpression expression = qCharge.isNotNull().and(qCharge.isDelete.eq(false).and(qCharge.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCharge.id.in(chargeIdsList)).and(qCharge.chargetype.equalsIgnoreCase(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT).or(qCharge.chargetype.equalsIgnoreCase(CommonConstants.CHARGE_TYPE_REFUNDABLE))));
        List<Charge> charges = IterableUtils.toList(entityRepository.findAll(expression));
//        chargePojoList = charges.stream().map(charge -> chargeMapper.domainToDTO(charge, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
            chargePojoList = charges.stream().map(data ->
                            chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    // TODO: pass mvnoID manually 6/5/2025
                    .stream().filter(charge -> (charge.getMvnoId() == mvnoId || charge.getMvnoId() == 1 || mvnoId == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId())) &&
                            (Objects.isNull(charge.getBusinessType()) || (CommonConstants.RETAIL).equalsIgnoreCase(charge.getBusinessType()))).collect(Collectors.toList());
        } else if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)) {
            // TODO: pass mvnoID manually 6/5/2025
            chargePojoList = entityRepository.findAllByChargetypeAndIsDeleteIsFalse(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT).stream().map(data ->
                            chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(charge -> (charge.getMvnoId() == mvnoId || charge.getMvnoId() == 1 || mvnoId == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId())) &&
                            (CommonConstants.ENTERPRISE).equalsIgnoreCase(charge.getBusinessType())).collect(Collectors.toList());
        }
        return chargePojoList;
    }

    public void sendCreateDataShared(ChargePojo pojo, Integer operation) throws Exception {
        try {
            Charge chargeEntity = convertChargePojoToChargeModel(pojo);
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                createDataSharedService.sendEntitySaveDataForAllMicroService(chargeEntity);
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                createDataSharedService.updateEntityDataForAllMicroService(chargeEntity);
            } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
                createDataSharedService.deleteEntityDataForAllMicroService(chargeEntity);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    @Transactional
    public void saveNewProductCharge(InventoryChargeMessage message) throws Exception{
        try {
            Charge charge = new Charge();
            charge.setName(message.getName());
            charge.setChargecategory(message.getChargecategory());
            charge.setChargetype(message.getChargetype());
            charge.setService(message.getService());
            charge.setDesc(message.getDesc());
            charge.setStatus(message.getStatus());
            charge.setActualprice(message.getActualprice());
            charge.setPrice(message.getPrice());
            Tax tax = taxRepository.findById(message.getTaxId()).orElse(null);
            charge.setTax(tax);
            charge.setTaxamount(message.getTaxamount());
            charge.setIsDelete(message.getIsDelete());
            charge.setIsinventorycharge(message.getIsinventorycharge());
            charge.setCreatedById(message.getCreatedById());
            charge.setLastModifiedById(message.getLastModifiedById());
            charge.setMvnoId(message.getMvnoId());
            charge.setProductId(message.getProductId());
            charge.setInventoryChargeType(CommonConstants.NEW);
            Charge saveNewProCharge = entityRepository.save(charge);
            createDataSharedService.sendEntitySaveDataForAllMicroService(saveNewProCharge);
            ApplicationLogger.logger.info("New Product Charge created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to update new product charge with name " + message.getName(), e.getMessage());
        }
    }

    @Transactional
    public void updateNewProductCharge(InventoryChargeMessage message) throws Exception{
        try {
            Charge newcharge = entityRepository.findByName(message.getName());
            if (newcharge != null) {
                newcharge.setName(message.getName());
                newcharge.setChargecategory(message.getChargecategory());
                newcharge.setChargetype(message.getChargetype());
                newcharge.setService(message.getService());
                newcharge.setDesc(message.getDesc());
                newcharge.setStatus(message.getStatus());
                newcharge.setActualprice(message.getActualprice());
                newcharge.setPrice(message.getPrice());
                Tax tax = taxRepository.findById(message.getTaxId()).orElse(null);
                newcharge.setTax(tax);
                newcharge.setTaxamount(message.getTaxamount());
                newcharge.setIsDelete(message.getIsDelete());
                newcharge.setIsinventorycharge(message.getIsinventorycharge());
                newcharge.setCreatedById(message.getCreatedById());
                newcharge.setLastModifiedById(message.getLastModifiedById());
                newcharge.setMvnoId(message.getMvnoId());
                newcharge.setProductId(message.getProductId());
                newcharge.setInventoryChargeType(CommonConstants.NEW);
                Charge updateNewProCharge = entityRepository.save(newcharge);
                createDataSharedService.updateEntityDataForAllMicroService(updateNewProCharge);
                ApplicationLogger.logger.info("New Product Charge updated successfully with name " + message.getName());
            } else {
                Charge charge = new Charge();
                charge.setName(message.getName());
                charge.setChargecategory(message.getChargecategory());
                charge.setChargetype(message.getChargetype());
                charge.setService(message.getService());
                charge.setDesc(message.getDesc());
                charge.setStatus(message.getStatus());
                charge.setActualprice(message.getActualprice());
                charge.setPrice(message.getPrice());
                Tax tax = taxRepository.findById(message.getTaxId()).orElse(null);
                tax.setId(message.getTaxId());
                charge.setTax(tax);
                charge.setTaxamount(message.getTaxamount());
                charge.setIsDelete(message.getIsDelete());
                charge.setIsinventorycharge(message.getIsinventorycharge());
                charge.setCreatedById(message.getCreatedById());
                charge.setLastModifiedById(message.getLastModifiedById());
                charge.setMvnoId(message.getMvnoId());
                charge.setProductId(message.getProductId());
                charge.setInventoryChargeType(CommonConstants.NEW);
                Charge saveNewProCharge = entityRepository.save(charge);
                createDataSharedService.sendEntitySaveDataForAllMicroService(saveNewProCharge);
                ApplicationLogger.logger.info("New Product Charge created successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to updated new product charge with name " + message.getName(), e.getMessage());
        }
    }
    @Transactional
    public void saveRefProductCharge(InventoryChargeMessage message) throws Exception{
        try {
            Charge charge = new Charge();
            charge.setName(message.getName());
            charge.setChargecategory(message.getChargecategory());
            charge.setChargetype(message.getChargetype());
            charge.setService(message.getService());
            charge.setDesc(message.getDesc());
            charge.setStatus(message.getStatus());
            charge.setActualprice(message.getActualprice());
            charge.setPrice(message.getPrice());
            Tax tax = taxRepository.findById(message.getTaxId()).orElse(null);
            charge.setTax(tax);
            charge.setTaxamount(message.getTaxamount());
            charge.setIsDelete(message.getIsDelete());
            charge.setIsinventorycharge(message.getIsinventorycharge());
            charge.setCreatedById(message.getCreatedById());
            charge.setLastModifiedById(message.getLastModifiedById());
            charge.setMvnoId(message.getMvnoId());
            charge.setProductId(message.getProductId());
            charge.setInventoryChargeType(CommonConstants.REFURBISHED);
            Charge saveRefProCharge = entityRepository.save(charge);
            createDataSharedService.sendEntitySaveDataForAllMicroService(saveRefProCharge);
            ApplicationLogger.logger.info("Refurbished Product Charge created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to created refurbished product charge with name " + message.getName(), e.getMessage());
        }
    }

    @Transactional
    public void updateRefProductCharge(InventoryChargeMessage message) throws Exception{
        try {
            Charge oldCharge = entityRepository.findByName(message.getName());
            if (oldCharge != null) {
                oldCharge.setName(message.getName());
                oldCharge.setChargecategory(message.getChargecategory());
                oldCharge.setChargetype(message.getChargetype());
                oldCharge.setService(message.getService());
                oldCharge.setDesc(message.getDesc());
                oldCharge.setStatus(message.getStatus());
                oldCharge.setActualprice(message.getActualprice());
                oldCharge.setPrice(message.getPrice());
                Tax tax = taxRepository.findById(message.getTaxId()).orElse(null);
                oldCharge.setTax(tax);
                oldCharge.setTaxamount(message.getTaxamount());
                oldCharge.setIsDelete(message.getIsDelete());
                oldCharge.setIsinventorycharge(message.getIsinventorycharge());
                oldCharge.setCreatedById(message.getCreatedById());
                oldCharge.setLastModifiedById(message.getLastModifiedById());
                oldCharge.setMvnoId(message.getMvnoId());
                oldCharge.setProductId(message.getProductId());
                oldCharge.setInventoryChargeType(CommonConstants.REFURBISHED);
                Charge updateRefProCharge = entityRepository.save(oldCharge);
                createDataSharedService.updateEntityDataForAllMicroService(updateRefProCharge);
                ApplicationLogger.logger.info("Refurbished Product Charge updated successfully with name " + message.getName());
            } else {
                Charge charge = new Charge();
                charge.setName(message.getName());
                charge.setChargecategory(message.getChargecategory());
                charge.setChargetype(message.getChargetype());
                charge.setService(message.getService());
                charge.setDesc(message.getDesc());
                charge.setStatus(message.getStatus());
                charge.setActualprice(message.getActualprice());
                charge.setPrice(message.getPrice());
                Tax tax = taxRepository.findById(message.getTaxId()).orElse(null);
                charge.setTax(tax);
                charge.setTaxamount(message.getTaxamount());
                charge.setIsDelete(message.getIsDelete());
                charge.setIsinventorycharge(message.getIsinventorycharge());
                charge.setCreatedById(message.getCreatedById());
                charge.setLastModifiedById(message.getLastModifiedById());
                charge.setMvnoId(message.getMvnoId());
                charge.setProductId(message.getProductId());
                charge.setInventoryChargeType(CommonConstants.REFURBISHED);
                Charge saveRefProCharge = entityRepository.save(charge);
                createDataSharedService.sendEntitySaveDataForAllMicroService(saveRefProCharge);
                ApplicationLogger.logger.info("Refurbished Product Charge created successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to update refurbished product charge with name " + message.getName(), e.getMessage());
        }
    }

    public List<ChargePojo> getchargeByPriceCompare(double price, String priceCompare, boolean isDeleted,Integer mvnoId) {
        if(priceCompare == null) {
            priceCompare = ChargeConstants.PRICE_EQUAL;
        }
        QCharge qCharge = QCharge.charge;
        BooleanExpression expression = qCharge.isNotNull();
        // TODO: pass mvnoID manually 6/5/2025
        if(mvnoId != -1)
            // TODO: pass mvnoID manually 6/5/2025
            expression = expression.and(qCharge.mvnoId.eq(mvnoId));

        switch (priceCompare) {
            case "equal":
                expression = expression.and(qCharge.price.eq(price));
                break;
            case "greater":
                expression = expression.and(qCharge.price.goe(price));
                break;
            case "less":
                expression = expression.and(qCharge.price.loe(price));
                break;
        }
        expression = expression.and(qCharge.isDelete.eq(isDeleted));
        List<Charge> charges = (List<Charge>) entityRepository.findAll(expression);
        if(!CollectionUtils.isEmpty(charges)) {
            return chargeMapper.domainToDTO(charges, new CycleAvoidingMappingContext());
        }
        return new ArrayList<>();
    }

    public List<ChargePojo> getChargeByMvno(Integer mvnoId) {
        // TODO: pass mvnoID manually 6/5/2025
        List<Integer> mvnoIds = new ArrayList<>(Collections.singletonList(getLoggedInMvnoId(null)));
        mvnoIds.add(mvnoId);
        return entityRepository.findAllChargeByMvnoId(mvnoIds);
    }
}
