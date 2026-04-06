package com.adopt.apigw.modules.InventoryManagement.inward;


import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.Cas.Domain.CasMaster;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingDto;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.*;
import com.adopt.apigw.modules.InventoryManagement.ItemGroup.ItemAssemblyServiceImp;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItem.NonSerializedItemServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMappingDto;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMappingService;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.modules.InventoryManagement.item.ItemDto;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.adopt.apigw.modules.InventoryManagement.item.ItemServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductDto;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.product.ProductServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategoryDto;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategoryService;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerService;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.service.common.CustomerCafAssignmentService;
import com.adopt.apigw.service.common.EzBillServiceUtility;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.ezbill.entity.ReactivateBoxResponse;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@Api(value = "InwardController", description = "REST APIs related to inward Entity!!!!", tags = "inwards-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.INWARDS)
public class InwardController extends ExBaseAbstractController<InwardDto> {

    @Autowired
    InwardServiceImpl inwardService;
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;

    @Autowired
    InventoryMappingService inventoryMappingService;
    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private InOutWardMACService inOutWardMACService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private InwardRepository inwardRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NonSerializedItemServiceImpl nonSerializedItemService;

    @Autowired
    private ItemAssemblyServiceImp itemAssemblyServiceImp;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private EzBillServiceUtility ezBillServiceUtility;
    @Autowired
    private InOutWardMacRepo inOutWardMacRepo;
    @Autowired
    private ProductOwnerService productOwnerService;
    @Autowired
    private HierarchyService hierarchyService;
    public InwardController(InwardServiceImpl inwardService) {
        super(inwardService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[InwardController]";
    }
    private static final Logger logger = LoggerFactory.getLogger(InwardController.class);

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody InwardDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Create");
//        try {
//            if(entityDTO.getType() == null)
//                entityDTO.setType(CommonConstants.NEW);
//            String defaultTimezone = TimeZone.getDefault().getID();
//            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
//            Integer second = tz.getOffset(new Date().getTime()) / 1000 ;
//            LocalDateTime localDateTime = entityDTO.getInwardDateTime().plusSeconds(second);
//            entityDTO.setInwardDateTime(localDateTime);
//            InwardDto inwardDto = inwardService.saveEntity(entityDTO, false, false);
//            genericDataDTO.setData(inwardDto);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, inwardDto.getId(), inwardDto.getInwardNumber().toString());
//            logger.info("InWard controller successfully created  :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(), APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to search :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),HttpStatus.NOT_ACCEPTABLE, APIConstants.FAIL,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ADD + "\")")
//    public GenericDataDTO save(@Valid @RequestBody List<InwardDto> entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Create");
//        try {
//            //InwardDto inwardDto = inwardService.saveEntity(entityDTO, false, false);
//            List<InwardDto> dtoList = new ArrayList<>();
//            InwardDto inwardDto = new InwardDto();
//            for(int i=0;i<entityDTO.size();i++) {
//                if(entityDTO.get(i).getType() == null)
//                    entityDTO.get(i).setType(CommonConstants.NEW);
//                inwardDto = inwardService.saveEntity((InwardDto) entityDTO, false, false);
//                dtoList.add(inwardDto);
//                genericDataDTO.setData(dtoList);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, dtoList.get(i).getId(), dtoList.get(i).getInwardNumber().toString());
//            }
//            logger.info("InWard controller successfully created  :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(), APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to search :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),HttpStatus.NOT_ACCEPTABLE, APIConstants.FAIL,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getInwardDetailsByProductAndDestination")
//    public GenericDataDTO getInwardDetailsByProductAndDestId(@RequestParam(name = "productId")Long productId, @RequestParam(name = "destinationId")Long destinationId, @RequestParam(name = "destinationType")String destinationType, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(inwardService.getInwardDetailsByProductAndDestination(productId, destinationId, destinationType));
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get Inward Details By product and warehouse :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get Inward product by product and warehouse :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody InwardDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        try {
//            InwardDto existingInward = inwardService.getEntityById(entityDTO.id);
//            String defaultTimezone = TimeZone.getDefault().getID();
//            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
//            Integer second = tz.getOffset(new Date().getTime()) / 1000 ;
//            LocalDateTime localDateTime = entityDTO.getInwardDateTime().plusSeconds(second);
//            entityDTO.setInwardDateTime(localDateTime);
//            InwardDto inwardDto = inwardService.updateEntity(entityDTO, false,false);
//            genericDataDTO.setData(inwardDto);
//
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, inwardDto.getId(), inwardDto.getInwardNumber().toString());
//            logger.info("Inward with old number "+existingInward.getInwardNumber()+" is updated to "+entityDTO.inwardNumber+" is Successfully updated :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to Update Inward With  "+entityDTO.inwardNumber+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.EXPECTATION_FAILED,ce.getMessage());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Update Inward With  "+entityDTO.inwardNumber+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }


//   @PostMapping("/searchByCustomerAndPopAndServiceAreaName")
//   public GenericDataDTO searchByCustomerAndPopAndServiceAreaName(@RequestBody PaginationRequestDTO requestDTO,@RequestParam("staffId")Long staffId,@RequestParam("filtername")String filterName,@RequestParam("isSerelized") boolean isSerelized) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            genericDataDTO = inwardService.searchByCustomerAndPopAndServiceAreaName(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
//                    requestDTO.getSortBy(), requestDTO.getSortOrder(),staffId,filterName,isSerelized);
//        } catch (Exception ex) {
//            throw ex;
//        }
//        return genericDataDTO;
//    }



//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllInwardByProductAndStaff")
//    public GenericDataDTO getAllInwardByProductAndStaff(@RequestParam(name = "productId") Long productId, @RequestParam(name = "staffId") Long staffId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(inwardService.getAllInwardByProductAndStaff(productId, staffId));
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, inwardDto.getId(), inwardDto.getInwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }



//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllInwardByProductAndStaffforpopandserivearea")
//    public GenericDataDTO getAllInwardByProductAndStaffforPopandServiceArea(@RequestParam(name = "productId") Long productId, @RequestParam(name = "staffId") Long staffId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(inwardService.getAllNetworkBindInwards(productId, staffId));
//         } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllInwardByProductAndStaffforPopandSeriveareaandCustomer")
//    public GenericDataDTO getAllInwardByProductAndStaffforPopandServiceAreaandCustomer(@RequestParam(name = "productId") Long productId, @RequestParam(name = "staffId") Long staffId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(inwardService.getAllNetworkBindandCustomerandPopInwards(productId, staffId));
//         } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @PostMapping("/getByCustomerId")
//    public GenericDataDTO getByCustomerId(@RequestBody PaginationRequestDTO requestDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return customerInventoryMappingService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getInwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @GetMapping("/getAllCustomerInventoryList")
//    public GenericDataDTO getAllCustomerInventoryList(@RequestParam("custId") Integer custId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setDataList(customerInventoryMappingService.getAllCustomerInventoryList(custId));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }




//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @PostMapping("/getByOwnerIdAndType")
//    public GenericDataDTO getByOwnerIdAndType(@RequestBody PaginationRequestDTO requestDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return inventoryMappingService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getInwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ADD + "\")")
//@PostMapping("/assignToCustomer")
//public GenericDataDTO assignToCustomer(@RequestBody CustomerInventoryMappingDto inventoryMappingDto) {
//    GenericDataDTO genericDataDTO = new GenericDataDTO();
//    try {
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        inventoryMappingDto.setDiscount(0d);
//        ProductDto productDto = productService.getEntityForUpdateAndDelete(inventoryMappingDto.getProductId());
//        boolean hasMac = productDto.getProductCategory().isHasMac();
//        boolean hasSerial = productDto.getProductCategory().isHasSerial();
//        boolean hasCas = productDto.getProductCategory().isHasCas();
//        if (hasMac){
//            customerInventoryMappingService.validateMac(inventoryMappingDto);
//        }
//        if (hasSerial) {
//            customerInventoryMappingService.validateSerialNumber(inventoryMappingDto);
//        }
//        if (hasCas) {
//            customerInventoryMappingService.validateConnectionNumber(inventoryMappingDto);
//        }
//        String defaultTimezone = TimeZone.getDefault().getID();
//        TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
//        Integer second = tz.getOffset(new Date().getTime()) / 1000 ;
//        LocalDateTime localDateTime = inventoryMappingDto.getAssignedDateTime().plusSeconds(second);
//        inventoryMappingDto.setAssignedDateTime(localDateTime);
//        List<CustomerInventoryMappingDto> customerInventoryMappingDtoList=customerInventoryMappingService.saveEntityList(inventoryMappingDto);
//        //update inoutward history
//        if(inventoryMappingDto.getExternalItemId() == null && !inventoryMappingDto.isItemAssemblyflag()) {
//            List<Long> custInventoryId = customerInventoryMappingDtoList.stream().filter(customerInventoryMappingDto -> customerInventoryMappingDto.getCustomerId().equals(inventoryMappingDto.getCustomerId())).map(CustomerInventoryMappingDto::getId).collect(Collectors.toList());
//            inOutWardMACService.updateInoutwardMacMappingforSerialized(custInventoryId.get(0), inventoryMappingDto);
//        }
//        //update product owner after assign inventory to customer
//        if(inventoryMappingDto.getExternalItemId() == null) {
//            productOwnerService.updateProductOwnerForSerializedProduct(inventoryMappingDto.getQty(), inventoryMappingDto.getProductId(), Long.valueOf(inventoryMappingDto.getStaffId()), CommonConstants.STAFF);
//        }
//        if(inventoryMappingDto.getExternalItemId() ==null && inventoryMappingDto.isItemAssemblyflag() && customerInventoryMappingDtoList.size()==2)  {
//           List<CustomerInventoryMappingDto> customerInventoryMappingDtos= customerInventoryMappingService.setCustomerInventoryIdToItemHistory(customerInventoryMappingDtoList,inventoryMappingDto);
//            genericDataDTO.setDataList(customerInventoryMappingDtos);
//
//        }else {
//            genericDataDTO.setDataList(customerInventoryMappingDtoList);
//        }
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//    } catch (CustomValidationException ce) {
//        ApplicationLogger.logger.error(ce.getMessage(), ce);
//        genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//        genericDataDTO.setResponseMessage(ce.getMessage());
//    } catch (Exception ex) {
//        ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//        genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//        genericDataDTO.setResponseMessage(ex.getMessage());
//    }
//    return genericDataDTO;
//
//}

//    @Transactional
//    @PostMapping("/assignToEndOwner")
//    public GenericDataDTO assignToEndOwner(@RequestBody InventoryMappingDto inventoryMappingDto) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            ProductDto productDto = productService.getEntityForUpdateAndDelete(inventoryMappingDto.getProductId());
//            boolean hasMac = productDto.getProductCategory().isHasMac();
//            boolean hasSerial = productDto.getProductCategory().isHasSerial();
//            if (hasMac){
//                inventoryMappingService.validateMac(inventoryMappingDto);
//            }
//            if (hasSerial) {
//                inventoryMappingService.validateSerialNumber(inventoryMappingDto);
//            }
//            //inventoryMappingService.validateMac(inventoryMappingDto);
//            String defaultTimezone = TimeZone.getDefault().getID();
//            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
//            Integer second = tz.getOffset(new Date().getTime()) / 1000 ;
//            LocalDateTime localDateTime = inventoryMappingDto.getAssignedDateTime().plusSeconds(second);
//            inventoryMappingDto.setAssignedDateTime(localDateTime);
//            genericDataDTO.setData(inventoryMappingService.saveEntity(inventoryMappingDto));
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

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
//    @PostMapping("/replaceInventory")
//    public GenericDataDTO replaceInventory(@RequestBody List<ApproveReplaceAllInventoryDTO> approveReplaceAllInventoryDTOS,Long customerId,@RequestParam("inventoryType") String ownerShipType,@RequestParam("replacementReason") String replacementReason,@RequestParam("approvalRemark") String approvalRemark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(customerInventoryMappingService.replaceAllInvetories(approveReplaceAllInventoryDTOS,customerId,ownerShipType,replacementReason,approvalRemark));
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

//    @PostMapping("/searchByProductAndStatusAndServiceName")
//    public GenericDataDTO searchByProductAndStatusAndServiceName(@RequestParam("filterColumn")String filterColumn,@RequestParam("filterValue")String filterValue,@RequestParam("customerId") Long customerId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//              return   inwardService.searchByProductAndStatusAndServiceName(filterColumn,filterValue,customerId);
//        } catch (Exception ex) {
//            throw ex;
//        }
//     }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
//    @GetMapping("/replaceInventoryFromEndOwner")
//    public GenericDataDTO replaceInventoryFromEndOwner(@RequestParam(name = "oldMacMappingId") Long oldMacMappingId, @RequestParam(name = "newMacMappingId") Long newMacMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setData(inventoryMappingService.replaceInventory(oldMacMappingId, newMacMappingId));
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

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody InwardDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Delete");
//        boolean flag = inwardService.deleteVerification(entityDTO.getId().intValue());
//        if (flag) {
//            inwardService.deleteInward(entityDTO);
//            if (entityDTO.getApprovalStatus().equalsIgnoreCase("Pending") && entityDTO.getOutwardId() == null) {
//                entityDTO.setApprovalStatus("Deleted");
//            }
//            genericDataDTO = super.delete(entityDTO, authentication, req);
//            InwardDto inwardDto = (InwardDto) genericDataDTO.getData();
//            if(entityDTO != null)
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_INWARD_MANAGEMENT,
//                        AclConstants.OPERATION_INWARD_MANAGEMENT_DELETE, req.getRemoteAddr(), null, inwardDto.getId(), inwardDto.getInwardNumber());
//            logger.info("Deleting Inward With InwardNumber "+entityDTO.getInwardNumber()+" is successfull :   Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        } else {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(DeleteContant.INWARD_NUMBER_DELETE_EXIST);
//            logger.error("Unable to Delete Inward with InwardNumber "+entityDTO.getInwardNumber()+"  : Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }




//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
//    @PostMapping("/approveInventory")
//    public GenericDataDTO approveInventory(@RequestBody List<Long> customerInventoryMappingId, boolean isApproveRequest,Integer nextstaff, String remark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting Inventory Approve from  with id  " + customerInventoryMappingId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return customerInventoryMappingService.approveIndividualInventory(customerInventoryMappingId, isApproveRequest,nextstaff, remark);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (CustomValidationException ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Approve inventory  with " + customerInventoryMappingId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Approve inventory  with " + customerInventoryMappingId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
//    @GetMapping("/approveInventoryFromOwner")
//    public GenericDataDTO approveInventoryFromOwner(@RequestParam(name = "inventoryMappingId") Long inventoryMappingId,@RequestParam(name = "isApproveRequest") boolean isApproveRequest, @RequestParam(name = "inventoryApprovalRemark") String inventoryApprovalRemark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting Inventory Approve from  with id  "+inventoryMappingId+"  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            return inventoryMappingService.approveInventory(inventoryMappingId,isApproveRequest, inventoryApprovalRemark);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Approve inventory  with "+inventoryMappingId+":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
//    @PostMapping("/approveReplaceInventory")
//    public GenericDataDTO approveReplaceInventory(@RequestBody List<ApproveReplaceAllInventoryDTO> approveReplaceAllInventoryDTOS, @RequestParam(name = "billAble") String billAble, @RequestParam(name = "isApproveRequest") boolean isApproveRequest) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return customerInventoryMappingService.approveAllReplaceInventory(approveReplaceAllInventoryDTOS, Boolean.parseBoolean(billAble),isApproveRequest);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
//    @GetMapping("/approveReplaceInventoryFromEndOwner")
//    public GenericDataDTO approveReplaceInventoryFromEndOwner(@RequestParam(name = "macMappingId") Long macMappingId, @RequestParam(name = "billAble") String billAble, @RequestParam(name = "isApproveRequest") boolean isApproveRequest) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return inventoryMappingService.approveReplaceInventory(macMappingId, Boolean.parseBoolean(billAble),isApproveRequest);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @PostMapping(value = "/getAllAssignInventories")
//    public GenericDataDTO getAllAssignInventories(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "staffId") Long staffId) {
//        String SUBMODULE = getModuleNameForLog() + " [getAllAssignInventories()] ";
//
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            genericDataDTO = inwardService.getAssignInventories(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), staffId);
//            if (null != genericDataDTO) {
//                logger.info("fetching allAssigned inventories:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//                logger.error("Unable to fetch all inventories :  request: { From : {},}; Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            }
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to  to fetch all inventories :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @PostMapping("/getCustomerInventoryMappingByStaffId")
//    public GenericDataDTO getCustomerInventoryMappingByStaffId(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "staffId") Long staffId, boolean isGetSerializedItem) {
//        String SUBMODULE = getModuleNameForLog() + " [getCustomerInventoryMappingByStaffId()] ";
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            genericDataDTO = customerInventoryMappingService.getCustomerInventoryMappingByStaffId(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), staffId, isGetSerializedItem);
//            if (null != genericDataDTO) {
//                logger.info("fetching allAssigned inventories:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//                logger.error("Unable to fetch all inventories :  request: { From : {},}; Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            }
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to  to fetch all inventories :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
//        }
//        return null;
//    }


