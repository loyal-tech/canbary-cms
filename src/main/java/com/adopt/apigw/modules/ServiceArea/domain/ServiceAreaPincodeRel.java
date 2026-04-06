package com.adopt.apigw.modules.ServiceArea.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblserviceareapincoderel")
public class ServiceAreaPincodeRel implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(targetEntity = ServiceArea.class)
    @JoinColumn(name = "serviceareaid", referencedColumnName = "service_area_id", updatable = true, insertable = true)
    private ServiceArea serviceArea;

    @ManyToOne(targetEntity = Pincode.class)
    @JoinColumn(name = "pincodeid", referencedColumnName = "pincodeid", updatable = true, insertable = true)
    private Pincode pincodeData;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @ManyToOne(targetEntity = City.class)
    @JoinColumn(name = "cityid", referencedColumnName = "CITYID", updatable = true, insertable = true)
    private City cityData;

    public ServiceAreaPincodeRel(ServiceArea serviceArea, Pincode pincode, Boolean isDeleted){
        this.serviceArea = serviceArea;
        this.pincodeData = pincode;
        this.isDeleted = isDeleted;
        this.cityData = getCityData();
    }

    @Override
    public Long getPrimaryKey() { return id; }

    @Override
    public void setDeleteFlag(boolean deleteFlag) { this.isDeleted = deleteFlag; }

    @Override
    public boolean getDeleteFlag()  {
        return isDeleted;
    }
}
