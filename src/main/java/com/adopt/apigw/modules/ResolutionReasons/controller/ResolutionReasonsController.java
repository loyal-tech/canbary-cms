package com.adopt.apigw.modules.ResolutionReasons.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.service.ExBaseService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.BankManagement.repository.BankManagementRepository;
import com.adopt.apigw.modules.ResolutionReasons.repository.ResolutionReasonsRepository;
import com.adopt.apigw.modules.planUpdate.controller.CustomerPackageController;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.modules.ResolutionReasons.model.ResolutionReasonsDTO;
import com.adopt.apigw.modules.ResolutionReasons.service.ResolutionReasonsService;
import com.adopt.apigw.spring.SpringContext;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.RESOLUTION_REASONS)
public class ResolutionReasonsController extends ExBaseAbstractController2<ResolutionReasonsDTO> {
	private static String MODULE = " [ResolutionReasonsController] ";

	@Autowired
	private ResolutionReasonsRepository resolutionReasonsRepository;

	@Autowired
	private ResolutionReasonsService resolutionReasonsService;



	public ResolutionReasonsController(ResolutionReasonsService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ResolutionReasons Controller]";
    }
	private static final Logger logger = LoggerFactory.getLogger(ResolutionReasonsController.class);
    @Override
    public GenericDataDTO save(@Valid @RequestBody ResolutionReasonsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
    	ResolutionReasonsService resolutionReasonsService = SpringContext.getBean(ResolutionReasonsService.class);
    	GenericDataDTO genericDataDTO = new GenericDataDTO();
		MDC.put("type", "Fetch");
    	try {
			boolean flag = resolutionReasonsService.duplicateVerifyAtSave(entityDTO.getName(),mvnoId);

			if (flag) {
				// TODO: pass mvnoID manually 6/5/2025
				if (mvnoId != null) {
					// TODO: pass mvnoID manually 6/5/2025
					entityDTO.setMvnoId(mvnoId);
					if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
						logger.error("unable create resolution reasone	with name "+entityDTO.getName()+":  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
						throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
					}else if (getBUIdsFromCurrentStaff().size() == 1) {
						entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
					}
				}


				if(getLoggedInUser().getLco())
					entityDTO.setLcoId(getLoggedInUser().getPartnerId());
				else
					entityDTO.setLcoId(null);

				genericDataDTO = super.save(entityDTO, result, authentication, req,mvnoId);
				logger.info("Creating resolution reasone "+entityDTO.getName() +" is successfull :  request: { From : {}}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
			} else {
				genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
				genericDataDTO.setResponseMessage(MessageConstants.RESOLUTION_REASON_NAME_EXITS);
				logger.error("unable create resolution reasone with name "+entityDTO.getName()+":  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());

			}
		}
        catch(CustomValidationException e)
		{
			genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
			genericDataDTO.setResponseMessage(e.getMessage());
			logger.error("unable create resolution reasone with name "+entityDTO.getName()+":  request: { From : {}, Request Url : {}}; Response : {{};}exception :{}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),e.getMessage());
		}
		MDC.remove("type");
    	return genericDataDTO;
    }

    @Override
    public GenericDataDTO update(@Valid @RequestBody ResolutionReasonsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
    	ResolutionReasonsService resolutionReasonsService = SpringContext.getBean(ResolutionReasonsService.class);
    	GenericDataDTO genericDataDTO = new GenericDataDTO();
		MDC.put("type", "Fetch");
    	boolean flag = resolutionReasonsService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId());	
        if (flag) {
			// TODO: pass mvnoID manually 6/5/2025
        	if(getMvnoIdFromCurrentStaff(null) != null) {
				// TODO: pass mvnoID manually 6/5/2025
        		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
        	}

        	genericDataDTO = super.update(entityDTO, result, authentication, req,mvnoId);
		//	logger.info("Updating resolution reasone With name "+entityDTO.getName() +" is successfull :  request: { From : {}}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
        } else {
        	genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
        	genericDataDTO.setResponseMessage(MessageConstants.RESOLUTION_REASON_NAME_EXITS);
			logger.error("Unable to update resolution reasone "+entityDTO.getName()+":  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
        }
		MDC.remove("type");
    	return genericDataDTO;
    }
	@GetMapping("/searchByStatus")
	public GenericDataDTO getAllByStatus(HttpServletRequest req) {
		String SUBMODULE = getModuleNameForLog() + " [getALlByStatus] ";
		MDC.put("type", "Fetch");
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			genericDataDTO = GenericDataDTO.getGenericDataDTO(resolutionReasonsService.findByStatus());
			if (null != genericDataDTO) {

				if (genericDataDTO.getDataList().isEmpty())
				{
					genericDataDTO = new GenericDataDTO();
					genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
					genericDataDTO.setResponseMessage("No Record Found!");
					genericDataDTO.setDataList(new ArrayList<>());
					genericDataDTO.setTotalRecords(0);
					genericDataDTO.setPageRecords(0);
					genericDataDTO.setCurrentPageNumber(1);
					genericDataDTO.setTotalPages(1);

				}

				logger.info("No data Found  :  request: { From : {}}; Response : {{}};}", req.getHeader("requestFrom"),genericDataDTO.getResponseCode());
				return genericDataDTO;
			}

		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
			genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
			genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
			logger.error("Unable to Search data  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.EXPECTATION_FAILED.value(),HttpStatus.EXPECTATION_FAILED.getReasonPhrase(),ex.getStackTrace());
			return genericDataDTO;
		}
		MDC.remove("type");
		return genericDataDTO;
	}

	@PostMapping(value = "/searchAll")
	public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO,@RequestParam Integer mvnoId) {
		return resolutionReasonsService.search( paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(),
				paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),paginationRequestDTO.getSortOrder(),mvnoId);
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


	@Override
	@PostMapping
	public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId) {
		String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
			requestDTO = setDefaultPaginationValues(requestDTO);

			if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
				genericDataDTO = resolutionReasonsService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
						, requestDTO.getPageSize()
						, requestDTO.getSortBy()
						, requestDTO.getSortOrder()
						, requestDTO.getFilters(),mvnoId);
			else
				genericDataDTO = resolutionReasonsService.search(requestDTO.getFilters()
						, requestDTO.getPage(), requestDTO.getPageSize()
						, requestDTO.getSortBy()
						, requestDTO.getSortOrder(),mvnoId);


			if (null != genericDataDTO) {
				logger.info("Fetching All Entities records:  request: { Module : {}}; Response : {Code :{}; Message : {}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
				return genericDataDTO;
			} else {
				genericDataDTO = new GenericDataDTO();
				genericDataDTO.setDataList(new ArrayList<>());
				genericDataDTO.setTotalRecords(0);
				genericDataDTO.setPageRecords(0);
				genericDataDTO.setCurrentPageNumber(1);
				genericDataDTO.setTotalPages(1);
				logger.error("Unable to fetch all Entities No records found:  request: { Module : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
			}
		} catch (Exception ex) {
			genericDataDTO = new GenericDataDTO();
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
			genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
			genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
			genericDataDTO.setTotalRecords(0);
			logger.error("Unable to fetch all Entities:  request: { module : {}}; Response : {Code :{}; Message : {};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
		}
		return genericDataDTO;
	}
	@GetMapping("/searchBySubCategory/{id}")
	public GenericDataDTO getAllByResoReasons(@PathVariable Long id, HttpServletRequest req) {
		String SUBMODULE = getModuleNameForLog() + " [getALlByResoReasons] ";
		MDC.put("type", "Fetch");
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			genericDataDTO = GenericDataDTO.getGenericDataDTO(resolutionReasonsService.findByResoReasons(id));
			if (null != genericDataDTO) {

				if (genericDataDTO.getDataList().isEmpty())
				{
					genericDataDTO = new GenericDataDTO();
					genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
					genericDataDTO.setResponseMessage("No Record Found!");
					genericDataDTO.setDataList(new ArrayList<>());
					genericDataDTO.setTotalRecords(0);
					genericDataDTO.setPageRecords(0);
					genericDataDTO.setCurrentPageNumber(1);
					genericDataDTO.setTotalPages(1);

				}

				logger.info("No data Found  :  request: { From : {}}; Response : {{}};}", req.getHeader("requestFrom"),genericDataDTO.getResponseCode());
				return genericDataDTO;
			}

		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
			genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
			genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
			logger.error("Unable to Search data  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.EXPECTATION_FAILED.value(),HttpStatus.EXPECTATION_FAILED.getReasonPhrase(),ex.getStackTrace());
			return genericDataDTO;
		}
		MDC.remove("type");
		return genericDataDTO;
	}

}
