package com.adopt.apigw.modules.tickets.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import com.adopt.apigw.constants.UrlConstants;
//import com.adopt.apigw.core.controller.ExBaseAbstractController;
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.dto.GenericSearchDTO;
//import com.adopt.apigw.core.dto.PaginationRequestDTO;
//import com.adopt.apigw.core.exceptions.DataNotFoundException;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.modules.tickets.model.CaseReasonConfigPojo;
//import com.adopt.apigw.modules.tickets.service.CaseReasonConfigService;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//
//@RestController
//@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.CASE_REASON_CONFIG)
public class CaseReasonConfigController {
//
//    @Autowired
//    private CaseReasonConfigService caseReasonConfigService;
//
//    public CaseReasonConfigController(CaseReasonConfigService service) {
//        super(service);
//    }
//
//    @Deprecated
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }
//
//    @Override
//    public GenericDataDTO delete(@RequestBody CaseReasonConfigPojo entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        return super.delete(entityDTO, authentication, req);
//    }
//
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody CaseReasonConfigPojo entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//    	if(getMvnoIdFromCurrentStaff() != null) {
//    		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//    	}
//    	return super.save(entityDTO, result, authentication, req);
//    }
//
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody CaseReasonConfigPojo entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//    	if(getMvnoIdFromCurrentStaff() != null) {
//    		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//    	}
//    	return super.update(entityDTO, result, authentication, req);
//    }
//
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        return super.getEntityById(id, req);
//    }
//
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
////    @GetMapping("/byCaseReasonId/{caseReasonId}")
////    public GenericDataDTO getEntityByCaseReasonId(@PathVariable Long caseReasonId) {
////        GenericDataDTO genericDataDTO = new GenericDataDTO();
////        genericDataDTO.setResponseCode(HttpStatus.OK.value());
////        genericDataDTO.setResponseMessage("Success");
////        try {
////            return GenericDataDTO.getGenericDataDTO(caseReasonConfigService.getEntityByCaseReasonId(caseReasonId));
////        } catch (Exception e) {
////            ApplicationLogger.logger.error(e.getMessage(), e);
////            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
////            if (e instanceof DataNotFoundException) {
////                genericDataDTO.setResponseMessage("Data Not Found");
////            } else {
////                genericDataDTO.setResponseMessage(e.getMessage());
////            }
////            genericDataDTO.setTotalRecords(0);
////            genericDataDTO.setDataList(null);
////        }
////        return genericDataDTO;
////    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[CaseReasonConfigController]";
//    }
}
