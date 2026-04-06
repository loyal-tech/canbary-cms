package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.CustIpMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustIpMappingRepo extends JpaRepository<CustIpMapping,Integer> {


    boolean existsByIpAddressIn(List<String> ipAddress);

    boolean existsByIpAddressAndCustid(String ip, Integer custId);

    List<CustIpMapping>getAllByCustid(Integer custId);

    List<CustIpMapping>getAllByIpAddressInAndCustidNot(List<String> ipAddress, Integer custId);

    boolean existsByIpAddressAndCustidNot(String ip, Integer custId);

    @Query("SELECT c.ipAddress FROM CustIpMapping c WHERE c.ipAddress IN :ipAddresses AND c.custid != :custId")
    List<String> getIpAddressesByIpAddressInAndCustidNot(@Param("ipAddresses") List<String> ipAddresses, @Param("custId") Integer custId);
}
