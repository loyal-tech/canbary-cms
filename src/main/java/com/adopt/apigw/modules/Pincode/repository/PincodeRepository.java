package com.adopt.apigw.modules.Pincode.repository;

import com.adopt.apigw.modules.Pincode.domain.Pincode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PincodeRepository extends JpaRepository<Pincode, Long>, QuerydslPredicateExecutor<Pincode>  {
    Pincode findByPincodeAndIsDeletedIsFalse(String pincode);

    Page<Pincode> findAllByPincodeContainingIgnoreCaseAndIsDeletedIsFalse(String pincode, Pageable pageable);

    Page<Pincode> findAllByPincodeContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String pincode, Pageable pageable, List mvnoIds);

    @Query("SELECT t from Pincode t WHERE t.isDeleted = false")
    Page<Pincode> findAll(Pageable pageable);

    List<Pincode> findAllByPincodeStartingWithAndIsDeletedIsFalse(String s1);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblmsubscriberaddressrel t4 where t4.PINCODEID =:id and t4.is_delete =false \n" +
            "union all \n" +
            "select count(*) as tab from tblmarea t5  where t5.pincodeid =:id and t5.is_deleted =false \n" +
            ")tbl",nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);
    
    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("pincode") String pincode, @Param("mvnoIds")List mvnoIds);


    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and pincodeid =:id and cityId=:cityId and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("pincode") String pincode, @Param("id") Long id, @Param("cityId") Integer cityid,@Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("pincode") String pincode);

    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and pincodeid =:id and cityId=:cityId and is_deleted=false", nativeQuery = true)
   Integer duplicateVerifyAtEdit(@Param("pincode") String pincode, @Param("id") Long id,@Param("cityId") Integer cityid);

    @Query(value = "select t from Pincode t where t.isDeleted=false and mvnoId in :mvnoIds")
    Page<Pincode> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and cityId=:cityId and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSaveWithPincodeAndCityID(@Param("pincode") String pincode, @Param("cityId") Integer cityId, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and cityId=:cityId and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSaveWithPincodeAndCityID(@Param("pincode") String pincode,@Param("cityId") Integer cityId);

    @Query(value="SELECT pincode from tblmpincode where pincodeid=:id and is_deleted=false", nativeQuery = true)
    String getPincode(@Param("id")Long id);

    List<Pincode> findByPincode(String pincode);

    @Query(value = "select * from tblmpincode where cityid=:id and status = 'Active' and is_deleted = false", nativeQuery = true)
    List<Pincode> findallcitybyid(@Param("id") Integer id);

    Optional<Pincode> findByPincodeEqualsAndMvnoIdIn(String pincode, List<Integer> mvnoId);
    @Query(value = "select t.pincode from tblmpincode t where t.pincodeid=:pincodeid",nativeQuery = true)
    String findPincodeNameById(@Param("pincodeid") Long pincodeid);
    Optional<Pincode> findByPincodeAndMvnoIdIn(String pincode, List<Integer> mvnoId);

    @Query(value = "select t.pincodeid ,t.COUNTRYID ,t.STATEID ,t.CITYID  from tblmpincode t where t.pincodeid IN " +
            "(select t2.pincodeid  from tblserviceareapincoderel t2 where t2.serviceareaid = :serviceAreaId) order by t.pincodeid desc limit 1;", nativeQuery = true)
    List<Object[]> getPincodeDetailByServiceAreaId(Long serviceAreaId);
}
