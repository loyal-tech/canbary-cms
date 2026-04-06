package com.adopt.apigw.modules.PriceGroup.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblpricebookslabdtls")
@NoArgsConstructor
public class PriceBookSlabDetails implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pbslabdetailid", updatable = false, nullable = false)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(targetEntity = PriceBook.class)
    @JoinColumn(name = "bookid", updatable = true, insertable = true)
    @JsonBackReference
    private PriceBook priceBook;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Column(name = "from_range")
    private Long fromRange;

    @Column(name = "to_range")
    private Long toRange;

    @Column(name = "commission_amount")
    private Double commissionAmount;

    @Transient
    private Long pricebookid;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    public PriceBookSlabDetails(PriceBookSlabDetails priceBookSlabDetails) {
        this.id = priceBookSlabDetails.getId();
        this.isDeleted = priceBookSlabDetails.getIsDeleted();
        this.fromRange = priceBookSlabDetails.getFromRange();
        this.toRange = priceBookSlabDetails.getToRange();
        this.commissionAmount = priceBookSlabDetails.getCommissionAmount();
        this.pricebookid = priceBookSlabDetails.getPriceBook().getId();
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
