package com.adopt.apigw.rabbitMq;


import lombok.Data;

@Data
public class UpdateMvnoData {
    private Integer oldmvnoId;
    private Integer newmvnoId;
}
