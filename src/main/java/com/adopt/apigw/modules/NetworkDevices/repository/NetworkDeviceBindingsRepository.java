package com.adopt.apigw.modules.NetworkDevices.repository;

import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDeviceBindings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Set;

public interface NetworkDeviceBindingsRepository extends JpaRepository<NetworkDeviceBindings, Long>, QuerydslPredicateExecutor<NetworkDeviceBindings> {
    List<NetworkDeviceBindings> findByDeviceId(Long id);
    List<NetworkDeviceBindings> findByParentDeviceId(Long id);
    void deleteByDeviceIdAndParentDeviceIdIn(Long id, Set<Long> parentId);
    NetworkDeviceBindings findByDeviceIdAndParentDeviceId(Long deviceId, Long parentDeviceId);
    NetworkDeviceBindings findByDeviceIdAndInBind(Long deviceId, String inPortName);
    NetworkDeviceBindings findByParentDeviceIdAndOutBind(Long parentDeviceId, String outPortName);
}
