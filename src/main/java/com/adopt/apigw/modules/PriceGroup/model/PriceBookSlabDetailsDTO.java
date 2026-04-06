package com.adopt.apigw.modules.PriceGroup.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class PriceBookSlabDetailsDTO implements IBaseDto {

    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private PriceBookDTO priceBook;

    private Boolean isDeleted = false;
    private Long fromRange;
    private Long toRange;
    private Double commissionAmount;
    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
