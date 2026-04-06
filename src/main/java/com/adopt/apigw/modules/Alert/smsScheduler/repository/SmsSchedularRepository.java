package com.adopt.apigw.modules.Alert.smsScheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.Alert.smsScheduler.domain.SmsScheduler;

import java.util.List;

@Repository
public interface SmsSchedularRepository extends JpaRepository<SmsScheduler,Long> {
    public SmsScheduler findByJobId(String jobId);

    public List<SmsScheduler> findByStatusOrIsSended(Boolean status, Boolean isSended);
}
