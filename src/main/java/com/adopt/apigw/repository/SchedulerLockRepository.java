package com.adopt.apigw.repository;

import com.adopt.apigw.core.entity.SchedulerLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulerLockRepository extends JpaRepository<SchedulerLock, Integer> {
    SchedulerLock findByName(String name);

    @Query(value = "SELECT locked FROM SchedulerLock WHERE name = :name")
    Boolean isSchedulerLocked(String name);
}
