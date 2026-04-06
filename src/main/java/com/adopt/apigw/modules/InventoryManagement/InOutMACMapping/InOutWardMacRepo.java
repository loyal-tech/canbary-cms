package com.adopt.apigw.modules.InventoryManagement.InOutMACMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InOutWardMacRepo extends JpaRepository<InOutWardMACMapping, Long>, QuerydslPredicateExecutor<InOutWardMACMapping> {
    @Query(nativeQuery = true
    ,value = "select count(*)  from adoptconvergebss.tblhitemhistory tiowmm\n" +
            "left join\n" +
            "adoptconvergebss.tbltinward t \n" +
            "on t.inward_id = tiowmm.inward_id \n" +
            "where tiowmm.inward_id =:id and t.is_deleted =false")
    Integer countInward(@Param("id") Integer id);

    @Query(nativeQuery = true
            ,value = "select count(*)  from adoptconvergebss.tblhitemhistory tiowmm\n" +
            "left join\n" +
            "adoptconvergebss.tbltinward t \n" +
            "on t.inward_id = tiowmm.inward_id \n" +
            "where tiowmm.inward_id =:id and t.is_deleted =false and t.mvno_id in :mvnoIds")
    Integer countInward(@Param("id") Integer id,  @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true
            ,value = "select count(*)  from adoptconvergebss.tblhitemhistory tiowmm\n" +
            "left join\n" +
            "adoptconvergebss.tbltinward t \n" +
            "on t.inward_id = tiowmm.inward_id \n" +
            "where tiowmm.inward_id_of_outward =:id and t.is_deleted =false")
    Integer countInwardIdOfOutward(@Param("id") Integer id);

    @Query(nativeQuery = true
            ,value = "select count(*)  from adoptconvergebss.tblhitemhistory tiowmm\n" +
            "left join\n" +
            "adoptconvergebss.tbltinward t \n" +
            "on t.inward_id = tiowmm.inward_id \n" +
            "where tiowmm.inward_id_of_outward =:id and t.is_deleted =false and t.mvno_id in :mvnoIds")
    Integer countInwardIdOfOutward(@Param("id") Integer id,  @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from adoptconvergebss.tblhitemhistory tiowmm where tiowmm.inward_id =:id")
    List<InOutWardMACMapping> findByInwardId(@Param("id") Long id);

//    @Query(nativeQuery = true, value = "select * from adoptconvergebss.tblhitemhistory tiowmm where tiowmm .mac_mapping_id =:id")
//    InOutWardMACMapping findByMacMappingId(@Param("id") Long id);
    List<InOutWardMACMapping> findByCustInventoryMappingId(Long id);

    List<InOutWardMACMapping> findByInventoryMappingId(Long id);

    InOutWardMACMapping findByItemId(Long id);

    List<InOutWardMACMapping> findAllByItemId(Long id);

    List<InOutWardMACMapping> findAllByItemIdIn(List<Long> id);

    @Query(value = "select count(*) from tblmserializeditem m where m.mac=:mac and m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("mac") String mac);

    @Query(value = "select count(*) from tblmserializeditem m where m.mac=:mac and m.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("mac") String mac, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmserializeditem where mac=:mac and item_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("mac") String mac, @Param("id") Long id);

    @Query(value = "select count(*) from tblmserializeditem where mac=:mac and item_id =:id and is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("mac") String mac, @Param("id") Long id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblhitemhistory where inward_id =:id and is_forwarded =0 and is_deleted=false",nativeQuery = true)
    List<InOutWardMACMapping> findbyinwardid(@Param("id") Long id);

    @Query(value = "select * from tblhitemhistory where outward_id =:id and is_deleted=false",nativeQuery = true)
    List<InOutWardMACMapping> findbyoutwardid(@Param("id") Long id);

    @Query(value = "select * from tblhitemhistory tiowmm where inward_id =:id and is_deleted =false",nativeQuery = true)
    List<InOutWardMACMapping> deleteVerify(@Param("id") Integer id);

    List<InOutWardMACMapping> bulkConsumptionId(Long id);

    @Query(value="select * from adoptconvergebss.tblhitemhistory tiowmm  left join adoptconvergebss.tblmserializeditem t on t.id = tiowmm.item_id\n" +
            "where tiowmm.is_deleted = 0 and tiowmm.inward_id = :inwardId and tiowmm.is_forwarded = 0 and cust_inventory_mapping_id is null and bulkconsumption_id is null and inventory_mapping_id is null and t.item_status in ('UnAllocated','Defective');\n",nativeQuery = true)
    List<InOutWardMACMapping> findAllItemsByInwardIdAndItemStatus(@Param("inwardId")Long inwardId);

    @Query(value="select * from adoptconvergebss.tblhitemhistory tiowmm  left join adoptconvergebss.tblmserializeditem t on t.id = tiowmm.item_id\n" +
            "where tiowmm.is_deleted = 0 and tiowmm.external_item_id = :externalId and tiowmm.is_forwarded = 0 and cust_inventory_mapping_id is null and bulkconsumption_id is null and inventory_mapping_id is null and t.item_status = 'UnAllocated';\n",nativeQuery = true)
    List<InOutWardMACMapping> findAllItemsByExternalIdAndItemStatus(@Param("externalId")Long externalId);
    List<InOutWardMACMapping> findAllByItemIdInAndIsForwardedAndIsDeletedIsFalse(List<Long> itemId, Integer forwarded);
    InOutWardMACMapping findByItemIdAndIsForwardedAndIsDeletedIsFalse(Long itemId, Integer forwarded);

    List<InOutWardMACMapping> findAllByItemIdInAndIsForwarded(List<Long> itemId, Integer forwarded);
}
