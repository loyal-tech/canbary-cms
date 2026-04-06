package com.adopt.apigw.audit;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.radius.ACLRepositroy;
import com.adopt.apigw.service.common.StaffUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    @Autowired
    private Javers javers;

    @Autowired
    private ACLRepositroy aclrepository;

    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private StaffUserRepository staffUserRepository;

    ObjectMapper mapper = new ObjectMapper();


    public List<AuditResponse> getChangesByEntity(AuditSearchRequest auditSearchRequestPojo) throws ClassNotFoundException, JsonProcessingException {
        AuditSearchRequest auditSearchRequest = getAuditSearchRequest(auditSearchRequestPojo);
        List<AuditResponse> auditResponseList = new ArrayList<AuditResponse>();
        JqlQuery jqlQuery = null;
        jqlQuery = getQueryBuilder(auditSearchRequest).build();
        Changes changes = javers.findChanges(jqlQuery);
        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setClassName(auditSearchRequest.getClassName());
        auditResponse.setChangesObject(javers.getJsonConverter().toJson(changes));
        auditResponseList.add(auditResponse);
        return auditResponseList;
    }

    public List<AuditResponse> getShadowByEntity(AuditSearchRequest auditSearchRequestPojo) throws ClassNotFoundException, JsonProcessingException {
        AuditSearchRequest auditSearchRequest = getAuditSearchRequest(auditSearchRequestPojo);
        JqlQuery jqlQuery = null;
        jqlQuery = getQueryBuilder(auditSearchRequest).build();
        List<Shadow<Object>> shadows = javers.findShadows(jqlQuery);
        List<AuditResponse> auditResponseList = new ArrayList<AuditResponse>();
        if (shadows != null && shadows.size() > 0) {
            for (Shadow<?> shadowList : shadows) {
                AuditResponse auditResponse = new AuditResponse();
                auditResponse.setClassName(auditSearchRequest.getClassName());
                auditResponse.setEntityObject(mapper.writeValueAsString(shadowList.get()));
                CommitMetadata commitMetadata = shadowList.getCommitMetadata();
                auditResponse.setAuthor(commitMetadata.getAuthor());
                auditResponse.setCommitDate(commitMetadata.getCommitDate());
                auditResponse.setIpAddress(commitMetadata.getProperties().get("ip_address"));
                auditResponse.setUserName(commitMetadata.getProperties().get("user_name"));
                auditResponse.setUserId(commitMetadata.getProperties().get("user_id"));
                auditResponseList.add(auditResponse);
            }
        }
        return auditResponseList;
    }

    public String getShadowList(AuditSearchRequest auditSearchRequest) throws ClassNotFoundException {
        JqlQuery jqlQuery = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fromDate = LocalDate.parse(auditSearchRequest.getFromDate(), formatter).atStartOfDay();
        LocalDateTime toDate = LocalDate.parse(auditSearchRequest.getToDate(), formatter).atStartOfDay();
        if (auditSearchRequest.getAuthor() != null && !auditSearchRequest.getAuthor().equalsIgnoreCase("")) {
            jqlQuery = getQueryBuilder(auditSearchRequest)
                    .from(fromDate)
                    .to(toDate)
                    .byAuthor(auditSearchRequest.getAuthor()).withChildValueObjects()
                    .build();
        } else {
            jqlQuery = getQueryBuilder(auditSearchRequest)
                    .from(fromDate)
                    .to(toDate).withChildValueObjects()
                    .build();
        }

        List<Shadow<Object>> shadows = javers.findShadows(jqlQuery);
        return javers.getJsonConverter().toJson(shadows);
    }

    public List<AuditResponse> getCodSnapshot(AuditSearchRequest auditSearchRequestPojo) throws ClassNotFoundException {
        AuditSearchRequest auditSearchRequest = getAuditSearchRequest(auditSearchRequestPojo);
        JqlQuery jqlQuery = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fromDate = LocalDate.parse(auditSearchRequest.getFromDate(), formatter).atStartOfDay();
        LocalDateTime toDate = LocalDate.parse(auditSearchRequest.getToDate(), formatter).atStartOfDay();

        if (auditSearchRequest.getAuthor() != null && !auditSearchRequest.getAuthor().equalsIgnoreCase("")) {
            jqlQuery = getQueryBuilder(auditSearchRequest)
                    .from(fromDate)
                    .to(toDate)
                    .byAuthor(getUsernameById(Integer.parseInt(auditSearchRequest.getAuthor())))
                    .build();
        } else {
            jqlQuery = getQueryBuilder(auditSearchRequest)
                    .from(fromDate)
                    .to(toDate)
                    .build();
        }

        List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery);

        List<AuditResponse> auditResponseList = new ArrayList<AuditResponse>();
        for (CdoSnapshot cdoSnapshot : snapshots) {

            AuditResponse auditResponse = new AuditResponse();

            CdoSnapshotState cdoSnapshotState = cdoSnapshot.getState();

            CommitMetadata commitMetadata = cdoSnapshot.getCommitMetadata();

            if (commitMetadata.getId() != null)
                auditResponse.setMetadataId(Double.parseDouble(String.valueOf(commitMetadata.getId())));

            if (cdoSnapshot.getManagedType().getName() != null)
                auditResponse.setClassPath(String.valueOf(cdoSnapshot.getManagedType().getName()));

            if (cdoSnapshot.getManagedType().getName() != null)
                auditResponse.setClassName(getClassNameByPath(cdoSnapshot.getManagedType().getName()));

            if (commitMetadata.getAuthor() != null)
                auditResponse.setAuthor(commitMetadata.getAuthor());

            if (commitMetadata.getCommitDate() != null)
                auditResponse.setCommitDate(commitMetadata.getCommitDate());

            if (commitMetadata.getProperties().get("ip_address") != null)
                auditResponse.setIpAddress(commitMetadata.getProperties().get("ip_address"));

            if (commitMetadata.getProperties().get("user_name") != null)
                auditResponse.setUserName(commitMetadata.getProperties().get("user_name"));

            if (commitMetadata.getProperties().get("user_id") != null)
                auditResponse.setUserId(commitMetadata.getProperties().get("user_id"));

            if (String.valueOf(cdoSnapshot.getType()).equalsIgnoreCase("INITIAL")) {

                auditResponse.setEntityId(Integer.parseInt(cdoSnapshot.getState().getPropertyValue("id").toString()));

                if (cdoSnapshotState != null)
                    auditResponse.setEntityState(javers.getJsonConverter().toJson(cdoSnapshotState));

                auditResponse.setOperationType("CREATE");

                if (cdoSnapshotState.getPropertyValue("createdate") != null)
                    auditResponse.setCreatedate(String.valueOf(cdoSnapshotState.getPropertyValue("createdate")));

                if (cdoSnapshotState.getPropertyValue("createdById") != null)
                    auditResponse.setCreatedById(Integer.parseInt(String.valueOf(cdoSnapshotState.getPropertyValue("createdById"))));

                if (cdoSnapshotState.getPropertyValue("updatedate") != null)
                    auditResponse.setUpdatedate(String.valueOf(cdoSnapshotState.getPropertyValue("updatedate")));

                if (cdoSnapshotState.getPropertyValue("lastModifiedById") != null)
                    auditResponse.setLastModifiedById(Integer.parseInt(String.valueOf(cdoSnapshotState.getPropertyValue("lastModifiedById"))));

            } else if (String.valueOf(cdoSnapshot.getType()).equalsIgnoreCase("UPDATE")) {

                if (cdoSnapshot.getState().getPropertyValue("id") != null)
                    auditResponse.setEntityId(Integer.parseInt(cdoSnapshot.getState().getPropertyValue("id").toString()));

                if (cdoSnapshotState != null)
                    auditResponse.setEntityState(javers.getJsonConverter().toJson(cdoSnapshotState));

                auditResponse.setOperationType("UPDATE");

                if (cdoSnapshotState.getPropertyValue("updatedate") != null)
                    auditResponse.setUpdatedate(String.valueOf(cdoSnapshotState.getPropertyValue("updatedate")));

                if (cdoSnapshotState.getPropertyValue("lastModifiedById") != null)
                    auditResponse.setLastModifiedById(Integer.parseInt(String.valueOf(cdoSnapshotState.getPropertyValue("lastModifiedById"))));

            } else {
                auditResponse.setOperationType("DELETE");
            }
            auditResponseList.add(auditResponse);
        }
        auditResponseList.sort(Comparator.comparing(AuditResponse::getCommitDate));

        return auditResponseList;

    }

    public String Snapshot() {
        List<CdoSnapshot> snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject().build());
        return javers.getJsonConverter().toJson(snapshots);
    }

    public QueryBuilder getQueryBuilder(AuditSearchRequest auditSearchRequestPojo) throws ClassNotFoundException {
        QueryBuilder jqlQuery = null;

        AuditSearchRequest auditSearchRequest = getAuditSearchRequest(auditSearchRequestPojo);
        if (auditSearchRequest.getMetadataId() != null && auditSearchRequest.getEntityId() != null && !"".equalsIgnoreCase(auditSearchRequest.getClassPath()) && auditSearchRequest.getClassPath() != null) {
            jqlQuery = QueryBuilder.byInstanceId(auditSearchRequest.getEntityId(), auditSearchRequest.getClassPath()).withCommitId(BigDecimal.valueOf(auditSearchRequest.getMetadataId())).withScopeDeepPlus();
        } else if ("0".equalsIgnoreCase(auditSearchRequest.getEntity()) || auditSearchRequest.getEntity() == null) {
            jqlQuery = QueryBuilder.anyDomainObject();
        } else {
            Class<?> className = Class.forName(auditSearchRequest.getClassPath());
            jqlQuery = QueryBuilder.byClass(className).withChildValueObjects();
        }
        return jqlQuery;
    }

    public List<EntityPojo> getAllEntityPojo() {
        List<EntityPojo> entityPojo = new ArrayList<EntityPojo>();
        List<Object[]> resultSet = aclrepository.generateACLList();
        Object[] resultFields = null;
        if (resultSet != null && resultSet.size() > 0) {
            for (int i = 0; i < resultSet.size(); i++) {
                resultFields = resultSet.get(i);
                Integer id = Integer.parseInt(resultFields[0].toString());
                String classPath = String.valueOf(resultFields[1].toString());
                String className = String.valueOf(resultFields[2].toString());
                entityPojo.add(new EntityPojo(id, classPath, className));
            }
        }
        return entityPojo;
    }

    public AuditSearchRequest getAuditSearchRequest(AuditSearchRequest auditSearchRequest) {
        List<EntityPojo> entity = getAllEntityPojo();
        if (auditSearchRequest.getEntity() != null && !"".equalsIgnoreCase(auditSearchRequest.getEntity())) {
            entity = getAllEntityPojo().stream()
                    .filter(item -> item.getId() == Integer.parseInt(auditSearchRequest.getEntity()))
                    .collect(Collectors.toList());
            if (entity.size() > 0) {
                //auditSearchRequest.setEntity(entity.get(0).getClassName());
                auditSearchRequest.setClassName(entity.get(0).getClassName());
                auditSearchRequest.setClassPath(entity.get(0).getClassPath());
            }

        }
        if (auditSearchRequest.getClassPath() != null && !"".equalsIgnoreCase(auditSearchRequest.getClassPath())) {
            entity = getAllEntityPojo().stream()
                    .filter(item -> item.getClassPath().equalsIgnoreCase(auditSearchRequest.getClassPath()))
                    .collect(Collectors.toList());
            if (entity.size() > 0) {
                auditSearchRequest.setClassName(entity.get(0).getClassName());
                auditSearchRequest.setClassPath(entity.get(0).getClassPath());
            }
        }
        return auditSearchRequest;
    }

    public String getClassNameByPath(String path) {
        List<EntityPojo> entity = getAllEntityPojo();
        entity = getAllEntityPojo().stream()
                .filter(item -> item.getClassPath().equalsIgnoreCase(path))
                .collect(Collectors.toList());
        if (entity.size() > 0) {
            return entity.get(0).getClassName();
        }
        return "";
    }

    public String getUsernameById(Integer id) {
        if (id != null) {
            StaffUser user = staffUserRepository.findById(id).get();
            return user.getUsername();
        }
        return "";
    }
}
