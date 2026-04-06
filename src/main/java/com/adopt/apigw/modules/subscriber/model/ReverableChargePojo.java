package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.adopt.apigw.model.postpaid.CustChargeDetails;
import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import com.adopt.apigw.pojo.api.CustChargeDetailsPojo;

@Data
public class ReverableChargePojo {
    List<CustChargeDetailsPojo> custChargeDetailsList;
    List<CommonListDTO> reversalTypeCommonList;
}
