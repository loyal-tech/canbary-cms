package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.TaxTypeTier;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface TaxTypeTierRepository extends JpaRepository<TaxTypeTier, Integer> {

    @Query(value = "select * from TBLMTIERTAX where lower(name) like '%' || :search || '%' order by SLABTaxTypeSlabID",
            countQuery = "select count(*) from TBLMTIERTAX where lower(name) like '%' || :search ",
            nativeQuery = true)
    Page<TaxTypeTier> searchEntity(@Param("search") String searchText, Pageable pageable);

    @Query(value = "select RATE from TBLMTIERTAX where TAXID=:taxId", nativeQuery = true)
    Double getTaxRate(@Param("taxId") Integer taxId);

    @Query(value = "select * from TBLMTIERTAX where TAXID=:taxId", nativeQuery = true)
    List<TaxTypeTier> getTaxRateList(@Param("taxId") Integer taxId);


}
