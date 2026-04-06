package com.adopt.apigw.modules.servicePlan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.servicePlan.domain.Services;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Services, Long> {

    List<Services> findServicesByIdIn(List<Long> serviceIds);
    Services findServicesById(Long serviceIds);

    @Query("SELECT s.id FROM Services s WHERE s.serviceName IN :serviceNames")
    Long findServiceIdsByName(@Param("serviceNames") String serviceNames);

    @Query("SELECT s FROM Services s WHERE s.id =:id")
    Services findServiceId(@Param("id") Long id);

    @Query("SELECT s.serviceName FROM Services s WHERE s.id IN :serviceIds")
    List<String> findServiceNamesByIds(@Param("serviceIds") List<Long> serviceIds);

    @Query(value = "SELECT s.servicename FROM tblmservices s WHERE s.serviceid = :serviceId",nativeQuery = true)
    String findServiceNameById(@Param("serviceId") Integer id);



}
