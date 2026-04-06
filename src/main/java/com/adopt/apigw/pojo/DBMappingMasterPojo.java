package com.adopt.apigw.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DBMappingMasterPojo {
    private Integer id;
    private String name;
    private LocalDateTime createdate;
    private LocalDateTime updatedate;
    private List<Integer> dbMappingIdsList;
}
