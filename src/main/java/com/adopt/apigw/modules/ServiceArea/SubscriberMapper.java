package com.adopt.apigw.modules.ServiceArea;

import com.adopt.apigw.constants.DocumentConstants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.CustomerAddress;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.NetworkDevices.domain.OLTPortDetails;
import com.adopt.apigw.modules.NetworkDevices.domain.Oltslots;
import com.adopt.apigw.modules.ippool.model.IPPoolDTO;
import com.adopt.apigw.modules.ippool.service.IPPoolService;
import com.adopt.apigw.modules.subscriber.model.CaseCountModel;
import com.adopt.apigw.modules.subscriber.model.CustIPDetailsDTO;
import com.adopt.apigw.modules.subscriber.model.CustomerPlansModel;
import com.adopt.apigw.modules.subscriber.model.CustomersBasicDetailsPojo;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.api.CustNetworkDetailsDTO;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.StatusMapperUtil;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public abstract class SubscriberMapper {
    private static String MODULE = " [SubscriberMapper] ";

    @Mapping(source = "customers.servicearea", target = "networkDetails")
    @Mapping(source = "customers.altemail", target = "altemail", defaultValue = "-")
    @Mapping(source = "customers.altphone", target = "altphone", defaultValue = "-")
    @Mapping(source = "customers.altmobile", target = "altmobile", defaultValue = "-")
    @Mapping(source = "customers.fax", target = "fax", defaultValue = "-")
//    @Mapping(source = "customers.gst", target = "gst", defaultValue = "-", qualifiedByName = "maskGst")
//    @Mapping(source = "customers.pan", target = "pan", defaultValue = "-", qualifiedByName = "maskPan")
//    @Mapping(source = "customers.aadhar", target = "aadhar", defaultValue = "-", qualifiedByName = "maskAadhar")
    @Mapping(source = "customers.phone", target = "phone", defaultValue = "-")
    @Mapping(source = "customers.voicesrvtype", target = "voicesrvtype", defaultValue = "-")
    @Mapping(source = "customers.didno", target = "didno", defaultValue = "-")
    @Mapping(source = "customers.childdidno", target = "childdidno", defaultValue = "-")
    @Mapping(source = "customers.intercomno", target = "intercomno", defaultValue = "-")
    @Mapping(source = "customers.intercomgrp", target = "intercomgrp", defaultValue = "-")
    @Mapping(source = "customers.strconntype", target = "strconntype", defaultValue = "-")
    @Mapping(source = "customers.stroltname", target = "stroltname", defaultValue = "-")
    @Mapping(source = "customers.strslotname", target = "strslotname", defaultValue = "-")
    @Mapping(source = "customers.strportname", target = "strportname", defaultValue = "-")
    @Mapping(source = "customers.onuid", target = "onuid", defaultValue = "-")
    @Mapping(source = "customers.latitude", target = "latitude", defaultValue = "-")
    @Mapping(source = "customers.longitude", target = "longitude", defaultValue = "-")
    @Mapping(source = "customers.url", target = "url", defaultValue = "-")
    @Mapping(source = "customers.gis_code", target = "gis_code", defaultValue = "-")
    @Mapping(source = "customers.servicetype", target = "servicetype", defaultValue = "-")
    @Mapping(source = "customers.salesremark", target = "salesremark", defaultValue = "-")
    @Mapping(source = "customers.custtype", target = "custtype", defaultValue = "-")
    @Mapping(source = "customers.passportNo", target = "passportNo", defaultValue = "-")
    @Mapping(source = "customers.password", target = "password", defaultValue = "-")
    public abstract CustomersBasicDetailsPojo domainToDTO(Customers customers, CycleAvoidingMappingContext context) throws NoSuchFieldException;

    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private StatusMapperUtil statusMapperUtil;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private IPPoolService ipPoolService;

    @AfterMapping
    void afterMapping(@MappingTarget CustomersBasicDetailsPojo detailsPojo, Customers customers) throws NoSuchFieldException {
        try {
            if (null != customers) {

                detailsPojo.setName(customers.getFullName());
                if (null == detailsPojo.getNetworkDetails())
                    detailsPojo.setNetworkDetails(new CustNetworkDetailsDTO());
                if (customers.getOutstanding() != null) {
                    detailsPojo.setOutstanding(Double.parseDouble(new DecimalFormat("##.##").format(customers.getOutstanding())));
                }
                if (null != customers.getNetworkdevices()) {
                    detailsPojo.getNetworkDetails().setNetworkdeviceid(customers.getNetworkdevices().getId());
                    detailsPojo.getNetworkDetails().setNetworkdevicename(null != customers.getNetworkdevices().getName()
                            || customers.getNetworkdevices().getName().trim().length() <= 0
                            ? customers.getNetworkdevices().getName() : "-");

                    if (null != customers.getNetworkdevices().getOltslotsList() && 0 < customers.getNetworkdevices().getOltslotsList().size()) {
                        List<Oltslots> oltslotList = customers.getNetworkdevices().getOltslotsList().stream()
                                .filter(dto -> null != customers.getOltslotid() && dto.getId().equals(customers.getOltslotid()))
                                .collect(Collectors.toList());

                        if (null != oltslotList && 0 < oltslotList.size()) {
                            Oltslots oltslots = oltslotList.get(0);
                            detailsPojo.getNetworkDetails().setSlotname(null == oltslots.getName()
                                    || oltslots.getName().trim().length() <= 0 ? "-" : oltslots.getName());
                            detailsPojo.getNetworkDetails().setSlotid(oltslots.getId());

                            if (null != oltslots.getOltPortDetailsList() && 0 < oltslots.getOltPortDetailsList().size()) {
                                List<OLTPortDetails> oltPortDetailsList = oltslots.getOltPortDetailsList().stream()
                                        .filter(data -> null != customers.getOltportid() && data.getId()
                                                .equals(customers.getOltportid())).collect(Collectors.toList());

                                if (null != oltPortDetailsList && 0 < oltPortDetailsList.size()) {
                                    OLTPortDetails portDetails = oltPortDetailsList.get(0);
                                    detailsPojo.getNetworkDetails().setPortid(portDetails.getId());
                                    detailsPojo.getNetworkDetails().setPortname(null == portDetails.getName()
                                            || portDetails.getName().trim().length() <= 0
                                            ? "-" : portDetails.getName());
                                }
                            }
                        }
                    }
                } else {
                    detailsPojo.getNetworkDetails().setNetworkdevicename("-");
                    detailsPojo.getNetworkDetails().setServiceareaname("-");
                    detailsPojo.getNetworkDetails().setPortname("-");
                    detailsPojo.getNetworkDetails().setSlotname("-");
                }
                if (null == customers.getFullName() || customers.getFullName().trim().length() <= 0) {
                    detailsPojo.setName("-");
                }
                if (customers.getFax() == null || customers.getFax().trim().length() <= 0) {
                    detailsPojo.setFax("-");
                }
                if (customers.getAltmobile() == null || customers.getAltmobile().trim().length() <= 0) {
                    detailsPojo.setAltmobile("-");
                }
                if (customers.getAltphone() == null || customers.getAltphone().trim().length() <= 0) {
                    detailsPojo.setAltphone("-");
                }
                if (null == customers.getContactperson() || customers.getContactperson().trim().length() <= 0) {
                    detailsPojo.setContactperson("-");
                }
                if (customers.getDidno() == null || customers.getDidno().trim().length() <= 0) {
                    detailsPojo.setDidno("-");
                }
                if (customers.getChilddidno() == null || customers.getChilddidno().trim().length() <= 0) {
                    detailsPojo.setChilddidno("-");
                }
                if (customers.getIntercomno() == null || customers.getIntercomno().trim().length() <= 0) {
                    detailsPojo.setIntercomno("-");
                }
                if (customers.getIntercomgrp() == null || customers.getIntercomgrp().trim().length() <= 0) {
                    detailsPojo.setIntercomgrp("-");
                }

                if (null == customers.getPan() || customers.getPan().trim().length() <= 0) {
                    detailsPojo.setPan("-");
                }

                List<CustIPDetailsDTO> custIPList = ipPoolService.getCustIpDetails(customers.getId().longValue());
                if (null != custIPList && 0 < custIPList.size()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    String ipPurchaseDate = formatter.format(custIPList.get(0).getIpPurchaseDate());
                    String ipExpiredDate = formatter.format(custIPList.get(0).getIpExpiredDate());
                    detailsPojo.setIpPurDate(ipPurchaseDate);
                    detailsPojo.setIpExpDate(ipExpiredDate);
                    detailsPojo.setIpAddress(custIPList.get(0).getIpAddress());
                } else {
                    detailsPojo.setIpPurDate("-");
                    detailsPojo.setIpExpDate("-");
                    detailsPojo.setIpAddress("-");
                }


                //Set serviceArea in network detail
                if (null != customers.getServicearea()) {
                    detailsPojo.getNetworkDetails().setServiceareaid(customers.getServicearea().getId());
                    detailsPojo.getNetworkDetails().setServiceareaname(null == customers.getServicearea().getName()
                            || customers.getServicearea().getName().trim().length() <= 0
                            ? "-" : customers.getServicearea().getName());
                }


                // Plan list
                List<CustomerPlansModel> planList = subscriberService.getCustomerPlanList(customers.getId(), false);
                detailsPojo.setPlanList(planList);

                if (null != planList && 0 < planList.size()) {

                    List<CustomerPlansModel> activePlanList = planList.stream().filter(data -> null != data.getService()
                                    && null != data.getPlanstage()
                                    && data.getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA)
                                    && data.getPlanstage().equalsIgnoreCase(SubscriberConstants.PLAN_STAGE_ACTIVE)
                                    && data.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW))
                            .collect(Collectors.toList());

                    if (null != activePlanList && 0 < activePlanList.size()) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        String expiryDate = "-";
                        if (null != activePlanList.get(0).getExpiryDate()) {
                            expiryDate = formatter.format(activePlanList.get(0).getExpiryDate());
                            detailsPojo.setExpiryDate(expiryDate);
                        } else {
                            detailsPojo.setExpiryDate("-");
                        }
                        detailsPojo.setMaxSession(activePlanList.get(0).getMaxsession());
                    }
                }


                //Set Mac addresses
                List<CustMacMappping> custMacMapppingPojoList = customers.getCustMacMapppingList().stream()
                        .filter(dto -> !dto.getIsDeleted()).collect(Collectors.toList());

                if (null != custMacMapppingPojoList && 0 < custMacMapppingPojoList.size())
                    detailsPojo.setMacAddressModelList(custMacMapppingPojoList);
                else
                    detailsPojo.setMacAddressModelList(new ArrayList<>());

                if (null != customers.getAddressList() && 0 < customers.getAddressList().size()) {
                    List<CustomerAddress> fullAddressList = customers.getAddressList().stream().filter(data -> data.getAddressType()
                            .equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)).collect(Collectors.toList());
                    if (null != fullAddressList && 0 < fullAddressList.size()) {
                        detailsPojo.setAddress(fullAddressList.get(0).getFullAddress());
                        if (fullAddressList.get(0).getFullAddress().trim().length() <= 0) {
                            detailsPojo.setAddress("-");
                        }
                    } else {
                        detailsPojo.setAddress("-");
                    }
                }

                if (null != customers.getSalesrep()) {
                    StaffUser staffUser = staffUserRepository.findById(customers.getSalesrep().getId()).get();
                    if (null != staffUser) {
                        detailsPojo.setSalesRepId(staffUser.getId());
                        detailsPojo.setSalesRepName(staffUser.getFirstname() + " " + staffUser.getLastname());
                    } else
                        detailsPojo.setSalesRepName("-");
                }

                if (null != customers.getPartner()) {
                    Partner partner = partnerRepository.findById(customers.getPartner().getId()).get();
                    if (null != partner) {
                        detailsPojo.setPartnerId(partner.getId());
                        detailsPojo.setPartnerName(partner.getName());
                        if (partner.getName().trim().length() <= 0) {
                            detailsPojo.setPartnerName("-");
                        }
                    } else
                        detailsPojo.setPartnerName("-");
                }

