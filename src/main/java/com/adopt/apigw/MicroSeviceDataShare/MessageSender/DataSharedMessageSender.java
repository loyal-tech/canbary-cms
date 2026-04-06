package com.adopt.apigw.MicroSeviceDataShare.MessageSender;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.*;
import com.adopt.apigw.modules.ChangePlanDTOs.ChangePlanMessage;
import com.adopt.apigw.modules.ChangePlanDTOs.ChangePlanNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.SavePricebookSharedMessage;
import com.adopt.apigw.rabbitMq.message.SaveVoucherBatchSharedDataMessage;
import com.adopt.apigw.rabbitMq.message.ServiceTerminationMessage;
import com.adopt.apigw.rabbitMq.message.UpdatePricebookSharedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataSharedMessageSender {
/*
    private static Logger log = LoggerFactory.getLogger(DataSharedMessageSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;


    //Send Saved Entity Data

    public String send(SaveCountrySharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }
    public String send(SaveStateSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveCitySharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SavePincodeSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveAreaSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveServiceAreaSharedDataMessge message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveBusinessUnitSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveBranchSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }
    public String send(SaveMvnoSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveRoleSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveStaffUserSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveServicesSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }
    public String send(SaveHierarchyShareDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }
    public String send(SaveRegionSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }


    public String send(SaveBusinessVerticalSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveCustomerDataShareMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }


    public String send(SavePartnerSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveTaxSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SavePlanSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SavePlanGroupSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveChargeSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }
    //Send Update/Delete Entity Data


    public String send(UpdateCountrySharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }
    public String send(UpdateStateSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateCitySharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdatePincodeSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateAreaSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateServiceAreaSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateBusinessUnitSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateBranchSharedData message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

//Teams

    public String send(SaveTeamsSharedSharedData message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateTeamsSharedData message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateHierarchyShareDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }


    public String send(UpdateMvnoSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateRoleSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateStaffUserSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateServicesSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateRegionSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }


    public String send(UpdateBusinessVerticalSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }


    public String send(UpdateCustomerShareDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdatePartnerSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateTaxSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdatePlanSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdatePlanGroupSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateChargeSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveClientServMessge message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }
    public String send(UpdateClientServMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateDiscountSharedMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveDiscountSharedMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }


    public String send(ChangePlanMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateCustomerCprDateAndStatus message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    //    public String send(UpdateCustomerCprDateAndStatus message, String queueName) {
    //        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
    //        log.info("Send msg  " + message);
    //        return "Message Published";
    //    }
    public String send(SavePricebookSharedMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }
    public String send(UpdatePricebookSharedMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveCasMasterSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(UpdateCasMasterSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

    public String send(SaveVoucherBatchSharedDataMessage message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }


    public String send(ChangePlanNotification message, String queueName) {
        rabbitTemplate.convertAndSend(RabbitMqConstants.ADOPT_EXCHANGE, queueName, message);
        log.info("Send msg  " + message);
        return "Message Published";
    }

 */
}
