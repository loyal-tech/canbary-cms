package com.adopt.apigw.modules.Area.repository;

import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.adopt.apigw.modules.Area.domain.Area;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Long>, QuerydslPredicateExecutor<Area> {

    Page<Area> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);

    Page<Area> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

    @Query("SELECT t from Area t WHERE t.isDeleted = false")
    Page<Area> findAll(Pageable pageable);

    @Query(value = "select t from Area t where t.isDeleted=false and mvnoId in :mvnoIds")
    Page<Area> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select sum(tbl.tab) from(\n" +
    "select count(*) as tab from tblmsubscriberaddressrel t4 where t4.AREAID =:id and t4.is_delete =false \n" +
    "union all\n" +
    "select count(*) as tab from tblservicearea t2 where t2.areaid =:id and t2.is_deleted =false \n" +
    ")tbl",nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);
    
    @Query(value = "select count(*) from tblmarea where name=:name and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmarea where name=:name and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmarea where name=:name and areaid =:id and  countryid=:countryId and  stateid = :stateId and cityid=:cityId and pincodeid = :pincodeId and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("countryId") Integer countryId,@Param("stateId") Integer stateId,@Param("cityId") Integer cityId, @Param("pincodeId") Integer pincodeId,@Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmarea where name=:name and areaid =:id and countryid=:countryId and  stateid = :stateId and cityid=:cityId and pincodeid = :pincodeId and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id,@Param("countryId") Integer countryId,@Param("stateId") Integer stateId,@Param("cityId") Integer cityId, @Param("pincodeId") Integer pincodeId);

    @Query(value = "select * from tblmarea where pincodeid = :pincodeId and is_deleted=false", nativeQuery = true)
    List<Area> findAreasByPincode(@Param("pincodeId")Long pincodeId);

    @Query(value = "select count(*) from tblmarea where name=:name and countryid=:countryId and  stateid = :stateId and cityid=:cityId and pincodeid = :pincodeId and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name,@Param("countryId") Integer countryId,@Param("stateId") Integer stateId,@Param("cityId") Integer cityId, @Param("pincodeId") Integer pincodeId);

    @Query(value = "select count(*) from tblmarea where name=:name and countryid=:countryId and  stateid = :stateId and cityid=:cityId and pincodeid = :pincodeId and is_deleted=false  and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name,@Param("countryId") Integer countryId,@Param("stateId") Integer stateId,@Param("cityId") Integer cityId, @Param("pincodeId") Integer pincodeId,  @Param("mvnoIds") List mvnoIds);

    Optional<Area> getFirstByCityIdAndCountryIdAndStateIdAndMvnoId(Integer cityId, Integer countryId, Integer stateId, Integer mvnoId);
    Optional<Area> findAllByNameEqualsIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, List mvnoIds);
    @Query(value = "select new Area(t.id,t.name,t.pincode.id) from Area t where t.id = :areaid")
    Area findAreaById(@Param("areaid") Long areaid);

    @Query(value = "SELECT a.areaid FROM tblmarea a WHERE a.pincodeid = :pincodeId ORDER BY a.areaid DESC LIMIT 1", nativeQuery = true)
    Integer getLatestAreaIdByPincode(Long pincodeId);

    @Query(value = "select id from Area where name=:name and isDeleted=false")
    Integer getWardIdByName(@Param("name") String name);
}
