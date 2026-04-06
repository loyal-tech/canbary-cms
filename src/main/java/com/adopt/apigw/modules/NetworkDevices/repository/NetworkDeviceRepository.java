package com.adopt.apigw.modules.NetworkDevices.repository;

import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NetworkDeviceRepository extends JpaRepository<NetworkDevices, Long>, QuerydslPredicateExecutor<NetworkDevices> {
    List<NetworkDevices> findByServiceareaId(Long serviceId);

    List<NetworkDevices> findByServiceareaIdAndIsDeletedIsFalse(Long serviceId);

    List<NetworkDevices> findByNameAndDevicetypeAndIsDeletedIsFalse(String networkDeviceName, String deviceType);

    @Query(value = "select * from tblnetworkdevices t\n" +
            "left join tblservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where  t.is_deleted = 0", countQuery = "select count(*) from tblnetworkdevices t\n" +
            "left join tblservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where  t.is_deleted = 0", nativeQuery = true)
    Page<NetworkDevices> findAll(Pageable pageable);

    @Query(value = "select * from tblnetworkdevices t\n" +
            "left join tblservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where  t.is_deleted = 0 and t.MVNOID in :mvnoIds", countQuery = "select count(*) from tblnetworkdevices t\n" +
            "left join tblservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where  t.is_deleted = 0 and t.MVNOID in :mvnoIds", nativeQuery = true)
    Page<NetworkDevices> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from tblnetworkdevices t\n" +
            "left join tblservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where (t.name like '%' :s1 '%' or t.devicetype like '%' :s2 '%' or t2.name like '%' :s3 '%') and  t.is_deleted = 0"
            , countQuery = "select count(*) from tblnetworkdevices t\n" +
            "left join tblservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where (t.name like '%' :s1 '%' or t.devicetype like '%' :s2 '%' or t2.name like '%' :s3 '%') and  t.is_deleted = 0")
    Page<NetworkDevices> findAllByNameContainingIgnoreCaseOrDevicetypeContainingIgnoreCaseOrServicearea_NameContainingIgnoreCase(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tblnetworkdevices t\n" +
            "left join tblservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where (t.name like '%' :s1 '%' or t.devicetype like '%' :s2 '%' or t2.name like '%' :s3 '%') and  t.is_deleted = 0 AND t.MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblnetworkdevices t\n" +
            "left join tblservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where (t.name like '%' :s1 '%' or t.devicetype like '%' :s2 '%' or t2.name like '%' :s3 '%') and  t.is_deleted = 0 AND t.MVNOID in :mvnoIds;")
    Page<NetworkDevices> findAllByNameContainingIgnoreCaseOrDevicetypeContainingIgnoreCaseOrServicearea_NameContainingIgnoreCaseAndMvnoIdIn(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tbloltslots t3 where t3.deviceid =:id and t3.is_deleted =false \n" +
            "union all\n" +
            "select count(*) as tab from tbloltportdetails t where t.deviceid =:id and t.is_deleted =false\n" +
            "union all \n" +
            "select count(*) as tab from tbltproduct t4 where t4.product_id =:id and t4.is_deleted =false\n" +
            "union all \n"+
            "select count(*) as tab from tblcustomers t2 where t2.network_device_id =:id and t2.is_deleted =false\n" +
            ")tbl", nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    @Query(value = "select count(*) from tblnetworkdevices m where m.name=:name and m.is_deleted=false and m.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblnetworkdevices m where m.name=:name and m.deviceid !=:id and  m.is_deleted=false and m.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblnetworkdevices m where m.name=:name and m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblnetworkdevices m where m.name=:name and m.deviceid !=:id and  m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select * from tblnetworkdevices where like '%Splitter%' and  is_deleted=false;", nativeQuery = true)
    List<NetworkDevices> getAllSplitters();

    @Query(value = "select * from tblnetworkdevices where parent_network_device_id=:parentId and is_deleted=false", nativeQuery = true)
    List<NetworkDevices> getByNetworkDeviceParentId(Long parentId);

    NetworkDevices findByIdAndMvnoIdIn(Long id, List mvnoIds);

    NetworkDevices findByCustInventoryId(Long id);

    NetworkDevices findByInventorymappingId(Long id);

    NetworkDevices findByItemIdAndIsDeletedFalse(Long itemId);

    NetworkDevices findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(Long itemId, Long custInventoryId);
    List<NetworkDevices> findAllByIsDeletedFalseAndDevicetypeAndStatus(String deviceType, String status);

    @Query("SELECT n.name FROM NetworkDevices n WHERE n.id = :deviceId")
    String findDeviceNameById(@Param("deviceId") Long deviceId);
    @Query(nativeQuery = true, value = "select t.name from tblnetworkdevices t where t.deviceid = :id and t.is_deleted=false")
    Optional<String> findNameByIdAndIsDeletedFalse(@Param("id") Long id);

}
