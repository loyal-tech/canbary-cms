package com.adopt.apigw.repository;

import com.adopt.apigw.model.common.CustomerNotes;
import com.adopt.apigw.model.common.Customers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerNotesRepository extends JpaRepository<CustomerNotes, Integer>{
    @Query(value = "SELECT * FROM tbltcustomernotes cn WHERE cn.custid = :customerId order by cn.customer_notes_id DESC",nativeQuery = true)
    Page<CustomerNotes> findByCustomerId(@Param("customerId") Integer customerId, Pageable pageable);

}