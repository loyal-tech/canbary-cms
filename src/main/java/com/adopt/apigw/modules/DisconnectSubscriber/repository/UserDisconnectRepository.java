package com.adopt.apigw.modules.DisconnectSubscriber.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.DisconnectSubscriber.domain.UserDisconnect;

public interface UserDisconnectRepository extends JpaRepository<UserDisconnect,Long> {
}
