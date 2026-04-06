package com.adopt.apigw.service.common;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.kafka.KafkaConstant;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.pojo.api.CustQuotaDtlsPojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustomerQuotaNotificationMessage;
import com.adopt.apigw.rabbitMq.message.SendQuotaMsg;
import com.adopt.apigw.repository.common.CustQuotaRepository;
import com.adopt.apigw.service.postpaid.CustPlanMappingService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.CommonConstants;
import com.google.gson.Gson;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustQuotaService extends AbstractService<CustQuotaDetails, CustQuotaDtlsPojo, Integer> {

    @Autowired
    private CustQuotaRepository entityRepository;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private CustomerMapper customerMapper;


    @Autowired
    private NotificationTemplateRepository templateRepository;

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    private CustQuotaRepository custQuotaRepository;



    @Override
    protected JpaRepository<CustQuotaDetails, Integer> getRepository() {
        return entityRepository;
    }

    public List<CustQuotaDtlsPojo> findAllByCustomersId(Integer id) {
        List<CustQuotaDetails> custQuotaDetailsList = entityRepository.findAllBycustPlanMapppingId(id);
        List<CustQuotaDtlsPojo> custQuotaDtlsPojoList = convertQuotaDomainListToQuotaPojoList(custQuotaDetailsList.stream().filter(o -> o.getCustomer().getId().equals(id)).collect(Collectors.toList()));
        return custQuotaDtlsPojoList;
    }

    public CustQuotaDetails getEntityById(Integer id) {
        return entityRepository.findById(id).get();
    }

    public CustQuotaDetails getByCustPackId(Integer id) {
        return entityRepository.findByCustPlanMappping_Id(id);
    }

    public CustQuotaDetails findByPostpaidPlanIdAndCustomerId(Integer id, Integer custId) {
        return entityRepository.findByPostpaidPlanIdAndCustomerId(id, custId);
    }

    public CustQuotaDetails convertCustQuotaDtlsPojoToCustQuotaDtls(CustQuotaDtlsPojo custQuotaDtlsPojo,Integer mvnoId) {
        CustQuotaDetails custQuotaDetails = new CustQuotaDetails();
        if (custQuotaDtlsPojo != null) {
            custQuotaDetails.setId(custQuotaDtlsPojo.getId());
            custQuotaDetails.setPostpaidPlan(postpaidPlanService.get(custQuotaDtlsPojo.getPlanId(),mvnoId));
            custQuotaDetails.setQuotaType(custQuotaDtlsPojo.getQuotaType());
            custQuotaDetails.setTotalQuota(custQuotaDtlsPojo.getTotalQuota());
            custQuotaDetails.setTotalQuotaKB(custQuotaDtlsPojo.getTotalQuotaKB());
            custQuotaDetails.setTimeTotalQuotaSec(custQuotaDtlsPojo.getTimeTotalQuotaSec());
            custQuotaDetails.setUsedQuota(custQuotaDtlsPojo.getUsedQuota());
            custQuotaDetails.setCreatedate(custQuotaDtlsPojo.getCreatedate());
            custQuotaDetails.setUpdatedate(custQuotaDtlsPojo.getUpdatedate());
            custQuotaDetails.setQuotaUnit(custQuotaDtlsPojo.getQuotaUnit());
            custQuotaDetails.setTimeTotalQuota(custQuotaDtlsPojo.getTimeTotalQuota());
            custQuotaDetails.setTimeQuotaUsed(custQuotaDtlsPojo.getTimeQuotaUsed());
            custQuotaDetails.setTimeQuotaUnit(custQuotaDtlsPojo.getTimeQuotaUnit());
            custQuotaDetails.setIsDelete(custQuotaDtlsPojo.getIsDelete());
            custQuotaDetails.setParentQuotaType(custQuotaDtlsPojo.getParentQuotaType());
            custQuotaDetails.setUsedQuota(custQuotaDtlsPojo.getUsedQuota());
            custQuotaDetails.setReservedQuotaInPer(custQuotaDtlsPojo.getReservedQuotaInPer());
            custQuotaDetails.setTotalReservedQuota(custQuotaDtlsPojo.getTotalReservedQuota());
            custQuotaDetails.setIsChunkAvailable(custQuotaDtlsPojo.isChunkAvailable());
            custQuotaDetails.setDownstreamprofileuid(custQuotaDtlsPojo.getDownstreamprofileuid());
            custQuotaDetails.setUpstreamprofileuid(custQuotaDtlsPojo.getUpstreamprofileuid());
            custQuotaDetails.setUsageQuotaType(custQuotaDtlsPojo.getUsageQuotaType());
            custQuotaDetails.setSkipQuotaUpdate(custQuotaDtlsPojo.skipQuotaUpdate);
            custQuotaDetails.setCustomer(customerMapper.dtoToDomain(custQuotaDtlsPojo.getCustomer(), new CycleAvoidingMappingContext()));
        }
        return custQuotaDetails;
    }

    public CustQuotaDtlsPojo convertCustQuotaDtlsToCustQuotaDtlsPojo(CustQuotaDetails custQuotaDetails) {
        CustQuotaDtlsPojo pojo = null;
        if (custQuotaDetails != null) {
            pojo = new CustQuotaDtlsPojo();
            pojo.setId(custQuotaDetails.getId());
            pojo.setPlanId(custQuotaDetails.getPostpaidPlan().getId());
            pojo.setPlanName(custQuotaDetails.getPostpaidPlan().getDisplayName());
            pojo.setQuotaType(custQuotaDetails.getQuotaType());
            pojo.setTotalQuota(custQuotaDetails.getTotalQuota());
            pojo.setTotalQuotaKB(custQuotaDetails.getTotalQuotaKB());
            pojo.setTimeTotalQuotaSec(custQuotaDetails.getTimeTotalQuotaSec());
            pojo.setUsedQuota(custQuotaDetails.getUsedQuota());
            pojo.setCreatedate(custQuotaDetails.getCreatedate());
            pojo.setUpdatedate(custQuotaDetails.getUpdatedate());
            pojo.setQuotaUnit(custQuotaDetails.getQuotaUnit());
            pojo.setTimeTotalQuota(custQuotaDetails.getTimeTotalQuota());
            pojo.setTimeQuotaUsed(custQuotaDetails.getTimeQuotaUsed());
            pojo.setTimeQuotaUnit(custQuotaDetails.getTimeQuotaUnit());
            pojo.setIsDelete(custQuotaDetails.getIsDelete());
            pojo.setPlanGroup(custQuotaDetails.getPostpaidPlan().getPlanGroup());
            Customers customers = custQuotaDetails.getCustomer();
            customers.setDebitDocList(null); // Get lazy error in mapper
//            pojo.setCustomer(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()));
            if(custQuotaDetails.getCustPlanMappping()!=null) {
                pojo.setCprId(custQuotaDetails.getCustPlanMappping().getId());
            }
            if(custQuotaDetails.getCurrentSessionUsageTime() != null){
                pojo.setCurrentSessionUsageTime(custQuotaDetails.getCurrentSessionUsageTime());
            }
            if(custQuotaDetails.getCurrentSessionUsageVolume() != null){
                pojo.setCurrentSessionUsageVolume(custQuotaDetails.getCurrentSessionUsageVolume());
            }
            pojo.setParentQuotaType(custQuotaDetails.getParentQuotaType());
            pojo.setUsedQuota(custQuotaDetails.getUsedQuota());
            if(pojo.getUpstreamprofileuid()!=null){
                pojo.setDownstreamprofileuid(custQuotaDetails.getDownstreamprofileuid());
                pojo.setUpstreamprofileuid(custQuotaDetails.getUpstreamprofileuid());
            }
            if(custQuotaDetails.getIsChunkAvailable() != null)
                pojo.setChunkAvailable(custQuotaDetails.getIsChunkAvailable());
            else
                pojo.setChunkAvailable(false);
            pojo.setReservedQuotaInPer(custQuotaDetails.getReservedQuotaInPer());
            pojo.setTotalReservedQuota(custQuotaDetails.getTotalReservedQuota());
            pojo.setUsageQuotaType(custQuotaDetails.getUsageQuotaType());
            pojo.setSkipQuotaUpdate(custQuotaDetails.getSkipQuotaUpdate());
        }
        return pojo;
    }

    public List<CustQuotaDtlsPojo> convertQuotaDomainListToQuotaPojoList(List<CustQuotaDetails> custQuotaDetailsList) {
        List<CustQuotaDtlsPojo> pojoList = new ArrayList<>();
        if (null != custQuotaDetailsList && custQuotaDetailsList.size() > 0) {
            for (CustQuotaDetails custQuotaDetails : custQuotaDetailsList) {
                pojoList.add(convertCustQuotaDtlsToCustQuotaDtlsPojo(custQuotaDetails));
            }
        }
        return pojoList;
    }

    public List<CustQuotaDetails> convertQuotaPojoListToQuotaDomainList(List<CustQuotaDtlsPojo> custQuotaDetailsPojoList,Integer mvnoId) {
        List<CustQuotaDetails> pojoList = new ArrayList<>();
        if (null != custQuotaDetailsPojoList && custQuotaDetailsPojoList.size() > 0) {
            for (CustQuotaDtlsPojo custQuotaDtlsPojo : custQuotaDetailsPojoList) {
                pojoList.add(convertCustQuotaDtlsPojoToCustQuotaDtls(custQuotaDtlsPojo,mvnoId));
            }
        }
        return pojoList;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Customer Quota");
        List<CustQuotaDtlsPojo> custQuotaDtlsPojos =  new ArrayList<>();
        List<CustQuotaDetails> custQuotaDetailsList = entityRepository.findAll();
        for(CustQuotaDetails custQuotaDetails : custQuotaDetailsList)
            custQuotaDtlsPojos.add(convertCustQuotaDtlsToCustQuotaDtlsPojo(custQuotaDetails));
        createExcel(workbook, sheet, CustQuotaDtlsPojo.class, custQuotaDtlsPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<CustQuotaDtlsPojo> custQuotaDtlsPojos =  new ArrayList<>();
        List<CustQuotaDetails> custQuotaDetailsList = entityRepository.findAll();
        for(CustQuotaDetails custQuotaDetails : custQuotaDetailsList)
            custQuotaDtlsPojos.add(convertCustQuotaDtlsToCustQuotaDtlsPojo(custQuotaDetails));
        createPDF(doc, CustQuotaDtlsPojo.class, custQuotaDtlsPojos, null);
    }

    public void sendNotificationOfQuota(Map<String , Object> quota){
        Optional<CustPlanMappping> custPlanMappping = custPlanMapppingRepository.findById(Integer.parseInt(quota.get("cprid").toString()));
        if(custPlanMappping.isPresent()) {
            if (custPlanMappping.get().getCustomer() != null) {
                Customers customers
                        = custPlanMappping.get().getCustomer();
                PostpaidPlan postpaidPlan = postpaidPlanService.findById(custPlanMappping.get().getPlanId());
                Long buId = null;
                if (custPlanMappping.get().getCustomer().getBuId() != null) {
                    buId = custPlanMappping.get().getCustomer().getBuId();
                }
                sendNotificationToCustomer(customers.getUsername(), customers.getMobile(), customers.getEmail(), Double.parseDouble(quota.get("percentage").toString()), postpaidPlan.getName(), customers.getCountryCode(), customers.getMvnoId(), buId,(long) getLoggedInStaffId());
            }
        }

    }

    public void sendNotificationToCustomer(String name , String mobileno , String email , Double percentage , String planname,String countrycode,Integer mvnoId ,Long buId,Long staffId){
        try {
            Integer roundedUppercentage = percentage.intValue();
            Optional<TemplateNotification> optionalTemplate = Optional.empty();
            if(percentage >= 100) {
                optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_QUOTA_EXHAUST_TEMPLATE);
                if (optionalTemplate.isPresent()) {
                    if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                        CustomerQuotaNotificationMessage customerQuotaNotificationMessage = new CustomerQuotaNotificationMessage(mobileno,email,optionalTemplate.get().getTemplateName(),optionalTemplate.get(),name,roundedUppercentage,planname,countrycode,mvnoId,buId,staffId);
                        Gson gson = new Gson();
                        gson.toJson(customerQuotaNotificationMessage);
//                        messageSender.send(customerQuotaNotificationMessage, RabbitMqConstants.QUEUE_SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER);
                        kafkaMessageSender.send(new KafkaMessageData(customerQuotaNotificationMessage,CustomerQuotaNotificationMessage.class.getSimpleName(),KafkaConstant.SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER));
                    }
                } else {
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                    System.out.println("Quota  Template not available.");
                }
            }
            else{
                optionalTemplate  = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_QUOTA_USAGE_TEMPLATE);
                if (optionalTemplate.isPresent()) {
                    if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                        CustomerQuotaNotificationMessage customerQuotaNotificationMessage = new CustomerQuotaNotificationMessage(mobileno,email,optionalTemplate.get().getTemplateName(),optionalTemplate.get(),name,roundedUppercentage,planname,countrycode,mvnoId,buId,staffId);
                        Gson gson = new Gson();
                        gson.toJson(customerQuotaNotificationMessage);
//                        messageSender.send(customerQuotaNotificationMessage, RabbitMqConstants.QUEUE_SEND_QUOTA_NOTIFICATION_CUSTOMER);
                        kafkaMessageSender.send(new KafkaMessageData(customerQuotaNotificationMessage,CustomerQuotaNotificationMessage.class.getSimpleName(), KafkaConstant.SEND_QUOTA_NOTIFICATION_CUSTOMER));
                    }
                } else {
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                    System.out.println("Quota  Template not available.");
                }
            }

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    public void saveCustQuotaIntrim(Map<String , Object> quota){
        Optional<CustQuotaDetails> custQuotaDetails = Optional.ofNullable(custQuotaRepository.findByCustPlanMappping_Id(Integer.parseInt(quota.get("cprid").toString())));
        if(custQuotaDetails.isPresent()){
            custQuotaDetails.get().setCurrentSessionUsageTime(Double.parseDouble(quota.get("currentsessionusagetime").toString()));
            custQuotaDetails.get().setCurrentSessionUsageVolume(Double.parseDouble(quota.get("currentsessionusagevolume").toString()));
            custQuotaRepository.save(custQuotaDetails.get());
        }
    }

    public String getParentQuotaType(Integer custId) {
        List<String> parentQuotaList = custQuotaRepository.getParentQuotaType(custId);
        if(!CollectionUtils.isEmpty(parentQuotaList)) {
            return parentQuotaList.get(0);
        }
        return CommonConstants.CUST_QUOTA_TYPE.INDIVIDUAL;
    }

    @Transactional
    public void setCustomerChunkQuota(SendQuotaMsg message){
        Map<String , Object> quota = message.getQuotaData();
        if(quota != null) {
            Optional<CustQuotaDetails> custQuotaDetails = Optional.ofNullable(custQuotaRepository.findByCustPlanMappping_Id(Integer.parseInt(quota.get("cprid").toString())));
            if(custQuotaDetails.isPresent()){
                if(quota.get("isChunkAvaibale") != null){
                    Boolean isChunkAvailable = Boolean.parseBoolean(quota.get("isChunkAvaibale").toString());
                custQuotaDetails.get().setIsChunkAvailable(isChunkAvailable);
                if(isChunkAvailable){
                    if(quota.get("reservequota") != null){
                        custQuotaDetails.get().setTotalReservedQuota(Double.parseDouble(quota.get("reservequota").toString()));
                    }
                }
                custQuotaRepository.save(custQuotaDetails.get());
                }
            }
        }

    }

    public LocalDate fincNearestQuotaResetDateUsingCprId(Integer cprId) {
        CustQuotaDetails custQuotaDetailsList = custQuotaRepository.findByCustPlanMappping_Id(cprId);
        if(custQuotaDetailsList == null){
            return null;
        }
        return calculateNextQuotaReset(custQuotaDetailsList, LocalDateTime.now());

    }

    public LocalDate calculateNextQuotaReset(CustQuotaDetails custQuotaDetails, LocalDateTime todayDate) {
        String quotaResetInterval = custQuotaDetails.getPostpaidPlan().getQuotaResetInterval();
        LocalDateTime lastQuotaReset = custQuotaDetails.getLastQuotaReset();

        // Default to today if no last reset date is available
        if (lastQuotaReset == null) {
            lastQuotaReset = todayDate;
        }

        switch (quotaResetInterval) {
            case "Daily":
                return LocalDate.from(lastQuotaReset.plus(1, ChronoUnit.DAYS));
            case "Weekly":
                return LocalDate.from(lastQuotaReset.plus(7, ChronoUnit.DAYS));
            case "Monthly":
                return LocalDate.from(lastQuotaReset.plus(1, ChronoUnit.MONTHS));
            case "Total":
                return LocalDate.MAX;
            default:
                throw new IllegalArgumentException("Invalid quota reset interval: " + quotaResetInterval);
        }
    }
}
