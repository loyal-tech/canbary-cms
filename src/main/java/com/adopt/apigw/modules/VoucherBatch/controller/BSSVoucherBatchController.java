package com.adopt.apigw.modules.VoucherBatch.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.MenuConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.radius.plan.PlanController;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.Voucher.module.SNMPCounters;
import com.adopt.apigw.modules.VoucherBatch.domain.BSSVoucherBatch;
import com.adopt.apigw.modules.VoucherBatch.module.UpdateVoucherBatchDto;
import com.adopt.apigw.modules.VoucherBatch.module.VoucherBatchDto;
import com.adopt.apigw.modules.VoucherBatch.module.VoucherBatchInfoDto;
import com.adopt.apigw.modules.VoucherBatch.service.VoucherBatchService;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "VoucherBatch Management", description = "REST APIs related to VoucherBatch Entity!!!!", tags = "VoucherBatch")
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.VOUCHERBATCH)
public class BSSVoucherBatchController {

	private final Logger log = LoggerFactory.getLogger(PlanController.class);
	private static String MODULE = " [BSSVoucherBatchController] ";
	private static final String VOUCHERBATCH_LIST = "voucherBatchList";
	private static final String VOUCHERBATCH = "voucherBatch";
	private SNMPCounters snmpCounters=new SNMPCounters();

	@Autowired
	APIResponseController apiResponseController;

	@Autowired
	private VoucherBatchService voucherBatchService;

	@Autowired
	private PartnerRepository partnerRepository;

	@Autowired
	private Tracer tracer;

