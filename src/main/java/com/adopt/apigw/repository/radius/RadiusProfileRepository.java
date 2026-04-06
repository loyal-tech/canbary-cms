package com.adopt.apigw.repository.radius;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.radius.RadiusProfile;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface RadiusProfileRepository extends JpaRepository<RadiusProfile, Integer> {

    @Query(value = "select * from tblradiusprofile where lower(name) like '%' || :search || '%' order by radiusprofileid",
            countQuery = "select count(*) from tblradiusprofile where lower(name) like '%' || :search ",
            nativeQuery = true)
    Page<RadiusProfile> searchEntity(@Param("search") String searchText, Pageable pageable);

    List<RadiusProfile> findByStatusAndIsDeleteIsFalse(String status);

    @Query("select t from RadiusProfile t where t.isDelete=false")
    List<RadiusProfile> findAll();

    @Query("update RadiusProfile b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(value = "select * from tblradiusprofile t where t.is_delete=false", nativeQuery = true
            , countQuery = "select count(*) from tblradiusprofile t where t.is_delete=false")
    Page<RadiusProfile> findAll(Pageable pageable);

    @Query(value = "select * from tblradiusprofile t where t.name like '%' :s1 '%' and t.is_delete=false", nativeQuery = true
            , countQuery = "select count(*) from tblradiusprofile t where t.name like '%' :s1 '%' and t.is_delete=false")
    Page<RadiusProfile> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(Pageable pageable, @Param("s1") String s1);

    @Query(value = "select count(*) from tblpostpaidplanradiusprofilerel where radiusprofileid=:id",nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    @Query(value = "select count(*) from tblradiusprofile c where c.NAME=:name and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblradiusprofile c where c.NAME=:name and c.radiusprofileid =:id and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select count(*) from tblradiusprofile c where c.NAME=:name and c.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblradiusprofile c where c.NAME=:name and c.radiusprofileid =:id and c.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds")List mvnoIds);
}
