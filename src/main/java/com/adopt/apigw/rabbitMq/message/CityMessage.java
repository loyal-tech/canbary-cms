package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.model.postpaid.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityMessage {

    private Integer id;

    private String name;

    private String status;

    private Integer countryId;

    private State state;

    private Boolean isDelete;

    private Integer mvnoId;

    public CityMessage(City obj) {
        this.id = obj.getId();
        this.name = obj.getName();
        this.status=obj.getStatus();
        this.countryId=obj.getCountryId();
        this.state=obj.getState();
        this.isDelete=obj.getIsDelete();
        this.mvnoId=obj.getMvnoId();
    }

}
