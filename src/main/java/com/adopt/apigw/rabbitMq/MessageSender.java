package com.adopt.apigw.rabbitMq;

import org.springframework.stereotype.Component;

//
//import javax.transaction.Transactional;
//
//import com.adopt.apigw.MicroSeviceDataShare.SavePartnerPaymentMessage;
//import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveTeamHierarchyMappingMessage;
//import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.ShiftlocationMessage;
//import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateTeamHierarchyMappingMessage;
//import com.adopt.apigw.model.common.Customers;
//import com.adopt.apigw.modules.ChangePlanDTOs.ChangePlanMessageList;
//import com.adopt.apigw.modules.InventoryManagement.inward.InwardDto;
//import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategoryDto;
//import com.adopt.apigw.modules.InventoryManagement.warehouse.WareHouseDto;
//import com.adopt.apigw.pojo.api.CustomerDiscountPojo;
//import com.adopt.apigw.rabbitMq.message.*;
//import com.adopt.apigw.rabbitMq.message.ServiceTerminationMessage;
//import com.adopt.apigw.utils.NMSServiceActivationDTO;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
@Component
public class MessageSender {
//    private static Logger log = LoggerFactory.getLogger(MessageSender.class);
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    public String send(CustApprovalMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(InwardDto message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(WareHouseDto message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(ProductCategoryDto message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustApprovalFailMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerRegistrationSuccessMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerRegistrationFailMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerRenewalMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//
//    }
//
//    public String send(CustomerRechargeMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//
//    }
//
//    public String send(PaymentLinkMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//
//    }
//
//    public String send(PaymentSuccess message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//
//    }
//
//
//    public String send(CustomMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(LeadStatusMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(StaffUserMessage message, String queueName, String otherQueueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(QuotaDetailsMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustPackageRelMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustMacMappingMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(PostpaidPlanMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(QosPolicyMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerReplyMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(TicketAssignMessege message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(ServiceAreaMesseage message, String queueName,String otherQueueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerDunningMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerDeactivationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(OtpMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerOtpRegistrationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(CustomerTimeBasePolicyDetailsMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerBillingMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    //    public String send(CustomerOtpRegistrationMessage message,String queueName)
////    {
////        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
////        log.info("Send msg  "+ message);
////        return "Message Published";
////    }
//    public String send(StaffExpiredMassage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(RoleMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(UserMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(BusinessUnitMessage message, String queueName, String otherQueueName,String thirdQueueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, thirdQueueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(StaffForLeadMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    //    public String send(SendUpdateLeadData message, String queueName)
////    {
////        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
////        log.info("Send msg  "+ message);
////        return "Message Published";
////    }
//    @Transactional
//    public String send(SendApproverForLeadMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(SendUpdatedLeadInfo message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    //    public String send(SendTeamHierarchyDTO message, String queueName)
////    {
////        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
////        log.info("Send msg  "+ message);
////        return "Message Published";
////    }
//    public String send(TimeBasePolicyMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//
//    }
//
//    public String send(TimeBasePolicyDetailsListMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//
//    }
//
//    public String send(BranchMessage message, String queueName,String otherQueueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//
//    }
//
//    public String send(PartnerMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//
//    }
//
//    public String send(ServiceareaMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//
//    }
//
//    public String send(CustomerMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//
//    }
//
//    public String send(StaffStatusChangeMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(ClientServiceMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerCOADMupdateMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerUpdateMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(WorkflowTicketMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(MvnoMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//
//    public String send(CustTicketStatusMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerPackageRelMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(TicketPickMessageToTeam message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(PartnerBillingMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(SendFollowUpRemarkMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//
//    public String send(SendProblemDomainChangeMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(PopManagementMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(TicketETRMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(TeamsMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(CafFollowUpMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(TeamsMessage message, String queueName, String otherQueueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(TicketFollowUpMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(CreditNoteMessageIntegrationSystem creditNoteMessageIntegrationSystem, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, creditNoteMessageIntegrationSystem);
////        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
//        log.info("Send msg  " + creditNoteMessageIntegrationSystem);
//        return "Message Published";
//    }
//
//    public String send(Customers message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
////        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(ChargeMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        // rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(PlanServiceMessage message, String queueName,String otherQueueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//         rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
////    public String send(CustomersMessage message, String queueName) {
////        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
////        // rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
////        log.info("Send msg  " + message);
////        return "Message Published";
////    }
//
//    public String send(TaxMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(ServiceAreaIn message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CreditDocMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(BusinessUnitMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(DebitDocumentMessage message, String queueName,String otherQueueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, otherQueueName, message);
//        log.info("Send msg  " + message.getDocnumber());
//        return "Message Published";
//    }
//
//    public String send(CustPlanMappingUpdateMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message.getDebitDocumentId());
//        return "Message Published";
//    }
//
//    public String send(TicketCreationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(SendLeadAssignMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(BranchMessageIn message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//
//    }
//
//    public String send(CancelRegenerateInvoice message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//
//    }
//
//    public String send(CustomerMessageIn message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(TicketRescheduleMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    public String send(TicketTatReminderNotification message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
////    public String send(TicketTatOverDueNotification message, String queueName) {
////        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
////        log.info("Send msg " + message);
////        return "Message Published";
////    }
//
//    /**
//     * Message Sender For Voucher send Message
//     **/
//    public String send(VoucherCodeMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(UpdatePlanPricesMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(LeadMasterPojoMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**add new message sender for partner document**/
//
//    public String send(PartnerExpiredDocumentMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**add new message sender for partner document Deactivation**/
//
//    public String send(PartnerExpiredDocumentDeactivationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**add new message sender for partner document Deactivation to staff**/
//
//    public String send(PartnerExpiredDocumentDeactivationStaffMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
////    public String send(SerialNumberMessage message, String queueName) {
////        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
////        log.info("Send msg  " + message);
////        return "Message Published";
////    }
//
//    public String send(CustomerStatusInActiveMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustDocumentVerificationMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustServiceActiveMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(CustServiceInActiveMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(CustChangePasswordMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(SendLeadQuotationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**add new message sender for customer document**/
//    public String send(CustomerExpiredDocumentMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(CustomerMessageKpi message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//
//    /**add new message sender for customer address shifting**/
//    public String send(CustAddressShiftingMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**add new message sender for customer payment verification**/
//    public String send(CustPaymentVerificationMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**add new message sender for customer ticket close **/
//    public String send(CustTicketCloseMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustServiceMappingMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(PlanServiceForIntegrationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(CustomerInventoryMappingMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(ItemMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
////    public String send(QuickInvoicePojoMessage quickInvoicePojoMessage, String queueName){
////        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName,quickInvoicePojoMessage);
////        log.info("Send msg: ",quickInvoicePojoMessage);
////        return "Message Published";
////    }
//
//
//    /**add new message sender for lead creation **/
//    public String send(LeadCreationMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(CustServiceChargeIPDtlsMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//    public String send(TicketMessageIntegration message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(StaffUserMessage message, String queueName) {
//
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    };
//
//
//    public String send(List<UserMessage> message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CountryMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(StateMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CityMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(PlanGroupMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(PincodeMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(AreaMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(PlanServiceAreaBindingCheckMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CaftoCustomerMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(AppproveOrgInvoiceMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerTerminationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(ShiftlocationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(SavePartnerPaymentMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CAFCustomerStatusMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /** Message Sender for Quota usage to customer started**/
//    public String send(CustomerQuotaNotificationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    /**Message Sender for Quota usage to customer ended**/
//
//    public String send(ServiceTerminationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(SendOnlinePaymentRevenueMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(SaveTeamHierarchyMappingMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(UpdateTeamHierarchyMappingMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(QuotaCustomMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(DbrHoldResumeMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(PaymentFailedMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerDiscountPojo message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(NMSServiceActivationDTO message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(UuidDataDTO uuid, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, uuid);
//        log.info("Send msg  " + uuid);
//        return "Message Published";
//    }
//
//    public String send(SendSocketMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**Message sender for mvno document dunning started**/
//    public String send(MvnoDocumentDunningMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**Message sender for mvno status inactive**/
//    public String send(MvnoStatusMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**Message sender for mvno Expity MESSAGE**/
//    public String send(MvnoExpiryMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    /**Message sender for mvno status inactive**/
//    public String send(CustIPMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(BudPayPaymentMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**Message sender for mvno PAYMENT MESSAGE**/
//
//
//    /**Message sender for mvno document dunning started**/
//    public String send(MvnoPaymentDunningMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**Message sender for mvno document dunning started**/
//    public String send(PlanExpiryNotificationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(MvnoDiscountMessage message, String queueName) {
//        System.out.println("Send Message");
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(BudpayChangePlanMessage message, String queueName) {
//        System.out.println("Send Message");
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(CustPayDTOMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(OTPProfileMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(LocationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(ChangePlanMessageList message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//
//


//    /**add new message sender for lead creation **/
//    public String send(LeadCreationMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(CustServiceChargeIPDtlsMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg " + message);
//        return "Message Published";
//    }
//    public String send(TicketMessageIntegration message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(StaffUserMessage message, String queueName) {
//
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    };
//
//
//    public String send(List<UserMessage> message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CountryMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(StateMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CityMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(PlanGroupMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(PincodeMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(AreaMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(PlanServiceAreaBindingCheckMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CaftoCustomerMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(AppproveOrgInvoiceMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerTerminationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(ShiftlocationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    public String send(SavePartnerPaymentMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CAFCustomerStatusMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /** Message Sender for Quota usage to customer started**/
//    public String send(CustomerQuotaNotificationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//    /**Message Sender for Quota usage to customer ended**/
//
//    public String send(ServiceTerminationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(SendOnlinePaymentRevenueMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(SaveTeamHierarchyMappingMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(UpdateTeamHierarchyMappingMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(QuotaCustomMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(DbrHoldResumeMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(PaymentFailedMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(CustomerDiscountPojo message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(NMSServiceActivationDTO message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(UuidDataDTO uuid, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, uuid);
//        log.info("Send msg  " + uuid);
//        return "Message Published";
//    }
//
//    public String send(SendSocketMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**Message sender for mvno document dunning started**/
//    public String send(MvnoDocumentDunningMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**Message sender for mvno status inactive**/
//    public String send(MvnoStatusMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**Message sender for mvno Expity MESSAGE**/
//    public String send(MvnoExpiryMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    /**Message sender for mvno status inactive**/
//    public String send(CustIPMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(BudPayPaymentMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**Message sender for mvno PAYMENT MESSAGE**/
//
//
//    /**Message sender for mvno document dunning started**/
//    public String send(MvnoPaymentDunningMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    /**Message sender for mvno document dunning started**/
//    public String send(PlanExpiryNotificationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(MvnoDiscountMessage message, String queueName) {
//        System.out.println("Send Message");
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(BudpayChangePlanMessage message, String queueName) {
//        System.out.println("Send Message");
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(CustPayDTOMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(LocationMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(OTPProfileMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    public String send(ChangePlanMessageList message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//

}
