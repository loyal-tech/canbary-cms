package com.adopt.apigw.controller.radius.common;

import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.utils.APIConstants;

@Controller
public class CustomErrorController extends ApiBaseController implements ErrorController {
	
	@RequestMapping("/error")
	public Object handleError(HttpServletRequest request) {
		HashMap<String,Object> response = new HashMap<>();
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			String apiPath = "/api/v1/";
			String reuqestPath = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
			if (reuqestPath != null && reuqestPath != "" && reuqestPath.contains(apiPath)) {
				Integer statusCode = Integer.parseInt(status.toString());
				if (statusCode.equals(APIConstants.FORBIDDEN)) {
					response.put(APIConstants.ERROR_TAG, "Unauthorized Access");
					return apiResponse(statusCode,response);
				} else if (statusCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
					response.put(APIConstants.ERROR_TAG, "Internal Server Error");
					return apiResponse(statusCode,response);
				} else if (statusCode.equals(APIConstants.FAIL)) {
					response.put(APIConstants.ERROR_TAG, "Bad Request");
					return apiResponse(statusCode,response);
				} else {
					response.put(APIConstants.ERROR_TAG, "Internal Error");
					return apiResponse(statusCode,response);
				}
			} else {
				return "error-403";
			}
		} else {
			return "error-403";
		}
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}
