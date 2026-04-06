package com.adopt.apigw.modules.childcustomer.dto;

import lombok.Data;

import java.util.List;
@Data
public class CustomPageResponse<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}
