package com.adopt.apigw.service;

import com.adopt.apigw.core.entity.SchedulerLock;
import com.adopt.apigw.repository.SchedulerLockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SchedulerLockService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerLockService.class);

    @Value(value = "${instanceId}")
    private String instanceId;

    @Autowired
    private SchedulerLockRepository schedulerLockRepository;

    public SchedulerLock save(SchedulerLock lockScheduler) {
        return schedulerLockRepository.save(lockScheduler);
    }

    public SchedulerLock getLockSchedulerByName(String name) {
        return schedulerLockRepository.findByName(name);
    }

    public boolean isSchedulerLocked(String name) {
        Boolean isLocked = schedulerLockRepository.isSchedulerLocked(name);
        if (isLocked == null) {
            return false;
        }
        return isLocked;
    }

    public void acquireSchedulerLock(String name) {
        SchedulerLock schedulerLock = schedulerLockRepository.findByName(name);
        if (schedulerLock == null) {
            schedulerLock = new SchedulerLock();
            schedulerLock.setName(name);
        }
        schedulerLock.setLocked(true);
        schedulerLock.setLockedBy(instanceId);
        save(schedulerLock);
        logger.info("XXXXXXXXXXXX---------- " + name + " Lock Acquired by " + instanceId + " ---------XXXXXXXXXXXX");
    }

    public void releaseSchedulerLock(String name) {
        SchedulerLock lockScheduler = schedulerLockRepository.findByName(name);
        lockScheduler.setLocked(false);
        lockScheduler.setLockedBy(instanceId);
        save(lockScheduler);
    }
}