//    @PostMapping("/getPopByInventoryMappingByStaffId")
//    public GenericDataDTO getPopByInventoryMappingByStaffId(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "staffId") Long staffId, boolean isGetSerializedItem) {
//        String SUBMODULE = getModuleNameForLog() + " [getPopByInventoryMappingByStaffId()] ";
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            genericDataDTO = inventoryMappingService.getPopInventoryMappingByStaffId(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), staffId, isGetSerializedItem);
//            if (null != genericDataDTO) {
//                logger.info("fetching allAssigned inventories:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//                logger.error("Unable to fetch all inventories :  request: { From : {},}; Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            }
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to  to fetch all inventories :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
//        }
//        return null;
//    }
//    @PostMapping("/getServiceAreaByInventoryMappingByStaffId")
//    public GenericDataDTO getServiceAreaByInventoryMappingByStaffId(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "staffId") Long staffId, boolean isGetSerializedItem) {
//        String SUBMODULE = getModuleNameForLog() + " [getServiceAreaByInventoryMappingByStaffId()] ";
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            genericDataDTO = inventoryMappingService.getServiceAreaInventoryMappingByStaffId(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), staffId, isGetSerializedItem);
//            if (null != genericDataDTO) {
//                logger.info("fetching allAssigned inventories:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//                logger.error("Unable to fetch all inventories :  request: { From : {},}; Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            }
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to  to fetch all inventories :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
//        }
//        return null;
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getInventoryMappingByStaffId")
//    public GenericDataDTO getInventoryMappingByStaffId(@RequestParam(name = "staffId") Long staffId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String staffname=customerInventoryMappingService.getStaffDetails(staffId);
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(inventoryMappingService.getInventoryMappingByStaffId(staffId));
//            logger.info("Outward get Customer inventory by Staff  "+staffname+"  is created Successfully:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to  get Customer inventory by Staff  "+staffname+"  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
//
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//
//    }

 //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @PostMapping(value = "/getAllInventoriesByOwner")
