package com.adopt.apigw.repository.postpaid;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.InvoiceServer;
import com.adopt.apigw.model.postpaid.Location;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface LocationRepository extends JpaRepository<Location, Integer>, QuerydslPredicateExecutor<Location> {

    @Query(value = "select * from tbllocation where lower(name) like '%' :search  '%' order by locationid",
            countQuery = "select count(*) from tbllocation where lower(name) like '%' :search '%'",
            nativeQuery = true)
    Page<Location> searchEntity(@Param("search") String searchText, Pageable pageable);

    List<Location> findByStatus(String status);

    @Query("select t from Location t where t.isDelete=false")
    List<Location> findAll();

    @Query("update Location b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);
}
