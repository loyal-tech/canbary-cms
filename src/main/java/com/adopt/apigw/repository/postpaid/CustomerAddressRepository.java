package com.adopt.apigw.repository.postpaid;


import com.adopt.apigw.pojo.NewCustPojos.NewAddressListPojo;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustomerAddress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

//@JaversSpringDataAuditable
@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> , QuerydslPredicateExecutor<CustomerAddress> {

    Page<CustomerAddress> findByCustomer(Customers cust, Pageable pageable);

    List<CustomerAddress> findAllByCustomer(Customers customer);

    CustomerAddress findByAddressTypeAndCustomerAndVersion(String addressType, Customers customer , String version);
    CustomerAddress findByAddressTypeAndCustomerIdAndVersion(String addressType, Integer customerId , String version);

    @Query("select t from CustomerAddress t where t.isDelete=false")
    List<CustomerAddress> findAll();

    @Query("update CustomerAddress t set t.isDelete=true where t.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    List<CustomerAddress> findAllByCustomerAndStatus(Customers customers, String status);

    List<CustomerAddress> findAllByCustomerAndVersion(Customers customers, String version);

    CustomerAddress findByAddressTypeAndCustomerId(String addressType, Integer customerId );
    @Query(value = "SELECT DISTINCT t2.building_name, t3.name " +
            "FROM tblmsubscriberaddressrel t1 " +
            "LEFT JOIN tblmbuildingmanagement t2 " +
            "ON t1.building_mgmt_id = t2.building_mgmt_id " +
            "LEFT JOIN tblmsubarea t3 " +
            "ON t2.sub_area_id = t3.subareaid " +
            "WHERE t1.SUBSCRIBERID = :subscriberId "+
            "AND t1.version = 'NEW' "+
            "LIMIT 1"
            , nativeQuery = true)
    Map<String, Object> findBuildingAndSubareaNames(@Param("subscriberId") Integer subscriberId);





    @Query(value = "select distinct (building_number) from adoptconvergebss.tblmsubscriberaddressrel where building_mgmt_id =:buildingmgmtID and building_number is not null",nativeQuery = true)
    List<String> findUsedBuildingNumbers(Integer buildingmgmtID);

    @Query("SELECT new com.adopt.apigw.pojo.NewCustPojos.NewAddressListPojo( " +
            "a.id, a.version, a.addressType, a.areaId, a.pincodeId,  a.landmark, a.buildingNumber, a.createdByName,a.city.name,a.state.name,a.pincode.pincode) " +
            "FROM CustomerAddress a WHERE a.customer.id = :customerId and a.version = 'NEW' order by a.updatedate desc ")
    List<NewAddressListPojo> findAddressListByCustomerId(@Param("customerId") Integer customerId);

    @Query(value = "select t.landmark from tblmsubscriberaddressrel t where t.SUBSCRIBERID = :customerId",nativeQuery = true)
    List<String> findLandMarkByCustId(@Param("customerId") Integer customerId);

    List<CustomerAddress>findAllByCustomerId(Integer custId);

    @Query("SELECT COUNT(a) FROM CustomerAddress a " +
            "WHERE a.status = 'Active' AND a.shiftId IS NOT NULL AND a.customer.id = :subscriberId")
    long countActiveShiftedAddressesBySubscriberId(@Param("subscriberId") Integer subscriberId);

    @Query("SELECT COUNT(a) FROM CustomerAddress a " +
            "WHERE a.status = 'Active' " +
            "AND a.shiftId IS NOT NULL " +
            "AND a.customer.id = :subscriberId " +
            "AND a.updatedate >= :startDate")
    long countActiveShiftedAddressesBySubscriberIdWithinPeriod(@Param("subscriberId") Integer subscriberId,
                                                               @Param("startDate") LocalDateTime startDate);


}