//    public GenericDataDTO getAllInventoriesByOwner(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "ownerId") Long ownerId, @RequestParam(name = "ownerType") String ownerType) {
//        String SUBMODULE = getModuleNameForLog() + " [getAllAssignInventories()] ";
//
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            genericDataDTO = inwardService.getAllInventoriesByOwner(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), ownerId,ownerType);
//            if (null != genericDataDTO) {
//                logger.info("fetching allAssigned inventories:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//                logger.error("Unable to fetch all inventories :  request: { From : {},}; Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            }
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to  to fetch all inventories :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
//    @PutMapping("/inwardApproval")
//    public GenericDataDTO saveInwardApproval(@Valid @RequestBody InwardDto inwardDto, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            boolean hasSerial = inwardDto.productId.getProductCategory().isHasSerial();
//            boolean isTrackable = inwardDto.productId.getProductCategory().isHasTrackable();
//            boolean hasMac = inwardDto.productId.getProductCategory().isHasMac();
//            String uom = inwardDto.productId.getProductCategory().getUnit();
//            try{
//                Inward inward = inwardRepository.findById(inwardDto.getId()).get();
//                if(inwardDto.getApprovalStatus().equalsIgnoreCase("Approve")) {
//                    if (inward.getOutwardId() != null && (hasMac || hasSerial)) {
//                        if (!Objects.equals(inward.getInTransitQty(), inward.getTotalMacSerial())) {
//                            throw new Exception("No items are present in inward");
//                        }
//                    }
//                    if (inward.getOutwardId() == null) {
//                        if (hasMac || hasSerial) {
//                            inOutWardMACService.saveAutoMAC(inward);
////                            inOutWardMACService.saveItem(inwardDto);
//                        }
//                        if (!hasSerial && isTrackable){
//                            inOutWardMACService.saveNonSerializedItemsAfterApprovalInward(inwardDto, uom);
//                        }
//                    }
//                }
//                genericDataDTO.setData(inwardService.saveInwardApproval(inwardDto.getId(), inwardDto.getApprovalStatus(), inwardDto.getApprovalRemark(), inwardDto.productId.getId()));
//            }
//            catch (Exception ex){
//                ex.getMessage();
//            }
//
//
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get Inward Details By product and warehouse :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get Inward product by product and warehouse :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping(value = "/getAllInwards")
//    public GenericDataDTO getAllInwards(){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(inwardService.getAllInwards());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getItemForInward")
//    public GenericDataDTO getItemForInward(@RequestParam(name = "inwardId")Long inwardId, @RequestParam(name = "productId")Long productId, @RequestParam(name = "ownerId")Long ownerId, @RequestParam(name = "ownerType")String ownerType, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(inwardId);
//            boolean hasSerial = inwardDto.productId.getProductCategory().isHasSerial();
//            boolean hasMac = inwardDto.productId.getProductCategory().isHasMac();
//            if (hasMac || hasSerial) {
//                genericDataDTO.setDataList(itemService.getSerializedItemForInward(inwardId, productId, ownerId, ownerType));
//            } else {
//                genericDataDTO.setDataList(nonSerializedItemService.getNonSerializedItemForInward(inwardId, productId, ownerId, ownerType));
//            }
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get Inward Details By product and warehouse :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get Inward product by product and warehouse :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//    @PostMapping("/assignNonSerializedItemToCustomer")
//    public GenericDataDTO assignNonSerializedItemToCustomer(@RequestBody CustomerInventoryMappingDto inventoryMappingDto) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            String defaultTimezone = TimeZone.getDefault().getID();
//            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
//            Integer second = tz.getOffset(new Date().getTime()) / 1000 ;
//            LocalDateTime localDateTime = inventoryMappingDto.getAssignedDateTime().plusSeconds(second);
//            inventoryMappingDto.setAssignedDateTime(localDateTime);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(customerInventoryMappingService.saveNonSerializedEntity(inventoryMappingDto));
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
//    @PostMapping("/assignNonSerializedItemToEndOwner")
//    public GenericDataDTO assignNonSerializedItemToEndOwner(@RequestBody InventoryMappingDto inventoryMappingDto) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            String defaultTimezone = TimeZone.getDefault().getID();
//            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
//            Integer second = tz.getOffset(new Date().getTime()) / 1000 ;
//            LocalDateTime localDateTime = inventoryMappingDto.getAssignedDateTime().plusSeconds(second);
//            inventoryMappingDto.setAssignedDateTime(localDateTime);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(inventoryMappingService.saveNonSerializedEntity(inventoryMappingDto));
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

    //Get Customer Based on DTV History
