package com.adopt.apigw.modules.PlanQosMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanQosMappingRepo extends JpaRepository<PlanQosMappingEntity , Long> {

  @Query(value = "select * from tbltplanqosmapping where planid =:id",nativeQuery = true)
  List<PlanQosMappingEntity> findAllByPlanId (@Param("id") Long id);

  @Query(value = "delete from tbltplanqosmapping t where t.planid is null",nativeQuery = true)
  void getDeleteList();

}
