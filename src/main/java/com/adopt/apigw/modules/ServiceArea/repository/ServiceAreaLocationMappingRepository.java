package com.adopt.apigw.modules.ServiceArea.repository;

import com.adopt.apigw.modules.LocationMaster.domain.ServiceAreaLocationMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceAreaLocationMappingRepository extends JpaRepository<ServiceAreaLocationMapping,Long> {

    boolean existsByServiceAreaIdAndLocationId(Long serviceAreaId, Long locationId);

    void deleteByServiceAreaId(Long serviceAreaId);

    List<ServiceAreaLocationMapping> findByserviceAreaId(Long serviceAreaId);

}
