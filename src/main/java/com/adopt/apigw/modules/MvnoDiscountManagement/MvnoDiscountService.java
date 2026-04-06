package com.adopt.apigw.modules.MvnoDiscountManagement;

import java.util.List;

public interface MvnoDiscountService {

    MvnoDiscountDTO saveMvnoDiscount(MvnoDiscountDTO mvnoDiscountDTO);

    MvnoDiscountDTO updateMvnoDiscount(MvnoDiscountDTO mvnoDiscountDTO);

    boolean deleteAllMvnoDiscountByMvnoId(Long mvnoId);

    List<MvnoDiscountMappingDTO> fetchAllMvnoDiscountDetailByMvnoId(Long mvnoId);
}
