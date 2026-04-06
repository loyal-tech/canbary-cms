package com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface CustomerInventoryMappingRepo extends JpaRepository<CustomerInventoryMapping, Long>, QuerydslPredicateExecutor<CustomerInventoryMapping> {

    List<CustomerInventoryMapping> findAllByCustomerAndStatusAndQtyIsGreaterThanAndIsDeletedFalse(Customers customers, String status,Long qty);
    List<CustomerInventoryMapping> findAllByCustomerAndStatus(Customers customers, String status);
//    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalse(String connectionNo);

    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(String connectionNo,Integer customerId);
    List<CustomerInventoryMapping> findAllByItemAssemblyId(Long id);
    List<CustomerInventoryMapping> findByItemId(Long id);

    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalse(String connectionNo);

    @Query("select cim.id from CustomerInventoryMapping cim where cim.connectionNo=:connectionNo")
    List<Long> findAllIdsByConnectionNoAndIsDeletedIsFalse(String connectionNo);

    //    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatus(String connectionNo,Integer customerId, String status);
   @Query(value = "SELECT t.mapping_id, t.customer_id, t.connection_no, t.item_id " +
            "FROM tblmcustomer_inventory_mapping t " +
            "WHERE t.connection_no = :connectionNo " +
            "AND t.customer_id = :customerId " +
            "AND t.status = :status " +
            "AND t.is_deleted = false", nativeQuery = true)
    List<Object[]> findByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatus(
            @Param("connectionNo") String connectionNo,
            @Param("customerId") Integer customerId,
            @Param("status") String status);

    @Query(value = "SELECT t.mapping_id, t.customer_id, t.connection_no, t.item_id " +
            "FROM tblmcustomer_inventory_mapping t " +
            "WHERE t.connection_no = :connectionNo " +
            "AND t.customer_id = :customerId " +
            "AND t.status = :status " +
            "AND t.is_deleted = false " +
            "AND t.mvno_id IN (:mvnoId)", nativeQuery = true)
    List<Object[]> findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatusAndMvnoIdIn(
            @Param("connectionNo") String connectionNo,
            @Param("customerId") Integer customerId,
            @Param("status") String status,
            @Param("mvnoId") List<Integer> mvnoId);


    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatus(String connectionNo,Integer customerId, String status);
//    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatusAndMvnoIdIn(String connectionNo,Integer customerId, String status, List mvnoId);

//    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatusAndMvnoIdIn(String connectionNo,Integer customerId, String status, List mvnoId);

    @Query(value = "select count(*) from tblcustomers t  where t.parentcustid =:partnerId" ,nativeQuery = true)
    Integer patnerIdPresent(@Param("partnerId")Integer partnerId);

    List<CustomerInventoryMapping> findAllByCustomerIdAndPlanIdAndIsDeletedFalse(Integer customerId,Long planId);

    List<CustomerInventoryMapping> findAllByCustomerId(Integer customerId);

    @Query("select cim.id from CustomerInventoryMapping cim where cim.serviceId=:serviceId and cim.customer.id=:custId and cim.isDeleted=:false")
    List<Long> findAllIDsByServiceIdAndCustomer_IdAndIsDeletedIsFalse(Long serviceId,Integer custId);


    CustomerInventoryMapping findByItemIdAndIsDeletedFalse(Long itemId);

    CustomerInventoryMapping findByCustomer_IdAndConnectionNoAndAndIsDeletedFalse(Integer custId, String ConnectionNumber);
    @Query(value = "select t.item_id from tblmcustomer_inventory_mapping t where t.customer_id = :custId",nativeQuery = true)
    List<Long> findItemIdByCustomerId (@Param("custId") Integer custId);
    List<CustomerInventoryMapping> findAllByCustomerAndStatusAndIsDeletedFalse(Customers customers, String status);
}
