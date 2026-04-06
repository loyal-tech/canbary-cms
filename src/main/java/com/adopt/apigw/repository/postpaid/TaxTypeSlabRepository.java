package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.TaxTypeSlab;

//@JaversSpringDataAuditable
@Repository
public interface TaxTypeSlabRepository extends JpaRepository<TaxTypeSlab, Integer> {

    @Query(value = "select * from TBLMSLABTAX where lower(name) like '%' || :search || '%' order by SLABTaxTypeSlabID",
            countQuery = "select count(*) from TBLMSLABTAX where lower(name) like '%' || :search ",
            nativeQuery = true)
    Page<TaxTypeSlab> searchEntity(@Param("search") String searchText, Pageable pageable);

    @Query(value = "select RATE from TBLMSLABTAX where TAXID=:taxId", nativeQuery = true)
    Double getTaxRate(@Param("taxId") Integer taxId);
}
