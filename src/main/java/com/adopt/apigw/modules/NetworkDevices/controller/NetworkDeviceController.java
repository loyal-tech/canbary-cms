package com.adopt.apigw.modules.NetworkDevices.controller;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDeviceBind;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.modules.NetworkDevices.domain.Oltslots;
import com.adopt.apigw.modules.NetworkDevices.model.*;
import com.adopt.apigw.modules.NetworkDevices.repository.NetworkDeviceRepository;
import com.adopt.apigw.modules.NetworkDevices.service.NetworkDeviceService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.utils.UtilsCommon;
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
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.NETWORK_DEVICE)
public class NetworkDeviceController extends ExBaseAbstractController<NetworkDeviceDTO> {
    private static String MODULE = " [NetworkDeviceController] ";
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private NetworkDeviceService networkDeviceService;
    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;


    public NetworkDeviceController(NetworkDeviceService service) {
        super(service);
    }

    private static final Logger logger = LoggerFactory.getLogger(NetworkDeviceController.class);
    //@Deprecated
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,@RequestParam Integer mvnoId) {
        return super.search(page, pageSize, sortOrder, sortBy, filter , req,mvnoId);
    }

    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(networkDeviceService
                    .getAllEntities(mvnoId));
            genericDataDTO.setTotalRecords(networkDeviceService
                    .getAllEntities(mvnoId).size());
            logger.info("Fetching All network devices Without pagination :  Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
          //  ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to fetch network devices Without Pagination  :  Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
//        return super.getAllWithoutPagination();
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody NetworkDeviceDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Delete");
        boolean flag = networkDeviceService.deleteVerification(entityDTO.getId().intValue());
        if (flag) {
            entityDTO.setServicearea(null);
            genericDataDTO = super.delete(entityDTO, authentication, req);
            NetworkDeviceDTO networkDevices = (NetworkDeviceDTO) genericDataDTO.getData();
            if(networkDevices != null)
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICES,
                        AclConstants.OPERATION_NETWORK_DEVICES_DELETE, req.getRemoteAddr(), null, networkDevices.getId(), networkDevices.getName());
            logger.info("Deleting Network devices With name "+entityDTO.getName()+" is successfull :   Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(DeleteContant.NETWORK_DEVICE_DELETE_EXIST);
            logger.error("Unable to Delete network device with name "+entityDTO.getName()+"  : Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody NetworkDeviceDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Update");
        String name=  networkDeviceService.getEntityToUpdate(entityDTO.getId()).getName();
        try {
            boolean flag = networkDeviceService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue());
            if (flag) {
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
                }
                NetworkDeviceDTO networkDevices = networkDeviceService.updateEntity(entityDTO);

                String updatedValues = UtilsCommon.getUpdatedDiff(networkDevices,entityDTO);
                genericDataDTO.setData(networkDevices);
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICES,
                        AclConstants.OPERATION_NETWORK_DEVICES_EDIT, req.getRemoteAddr(), null, networkDevices.getId(), networkDevices.getName());
                logger.info("Updating network with "+updatedValues+" is successfull :   Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.NETWORK_DEVICE_NAME_EXITS);
                logger.error("Unable To update Network devices With name "+name+"  :  Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
            return genericDataDTO;
        } catch (Exception ex){
           //
            // ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable To update Network devices With name "+name+"  :  Response : {{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_ADD + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody NetworkDeviceDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        try{
            boolean flag = networkDeviceService.duplicateVerifyAtSave(entityDTO.getName());
            if (flag) {
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
                }
                NetworkDeviceDTO networkDevices = networkDeviceService.saveEntity(entityDTO);
                genericDataDTO.setData(networkDevices);
                logger.info("crating Network devices With name "+entityDTO.getName()+" :  request: { From : {}}; Response : {{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICES,
                        AclConstants.OPERATION_NETWORK_DEVICES_ADD, req.getRemoteAddr(), null, networkDevices.getId(), networkDevices.getName());
                logger.error("Unable crete network devices With name "+entityDTO.getName()+"   :   Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.NETWORK_DEVICE_NAME_EXITS);
                logger.error("Unable crete network devices With name "+entityDTO.getName()+"  :   Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
        } catch (Exception ex){
            //ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable crete network devices With name "+entityDTO.getName()+"  :   Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = super.getEntityById(id, req,mvnoId);
        MDC.put("type", "Fetch");
        NetworkDeviceDTO networkDevices = (NetworkDeviceDTO) genericDataDTO.getData();
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICES,
                AclConstants.OPERATION_NETWORK_DEVICES_VIEW, req.getRemoteAddr(), null, networkDevices.getId(), networkDevices.getName());
        MDC.remove("type");
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[NetworkDeviceController]";
    }

    @GetMapping("/byServiceId/{serviceId}")
    public GenericDataDTO getEntityByServiceId(@PathVariable Long serviceId) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("Fetching network by service Id "+serviceId+" :   Response : {{}} responce-responce: {}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        try {
            return GenericDataDTO.getGenericDataDTO(networkDeviceService.getNetworkDevicesByServiceAreaId(serviceId));
        } catch (Exception e) {
        //    ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            logger.error("Fetching network by service Id "+serviceId+"  :  Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_EDIT + "\")")
    @PostMapping("/updateNetwork")
    public GenericDataDTO updateNetworkDevice(@RequestBody NetworkDeviceDTO requestDTO, HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [updateNetworkDevice()] ";
        MDC.put("type", "Update");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            boolean flag = networkDeviceService.duplicateVerifyAtEdit(requestDTO.getName(), requestDTO.getId().intValue());
            if (flag) {
                if (null == requestDTO.getId()) {
                    genericDataDTO.setResponseMessage("Please provide Network id!");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    logger.error("Unable to Update Networking Device   : Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                    return genericDataDTO;
                }

                NetworkDevices networkDevices = networkDeviceService.getEntityToUpdate(requestDTO.getId());
                String updatedValues = UtilsCommon.getUpdatedDiff(networkDevices,requestDTO);
                if (null == networkDevices) {
                    genericDataDTO.setResponseMessage("Network Id is Not Available or not permitted to update");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    logger.error("Unable to Update Networking Device "+updatedValues+"  :  Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                    return genericDataDTO;
                }
                List<Oltslots> oltSlots = networkDevices.getOltslotsList();
                networkDeviceService.UpdateNetworkDevice(requestDTO, oltSlots);
                NetworkDeviceDTO dtoResponse = networkDeviceService.getEntityById(requestDTO.getId(),requestDTO.getMvnoId());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setData(dtoResponse);
                logger.info("Updating networking device With oldname "+updatedValues+" :  Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICES,
                        AclConstants.OPERATION_NETWORK_DEVICES_EDIT, req.getRemoteAddr(), null, networkDevices.getId(), networkDevices.getName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.NETWORK_DEVICE_NAME_EXITS);
                logger.error("Unable to Update Networking Device "+requestDTO.getName()+"   :   Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
        } catch (Exception ex) {
          //  ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to Update Networking Device "+requestDTO.getName()+"  : Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @Override
    @PostMapping("/list")
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId) {
        return super.getAll(requestDTO, req,mvnoId);
        //        return networkDeviceService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());

    }

    @GetMapping("/hierarchy")
    public GenericDataDTO getHierarchy(@RequestParam Long id) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("Fetching Hirarchy with id "+id+"  Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        try {
            genericDataDTO.setData(networkDeviceService.getHierarchy(id));
            return genericDataDTO;
        } catch (Exception e) {
           // ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            logger.error("Unable to fetch Hirarchy by id "+id+"  : Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_ADD + "\")")
    @PostMapping("/parentDeviceMapping")
    public GenericDataDTO parentDeviceMapping(@Valid @RequestBody DeviceMappingDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Cerate");
        try{
            List<NetworkDeviceBindingsDTO> networkDeviceBindings = networkDeviceService.saveParentDeviceBindings(entityDTO);
            genericDataDTO.setDataList(networkDeviceBindings);
            logger.info("Creating parent device mapping to device id "+entityDTO.getDeviceId()+" : Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());

        } catch (Exception ex){
           // ApplicationLogger.logger.error(getModuleNameForLog() + " [parentDeviceMapping] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable create parent device Mapping To device "+entityDTO.getDeviceId()+" : Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
        } MDC.remove("type");

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_ADD + "\")")
    @PostMapping("/deviceChildParentBinding")
    public GenericDataDTO deviceChildParentBinding(@Valid @RequestBody DevicePortMappingDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            List<NetworkDeviceBindingsDTO> networkDeviceBindings = networkDeviceService.deviceChildParentBinding(entityDTO);
            genericDataDTO.setDataList(networkDeviceBindings);
        } catch (Exception ex){
    //        ApplicationLogger.logger.error(getModuleNameForLog() + " [parentDeviceMapping] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/boundParents")
    public GenericDataDTO boundParents(@RequestParam Long id) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(networkDeviceService.boundParents(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Fetching All bound parents  by id "+id+" :  Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception e) {
        //    ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            logger.error("Unable to fetch bounded parents :   Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/availableParents")
    public GenericDataDTO availableParents(@RequestParam Long id,@RequestParam("mvnoId") Integer mvnoId
    ) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(networkDeviceService.availableParents(id,mvnoId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Fetching All availableParents by id "+id+" :  Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception e) {
         //   ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            logger.error("Unable to fetch availableParents by id "+id+"  :  Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_DELETE + "\")")
    @DeleteMapping("/deleteDeviceMapping")
    public GenericDataDTO deleteDeviceMapping(@RequestParam Long id, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Delete");
        try {
            genericDataDTO.setResponseMessage(networkDeviceService.deleteDeviceMapping(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            logger.info("Deleting bevice apping with id "+id+" :  Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(DeleteContant.NETWORK_DEVICE_DELETE_EXIST);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            logger.error("Unable delete device mapping with id "+id+"  :  Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/checkPortAvailability")
    public GenericDataDTO checkPortAvailability(@RequestParam("parentDeviceId") Long parentDeviceId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try {
            genericDataDTO.setDataList(Arrays.asList(networkDeviceService.getPortsAvailability(parentDeviceId).toArray()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Fetching All available Ports  for "+parentDeviceId+":   Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception e) {
   //         ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            logger.error("Unable to fetch All available Ports  for "+parentDeviceId+"  :   Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        return genericDataDTO;
    }
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/getAllInwardByProduct")
    public GenericDataDTO getAllInwardByProduct(@RequestParam(name = "productId") Long productId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(networkDeviceService.getAllInwardByProduct(productId));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, inwardDto.getId(), inwardDto.getInwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/getAllProduct")
    public GenericDataDTO getAllProduct() {
//        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
        return networkDeviceService.getAllProduct();
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @PostMapping("/searchNetworkDevices")
    public GenericDataDTO searchNetworkDevices(@RequestBody PaginationRequestDTO requestDTO, @ModelAttribute SearchNetworkDevicesPojo entity) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (entity != null) {
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                requestDTO = setDefaultPaginationValues(requestDTO);
                genericDataDTO = networkDeviceService.searchNetworkDevices(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), entity);
            }
            if (genericDataDTO.getDataList().isEmpty()) {
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No Data Found", null);
            }
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        } catch (Exception ex) {
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/getNetworkDevicesByDeviceType")
    public GenericDataDTO getNetworkDevicesByDeviceType(@RequestParam(name = "deviceType") String deviceType) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                requestDTO = setDefaultPaginationValues(requestDTO);
                genericDataDTO = networkDeviceService.findNetworkDevicesByType(deviceType);
            if (genericDataDTO.getDataList().isEmpty()) {
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No Data Found", null);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_ADD + "\")")
    @PostMapping("saveMappingData")
    public GenericDataDTO save(@RequestBody NetworkDeviceBindDTO networkDeviceBindDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            NetworkDeviceBind networkDeviceBindDTO1 = networkDeviceService.saveNetworks(networkDeviceBindDTO);
            genericDataDTO.setData(networkDeviceBindDTO1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Mapped Successfully");


        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NETWORK_DEVICES_ALL + "\",\"" + AclConstants.OPERATION_NETWORK_DEVICES_VIEW + "\")")
    @GetMapping("/getAllMappingDataByDeviceId")
    public GenericDataDTO MappingData(@RequestParam Long id) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(networkDeviceService.getAllMappingData(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Fetching All bound parents  by id "+id+" :  Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception e) {
            //    ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            logger.error("Unable to fetch bounded parents :   Response : {{}{};Exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
}
