package com.adopt.apigw.model.common;


import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "TBLTSTAFFUSERRECEIPTMAPPING")
@EntityListeners(AuditableListener.class)
public class StaffUserServiceMapping extends Auditable implements IBaseData2<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "prefix")
    private String prefix;

    @Column(name = "fromreceiptnumber")
    private Long fromreceiptnumber;

    @Column(name = "toreceiptnumber")
    private Long toreceiptnumber;

    @Column(name = "is_active", columnDefinition = "Boolean default false")
    private Boolean isActive = true;
    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "staffmapping_id")
    private int stfmappingId;

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

    @Override
    public void setBuId(Long buId) {
    }
}
