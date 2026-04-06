package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaIn {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted = false;
    private String latitude;
    private String longitude;
    private Long areaid;
    private Integer mvnoId;
    //private List<Integer> pincodes;
    private Long cityid;
        public ServiceAreaIn(ServiceAreaDTO serviceAreaDTO) {
            this.id = serviceAreaDTO.getId();
            this.name = serviceAreaDTO.getName();
            this.status = serviceAreaDTO.getStatus();
            this.isDeleted = serviceAreaDTO.getIsDeleted();
            this.latitude = serviceAreaDTO.getLatitude();
            this.longitude = serviceAreaDTO.getLongitude();
            // this.areaid=serviceAreaDTO.getAreaId();
            this.mvnoId = serviceAreaDTO.getMvnoId();
            this.cityid = serviceAreaDTO.getCityid();

        }
}
