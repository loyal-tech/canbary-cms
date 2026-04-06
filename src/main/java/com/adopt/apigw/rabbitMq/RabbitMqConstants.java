package com.adopt.apigw.rabbitMq;

public class RabbitMqConstants {



    public static final String ADOPT_EXCHANGE="adopt.exchange";

    public static final String DEAD_LETTER_QUEUE = "deadLetter.queue";
    public static final String DEAD_LETTER_EXCHANGE = "deadLetterExchange";
    public static final String DEAD_LETTER_KEY = "deadLetterKey";

    public static final String MOBILE_NUMBER = "mobileNumber";
    public static final String PASSWORD = "password";
    public static final String USER_NAME = "username";
    public static final String EMAIL_ID = "emailId";
    public static final String EMAIL_ADDRESS = "emailAddress";

    public static final String SOURCE_NAME_ADOPT_BSS_GATEWAY = "Adopt BSS API GATEWAY";
    public static final String MVNO_ID = "mvnoId";
    public static final String BU_ID = "buId";
    public static final String  TEAM_NAME = "TeamName";

    public static final String  SLICE_CHUNK="slicechunk";




    public static final String QUEUE_BSS_CUSTOMER_APPROVAL_SUCCESS="bss.customer.approval.success";
    public static final String QUEUE_BSS_CUSTOMER_APPROVAL_FAIL="bss.customer.approval.fail";

    public static final String QUEUE_BSS_CUSTOMER_REGISTRATION_SUCCESS="bss.customer.registration.success";
    public static final String QUEUE_BSS_CUSTOMER_REGISTRATION_FAIL="bss.customer.registration.fail";

    public static final String QUEUE_BSS_CUSTOMER_RENEWAL_SUCCESS="bss.customer.renewal.success";
    public static final String QUEUE_BSS_CUSTOMER_RECHARGE_SUCCESS="bss.customer.recharge.success";
    public static final String QUEUE_BSS_CUSTOMER_RENEWAL_FAIL="bss.customer.renewal.fail";
    public static final String QUEUE_BSS_CUSTOMER_RECHARGE_FAIL="bss.customer.recharge.fail";

    public static final String QUEUE_BSS_RECHARGE_SUCCESS="bss.customer.recharge.success";
    public static final String QUEUE_BSS_RECHARGE_FAIL="bss.customer.recharge.fail";

    public static final String QUEUE_BSS_CUSTOMER_PLAN_EXPIRE="bss.customer.plan.expire";

    public static final String QUEUE_BSS_CUSTOMER_PAYMENT_LINK="bss.customer.payment.link";
    public static final String QUEUE_BSS_CUSTOMER_PAYMENT_SUCCESS="bss.customer.payment.success";


    public static final String PREVIOUS_CAF_APPROVER="previousCafApprover";

    public static final String NEXT_CAF_APPROVER="nextCafApprover";

    public static final String CAF_APPROVAL_STATUS="cafApproveStatus";


    public static final String CUSTOMER_APPROVAL_SUCCESS="Customer Approval Success";
    public static final String CUSTOMER_APPROVAL_FAIL="Customer Approval Fail";
    public static final String CUSTOMER_RECHARGE_SUCCESS="Customer Recharge Success";
    public static final String CUSTOMER_RENEW_SUCCESS="Customer Renew Success";

    public static final String APPROVE_SUCCESS = "Customer Approval Success";
    public static final String APPROVE_REJECT="Customer Approval Failure";

    public static final String REGISTRATION_SUCCESS= "Registration Success";

    public static final String REGISTRATION_FAIL = "Registration Failure";

    public static final String CUSTOMER_RENEW = "Renewal Success";

    public static final String CUSTOMER_RECHARGE = "Recharge Success";


    public static final String CUSTOMER_PAYMENT_LINK = "Payment Link";
    public static final String CUSTOMER_PAYMENT_SUCCESS = "Payment Success";
    public static final String CUSTOMER_PAYMENT_FAILED = "Payment Failed";

    public static final String CUSTOMER_STATUS_NEW_ACTIVATION  = "NewActivation";
    public static final String CUSTOMER_STATUS_ACTIVE  = "Active";

    public static final String QUEUE_APIGW_CUSTOMER = "apigw.customer.queue";
    public static final String QUEUE_APIGW_CUSTOMER_STATUS_UPDATE = "apigw.customer.status.queue";
	public static final String QUEUE_APIGW_CUSTOMER_MAC_MAPPING = "apigw.customer.mac.mapping";
	public static final String QUEUE_APIGW_CUSTOMER_PACKAGE_REL = "apigw.customer.package.rel";
    public static final String QUEUE_APIGW_POSTPAIDPLAN = "apigw.plan";
    public static final String QUEUE_APIGW_QOS_POLICY = "apigw.qospolicy";
    public static final String QUEUE_APIGW_CUST_REPLY = "apigw.custreply";
    public static final String CURRENCY_SYMBOLE = "Rs.";

    public static final String TICKET_SUCCESS = "Ticket";
    public static final String TICKET_ASSIGN_SUCCESS = "Ticket";
    public static final String QUEUE_TICKET_ASSIGN_TEAM_SUCCESS = "bss.ticket.assign.team.success";

    public static final String QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS = "staff_create_from_bss";

    public static final String QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS1 = "staff_create_from_bss1";
    public static final String QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS = "staff_create_from_bss_to_task_mgmt";

    public static final String QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS = "service_area_created_from_bss";

    public static final String QUEUE_UPDATE_QUOTA = "update.bssquota.queue";

