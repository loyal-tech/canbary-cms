package com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.repository;

import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalItemManagementRepository extends JpaRepository<ExternalItemManagement, Long>, QuerydslPredicateExecutor<ExternalItemManagement> {
    @Query(nativeQuery = true, value = "select * from adoptconvergebss.tbltexternalitemmanagement t where t.product_id =:id")
    List<ExternalItemManagement> findAllByProductId(@Param("id") Integer id);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblhitemhistory tiowmm where tiowmm.external_item_id =:id and tiowmm.is_deleted =false\n" +
            "union all\n" +
            "select count(*) as tab from tblmcustomer_inventory_mapping t2 where t2.external_item_id =:id and t2.is_deleted =false\n" +
            ")tbl",nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);
    Page<ExternalItemManagement> findAllByIdIn(List<Long> ids, Pageable pageable);

    List<ExternalItemManagement> findAllByServiceAreaIdIdInAndIsDeletedIsFalseAndMvnoIdIn(List<Long> servicearea_ids, List<Integer> mvno_ids);
    Page<ExternalItemManagement> findAllByexternalItemGroupNumberContainingIgnoreCaseAndIsDeletedIsFalse(String externalItemGroupNumber, Pageable pageable);
    List<ExternalItemManagement> findAllByexternalItemGroupNumberContainingIgnoreCaseAndServiceAreaIdIdInAndIsDeletedIsFalseAndMvnoIdIn(String externalItemGroupNumber, List<Long> servicearea_ids, List<Integer> mvno_ids);

    ExternalItemManagement findTopByOrderByIdDesc();
}