//                if (null != customers.g()) {
//                    detailsPojo.setPreviousCafApprover(customers.getPreviousCafApprover());
//                }
                if (null != customers.getCurrentAssigneeId()) {
                    detailsPojo.setNextCafApprover(customers.getCurrentAssigneeId());
                }

                if (null != customers.getNextTeamHierarchyMapping()) {
                    detailsPojo.setNextTeamHierarchyMapping(customers.getNextTeamHierarchyMapping());
                }


                if (null != customers.getMactelflag()) {
                    detailsPojo.setMactelflag(customers.getMactelflag() ? "YES" : "NO");
                } else
                    detailsPojo.setMactelflag("NO");

                if (null != customers.getOnlinerenewalflag()) {
                    detailsPojo.setOnlinerenewalflag(customers.getOnlinerenewalflag() ? "YES" : "NO");
                } else
                    detailsPojo.setOnlinerenewalflag("NO");

                if (null != customers.getVoipenableflag()) {
                    detailsPojo.setVoipenableflag(customers.getVoipenableflag() ? "YES" : "NO");
                } else
                    detailsPojo.setVoipenableflag("NO");

                if (null != customers.getDefaultpoolid()) {
                    try {
                        IPPoolDTO ipPool = ipPoolService.getEntityById(customers.getDefaultpoolid(),customers.getMvnoId());
                        if (null != ipPool) {
                            detailsPojo.setDefaultpool(null == ipPool.getPoolName() || ipPool.getPoolName().trim().length() <= 0 ? "-" : ipPool.getPoolName());
                            detailsPojo.setDefaultpoolid(ipPool.getPoolId());
                        }
                    } catch (Exception ex) {
                        ApplicationLogger.logger.error(MODULE + " IP Pool " + ex.getMessage());
                    }
                } else
                    detailsPojo.setDefaultpool("-");


                //Set customer status
                detailsPojo.setStatus(statusMapperUtil.getMappedStatus(customers, customers.getClass().getDeclaredField("status")
                        , customers.getStatus()));


                //Case Count for customer
                //commented as no use
//                List<CaseCountModel> countModelList = subscriberService.getCaseCountByCustomer(customers.getId());
//                if (null != countModelList && 0 < countModelList.size()) {
//                    detailsPojo.setCaseCount(subscriberService.getCaseCountByCustomer(customers.getId()).get(0));
//                }
            }
        } catch (NoSuchFieldException ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage());
            throw ex;
        }
    }

    @Named("maskAadhar")
    String maskAadhar(String aadhar) throws Exception {
        if (!StringUtils.isEmpty(aadhar)) {
            String lastFourDigits = aadhar.substring(8);
            return DocumentConstants.AADHAR_STAR_PATTERN + lastFourDigits;
        }
        return aadhar;
    }

    @Named("maskPan")
    String maskPan(String pan) throws Exception {
        if (!StringUtils.isEmpty(pan)) {
            String lastFourDigits = pan.substring(6);
            return DocumentConstants.PAN_STAR_PATTERN + lastFourDigits;
        }
        return pan;
    }

    @Named("maskGst")
    String maskGst(String string) throws Exception {
        if (!StringUtils.isEmpty(string)) {
            String lastFiveDigits = string.substring(9);
            return DocumentConstants.GST_STAR_PATTERN + lastFiveDigits;
        }
        return string;
    }
}
