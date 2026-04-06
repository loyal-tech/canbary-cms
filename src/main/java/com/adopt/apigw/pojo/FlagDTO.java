package com.adopt.apigw.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlagDTO {

    private Boolean discount;
    private List<Integer> oldCustPlanMappingIds;
    private Boolean isFuturePlan;
    private Boolean isCafNewService;
}
