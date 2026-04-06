package com.adopt.apigw.service;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.CustPlanMapppingRepository;
import com.adopt.apigw.model.postpaid.CustomerMapper;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.subscriber.model.Constants;
import com.adopt.apigw.modules.subscriber.model.CustomerPlansModel;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustServiceActiveMsg;
import com.adopt.apigw.rabbitMq.message.CustServiceInActiveMsg;
import com.adopt.apigw.rabbitMq.message.CustomMessage;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.schedulers.CustomerScheduler;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.EzBillServiceUtility;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.StatusConstants;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

//@Service
//@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class CustomerThreadService{

//    @Autowired
//    public CustomerScheduler customerScheduler;
    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    MessageSender messageSender;
    @Autowired
    NotificationTemplateRepository templateRepository;

    @Autowired
    private EzBillServiceUtility ezBillServiceUtility;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    CustomersService customersService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    public Runnable newRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                CustomersRepository customersRepository;
                CustPlanMappingRepository custPlanMappingRepository;
                CustomerServiceMappingRepository customerServiceMappingRepository;
                ClientServiceRepository clientServiceRepository;

                SchedulerAudit schedulerAudit = new SchedulerAudit();
                schedulerAudit.setStartTime(LocalDateTime.now());
                schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.SCHEDULAR_AUDIT_FOR_UPDATE_CUSTOMER_AND_ITS_SERVICE);

                try {
                    System.out.println("***** cronJobTimeForUpdateCustomerAndItsServicesStatus Started !!! *****");
                    updateCustomers();
                    System.out.println("***** cronJobTimeForUpdateCustomerAndItsServicesStatus Ended !!! *****");
                    schedulerAudit.setEndTime(LocalDateTime.now());
                    schedulerAudit.setDescription("Customer update and its service Run Successfully");
                    schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                } catch (Exception ex) {
                    schedulerAudit.setEndTime(LocalDateTime.now());
                    schedulerAudit.setDescription(ex.getMessage());
                    schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                } finally {
                    schedulerAuditService.saveEntity(schedulerAudit);
                }
            }
        };
    }

    public void updateCustomers(){
        List<String> customerStatusList = new ArrayList<String>();
        customerStatusList.add(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE);
        customerStatusList.add(StatusConstants.CUSTOMER_SERVICE_STATUS.INAVCTIVE);
//        customerStatusList.add(StatusConstants.CUSTOMER_SERVICE_STATUS.SUSPEND);

        List<Integer> customersList = new ArrayList<>();
        customersList =customersRepository.findAllIdByIsDeletedIsFalseAndStatusInAndCustomerTypeIsNotPostpaid(customerStatusList);

        if(customersList != null){
            customersList.forEach(customer -> {
                List<CustPlanMappping> customerPlanMappingList = custPlanMappingRepository.findAllByCustomerId(customer);

                if (customerPlanMappingList != null && customerPlanMappingList.size() > 0) {
                    try {
                        List<CustomerPlansModel> customerPlansModels = subscriberService.getActivePlanList(customer, false);
                        customerPlanMappingList = customerPlanMappingList.stream()
                                .filter(custPlanMappping -> customerPlansModels.stream().allMatch(customerPlansModel -> !customerPlansModel.getService().equalsIgnoreCase(custPlanMappping.getService()))).collect(Collectors.toList());

                        if(customerPlanMappingList != null && customerPlanMappingList.size() > 0){
                            setStatusOfCustomerPlan(customerPlanMappingList, customer);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ApplicationLogger.logger.error("Customer Status Update CronJob And Customer ID=" + customer + " " + e.getMessage(), e);
                    }
                }

            });
        }
    }

    public void setStatusOfCustomerPlan(List<CustPlanMappping> customerPlanMappingList, Integer customer) {
        Long systemConfigDays = Long.parseLong(clientServiceRepository.findValueByNameandMvnoId("suspend_cust_after_days",customerPlanMappingList.get(0).getCustomer().getMvnoId()));

        if (customerPlanMappingList != null && customerPlanMappingList.size() > 0) {
            List<CustomerServiceMapping> updateCustomerServiceMappingList = new ArrayList<>();
            customerPlanMappingList.forEach(custPlan -> {
                CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(custPlan.getCustServiceMappingId()).orElse(null);
                Long day = ChronoUnit.DAYS.between(custPlan.getEndDate(), LocalDateTime.now());
                if (day > 0 && day <= systemConfigDays) {
                    custPlan.setCustPlanStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.INAVCTIVE);
                    if (customerServiceMapping != null && customerServiceMapping.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE)) {
                        customerServiceMapping.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.INAVCTIVE);
                    }
                    customerServiceMappingRepository.save(customerServiceMapping);
                    updateCustomerServiceMappingList.add(customerServiceMapping);
                    updatePlanAndServiceStatus(customerPlanMappingList, updateCustomerServiceMappingList);
                    if(customerServiceMapping.getUuid()!=null){
                        customersService.sendDeleteRequestForNMS(customerServiceMapping.getCustId(),customerServiceMapping.getId());
                    }

                } else if (day == systemConfigDays) {
                    custPlan.setCustPlanStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.SUSPEND);
                    if (customerServiceMapping != null && customerServiceMapping.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE) && customerServiceMapping.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.INAVCTIVE)) {
                        customerServiceMapping.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.SUSPEND);
                    }
                    customerServiceMappingRepository.save(customerServiceMapping);
                    updateCustomerServiceMappingList.add(customerServiceMapping);
                    updatePlanAndServiceStatus(customerPlanMappingList, updateCustomerServiceMappingList);
                }
                /* else part commented because service will be not ACTIVE from this scheduler */
