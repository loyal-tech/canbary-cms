package com.adopt.apigw.modules.PurchaseOrder.Repository;

import com.adopt.apigw.modules.PurchaseOrder.DTO.PurchaseOrderDTO;
import com.adopt.apigw.modules.PurchaseOrder.Domain.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, QuerydslPredicateExecutor<PurchaseOrder> {


    @Query(value = "select * from tbltpurchaseorder t where t.is_deleted = false and MVNOID in :mvnoIds",nativeQuery = true)
    Page<PurchaseOrder>findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select * from tbltpurchaseorder t where t.is_deleted = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))",nativeQuery = true)
    Page<PurchaseOrder> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tbltpurchaseorder t where t.purchaseorder_number=:ponumber and t.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("ponumber") String ponumber);

    @Query(value = "select count(*) from tbltpurchaseorder t where t.purchaseorder_number=:ponumber and t.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("ponumber") String ponumber,@Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tbltpurchaseorder t where t.purchaseorder_number=:ponumber and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds))",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("ponumber") String ponumber,@Param("mvnoIds") Integer mvnoIdFromCurrentStaff, @Param("buIds")List buIdsFromCurrentStaff);

    @Query(value = "select count(*) from tbltpurchaseorder t where t.id =:id and t.is_deleted = false",nativeQuery = true)
    Integer deleteVerify(@Param("id") Long valueOf);

    PurchaseOrder findByponumber(String ponumber);

//        @Query("SELECT new com.your.project.MyDomainDto(q.firstname, q.lastname) from MyDomain q WHERE q.firstname = :firstname")
//        Page<MyDomainDto> findByFirstName(String firstname, Pageable pageable);
//    }
}
