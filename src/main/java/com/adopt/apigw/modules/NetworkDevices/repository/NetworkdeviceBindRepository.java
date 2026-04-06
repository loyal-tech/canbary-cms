package com.adopt.apigw.modules.NetworkDevices.repository;

import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDeviceBind;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDeviceBindings;
import com.adopt.apigw.modules.NetworkDevices.model.NetworkDeviceBindDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface NetworkdeviceBindRepository extends JpaRepository<NetworkDeviceBind, Long>, QuerydslPredicateExecutor<NetworkDeviceBindings> {

//    List<NetworkDeviceBind> findByDeviceId(Long id);

    List<NetworkDeviceBind> findByCurrentDeviceId(Long id);

    NetworkDeviceBind findTopByOrderByIdDesc();

}
