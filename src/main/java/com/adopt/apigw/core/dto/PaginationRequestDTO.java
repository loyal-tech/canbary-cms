package com.adopt.apigw.core.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationRequestDTO {
    private Integer page;
    private Integer pageSize;
    private Integer sortOrder;
    private String sortBy;
    private List<GenericSearchModel> filters = new ArrayList<>();
    private String status;
    private String filterBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;
}
