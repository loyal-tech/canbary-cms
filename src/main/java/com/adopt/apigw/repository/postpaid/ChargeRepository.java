package com.adopt.apigw.repository.postpaid;


import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.pojo.api.ChargePojo;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

//@JaversSpringDataAuditable
@JaversSpringDataAuditable
@Repository
public interface ChargeRepository extends JpaRepository<Charge, Integer>, QuerydslPredicateExecutor<Charge> {

    @Query(value = "select * from TBLCHARGES where lower(CHARGENAME) like '%' :search  '%' order by CHARGEID AND MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from TBLCHARGES where lower(CHARGENAME) like '%' :search '%' AND MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<Charge> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer mvnoId);

    List<Charge> findByChargetype(String chargeType);

    List<Charge> findAllByChargetype(String chargeTypeCustomerDirect);

    List<Charge> findAllByChargetypeAndIsDeleteIsFalse(String chargeTypeCustomerDirect);

    @Query(value = "select count(*) from tblcustchargedtls t where t.chargeid=:id", nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    @Query("select t from Charge t where t.isDelete=false")
    List<Charge> findAll();
    @Query(value = "select count(*) from tblmpostpaidplanchargerel t where t.CHARGEID=:id", nativeQuery = true)
    Integer deleteVerifyForPlan(@Param("id") Integer id);



    @Query(value = "select * from tblcharges as t where t.is_delete = false"
            , nativeQuery = true
            , countQuery = "select count(*) from tblcharges as t where t.is_delete = false")
    Page<Charge> findAll(Pageable pageable);

    @Query(value = "select * from tblcharges as t where t.is_delete = false and MVNOID in :mvnoIds and (business_type= 'Retail' or business_type is null or business_type = '')"
            , nativeQuery = true
            , countQuery = "select count(*) from tblcharges as t where t.is_delete = false and MVNOID in :mvnoIds and (business_type= 'Retail' or business_type is null or business_type = '')")
    Page<Charge> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    /**
     * Used for superadmin only
     * @param pageable
     * @return
     */

    @Query(value = "select * from tblcharges as t where t.is_delete = false"
            , nativeQuery = true
            , countQuery = "select count(*) from tblcharges as t where t.is_delete = false")
    Page<Charge> findAllWithoutMvno(Pageable pageable);

    @Query(value = "select * from tblcharges as t where t.is_delete = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))"
            , nativeQuery = true
            , countQuery = "select count(*) from tblcharges as t where t.is_delete = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<Charge> findAll(Pageable pageable, @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);

    @Query("update Charge b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    List<Charge> findAllByChargecategoryInAndIsDeleteIsFalse(List<String> category);

    List<Charge> findAllByChargetypeAndChargecategory(String type, String category);


    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' ) \n" +
            " AND t.is_delete = false"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' ) \n" +
            " AND t.is_delete = false")
    Page<Charge> findAllByName(@Param("s1") String s1, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' ) \n" +
            " AND t.is_delete = false AND MVNOID in :mvnoIds AND (business_type= 'Retail' or business_type is null or business_type= '')"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' ) \n" +
            " AND t.is_delete = false AND MVNOID in :mvnoIds AND (business_type= 'Retail' or business_type is null or business_type= '')")
    Page<Charge> findAllByNameAndMvnoIdIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblcharges c where c.CHARGENAME=:name and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblcharges c where c.CHARGENAME=:name and c.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblcharges c where c.CHARGENAME=:name and c.CHARGEID =:id and c.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblcharges c where c.CHARGENAME=:name and c.CHARGEID =:id and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblcharges c where c.CHARGENAME=:name and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblcharges c where c.CHARGENAME=:name and c.CHARGEID =:id and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select * from tblcharges where status = 'Active' and is_delete=false" +
            "(service =:service or service is null)",nativeQuery = true)
    List<Charge> getchargeByService(@Param("service") String service);

    @Query(value = "select * from tblcharges as t where t.is_delete = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) and (business_type= 'Retail' or business_type is null or business_type= '')"
            , nativeQuery = true
            , countQuery = "select count(*) from tblcharges as t where t.is_delete = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) and (business_type= 'Retail' or business_type is null or business_type= '')")
    Page<Charge> findAllByRetail(Pageable pageable, @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);


    @Query(value = "select * from tblcharges as t where t.is_delete = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) and business_type= 'Enterprise' order by CHARGEID"
            , nativeQuery = true
            , countQuery = "select count(*) from tblcharges as t where t.is_delete = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) and business_type= 'Enterprise' order by CHARGEID")
    Page<Charge> findAllByEnterprise(Pageable pageable, @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);

    @Query("select name from Charge where id =:id")
    String findNameById(@Param("id")Integer id);



//

    @Query(value = "select * from tblcharges as t where t.is_delete = false and MVNOID in :mvnoIds and (business_type= 'Retail' or business_type is null)"
            , nativeQuery = true
            , countQuery = "select count(*) from tblcharges as t where t.is_delete = false and MVNOID in :mvnoIds and (business_type= 'Retail' or business_type is null)")
    Page<Charge> findAll4(Pageable pageable, @Param("mvnoIds")List mvnoIds);

//
    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND (business_type= 'Retail' or business_type is null or business_type= '')"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND (business_type= 'Retail' or business_type is null or business_type= '')")
    Page<Charge> findAllByNameAndMvnoIdInAndRetail(@Param("s1") String s1, Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND business_type= 'Enterprise'"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND business_type= 'Enterprise'")
    Page<Charge> findAllByNameAndMvnoIdInAndEnterprise(@Param("s1") String s1, Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    Charge findByName(String name);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where ( t.CHARGETYPE = :s1 ) \n" +
            " AND t.is_delete = false"
            , countQuery = "select * from tblcharges as t \n" +
            "where ( t.CHARGETYPE = :s1 ) \n" +
            " AND t.is_delete = false")
    Page<Charge> findAllByChargetype(@Param("s1") String s1, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.CHARGETYPE = :s1) \n" +
            " AND t.is_delete = false AND MVNOID in :mvnoIds AND (business_type= 'Retail' or business_type is null or business_type= '')"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGETYPE = :s1) \n" +
            " AND t.is_delete = false AND MVNOID in :mvnoIds AND (business_type= 'Retail' or business_type is null or business_type= '')")
    Page<Charge> findAllByChargetypeAndMvnoIdIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.chargecategory like '%' :s1 '%') \n" +
            " AND t.is_delete = false"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.chargecategory like '%' :s1 '%') \n" +
            " AND t.is_delete = false")
    Page<Charge> findAllByChargecategory(@Param("s1") String s1, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.chargecategory like '%' :s1 '%') \n" +
            " AND t.is_delete = false AND MVNOID in :mvnoIds AND (business_type= 'Retail' or business_type is null or business_type= '')"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.chargecategory like '%' :s1 '%') \n" +
            " AND t.is_delete = false AND MVNOID in :mvnoIds AND (business_type= 'Retail' or business_type is null or business_type= '')")
    Page<Charge> findAllByChargecategoryAndMvnoIdIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where ( t.CHARGETYPE = :s1) \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND (business_type= 'Retail' or business_type is null or business_type= '')"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGETYPE = :s1) \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND (business_type= 'Retail' or business_type is null or business_type= '')")
    Page<Charge> findAllByChargetypeAndMvnoIdInAndRetail(@Param("s1") String s1, Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.CHARGETYPE = :s1) \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND business_type= 'Enterprise'"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGETYPE = :s1) \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND business_type= 'Enterprise'")
    Page<Charge> findAllByChargetypeAndMvnoIdInAndEnterprise(@Param("s1") String s1, Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.chargecategory like '%' :s1 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND (business_type= 'Retail' or business_type is null or business_type= '')"
            , countQuery = "select * from tblcharges as t \n" +
            "where ( t.chargecategory like '%' :s1 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND (business_type= 'Retail' or business_type is null or business_type= '')")
    Page<Charge> findAllByChargecategoryAndMvnoIdInAndRetail(@Param("s1") String s1, Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.chargecategory like '%' :s1 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND business_type= 'Enterprise'"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.chargecategory like '%' :s1 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND business_type= 'Enterprise'")
    Page<Charge> findAllByChargecategoryAndMvnoIdInAndEnterprise(@Param("s1") String s1, Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' or t.CHARGETYPE = :s2 or t.chargecategory like '%' :s3 '%') \n" +
            " AND t.is_delete = false"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' or t.CHARGETYPE = :s2 or t.chargecategory like '%' :s3 '%') \n" +
            " AND t.is_delete = false")
    Page<Charge> findAllByNameOrChargetypeOrChargecategory(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable);


    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' or t.CHARGETYPE = :s2  or t.chargecategory like '%' :s3 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND (business_type= 'Retail' or business_type is null or business_type= '')"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' or t.CHARGETYPE = :s2 or t.chargecategory like '%' :s3 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND (business_type= 'Retail' or business_type is null or business_type= '')")
    Page<Charge> findAllByNameOrChargetypeOrChargecategoryAndMvnoIdInAndRetail(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' or t.CHARGETYPE = :s2  or t.chargecategory like '%' :s3 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND business_type= 'Enterprise'"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' or t.CHARGETYPE = :s2 or t.chargecategory like '%' :s3 '%') \n" +
            " AND t.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND business_type= 'Enterprise'")
    Page<Charge> findAllByNameOrChargetypeOrChargecategoryAndMvnoIdInAndEnterprise(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' or t.CHARGETYPE = :s2  or t.chargecategory like '%' :s3 '%') \n" +
            " AND t.is_delete = false AND MVNOID in :mvnoIds AND (business_type= 'Retail' or business_type is null or business_type= '')"
            , countQuery = "select * from tblcharges as t \n" +
            "where (t.CHARGENAME like '%' :s1 '%' or t.CHARGETYPE = :s2  or t.chargecategory like '%' :s3 '%') \n" +
            " AND t.is_delete = false AND MVNOID in :mvnoIds AND (business_type= 'Retail' or business_type is null or business_type= '')")
    Page<Charge> findAllByNameOrChargetypeOrChargecategoryAndMvnoIdIn(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable, @Param("mvnoIds") List mvnoIds);
    Optional<Charge> findAllByNameEqualsAndMvnoIdIn(String name, List<Integer> mvnoId);
    @Query("select new com.adopt.apigw.pojo.api.ChargePojo(c.id, c.name) from Charge c where c.mvnoId in :mvnoIds")
    List<ChargePojo> findAllChargeByMvnoId(@Param(value = "mvnoIds") List<Integer> mvnoIds);

    @Query("select c.name from Charge c where c.id in :chargeId")
    String findChargeNameById(@Param(value = "chargeId") Integer chargeId);

    Charge findByNameAndChargetypeAndPrice(String name,String type,Double price);


    @Query("SELECT c.id FROM Charge c WHERE c.status = 'active' AND c.isDelete = false AND " +
            "(c.mvnoId = 1 OR :mvnoId = 1 OR c.mvnoId = :mvnoId) AND " +
            "(:buSize = 0 OR c.buId IN :buIds) AND " +
            "(:type IS NULL OR c.businessType = :type)")
    List<Integer> findFilteredChargeIds(@Param("type") String planBindingType,
                                        @Param("mvnoId") Integer mvnoId,
                                        @Param("buIds") List<Long> buIds,
                                        @Param("buSize") int buSize);



    @Query("SELECT DISTINCT new com.adopt.apigw.pojo.api.ChargePojo(" +
            "c.id, c.name, c.desc, c.chargetype, c.price, " +
            "t.id, t.name, c.taxamount, " +
            "c.discountid, c.dbr, c.actualprice, c.isDelete, " +
            "c.chargecategory, c.saccode, c.mvnoId, c.buId, " +
            "c.status, c.ledgerId, c.royalty_payable, " +
            "c.businessType, c.pushableLedgerId, " +
            "c.isinventorycharge, c.productId, c.inventoryChargeType, " +
            "c.mvnoName, c.currency) " +
            "FROM Charge c LEFT JOIN c.tax t " +
            "WHERE c.isDelete = false AND " +
            "(:mvnoId = 1 OR c.mvnoId = 1 OR c.mvnoId = :mvnoId) AND " +
            "(:mvnoId = 1 OR :buIdListSize = 0 OR c.buId IN :buIds) AND " +
            "(c.businessType IS NULL OR LOWER(c.businessType) = LOWER(:businessType)) " +
            "ORDER BY c.id DESC")
    List<ChargePojo> findFilteredCharges(
            @Param("mvnoId") Integer mvnoId,
            @Param("buIds") List<Long> buIds,
            @Param("buIdListSize") int buIdListSize,
            @Param("businessType") String businessType);

    @Query("SELECT c FROM Charge c WHERE c.chargecategory = :chargeType and c.isDelete = false")
    List<Charge> findByChargeCategory(@Param("chargeType") String chargeType);

    @Query("SELECT c FROM Charge c WHERE c.chargecategory = :chargeType AND c.mvnoId IN :mvnoIds and c.isDelete = false")
    List<Charge> findByChargeCategoryAndMvnoIdIn(@Param("chargeType") String chargeType,
                                                 @Param("mvnoIds") List<Integer> mvnoIdList);

}
