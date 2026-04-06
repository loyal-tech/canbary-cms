package com.adopt.apigw.modules.CustomerQRLogin.repository;

import com.adopt.apigw.modules.CustomerQRLogin.domain.CustomerQRLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerQRLoginRepository extends JpaRepository<CustomerQRLogin , Long> {

    CustomerQRLogin findByCodeAndStatusEqualsIgnoreCase(String code  ,String status);

    CustomerQRLogin findByCode(String code);

}
