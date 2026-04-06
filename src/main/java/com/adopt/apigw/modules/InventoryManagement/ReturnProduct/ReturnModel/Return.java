package com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnModel;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_return_inventory")
@EntityListeners(AuditableListener.class)

public class Return extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @Column(name = "product_name",length = 40)
    private String product_name;

    @Column(name = "mac_name",length = 40)
    private String mac_name;

    @Column(name = "serial_no",length = 40)
    private String serial_no;

    @Column(name = "item_condition",length = 40)
    private String item_condition;

    @Column(name = "mvno_id",length = 40)
    private Integer mvnoId;

    @Column(name = "product_id",length = 40)
    private Long product_id;

    @Column(name = "current_inward_id",length = 40)
    private Long current_inward_id;

    @Column(name = "current_inward_type",length = 40)
    private String current_inward_type;

    @Column(name = "item_status",length = 40)
    private String item_status;

    @Column(name = "cust_id",length = 40)
    private Long cust_id;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
