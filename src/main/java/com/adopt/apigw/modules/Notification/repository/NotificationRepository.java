package com.adopt.apigw.modules.Notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.adopt.apigw.modules.Notification.domain.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByCategoryAndStatus(String category, String status);

    List<Notification> findAllByName(String name);

    @Query(nativeQuery = true
            , value = "select * from tblnotifications t where t.name like '%' :s1 '%' and t.is_deleted = 0"
            , countQuery = "select count(*) from tblnotifications t where t.name like '%' :s1 '%' and t.is_deleted = 0")
    Page<Notification> findAllByNameOrStatus(Pageable pageable, @Param("s1") String name);

    @Query(nativeQuery = true
            , value = "select * from tblnotifications t where t.is_deleted = 0"
            , countQuery = "select count(*) from tblnotifications t where t.is_deleted = 0")
    Page<Notification> findAll(Pageable pageable);
}
