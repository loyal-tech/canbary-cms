package com.adopt.apigw.modules.fieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FieldsBuidMappingRepo extends JpaRepository<FieldsBuidMapping,Long> , QuerydslPredicateExecutor<FieldsBuidMapping> {
/*

    @Query(value = "select t.field_id, t.is_mandatory, t.screen, t.module FROM tbltfieldsBuidMapping t where t.is_deleted=false and t.buid in :buid",nativeQuery = true)
    Set<FieldsBuidMapping> getFields(@Param("buid")List<Long> buid);
*/

    @Query(value = "select * FROM tbltfieldsBuidMapping t where t.is_deleted=false and t.screen=:screen and t.buid in :buid",nativeQuery = true)
    Set<FieldsBuidMapping> getFields(@Param("screen") String screen, @Param("buid")List<Long> buid);


    @Query(value = "select * FROM tbltfieldsBuidMapping t where t.is_deleted=false and t.screen=:screen and t.buid is null",nativeQuery = true)
    Set<FieldsBuidMapping> getFields(@Param("screen") String screen);

    @Query(value = "select * FROM tbltfieldsBuidMapping t where t.is_deleted=false",nativeQuery = true)
    Set<FieldsBuidMapping> getAll();

    List<FieldsBuidMapping> findAllByBuid(Long id);

    @Query(value = "select * FROM tbltfieldsBuidMapping t where t.is_deleted=false and t.screen= 2 and t.buid is null",nativeQuery = true)
    List<FieldsBuidMapping> findAllByNullBuids();

    List<FieldsBuidMapping> findAllByScreen(String name);

    List<FieldsBuidMapping> findAllByserviceParamIdIn(List<Long> serviceparamIdList);
}
