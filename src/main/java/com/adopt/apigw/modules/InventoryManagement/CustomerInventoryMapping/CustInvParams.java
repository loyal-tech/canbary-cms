package com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name="tblmcustinventoryparams")
public class CustInvParams {

     @Id
     @Column(name = "id")
     private Long id;

     @Column(name = "param_name")
     private String paramName;

     @Column(name = "param_value")
     private String paramValue;

	 @Column(name = "cust_id")
     private Long custId;

     @Column(name="cust_serv_id")
     private Long custSerMapId;

     @Column(name="cust_inv_id")
     private Long custInvId;

}
