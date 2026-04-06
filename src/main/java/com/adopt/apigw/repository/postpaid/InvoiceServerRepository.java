package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.DunningRule;
import com.adopt.apigw.model.postpaid.InvoiceServer;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface InvoiceServerRepository extends JpaRepository<InvoiceServer, Integer> {

    List<InvoiceServer> findByStatus(String status);
    List<InvoiceServer> findByStatusAndServertype(String status,String type);
    List<InvoiceServer> findAllByStatus(String status);

    @Query("select t from InvoiceServer t where t.isDelete=false")
    List<InvoiceServer> findAll();

    @Query("update InvoiceServer b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);
}
