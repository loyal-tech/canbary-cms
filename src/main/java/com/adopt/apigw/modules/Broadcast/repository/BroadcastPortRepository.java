package com.adopt.apigw.modules.Broadcast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.Broadcast.domain.BroadcastPorts;

import java.util.List;

@Repository
public interface BroadcastPortRepository  extends JpaRepository<BroadcastPorts,Long> {
}
