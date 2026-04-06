package com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustInvParamsRepo  extends JpaRepository<CustInvParams,Long>, QuerydslPredicateExecutor<CustInvParams> {

    List<CustInvParams> findAllByCustSerMapId(Long custSerMapId);

    List<CustInvParams> findAllByCustInvId(Long custInvMapId);

    @Query(value = "SELECT param_name, param_value FROM tblmcustinventoryparams WHERE cust_inv_id = :custInvMapId", nativeQuery = true)
    List<Object[]> findParamNameAndValueByCustInvId(Long custInvMapId);
}
