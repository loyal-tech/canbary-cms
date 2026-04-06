package com.adopt.apigw.modules.CustomerFeedback.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedbackDetailDTO {
    private double rating;
    private String createdDate;
    private String feedbackMessage;
}
