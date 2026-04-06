package com.adopt.apigw.modules.InventoryManagement.productCategory;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.InventoryManagement.product.QProduct;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductCategoryService extends ExBaseAbstractService<ProductCategoryDto, ProductCategory, Long> {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private MessageSender messageSender;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository, IBaseMapper<ProductCategoryDto, ProductCategory> mapper) {
        super(productCategoryRepository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ProductCategory]";
    }

    GenericDataDTO getAllProductCategory() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            QProductCategory qProductCategory = QProductCategory.productCategory;
            BooleanExpression booleanExpression = qProductCategory.isNotNull().and(qProductCategory.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProductCategory.isDeleted.eq(false));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProductCategory.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            genericDataDTO.setDataList(IterableUtils.toList(this.productCategoryRepository.findAll(booleanExpression)));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }

    @Override
    public ProductCategoryDto saveEntity(ProductCategoryDto entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff(null));      // TODO: pass mvnoID manually 6/5/2025
        ProductCategoryDto productCategoryDto = super.saveEntity(entity);
        //messageSender.send(productCategoryDto, RabbitMqConstants.QUEUE_PRODUCTCATEGORY_INTEGRATOIN);
        return productCategoryDto;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = productCategoryRepository.duplicateVerifyAtSave(name);
            else count = productCategoryRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = productCategoryRepository.duplicateVerifyAtSave(name);
            else count = productCategoryRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) == 1)
                    countEdit = productCategoryRepository.duplicateVerifyAtEdit(name, id);
                else countEdit = productCategoryRepository.duplicateVerifyAtEdit(name, id, mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public ProductCategory getById(Long id) {
        return productCategoryRepository.findById(id).get();
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = productCategoryRepository.deleteVerify(id);
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        QProductCategory qProductCategory = QProductCategory.productCategory;
        BooleanExpression booleanExpression = qProductCategory.isNotNull().and(qProductCategory.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<ProductCategory> paginationList = null;
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProductCategory.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);

            paginationList = productCategoryRepository.findAll(booleanExpression, pageRequest);

            if (paginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public GenericDataDTO getByNameOrType(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getByNameOrType()] ";

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        QProductCategory qProductCategory = QProductCategory.productCategory;
        BooleanExpression booleanExpression = qProductCategory.isNotNull()
                .and(qProductCategory.isDeleted.eq(false))
                .and((qProductCategory.name.likeIgnoreCase("%" + s1 + "%")).or(qProductCategory.type.like("%" + s1 + "%")));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qProductCategory.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        }
        Page<ProductCategory> productCategories = productCategoryRepository.findAll(booleanExpression, pageRequest);
        if (null != productCategories && 0 < productCategories.getSize()) {
            makeGenericResponse(genericDataDTO, productCategories);
        }
        if (productCategories.getTotalElements() == 0) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage("Data Not Found.");
        }

        return genericDataDTO;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getByNameOrType(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    GenericDataDTO getAllProductCategoriesByType(String Type) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            QProductCategory qProductCategory = QProductCategory.productCategory;
            BooleanExpression booleanExpression = qProductCategory.isNotNull().and(qProductCategory.isDeleted.eq(false)).and(qProductCategory.productCategory.type.contains(Type));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProductCategory.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            genericDataDTO.setDataList(IterableUtils.toList(this.productCategoryRepository.findAll(booleanExpression)));
//            logger.info("Fetching AllProductCategoriesByType  :  request:; Response : {{}}", genericDataDTO.getResponseCode());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Fetch AllProductCategoriesByType :  Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        return genericDataDTO;

    }



    public List<ProductCategory> getallproduct(){
        List<ProductCategory> list = new ArrayList<>();
        list = productCategoryRepository.getall();
        return list;
    }


    public GenericDataDTO getAllActiveProductCategories() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            QProductCategory qProductCategory = QProductCategory.productCategory;
            BooleanExpression booleanExpression = qProductCategory.isNotNull().and(qProductCategory.isDeleted.eq(false)).and(qProductCategory.status.eq(CommonConstants.ACTIVE_STATUS));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProductCategory.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            genericDataDTO.setDataList(IterableUtils.toList(this.productCategoryRepository.findAll(booleanExpression)));
//            logger.info("Fetching AllProductCategoriesByType  :  request:; Response : {{}}", genericDataDTO.getResponseCode());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Fetch AllProductCategoriesByType :  Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        return genericDataDTO;

    }

    public GenericDataDTO getAllActiveProductCategoriesByCB() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            QProductCategory qProductCategory = QProductCategory.productCategory;
            BooleanExpression booleanExpression = qProductCategory.isNotNull().and(qProductCategory.isDeleted.eq(false)).and(qProductCategory.productCategory.type.contains("CustomerBind, NetworkBind").or(qProductCategory.productCategory.type.contains(CommonConstants.CUSTOMER_BIND))).and(qProductCategory.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProductCategory.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            genericDataDTO.setDataList(IterableUtils.toList(this.productCategoryRepository.findAll(booleanExpression)));
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }

}
