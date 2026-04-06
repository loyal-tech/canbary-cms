package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.WorkflowAudit;
import com.adopt.apigw.modules.WorkFlowInProgressEntity.Entity.WorkFlowInProgressData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface WorkflowAuditRepository extends JpaRepository<WorkflowAudit, Long>, QuerydslPredicateExecutor<WorkflowAudit> {

    //Getall workflowauditbycustomerid with pagination
    @Query(nativeQuery = true,
    value = "select * from adoptconvergebss.tbltworkflowaudit t where (t.event_id =:eventid and t.custcaf_id =:id)")
    Page<WorkflowAudit> findByevent_idandcustcaf_id(@Param("eventid") Integer eventid, @Param("id") Integer id, Pageable pageable);

    //Getall workflowauditbyplanid with pagination
    @Query(nativeQuery = true,
            value = "select * from adoptconvergebss.tbltworkflowaudit t where t.plan_id =:id")
    Page<WorkflowAudit> findByplan_id(@Param("id") Integer id, Pageable pageable);

    //Getall workflowauditbycreditDocid with pagination
    @Query(nativeQuery = true,
            value = "select * from adoptconvergebss.tbltworkflowaudit t where t.creditdoc_id =:id")
    Page<WorkflowAudit> findBycreditdoc_id(@Param("id") Integer id, Pageable pageable);

    @Query(nativeQuery = true,
            value = "select * from adoptconvergebss.tbltworkflowaudit t where t.custpackage_id =:id")
    Page<WorkflowAudit> findBycustPackage_id(@Param("id") Integer id, Pageable pageable);

    @Query(nativeQuery = true,
            value = "select * from adoptconvergebss.tbltworkflowaudit t where t.custcaf_id =:custcaf_id and t.staff_id =:staff_id and t.status ='Pending'")
    WorkflowAudit findByCustCAFANDStaffUser(@Param("custcaf_id") Integer custcaf_id, @Param("staff_id") Integer staff_id);

    @Query(nativeQuery = true,
            value = "select * from adoptconvergebss.tbltworkflowaudit t where t.creditdoc_id =:creditdoc_id and t.staff_id =:staff_id and t.status ='Pending'")
    WorkflowAudit findByCreditDocIDANDStaffUser(@Param("creditdoc_id") Integer creditdoc_id, @Param("staff_id") Integer staff_id);

    @Query(nativeQuery = true,
            value = "select * from adoptconvergebss.tbltworkflowaudit t where t.plan_id =:plan_id and t.staff_id =:staff_id and t.status ='Pending'")
    WorkflowAudit findByPlanIdANDStaffUser(@Param("plan_id") Integer plan_id, @Param("staff_id") Integer staff_id);

    @Query(nativeQuery = true,
            value = "select * from adoptconvergebss.tbltworkflowaudit t where t.custpackage_id =:custpackage_id and t.staff_id =:staff_id and t.status ='Pending'")
    WorkflowAudit findByCustPackIdANDStaffUser(@Param("custpackage_id") Integer custpackage_id, @Param("staff_id") Integer staff_id);
    
    //find next staff approver for caf customer
    @Query(nativeQuery = true,
            value = "select t.staff_id  from adoptconvergebss.tblcustomercafassignment t where t.custcaf_id =:id")
    Integer findByNextApprover (@Param("id") Integer id);

    @Query(nativeQuery = true,
            value = "select t.staff_id  from adoptconvergebss.tblcustomercafassignment t where t.creddoc_id =:id")
    Integer findByPaymentNextApprover (@Param("id") Integer id);

    @Query(nativeQuery = true,
            value = "select t.staff_id  from adoptconvergebss.tblcustomercafassignment t where t.custpackage_id =:id")
    Integer findByCustPackIdNextApprover (@Param("id") Integer id);

    @Query(nativeQuery = true,
            value = "select t.staff_id  from adoptconvergebss.tblcustomercafassignment t where t.plan_id =:id")
    Integer findByPlanNextApprover (@Param("id") Integer id);

    @Query(nativeQuery = true,
    value = "select t2.username from adoptconvergebss.tbltcreditdoc t \n" +
            "left join\n" +
            "adoptconvergebss.tblcustomers t2 \n" +
            "on t2.custid = t.CUSTID \n" +
            "where t.CREDITDOCID =:id")
    String findByCustomerName(@Param("id") Integer id);

    @Query(nativeQuery = true,
    value = "select max(t.workflow_audit_id), t.* from adoptconvergebss.tbltworkflowaudit t where t.custpackage_id =:id and t.next_staff_status ='Approved'")
    WorkflowAudit findById(@Param("id") Integer id);


    List<WorkflowAudit> findAllByEntityId(Integer entityID);
    @Query("select t from WorkflowAudit t where t.entityId = :entityID and t.eventName = :eventName")
    List<WorkflowAudit> findAllByEntityIdAndEventName(Integer entityID,String eventName);

    @Query(value = "CALL get_workflow_in_process_data(:mvnoid)", nativeQuery = true)
    List<Object[]> getWorkflowInProgressData(@Param("mvnoid") Integer mvnoid);

}
