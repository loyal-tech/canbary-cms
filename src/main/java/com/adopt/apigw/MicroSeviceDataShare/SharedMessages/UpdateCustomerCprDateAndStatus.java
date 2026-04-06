package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UpdateCustomerCprDateAndStatus {
    private Integer id;
    private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();

    public UpdateCustomerCprDateAndStatus(Integer id, List<CustPlanMappping> custPlanMapppingList) {
        this.id = id;
        for (CustPlanMappping planMappping: custPlanMapppingList) {
            CustPlanMapppingPojo custPlanMapppingPojo = new CustPlanMapppingPojo();
            custPlanMapppingPojo.setId(planMappping.getId());
            custPlanMapppingPojo.setStartDateString(planMappping.getStartDate().toString());
            custPlanMapppingPojo.setEndDateString(planMappping.getEndDate().toString());
            if(planMappping.getExpiryDate() != null)
                custPlanMapppingPojo.setExpiryDateString(planMappping.getExpiryDate().toString());
            custPlanMapppingPojo.setCustPlanStatus(planMappping.getCustPlanStatus());
            custPlanMapppingPojo.setIstrialplan(planMappping.getIstrialplan());
            custPlanMapppingPojo.setTrialPlanValidityCount(planMappping.getTrialPlanValidityCount());
            custPlanMapppingPojo.setIsTrialValidityDays(planMappping.getIsTrialValidityDays());
            custPlanMapppingPojo.setTrailPlanFromToday(planMappping.isTrailPlanFromToday());
            custPlanMapppingPojo.setTrailPlanFromTrailDay(planMappping.isTrailPlanFromTrailDay());
            this.custPlanMapppingList.add(custPlanMapppingPojo);
        }
    }
}
