package com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class CustInvParamsDto {

     private Long id;

     private String paramName;

     private String paramValue;

     private Long custId;

     private Long custSerMapId;

     private Long custInvId;
}
