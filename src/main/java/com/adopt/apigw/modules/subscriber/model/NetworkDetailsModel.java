package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import com.adopt.apigw.modules.NetworkDevices.domain.OLTPortDetails;
import com.adopt.apigw.modules.NetworkDevices.model.NetworkDeviceDTO;
import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.OLTPortDTO;
import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.OLTSlotDetailDTO;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.ippool.model.IPPoolDTO;

@Data
public class NetworkDetailsModel {
    private List<CommonListDTO> networkType;
    private List<CommonListDTO> serviceType;
    private List<IPPoolDTO> defaultPool;
    private List<ServiceAreaDTO> serviceArea;
    private IPPoolDTO selectedDefaultIpPool;
    private ServiceAreaDTO selectedServiceArea;
    private NetworkDeviceDTO selectedNetworkDeviceDTO;
    private OLTSlotDetailDTO selectedOltSlotDetailDTO;
    private OLTPortDTO selectedOltPortDetailsDTO;
    private String selectedNetworkType;
    private String selectedOnuId;
    private String selectedConnectionType;
    private String selectedServiceType;
    private String remarks;
}
