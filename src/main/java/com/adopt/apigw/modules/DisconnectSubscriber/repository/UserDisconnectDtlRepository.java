package com.adopt.apigw.modules.DisconnectSubscriber.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.DisconnectSubscriber.domain.UserDisconnectDtl;

public interface UserDisconnectDtlRepository extends JpaRepository<UserDisconnectDtl,Long> {
}
