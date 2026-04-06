package com.adopt.apigw.modules.ippool.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.ippool.domain.IPPoolDtls;
import com.adopt.apigw.modules.ippool.mapper.IPPoolDtlsMapper;
import com.adopt.apigw.modules.ippool.model.IPDetailsDTO;
import com.adopt.apigw.modules.ippool.model.IPPoolDTO;
import com.adopt.apigw.modules.ippool.model.IPPoolDtlsDTO;
import com.adopt.apigw.modules.ippool.model.IpAddressFindResDTO;
import com.adopt.apigw.modules.ippool.model.IpFind;
import com.adopt.apigw.modules.ippool.repository.IPPoolDtlsRepository;
import com.adopt.apigw.modules.ippool.service.IPPoolDtlsService;
import com.adopt.apigw.modules.ippool.service.IPPoolService;
import com.adopt.apigw.modules.ippool.service.IpPoolThread;
import com.adopt.apigw.modules.ippool.utils.SubnetUtils;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.UtilsCommon;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.IP_POOL)
public class IPPoolController extends ExBaseAbstractController<IPPoolDTO> {

    @Autowired
    private IPPoolService ipPoolService;

    @Autowired
    private IPPoolDtlsService ipPoolDtlsService;

    @Autowired
    private IPPoolDtlsRepository ipPoolDtlsRepository;

    @Autowired
    private IPPoolDtlsMapper ipPoolDtlsMapper;
    @Autowired
    AuditLogService auditLogService;

    @Autowired
    private Tracer tracer;

    public IPPoolController(IPPoolService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[IPPoolController]";
    }

