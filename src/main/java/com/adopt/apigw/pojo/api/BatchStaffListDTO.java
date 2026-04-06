package com.adopt.apigw.pojo.api;

import lombok.Data;

@Data
public class BatchStaffListDTO {
    Integer staffId;
    String username;
    String firstName;
    String lastName;
    String fullName;
}