    public static final String CUSTOMER_DUNNING_TEMPLATE = "Customer Dunning";
    public static final String CUSTOMER_DEACTIVATION_TEMPLATE = "Customer Deactivation";
    public static final String STAFF_EXPIRED_TEMPLATE = "Staff Expired";
    public static final String STAFF_EXPIRED_TEMPLATE_HEADER = "Staff Expired";
    public static final String STAFF_STATUS_CHANGE_TEMPLATE = "Staff Status Change";
    public static final String STAFF_STATUS_CHANGE_TEMPLATE_HEADER = "Staff Status Change";


    public static final String QUEUE_BSS_CUSTOMER_DUNNING = "bss.customer.dunning";


    public static final String QUEUE_BSS_CUSTOMER_DEACTIVATION = "bss.customer.deactivation";
    public static final String CUSTOMER_DUNNING_TEMPLATE_HEADER = "Payment Reminder";

    public static final String CUSTOMER_DEACTIVATION_TEMPLATE_HEADER= "Customer Deactivation";
    
	public static final String QUEUE_RADIUS_CUST_MAC_ADD = "radius.add.mac";

    //OTPCOnstast
    public static final String OTP = "otp";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String QUEUE_OTP_GENERATION = "otp.generation.queue";
    //Time Base Policy
    public static final String QUEUE_APIGW_TIME_BASE_POLICY = "apigw.timebasepolicy";
    public static final String QUEUE_UPDATE_CUSTOMER_QUOTA = "update.customer.quota";
    public static final String QUEUE_BILLING_INVOICE = "billing.invoice";

//    public static final String QUEUE_UPDATE_CUSTOMER_QUOTA = "update.customer.quota";

    public static final String QUEUE_CUSTOMER_OTP_REGISTRATION = "customer.registration.message";

    public static final String CUSTOMER_OTP_REGISTRATION_TEMPLATE_HEADER = "Welcome Message 2";
    public static final String CUSTOMER_OTP_REGISTRATION_TEMPLATE = "Welcome Message 2";

    public static final String QUEUE_UPDATE_CUSTOMER_PASSWORD = "update.customer.password";

//Lead Mgmt workfloq queues

//    public static final String QUEUE_FIND_STAFF_FOR_LEAD = "bss.staff.find.for.lead";
//
//    public static final String QUEUE_COUNT_FOR_STAFF ="bss.count.for.staff";



    public static final String QUEUE_LEAD_MGMT_INIT_DATA ="bss.lead.mgmt.init.data";

    //Staff expired Document
    public static final String QUEUE_BSS_DOCUMENT_DUNNING_STAFF = "bss.customer.dunning.staff_expire";
    public static final String EXPIRED_DOCUMENT_TEMPLATE_HEADER = "Expired Document";
    public static final String EXPIRED_DOCUMENT_TEMPLATE = "Expired Document";

    //user create
    public static final String QUEUE_ROLE = "bss.role";
    public static final String QUEUE_USER = "bss.user";

    //businessunit create
    public static final String QUEUE_BUSINESS_UNIT = "bss.business.unit";

    public static final String QUEUE_SUB_BUSINESS_UNIT = "bss.subbusiness.unit";
    public static final String QUEUE_SEND_APPROVER_DETAIL = "send.aprover.detail";
    public static final String QUEUE_SEND_APPROVER_UPDATE_DETAIL = "send.aprover.update.detail";
    public static final String QUEUE_SEND_UPDATE_LEAD_INFO = "send.updated.lead.info";

    public static final String QUEUE_SEND_LEAD_STATUS_INFO = "send.lead.status.info";

    public static final String QUEUE_SEND_LEAD_STATUS_DTO = "send.lead.status.dto";

    public static final String QUEUE_SEND_NOTIFICATION_TAT = "send.tat.notification";

    public static final String QUEUE_APIGW_CREATE_TIME_BASE_POLICY = "apigw.create.timebasepolicy";

    public static final String QUEUE_APIGW_CREATE_TIME_BASE_POLICY_DETAILS = "apigw.create.timebasepolicydetails";

    public static final String TAT_SUCCESS = "TATNOTIFICATION";

    public static final String QUEUE_SEND_CUSTOMER_CAF_POJO = "send.customer.caf.pojo";
    
    public static final String QUEUE_APIGW_SEND_BRANCH = "apigw.send.branch";
    
    public static final String QUEUE_APIGW_SEND_PARTNER = "apigw.send.partner";
    
    public static final String QUEUE_APIGW_SEND_SERVICE_AREA = "apigw.send.servicearea";
    
    public static final String QUEUE_APIGW_SEND_CUSTOMER = "apigw.send.customer";

    public static final String QUEUE_STAFF_SEND_STATUS = "staff.send.status";
    
    public static final String QUEUE_APIGW_SEND_LEAD_DOC_CONVERT = "apigw.send.lead.doc.convert";

    public static final String QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION="prepaid.invoice";
    public static final String QUEUE_POSTPAID_CUSTOMER_INVOICE_CREATION="postpaid.invoice";
    public static final String QUEUE_POSTPAID_CUSTOMER_INVOICE_DIRECT_CHARGE="postpaid.charge";
    public static final String QUEUE_PREPAID_CUSTOMER_INVOICE_DIRECT_CHARGE="prepaid.charge";
    public static final String QUEUE_CUSTOMER_INVOICE_INVENTORY_CHARGE="inventory.charge";

    public static final String QUEUE_CLIENT_SERVICE_UPDATE="apigw.send.client.service.update";

    public static final String QUEUE_RADIUS_COA_DM = "apiw.send.radius.coadm";

    public static final String QUEUE_RADIUS_CUSTOMER_UPDATE_STATUS = "apiw.send.radius.customer.update.status";

    public static final String QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE= "apiw.send.workflow.action.assign.message";

