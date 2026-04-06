package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.postpaid.TaxTypeSlab;
import com.adopt.apigw.model.postpaid.TaxTypeTier;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateTaxSharedDataMessage {
    private Integer id;
    private String name;
    private String desc;
    private String taxtype;
    private String status;
    private Integer mvnoId;
    private Long buId;
    private List<TaxTypeTier> tieredList;
    private List<TaxTypeSlab> slabList;
    private Boolean isDelete;
    private Integer createdById;
    private Integer lastModifiedById;
}
