package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveUpdateVendorMessage {


    private Long id;

    private String name;

    private String status;

    private boolean isDeleted;


    private Integer mvnoId;
}
