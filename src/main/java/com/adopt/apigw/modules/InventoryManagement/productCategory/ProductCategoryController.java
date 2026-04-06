package com.adopt.apigw.modules.InventoryManagement.productCategory;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL +UrlConstants.PRODUCT_CATEGORY)
public class ProductCategoryController extends ExBaseAbstractController<ProductCategoryDto> {

    @Autowired
    AuditLogService auditLogService;
    @Autowired
    ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        super(productCategoryService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ProductCategory]";
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody ProductCategoryDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        try {
//            if (getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            boolean isHasCas = entityDTO.isHasCas();
//            boolean isHasMac = entityDTO.isHasMac();
//            boolean isHasSerial = entityDTO.isHasSerial();
//            boolean isHasPort = entityDTO.isHasPort();
//            if(isHasPort) {
//                if (!isHasMac && !isHasSerial) {
//                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select Has Mac/ Has Serial", null);
//                }
//            }
//            if(isHasCas) {
//                if (isHasMac) {
//                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please only select Has Serial ", null);
//                } else if(!isHasSerial) {
//                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select Has Serial", null);
//                }
//            }
//            if (isHasCas && (entityDTO.getDtvCategory()==null || entityDTO.getDtvCategory().equals(""))) {
//                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select dtv category", null);
//            }
//            if (entityDTO.type.equalsIgnoreCase("CustomerBind, NA") || entityDTO.type.equalsIgnoreCase("NA, NetworkBind") ||  entityDTO.type.equalsIgnoreCase("CustomerBind, NA, NetworkBind")) {
//                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "This combination of product category type is not valid", null);
//            }
//            boolean flag = productCategoryService.duplicateVerifyAtSave(entityDTO.getName());
//            if (entityDTO.getName().length() > 250 || entityDTO.getUnit().length() > 100) {
//                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                dataDTO.setResponseMessage("Input size is Exceeded");
//            } else {
//                if (flag) {
//                    dataDTO = super.save(entityDTO, result, authentication, req);
//                    ProductCategoryDto productCategoryDto = (ProductCategoryDto) dataDTO.getData();
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_CATEGORY, AclConstants.OPERATION_PRODUCT_CATEGORY_ADD, req.getRemoteAddr(), null, productCategoryDto.getId(), productCategoryDto.getName());
//
//                } else {
//                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    dataDTO.setResponseMessage(MessageConstants.PRODUCT_NAME_EXITS);
//                }
//            }
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(ce.getMessage());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(ex.getMessage());
//        }
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody ProductCategoryDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if (getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        if (entityDTO.type.equalsIgnoreCase("CustomerBind, NA") || entityDTO.type.equalsIgnoreCase("NA, NetworkBind") ||  entityDTO.type.equalsIgnoreCase("CustomerBind, NA, NetworkBind")) {
//            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "This combination of product category type is not valid", null);
//        }
//        boolean flag = productCategoryService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId());
//        if(entityDTO.getName().length()>250 || entityDTO.getUnit().length()>100){
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage("Input size is Exceeded");
//        }
//        else {
//            if (flag) {
//                dataDTO = super.update(entityDTO, result, authentication, req);
//                ProductCategoryDto productCategoryDto = (ProductCategoryDto) dataDTO.getData();
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_CATEGORY, AclConstants.OPERATION_PRODUCT_CATEGORY_EDIT, req.getRemoteAddr(), null, productCategoryDto.getId(), productCategoryDto.getName());
//
//            } else {
//                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                dataDTO.setResponseMessage(MessageConstants.PRODUCT_NAME_EXITS);
//            }
//        }
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody ProductCategoryDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        boolean flag = productCategoryService.deleteVerification(entityDTO.getId().intValue());
//        if (flag) {
//            dataDTO = super.delete(entityDTO, authentication, req);
//            ProductCategoryDto productCategoryDto = (ProductCategoryDto) dataDTO.getData();
//            if(productCategoryDto != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_CATEGORY, AclConstants.OPERATION_PRODUCT_CATEGORY_DELETE, req.getRemoteAddr(), null, productCategoryDto.getId(), productCategoryDto.getName());
//            }
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(DeleteContant.PRODUCT_CATEGORY_EXITS);
//        }
//        return dataDTO;
//    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_CATEGORY_ALL + "\",\""  + AclConstants.OPERATION_PRODUCT_CATEGORY_VIEW + "\")")
    @PostMapping("/searchByNameCategory")
    public GenericDataDTO searchByNameCategory(@RequestBody PaginationRequestDTO requestDTO,@RequestParam Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = productCategoryService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder(),mvnoId);
        } catch (Exception ex) {
            throw ex;
        }
        return genericDataDTO;
    }

//    @GetMapping("/getAllProductCategoriesByType")
//    public GenericDataDTO getAllProductCategoriesByType(@Valid @RequestParam String Type) {
//        return productCategoryService.getAllProductCategoriesByType(Type);
//    }

//    @GetMapping("/getallproductbycustomerbind")
//    public List<ProductCategory> getAllProduct() {
//        return productCategoryService.getallproduct();
//    }
//    @GetMapping("/getAllActiveProductCategories")
//    public GenericDataDTO getAllActiveProductCategories() {
//        return productCategoryService.getAllActiveProductCategories();
//    }
//
//
//    @GetMapping("/getAllActiveProductCategoriesByCB")
//    public GenericDataDTO getAllActiveProductCategoriesByCB() {
//        return productCategoryService.getAllActiveProductCategoriesByCB();
//    }
}
