package com.adopt.apigw.modules.Integration;

import com.adopt.apigw.modules.Integration.Pojo.CdataCustDetailsPojo;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.NMSIntegrationMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ADOPTINTEGRATIONSYSTEM-SERVICE",contextId = "AdoptIntegrationMicroService")
public interface IntegrationClient {


    @PostMapping("/api/v1/AdoptIntegrationSystem/nmsMaster/CdataProvisioning")
    String generateCdataAPIcall(@RequestHeader("Authorization") String token, @RequestBody CdataCustDetailsPojo cdataCustDetailsPojo);

    @PostMapping("/api/v1/AdoptIntegrationSystem/nmsIntegration/NMSProvisioning")
    String generateNMSAPICALL(@RequestHeader("Authorization") String token, @RequestBody NMSIntegrationMessage nmsIntegrationMessage);

    @PostMapping("/api/v1/AdoptIntegrationSystem/nmsIntegration/NMSUpdateWANConfig")
    String generateNMSUpdateWANConfig(@RequestHeader("Authorization") String token, @RequestBody NMSIntegrationMessage nmsIntegrationMessage);
}