    public static final String WORKFLOW_ASSIGN_ACTION_MESSAGE = "Workflow assign action";

    public static final String WORKFLOW_ASSIGN_ACTION = "Workflow Assign Action";
    
    public static final String QUEUE_APIGW_SEND_MVNO = "apigw.send.mvno";


    public static final String QUEUE_SEND_CUSTOMER_STATUS_CHANGE = "apiw.send.customer.status.change";
    public static final String SEND_CUSTOMER_STATUS_CHANGE_TEMPLATE = "Customer Ticket Status Change";

    public static final String QUEUE_APIGW_SEND_LEAD_MASTER = "apigw.send.leadMaster";

    public static final String QUEUE_APIGW_SERVICE_START_STOP = "apigw.service.status.change";

    public static final String QUEUE_TAT_SEND_PARENT_TO_TEAM = "apigw.tat.send.parent.to.team";

    public static final String TAT_SEND_PARENT_TO_TEAM = "TATNOTIFICATIONTOTEAM";

    public static final String TAT_NO_RESPONSE_TAKEN = "No response taken by team";

    public static final String QUEUE_PARTNER_INVOICE="partner.invoice";

    public static final String QUEUE_SEND_FOLLOWUP_REMARK_MSG = "apigw.send.followup.remark.msg";
    public static final String SEND_FOLLOWUP_REMARK_MSG = "Followup Remark Message";
    public static  final String FOLLOWUP_REMARK_MSG = "TicketFollowUpRemark";

    public static final String QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG = "send.problem.domain.change.msg";

    public static  final String SEND_PROBLEM_DOMAIN_TEMPLATE_NAME = "TicketDomainChangeMsg";
    public static final String SEND_PROBLEM_DOMAIN_REMARK_MSG = "Problem Domain Change";
    public static final String QUEUE_SEND_NASUPDATE = "bss.radius.send.nasupdate";

    public static final String QUEUE_APIGW_SEND_POP_MANAGEMENT = "apigw.send.popManagement";


    public static final String QUEUE_TICKET_ETR = "bss.ticket.etr";

    public static final String QUEUE_TICKET_ETR_AUDIT = "bss.ticket.etr.audit";

    public static final String QUEUE_CUSTOMER_EMAIL_DOC_AUDIT = "bss.customer.email.etr.audit";
    public static final String  TICKET_ETR_TEMPLATE = "Ticket ETR Template";
    public static final String  TICKET_ETR_TEMPLATE_DYNAMIC = "Ticket ETR Dynamic Template";

    public static final String QUEUE_APIGW_SEND_TEAMS = "apigw.send.teams";


    //template name
    public static final String CAF_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE = "CAF Follow Up Reminder For Staff";
    public static final String CAF_FOLLOW_UP_REMINDER_FOR_CUSTOMER_TEMPLATE = "CAF Follow Up Reminder For Customer";
    public static final String CAF_FOLLOW_UP_OVERDUE_FOR_STAFF_TEMPLATE = "CAF Follow Up OverDue For Staff";
    public static final String CAF_FOLLOW_UP_OVERDUE_FOR_PARENT_STAFF_TEMPLATE = "CAF Follow Up OverDue For Parent Staff";

  //notification header name
    public static final String CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER = "CAF Follow up Reminder For Customer";
    public static final String CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "CAF Follow up Reminder For Staff";
    public static final String CAF_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF = "CAF Follow up Overdue For Staff";
    public static final String CAF_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF = "CAF Follow up Overdue For Parent Staff";

    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF = "bss.customer.caf.followup.reminder.staff";
    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER = "bss.customer.caf.followup.reminder.customer";
    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF = "bss.customer.caf.followup.overdue.staff";
    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "bss.customer.caf.followup.overdue.parent.staff";
    public static final String QUEUE_TEAM_SEND_TASK_MGMT_SUCCESS = "team_create_from_bss_to_task_mgmt";
    public static final String QUEUE_BUSINESS_UNIT_SEND_TASK_MGMT_SUCCESS = "business_unit_create_from_bss_to_task_mgmt";
    public static final String QUEUE_BILL_GEN_SEND_INTEGRATION_SYSTEM = "bss.apigw.integrationsytem.billgen";
    public static final String QUEUE_CUSTOMER_SEND_INTEGRATION_SYSTEM = "bss.apigw.integrationsytem.customer";
    public static final String QUEUE_CHARGE_MGMTN_SUCCESS="charge_management";

    public static final String QUEUE_PLAN_SERVICE_SUCCESS="plan_service_management";
    public static final String QUEUE_CUSTOMERS_SUCCESS="customers_management";



//    public static final String QUEUE_SEND_NASUPDATE = "bss.radius.send.nasupdate";


    //ticket followuo template name
    public static final String TICKET_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE = "TICKET Follow Up Reminder For Staff";
    public static final String TICKET_FOLLOW_UP_REMINDER_FOR_CUSTOMER_TEMPLATE = "TICKET Follow Up Reminder For Customer";
    public static final String TICKET_FOLLOW_UP_OVERDUE_FOR_STAFF_TEMPLATE = "TICKET Follow Up OverDue For Staff";
    public static final String TICKET_FOLLOW_UP_OVERDUE_FOR_PARENT_STAFF_TEMPLATE = "TICKET Follow Up OverDue For Parent Staff";

    //ticket followup notification header name
    public static final String TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER = "TICKET Follow up Reminder For Customer";
    public static final String TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "TICKET Follow up Reminder For Staff";
    public static final String TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF = "TICKET Follow up Overdue For Staff";
    public static final String TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF = "TICKET Follow up Overdue For Parent Staff";


    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF = "bss.ticket.followup.reminder.staff";
    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER = "bss.ticket.followup.reminder.customer";
    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF = "bss.ticket.followup.overdue.staff";
    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "bss.ticket.followup.overdue.parent.staff";


