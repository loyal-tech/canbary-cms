package com.adopt.apigw.kafka;

import com.adopt.apigw.MicroSeviceDataShare.PartnerAmountMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.*;
import com.adopt.apigw.dialShreeModule.CustCallLogsDTO;
import com.adopt.apigw.dialShreeModule.CustCallLogsService;
import com.adopt.apigw.model.lead.LeadMasterPojo;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Area.service.AreaService;
import com.adopt.apigw.modules.BankManagement.service.BankManagementService;
import com.adopt.apigw.modules.Branch.service.BranchService;
import com.adopt.apigw.modules.BuildingMgmt.Service.BuildingMgmtService;
import com.adopt.apigw.modules.BusinessUnit.service.BusinessUnitService;
import com.adopt.apigw.modules.BusinessVerticals.Service.BusinessVerticalsService;
import com.adopt.apigw.modules.CaseCustomerDetails.service.CaseCustometDetailsService;
import com.adopt.apigw.modules.ChangePlanDTOs.CreditDebitDocMessage;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustInvParamsService;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapingDTO;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.adopt.apigw.modules.InventoryManagement.VendorManagment.VendorService;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardDto;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.item.ItemDto;
import com.adopt.apigw.modules.InventoryManagement.item.ItemServiceImpl;
import com.adopt.apigw.modules.InvestmentCode.service.InvestmentCodeService;
import com.adopt.apigw.modules.Mvno.service.MvnoService;
import com.adopt.apigw.modules.PaymentConfig.service.PaymentConfigService;
import com.adopt.apigw.modules.Pincode.service.PincodeService;
import com.adopt.apigw.modules.Region.service.RegionService;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.SubArea.Service.SubAreaService;
import com.adopt.apigw.modules.SubBusinessUnit.Service.SubBusinessUnitService;
import com.adopt.apigw.modules.SubBusinessVertical.Service.SubBusinessVerticalService;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.Teams.service.TeamsService;
import com.adopt.apigw.modules.TumilIdValidation.IdValidationResponse;
import com.adopt.apigw.modules.TumilIdValidation.IdValidationService;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfileService;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.mvnoDocDetails.model.MvnoDocDetailsDTO;
import com.adopt.apigw.modules.mvnoDocDetails.service.DocDetailsService;
import com.adopt.apigw.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.adopt.apigw.modules.role.service.RoleService;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.CustMilestoneDetailsPojo;
import com.adopt.apigw.pojo.QuickInvoiceCreationPojo;
import com.adopt.apigw.pojo.SendLeadDocConvertPojo;
import com.adopt.apigw.pojo.api.AutoRenewOrAddonPlanRequestDto;
import com.adopt.apigw.rabbitMq.UpdateMvnoData;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.rabbitMq.message.QuotaReset.CustQuotaResetDTO;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.LeadMasterService;
import com.adopt.apigw.service.common.*;
import com.adopt.apigw.service.postpaid.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

//import static com.adopt.apigw.model.common.QClientService.clientService;


@Component
public class KafkaMessageReceiver implements Runnable {
    @Autowired
    private CountryService countryService;
    @Autowired
    private StateService stateService;
    @Autowired
    private CityService cityService;
    @Autowired
    private PincodeService pincodeService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private BusinessUnitService businessUnitService;
    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private BusinessVerticalsService businessVerticalsService;
    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private BankManagementService bankManagementService;
    @Autowired
    private InvestmentCodeService investmentCodeService;
    @Autowired
    private SubBusinessVerticalService subBusinessVerticalService;
    @Autowired
    private SubBusinessUnitService subBusinessUnitService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MvnoService mvnoService;
    @Autowired
    private TeamsService teamsService;
    @Autowired
    private PaymentConfigService paymentConfigService;
    @Autowired
    private CustMacMapppingService custMacMapppingService;
    @Autowired
    private CustomersService customerService;
    @Autowired
    private CustQuotaService custQuotaService;
    @Autowired
    private DocDetailsService docDetailsService;
    @Autowired
    HierarchyService hierarchyService;
    @Autowired
    CustomerDocDetailsService customerDocDetailsService;
    @Autowired
    private LeadMasterService leadMasterService;
    @Autowired
    private CustMilestoneDetailsService custMilestoneDetailsService;
    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    DebitDocService debitDocService;
    @Autowired
    CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    private DebitDocRepository debitDocRepository;
    @Autowired
    private CustChargeDetailsRepository custChargeDetailsRepository;
    @Autowired
    private CustChargeInstallmentRepository custChargeInstallmentRepository;
    @Autowired
    CustPlanMappingService custPlanMappingService;
    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;
    @Autowired
    CreditDocService creditDocService;
    @Autowired
    PartnerService partnerService;
    @Autowired
    ChargeService chargeService;
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;
    @Autowired
    private CustInvParamsService custInvParamsService;
    @Autowired
    private VendorService vendorService;
    @Autowired
    private InwardServiceImpl inwardService;
    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    private InOutWardMACService inOutWardMACService;
    @Autowired
    CaseCustometDetailsService caseCustometDetailsService;
    @Autowired
    CustAccountProfileService custAccountProfileService;
    @Autowired
    OTPManagmentService otpManagmentService;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private BuildingMgmtService buildingMgmtService;
    @Autowired
    private CustCallLogsService custCallLogsService;

