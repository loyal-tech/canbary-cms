package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.modules.Cas.Domain.CasPackageMapping;
import com.adopt.apigw.modules.Cas.Domain.CasParameterMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaveCasMasterSharedDataMessage {
    private Long id;
    private String casname;
    private String status;
    private Long buId;
    private Integer mvnoId;
    private Boolean isDeleted = false;
    private List<CasParameterMapping> casParameterMappings = new ArrayList<>();
    private String endpoint;
    private Integer createdById;
    private Integer lastModifiedById;
}
