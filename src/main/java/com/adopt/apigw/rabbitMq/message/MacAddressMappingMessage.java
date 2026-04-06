package com.adopt.apigw.rabbitMq.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
public class MacAddressMappingMessage {

    private List<HashMap<String, Object>> macAddress;
    private Long mvnoId;
    private boolean isUpdate;
    private boolean isDelete;

    public MacAddressMappingMessage(List<HashMap<String, Object>> macAddress, Long mvnoId, boolean isUpdate, boolean isDelete) {
        this.macAddress = macAddress;
        this.isUpdate = isUpdate;
        this.isDelete = isDelete;
        this.mvnoId = mvnoId;
    }
}
