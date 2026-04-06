package com.adopt.apigw.modules.InventoryManagement.CommonInterfaces;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ADOPTINVENTORYMANAGEMENT-SERVICE",contextId = "AdoptInventoryMicroservice")
public interface InventoryClient {


    @GetMapping("/api/v1/AdoptInventoryManagement/product/getProductVarifiedWithCDATAManufacturer")
    Boolean getProductVarifiedWithCDATAManufacturer(@RequestHeader("Authorization") String token, @RequestParam(name = "customerId", required = false) Integer customerId,@RequestParam(name = "connectionNumber", required = false) String connectionNumber,@RequestParam("manufacturerName") String manufacturerName);

    @GetMapping("/api/v1/AdoptInventoryManagement/product/getMenufacturerName")
    String getManufacturerName(@RequestHeader("Authorization") String token, @RequestParam(name = "customerId", required = false) Integer customerId,@RequestParam(name = "connectionNumber", required = false) String connectionNumber);

}
