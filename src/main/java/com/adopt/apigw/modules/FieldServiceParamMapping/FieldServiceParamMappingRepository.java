package com.adopt.apigw.modules.FieldServiceParamMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldServiceParamMappingRepository extends JpaRepository<FieldServiceParamMapping, Long> , QuerydslPredicateExecutor<FieldServiceParamMapping> {

    @Query(value = "select * from tbltfieldserviceparamrel t where t.serviceparamid = 1",nativeQuery = true)
    List<FieldServiceParamMapping> findAllByserviceParamIdIn(List<Long> serviceparamIdList);

    List<FieldServiceParamMapping> findAll();

    List<FieldServiceParamMapping> findAllByServiceParameterIdInAndServiceParameterIdIsNull(List<Long> serviceparamIdList);
    List<FieldServiceParamMapping> findAllByServiceParameterIdIsNull();
    List<FieldServiceParamMapping> findAllByServiceParameterIsNull();
    List<FieldServiceParamMapping> findAllByServiceParameterIdIn(List<Long> serviceparamIdList);
}
