package com.adopt.apigw.modules.DemoGraphicMapping.controller;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.core.controller.BaseAbstractController;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.service.ExBaseService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.DemoGraphicMapping.domain.DemoGraphicMappingTable;
import com.adopt.apigw.modules.DemoGraphicMapping.model.DemoGraphicMappingDTO;
import com.adopt.apigw.modules.DemoGraphicMapping.service.DemoGraphicMappingService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.dashboard.DashboardController;
import com.adopt.apigw.utils.APIConstants;
import org.apache.commons.math3.exception.NoDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL)
public class DemoGraphicMappingController extends BaseController<DemoGraphicMappingDTO> {
    private static final String MODULE = " [DemoGraphicMappingController] ";

    @Autowired
    private DemoGraphicMappingService demoGraphicMappingService;

//    public DemoGraphicMappingController(DemoGraphicMappingService service) {
//        super(service);
//    }
private static final Logger logger = LoggerFactory.getLogger(DemoGraphicMappingController.class);
    @GetMapping("/getdemographicmapping")
   public ResponseEntity<?> getAll()  {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;

        List<DemoGraphicMappingTable> demoGraphicMappingTables  = demoGraphicMappingService.getAll();


            if (!demoGraphicMappingTables.isEmpty()) {

                response.put("demographicmappingtable", demoGraphicMappingTables);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info("get Demographic mapping is Successfull  :  request: { From : {}}; Response : {{}}",MODULE ,RESP_CODE);

            } else {

                RESP_CODE = HttpStatus.NOT_FOUND.value();
                response.put(APIConstants.ERROR_TAG, "DATA NOT FOUND");

                logger.error("Unable to search :  request: { From : {}}; Response : {{}};Error :{} ;",MODULE,RESP_CODE,response);
            }
                    return apiResponse(RESP_CODE, response);
   }

}
