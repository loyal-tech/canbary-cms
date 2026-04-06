package com.adopt.apigw.repository.radius;

import com.adopt.apigw.core.dto.ConnectionNumberDto;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@JaversSpringDataAuditable
public interface CustomerServiceMappingRepository extends JpaRepository<CustomerServiceMapping, Integer>, QuerydslPredicateExecutor<CustomerServiceMapping> {
    List<CustomerServiceMapping> findByCustId(Integer custId);

    List<CustomerServiceMapping> findAllByCustId(Integer custId);

    List<CustomerServiceMapping> findAllByCustIdIn(List<Integer> custId);


    List<CustomerServiceMapping> findAllByServiceIdIn(List<Long> serviceIds);

    List<CustomerServiceMapping> findAllByIdIn(List<Integer> serviceIds);

    CustomerServiceMapping findByConnectionNo(String connectionNo);

    CustomerServiceMapping findByConnectionNoAndCustId(String connectionNo, Integer custId);

    List<CustomerServiceMapping> findAllByConnectionNo(String connectionNo);

    @Query(nativeQuery = true
            , value = "select * from tbltcustomerservicemapping t where t.lease_circuit_name like '%' :circuitName '%'"
            , countQuery = "select count(*) from tbltcustomerservicemapping t where t.lease_circuit_name like '%' :circuitName '%'")
    List<CustomerServiceMapping> findByLeaseCircuitNameLike(String circuitName);


    List<CustomerServiceMapping> findAllByCustIdAndInvoiceType(Integer cutId, String invoiceType);

    List<CustomerServiceMapping> findAllByCustIdAndServiceIdIn(Integer cutId, List<Long> serviceIds);

    boolean existsByCustIdInAndServiceIdInAndInvoiceType(List<Integer> cutId, List<Long> serviceIds, String invoiceType);

    List<CustomerServiceMapping> findAllByCustIdInAndServiceIdInAndInvoiceType(List<Integer> cutId, List<Long> serviceId, String invoiceType);

    Optional<CustomerServiceMapping> findByIdAndStatus(Integer id, String status);

    boolean existsByCustIdAndStatusNotIn(Integer custId, List<String> status);

    @Query("select csm.invoiceType from CustomerServiceMapping csm where csm.id= :custServId")
    String findInvoiceTypeByCustServiceId(Integer custServId);

    List<CustomerServiceMapping> findAllByCustIdAndStatus(Integer custId, String status);

    List<CustomerServiceMapping> findAllByCustIdAndStatusIn(Integer custId, List<String> status);

    Long countByStatusAndCustId(String status, Integer custId);

    @Query("select csm.id from CustomerServiceMapping csm where csm.custId= :custId")
    List<Integer> custServicemappingIdByCustId(Integer custId);

    @Query("SELECT csm.id FROM CustomerServiceMapping csm WHERE csm.serviceId = :serviceId AND csm.custId = :custId")
    List<Integer> custServicemappingIdByServiceIdAndCustomerId(@Param("serviceId") Long serviceId, @Param("custId") Integer custId);

    @Query("SELECT csm.id FROM CustomerServiceMapping csm WHERE csm.custId = :custId")
    List<Integer> custServicemappingIdByCustomerId(@Param("custId") Integer custId);

    @Query("SELECT csm.serviceId FROM CustomerServiceMapping csm WHERE csm.custId = :custId")
    List<Long> serviceIdByCustomerId(@Param("custId") Integer custId);


    CustomerServiceMapping findAllByCustIdAndServiceId(Integer cutId, Long serviceIds);

    List<CustomerServiceMapping> findAllByStatus(String status);

    @Query("select cs from CustomerServiceMapping cs where cs.status = :status and cast (datediff(cs.serviceResumeDate, curdate()) as integer) = 0")
    List<CustomerServiceMapping> findAllByStatusAndDate(@Param("status") String status);

    @Modifying
    @Transactional
    @Query("Update  CustomerServiceMapping cs set cs.serviceHoldAttempts=0 where cs.serviceHoldAttempts >0")
    int resetServiceHoldCount();


    @Query(value = "SELECT m.id AS custServiceMappingId, " +
            "m.custid AS customerId, " +
            "COALESCE(m.mvnoid, c.MVNOID) AS mvnoId, " +
            "COALESCE(m.partner_id, c.partnerid) AS partnerId, " +
            "COALESCE(m.createbyname, c.createbyname) AS mvnaoName " +
            "FROM tbltcustomerservicemapping m " +
            "LEFT JOIN adoptconvergebss.tblcustomers c ON m.custid = c.custid " +
            "WHERE (m.connection_no IS NULL OR TRIM(m.connection_no) = '')",
            nativeQuery = true)
    List<ConnectionNumberDto> findCustomersWithNullOrEmptyConnectionNumber();

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbltcustomerservicemapping " + "SET connection_no = :connectionNumber, " + "    partner_id = :partnerId, " + "    mvnoid = :mvnoId " + "WHERE id = :custServiceMappingId", nativeQuery = true)
    Integer updateConnectionNumberByMappingId(@Param("custServiceMappingId") Integer custServiceMappingId,
                                              @Param("connectionNumber") String connectionNumber,
                                              @Param("partnerId") Integer partnerId,
                                              @Param("mvnoId") Integer mvnoId);

    @Query("select csm.custId from CustomerServiceMapping csm where csm.status = :status")
    List<Integer> findByServiceStatus(@Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbltcustomerservicemapping SET status = :status, previous_status = :status WHERE id IN (:custserviceMappingIds)", nativeQuery = true)
    void updateServiceStatus(@Param("custserviceMappingIds") List<Integer> custserviceMappingIds,@Param("status") String status);
}