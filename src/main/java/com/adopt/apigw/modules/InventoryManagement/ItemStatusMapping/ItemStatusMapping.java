package com.adopt.apigw.modules.InventoryManagement.ItemStatusMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbltitemstatusmapping")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemStatusMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "item_status")
    private String itemStatus;

    @Column(name = "customerid")
    private Long customerId;


    @Column(name = "bulkconsumptionid")
    private Long BulkConsumptionId;

    @Column(name = "serviceareaid")
    private Long serviceAreaId;
    @Column(name = "popid")
    private Long popId;


    @Column(name = "itemid")
    private Long itemId;

    @Column(name = "event")
    private String event;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Transient
    private String condition;
    @Transient
    private String macAddress;
    @Transient
    private String serialNumber;
    @Transient
    private String externalItemGroupNumber;
    @Transient
    private String approvalRemark;
    @Transient
    private String postPaidPlanName;
    @Transient
    private String serviceName;
    @Transient
    private String connectionNo;
    @Transient
    private String billTo;
    @Transient
    private Boolean isInvoiceToOrg;
    @Transient
    private Boolean isRequiredApproval;
}
