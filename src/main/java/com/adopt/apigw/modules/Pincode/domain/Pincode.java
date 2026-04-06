package com.adopt.apigw.modules.Pincode.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Data
@Entity
@Table(name = "tblmpincode")
//@EntityListeners(AuditableListener.class)
public class Pincode implements IBaseData<Long> {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pincodeid", nullable = false, length = 40)
    private Long id;

    private String pincode;
    private String status;
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "COUNTRYID", nullable = false, length = 40)
    private Integer countryId;

    @DiffIgnore
    @Column(name = "CITYID", nullable = false, length = 40)
    private Integer cityId;

    @DiffIgnore
    @Column(name = "STATEID", nullable = false, length = 40)
    private Integer stateId;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pincode")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    @DiffIgnore
    private List<Area> areaList = new ArrayList<>();

    @DiffIgnore
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
    @DiffIgnore
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    @DiffIgnore
    private String lastModifiedByName;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
}
