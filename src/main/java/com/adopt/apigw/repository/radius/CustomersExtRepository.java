package com.adopt.apigw.repository.radius;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.common.Customers;

//@JaversSpringDataAuditable
@Repository
public interface CustomersExtRepository extends JpaRepository<Customers, Integer> {

/*	@Query("SELECT t FROM customers t WHERE " +
            "LOWER(t.firstname) LIKE LOWER(CONCAT('%',:searchTerm, '%')) OR " +
            "LOWER(t.lastname) LIKE LOWER(CONCAT('%',:searchTerm, '%')) OR " +  
            "LOWER(t.username) LIKE LOWER(CONCAT('%',:searchTerm, '%'))"
			)
//*/
    //Page<Customers> findAllWithSearch(String searchTerm, Pageable pageRequest);
}
