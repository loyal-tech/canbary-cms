package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.QCustomerServiceMapping;
import com.adopt.apigw.model.common.QCustomers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.qosPolicy.repository.QOSPolicyRepository;
import com.adopt.apigw.pojo.api.CustPlanMapppingDto;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.common.CustomerApproveRepo;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.repository.postpaid.PlanGroupRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.CustomerThreadService;
import com.adopt.apigw.service.common.CustQuotaService;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.EzBillServiceUtility;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.adopt.apigw.utils.StatusConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustPlanMappingService extends AbstractService<CustPlanMappping, CustPlanMapppingPojo, Long> {

    private static final Logger logger = LoggerFactory.getLogger(CustPlanMappingService.class);
    @Autowired
    CustomerApproveRepo customerApproveRepo;
    ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    @Autowired
    DbrService dbrService;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    private QOSPolicyRepository qosPolicyRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustQuotaService custQuotaService;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private PlanGroupRepository planGroupRepository;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private ClientServiceRepository clientServiceRepository;
    @Autowired
    private DebitDocRepository debitDocumentRepository;
    @Autowired
    private CreditDocService creditDocService;
    @Autowired
    private EzBillServiceUtility ezBillServiceUtility;
    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplates;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    CacheService cacheService;
    @Autowired
    CustomerThreadService customerThreadService;
    private static final Logger log = LoggerFactory.getLogger(CustomersService.class);
    public List<CustPlanMapppingPojo> findAllByCustomersId(Integer id) {
        System.out.println("...customerId: " + id);
        List<CustPlanMappping> planMapppingList = custPlanMappingRepository.findAllByCustomerId(id);
        List<CustPlanMapppingPojo> pojoList = new ArrayList<>();
        if (planMapppingList.size() > 0) {
            for (CustPlanMappping planMappping : planMapppingList) {
                pojoList.add(customerMapper.mapCustPlanMapToCustPlanMapPojo(planMappping, new CycleAvoidingMappingContext()));
            }
        }
        return pojoList;
    }

    public List<CustPlanMapppingPojo> findAllByCustomersIdAndPlanId(Integer customerId, Integer planId) {
        List<CustPlanMappping> planMapppingList = custPlanMappingRepository.findAllByCustomerIdAndPlanId(customerId, planId);
        List<CustPlanMapppingPojo> pojoList = new ArrayList<>();
        if (null != planMapppingList && planMapppingList.size() > 0) {
            for (CustPlanMappping planMappping : planMapppingList) {
                pojoList.add(customerMapper.mapCustPlanMapToCustPlanMapPojo(planMappping, new CycleAvoidingMappingContext()));
            }
        }
        return pojoList;
    }

    public CustPlanMappping getEntityById(Integer id) {
        CustPlanMappping planMappping = null;
        try {
//            CustPlanMappping planMappping = objectMapper.readValue("custPlanMappping:"+id, CustPlanMappping.class);
//            CustPlanMappping planMappping = CommonUtils.getFromCache("custPlanMappping:"+id, CustPlanMappping.class);
//            Long startTime = System.currentTimeMillis();
            String cacheKey = cacheKeys.CUSTPLANMAPPING + id;
            planMappping = (CustPlanMappping) cacheService.getFromCache(cacheKey, CustPlanMappping.class);
//            Long endtime = System.currentTimeMillis();
//            log.warn("::::::::: Total get postpaidplan mapping data from cache time ::::::::: "+ (endtime - startTime));

            if (planMappping != null) {
                return planMappping;
            }
            planMappping = custPlanMappingRepository.findById(id);
            if (planMappping != null) {
                cacheService.putInCacheWithExpire(cacheKey, planMappping);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
//        redisTemplate.opsForValue().set("custPlanMappping:" + custPlanMappping.getId(), objectMapper.writeValueAsString(custPlanMappping)); // Cache for future

        return planMappping;
    }

    public CustPlanMapppingPojo convertDomainToDto(CustPlanMappping custPlanMappping) {
        CustPlanMapppingPojo pojo = new CustPlanMapppingPojo();
        if (custPlanMappping != null) {
            pojo.setId(custPlanMappping.getId());
            pojo.setPlanId(custPlanMappping.getPlanId());
            pojo.setCustid(custPlanMappping.getCustomer().getId());
            pojo.setStartDate(custPlanMappping.getStartDate());
            pojo.setEndDate(custPlanMappping.getEndDate());
            pojo.setExpiryDate(custPlanMappping.getExpiryDate());
            pojo.setStatus(custPlanMappping.getStatus());
            pojo.setQospolicyId(null != custPlanMappping.getQospolicy() ? custPlanMappping.getQospolicy().getId() : null);
            pojo.setUploadqos(custPlanMappping.getUploadqos());
            pojo.setUploadts(custPlanMappping.getUploadts());
            pojo.setDownloadqos(custPlanMappping.getDownloadqos());
            pojo.setDownloadts(custPlanMappping.getDownloadts());
            pojo.setService(custPlanMappping.getService());
            pojo.setIsDelete(custPlanMappping.getIsDelete());
            pojo.setQuotaList(custQuotaService.convertQuotaDomainListToQuotaPojoList(custPlanMappping.getQuotaList()));
            pojo.setOfferPrice(custPlanMappping.getOfferPrice());
            pojo.setTaxAmount(custPlanMappping.getTaxAmount());
            pojo.setCreditdocid(custPlanMappping.getCreditdocid());
            pojo.setWalletBalUsed(custPlanMappping.getWalletBalUsed());
            pojo.setPurchaseType(custPlanMappping.getPurchaseType());
            pojo.setOnlinePurchaseId(custPlanMappping.getOnlinePurchaseId());
            pojo.setPurchaseFrom(custPlanMappping.getPurchaseFrom());
            pojo.setValidity(custPlanMappping.getValidity());
            pojo.setCustPlanStatus(custPlanMappping.getCustPlanStatus());
            pojo.setDiscount(custPlanMappping.getDiscount());
            pojo.setGraceDays(custPlanMappping.getGraceDays());
            pojo.setRemarks(custPlanMappping.getRemarks());
            if (custPlanMappping.getPlanGroup() != null)
                pojo.setPlangroupid(custPlanMappping.getPlanGroup().getPlanGroupId());
            pojo.setIsInvoiceCreated(custPlanMappping.getIsInvoiceCreated());
            pojo.setNewAmount(custPlanMappping.getNewAmount());
            pojo.setIsHold(custPlanMappping.getIsHold());
            pojo.setCustServiceMappingId(custPlanMappping.getCustServiceMappingId());
            pojo.setInvoiceType(custPlanMappping.getInvoiceType());
            pojo.setIsContainsCustomerInvoice(custPlanMappping.getIsContainsCustomerInvoice());
            pojo.setCustomerCpr(custPlanMappping.getCustomerCpr());
            pojo.setSkipQuotaUpdate(custPlanMappping.getSkipQuotaUpdate());
            pojo.setPlanValidityDays(custPlanMappping.getPlanValidityDays());
            if(custPlanMappping.getIstrialplan() != null && custPlanMappping.getIstrialplan())
            {
                pojo.setIstrialplan(true);
            }
            else{
                pojo.setIstrialplan(false);
            }
        }
        return pojo;
    }

    public CustPlanMappping convertDTOToDomain(CustPlanMapppingPojo custPlanMapppingPojo) {
        CustPlanMappping custPlanMappping = new CustPlanMappping();
        if (custPlanMapppingPojo != null) {
            custPlanMappping.setBillableCustomerId(custPlanMapppingPojo.getBillableCustomerId());
            custPlanMappping.setId(custPlanMapppingPojo.getId());
            custPlanMappping.setPlanId(custPlanMapppingPojo.getPlanId());
            custPlanMappping.setStartDate(custPlanMapppingPojo.getStartDate());
            custPlanMappping.setEndDate(custPlanMapppingPojo.getEndDate());
            custPlanMappping.setExpiryDate(custPlanMapppingPojo.getExpiryDate());
            custPlanMappping.setStatus(custPlanMapppingPojo.getStatus());
            if (custPlanMapppingPojo.getQospolicyId() != null)
                custPlanMappping.setQospolicy(qosPolicyRepository.findById(custPlanMapppingPojo.getQospolicyId()).get());

            custPlanMappping.setUploadqos(custPlanMapppingPojo.getUploadqos());
            custPlanMappping.setUploadts(custPlanMapppingPojo.getUploadts());
            custPlanMappping.setDownloadqos(custPlanMapppingPojo.getDownloadqos());
            custPlanMappping.setDownloadts(custPlanMapppingPojo.getDownloadts());
            custPlanMappping.setIsDelete(custPlanMapppingPojo.getIsDelete());
            custPlanMappping.setService(custPlanMapppingPojo.getService());
            custPlanMappping.setQuotaList(custQuotaService.convertQuotaPojoListToQuotaDomainList(custPlanMapppingPojo.getQuotaList(),getMvnoIdFromCurrentStaff(custPlanMapppingPojo.getCustid())));
            custPlanMappping.setCustomer(customerMapper.dtoToDomain(custPlanMapppingPojo.getCustomer(), new CycleAvoidingMappingContext()));
            if(custPlanMapppingPojo.getDiscountExpiryDate() != null){
                custPlanMappping.setDiscountExpiryDate(custPlanMapppingPojo.getDiscountExpiryDate());
            }
            custPlanMappping.setDiscount(custPlanMapppingPojo.getDiscount());
            custPlanMappping.setBillTo(custPlanMapppingPojo.getBillTo());
            custPlanMappping.setIsInvoiceToOrg(custPlanMapppingPojo.getIsInvoiceToOrg());
            custPlanMappping.setNewAmount(custPlanMapppingPojo.getNewAmount());
            custPlanMappping.setTaxAmount(custPlanMapppingPojo.getTaxAmount());
            custPlanMappping.setPurchaseFrom(custPlanMapppingPojo.getPurchaseFrom());
            custPlanMappping.setPurchaseType(custPlanMapppingPojo.getPurchaseType());
            custPlanMappping.setCustPlanStatus(custPlanMapppingPojo.getCustPlanStatus());
            custPlanMappping.setOfferPrice(custPlanMapppingPojo.getOfferPrice());
            custPlanMappping.setIsHold(custPlanMapppingPojo.getIsHold());
            if (custPlanMapppingPojo.getPlangroupid() != null) {
                Optional<PlanGroup> plangroup = planGroupRepository.findById(custPlanMapppingPojo.getPlangroupid());
                if (plangroup.isPresent()) custPlanMappping.setPlanGroup(plangroup.get());
            }
            custPlanMappping.setPlanValidityDays(custPlanMapppingPojo.getPlanValidityDays());
            custPlanMappping.setCustRefName(custPlanMapppingPojo.getCustRefName());
            custPlanMappping.setIsinvoicestop(custPlanMapppingPojo.getIsinvoicestop());
            custPlanMappping.setIstrialplan(custPlanMapppingPojo.getIstrialplan());
            if (custPlanMappping.getIstrialplan() != null && custPlanMappping.getIstrialplan()) {
                custPlanMappping.setIsTrialValidityDays(custPlanMapppingPojo.getIsTrialValidityDays());
                custPlanMappping.setTrialPlanValidityCount(custPlanMapppingPojo.getTrialPlanValidityCount());
            } else if (custPlanMappping.getIstrialplan() != null && !custPlanMappping.getIstrialplan()) {
                custPlanMappping.setIsTrialValidityDays(0.0);
                custPlanMappping.setTrialPlanValidityCount(0);


            }
            custPlanMappping.setIsInvoiceCreated(custPlanMapppingPojo.getIsInvoiceCreated());
            if (custPlanMapppingPojo.getCustServiceMappingId() != null) {
                custPlanMappping.setCustServiceMappingId(custPlanMapppingPojo.getCustServiceMappingId());
            }
            if (custPlanMapppingPojo.getInvoiceType() != null) {
                custPlanMappping.setInvoiceType(custPlanMapppingPojo.getInvoiceType());
            }
            if (custPlanMapppingPojo.getTraildebitdocid() != null) {
                custPlanMappping.setTraildebitdocid(custPlanMapppingPojo.getTraildebitdocid());
            }
            if (custPlanMapppingPojo.getRenewalId() != null) {
                custPlanMappping.setRenewalId(custPlanMapppingPojo.getRenewalId());
            }

            custPlanMappping.setIsContainsCustomerInvoice(custPlanMapppingPojo.getIsContainsCustomerInvoice());
            custPlanMappping.setCustomerCpr(custPlanMapppingPojo.getCustomerCpr());
            custPlanMappping.setSerialNumber(custPlanMapppingPojo.getSerialNumber());
            custPlanMappping.setServiceId(custPlanMapppingPojo.getServiceId());
            if (custPlanMapppingPojo.getVoucherId() != null) {
                custPlanMappping.setVoucherId(custPlanMapppingPojo.getVoucherId());
            }
        }
        return custPlanMappping;
    }

    @Override
    protected JpaRepository<CustPlanMappping, Long> getRepository() {
        return custPlanMappingRepository;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Customer Plan");
        List<CustPlanMapppingPojo> custPlanMapppingPojoList = new ArrayList<>();
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAll();
        for (CustPlanMappping custPlanMappping : custPlanMapppingList)
            custPlanMapppingPojoList.add(convertDomainToDto(custPlanMappping));
        createExcel(workbook, sheet, CustPlanMapppingPojo.class, custPlanMapppingPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<CustPlanMapppingPojo> custPlanMapppingPojoList = new ArrayList<>();
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAll();
        for (CustPlanMappping custPlanMappping : custPlanMapppingList)
            custPlanMapppingPojoList.add(convertDomainToDto(custPlanMappping));
        createPDF(doc, CustPlanMapppingPojo.class, custPlanMapppingPojoList, null);
    }

    public CustPlanMappping save(CustPlanMappping entity, String operation, Boolean isTriggerCoaDm) {
        return save(entity, operation, isTriggerCoaDm,null);
    }

    public CustPlanMappping save(CustPlanMappping entity, String operation, Boolean isTriggerCoaDm, CustomersPojo pojo) {
        if (!entity.getQuotaList().isEmpty()) {
            PostpaidPlan postpaidPlan = entity.getQuotaList().get(0).getPostpaidPlan();
            if (postpaidPlan.isUseQuota()) {
                entity.getQuotaList().get(0).setIsChunkAvailable(postpaidPlan.isUseQuota());
            } else {
                entity.getQuotaList().get(0).setIsChunkAvailable(false);
            }
        }
        CustPlanMappping custPlanMappping = custPlanMapppingRepository.saveAndFlush(entity);

        //add custplanmaping data in cache
        String cacheKey = cacheKeys.CUSTPLANMAPPING + custPlanMappping.getId();
        cacheService.putInCacheWithExpire(cacheKey, custPlanMappping);

        CustPlanMapppingPojo custPlanMapppingPojo = convertDomainToDto(custPlanMappping);
        if(pojo != null) {
//            Optional<Customers> customers = customersRepository.findById(pojo.getId());
            Optional<Customers> customers = Optional.ofNullable( customersRepository.findById(pojo.getId()).get());
            LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers.get(), customers.get().getNextBillDate());
            if(nextQuotaReset != null) {
                custPlanMapppingPojo.setNextQuotaResetDate(nextQuotaReset);
            } else {
                custPlanMapppingPojo.setNextQuotaResetDate(LocalDate.now());
            }
        }
        CustPackageRelMessage message = new CustPackageRelMessage(custPlanMapppingPojo, operation);
        message.setTriggerCoaDm(isTriggerCoaDm);
        LocalDateTime customerCreatedDate = custPlanMappping.getCustomer().getCreatedate();
        if (customerCreatedDate != null) {
            List<String> eventList = new ArrayList<>();
            eventList.add(CommonConstants.EVENTCONSTANTS.RENEW_PLAN);
            eventList.add(CommonConstants.EVENTCONSTANTS.NEW_BANDWIDTH_BOOSTER);
            eventList.add(CommonConstants.EVENTCONSTANTS.NEW_VOLUME_BOOSTER);
            eventList.add(CommonConstants.EVENTCONSTANTS.CHANGE_PLAN);
            if (customerCreatedDate.toLocalDate().isEqual(LocalDate.now()) && !eventList.contains(operation))
                message.setCustomerCreated(true);
        }
        kafkaMessageSender.send(new KafkaMessageData(message, CustPackageRelMessage.class.getSimpleName()));
        return custPlanMappping;

    }

    //    @Override
    public CustPlanMappping save(CustPlanMappping entity, String operation) {
        return save(entity, operation, true);
    }

    public CustPlanMappping saveCustPlan(CustPlanMappping entity, String operation) {
        for (CustQuotaDetails quota : entity.getQuotaList()) {
            if (quota.getCustPlanMappping() == null) {
                quota.setCustPlanMappping(entity);
            }
        }
        CustPlanMappping custPlanMappping = custPlanMapppingRepository.saveAndFlush(entity);
        String cacheKey = cacheKeys.CUSTPLANMAPPING + custPlanMappping.getId();
        try {
//            redisTemplates.opsForValue().set("custPlanMappping:" + custPlanMappping.getId(),custPlanMappping, Duration.ofMinutes(1)); // Cache for future
              cacheService.putInCacheWithExpire(cacheKey, custPlanMappping);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return custPlanMappping;
    }

    public void sendCustPlanMapping(Integer custId) {
        List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerId(custId);
        if (custPlanMappping != null) {
            for (CustPlanMappping item : custPlanMappping) {
                CustPlanMapppingPojo custPlanMapppingPojo = convertDomainToDto(item);
                CustPackageRelMessage message = new CustPackageRelMessage(custPlanMapppingPojo, "");
                //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_PACKAGE_REL);
                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
                //messageSender.send(message, RabbitMqConstants.QUEUE_CUSTOMER_PLAN_MAPPING_FOR_INTEGRATION);
                //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_PACKAGE_REL_KPI);
            }
        }
    }

    //    @Override
    public CustPlanMappping update(CustPlanMappping entity, String operation) {
        CustPlanMappping custPlanMappping = super.update(entity);
        CustPlanMapppingPojo custPlanMapppingPojo = convertDomainToDto(custPlanMappping);
        CustPackageRelMessage message = new CustPackageRelMessage(custPlanMapppingPojo, operation);
        message.setCustomerCreated(false);
        kafkaMessageSender.send(new KafkaMessageData(message, CustPackageRelMessage.class.getSimpleName()));
//        messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_PACKAGE_REL);
        //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_PACKAGE_REL_KPI);
        CustomerPackageRelMessage messageRadius = new CustomerPackageRelMessage(custPlanMapppingPojo, operation);
//        messageSender.send(messageRadius, RabbitMqConstants.QUEUE_APIGW_SERVICE_START_STOP);
        kafkaMessageSender.send(new KafkaMessageData(messageRadius, CustomerPackageRelMessage.class.getSimpleName()));
        return custPlanMappping;
    }

    public void updateCustPlanEndDateInRadius(CustPlanMappping custPlanMappping, String operation) {
        CustPlanMapppingPojo custPlanMapppingPojo = convertDomainToDto(custPlanMappping);
        if (custPlanMapppingPojo.getGraceDays() != null) {
            custPlanMapppingPojo.setExpiryDate(custPlanMapppingPojo.getExpiryDate().plusDays(custPlanMapppingPojo.getGraceDays()));
            custPlanMapppingPojo.setEndDate(custPlanMapppingPojo.getEndDate().plusDays(custPlanMapppingPojo.getGraceDays()));
        }
        CustomerPackageRelMessage message = new CustomerPackageRelMessage(custPlanMapppingPojo, operation);
        //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_SERVICE_START_STOP);
        kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

    }

    public Double getCustomerDiscount(Integer custId, Integer planId) {
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        BooleanExpression booleanExpression = qCustPlanMappping.isNotNull();
        booleanExpression = booleanExpression.and(qCustPlanMappping.expiryDate.after(LocalDateTime.now())).and(qCustPlanMappping.startDate.before(LocalDateTime.now()));
        booleanExpression = booleanExpression.and(qCustPlanMappping.customer.id.eq(custId));
        if (planId != null) booleanExpression = booleanExpression.and(qCustPlanMappping.planId.eq(planId));

        List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(booleanExpression);
        Double discount = custPlanMapppings.stream().filter(c -> c.getDiscount() != null).mapToDouble(CustPlanMappping::getDiscount).min().orElse(0);
        return discount;
    }

    public List<CustPlanMappping> findAllByDebitDocId(Integer id) {
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(qCustPlanMappping.debitdocid.eq(id.longValue()));
        return custPlanMapppings;

    }

    public GenericDataDTO getChangeDiscountApprovals(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", CommonConstants.SORT_ORDER_DESC);
//
            QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
            BooleanExpression booleanExpression = qCustomerServiceMapping.isDeleted.eq(false).and(qCustomerServiceMapping.nextStaff.eq(getLoggedInUserId())).and(qCustomerServiceMapping.discountFlowInProcess.equalsIgnoreCase("yes"));
            Page<CustomerServiceMapping> paginationList = customerServiceMappingRepository.findAll(booleanExpression, pageRequest);
            genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(paginationList.getTotalElements());
            genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
            genericDataDTO.setTotalPages(paginationList.getTotalPages());
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            return genericDataDTO;

        }
    }

    public List<CustPlanMappping> getAllWithConnectionNumberAndForDTVService(String connectionNumber) {
        return custPlanMappingRepository.getAllWithConnectionNumberAndForDTVService(connectionNumber);
    }

    /**
     * Change customer service status
     *
     * @param custServIds
     * @param status
     * @param remark
     * @param isChildCustomer
     * @return
     */
    @Transactional
    public List<CustomerServiceMapping> changeStatusOfCustServices(List<Integer> custServIds, String status, String remark, boolean isChildCustomer) {
        List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findAllByIdIn(custServIds);
        Customers customers = customersRepository.findById(customerServiceMappings.get(0).getCustId()).get();
        if (CollectionUtils.isEmpty(customerServiceMappings)) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer service not found!", null);
        }
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingIdIn(custServIds);
        List<PlanGroup> planGroups = new ArrayList<>();
        if (!status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP) && !status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)) {
            custPlanMapppingList.removeIf(custPlanMappping -> (custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP)
            || custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)));
            customerServiceMappings.removeIf(customerServiceMapping -> (customerServiceMapping.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP)
            || customerServiceMapping.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)));
            planGroups = custPlanMapppingList.stream().map(CustPlanMappping::getPlanGroup).collect(Collectors.toList());
        }

        try {
            switch (status) {
                case StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD: {
                    DbrHoldResumeMessage message = new DbrHoldResumeMessage(custPlanMapppingList.stream().map(x -> x.getId().longValue()).collect(Collectors.toList()), true);
                    kafkaMessageSender.send(new KafkaMessageData(message, DbrHoldResumeMessage.class.getSimpleName()));
//                    messageSender.send(message, RabbitMqConstants.QUEUE_DBR_SERVICE_HOLD_RESUME);
                    //dbrService.dbrHoldOnServicePause(custPlanMapppingList);
                    if (!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppingList.get(0).getCustomer(), planGroups, false);
                        if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            List<Integer> custSerIds = plangroupCustPlans.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(custSerIds)) {
                                List<CustomerServiceMapping> serviceMappings = customerServiceMappingRepository.findAllByIdIn(custSerIds);
                                if (!CollectionUtils.isEmpty(serviceMappings)) {
                                    customerServiceMappings.addAll(serviceMappings);
                                }
                            }
                        }
                    }
                    //Hold service
                    customerServiceMappings.forEach(customerServiceMapping -> {
                        customerServiceMapping.setStatus(status);
                        if (getLoggedInBy().length() > 0) {
                            customerServiceMapping.setServiceHoldBy(getLoggedInBy());
                        } else {
                            customerServiceMapping.setServiceHoldBy("Stop By Schedular");
                        }
                        if (customerServiceMapping.getUuid() != null) {
                            customersService.sendDeleteRequestForNMS(customerServiceMapping.getCustId(), customerServiceMapping.getId());
                        }
                        customerServiceMapping.setServiceHoldRemarks(remark);
                        customerServiceMapping.setServiceHoldDate(LocalDateTime.now());

                    });
                }
                break;
                case StatusConstants.CUSTOMER_SERVICE_STATUS.RESUME: {
                    DbrHoldResumeMessage message = new DbrHoldResumeMessage(custPlanMapppingList.stream().map(x -> x.getId().longValue()).collect(Collectors.toList()), false);
//                    messageSender.send(message, RabbitMqConstants.QUEUE_DBR_SERVICE_HOLD_RESUME);
                    kafkaMessageSender.send(new KafkaMessageData(message, DbrHoldResumeMessage.class.getSimpleName()));
                    //dbrService.dbrResumeOnServiceResume(custPlanMapppingList);
                    if (!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppingList.get(0).getCustomer(), planGroups, true);
                        if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            List<Integer> custSerIds = plangroupCustPlans.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(custSerIds)) {
                                List<CustomerServiceMapping> serviceMappings = customerServiceMappingRepository.findAllByIdIn(custSerIds);
                                if (!CollectionUtils.isEmpty(serviceMappings)) {
                                    customerServiceMappings.addAll(serviceMappings);
                                }
                            }
                        }
                    }
                    //Hold service
                    customerServiceMappings.forEach(customerServiceMapping -> {
                        customerServiceMapping.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE);
                        customerServiceMapping.setServiceResumeBy(getLoggedInBy());
                        customerServiceMapping.setServiceResumeRemarks(remark);
                        if (customerServiceMapping.getUuid() != null) {
                            customerServiceMapping.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVATION_PENDING);
                            String username = customersRepository.findCustomerName(customerServiceMapping.getCustId());
                            customersService.createHSNService(customerServiceMapping.getCustId(), username, getLoggedInUser().getUsername(), getLoggedInMvnoId(customerServiceMapping.getCustId()), customerServiceMapping.getCustId(), customerServiceMapping.getId());
                        }
                        customerServiceMapping.setServiceResumeDate(LocalDateTime.now());
                        Integer loggedInUserId = getLoggedInUserId();
                        Integer userId = loggedInUserId != null ? loggedInUserId : customers.getCreatedById();
                        Integer buId = customers.getBuId() == null ? null : Math.toIntExact(customers.getBuId());
                        customerThreadService.sendCustServiceActiveMessage(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getStatus(), customers.getMvnoId(), buId, Long.valueOf(userId));
                    });
                }
                break;
                default: {
                    if (!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppingList.get(0).getCustomer(), planGroups, false);
                        if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            List<Integer> custSerIds = plangroupCustPlans.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(custSerIds)) {
                                List<CustomerServiceMapping> serviceMappings = customerServiceMappingRepository.findAllByIdIn(custSerIds);
                                if (!CollectionUtils.isEmpty(serviceMappings)) {
                                    customerServiceMappings.addAll(serviceMappings);
                                }
                            }
                        }
                    }
                    customerServiceMappings.forEach(customerServiceMapping -> {
                        customerServiceMapping.setStatus(status);
                        customerServiceMapping.setRemarks(remark);
                    });
                }
            }
            if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
                changeStatusOfCustPlans(custPlanMapppingList, status, remark);
            }
            if (!isChildCustomer && customerServiceMappings.size() > 0) {
                List<CustomerServiceMapping> childServices = updateChildService(customerServiceMappings.get(0).getCustId(), customerServiceMappings, status, remark);
                if (!CollectionUtils.isEmpty(childServices)) customerServiceMappings.addAll(childServices);

            }
            //while doing service termination not create creditnote for same day
            if (!isChildCustomer && (status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP) || status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)) && (customers.getCreatedate().toLocalDate().isBefore(LocalDate.now())))
                createCNByCustService(customerServiceMappings, remark);
            customerServiceMappingRepository.saveAll(customerServiceMappings);
            if (status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)) {
                if (!customerServiceMappingRepository.existsByCustIdAndStatusNotIn(customers.getId(), Collections.singletonList(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE))) {
                    if (isCustomerTermintaitonWorkflowInProcess(customers.getId())) {
                        changeCustomerStatus(Collections.singletonList(customers), StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE);
                    }

                }
                customerServiceMappings.forEach(customerServiceMapping -> {
                    if (customerServiceMapping.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)) {
                        if (customerServiceMapping.getUuid() != null) {
                            customersService.sendDeleteRequestForNMS(customerServiceMapping.getCustId(), customerServiceMapping.getId());
                        }
                    }
                });

            }

        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Exception while updating customer service status: " + ex.getMessage(), null);
        }
        return customerServiceMappings;
    }


    /**
     * Change Customer Plan status
     *
     * @param custPlanMapppings
     * @param status
     * @param remark
     * @return
     */
    @Transactional
    public List<CustPlanMappping> changeStatusOfCustPlans(List<CustPlanMappping> custPlanMapppings, String status, String remark) {
        List<PlanGroup> planGroups = new ArrayList<>();
        if (!status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP))
            planGroups = custPlanMapppings.stream().map(CustPlanMappping::getPlanGroup).collect(Collectors.toList());
        try {
            switch (status) {
                case StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD: {
                    if (!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppings.get(0).getCustomer(), planGroups, false);
                        if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            custPlanMapppings.addAll(plangroupCustPlans);
                        }
                    }
                    //Hold service
                    custPlanMapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE)).collect(Collectors.toList());
                    custPlanMapppings.forEach(custPlanMappping -> {
                        custPlanMappping.setServiceHoldDate(LocalDateTime.now());
                        custPlanMappping.setCustPlanStatus(status);
                        custPlanMappping.setIsHold(Boolean.TRUE);
                    });
                    try {
                        ezBillServiceUtility.deactivateService(custPlanMapppings, 13);
                    } catch (Exception ex) {
                        logger.error("Error from ezBill " + ex.getMessage());
                    }
                }
                break;
                case StatusConstants.CUSTOMER_SERVICE_STATUS.RESUME: {
                    //Resume service
                    if (!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppings.get(0).getCustomer(), planGroups, true);
                        if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            custPlanMapppings.addAll(plangroupCustPlans);
                        }
                    }
                    custPlanMapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD)).collect(Collectors.toList());
                    boolean isFuturePlanAvailable = custPlanMapppings.stream().anyMatch(CustPlanMappping -> CustPlanMappping.getStartDate().isAfter(LocalDateTime.now()));
                    custPlanMapppings.forEach(custPlanMappping -> {
                        updateCustPlanEndDate(custPlanMappping.getServiceHoldDate(), LocalDateTime.now(), custPlanMappping, isFuturePlanAvailable);
                        if (custPlanMappping.getServiceHoldDate() != null) {
                            Long daysDiff = ChronoUnit.DAYS.between(custPlanMappping.getServiceHoldDate(), LocalDateTime.now());
                            if (custPlanMappping.getTotalHoldDays() != null)
                                daysDiff = daysDiff + custPlanMappping.getTotalHoldDays();
                            custPlanMappping.setTotalHoldDays(daysDiff.intValue());
                        } else {
                            custPlanMappping.setTotalHoldDays(1);
                        }
                        custPlanMappping.setIsHold(Boolean.FALSE);
                        if (custPlanMappping.getEzyBillServiceId() != null) {
                            try {
                                ezBillServiceUtility.manuallyActivate(custPlanMappping);
                            } catch (Exception ex) {
                                logger.error("Error from ezBill " + ex.getMessage());
                            }
                        }
                    });
                }
                break;
                case StatusConstants.CUSTOMER_SERVICE_STATUS.STOP:
                case StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE: {
                    if (!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppings.get(0).getCustomer(), planGroups, false);
                        if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            custPlanMapppings.addAll(plangroupCustPlans);
                        }
                    }
                    //terminate service
                    custPlanMapppings.removeIf(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE));
                    if (status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP))
                        custPlanMapppings.removeIf(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP));
                    custPlanMapppings.forEach(custPlanMappping -> {
                        custPlanMappping.setCustPlanStatus(status);
                        custPlanMappping.setEndDate(LocalDateTime.now());
                        custPlanMappping.setExpiryDate(LocalDateTime.now());
                        custPlanMappping.setIsVoid(Boolean.TRUE);
                        if (custPlanMappping.getStartDate().isAfter(custPlanMappping.getEndDate())) {
//                            custPlanMappping.setStartDate(LocalDateTime.now());
                            custPlanMappping.setEndDate(custPlanMappping.getStartDate().plusSeconds(1));
                            custPlanMappping.setExpiryDate(custPlanMappping.getStartDate().plusSeconds(1));
                        }
                    });
                    try {
                        ezBillServiceUtility.deactivateService(custPlanMapppings, 13);
                    } catch (Exception ex) {
                        logger.error("Error from ezBill " + ex.getMessage());
                    }
                }
                break;
                case StatusConstants.CUSTOMER_SERVICE_STATUS.INGRACE: {
                    //InGrace service
                    if (!CollectionUtils.isEmpty(planGroups)) {
                        List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppings.get(0).getCustomer(), planGroups, false);
                        if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                            custPlanMapppings.addAll(plangroupCustPlans);
                            custPlanMapppings = custPlanMapppings.stream().filter(UtilsCommon.distinctByKey(CustPlanMappping::getId)).collect(Collectors.toList());
                        }
                    }
                    // TODO: pass mvnoID manually 6/5/2025
                    Integer graceDays = Integer.valueOf(clientServiceRepository.findValueByNameandMvnoId("graceperiod", getLoggedInMvnoId(null)));
                    Long systemPromiseToPayCount = Long.valueOf(clientServiceRepository.findValueByNameandMvnoId(ClientServiceConstant.PROMISETOPAY_COUNT, getLoggedInMvnoId(null)));
                    custPlanMapppings.removeIf(custPlanMappping -> custPlanMappping.getPurchaseType().equalsIgnoreCase("Volume Booster") || custPlanMappping.getPurchaseType().equalsIgnoreCase("Bandwidthbooster") || custPlanMappping.getPurchaseType().equalsIgnoreCase("DTV Addon") || custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP));
                    custPlanMapppings.forEach(custPlanMappping -> {
                        Long count = null;
                        if (custPlanMappping.getPromisetopay_renew_count() == null) {
                            count = 0L;
                        } else {
                            count = custPlanMappping.getPromisetopay_renew_count();
                        }
                        if (count < systemPromiseToPayCount) {


                            count = count + 1;
                            LocalDateTime endDate = LocalDateTime.now();//custPlanMappping.get().getEndDate();
                            custPlanMappping.setEndDate(endDate.plusDays(graceDays));
                            custPlanMappping.setExpiryDate(endDate.plusDays(graceDays));
                            custPlanMappping.setGraceDays(graceDays);
                            custPlanMappping.setPromise_to_pay_remarks(remark);
                            custPlanMappping.setGraceDateTime(custPlanMappping.getEndDate().plusDays(graceDays));
                            custPlanMappping.setPromisetopay_renew_count(count);
                            custPlanMappping.setPromise_to_pay_startdate(LocalDateTime.now());
                            LocalDateTime promiseToPayEndDate = LocalDateTime.now().plusDays(graceDays);
                            custPlanMappping.setPromise_to_pay_enddate(promiseToPayEndDate);
                            custPlanMappping.setCustPlanStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.INGRACE);
                            custPlanMappingRepository.save(custPlanMappping);
                            try {
                                ezBillServiceUtility.extendExpiryDateInEZBill(custPlanMappping, custPlanMappping.getEndDate());
                            } catch (Exception ex) {
                                logger.error("Error from ezBill " + ex.getMessage());
                            }
                            //customer = custPlanMappping.getCustomer();
                        } else {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Promise to pay has already been used.", null);
                        }

                    });

                }
                break;
                default: {
                    custPlanMapppings.forEach(custPlanMappping -> {
                        custPlanMappping.setCustPlanStatus(status);
                        custPlanMappping.setStatus(status);
                    });
                }
            }
            if (status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE) || status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP)) {
                if (!CollectionUtils.isEmpty(planGroups) && !CollectionUtils.isEmpty(custPlanMapppings)) {
                    List<CustPlanMappping> plangroupCustPlans = getCustPlanMappingByPlanGroup(custPlanMapppings.get(0).getCustomer(), planGroups, false);
//                    plangroupCustPlans.removeIf(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)
//                            || custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP));
                    if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                        plangroupCustPlans = plangroupCustPlans.stream().peek(custPlanMappping -> custPlanMappping.setPlanGroup(null)).collect(Collectors.toList());
                        custPlanMapppings.addAll(plangroupCustPlans);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                custPlanMapppings.forEach(custPlanMappping -> {
                    updateEndAndStartDate(custPlanMappping, custPlanMappping.getEndDate());
                });
                return custPlanMappingRepository.saveAll(custPlanMapppings);
            } else return null;
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Exception while updating customer service status: " + ex.getMessage(), null);
        }
    }

    public List<CustPlanMappping> getCustPlanMappingByPlanGroup(Customers customers, List<PlanGroup> planGroups, boolean isHold) {
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerIsAndPlanGroupInAndIsHold(customers, planGroups, isHold);
        return custPlanMapppingList;
    }

    @Transactional
    public void createCNByCustService(List<CustomerServiceMapping> customerServiceMappings, String remarks) {
        List<Integer> custServiceIds = customerServiceMappings.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(custServiceIds)) {
            List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.getDebitDocIdByCustServiceMappingIdInCprIds(custServiceIds);
            if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                List<Integer> debitDocIds = custPlanMapppings.stream().map(CustPlanMappping::getDebitdocid).filter(Objects::nonNull).map(Long::intValue).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(debitDocIds)) {
                    List<DebitDocument> debitDocuments = debitDocumentRepository.findAllByIdInAndBillrunstatusIsNot(debitDocIds, StatusConstants.INVOICE_STATUS.VOID);
                    for (DebitDocument debitDocument : debitDocuments) {
                        creditDocService.creatCreditNotAsPerService(debitDocument, customerServiceMappings, remarks, Boolean.FALSE, null);
                    }
                }
            }
        }
    }

    /**
     * Update Customer plan end date with future plan flag
     *
     * @param fromDate
     * @param toDate
     * @param custPlanMappping
     * @param isFuturePlanAvailable
     * @return CustPlanMappping
     */
    @Transactional
    public CustPlanMappping updateCustPlanEndDate(LocalDateTime fromDate, LocalDateTime toDate, CustPlanMappping custPlanMappping, boolean isFuturePlanAvailable) {
        if (fromDate == null) fromDate = LocalDateTime.now();
        if (fromDate == null) toDate = LocalDateTime.now();
        Long daysDiff = ChronoUnit.DAYS.between(fromDate.toLocalDate(), toDate.toLocalDate());
        final LocalDateTime endDate = custPlanMappping.getEndDate();
        if (daysDiff > 1) {
            if (isFuturePlanAvailable && custPlanMappping.getStartDate().isAfter(LocalDateTime.now()))
                custPlanMappping.setEndDate(endDate.plusDays(daysDiff));
            else if (!isFuturePlanAvailable) custPlanMappping.setEndDate(endDate.plusDays(daysDiff));
            else custPlanMappping.setEndDate(endDate);
        } else {
            Long timeDiff = ChronoUnit.MINUTES.between(fromDate, LocalDateTime.now());
            if (isFuturePlanAvailable && custPlanMappping.getStartDate().isAfter(LocalDateTime.now()))
                custPlanMappping.setEndDate(endDate.plusMinutes(timeDiff));

            else if (!isFuturePlanAvailable) custPlanMappping.setEndDate(endDate.plusMinutes(timeDiff));
            else custPlanMappping.setEndDate(endDate);
        }
        if (custPlanMappping.getEndDate().toLocalDate().isAfter(LocalDate.now()) || custPlanMappping.getEndDate().toLocalDate().isEqual(LocalDate.now()))
            custPlanMappping.setCustPlanStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE);
        return custPlanMappping;
    }

    /**
     * Change customer status
     *
     * @param customers
     * @return
     */
    public List<Customers> changeCustomerStatus(List<Customers> customers, String status) {
        try {
            switch (status) {
                default: {
                    customers = customers.stream().peek(customer -> customer.setStatus(status)).collect(Collectors.toList());
                }
            }
            customersRepository.saveAll(customers);
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Exception while updating customer status: " + ex.getMessage(), null);
        }
        return customers;
    }

    /**
     * Update child customer
     *
     * @param parentCustid
     * @param customerServiceMappings
     * @param status
     * @param remark
     */
    @Transactional
    public List<CustomerServiceMapping> updateChildService(Integer parentCustid, List<CustomerServiceMapping> customerServiceMappings, String status, String remark) {
        List<CustomerServiceMapping> serviceMappings = new ArrayList<>();
        //child cust details extraction from customers table
        QCustomers qCustomers = QCustomers.customers;
        BooleanExpression exp = qCustomers.isNotNull().and(qCustomers.parentCustomers.id.eq(parentCustid));
        exp = exp.and(qCustomers.status.equalsIgnoreCase("Active"));
        List<Customers> childCust = (List<Customers>) customersRepository.findAll(exp);
        if (!CollectionUtils.isEmpty(childCust)) {
            List<List<CustomerServiceMapping>> customerServiceMappingList = childCust.stream().map(Customers::getCustomerServiceMappingList).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(customerServiceMappingList)) {
                for (List<CustomerServiceMapping> list : customerServiceMappingList) {
                    List<Long> existingIds = customerServiceMappings.stream().map(CustomerServiceMapping::getServiceId).collect(Collectors.toList());
//                    List<Integer> custServiceIds = list.stream().filter(custServiceMapping -> custServiceMapping.getInvoiceType() != null  && custServiceMapping.getInvoiceType().equalsIgnoreCase(StatusConstants.INVOICE_TYPE.GROUP)).map(CustomerServiceMapping::getId).collect(Collectors.toList());

                    List<Integer> custServiceIds = list.stream().filter(custServiceMapping -> custServiceMapping.getInvoiceType() != null && existingIds.contains(custServiceMapping.getServiceId()) && custServiceMapping.getInvoiceType().equalsIgnoreCase(StatusConstants.INVOICE_TYPE.GROUP)).map(CustomerServiceMapping::getId).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(custServiceIds)) {
                        List<CustomerServiceMapping> childServices = changeStatusOfCustServices(custServiceIds, status, remark, true);
                        if (!CollectionUtils.isEmpty(childServices)) serviceMappings.addAll(childServices);
                    }
                }
            }
        }
        return serviceMappings;
    }

    /**
     * Update service on radius
     *
     * @param custPlanMappping
     * @param updatedDateTime
     */
    public void updateEndAndStartDate(CustPlanMappping custPlanMappping, LocalDateTime updatedDateTime) {
        custPlanMappping.setEndDate(updatedDateTime);
        custPlanMappping.setExpiryDate(updatedDateTime);
        updateCustPlanEndDateInRadius(custPlanMappping, "");
    }

    public String getLoggedInBy() {
        if (getLoggedInUser() != null) {
            return getLoggedInUser().getFirstName() + " " + getLoggedInUser().getLastName();
        } else {
            return "";
        }
    }

    public void updateCustPlanStatus(OrganizationInvoiceRejectMesssage message) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

            for (CustPlanMapppingDto custPlanMapppingDto : message.getCustPlanMapppingDtos()) {
                Optional<CustPlanMappping> custPlanMappping = custPlanMapppingRepository.findById(custPlanMapppingDto.getId());
                if (custPlanMappping.isPresent()) {
                    custPlanMappping.get().setCustPlanStatus(custPlanMapppingDto.getCustPlanStatus());
                    custPlanMappping.get().setEndDate(LocalDateTime.parse(custPlanMapppingDto.getEndDateString(), formatter));
                    custPlanMappping.get().setExpiryDate(LocalDateTime.parse(custPlanMapppingDto.getExpirydateString(), formatter));
                    custPlanMapppingRepository.save(custPlanMappping.get());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void expirePlanByCprId(Integer id) {
        CustPlanMappping custPlanMappping = custPlanMapppingRepository.findById(id).get();
        custPlanMappping.setCustPlanStatus(CommonConstants.STOP_STATUS);

        if (custPlanMappping.getStartDate().isAfter(LocalDateTime.now())) {
            custPlanMappping.setStartDate(LocalDateTime.now().minusMinutes(1));
            custPlanMappping.setEndDate(LocalDateTime.now());
            custPlanMappping.setExpiryDate(LocalDateTime.now());
        } else {
            custPlanMappping.setEndDate(LocalDateTime.now().minusMinutes(1));
            custPlanMappping.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        }
        if (custPlanMappping.getStartDate().isAfter(custPlanMappping.getEndDate())) {
            custPlanMappping.setStartDate(LocalDateTime.now());
            custPlanMappping.setEndDate(custPlanMappping.getStartDate().plusSeconds(1));
            custPlanMappping.setExpiryDate(custPlanMappping.getStartDate().plusSeconds(1));
        }
        custPlanMappping.setIsVoid(true);
        custPlanMapppingRepository.save(custPlanMappping);
        CustPlanMapppingPojo custPlanMapppingPojo = new CustPlanMapppingPojo();
        updateCustPlanEndDateInRadius(custPlanMappping, "");
    }

    public void updateCustPlanMapping(UpdateCustplanMappingMessage message) {
        try {
            List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
            for (CustPlanMapppingDto dto : message.getCustPlanMapppingDtos()) {
                CustPlanMappping custPlanMappping = custPlanMapppingRepository.findById(dto.getId()).get();
                custPlanMappping.setStartDate(LocalDateTime.parse(dto.getStartDateString(), formatter2));
                custPlanMappping.setEndDate(LocalDateTime.parse(dto.getEndDateString(), formatter));
                custPlanMappping.setExpiryDate(LocalDateTime.parse(dto.getExpirydateString(), formatter));
                custPlanMappping.setGraceDateTime(LocalDateTime.parse(dto.getGraceDateTime(), formatter));
                custPlanMappping.setPromise_to_pay_startdate(LocalDateTime.parse(dto.getPromise_to_pay_startdate(), formatter));
                custPlanMappping.setPromise_to_pay_enddate(LocalDateTime.parse(dto.getPromise_to_pay_enddate(), formatter));
                custPlanMappping.setCustPlanStatus(dto.getCustPlanStatus());
                custPlanMappping.setGraceDays(dto.getGraceDays());
                custPlanMappping.setPromise_to_pay_remarks(dto.getPromise_to_pay_remarks());
                custPlanMappping.setPromisetopay_renew_count(dto.getPromisetopay_renew_count());
                custPlanMapppingList.add(custPlanMappping);
            }
            custPlanMapppingRepository.saveAll(custPlanMapppingList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isCustomerTermintaitonWorkflowInProcess(Integer custId) {
        QCustomerApprove qCustomerApprove = com.adopt.apigw.model.common.QCustomerApprove.customerApprove;
        BooleanExpression booleanExpression = qCustomerApprove.isNotNull().and(qCustomerApprove.customerID.eq(custId)).and(qCustomerApprove.status.equalsIgnoreCase("pending"));
        CustomerApprove customerApprove = customerApproveRepo.findOne(booleanExpression).orElse(null);
        return customerApprove == null;
    }

    public List<DebitDocument> getUnpaidDebitDoc(List<Integer> serviceId) {
        List<DebitDocument> debitDocuments = new ArrayList<>();
        try {
            List<Integer> cprId = custPlanMappingRepository.getAllByCustServiceMappingIn(serviceId);
            debitDocuments = debitDocumentRepository.findAllByCustpackrelid(cprId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return debitDocuments;
    }


    public CustPlanMappping getCustPlanMappingBYCustId(Integer custId) {
        String cacheKey = cacheKeys.CUSTPLANMAPPING_CUSTID + custId;

        try {
            CustPlanMappping mapping = (CustPlanMappping) cacheService.getFromCache(cacheKey, CustPlanMappping.class);
            if (mapping != null) {
                return mapping;
            }

            CustPlanMappping custMapping = custPlanMappingRepository.findById(custId);
            if (custMapping != null) {
                cacheService.putInCacheWithExpire(cacheKey, custMapping);
                return custMapping;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
