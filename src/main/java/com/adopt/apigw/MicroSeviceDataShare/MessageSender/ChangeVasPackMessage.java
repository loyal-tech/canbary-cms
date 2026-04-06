package com.adopt.apigw.MicroSeviceDataShare.MessageSender;

import com.adopt.apigw.model.postpaid.CustPlanMappping;
import lombok.Data;

import java.util.List;

@Data
public class ChangeVasPackMessage {

    VasPackDTO newVasPackdto;
    List<Integer> oldVasPackId;
}
