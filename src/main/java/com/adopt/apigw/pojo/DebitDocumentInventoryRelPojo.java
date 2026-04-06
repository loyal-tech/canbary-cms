package com.adopt.apigw.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DebitDocumentInventoryRelPojo {

    private Integer debitdocinvid;

    private Integer debitdocumentid;

    private Long custInventoryMappingId;

    private String productName;

    private Double price;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime assignedDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expirayDate;

    private String productType;

    private String itemName;

    private String itemSerialNumber;

    private String itemMac;

    private String connectionNo;

    public DebitDocumentInventoryRelPojo(Integer debitdocinvid, Integer debitdocumentid, Long custInventoryMappingId, String productName, Double price, LocalDateTime assignedDate, LocalDateTime expirayDate, String productType, String itemName, String itemSerialNumber, String itemMac, String connectionNo) {
        this.debitdocinvid = debitdocinvid;
        this.debitdocumentid = debitdocumentid;
        this.custInventoryMappingId = custInventoryMappingId;
        this.productName = productName;
        this.price = price;
        this.assignedDate = assignedDate;
        this.expirayDate = expirayDate;
        this.productType = productType;
        this.itemName = itemName;
        this.itemSerialNumber = itemSerialNumber;
        this.itemMac = itemMac;
        this.connectionNo = connectionNo;
    }
}
