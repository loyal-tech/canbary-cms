package com.adopt.apigw.modules.Voucher.module;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Api(value = "Service Status" , description = "REST API to check service status!!!!",tags = "Status")
@RestController
@RequestMapping("/AdoptWifi")
@Primary
public class APIResponseController {
	@Autowired
	private CustomersRepository customersRepository;
	final Logger log = LoggerFactory.getLogger(APIResponseController.class);
	public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, Map<String, Object> response) {
		try {
//			log.info(String.format("%s", new ObjectMapper().writeValueAsString(response)));
			response.put("timestamp",
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
			response.put("status", responseCode);
			if(response.get(APIConstants.ERROR_MESSAGE) != null)
			{
				String errorMsg = response.get(APIConstants.ERROR_MESSAGE).toString().replace(APIConstants.NOT_FOUND.toString(), "");
				response.put(APIConstants.ERROR_MESSAGE, errorMsg);
			}
			if (responseCode.equals(APIConstants.SUCCESS)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			} else if (responseCode.equals(APIConstants.FAIL)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			} else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}else if (responseCode.equals(APIConstants.NO_CONTENT_FOUND)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			}else if (responseCode.equals(472)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			} else {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.valueOf(APIConstants.NO_CONTENT_FOUND.toString()));
			}

		} catch (Exception e) {
			log.error("Error while performing operation", e);
			if (response == null) {
				response = new HashMap<>();
			}
			response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
			response.put(APIConstants.ERROR_TAG, e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void buildErrorMessageForResponse(Map<String, Object> response, Throwable e)
	{
		String errorMsg = e.getMessage();
		if(errorMsg.contains(APIConstants.NOT_PUT_IN_QUEUE))
		{
			errorMsg = errorMsg.replace(APIConstants.NOT_PUT_IN_QUEUE, "");
		}
		if (errorMsg.contains(APIConstants.BASIC_STRING_MSG))
		{
			errorMsg = errorMsg.replace(APIConstants.BASIC_STRING_MSG, "");
			response.put(APIConstants.VALIDATION_REASON, APIConstants.VALIDATION_REASON_BASIC_STRING_MESSAGE);
			response.put(APIConstants.ERROR_MESSAGE, errorMsg);
		}
		else if (errorMsg.contains(APIConstants.BASIC_NUMERIC_MSG))
		{
			errorMsg = errorMsg.replace(APIConstants.BASIC_NUMERIC_MSG, "");
			response.put(APIConstants.VALIDATION_REASON, APIConstants.VALIDATION_REASON_BASIC_NUMERIC_MESSAGE);
			response.put(APIConstants.ERROR_MESSAGE, errorMsg);
		}
		else
		{
			response.put(APIConstants.ERROR_MESSAGE, errorMsg);
		}
	}

//	@PostMapping("/welcome")
//	public String showWelcomePage() {
//		return "Welcome To 'AdoptWifi'" + "<br>" + "User is authenticated and successfully logged in." + "<br>"
//				+ "You can access api by providing proper and correct url.";
//	}


	public Integer getMvnoIdFromCurrentStaff() {
		Integer mvnoId = null;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff response:{},exception:{}" ,APIConstants.FAIL,e.getStackTrace());
		}
		return mvnoId;
	}
	public Integer getMvnoIdFromCurrentStaff(Integer custId) {
		//TODO: Change once API work on live BSS server
		Integer mvnoId = null;
		try {
			if(custId!=null){
				mvnoId = customersRepository.getCustomerMvnoIdByCustId(custId);

			}
//			else {
//				SecurityContext securityContext = SecurityContextHolder.getContext();
//				if (null != securityContext.getAuthentication()) {
//					if(securityContext.getAuthentication().getPrincipal() != null)
//						mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
//				}
//			}
		} catch (Exception e) {
			ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
		}
		return mvnoId;
	}


}
