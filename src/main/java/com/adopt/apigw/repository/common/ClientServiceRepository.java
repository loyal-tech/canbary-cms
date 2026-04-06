package com.adopt.apigw.repository.common;

import com.adopt.apigw.pojo.ClientServicePojo;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.common.ClientService;

import java.util.List;

@Repository
//@JaversSpringDataAuditable
public interface ClientServiceRepository extends JpaRepository<ClientService, Integer> {

   // ClientService findByName(String name);
    ClientService findByNameAndMvnoId(String name, Integer mvnoId);

    ClientService getByNameAndMvnoIdIn(String name, List mvnoIds);


    ClientService getByNameAndMvnoIdEquals(String name, Integer mvnoId);
    ClientService getByNameAndMvnoId(String name, Integer mvnoIds);

//    @Query(value = "select value from tblclientservice m where m.name=:name",nativeQuery = true)
//    String findValueByName(@Param(value = "name") String name);

    @Query(value = "select value from tblclientservice m where m.name=:name and m.MVNOID=:mvnoId",nativeQuery = true)
    String findValueByNameandMvnoId(@Param(value = "name") String name,@Param(value = "mvnoId") Integer mvnoId);

    boolean existsByNameAndMvnoId(String name, Integer mvnoId);

    @Query(value = "SELECT MAX(m.id) FROM ClientService m")
    Integer findlast();

    @Query(value = "select value from tblclientservice m where m.name=:name and m.mvnoId=:mvnoId",nativeQuery = true)
    String findValueByNameAndMvnoId(@Param(value = "name") String name,@Param(value = "mvnoId") Integer mvnoId);



    List<ClientService> findAllByNameAndMvnoIdIn(String name, List<Integer> mvnoids);

    List<ClientService> findAllByNameAndMvnoId(String name, Integer mvnoId);


}
