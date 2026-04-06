package com.adopt.apigw.modules.subscriber.mapper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.CustomerAddress;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.service.common.StaffUserService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.NetworkDevices.domain.OLTPortDetails;
import com.adopt.apigw.modules.NetworkDevices.domain.Oltslots;
import com.adopt.apigw.modules.subscriber.model.CustomerListingPojo;
import com.adopt.apigw.pojo.api.CustNetworkDetailsDTO;
import com.adopt.apigw.utils.StatusMapperUtil;

@Mapper
public abstract class SubscriberDetailsMapper {
    @Autowired
    private StatusMapperUtil statusMapperUtil;

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    StaffUserRepository staffUserRepository;


    private static String MODULE = " [SubscriberDetailsMapper] ";

    @Mapping(source = "customers.servicearea", target = "networkDetails")
//    @Mapping(source = "aadhar", target = "aadhar", qualifiedByName = "maskAadhar")
    public abstract CustomerListingPojo domainToDTO(Customers customers, CycleAvoidingMappingContext context) throws NoSuchFieldException;


    @AfterMapping
    void afterMapping(@MappingTarget CustomerListingPojo custListingPojo, Customers customers) throws NoSuchFieldException {
        try {
            if (null != customers) {
                custListingPojo.setName(customers.getFullName());
            }
            if (null == custListingPojo.getNetworkDetails())
                custListingPojo.setNetworkDetails(new CustNetworkDetailsDTO());
            if (customers.getOutstanding() != null) {
                custListingPojo.setOutstanding(Double.parseDouble(new DecimalFormat("##.##").format(customers.getOutstanding())));
            }
            if (null != customers.getNetworkdevices()) {
                custListingPojo.getNetworkDetails().setNetworkdeviceid(customers.getNetworkdevices().getId());
                custListingPojo.getNetworkDetails().setNetworkdevicename(null != customers.getNetworkdevices().getName()
                        || customers.getNetworkdevices().getName().trim().length() <= 0
                        ? customers.getNetworkdevices().getName() : "-");

                if (null != customers.getNetworkdevices().getOltslotsList() && 0 < customers.getNetworkdevices().getOltslotsList().size()) {
                    List<Oltslots> oltslotList = customers.getNetworkdevices().getOltslotsList().stream()
                            .filter(dto -> null != customers.getOltslotid() && dto.getId().equals(customers.getOltslotid()))
                            .collect(Collectors.toList());

                    if (null != oltslotList && 0 < oltslotList.size()) {
                        Oltslots oltslots = oltslotList.get(0);
                        custListingPojo.getNetworkDetails().setSlotname(null == oltslots.getName()
                                || oltslots.getName().trim().length() <= 0 ? "-" : oltslots.getName());
                        custListingPojo.getNetworkDetails().setSlotid(oltslots.getId());

                        if (null != oltslots.getOltPortDetailsList() && 0 < oltslots.getOltPortDetailsList().size()) {
                            List<OLTPortDetails> oltPortDetailsList = oltslots.getOltPortDetailsList().stream()
                                    .filter(data -> null != customers.getOltportid() && data.getId()
                                            .equals(customers.getOltportid())).collect(Collectors.toList());

                            if (null != oltPortDetailsList && 0 < oltPortDetailsList.size()) {
                                OLTPortDetails portDetails = oltPortDetailsList.get(0);
                                custListingPojo.getNetworkDetails().setPortid(portDetails.getId());
                                custListingPojo.getNetworkDetails().setPortname(null == portDetails.getName()
                                        || portDetails.getName().trim().length() <= 0
                                        ? "-" : portDetails.getName());
                            }
                        }
                    }
                }
            } else {
                custListingPojo.getNetworkDetails().setNetworkdevicename("-");
                custListingPojo.getNetworkDetails().setServiceareaname("-");
                custListingPojo.getNetworkDetails().setPortname("-");
                custListingPojo.getNetworkDetails().setSlotname("-");
            }

            //Set fullname
            if (null == customers.getFullName() || customers.getFullName().trim().length() <= 0) {
                custListingPojo.setName("-");
            }

            custListingPojo.setNextCafApprover(customers.getCurrentAssigneeId());
            custListingPojo.setNextTeamHierarchyMapping(customers.getNextTeamHierarchyMapping());

            //Set serviceArea in network detail
            if (null != customers.getServicearea()) {
                custListingPojo.getNetworkDetails().setServiceareaid(customers.getServicearea().getId());
                custListingPojo.getNetworkDetails().setServiceareaname(null == customers.getServicearea().getName()
                        || customers.getServicearea().getName().trim().length() <= 0
                        ? "-" : customers.getServicearea().getName());
            }

            //Set customer status
            custListingPojo.setStatus(statusMapperUtil.getMappedStatus(customers, customers.getClass().getDeclaredField("status")
                    , customers.getStatus()));
            if (customers.getCusttype() != null && !"".equals(customers.getCusttype())) {
                custListingPojo.setCusttype(customers.getCusttype());
            } else {
                custListingPojo.setCusttype("-");
            }

            //Set Service Area
            if(customers.getServicearea()!=null){
                custListingPojo.setServiceArea(customers.getServicearea());
            }
            if(customers.getAddressList() !=null){
                custListingPojo.setCustAddressList(customers.getAddressList());
                //StringBuilder stringBuilder = new StringBuilder();
                List<CustomerAddress> customerAddressesList = new ArrayList<>();
                customerAddressesList = custListingPojo.getCustAddressList();

                for (CustomerAddress customerAddress:customerAddressesList) {
                    if(customerAddress.getAddressType().equalsIgnoreCase("Present")){
                        if(customerAddress.getFullAddress()!=null){
                            String fullCustAddress = customerAddress.getFullAddress();
                            custListingPojo.setCustomerAddress(fullCustAddress);
                        }
                    }
                }
            }
            if(customers.getCurrentAssigneeId()!=null){

                StaffUser parent = staffUserRepository.findParentByStaffUserId(customers.getCurrentAssigneeId());
                if (parent != null) {
                    custListingPojo.setCurrentAssigneeParentId(parent.getId());
                }
                //** change for improve perfomance

//                StaffUser staffUser = staffUserService.getRepository().findById(customers.getCurrentAssigneeId()).orElse(null);
//                if(staffUser!=null){
//                    if(staffUser.getStaffUserparent()!=null){
//                        custListingPojo.setCurrentAssigneeParentId(staffUser.getStaffUserparent().getId());
//                    }
//                }
            }



        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage());
            throw ex;
        }
    }


