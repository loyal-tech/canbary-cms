package com.adopt.apigw.repository.postpaid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.CustMacMappping;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

//@JaversSpringDataAuditable
@Repository
public interface CustMacMapppingRepository extends JpaRepository<CustMacMappping, Integer>, QuerydslPredicateExecutor<CustMacMappping> {

    List<CustMacMappping> findByCustomerId(Integer custId);

    List<CustMacMappping> findByCustomerIdAndIsDeletedIsFalse(Integer custId);
    @Transactional
    void deleteByCustomerId(Integer custId);

    List<CustMacMappping> findByMacAddressAndIsDeletedIsFalseAndMacAddressIsNotNull(String maccAddress);

    boolean existsByMacAddressInAndIsDeletedIsFalse(List<String> macs);

    @Modifying
    @Query(value = "DELETE FROM tblcustmacmapping WHERE macaddress in :macAddress and custid =:custId",nativeQuery = true)
    void deleteByCustomerIdAndMacAddressIn(@Param("custId") Integer custId , @Param("macAddress") List macAddress);

    CustMacMappping findByCustsermappingidAndAndIsDeletedFalse(Integer custSerMapid);

    List<CustMacMappping> findAllByCustsermappingidAndIsDeletedFalse(Integer custsermappingid);


    CustMacMappping findByCustomerIdAndCustsermappingid(Long custId, Integer custsermappingid);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tblcustmacmapping WHERE macaddress =:mac and custid =:custId",nativeQuery = true)
    void deleteByCustomerIdAndMac(Integer custId, String mac);

    @Query("SELECT c FROM CustMacMappping c " +
            "WHERE REPLACE(REPLACE(REPLACE(c.macAddress, '-', ''), ':', ''), '.', '') = :normalizedMac " +
            "AND c.customer.id = :customerId")
    Optional<CustMacMappping> findByNormalizedMacAndCustomer(@Param("normalizedMac") String normalizedMac,
                                                             @Param("customerId") Integer customerId);

    @Query("SELECT c FROM CustMacMappping c " +
            "WHERE REPLACE(REPLACE(REPLACE(c.macAddress, '-', ''), ':', ''), '.', '') = :normalizedMac " +
            "AND c.customer.id = :customerId")
    List<CustMacMappping> findAllByNormalizedMacAndCustomer(@Param("normalizedMac") String normalizedMac,
                                                             @Param("customerId") Integer customerId);

    @Query("SELECT c.macRetentionDate FROM CustMacMappping c " +
            "WHERE c.customer.id = :custid AND c.isDeleted = false " +
            "ORDER BY ABS(TIMESTAMPDIFF(SECOND, c.macRetentionDate, CURRENT_TIMESTAMP)) ASC")
    List<Timestamp> findNearestMacRetentionDateByCustomerId(@Param("custid") Integer custid);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value = "DELETE FROM tblcustmacmapping m WHERE m.custid = :customerId", nativeQuery = true)
    void deleteByCustomerid(@Param("customerId") Long customerId);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value = "DELETE FROM tblcustmacmapping m WHERE m.custid = :customerId AND m.macaddress = :mac", nativeQuery = true)
    void deleteByCustomeridAndMacAddress(@Param("customerId") Long customerId, @Param("mac") String mac);
}