    private final ExecutorService executor = new ThreadPoolExecutor(30, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @Autowired
    KafkaConsumerConfig consumerConfig;

    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    SubAreaService subAreaService;

    @Autowired
    IdValidationService idValidationService;

    //    private static Log log = LogFactory.getLog(KafkaMessageReceiver.class);
    private static final Logger log = LoggerFactory.getLogger(KafkaMessageReceiver.class);

    private final Map<String, Consumer<KafkaMessageData>> messageHandlers = new HashMap<>();

    public KafkaMessageReceiver() {
        //Receive From Common
        messageHandlers.put("SaveCountrySharedDataMessage", this::handleCountry);
        messageHandlers.put("UpdateCountrySharedDataMessage", this::handleUpdateCountry);
        messageHandlers.put("SaveStateSharedDataMessage", this::handleState);
        messageHandlers.put("UpdateStateSharedDataMessage", this::handleUpdateState);
        messageHandlers.put("SaveCitySharedDataMessage", this::handleCity);
        messageHandlers.put("UpdateCitySharedDataMessage", this::handleUpdateCity);
        messageHandlers.put("SavePincodeSharedDataMessage", this::handlePincode);
        messageHandlers.put("UpdatePincodeSharedDataMessage", this::handleUpdatePincode);
        messageHandlers.put("SaveAreaSharedDataMessage", this::handleArea);
        messageHandlers.put("UpdateAreaSharedDataMessage", this::handleUpdateArea);
        messageHandlers.put("SaveBusinessUnitSharedDataMessage", this::handleBusinessUnit);
        messageHandlers.put("UpdateBusinessUnitSharedDataMessage", this::handleUpdateBusinessUnit);
        messageHandlers.put("SaveServiceAreaSharedDataMessge", this::handleServiceArea);
        messageHandlers.put("UpdateServiceAreaSharedDataMessage", this::handleUpdateServiceArea);
        messageHandlers.put("SaveBranchSharedDataMessage", this::handleBranch);
        messageHandlers.put("UpdateBranchSharedData", this::handleUpdateBranch);
        messageHandlers.put("SaveRegionSharedDataMessage", this::handleRegion);
        messageHandlers.put("UpdateRegionSharedDataMessage", this::handleUpdateRegion);
        messageHandlers.put("SaveDepartmentSharedDataMessage", this::handleDepartment);
        messageHandlers.put("UpdateDepartmentSharedDataMessage", this::handleUpdateDepartment);
        messageHandlers.put("SaveBusinessVerticalSharedDataMessage", this::handleSaveBusinessVerticalSharedDataMessage);
        messageHandlers.put("UpdateBusinessVerticalSharedDataMessage", this::handleUpdateBusinessVertical);
        messageHandlers.put("SaveSubBusinessVerticalsSharedDataMessage", this::handleSaveSubBusinessVerticals);
        messageHandlers.put("UpdateSubBusinessVerticalsSharedDataMessage", this::handleUpdateSubBusinessVerticals);
        messageHandlers.put("SaveStaffUserSharedDataMessage", this::handleSaveStaffUserSharedDataMessage);
        messageHandlers.put("UpdateStaffUserSharedDataMessage", this::handleUpdateStaffUser);
        messageHandlers.put("SaveRoleSharedDataMessage", this::handleSaveRoleSharedDataMessage);
        messageHandlers.put("UpdateRoleSharedDataMessage", this::handleUpdateRole);
        messageHandlers.put("SaveCustAccountProfileSharedDataMessage", this::handleSaveCustAccountProfileSharedDataMessage);
        messageHandlers.put("UpdateCustAccountProfileSharedDataMessage", this::handleUpdateCustAccountProfileSharedDataMessage);
        messageHandlers.put("SaveMvnoSharedDataMessage", this::handleSaveMvnoSharedDataMessage);
        messageHandlers.put("UpdateMvnoSharedDataMessage", this::handleUpdateMvnoSharedDataMessage);
        messageHandlers.put("SaveTeamsSharedSharedData", this::handleSaveTeamSharedDataMessage);
        messageHandlers.put("UpdateTeamsSharedData", this::handleUpdateTeam);
        messageHandlers.put("SaveClientServMessge:" +KafkaConstant.CREATE_SERVICE_CONFIG, this::handleSaveClientServMess);
        messageHandlers.put("UpdateClientServMessage:"+KafkaConstant.UPDATE_SERVICE_CONFIG, this::handleUpdateClientServMess);
        messageHandlers.put("SaveSubBusinessUnitSharedDataMessage", this::handleSaveSubBusinessUnitSharedDataMessage);
        messageHandlers.put("UpdateSubBusinessUnitSharedDataMessage", this::handleUpdateSubBusinessUnitSharedDataMessage);
        messageHandlers.put("PaymentConfigMessage", this::handleSavePaymentConfigSharedDataMessage);
        messageHandlers.put("SaveInvestmentCodeSharedDataMessage", this::handleSaveInvestmentCodeSharedDataMessage);
        messageHandlers.put("UpdateInvestmentCodeSharedDataMessage", this::handleUpdateInvestmentCodeSharedDataMessage);
        messageHandlers.put("SaveBankManagementSharedDataMessage", this::handleSaveBankManagementSharedDataMessage);
        messageHandlers.put("UpdateBankManagementSharedDataMessage", this::handleUpdateBankManagementSharedDataMessage);
        messageHandlers.put("UpdateMvnoData", this::handleUpdateMvnoData);
        messageHandlers.put("MvnoDocDetailsDTO", this::handleMvnoDocDetailsDTO);
        messageHandlers.put("SaveStaffAssignmentMessage", this::handleStaffUserServiceAreaMapping);

        //EventType
        messageHandlers.put("CommonRoleMessage:" + KafkaConstant.CREATE_DATA_ROLE, this::handleCreateDataRole);
        messageHandlers.put("CommonRoleMessage:" + KafkaConstant.DELETE_DATA_ROLE, this::handleDeleteDataRole);
        messageHandlers.put("SendQuotaMsg:" + KafkaConstant.SEND_QUOTA, this::handleSendQuotaMsg);
        messageHandlers.put("SendQuotaMsg:" + KafkaConstant.CUSTOMERS_UPDATE_RESERVED_QUOTA, this::handleCUSTOMERS_UPDATE_RESERVED_QUOTA);
        messageHandlers.put("SendQuotaIntrimMsg:" + KafkaConstant.QUOTA_INTRIM, this::handleQUOTA_INTRIM);
        messageHandlers.put("CustomerUpdateMessage:" + KafkaConstant.UPDATE_CONCURRENCY, this::handleUPDATE_CONCURRENCY);
        messageHandlers.put("CustomerEndDateUpdateMessage:" + KafkaConstant.CUSTOMER_ENDDATE, this::handleCUSTOMER_ENDDATE);


        //Receive From Redius
        messageHandlers.put("CustomerPackageRelMessage", this::handleGetCustomerPackageRelMessage);
        messageHandlers.put("CustMacMessage", this::handleCustMacMessage);
        messageHandlers.put("NasUpdateMessage", this::handleNasUpdateMessage);
        messageHandlers.put("CustomerQuotaInfo", this::handleCustomerQuotaInfo);
        messageHandlers.put("CustNextBilldateMessage", this::handleCustNextBillDateMessage);
        messageHandlers.put("CustQuotaResetDTO:"+KafkaConstant.SEND_QUOTA_RESET,this::handleSendQuotaResetMsg);


        //Receive From SalesCrm
        messageHandlers.put("SendSaveLeadData:ASSIGN_WORKFLOW", this::handleLeadData);
        messageHandlers.put("SendLeadDocConvertPojo", this::handlereceiveLeadDocDetails);
        messageHandlers.put("LeadMasterPojoMessage", this::handlereceiveLeadMaster);
        messageHandlers.put("SendLeadQuotationMessage", this::handlereceiveLeadData);
        messageHandlers.put("QuickInvoicePojoMessage", this::handleCustMilestoneDetails);

        //Receive From Revnue
        messageHandlers.put("PrepaidInvoiceCharges", this::handlePrepaidCustomerInvoiceChargesDetail);
        messageHandlers.put("CreditDocMessageList", this::handleMessageforcreditDocFromRevenue);
        messageHandlers.put("OrganizationInvoiceRejectMesssage", this::handleMessageBillToOrg);
        messageHandlers.put("CustPlanStatusMessage", this::handleMessageToExpirePlanOnCreditNoteApprove);
        messageHandlers.put("UpdateCustplanMappingMessage", this::handleUpdateCustPlanMappingForP2Pmessage);
        messageHandlers.put("CreditDocIdsMessages", this::handleCreditDocIdsMessage);
        messageHandlers.put("ListOfCreditDocForBatch", this::handleCreditDocMessage);
        messageHandlers.put("CreditDebitDocMessage", this::handleCreditDebitDocMessage);
        messageHandlers.put("BudPayPaymentMessage", this::handleBudPayPaymentDetailsMessage);
        messageHandlers.put("CustPlanMappingStatusMessage", this::handleCustPlanMappingStatusUpdateFromRevenue);
        messageHandlers.put("PartnerAmountMessage:" + KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER, this::handleMessagePartnerBalanceFromRevenue);
        messageHandlers.put("OTPProfileMessage", this::handleOTPProfileSaveSync);
        messageHandlers.put("AutoRenewOrAddonPlanRequestDto", this::handleAutoRenewOrAddonPlanRequestSync);
        messageHandlers.put("WorkFlowAutoApprovalMessage", this::handleMessageforCafAutoApprovalIfPaymentSettled);
        messageHandlers.put("UpdateDebitdocGraceDayMessage", this::handleUpdateDebitdocGraceDayMessage);
        messageHandlers.put("CustChargeInstallmentsMessage", this::handleCustChargeInstallmentsMessage);


        //Receive From Inventory
        messageHandlers.put("ChargeMessage:" + KafkaConstant.CREATE_NEW_CHARGE, this::handleMessageForCreateNewProCharge);
        messageHandlers.put("ChargeMessage:" + KafkaConstant.UPDATE_NEW_CHARGE, this::handleMessageForUpdateNewProCharge);
        messageHandlers.put("ChargeMessage:" + KafkaConstant.CREATE_REF_CHARGE, this::handleMessageForCreateRefProCharge);
        messageHandlers.put("ChargeMessage:" + KafkaConstant.UPDATE_REF_CHARGE, this::handleMessageForUpdateRefProCharge);
        messageHandlers.put("InventorySerialNumberMessage", this::handleItemSerialDatamessage);
        messageHandlers.put("CustInvParamsMessage", this::handleCustInvParamsMessage);
        messageHandlers.put("SaveUpdateVendorMessage:" + KafkaConstant.SAVE_VENDOR, this::handleMessageForSaveVendor);
        messageHandlers.put("SaveUpdateVendorMessage:" + KafkaConstant.UPDATE_VENDOR, this::handleMessageForUpdateVendor);

        //Receive From Integration
        messageHandlers.put("InwardDto", this::handleinwardFromRms);
        messageHandlers.put("ItemDto", this::handleitemFromRms);
        messageHandlers.put("CustPayDTOMessage", this::handleCustomerPaymentStatus);
        messageHandlers.put("CustPayDTOMessage:" + KafkaConstant.BUY_PLAN, this::handleCustomerPaymentBuyPlan);
        messageHandlers.put("CustCallLogsDTO",this::handleCustomerCallLogsData);
        messageHandlers.put("CustCwscOnlinePaymentDTO",this::handleCustCwscOnlinePaymentDTO);
        messageHandlers.put("IdValidationResponse",this::handleHouseHoldIdValidationResponse);


        //Receive From Ticket
        messageHandlers.put("CloseTicketCheckMessage:SAVE_TICKET_DATA", this::handlesaveTicketDataFromMicroService);
        messageHandlers.put("CloseTicketCheckMessage:UPDATED_TICKET_DATA", this::handleupdateTicketDataFromMicroService);
        messageHandlers.put("SavePartnerSharedDataMessage:" + KafkaConstant.CREATE_PARTNER, this::handleSavePartnerData);
        messageHandlers.put("UpdatePartnerSharedDataMessage:" + KafkaConstant.UPDATE_PARTNER, this::handleUpdatePartnerData);
        messageHandlers.put("SubAreaMessage", this::handleSubArea);


        //Recieve From CommonGw for BuildingMgmt
        messageHandlers.put("BuildingMgmtMessage:" + KafkaConstant.BUILDING_MGMT_SAVE, this::handleMessageforSavingBuildingMgmt);
        messageHandlers.put("BuildingMgmtMessage:" + KafkaConstant.BUILDING_MGMT_UPDATE, this::handleMessageforUpdataingBuildingMgmt);
        messageHandlers.put("DebitDocumentDTOForAdjustment",this::handleDebitDocAfterBudPay);

        messageHandlers.put("SyncClientServiceMessage:" + KafkaConstant.SYNC_SERVICE_CONFIG, this::syncClientServiceHandler);
    }
//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_COMMON_TOPIC},groupId = KafkaConstant.KAFKA_COMMON_GROUP_ID,containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFromCommonMicroService(KafkaMessageData message) {
//        log.info("kafkaMessageReceiveFromCommonMicroService: topic: "+KafkaConstant.KAFKA_COMMON_TOPIC+" group Id: "+KafkaConstant.KAFKA_COMMON_GROUP_ID);
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
// //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Common-Micro-Service: " + e.getMessage(), e);
//        }
//    }


    @Transactional
    @Override
    public void run() {
        JsonDeserializer<KafkaMessageData> deserializer = new JsonDeserializer<>(KafkaMessageData.class, false);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        DefaultKafkaConsumerFactory<String, KafkaMessageData> factory =
                new DefaultKafkaConsumerFactory<>(consumerConfig.packetDataPropsPrimary(),
                        new StringDeserializer(), deserializer);

        try (KafkaConsumer<String, KafkaMessageData> primaryConsumer = (KafkaConsumer<String, KafkaMessageData>) factory.createConsumer()) {
            primaryConsumer.subscribe(Arrays.asList(
                    KafkaConstant.KAFKA_COMMON_TOPIC,
                    KafkaConstant.KAFKA_PMS_TOPIC,
                    KafkaConstant.KAFKA_INVENTORY_TOPIC,
                    KafkaConstant.KAFKA_REVENUE_TOPIC,
                    KafkaConstant.KAFKA_NOTIFICATION_TOPIC,
                    KafkaConstant.KAFKA_RADIUS_TOPIC,
                    KafkaConstant.KAFKA_SALES_CRM_TOPIC,
                    KafkaConstant.KAFKA_INTEGRATION_TOPIC,
                    KafkaConstant.KAFKA_TICKET_TOPIC
            ));

            while (true) {
                long startTime = System.currentTimeMillis();

                ConsumerRecords<String, KafkaMessageData> records = primaryConsumer.poll(Duration.ofMillis(5000));
                log.info("Kafka records received::::::::::::::: " + records.count());

                for (ConsumerRecord<String, KafkaMessageData> record : records) {
                    try {
                        KafkaMessageData message = record.value();
                        String dataType = message.getDataType();
                        String eventType = message.getEventType();

                        String keyWithEventType = dataType + ":" + eventType;

                        Consumer<KafkaMessageData> handler = messageHandlers.getOrDefault(keyWithEventType, messageHandlers.get(dataType));

                        if (handler != null) {
                            log.debug("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : dataType));
                            CompletableFuture.runAsync(() -> handler.accept(message), executor);
                        }

                        log.debug("Received message: dataType = " + dataType + ", eventType = " + eventType);
                    } catch (Exception e) {
                        log.error("Error processing message at offset " + record.offset() + " from partition " + record.partition(), e);
                    }
                }

                try {
                    primaryConsumer.commitSync(); // safer manual commit
                } catch (Exception e) {
                    log.error("Commit failed", e);
                }

                long endTime = System.currentTimeMillis();
                log.debug("Poll processing time (ms): " + (endTime - startTime));
            }
        } catch (Exception e) {
            log.error("Kafka Error: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCountry(KafkaMessageData message) {
        try {
            SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCountrySharedDataMessage.class);
            countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCountry(KafkaMessageData message) {
        try {
            UpdateCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCountrySharedDataMessage.class);
            countryService.updateCountry(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateCountrySharedDataMessage: " + e.getMessage(), e);
        }

    }

    @Async
    public void handleState(KafkaMessageData message) {
        try {
            SaveStateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveStateSharedDataMessage.class);
            stateService.saveStateEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveStateSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateState(KafkaMessageData message) {
        try {
            UpdateStateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateStateSharedDataMessage.class);
            stateService.updateStateEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateStateSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCity(KafkaMessageData message) {
        try {
            SaveCitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCitySharedDataMessage.class);
            cityService.saveCityEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCitySharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCity(KafkaMessageData message) {
        try {
            UpdateCitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCitySharedDataMessage.class);
            cityService.updateCityEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateCitySharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlePincode(KafkaMessageData message) {
        try {
            SavePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePincodeSharedDataMessage.class);
            pincodeService.savePincode(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SavePincodeSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdatePincode(KafkaMessageData message) {
        try {
            UpdatePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePincodeSharedDataMessage.class);
            pincodeService.updatePincode(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdatePincodeSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleArea(KafkaMessageData message) {
        try {
            SaveAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveAreaSharedDataMessage.class);
            areaService.saveAreaEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveAreaSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateArea(KafkaMessageData message) {
        try {
            UpdateAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateAreaSharedDataMessage.class);
            areaService.updateAreaEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateAreaSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleBusinessUnit(KafkaMessageData message) {
        try {
            SaveBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBusinessUnitSharedDataMessage.class);
            businessUnitService.saveBusineeUnit(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveBusinessUnitSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateBusinessUnit(KafkaMessageData message) {
        try {
            UpdateBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBusinessUnitSharedDataMessage.class);
            businessUnitService.updateBusinessUnit(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateBusinessUnitSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveSubBusinessVerticals(KafkaMessageData message) {
        try {
            SaveSubBusinessVerticalsSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveSubBusinessVerticalsSharedDataMessage.class);
            subBusinessVerticalService.saveSubBuVertical(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveSubBusinessVerticalsSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateSubBusinessVerticals(KafkaMessageData message) {
        try {
            UpdateSubBusinessVerticalsSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateSubBusinessVerticalsSharedDataMessage.class);
            subBusinessVerticalService.updateSubBuVertical(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateSubBusinessVerticalsSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleServiceArea(KafkaMessageData message) {
        try {
            SaveServiceAreaSharedDataMessge dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveServiceAreaSharedDataMessge.class);
            serviceAreaService.saveServiceArea(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  ::::::::::::::::::: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveServiceAreaSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateServiceArea(KafkaMessageData message) {
        try {
            UpdateServiceAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateServiceAreaSharedDataMessage.class);
            serviceAreaService.updateServiceArea(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  :::::::::::::::::::: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateServiceAreaSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleBranch(KafkaMessageData message) {
        try {
            SaveBranchSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBranchSharedDataMessage.class);
            branchService.saveBranch(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveBranchSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateBranch(KafkaMessageData message) {
        try {
            UpdateBranchSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBranchSharedData.class);
            branchService.updateBranch(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateBranchSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleRegion(KafkaMessageData message) {
        try {
            SaveRegionSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveRegionSharedDataMessage.class);
            regionService.saveRegion(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveRegionSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateRegion(KafkaMessageData message) {
        try {
            UpdateRegionSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateRegionSharedDataMessage.class);
            regionService.updateRegion(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateRegionSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleDepartment(KafkaMessageData message) {
        try {
            SaveDepartmentSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveDepartmentSharedDataMessage.class);
            departmentService.saveDepartment(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveDepartmentSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateDepartment(KafkaMessageData message) {
        try {
            UpdateDepartmentSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateDepartmentSharedDataMessage.class);
            departmentService.updateDepartment(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateDepartmentSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveBusinessVerticalSharedDataMessage(KafkaMessageData message) {
        try {
            SaveBusinessVerticalSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBusinessVerticalSharedDataMessage.class);
            businessVerticalsService.saveBusinessVertical(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveBusinessVerticalSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateBusinessVertical(KafkaMessageData message) {
        try {
            UpdateBusinessVerticalSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBusinessVerticalSharedDataMessage.class);
            businessVerticalsService.updateBusinessVertical(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateBusinessVerticalSharedDataMessage: " + e.getMessage(), e);
        }

    }

    @Async
    public void handleSaveStaffUserSharedDataMessage(KafkaMessageData message) {
        try {
            SaveStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveStaffUserSharedDataMessage.class);
            staffUserService.saveStaffUserEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveStaffUserSharedDataMessage: " + e.getMessage(), e);
        }

    }

    @Async
    public void handleUpdateStaffUser(KafkaMessageData message) {
        try {
            UpdateStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateStaffUserSharedDataMessage.class);
            staffUserService.updatetaffUserEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateStaffUserSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveRoleSharedDataMessage(KafkaMessageData message) {
        try {
            SaveRoleSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveRoleSharedDataMessage.class);
            roleService.saveRoleEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveRoleSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateRole(KafkaMessageData message) {
        try {
            UpdateRoleSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateRoleSharedDataMessage.class);
            roleService.updateRoleEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateRoleSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Async
    public void handleSaveCustAccountProfileSharedDataMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveCustAccountProfileSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SaveCustAccountProfileSharedDataMessage.class);
            custAccountProfileService.saveCustAccountProfileEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveMvnoSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Async
    public void handleUpdateCustAccountProfileSharedDataMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UpdateCustAccountProfileSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateCustAccountProfileSharedDataMessage.class);
            custAccountProfileService.updateCustAccountProfileEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveMvnoSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Async
    public void handleSaveMvnoSharedDataMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveMvnoSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SaveMvnoSharedDataMessage.class);
            mvnoService.saveMVNOEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveMvnoSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateMvnoSharedDataMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UpdateMvnoSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateMvnoSharedDataMessage.class);
            mvnoService.updateMVNOEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveTeamSharedDataMessage(KafkaMessageData message) {
        try {
            //SaveTeamsSharedSharedData dataMessage=new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()),SaveTeamsSharedSharedData.class);
            SaveTeamsSharedSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveTeamsSharedSharedData.class);
            teamsService.saveTeams(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveTeamSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateTeam(KafkaMessageData message) {
        try {
            UpdateTeamsSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateTeamsSharedData.class);
            teamsService.updateTeams(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateTeamSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveClientServMess(KafkaMessageData message) {
        try {
            SaveClientServMessge dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveClientServMessge.class);
            clientServiceSrv.saveSharedClientService(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveClientServMess: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateClientServMess(KafkaMessageData message) {
        try {
            UpdateClientServMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateClientServMessage.class);
            clientServiceSrv.updateSharedClientService(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateClientServMess: " + e.getMessage(), e);
        }
    }

    @Async
    private void syncClientServiceHandler(KafkaMessageData messageData) {
        try {
            SyncClientServiceMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SyncClientServiceMessage.class);
            clientServiceSrv.syncSharedClientService(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("CMS Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveSubBusinessUnitSharedDataMessage(KafkaMessageData message) {
        try {
            SaveSubBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveSubBusinessUnitSharedDataMessage.class);
            subBusinessUnitService.saveSubBU(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveSubBusinessUnitSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateSubBusinessUnitSharedDataMessage(KafkaMessageData message) {
        try {
            UpdateSubBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateSubBusinessUnitSharedDataMessage.class);
            subBusinessUnitService.updateSubBU(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling updateSubBusinessUnitSharedDataMessageSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSavePaymentConfigSharedDataMessage(KafkaMessageData message) {
        try {
            PaymentConfigMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), PaymentConfigMessage.class);
            paymentConfigService.handleRecievePaymentConfig(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SavePaymentConfigSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveInvestmentCodeSharedDataMessage(KafkaMessageData message) {
        try {
            SaveInvestmentCodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveInvestmentCodeSharedDataMessage.class);
            investmentCodeService.saveInvestMentCode(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SavePaymentConfigSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveBankManagementSharedDataMessage(KafkaMessageData message) {
        try {
            SaveBankManagementSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBankManagementSharedDataMessage.class);
            bankManagementService.saveBank(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveBankManagementSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateInvestmentCodeSharedDataMessage(KafkaMessageData message) {
        try {
            UpdateInvestmentCodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateInvestmentCodeSharedDataMessage.class);
            investmentCodeService.updateInvestMentCode(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateInvestmentCodeSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateBankManagementSharedDataMessage(KafkaMessageData message) {
        try {
            UpdateBankManagementSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBankManagementSharedDataMessage.class);
            bankManagementService.updateBank(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateBankManagementSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateMvnoData(KafkaMessageData message) {
        try {
            UpdateMvnoData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateMvnoData.class);
            mvnoService.updateMvnoIdIsptoIsp(dataMessage.getOldmvnoId(), dataMessage.getNewmvnoId());
            log.info("Received message with dataType: " + message.getDataType() + " and eventType: " + message.getEventType());
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoData: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCreateDataRole(KafkaMessageData message) {
        try {
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            roleService.saveRole(dataMessage);
            log.info("Received message with dataType: " + message.getDataType() + " and eventType: " + message.getEventType());
        } catch (Exception e) {
            log.error("Error handling CreateDataRole: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleDeleteDataRole(KafkaMessageData message) {
        try {
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            roleService.deleteRole(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service: " + message);
        } catch (Exception e) {
            log.error("Error handling DeleteRole: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMvnoDocDetailsDTO(KafkaMessageData message) {
        try {
            MvnoDocDetailsDTO dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), MvnoDocDetailsDTO.class);
            docDetailsService.saveEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling MvnoDocDetailsDTO" + e.getMessage());
        }
    }
    @Async
    public void handleStaffUserServiceAreaMapping(KafkaMessageData message) {
        try {
            Gson gson = GsonConfig.buildGson();
            SaveStaffAssignmentMessage dataMessage = gson.fromJson(gson.toJson(message.getData()),SaveStaffAssignmentMessage.class);
            serviceAreaService.assignStaffToServiceArea(dataMessage.getMappingList());
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling StaffUserServiceAreaMapping" + e.getMessage());
        }
    }

    //    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_PMS_TOPIC},groupId = KafkaConstant.KAFKA_PMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFromPartnerMicroService(KafkaMessageData message) {
//        log.info("kafkaMessageReceiveFromPartnerMicroService: topic: "+KafkaConstant.KAFKA_PMS_TOPIC+" group Id: "+KafkaConstant.KAFKA_PMS_GROUP_ID);
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }
    @Async
    public void handlerName(KafkaMessageData message) {
        try {
            //SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCountrySharedDataMessage.class);
            //log.info("Attempting to save country with data: " + dataMessage);
            //countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_INVENTORY_TOPIC},groupId = KafkaConstant.KAFKA_INVENTORY_GROUP_ID,  containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFromInventoryMicroService(KafkaMessageData message) {
//        log.info("kafkaMessageReceiveFromInventoryMicroService: topic: "+KafkaConstant.KAFKA_INVENTORY_TOPIC+" group Id: "+KafkaConstant.KAFKA_INVENTORY_GROUP_ID);
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {

    /// /                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("CMS Service Receive Kafka Error Message From Inventory-Micro-Service : " + e.getMessage());
//        }
//    }
    @Async
    public void handleMessageForCreateNewProCharge(KafkaMessageData message) {
        try {
            InventoryChargeMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InventoryChargeMessage.class);
            chargeService.saveNewProductCharge(dataMessage);
            log.info("HandledMessageForCreateNewProCharge successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageForCreateNewProCharge: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageForUpdateNewProCharge(KafkaMessageData message) {
        try {
            InventoryChargeMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InventoryChargeMessage.class);
            chargeService.updateNewProductCharge(dataMessage);
            log.info("Handled MessageForUpdateNewProCharge successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageForUpdateNewProCharge: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageForCreateRefProCharge(KafkaMessageData message) {
        try {
            InventoryChargeMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InventoryChargeMessage.class);
            chargeService.saveRefProductCharge(dataMessage);
            log.info("Handled MessageForCreateRefProCharge successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageForCreateRefProCharge: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageForUpdateRefProCharge(KafkaMessageData message) {
        try {
            InventoryChargeMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InventoryChargeMessage.class);
            chargeService.updateRefProductCharge(dataMessage);
            log.info("Handled MessageForUpdateRefProCharge successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageForUpdateRefProCharge: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleItemSerialDatamessage(KafkaMessageData message) {
        try {
            log.info("Get Inventory Serial Number Message");
            InventorySerialNumberMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InventorySerialNumberMessage.class);
            log.debug("Inventory Serial Number Message Data Message: " + dataMessage);
            customerInventoryMappingService.saveInventoryData(dataMessage);
            log.info("Handled ItemSerialDatamessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling ItemSerialDatamessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCustInvParamsMessage(KafkaMessageData message) {
        try {
            CustInvParamsMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustInvParamsMessage.class);
            if (!dataMessage.getIsUpdate())
                custInvParamsService.saveCustInvParams(dataMessage);
            else
                custInvParamsService.updateCustInvParams(dataMessage);
            log.info("Handled CustInvParamsMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustInvParamsMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageForSaveVendor(KafkaMessageData message) {
        try {
            SaveUpdateVendorMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveUpdateVendorMessage.class);
            vendorService.saveVendorEntity(dataMessage);
            log.info("Handled MessageForSaveVendor successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageForSaveVendor: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageForUpdateVendor(KafkaMessageData message) {
        try {
            SaveUpdateVendorMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveUpdateVendorMessage.class);
            vendorService.updateVendorEntity(dataMessage);
            log.info("Handled MessageForUpdateVendor successfully: " + message);
        } catch (Exception e) {
            log.error("Error handlingMessageForUpdateVendor: " + e.getMessage(), e);
        }
    }

//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_REVENUE_TOPIC},groupId = KafkaConstant.KAFKA_REVENUE_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFromRevenueMicroService(KafkaMessageData message) {
//        log.info("kafkaMessageReceiveFromRevenueMicroService: topic: "+KafkaConstant.KAFKA_REVENUE_TOPIC+" group Id: "+KafkaConstant.KAFKA_REVENUE_GROUP_ID);
//        try {
//
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {

    /// /                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("CMS Service Receive Kafka Error Message From Revenue-Micro-Service : " + e.getMessage());
//        }
//    }
    @Async
    public void handlePrepaidCustomerInvoiceChargesDetail(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonData = objectMapper.writeValueAsString(message.getData());

            PrepaidInvoiceCharges dataMessage = objectMapper.readValue(jsonData, PrepaidInvoiceCharges.class);
            debitDocService.saveDebitDocInCms(dataMessage);
            log.info("Handled customerDebitDocumentSavingINCMs successfully: " + message);

        } catch (Exception e) {
            log.error("Error customerDebitDocumentSavingINCMs: " + e.getMessage(), e);
        }
    }


    @Async
    public void handleMessageforcreditDocFromRevenue(KafkaMessageData message) {
        try {
            CreditDocMessageList dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CreditDocMessageList.class);
            creditDocService.savecreditDoc(dataMessage);
            log.info("Handled MessageforcreditDocFromRevenue successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageforcreditDocFromRevenue: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageBillToOrg(KafkaMessageData message) {
        try {
            OrganizationInvoiceRejectMesssage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), OrganizationInvoiceRejectMesssage.class);
            custPlanMappingService.updateCustPlanStatus(dataMessage);
            log.info("Handled MessageBillToOrg successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageBillToOrg: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageToExpirePlanOnCreditNoteApprove(KafkaMessageData message) {
        try {
            CustPlanStatusMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustPlanStatusMessage.class);
            custPlanMappingService.expirePlanByCprId(dataMessage.getId());
            System.out.println("XXX MessageToExpirePlanOnCreditNoteApprovesuccessfully XXX");
            log.info("Handled MessageToExpirePlanOnCreditNoteApprovesuccessfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageToExpirePlanOnCreditNoteApprove: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCustPlanMappingForP2Pmessage(KafkaMessageData message) {
        try {
            UpdateCustplanMappingMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustplanMappingMessage.class);
            custPlanMappingService.updateCustPlanMapping(dataMessage);
            log.info("Handled UpdateCustPlanMappingForP2Pmessage: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateCustPlanMappingForP2Pmessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCreditDocIdsMessage(KafkaMessageData message) {
        try {
            CreditDocIdsMessages dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CreditDocIdsMessages.class);
            creditDocService.processCreditDocIds(dataMessage);
            log.info("Handled CreditDocIdsMessage: " + message);
        } catch (Exception e) {
            log.error("Error handling CreditDocIdsMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCreditDocMessage(KafkaMessageData message) {
        try {
            ListOfCreditDocForBatch dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ListOfCreditDocForBatch.class);
            creditDocService.processCreditDoc(dataMessage);
            log.info("Handled CreditDocIdsMessage: " + message);
        } catch (Exception e) {
            log.error("Error handling CreditDocIdsMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCreditDebitDocMessage(KafkaMessageData message) {
        try {
            CreditDebitDocMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CreditDebitDocMessage.class);
            if (!dataMessage.getCreditDebitDocMappingList().isEmpty())
                creditDocService.savecreditDebitDocMappings(dataMessage.getCreditDebitDocMappingList());
            log.info("Handled CreditDebitDocMessage: " + message);
        } catch (Exception e) {
            log.error("Error handling CreditDebitDocMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleBudPayPaymentDetailsMessage(KafkaMessageData message) {
        try {
            BudPayPaymentMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), BudPayPaymentMessage.class);
            creditDocService.updateBudPaymentData(dataMessage);
            log.info("Handled BudPayPaymentDetailsMessage: " + message);
        } catch (Exception e) {
            log.error("Error handling BudPayPaymentDetailsMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCustPlanMappingStatusUpdateFromRevenue(KafkaMessageData message) {
        try {
            CustPlanMappingStatusMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustPlanMappingStatusMessage.class);
            if (dataMessage != null && dataMessage.getCustPlanMappings() != null && !dataMessage.getCustPlanMappings().isEmpty()) {
                List<CustPlanMappingMessage> mappingMessages = dataMessage.getCustPlanMappings();
                mappingMessages.stream().forEach(mapping -> {
                    CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(mapping.getId());
                    if (custPlanMappping != null) {
                        custPlanMappping.setStatus(mapping.getStatus());
                        custPlanMappping.setCustPlanStatus(mapping.getCustPlanStatus());
                        custPlanMappping.setExpiryDate(LocalDateTime.now());
                        custPlanMappping.setEndDate(LocalDateTime.now());
                        custPlanMappingRepository.save(custPlanMappping);
                    }
                });
            }
            log.info("Handled CustPlanMappingStatusUpdateFromRevenue: " + message);
        } catch (Exception e) {
            log.error("Error handling CustPlanMappingStatusUpdateFromRevenue: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessagePartnerBalanceFromRevenue(KafkaMessageData message) {
        try {
            PartnerAmountMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), PartnerAmountMessage.class);
            partnerService.updateAmount(dataMessage);
            log.info("Handled MessagePartnerBalanceFromRevenue: " + message);
        } catch (Exception e) {
            log.error("Error handling MessagePartnerBalanceFromRevenue: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleOTPProfileSaveSync(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            OTPProfileMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), OTPProfileMessage.class);
            otpManagmentService.saveOtpProfileFromRabbitMq(dataMessage.getOtpManagement());
            log.info("Handled OTPProfileMessage: " + message);
        } catch (Exception e) {
            log.error("Error handling OTPProfileMessage: " + e.getMessage(), e);
        }
    }

    //    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_NOTIFICATION_TOPIC},groupId = KafkaConstant.KAFKA_NOTIFICATION_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormNotificationMicroService(KafkaMessageData message) {
//        log.info("kafkaMessageReceiveFormNotificationMicroService: topic: "+KafkaConstant.KAFKA_NOTIFICATION_TOPIC+" group Id: "+KafkaConstant.KAFKA_NOTIFICATION_GROUP_ID);
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }
    @Async
    public void handlerNaamee(KafkaMessageData message) {
        try {
            //SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCountrySharedDataMessage.class);
            //log.info("Attempting to save country with data: " + dataMessage);
            //countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_RADIUS_TOPIC},groupId = KafkaConstant.KAFKA_RADIUS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormRadiusMicroService(KafkaMessageData message) {
//        log.info("kafkaMessageReceiveFormRadiusMicroService: topic: "+KafkaConstant.KAFKA_RADIUS_TOPIC+" group Id: "+KafkaConstant.KAFKA_RADIUS_GROUP_ID);
//
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
////                  key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
////                  handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {

    /// /                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                Consumer<KafkaMessageData> finalHandler = handler;
//                CompletableFuture.runAsync(() -> finalHandler.accept(message), executor);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }
    @Async
    public void handleGetCustomerPackageRelMessage(KafkaMessageData message) {
        try {
            CustomerPackageRelMessage dataMessage =  new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerPackageRelMessage.class);
//                    new Gson().fromJson(new Gson().toJson(message.getData()), CustomerPackageRelMessage.class);
            customerService.updateCustPlanStatusFromRadius(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling CustomerPackageRelMessage" + e.getMessage());
        }
    }

    @Async
    public void handleCustMacMessage(KafkaMessageData message) {
        try {
            CustMacMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustMacMessage.class);
            log.info("CMS Service Receive Kafka Message From Radius-Micro-Service  : " + message);
            if (dataMessage.isBulkDelete()) {
                custMacMapppingService.deleteMacFromRadius(dataMessage);
            } else {
                customerService.addOrUpdateMacFromRadius(dataMessage);
            }
        } catch (Exception e) {
            log.error("Error in handleCustMacMessage: " + message + " Exception: " + e.getMessage());
        }
    }

    @Async
    public void handleCustNextBillDateMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule for LocalDate and LocalDateTime
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message.getData());

            CustNextBilldateMessage dataMessage = objectMapper.readValue(json, CustNextBilldateMessage.class);
            log.info("CMS Service Receive Kafka Message From Radius-Micro-Service  : " + message);
            customerService.updateCustomerAndCustomerPlanDate(dataMessage);
        } catch (Exception e) {
            log.error("Error in handleCustMacMessage: " + message + " Exception: " + e.getMessage());
        }
    }
    @Async
    public void handleSendQuotaResetMsg(KafkaMessageData message){
        try{
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
            CustQuotaResetDTO dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustQuotaResetDTO.class);
            customerService.resetCustomerQuota(dataMessage);
        }catch(Exception e){
            log.error("Error handling SEND_QUOTA"+e.getMessage());
        }
    }

    @Async
    public void handleNasUpdateMessage(KafkaMessageData message) {
        try {
            NasUpdateMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), NasUpdateMessage.class);
            customerService.nasUpdate(dataMessage.getCustomerId(), dataMessage.getNasPort(), dataMessage.getFramedIp());
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling NasUpdateMessage" + e.getMessage());
        }
    }

    @Async
    public void handleCustomerQuotaInfo(KafkaMessageData message) {
        try {
            log.info("Success handling handleCustomerQuotaInfo  : " + message);
            CustomerQuotaInfo dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerQuotaInfo.class);
            customerService.updateCustomerPlanDetail(dataMessage);
        } catch (Exception e) {
            log.error("Error handling handleCustomerQuotaInfo" + e.getMessage());
        }
    }

    @Async
    public void handleSendQuotaMsg(KafkaMessageData message) {
        try {
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
            SendQuotaMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendQuotaMsg.class);
            custQuotaService.sendNotificationOfQuota(dataMessage.getQuotaData());
        } catch (Exception e) {
            log.error("Error handling SEND_QUOTA" + e.getMessage());
        }
    }

    @Async
    public void handleCUSTOMERS_UPDATE_RESERVED_QUOTA(KafkaMessageData message) {
        try {
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
            SendQuotaMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendQuotaMsg.class);
            custQuotaService.setCustomerChunkQuota(dataMessage);
        } catch (Exception e) {
            log.error("Error Handling CUSTOMERS_UPDATE_RESERVED_QUOTA Message" + e.getMessage());
        }
    }

    @Async
    public void handleQUOTA_INTRIM(KafkaMessageData message) {
        try {
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
            SendQuotaIntrimMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendQuotaIntrimMsg.class);
            custQuotaService.saveCustQuotaIntrim(dataMessage.getQuotaData());
        } catch (Exception e) {
            log.error("Error Handling QUOTA_INTRIM Message" + e.getMessage());
        }
    }

    @Async
    public void handleUPDATE_CONCURRENCY(KafkaMessageData message) {
        try {
            CustomerUpdateMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerUpdateMessage.class);
            custMacMapppingService.updateCustomerConcurrency(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error Handling UPDATE_CONCURRENCY Message" + e.getMessage());
        }
    }

    @Async
    public void handleCUSTOMER_ENDDATE(KafkaMessageData message) {
        try {
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
            CustomerEndDateUpdateMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerEndDateUpdateMessage.class);
            customerService.recieveCustomerEnddateMessage(dataMessage);
        } catch (Exception e) {
            log.error("Error Handling Customer_EndDateMessage" + e.getMessage());
        }
    }

//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_TICKET_TOPIC},groupId = KafkaConstant.KAFKA_TICKET_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormTicketMicroService(KafkaMessageData message) {
//        log.info("kafkaMessageReceiveFormTicketMicroService: topic: "+KafkaConstant.KAFKA_TICKET_TOPIC+" group Id: "+KafkaConstant.KAFKA_TICKET_GROUP_ID);
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {

    /// /                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Ticket Microservice: " + e.getMessage(), e);
//        }
//    }
    @Async
    public void handlesaveTicketDataFromMicroService(KafkaMessageData message) {
        try {
            CloseTicketCheckMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CloseTicketCheckMessage.class);
            caseCustometDetailsService.saveCaseCustomerDetails(dataMessage);
            log.info("CMS Service Receive Kafka Message From SalesCrm-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleCustomerQuotaInfo" + e.getMessage());
        }
    }

    @Async
    public void handleupdateTicketDataFromMicroService(KafkaMessageData message) {
        try {
            CloseTicketCheckMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CloseTicketCheckMessage.class);
            caseCustometDetailsService.updateCaseCustomerDetails(dataMessage);
            log.info("CMS Service Receive Kafka Message From SalesCrm-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleCustomerQuotaInfo" + e.getMessage());
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_SALES_CRM_TOPIC},groupId = KafkaConstant.KAFKA_SALSE_CRM_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFromSalesCrmService(KafkaMessageData message) {
//        log.info("kafkaMessageReceiveFromSalesCrmService: topic: "+KafkaConstant.KAFKA_SALES_CRM_TOPIC+" group Id: "+KafkaConstant.KAFKA_SALSE_CRM_GROUP_ID);
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
////                  key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
////                  handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {

    /// /                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }
    @Async
    public void handleLeadData(KafkaMessageData message) {
        try {
            SendSaveLeadData dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SendSaveLeadData.class);
            this.hierarchyService.leadManagementWorkflowRequest(dataMessage.getLeadMgmtWfDTO());
            log.info("CMS Service Receive Kafka Message From SalesCrm-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleCustomerQuotaInfo" + e.getMessage());
        }
    }

    @Async
    public void handlereceiveLeadDocDetails(KafkaMessageData message) {
        try {
            SendLeadDocConvertPojo dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendLeadDocConvertPojo.class);
            if (dataMessage != null && dataMessage.getCustomerDocDetailsDTOList() != null && dataMessage.getCustomerDocDetailsDTOList().size() > 0) {
                customerDocDetailsService.saveCustDocFromLeadDoc(dataMessage.getCustomerDocDetailsDTOList());
            }
            log.info("CMS Service Receive Kafka Message From SalesCrm-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleCustomerQuotaInfo" + e.getMessage());
        }
    }

    @Async
    public void handlereceiveLeadMaster(KafkaMessageData message) {
        try {
            LeadMasterPojoMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), LeadMasterPojoMessage.class);
            this.leadMasterService.save(new LeadMasterPojo(dataMessage));
            log.info("CMS Service Receive Kafka Message From SalesCrm-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleCustomerQuotaInfo" + e.getMessage());
        }
    }

    @Async
    public void handlereceiveLeadData(KafkaMessageData message) {
        try {
            SendLeadQuotationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendLeadQuotationMessage.class);
            this.hierarchyService.leadQuotationWorkflowRequest(dataMessage.getLeadQuotationWfDTO());
            log.info("CMS Service Receive Kafka Message From SalesCrm-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleCustomerQuotaInfo" + e.getMessage());
        }
    }

    @Async
    public void handleCustMilestoneDetails(KafkaMessageData message) {
        try {
            QuickInvoicePojoMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), QuickInvoicePojoMessage.class);
            QuickInvoiceCreationPojo pojo = new QuickInvoiceCreationPojo(dataMessage);
            List<CustMilestoneDetailsPojo> list = custMilestoneDetailsService.saveCustomerMileStoneWithLead(pojo);
            log.info("CMS Service Receive Kafka Message From SalesCrm-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleCustomerQuotaInfo" + e.getMessage());
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_INTEGRATION_TOPIC},groupId = KafkaConstant.KAFKA_INTEGRATION_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFromIntegrationMicroService(KafkaMessageData message) {
//        log.info("kafkaMessageReceiveFromIntegrationMicroService: topic: "+KafkaConstant.KAFKA_INTEGRATION_TOPIC+" group Id: "+KafkaConstant.KAFKA_INTEGRATION_GROUP_ID);
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {

    /// /                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Integration Microservice: " + e.getMessage(), e);
//        }
//    }
    @Async
    public void handleinwardFromRms(KafkaMessageData message) {
        try {
            InwardDto dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InwardDto.class);
            inwardService.saveEntityFromRms(dataMessage, false, false);
            log.info("CMS Service Receive Kafka Message From Integration-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleinwardFromRms" + e.getMessage());
        }
    }

    @Async
    public void handleitemFromRms(KafkaMessageData message) {
        try {
            ItemDto dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ItemDto.class);
            itemService.saveEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Integration-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleitemFromRms" + e.getMessage());
        }
    }

    @Async
    public void handleitemHistoryFromRms(KafkaMessageData message) {
        try {
            InOutWardMACMapingDTO dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InOutWardMACMapingDTO.class);
            inOutWardMACService.saveEntityFromIntegrationRms(dataMessage);
            log.info("CMS Service Receive Kafka Message From Integration-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleitemHistoryFromRms" + e.getMessage());
        }
    }

    @Async
    private void handleSavePartnerData(KafkaMessageData message) {
        try {
            SavePartnerSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePartnerSharedDataMessage.class);
            partnerService.savePartnerEntiry(dataMessage);
            log.info("CMS Service Receive Kafka Message From Integration-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleitemHistoryFromRms" + e.getMessage());
        }
    }

    @Async
    private void handleUpdatePartnerData(KafkaMessageData message) {
        try {
            UpdatePartnerSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePartnerSharedDataMessage.class);
            partnerService.updatePartnerData(dataMessage);
            log.info("CMS Service Receive Kafka Message From Integration-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleitemHistoryFromRms" + e.getMessage());
        }
    }

    @Async
    private void handleCustomerPaymentStatus(KafkaMessageData message) {
        try {
            CustPayDTOMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustPayDTOMessage.class);
            paymentGatewayService.addCustomerPayment(dataMessage);
        } catch (Exception e) {
            log.error("Error handling CustPayDTOMessage: " + e.getMessage());
        }
    }

    private void handleCustomerPaymentBuyPlan(KafkaMessageData message) {
        try {
            CustPayDTOMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustPayDTOMessage.class);
            paymentGatewayService.getCommonPaymentGatewayResponse(dataMessage.getOrderId(), dataMessage.getPgTransactionId(), dataMessage.getPaymentGatewayName());
        } catch (Exception e) {
            log.error("Error handling CustPayDTOMessage: " + e.getMessage());
        }
    }

    private void handleCustomerCallLogsData(KafkaMessageData message) {
        try {
            log.info("::::::::::::Receive Kafka Call For save CustomerCallLogs Data:::::::::::::::");
            CustCallLogsDTO custCallLogsDTO = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustCallLogsDTO.class);
            custCallLogsService.save(custCallLogsDTO);
        } catch (Exception e) {
            log.error("Error handling CustCallLogsDTO: " + e.getMessage());
        }
    }

    @Async
    private void handleAutoRenewOrAddonPlanRequestSync(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            JsonNode node = objectMapper.valueToTree(message.getData());

            if (node.isArray()) {
                List<AutoRenewOrAddonPlanRequestDto> dataList = objectMapper.convertValue(
                        node,
                        new TypeReference<List<AutoRenewOrAddonPlanRequestDto>>() {}
                );
                subscriberService.autoRenewOrAddonPlan(dataList);
            } else {
                AutoRenewOrAddonPlanRequestDto data = objectMapper.convertValue(
                        node,
                        AutoRenewOrAddonPlanRequestDto.class
                );
                subscriberService.autoRenewOrAddonPlan(data);
            }

        } catch (Exception e) {
            log.error("Error handling AutoRenewOrAddonPlanRequest: " + e.getMessage());
        }
    }

    @Async
    public void handleMessageforCafAutoApprovalIfPaymentSettled(KafkaMessageData message) {
        try {
            WorkFlowAutoApprovalMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), WorkFlowAutoApprovalMessage.class);
            System.out.println("::::::::: Kafka Message for WorkFlow AutoApproval for Wallet Settlement Action Recieved Started ::::::::::::::"+ dataMessage);
            hierarchyService.autoApproveEntityBasedOnEventTrigger(dataMessage.getCustomerId(),dataMessage.getTriggeredAction(),dataMessage.getMvnoId(), dataMessage.getBuId());
            System.out.println("::::::::: Kafka Message for WorkFlow AutoApproval for Wallet Settlement Action Recieved Completed ::::::::::::::"+ dataMessage);
            log.info("Handled Auto Payment Approve call for fully adjusted amount successfully: " + message);
        } catch (Exception e) {
            log.error("Error Auto Payment Approve call for fully adjusted amount : " + e.getMessage(), e);
        }
    }


    @Async
    public void handleSubArea(KafkaMessageData message) {
        try {
            SubAreaMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SubAreaMessage.class);
            subAreaService.saveData(dataMessage);
            log.info("Handled Auto Payment Approve call for fully adjusted amount successfully: " + message);
        } catch (Exception e) {
            log.error("Error Auto Payment Approve call for fully adjusted amount : " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageforSavingBuildingMgmt(KafkaMessageData message) {
        try {
            BuildingMgmtMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), BuildingMgmtMessage.class);
            buildingMgmtService.saveEntity(dataMessage);
            log.info("Handled Building Management successfully: " + message);
        } catch (Exception e) {
            log.error("Error Handleling Building Management successfully  : " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageforUpdataingBuildingMgmt(KafkaMessageData message) {
        try {
            BuildingMgmtMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), BuildingMgmtMessage.class);
            buildingMgmtService.UpdateEntity(dataMessage);
            log.info("Handled Building Management successfully: " + message);
        } catch (Exception e) {
            log.error("Error Handleling Building Management successfully  : " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateDebitdocGraceDayMessage(KafkaMessageData message) {
        try {
            log.info("****** Inside handleUpdateDebitdocGraceDayMessage: " + message);
            UpdateDebitdocGraceDayMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateDebitdocGraceDayMessage.class);
            debitDocService.updateDebitdocGraceDay(dataMessage);
            log.info("Handled UpdateDebitdocGraceDayMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error UpdateDebitdocGraceDayMessage failed  : " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCustChargeInstallmentsMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            CustChargeInstallmentsMessage dataMessage = mapper.convertValue(message.getData(), CustChargeInstallmentsMessage.class);
            if(dataMessage.getCustChargeDetailsId() != null) {
                custChargeInstallmentRepository.updateChargeDetailsInstallmentDatesAndNo(
                        dataMessage.getInstallmentStartDate(),
                        dataMessage.getNextInstallmentDate(),
                        dataMessage.getLastInstallmentDate(),
                        dataMessage.getInstallmentNo(),
                        dataMessage.getCustChargeDetailsId()
                );
            } else if(dataMessage.getCustChargeHistoryId() != null){
                custChargeInstallmentRepository.updateChargeHistoryInstallmentDatesAndNo(
                        dataMessage.getInstallmentStartDate(),
                        dataMessage.getNextInstallmentDate(),
                        dataMessage.getLastInstallmentDate(),
                        dataMessage.getInstallmentNo(),
                        dataMessage.getCustChargeHistoryId()
                );
            } else {
                log.error("custChargeHistoryId and custChargeDetailsId both are null so not able to update in custChargeInstallment: " + message);
            }
            log.info("Handled handleCustChargeDetailsMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handleCustChargeDetailsMessage failed  : " + e.getMessage(), e);
        }
    }



    @Async
    public void handleCustCwscOnlinePaymentDTO(KafkaMessageData message) {
        try {
            log.info("****** Inside handleCustCwscOnlinePaymentDTO: " + message);
            CustCwscOnlinePaymentDTO dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustCwscOnlinePaymentDTO.class);
            paymentGatewayService.changePlanAndCreatInvoiceFromCustomer(dataMessage);
            log.info("Handled customer change plan and creating invoice successfully: " + message);
        } catch (Exception e) {
            log.error("Error customer change plan and creating invoice failed  : " + e.getMessage(), e);
        }
    }
    @Async
    public void handleDebitDocAfterBudPay(KafkaMessageData message) {
        System.out.println("****** Handle BudPay Payment Data: " + message);
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<DebitDocumentDTOForAdjustment> debitDocumentDTOForAdjustmentList = mapper.convertValue(message.getData(), new TypeReference<List<DebitDocumentDTOForAdjustment>>() {});
            debitDocService.updateDebitDocuments(debitDocumentDTOForAdjustmentList);
        }
       catch (Exception e){
            e.getMessage();
       }
    }

    @Async
    public void handleHouseHoldIdValidationResponse(KafkaMessageData message) {
        try {
            log.info("****** Inside handleHouseHoldIdValidationResponse: " + message);
            IdValidationResponse dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), IdValidationResponse.class);
//            idValidationService.save(dataMessage);
            log.info("Handled HouseHoldId Validation Response and creating new Entry successfully: " + message);
        } catch (Exception e) {
            log.error("Error while saving  HouseHoldId Validation Response : " + e.getMessage(), e);
        }
    }

}