//    private Integer setNextApprover(Customers customers) {
//        QCustomerCafAssignment qCustomerCafAssignment = QCustomerCafAssignment.customerCafAssignment;
//        BooleanExpression booleanExpression = qCustomerCafAssignment.isNotNull().and(qCustomerCafAssignment.customersCaf.id.eq(customers.getId()));
//        CustomerCafAssignment customerCafAssignment = new CustomerCafAssignment();
//        customerCafAssignment = customerCafAssignmentRepository.findOne(booleanExpression).orElse(null);
//        if (Objects.nonNull(customerCafAssignment)) {
//            if (Objects.nonNull(customerCafAssignment.getStaffUser())) {
//                return customerCafAssignment.getStaffUser().getId();
//            } else {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }

/*    @Named("maskAadhar")
    String extractMaskAadhar(String string) throws Exception {
//        548005556155
        String lastFourDigits = string.substring(7);
        return "**** **** "+lastFourDigits;
    }*/

//    private Integer setNextApprover(Customers customers) {
//
//        QCustomerCafAssignment qCustomerCafAssignment = QCustomerCafAssignment.customerCafAssignment;
//        BooleanExpression booleanExpression = qCustomerCafAssignment.isNotNull().and(qCustomerCafAssignment.customersCaf.id.eq(customers.getId()));
//        CustomerCafAssignment customerCafAssignment = new CustomerCafAssignment();
//        customerCafAssignment = customerCafAssignmentRepository.findOne(booleanExpression).orElse(null);
//        if (Objects.nonNull(customerCafAssignment)) {
//            if (Objects.nonNull(customerCafAssignment.getStaffUser())) {
//                return customerCafAssignment.getStaffUser().getId();
//            } else {
//                return null;
//            }
//
//        } else {
//            return null;
//        }
//    }
}
