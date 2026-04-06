package com.adopt.apigw.modules.InventoryManagement.warehouse;

import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.acl.constants.AclConstants;

import com.adopt.apigw.modules.auditLog.service.AuditLogService;
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
import java.util.List;

@RestController
@Api(value = "WarehouseManagementController", description = "REST APIs related to warehouse Entity!!!!", tags = "warehouse-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.WAREHOUSE_MANAGEMENT)
public class WarehouseManagementController extends ExBaseAbstractController<WareHouseDto> {

    private static final String MODULE = " [WarehouseManagementController] ";
    @Autowired
    WarehouseManagementServiceImpl warehouseManagementService;

    @Autowired
    AuditLogService auditLogService;

    public WarehouseManagementController(WarehouseManagementServiceImpl warehouseManagementService,
                                         WarehouseManagementRepository warehouseManagementRepository) {
        super(warehouseManagementService);
        this.warehouseManagementRepository = warehouseManagementRepository;
    }

    @Override
    public String getModuleNameForLog() {
        return "[WarehouseManagementController]";
    }
    private static final Logger logger = LoggerFactory.getLogger(WarehouseManagementController.class);
    private final WarehouseManagementRepository warehouseManagementRepository;

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(warehouseManagementService
//                    .getAllEntities());
//            genericDataDTO.setTotalRecords(warehouseManagementService
//                    .getAllEntities().size());
//            logger.info("Fetching All Warehouse Without pagination  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Fetch all without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllActiveWarehouse")
//    public GenericDataDTO getAllActiveWarehouse() {
////        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
//        return warehouseManagementService.getAllActiveWarehouse();
//    }

    @GetMapping("/getAllWarehouseView")
    public GenericDataDTO getAllWarehouseView(@RequestParam("mvnoId") Integer mvnoId
    ) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(warehouseManagementService
                    .getAllWarehouseView(mvnoId));
            genericDataDTO.setTotalRecords(warehouseManagementService
                    .getAllWarehouseView(mvnoId).size());
            logger.info("Fetching All Warehouse Without pagination  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        }
        catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to Fetch all without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
        }
        return genericDataDTO;
    }
    
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody WareHouseDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            boolean flag = warehouseManagementService.duplicateVerifyAtSave(entityDTO.getName());
//            if (flag) {
//                // To compare parentSAIds and SAIds
//                String warehouseOperation = "SaveWarehouseOperation";
//                warehouseManagementService.comparePSAIdsAndSAIds(entityDTO, warehouseOperation);
//                genericDataDTO = super.save(entityDTO, result, authentication, req);
//                WareHouseDto caseEntity = (WareHouseDto) genericDataDTO.getData();
//                warehouseManagementService.saveParentServicearea(entityDTO);
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                logger.info("creating New warehouse with name "+entityDTO.getName()+"  :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(MessageConstants.WAREHOUSE_NAME_EXITS);
//                logger.error("Unable to create Warehouse With name "+entityDTO.getName()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            }
//        } catch (Exception e){
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + e.getMessage(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to create warehouse With name "+entityDTO.getName()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody WareHouseDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            String Oldname=warehouseManagementService.getName(entityDTO);
//            boolean flag = warehouseManagementService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue());
//            if (flag) {
//                // To compare parentSAIds and SAIds
//                String warehouseOperation = "UpdateWarehouseOperation";
//                warehouseManagementService.comparePSAIdsAndSAIds(entityDTO, warehouseOperation);
//                genericDataDTO = super.update(entityDTO, result, authentication, req);
//                WareHouseDto caseEntity = (WareHouseDto) genericDataDTO.getData();
//                warehouseManagementService.updateParentServicearea(entityDTO);
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                logger.info("Updating Warehouse With old name "+Oldname+" to new name "+entityDTO.getName()+" is successfull :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(MessageConstants.WAREHOUSE_NAME_EXITS);
//                logger.error("Unable to Update Warehouse "+Oldname+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            }
//        } catch (Exception e){
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + e.getMessage(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to Unable to update Warehouse :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllParentServiceAreaList")
//    public GenericDataDTO getAllParentServiceAreaList() throws Exception{
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(warehouseManagementService.getAllParentServiceAreas());
//        }
//        catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllParentServiceAreaListByWarehouseId"+"/{warehouseId}")
//    public GenericDataDTO getAllParentServiceAreaListByWarehouseId(@PathVariable Integer warehouseId, HttpServletRequest req) throws Exception{
//        MDC.put("type", "Fetch");
//        String SUBMODULE = getModuleNameForLog() + " [getAllParentServiceAreaListByWarehouseId()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(warehouseManagementService.getAllParentServiceAreasByWarehouseId(warehouseId));
//        }
//        catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_WAREHOUSE_MANAGEMENT_EDIT + "\")")
//    @PostMapping("/getAllByWarehouseIds")
//    public GenericDataDTO getAllByWarehouseIds(@RequestBody List<Long> warehouseIds, HttpServletRequest req) throws Exception{
//        MDC.put("type", "Fetch");
//        String SUBMODULE = getModuleNameForLog() + " [getAllByWarehouseIds()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(warehouseManagementService.getAllByWarehouseIds(warehouseIds));
//        }
//        catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }



//    @DeleteMapping("/delete/{id}")
//    public GenericDataDTO delete(@PathVariable("id")Long id) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            //check Inward Bind
//            boolean check= warehouseManagementService.deleteVerification(Math.toIntExact(id));
//            if(check){
//                throw new RuntimeException("WareHouse is already in use");
//            }
//            warehouseManagementRepository.deleteById(id)
//            ;
//
//            genericDataDTO.setResponseMessage("Deleted Successfully");
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        } catch (Exception ex) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage("WareHouse is Already Use!");
//        }
//        return genericDataDTO;
//    }
}
