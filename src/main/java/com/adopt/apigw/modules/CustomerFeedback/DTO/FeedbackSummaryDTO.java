package com.adopt.apigw.modules.CustomerFeedback.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FeedbackSummaryDTO {
    private String event;
    private double avgRating;
    private List<FeedbackDetailDTO> feedbackDetails;
}
