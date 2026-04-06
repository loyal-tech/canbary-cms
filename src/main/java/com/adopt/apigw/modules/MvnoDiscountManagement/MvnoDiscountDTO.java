package com.adopt.apigw.modules.MvnoDiscountManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MvnoDiscountDTO {

    private List<MvnoDiscountMappingDTO> mvnoDiscountMappings;

    private Long mvnoId;
}
