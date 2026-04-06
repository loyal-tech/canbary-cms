package com.adopt.apigw.modules.Reseller.mapper;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageableResponse<T>{

    private Integer totalPages;
    private Integer currentPage;
    private Long totalRecords;
    private List<T> data;

    /**
     * Convert.
     *
     * @param page the page
     */
    @SuppressWarnings("rawtypes")
    public PageableResponse convert(Page<T> page) {
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber() + 1;
        this.totalRecords = page.getTotalElements();
        this.data = page.getContent();
        return this;
    }

}
