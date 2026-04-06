package com.adopt.apigw.repository.postpaid;


import java.util.List;
import java.util.Optional;

import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.Partner;

@JaversSpringDataAuditable
@Repository
public interface PartnerRepository extends JpaRepository<Partner, Integer>, QuerydslPredicateExecutor<Partner> {

    @Query(value = "select * from tblpartners where lower(name) like '%' :search  '%' order by partnerid AND MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from tblpartners where lower(name) like '%' :search '%' AND MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<Partner> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer mvnoId);

    List<Partner> findByStatusAndIsDeleteIsFalse(String status);
    
    @Query(value = "select * from tblpartners where partnerid <> :id", nativeQuery = true)
    List<Partner> getAllParentPartners(@Param("id") Integer id);

    @Override
    @Query("select p from Partner p where p.isDelete=false")
    List<Partner> findAll();

    @Query(value = "select * from tblpartners as t where t.is_delete = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))"
            , nativeQuery = true
            , countQuery = "select count(*) from tblpartners as t where t.is_delete = false and MVNOID = :mvnoId AND t.BUID in :buIds")
    Page<Partner> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);


    @Query(value = "select * from tblpartners t \n" +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " group by t2.partnerid ) srn on srn.partnerid = t.PARTNERID \n" +
            " where t.is_delete = 0 AND t.MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblpartners t \n" +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " group by t2.partnerid ) srn on srn.partnerid = t.PARTNERID \n" +
            " where t.is_delete = 0 AND t.MVNOID in :mvnoIds", nativeQuery = true)
    Page<Partner> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblpartners t \n" +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " group by t2.partnerid ) srn on srn.partnerid = t.PARTNERID \n" +
            " where t.is_delete = 0"
            , countQuery = "select count(*) from tblpartners t \n" +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " group by t2.partnerid ) srn on srn.partnerid = t.PARTNERID \n" +
            " where t.is_delete = 0", nativeQuery = true)
    Page<Partner> findAll(Pageable pageable);

    @Query("update Partner p set p.isDelete=true where p.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

//    @Query(nativeQuery = true,
//            value = "select * from tblpartners t \n" +
//                    "where (t.PARTNERNAME like '%' :s1 '%' \n" +
//                    "or t.mobile like '%' :s2 '%'\n" +
//                    "or t.email like '%' :s3 '%')\n" +
//                    "and t.is_delete = 0 AND t.MVNOID= :MVNOID OR t.MVNOID IS NULL",
//            countQuery = "select count(*) from tblpartners t \n" +
//                    "where (t.PARTNERNAME like '%' :s1 '%' \n" +
//                    "or t.mobile like '%' :s2 '%'\n" +
//                    "or t.email like '%' :s3 '%')\n" +
//                    "and t.is_delete = 0 AND t.MVNOID= :MVNOID OR t.MVNOID IS NULL")
//    Page<Partner> findAllByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrMobileContainingIgnoreCaseAndIsDeleteIsFalse
//            (@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable, @Param("MVNOID") Integer MVNOID);

//    @Query(nativeQuery = true, value = "select * from tblpartners t where (t.PARTNERNAME like '%' :s1 '%'  or t.mobile like '%' :s2 '%' or t.email like '%' :s3 '%')\n" +
//            "and t.is_delete = 0")
//    List<Partner> searchPartner(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3);

    List<Partner> findAllByEmailAndIsDeleteIsFalseOrderByIdDesc(String email);

    @Query(nativeQuery = true, value = "select * from tblpartners t \n" +
            "where t.PARTNERNAME like '%' :s1 '%' and t.is_delete = 0 AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", countQuery = "select count(*) from tblpartners t \n" +
            "where t.PARTNERNAME like '%' :s1 '%' and t.is_delete = 0 AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<Partner> findAllByNameAndIsDeleteIsFalse(@Param("s1") String s1, Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);


    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId")Integer mvnoId , @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);


    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.PARTNERID =:id and t.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.PARTNERID =:id and t.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoId")Integer mvnoIds ,@Param("buIds") List buIds );


    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.PARTNERID =:id and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id);

    @Query(value = "select CREATEDBYSTAFFID from tblpartners where PARTNERNAME=:name and is_delete=false and MVNOID=:mvnoId and BUID in :buIds", nativeQuery = true)
	Integer getCreatedBy(@Param("name")String name,  @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select CREATEDBYSTAFFID from tblpartners where PARTNERNAME=:name and is_delete=false and MVNOID=:mvnoId", nativeQuery = true)
    Integer getCreatedBy(@Param("name")String name,  @Param("mvnoId")Integer mvnoId);
    
    @Query(value = "select PARTNERID from tblpartners where status='ACTIVE' ", nativeQuery = true)
    List<Integer> getAllPartnerId();

    @Query(value = "select PARTNERID from tblpartners t where status='ACTIVE' and t.MVNOID in :mvnoIds", nativeQuery = true)
	List<Integer> getAllPartnerId(@Param("mvnoIds")List mvnoIds);

    @Query(value = "select PARTNERID from tblpartners t where status='ACTIVE' and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
	List<Integer> getAllPartnerId( @Param("mvnoId")Integer mvnoId , @Param("buIds") List buIds);

    Partner findByIdAndIsDeleteIsFalseAndMvnoIdIn(Integer id, List<Integer> asList);

    Partner findByIdAndIsDeleteIsFalse(Integer id);

    @Query(value = "select t.PARTNERID  from tblpartners t where t.parentpartnerid =:parentpartnerid",nativeQuery = true)
    List<Integer> getChildPartnerIdFromParentPartnerId(@Param("parentpartnerid") Integer parentpartnerid);

    @Query(value = "select t.* from tblpartners t where t.PARTNERID in :partnerids",nativeQuery = true)
    List<Partner> getAllPartnerByPartnerIds(@Param("partnerids") List<Integer> partnerids);

    @Query(value = "select t.PARTNERNAME from tblpartners t where t.PARTNERID in :partnerids",nativeQuery = true)
    List<String> getAllPartnerNamesByPartnerIds(@Param("partnerids") List<Integer> partnerids);

    Optional<Partner>findAllByNameEqualsAndMvnoIdIn(String name,List<Integer>mvnoId);

    @Query("SELECT p.name FROM Partner p WHERE p.id = :partnerId")
    String findBranchNameByPartnerId(@Param("partnerId") Integer partnerId);


    @Query(value = "select t.PARTNERNAME from tblpartners t where t.PARTNERID =:id and t.is_delete=false ",nativeQuery = true)
    String findNameByPartnerId(@Param("id") Integer id);


    @Query("SELECT p.priceBookId FROM Partner p WHERE p.id = :partnerId")
    PriceBook findPriceBookByPartnerId(@Param("partnerId") Integer partnerId);
   Optional<Partner> findByNameAndMvnoIdIn(String username , List<Integer> mvnoid);
}
