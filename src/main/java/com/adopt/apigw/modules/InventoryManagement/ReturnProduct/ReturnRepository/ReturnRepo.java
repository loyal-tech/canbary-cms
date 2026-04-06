package com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnRepository;

import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnModel.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRepo extends JpaRepository<Return,Long>, QuerydslPredicateExecutor {

    @Query(value = "select * from tbl_return_inventory where cust_id =:id",nativeQuery = true)
    List<Return> getallforCustomer(@Param("id")Long id);
}
