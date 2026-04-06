package com.adopt.apigw.modules.InventoryManagement.item;


import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.CreditDocController;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.repository.ExternalItemManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.adopt.apigw.modules.InventoryManagement.ItemStatusMapping.ItemStatusMapping;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardRepository;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionsMapping;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.FileSystemService;
import com.adopt.apigw.utils.APIConstants;
//import gnu.trove.impl.sync.TSynchronizedShortByteMap;
import com.adopt.apigw.spring.SpringContext;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(value = "ItemController", description = "REST APIs related to item Entity!!!!", tags = "item-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ITEM_MANAGEMENT)
public class ItemController extends ExBaseAbstractController<ItemDto> {

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    InOutWardMACService inOutWardMACService;
    private static String MODULE = " [CreditDocController] ";

    private static final Logger logger = LoggerFactory.getLogger(CreditDocController.class);

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemConditionMappingRepository itemConditionMappingRepository;

    @Autowired
    InwardRepository repository;


    public ItemController(ItemServiceImpl itemService) {
        super(itemService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ItemController]";
    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody ItemDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        if (getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        try {
//            boolean flag = itemService.duplicateVerifyAtSave(entityDTO.getName());
//            if (flag) {
//                if (getMvnoIdFromCurrentStaff() != null) {
//                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//                }
//                ItemDto entity = itemService.saveEntity(entityDTO);
//                genericDataDTO.setData(entity);
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(MessageConstants.ITEM_NAME_EXITS);
//            }
//        } catch (Exception ex) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page, @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize, @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder, @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody ItemDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        if (getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        boolean flag = itemService.deleteVerification(entityDTO.getId().intValue());
//        if (flag) {
//            dataDTO = super.delete(entityDTO, authentication, req);
//
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.ITEM_NAME_EXITS);
//        }
//        return dataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody ItemDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        boolean flag = itemService.duplicateVerifyAtEdit(entityDTO.getName(), (entityDTO.getId()));
//        if (flag) {
//            if(getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            dataDTO = super.update(entityDTO, result, authentication, req);
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.ITEM_NAME_EXITS);
//        }
//        return dataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_VIEW + "\")")
//    @PostMapping(value = "/getAllItemsByOwner")
//    public GenericDataDTO getAllItemsByOwner(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "ownerId") Long ownerId, @RequestParam(name = "ownerType") String ownerType) {
//        String SUBMODULE = getModuleNameForLog() + " [getAllAssignInventories()] ";
//
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            genericDataDTO = itemService.getAllItemsByOwner(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), ownerId,ownerType);
//            if (null != genericDataDTO) {
////                logger.info("fetching allAssigned inventories:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
////                logger.error("Unable to fetch all inventories :  request: { From : {},}; Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            }
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
////            logger.error("Unable to  to fetch all inventories :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

// Before hitting this api, check  all ids selected has same inward, if not give error from gui
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
//    @PostMapping(value = "/return")
//    public GenericDataDTO returnItems(@Valid @RequestBody List<ItemReturnDTO> itemsToReturn, Authentication authentication) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        List<Long> itemIds = new ArrayList<>();
//        for(ItemReturnDTO itemReturnDTO : itemsToReturn){
//            itemIds.add(itemReturnDTO.getId());
//        }
//        boolean flag = itemService.itemReturnCheck(itemIds);
//        if (flag) {
//            dataDTO.setResponseCode(HttpStatus.OK.value());
//            dataDTO.setResponseMessage(itemService.returnItem(itemsToReturn));
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.CANNOT_RETURN_ITEM);
//        }
//        return dataDTO;
//    }

//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
//    @PostMapping(value = "/updateItemTypeByList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public GenericDataDTO updateItemTypeByList(@Valid @RequestParam String entityDTOs, @RequestParam(required = false, value = "file") List<MultipartFile> file, Authentication authentication) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        try {
//
//            if (entityDTOs != null) {
//                List<ItemChangeTypeDto> itemChangeTypeDtoList = new ArrayList<>(6);
//                // List<StringBuffer> sb = new ArrayList<>();
//                ItemChangeTypeDto itemChangeTypeDto = null;
//                Gson gson = new Gson();
//                ArrayList<LinkedTreeMap> list = gson.fromJson(entityDTOs, ArrayList.class);
//                itemChangeTypeDtoList = list.stream().map(s -> gson.fromJson(gson.toJson(s), ItemChangeTypeDto.class)).collect(Collectors.toList());
//
//
////                    List<String> resultList = Splitter.on("//")
////                            .trimResults()
////                            .omitEmptyStrings()
////                            .splitToList(entityDTOs);
////                    //sb.get(i).append(entityDTOs.split("}"));
//                //System.out.println(sb);
////                for(int i =0 ;i<file.size();i++) {
////                                     itemChangeTypeDto = new ObjectMapper().registerModule(new JavaTimeModule())
////                            .readValue(resultList.get(i), new TypeReference<ItemChangeTypeDto>() {
////                            });
////                    itemChangeTypeDtoList.add(itemChangeTypeDto);
////
////                }
//
//
//                dataDTO = itemService.updateItemTypeByList(itemChangeTypeDtoList, file);
//            }
//            dataDTO.setResponseCode(HttpStatus.OK.value());
//            dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//
//        } catch (Exception e) {
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return dataDTO;
//
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
//    @PostMapping(value = "/updateItemWarrantyByList")
//    public GenericDataDTO updateItemWarrantyByList(@Valid @RequestBody List<ItemWarrantyTypeDTO> itemWarrantyTypeDTOS, Authentication authentication) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        try {
//            dataDTO = itemService.updateItemWarrantyByList(itemWarrantyTypeDTOS);
//
//            dataDTO.setResponseCode(HttpStatus.OK.value());
//            dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//
//        } catch (Exception e) {
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return dataDTO;
//
//    }

    //API for Item-Type By Single Id .
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
//    @GetMapping(value = "/updateItemType")
//    public GenericDataDTO updateItemType(@Valid @RequestParam Long itemId, @RequestParam String itemCondition, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        try {
//            if (itemId != null) {
//                dataDTO = itemService.updateItemType(itemId, itemCondition);
//
//                dataDTO.setResponseCode(HttpStatus.OK.value());
//                dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            }
//        } catch (Exception e) {
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return dataDTO;
//
//    }

    // API for Item-Warranty By Single Id .
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
//    @GetMapping(value = "/updateItemWarranty")
//    public GenericDataDTO updateItemWarranty(@Valid @RequestParam Long itemId, @RequestParam String itemWarranty, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        try {
//            if (itemId != null) {
//                dataDTO = itemService.updateItemWarranty(itemId, itemWarranty);
//
//                dataDTO.setResponseCode(HttpStatus.OK.value());
//                dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            }
//        } catch (Exception e) {
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return dataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
//    @PostMapping(value = "/updateItemStatusByList")
//    public GenericDataDTO updateItemStatusByList(@Valid @RequestBody List<ItemStatusDTO> itemStatusLists, Authentication authentication) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        try {
//            dataDTO = itemService.updateItemStatusByList(itemStatusLists);
//
//            dataDTO.setResponseCode(HttpStatus.OK.value());
//            dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//
//        } catch (Exception e) {
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return dataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
//    @PostMapping(value = "/updateItemOwnerShipStatusByList")
//    public GenericDataDTO updateItemOwnerShipStatusByList(@Valid @RequestBody List<ItemOwnerShipDTO> itemOwnerShipDTOS, Authentication authentication) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        try {
//            dataDTO = itemService.updateItemOwnerShipStatusByList(itemOwnerShipDTOS);
//
//            dataDTO.setResponseCode(HttpStatus.OK.value());
//            dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//
//        } catch (Exception e) {
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return dataDTO;
//
//    }

    @RequestMapping(value = "/documentForItemComplain/download/{conditionId}/{itemId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long conditionId, @PathVariable Integer itemId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            Item item = itemRepository.getOne(itemId.longValue());
            if (null == item) {
                return ResponseEntity.notFound().build();
            }
            Optional<ItemConditionsMapping> itemConditionsMapping = itemConditionMappingRepository.findById(conditionId);
            if (null == itemConditionsMapping) {
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getItemDoc(item.getName().trim(), itemConditionsMapping.get().getUniquename());
            //resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Downloading document with  " + conditionId + " downloaded Successfully  :  request: { From : {} }; Response : {{}}", SUBMODULE, APIConstants.SUCCESS);
                System.out.println("dowload document");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                logger.error("Unable to downloadDocument " + conditionId + " :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Unable to downloadDocument " + conditionId + "   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound(), ex.getStackTrace());
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        MDC.remove("type");
        return null;
    }


//    @PostMapping("/searchItems")
//    public GenericDataDTO searchItems(@RequestBody PaginationRequestDTO requestDTO, @ModelAttribute SearchItemsPojo entity) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (entity != null) {
//                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                requestDTO = setDefaultPaginationValues(requestDTO);
//                ItemServiceImpl itemService1 = SpringContext.getBean(ItemServiceImpl.class);
//                genericDataDTO = itemService1.searchItems(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), entity);
//            }
//            if (genericDataDTO.getDataList().isEmpty()) {
//                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No Data Found", null);
//            }
//        } catch (CustomValidationException ce) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//        } catch (Exception ex) {
//            genericDataDTO.setTotalRecords(0);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return genericDataDTO;
//    }


//    @PostMapping("/searchByProductAndCustomer")
//     public GenericDataDTO searchByNameCategory(@RequestBody PaginationRequestDTO requestDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            genericDataDTO = itemService.searchItembasedOnProductAndCustomer(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
//                    requestDTO.getSortBy(), requestDTO.getSortOrder());
//        } catch (Exception ex) {
//            throw ex;
//        }
//        return genericDataDTO;
//    }

//
//    @RequestMapping(value = "/getAllSuibiuseItem/currentInwardId", method = RequestMethod.GET)
//    public List<ItemDto> getAllSuibsuOwnedItem(@RequestParam("currentInwardId") Long currentInwardId) {
//        List<ItemDto> itemList = null;
//        try {
//            itemList = itemService.findItemsSuibiseOwned(currentInwardId);
//        } catch (Exception ex) {
//            throw new RuntimeException(ex.getMessage());
//        }
//        return itemList;
//    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
//    @PostMapping("/updateItemMacAndSerial")
//    public GenericDataDTO updateItemMacAndSerial(@Valid @RequestParam("itemId") Long itemId, @RequestParam("macAddress") String macAddress, @RequestParam("serialNumber") String serialNumber, HttpServletRequest req) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            itemService.validateMac(macAddress, serialNumber);
//            if (!Objects.equals(macAddress, "null")) {
//                    boolean flag = inOutWardMACService.duplicateVerifyAtSave(macAddress);
//                    if (flag) {
//                        genericDataDTO.setData(itemService.updateItemMacAndSerial(itemId, macAddress, serialNumber));
//                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                        genericDataDTO.setResponseMessage("Success");
//                    }
//                    else {
//                        logger.error("Unable to save mac address with  " + macAddress + " ," + "," + " request: { From : {}}; Response : {{}};", req.getHeader("requestFrom"), req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE);
////                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Mac Address Already Exists, It Should Be Unique", null);
//                        genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//                        genericDataDTO.setResponseMessage("Mac Address Already Exists, It Should Be Unique");
//                    }
//                } else {
//                    genericDataDTO.setData(itemService.updateItemSerial(itemId, serialNumber));
//                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                    genericDataDTO.setResponseMessage("Success");
//                }
//        } catch (CustomValidationException ex) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        catch (Exception e) {
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/getAllCustomerInvetoryDetailshistory")
//    public GenericDataDTO getAllCustomerInvetoryDetailshistory(@RequestParam("custId") Long custId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        //List<ItemStatusMapping> itemList = null;
//        try {
//            genericDataDTO.setDataList(itemService.getAllCustomerInvetoryHistory(custId));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            //itemList = itemService.getAllCustomerInvetoryHistory(custId);
//        } catch (Exception ex) {
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return genericDataDTO;
//    }
//    @GetMapping("/getItemDetails")
//    public GenericDataDTO getItemDetails(@RequestParam("itemId") Long itemId, @RequestParam("custinventoryid") Long custinventoryid) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setData(itemService.getItemDetails(itemId,custinventoryid));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Successful");
//        } catch (CustomValidationException ex) {
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }
}
