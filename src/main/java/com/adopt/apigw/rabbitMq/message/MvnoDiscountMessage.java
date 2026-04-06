package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.MvnoDiscountManagement.MvnoDiscountDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MvnoDiscountMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String operation;
    private MvnoDiscountDTO mvnoDiscountDTO;

    public MvnoDiscountMessage(String operation, MvnoDiscountDTO mvnoDiscountDTO) {
        this.messageId = String.valueOf(UUID.randomUUID());
        this.message = "Save or update message for mvno discount";
        this.messageDate = new Date();
        this.sourceName = "Common API Gateway";
        this.operation = operation;
        this.mvnoDiscountDTO = mvnoDiscountDTO;
    }
}
