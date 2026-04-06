package com.adopt.apigw.modules.tickets.mapper;
//
//import com.adopt.apigw.core.exceptions.DataNotFoundException;
//import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
//import com.adopt.apigw.core.mapper.IBaseMapper;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.model.common.Customers;
//import com.adopt.apigw.model.common.StaffUser;
//import com.adopt.apigw.model.postpaid.Partner;
//import com.adopt.apigw.modules.NetworkDevices.model.NetworkDeviceDTO;
//import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.OLTPortDTO;
//import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.OLTSlotDetailDTO;
//import com.adopt.apigw.modules.NetworkDevices.service.NetworkDeviceService;
//import com.adopt.apigw.modules.NetworkDevices.service.SlotService.OLTSlotService;
//import com.adopt.apigw.modules.NetworkDevices.service.SlotService.OltPortService;
//import com.adopt.apigw.modules.ResolutionReasons.domain.ResolutionReasons;
//import com.adopt.apigw.modules.ResolutionReasons.mapper.ResolutionReasonsMapper;
//import com.adopt.apigw.modules.ResolutionReasons.model.ResolutionReasonsDTO;
//import com.adopt.apigw.modules.ResolutionReasons.service.ResolutionReasonsService;
//import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
//import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
//import com.adopt.apigw.modules.tickets.domain.*;
//import com.adopt.apigw.modules.tickets.model.CaseDTO;
//import com.adopt.apigw.modules.tickets.model.CaseUpdateDTO;
//import com.adopt.apigw.modules.tickets.model.TicketReasonSubCategoryDTO;
//import com.adopt.apigw.modules.tickets.repository.CaseDocDetailsRepository;
//import com.adopt.apigw.modules.tickets.repository.TicketReasonCategoryRepo;
//import com.adopt.apigw.modules.tickets.service.LiveCustomerNetworkDetailsService;
//import com.adopt.apigw.modules.tickets.service.TicketReasonCategoryService;
//import com.adopt.apigw.modules.tickets.service.TicketReasonSubCategoryService;
//import com.adopt.apigw.service.common.CustomersService;
//import com.adopt.apigw.service.common.StaffUserService;
//import com.adopt.apigw.service.postpaid.PartnerService;
//import org.mapstruct.*;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Mapper(uses = CaseAssignmentMapper.class)
public abstract class CaseMapper  {
//
//    private String MODULE = " [CaseMapper] ";
//
//    @Override
//    @Mapping(target = "customers", source = "dto.customersId")
//    @Mapping(target = "currentAssignee", source = "dto.currentAssigneeId")
//    @Mapping(target = "finalResolution", source = "dto.finalResolutionId")
//    @Mapping(target = "finalResolvedBy", source = "dto.finalResolvedById")
//    @Mapping(target = "finalClosedBy", source = "dto.finalClosedById")
//    @Mapping(target = "partner", source = "dto.partnerid")
//    public abstract Case dtoToDomain(CaseDTO dto, @Context CycleAvoidingMappingContext context);
//
//    @Override
//    @Mapping(source = "domain.customers", target = "customersId")
//    @Mapping(source = "domain.currentAssignee", target = "currentAssigneeId")
//    @Mapping(source = "domain.finalResolution", target = "finalResolutionId")
//    @Mapping(source = "domain.finalResolvedBy", target = "finalResolvedById")
//    @Mapping(source = "domain.finalClosedBy", target = "finalClosedById")
//    @Mapping(source = "finalClosedDate", target = "finalClosedByDateString", dateFormat = "dd-MM-yyyy hh:mm a")
//    @Mapping(source = "finalResolutionDate", target = "finalResolutionDateString", dateFormat = "dd-MM-yyyy hh:mm a")
//    @Mapping(source = "caseStartedOn", target = "caseStartedOnString", dateFormat = "dd-MM-yyyy hh:mm a")
//    @Mapping(source = "firstAssignedOn", target = "firstAssignedOnString", dateFormat = "dd-MM-yyyy hh:mm a")
//    @Mapping(source = "createdate", target = "createDateString", dateFormat = "dd-MM-yyyy hh:mm a")
//    @Mapping(source = "updatedate", target = "updateDateString", dateFormat = "dd-MM-yyyy hh:mm a")
//    @Mapping(source = "domain.partner", target = "partnerid")
//    public abstract CaseDTO domainToDTO(Case domain, @Context CycleAvoidingMappingContext context);
//
//    @Autowired
//    private StaffUserService staffUserService;
//    @Autowired
//    private CustomersService customersService;
//    //    @Autowired
////    private CaseReasonService caseReasonService;
////    @Autowired
////    private CaseReasonMapper caseReasonMapper;
//    @Autowired
//    private ResolutionReasonsService resolutionReasonsService;
//    @Autowired
//    private ResolutionReasonsMapper resolutionReasonsMapper;
//    @Autowired
//    private NetworkDeviceService networkDeviceService;
//    @Autowired
//    private OLTSlotService oltSlotService;
//    @Autowired
//    private OltPortService oltPortService;
//    @Autowired
//    private ServiceAreaService serviceAreaService;
//    @Autowired
//    private LiveCustomerNetworkDetailsService liveCustomerNetworkDetailsService;
//    @Autowired
//    private PartnerService partnerService;
//
//    @Autowired
//    TicketReasonCategoryService ticketReasonCategoryService;
//    @Autowired
//    TicketReasonSubCategoryService ticketReasonSubCategoryService;
//
//    @Autowired
//    CaseDocDetailsRepository caseDocDetailsRepository;
//
//    @Autowired
//    TicketReasonCategoryRepo ticketReasonCategoryRepo;
//
//    Integer fromPartnerToId(Partner partner) {
//        return null != partner ? partner.getId() : null;
//    }
//
//    Partner fromIdToPartner(Integer id) {
//        if (null == id) return null;
//        Partner entity = null;
//        try {
//            entity = partnerService.get(id);
//            entity.setId(id);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }
//
//    Integer fromStaffUserToId(StaffUser staffUser) {
//        return null != staffUser ? staffUser.getId() : null;
//    }
//
//    StaffUser fromIdToStaffUser(Integer id) {
//        if (null == id) return null;
//        StaffUser entity = null;
//        try {
//            entity = staffUserService.get(id);
//            entity.setId(id);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }
//
////    Long fromCaseReasonToId(CaseReason caseReason) {
////        return null != caseReason ? caseReason.getReasonId() : null;
////    }
//
////    CaseReason fromIdToCaseReason(Long id) {
////        if (null == id) return null;
////        CaseReason entity = null;
////        try {
////            CaseReasonDTO dto = caseReasonService.getEntityById(id);
////            entity = caseReasonMapper.dtoToDomain(dto, new CycleAvoidingMappingContext());
////            entity.setReasonId(dto.getReasonId());
////        } catch (Exception ex) {
////            ex.printStackTrace();
////            entity = null;
////        }
////        return entity;
////    }
//
//    Integer fromCustomerToId(Customers customer) {
//        return null != customer ? customer.getId() : null;
//    }
//
//    Customers fromIdToCustomer(Integer id) {
//        if (null == id) return null;
//        Customers entity = null;
//        try {
//            entity = customersService.get(id);
//            entity.setId(id);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }
//
//    Long fromReasonToId(ResolutionReasons resolutionReasons) {
//        return null != resolutionReasons ? resolutionReasons.getId() : null;
//    }
//
//    ResolutionReasons fromIdToReason(Long id) {
//        if (null == id) return null;
//        ResolutionReasons entity = null;
//        try {
//            ResolutionReasonsDTO dto = resolutionReasonsService.getEntityById(id);
//            entity = resolutionReasonsMapper.dtoToDomain(dto, new CycleAvoidingMappingContext());
//            entity.setId(dto.getId());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }
//
//    @Mapping(source = "caseUpdate.ticket", target = "ticketId")
//    @Mapping(source = "createdate", target = "createDateString", dateFormat = "dd-MM-yyyy hh:mm a")
//    @Mapping(source = "updatedate", target = "updateDateString", dateFormat = "dd-MM-yyyy hh:mm a")
//    public abstract CaseUpdateDTO updateToUpdateDTO(CaseUpdate caseUpdate, @Context CycleAvoidingMappingContext context);
//
//    @Mapping(source = "dtoData.ticketId", target = "ticket")
//    public abstract CaseUpdate updateDTOToUpdate(CaseUpdateDTO dtoData, @Context CycleAvoidingMappingContext context);
//
//    @AfterMapping
//    void afterMapping(@MappingTarget CaseDTO caseDTO, Case caseDomain) {
//        try {
//            if (null != caseDomain && null != caseDomain.getCustomers()) {
//                Customers customers = customersService.get(caseDomain.getCustomers().getId());
//                if (null != customers) {
//
//                    if (null != customers.getNetworkdevices()) {
//                        try {
//                            NetworkDeviceDTO oltDTO = networkDeviceService.getEntityById(customers.getNetworkdevices().getId());
//                            caseDTO.setOltName(null != oltDTO ? oltDTO.getName() : "-");
//                        } catch (DataNotFoundException dnfe) {
//                            ApplicationLogger.logger.error(MODULE + " [Network Devices] " + dnfe.getMessage(), dnfe);
//                            caseDTO.setOltName("-");
//                        }
//                    } else caseDTO.setOltName("-");
//
//                    if (null != customers.getServicearea()) {
//                        try {
//                            ServiceAreaDTO serviceArea = serviceAreaService.getEntityById(customers.getServicearea().getId());
//                            caseDTO.setServiceAreaName(null != serviceArea ? serviceArea.getName() : "-");
//                            caseDTO.setServiceAreaId(null != serviceArea ? serviceArea.getId() : null);
//                        } catch (DataNotFoundException dnfe) {
//                            ApplicationLogger.logger.error(MODULE + " [Service Area] " + dnfe.getMessage(), dnfe);
//                            caseDTO.setServiceAreaName("-");
//                            caseDTO.setServiceAreaId(null);
//                        }
//                    } else caseDTO.setServiceAreaName("-");
//
////                    if(caseDomain.getCaseReason().getPrimaryKey()!=null) {
////                        caseDTO.setCaseReasonName(caseDomain.getCaseReason().getName());
////                        caseDTO.setCaseReasonTimeUnit(caseDomain.getCaseReason().getTimeUnit());
////                        caseDTO.setCaseReasonTime(caseDomain.getCaseReason().getTime());
////                        caseDTO.setTatConsideration(caseDomain.getCaseReason().getTatConsideration());
////                    }else {
////                    	caseDTO.setCaseReasonName("-");
////                    }
//                    if (null != customers.getOltslotid()) {
//                        try {
//                            OLTSlotDetailDTO slotDetailDTO = oltSlotService.getEntityById(customers.getOltslotid());
//                            caseDTO.setSlotName(null != slotDetailDTO ? slotDetailDTO.getName() : "-");
//                        } catch (DataNotFoundException dnfe) {
//                            ApplicationLogger.logger.error(MODULE + " [Slot] " + dnfe.getMessage(), dnfe);
//                            caseDTO.setSlotName("-");
//                        }
//                    } else caseDTO.setSlotName("-");
//
//                    if (null != customers.getOltportid()) {
//                        try {
//                            OLTPortDTO portDTO = oltPortService.getEntityById(customers.getOltportid());
//                            caseDTO.setPortName(null != portDTO ? portDTO.getName() : "-");
//                        } catch (DataNotFoundException dnfe) {
//                            ApplicationLogger.logger.error(MODULE + " [Port] " + dnfe.getMessage(), dnfe);
//                            caseDTO.setPortName("-");
//                        }
//                    } else caseDTO.setPortName("-");
//
//
////                    List<LiveUserServiceAreaWiseDetailsModel> list = liveCustomerNetworkDetailsService
////                            .getCustomerWiseNetworkDetailsFromLiveUser(customers.getId());
//
////                    if (null != list && 0 < list.size()) {
////                        caseDTO.setLiveUserServiceAreaDetails(list.get(0));
////                    }
//                    caseDTO.setCustomerName(customers.getFullName());
//
//                    if (null != caseDTO.getCurrentAssigneeId()) {
//                        StaffUser staffUser = staffUserService.get(caseDTO.getCurrentAssigneeId());
//                        if (null != staffUser) {
//                            caseDTO.setCurrentAssigneeName(staffUser.getFullName());
//                        } else caseDTO.setCurrentAssigneeName("-");
//                    } else caseDTO.setCurrentAssigneeName("-");
//
//                    if (null != caseDTO.getFinalClosedById()) {
//                        StaffUser staffUser = staffUserService.get(caseDTO.getFinalClosedById());
//                        if (null != staffUser) {
//                            caseDTO.setFinalClosedByName(staffUser.getFullName());
//                        } else caseDTO.setFinalClosedByName("-");
//                    } else caseDTO.setFinalClosedByName("-");
//
//
//                    if (null != caseDTO.getFinalResolvedById()) {
//                        StaffUser staffUser = staffUserService.get(caseDTO.getFinalResolvedById());
//                        if (null != staffUser) {
//                            caseDTO.setFinalResolvedByName(staffUser.getFullName());
//                        } else caseDTO.setFinalResolvedByName("-");
//                    } else caseDTO.setFinalResolvedByName("-");
//
//
//                    if (null != caseDTO.getFinalResolutionId()) {
//                        try {
//                            ResolutionReasonsDTO reasonsDTO = resolutionReasonsService.getEntityById(caseDTO.getFinalResolutionId().longValue());
//                            caseDTO.setFinalResolutionName(null != reasonsDTO ? reasonsDTO.getName() : "-");
//                        } catch (DataNotFoundException dnfe) {
//                            ApplicationLogger.logger.error(MODULE + " [Final Resolution] " + dnfe.getMessage(), dnfe);
//                            caseDTO.setFinalResolutionName("-");
//                        }
//                    } else caseDTO.setFinalResolutionName("-");
//
//
////                    if (null != caseDomain.getCaseReason()) {
////                        caseDTO.setReason(caseDomain.getCaseReason().getName());
////                    } else
////                        caseDTO.setReason("-");
//
//                    caseDTO.setUserName(customers.getUsername());
//                    caseDTO.setMobile(customers.getMobile());
//                    caseDTO.setEmail(customers.getEmail());
//
//                    if (null != customers.getPartner()) {
//                        caseDTO.setPartnerName(null != customers.getPartner().getName() ? customers.getPartner().getName() : "-");
//                    } else {
//                        caseDTO.setPartnerName("-");
//                    }
//
//                    caseDTO.setCaseTitle(null != caseDomain.getCaseTitle() ? caseDomain.getCaseTitle() : "-");
//
//                    if (Objects.nonNull(caseDTO.getTicketReasonCategoryId())) {
//                        TicketReasonCategory ticketReasonCategory = ticketReasonCategoryRepo.findById(caseDTO.getTicketReasonCategoryId()).orElse(null);
//                        caseDTO.setCaseReasonCategory(ticketReasonCategory.getCategoryName());
//                        if (caseDTO.getReasonSubCategoryId() != null) {
//                            TicketReasonSubCategoryDTO ticketReasonSubCategoryDTO = ticketReasonSubCategoryService.getEntityById(caseDTO.getReasonSubCategoryId());
//                            if(ticketReasonSubCategoryDTO!=null) {
//                                caseDTO.setCaseReasonSubCategory(ticketReasonSubCategoryDTO.getSubCategoryName());
//                            }else{
//                                caseDTO.setCaseReasonCategory("-");
//                            }
//                                if (caseDTO.getGroupReasonId() != null) {
//                                List<TicketSubCategoryGroupReasonMapping> ticketSubCategoryGroupReasonMapping = ticketReasonSubCategoryDTO.getTicketSubCategoryGroupReasonMappingList().stream().filter(t -> t.getId().equals(caseDTO.getGroupReasonId())).collect(Collectors.toList());
//                                if (ticketSubCategoryGroupReasonMapping.size() > 0) {
//                                    caseDTO.setCaseReason(ticketSubCategoryGroupReasonMapping.get(0).getReason());
//                                }
//                            } else {
//                                caseDTO.setCaseReason("-");
//                            }
//                        } else {
//                            caseDTO.setCaseReasonSubCategory("-");
//                        }
//
//
//                    } else {
//                        caseDTO.setCaseReasonCategory("-");
//                    }
//                }
//                if (caseDomain.getCaseId() != null) {
//                    List<CaseDocDetails> caseDocDetails = caseDocDetailsRepository.findAllByTicketId(caseDomain.getCaseId());
//                    if (caseDocDetails.size() > 0) {
//                        caseDTO.setCaseDocDetails(caseDocDetails);
//                    } else {
//                        caseDTO.setCaseDocDetails(new ArrayList<>());
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
//            ex.printStackTrace();
//        }
//    }
//
}
