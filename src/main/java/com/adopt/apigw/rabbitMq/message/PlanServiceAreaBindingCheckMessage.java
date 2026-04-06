package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class PlanServiceAreaBindingCheckMessage {

    List<Long> servicAreaIds = new ArrayList<>();



}