    public static final String QUEUE_TAX_MGMTN_SUCCESS="tax_management";
    public static final String QUEUE_SERVICE_AREA_SUCCESS="service_area";
    public static final String QUEUE_CREDIT_DOCUMENT_SUCCESS="credit_document";
    public static final String QUEUE_CREDIT_DOCUMENT_APPROVED_SUCCESS="credit_document";

    public static final String QUEUE_BUSINESS_UNIT_SUCCESS="business_unit";
    public static final String QUEUE_DEBIT_DOCUMENT_SUCCESS="debit_document";

    public static final String QUEUE_CUST_PLAN_MAPPING_UPDATE="cpr.debit.update";
    public static final String QUEUE_CANCEL_REGENERATE_SUCCESS="cancel_regenerate";
    public static final String QUEUE_STAFF_MANAGEMENT_SUCCESS="Staff Management";

    public static final String TICKET_CREATION = "Ticket Creation";
    public static final String TICKET_CREATION_SUCCESS= "Ticket Creation Success";

    public static final String QUEUE_TICKET_CREATION_SUCCESS="Staff Management";
    public static final String QUEUE_LEAD_ASSIGN_MESSAGE="lead.assign.message";
    public static final String QUEUE_BRANCH_SUCCESS="branch_success";
    public static final String QUEUE_CUSTOMER_SUCCESS="customer_success";

    public static final String QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG="bss.ticket.reschedule.success.message";

    public static final String TICKET_RESCHEDULE_MESSAGE = "TicketFollowUpRemark";
    public static final String TICKET_RESCHEDULE_SUCCESS_MSG = "Ticket Reschedule Successful";

    public static final String QUEUE_INTEGRATION_SYSTEM_CREDIT_NOTE_GEN = "bss.apigw.integrationsytem.creditnotegen";

    public static final String TICKET_TAT_REMINDER_NOTIFICATION = "TatBreachedFollowup";
    public static final String TICKET_TAT_REMINDER_NOTIFICATION_MSG = "Tat Breach Reminder";

    public static final String QUEUE_TICKET_TAT_BREACHED_REMINDER = "bss.ticket.tat.breached.reminder";

    public static final String TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION = "TatBreachedOverDueFollowup";
    public static final String TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION_MSG = "Tat Breach Overdue Reminder";

    //template name
    public static final String NO_CAF_FOLLOW_UP_REMINDER_FOR_PARENT_STAFF_TEMPLATE = "No CAF FollowUp Reminder for ParentStaff";
    public static final String NO_CAF_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE = "No CAF FollowUp Reminder for Staff";

    //message header name
    public static final String NO_LEAD_FOLLOW_UP_REMINDER_CAF_TEMPLATE_HEADER_FOR_STAFF = "No CAF FollowUp Reminder for Staff";
    public static final String NO_LEAD_FOLLOW_UP_REMINDER_CAF_TEMPLATE_HEADER_FOR_PARENT_STAFF = "No CAF FollowUp Reminder for ParentStaff";
    public static final String NO_FOLLOW_UP_REMINDER_CAF_TEMPLATE_HEADER_FOR_STAFF = "No FollowUp Reminder for Staff";
    public static final String NO_FOLLOW_UP_REMINDER_CAF_TEMPLATE_HEADER_FOR_PARENT_STAFF = "No FollowUp Reminder for ParentStaff";

    public static final String NO_FOLLOW_UP_REMINDER_FOR_CAF_STAFF_TEMPLATE = "No FollowUp Reminder for Staff";


    public static final String QUEUE_BSS_NO_FOLLOW_UP_REMINDER_STAFF = "bss.nofollowup.reminder.staff";

    public static final String QUEUE_BSS_NO_FOLLOW_UP_REMINDER_CAF_PARENT_STAFF = "bss.nofollowup.reminder.caf.parent.staff";




    /** RabbitMq for voucher code**/
    public static final String QUEUE_SEND_VOUCHERCODE = "send.vouchercode.queue";

    public static final String CUSTOMER_VOUCHER_TEMPLATE = "Voucher Code";


    /** Rabbitmq for advance notification**/
    public static final String CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE = "Customer Dunning Advance Notification";

    public static final String CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE_HEADER = "Plan Expiry Reminder";

    public static final String QUEUE_DUNNING_ADVANCE_NOTIFICATION = "send.dunning.advance.notification";

    /** Rabbitmq for partner document**/
    public static final String PARTNER_DUNNING_DOCUMENT_TEMPLATE = "Partner Dunning Document";

    public static final String PARTNER_DUNNING_DOCUMENT_TEMPLATE_HEADER = "Partner Document  Reminder";

    public static final String QUEUE_PARTNER_DUNNING_DOCUMENT = "send.dunning.partner.document";

   /**Rabbitmq for partner document ended**/



    /** Rabbitmq for partner document Deactivation started**/
    public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE = "Partner Dunning Deactivation Document";

    public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER = "Partner Document Deactivation Reminder";

    public static final String QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION = "send.dunning.partner.document.deactivation";

    /**Rabbitmq for partner document deactivation ended**/


    /** Rabbitmq for partner document Deactivation send to staff started**/
    public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF_TEMPLATE = "Staff Document Deactivation";

    public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF_TEMPLATE_HEADER = "Staff Document Deactivation Reminder";

    public static final String QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF = "send.dunning.partner.document.deactivation.staff";

    /**Rabbitmq for partner document deactivation send to staff ended**/

    public static final String QUEUE_SEND_BUD_PAYMENT_CREDIT_TO_REVENUE = "queue.bud.payment.credit.to.revenue";


