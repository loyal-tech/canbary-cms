package com.adopt.apigw.modules.InventoryManagement.product;


import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapingDTO;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.model.PopManagementDTO;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.ProductPlanGroupMapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.dto.Productplanmappingdto;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
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
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(value = "ProductController", description = "REST APIs related to product Entity!!!!", tags = "product-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PRODUCT_MANAGEMENT)
public class ProductController extends ExBaseAbstractController<ProductDto> {


    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private ProductServiceImpl productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);


    public ProductController(ProductServiceImpl productService) {
        super(productService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ProductController]";
    }


    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        return super.getAllWithoutPagination(mvnoId);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody ProductDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Successful");
//        if (getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
////        ProductDto productDto = new ProductDto();
//        try {
//            if(entityDTO.getName().length()>250 || entityDTO.getDescription().length()>500){
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Input size is Exceeded");
//                logger.error("Error while creating  product: " + "," + " request: { From : {}}; Response : {{}};Error : Input size is Exceeded : ", req.getHeader("requestFrom"), HttpStatus.NOT_ACCEPTABLE.value());
//            }
//            else {
//                productService.validateProduct(entityDTO);
//                boolean flag = productService.duplicateVerifyAtSave(entityDTO.getName());
//                productService.duplicateProductIdVerifyAtSave(entityDTO.getProductId());
//                if (flag) {
//                    if (getMvnoIdFromCurrentStaff() != null) {
//                        entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//                    }
//                    ProductDto productDto = productService.saveEntity(entityDTO);
//                    genericDataDTO.setData(productDto);
//                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                    genericDataDTO.setResponseMessage("Successful");
//                    logger.info("Product with name " + entityDTO.getName() + " is created successfully " + "," + "request: { From : {}}; Response : {{}}", req.getHeader("requestForm"), APIConstants.SUCCESS);
//                } else {
//                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    genericDataDTO.setResponseMessage(MessageConstants.PRODUCT_NAME_EXITS);
//                    logger.error("Unable to create product with name "+ entityDTO.getName() +" : request: { From : {}}; Response : {{}}; Error : {}; Exception:{}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//                }
//            }
//        } catch (CustomValidationException ex) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to create product with name "+ entityDTO.getName() +" : request: { From : {}}; Response : {{}}; Error : {}; Exception:{}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        } catch (Exception ex) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to create product with name "+ entityDTO.getName() +" : request: { From : {}}; Response : {{}}; Error : {};",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @PostMapping("/searchProduct")
    public GenericDataDTO searchProduct(@RequestBody PaginationRequestDTO pageDto) {
//        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
        return null;
    }

//    @GetMapping("/getAllActiveProduct")
//    public GenericDataDTO getAllActiveProduct() {
////        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
//        return productService.getAllActiveProduct();
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllProductByProductCategory")
//    public GenericDataDTO getAllProductByProductCategory(@RequestParam(name="serviceId")Long serviceId) {
////        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
//        return productService.getAllProductByServiceId(serviceId);
//    }

//    @GetMapping("/getAllNetworkandNaBindProduct")
//    public GenericDataDTO getAllNetworkandNaBindProduct() {
////        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
//        return productService.getAllNetworkandNaBindProduct();
//    }

//    @GetMapping("/getAllChargeType/{chargeType}")
//    public GenericDataDTO getAllChargeType(@PathVariable(name = "chargeType") String chargeType) {
////        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
//        return productService.getAllChargeByType(chargeType);
//    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody ProductDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != null) {
            // TODO: pass mvnoID manually 6/5/2025
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
        }
        boolean flag = productService.deleteVerification(entityDTO.getId().intValue());
        if (flag == true) {
            dataDTO = super.delete(entityDTO, authentication, req);

        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(DeleteContant.PRODUCT_NAME_EXITS);
        }
        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @PostMapping("/searchByNameCategory")
    public GenericDataDTO searchByNameCategory(@RequestBody PaginationRequestDTO requestDTO,@RequestParam Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = productService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder(),mvnoId);
        } catch (Exception ex) {
            throw ex;
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody ProductDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        try {
//            productService.validateProduct(entityDTO);
//            boolean flag = productService.duplicateVerifyAtEdit(entityDTO.getName(), (entityDTO.getId()));
//            productService.duplicateProductIdVerifyAtEdit(entityDTO.getProductId(), (entityDTO.getId()));
//            if (flag) {
//                if (getMvnoIdFromCurrentStaff() != null) {
//                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//                }
//                ProductDto productDto= productService.updateEntity(entityDTO);
//                dataDTO.setData(productDto);
//            } else {
//                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                dataDTO.setResponseMessage(MessageConstants.PRODUCT_NAME_EXITS);
//            }
//        } catch (CustomValidationException e) {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(e.getMessage());
//        }
//        return dataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllProductsByMacSerial")
//    public GenericDataDTO getAllProductsByMacSerial(@RequestParam(value = "macMappingId") Long macMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO = productService.getAllProductsByMacSerial(macMappingId);
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/getAllProductsByProductCategoryId")
//    public GenericDataDTO getAllProductsByProductCategoryId(@RequestParam("pc_id") Long id, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setDataList(productService.getAllProductsByProductCategoryId(id));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Successful");
//            logger.info("Fetching all products by product category id " + id + " :  request: { From : {}}; Response : {{}}", APIConstants.SUCCESS);
//        } catch (CustomValidationException e) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to fetch all products by product category id " + id + " :  request: { From : {} }; Response : {{}};Error :{} ;Exception :{}", req.getHeader("requestFrom"), HttpStatus.NOT_FOUND.value(), e.getMessage());
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to fetch all products by product category id " + id + " :  request: { From : {} }; Response : {{}};Error :{} ;Exception :{}", req.getHeader("requestFrom"), HttpStatus.NOT_FOUND.value(), e.getMessage());
//        }
//        return genericDataDTO;
//    }

    // Get Plan Inventory Id if Customer Plan Category is Individual
//    @GetMapping("/getAllPlanIvnetoryIdOnPlanId/planId")
//    public GenericDataDTO getAllPlanIvnetoryIdOnPlanId(@RequestParam("planId") Long planId, HttpServletRequest req){
////        List<Productplanmappingdto> productplanmappings=null;
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setDataList(productService.getAllPlanInventorysIdOnPlanId(planId));
//            genericDataDTO.setResponseMessage("Successful");
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            logger.info("Fetching all plan inventory id by plan id " + planId + " :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//        } catch (CustomValidationException exception){
//            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            genericDataDTO.setResponseMessage(exception.getMessage());
//            logger.error("Unable to fetch all plan inventory id by plan id " + planId + " :  request: { From : {} }; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), HttpStatus.NOT_FOUND.value(), exception.getMessage());
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to fetch all plan inventory id by plan id " + planId + " :  request: { From : {} }; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), HttpStatus.NOT_FOUND.value(), e.getMessage());
//        }
//        return  genericDataDTO;
//    }

    // Get Plan Inventory Id if Customer Plan Category is PlanGroup
//    @GetMapping("/getAllInventoryIdOnPlanIdAndPlanGroupId")
//    public GenericDataDTO getAllInventoryIdOnPlanIdAndPlanGroupId(@RequestParam(name = "planId") Long planId, @RequestParam(name = "planGroupId") Long planGroupId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try{
//            genericDataDTO.setDataList(productService.getAllInventoryIdOnPlanIdAndPlanGroupId(planId, planGroupId));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//        }
//        catch (Exception exception) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(exception.getMessage());
//        }
//        return genericDataDTO;
//    }
    // get ProductCategory By Product Plan Group MappingId If Customer Plan Category is PlanGroup
//    @GetMapping("/getProductCategoryByProductPlanGroupMappingId")
//    public GenericDataDTO getProductCategoryByProductPlanGroupMappingId(@RequestParam("mappingId") Long mappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(productService.getProductCategoryByProductPlanGroupMappingId(mappingId));
//        } catch (Exception ex){
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            ApplicationLogger.logger.error("Unable to fetch by type  :code:{};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
    // Get Product By Product Plan Group MappingId If Customer Plan Category is PlanGroup
//    @GetMapping("/getProductByProductPlanGroupMappingId")
//    public GenericDataDTO getProductByProductPlanGroupMappingId(@RequestParam("mappingId") Integer mappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(productService.getProductByProductPlanGroupMappingId(mappingId));
//        } catch (Exception ex){
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            ApplicationLogger.logger.error("Unable to fetch by type  :code:{};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//    @PostMapping("/getAllItemBasedOnProduct")
//    public List<InOutWardMACMapingDTO> getAllItemBasedOnProduct(@RequestBody List<Long> productId){
//        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS=null;
//        try {
//            inOutWardMACMapingDTOS= productService.getAllItemBasedOnProduct(productId);
//        }catch (Exception exception){
//            throw new RuntimeException(exception.getMessage());
//        }
//        return  inOutWardMACMapingDTOS;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllProductForNonTrackableProductCategory")
//    public GenericDataDTO getAllProductForNonTrackableProductCategory() {
////        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
//        return productService.getAllProductForNonTrackableProductCategory();
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllCBProducts")
//    public GenericDataDTO getAllCBProducts() {
//        return productService.getAllCBProducts();
//    }
//
//    @GetMapping("/getAllProductbasedOnItemType")
//    public GenericDataDTO getAllProductbasedOnItemType(@RequestParam("itemtype")String itemType) {
//        return productService.getAllProductbasedOnItemType(itemType);
//    }

//    @GetMapping("/getAllSerializedItemBaseOnProduct")
//    public GenericDataDTO getAllSerializedItemBaseOnProduct(@RequestParam("productId")Long productId, @RequestParam("ownerId") Long ownerId, @RequestParam("ownerType") String ownerType) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            String itemType = "Serialized Item";
//            genericDataDTO.setDataList(productService.getAllSerializedItemBaseOnProduct(productId,itemType, ownerId, ownerType));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Successful");
//            logger.info("Fetching All Serialized Item  :  request:; Response : {{}}", genericDataDTO.getResponseCode());
//         }catch (CustomValidationException ex){
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Fetch All Serialized Item :  Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllProductsByCustomerOwned")
//    public GenericDataDTO getAllProductsByCustomerOwned(@RequestParam("custId") Long custId) {
//        return productService.getAllProductsByCustomerOwned(custId);
//    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllNetworkAndNABindNonSerializedProduct")
//    public GenericDataDTO getAllNetworkAndNABindNonSerializedProduct() {
////        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
//        return productService.getAllNetworkAndNABindNonSerializedProduct();
//    }
//    @GetMapping("/getMappingDetails")
//    public GenericDataDTO getMappingDetails(@RequestParam(name = "planGroupId", required = false) Long planGroupId, @RequestParam(name = "planId") Long planId, @RequestParam(name = "productCategoryId") Long productCategoryId, @RequestParam(name = "productId") Long productId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (planGroupId != null) {
//                genericDataDTO.setDataList(productService.getProductPlanGroupMappingDetails(planGroupId, planId, productCategoryId, productId));
//            } else {
//                genericDataDTO.setDataList(productService.getProductPlanMappingDetails(planId, productCategoryId, productId));
//            }
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//        }
//        return genericDataDTO;
//    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllActiveProductsByProductCategoryId")
//    public GenericDataDTO getAllActiveProductsByProductCategoryId(@RequestParam("pc_id") Long id) throws Exception {
//        return productService.getAllActiveProductsByProductCategoryId(id);
//    }
}
