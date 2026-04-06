package com.adopt.apigw.service.feignService;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;

@FeignClient(name="ADOPTINVENTORYMANAGEMENT-SERVICE")
public interface SpecificParameterInventoryConsumer {

    @GetMapping("/specificationParameters/customer/{custId}")
    public ResponseEntity<?> getBookData(@RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "custId") String custId);

    @GetMapping
    public ResponseEntity<?> getBookDataByUri(URI baseUri, @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "custId") Integer custId, @PathVariable(name = "connectionnum") String connectionnum);
}
