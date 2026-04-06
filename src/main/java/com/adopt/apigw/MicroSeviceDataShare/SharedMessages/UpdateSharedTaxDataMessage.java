package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.postpaid.TaxTypeSlab;
import com.adopt.apigw.model.postpaid.TaxTypeTier;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UpdateSharedTaxDataMessage {
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String desc;

    @NotNull
    private String taxtype;

    private String status;
    private Integer mvnoId;
    private Long buId;
    // private String ledgerId;
    private Boolean isDelete = false;
    private List<TaxTypeSlab> slabList = new ArrayList<>();

    private List<TaxTypeTier> tieredList = new ArrayList<>();
}
