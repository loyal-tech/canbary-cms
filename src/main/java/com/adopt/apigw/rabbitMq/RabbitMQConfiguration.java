package com.adopt.apigw.rabbitMq;
//
//import com.adopt.apigw.MicroSeviceDataShare.SharedDataConstants.SharedDataConstants;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//
@Configuration
public class RabbitMQConfiguration
{
//    /*
//    @Bean
//    Queue deadLetterQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.DEAD_LETTER_QUEUE).build();
//    }
//
//    @Bean
//    DirectExchange deadLetterExchange() {
//        return new DirectExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE);
//    }
//
//    @Bean
//    Binding DLQbinding() {
//        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(RabbitMqConstants.DEAD_LETTER_KEY);
//    }
//
//    @Bean
//    public MessageConverter jsonMessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
//        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(jsonMessageConverter());
//        return rabbitTemplate;
//    }
//
//
//    @Bean
//    public Queue bssCustomerApprovalSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Queue bssCustomerApprovalFailQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_FAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerRegistrationSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_REGISTRATION_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerRegistrationFailQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_REGISTRATION_FAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerRenewalSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_RENEWAL_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerRenewalFailQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_RENEWAL_FAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//
//    @Bean
//    public Queue bssCustomerRechargeSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_RECHARGE_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerRechargeFailQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_RECHARGE_FAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerPlanExpireQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PLAN_EXPIRE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerPaymentLinkQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_LINK)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerPaymentSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssAssignTicketToteam() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Queue staffCreatedFrombss() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding bssstaffCreateSuccessBinding() {
//        return BindingBuilder.bind(staffCreatedFrombss()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public DirectExchange adoptExchange() {
//        return new DirectExchange(RabbitMqConstants.ADOPT_EXCHANGE);
//    }
//
//    @Bean
//    public Binding bssCustomerApprovalSuccessBinding() {
//        return BindingBuilder.bind(bssCustomerApprovalSuccessQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerApprovalFailBinding() {
//        return BindingBuilder.bind(bssCustomerApprovalFailQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerRegistrationSuccessBinding() {
//        return BindingBuilder.bind(bssCustomerRegistrationSuccessQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerRegistrationFaiBinding() {
//        return BindingBuilder.bind(bssCustomerRegistrationFailQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerRenewalSuccessBinding() {
//        return BindingBuilder.bind(bssCustomerRenewalSuccessQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerRenewalFailBinding() {
//        return BindingBuilder.bind(bssCustomerRenewalFailQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Binding bssCustomerRechargeSuccessBinding() {
//        return BindingBuilder.bind(bssCustomerRechargeSuccessQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerRechargeFailBinding() {
//        return BindingBuilder.bind(bssCustomerRechargeFailQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerPlanExpireBinding() {
//        return BindingBuilder.bind(bssCustomerPlanExpireQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerPaymentLinkBinding() {
//        return BindingBuilder.bind(bssCustomerPaymentLinkQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerPaymentSuccessBinding() {
//        return BindingBuilder.bind(bssCustomerPaymentSuccessQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding createCustomerBinding() {
//        return BindingBuilder.bind(createCustomerQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createCustomerQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendApiGWCustStatusUpdateBinding() {
//        return BindingBuilder.bind(sendApiGWCustStatusUpdate()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendApiGWCustStatusUpdate() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_STATUS_UPDATE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue custPackageRelQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_PACKAGE_REL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue serviceArea() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding serviceareabinding() {
//        return BindingBuilder.bind(serviceArea()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding custPackageRelBinding() {
//        return BindingBuilder.bind(custPackageRelQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue custMacMappingQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding custMacMappingBinding() {
//        return BindingBuilder.bind(custMacMappingQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue postpaidPlanQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_POSTPAIDPLAN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding postpaidPlanBinding() {
//        return BindingBuilder.bind(postpaidPlanQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue qosPolicyQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_QOS_POLICY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding qosPolicyBinding() {
//        return BindingBuilder.bind(qosPolicyQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerTimeBasePolicyDetailsQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_TIME_BASE_POLICY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerTimeBasePolicyDetailsBinding() {
//        return BindingBuilder.bind(customerTimeBasePolicyDetailsQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue custReplyQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUST_REPLY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding custReplyBinding() {
//        return BindingBuilder.bind(custReplyQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssAssignTicketSuccessBinding() {
//        return BindingBuilder.bind(bssAssignTicketToteam()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue updateQuotaQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_UPDATE_QUOTA)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding updateQuotaBinding() {
//        return BindingBuilder.bind(updateQuotaQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue bssCustomerDunning() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_DUNNING)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding bssCustomerDunningBinding() {
//        return BindingBuilder.bind(bssCustomerDunning()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue bssCustomerDeactivation() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_DUNNING)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding bssCustomerDeactivations() {
//        return BindingBuilder.bind(bssCustomerDeactivation()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue AddMacFromRadiusQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_RADIUS_CUST_MAC_ADD).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding AddMacFromRadiusBinding() {
//        return BindingBuilder.bind(AddMacFromRadiusQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue optGenerationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_GENERATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding optGenerationBinding() {
//        return BindingBuilder.bind(optGenerationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue updateCustomerQuotaQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_UPDATE_CUSTOMER_QUOTA)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding updateCustomerQuotaBinding() {
//        return BindingBuilder.bind(updateCustomerQuotaQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerBillingInvoiceQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BILLING_INVOICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerBillingInvoiceBinding() {
//        return BindingBuilder.bind(customerBillingInvoiceQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerRegistrationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_OTP_REGISTRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerregistrationQueue() {
//        return BindingBuilder.bind(customerRegistrationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue staffExpiredDocumentQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding staffExpireddocumentQueue() {
//        return BindingBuilder.bind(staffExpiredDocumentQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue roleQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_ROLE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding roleBindings() {
//        return BindingBuilder.bind(roleQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue userQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_USER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding userBindings() {
//        return BindingBuilder.bind(userQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue staffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_SAVE_USER_SEND)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding staffBindingsave() {
//        return BindingBuilder.bind(staffQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue staffQueuedelete() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFFUSER_SEND_DELETE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding staffBindingsdelete() {
//        return BindingBuilder.bind(staffQueuedelete()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue saveAccesslevelGrpQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_ALG_SAVE_SEND)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding saveAccesslevelGrpQueueBinding() {
//        return BindingBuilder.bind(saveAccesslevelGrpQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue updateAccesslevelGrpQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_ALG_UPDATE_SEND)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding updateAccesslevelGrpQueueBinding() {
//        return BindingBuilder.bind(updateAccesslevelGrpQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue deleteAccesslevelGrpQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_ALG_DELETE_SEND)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding deleteAccesslevelGrpQueueBinding() {
//        return BindingBuilder.bind(deleteAccesslevelGrpQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue businessUnitQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding businessUnitBindings() {
//        return BindingBuilder.bind(businessUnitQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue updateCustomerPasswordQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_UPDATE_CUSTOMER_PASSWORD)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding updateCustomerPasswordBinding() {
//        return BindingBuilder.bind(updateCustomerPasswordQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
////    @Bean
////    public Queue staffForLeadQueue() {
////        return QueueBuilder.durable(RabbitMqConstants.QUEUE_FIND_STAFF_FOR_LEAD)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////    }
////
////    @Bean
////    public Binding staffForLeadsBindings() {
////        return BindingBuilder.bind(staffForLeadQueue()).to(adoptExchange()).withQueueName();
////    }
//
//
//    @Bean
//    public Queue leadInitDataQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_MGMT_INIT_DATA)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadInitDataBindings() {
//        return BindingBuilder.bind(leadInitDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadApproverDataQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadApproverDataBindings() {
//        return BindingBuilder.bind(leadApproverDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
////    @Bean
////    public Queue leadApproverUpdateDataQueue() {
////        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_APPROVER_UPDATE_DETAIL)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////    }
////
////    @Bean
////    public Binding leadApproverUpdateDataBindings() {
////        return BindingBuilder.bind(leadApproverUpdateDataQueue()).to(adoptExchange()).withQueueName();
////    }
//
//    @Bean
//    public Queue leadApproverUpdateResponseQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_APPROVER_UPDATE_DETAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadApproverUpdateResponseBindings() {
//        return BindingBuilder.bind(leadApproverUpdateResponseQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue leadUpdateLeadInfoDataQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_LEAD_INFO)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadSendUpdateLeadInfoBindings() {
//        return BindingBuilder.bind(leadUpdateLeadInfoDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue leadStatusReqDataQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_LEAD_STATUS_INFO)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadStatusReqDataBindings() {
//        return BindingBuilder.bind(leadStatusReqDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue leadStatusDtoQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_LEAD_STATUS_DTO)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadStatusDtoBindings() {
//        return BindingBuilder.bind(leadStatusDtoQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue bssNotificationToStaff() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_NOTIFICATION_TAT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding bssNotificationToStaffBinding() {
//        return BindingBuilder.bind(bssNotificationToStaff()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue timeBasePolicyQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CREATE_TIME_BASE_POLICY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding timeBasePolicyBinding() {
//        return BindingBuilder.bind(timeBasePolicyQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue timeBasePolicyDetailQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CREATE_TIME_BASE_POLICY_DETAILS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding timeBasePolicyDetailsBinding() {
//        return BindingBuilder.bind(timeBasePolicyDetailQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerCafQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_CAF_POJO)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerCafBindings() {
//        return BindingBuilder.bind(sendCustomerCafQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBranchQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendBranchBindings() {
//        return BindingBuilder.bind(sendBranchQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPartnerQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendParnerBindings() {
//        return BindingBuilder.bind(sendPartnerQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendServiceAreaQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaBindings() {
//        return BindingBuilder.bind(sendServiceAreaQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_CUSTOMER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerBindings() {
//        return BindingBuilder.bind(sendCustomerQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendStaffStatusQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_SEND_STATUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendStaffStatusBindings() {
//        return BindingBuilder.bind(sendStaffStatusQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue prepaidCustomerInvoiceCreation() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding prepaidCustomerInvoiceCreationBinding() {
//        return BindingBuilder.bind(prepaidCustomerInvoiceCreation()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue postpaidCustomerInvoiceCreation() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_CREATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding postpaidCustomerInvoiceCreationBinding() {
//        return BindingBuilder.bind(postpaidCustomerInvoiceCreation()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue postpaidCustomerInvoiceDirectCharge() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding postpaidCustomerInvoiceDirectChargeBinding() {
//        return BindingBuilder.bind(postpaidCustomerInvoiceDirectCharge()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue prepaidCustomerInvoiceDirectCharge() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding prepaidCustomerInvoiceDirectChargeBinding() {
//        return BindingBuilder.bind(prepaidCustomerInvoiceDirectCharge()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue customerInvoiceInventoryCharge() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_INVOICE_INVENTORY_CHARGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding customerInvoiceInventoryChargeBinding() {
//        return BindingBuilder.bind(customerInvoiceInventoryCharge()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendLeadDocConvertQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_DOC_CONVERT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendLeadDocConvertBindings() {
//        return BindingBuilder.bind(sendLeadDocConvertQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue clientServiceQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CLIENT_SERVICE_UPDATE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding clientServiceBinding() {
//        return BindingBuilder.bind(clientServiceQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCOADMForRadiusCustomer() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_RADIUS_COA_DM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCOADMForRadiusCustomerBinding() {
//        return BindingBuilder.bind(sendCOADMForRadiusCustomer()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateRadiusCustomer() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_RADIUS_CUSTOMER_UPDATE_STATUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendUpdateRadiusCustomerBinding() {
//        return BindingBuilder.bind(sendUpdateRadiusCustomer()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendWorkflowActionAssignMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendWorkflowActionAssignMessageBinding() {
//        return BindingBuilder.bind(sendWorkflowActionAssignMessage()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMvnoQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_MVNO)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoBindings() {
//        return BindingBuilder.bind(sendMvnoQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendChangeCustomerStatus() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_CHANGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChangeCustomerStatusBinding() {
//        return BindingBuilder.bind(sendChangeCustomerStatus()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendLeadMasterQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_MASTER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendLeadMasterBindings() {
//        return BindingBuilder.bind(sendLeadMasterQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendServiceStatusQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SERVICE_START_STOP)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendServiceStatusBindings() {
//        return BindingBuilder.bind(sendServiceStatusQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendTatParentToTeamQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTatParentToTeamBindings() {
//        return BindingBuilder.bind(sendTatParentToTeamQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue partnerInvoiceCreation() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_INVOICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding partnerInvoiceCreationBinding() {
//        return BindingBuilder.bind(partnerInvoiceCreation()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendFollowupRemarkMsgQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_FOLLOWUP_REMARK_MSG)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendFollowupRemarkMsgBindings() {
//        return BindingBuilder.bind(sendFollowupRemarkMsgQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendProblemDomainChangeMsgQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendProblemDomainChangeMsgBindings() {
//        return BindingBuilder.bind(sendProblemDomainChangeMsgQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendNasupdateQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_NASUPDATE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendNasupdateBindings() {
//        return BindingBuilder.bind(sendNasupdateQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPopManagementQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_POP_MANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPopManagementBindings() {
//        return BindingBuilder.bind(sendPopManagementQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendTicketETRQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ETR)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTicketETRBindings() {
//        return BindingBuilder.bind(sendTicketETRQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendTeamsQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_TEAMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTeamsBindings() {
//        return BindingBuilder.bind(sendTeamsQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendTicketETRAuditQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTicketETRAuditBindings() {
//        return BindingBuilder.bind(sendTicketETRAuditQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerFollowUpReminderForStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerFollowUpReminderForStaffBindings() {
//        return BindingBuilder.bind(customerFollowUpReminderForStaffQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerFollowUpReminderForCustomerQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerFollowUpReminderForCustomerBindings() {
//        return BindingBuilder.bind(customerFollowUpReminderForCustomerQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerFollowUpOverDueForStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerFollowUpOverDueForStaffBindings() {
//        return BindingBuilder.bind(customerFollowUpOverDueForStaffQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerFollowUpOverDueForParentStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerFollowUpOverDueForParentStaffBindings() {
//        return BindingBuilder.bind(customerFollowUpOverDueForParentStaffQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue teamCreatedFrombss() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TEAM_SEND_TASK_MGMT_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding bssteamCreateSuccessBinding() {
//        return BindingBuilder.bind(teamCreatedFrombss()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue businessunitCreatedFrombss() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_SEND_TASK_MGMT_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding bssbusinessunitCreateSuccessBinding() {
//        return BindingBuilder.bind(businessunitCreatedFrombss()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustDocETRAuditQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_EMAIL_DOC_AUDIT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustDocETRAuditBinding() {
//        return BindingBuilder.bind(sendCustDocETRAuditQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue troubleTicketFollowUpReminderForStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding troubleTicketFollowUpReminderForStaffBindings() {
//        return BindingBuilder.bind(troubleTicketFollowUpReminderForStaffQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue troubleTicketFollowUpReminderForCustomerQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding troubleTicketFollowUpReminderForCustomerBindings() {
//        return BindingBuilder.bind(troubleTicketFollowUpReminderForCustomerQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue troubleTicketFollowUpOverDueForStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding troubleTicketFollowUpOverDueForStaffBindings() {
//        return BindingBuilder.bind(troubleTicketFollowUpOverDueForStaffQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue troubleTicketFollowUpOverDueForParentStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding troubleTicktFollowUpOverDueForParentStaffBindings() {
//        return BindingBuilder.bind(troubleTicketFollowUpOverDueForParentStaffQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue billGenDataCreateFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHARGE_MGMTN_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding billGenDataCreateFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(billGenDataCreateFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue chargeMAnagementFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHARGE_MGMTN_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding chargeMAnagementFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(chargeMAnagementFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue planServiceMAnagementFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_SERVICE_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding planServiceMAnagementFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(planServiceMAnagementFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
////    @Bean
////    public Queue customersMAnagementFromAPIGW() {
////        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_SUCCESS)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding customersMAnagementFromAPIGWSuccessBinding() {
////        return BindingBuilder.bind(customersMAnagementFromAPIGW()).to(adoptExchange()).withQueueName();
////    }
//
//    @Bean
//    public Queue taxMAnagementFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAX_MGMTN_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding taxMAnagementFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(taxMAnagementFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue serviceAreaFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding serviceAreaFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(serviceAreaFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue businessUnitFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding businessUnitFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(businessUnitFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue debitDocumentFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_DEBIT_DOCUMENT_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding debitDocumentFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(debitDocumentFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue staffManagementFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_MANAGEMENT_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding staffManagementFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(staffManagementFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue ticketCreationSuccess() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_CREATION_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding ticketCreationSuccessBinding() {
//        return BindingBuilder.bind(ticketCreationSuccess()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue LeadAssignMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_CREATION_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding LeadAssignMessageBinding() {
//        return BindingBuilder.bind(LeadAssignMessage()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue branchMessageFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BRANCH_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding branchMessageFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(branchMessageFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerMessageFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding customerMessageFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(customerMessageFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue ticketRescheduleforStaff() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding ticketRescheduleforStaffBinding() {
//        return BindingBuilder.bind(ticketRescheduleforStaff()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue creditNoteGenMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INTEGRATION_SYSTEM_CREDIT_NOTE_GEN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding creditNoteGenMessageBinding() {
//        return BindingBuilder.bind(ticketRescheduleforStaff()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue  ticketTatBreachedReminder() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding ticketTatBreachedReminderBinding() {
//        return BindingBuilder.bind(ticketTatBreachedReminder()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createUpdatePlanPricesQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.UPDATE_PLAN_PRICES_IN_CRM).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createUpdatePlanPricesBinding() {
//        return BindingBuilder.bind(createUpdatePlanPricesQueue()).to(adoptExchange()).withQueueName();
//    }
//
////    @Bean
////    public Queue  ticketTatBreachedOverDueReminder() {
////        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_OVERDUE_TAT_BREACHED_REMINDER)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////    @Bean
////    public Binding ticketTatBreachedOverDueReminderBinding() {
////        return BindingBuilder.bind(ticketTatBreachedOverDueReminder()).to(adoptExchange()).withQueueName();
////    }
//
//    /** Rabbitmq Binding for voucher queue**/
//
//    @Bean
//    public Queue CreateVoucherCodeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_VOUCHERCODE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateVoucherCodeBinding() {
//        return BindingBuilder.bind(CreateVoucherCodeQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /** Rabbitmq Binding for advance notification queue**/
//
//    @Bean
//    public Queue CreateDunningAdvanceNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_DUNNING_ADVANCE_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateAdvanceNotificationQueueBinding() {
//        return BindingBuilder.bind(CreateDunningAdvanceNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**Rabbitmq binding for advance notification ended**/
////    @Bean
////    public Queue createSendSerialNumberQueue() {
////        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_SERIAL_NUMBER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////    }
////
////    @Bean
////    public Binding createSendSerialNumberQueueBinding() {
////        return BindingBuilder.bind(createSendSerialNumberQueue()).to(adoptExchange()).withQueueName();
////    }
//
//    @Bean
//    public Queue sendCustomerkpiQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_CUSTOMER_KPI)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding sendCustomerkpiBindings() {
//        return BindingBuilder.bind(sendCustomerkpiQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBusinessunitKpiQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_KPI)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBusinessunitQueueBindings() {
//        return BindingBuilder.bind(sendBusinessunitKpiQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendServiceAreaKpi() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS_KPI)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding sendServiceAreaKpibinding() {
//        return BindingBuilder.bind(sendServiceAreaKpi()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBranchKpiQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_BRANCH_KPI)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding sendBranchKpiBindings() {
//        return BindingBuilder.bind(sendBranchKpiQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue custPackageRelKpiQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_PACKAGE_REL_KPI)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding custPackageRelKpiBinding() {
//        return BindingBuilder.bind(custPackageRelKpiQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue planServiceKpiQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_SERVICE_KPI)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding planServiceKpiQueueBinding() {
//        return BindingBuilder.bind(planServiceKpiQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue CancelRegenerateInvoice() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CANCEL_REGENERATE_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CancelRegenerateInvoiceBinding() {
//        return BindingBuilder.bind(CancelRegenerateInvoice()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue LeadCafConvertion() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_CAF_CONVERTION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding LeadCafConvertionBinding() {
//        return BindingBuilder.bind(LeadCafConvertion()).to(adoptExchange()).withQueueName();
//    }
//    /** Rabbitmq Binding for  partner document expired queue**/
//
//    @Bean
//    public Queue CreateDunningDocumentExpiredQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateDunningDocumentExpiredQueueBinding() {
//        return BindingBuilder.bind(CreateDunningDocumentExpiredQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**Rabbitmq binding for partner document dunning ended**/
//
//    /** Rabbitmq Binding for  partner document expired deactivation queue**/
//
//    @Bean
//    public Queue CreateDunningDocumentExpiredDeactivationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateDunningDocumentExpiredDeactivationQueueBinding() {
//        return BindingBuilder.bind(CreateDunningDocumentExpiredDeactivationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**Rabbitmq binding for partner document dunning ended**/
//
//    /** Rabbitmq Binding for  partner document expired deactivation to staff queue**/
//
//    @Bean
//    public Queue CreateDunningDocumentExpiredDeactivationStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateDunningDocumentExpiredDeactivationStaffQueueBinding() {
//        return BindingBuilder.bind(CreateDunningDocumentExpiredDeactivationStaffQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**Rabbitmq binding for partner document dunning to staff ended**/
//
//    //message for customer status inactive
//    @Bean
//    public Queue CustStatusInActive() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_STATUS_INACTIVATE_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustStatusInActiveBinding() {
//        return BindingBuilder.bind(CustStatusInActive()).to(adoptExchange()).withQueueName();
//    }
//
//    //message for customer service active
//    @Bean
//    public Queue CustServiceActive() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_ACTIVE_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustServiceActiveBinding() {
//        return BindingBuilder.bind(CustServiceActive()).to(adoptExchange()).withQueueName();
//    }
//   /* message for customer service inactive*/
//   @Bean
//    public Queue CustServiceInActive() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_INACTIVE_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustServiceInActiveBinding() {
//        return BindingBuilder.bind(CustServiceInActive()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue CustChangePassword() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_CHANGE_PASSWORD_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustChangePasswordBinding() {
//        return BindingBuilder.bind(CustChangePassword()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadQuotationDetailsForWorkflowMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_QUOTATION_WF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding leadQuotationDetailsForWorkflowBinding() {
//        return BindingBuilder.bind(leadQuotationDetailsForWorkflowMessage()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendApproverDetailsForQuotationMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL_QUOTATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendApproverDetailsForQuotationBinding() {
//        return BindingBuilder.bind(sendApproverDetailsForQuotationMessage()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendLeadQuotationAssigneMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_QUOTATION_ASSIGN_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendLeadQuotationAssigneBinding() {
//        return BindingBuilder.bind(sendLeadQuotationAssigneMessage()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue CreateDunningDocumentExpiredQueueForCustomer() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateDunningDocumentExpiredQueueForCustomerBinding() {
//        return BindingBuilder.bind(CreateDunningDocumentExpiredQueueForCustomer()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue custOpenAddressShifting() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding custOpenAddressShiftingBinding() {
//        return BindingBuilder.bind(custOpenAddressShifting()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue custCloseAddressShifting() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding custCloseAddressShiftingBinding() {
//        return BindingBuilder.bind(custCloseAddressShifting()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue custPaymentVerification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_PAYMENT_VERIFICATION_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding custPaymentVerificationBinding() {
//        return BindingBuilder.bind(custPaymentVerification()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue custTicketClose() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding custTicketCloseBinding() {
//        return BindingBuilder.bind(custTicketClose()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerPlanMappingSendInIntegration() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_PLAN_MAPPING_FOR_INTEGRATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerPlanMappingSendInIntegrationBinding() {
//        return BindingBuilder.bind(customerPlanMappingSendInIntegration()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue approveRemoveInventoryserializedItemSendInIntegration() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_APPROVE_REMOVE_INVENTORY_SERIALIZEDITEM_REQUEST_IN_INTEGRATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding removeInventoryserializedItemSendInIntegrationBinding() {
//        return BindingBuilder.bind(approveRemoveInventoryserializedItemSendInIntegration()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//    @Bean
//    public Queue approveInventoryserializedItemSendInIntegration(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_APPROVE_SERIALIZEDITEM_FOR_INTEGRATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding serializedItemSendInIntegrationBinding(){
//        return BindingBuilder.bind(approveInventoryserializedItemSendInIntegration()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerServiceMappingSendInIntegration() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_MAPPING_FOR_INTEGRATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerServiceMappingSendInIntegrationBinding() {
//        return BindingBuilder.bind(customerServiceMappingSendInIntegration()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue ServiceSendInIntegration() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_FOR_INTEGRATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding ServiceSendInIntegrationBinding() {
//        return BindingBuilder.bind(ServiceSendInIntegration()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue CreateCustomerInventoryQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateCustomerInventoryQueueBinding() {
//        return BindingBuilder.bind(CreateDunningDocumentExpiredQueueForCustomer()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue CreateInventoryItemQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateInventoryItemQueueBinding() {
//        return BindingBuilder.bind(CreateDunningDocumentExpiredQueueForCustomer()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue CreatePostpaidPlanQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_POSTPAIDPLAN_FOR_INTEGRATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding CreatePostpaidPlanQueueBinding() {
//        return BindingBuilder.bind(CreatePostpaidPlanQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue custplanMappingUpdateFromAPIGW() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUST_PLAN_MAPPING_UPDATE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding custplanMappingUpdateFromAPIGWSuccessBinding() {
//        return BindingBuilder.bind(custplanMappingUpdateFromAPIGW()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue custsentToNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding custsentToNotificationQueueBinding() {
//        return BindingBuilder.bind(custsentToNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue mapLeadWithMilestones(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_LEAD_MILESTONES_MAPPING)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding mapLeadWithMilestonesQueueBinding() {
//        return BindingBuilder.bind(mapLeadWithMilestones()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadCreationNotification(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_CREATION_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding leadCreationNotificationBinding() {
//        return BindingBuilder.bind(leadCreationNotification()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue custServiceChargeIpDtlsQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CREATE_CUST_SERVICE_CHARGE_IP_DTLS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding custServiceChargeIpDtlsQueueBinding() {
//        return BindingBuilder.bind(custServiceChargeIpDtlsQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue updateCustServiceChargeIpDtlsQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_UPDATE_CUST_SERVICE_CHARGE_IP_DTLS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding updateCustServiceChargeIpDtlsQueueBinding() {
//        return BindingBuilder.bind(updateCustServiceChargeIpDtlsQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue ticketmessageIntegrationSendInQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_TICKET_MESSAGE_INTEGRATION_SYSTEM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding ticketmessageIntegrationSendInQueueBinding() {
//        return BindingBuilder.bind(ticketmessageIntegrationSendInQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue makeRequestStaffFromGatewayReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_REQUEST_GATEWAY_FOR_STAFFUSER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding makeRequestStaffFromGatewayReceiveQueueBinding() {
//        return BindingBuilder.bind(makeRequestStaffFromGatewayReceiveQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue makeResponseStaffFromGatewayReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_RESPONSE_GATEWAY_FOR_STAFFUSER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding makeResponseStaffFromGatewayReceiveQueueBinding() {
//        return BindingBuilder.bind(makeResponseStaffFromGatewayReceiveQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue saveStaffFromGatewayReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_RESPONSE_TO_SAVE_STAFFUSER_FROM_GATEWAY).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding saveStaffFromGatewayReceiveQueueBinding() {
//        return BindingBuilder.bind(saveStaffFromGatewayReceiveQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createCountryQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_COUNTRY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding createCountryBinding() {
//        return BindingBuilder.bind(createCountryQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createStateQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_STATE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createStateBinding() {
//        return BindingBuilder.bind(createStateQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createCityQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CITY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createCityBinding() {
//        return BindingBuilder.bind(createCityQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPlanGroupQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_SALESCRM).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupQueueBinding() {
//        return BindingBuilder.bind(sendPlanGroupQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createPincodeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PINCODE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createPincodeBinding() {
//        return BindingBuilder.bind(createPincodeQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createAreaQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_AREA)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createAreaBinding() {
//        return BindingBuilder.bind(createAreaQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendTicketTATAuditQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTicketTATAuditBindings() {
//        return BindingBuilder.bind(sendTicketTATAuditQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendTicketTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTicketTATMessageBindings() {
//        return BindingBuilder.bind(sendTicketTATMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue selfCareCreateTicketIntegrationInQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INTEGRATION_CREATE_SELFCARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding selfCareCreateTicketIntegrationInQueueBinding() {
//        return BindingBuilder.bind(selfCareCreateTicketIntegrationInQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue productFromRms(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PRODUCT_FROM_RMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding productFromRmsBinding() {
//        return BindingBuilder.bind(productFromRms()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue productCategoryToIntegration(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PRODUCTCATEGORY_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding productCategoryToIntegrationBinding() {
//        return BindingBuilder.bind(productCategoryToIntegration()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue wareHouseToIntegration(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_WAREHOUSE_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding wareHouseToIntegrationBinding() {
//        return BindingBuilder.bind(wareHouseToIntegration()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue inwardToIntegration(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INWARD_RMS_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding inwardToIntegrationBinding() {
//        return BindingBuilder.bind(inwardToIntegration()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue serializedItemFromRms(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERIALIZED_ITEM_FROM_RMS_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding serializedItemFromRmsBinding() {
//        return BindingBuilder.bind(serializedItemFromRms()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue serializedItemHistoryFromRms(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERIALIZED_ITEM_HISTORY_RMS_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding serializedItemHistoryFromRmsBinding() {
//        return BindingBuilder.bind(serializedItemHistoryFromRms()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue inwardSendToIntegration(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_INWARD_TO_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding inwardSendToIntegrationBinding() {
//        return BindingBuilder.bind(inwardSendToIntegration()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCountrySharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCountrySharedDataBinding() {
//        return BindingBuilder.bind(sendCountrySharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCitySharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCitySharedDataBinding() {
//        return BindingBuilder.bind(sendCitySharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//
//    @Bean
//    public Queue sendStateSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendStateSharedDataBinding() {
//        return BindingBuilder.bind(sendStateSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//
//
//    @Bean
//    public Queue sendCountryUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCountryUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendCountryUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCityUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCityUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendCityUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendStateUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendStateUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendStateUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
////    Share Data to Inventory Microservice
////    Create Country APIGW to Inventory Microservice
//    @Bean
//    public Queue sendCountrySharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCountrySharedDataBindingInventory() {
//        return BindingBuilder.bind(sendCountrySharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create City APIGW to Inventory Microservice
//    @Bean
//    public Queue sendCitySharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCitySharedDataBindingInventory() {
//        return BindingBuilder.bind(sendCitySharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create State APIGW to Inventory Microservice
//    @Bean
//    public Queue sendStateSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendStateSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendStateSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    // Create MVNO APIGW to Inventory Microservice
//    @Bean
//    public Queue sendMVNOSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendMVNOSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendMVNOSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    // Create Role APIGW to Inventory Microservice
//    @Bean
//    public Queue sendRoleSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendRoleSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    // Create Staff APIGW to Inventory Microservice
//    @Bean
//    public Queue sendStaffUserSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendStaffUserSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendStaffUserSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Country APIGW to Inventory Microservice
//    @Bean
//    public Queue sendCountryUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCountryUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendCountryUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//    //    Update City APIGW to Inventory Microservice
//    @Bean
//    public Queue sendCityUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCityUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendCityUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
////    Update State APIGW to Inventory Microservice
//    @Bean
//    public Queue sendStateUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendStateUpdatedSharedDataBindingInventory() {
//       return BindingBuilder.bind(sendStateUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update MVNO APIGW to Inventory Microservice
//    @Bean
//    public Queue sendMVNOUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendMVNOUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendMVNOUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Role APIGW to Inventory Microservice
//    @Bean
//    public Queue sendRoleUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendRoleUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Staffuser APIGW to Inventory Microservice
//    @Bean
//    public Queue sendStaffUserUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendStaffUserUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendStaffUserUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //
//
//
//    @Bean
//    public Queue sendPincodeSaveSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPincodeSaveSharedDataBinding() {
//        return BindingBuilder.bind(sendPincodeSaveSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPincodeUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPincodeUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//    @Bean
//    public Queue sendAreaSaveSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendAreaSaveSharedDataBinding() {
//        return BindingBuilder.bind(sendAreaSaveSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendAreaUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendAreaUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendAreaUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //ServiceArea
//
//    @Bean
//    public Queue sendServiceAreaSaveSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaSaveSharedDataBinding() {
//        return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendServiceAreaUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /* Business Unit */
//
//    @Bean
//    public Queue sendBusinessUnitSaveSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessUnitSaveSharedDataBinding() {
//        return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendBusinessUnitUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessUnitUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBranchSaveSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBranchSaveSharedDataBinding() {
//        return BindingBuilder.bind(sendBranchSaveSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBranchUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBranchUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendBranchUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Pincode APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPincodeSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPincodeSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendPincodeSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Pincode APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPincodeUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPincodeUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //    Create Area APIGW to Inventory Microservice
//    @Bean
//    public Queue sendAreaSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendAreaSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendAreaSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Area APIGW to Inventory Microservice
//    @Bean
//    public Queue sendAreaUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendAreaUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendAreaUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //    Create Service Area APIGW to Inventory Microservice
//
//    @Bean
//    public Queue sendServiceAreaSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Service Area APIGW to Inventory Microservice
//    @Bean
//    public Queue sendServiceAreaUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Business Unit APIGW to Inventory Microservice
//
//    @Bean
//    public Queue sendBusinessUnitSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    //Teams
//
//    @Bean
//    public Queue sendTeamsSaveSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTeamsSaveSharedDataBinding() {
//        return BindingBuilder.bind(sendTeamsSaveSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendTeamsUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTeamsUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendTeamsUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //hierarchy
//
//    @Bean
//    public Queue sendHierarchySaveSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_HIERARCHY_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendHierarchySaveSharedDataBinding() {
//        return BindingBuilder.bind(sendHierarchySaveSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendHierarchyUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendHierarchyUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendHierarchyUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendHierarchySaveSharedDataCommonAPIQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TEAM_HIERARCHY_CREATE_DATA_SHARE_COMMONAPIGW)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendHierarchySaveSharedDataCommonAPIBinding() {
//        return BindingBuilder.bind(sendHierarchySaveSharedDataCommonAPIQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendHierarchyUpdatedSharedDataCommonAPIQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TEAM_HIERARCHY_UPDATE_DATA_SHARE_COMMONAPIGW)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendHierarchyUpdatedSharedDataCommonAPIBinding() {
//        return BindingBuilder.bind(sendHierarchyUpdatedSharedDataCommonAPIQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //Region
//
//    @Bean
//    public Queue sendRegionSaveSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendRegionSaveSharedDataBinding() {
//        return BindingBuilder.bind(sendRegionSaveSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendRegionUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendRegionUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendRegionUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
////Business Vertical
//
//    @Bean
//    public Queue sendBusinessVerticalsSaveSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESSVERTICALS_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessVerticalsSaveSharedDataBinding() {
//        return BindingBuilder.bind(sendBusinessVerticalsSaveSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBusinessVerticalsUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessVerticalsUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendBusinessVerticalsUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //Customers
//
//    @Bean
//    public Queue sendCustomersSaveSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersSaveSharedDataBinding() {
//        return BindingBuilder.bind(sendCustomersSaveSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomersUpdatedSharedDataQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersUpdatedSharedDataBinding() {
//        return BindingBuilder.bind(sendCustomersUpdatedSharedDataQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //save staff in ticket
//    @Bean
//    public Queue sendStaffUserSaveSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendStaffUserSaveSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendStaffUserSaveSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //update staff in ticket
//    @Bean
//    public Queue sendStaffUserUpdateSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendStaffUserUpdateSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendStaffUserUpdateSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//
//    //save role in ticket
//    @Bean
//    public Queue sendRoleSaveSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleSaveSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendRoleSaveSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //update role in ticket
//    @Bean
//    public Queue sendRoleUpdateSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleUpdateSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendRoleUpdateSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//    //save mvno in ticket
//    @Bean
//    public Queue sendMvnoSaveSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendMvnoSaveSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendMvnoSaveSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //update mvno in ticket
//    @Bean
//    public Queue sendMvnoUpdateSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendMvnoUpdateSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendMvnoUpdateSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Binding sendBusinessUnitSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Business Unit APIGW to Inventory Microservice
//    @Bean
//    public Queue sendBusinessUnitUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessUnitUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Branch APIGW to Inventory Microservice
//    @Bean
//    public Queue sendBranchSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBranchSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendBranchSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Branch APIGW to Inventory Microservice
//    @Bean
//    public Queue sendBranchUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBranchUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendBranchUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Services APIGW to Inventory Microservice
//    @Bean
//    public Queue sendServiceSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendServiceSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Services APIGW to Inventory Microservice
//    @Bean
//    public Queue sendServiceUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendServiceUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//
//
//    //    Create Services APIGW to Ticket Microservice
//    @Bean
//    public Queue sendServiceSaveSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceSaveSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendServiceSaveSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Services APIGW to Ticket Microservice
//    @Bean
//    public Queue sendServiceUpdatedSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceUpdatedSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendServiceUpdatedSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Partner APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPartnerSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendPartnerSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Partner APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPartnerUpdateSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerUpdateSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendPartnerUpdateSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Tax APIGW to Inventory Microservice
//    @Bean
//    public Queue sendTaxSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTaxSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendTaxSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Tax APIGW to Inventory Microservice
//    @Bean
//    public Queue sendTaxUpdateSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTaxUpdateSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendTaxUpdateSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Plan APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPlanSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendPlanSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Plan APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPlanUpdateSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanUpdateSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendPlanUpdateSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Plan Group APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPlanGroupSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendPlanGroupSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Plan Group APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPlanGroupUpdateSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupUpdateSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendPlanGroupUpdateSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Charge APIGW to Inventory Microservice
//    @Bean
//    public Queue sendChargeSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChargeSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendChargeSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Charge APIGW to Inventory Microservice
//    @Bean
//    public Queue sendChargeUpdateSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChargeUpdateSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendChargeUpdateSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Team APIGW to Inventory Microservice
//    @Bean
//    public Queue sendTeamsSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTeamsSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendTeamsSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Team APIGW to Inventory Microservice
//    @Bean
//    public Queue sendTeamsUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTeamsUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendTeamsUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Hierarchy APIGW to Inventory Microservice
//    @Bean
//    public Queue sendHierarchySaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_HIERARCHY_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendHierarchySaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendHierarchySaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Hierarchy APIGW to Inventory Microservice
//    @Bean
//    public Queue sendHierarchyUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendHierarchyUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendHierarchyUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Create Customers APIGW to Inventory Microservice
//    @Bean
//    public Queue sendCustomersSaveSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersSaveSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendCustomersSaveSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Customers APIGW to Inventory Microservice
//    @Bean
//    public Queue sendCustomersUpdatedSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersUpdatedSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendCustomersUpdatedSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //    Create Plan APIGW to Ticket Microservice
//    @Bean
//    public Queue sendPlanSaveSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanSaveSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendPlanSaveSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Plan APIGW to Ticket Microservice
//    @Bean
//    public Queue sendPlanUpdateSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanUpdateSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendPlanUpdateSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//
//    //    Create Partner APIGW to Ticket Microservice
//    @Bean
//    public Queue sendPartnerSaveSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerSaveSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendPartnerSaveSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Partner APIGW to Ticket Microservice
//    @Bean
//    public Queue sendPartnerUpdateSharedDataQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerUpdateSharedDataBindingTicket() {
//        return BindingBuilder.bind(sendPartnerUpdateSharedDataQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//    // Sample Microservice Data Shared
//    @Bean
//    public Queue sendCountrySharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCountrySharedDataBindingSample() {
//        return BindingBuilder.bind(sendCountrySharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCitySharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCitySharedDataBindingSample() {
//        return BindingBuilder.bind(sendCitySharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendStateSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendStateSharedDataBindingSample() {
//        return BindingBuilder.bind(sendStateSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMVNOSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendMVNOSharedDataBindingSample() {
//        return BindingBuilder.bind(sendMVNOSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendRoleSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleSharedDataBindingSample() {
//        return BindingBuilder.bind(sendRoleSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendStaffUserSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendStaffUserSharedDataBindingSample() {
//        return BindingBuilder.bind(sendStaffUserSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCountryUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCountryUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendCountryUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCityUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCityUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendCityUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendStateUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendStateUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendStateUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMVNOUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendMVNOUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendMVNOUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendRoleUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendRoleUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendStaffUserUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendStaffUserUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendStaffUserUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPincodeSaveSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPincodeSaveSharedDataBindingSample() {
//        return BindingBuilder.bind(sendPincodeSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPincodeUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPincodeUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendAreaSaveSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendAreaSaveSharedDataBindingSample() {
//        return BindingBuilder.bind(sendAreaSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendAreaUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendAreaUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendAreaUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendServiceAreaSaveSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaSaveSharedDataBindingSample() {
//        return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendServiceAreaUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBusinessUnitSaveSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBusinessUnitSaveSharedDataBindingSample() {
//        return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBusinessUnitUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessUnitUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBranchSaveSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBranchSaveSharedDataBindingSample() {
//        return BindingBuilder.bind(sendBranchSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBranchUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBranchUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendBranchUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendServiceSaveSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceSaveSharedDataBindingSample() {
//        return BindingBuilder.bind(sendServiceSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendServiceUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendServiceUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPartnerSaveSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerSaveSharedDataBindingSample() {
//        return BindingBuilder.bind(sendPartnerSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//
//
//    //    Update Close Ticket Call from APIGW to Ticket Microservice
//    @Bean
//    public Queue sendTicketDataToAPIGw() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_TICKET_DATA_TO_APIGW)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTicketDataToAPIGwBinding() {
//        return BindingBuilder.bind(sendTicketDataToAPIGw()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Close Ticket Call from APIGW to Ticket Microservice
//    @Bean
//    public Queue sendUpdatedTicketDataToAPIGw() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATED_TICKET_DATA_TO_APIGW)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendUpdatedTicketDataToAPIGwBinding() {
//        return BindingBuilder.bind(sendUpdatedTicketDataToAPIGw()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//
//    @Bean
//    public Queue sendPartnerUpdateSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerUpdateSharedDataBindingSample() {
//        return BindingBuilder.bind(sendPartnerUpdateSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
////    @Bean
////    public Queue sendTaxSaveSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendTaxSaveSharedDataBindingSample() {
////        return BindingBuilder.bind(sendTaxSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
////    @Bean
////    public Queue sendTaxUpdateSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendTaxUpdateSharedDataBindingSample() {
////        return BindingBuilder.bind(sendTaxUpdateSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
////    @Bean
////    public Queue sendPlanSaveSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendPlanSaveSharedDataBindingSample() {
////        return BindingBuilder.bind(sendPlanSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
////    @Bean
////    public Queue sendPlanUpdateSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendPlanUpdateSharedDataBindingSample() {
////        return BindingBuilder.bind(sendPlanUpdateSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
////    @Bean
////    public Queue sendPlanGroupSaveSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendPlanGroupSaveSharedDataBindingSample() {
////        return BindingBuilder.bind(sendPlanGroupSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
////    @Bean
////    public Queue sendPlanGroupUpdateSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendPlanGroupUpdateSharedDataBindingSample() {
////        return BindingBuilder.bind(sendPlanGroupUpdateSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
////    @Bean
////    public Queue sendChargeSaveSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendChargeSaveSharedDataBindingSample() {
////        return BindingBuilder.bind(sendChargeSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
////    @Bean
////    public Queue sendChargeUpdateSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendChargeUpdateSharedDataBindingSample() {
////        return BindingBuilder.bind(sendChargeUpdateSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
//    @Bean
//    public Queue sendTeamsSaveSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTeamsSaveSharedDataBindingSample() {
//        return BindingBuilder.bind(sendTeamsSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendTeamsUpdatedSharedDataQueueSample() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTeamsUpdatedSharedDataBindingSample() {
//        return BindingBuilder.bind(sendTeamsUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
//    }
//
////    @Bean
////    public Queue sendHierarchySaveSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_HIERARCHY_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendHierarchySaveSharedDataBindingSample() {
////        return BindingBuilder.bind(sendHierarchySaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
////    @Bean
////    public Queue sendHierarchyUpdatedSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendHierarchyUpdatedSharedDataBindingSample() {
////        return BindingBuilder.bind(sendHierarchyUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
////    @Bean
////    public Queue sendCustomersSaveSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendCustomersSaveSharedDataBindingSample() {
////        return BindingBuilder.bind(sendCustomersSaveSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
////    @Bean
////    public Queue sendCustomersUpdatedSharedDataQueueSample() {
////        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////
////    @Bean
////    public Binding sendCustomersUpdatedSharedDataBindingSample() {
////        return BindingBuilder.bind(sendCustomersUpdatedSharedDataQueueSample()).to(adoptExchange()).withQueueName();
////    }
//
//
//
//    @Bean
//    public Queue sendClientServDataUpdateShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CLIENT_SERV_UPDATE_DATA_SHARE_TICKET_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendClientServDataUpdateShareQueueBinding() {
//        return BindingBuilder.bind(sendClientServDataUpdateShareQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendTaxCreateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTaxCreateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendTaxCreateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendTaxUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTaxUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendTaxUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendChargeSaveDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendsendChargeSaveDataShareQueueBinding() {
//        return BindingBuilder.bind(sendChargeSaveDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendChargeUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendsendChargeUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendChargeUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPlanSaveDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanSaveDataShareQueueBinding() {
//        return BindingBuilder.bind(sendPlanSaveDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendPlanUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendPlanUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendDiscountSaveDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DISCOUNT_SAVE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                    .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendDiscountSaveDataShareQueueBinding() {
//        return BindingBuilder.bind(sendDiscountSaveDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendDiscountUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DISCOUNT_UPDATE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendDiscountUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendDiscountUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendPlanGroupSaveDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupSaveQueueSaveDataShareQueueBinding() {
//        return BindingBuilder.bind(sendPlanGroupSaveDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendPlanGroupUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendPlanGroupUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomersSaveDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersSaveQueueSaveDataShareQueueBinding() {
//        return BindingBuilder.bind(sendCustomersSaveDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendCustomersUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendCustomersUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendCreditDocument() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCreditDocumentQueueBinding() {
//        return BindingBuilder.bind(sendCreditDocument()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendCustomersDocument() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendsendCustomersDocumentQueueBinding() {
//        return BindingBuilder.bind(sendCustomersDocument()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomersUpdateDocument() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendsendCustomersUpdatetQueueBinding() {
//        return BindingBuilder.bind(sendCustomersDocument()).to(adoptExchange()).withQueueName();
//    }
//
//    // Setting Data Transfer to Common APIGW TO CMS
//    //MVNO
//    @Bean
//    public Queue sendCreateMvnoCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateMvnoCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendCreateMvnoCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateMvnoCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateMvnoCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendUpdateMvnoCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //Role
//    @Bean
//    public Queue sendCreateRoleCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_ROLE_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateRoleCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendCreateRoleCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateRoleCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_ROLE_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateRoleCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendUpdateRoleCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //Staff User
//    @Bean
//    public Queue sendCreateStafUserCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_STAFFUSER_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateStaffUserCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendCreateStafUserCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateStaffUserCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_STAFFUSER_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateStaffUserCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendUpdateStaffUserCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //Teams
//    @Bean
//    public Queue sendCreateTeamsCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateTeamsCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendCreateTeamsCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateTeamsCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateTeamsCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendUpdateTeamsCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //System Configuration
//    @Bean
//    public Queue sendCreateSystemConfigurationCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateSystemConfigurationCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendCreateSystemConfigurationCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateSystemConfigurationCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateSystemConfigurationCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendUpdateSystemConfigurationCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //customer for common apigateway
//
//    @Bean
//    public Queue sendCustomersSaveSharedDataForApiCommonQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersSaveSharedDataForApiCommonBinding() {
//        return BindingBuilder.bind(sendCustomersSaveSharedDataForApiCommonQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    // COMMON APIGW TO APIGW QUEUE CONFIGURATION
//
//
//    //COUNTRY
//    @Bean
//    public Queue sendCountrySaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCountrySaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendCountrySaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendCountryUpdateSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCountryUpdateSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendCountryUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //CITY
//    @Bean
//    public Queue sendCitySaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCitySharedDataBindingCPM() {
//        return BindingBuilder.bind(sendCitySaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCityUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCityUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendCityUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //PINCODE
//    @Bean
//    public Queue sendPincodeSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPincodeSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendPincodeSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPincodeUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPincodeUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //STATE
//    @Bean
//    public Queue sendStateSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendStateSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendStateSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendStateUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendStateUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendStateUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //AREA
//    @Bean
//    public Queue sendAreaSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendAreaSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendAreaSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendAreaUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendAreaUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendAreaUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //SERVICEAREA
//    @Bean
//    public Queue sendServiceAreaSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendServiceAreaUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //BUSINESS UNIT
//    @Bean
//    public Queue sendBusinessUnitSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBusinessUnitSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBusinessUnitUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessUnitUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //BUSINESS VERTICAL
//
//    @Bean
//    public Queue sendBusinessVerticalsSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_VERTICALS_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessVerticalsSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBusinessVerticalsSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBusinessVerticalsUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessVerticalsUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBusinessVerticalsUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //BRANCH
//    @Bean
//    public Queue sendBranchSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBranchSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBranchSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendBranchUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBranchUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBranchUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //INVESTMENTCODE
//    @Bean
//    public Queue sendInvestmentCodeSaveSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendInvestmentCodeSaveSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendInvestmentCodeSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendInvestmentCodeUpdateSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendInvestmentCodeUpdateSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendInvestmentCodeUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //SUB BUSINESS UNIT
//    @Bean
//    public Queue sendSubBusinessUnitSaveSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendSubBusinessUnitSaveSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendSubBusinessUnitSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendSubBusinessUnitUpdateSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendSubBusinessUnitUpdateSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendSubBusinessUnitUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //SUB BUSINESS VERTICALS
//    @Bean
//    public Queue sendSubBusinessVerticalsSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendSubBusinessVerticalsSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendSubBusinessVerticalsSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendSubBusinessVerticalsUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendSubBusinessVerticalsUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendSubBusinessVerticalsUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //DEPARTMENT
//    @Bean
//    public Queue sendDepartmentSaveSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DEPARTMENT_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendDepartmentSaveShareDataBindingCPM(){
//        return BindingBuilder.bind(sendDepartmentSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendDepartmentUpdateSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DEPARTMENT_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendDepartmentUpdateShareDataBindingCPM(){
//        return BindingBuilder.bind(sendDepartmentUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //BANK MANAGEMENT
//    @Bean
//    public Queue sendBankManagementSaveSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BANK_MANAGEMENT_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBankManagementSaveShareDataBindingCPM(){
//        return BindingBuilder.bind(sendBankManagementSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBankManagementUpdateSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BANK_MANAGEMENT_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBankManagementUpdateShareDataBindingCPM(){
//        return BindingBuilder.bind(sendBankManagementUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    // REGION
//
//    @Bean
//    public Queue sendRegionSaveSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRegionSaveSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendRegionSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendRegionUpdateSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRegionUpdateSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendRegionUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendChangePlanDataShareQueuerevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChangePlanDataShareQueuerevenueBinding() {
//        return BindingBuilder.bind(sendChangePlanDataShareQueuerevenue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPlanServiceAreBindCheckQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanServiceAreBindCheckBinding() {
//        return BindingBuilder.bind(sendPlanServiceAreBindCheckQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPlanServiceAreBindCheckAtDeleteQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK_AT_DELETE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanServiceAreBindCheckAtDeleteBinding() {
//        return BindingBuilder.bind(sendPlanServiceAreBindCheckAtDeleteQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendClientServDataSaveShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CLIENT_SERV_SAVE_DATA_SHARE_TICKET_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendClientServDataSaveShareQueueBinding() {
//        return BindingBuilder.bind(sendClientServDataSaveShareQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendDirectChargeDataToRevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendDirectChargeDataToRevenueQueueBind() {
//        return BindingBuilder.bind(sendDirectChargeDataToRevenue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendDCreditDoctoCMS() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendDCreditDoctoCMSBind() {
//        return BindingBuilder.bind(sendDCreditDoctoCMS()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendInventoryCreateNewChargeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_NEW_CHARGE_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendInventoryCreateNewChargeBindings() {
//        return BindingBuilder.bind(sendInventoryCreateNewChargeQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendInventoryUpdateNewChargeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_NEW_CHARGE_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendInventoryUpdateNewChargeBindings() {
//        return BindingBuilder.bind(sendInventoryUpdateNewChargeQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendInventoryCreateRefChargeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_REF_CHARGE_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendInventoryCreateRefChargeBindings() {
//        return BindingBuilder.bind(sendInventoryCreateRefChargeQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendInventoryUpdateRefChargeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_REF_CHARGE_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendInventoryUpdateRefChargeBindings() {
//        return BindingBuilder.bind(sendInventoryUpdateRefChargeQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendCafToCustomerQueuerevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCafToCustomerQueuerevenueBindinf() {
//        return BindingBuilder.bind(sendCafToCustomerQueuerevenue()).to(adoptExchange()).withQueueName();
//    }
//// Service for partner
//
//    @Bean
//    public Queue sendServiceCreateForPartnerQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendServiceCreateForPartnerQueueBindings() {
//        return BindingBuilder.bind(sendServiceCreateForPartnerQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendServiceUpdateForPartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendserviceUpdateForPartnerBindinf() {
//        return BindingBuilder.bind(sendServiceUpdateForPartner()).to(adoptExchange()).withQueueName();
//    }
//    //Tax Create To partner
//
//    @Bean
//    public Queue sendTAXCreateForPartnerQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTAXCreateForPartnerQueueQueueBindings() {
//        return BindingBuilder.bind(sendTAXCreateForPartnerQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendTAXUpdateForPartnerQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTAXUpdateForPartnerQueueBindinf() {
//        return BindingBuilder.bind(sendTAXUpdateForPartnerQueue()).to(adoptExchange()).withQueueName();
//    }
//
////charge
//@Bean
//public Queue sendChargeSaveSharedDataQueuePartner() {
//    return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_PARTNER)
//            .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//            .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//            .build();
//}
//
//    @Bean
//    public Binding sendChargeSaveSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendChargeSaveSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Charge APIGW to Inventory Microservice
//    @Bean
//    public Queue sendChargeUpdateSharedDataQueuePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChargeUpdateSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendChargeUpdateSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //postpaid plan
//    @Bean
//    public Queue sendPlanSaveSharedDataQueuePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanSaveSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendPlanSaveSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerSaveSharedDataQueueRevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CREATE_PARTNER_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//
//    @Bean
//    public Queue sendApproveOrgInvoiceQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APPROVE_ORG_INVOICE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendApproveOrgInvoiceQueueBindings() {
//        return BindingBuilder.bind(sendApproveOrgInvoiceQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendApproveCPRDate() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CPR_UPDATE_DATE_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendApproveCPRDateBindings() {
//        return BindingBuilder.bind(sendApproveCPRDate()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendPriceBookDetailsQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_CREATE_DATA_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPriceBookQueueBindings() {
//        return BindingBuilder.bind(sendPriceBookDetailsQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdatedVoidInvoice() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_UPDATE_VOID_INVOICE_STATUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendsendUpdatedVoidInvoiceBindinf() {
//        return BindingBuilder.bind(sendUpdatedVoidInvoice()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding sendPartnerSaveSharedDataBindingRevenue() {
//        return BindingBuilder.bind(sendPartnerSaveSharedDataQueueRevenue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerUpdateSharedDataQueueRevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_UPDATE_PARTNER_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerUpdateSharedDataBindingRevenue() {
//        return BindingBuilder.bind(sendPartnerUpdateSharedDataQueueRevenue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerDeleteSharedDataQueueRevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DELETE_PARTNER_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerDeleteSharedDataBindingRevenue() {
//        return BindingBuilder.bind(sendPartnerDeleteSharedDataQueueRevenue()).to(adoptExchange()).withQueueName();
//    }
//    //    Update Plan APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPlanUpdateSharedDataQueuePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanUpdateSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendPlanUpdateSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //plan group
//    //    Create Plan Group APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPlanGroupSaveSharedDataQueuePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupSaveSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendPlanGroupSaveSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Plan Group APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPlanGroupUpdateSharedDataQueuePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupUpdateSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendPlanGroupUpdateSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //pricebook
//    @Bean
//    public Queue sendPartnerPlanBundleCreatePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_CREATE_DATA_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnerPlanBundleCreatePartnerBindings() {
//        return BindingBuilder.bind(sendPartnerPlanBundleCreatePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //pricebook update
//    @Bean
//    public Queue sendPartnerPlanBundlePartnerUpDate() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_UPDATE_DATA_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnerPlanBundleUpDatePartnerBindings() {
//        return BindingBuilder.bind(sendPartnerPlanBundlePartnerUpDate()).to(adoptExchange()).withQueueName();
//    }
//    //pricebook update
//    @Bean
//    public Queue sendPartnerPlanBundlerevenuerUpDate() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_UPDATE_DATA_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnerPlanBundleUpDateRevenueBindings() {
//        return BindingBuilder.bind(sendPartnerPlanBundlerevenuerUpDate()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerChangeStatus() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMER_TERMINATION_DATA_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerChangeStatusBindings() {
//        return BindingBuilder.bind(sendCustomerChangeStatus()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendPartnerBalanceApi() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnerBalanceApiBindings() {
//        return BindingBuilder.bind(sendPartnerBalanceApi()).to(adoptExchange()).withQueueName();
//    }
////Shift Location
//    @Bean
//    public Queue sendPartnerShiftLocationPartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_SHIFT_LOCATION_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnerShiftLocationPartnerBindings() {
//        return BindingBuilder.bind(sendPartnerShiftLocationPartner()).to(adoptExchange()).withQueueName();
//    }
//
////approve Payment Revenue
//
//    @Bean
//    public Queue sendPartnerApprovePaymentRevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnersendPartnerApprovePaymentRevenueBindings() {
//        return BindingBuilder.bind(sendPartnerApprovePaymentRevenue()).to(adoptExchange()).withQueueName();
//    }
//    //approve Payment Partner
//    @Bean
//    public Queue sendPartnerApprovePaymentPartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnersendPartnerApprovePaymentPartnerBindings() {
//        return BindingBuilder.bind(sendPartnerApprovePaymentPartner()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerSaveSharedDataCmsQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerSaveSharedDataCmsBinding() {
//        return BindingBuilder.bind(sendPartnerSaveSharedDataCmsQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerUpdateSharedDataCmsQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerUpdateSharedDataCmsBinding() {
//        return BindingBuilder.bind(sendPartnerUpdateSharedDataCmsQueue()).to(adoptExchange()).withQueueName();
//    }
//    //Send CAF to Customer To Inventory
//    @Bean
//    public Queue sendCafToCustomerInventoryQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//// CAS Master
//    @Bean
//    public Queue sendCasMasterCreateSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CASMASTER_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCafToCustomerInventoryBinding() {
//        return BindingBuilder.bind(sendCafToCustomerInventoryQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCafToCustomerCommonAPIGWQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_COMMONAPIGW)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCasMasterCreateSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendCasMasterCreateSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCasMasterUpdateSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CASMASTER_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCafToCustomerCommonAPIGWBinding() {
//        return BindingBuilder.bind(sendCafToCustomerCommonAPIGWQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCafToCustomerTicketQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCasMasterUpdateSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendCasMasterUpdateSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//    @Bean
//    public Queue sendServiceTerminationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_TERMINATION_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCafToCustomerTikcetBinding() {
//        return BindingBuilder.bind(sendCafToCustomerTicketQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    /**quota recieve to apigw queue binding started**/
//    @Bean
//    public Queue SendQuotaMsgQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_QUOTA_FROM_RADIUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Queue sendServiceCreateForCommonQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendQuotaMsgQueueBinding() {
//        return BindingBuilder.bind(SendQuotaMsgQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**quota recieve to apigw queue binding ended**/
//
//    /**quota notification send to notificatiob queue binding started**/
//    @Bean
//    public Queue SendQuotaNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_QUOTA_NOTIFICATION_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendQuotaNotificationQueueBinding() {
//        return BindingBuilder.bind(SendQuotaNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**quota recieve to apigw queue binding ended**/
//
//    public Binding sendServiceCreateForCommonQueueBindings() {
//        return BindingBuilder.bind(sendServiceCreateForCommonQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendServiceUpdateForCommon() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendserviceUpdateForCommonBindinf() {
//        return BindingBuilder.bind(sendServiceUpdateForCommon()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBillToOrgRejectCustPackrel(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_REJECT_ORG_INVOICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceTerminationQueueBinding() {
//        return BindingBuilder.bind(sendServiceTerminationQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Binding sendBillToOrgRejectCustPackrelBinding(){
//        return BindingBuilder.bind(sendBillToOrgRejectCustPackrel()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendRoleCreationDetailsTocms(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleCreationDetailsTocmsBind(){
//        return BindingBuilder.bind(sendRoleCreationDetailsTocms()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendRoleDeletionDetailsTocms(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleDeletionDetailsTocmsBind(){
//        return BindingBuilder.bind(sendRoleDeletionDetailsTocms()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue SendQuotaIntrimMsgQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendQuotaIntrimMsgQueueBinding() {
//        return BindingBuilder.bind(SendQuotaIntrimMsgQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    /**customer online payment send to reveneue queue binding started**/
//    @Bean
//    public Queue SendCustomerOnlinePaymentQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_ONLINE_PAYMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendCustomerOnlinePaymentQueueBinding() {
//        return BindingBuilder.bind(SendCustomerOnlinePaymentQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**customer online payment send to reveneue queue binding ended**/
//
//    //   Plan or Plan Group Inventory Item Serial Number Send Inventory To CMS
//    @Bean
//    public Queue sendItemSerialNumberInventoryToCMSQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendItemSerialNumberInventoryToCMSBindings() {
//        return BindingBuilder.bind(sendItemSerialNumberInventoryToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomersUpdatedSharedDataSalescrmQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_SALESCRM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersUpdatedSharedDatasalescrmBinding() {
//        return BindingBuilder.bind(sendCustomersUpdatedSharedDataSalescrmQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendCustomersUpdatedSharedDataRadiusQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_RADIUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersUpdatedSharedDatRadiusBinding() {
//        return BindingBuilder.bind(sendCustomersUpdatedSharedDataRadiusQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendChangePlanDataShareQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChangePlanDataShareQueueTicketBinding() {
//        return BindingBuilder.bind(sendChangePlanDataShareQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPostpaidTrailInoivceFromRevenue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_POSTPAID_TRIAL_INVOICE_FROM_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPostpaidTrailInoivceFromRevenueBinding() {
//        return BindingBuilder.bind(sendPostpaidTrailInoivceFromRevenue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**Recieve Customer mac delete from radius  binding started**/
//    @Bean
//    public Queue DeleteMacFromRadius() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_DELETE_MAC_FROM_RADIUS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding DeleteMacFromRadiusBinding() {
//        return BindingBuilder.bind(DeleteMacFromRadius()).to(adoptExchange()).withQueueName();
//    }
//    /**Recieve Customer mac delete from radius  binding ended**/
//
//    @Bean
//    public Queue UpdateConcurrencyFromRadius() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_UPDATE_CONCURRENCY_FROM_RADIUS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding UpdateConcurrencyFromRadiusBinding() {
//        return BindingBuilder.bind(UpdateConcurrencyFromRadius()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//    /**Recieve Customer enddate change  from radius  binding started**/
//    @Bean
//    public Queue CustomerEndDateSendFromRadius() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_ENDDATE_FROMRADIUS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustomerEndDateSendFromRadiusBinding() {
//        return BindingBuilder.bind(CustomerEndDateSendFromRadius()).to(adoptExchange()).withQueueName();
//    }
//    /**Recieve Customer enddate change from radius  binding ended**/
//    @Bean
//    public Queue custPlanStatusChangeOnCreditNoteApproveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHANGE_PLAN_STATUS_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding custPlanStatusChangeOnCreditNoteApproveQueueBinding() {
//        return BindingBuilder.bind(custPlanStatusChangeOnCreditNoteApproveQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue dbrHoldResume() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_DBR_SERVICE_HOLD_RESUME).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding dbrHoldResumeBinding() {
//        return BindingBuilder.bind(dbrHoldResume()).to(adoptExchange()).withQueueName();
//    }
//
//    /**Send Customer payment failed to notification binding started**/
//    @Bean
//    public Queue CustomerPaymentFailedSendNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_PAYMENT_FAILED).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustomerPaymentFailedSendNotificationQueueBinding() {
//        return BindingBuilder.bind(CustomerPaymentFailedSendNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//    /**Send Customer payment failed to notification binding ended**/
//
//    /**receive Customer quota reserve  message from radius binding started**/
//    @Bean
//    public Queue updateReservedQuotaQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding updateReservedQuotaBinding() {
//        return BindingBuilder.bind(updateReservedQuotaQueue()).to(adoptExchange()).withQueueName();
//    }
//    /**receive Customer quota reserve  message from radius binding ended**/
//
//    @Bean
//    public Queue saveCustomerDiscountQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DISCOUNT_SAVE_DATA_SHARE_CMS_REVENUEMANAGEMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding saveCustomerDiscountBinding() {
//        return BindingBuilder.bind(saveCustomerDiscountQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**quota exhaust notification send to notificatiob queue binding started**/
//    @Bean
//    public Queue SendQuotaExhuastNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendQuotaExhuastNotificationQueueBinding() {
//        return BindingBuilder.bind(SendQuotaExhuastNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**quota recieve to apigw queue binding ended**/
//
//
//    @Bean
//    public Queue sendSaveVoucherBatchQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SAVE_VOUCHER_BATCH_DATA_SHARE_TO_REVENUEMANAGEMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendSaveVoucherBatchQueueBinding() {
//        return BindingBuilder.bind(sendSaveVoucherBatchQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPlanGroupUpdateQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_SALESCRM_UPDATE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupUpdateQueueBinding() {
//        return BindingBuilder.bind(sendPlanGroupUpdateQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendupdatedCustPlanMappingQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTPLANMAPPINGS_REVENUE_TO_CMS_P2P).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendupdatedCustPlanMappingQueueBinding() {
//        return BindingBuilder.bind(sendupdatedCustPlanMappingQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**receive Payment configuration message queue binding from common started**/
//
//    @Bean
//    public Queue sendPaymentConfigurationToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_CONFIGURTION_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendPaymentConfigurationToCMSQueueBinding(){
//        return BindingBuilder.bind(sendPaymentConfigurationToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding sendConfigurationToIntigrationQueueBinding(){
//        return BindingBuilder.bind(sendConfigurationToIntigrationQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendConfigurationToIntigrationQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CMS_CONFIGURATION_INTIGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    /**receive Payment configuration message queue binding from common ended**/
//
//    @Bean
//    public Queue sendCreditDocIdsToCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CREDIT_DOC_IDS_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreditDocIdsToCMSQueueBinding(){
//        return BindingBuilder.bind(sendCreditDocIdsToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCreditDocToCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CREDIT_DOC_DETAILS_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreditDocToCMSQueueBinding(){
//        return BindingBuilder.bind(sendCreditDocToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustInvParamsToCMSQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUST_INV_DETAIL_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustInvParamsToCMSBindings() {
//        return BindingBuilder.bind(sendCustInvParamsToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Binding sendCUuidToCMSQueueBinding(){
//        return BindingBuilder.bind(sendUuidToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendUuidToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CMS_CONFIGURATION_INTIGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCNmsServiceDeleteRequestQueueBinding(){
//        return BindingBuilder.bind(sendServiceDeleteRequestQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendServiceDeleteRequestQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_NMS_SERVICE_DELETE_REQUEST)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendUuidDataToCMSQueueBinding(){
//        return BindingBuilder.bind(sendUuidDataToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendUuidDataToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UUID_DATA_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendStatusUpdateToInventoryQueueBinding(){
//        return BindingBuilder.bind(sendStatusUpdateToInventoryQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendStatusUpdateToInventoryQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CMS_UPDATE_STATUS_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Queue sendCreditDebitDocToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CREDIT_DEBIT_DOC_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreditDebitDocToCMSQueueBinding(){
//        return BindingBuilder.bind(sendCreditDebitDocToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**send socket message to common queue binding started**/
//    @Bean
//    public Queue SendSocketMessageToCommonQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_SOCKET_MESSAGE_TO_COMMON).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendSocketMessageToCommonQueueBinding() {
//        return BindingBuilder.bind(SendSocketMessageToCommonQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendCAFTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CAF_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCAFTATMessageBindings() {
//        return BindingBuilder.bind(sendCAFTATMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendTerminationTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TREMINATION_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendsendTerminationTATMessageQueueTATMessageBindings() {
//        return BindingBuilder.bind(sendTerminationTATMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendLEADTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendLEADTATMessageBindings() {
//        return BindingBuilder.bind(sendLEADTATMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMvnoDocSaveMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_DOC_SAVE_FROM_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoDocSaveMessageQueueBindings() {
//        return BindingBuilder.bind(sendMvnoDocSaveMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMvnoDocUpdateMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_DOC_UPDATE_FROM_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoDocUpdateMessageQueueBindings() {
//        return BindingBuilder.bind(sendMvnoDocSaveMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**send socket message to common queue binding ended**/
//    /**send mvno document dunning  queue binding started**/
//    @Bean
//    public Queue sendMvnoDocumentDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoDocumentDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoDocumentDunningNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendMvnoStatusDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_STATUS_DUNNING_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoStatusDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoStatusDunningNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendStaffStatusDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_STAFF_STATUS_DUNNING_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendStaffStatusDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendStaffStatusDunningNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerStatusDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_DUNNING_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerStatusDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendCustomerStatusDunningNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerIpToRadiusUpdateQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_IP_TO_UPDATE_RADIUS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerIpToRadiusUpdateQueueBindings() {
//        return BindingBuilder.bind(sendCustomerIpToRadiusUpdateQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerIpToRadiusSaveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_IP_TO_SAVE_RADIUS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerIpToRadiusSaveQueueBindings() {
//        return BindingBuilder.bind(sendCustomerIpToRadiusSaveQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerIpToRadiusDeleteQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_IP_TO_DELETE_RADIUS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerIpToRadiusDeleteQueueBindings() {
//        return BindingBuilder.bind(sendCustomerIpToRadiusDeleteQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    /**send mvno Deactivation **/
//    @Bean
//    public Queue sendMvnoExpireNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoDeactivationNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoExpireNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//    /**send mvno Deactivation End **/
//
//    @Bean
//    public Queue sendCustomerStatusInactiveDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_INACTIVE_DUNNING_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerStatusInactiveDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendCustomerStatusInactiveDunningNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
// /**send mvno Payment **/
//    @Bean
//    public Queue sendMvnoPaymentAdvanceNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding sendMvnoPaymentNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoPaymentAdvanceNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//    @Bean
//    public Queue sendBudPayPaymentStatusQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_BUDPAY_PAYMENT_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendBudPayPaymentStatusQueueBindings() {
//        return BindingBuilder.bind(sendBudPayPaymentStatusQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMvnoPaymentExpiryNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding sendMvnoPaymentExpiryNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoPaymentExpiryNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendMVNOSharedDataQueueCMSForISP() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS_ISP)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendMVNOSharedDataBindingCMSISP() {
//        return BindingBuilder.bind(sendMVNOSharedDataQueueCMSForISP()).to(adoptExchange()).withQueueName();
//    }
//        /**send mvno Payment End **/
//        @Bean
//        public Queue sendCustPlanStatusRadius() {
//            return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUST_PLAN_DETAIL_FROM_RADIUS)
//                    .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                    .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                    .build();
//        }
//
//    @Bean
//    public Binding sendCustPlanStatusRadiusBinding() {
//        return BindingBuilder.bind(sendCustPlanStatusRadius()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPlanExpiryNotification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PLAN_EXPIRY_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanExpiryNotificationBinding() {
//        return BindingBuilder.bind(sendPlanExpiryNotification()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendCustomerStatusToRadius() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_STATUS_UPDATE_RADIUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCustomerStatusToRadiusBinding() {
//        return BindingBuilder.bind(sendCustomerStatusToRadius()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBudPayPaymentcreditQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_BUD_PAYMENT_CREDIT_TO_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendBudPayPaymentcreditQueueBindings() {
//        return BindingBuilder.bind(sendBudPayPaymentcreditQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBusinessUnitSaveSharedDataQueueCMS() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBusinessUnitSaveSharedDataBindingCMS() {
//        return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueCMS()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBusinessUnitUpdatedSharedDataQueueCMS() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBusinessUnitUpdatedSharedDataBindingCMS() {
//        return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueCMS()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue AddMacFromAPIGTQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING_CMS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding AddMacFromAPIGTBinding() {
//        return BindingBuilder.bind(AddMacFromAPIGTQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMVNODiscountQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_DISCOUNT_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendMVNODiscountQueueBinding() {
//        return BindingBuilder.bind(sendMVNODiscountQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendCreateCustomer() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCreateCustomerBinding() {
//        return BindingBuilder.bind(sendCreateCustomer()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendUpdateCustomer() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_API_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendUpdateCustomerBinding() {
//        return BindingBuilder.bind(sendUpdateCustomer()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBudPayChangePlanMessageToRevenue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_BUDPAY_CUSTOMER_CWSC_CHANGE_PLAN_TO_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBudPayChangePlanMessageToRevenueBinding() {
//        return BindingBuilder.bind(sendBudPayChangePlanMessageToRevenue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendVendorSavedDataToCMS() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_SAVE_VENDOR_QUEUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendVendorSavedDataToCMSBinding() {
//        return BindingBuilder.bind(sendVendorSavedDataToCMS()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendVendorUpdatedDataToCMS() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_VENDOR_QUEUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendVendorUpdatedDataToCMSBinding() {
//        return BindingBuilder.bind(sendVendorUpdatedDataToCMS()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPaymentAuditToCmsQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendPaymentAuditToCmsQueueBinding(){
//        return BindingBuilder.bind(sendPaymentAuditToCmsQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPaymentAuditToIntegrationQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_INTEGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendPaymentAuditToIntegrationQueueBinding(){
//        return BindingBuilder.bind(sendPaymentAuditToIntegrationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendCustMappingStatusUpdateToCMS() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_MAPPING_STATUS_UPDATE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustMappingStatusUpdateToCMSQueueBind() {
//        return BindingBuilder.bind(sendCustMappingStatusUpdateToCMS()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendOTPProfileToCommonQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendOTPProfileToCommonBinding(){
//        return BindingBuilder.bind(sendOTPProfileToCommonQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendOTPProfileUpdateToCommonQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON_UPDATE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendOTPProfileUpdateToCommonBinding(){
//        return BindingBuilder.bind(sendOTPProfileUpdateToCommonQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendOTPProfileToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_PROFILE_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendOTPProfileToCMSBinding(){
//        return BindingBuilder.bind(sendOTPProfileToCMSQueue()).to(adoptExchange()).withQueueName();
//    }



//    @Bean
//    public Queue sendClientServDataUpdateShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CLIENT_SERV_UPDATE_DATA_SHARE_TICKET_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendClientServDataUpdateShareQueueBinding() {
//        return BindingBuilder.bind(sendClientServDataUpdateShareQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendTaxCreateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTaxCreateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendTaxCreateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendTaxUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTaxUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendTaxUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendChargeSaveDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendsendChargeSaveDataShareQueueBinding() {
//        return BindingBuilder.bind(sendChargeSaveDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendChargeUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendsendChargeUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendChargeUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPlanSaveDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanSaveDataShareQueueBinding() {
//        return BindingBuilder.bind(sendPlanSaveDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendPlanUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendPlanUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendDiscountSaveDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DISCOUNT_SAVE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                    .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendDiscountSaveDataShareQueueBinding() {
//        return BindingBuilder.bind(sendDiscountSaveDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendDiscountUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DISCOUNT_UPDATE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendDiscountUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendDiscountUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendPlanGroupSaveDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupSaveQueueSaveDataShareQueueBinding() {
//        return BindingBuilder.bind(sendPlanGroupSaveDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendPlanGroupUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendPlanGroupUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomersSaveDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersSaveQueueSaveDataShareQueueBinding() {
//        return BindingBuilder.bind(sendCustomersSaveDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendCustomersUpdateDataShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersUpdateDataShareQueueBinding() {
//        return BindingBuilder.bind(sendCustomersUpdateDataShareQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendCreditDocument() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCreditDocumentQueueBinding() {
//        return BindingBuilder.bind(sendCreditDocument()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendCustomersDocument() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendsendCustomersDocumentQueueBinding() {
//        return BindingBuilder.bind(sendCustomersDocument()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomersUpdateDocument() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendsendCustomersUpdatetQueueBinding() {
//        return BindingBuilder.bind(sendCustomersDocument()).to(adoptExchange()).withQueueName();
//    }
//
//    // Setting Data Transfer to Common APIGW TO CMS
//    //MVNO
//    @Bean
//    public Queue sendCreateMvnoCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateMvnoCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendCreateMvnoCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateMvnoCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateMvnoCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendUpdateMvnoCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //Role
//    @Bean
//    public Queue sendCreateRoleCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_ROLE_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateRoleCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendCreateRoleCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateRoleCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_ROLE_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateRoleCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendUpdateRoleCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //Staff User
//    @Bean
//    public Queue sendCreateStafUserCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_STAFFUSER_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateStaffUserCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendCreateStafUserCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateStaffUserCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_STAFFUSER_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateStaffUserCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendUpdateStaffUserCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //Teams
//    @Bean
//    public Queue sendCreateTeamsCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateTeamsCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendCreateTeamsCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateTeamsCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateTeamsCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendUpdateTeamsCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    //System Configuration
//    @Bean
//    public Queue sendCreateSystemConfigurationCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateSystemConfigurationCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendCreateSystemConfigurationCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateSystemConfigurationCommonApiGwCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateSystemConfigurationCommonApiGwCMSBinding(){
//        return BindingBuilder.bind(sendUpdateSystemConfigurationCommonApiGwCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //customer for common apigateway
//
//    @Bean
//    public Queue sendCustomersSaveSharedDataForApiCommonQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersSaveSharedDataForApiCommonBinding() {
//        return BindingBuilder.bind(sendCustomersSaveSharedDataForApiCommonQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    // COMMON APIGW TO APIGW QUEUE CONFIGURATION
//
//
//    //COUNTRY
//    @Bean
//    public Queue sendCountrySaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCountrySaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendCountrySaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendCountryUpdateSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCountryUpdateSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendCountryUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //CITY
//    @Bean
//    public Queue sendCitySaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCitySharedDataBindingCPM() {
//        return BindingBuilder.bind(sendCitySaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCityUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCityUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendCityUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //PINCODE
//    @Bean
//    public Queue sendPincodeSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPincodeSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendPincodeSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPincodeUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPincodeUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//
//    //STATE
//    @Bean
//    public Queue sendStateSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendStateSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendStateSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendStateUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendStateUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendStateUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //AREA
//    @Bean
//    public Queue sendAreaSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendAreaSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendAreaSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendAreaUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendAreaUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendAreaUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //SERVICEAREA
//    @Bean
//    public Queue sendServiceAreaSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendServiceAreaUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //BUSINESS UNIT
//    @Bean
//    public Queue sendBusinessUnitSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBusinessUnitSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBusinessUnitUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessUnitUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //BUSINESS VERTICAL
//
//    @Bean
//    public Queue sendBusinessVerticalsSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_VERTICALS_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessVerticalsSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBusinessVerticalsSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBusinessVerticalsUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBusinessVerticalsUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBusinessVerticalsUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //BRANCH
//    @Bean
//    public Queue sendBranchSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBranchSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBranchSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendBranchUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBranchUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendBranchUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //INVESTMENTCODE
//    @Bean
//    public Queue sendInvestmentCodeSaveSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendInvestmentCodeSaveSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendInvestmentCodeSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendInvestmentCodeUpdateSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendInvestmentCodeUpdateSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendInvestmentCodeUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //SUB BUSINESS UNIT
//    @Bean
//    public Queue sendSubBusinessUnitSaveSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendSubBusinessUnitSaveSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendSubBusinessUnitSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendSubBusinessUnitUpdateSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendSubBusinessUnitUpdateSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendSubBusinessUnitUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //SUB BUSINESS VERTICALS
//    @Bean
//    public Queue sendSubBusinessVerticalsSaveSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendSubBusinessVerticalsSaveSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendSubBusinessVerticalsSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendSubBusinessVerticalsUpdatedSharedDataQueueCPM() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendSubBusinessVerticalsUpdatedSharedDataBindingCPM() {
//        return BindingBuilder.bind(sendSubBusinessVerticalsUpdatedSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //DEPARTMENT
//    @Bean
//    public Queue sendDepartmentSaveSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DEPARTMENT_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendDepartmentSaveShareDataBindingCPM(){
//        return BindingBuilder.bind(sendDepartmentSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendDepartmentUpdateSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DEPARTMENT_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendDepartmentUpdateShareDataBindingCPM(){
//        return BindingBuilder.bind(sendDepartmentUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    //BANK MANAGEMENT
//    @Bean
//    public Queue sendBankManagementSaveSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BANK_MANAGEMENT_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBankManagementSaveShareDataBindingCPM(){
//        return BindingBuilder.bind(sendBankManagementSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBankManagementUpdateSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BANK_MANAGEMENT_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBankManagementUpdateShareDataBindingCPM(){
//        return BindingBuilder.bind(sendBankManagementUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    // REGION
//
//    @Bean
//    public Queue sendRegionSaveSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRegionSaveSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendRegionSaveSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendRegionUpdateSharedDataQueueCPM(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_CPM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRegionUpdateSharedDataBindingCPM(){
//        return BindingBuilder.bind(sendRegionUpdateSharedDataQueueCPM()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendChangePlanDataShareQueuerevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChangePlanDataShareQueuerevenueBinding() {
//        return BindingBuilder.bind(sendChangePlanDataShareQueuerevenue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPlanServiceAreBindCheckQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanServiceAreBindCheckBinding() {
//        return BindingBuilder.bind(sendPlanServiceAreBindCheckQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPlanServiceAreBindCheckAtDeleteQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK_AT_DELETE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanServiceAreBindCheckAtDeleteBinding() {
//        return BindingBuilder.bind(sendPlanServiceAreBindCheckAtDeleteQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendClientServDataSaveShareQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CLIENT_SERV_SAVE_DATA_SHARE_TICKET_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendClientServDataSaveShareQueueBinding() {
//        return BindingBuilder.bind(sendClientServDataSaveShareQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendDirectChargeDataToRevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendDirectChargeDataToRevenueQueueBind() {
//        return BindingBuilder.bind(sendDirectChargeDataToRevenue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendDCreditDoctoCMS() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendDCreditDoctoCMSBind() {
//        return BindingBuilder.bind(sendDCreditDoctoCMS()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendInventoryCreateNewChargeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_NEW_CHARGE_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendInventoryCreateNewChargeBindings() {
//        return BindingBuilder.bind(sendInventoryCreateNewChargeQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendInventoryUpdateNewChargeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_NEW_CHARGE_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendInventoryUpdateNewChargeBindings() {
//        return BindingBuilder.bind(sendInventoryUpdateNewChargeQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendInventoryCreateRefChargeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_REF_CHARGE_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendInventoryCreateRefChargeBindings() {
//        return BindingBuilder.bind(sendInventoryCreateRefChargeQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendInventoryUpdateRefChargeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_REF_CHARGE_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendInventoryUpdateRefChargeBindings() {
//        return BindingBuilder.bind(sendInventoryUpdateRefChargeQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendCafToCustomerQueuerevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCafToCustomerQueuerevenueBindinf() {
//        return BindingBuilder.bind(sendCafToCustomerQueuerevenue()).to(adoptExchange()).withQueueName();
//    }
//// Service for partner
//
//    @Bean
//    public Queue sendServiceCreateForPartnerQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendServiceCreateForPartnerQueueBindings() {
//        return BindingBuilder.bind(sendServiceCreateForPartnerQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendServiceUpdateForPartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendserviceUpdateForPartnerBindinf() {
//        return BindingBuilder.bind(sendServiceUpdateForPartner()).to(adoptExchange()).withQueueName();
//    }
//    //Tax Create To partner
//
//    @Bean
//    public Queue sendTAXCreateForPartnerQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTAXCreateForPartnerQueueQueueBindings() {
//        return BindingBuilder.bind(sendTAXCreateForPartnerQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendTAXUpdateForPartnerQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendTAXUpdateForPartnerQueueBindinf() {
//        return BindingBuilder.bind(sendTAXUpdateForPartnerQueue()).to(adoptExchange()).withQueueName();
//    }
//
////charge
//@Bean
//public Queue sendChargeSaveSharedDataQueuePartner() {
//    return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_PARTNER)
//            .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//            .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//            .build();
//}
//
//    @Bean
//    public Binding sendChargeSaveSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendChargeSaveSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Charge APIGW to Inventory Microservice
//    @Bean
//    public Queue sendChargeUpdateSharedDataQueuePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChargeUpdateSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendChargeUpdateSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //postpaid plan
//    @Bean
//    public Queue sendPlanSaveSharedDataQueuePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanSaveSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendPlanSaveSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerSaveSharedDataQueueRevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CREATE_PARTNER_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//
//    @Bean
//    public Queue sendApproveOrgInvoiceQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APPROVE_ORG_INVOICE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendApproveOrgInvoiceQueueBindings() {
//        return BindingBuilder.bind(sendApproveOrgInvoiceQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendApproveCPRDate() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CPR_UPDATE_DATE_SHARE_REVENUEMANAGEMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendApproveCPRDateBindings() {
//        return BindingBuilder.bind(sendApproveCPRDate()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendPriceBookDetailsQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_CREATE_DATA_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPriceBookQueueBindings() {
//        return BindingBuilder.bind(sendPriceBookDetailsQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdatedVoidInvoice() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_UPDATE_VOID_INVOICE_STATUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendsendUpdatedVoidInvoiceBindinf() {
//        return BindingBuilder.bind(sendUpdatedVoidInvoice()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding sendPartnerSaveSharedDataBindingRevenue() {
//        return BindingBuilder.bind(sendPartnerSaveSharedDataQueueRevenue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerUpdateSharedDataQueueRevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_UPDATE_PARTNER_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerUpdateSharedDataBindingRevenue() {
//        return BindingBuilder.bind(sendPartnerUpdateSharedDataQueueRevenue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerDeleteSharedDataQueueRevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DELETE_PARTNER_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerDeleteSharedDataBindingRevenue() {
//        return BindingBuilder.bind(sendPartnerDeleteSharedDataQueueRevenue()).to(adoptExchange()).withQueueName();
//    }
//    //    Update Plan APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPlanUpdateSharedDataQueuePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanUpdateSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendPlanUpdateSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //plan group
//    //    Create Plan Group APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPlanGroupSaveSharedDataQueuePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupSaveSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendPlanGroupSaveSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //    Update Plan Group APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPlanGroupUpdateSharedDataQueuePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupUpdateSharedDataBindingPartner() {
//        return BindingBuilder.bind(sendPlanGroupUpdateSharedDataQueuePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //pricebook
//    @Bean
//    public Queue sendPartnerPlanBundleCreatePartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_CREATE_DATA_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnerPlanBundleCreatePartnerBindings() {
//        return BindingBuilder.bind(sendPartnerPlanBundleCreatePartner()).to(adoptExchange()).withQueueName();
//    }
//
//    //pricebook update
//    @Bean
//    public Queue sendPartnerPlanBundlePartnerUpDate() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_UPDATE_DATA_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnerPlanBundleUpDatePartnerBindings() {
//        return BindingBuilder.bind(sendPartnerPlanBundlePartnerUpDate()).to(adoptExchange()).withQueueName();
//    }
//    //pricebook update
//    @Bean
//    public Queue sendPartnerPlanBundlerevenuerUpDate() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_UPDATE_DATA_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnerPlanBundleUpDateRevenueBindings() {
//        return BindingBuilder.bind(sendPartnerPlanBundlerevenuerUpDate()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerChangeStatus() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMER_TERMINATION_DATA_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerChangeStatusBindings() {
//        return BindingBuilder.bind(sendCustomerChangeStatus()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendPartnerBalanceApi() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnerBalanceApiBindings() {
//        return BindingBuilder.bind(sendPartnerBalanceApi()).to(adoptExchange()).withQueueName();
//    }
////Shift Location
//    @Bean
//    public Queue sendPartnerShiftLocationPartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_SHIFT_LOCATION_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnerShiftLocationPartnerBindings() {
//        return BindingBuilder.bind(sendPartnerShiftLocationPartner()).to(adoptExchange()).withQueueName();
//    }
//
////approve Payment Revenue
//
//    @Bean
//    public Queue sendPartnerApprovePaymentRevenue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnersendPartnerApprovePaymentRevenueBindings() {
//        return BindingBuilder.bind(sendPartnerApprovePaymentRevenue()).to(adoptExchange()).withQueueName();
//    }
//    //approve Payment Partner
//    @Bean
//    public Queue sendPartnerApprovePaymentPartner() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPartnersendPartnerApprovePaymentPartnerBindings() {
//        return BindingBuilder.bind(sendPartnerApprovePaymentPartner()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerSaveSharedDataCmsQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerSaveSharedDataCmsBinding() {
//        return BindingBuilder.bind(sendPartnerSaveSharedDataCmsQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerUpdateSharedDataCmsQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerUpdateSharedDataCmsBinding() {
//        return BindingBuilder.bind(sendPartnerUpdateSharedDataCmsQueue()).to(adoptExchange()).withQueueName();
//    }
//    //Send CAF to Customer To Inventory
//    @Bean
//    public Queue sendCafToCustomerInventoryQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//// CAS Master
//    @Bean
//    public Queue sendCasMasterCreateSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CASMASTER_CREATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCafToCustomerInventoryBinding() {
//        return BindingBuilder.bind(sendCafToCustomerInventoryQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCafToCustomerCommonAPIGWQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_COMMONAPIGW)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCasMasterCreateSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendCasMasterCreateSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCasMasterUpdateSharedDataQueueInventory() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CASMASTER_UPDATE_DATA_SHARE_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCafToCustomerCommonAPIGWBinding() {
//        return BindingBuilder.bind(sendCafToCustomerCommonAPIGWQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCafToCustomerTicketQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCasMasterUpdateSharedDataBindingInventory() {
//        return BindingBuilder.bind(sendCasMasterUpdateSharedDataQueueInventory()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//    @Bean
//    public Queue sendServiceTerminationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_TERMINATION_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCafToCustomerTikcetBinding() {
//        return BindingBuilder.bind(sendCafToCustomerTicketQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    /**quota recieve to apigw queue binding started**/
//    @Bean
//    public Queue SendQuotaMsgQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_QUOTA_FROM_RADIUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Queue sendServiceCreateForCommonQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendQuotaMsgQueueBinding() {
//        return BindingBuilder.bind(SendQuotaMsgQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**quota recieve to apigw queue binding ended**/
//
//    /**quota notification send to notificatiob queue binding started**/
//    @Bean
//    public Queue SendQuotaNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_QUOTA_NOTIFICATION_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendQuotaNotificationQueueBinding() {
//        return BindingBuilder.bind(SendQuotaNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**quota recieve to apigw queue binding ended**/
//
//    public Binding sendServiceCreateForCommonQueueBindings() {
//        return BindingBuilder.bind(sendServiceCreateForCommonQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendServiceUpdateForCommon() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendserviceUpdateForCommonBindinf() {
//        return BindingBuilder.bind(sendServiceUpdateForCommon()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBillToOrgRejectCustPackrel(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_REJECT_ORG_INVOICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendServiceTerminationQueueBinding() {
//        return BindingBuilder.bind(sendServiceTerminationQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Binding sendBillToOrgRejectCustPackrelBinding(){
//        return BindingBuilder.bind(sendBillToOrgRejectCustPackrel()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendRoleCreationDetailsTocms(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleCreationDetailsTocmsBind(){
//        return BindingBuilder.bind(sendRoleCreationDetailsTocms()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendRoleDeletionDetailsTocms(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleDeletionDetailsTocmsBind(){
//        return BindingBuilder.bind(sendRoleDeletionDetailsTocms()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue SendQuotaIntrimMsgQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendQuotaIntrimMsgQueueBinding() {
//        return BindingBuilder.bind(SendQuotaIntrimMsgQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    /**customer online payment send to reveneue queue binding started**/
//    @Bean
//    public Queue SendCustomerOnlinePaymentQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_ONLINE_PAYMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendCustomerOnlinePaymentQueueBinding() {
//        return BindingBuilder.bind(SendCustomerOnlinePaymentQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**customer online payment send to reveneue queue binding ended**/
//
//    //   Plan or Plan Group Inventory Item Serial Number Send Inventory To CMS
//    @Bean
//    public Queue sendItemSerialNumberInventoryToCMSQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendItemSerialNumberInventoryToCMSBindings() {
//        return BindingBuilder.bind(sendItemSerialNumberInventoryToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomersUpdatedSharedDataSalescrmQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_SALESCRM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersUpdatedSharedDatasalescrmBinding() {
//        return BindingBuilder.bind(sendCustomersUpdatedSharedDataSalescrmQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendCustomersUpdatedSharedDataRadiusQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_RADIUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersUpdatedSharedDatRadiusBinding() {
//        return BindingBuilder.bind(sendCustomersUpdatedSharedDataRadiusQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendChangePlanDataShareQueueTicket() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChangePlanDataShareQueueTicketBinding() {
//        return BindingBuilder.bind(sendChangePlanDataShareQueueTicket()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPostpaidTrailInoivceFromRevenue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_POSTPAID_TRIAL_INVOICE_FROM_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPostpaidTrailInoivceFromRevenueBinding() {
//        return BindingBuilder.bind(sendPostpaidTrailInoivceFromRevenue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**Recieve Customer mac delete from radius  binding started**/
//    @Bean
//    public Queue DeleteMacFromRadius() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_DELETE_MAC_FROM_RADIUS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding DeleteMacFromRadiusBinding() {
//        return BindingBuilder.bind(DeleteMacFromRadius()).to(adoptExchange()).withQueueName();
//    }
//    /**Recieve Customer mac delete from radius  binding ended**/
//
//    @Bean
//    public Queue UpdateConcurrencyFromRadius() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_UPDATE_CONCURRENCY_FROM_RADIUS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding UpdateConcurrencyFromRadiusBinding() {
//        return BindingBuilder.bind(UpdateConcurrencyFromRadius()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//    /**Recieve Customer enddate change  from radius  binding started**/
//    @Bean
//    public Queue CustomerEndDateSendFromRadius() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_ENDDATE_FROMRADIUS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustomerEndDateSendFromRadiusBinding() {
//        return BindingBuilder.bind(CustomerEndDateSendFromRadius()).to(adoptExchange()).withQueueName();
//    }
//    /**Recieve Customer enddate change from radius  binding ended**/
//    @Bean
//    public Queue custPlanStatusChangeOnCreditNoteApproveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHANGE_PLAN_STATUS_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding custPlanStatusChangeOnCreditNoteApproveQueueBinding() {
//        return BindingBuilder.bind(custPlanStatusChangeOnCreditNoteApproveQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue dbrHoldResume() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_DBR_SERVICE_HOLD_RESUME).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding dbrHoldResumeBinding() {
//        return BindingBuilder.bind(dbrHoldResume()).to(adoptExchange()).withQueueName();
//    }
//
//    /**Send Customer payment failed to notification binding started**/
//    @Bean
//    public Queue CustomerPaymentFailedSendNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_PAYMENT_FAILED).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustomerPaymentFailedSendNotificationQueueBinding() {
//        return BindingBuilder.bind(CustomerPaymentFailedSendNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//    /**Send Customer payment failed to notification binding ended**/
//
//    /**receive Customer quota reserve  message from radius binding started**/
//    @Bean
//    public Queue updateReservedQuotaQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding updateReservedQuotaBinding() {
//        return BindingBuilder.bind(updateReservedQuotaQueue()).to(adoptExchange()).withQueueName();
//    }
//    /**receive Customer quota reserve  message from radius binding ended**/
//
//    @Bean
//    public Queue saveCustomerDiscountQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DISCOUNT_SAVE_DATA_SHARE_CMS_REVENUEMANAGEMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding saveCustomerDiscountBinding() {
//        return BindingBuilder.bind(saveCustomerDiscountQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**quota exhaust notification send to notificatiob queue binding started**/
//    @Bean
//    public Queue SendQuotaExhuastNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendQuotaExhuastNotificationQueueBinding() {
//        return BindingBuilder.bind(SendQuotaExhuastNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**quota recieve to apigw queue binding ended**/
//
//
//    @Bean
//    public Queue sendSaveVoucherBatchQueue() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SAVE_VOUCHER_BATCH_DATA_SHARE_TO_REVENUEMANAGEMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendSaveVoucherBatchQueueBinding() {
//        return BindingBuilder.bind(sendSaveVoucherBatchQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPlanGroupUpdateQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_SALESCRM_UPDATE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupUpdateQueueBinding() {
//        return BindingBuilder.bind(sendPlanGroupUpdateQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendupdatedCustPlanMappingQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTPLANMAPPINGS_REVENUE_TO_CMS_P2P).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendupdatedCustPlanMappingQueueBinding() {
//        return BindingBuilder.bind(sendupdatedCustPlanMappingQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**receive Payment configuration message queue binding from common started**/
//
//    @Bean
//    public Queue sendPaymentConfigurationToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_CONFIGURTION_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendPaymentConfigurationToCMSQueueBinding(){
//        return BindingBuilder.bind(sendPaymentConfigurationToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding sendConfigurationToIntigrationQueueBinding(){
//        return BindingBuilder.bind(sendConfigurationToIntigrationQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendConfigurationToIntigrationQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CMS_CONFIGURATION_INTIGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    /**receive Payment configuration message queue binding from common ended**/
//
//    @Bean
//    public Queue sendCreditDocIdsToCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CREDIT_DOC_IDS_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreditDocIdsToCMSQueueBinding(){
//        return BindingBuilder.bind(sendCreditDocIdsToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCreditDocToCMSQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CREDIT_DOC_DETAILS_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreditDocToCMSQueueBinding(){
//        return BindingBuilder.bind(sendCreditDocToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustInvParamsToCMSQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUST_INV_DETAIL_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustInvParamsToCMSBindings() {
//        return BindingBuilder.bind(sendCustInvParamsToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Binding sendCUuidToCMSQueueBinding(){
//        return BindingBuilder.bind(sendUuidToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendUuidToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CMS_CONFIGURATION_INTIGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCNmsServiceDeleteRequestQueueBinding(){
//        return BindingBuilder.bind(sendServiceDeleteRequestQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendServiceDeleteRequestQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_NMS_SERVICE_DELETE_REQUEST)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendUuidDataToCMSQueueBinding(){
//        return BindingBuilder.bind(sendUuidDataToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendUuidDataToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UUID_DATA_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendStatusUpdateToInventoryQueueBinding(){
//        return BindingBuilder.bind(sendStatusUpdateToInventoryQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendStatusUpdateToInventoryQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CMS_UPDATE_STATUS_INVENTORY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Queue sendCreditDebitDocToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CREDIT_DEBIT_DOC_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreditDebitDocToCMSQueueBinding(){
//        return BindingBuilder.bind(sendCreditDebitDocToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**send socket message to common queue binding started**/
//    @Bean
//    public Queue SendSocketMessageToCommonQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_SOCKET_MESSAGE_TO_COMMON).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendSocketMessageToCommonQueueBinding() {
//        return BindingBuilder.bind(SendSocketMessageToCommonQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendCAFTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CAF_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCAFTATMessageBindings() {
//        return BindingBuilder.bind(sendCAFTATMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendTerminationTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TREMINATION_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendsendTerminationTATMessageQueueTATMessageBindings() {
//        return BindingBuilder.bind(sendTerminationTATMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendLEADTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendLEADTATMessageBindings() {
//        return BindingBuilder.bind(sendLEADTATMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMvnoDocSaveMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_DOC_SAVE_FROM_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoDocSaveMessageQueueBindings() {
//        return BindingBuilder.bind(sendMvnoDocSaveMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMvnoDocUpdateMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_DOC_UPDATE_FROM_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoDocUpdateMessageQueueBindings() {
//        return BindingBuilder.bind(sendMvnoDocSaveMessageQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    /**send socket message to common queue binding ended**/
//    /**send mvno document dunning  queue binding started**/
//    @Bean
//    public Queue sendMvnoDocumentDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoDocumentDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoDocumentDunningNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendMvnoStatusDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_STATUS_DUNNING_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoStatusDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoStatusDunningNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendStaffStatusDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_STAFF_STATUS_DUNNING_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendStaffStatusDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendStaffStatusDunningNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerStatusDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_DUNNING_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerStatusDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendCustomerStatusDunningNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerIpToRadiusUpdateQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_IP_TO_UPDATE_RADIUS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerIpToRadiusUpdateQueueBindings() {
//        return BindingBuilder.bind(sendCustomerIpToRadiusUpdateQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerIpToRadiusSaveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_IP_TO_SAVE_RADIUS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerIpToRadiusSaveQueueBindings() {
//        return BindingBuilder.bind(sendCustomerIpToRadiusSaveQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerIpToRadiusDeleteQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_IP_TO_DELETE_RADIUS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerIpToRadiusDeleteQueueBindings() {
//        return BindingBuilder.bind(sendCustomerIpToRadiusDeleteQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    /**send mvno Deactivation **/
//    @Bean
//    public Queue sendMvnoExpireNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoDeactivationNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoExpireNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//    /**send mvno Deactivation End **/
//
//    @Bean
//    public Queue sendCustomerStatusInactiveDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_INACTIVE_DUNNING_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerStatusInactiveDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendCustomerStatusInactiveDunningNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
// /**send mvno Payment **/
//    @Bean
//    public Queue sendMvnoPaymentAdvanceNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding sendMvnoPaymentNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoPaymentAdvanceNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//
//    @Bean
//    public Queue sendBudPayPaymentStatusQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_BUDPAY_PAYMENT_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendBudPayPaymentStatusQueueBindings() {
//        return BindingBuilder.bind(sendBudPayPaymentStatusQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMvnoPaymentExpiryNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding sendMvnoPaymentExpiryNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoPaymentExpiryNotificationQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendMVNOSharedDataQueueCMSForISP() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS_ISP)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendMVNOSharedDataBindingCMSISP() {
//        return BindingBuilder.bind(sendMVNOSharedDataQueueCMSForISP()).to(adoptExchange()).withQueueName();
//    }
//        /**send mvno Payment End **/
//        @Bean
//        public Queue sendCustPlanStatusRadius() {
//            return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUST_PLAN_DETAIL_FROM_RADIUS)
//                    .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                    .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                    .build();
//        }
//
//    @Bean
//    public Binding sendCustPlanStatusRadiusBinding() {
//        return BindingBuilder.bind(sendCustPlanStatusRadius()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPlanExpiryNotification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PLAN_EXPIRY_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanExpiryNotificationBinding() {
//        return BindingBuilder.bind(sendPlanExpiryNotification()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendCustomerStatusToRadius() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_STATUS_UPDATE_RADIUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCustomerStatusToRadiusBinding() {
//        return BindingBuilder.bind(sendCustomerStatusToRadius()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBudPayPaymentcreditQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_BUD_PAYMENT_CREDIT_TO_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendBudPayPaymentcreditQueueBindings() {
//        return BindingBuilder.bind(sendBudPayPaymentcreditQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBusinessUnitSaveSharedDataQueueCMS() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBusinessUnitSaveSharedDataBindingCMS() {
//        return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueCMS()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendBusinessUnitUpdatedSharedDataQueueCMS() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBusinessUnitUpdatedSharedDataBindingCMS() {
//        return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueCMS()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue AddMacFromAPIGTQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING_CMS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding AddMacFromAPIGTBinding() {
//        return BindingBuilder.bind(AddMacFromAPIGTQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMVNODiscountQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_DISCOUNT_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendMVNODiscountQueueBinding() {
//        return BindingBuilder.bind(sendMVNODiscountQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendCreateCustomer() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCreateCustomerBinding() {
//        return BindingBuilder.bind(sendCreateCustomer()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendUpdateCustomer() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_API_PARTNER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendUpdateCustomerBinding() {
//        return BindingBuilder.bind(sendUpdateCustomer()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBudPayChangePlanMessageToRevenue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_BUDPAY_CUSTOMER_CWSC_CHANGE_PLAN_TO_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendBudPayChangePlanMessageToRevenueBinding() {
//        return BindingBuilder.bind(sendBudPayChangePlanMessageToRevenue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendVendorSavedDataToCMS() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_SAVE_VENDOR_QUEUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendVendorSavedDataToCMSBinding() {
//        return BindingBuilder.bind(sendVendorSavedDataToCMS()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendVendorUpdatedDataToCMS() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_VENDOR_QUEUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendVendorUpdatedDataToCMSBinding() {
//        return BindingBuilder.bind(sendVendorUpdatedDataToCMS()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPaymentAuditToCmsQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendPaymentAuditToCmsQueueBinding(){
//        return BindingBuilder.bind(sendPaymentAuditToCmsQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPaymentAuditToIntegrationQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_INTEGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendPaymentAuditToIntegrationQueueBinding(){
//        return BindingBuilder.bind(sendPaymentAuditToIntegrationQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendCustMappingStatusUpdateToCMS() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_MAPPING_STATUS_UPDATE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustMappingStatusUpdateToCMSQueueBind() {
//        return BindingBuilder.bind(sendCustMappingStatusUpdateToCMS()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendLocationToCMS() {
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_LOCATION_TO_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendLocationToCMSToCMSQueueBind() {
//        return BindingBuilder.bind(sendLocationToCMS()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendLocationServiceareamappingQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA_LOCATION_MAPPING)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendLocationServiceareamappingQueueQueueBindings() {
//        return BindingBuilder.bind(sendLocationServiceareamappingQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendOTPProfileToCommonQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendOTPProfileToCommonBinding(){
//        return BindingBuilder.bind(sendOTPProfileToCommonQueue()).to(adoptExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendOTPProfileUpdateToCommonQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON_UPDATE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendOTPProfileUpdateToCommonBinding(){
//        return BindingBuilder.bind(sendOTPProfileUpdateToCommonQueue()).to(adoptExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendOTPProfileToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_PROFILE_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendOTPProfileToCMSBinding(){
//        return BindingBuilder.bind(sendOTPProfileToCMSQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendDirectChargeListQueue(){
//        return QueueBuilder.durable(SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_List_SHARE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendDirectChargeListQueueBinding(){
//        return BindingBuilder.bind(sendDirectChargeListQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCprUpdateToCmsListQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CPR_UPDATE_FROM_REVENUE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCprUpdateToCmsListQueueQueueBinding(){
//        return BindingBuilder.bind(sendCprUpdateToCmsListQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendInvoiceNumberUpdateToCMSListQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVOICE_NUMBER_UPDATE_FROM_REVENUE_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendInvoiceNumberUpdateToCMSListQueueBinding(){
//        return BindingBuilder.bind(sendInvoiceNumberUpdateToCMSListQueue()).to(adoptExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerStatusToRevenue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_STATUS_UPDATE_REVENUE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCustomerStatusToRevenueBinding() {
//        return BindingBuilder.bind(sendCustomerStatusToRevenue()).to(adoptExchange()).withQueueName();
//    }
}