    //    public static final String QUEUE_SEND_SERIAL_NUMBER = "bss.apigateway.send.serialnumber";
    public static final String UPDATE_PLAN_PRICES_IN_CRM = "send.updated.plan.prices";

    //QUEUE_TICKET_OVERDUE_TAT_BREACHED_REMINDER = "bss.ticket.tat.overdue.breached.reminder";
    public static final String QUEUE_COMMON_QUEUE_FOR_ALL_NOTIFICATION = "bss.common.queue.for.all.notification";

    public static final String QUEUE_LEAD_CAF_CONVERTION = "lead.caf.convertion";

    /** for customer inactive status **/
    public static final String CUSTOMER_STATUS_INACTIVATE_TEMPLATE = "Customer Status InActive";
    public static final String CUSTOMER_STATUS_INACTIVATE_EVENT = "Customer Status InActive";
    public static final String QUEUE_CUSTOMER_STATUS_INACTIVATE_NOTIFICATION = "customer.status.inactive.notification";
    /** for customer inactive status **/


    /** for customer document verification **/
    public static final String CUSTOMER_DOCUMENT_VERIFICATION_TEMPLATE = "Customer Document Verification Template";
    public static final String CUSTOMER_DOCUMENT_VERIFICATION_EVENT = "Customer Document Verification";
    public static final String QUEUE_CUSTOMER_DOCUMENT_VERIFICATION_NOTIFICATION = "customer.document.verification.notification";
    /** for customer document verification ended**/

    /** for customer service active **/
    public static final String CUSTOMER_SERVICE_ACTIVE_TEMPLATE = "Customer Service Active Template";
    public static final String CUSTOMER_SERVICE_ACTIVE_EVENT = "Customer Service Active";
    public static final String QUEUE_CUSTOMER_SERVICE_ACTIVE_NOTIFICATION = "customer.service.active.notification";
    /** for customer service active ended**/


    /** for customer service inactive **/
    public static final String CUSTOMER_SERVICE_INACTIVE_TEMPLATE = "Customer Service InActive Template";
    public static final String CUSTOMER_SERVICE_INACTIVE_EVENT = "Customer Service InActive";
    public static final String QUEUE_CUSTOMER_SERVICE_INACTIVE_NOTIFICATION = "customer.service.inactive.notification";
    /** for customer service inactive ended**/

    public static final String QUEUE_LEAD_QUOTATION_WF = "send.lead.quotation.wf";
    public static final String QUEUE_SEND_APPROVER_DETAIL_QUOTATION = "send.aprrover.detail.quotation";
    public static final String QUEUE_LEAD_QUOTATION_ASSIGN_MESSAGE="lead.quotation.assign.message";

    /** for customer change password**/
    public static final String CUSTOMER_CHANGE_PASSWORD_TEMPLATE = "Customer Change Password Template";
    public static final String CUSTOMER_CHANGE_PASSWORD_EVENT = "Customer Change Password";
    public static final String QUEUE_CUSTOMER_CHANGE_PASSWORD_NOTIFICATION = "customer.change.password.notification";
    /** for customer change password ended**/


    /** Rabbitmq for partner document**/
    public static final String CUSTOMER_DUNNING_DOCUMENT_TEMPLATE = "Customer Dunning Document";

    public static final String CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER = "Customer Document Reminder";

    public static final String QUEUE_CUSTOMER_DUNNING_DOCUMENT = "send.dunning.customer.document";

    /** for customer open address shifting**/
    public static final String CUSTOMER_OPEN_ADDRESS_SHIFTING_TEMPLATE = "Customer Open Address Shifting Template";
    public static final String CUSTOMER_OPEN_ADDRESS_SHIFTING_EVENT = "Customer Open Address Shifting";
    public static final String QUEUE_CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION = "customer.open.address.shifting.notification";
    /** for customer open address shifting ended**/

    /** for customer close address shifting**/
    public static final String CUSTOMER_CLOSE_ADDRESS_SHIFTING_TEMPLATE = "Customer Close Address Shifting Template";
    public static final String CUSTOMER_CLOSE_ADDRESS_SHIFTING_EVENT = "Customer Close Address Shifting";
    public static final String QUEUE_CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION = "customer.close.address.shifting.notification";
    /** for customer close address shifting ended**/

    /** for customer payment verification**/
    public static final String CUSTOMER_PAYMENT_VERIFICATION_TEMPLATE = "Customer Payment Verification Template";
    public static final String CUSTOMER_PAYMENT_VERIFICATION_EVENT = "Customer Payment Verification";
    public static final String QUEUE_CUSTOMER_PAYMENT_VERIFICATION_NOTIFICATION = "customer.payment.verification.notification";
    /** for customer payment verification ended**/

    /** for ticket close notification**/
    public static final String CUSTOMER_TICKET_CLOSE_TEMPLATE = "Customer Ticket Close Template";
    public static final String CUSTOMER_TICKET_CLOSE_EVENT = "Customer Ticket Close";
    public static final String QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION = "customer.ticket.close.notification";
    /** for customer ticket close ended**/

  /**Rabbitmq added for custpackage rel **/
  public static final String QUEUE_CUSTOMER_PLAN_MAPPING_FOR_INTEGRATION = "apigw.customer.planmapping.integration" ;

    public static final String QUEUE_CUSTOMER_SERVICE_MAPPING_FOR_INTEGRATION = "apigw.customer.servicemapping.integration" ;

    public static final String QUEUE_SERVICE_FOR_INTEGRATION = "apigw.service.integration" ;

    public static final String QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY = "apigw.customer.inventory" ;

