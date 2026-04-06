package com.adopt.apigw.modules.Pincode.model;

import lombok.Data;

import java.util.List;

import com.adopt.apigw.core.dto.GenericRequestDTO;

@Data
public class PincodeDetailDTO {
    private GenericRequestDTO state;
    private GenericRequestDTO city;
    private GenericRequestDTO country;
    private GenericRequestDTO pincode;
    private List<GenericRequestDTO> areaList;

}
