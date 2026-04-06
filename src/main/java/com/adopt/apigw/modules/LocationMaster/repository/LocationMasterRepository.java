package com.adopt.apigw.modules.LocationMaster.repository;

import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.Set;

@JaversSpringDataAuditable
@Repository
public interface LocationMasterRepository extends JpaRepository<LocationMaster, Long>, QuerydslPredicateExecutor<LocationMaster> {
	Set<LocationMaster> findAllByLocationIdentifyValueIn(Set<String> locations);
	
	Optional<LocationMaster> findByLocationIdentifyValue(String locations);

	List<LocationMaster> findAllByLocationMasterIdIn(List<Long> locations);
}