    public static final String QUEUE_SERVICE_FOR_INVENTORY_ITEM = "apigw.inventory.item" ;


    public static final String QUEUE_APIGW_POSTPAIDPLAN_FOR_INTEGRATION = "apigw.plan.integration";

    public static final String QUEUE_APIGW_CUSTOMER_NOTIFICATION = "apigw.customer.notification.queue";

    public static final String QUEUE_APIGW_LEAD_MILESTONES_MAPPING = "apigw.lead.milestones.mapping";

    public static final String CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE = "Customer Dunning Deactivation Document";

    public static final String CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER = "CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER";


    /** for lead creation notificaton**/
    public static final String LEAD_CREATION_TEMPLATE = "Lead Creation Template";
    public static final String LEAD_CREATION_EVENT = "Lead Creation Success";
    public static final String QUEUE_LEAD_CREATION_NOTIFICATION = "lead.creation.notification";
    /** for lead creation notificaton ended**/


    public static final String QUEUE_APIGW_APPROVE_SERIALIZEDITEM_FOR_INTEGRATION = "apigw.approve.item.integration";
    public static final String QUEUE_APIGW_APPROVE_REMOVE_INVENTORY_SERIALIZEDITEM_REQUEST_IN_INTEGRATION = "apigw.approve.remove.item.request.integration";
    public static final String QUEUE_APIGW_CREATE_CUST_SERVICE_CHARGE_IP_DTLS = "apigw.create.custservicechargeipdtls";
    public static final String QUEUE_APIGW_UPDATE_CUST_SERVICE_CHARGE_IP_DTLS = "apigw.update.custservicechargeipdtls";

//    public static final String QUEUE_SEND_SERIAL_NUMBER = "bss.apigateway.send.serialnumber";

    public static final String QUEUE_APIGW_SEND_CUSTOMER_KPI = "apigw.send.customer.kpi";

    public static final String QUEUE_SERVICE_AREA_SUCCESS_KPI="service_area";

    public static final String QUEUE_BUSINESS_UNIT_KPI = "bss.business.unit.kpi";

    public static final String QUEUE_APIGW_BRANCH_KPI = "apigw.branch.kpi";

    public static final String QUEUE_APIGW_CUSTOMER_PACKAGE_REL_KPI = "apigw.customer.package.rel.kpi";

    public static final String QUEUE_PLAN_SERVICE_KPI="plan.service.kpi";

    public static final String QUEUE_DEBIT_DOCUMENT_SUCCESS_KPI="debit.document.kpi";

    public static final String QUEUE_CREDIT_DOCUMENT_KPI="credit.document.kpi";

    public static final String QUEUE_CUSTOMER_SERVICE_MAPPING_KPI = "apigw.customer.servicemapping.kpi" ;
    public static final String QUEUE_APIGW_TICKET_MESSAGE_INTEGRATION_SYSTEM = "apigw.ticketmessage.integrationsytem";

    public static final String QUEUE_REQUEST_GATEWAY_FOR_STAFFUSER = "request.apigw.get.staffuser";

    public static final String QUEUE_RESPONSE_GATEWAY_FOR_STAFFUSER = "response.apigw.get.staffuser";

    public static final String QUEUE_RESPONSE_TO_SAVE_STAFFUSER_FROM_GATEWAY = "response.apigw.to.kpi.save.staffuser";
    public static final String QUEUE_TICKET_TAT_AUDIT = "bss.ticket.tat.audit";

    public static final String QUEUE_TICKET_TAT_SUCCESS_MESSAGE= "bss.ticket.tat.success.message";
    public static final String QUEUE_CAF_TAT_SUCCESS_MESSAGE= "bss.caf.tat.success.message";
    public static final String QUEUE_TREMINATION_TAT_SUCCESS_MESSAGE= "bss.termination.tat.success.message";
    public static final String QUEUE_LEAD_TAT_SUCCESS_MESSAGE= "bss.lead.tat.success.message";

    public static final String QUEUE_COUNTRY = "country.queue";
    public static final String QUEUE_STATE = "state.queue";
    public static final String QUEUE_CITY = "city.queue";

    public static final String QUEUE_PLANGROUP_SALESCRM = "queue.plangroup.salescrm";
    public static final String QUEUE_PLANGROUP_SALESCRM_UPDATE = "queue.plangroup.salescrm.update";
    public static final String QUEUE_PINCODE = "pincode.queue";
    public static final String QUEUE_AREA = "area.queue";
    public static final String QUEUE_INTEGRATION_CREATE_SELFCARE_TICKET = "integration.create.selfcareticket";
    /** for tacacs sending message **/
    public static final String QUEUE_STAFF_SAVE_USER_SEND = "apigw.staff.save.user.queue";
    public static final String QUEUE_STAFFUSER_SEND_DELETE = "apigw.staff.user.queue.delete";
    public static final String QUEUE_ALG_SAVE_SEND = "tacacs.alg.save.queue";
    public static final String QUEUE_ALG_UPDATE_SEND = "tacacs.alg.update.queue";
    public static final String QUEUE_ALG_DELETE_SEND = "tacacs.alg.delete.queue";

    public static final String QUEUE_PRODUCT_FROM_RMS = "apigw.product.from.rms.integration";
    public static final String QUEUE_PRODUCTCATEGORY_INTEGRATOIN = "apigw.productcategory.integration";

    public static final String QUEUE_WAREHOUSE_INTEGRATOIN = "apigw.warehouse.integration";
    public static final String QUEUE_INWARD_RMS_INTEGRATOIN = "apigw.inward.rms.integration";
    public static final String QUEUE_SERIALIZED_ITEM_FROM_RMS_INTEGRATOIN = "apigw.serialized.item.from.rms.integration";
    public static final String QUEUE_SERIALIZED_ITEM_HISTORY_RMS_INTEGRATOIN = "apigw.serialized.item.history.rms.integration";
    public static final String QUEUE_SEND_INWARD_TO_INTEGRATOIN = "apigw.send.inward.to.integration";


