package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Auditable2;
import com.adopt.apigw.pojo.api.CountryPojo;
import com.adopt.apigw.spring.security.AuditableListener2;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLMCOUNTRY")
public class Country extends Auditable {
	
	
	/*
	 CREATE TABLE TBLMCOUNTRY
  (
    COUNTRYID serial,
    NAME      VARCHAR(64) NOT NULL,
    STATUS    CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_MCOUNTRY PRIMARY KEY (COUNTRYID)
  );
  
	 */

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COUNTRYID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @DiffIgnore
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "country")
    private List<State> stateList = new ArrayList<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    /* @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById; */

    @ApiModelProperty(hidden = true)
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    /* @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    //@DiffIgnore
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    //@DiffIgnore
    private String lastModifiedByName;*/

    public Country(CountryPojo pojo, Integer id) {
        this.id = pojo.getId();
        this.name = pojo.getName();
        this.isDelete = pojo.getIsDelete();
        this.status = pojo.getStatus();

    }

    public Country() {
    }

    public Country(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
//    @Column(name = "MVNOID", nullable = false, length = 40)
//    private Integer mvnoId;
}
