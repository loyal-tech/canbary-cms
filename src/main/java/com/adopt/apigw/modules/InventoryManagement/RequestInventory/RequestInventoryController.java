package com.adopt.apigw.modules.InventoryManagement.RequestInventory;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.REQUEST_INVENTORY)
@Api(value = "RequestInventoryController", description = "REST APIs related to RequestInventory Entity!!!!", tags = "RequestInventory_Controller")

public class RequestInventoryController extends ExBaseAbstractController<RequestInventoryDto> {

    @Autowired
    RequestInventoryServiceImpl requestInventoryService;


    //PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REQUEST_INVENTORY_ADD)
//    @Override
//    @PostMapping("/save")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ADD + "\")")
//    public GenericDataDTO save(@Valid @RequestBody RequestInventoryDto requestInventoryDto, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Successful");
//        if (getMvnoIdFromCurrentStaff() != null) {
//            requestInventoryDto.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        try {
//            requestInventoryService.validateRequest(requestInventoryDto);
//            RequestInventoryDto inventoryDto = requestInventoryService.saveEntity(requestInventoryDto);
//            genericDataDTO.setData(inventoryDto);
//
//        } catch (Exception ex) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/getById")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_VIEW + "\")")
//    public GenericDataDTO findById(@RequestParam("id") Long id) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        try {
//            RequestInventoryDto requestInventoryDto = requestInventoryService.findById(id);
//            genericDataDTO.setResponseMessage("Successfully");
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(requestInventoryDto);
//
//        } catch (Exception ex) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }


    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REQUEST_INVENTORY_ADD)
//    @GetMapping("/approveStatus")
//    public GenericDataDTO approvreStatus(@RequestParam("status") String status, @RequestParam("id") Long id, @RequestParam("remarks") String remarks) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        try {
//            if(status.equalsIgnoreCase("Approve")) {
//                requestInventoryService.validateApproveRequest(id);
//            }
//            RequestInventoryDto requestInventoryDto = requestInventoryService.approveStatus(status, id, remarks);
//            genericDataDTO.setResponseMessage("Successfully");
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(requestInventoryDto);
//
//        } catch (Exception ex) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }


    //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REQUEST_INVENTORY_ADD)
//    @GetMapping("/onbehalfoff")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_VIEW + "\")")
//    public GenericDataDTO getAll(@RequestParam("onBehalfOf") String onBehalfOf) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO = requestInventoryService.getAll(onBehalfOf);
//
//        } catch (Exception ex) {
//            throw new RuntimeException(ex.getMessage());
//
//        }
//        return genericDataDTO;
//    }

//    @PostMapping("/getAllAssignedRequestInventory")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_VIEW + "\")")
//    public GenericDataDTO getAllAssignedRequestInventory(@RequestBody PaginationRequestDTO requestDTO) {
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            genericDataDTO = requestInventoryService.getAllAssignedRequestInventory(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
//                    requestDTO.getSortBy(), requestDTO.getSortOrder());
//            return genericDataDTO;
//
//        } catch (Exception ex) {
//            throw new RuntimeException(ex.getMessage());
//        }
//    }


//    @PostMapping("/getAllByCurrentStaff")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_VIEW + "\")")
//    public GenericDataDTO getAllRequestByCurrentStaff(@RequestBody PaginationRequestDTO requestDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO = requestInventoryService.getAllRequestByCurrentStaff(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
//                    requestDTO.getSortBy(), requestDTO.getSortOrder());
//
//        } catch (Exception ex) {
//            throw new RuntimeException(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/getAllWareHouses")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_VIEW + "\")")
//    public GenericDataDTO getAllWareHouse() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setDataList(requestInventoryService.getAllWareHouse());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Successful");
//        } catch (CustomValidationException ex) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }


    //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REQUEST_INVENTORY_DELETE)
//    @DeleteMapping("/delete")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_DELETE + "\")")
//    public GenericDataDTO delete(@RequestParam("id") Long id, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        {
//            dataDTO.setData(requestInventoryService.deleteInventory(id));
//            dataDTO.setResponseMessage("Deleted SuccessFully");
//            dataDTO.setResponseCode(HttpStatus.OK.value());
//
//        }
//        return dataDTO;
//
//    }

    public RequestInventoryController(RequestInventoryServiceImpl service) {
        super(service);
    }


    @Override
    public String getModuleNameForLog() {
        return null;
    }


//    @PostMapping("/forwardReqInv")
//    public GenericDataDTO forwardRequestToWareHouse(@RequestParam("reqId") Long reqId, @RequestParam("forwardToReqId") Long forwardToReqId, @RequestParam("remarks") String remarks){
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        try {
////            requestInventoryService.forwardRequestToWareHouse(reqId,forwardToReqId,remarks);
//            genericDataDTO.setResponseMessage("Successfully");
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(requestInventoryService.forwardRequestToWareHouse(reqId,forwardToReqId,remarks));
//
//        } catch (Exception ex) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }


}
