package com.adopt.apigw.modules.InventoryManagement.VendorManagment;

import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.VENDOR)
@Api(value = "VendorController", description = "REST APIs related to Vendor Entity!!!!", tags = "vendor_Controller")
public class VendorController extends ExBaseAbstractController<VendorDto> {

    private static String MODULE = " [ManufacturerController] ";
    private static final Logger logger = LoggerFactory.getLogger(VendorController.class);

    @Autowired
    VendorRepo vendorRepo;

    @Autowired
    VendorService vendorService;

    public VendorController(VendorService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }


//    @Override
//    public GenericDataDTO save(@RequestBody VendorDto vendorDto, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            //add
//            if (vendorDto.getId() == null) {
//                if (vendorDto.getName().length() > 250) {
//                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    genericDataDTO.setResponseMessage("Input size is Exceeded");
//                } else {
//                    boolean flag = vendorService.duplicateVerifyAtSave(vendorDto.getName());
//                    if (flag) {
//                        if (getMvnoIdFromCurrentStaff() != null) {
//                            vendorDto.setMvnoId(getMvnoIdFromCurrentStaff());
//                        }
//
//                        VendorDto vendorDto1 = vendorService.saveEntity(vendorDto);
//                        genericDataDTO.setData(vendorDto1);
//                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                        genericDataDTO.setResponseMessage("Successful Created");
//                        logger.info("Manufacturer with name " + vendorDto.getName() + " is created successfully " + "," + "request: { From : {}}; Response : {{}}", req.getHeader("requestForm"), APIConstants.SUCCESS);
//                    }else {
//                        genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        genericDataDTO.setResponseMessage(MessageConstants.VENDOR_NAME);
//                        logger.error("Unable to create manufacturer with name " + vendorDto.getName() + " : request: { From : {}}; Response : {{}};Error : {} ;", req.getHeader("requestForm"),HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.VENDOR_NAME);
//                    }
//                }
//            }
//        } catch (CustomValidationException e) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to create manufacturer with name " + vendorDto.getName() + " : request: { From : {}}; Response : {{}};Error : {} ; Exception{}}", req.getHeader("requestForm"),HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
//        }
//        return genericDataDTO;
//    }


//    @DeleteMapping("/delete/{id}")
//    public GenericDataDTO delete(@PathVariable("id") Long id, HttpServletRequest request) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Vendor vendor = vendorRepo.findById(id).get();
//        try {
//            //check Inward Bind
//            boolean check = vendorService.deleteVerification(Math.toIntExact(id));
//            if (check) {
//                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Manufacturer is already in use", null);
//            }
//            vendorRepo.deleteById(id);
//            genericDataDTO.setResponseMessage("Deleted Successfully");
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            logger.info("Manufacturer with name " + vendor.getName() + " is successfully deleted " + "," + " : request: { From : {}; Response : {}}", request.getHeader("requestFrom"),APIConstants.SUCCESS);
//        } catch (CustomValidationException ex) {
//            //throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage(), null);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to delete manufacturer with name " + vendor.getName() + " : request { From : {}}; Response : {{}}; Error :{} ;Exception:{}", request.getHeader("resquestFrom"),HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
//        }
//        return genericDataDTO;
//    }


//    @GetMapping("/getById")
//    public VendorDto getVendorByID(@RequestParam(name = "id") Long id) {
//        VendorDto vendorDto;
//        try {
//            vendorDto = vendorService.getVendor(id);
//        } catch (Exception exception) {
//            throw new RuntimeException(exception.getMessage());
//        }
//        return vendorDto;
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @PostMapping("/getAllVendor")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
////            requestDTO = setDefaultPaginationValues(requestDTO);
////            genericDataDTO = vendorService.getAll(requestDTO);
//            genericDataDTO = super.getAll(requestDTO);
//        } catch (Exception ex) {
//            throw ex;
//        }
//        return genericDataDTO;
//    }
//
//    @GetMapping("/findAll")
//    public GenericDataDTO findAll() {
//        return vendorService.findAllVendor();
//    }
//    @Override
//    public GenericDataDTO update(@RequestBody VendorDto vendorDto, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            //update
//            if (vendorDto.getId() != null) {
//                if (vendorDto.getName().length() > 250) {
//                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    genericDataDTO.setResponseMessage("Input size is Exceeded");
//                } else {
//                    boolean flag = vendorService.duplicateVerifyAtEdit(vendorDto.getName(), Math.toIntExact(vendorDto.getId()));
//                    if(flag) {
//                        if (getMvnoIdFromCurrentStaff() != null) {
//                            vendorDto.setMvnoId(getMvnoIdFromCurrentStaff());
//                        }
//                        VendorDto vendorDto1 = vendorService.updateEntity(vendorDto);
//                        genericDataDTO.setData(vendorDto1);
//                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                        genericDataDTO.setResponseMessage("Successful");
//                    } else {
//                        genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        genericDataDTO.setResponseMessage(MessageConstants.VENDOR_NAME);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//        }
//        return genericDataDTO;
//    }
}
