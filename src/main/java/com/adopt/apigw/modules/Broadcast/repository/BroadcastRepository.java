package com.adopt.apigw.modules.Broadcast.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.Broadcast.domain.Broadcast;

public interface BroadcastRepository extends JpaRepository<Broadcast,Long> {
}