	@ApiOperation(value = "Add new Voucher Batch")
	@PostMapping("/addVoucherBatch")
	//@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','createUpdateAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> addVoucherBatch(@RequestBody VoucherBatchDto voucherBatchDto, HttpServletRequest request) {
		Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);	// TODO: pass mvnoID manually 6/5/2025
		Map<String, Object> response = new HashMap<>();
		MDC.put(APIConstants.TYPE, APIConstants.TYPE_CREATE);
		try {
			VoucherBatchDto voucherBatch = voucherBatchService.saveVoucherBatch(voucherBatchDto, Long.valueOf(mvnoId));
			Integer responseCode = APIConstants.SUCCESS;
			response.put(VOUCHERBATCH, voucherBatch);
			response.put(APIConstants.MESSAGE, "VoucherBatch has been added successfully.");
			log.info("VoucherBatch has been added successfully: '" + voucherBatch.getVoucherBatchId() + "' by "
					+ MDC.get("userName"));
			snmpCounters.increamentCreateVoucherBatchSuccess();
			return apiResponseController.apiResponse(responseCode, response);
		} catch (Exception e) {
			log.error("Error while add new VoucherBatch: " + e.getMessage());
			Integer responseCode = APIConstants.FAIL;
			response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
			snmpCounters.increamentCreateVoucherBatchFailure();
			return apiResponseController.apiResponse(responseCode, response);
		} finally {
			MDC.remove(APIConstants.TYPE);
		}
	}

	@ApiOperation(value = "Update existing VoucherBatch data")
	@PutMapping("/updateVoucherBatch")
	//@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','createUpdateAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> updateVoucherBatch(@RequestBody UpdateVoucherBatchDto voucherBatchDto, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		try {
			// TODO: pass mvnoID manually 6/5/2025
			Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);
			UpdateVoucherBatchDto voucherVo = voucherBatchService.updateVoucherBatch(voucherBatchDto, Long.valueOf(mvnoId));
			Integer responseCode = APIConstants.SUCCESS;
			response.put(VOUCHERBATCH, voucherVo);
			response.put(APIConstants.MESSAGE, "VoucherBatch has been updated successfully.");
			snmpCounters.increamentUpdateVoucherBatchSuccess();
			return apiResponseController.apiResponse(responseCode, response);
		} catch (Exception e) {
			log.error("Error while update VoucherBatch: " + e.getMessage());
			Integer responseCode = APIConstants.FAIL;
			response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
			snmpCounters.increamentUpdateVoucherBatchFailure();
			return apiResponseController.apiResponse(responseCode, response);
		}
	}

	@ApiOperation(value = "Delete voucherBatch based on the given voucherBatch id")
	@DeleteMapping("/deleteVoucherBatch")
	//@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','deleteAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> deleteVoucherBatch(
			@RequestParam(name = "voucherBatchId", required = true) Long voucherBatchId,HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		try {
			// TODO: pass mvnoID manually 6/5/2025
			Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);
			voucherBatchService.deleteVoucherBatchById(voucherBatchId, Long.valueOf(mvnoId));
			Integer responseCode = APIConstants.SUCCESS;
			response.put(APIConstants.MESSAGE, "voucherBatch has been deleted successfully.");
			snmpCounters.increamentDeleteVoucherBatchSuccess();
			return apiResponseController.apiResponse(responseCode, response);
		} catch (Exception e) {
			Integer responseCode = APIConstants.FAIL;
			MDC.put(APIConstants.TYPE, APIConstants.TYPE_DELETE);
			log.error("Error while delete voucherBatch: " + e.getMessage());
			response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
			snmpCounters.increamentDeleteVoucherBatchFailure();
			return apiResponseController.apiResponse(responseCode, response);
		} finally {
			MDC.remove(APIConstants.TYPE);
		}
	}

	@ApiOperation(value = "Get list of voucherBatch in the system")
	@GetMapping("/voucherBatches")

	//@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> findAllVoucherBatch(
			@RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) {
		Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);		// TODO: pass mvnoID manually 6/5/2025
		Map<String, Object> response = new HashMap<>();
		MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
		try {
			List<VoucherBatchInfoDto> voucherBatchList = voucherBatchService.findAllVoucherBatch(Long.valueOf(mvnoId));
			Integer responseCode = APIConstants.SUCCESS;
			response.put(VOUCHERBATCH_LIST, voucherBatchList);
			snmpCounters.increamentfindAllVoucherBatchSuccess();
			return apiResponseController.apiResponse(responseCode, response);
		} catch (Exception e) {
			Integer responseCode = APIConstants.FAIL;
			log.error("Error while fetch voucherBatches: " + e.getMessage());
			response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
			snmpCounters.increamentfindAllVoucherBatchFailure();
			return apiResponseController.apiResponse(responseCode, response);
		} finally {
			MDC.remove(APIConstants.TYPE);
		}
	}

	@ApiOperation(value = "Get list of voucher batch in the system")
	@GetMapping("/getAllVoucherBatch")
	//@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")

	@PreAuthorize("validatePermission(\"" + MenuConstants.SHOW_VOUCHER_BATCH + "\")")
	public ResponseEntity<Map<String, Object>> getAllVoucherBatch(PaginationDTO paginationDto
			, @RequestParam(name = "resellerId", required = false) Long resellerId, @RequestParam(name = "batchName", required = false) String batchName, HttpServletRequest request,@RequestParam Integer mvnoId) {

		Map<String,Object> response = new HashMap<>();
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Fetch");
		MDC.put("userName", getLoggedInUser().getUsername());
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());

		try {
			// TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId =  apiResponseController.getMvnoIdFromCurrentStaff(null);
			PageableResponse<VoucherBatchInfoDto> voucherBatchPage = voucherBatchService.getAllVoucherBatch(Long.valueOf(mvnoId), resellerId, batchName, paginationDto);
			Integer responseCode = 0;
			if(CollectionUtils.isEmpty(voucherBatchPage.getData())) {
				responseCode=APIConstants.NO_CONTENT_FOUND;
				response.put(APIConstants.ERROR_MESSAGE, "No Records Found!");
				snmpCounters.increamentgetAllVoucherBatchListFailure();
			} else {
				responseCode = APIConstants.SUCCESS;
				response.put("voucherbatch", voucherBatchPage);
				log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Voucher Batch"+LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
				snmpCounters.increamentgetAllVoucherBatchListSuccess();
			}
			return apiResponseController.apiResponse(responseCode,response);

		}
		catch (Exception e) {
			Integer responseCode=APIConstants.FAIL;
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Voucher Batch"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
			response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
	        e.printStackTrace();
			snmpCounters.increamentgetAllVoucherBatchListFailure();
			return apiResponseController.apiResponse(responseCode,response);

		} finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
	}

	@ApiOperation(value = "Get voucherBatch based on the given voucherBatch id")
	@GetMapping("/findVoucherBatchById")
