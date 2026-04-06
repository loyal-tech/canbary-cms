package com.adopt.apigw.modules.InventoryManagement.ReplacementItemHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplacementItemHistoryRepo extends JpaRepository<ReplacementItemHistory,Long> {
}
