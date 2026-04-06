package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.Tax;
import com.adopt.apigw.model.postpaid.TaxTypeSlab;
import com.adopt.apigw.model.postpaid.TaxTypeTier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxMessage {


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

    public TaxMessage(Tax obj) {
        this.id = obj.getId();
        this.name = obj.getName();
        this.desc = obj.getDesc();
        this.taxtype = obj.getTaxtype();
        this.status = obj.getStatus();
        this.mvnoId = obj.getMvnoId();
        this.buId = obj.getBuId();
        this.isDelete = obj.getIsDelete();
    }

}
