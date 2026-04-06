package com.adopt.apigw.modules.Region.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Data
public class RegionDTO extends Auditable implements IBaseDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rname;

    private String status;

    private List<Long> branchid;

    private Boolean isDeleted = false;

    private Integer mvnoId;


    @Override
    public Long getIdentityKey() {
        return  id;
    }

    @Override
    public Integer getMvnoId() {
        // TODO Auto-generated method stub
        return mvnoId;
    }
}