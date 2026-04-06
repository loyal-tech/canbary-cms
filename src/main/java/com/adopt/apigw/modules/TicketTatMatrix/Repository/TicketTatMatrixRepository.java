package com.adopt.apigw.modules.TicketTatMatrix.Repository;

import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import com.adopt.apigw.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;


public interface TicketTatMatrixRepository  extends JpaRepository<TicketTatMatrix,Long> , QuerydslPredicateExecutor<TicketTatMatrix> {

    @Query(value = "select count(*) from tblttickettatmatrix t where t.name=:matrixname and t.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("matrixname") String matrixname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblttickettatmatrix t where t.name=:matrixname and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds))",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("matrixname")String matrixname, @Param("mvnoIds") Integer mvnoIds, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblttickettatmatrix t where t.name=:matrixname and t.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("matrixname") String matrixname);

    @Query(value = "select count(*) from tblttickettatmatrix t where t.name=:tatmatrixname and t.id=:tatmatrixid and t.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("tatmatrixname") String tatmatrixname, @Param("tatmatrixid") Integer tatmatrixid);

    @Query(value = "select count(*) from tblttickettatmatrix t where t.name=:tatmatrixname and t.id=:matrixid and t.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("tatmatrixname") String tatmatrixname, @Param("matrixid") Integer matrixid, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblttickettatmatrix t where t.name=:tatmatrixname and t.id=:tatmatrixid and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("tatmatrixname") String tatmatrixname, @Param("tatmatrixid") Integer tatmatrixid, @Param("mvnoIds") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select * from tblttickettatmatrix m WHERE m.status like 'Active' and m.is_deleted=false",nativeQuery = true)
    List <TicketTatMatrix>findAllByStatus();

      @Query(value = "select count(*)as tab from tblttickettatsubcategorymapping t1 where t1.ticket_tat_mapping_id =:id " ,nativeQuery = true)
      Integer deleteVerify(@Param("id")Integer id);

    @Query(value = "select t from tblttickettatmatrix t where t.status='Active' AND t.is_deleted = false",nativeQuery = true)
    List<TicketTatMatrix> getAllByStatus();

    @Query(value = "select * from tblttickettatmatrix t where t.is_deleted = false and MVNOID in :mvnoIds",nativeQuery = true)
    Page<TicketTatMatrix> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select * from tblttickettatmatrix t where t.is_deleted = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))",nativeQuery = true)
    Page<TicketTatMatrix> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);


}

