package com.adopt.apigw.modules.BusinessVerticals.domain;

import com.adopt.apigw.core.data.IBaseData;
//import com.adopt.apigw.modules.BusinessUnit.domain.BusinessRegion;
import com.adopt.apigw.modules.Region.domain.Region;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltbusinessverticalsmapping")
public class BusinessVerticalsMapping implements IBaseData<Long>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(targetEntity = BusinessVerticals.class)
    @JoinColumn(name = "buverticalsid", referencedColumnName = "bu_verticals_id", updatable = true, insertable = true)
    private BusinessVerticals businessVerticals;

    @ManyToOne(targetEntity = Region.class)
    @JoinColumn(name = "region_id", referencedColumnName = "region_id", updatable = true, insertable = true)
    private Region region;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Override
    public Long getPrimaryKey() { return id; }

    @Override
    public void setDeleteFlag(boolean deleteFlag) { this.isDeleted = deleteFlag; }

    @Override
    public boolean getDeleteFlag()  {
        return isDeleted;
    }
}
