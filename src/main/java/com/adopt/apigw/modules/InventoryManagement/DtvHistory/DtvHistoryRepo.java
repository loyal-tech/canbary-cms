package com.adopt.apigw.modules.InventoryManagement.DtvHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DtvHistoryRepo extends JpaRepository<DtvHistory,Long> {
    List<DtvHistory> findAllByCustomerId(Long id);

    List<DtvHistory> findAllByCustomerIdIn(List<Long> id);
}
