package com.adopt.apigw.modules.InventoryManagement.RequestInventory;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Setter
@Getter
@EntityListeners(AuditableListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tbltrequestinvenotryproductmapping")
public class RequestInvenotryProductMapping extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="inventory_request_id")
    private Long inventoryRequestId;

    @Column(name="product_category_id")
    private Long productCategoryId;


    @Column(name="product_id")
    private Long productId;

    @Column(name="item_Type")
    private String itemType;

    @Column(name="quantity")
    private Long quantity;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    @Column(name = "requeststatus")
    private String requestStatus;


    @Transient
    private String productName;
    @Transient
    private String productCategoryName;

    @Override
    public String toString() {
        return "RequestInvenotryProductMapping{" +
                "id=" + id +
                ", inventoryRequestId=" + inventoryRequestId +
                ", productCategoryId=" + productCategoryId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", isDeleted=" + isDeleted +
                ", requestStatus='" + requestStatus + '\'' +
                ", productName='" + productName + '\'' +
                ", productCategoryName='" + productCategoryName + '\'' +
                '}';
    }
}
