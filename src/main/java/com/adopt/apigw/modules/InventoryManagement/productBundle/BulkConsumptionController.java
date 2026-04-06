package com.adopt.apigw.modules.InventoryManagement.productBundle;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardDto;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardRepository;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
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

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BULKCONSUMPTION)
@Api(value = "BulkConsumptionController", description = "REST APIs related to BulkConsumption Entity!!!!", tags = "bulk_Consumption_Controller")
public class BulkConsumptionController extends ExBaseAbstractController<BulkConsumptionDto> {

    @Autowired
    BulkConsumptionServiceImp bulkConsumptionService;

    @Autowired
    InOutWardMACService inOutWardMACService;

    public BulkConsumptionController(BulkConsumptionServiceImp productBundleService) {
        super(productBundleService);
    }

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InwardRepository inwardRepository;


    @Override
    public String getModuleNameForLog() {
        {
            return "[BulkConsumptionController]";
        }
    }

//    @Override
//    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_ADD + "\")")
//    public GenericDataDTO save(@RequestBody BulkConsumptionDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//
//            if (entityDTO.getBulkConsumptionName().length() > 250) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Input size is Exceeded");
//            } else {
//                boolean flag = bulkConsumptionService.duplicateVerifyAtSave(entityDTO.getBulkConsumptionName());
//                bulkConsumptionService.validateBulkConsumption(entityDTO);
//                if (flag) {
//                    if (getMvnoIdFromCurrentStaff() != null) {
//                        entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//                    }
//                    BulkConsumptionDto bulkConsumptionDto = bulkConsumptionService.saveEntity(entityDTO);
//                    genericDataDTO.setData(bulkConsumptionDto);
//                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                } else {
//                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    genericDataDTO.setResponseMessage(MessageConstants.BULK_COSUMPTION);
//                }
//            }
//
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//        }
//        return genericDataDTO;
//    }


//    @Override
//  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_EDIT + "\")")
//    public GenericDataDTO update(@RequestBody BulkConsumptionDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//         boolean flag = bulkConsumptionService.duplicateVerifyAtEdit(entityDTO.getBulkConsumptionName(),entityDTO.getId());
//        if (flag) {
//            if (getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            //dataDTO = super.update(entityDTO, result, authentication, req);
//            BulkConsumptionDto bulkConsumptionDto = bulkConsumptionService.updateEntity(entityDTO);
//            dataDTO.setData(bulkConsumptionDto);
//            dataDTO.setResponseCode(HttpStatus.OK.value());
//            dataDTO.setResponseMessage("Updated Successfully");
//         } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.PRODUCT_NAME_EXITS);
//        }
//        return dataDTO;
//    }



//    @Override
//   // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_DELETE + "\")")
//    public GenericDataDTO delete(@RequestBody BulkConsumptionDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        if (getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        boolean flag = bulkConsumptionService.deleteVerification(entityDTO.getId().intValue());
//        if (flag) {
//            dataDTO =super.delete(entityDTO, authentication, req);
//            bulkConsumptionService.deleteBulkConsumption(entityDTO);
//            dataDTO.setResponseMessage("Deleted SuccessFully");
//            dataDTO.setResponseCode(HttpStatus.OK.value());
//
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(DeleteContant.PRODUCT_NAME_EXITS);
//        }
//        return dataDTO;
//
//     }


//     @PostMapping("/searchByNamebybulkconsumption")
//   //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_VIEW + "\")")
//     public GenericDataDTO searchByNameCategory(@RequestBody PaginationRequestDTO requestDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            genericDataDTO = bulkConsumptionService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
//                    requestDTO.getSortBy(), requestDTO.getSortOrder());
//        } catch (Exception ex) {
//            throw ex;
//        }
//        return genericDataDTO;
//    }

//    @PostMapping("/approveStatus")
////    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_VIEW + "\")")
////    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_VIEW + "\")")
//    public GenericDataDTO  approveStatus(@Valid @RequestBody BulkConsumptionDto bulkConsumptionDto, HttpServletRequest req) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(bulkConsumptionService.saveInwardApproval(bulkConsumptionDto.getId(), bulkConsumptionDto.getApprovalStatus(), bulkConsumptionDto.getApprovalRemark()));
//
//        } catch (Exception ex) {
//            throw new RuntimeException(ex.getMessage());
//        }
//        return genericDataDTO;
//    }


//    @GetMapping("/getById")
//   // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_VIEW + "\")")
//    public GenericDataDTO  findByBulkId(@RequestParam("id") Long id) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            BulkConsumptionDto bulkConsumption=bulkConsumptionService.findByBulkId(id);
//            genericDataDTO.setData(bulkConsumption);
//            genericDataDTO.setResponseMessage("Success");
//
//        } catch (Exception ex) {
//            throw new RuntimeException(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/getBulkConsumptionMapping")
//  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BULKCONSUMPTION_ALL + "\",\"" + AclConstants.OPERATION_BULKCONSUMPTION_VIEW + "\")")
//    public GenericDataDTO getBulkConsumptionMacMapping(@RequestParam(name = "bulkconsumptionId") Long bulkconsumptionId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(inOutWardMACService.getByBulkConsumptionId(bulkconsumptionId));
//            genericDataDTO.setTotalRecords(inOutWardMACService.getByBulkConsumptionId(bulkconsumptionId).size());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

}
