package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerDetailsDTO {
    private Integer custId;
    private String username;
    private Integer id;
    private Integer mvnoId;
    private String email;
    private Long buId;
    private Boolean renewalForBooster;
    private String phone;

    // Constructor matching the SELECT order
    public CustomerDetailsDTO( String username, Integer id, Integer mvnoId,
                              String email, Long buId, Boolean renewalForBooster, String phone) {
        this.username = username;
        this.id = id;
        this.mvnoId = mvnoId;
        this.email = email;
        this.buId = buId;
        this.renewalForBooster = renewalForBooster;
        this.phone = phone;
    }

}
