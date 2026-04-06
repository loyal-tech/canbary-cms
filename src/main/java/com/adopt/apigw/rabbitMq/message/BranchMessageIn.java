package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchMessageIn {
        private Long id;
        private String name;
        private String status;
        private String branch_code;
        private Boolean isDeleted = false;
        private Integer mvnoId;
}