    public static final String QUEUE_CUSTOMER_TERMINATION_CLOSE_TICKET_CALL = "queue.customer.termination.close.ticket.call";

    public static final String QUEUE_CREDIT_DOCUMENT_APPROVED_REVENUE="credit_document_revenue";


    public static final String QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_COMMON = "queue.customer.create.data.share.api.common";

    public static final String QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK = "queue.plan.service.area.binding.check";

    public static final String QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK_AT_DELETE = "queue.plan.service.area.binding.check.at.delete";

    public static final String QUEUE_INVENTORY_SEND_CREATE_NEW_CHARGE_TO_CMS = "bss.inventory.send.create.new.charge.to.cms";
    public static final String QUEUE_INVENTORY_SEND_UPDATE_NEW_CHARGE_TO_CMS = "bss.inventory.send.update.new.charge.to.cms";
    public static final String QUEUE_INVENTORY_SEND_CREATE_REF_CHARGE_TO_CMS = "bss.inventory.send.create.ref.charge.to.cms";
    public static final String QUEUE_INVENTORY_SEND_UPDATE_REF_CHARGE_TO_CMS = "bss.inventory.send.update.ref.charge.to.cms";

    public static final String QUEUE_APPROVE_ORG_INVOICE_REVENUE = "queue.org.invoice.approve.revenue";
    public static final String QUEUE_UPDATE_VOID_INVOICE_STATUS = "queue.update.void.invoice.status";

    public static final String QUEUE_PARTNER_CREATE_DATA_SHARE_CMS = "queue.partner.create.data.share.cms";
    public static final String QUEUE_PARTNER_UPDATE_DATA_SHARE_CMS = "queue.partner.update.data.share.cms";


    /**RabbitMq for Recieve quota details from radius start**/
    public static final String QUEUE_SEND_QUOTA_FROM_RADIUS = "send.quota.detail.radius.queue";
    /**RabbitMq for Recieve quota details from radius end**/

    /**RabbitMq for sending quota notifictaion to customer**/
    public static final String CUSTOMER_QUOTA_USAGE_TEMPLATE = "Quota Usage";

    public static final String CUSTOMER_QUOTA_EXHAUST_TEMPLATE = "Quota Exhaust";

    public static final String QUEUE_SEND_QUOTA_NOTIFICATION_CUSTOMER = "send.cust_quota_notification_customer";

    public static final String QUEUE_SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER = "send.cust_quota_exhuast_notification_customer";

    /**RabbitMq for sending quota notifictaion to customer**/


    public static final String QUEUE_SERVICE_TERMINATION_REVENUE = "queue.service.termination.data.share.revenue";
    public static final String QUEUE_SEND_CREATE_DATA_ROLE_CMS = "queue.send.create.data.role.to.cms";
    public static final String QUEUE_SEND_DELETE_DATA_ROLE_CMS = "queue.send.delete.data.role.to.cms";

    public static final String QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS = "send.quota.intrim.radius.queue";

    /**Send Customer online payment amount to revenue started**/

    public static final String QUEUE_SEND_CUSTOMER_ONLINE_PAYMENT = "queue.send.customer.online.payment.to.revenue";

    /**Send Customer online payment amount to revenue started ended**/

    public static final String QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS = "queue.send.item.serial.number.inventory.to.cms";

    public static final String QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_SALESCRM = "queue.customers.update.data.share.salescrm";
    public static final String QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_RADIUS = "queue.customers.update.data.share.radius";
    /**Recieve Customer mac delete from radius started**/

    public static final String QUEUE_DELETE_MAC_FROM_RADIUS = "delete.mac.from.radius";

    /**Recieve Customer mac delete from radius ended**/

    public static final String QUEUE_UPDATE_CONCURRENCY_FROM_RADIUS = "update.concurrency.from.radius";

    /**Recieve Customer enddate from radius started**/

    public  static  final String QUEUE_SEND_CUSTOMER_ENDDATE_FROMRADIUS = "radius.send.enddate.cms.queue";

    /**Recieve Customer enddate from radius ended**/

     public static final String QUEUE_CHANGE_PLAN_STATUS_CMS = "queue_change_plan_status_cms";

    public static final String QUEUE_DBR_SERVICE_HOLD_RESUME="revenue.dbr.service.hold.resume";

    /**send email for invoice ended**/

    public static final String QUEUE_SEND_POSTPAID_TRIAL_INVOICE_FROM_REVENUE = "queue.send.postpaid.trial.invoice.from.revenue";

    /**send Customer payment failed message from apigw started**/

    public  static  final String QUEUE_SEND_CUSTOMER_PAYMENT_FAILED = "apigw.send.customer.payment.failed.queue";

    /**send Customer payment failed message from apigw ended**/

    /**receive Customer quota reserve  message from radius started**/

    public static final String QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS = "queue.customers.update.reserved.quota.radius";

    /**receive Customer quota reserve  message from radius ended**/

    public static final String QUEUE_SEND_CUSTPLANMAPPINGS_REVENUE_TO_CMS_P2P = "queue.send.custplammap.revenue.to.cms";

    /**receive Payment configuration message from common started**/

    public static final String QUEUE_SEND_PAYMENT_CONFIGURTION_TO_CMS = "queue.send.payment.configration.to.cms";

    public static final String QUEUE_CMS_CONFIGURATION_INTIGRATION = "queue.customer.configuration.intigration";

