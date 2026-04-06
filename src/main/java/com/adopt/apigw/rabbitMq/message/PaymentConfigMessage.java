package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.PaymentConfig.model.PaymentConfigDTO;
import com.adopt.apigw.modules.PaymentConfig.model.SendPaymentConfigDTO;
import lombok.Data;

@Data
public class PaymentConfigMessage {

      SendPaymentConfigDTO paymentConfigDTO;
      String flag;
}
