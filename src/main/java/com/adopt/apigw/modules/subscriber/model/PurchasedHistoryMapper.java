package com.adopt.apigw.modules.subscriber.model;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;

import java.text.DecimalFormat;

@Mapper
public abstract class PurchasedHistoryMapper implements IBaseMapper<PurchasedHistoryDTO, CustPlanMappping> {

    public static final String MODULE = " [PurchasedHistoryMapper] ";

    //  @Mapping(source = "customer", target = "custId")
    //@Mapping(source = "postpaidPlan", target = "planId")
    //@Mapping(source = "docnumber", target = "invoiceNo")
    @Mapping(source = "createdate", target = "purchesdate", dateFormat = "dd-MM-yyyy hh:mm a")
    //@Mapping(source = "totalamount", target = "amount")
    public abstract PurchasedHistoryDTO domainToDTO(CustPlanMappping data, @Context CycleAvoidingMappingContext context);

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private PostpaidPlanService planService;

    @AfterMapping
    public void loadPurchaseHistory(CustPlanMappping planMappping, @MappingTarget PurchasedHistoryDTO dto) {
        String SUBMODULE = MODULE + " [loadPurchaseHistory()] ";
        try {
            Long servId = null;
            if (null != planMappping) {
                if (null != planMappping.getPlanId()) {
                    PostpaidPlan plan = planService.get(planMappping.getPlanId(),planMappping.getCustomer().getMvnoId());
                    if (null != plan) {
                        dto.setPlanName(plan.getDisplayName());
                    } else {
                        dto.setPlanName("-");
                    }
                } else {
                    dto.setPlanName("-");
                }
                if (null != planMappping.getCustomer() && null != planMappping.getCustomer().getPartner()) {
                    String partnerId = planMappping.getCustomer().getPartner().getName();
                    dto.setPartnerName(partnerId);
                    dto.setCustId(planMappping.getCustomer().getId());
                } else {
                    dto.setPartnerName("-");
                }
                Double amount = (null != planMappping.getOfferPrice() ? planMappping.getOfferPrice() : 0.0) + (null != planMappping.getTaxAmount() ? planMappping.getTaxAmount() : 0.0);
                dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(amount)));
                dto.setId(planMappping.getDebitdocid());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
    }
}
