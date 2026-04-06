package com.adopt.apigw.modules.tickets.domain;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@EntityListeners(AuditableListener.class)
@Entity
@Table(name = "tblmticketreasonsubcategory")
public class TicketReasonSubCategory extends Auditable implements IBaseData2<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String subCategoryName;

    /*@ManyToOne(targetEntity = TicketReasonCategory.class)
    @JoinColumn(name = "parent_category_id", nullable = false, referencedColumnName = "id")
    private TicketReasonCategory parentCategory;*/

//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(targetEntity = TicketSubCategoryGroupReasonMapping.class, cascade = CascadeType.ALL)
//    @JoinColumn(name = "ticket_reason_sub_category_id")
//    List<TicketSubCategoryGroupReasonMapping> ticketSubCategoryGroupReasonMappingList;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = TicketSubCategoryReasonCategoryMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_reason_sub_category_id")
    List<TicketSubCategoryReasonCategoryMapping> ticketSubCategoryReasonCategoryMappingList;

    @Column(name = "mvno_id", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    String status;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = TicketSubCategoryTatMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "sub_category_mapping_id")
    List<TicketSubCategoryTatMapping> ticketSubCategoryTatMappingList;

//    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
//    private Integer lcoId;

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }

    @Override
    public Long getPrimaryKey() {
        return this.id;
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
