package com.adopt.apigw.modules.MvnoDiscountManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MvnoDiscountMappingDTO {

    private Long id;
    
    private double discount;
    
    private Long mvnoId;
    
    private Long countFrom;

    private Long countTo;

    private Long chargeId;

    private String lastModifiedByName;

    private String createdByName;

    private Integer createdById;

    private Integer lastModifiedById;

    private String chargeName;

}
