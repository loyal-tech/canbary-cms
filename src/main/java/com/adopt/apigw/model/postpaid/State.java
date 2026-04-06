package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.pojo.api.StatePojo;
import com.adopt.apigw.service.postpaid.CountryService;
import com.adopt.apigw.service.postpaid.StateService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLMSTATE")
//@EntityListeners(AuditableListener.class)
public class State  {

	
	
	/*
	CREATE TABLE TBLMSTATE
  (
    STATEID   serial,
    NAME      VARCHAR(64) NOT NULL,
    COUNTRYID NUMERIC(20),
    STATUS    CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_MSTATE PRIMARY KEY (STATEID)
  );  
	 */

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STATEID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "COUNTRYID")
    @ToString.Exclude
    private Country country;

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "state")
    @EqualsAndHashCode.Exclude
    private List<City> cityList = new ArrayList<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    
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


    public State() {
    }
    public State(StatePojo state,Integer id) throws Exception {
        CountryService stateService = SpringContext.getBean(CountryService.class);
        this.id=state.getId();
        this.name=state.getName();
        this.status=state.getStatus();
        this.country  = stateService.convertCountryPojoToCountryModel(state.getCountryPojo());;
    }
}