//	@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> findVoucherBatchById(
			@RequestParam(name = "voucherBatchId", required = true) Long voucherBatchId,
			HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
		try {
			// TODO: pass mvnoID manually 6/5/2025
			Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);
			VoucherBatchDto voucherBatchDto = new VoucherBatchDto(voucherBatchService.findVoucherBatchById(voucherBatchId, Long.valueOf(mvnoId)));
			Integer responseCode = APIConstants.SUCCESS;
			response.put(VOUCHERBATCH, voucherBatchDto);
			snmpCounters.increamentfindVoucherBatchByIdSuccess();
			return apiResponseController.apiResponse(responseCode, response);
		} catch (Exception e) {
			log.error("Error while fetch voucherBatchs by Id: " + voucherBatchId + " " + e.getMessage());
			Integer responseCode = APIConstants.FAIL;
			response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
			snmpCounters.increamentfindVoucherBatchByIdFailure();
			return apiResponseController.apiResponse(responseCode, response);
		} finally {
			MDC.remove(APIConstants.TYPE);
		}
	}

	@ApiOperation(value = "Get voucherBatch where reseller id is null")
	@GetMapping("/findVoucherBatchesWithoutReseller")
	//@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> findVoucherBatcheWithoutReseller(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
		try {
			// TODO: pass mvnoID manually 6/5/2025
			Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);
			List<BSSVoucherBatch> voucherBatchVo = voucherBatchService.findVoucherBatcheWithoutReseller(Long.valueOf(mvnoId));
			Integer responseCode = APIConstants.SUCCESS;
			response.put(VOUCHERBATCH, voucherBatchVo);
			snmpCounters.increamentVoucherBatchwithoutResellerSuccess();
			return apiResponseController.apiResponse(responseCode, response);
		} catch (Exception e) {
			log.error("Error while fetch voucherBatchs " + e.getMessage());
			Integer responseCode = APIConstants.FAIL;
			response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
			snmpCounters.increamentVoucherBatchwithoutResellerFailure();
			return apiResponseController.apiResponse(responseCode, response);
		} finally {
			MDC.remove(APIConstants.TYPE);
		}
	}

	@ApiOperation(value = "Get list of voucherBatch in the system")
	@GetMapping("/searchByDate")
	//@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> searchByDate(
															@RequestParam(name = "createDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime createDate,
																    HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
		try {
			// TODO: pass mvnoID manually 6/5/2025
			Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);
			List<VoucherBatchDto> voucherBatchDtoList = voucherBatchService.searchByDate(Long.valueOf(mvnoId), createDate);
			Integer responseCode = APIConstants.SUCCESS;
			response.put(VOUCHERBATCH_LIST, voucherBatchDtoList);
			snmpCounters.increamentSearchVoucherBatchByDateSuccess();
			return apiResponseController.apiResponse(responseCode, response);
		} catch (Exception e) {
			Integer responseCode = APIConstants.FAIL;
			log.error("Error while fetch voucherBatches: " + e.getMessage());
			response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
			snmpCounters.increamentSearchVoucherBatchByDateFailure();
			return apiResponseController.apiResponse(responseCode, response);
		} finally {
			MDC.remove(APIConstants.TYPE);
		}
	}

	@ApiOperation(value = "Get voucherBatch based on the given voucherBatch id")
	@GetMapping("/assignReseller")
	//@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> assignResellerToVoucherBatch(
			@RequestParam(name = "voucherBatchId", required = true) Long voucherBatchId,
			@RequestParam(name = "resellerId", required = true) Long resellerId,
			HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
		try {
			// TODO: pass mvnoID manually 6/5/2025
			Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);
			voucherBatchService.assignResellerToVoucherBatch(voucherBatchId, resellerId, Long.valueOf(mvnoId));
			Integer responseCode = APIConstants.SUCCESS;
			response.put(APIConstants.MESSAGE, "Reseller assigned to Voucher Batch Successfully");
			snmpCounters.increamentAssignResellertoVoucherBatchSuccess();
			return apiResponseController.apiResponse(responseCode, response);
		}
		catch (Exception e)
		{
			String msg = e.getMessage();
			Integer responseCode = APIConstants.FAIL;
			if(msg.contains(APIConstants.INVALID_RESELLER_MSG))
			{
				msg = msg.replace(APIConstants.INVALID_RESELLER_MSG, "");
				responseCode = APIConstants.INVALID_RESELLER_CODE;
			}
			log.error("Error while assigning reseller to voucherBatch " + msg);
			response.put(APIConstants.ERROR_MESSAGE, msg);
			snmpCounters.increamentAssignResellertoVoucherBatchFailure();
			return apiResponseController.apiResponse(responseCode, response);
		}
		finally
		{
			MDC.remove(APIConstants.TYPE);
		}
	}

	@ApiOperation(value = "Get list of voucherBatch in the system")
	@GetMapping("/searchVoucherBatches")
	//@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> findAllVoucherBatch(
			@RequestParam(name = "batchName", required = false) String batchName, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
		try {
			// TODO: pass mvnoID manually 6/5/2025
			Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);
			List<BSSVoucherBatch> voucherBatchList = voucherBatchService.searchVoucherBatch(batchName, Long.valueOf(mvnoId));
			Integer responseCode = APIConstants.SUCCESS;
			response.put(VOUCHERBATCH_LIST, voucherBatchList);
			snmpCounters.increamentfindAllVoucherSuccess();
			return apiResponseController.apiResponse(responseCode, response);
		} catch (Exception e) {
			Integer responseCode = APIConstants.FAIL;
			log.error("Error while fetch voucherBatches: " + e.getMessage());
			response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
			snmpCounters.increamentfindAllVoucherFailure();
			return apiResponseController.apiResponse(responseCode, response);
		} finally {
			MDC.remove(APIConstants.TYPE);
		}
	}

	@ApiOperation(value = "Generate new voucher based on the configration")
	@PostMapping("/generate")

	@PreAuthorize("validatePermission(\"" + MenuConstants.VOUCHER_GENERATE+ "\")")
