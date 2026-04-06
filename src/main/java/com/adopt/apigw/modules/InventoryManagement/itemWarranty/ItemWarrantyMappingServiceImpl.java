package com.adopt.apigw.modules.InventoryManagement.itemWarranty;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.InventoryManagement.product.QProduct;
import com.adopt.apigw.service.postpaid.ChargeService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemWarrantyMappingServiceImpl extends ExBaseAbstractService<ItemWarrantyMappingDto, ItemWarrantyMapping, Long> {

    @Autowired
    private ItemWarrantyMappingRepository repository;
    @Autowired
    ChargeService chargeService;

    public ItemWarrantyMappingServiceImpl(ItemWarrantyMappingRepository repository, IBaseMapper<ItemWarrantyMappingDto, ItemWarrantyMapping> mapper) {
        super(repository, mapper);
    }
    private static final Logger logger = LoggerFactory.getLogger(ItemWarrantyMappingServiceImpl.class);
    @Override
    public String getModuleNameForLog() {
        return "[ProductServiceImpl]";
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        QProduct qProduct = QProduct.product;
        BooleanExpression booleanExpression = qProduct.isNotNull().and(qProduct.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<ItemWarrantyMapping> paginationList = null;
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                paginationList = itemRepository.findAll(pageRequest);
//            } else {
            paginationList = repository.findAll(booleanExpression, pageRequest);
//            }

            if (paginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    /*@Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getProductList(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Unable to Search :  Response : {{}};Error :{} ;Exception:{}",APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getStackTrace());
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
*/
    /*public GenericDataDTO getProductList(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getProductList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Page<ItemConditionsMapping> productList;
            if (getMvnoIdFromCurrentStaff() == 1)
                productList = repository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
            else
                productList = repository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != productList && 0 < productList.getSize()) {
                makeGenericResponse(genericDataDTO, productList);
            }
            if(productList.getTotalElements()==0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Data Not Found.");
            }
        } catch (Exception ex) {
            logger.error("Unable to Fetch all charge by Type :  Response : {{}};Error :{} ;Exception:{}",APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getStackTrace());
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return genericDataDTO;
    }
*/
    @Override
    public void deleteEntity(ItemWarrantyMappingDto entity) throws Exception {
        super.deleteEntity(entity);
    }

    @Override
    public ItemWarrantyMappingDto saveEntity(ItemWarrantyMappingDto entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff(null));  // TODO: pass mvnoID manually 6/5/2025
        return super.saveEntity(entity);
    }

/*
    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(name);
            else count = repository.duplicateVerifyAtSave(name, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }
*/

    @Override
    public boolean deleteVerification(Integer id)throws Exception{
        boolean flag = false;
        Integer count = repository.deleteVerify(id);
        if(count==1){
            flag=true;
        }
        return flag;
    }
    /*public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = itemRepository.duplicateVerifyAtSave(name);
            else count = itemRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = itemRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id));
                else countEdit = itemRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id), mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }
*/
}
