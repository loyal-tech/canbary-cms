package com.adopt.apigw.model.postpaid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbltdebitdocumentinventoryrel")
public class DebitDocumentInventoryRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debitdocinvid")
    private Integer debitdocinvid;

    @Column(name = "debitdocumentid")
    private Integer debitdocumentid;

    @Column(name = "cust_inventory_mapping_id")
    private Long custInventoryMappingId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "price")
    private Double price;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name="assigned_date")
    private LocalDateTime assignedDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name="expiray_date")
    private LocalDateTime expirayDate;


    @Column(name = "product_type")
    private String productType;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_serial_number")
    private String itemSerialNumber;

    @Column(name = "item_mac")
    private String itemMac;

    @Column(name = "connection_no")
    private String connectionNo;

}