    /**receive Payment configuration message from common started**/
    public static final String QUEUE_SEND_CUST_INV_DETAIL_TO_CMS = "queue.send.cust.inv.detail.to.cms";

    public static final String QUEUE_SEND_UUID_DATA_TO_CMS = "queue.send.uuid.data.to.cms";
    public static final String QUEUE_SEND_NMS_SERVICE_DELETE_REQUEST = "queue.send.nms.service.delete.request";
    public static final String QUEUE_SEND_CMS_UPDATE_STATUS_INVENTORY = "queue.send.cms.update.status.inventory";
    public static final String QUEUE_CREDIT_DEBIT_DOC_TO_CMS = "queue_credit_debit_doc_to_cms";

    /**send socket message to common started**/

    public static final String QUEUE_SEND_SOCKET_MESSAGE_TO_COMMON = "queue_send_socket_message_to_common";

    public static final String QUEUE_MVNO_DOC_SAVE_FROM_COMMON = "queue.mvno.doc.save.common";
    public static final String QUEUE_MVNO_DOC_UPDATE_FROM_COMMON = "queue.mvno.doc.update.common";

    /**send socket message to common ended**/

    /**send mvno document dunning message to  started**/

        public static final String QUEUE_SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION = "queue_send_mvno_dunning_message_to_notification";

    public static final String QUEUE_SEND_MVNO_STATUS_DUNNING_MESSAGE = "queue.send.mvno.status.message";
    public static final String QUEUE_SEND_STAFF_STATUS_DUNNING_MESSAGE = "queue.send.staff.status.message";

    public static final String QUEUE_SEND_CUSTOMER_STATUS_DUNNING_MESSAGE = "queue.send.customer.status.message";
    public static final String QUEUE_SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION = "queue_send_mvno_deactivation_message_to_notification";
    public static final String QUEUE_SEND_CUSTOMER_IP_TO_UPDATE_RADIUS_MESSAGE = "queue.send.customer.ip.to.update.radius.message";
    public static final String QUEUE_SEND_CUSTOMER_IP_TO_SAVE_RADIUS_MESSAGE = "queue.send.customer.ip.to.save.radius.message";

    public static final String QUEUE_SEND_CUSTOMER_IP_TO_DELETE_RADIUS_MESSAGE = "queue.send.customer.ip.to.delete.radius.message";

    public static final String QUEUE_SEND_CUSTOMER_STATUS_INACTIVE_DUNNING_MESSAGE = "queue.send.customer.status.inactive.message";
    public static final String QUEUE_SEND_MVNO_PAYMENT_MESSAGE_TO_NOTIFICATION = "queue_send_mvno_paayment_message_to_notification";
public static final String QUEUE_SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION = "queue_send_mvno_paayment_advance_notification";
    public static final String QUEUE_SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION = "queue_send_mvno_paayment_remainder_notification";
    public static final String QUEUE_SEND_BUDPAY_PAYMENT_SUCCESS = "queue_send_budpay_payment_success";

    public static final String QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS_ISP = "queue.create.mvno.common.apigw.to.cms.isp";

    public static final String QUEUE_SEND_CUST_PLAN_DETAIL_FROM_RADIUS = "send.cust.plan.detail.radius.queue";

    public static final String QUEUE_SEND_PLAN_EXPIRY_NOTIFICATION = "queue.plan.expiry.notification";

    public static final String QUEUE_APIGW_CUSTOMER_STATUS_UPDATE_RADIUS = "apigw.customer.status.radius.queue";

    public static final String QUEUE_APIGW_CUSTOMER_MAC_MAPPING_CMS = "apigw.customer.mac.mapping.cms";

    public static final String QUEUE_SEND_MVNO_DISCOUNT_REVENUE = "queue.mvno.discount.revenue";

    /**send Customer Budpay change plan message from apigw started**/

    public  static  final String QUEUE_SEND_BUDPAY_CUSTOMER_CWSC_CHANGE_PLAN_TO_REVENUE = "apigw.send.customer.budpay.cwsc.change.plan.revenue.queue";

    /**send Customer Budpay change plan message from apigw ended**/


    public static final String QUEUE_SEND_SAVE_VENDOR_QUEUE = "queue.send.save.vendor.queue";

    public static final String QUEUE_SEND_UPDATE_VENDOR_QUEUE = "queue.send.update.vendor.queue";
    public static final String QUEUE_SEND_PAYMENT_AUDIT_TO_CMS = "queue.send.payment.audit.to.cms";

    public static final String QUEUE_SEND_PAYMENT_AUDIT_TO_INTEGRATION = "queue.send.payment.audit.to.integration";

    public static final String QUEUE_SEND_LOCATION_TO_COMMON = "queue.send.location.to.common";
    public static final String QUEUE_APIGW_SEND_SERVICE_AREA_LOCATION_MAPPING = "queue.apigw.send.service.area.location.mapping";

    public static final String QUEUE_OTP_PROFILE_TO_COMMON = "queue.send.otp.profile.to.common";

    public static final String QUEUE_OTP_PROFILE_TO_COMMON_UPDATE = "queue.send.otp.profile.to.common.update";

    public static final String QUEUE_OTP_PROFILE_TO_CMS = "queue.send.otp.profile.to.cms";

    public static final String QUEUE_CPR_UPDATE_FROM_REVENUE_CMS = "revenue.cpr.enddate.update.cms";

    public static final String QUEUE_INVOICE_NUMBER_UPDATE_FROM_REVENUE_CMS = "revenue.invoice.number.update.cms";

    public static final String QUEUE_APIGW_CUSTOMER_STATUS_UPDATE_REVENUE = "apigw.customer.status.revenue.queue";
}
