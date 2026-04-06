package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class AddonEligibilityRequestDTO {
    private Boolean isConvertToAddon;
    private Boolean isValidActivePlan;

    public AddonEligibilityRequestDTO(Boolean isConvertToAddon, Boolean isValidActivePlan) {
        this.isConvertToAddon = isConvertToAddon;
        this.isValidActivePlan = isValidActivePlan;
    }
}
