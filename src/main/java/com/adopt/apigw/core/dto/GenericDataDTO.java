package com.adopt.apigw.core.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class GenericDataDTO {


    private int responseCode;
    private String responseMessage;
    private Object data;
    private List dataList;
    private List excelDataList;
    private long totalRecords = 0;
    private long pageRecords = 0;
    private long currentPageNumber;
    private long totalPages;

    public static GenericDataDTO getGenericDataDTO(List entityList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setDataList(entityList);
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        if (null == entityList) {
            genericDataDTO.setTotalRecords(0);
        } else {
            genericDataDTO.setTotalRecords(entityList.size());
        }
        genericDataDTO.setResponseMessage("Success");
        return genericDataDTO;
    }
}
