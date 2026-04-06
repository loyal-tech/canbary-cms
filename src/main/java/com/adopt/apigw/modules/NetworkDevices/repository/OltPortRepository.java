package com.adopt.apigw.modules.NetworkDevices.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.NetworkDevices.domain.OLTPortDetails;

public interface OltPortRepository extends JpaRepository<OLTPortDetails,Long> {
}
