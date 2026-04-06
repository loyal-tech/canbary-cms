package com.adopt.apigw.modules.Branch.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import com.adopt.apigw.modules.BranchService.model.BranchServiceMappingEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.spring.security.AuditableListener;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmbranch")
public class Branch implements IBaseData<Long>{

 	@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branchid")
    private Long id;

    private String name;

    private String status;

    @Column(name = "branch_code",length = 40)
    private String branch_code;
    
    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tblmbranchservicearearel", joinColumns = @JoinColumn(name = "branchid"), inverseJoinColumns = @JoinColumn(name = "servicearea_id"))
	@ToString.Exclude
	@LazyCollection(LazyCollectionOption.FALSE)
    private Set<ServiceArea> serviceAreaNameList = new HashSet<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "revenue_sharing", length = 40)
    private Boolean revenue_sharing;

    @Column(name = "sharing_percentage", length = 40)
    private Double sharing_percentage;

    @Column(name = "dunning_days")
    private String dunningDays;

    @OneToMany(targetEntity = BranchServiceMappingEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "branch_mapping_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    List<BranchServiceMappingEntity> branchServiceMappingEntityList;

    public Branch(Long id) {
        this.id = id;
    }

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
}
