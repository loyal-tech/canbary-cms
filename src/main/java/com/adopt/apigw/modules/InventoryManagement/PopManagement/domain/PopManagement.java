package com.adopt.apigw.modules.InventoryManagement.PopManagement.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmpopmanagement")
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class PopManagement extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pop_id")
    private Long id;

    @Column(name = "pop_name", nullable = false)
    private String popName;

    @Column(name = "latitude", nullable = false)
    private String latitude;

    @Column(name = "longitude", nullable = false)
    private String longitude;
    @Column(name = "pop_code")
    private String popCode;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblpopmanagemengservicearearel", joinColumns = {@JoinColumn(name = "pop_id")}
            , inverseJoinColumns = {@JoinColumn(name = "servicearea_id")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "mvno_id", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    //private transient String serviceareaName;

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

	public PopManagement(Long id) {
		this.id = id;
	}
}
