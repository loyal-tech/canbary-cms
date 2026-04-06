package com.adopt.apigw.modules.SubArea.Repository;

import com.adopt.apigw.modules.SubArea.Domain.SubArea;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubAreaRepository extends JpaRepository<SubArea,Long>, QuerydslPredicateExecutor<SubArea> {

    @Query(value = "select t.name from tblmsubarea t where t.cityid = :cityId",nativeQuery = true)
    String findSubAreaNameByCityId(@Param("cityId")Integer cityId);
}
