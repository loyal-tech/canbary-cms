package com.adopt.apigw.modules.PurchaseOrder.Controller;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.PurchaseOrder.DTO.PurchaseOrderDTO;
import com.adopt.apigw.modules.PurchaseOrder.Domain.PurchaseOrder;
import com.adopt.apigw.modules.PurchaseOrder.Mapper.PurchaseOrderMapper;
import com.adopt.apigw.modules.PurchaseOrder.Repository.PurchaseOrderRepository;
import com.adopt.apigw.modules.PurchaseOrder.Service.PurchaseOrderService;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.FileSystemService;
import com.adopt.apigw.utils.APIConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PURCHASE_ORDER)
public class PurchaseOrderController extends ExBaseAbstractController<PurchaseOrderDTO> {
    public PurchaseOrderController(PurchaseOrderService service) {
        super(service);

    }

    @Override
    public String getModuleNameForLog() {
        return "[PurchaseOrderController]";
    }
    private static final Logger logger= LoggerFactory.getLogger(PurchaseOrderController.class);

    @Autowired
    PurchaseOrderService purchaseOrderService;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    CustomersService customersService;

    @Autowired
    PurchaseOrderMapper mapper;
    @Autowired
    private CustomersRepository customersRepository;

    @PostMapping("/savePo")
    public GenericDataDTO save(@Valid @RequestParam String spojo, @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
       PurchaseOrderDTO dto = new PurchaseOrderDTO();
       dto = new ObjectMapper().registerModule(new JavaTimeModule()).
               readValue(spojo, new TypeReference<PurchaseOrderDTO>() {
               });

        // TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) != null) {
            // TODO: pass mvnoID manually 6/5/2025
            dto.setMvnoId(getMvnoIdFromCurrentStaff(null));
        }
        MDC.put("type", "Create");
        boolean flag = purchaseOrderService.duplicateVerifyAtSave(dto.getPonumber());
        GenericDataDTO dataDTO = new GenericDataDTO();
        if (file != null) {
            purchaseOrderService.uploadDocument(dto, file);
        }
        if (flag /*&& flagforUcode*/) {
          dataDTO = purchaseOrderService.save(dto);
            PurchaseOrderDTO purchaseOrderDTO= (PurchaseOrderDTO) dataDTO.getData();
        //    logger.info("PO created Successfully With Po nnumber "+ dto.getPonumber()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
        } else{
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.PO_NUMBER_EXITS);
         //   logger.error("Unable to Create PO number " +dto.getPonumber()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE);
        }
        MDC.remove("type");
        return dataDTO;
    }

//    @Override
//    public GenericDataDTO update(@Valid @RequestBody PurchaseOrderDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        String oldname=purchaseOrderService.getById(entityDTO.getId()).getIcname();
//        MDC.put("type", "Update");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = purchaseOrderService.duplicateVerifyAtEdit(entityDTO.getIcname(), entityDTO.getId());
//        if (flag) {
//            dataDTO = super.update(entityDTO, result, authentication, req);
//            PurchaseOrderDTO purchaseOrderDTO = (PurchaseOrderDTO) dataDTO.getData();
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.PO_NUMBER_EXITS);
//        }
//        return dataDTO;
//    }

    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId) {
        return super.getAll(requestDTO, req,mvnoId);
    }

    public GenericDataDTO search (@RequestParam(required = false, defaultValue = "${request.defaultPage}") List<GenericSearchModel>
                                          page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String
                                          sortBy, @RequestBody Integer filter,@RequestParam Integer mvnoId){
        return purchaseOrderService.search(page, pageSize, sortOrder, sortBy, filter,mvnoId);
    }

    @Override
    public GenericDataDTO delete(@RequestBody PurchaseOrderDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        org.slf4j.MDC.put("type", "Delete");
        GenericDataDTO dataDTO = new GenericDataDTO();
        boolean flag = purchaseOrderService.deleteVerification(entityDTO.getId().intValue());
        if (flag) {
            dataDTO = super.delete(entityDTO, authentication, req);
            PurchaseOrderDTO purchaseOrderDTO = (PurchaseOrderDTO) dataDTO.getData();
            if (purchaseOrderDTO != null) {
                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
                //  AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
                logger.info("PO  With name " + entityDTO.getPonumber() + " is deleted Successfully  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
            }
        } else {
            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            dataDTO.setResponseMessage(DeleteContant.PO_NUMBER_DELETE_EXIST);
            logger.error("Unable to Delete PO With name: " + entityDTO.getPonumber() + "  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), HttpStatus.NOT_ACCEPTABLE.value());
        }

        org.slf4j.MDC.remove("type");
        return dataDTO;
    }

    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO dataDTO = super.getEntityById(id, req,mvnoId);
        PurchaseOrderDTO purchaseOrderDTO = (PurchaseOrderDTO) dataDTO.getData();
        MDC.remove("type");
        return dataDTO;
    }

    @RequestMapping(value = "/document/download/{ponumber}/{custId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable String ponumber, @PathVariable Integer custId) {
        MDC.put("type", "Fetch");

        Resource resource = null;
        try {
            Customers customers = customersRepository.findById(custId).get();
            if (null == customers) {
                logger.error("Unable to Download recipt for customer" + customers.getUsername() + " for document id" + ponumber + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL);
                return ResponseEntity.notFound().build();
            }
            PurchaseOrder docDetails = purchaseOrderRepository.findByponumber(ponumber);
            PurchaseOrderDTO docDetailsDTO = mapper.domainToDTO(docDetails,new CycleAvoidingMappingContext());
            if (null == docDetailsDTO) {
                logger.error("Unable to Download doc]umrnt  for customer " + customers.getUsername() + "for document id" + ponumber + " :  request: { From : {}}; Response : {{} code:{};Exception:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL);
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getpurchaseOrderDoc(docDetailsDTO.getPonumber().trim(), docDetailsDTO.getUniquename());
            // resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Unable to Download recipt for customer " + customers.getUsername() + " for payment id" + ponumber + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);

            } else {
                logger.error("Unable to Download recipt for customer " + customers.getUsername() + "for document id" + ponumber + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Unable to Download document for customer " + customersRepository.findById(custId).get().getUsername() + " for doccument id" + ponumber + " :  request: { From : {}}; Response : {{} code:{};Exception:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL, ex.getStackTrace());
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        MDC.remove("type");

        return null;
    }

}
