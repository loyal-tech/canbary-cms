package com.adopt.apigw.modules.BusinessVerticals.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
//import com.adopt.apigw.modules.BusinessUnit.domain.BusinessRegion;
import com.adopt.apigw.modules.Region.domain.Region;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmbusinessverticals")
@EntityListeners(AuditableListener.class)
public class BusinessVerticals extends Auditable implements IBaseData<Long> {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bu_verticals_id")
    private Long id;

    private String vname;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbltbusinessverticalsmapping", joinColumns = {@JoinColumn(name = "buverticalsid")}, inverseJoinColumns = {@JoinColumn(name = "region_id")})
    private List<Region> buregionidList = new ArrayList<>();

    private String status;

   @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
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

