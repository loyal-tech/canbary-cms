package com.adopt.apigw.modules.Area.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblmarea")
@NoArgsConstructor
@AllArgsConstructor
//@EntityListeners(AuditableListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Area implements IBaseData<Long> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "areaid")
    private Long id;

    private String name;
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

    @JsonBackReference
    @ManyToOne()
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "pincodeid")
    @DiffIgnore
    private Pincode pincode;

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


    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
    public Area(Long id){
        this.id= id;
    }

    public Area(Long id, String name, Long pincodeId ) {
        this.id = id;
        this.name = name;
        this.pincode = new Pincode();
        this.pincode.setId(pincodeId);
    }
}
