/*
package com.adopt.apigw.modules.FieldServiceParamMapping;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.FIELD_SERVICEPARAM_MAPPING)
public class FieldServiceParamMappingController extends ExBaseAbstractController2<FieldServiceParamMappingDto> {
    public FieldServiceParamMappingController(FieldServiceParamMappingService service) {
        super(service);
    }

    @Autowired
    FieldServiceParamMappingService fieldServiceParamMappingService;

    @GetMapping("/postpaidplanfieldsByServiceid/{serviceId}")
    public GenericDataDTO getPlanFieldsByServiceId(@PathVariable Long serviceId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
//            genericDataDTO.setDataList(fieldServiceParamMappingService.getPlanFieldsByServiceId(serviceId));
            genericDataDTO.setTotalRecords(1);
        }
        catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
        }
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
}
*/
