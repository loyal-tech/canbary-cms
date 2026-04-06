package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.pojo.api.CityPojo;
import com.adopt.apigw.service.postpaid.CityService;
import com.adopt.apigw.service.postpaid.StateService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "TBLMCITY")
//@EntityListeners(AuditableListener.class)
public class City  {
	
	
	/*
	CREATE TABLE TBLMCITY
  (
    CITYID  serial,
    NAME    VARCHAR(64) NOT NULL,
    STATEID NUMERIC(20),
    STATUS  CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_MCITY PRIMARY KEY (CITYID)
  );
	 */

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CITYID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "COUNTRYID", nullable = false, length = 40)
    private Integer countryId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "STATEID")
    @ToString.Exclude
    private State state;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    @DiffIgnore
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    @DiffIgnore
    private LocalDateTime updatedate;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    @DiffIgnore
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    @DiffIgnore
    private Integer lastModifiedById;
    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    //@DiffIgnore
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    //@DiffIgnore
    private String lastModifiedByName;

    public City() {
    }
    public City(City pojo,Integer id) {
        this.id=pojo.getId();
        this.name=pojo.getName();
        this.status=pojo.getStatus();
        this.state= pojo.getState();
        this.countryId=pojo.getCountryId();
    }
}
