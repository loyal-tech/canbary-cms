package com.adopt.apigw.modules.PriceGroup.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.service.spi.ServiceException;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblservicecommission")
@NoArgsConstructor
public class ServiceCommission implements IBaseData<Long> {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "bookid")
    @JsonBackReference
    private PriceBook priceBook;

    @Column(name = "serviceid")
    private Long serviceId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "revenue_share_percentage")
    private Integer revenue_share_percentage;

    @Column(name = "royalty_percentage")
    private Double royaltyPercentage;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;
    @Transient
    private Long pricebookid;
    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }

    public ServiceCommission(ServiceCommission serviceCommission) {
        this.id = serviceCommission.getId();
        this.serviceId = serviceCommission.getServiceId();
        this.serviceName = serviceCommission.getServiceName();
        this.revenue_share_percentage = serviceCommission.getRevenue_share_percentage();
        this.royaltyPercentage = serviceCommission.getRoyaltyPercentage();
        this.isDeleted = serviceCommission.getIsDeleted();
        this.pricebookid = serviceCommission.getPriceBook().getId();
    }
}
