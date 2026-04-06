package com.adopt.apigw.modules.InventoryManagement.outward;


import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItem.NonSerializedItemServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.item.ItemServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.product.ProductServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@Api(value = "OutwardController", description = "REST APIs related to inward Entity!!!!", tags = "outwards-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.OUTWARDS)
public class OutwardController extends ExBaseAbstractController<OutwardDto> {
    @Autowired
    OutwardServiceImpl outwardService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    InwardServiceImpl inwardService;
    @Autowired
    AuditLogService auditLogService;

    @Autowired
    ProductOwnerService productOwnerService;

    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    ProductServiceImpl productService;
    @Autowired
    NonSerializedItemServiceImpl nonSerializedItemService;

    @Autowired
    InOutWardMACService inOutWardMACService;
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PartnerService partnerService;
    @Autowired
    StaffUserRepository staffUserRepository;

    public OutwardController(OutwardServiceImpl outwardService) {
        super(outwardService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[OutwardController]";
    }
    private static final Logger logger = LoggerFactory.getLogger(OutwardController.class);
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ADD + "\")")
//    @Override
//    @Transactional
//    public GenericDataDTO save(@Valid @RequestBody OutwardDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Create");
//        try {
//            String defaultTimezone = TimeZone.getDefault().getID();
//            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
//            Integer second = tz.getOffset(new Date().getTime()) / 1000 ;
//            LocalDateTime localDateTime = entityDTO.getOutwardDateTime().plusSeconds(second);
//            entityDTO.setOutwardDateTime(localDateTime);
//            OutwardDto outwardDto = outwardService.saveEntity(entityDTO, false);
//            genericDataDTO.setData(outwardDto);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            logger.info("Outward Service With Outward number  "+entityDTO.outwardNumber+"  is created Successfully:  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to create OutWard Service with number "+entityDTO.outwardNumber+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.EXPECTATION_FAILED,ce.getMessage());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to create outWard Service with number "+entityDTO.outwardNumber+ ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//            return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
//    @Override
//    @Transactional
//    public GenericDataDTO update(@Valid @RequestBody OutwardDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        OutwardDto outwardD = outwardService.getEntityForUpdateAndDelete(entityDTO.getId());
//        try {
//            long increasedQty = (entityDTO.getQty() - entityDTO.getUsedQty() - outwardD.getUnusedQty());
//            if (increasedQty != 0) {
//                InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(entityDTO.getInwardId().getId());
//                inwardDto.setUsedQty(increasedQty + inwardDto.getUsedQty());
//                inwardDto.setUnusedQty(inwardDto.getUnusedQty() - increasedQty);
//                inwardService.updateEntity(inwardDto);
//                logger.info("Outward Service With  old number "+outwardD.outwardNumber +" to new  "+entityDTO.outwardNumber+"  updated Successfyly:  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//            }
//            entityDTO.setQty(0L);
//            entityDTO.setUnusedQty(0L);
//            entityDTO.setInTransitQty(entityDTO.getInTransitQty());
//            entityDTO.setUsedQty(0L);
//            entityDTO.setOutTransitQty(0L);
//            entityDTO.setRejectedQty(0L);
//            entityDTO.setApprovalStatus("Pending");
//            String defaultTimezone = TimeZone.getDefault().getID();
//            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
//            Integer second = tz.getOffset(new Date().getTime()) / 1000 ;
//            LocalDateTime localDateTime = entityDTO.getOutwardDateTime().plusSeconds(second);
//            entityDTO.setOutwardDateTime(localDateTime);
//            OutwardDto outwardDto = outwardService.updateEntity(entityDTO);
//            inwardService.updateInwardOfOutwardStatus(entityDTO.getId(), entityDTO.getStatus());
//            genericDataDTO.setData(outwardDto);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//            logger.info("Outward Service With  old number "+outwardD.outwardNumber +" to new  "+entityDTO.outwardNumber+"  updated Successfyly:  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to Update Outward Seervice with Old number "+outwardD.outwardNumber+":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,genericDataDTO.getResponseMessage(),ce.getMessage());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Update Outward Seervice with Old number "+outwardD.outwardNumber+":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    /*@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllOutwardByProductAndStaff")
    public GenericDataDTO getAllOutwardByProductAndStaff(@RequestParam(name = "productId") Long productId, @RequestParam(name = "staffId") Long staffId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(outwardService.getAllOutwardByProductAndStaff(productId, staffId));
            logger.info("get All outward product and Staff with Staff id "+productId+"   is Fetched Succesfully Successfully:  request: { From : {}}; Response : {{}}",getModuleNameForLog(),APIConstants.SUCCESS);
            //            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("get All outward product and Staff with product id "+productId+":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
        }
        MDC.remove("type");
        return genericDataDTO;

    }
*/
    /*@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ADD + "\")")
    @Transactional
    @PostMapping("/assignToCustomer")
    public GenericDataDTO assignToCustomer(@RequestBody CustomerInventoryMappingDto inventoryMappingDto) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        MDC.put("type", "Update");
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(customerInventoryMappingService.saveEntity(inventoryMappingDto));
            logger.info("Assigning outward  To customer With customer name "+inventoryMappingDto.getCustomerName()+" is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(ce.getMessage(), ce);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable to asign outward to customer with name "+inventoryMappingDto.getCustomerName()+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog() ,genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ce.getMessage());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to asign outward to customer with name "+inventoryMappingDto.getCustomerName()+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog() ,genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
*/
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ADD + "\")")
//    @Transactional
//    @PostMapping("/assignToCustomer")
//    public GenericDataDTO assignToCustomer(@RequestBody CustomerInventoryMappingDto inventoryMappingDto) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(customerInventoryMappingService.saveEntity(inventoryMappingDto));
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getByStaffId")
//    public GenericDataDTO assignToCustomer(@RequestParam(name = "staffId") Long staffId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String staffname=customerInventoryMappingService.getStaffDetails(staffId);
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(outwardService.getByStaffId(staffId));
//           // logger.error("Unable to search :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),RESP_CODE,response,ce.getMessage());
//            logger.info("Outward Service With Staff name   "+staffname+"  is Asigned Successfully:  request: { From : {}}; Response : {{};{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            //            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Assign Outward Service to Staff name "+staffname+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//
//    }

  /*  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getCustomerInventoryMappingByStaffId")
    public GenericDataDTO getCustomerInventoryMappingByStaffId(@RequestParam(name = "staffId") Long staffId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String staffname=customerInventoryMappingService.getStaffDetails(staffId);
        MDC.put("type", "Fetch");
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(customerInventoryMappingService.getCustomerInventoryMappingByStaffId(staffId));
            logger.info("Outward get Customer inventory by Staff  "+staffname+"  is created Successfully:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to  get Customer inventory by Staff  "+staffname+"  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());

        }
        MDC.remove("type");
        return genericDataDTO;

    }*/

  /*  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping("/getByCustomerId")
    public GenericDataDTO getByCustomerId(@RequestBody PaginationRequestDTO requestDTO) {

        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            logger.info("Outward get by Customer Id  "+requestDTO+"  is created Successfully:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
            return customerInventoryMappingService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Outward get  by Customer Id  "+requestDTO+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());

        }
        MDC.remove("type");
        return genericDataDTO;

    }*/

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page, @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize, @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder, @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping(value = "/searchAssignInventories")
    public GenericDataDTO searchAssignInventories(@RequestParam(name = "staffId") Long staffId, @RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page, @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize, @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder, @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [searchAssignInventories()] ";
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        try {
//            if (genericDataDTO.getResponseCode() == 406)
//            {
//                List<DTO> list = service.getAllEntities().stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff() || d.getMvnoId() == null ).collect(Collectors.toList());
//                genericDataDTO.setDataList(list);
//                genericDataDTO.setTotalRecords(list.size());
//                return genericDataDTO;
//            }
            if (null == filter || null == filter.getFilter() || 0 == filter.getFilter().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please provide search criteria!");
                logger.error("Unable to search  with Assign invoices :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            if (null != pageSize && pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;
            genericDataDTO = outwardService.searchAssignInventories(filter.getFilter(), page, pageSize, sortBy, sortOrder, staffId);

            if (null != genericDataDTO) {

                if (genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO = new GenericDataDTO();
                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);

                }
                logger.info("Outward Service With Assigned Invoices:  request: { From : {}}; Response : {{};{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
                return genericDataDTO;

            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No Record Found!");
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                logger.error("Unable to search  with Assign invoices :  request: { From : {}}; Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());

            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to search  with Assign invoices :  request: { From : {}}; Response : {{}};Error :{} ;}", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

  /*  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping(value = "/getAllAssignInventories")
    public GenericDataDTO getAllAssignInventories(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "staffId") Long staffId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllAssignInventories()] ";

        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = outwardService.getAssignInventories(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), staffId);
            if (null != genericDataDTO) {
                logger.info("fetching allAssigned inventories:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                logger.error("Unable to fetch all inventories :  request: { From : {},}; Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to  to fetch all inventories :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }*/

   /* @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
    @GetMapping("/approveInventory")
    public GenericDataDTO approveInventory(@RequestParam(name = "customerInventoryMappingId") Long customerInventoryMappingId,@RequestParam(name = "isApproveRequest") boolean isApproveRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        MDC.put("type", "Fetch");
        try {
            logger.info("Getting Inventory Approve from  with id  "+customerInventoryMappingId+"  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
            return customerInventoryMappingService.approveInventory(customerInventoryMappingId,isApproveRequest);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Approve inventory  with "+customerInventoryMappingId+":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
        }
        MDC.remove("type");
        return genericDataDTO;

    }*/

  /*    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
    @GetMapping("/replaceInventory")
    public GenericDataDTO replaceInventory(@RequestParam(name = "oldMacMappingId") Long oldMacMappingId, @RequestParam(name = "newMacMappingId") Long newMacMappingId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setData(customerInventoryMappingService.replaceInventory(oldMacMappingId, newMacMappingId));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());

//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }*/

  /*  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
    @GetMapping("/approveReplaceInventory")
    public GenericDataDTO approveReplaceInventory(@RequestParam(name = "macMappingId") Long macMappingId, @RequestParam(name = "billAble") String billAble, @RequestParam(name = "isApproveRequest") boolean isApproveRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return customerInventoryMappingService.approveReplaceInventory(macMappingId, Boolean.parseBoolean(billAble),isApproveRequest);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }*/

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getInventoryApproveProgressForReplace")
//    public GenericDataDTO getInventoryApproveProgressForReplace(@RequestParam(name = "macMappingId") Long macMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setDataList(customerInventoryMappingService.getInventoryApproveProgressForReplace(macMappingId));
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
//    @GetMapping("/rejectInventory")
//    public GenericDataDTO rejectInventory(@RequestParam(name = "customerInventoryMappingId") Long customerInventoryMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return customerInventoryMappingService.rejectInventory(customerInventoryMappingId);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
//    @GetMapping("/rejectReplaceInventory")
//    public GenericDataDTO rejectReplaceInventory(@RequestParam(name = "macMappingId") Long macMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return customerInventoryMappingService.rejectReplaceInventory(macMappingId);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody OutwardDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Delete");
//        boolean flag = outwardService.deleteVerification(entityDTO.getId().intValue());
//        if (flag) {
//
//            genericDataDTO = super.delete(entityDTO, authentication, req);
//            OutwardDto outwardDto = (OutwardDto) genericDataDTO.getData();
//            if(outwardDto != null)
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_OUTWARD_MANAGEMENT,
//                        AclConstants.OPERATION_OUTWARD_MANAGEMENT_DELETE, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getInwardNumber());
//            logger.info("Deleting Outward With OutwardNumber "+entityDTO.getInwardNumber()+" is successfull :   Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        } else {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(DeleteContant.OUTWARD_NUMBER_DELETE_EXIST);
//            logger.error("Unable to Delete Outward with OutwardNumber "+entityDTO.getInwardNumber()+"  : Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getItemHistoryByProduct")
//    public GenericDataDTO getInOutMacMapping(@RequestParam(name = "productId")Long productId, @RequestParam(name = "ownerId")Long ownerId, @RequestParam(name = "ownerType")String ownerType, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            Product product = productRepository.findById(productId).get();
//            boolean hasSerial = product.getProductCategory().isHasSerial();
//            boolean isTrackable = product.getProductCategory().isHasTrackable();
//            boolean hasMac = product.getProductCategory().isHasMac();
//            if(product.getProductCategory().getType().contains("CustomerBind")) {
//                StaffUser staffUser = staffUserRepository.findById(Math.toIntExact(ownerId)).get();
//                if(staffUser.getPartnerid() != 1) {
//                    ownerId = Long.valueOf(staffUser.getPartnerid());
//                    ownerType = CommonConstants.PARTNER;
//                }
//                if (hasMac || hasSerial) {
//                    genericDataDTO.setDataList(itemService.getInOutMacMappingForSerializedItem(productId, ownerId, ownerType));
//                }
//                if (!hasSerial && isTrackable) {
//                    genericDataDTO.setDataList(nonSerializedItemService.getInOutMacMappingForNonSerializedItem(productId, ownerId, ownerType));
//                }
//                if (!hasSerial && !isTrackable) {
//                    genericDataDTO.setDataList(itemService.getInOutMacMappingForSerializedItem(productId, ownerId, ownerType));
//                }
//            }
//            else if(product.getProductCategory().getType().contains("NetworkBind") || product.getProductCategory().getType().equalsIgnoreCase("NA"))
//            {
//                if ((hasSerial) || (isTrackable)) {
//                    genericDataDTO.setDataList(itemService.getInOutMacMappingForPopAndSA(productId, ownerId, ownerType));
//                }
//                if (!hasSerial && !isTrackable) {
//                    genericDataDTO.setDataList(productOwnerService.getNonTrackableProductQty(productId, ownerId, ownerType));
//                }
//            }
//            if(genericDataDTO.getDataList().isEmpty()){
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//            }
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get Item History By product, owner and owner type :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get Item History By product, owner and owner type :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//     @GetMapping("/getItemBasedOnCondtion")
//    public GenericDataDTO getInOutMacMappingForItemCondition(@RequestParam(name = "productId")Long productId,@RequestParam(name="itemId")Long itemId,@RequestParam(name="ownerId")Long ownerId,@RequestParam("ownerShipType")String ownerShipType,@RequestParam("replacementReason")String replacementReason,HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            Product product = productRepository.findById(productId).get();
//            boolean hasSerial = product.getProductCategory().isHasSerial();
//            boolean isTrackable = product.getProductCategory().isHasTrackable();
//            boolean hasMac = product.getProductCategory().isHasMac();
//            if(product.getProductCategory().getType().contains("CustomerBind")) {
//                StaffUser staffUser = staffUserRepository.findById(Math.toIntExact(ownerId)).get();
//                if(staffUser.getPartnerid() != 1) {
//                    ownerId = Long.valueOf(staffUser.getPartnerid());
//                    ownerShipType = CommonConstants.PARTNER;
//                }
//                if (hasMac || hasSerial) {
//                    genericDataDTO.setDataList(itemService.getInOutMacMappingForSerializedItemBasedOnItemCondtion(productId,itemId,ownerId,ownerShipType,replacementReason));
//                }
//                if (!hasSerial && !isTrackable) {
//                    genericDataDTO.setDataList(itemService.getInOutMacMappingForSerializedItemBasedOnItemCondtion(productId,itemId,ownerId,ownerShipType,replacementReason));
//                }
//            }
//            if(genericDataDTO.getDataList().isEmpty()){
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//            }
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get Item History By itemCondition, owner and owner type :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get Item History By product, owner and owner type :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }






//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getItemBasedOnProductType")
//    public GenericDataDTO getAllItembasedOnProductType(@RequestParam("productId") Long productId, @RequestParam("ownerid") Long ownerid, @RequestParam("ownerType") String ownerType, @RequestParam("planId") Long planId, @RequestParam(name = "planGroupId", required = false) Long planGroupId, @RequestParam(name = "productCategoryId", required = false) Long productCategoryId,HttpServletRequest req) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOList = new ArrayList<>();
//            Product product = productRepository.findById(productId).get();
//            boolean hasSerial = product.getProductCategory().isHasSerial();
//            boolean isTrackable = product.getProductCategory().isHasTrackable();
//            boolean hasMac = product.getProductCategory().isHasMac();
//            if (product.getProductCategory().getType().equalsIgnoreCase("CustomerBind") || product.getProductCategory().getType().equalsIgnoreCase("CustomerBind, NetworkBind")) {
//                if (hasMac || hasSerial) {
//                    genericDataDTO.setDataList(itemService.getInOutMacMappingBasedOnProductType(productId, ownerid, ownerType, planId, planGroupId, productCategoryId));
//                }
//                if (!hasSerial && isTrackable) {
//                    genericDataDTO.setDataList(itemService.getInOutMacMappingForNonSerializedItemBasedOnProductCondtion(productId, ownerid, ownerType, planId, planGroupId, productCategoryId));
//                }
//                if (!hasSerial && !isTrackable) {
//                    genericDataDTO.setDataList(itemService.getInOutMacMappingForSerializedItemBasedOnProductType(productId, ownerid, ownerType, planId, planGroupId, productCategoryId));
//                }
//            }
//            if(genericDataDTO.getDataList().isEmpty()){
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//            }
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get Item History By product, owner and owner type :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get Item History By product, owner and owner type :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAvailableQtyDetailsByProductAndDestination")
//    public GenericDataDTO getAvailableQtyDetailsByProductAndDestination(@RequestParam(name = "productId")Long productId, @RequestParam(name = "ownerId")Long ownerId, @RequestParam(name = "ownerType")String ownerType, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(productOwnerService.getAvailableQtyDetailsByProductAndDestination(productId, ownerId, ownerType));
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get available qty details by product, owner and owner type :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get available qty details by product, owner and owner type :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getItemForOutward")
//    public GenericDataDTO getItemForOutward(@RequestParam(name = "productId")Long productId, @RequestParam(name = "ownerId")Long ownerId, @RequestParam(name = "ownerType")String ownerType, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            ProductDto productDto = productService.getEntityForUpdateAndDelete(productId);
//            boolean hasSerial = productDto.getProductCategory().isHasSerial();
//            boolean hasMac = productDto.getProductCategory().isHasMac();
//            if (hasMac || hasSerial) {
//                genericDataDTO.setDataList(itemService.getSerializedItemForOutward(productId, ownerId, ownerType));
//            } else {
//                genericDataDTO.setDataList(nonSerializedItemService.getNonSerializedItemForOutward(productId, ownerId, ownerType));
//            }
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get Items for outward :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get items for outward :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAssignOutwardItem")
//    public GenericDataDTO getItemForInward(@RequestParam(name = "outwardId")Long outwardId, @RequestParam(name = "productId")Long productId, @RequestParam(name = "ownerId")Long ownerId, @RequestParam(name = "ownerType")String ownerType, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            InwardDto inwardDto = inwardService.getInwardOfOutwardByOutwardId(outwardId);
//            Long inwardId = inwardDto.getId();
//            boolean hasSerial = inwardDto.getProductId().getProductCategory().isHasSerial();
//            boolean hasMac = inwardDto.getProductId().getProductCategory().isHasMac();
//            if (hasMac || hasSerial) {
//                genericDataDTO.setDataList(itemService.getSerializedItemForInward(inwardId, productId, ownerId, ownerType));
//            } else {
//                genericDataDTO.setDataList(nonSerializedItemService.getNonSerializedItemForInward(inwardId, productId, ownerId, ownerType));
//            }
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get assign items for outward :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get assign items for outward :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getNonTrackableProductQty")
//    public GenericDataDTO getNonTrackableProductQty(@RequestParam(name = "productId")Long productId, @RequestParam(name = "ownerId")Long ownerId, @RequestParam(name = "ownerType")String ownerType, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            Product product = productRepository.findById(productId).get();
//            boolean hasSerial = product.getProductCategory().isHasSerial();
//            boolean isTrackable = product.getProductCategory().isHasTrackable();
//            if (!hasSerial && !isTrackable) {
//                genericDataDTO.setDataList(productOwnerService.getNonTrackableProductQty(productId, ownerId, ownerType));
//            }
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get Item History By product, owner and owner type :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get Item History By product, owner and owner type :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
}
