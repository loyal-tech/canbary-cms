package com.adopt.apigw.modules.TicketTatMatrix.Controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.TicketTatMatrix.Model.TicketTatMatrixDTO;
import com.adopt.apigw.modules.TicketTatMatrix.Repository.TicketTatMatrixRepository;
import com.adopt.apigw.modules.TicketTatMatrix.Service.TicketTatMatrixService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.UtilsCommon;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TICKET_TAT_MATRIX)
public class TicketTatMatrixController  extends ExBaseAbstractController2<TicketTatMatrixDTO> {

    private static final Logger logger= LoggerFactory.getLogger(TicketTatMatrixController.class);

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    TicketTatMatrixService tatMatrixService;

    @Autowired
    TicketTatMatrixRepository repository;

    public TicketTatMatrixController(TicketTatMatrixService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return"[TicketTatMatrixController]";
    }



    @Override
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ADD + "\")")
    public GenericDataDTO save(@Valid @RequestBody TicketTatMatrixDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        boolean flag = tatMatrixService.duplicateVerifyAtSave(entityDTO.getName(),mvnoId);
        TicketTatMatrixDTO matrixDTO = null;
        if (flag) {
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId != null) {
                // TODO: pass mvnoID manually 6/5/2025
                entityDTO.setMvnoId(mvnoId);

                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                    throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
                }
                if (getBUIdsFromCurrentStaff().size() == 1) {
                    entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
                }
            }
            dataDTO = super.save(entityDTO, result, authentication, req,mvnoId);
            matrixDTO = (TicketTatMatrixDTO) dataDTO.getData();

        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.MATRIX_NAME);


        }
        MDC.remove("type");
        return dataDTO;
    }


    @Override
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_EDIT + "\")")
    public GenericDataDTO update(@Valid @RequestBody TicketTatMatrixDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        MDC.put("type", "Update");
        // TODO: pass mvnoID manually 6/5/2025
//        if(getMvnoIdFromCurrentStaff(null) != null) {
//            // TODO: pass mvnoID manually 6/5/2025
            entityDTO.setMvnoId(mvnoId);
//        }
        TicketTatMatrixDTO td=tatMatrixService.getEntityById(entityDTO.getId(), mvnoId);
        GenericDataDTO dataDTO = new GenericDataDTO();
        String updatedValues = UtilsCommon.getUpdatedDiff(entityDTO,td);
        boolean flag = tatMatrixService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue(),mvnoId);
        if (flag) {
            dataDTO = super.update(entityDTO, result, authentication, req,mvnoId);
            TicketTatMatrixDTO tatMatrixDTODTO = (TicketTatMatrixDTO) dataDTO.getData();
            if(tatMatrixDTODTO != null) {
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MATRIX,
                        AclConstants.OPERATION_MATRIX_EDIT, req.getRemoteAddr(), null, tatMatrixDTODTO.getId(), tatMatrixDTODTO.getName());
            }
        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.MATRIX_NAME);
        }
        MDC.remove("type");
        return dataDTO;
    }

    @Override
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_VIEW + "\")")
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO dataDTO = super.getEntityById(id, req,mvnoId);
        TicketTatMatrixDTO tatMatrixDTO = (TicketTatMatrixDTO) dataDTO.getData();
        MDC.remove("type");
        return dataDTO;
    }


    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_VIEW + "\")")
    @PostMapping(value = "/searchAll")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO, @RequestParam Integer mvnoId) {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO=tatMatrixService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder(),mvnoId);
            logger.info("Fetching all TatMatrix"+paginationRequestDTO.getFilterBy()+":  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable to fetch TatMatrix "+paginationRequestDTO.getFilterBy()+"  :  request: { From : {},}; Response : {{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(),ce.getStackTrace());
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable to fetch TatMatrix "+paginationRequestDTO.getFilterBy()+"  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        return super.getAllWithoutPagination(mvnoId);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, Integer mvnoId) {
        return super.getAll(requestDTO, req,mvnoId);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_VIEW + "\")")
    @GetMapping("/searchByStatus")
    public GenericDataDTO getAllByStatus(HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [getALlByStatus] ";
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = GenericDataDTO.getGenericDataDTO(tatMatrixService.findAllByStatus());
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



  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_DELETE + "\")")
    @Override
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_DELETE + "\")")
    public GenericDataDTO delete(@RequestBody TicketTatMatrixDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            MDC.put("type", "Delete");
            boolean flag = tatMatrixService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, authentication, req);
                TicketTatMatrixDTO tatMatrixDTODTO = (TicketTatMatrixDTO) dataDTO.getData();
                if (tatMatrixDTODTO != null) {
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MATRIX,
                            AclConstants.OPERATION_MATRIX_DELETE, req.getRemoteAddr(), null, tatMatrixDTODTO.getId(), tatMatrixDTODTO.getName());
                }
            } else {
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.MATRIX_EXIST);
            }
        }catch (Exception ex){
            if (ex instanceof RuntimeException) {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(DeleteContant.MATRIX_EXIST);

            } else {
                ex.printStackTrace();

            }
        }
        MDC.remove("type");
        return dataDTO;
    }
}