//    @GetMapping("/getCustomerbasedOnDtvHistory")
//    public GenericDataDTO getCustomerbasedOnDtvHistory(@RequestParam("customerId") Long customerid) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//          return  customerInventoryMappingService.getAllDtvHistoryByCustomer(customerid);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }
//    @PostMapping("/reactivateBoxResponse")
//    public GenericDataDTO reactivateBoxResponse(@RequestBody List<Long> customerInventoryMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            customerInventoryMappingService.getCas(customerInventoryMappingId);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Reactivate Successfully");
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @PostMapping("/pairBox")
//    public GenericDataDTO pairBox(@RequestBody List<Long> customerInventoryMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            customerInventoryMappingService.getpairSTB(customerInventoryMappingId);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Paired  Successfully");
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @PostMapping("/unPairBox")
//    public GenericDataDTO unPairBox(@RequestBody List<Long> customerInventoryMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            customerInventoryMappingService.getunpairSTB(customerInventoryMappingId);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Unpaired Successfully");
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/getDetailsBasedOnConnectionNumber")
//    public GenericDataDTO getDetailsBasedOnConnectionNumber(@RequestParam("connectionNumber")String connectionNumber) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setDataList(customerInventoryMappingService.getDetailsBasedOnConnectionNumber(connectionNumber));
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/swapServicesFromParantCustomerToChildCustomer")
//    public GenericDataDTO swapServicesFromParantCustomerToChildCustomer(@RequestParam("childconnectionNumber")String childconnectionNumber,@RequestParam("parentconnectionNumber")String parentconnectionNumber,@RequestParam("serviceId")Long serviceId,@RequestParam("serviceName")String serviceName) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return customerInventoryMappingService.swapServicesFromParantToChild(childconnectionNumber,parentconnectionNumber,serviceId,serviceName);
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            return genericDataDTO;
//        }
//     }


