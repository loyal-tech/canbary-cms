package com.adopt.apigw.modules.ServiceArea.repository;

import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceAreaRepository extends JpaRepository<ServiceArea, Long>, QuerydslPredicateExecutor<ServiceArea> {
    ServiceArea findByName(String serviceName);

    @Query(value = "SELECT * from tblservicearea t WHERE t.is_deleted = false"
            , nativeQuery = true
            , countQuery = "SELECT count(*) from tblservicearea t WHERE t.is_deleted = false")
    Page<ServiceArea> findAll(Pageable pageable);

    @Query(value = "select * from tblservicearea t where t.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Page<ServiceArea> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

//    @Query(value = "select * from tblservicearea t where t.name like '%' :s1 '%' and t.is_deleted  = 0", nativeQuery = true
//            , countQuery = "select count(*) from tblservicearea t where t.name like '%' :s1 '%' and t.is_deleted  = 0")
//    Page<ServiceArea> findAllByNameAndIsDeletedIsFalse(@Param("s1") String s1, Pageable pageable);

    @Query(value = "select * from tblservicearea t WHERE t.is_deleted = false and t.service_area_id NOT IN :ids", nativeQuery = true)
    List<ServiceArea> findAllByIdOut(@Param("ids") List<Long> ids);

    @Query(value = "select * from tblservicearea t \n" +
            "where t.service_area_id not in (\n" +
            "select t.service_area_id from tblservicearea t \n" +
            "inner join tblcasereasonconfig t2 \n" +
            "on t2.serviceareaid = t.service_area_id\n" +
            "inner join tblcasereasons t3 \n" +
            "on t3.reason_id = t2.reasonid \n" +
            "where t3.reason_id = :s1 )\n" +
            "and t.is_deleted = 0  and MVNOID in :mvnoIds", nativeQuery = true)
    List<ServiceArea> findAllServiceArea(@Param("s1") Long s1, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblcustomers t2 where t2.servicearea_id =:id and t2.is_deleted =false\n" +
            "union all\n" +
            "select count(*) as tab from tblnetworkdevices t where t.servicearea_id =:id and t.is_deleted =false \n" +
            ")tbl;",nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    @Query(value = "select count(*) from tblservicearea m where m.name=:name and m.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblservicearea m where m.name=:name and m.service_area_id !=:id and  m.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblservicearea m where m.name=:name and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);

    @Query(value = "select count(*) from tblservicearea m where m.name=:name and m.service_area_id !=:id and  m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    List<ServiceArea> findAllByIdIn(List<Long> result);

    List<ServiceArea> findAllByIdInAndStatusAndIsDeletedIsFalse(List<Long> result,String Status);

    @Query("select  new  ServiceArea(c) from ServiceArea c WHERE c.id in :ids" )
    List<ServiceArea> getServiceAreaByServiceAreaId( @Param("ids") List<Long> ids );


    ServiceArea findFirstByOrderById();

    @Query("select c.siteName from ServiceArea c WHERE c.id in :ids" )
    List<String> findSiteNameByServiceAreaId(List<Long> ids);

    @Query("select c.id from ServiceArea c WHERE c.siteName in :siteName and c.mvnoId != 1" )
    List<Long> findServiceAreaIdsFromSiteName(List<String> siteName);
    List<ServiceArea>findAllByNameInAndMvnoIdIn(List<String>nameList,List<Integer> mvnoId);
    Optional<ServiceArea> findAllByNameAndMvnoIdIn(String nameList, List<Integer> mvnoId);
    @Query(nativeQuery = true, value = "select t.cityid from tblservicearea t where t.service_area_id = :Id ")
    Long findCityId(@Param("Id") Long Id);


    @Query(value = "SELECT m.POSTPAIDPLANID , m.DISPLAYNAME FROM tblmpostpaidplan m WHERE m.STATUS = 'Active' AND m.MVNOID = :mvnoId AND NOT EXISTS ( SELECT 1 FROM tblplanservicearearel r WHERE r.planid = m.POSTPAIDPLANID AND r.serviceareaid = :serviceArea)", nativeQuery = true)
    List<Object[]> findRemainingPlansForServiceArea(@Param("serviceArea") Integer serviceArea,@Param("mvnoId") Integer mvnoId);

}
