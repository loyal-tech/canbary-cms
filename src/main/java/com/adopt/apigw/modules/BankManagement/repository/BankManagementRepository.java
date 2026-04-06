package com.adopt.apigw.modules.BankManagement.repository;

import com.adopt.apigw.modules.BankManagement.domain.BankManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankManagementRepository extends JpaRepository<BankManagement , Long> , QuerydslPredicateExecutor<BankManagement> {

     @Query(value = "SELECT * from tblmbankmanagement t WHERE t.is_deleted = false"
      , nativeQuery = true
      , countQuery = "SELECT count(*) from tblmbankmanagement t WHERE t.is_deleted = false")
     Page<BankManagement> findAll(Pageable pageable);

    @Query(value = "select * from tblmbankmanagement t where t.is_deleted = false and MVNOID in :mvnoIds", nativeQuery = true)
    Page<BankManagement> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbankmanagement m where m.accountnum=:accountnum and m.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("accountnum")String accountnum, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbankmanagement m where m.accountnum=:accountnum and m.is_deleted=false",nativeQuery = true)
            Integer duplicateVerifyAtSave(@Param("accountnum")String accountnum);

    @Query(value = "select count(*) from tblmbankmanagement where accountnum=:accountnum and bankid =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
            Integer duplicateVerifyAtEdit(@Param("accountnum") String accountnum, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmbankmanagement where accountnum=:accountnum and bankid =:id and is_deleted=false", nativeQuery = true)
            Integer duplicateVerifyAtEdit(@Param("accountnum") String accountnum, @Param("id") Long id);
    @Query(value = "select count(*) from tbltcreditdoc where bankid =:id or destination_bank=:id and status != 'rejected'" ,nativeQuery = true)
        Integer deleteVerify(@Param("id")Long id);
    @Query(value = "select * from tblmbankmanagement m WHERE m.status like 'Active' and m.is_deleted=false",nativeQuery = true)
    List<BankManagement> findAllByStatus();

    BankManagement findByAccountnum(String accountnum);


   // List<BankManagement> findByAccountnumAndMvnoId(String accountnum,   List mvnoIds);

    List<BankManagement> findByAccountnumAndMvnoIdIn(String accountnum, List mvnoIds);
}
