package com.adopt.apigw.model.radius;

import lombok.Data;

import java.sql.Date;

import com.adopt.apigw.core.dto.PaginationRequestDTO;

@Data
public class DbCDRRequestDTO extends PaginationRequestDTO  {

    private Date startDate;
    private Date endDate;
    private String username;
    private String requestType;
}
