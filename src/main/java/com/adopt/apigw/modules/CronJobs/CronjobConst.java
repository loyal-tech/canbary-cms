package com.adopt.apigw.modules.CronJobs;

public class CronjobConst {

    public static final String TIME_FOR_IP_RELEASE = "cronjobtimeforiprelease";

    public static final String EVERY_DAY_IP_RELEASE = "everydaycronjobtimeforiprelease";

    public static final String PLAN_EXPIRY_JOB = "planexpiryjob";

    public static final String PLAN_STATUS_EXPIRY_JOB = "cronJobTimeForPlanExpiryStatusJob";

    public static final String QUOTA_EXPIRY_JOB = "quotaexpiryjob";

    public static final String REFUND_RELEASE = "cronjobtimeforrefundrelease";

    public static final String NEX_GE_PROVISION = "cronjobtimefornexgeprovision";

    public static final String CHANGE_PLAN = "cronjobtimefornexgechangeplan";

    public static final String REVENUE_DAY_MID_NIGHT = "cronjobtimeforrevenueeverydaymidnight";

    public static final String EVERY_MONTH_FIRST_DAY = "cronjobtimeforeverymonthfirstday";

    public static final String PARTNER_INVOICE = "cronjobtimeforpartnerinvoice";

    public static final String PARTNER_INVOICE_ADJUSTMENT = "cronjobtimeforpartnerinvoiceadjustment"; // Declare In App.prop But Not till  Use

    public static final String UPDATE_CUSTOMER_AND_SERVICE = "cronjobtimeforupdatecustomeranditsservice";

    public static final String QUOTA_RESET_DAY_MIDNIGHT = "cronjobtimeforquotaresetdaymidnight";

    public static final String ISP_AUTO_BILL = "cronjobtimeforispautobill";

    public static final String TIME_FOR_DUNNING = "cronJobTimeForDunning";

    public static final String TIME_FOR_PARTNER_COUNT = "cronJobTimeForPartnerCount";

    public static final String TIME_FOR_AUTOMATE_PAYMENT = "cronJobTimeForAutomatePayment";

    public static final String TIME_FOR_REASSIGN_TICKET = "cronJobTimeForReassignTicket";

    public static final String TIME_FOR_TAT_MATRIX = "cronJobTimeForTatMatrix";

    public static final String TIME_FOR_WARRANTY_DAYS = "cronJobTimeForwarrentydays";

    public static final String TIME_FOR_REMINDER_CAF_FOLLOWUP = "cronJobTimeForReminderCafFollowUp";

    public static final String TIME_FOR_OVER_DUE_CAF_FOLLOWUP = "cronJobTimeForOverDueCafFollowUp";

    public static final String TIME_FOR_REMINDER_TICKET_FOLLOWUP = "cronJobTimeForReminderTicketFollowUp"; // NO-USE

    public static final String TIME_FOR_ACTIVATE_SERVICE_IN_EZ_BILL = "cronJobTimeForActivateServiceInEZBill"; //No-Use

    public static final String TIME_FOR_TAT_OVER_DUE_MATRIX = "cronJobTimeForTatOverDueMatrix";//No-Use

    public static final String TIME_FOR_SERVICE_HOLD = "cronJobTimeForServiceHold";

    public static final String TIME_FOR_PLAN_EXPIRY_NOTIFICATION = "cronJobTimeForPlanExpiryNotification";

    public static final String TIME_FOR_CONNECTION_NUMBER_GENERATE = "cronjobtimeforconnectiongenerate";

    public static final String TIME_FOR_CUSTOMER_SERVICE_HOLD= "cronjobforServiceHold";
    public static final String TIME_FOR_CUSTOMER_SERVICE_RESUME= "cronjobforServiceResume";

    public static final String CAF_SCHEDULAR_CRONJOB = "cronjobtimeforcafclosedschedular";
    public interface SCHEDULER_AUDIT {
        public static final String SCHEDULER_STATUS_SUCCESS = "success";

        public static final String SCHEDULER_STATUS_LOCKED = "locked";

