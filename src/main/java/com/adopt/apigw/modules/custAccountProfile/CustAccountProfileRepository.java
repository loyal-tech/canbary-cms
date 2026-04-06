package com.adopt.apigw.modules.custAccountProfile;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@JaversSpringDataAuditable
@Repository
public interface CustAccountProfileRepository extends JpaRepository<CustAccountProfile, Long>, QuerydslPredicateExecutor<CustAccountProfile> {


    @Query(nativeQuery = true,value = "select * from tbltcustprofile where mvno_id =:id")
   Optional<CustAccountProfile> findByMvnoId(Long id);

    @Query(nativeQuery = true,value = "select * from tbltcustprofile where profile_id =:id")
    Optional<CustAccountProfile> findByProfileId(Long id);

}
