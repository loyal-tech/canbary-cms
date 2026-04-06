package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.pojo.api.CountryPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateMessage {

    private Integer id;

    private String name;

    private String status;

    private Country country;

    private Boolean isDeleted;

    private Integer mvnoId;

    public StateMessage(State obj) {
        this.id = obj.getId();
        this.name = obj.getName();
        this.status=obj.getStatus();
        this.country=obj.getCountry();
        this.isDeleted=obj.getIsDeleted();
        this.mvnoId=obj.getMvnoId();
    }
}
