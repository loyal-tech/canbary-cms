package com.adopt.apigw.repository.radius;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.radius.VoucherMaster;

//@JaversSpringDataAuditable
@Repository
public interface VoucherMasterRepository extends JpaRepository<VoucherMaster, Integer> {
    @Query(value = "select * from tblvouchermaster where lower(vcname) like '%' || :search || '%'  order by id desc",
            countQuery = "select count(*) from tblvouchermaster where lower(vcname) like '%' || :search || '%' ",
            nativeQuery = true)
    Page<VoucherMaster> findVoucherMaster(@Param("search") String searchText, Pageable pageable);
}
