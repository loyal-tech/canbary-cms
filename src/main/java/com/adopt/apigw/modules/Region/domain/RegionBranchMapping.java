package com.adopt.apigw.modules.Region.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.modules.Branch.domain.Branch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltregionbranchmapping")
public class RegionBranchMapping implements IBaseData<Long>
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id ;

   @ToString.Exclude
   @EqualsAndHashCode.Exclude
   @ManyToOne(targetEntity = Region.class)
   @JoinColumn(name = "region_id", referencedColumnName = "region_id", updatable = true, insertable = true)
    private Region regionid;

    @ManyToOne(targetEntity = Branch.class)
    @JoinColumn(name = "branchid", referencedColumnName = "branchid",updatable = true, insertable = true)
    private Branch branchid;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

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
        return isDeleted;
    }
}