package com.adopt.apigw.modules.placeOrder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.placeOrder.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