        public static final String SCHEDULER_STATUS_FAILURE = "failure";

        public static final String IP_RELEASE_SCHEDULER = "IP RELEASE Scheduler";

        public static final String EVERY_DAY_IP_RELEASE_SCHEDULER = "Every Day IP RELEASE Scheduler";

        public static final String PLAN_EXPIRY_JOB_SCHEDULER = "Plan Expiry Scheduler";

        public static final String PLAN_STATUS_EXPIRY_JOB_SCHEDULER = "Plan Status Expiry Scheduler";

        public static final String QUOTA_EXPIRY_SCHEDULER = "Quota Expiry Scheduler";

        public static final String REFUND_RELEASE_SCHEDULER = "Refund Release Scheduler";

        public static final String NEX_GE_PROVISION_SCHEDULER = "NexGe Provision Scheduler";

        public static final String CHANGE_PLAN_SCHEDULER = "Change Plan Scheduler";

        public static final String REVENUE_DAY_MID_NIGHT_SCHEDULER = "RevMid Night Scheduler";

        public static final String EVERY_MONTH_FIRST_DAY_SCHEDULER = "Every Month First Day Scheduler";

        public static final String PARTNER_INVOICE_SCHEDULER = "Partner Invoice Scheduler";

        public static final String PARTNER_INVOICE_ADJUSTMENT_SCHEDULER = "Partner Invoice Adjustment Scheduler";

        public static final String UPDATE_CUSTOMER_AND_SERVICE_SCHEDULER = "Update Customer And Service Scheduler";

        public static final String QUOTA_RESET_DAY_MIDNIGHT_SCHEDULER = "Quota Reset MidNight Scheduler";

        public static final String ISP_AUTO_BILL_SCHEDULER = "Isp Auto Bill Scheduler";

        public static final String TIME_FOR_DUNNING_SCHEDULER = "Time For Dunning Scheduler";

        public static final String TIME_FOR_PARTNER_COUNT_SCHEDULER = "Time For Partner Count Scheduler";

        public static final String TIME_FOR_AUTOMATE_PAYMENT_SCHEDULER = "Automate Payment Scheduler";

        public static final String TIME_FOR_REASSIGN_TICKET_SCHEDULER = "Reassign Ticket Scheduler";

        public static final String TIME_FOR_TAT_MATRIX_SCHEDULER = "Tat Matrix Scheduler";

        public static final String TIME_FOR_WARRANTY_DAYS_SCHEDULER = "Warranty days Scheduler";

        public static final String TIME_FOR_REMINDER_CAF_FOLLOWUP_SCHEDULER = "Reminder Caf-Followup Scheduler";

        public static final String TIME_FOR_OVER_DUE_CAF_FOLLOWUP_SCHEDULER = "Over-due CAF FollowUp Scheduler";

        public static final String TIME_FOR_REMINDER_TICKET_FOLLOWUP_SCHEDULER = "Ticket Followup Scheduler"; //NO-Use

        public static final String TIME_FOR_ACTIVATE_SERVICE_IN_EZ_BILL_SCHEDULER = "Activate Service In EZ-Bill Scheduler";

        public static final String TIME_FOR_TAT_OVER_DUE_MATRIX_SCHEDULER = "Tat Over-due Matrix Scheduler";

        public static final String TIME_FOR_SERVICE_HOLD_SCHEDULER = "Service Hold Scheduler";

        public static final String TIME_FOR_PLAN_EXPIRY_NOTIFICATION_SCHEDULER = "Expiry Plan Notification Scheduler";

        public static final String TIME_FOR_PLAN_CONNECTION_NUMBER_GENERATE_SCHEDULER = "Connection Number Generate Scheduler";
        public static final String SERVICE_HOLD_SERVICE_SCHEDULER = "Service Hold Scheduler";
        public static final String SERVICE_RESUME_SERVICE_SCHEDULER = "Service Resume Scheduler";

        public static final String CAF_CLOSED_SCHEDULER = "Caf Closed Scheduler";
    }

}
