package com.adopt.apigw.pojo.api;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import com.adopt.apigw.model.common.Auditable;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaxPojo extends Auditable {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    @Length(max = 150, message = "The field must be less than 150 characters")
    private String desc;

    @NotNull
    private String taxtype;
    @NotNull
    private String status;
    private Boolean isDelete = false;

    private List<TaxTypeTierPojo> tieredList = new ArrayList<>();

    private List<TaxTypeSlabPojo> slabList = new ArrayList<>();
    
    private Integer mvnoId;

    private Long buId;

    private String mvnoName;


    @Override
    public String toString() {
        return "TaxPojo [id=" + id + ", name=" + name + ", desc=" + desc + ", taxType=" + taxtype + ", status=" + status
                + ", tieredList=" + tieredList + ", slabList=" + slabList + "]";
    }

}
