package com.adopt.apigw.modules.CafFollowUp.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.CafFollowUp.repository.CafFollowUpRepository;
import com.adopt.apigw.spring.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.CafFollowUp.model.CafFollowUpDTO;
import com.adopt.apigw.modules.CafFollowUp.model.CafFollowUpRemarkDTO;
import com.adopt.apigw.modules.CafFollowUp.service.CafFollowUpRemarkService;
import com.adopt.apigw.modules.CafFollowUp.service.CafFollowUpService;
import com.adopt.apigw.utils.APIConstants;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.CAF_FOLLOW_UP)
public class CafFollowUpController extends ExBaseAbstractController<CafFollowUpDTO> {

	public CafFollowUpController(CafFollowUpService service) {
		super(service);
	}

	@Autowired
	private CafFollowUpService cafFollowUpService;
	
	@Autowired
    private CafFollowUpRemarkService cafFollowUpRemarkService;

	@Autowired
	private Tracer tracer;

	@Autowired
	private CafFollowUpRepository cafFollowUpRepository;
	
	@Override
	public String getModuleNameForLog() {
		return "[CafFollowUpController]";
	}

	private final Logger log = LoggerFactory.getLogger(APIController.class);

	@Override
	public GenericDataDTO save(@Valid @RequestBody CafFollowUpDTO entityDTO, BindingResult result,
			Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Create");
		MDC.put("userName", getLoggedInUser().getFirstName());
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
	        GenericDataDTO genericDataDTO = new GenericDataDTO();
	        try {
				// TODO: pass mvnoID manually 6/5/2025
	        	 if(getMvnoIdFromCurrentStaff(null) != null) {
					 // TODO: pass mvnoID manually 6/5/2025
	         		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
	         	  }
	        	entityDTO.setStaffUserId(getStaffId());
	        	entityDTO.setCreatedBy(getStaffId());
	        	genericDataDTO = cafFollowUpService.save(entityDTO);
				RESP_CODE = APIConstants.SUCCESS;
				log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Create Caf FollowUp"+LogConstants.LOG_BY_NAME+entityDTO.getFollowUpName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} catch (CustomValidationException ce) {
	            ce.printStackTrace();
	            genericDataDTO.setResponseCode(ce.getErrCode());
	            genericDataDTO.setResponseMessage(ce.getMessage());
				RESP_CODE = ce.getErrCode();
				log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create Caf FollowUp"+LogConstants.LOG_BY_NAME+entityDTO.getFollowUpName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
	        } catch (Exception e) {
	            e.printStackTrace();
	            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
	            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
				RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
				log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create Caf FollowUp"+LogConstants.LOG_BY_NAME+entityDTO.getFollowUpName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}
			finally {
				MDC.remove("type");
				MDC.remove("userName");
				MDC.remove("traceId");
				MDC.remove("spanId");
			}
	        return genericDataDTO;
	}
	
	@PostMapping("/reSchedulefollowup")
	public GenericDataDTO reSchedulefollowup(@Valid @RequestBody CafFollowUpDTO entityDTO, @RequestParam("followUpId") Long followUpId, @RequestParam("remarks") String remarks,BindingResult result,
			Authentication authentication, HttpServletRequest req) throws Exception {
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Update");
		MDC.put("userName", getLoggedInUser().getFirstName());
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			// TODO: pass mvnoID manually 6/5/2025
			if(getMvnoIdFromCurrentStaff(null) != null) {
				// TODO: pass mvnoID manually 6/5/2025
				entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
			}
			entityDTO.setStaffUserId(getStaffId());
			entityDTO.setCreatedBy(getStaffId());
			genericDataDTO = cafFollowUpService.reSchedule(entityDTO, followUpId, remarks,getStaffId());
			RESP_CODE = APIConstants.SUCCESS;
			log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"ReSchedule FollowUp"+LogConstants.LOG_BY_NAME+entityDTO.getFollowUpName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		} catch (CustomValidationException ce) {
			ce.printStackTrace();
			genericDataDTO.setResponseCode(ce.getErrCode());
			genericDataDTO.setResponseMessage(ce.getMessage());
			RESP_CODE = ce.getErrCode();
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"ReSchedule FollowUp"+LogConstants.LOG_BY_NAME+entityDTO.getFollowUpName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
			genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
			RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"ReSchedule FollowUp"+LogConstants.LOG_BY_NAME+entityDTO.getFollowUpName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
		return genericDataDTO;
	}
	
	@GetMapping("/closefollowup")
	public GenericDataDTO closefollowup(@RequestParam("followUpId") Long followUpId, @RequestParam("remarks") String remarks, HttpServletRequest req) throws Exception {
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Delete");
		MDC.put("userName", getLoggedInUser().getFirstName());
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		String cafFollowUpName = null;
		try {
			cafFollowUpName = cafFollowUpRepository.findNameById(followUpId);
			genericDataDTO = cafFollowUpService.closefollowup(followUpId, remarks,getStaffId());
			RESP_CODE = APIConstants.SUCCESS;
			log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Delete Caf FollowUp"+LogConstants.LOG_BY_NAME+cafFollowUpName+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);;
		} catch (CustomValidationException ce) {
			ce.printStackTrace();
			genericDataDTO.setResponseCode(ce.getErrCode());
			genericDataDTO.setResponseMessage(ce.getMessage());
			RESP_CODE = ce.getErrCode();
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create Caf FollowUp"+LogConstants.LOG_BY_NAME+cafFollowUpName+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
			genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
			RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create Caf FollowUp"+LogConstants.LOG_BY_NAME+cafFollowUpName+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
		return genericDataDTO;
	}
	
	@GetMapping("/findAll")
	public GenericDataDTO getAllByCustomerId(@RequestParam("customerId") Integer customerId,
			@RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,HttpServletRequest req, @RequestParam Integer mvnoId) throws Exception {
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Fetch");
		MDC.put("userName", getLoggedInUser().getFirstName());
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
			paginationRequestDTO.setPage(page);
			paginationRequestDTO.setPageSize(pageSize);
			paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
			genericDataDTO = cafFollowUpService.getAllByCustomerId(customerId,paginationRequestDTO);
			RESP_CODE = APIConstants.SUCCESS;
			log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch All Caf FollowUp"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		} catch (CustomValidationException ce) {
			ce.printStackTrace();
			genericDataDTO.setResponseCode(ce.getErrCode());
			genericDataDTO.setResponseMessage(ce.getMessage());
			RESP_CODE = ce.getErrCode();
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Caf FollowUp"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
			genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
			RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Caf FollowUp"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
		return genericDataDTO;
	}
	
	@PostMapping("/cafFollowUp/remark")
	public GenericDataDTO addCafFollowUpRemark(@Valid @RequestBody CafFollowUpRemarkDTO entityDTO, BindingResult result,
			Authentication authentication, HttpServletRequest req) throws Exception {
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Create");
		MDC.put("userName", getLoggedInUser().getFirstName());
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
	        GenericDataDTO genericDataDTO = new GenericDataDTO();
	        try {
	        	genericDataDTO = cafFollowUpRemarkService.save(entityDTO, getStaffId());
				RESP_CODE = APIConstants.SUCCESS;
				log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Add Caf FollowUp remark"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} catch (CustomValidationException ce) {
	            ce.printStackTrace();
	            genericDataDTO.setResponseCode(ce.getErrCode());
	            genericDataDTO.setResponseMessage(ce.getMessage());
				RESP_CODE = ce.getErrCode();
				log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Add Caf FollowUp remark"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} catch (Exception e) {
	            e.printStackTrace();
	            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
	            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
				RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
				log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Add Caf FollowUp remark"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}
			finally {
				MDC.remove("type");
				MDC.remove("userName");
				MDC.remove("traceId");
				MDC.remove("spanId");
			}
	        return genericDataDTO;
	}
	
	@GetMapping("/findAll/cafFollowUpRemark/{cafFollowUpId}")
	public GenericDataDTO getAllCafFollowUpRemarkByCafFollowUpId(@PathVariable("cafFollowUpId") Long cafFollowUpId,HttpServletRequest req) throws Exception {
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Fetch");
		MDC.put("userName", getLoggedInUser().getFirstName());
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			genericDataDTO = cafFollowUpRemarkService.getAllByCafFollowUpId(cafFollowUpId);
			RESP_CODE = APIConstants.SUCCESS;
			log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch caf FollowUp Remark"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		} catch (CustomValidationException ce) {
			ce.printStackTrace();
			genericDataDTO.setResponseCode(ce.getErrCode());
			genericDataDTO.setResponseMessage(ce.getMessage());
			RESP_CODE = ce.getErrCode();
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch caf FollowUp Remark"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
			genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
			RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch caf FollowUp Remark"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
		return genericDataDTO;
	}
	
	@GetMapping("/generateNameOfTheCafFollowUp/{customersId}")
	public GenericDataDTO generateNameOfTheFollowUpByCustomersId(@PathVariable("customersId") Integer customersId,HttpServletRequest req) throws Exception {
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Create");
		MDC.put("userName", getLoggedInUser().getFirstName());
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			genericDataDTO = cafFollowUpService.generateNameOfTheFollowUp(customersId);
			RESP_CODE = APIConstants.SUCCESS;
			log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"generate Name Of The CafFollowUp"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		} catch (CustomValidationException ce) {
			ce.printStackTrace();
			genericDataDTO.setResponseCode(ce.getErrCode());
			genericDataDTO.setResponseMessage(ce.getMessage());
			RESP_CODE = ce.getErrCode();
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"generate Name Of The CafFollowUp"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
			genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
			RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"generate Name Of The CafFollowUp"+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e
					.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
		return genericDataDTO;
	}
	public LoggedInUser getLoggedInUser() {
		LoggedInUser loggedInUser = null;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error(getModuleNameForLog() + e.getStackTrace(), e);
		}
		return loggedInUser;
	}

}
