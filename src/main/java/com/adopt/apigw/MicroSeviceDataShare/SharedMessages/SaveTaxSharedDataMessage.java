package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.postpaid.TaxTypeSlab;
import com.adopt.apigw.model.postpaid.TaxTypeTier;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class SaveTaxSharedDataMessage {
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
