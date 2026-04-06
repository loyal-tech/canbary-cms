package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncClientServiceMessage {
    List<SaveClientServMessge> clientServiceList = new ArrayList<SaveClientServMessge>();

    Integer mvnoId;
}
