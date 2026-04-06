package com.adopt.apigw.model.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.spring.security.AuditableListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "tblmbranchservicearearel")
@Data
@NoArgsConstructor
public class BranchServiceAreaMapping implements IBaseData<Long> {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branchid", nullable = false, length = 40)
    private Integer branchId;

    @Column(name = "servicearea_id", nullable = false, length = 40)
    private Integer serviceareaId;

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