//                else {
//                    custPlan.setCustPlanStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE);
//                    if (customerServiceMapping != null) {
//                        customerServiceMapping.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE);
//                    }
//                    if(custPlan.getStartDate().equals(LocalDateTime.now()) || custPlan.getStartDate().isBefore(LocalDateTime.now())){
//                        //Ezbill call for renew plan
//                        ezBillServiceUtility.renewPlanInEzBill(custPlan, custPlan.getId(), "New");
//                    }
//                }

            });

        }

        List<CustomerServiceMapping> newCustomerServiceMappingList = customerServiceMappingRepository.findByCustId(customer);
        List<CustomerServiceMapping> inActiveServiceMapping = newCustomerServiceMappingList.stream().filter(customerService -> customerService.getStatus() != null && customerService.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.INAVCTIVE)).collect(Collectors.toList());
        List<CustomerServiceMapping> activeServiceMapping = newCustomerServiceMappingList.stream().filter(customerService -> customerService.getStatus() != null && customerService.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE)).collect(Collectors.toList());
        List<CustomerServiceMapping> suspendServiceMapping = newCustomerServiceMappingList.stream().filter(customerService -> customerService.getStatus() != null && customerService.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.SUSPEND)).collect(Collectors.toList());

        Customers cust = customersRepository.findById(customer).orElse(null);
        if(cust != null){
            /* if part commented because customer not active from this scheduler */
//            if (activeServiceMapping.size() > 0) {
//                cust.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE);
//            } else
            if (activeServiceMapping.size() == 0 && inActiveServiceMapping.size() > 0) {
                cust.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.INAVCTIVE);
                CustomMessage customMessage = new CustomMessage(customerMapper.domainToDTO(cust, new CycleAvoidingMappingContext()));
                //messageSender.send(customMessage, RabbitMqConstants.QUEUE_APIGW_CUSTOMER);
                kafkaMessageSender.send(new KafkaMessageData(customMessage,customMessage.getClass().getSimpleName(),"CUSTOMER_CREATE"));
            } else if (activeServiceMapping.size() == 0 && inActiveServiceMapping.size() == 0 && suspendServiceMapping.size() > 0) {
                cust.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.SUSPEND);
                CustomMessage customMessage = new CustomMessage(customerMapper.domainToDTO(cust, new CycleAvoidingMappingContext()));
                //messageSender.send(customMessage, RabbitMqConstants.QUEUE_APIGW_CUSTOMER);
                kafkaMessageSender.send(new KafkaMessageData(customMessage,customMessage.getClass().getSimpleName(),"CUSTOMER_CREATE"));
            }
            customersRepository.saveAndFlush(cust);
        }
    }

    public void updatePlanAndServiceStatus(List<CustPlanMappping> updateCustPlanMappingList, List<CustomerServiceMapping> updateCustomerServiceMappingList){
        if (updateCustPlanMappingList != null) {
            custPlanMappingRepository.saveAll(updateCustPlanMappingList);
        }

            /*Set status in CustomerServiceMapping Table*/
        if (updateCustomerServiceMappingList != null) {
            updateCustomerServiceMappingList.forEach(service -> {
                Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(service.getCustId());

                if(service.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE)){

                    Optional<Integer> buIdOptional = Optional.ofNullable(customers.getBuId())
                            .map(Math::toIntExact);
                    Integer buid = buIdOptional.orElse(null);
                    /*service active send notification*/
                    sendCustServiceActiveMessage(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getStatus(), customers.getMvnoId(),  buid, (long) customersService.getLoggedInStaffId());

                }
                if(service.getStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.INAVCTIVE)){

                    /*service inactive send notification*/
                    sendCustServiceInActiveMessage(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getStatus(), customers.getMvnoId() ,customers.getBuId(),(long) customersService.getLoggedInStaffId());

                }
            });
        }
    }


    /*method for send notification if customer service active*/
    public void sendCustServiceActiveMessage(String username, String mobileNumber, String emailId, String status, Integer mvnoId,Integer buId,Long staffId) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_SERVICE_ACTIVE_TEMPLATE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    CustServiceActiveMsg custServiceActiveMsg = new CustServiceActiveMsg(username, mobileNumber, emailId, status, mvnoId, RabbitMqConstants.CUSTOMER_SERVICE_ACTIVE_EVENT, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,buId,staffId);
                    Gson gson = new Gson();
                    gson.toJson(custServiceActiveMsg);
                    kafkaMessageSender.send(new KafkaMessageData(custServiceActiveMsg,CustServiceActiveMsg.class.getSimpleName()));
//                    messageSender.send(custServiceActiveMsg, RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_ACTIVE_NOTIFICATION);
                }
            } else {
                System.out.println("Message of Customer Service Active is not sent because template is not present.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*method for send notification if customer service inactive*/
    public void sendCustServiceInActiveMessage(String username, String mobileNumber, String emailId, String status, Integer mvnoId,Long buId,Long staffId) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_SERVICE_INACTIVE_TEMPLATE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    CustServiceInActiveMsg custServiceInActiveMsg = new CustServiceInActiveMsg(username, mobileNumber, emailId, status, mvnoId, RabbitMqConstants.CUSTOMER_SERVICE_INACTIVE_EVENT, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,buId,staffId);
                    Gson gson = new Gson();
                    gson.toJson(custServiceInActiveMsg);
                    kafkaMessageSender.send(new KafkaMessageData(custServiceInActiveMsg,CustServiceInActiveMsg.class.getSimpleName()));
//                    messageSender.send(custServiceInActiveMsg, RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_INACTIVE_NOTIFICATION);
                }
            } else {
                System.out.println("Message of Customer Service InActive is not sent because template is not present.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
