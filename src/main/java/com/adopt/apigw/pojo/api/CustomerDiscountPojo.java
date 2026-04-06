package com.adopt.apigw.pojo.api;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class CustomerDiscountPojo {

    private List<Integer> custServiceIds;

    private double discount;

    private String discountExpiryDateStr;

    private String newDiscountType;

    public CustomerDiscountPojo(List<Integer> custServiceIds, double discount, String discountExpiryDateStr, String newDiscountType) {
        this.custServiceIds = custServiceIds;
        this.discount = discount;
        this.discountExpiryDateStr = discountExpiryDateStr;
        this.newDiscountType = newDiscountType;
    }
}
