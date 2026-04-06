package com.adopt.apigw.modules.Matrix.repository;

import com.adopt.apigw.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TatMatrixWorkFlowDetailsRepo  extends JpaRepository<TatMatrixWorkFlowDetails, Long>, QuerydslPredicateExecutor<TatMatrixWorkFlowDetails> {

    List<TatMatrixWorkFlowDetails> findAllByIsActive(Boolean isActive);

    Optional<TatMatrixWorkFlowDetails> findByWorkFlowIdAndCurrentTeamHeirarchyMappingIdAndIsActive(Long workFlowId, Integer currentTeamHeirarchyMappingId, Boolean isActive);

    List<TatMatrixWorkFlowDetails> findAllByWorkFlowIdAndEntityIdAndEventNameAndIsActive(Long workFlowId, Integer entityId, String eventName, boolean isActive);

    List<TatMatrixWorkFlowDetails> findAllByStaffIdAndEntityIdAndEventNameAndIsActive(Integer staffId, Integer entityId, String eventName, boolean isActive);

//    List<TatMatrixWorkFlowDetails> findAllByWorkFlowIdAndEntityIdAndEventNameAndIsActive(Long workFlowId, Integer entityId, String eventName, boolean isActive);
TatMatrixWorkFlowDetails findByStaffIdAndEntityIdAndEventNameAndIsActive(Integer staffId, Integer entityId, String eventName, boolean isActive);

    TatMatrixWorkFlowDetails findAllByStaffIdAndEntityIdAndEventNameAndIsActiveAndNotificationType(Integer staffId, Integer entityId, String eventName, boolean isActive, String notificationType);

    List<TatMatrixWorkFlowDetails> findAllByLevelAndIsActive(String filterValue,Boolean isactive);

    @Query(value = "select distinct  t.entityId  from TatMatrixWorkFlowDetails t where  t.level!='Level 1' and t.isActive =true")
    List<Long> getAlltatBreachdetails();

    TatMatrixWorkFlowDetails findByEventIdAndIsActive(Integer eventId,boolean isActive);

    List<TatMatrixWorkFlowDetails> findByEventIdAndIsActiveAndEventName(Integer eventId,boolean isActive, String eventName);

    TatMatrixWorkFlowDetails findByStaffIdAndEntityIdAndEventName(Integer staffId, Integer entityId, String eventName);
    List<TatMatrixWorkFlowDetails> findByStaffIdAndEntityIdAndEventNameAndCurrentTeamHeirarchyMappingId(Integer staffId, Integer entityId, String eventName,Integer nextteamhirarchymapping);

}
