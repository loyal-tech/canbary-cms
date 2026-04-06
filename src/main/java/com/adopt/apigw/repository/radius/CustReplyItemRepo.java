package com.adopt.apigw.repository.radius;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.radius.CustReplyItem;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface CustReplyItemRepo extends JpaRepository<CustReplyItem, Integer> {

    List<CustReplyItem> findBycustid(Integer custid);
}
