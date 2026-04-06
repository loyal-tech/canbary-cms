package com.adopt.apigw.modules.InventoryManagement.warehouse;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.Teams.model.TeamsDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WareHouseDto implements IBaseDto {
    Long id;
    String name;
    String description;
    String status;
    String address1;
    String address2;
    String pincode;
    String city;
    String state;
    String country;
    String longitude;
    String latitude;
    Integer mvnoId;
    List<Long> serviceAreaIdsList;
    List<ServiceAreaDTO> serviceAreaNameList = new ArrayList<>();
    List<Long> parentServiceAreaIdsList;
    List<ServiceAreaDTO> parenetServiceAreaNameList = new ArrayList<>();
    String warehouseType;
    private String rmsWarehouseId;
    private String navWarehouseId;
    private Long branchId;
    private List<TeamsDTO> teamsList =  new ArrayList<>();
    private List<TeamsDTO> teamsDTOList = new ArrayList<>();
    private List<Long> teamsIdsList = new ArrayList<>();
    private String warehouseCode;
    @Override
    public Long getIdentityKey() {
        return id;
    }
    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }
}
