package com.adopt.apigw.modules.ippool.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.ippool.domain.IPPoolDtls;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IPPoolDtlsRepository extends JpaRepository<IPPoolDtls, Long> {
    List<IPPoolDtls> findByIpAddress(String ipAddress);

    List<IPPoolDtls> findByPoolId(Long poolId);

    Page<IPPoolDtls> findAllByPoolIdAndStatus(Long poolId, String status, Pageable pageable);

    IPPoolDtls findByAllocatedIdAndStatus(Long allocatedIp, String status);

    List<IPPoolDtls> findAllByStatusAndUnblockTimeLessThanEqual(String status, LocalDateTime unblockTime);


    @Query(value = "select * from tblippooldtls t where t.ip_address=:ipAddress", nativeQuery = true)
    List<IPPoolDtls> findAllByIpAddress(@Param("ipAddress") String ipAddress);

    @Transactional
    @Modifying
    @Query(value = "UPDATE tblippooldtls p set status ='Free' , unblock_time = null , block_by_cust_id = null where p.status = 'Block' and p.unblock_time<=:unblockTime",
            nativeQuery = true)
    void releaseIP(@Param("unblockTime") LocalDateTime unblockTime);

    @Transactional
    @Modifying
    @Query(value = "Update tblippooldtls p inner join tblcustchargedtls t on t.purchase_entity_id = p.allocated_id " +
            "left join tblipallocationdtls t2 on t2.id  = p.allocated_id set p.status = 'Free',p.allocated_id = null,t2.is_system_updated = true" +
            ",t2.termination_reason = 'Release',t2.terminated_date =:unblockTime where t.enddate <=:unblockTime and p.status = 'Allocated'",
            nativeQuery = true)
    void releaseIP2(@Param("unblockTime") LocalDateTime unblockTime);
}