    private static final Logger logger = LoggerFactory.getLogger(IPPoolController.class);
    @Deprecated
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }

    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        return super.getAllWithoutPagination(mvnoId);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody IPPoolDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        boolean flag = ipPoolService.deleteVerification(entityDTO.getPoolId().intValue());
        if (flag) {
            genericDataDTO = super.delete(entityDTO, authentication, req);
            IPPoolDTO ipPool = (IPPoolDTO) genericDataDTO.getData();
            logger.info("Deleting Ip pool with id "+entityDTO.getPoolId()+" is Successfull    :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
            //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
            // AclConstants.OPERATION_IP_POOL_DELETE, req.getRemoteAddr(), null, ipPool.getPoolId().longValue(), ipPool.getPoolName());
        } else {
            genericDataDTO.setResponseMessage(DeleteContant.IP_POOL_DELETE_EXIST);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());

            logger.error("Unable to Delete ip Pool with id "+entityDTO.getPoolId()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE);
        }
        MDC.remove("type");

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = super.getEntityById(id, req,mvnoId);
        IPPoolDTO ipPool = (IPPoolDTO) genericDataDTO.getData();
        //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
        // AclConstants.OPERATION_IP_POOL_VIEW, req.getRemoteAddr(), null, ipPool.getPoolId().longValue(), ipPool.getPoolName());
        MDC.remove("type");

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.getAll(requestDTO,req,mvnoId);
            if(!genericDataDTO.getDataList().isEmpty()){
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch Timebase policy"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }
            else {
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Timebase policy" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }
        }catch (Exception ex){
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Timebase policy"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +APIConstants.EXPECTATION_FAILED+ LogConstants.LOG_NO_RECORD_FOUND+ APIConstants.ERROR_MESSAGE+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ADD + "\")")
    @PostMapping(value = {"/save"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO save(@Valid @RequestBody IPPoolDTO ipPoolDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) {
        MDC.put("type", "Save");
        ApplicationLogger.logger.info(getModuleNameForLog() + " [save]");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            boolean flag = ipPoolService.duplicateVerifyAtSave(ipPoolDTO.getPoolName());
            if(ipPoolDTO.getPoolId() != null) {
            	flag = true;
            }
            if (flag) {
                if (ipPoolDTO.getIsStaticIpPool()) {
                    SubnetUtils subnetUtils = new SubnetUtils(ipPoolDTO.getIpRange());
                    ipPoolDTO.setNetMask(subnetUtils.getNetmask());
                    ipPoolDTO.setNetworkIp(subnetUtils.getNetworkIp());
                    ipPoolDTO.setBroadcastIp(subnetUtils.getBroadcastAddress());
                    ipPoolDTO.setFirstHost(subnetUtils.getFirstIp());
                    ipPoolDTO.setLastHost(subnetUtils.getLastIp());
                    ipPoolDTO.setTotalHost(subnetUtils.getNumberOfHosts());
//            ipPoolDTO.setIpRange(subnetUtils.getHostAddressRange());

                    ipPoolDTO = ipPoolService.saveEntity(ipPoolDTO);
                    LoggedInUser user = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    List<IPPoolDtlsDTO> ipPoolDtlsDTOList = new ArrayList<>();
                    for (String ipAddress : subnetUtils.getAvailableIPs(subnetUtils.getNumberOfHosts())) {
                        IPPoolDtlsDTO ipPoolDtlsDTO = new IPPoolDtlsDTO();
                        ipPoolDtlsDTO.setPoolId(ipPoolDTO.getPoolId());
                        ipPoolDtlsDTO.setIpAddress(ipAddress);
                        ipPoolDtlsDTO.setStatus("Free");
                        ipPoolDtlsDTO.setIsDelete(false);
                        ipPoolDtlsDTOList.add(ipPoolDtlsDTO);
                        ipPoolDtlsDTO.setCreatedById(user.getUserId());
                        ipPoolDtlsDTO.setCreatedByName(user.getFullName());
                        ipPoolDtlsDTO.setLastModifiedById(user.getUserId());
                        ipPoolDtlsDTO.setLastModifiedByName(user.getFullName());
                        ipPoolDtlsDTO.setCreatedate(LocalDateTime.now());
                        ipPoolDtlsDTO.setUpdatedate(LocalDateTime.now());
                    }
                    Runnable ippoolThread = new IpPoolThread(ipPoolDtlsDTOList, ipPoolDtlsMapper, ipPoolDtlsRepository);
                    Thread ippool = new Thread(ippoolThread);
                    ippool.start();

                } else {
                    ipPoolDTO = ipPoolService.saveEntity(ipPoolDTO);
                }
                //  ipPoolDtlsService.saveEntity(ipPoolDtlsDTO);
                genericDataDTO.setData(ipPoolDTO);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
                genericDataDTO.setTotalRecords(1);
                logger.info("creating new IPPool with name "+ipPoolDTO.getPoolName()+" :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
                //     AclConstants.OPERATION_IP_POOL_ADD, req.getRemoteAddr(), null, ipPoolDTO.getPoolId(), ipPoolDTO.getPoolName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.IPPOOL_NAME_EXITS);
                logger.error("Unable to Delete ip Pool with name "+ipPoolDTO.getPoolName()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE);
                return genericDataDTO;
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
                logger.error("Unable to Delete ip Pool with name "+ipPoolDTO.getPoolName()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception: {}", req.getHeader("requestFrom"),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,e.getMessage());
            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
                logger.error("Unable to Delete ip Pool with name "+ipPoolDTO.getPoolName()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception: {}", req.getHeader("requestFrom"),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,e.getMessage());
            }
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }
        MDC.remove("type");

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_EDIT + "\")")
    @PostMapping(value = {"/update"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO update(@Valid @RequestBody IPPoolDTO ipPoolDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Update");
        //boolean flag = ipPoolService.duplicateVerifyAtEdit(ipPoolDTO.getPoolName(), ipPoolDTO.getPoolId().intValue());
//        String oldname=ipPoolService.getEntityForUpdateAndDelete(ipPoolDTO.getPoolId()).getPoolName();
        IPPoolDTO oldvalue=ipPoolService.getEntityForUpdateAndDelete(ipPoolDTO.getPoolId(),ipPoolDTO.getMvnoId());
        String updatedValues = UtilsCommon.getUpdatedDiff(oldvalue,ipPoolDTO);
        //if (flag) {
        try {

            ipPoolService.getEntityForUpdateAndDelete(ipPoolDTO.getPoolId(),ipPoolDTO.getMvnoId());
            genericDataDTO = save(ipPoolDTO, result, authentication, req,mvnoId);
            IPPoolDTO ipPool = (IPPoolDTO) genericDataDTO.getData();
            // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
            //       AclConstants.OPERATION_IP_POOL_EDIT, req.getRemoteAddr(), null, ipPool.getPoolId(), ipPool.getPoolName());
            //} else {
            //   genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            //   genericDataDTO.setResponseMessage(MessageConstants.IPPOOL_NAME_EXITS);
            logger.info(" Updating Ip pool With oldname "+updatedValues+" :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            //}
        } catch (CustomValidationException ex){
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Update ip Pool with name "+ipPoolDTO.getPoolName()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception: {}", req.getHeader("requestFrom"),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getStackTrace());
        }

        MDC.remove("type");

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @PostMapping("/ipFind")
    public GenericDataDTO ipAddressFind(@RequestBody IpFind ipFind, HttpServletRequest req) {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info(getModuleNameForLog() + " [ipAddress]");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (ipFind.getIpaddress() != null) {
                IpAddressFindResDTO ippoolreqDtl = ipPoolDtlsService.findByIpAddress(ipFind.getIpaddress());
                genericDataDTO.setData(ippoolreqDtl);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
                logger.info("Fetching Ip Address from ip pool "+ipFind+" :   Response : {{}{}}",genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
                //      AclConstants.OPERATION_IP_POOL_VIEW, req.getRemoteAddr(), null, ippoolreqDtl.getPoolDetailsId(), ippoolreqDtl.getCustomerName());

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Please Provide Ip Address");
                logger.error("Unable to Find ip address "+ipFind+"  : Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
            }
            logger.error("Unable to find ipAddress "+ipFind+" :   Response : {{}};Exception :{}", genericDataDTO.getResponseCode(),e.getMessage());
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }
        MDC.remove("type");

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_EDIT + "\")")
    @PostMapping("/ipFind/update")
    public GenericDataDTO ipAddressUpdate(@RequestBody IPPoolDtlsDTO ipPoolDtlsDTO, HttpServletRequest req) {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info(getModuleNameForLog() + " [ipAddressUpdate]");
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            IPPoolDtlsDTO ipPoolDtlsDTOold = ipPoolDtlsService.updateIPAddress(ipPoolDtlsDTO);
            if (ipPoolDtlsDTO.getPoolDetailsId() != null) {

                IPPoolDtlsDTO ipPoolDtlsDTO1 = ipPoolDtlsService.updateIPAddress(ipPoolDtlsDTO);
                genericDataDTO.setData(ipPoolDtlsDTO1);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
                logger.info("Updating ip address from "+ipPoolDtlsDTOold.getIpAddress()+" to "+ipPoolDtlsDTO1.getIpAddress()+" is Successfull : Response : {{}}",genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());

                //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
                //     AclConstants.OPERATION_IP_POOL_EDIT, req.getRemoteAddr(), null, ipPoolDtlsDTO.getPoolDetailsId(), ipPoolDtlsDTO.getIpAddress());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("Unable to update ipaddress "+ipPoolDtlsDTOold.getIpAddress()+" to "+ipPoolDtlsDTO.getIpAddress()+" :   Response : {{}}",genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
            }
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
            logger.error("Unable to Update ip address "+ipPoolDtlsDTO.getIpAddress()+" : Response : {{}};Exception :{}",genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getMessage());
        }
        MDC.remove("type");

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @GetMapping(value = "/getPoolByIp")
    public GenericDataDTO getIPDetailsByNetworkIp(@RequestParam("networkIp") String networkIp, Authentication authentication) {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info(getModuleNameForLog() + " [getIPDetailsByNetworkIp]");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            IPDetailsDTO ipDetailsDTO = new IPDetailsDTO();
            SubnetUtils subnetUtils = new SubnetUtils(networkIp);
            ipDetailsDTO.setNetMask(subnetUtils.getNetmask());
            ipDetailsDTO.setNetworkIp(subnetUtils.getNetworkIp());
            ipDetailsDTO.setBroadcastIp(subnetUtils.getBroadcastAddress());
            ipDetailsDTO.setFirstHost(subnetUtils.getFirstIp());
            ipDetailsDTO.setLastHost(subnetUtils.getLastIp());
            ipDetailsDTO.setTotalHost(subnetUtils.getNumberOfHosts());
            ipDetailsDTO.setIpRange(subnetUtils.getHostAddressRange());
            genericDataDTO.setData(ipDetailsDTO);
            genericDataDTO.setTotalRecords(1);
            logger.info("Fetching Ip Details by network Ip "+networkIp +" is Successfull :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to Fetch network details by ip "+networkIp+" :  request: { From : {}}; Response : {{}};Exception :{}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_VIEW + "\")")
//    @GetMapping("/searchIpAddress")
//    public GenericDataDTO searchIpAddress(@RequestParam("ipAddress") String ipAddress, Authentication authentication) {
//        ApplicationLogger.logger.info(getModuleNameForLog() + " [searchIpAddress]");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            List<IPPoolDTO> ipPoolList = new ArrayList<>();
//            for (IPPoolDtls ipPoolDtls : ipPoolDtlsService.findByIpAddress(ipAddress))
//                ipPoolList.add(ipPoolService.getEntityById(ipPoolDtls.getPoolId()));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(ipPoolList);
//            genericDataDTO.setTotalRecords(ipPoolList.size());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(e.getMessage(), e);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return genericDataDTO;
//    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getNonAllocatedIp")
    public GenericDataDTO getNonAllocatedIp(@RequestParam("poolId") Long poolId, Authentication authentication,
                                            @RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "poolDetailsId") String sortBy) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try {
            Page<IPPoolDtls> ipPoolDtls = ipPoolDtlsService.findNonAllocatedPoolId(poolId, page - 1, pageSize, sortBy, sortOrder);
            genericDataDTO.setDataList(ipPoolDtls.getContent());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setTotalRecords(ipPoolDtls.getTotalElements());
            genericDataDTO.setPageRecords(ipPoolDtls.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(ipPoolDtls.getNumber() + 1);
            genericDataDTO.setTotalPages(ipPoolDtls.getTotalPages());
            logger.info("Fetching non located ip "+poolId+" is Successfull : Response : {{}{}}", genericDataDTO.getResponseMessage(), genericDataDTO.getResponseCode());
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to fetch Non Located ip "+poolId+" :   Response : {{}};Exception :{}",genericDataDTO.getResponseCode(),e.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @PostMapping("/blockIP/{poolDetailsId}/{custId}")
    public GenericDataDTO blockIp(@PathVariable Long poolDetailsId, @PathVariable Long custId, HttpServletRequest req) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            IPPoolDtlsDTO ipPoolDtlsDTO =null;

            ipPoolDtlsDTO = ipPoolDtlsMapper.domainToDTO(ipPoolDtlsRepository.findById(poolDetailsId).get(),new CycleAvoidingMappingContext());
            if (ipPoolDtlsDTO == null) {
                genericDataDTO.setResponseMessage("Given ip is not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to Block ip address "+poolDetailsId+"  "+ipPoolDtlsDTO.getIpAddress()+" :  request: { From : {}}; Response : {{}};", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(ipPoolDtlsService.blockIp(ipPoolDtlsDTO, custId));

            //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_IP_POOL,
            //       AclConstants.OPERATION_IP_POOL_EDIT, req.getRemoteAddr(), null, custId.longValue(), ipPoolDtlsDTO.getIpAddress());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            logger.info("Ip address "+ipPoolDtlsDTO.getIpAddress()+" is  Successfully blocked :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to block ip address "+poolDetailsId+" :  request: { From : {}}; Response : {{}};Exception :{}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_IP_POOL_MANAGEMENT_VIEW + "\")")
    @GetMapping("/searchPoolId")
    public GenericDataDTO searchPoolId(@RequestParam("poolId") Long poolId) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<IPPoolDtls> ipPoolDtls = ipPoolDtlsService.findByPoolId(poolId);
            genericDataDTO.setDataList(ipPoolDtls);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setTotalRecords(ipPoolDtls.size());
            logger.info("Searching ip pool with pool id "+poolId+" is Successfull :  request: { From : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode());
        } catch (Exception e) {
    //        ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to find pool with poolid "+poolId+" :  request: { From : {}}; Response : {{}};Exception :{}", getModuleNameForLog(), genericDataDTO.getResponseCode(),e.getMessage());
        }
        MDC.remove("type");

        return genericDataDTO;
    }
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }
}
