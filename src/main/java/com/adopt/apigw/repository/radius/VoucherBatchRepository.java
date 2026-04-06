package com.adopt.apigw.repository.radius;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.radius.VoucherBatch;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface VoucherBatchRepository extends JpaRepository<VoucherBatch, Integer> {
    @Query(value = "select * from tblvoucherbatch where lower(vcname) like '%' || :search || '%'  order by id desc",
            countQuery = "select count(*) from tblvoucherbatch where lower(vcname) like '%' || :search || '%' ",
            nativeQuery = true)
    Page<VoucherBatch> findVoucherBatch(@Param("search") String searchText, Pageable pageable);

    @Query(value = "SELECT * FROM tblvoucherbatch WHERE tblvoucherbatch.vcid=1",
            countQuery = "SELECT count(*) FROM tblvoucherbatch WHERE tblvoucherbatch.vcid=1",
            nativeQuery = true)
    Page<VoucherBatch> getListById(@Param("vcid") String id, Pageable pageable);


    List<VoucherBatch> findByvcId(Integer vcid);

    VoucherBatch findByvoucherCode(String voucherCode);
}
