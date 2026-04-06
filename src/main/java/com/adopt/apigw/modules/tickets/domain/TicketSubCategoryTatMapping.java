package com.adopt.apigw.modules.tickets.domain;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategory;
import com.adopt.apigw.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import lombok.*;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tblttickettatsubcategorymapping")
public class TicketSubCategoryTatMapping implements IBaseData<Long> {
    public TicketSubCategoryTatMapping(Long ticketReasonSubCategoryId, TicketTatMatrix ticketTatMatrix, Boolean isDeleted) {
        this.ticketReasonSubCategoryId = ticketReasonSubCategoryId;
        this.ticketTatMatrix = ticketTatMatrix;
        this.isDeleted = isDeleted;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "sub_category_mapping_id",nullable = false)
    private Long ticketReasonSubCategoryId;

    @ManyToOne(targetEntity = TicketTatMatrix.class)
    @JoinColumn(name = "ticket_tat_mapping_id", nullable = false,referencedColumnName = "id")
    private TicketTatMatrix ticketTatMatrix;

    @OneToMany(targetEntity = TatQueryFieldMapping.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "tat_mapping_id")
    private List<TatQueryFieldMapping> tatQueryFieldMappingList;


    @Column(name ="is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name ="order_id")
    private Long orderid;


    @Override
    public Long getPrimaryKey() { return id; }

    @Override
    public void setDeleteFlag(boolean deleteFlag) { this.isDeleted = deleteFlag; }

    @Override
    public boolean getDeleteFlag()  {
        return isDeleted;
    }
}
