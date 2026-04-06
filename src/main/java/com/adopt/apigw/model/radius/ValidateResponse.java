package com.adopt.apigw.model.radius;

import lombok.Data;

@Data
public class ValidateResponse {
    private Integer validateCode;
    private String validateMassage;

    public ValidateResponse() {
    }

    public ValidateResponse(Integer validateCode, String validateMassage) {
        this.validateCode = validateCode;
        this.validateMassage = validateMassage;
    }
}
