package com.adopt.apigw.modules.fieldMapping;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.FIELD_MAPPING)
@Api(value = "FieldMappingController", description = "REST APIs related to FieldMapping Entity!!!!", tags = "field_mapping_controller")
public class FieldMappingController  {
    private static final Logger logger = LoggerFactory.getLogger(FieldMappingController.class);
    private static String SUBMODULE = " [FieldMappingController] ";

    public String getModuleNameForLog() {
        return "[FieldMappingController]";
    }
    @Autowired
    FieldmappingService fieldmappingService;

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getTemplates")
    public GenericDataDTO getTemplate(@RequestParam("id")Long id){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getTemplate()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching getTemplate .Data[" + id.toString() + "]");
        try{
            genericDataDTO.setDataList((List) fieldmappingService.getTemplate(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetch All Template Successfully  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        }catch (Exception exception){
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            logger.error(" Unable To Fetch All Template  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }
        return  genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getFields")
    public GenericDataDTO getFields(){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getFields()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching getFields .Data[" + SUBMODULE.toString() + "]");
        try{
            genericDataDTO.setDataList(fieldmappingService.getFields());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetching Fields  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        }catch (Exception exception){
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            logger.error("Unable To Fetch Fields :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }
        return  genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_ADD + "\")")
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO save(@Valid @RequestBody FielmappingDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [save()] ";
        logger.info(getModuleNameForLog() + "--" + " Save.Data[" + entityDTO.toString() + "]");
        try {
            List<String> errors = new ArrayList<>();
            if (result.getFieldErrors().size() != 0) {
                if ((result.getFieldError().getCode().equalsIgnoreCase("NotBlank"))
                        || (result.getFieldError().getCode().equalsIgnoreCase("NotNull"))
                        || (result.getFieldError().getCode().equalsIgnoreCase("NotEmpty"))
                        || (result.getFieldError().getCode().equalsIgnoreCase("Null"))
                        || (result.getFieldError().getCode().equalsIgnoreCase("Digits"))) {
                    errors.addAll(result.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(errors.toString());
                    logger.info(" Fetching FieldError :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                }
            } else if ( (result.getFieldErrors().size() == 0)) {
                FielmappingDto dtoData = fieldmappingService.saveEntity(entityDTO);
                genericDataDTO.setData(dtoData);
                genericDataDTO.setTotalRecords(1);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                logger.info(" SAVE ENTITY SUCCESSFULLY :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
            logger.error("Unable To SAVE DATA :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_ADD + "\")")
    @PostMapping(value = "/addTemplate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO addTemplate(@Valid @RequestBody List<FielmappingDto> entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [saveList()] ";
        logger.info(getModuleNameForLog() + "--" + " save List .Data[" + entityDTO.toString() + "]");
        try {
            List<FielmappingDto> dtoDataList = fieldmappingService.saveEntityList(entityDTO);
            genericDataDTO.setDataList(dtoDataList);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info(" Save List Successfully  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
                logger.error("Unable To Save List  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getbutypes")
    public GenericDataDTO getbutypes(@RequestParam("buid")Long id){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getbutypes()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching BU Types .Data[" + id.toString() + "]");
        try{
            genericDataDTO.setDataList(fieldmappingService.getbutypes(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetch BU type successfully  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        }catch (Exception exception){
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            logger.error("Unable To Fetch BU Type :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }
        return  genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")

    @GetMapping("/getPlanFieldsByServiceid/{serviceId}")
    public GenericDataDTO getPlanFieldsByServiceId(@PathVariable Long serviceId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getPlanFieldsByServiceId()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching PlanFields By ServiceId .Data[" + serviceId.toString() + "]");
        try {
            genericDataDTO.setDataList(fieldmappingService.getPlanFieldsByServiceId(serviceId));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetching Plan Fields By  Service Id  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }
        catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            logger.error("Unable To Fetch Plan Fields By Service Id   :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getAvailableAndBoundedFields")
    public GenericDataDTO getAvailableAndBoundedFields(@RequestParam("screen") Screen screen){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getAvailableAndBoundedFields()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching Available And Bounded Fields .Data[" + screen.toString() + "]");
        try{
            genericDataDTO.setDataList(fieldmappingService.getAvailableAndBoundedFields(screen.name()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetching Available And Bounded Fields  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        }catch (Exception exception){
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            logger.error("Unable To Fetching Available And Bounded Fields   :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),exception.getStackTrace());
        }
        return  genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getCustomerTemplate")
    public GenericDataDTO getAvailableAndBoundedField(@RequestParam("screen")Screen screen){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getCustomerTemplate()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching Available And Bounded Fields .Data[" + screen.toString() + "]");
        try{
            genericDataDTO.setDataList(fieldmappingService.getCustomerTemplate(screen.name()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetching Available And Bounded Fields  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        }catch (Exception exception){
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            logger.error("Unable To Fetching Available And Bounded Fields   :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),exception.getStackTrace());
        }
        return  genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getModuleWiseTemplate")
    public GenericDataDTO getModuleWiseFields(@RequestParam("screen")Screen screen){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getModuleWiseFields()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching Module Wise Fields .Data[" + screen.toString() + "]");
        try{
            genericDataDTO.setDataList(fieldmappingService.getModuleWiseFields(screen.name()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetching Module Wise Fields   :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        }catch (Exception exception){
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            logger.error("Unable To Fetching Module Wise Fields   :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),exception.getStackTrace());
        }
        return  genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/getPresentAddressByCustomerId")
    public GenericDataDTO getPresentAddressByCustomerId(@RequestParam Integer customerId) {
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getPresentAddressByCustomerId()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching PresentAddress By CustomerId .Data[" + customerId.toString() + "]");
        try{
            genericDataDTO.setData(fieldmappingService.getPresentAddressByCustomerId(customerId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetching PresentAddress By CustomerId  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        }catch (Exception exception){
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            logger.error("Unable To Fetch PresentAddress By CustomerId   :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),exception.getStackTrace());
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_FIELD_MAPPING_ALL + "\",\"" + AclConstants.OPERATION_FIELD_MAPPING_VIEW + "\")")
    @GetMapping("/fieldDetailsByParam")
    public GenericDataDTO getFieldDetailsByParam(@RequestParam Long paramId) {
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getFieldDetailsByParam()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching Field details By param .Data[" + paramId.toString() + "]");
        try {
            genericDataDTO.setDataList(fieldmappingService.getFieldDetailsByParam(paramId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        } catch (Exception exception) {
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_NOT_FOUND);
            logger.error("Unable To Fetch PresentAddress By CustomerId   :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),exception.getStackTrace());
        }
        return genericDataDTO;
    }

}
