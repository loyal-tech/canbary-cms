package com.adopt.apigw.modules.InventoryManagement.itemConditionMapping;


import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.service.common.ClientServiceSrv;
import io.swagger.annotations.Api;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Api(value = "ItemConditionsMappingController", description = "REST APIs related to Item Conditions Entity!!!!", tags = "item-conditions-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ITEM_CONDITION_MANAGEMENT)
public class ItemConditionsMappingController extends ExBaseAbstractController<ItemConditionsMappingDto> {

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    ItemConditionMappingServiceImpl service;


    public ItemConditionsMappingController(ItemConditionMappingServiceImpl service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ItemController]";
    }


//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }

    @Override
    public GenericDataDTO save(@Valid @RequestBody ItemConditionsMappingDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != null) {
            // TODO: pass mvnoID manually 6/5/2025
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
        }
        try {
            ItemConditionsMappingDto productDto = service.saveEntity(entityDTO);
            genericDataDTO.setData(productDto);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,@RequestParam Integer mvnoId) {
        return super.search(page, pageSize, sortOrder, sortBy, filter , req,mvnoId);
    }

    @Override
    public GenericDataDTO delete(@RequestBody ItemConditionsMappingDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != null) {
            // TODO: pass mvnoID manually 6/5/2025
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
        }
        boolean flag = service.deleteVerification(entityDTO.getId().intValue());
        if (flag) {
            dataDTO = super.delete(entityDTO, authentication, req);

        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(DeleteContant.PRODUCT_NAME_EXITS);
        }
        return dataDTO;
    }

//    @Override
//    public GenericDataDTO update(@Valid @RequestBody ItemConditionsMappingDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        dataDTO = super.update(entityDTO, result, authentication, req);
//        return dataDTO;
//    }
}
