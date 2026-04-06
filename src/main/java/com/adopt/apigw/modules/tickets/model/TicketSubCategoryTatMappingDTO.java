package com.adopt.apigw.modules.tickets.model;

import com.adopt.apigw.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.adopt.apigw.modules.tickets.domain.TatQueryFieldMapping;

import java.util.List;

public class TicketSubCategoryTatMappingDTO {

    Long id;

    private Long ticketReasonSubCategoryId;

    private Long ticketTatMatrixId;

    private Boolean isDeleted = false;

    private Long orderid;

    private List<TatQueryFieldMapping> tatQueryFieldMappingList;

}
