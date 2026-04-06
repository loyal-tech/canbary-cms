package com.adopt.apigw.modules.InvestmentCode.repository;

import com.adopt.apigw.modules.InvestmentCode.Domain.InvestmentCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentCodeRepository extends JpaRepository<InvestmentCode,Long>, QuerydslPredicateExecutor<InvestmentCode> {

    @Query(value = "select count(*) from tbltinvestmentcode m where m.icname=:icname and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("icname")String icname);

    @Query(value = "select count(*) from tbltinvestmentcode m where m.icname=:icname and m.is_deleted=false and m.MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("icname")String icname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tbltinvestmentcode m where m.icCode=:icCode and m.is_deleted=false and m.MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSaveForCode(@Param("icCode")String icCode, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tbltinvestmentcode where icname=:icname and investmentcode_id =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("icname") String icname, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tbltinvestmentcode where icname=:icname and investmentcode_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("icname") String icname, @Param("id") Long id);

    List<InvestmentCode> findAllByIdIn(List<Long> icIds);

    @Query(value = "select count(*) from tblmicnamebumapping t where t.investmentcode_id =:id and t.is_deleted = false",nativeQuery = true)
    Integer deleteVerifyForInvestmentCode(@Param("id")Long id);
}
