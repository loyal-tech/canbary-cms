package com.adopt.apigw.service.common;

import com.adopt.apigw.constants.CaseConstants;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.model.common.QWorkflowAudit;
import com.adopt.apigw.model.common.WorkflowAudit;
import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import com.adopt.apigw.modules.Teams.domain.QHierarchy;
import com.adopt.apigw.modules.Teams.domain.TeamHierarchyMapping;
import com.adopt.apigw.modules.Teams.domain.TeamUserMapping;
import com.adopt.apigw.modules.WorkFlowInProgressEntity.Entity.WorkFlowInProgressData;

import com.adopt.apigw.modules.tickets.domain.Case;
import com.adopt.apigw.modules.tickets.domain.QCase;
import com.adopt.apigw.modules.tickets.domain.QTicketAssignStaffMapping;
import com.adopt.apigw.modules.tickets.domain.TicketAssignStaffMapping;
import com.adopt.apigw.repository.common.WorkflowAuditRepository;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkflowAuditService {
    @Autowired
    WorkflowAuditRepository workflowAuditRepository;


    @Autowired
    ClientServiceSrv clientServiceSrv;

    public Integer MAX_PAGE_SIZE;
    public Map<String, String> sortColMap = new HashMap<>();



    public PageRequest pageRequest = null;
    public WorkflowAudit saveAudit(Integer eventId, String eventName, Integer entityId, String entityName, Integer actionByStaffId, String actionByUserName, String action, LocalDateTime actionDateTime, String remark) {
        WorkflowAudit workflowAudit = new WorkflowAudit();
        workflowAudit.setEventId(eventId);
        workflowAudit.setEventName(eventName);
        workflowAudit.setEntityId(entityId);
        workflowAudit.setEntityName(entityName);
        workflowAudit.setActionByStaffId(actionByStaffId);
        workflowAudit.setActionByName(actionByUserName);
        workflowAudit.setAction(action);
        workflowAudit.setActionDateTime(actionDateTime);
        workflowAudit.setRemark(remark);
        return workflowAuditRepository.save(workflowAudit);
    }
    public WorkflowAudit saveAudit(Integer eventId, String eventName, Integer entityId, String entityName, Integer actionByStaffId, String actionByUserName, String action, LocalDateTime actionDateTime, String remark,Integer cust_id,String approval_status) {
        WorkflowAudit workflowAudit = new WorkflowAudit();
        workflowAudit.setEventId(eventId);
        workflowAudit.setEventName(eventName);
        workflowAudit.setEntityId(entityId);
        workflowAudit.setEntityName(entityName);
        workflowAudit.setActionByStaffId(actionByStaffId);
        workflowAudit.setActionByName(actionByUserName);
        workflowAudit.setAction(action);
        workflowAudit.setActionDateTime(actionDateTime);
        workflowAudit.setRemark(remark);
        workflowAudit.setCustId(cust_id);
        workflowAudit.setApprovalStatus(approval_status);
        return workflowAuditRepository.save(workflowAudit);
    }

    public GenericDataDTO getListByCustomerId(
            Integer page, Integer pageSize, String sortBy, Integer sortOrder,
            List<GenericSearchModel> filters, Integer entityId, String eventName) {

        // Validate and set default sorting column if null
        String sortingColumn = (sortBy != null && !sortBy.isEmpty()) ? sortBy : "id";

        // Generate pageable request
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortingColumn, sortOrder);

        // Build QueryDSL boolean expression
        QWorkflowAudit qWorkflowAudit = QWorkflowAudit.workflowAudit;
        BooleanExpression booleanExpression = qWorkflowAudit.entityId.eq(entityId)
                .and(qWorkflowAudit.eventName.eq(eventName));

        // Execute query and build response
        Page<WorkflowAudit> auditPage = workflowAuditRepository.findAll(booleanExpression, pageRequest);
        return makeGenericResponse(new GenericDataDTO(), auditPage);
    }


    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
        return pageRequest;
    }

    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<WorkflowAudit> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    public GenericDataDTO filterAudit(String filterColumn,String filterValue, PaginationRequestDTO requestDTO) {
        PageRequest pageRequest = generatePageRequest(requestDTO.getPage(), requestDTO.getPageSize(), "id", CommonConstants.SORT_ORDER_DESC);
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        QWorkflowAudit qWorkflowAudit=QWorkflowAudit.workflowAudit;
        BooleanExpression booleanExpression=qWorkflowAudit.isNotNull();
        if(filterColumn.equalsIgnoreCase(CommonConstants.WORKFLOW_AUDIT_STATUS.APPROVAL_STATUS) )  {
            booleanExpression=booleanExpression.and(qWorkflowAudit.approvalStatus.equalsIgnoreCase(filterValue));
        }
        if(filterColumn.equalsIgnoreCase(CommonConstants.WORKFLOW_AUDIT_STATUS.CUST_ID)){
            booleanExpression=booleanExpression.and(qWorkflowAudit.custId.eq(Integer.valueOf(filterValue)));

        }
        Page<WorkflowAudit> paginationList = workflowAuditRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    public List<WorkFlowInProgressData> getWorkflowInProgressData(Integer mvnoid) {
        List<Object[]> results = workflowAuditRepository.getWorkflowInProgressData(mvnoid);
        return results.stream().map(result -> new WorkFlowInProgressData(
                convertToLong(result[0]),  // Convert BigInteger to Integer
                (String) result[1],
                (String) result[2],
                (String) result[3],
                (String) result[4],
                (String) result[5]
        )).collect(Collectors.toList());
    }



    private Long convertToLong(Object value) {
        if (value instanceof BigInteger) {
            return ((BigInteger) value).longValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }
    }
}
