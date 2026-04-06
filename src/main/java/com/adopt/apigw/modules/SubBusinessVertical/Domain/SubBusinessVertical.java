package com.adopt.apigw.modules.SubBusinessVertical.Domain;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.BusinessVerticals.domain.BusinessVerticals;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmsubbusinessvertical")
@SQLDelete(sql = "UPDATE tblmsubbusinessvertical SET is_deleted = true WHERE sbvid=?")
@Where(clause = "is_deleted=false")
@EntityListeners(AuditableListener.class)
public class SubBusinessVertical extends Auditable implements IBaseData<Long> {


    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sbvid")
    private Long id;

    @Column(name = "sbvname")
    private String sbvname;

//    @OneToOne
//    @JoinColumn(name = "bu_verticals_id")
    @Column(name = "bu_verticals_id")
    private Integer businessVerticalId ;

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
        this.isDeleted=deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
}

