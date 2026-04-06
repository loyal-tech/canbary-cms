package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.VasPlan;
//import com.adopt.apigw.service.common.VasPlanService;
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
public interface VasPlanRepository extends JpaRepository<VasPlan, Integer> , QuerydslPredicateExecutor<VasPlan> {

    @Query(value = "SELECT COUNT(*) FROM tblmvasplan v WHERE v.vas_name = :name AND v.isdelete = false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "SELECT COUNT(*) FROM tblmvasplan v WHERE v.vas_name = :name AND v.isdelete = false AND v.mvnoid IN (:mvnoIds)", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List<Integer> mvnoIds);

    @Query(value = "SELECT COUNT(*) FROM tblmvasplan v WHERE v.vas_name = :name AND v.id = :id AND v.isdelete = false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);
    @Query(value = "SELECT COUNT(*) FROM tblmvasplan v WHERE v.vas_name = :name AND v.id = :id AND v.isdelete = false AND v.mvnoid IN (:mvnoIds)", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List<Integer> mvnoIds);

    @Query(value = "SELECT * FROM tblmvasplan v WHERE v.id = :id AND v.isdelete = false AND v.mvnoid IN (:mvnoIds)", nativeQuery = true)
    Optional<VasPlan> findByIdAndIsdeleteFalseAndMvnoidIn(@Param("id") Integer id, @Param("mvnoIds") List<Integer> mvnoIds);

    List<VasPlan> findAllByIsdeleteFalse();
    List<VasPlan> findAllByIsdeleteFalseAndIsdefaultFalse();

    Optional<VasPlan> findByIdAndIsdeleteFalse(Integer id);


    @Query(value = "SELECT * FROM tblmvasplan v WHERE v.isdelete = false AND v.mvnoid IN (:mvnoIds)", nativeQuery = true)
    List<VasPlan> findAllByIsdeleteFalseAndMvnoidIn(@Param("mvnoIds") List<Integer> mvnoIds);

    @Query(value = "SELECT * FROM tblmvasplan v " +
            "WHERE v.isdelete = false " +
            "AND v.isdefault = false " +
            "AND v.mvnoid IN (:mvnoIds)",
            nativeQuery = true)
    List<VasPlan> findAllByIsdeleteFalseAndIsdefaultFalseAndMvnoidIn(@Param("mvnoIds") List<Integer> mvnoIds);

    @Query(value = "SELECT DISTINCT v.* FROM tblmvasplan v \n" +
            "INNER JOIN tblmvasplanchargerel vpc ON vpc.vasplanid = v.id \n" +
            "INNER JOIN tblcharges c ON vpc.chargeid = c.CHARGEID \n" +
            "WHERE v.isdelete = false AND v.isdefault = false AND v.mvnoid IN (:mvnoIds) AND c.currency IN (:currency)", nativeQuery = true)
    List<VasPlan> findAllByIsdeleteFalseAndIsdefaultFalseAndMvnoidInAndBaseCurrency(@Param("mvnoIds") List<Integer> mvnoIds, @Param("currency") String currency);



    Page<VasPlan> findAllByIsdeleteFalse(Pageable pageable);

    @Query(
            value = "SELECT * FROM tblmvasplan v WHERE v.isdelete = false AND v.mvnoid IN (:mvnoIds) ORDER BY v.id DESC",
            countQuery = "SELECT count(*) FROM tblmvasplan v WHERE v.isdelete = false AND v.mvnoid IN (:mvnoIds)",
            nativeQuery = true
    )
    Page<VasPlan> findAllByIsdeleteFalseAndMvnoidIn(@Param("mvnoIds") List<Integer> mvnoIds, Pageable pageable);

    @Query("SELECT plan.name FROM VasPlan plan where plan.id = :id")
    String findNameById(@Param("id") Integer id);

//    @Query("SELECT v FROM VasPlan v WHERE v.isdelete = false AND v.mvnoId IN (:mvnoIds)")
//    Page<VasPlan> findAllByIsdeleteFalseAndMvnoidIn(@Param("mvnoIds") List<Integer> mvnoIds, Pageable pageable);
     @Query("SELECT v FROM VasPlan v WHERE v.mvnoId in :mvnoId AND v.isdefault = true AND v.isdelete = false ORDER BY v.id DESC")
     List<VasPlan> findDefaultVasPlansByMvnoIdIn(@Param("mvnoId") List<Integer> mvnoId);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END " +
            "FROM VasPlan v " +
            "WHERE v.mvnoId = :mvnoId AND v.isdefault = true")
    boolean existsDefaultPlanByMvnoId(@Param("mvnoId") Integer mvnoId);
}

