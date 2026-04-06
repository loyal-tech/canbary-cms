package com.adopt.apigw.modules.SubArea.DTO;


import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubAreaDTO extends Auditable implements IBaseDto {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted;
    private Integer countryId;
    private Integer cityId;
    private Integer stateId;
    private Integer mvnoId;
    private Long buId;
    private Long areaId;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }
}
