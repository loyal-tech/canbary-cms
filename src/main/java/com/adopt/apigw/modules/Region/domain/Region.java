package com.adopt.apigw.modules.Region.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmregion")
@EntityListeners(AuditableListener.class)

public class Region extends Auditable implements IBaseData<Long>{

    @Id
    @Column(name = "region_id")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String rname;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbltregionbranchmapping", joinColumns = {@JoinColumn(name = "region_id")}, inverseJoinColumns = {@JoinColumn(name = "branchid")})
    private List<Branch> branchidList = new ArrayList<>();

    private String status;

    @Column(columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Column(name = "MVNOID")
    private Integer mvnoId;

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
