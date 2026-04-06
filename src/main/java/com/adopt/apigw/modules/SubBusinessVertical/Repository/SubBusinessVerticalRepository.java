package com.adopt.apigw.modules.SubBusinessVertical.Repository;


import com.adopt.apigw.modules.SubBusinessVertical.Domain.SubBusinessVertical;
import com.adopt.apigw.modules.SubBusinessVertical.Model.SubBusinessVerticalDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubBusinessVerticalRepository extends JpaRepository<SubBusinessVertical, Long>, QuerydslPredicateExecutor<SubBusinessVertical> {

    @Query(value = "select count(*) from tblmsubbusinessvertical m where m.sbvname=:sbvname and m.is_deleted=false and m.MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("sbvname")String sbvname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmsubbusinessvertical m where m.sbvname=:sbvname and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("sbvname")String sbvname);

    SubBusinessVerticalDTO findByIdAndIsDeletedIsFalse(Long id);

    SubBusinessVerticalDTO findByIdAndIsDeletedIsFalseAndMvnoIdIn(Long id, List<Integer> asList);

    @Query(value = "select count(*) from tblmsubbusinessvertical m where m.sbvname=:sbvname and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSaveWithName(@Param("sbvname")String sbvname);

    @Query(value = "select count(*) from tblmsubbusinessvertical m where m.sbvname=:sbvname and m.is_deleted=false and MVNOID in :mvnoId",nativeQuery = true)
    Integer duplicateVerifyAtSaveWithName(@Param("sbvname")String sbvname, @Param("mvnoId") List mvnoId);

    @Query(value = "select count(*) from tblmsubbusinessvertical where sbvname=:sbvname and sbvid =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("sbvname") String sbvname, @Param("id") Long id);

    @Query(value = "select count(*) from tblmsubbusinessvertical where sbvname=:sbvname and sbvid =:id and is_deleted=false and MVNOID in :mvnoId", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("sbvname") String sbvname, @Param("id") Long id, @Param("mvnoId")List mvnoId);

    List<SubBusinessVertical> findByStatus(String status);
}
