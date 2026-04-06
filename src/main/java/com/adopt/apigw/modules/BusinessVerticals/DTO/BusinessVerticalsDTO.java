package com.adopt.apigw.modules.BusinessVerticals.DTO;
import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.List;

@Data
public class BusinessVerticalsDTO extends Auditable implements IBaseDto {

    private Long id;
    private String vname;
    private List<Long> region_id;
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        // TODO Auto-generated method stub
        return mvnoId;
    }
}
