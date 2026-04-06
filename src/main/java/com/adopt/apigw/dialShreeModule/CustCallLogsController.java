package com.adopt.apigw.dialShreeModule;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController()
@RequestMapping(value = UrlConstants.BASE_API_URL)
public class CustCallLogsController {

    @Autowired
    CustCallLogsService custCallLogsService;

    @Autowired
    APIResponseController apiResponseController;

    private static final Logger logger = LoggerFactory.getLogger(CustCallLogsController.class);


    @PostMapping("/getCustCallLogs/{mobileNum}")
    public ResponseEntity<?>getCustCallLogsByMobileNum (@PathVariable String mobileNum, @RequestBody PaginationRequestDTO paginationRequestDTO){
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try{
            if(mobileNum == null && mobileNum.isEmpty()){
                RESP_CODE = APIConstants.FAIL;
                response.put(APIConstants.ERROR_TAG, "PLease provide MobileNum in request.");
                logger.error(":::::::::::::::CustCallLog Details fetch failed — reason: missing mobile number in the request.::::::::::::::");
                return apiResponseController.apiResponse(RESP_CODE, response);
            }
            Page<CustCallDTO> custCallLogs = custCallLogsService.getAllDetailsByMobileNum(mobileNum,paginationRequestDTO);
            RESP_CODE = APIConstants.SUCCESS;
            response.put("custCallLogDetails",custCallLogs);
            response.put("message", "CustCallLog Details fetch Successfully");
            logger.info(":::::::::::::::::::::CustCallLog Details fetch Successfully:::::::::::::::::::::");
        }catch (Exception e){
            logger.error(e.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return apiResponseController.apiResponse(RESP_CODE,response);
    }


    @GetMapping("/getAllCustCallLogs/{uniqueId}")
    public ResponseEntity<?>getByIdCustCallLogs (@PathVariable String uniqueId){
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try{
            if(uniqueId == null && uniqueId.isEmpty()){
                RESP_CODE = APIConstants.FAIL;
                response.put(APIConstants.ERROR_TAG, "PLease provide uniqueId in request.");
                logger.error(":::::::::::::::CustCallLog Details fetch failed — reason: missing uniqueId in the request.::::::::::::::");
                return apiResponseController.apiResponse(RESP_CODE, response);
            }
            CustCallLogs custCallLogs = custCallLogsService.getAllDetailsByuniqueId(uniqueId);
            RESP_CODE = APIConstants.SUCCESS;
            response.put("custCallLogDetails",custCallLogs);
            response.put("message", "CustCallLog Details fetch Successfully");
            logger.info(":::::::::::::::::::::CustCallLog Details fetch Successfully:::::::::::::::::::::");
        }catch (Exception e){
            logger.error(e.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return apiResponseController.apiResponse(RESP_CODE,response);
    }
}
