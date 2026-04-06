package com.adopt.apigw.modules.InventoryManagement.generateremoveInventoryRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GenerateRemoveRequestRepo extends JpaRepository<GenerateRemoveRequest,Long>, QuerydslPredicateExecutor<GenerateRemoveRequest> {
    GenerateRemoveRequest findByCustomerinventoryId(Long id);
    GenerateRemoveRequest findByCustomerinventoryIdAndCustomeridAndMacmappingid(Long customerInventoryId, Long customerId, Long macMappingId);
    GenerateRemoveRequest findByCustomerinventoryIdAndIsDeletedFalse(Long id);
}
