package com.adopt.apigw.modules.Alert.emailSchedular.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.Alert.emailSchedular.domain.Scheduler;

import java.util.List;

@Repository
public interface SchedularRepository extends JpaRepository<Scheduler,Long> {
    public Scheduler findByJobId(String jobId);

    public List<Scheduler> findByStatusOrIsSended(Boolean status, Boolean isSended);
}
