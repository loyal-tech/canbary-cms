package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;

@Data
public class QosPolicyDetailsModel {

    private List<CustomerPlansModel> planList;
    private List<QOSPolicyDTO> qosPolicyList;

}
