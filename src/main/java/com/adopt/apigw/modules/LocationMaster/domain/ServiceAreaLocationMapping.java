package com.adopt.apigw.modules.LocationMaster.domain;

import com.adopt.apigw.model.postpaid.Location;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbltservicearealocationmapping")
public class ServiceAreaLocationMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "service_area_id", nullable = false)
    private Long serviceAreaId;

    @Column(name = "location_id", nullable = false)
    private Long locationId;
}