//	@PreAuthorize("@roleAccesses.hasPermission('voucher','createUpdateAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> generate(@RequestBody VoucherBatchDto voucherBatchDto,
														HttpServletRequest request,@RequestParam Integer mvnoId)
	{
		Map<String, Object> response = new HashMap<>();
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Create");
		MDC.put("userName", getLoggedInUser().getUsername());
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());
		try
		{
			if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID)
			{
				if (!getLoggedInUser().getLco() && voucherBatchService.isPartnerBalanceInsufficient(voucherBatchDto,getLoggedInUserPartnerId())) {
					String msg="Partner has Insufficient balance!";
					Integer responseCode = APIConstants.FAIL;
					log.error("Error while generate new voucher: " + msg);
					response.put(APIConstants.ERROR_MESSAGE, msg);
					snmpCounters.increamentgenerateBatchAndVoucherFailure();
					return apiResponseController.apiResponse(responseCode, response);

				}
			}
			// TODO: pass mvnoID manually 6/5/2025
//			Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);
			voucherBatchService.generateBatchAndVouchers(voucherBatchDto, Long.valueOf(mvnoId));

			if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
				Partner partner=partnerRepository.findById(getLoggedInUserPartnerId()).orElse(null);
				if(partner!=null && partner.getPartnerType().equalsIgnoreCase("Franchise"))
					voucherBatchService.shareVoucherBatchData(voucherBatchDto, getLoggedInUserPartnerId());
			}
			response.put(APIConstants.MESSAGE, "Voucher Batch has been generated successfully.");
			Integer responseCode = APIConstants.SUCCESS;
			log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"create Voucher Batch"+LogConstants.LOG_BY_NAME + voucherBatchDto.getBatchName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + responseCode);
			snmpCounters.increamentgenerateBatchAndVoucherSuccess();
			return apiResponseController.apiResponse(APIConstants.SUCCESS, response);
		}
		catch (Exception e)
		{
			String msg = e.getMessage();
			Integer responseCode = APIConstants.FAIL;
			if(msg.contains(APIConstants.INVALID_RESELLER_MSG))
			{
				msg = msg.replace(APIConstants.INVALID_RESELLER_MSG, "");
				responseCode = APIConstants.INVALID_RESELLER_CODE;
			}
			log.error("Error while generate new voucher: " + msg);
			responseCode = APIConstants.FAIL;
			log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create Voucher Batch"+LogConstants.LOG_BY_NAME + voucherBatchDto.getBatchName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
			response.put(APIConstants.ERROR_MESSAGE, msg);
			snmpCounters.increamentgenerateBatchAndVoucherFailure();
			return apiResponseController.apiResponse(responseCode, response);
		}
		finally
		{
			MDC.remove(APIConstants.TYPE);
		}
	}


	@ApiOperation(value = "Update ExpDate")
	@GetMapping("/updateExpiryDate")

	@PreAuthorize("validatePermission(\"" + MenuConstants.EXTEND_EXPIRY_VOUCHER_BATCH+ "\")")
	//@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")
	public ResponseEntity<Map<String, Object>> updateExpiryDate(
			@RequestParam(name = "voucherBatchId", required = true) Long voucherBatchId,
			@RequestParam(name = "expiryDate", required = true) String expiryDate,
			@RequestParam(name = "lastModifiedBy", required = false) String lastModifiedBy,
			HttpServletRequest request,@RequestParam Integer mvnoId) {
		Map<String, Object> response = new HashMap<>();
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Update");
		MDC.put("userName", getLoggedInUser().getUsername());
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		ResponseEntity<Map<String, Object>> responseEntity = null;
		try {
			// TODO: pass mvnoID manually 6/5/2025
//			Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);
			voucherBatchService.updateExpiryDate(voucherBatchId, expiryDate, Long.valueOf(mvnoId), lastModifiedBy);
			Integer responseCode = APIConstants.SUCCESS;
			log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update voucher Expiry date"+ expiryDate + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
			response.put(APIConstants.MESSAGE, "Expiry date updated successfully");
			//snmpCounters.incrementAssignResellertoVoucherBatchSuccess();
			responseEntity = apiResponseController.apiResponse(responseCode, response);

			return apiResponseController.apiResponse(responseCode, response);
		} catch (Exception e) {
			String msg = e.getMessage();
			Integer responseCode = APIConstants.FAIL;
			if (msg.contains(APIConstants.INVALID_RESELLER_MSG)) {
				msg = msg.replace(APIConstants.INVALID_RESELLER_MSG, "");
				responseCode = APIConstants.INVALID_RESELLER_CODE;
			}
			responseEntity = apiResponseController.apiResponse(responseCode, response);
			response.put(APIConstants.ERROR_MESSAGE, msg);
			log.error(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update voucher Expiry date"+ expiryDate + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

			return apiResponseController.apiResponse(responseCode, response);
		} finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
	}



	public int getLoggedInUserPartnerId() {
		int partnerId = -1;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
			partnerId = -1;
		}
		return partnerId;
	}


	public LoggedInUser getLoggedInUser() {
		LoggedInUser loggedInUser = null;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
		}
		return loggedInUser;
	}

	public int getLoggedInUserId() {
		int loggedInUserId = -1;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
			loggedInUserId = -1;
		}
		return loggedInUserId;
	}

}