//    @GetMapping("/getChildAndParentCustomer")
//    public GenericDataDTO getChildAndParentCustomer(@RequestParam("customerId")Long customerId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return customerInventoryMappingService.getChildAndParentCustomer(customerId);
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            return genericDataDTO;
//        }
//    }


//    @GetMapping("/getActiveSerialnumberByConnectionNo")
//    public GenericDataDTO getActiveSerialnumberByConnectionNo(@RequestParam("connectionNumber") String connectionNumber, @RequestParam("customerId") Integer customerId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setDataList(customerInventoryMappingService.getActiveSerialnumberByConnectionNo(connectionNumber, customerId));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Successful");
//        } catch (CustomValidationException exception) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(exception.getMessage());
//        }
//        return genericDataDTO;
//    }

    //Assign From Staff List for Inventory Workflow
//    @GetMapping("/assignFromStaffList")
//    public GenericDataDTO assignFromStaffList(@RequestParam(name = "nextAssignStaff") Integer nextAssignStaff, @RequestParam(name = "eventName") String eventName, @RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "isApproveRequest") boolean isApproveRequest, @RequestParam(name = "isAssignPairItem") boolean isAssignPairItem) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
//            genericDataDTO.setResponseMessage("Assigned to next staff");
//            customerInventoryMappingService.assignFromStaffList(nextAssignStaff, eventName, entityId, isApproveRequest, isAssignPairItem);
//            genericDataDTO.setTotalRecords(0);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException e) {
//            genericDataDTO.setResponseMessage(e.getMessage());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
}
