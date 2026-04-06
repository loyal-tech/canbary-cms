package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.mapper.postpaid.PartnerCommissionMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.CustomerDBR.domain.CustomerDBR;
import com.adopt.apigw.modules.CustomerDBR.repository.CustomerDBRRepository;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedger;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerLedgerGetDTO;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerLedgerDetailsRepository;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerLedgerRepository;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerDetailsService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerService;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookSlabDetails;
import com.adopt.apigw.modules.PriceGroup.domain.ServiceCommission;
import com.adopt.apigw.modules.subscriber.model.ChangePlanRequestDTO;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqDTO;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqModel;
import com.adopt.apigw.modules.xmlConversion.PaymentDetailsXml;
import com.adopt.apigw.pojo.api.ChangePlanRequestDTOList;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.pojo.api.PartnerCommissionPojo;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.adopt.apigw.utils.NumberSequenceUtil;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PartnerCommissionService extends AbstractService<PartnerCommission, PartnerCommissionPojo, Integer> {

    @Autowired
    private PartnerCommissionRepository entityRepository;
    @Autowired
    private PartnerCommissionMapper partnerCommissionMapper;
    @Autowired
    private PostpaidPlanService postpaidPlanService;
    @Autowired
    private PartnerLedgerDetailsService partnerLedgerDetailsService;
    @Autowired
    private PartnerLedgerService ledgerService;

    @Autowired
    private TaxService taxService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private TaxTypeTierService typeTierService;

    @Autowired
    private TaxTypeSlabService taxTypeSlabService;

    @Autowired
    private PartnerLedgerService partnerLedgerService;

    @Autowired
    TaxTypeTierRepository taxTypeTierRepository;

    @Autowired
    TaxTypeSlabRepository taxTypeSlabRepository;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;

    @Autowired
    PartnerLedgerRepository partnerLedgerRepository;

    @Autowired
    TempPartnerLedgerDetailsRepository tempPartnerLedgerDetailsRepository;

    @Autowired
    DbrService dbrService;

    @Autowired
    PartnerCreditDocumentRepository partnerCreditDocumentRepository;

    @Autowired
    PartnerCreditDocRepository partnerCreditDocRepository;

    @Autowired
    PartnerCreditDebitMappingRepository partnerCreditDebitMappingRepository;

    @Autowired
    PartnerCommissionRepository partnerCommissionRepository;

    @Autowired
    CustomerDBRRepository  customerDBRRepository;

    @Autowired
    PlanGroupMappingService planGroupMappingService;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    CreditDocService creditDocService;

    @Autowired
    CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    PlanGroupService planGroupService;;

    @Autowired
    PlanGroupMappingRepository planGroupMappingRepository;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;


    @Override
    protected JpaRepository<PartnerCommission, Integer> getRepository() {
        return entityRepository;
    }

    public void setPartnerCommission(Customers customer, Partner partner, Long validity, String requestFrom) throws Exception {

        if (customer != null && partner != null) {
            if (null != customer.getPlanMappingList() && 0 < customer.getPlanMappingList().size()) {

                if (null != partner.getPriceBookId() && null != partner.getPriceBookId().getPriceBookPlanDetailList() && 0 < partner.getPriceBookId().getPriceBookPlanDetailList().size()) {
                    PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
                    //PriceBookPlan List
                    List<PriceBookPlanDetail> priceBookPlanDetailList = partner.getPriceBookId().getPriceBookPlanDetailList();
                    Long newCustCount = null;
                    Long renewCustCount = null;
                    if (partner.getNewCustomerCount() == null)
                        newCustCount = 0L + 1;
                    else
                        newCustCount = partner.getNewCustomerCount() + 1;
                    if (partner.getRenewCustomerCount() == null)
                        renewCustCount = 0L;
                    else
                        renewCustCount = partner.getRenewCustomerCount();
                    final Long totalCustCount = newCustCount + renewCustCount;
                    partner.setNewCustomerCount(newCustCount);
                    partner.setRenewCustomerCount(renewCustCount);
                    partner.setTotalCustomerCount(totalCustCount);

                    for (CustPlanMappping planMappping : customer.getPlanMappingList()) {
                        if (null != planMappping.getPlanId()) {
                            PostpaidPlan plan = postpaidPlanService.get(planMappping.getPlanId(),customer.getMvnoId());
                            if (null != plan) {
                                List<PriceBookPlanDetail> tempList = priceBookPlanDetailList.stream().filter(data -> null != data.getPostpaidPlan() && planMappping.getPlanId().equals(data.getPostpaidPlan().getId())).collect(Collectors.toList());
                            }
                        }
                    }
                } else {
                    throw new DataNotFoundException("Partner does not have any PriceBook!");
                }
            }
        } else {
            throw new Exception("Insufficient data to calculate partner commission");
        }
    }


    public void setPartnerCommission(ChangePlanRequestDTO requestDTO, Customers customer, Partner partner, String requestFrom) throws Exception {
        if (customer != null && partner != null) {
            //  Long newCustCount = null;
            Long renewCustCount = null;
//  if (partner.getNewCustomerCount() == null)
            //                newCustCount = 0L + 1;
//            else
//                newCustCount = partner.getNewCustomerCount() + 1;
            if (partner.getRenewCustomerCount() == null)
                renewCustCount = 0L+1;
            else
                renewCustCount = partner.getRenewCustomerCount()+1;
            
            if(partner.getNewCustomerCount()==null)
                partner.setNewCustomerCount(0l);

            final Long totalCustCount = partner.getNewCustomerCount() + renewCustCount;
            //  partner.setNewCustomerCount(newCustCount);
            partner.setRenewCustomerCount(renewCustCount);
            partner.setTotalCustomerCount(totalCustCount);

            if (null != requestDTO.getPlanId()) {
                if (null != partner.getPriceBookId() && ((null != partner.getPriceBookId().getPriceBookPlanDetailList() && 0 < partner.getPriceBookId().getPriceBookPlanDetailList().size()) || partner.getPriceBookId().getIsAllPlanSelected() || partner.getPriceBookId().getIsAllPlanGroupSelected())) {
                    //PriceBookPlan List
                    List<PriceBookPlanDetail> priceBookPlanDetailList = partner.getPriceBookId().getPriceBookPlanDetailList();
                    if (null != requestDTO.getPlanId()) {
                        PostpaidPlan plan = postpaidPlanService.get(requestDTO.getPlanId(),customer.getMvnoId());
                        if (null != plan) {
                            List<PriceBookPlanDetail> tempList = priceBookPlanDetailList.stream().filter(data -> null != data.getPostpaidPlan() && requestDTO.getPlanId().equals(data.getPostpaidPlan().getId())).collect(Collectors.toList());
                        }
                    }
                } else {
                    throw new DataNotFoundException("Partner does not have any PriceBook!");
                }
            }
        } else {
            throw new Exception("Insufficient data to calculate partner commission");
        }
    }

    public List<PartnerCommissionPojo> getByTime(PartnerLedgerGetDTO dto) throws Exception {
        List<PartnerCommission> partnerCommissionList = new ArrayList<>();
        List<PartnerCommissionPojo> partnerCommissionPojoList = new ArrayList<>();
        if (dto.getSTART_DATE() != null && dto.getEND_DATE() != null) {
            partnerCommissionList = entityRepository.findAllByStartDateAndEndDateAndPartnerId(dto.getSTART_DATE(), dto.getEND_DATE(), dto.getPartner_id());
        } else {
            partnerCommissionList = entityRepository.findAllByPartnerId(dto.getPartner_id());
        }
        partnerCommissionPojoList = partnerCommissionList.stream().map(data -> partnerCommissionMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return partnerCommissionPojoList;
    }

    public List<PartnerCommissionPojo> getByPartnerId(Integer partnerId) throws Exception {
        List<PartnerCommission> partnerCommission = entityRepository.findAllByPartnerId(partnerId);
        List<PartnerCommissionPojo> partnerCommissionPojoList = partnerCommission.stream()
                .map(data -> partnerCommissionMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return partnerCommissionPojoList;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Partner Commission");
        List<PartnerCommissionPojo> partnerCommissionPojoList = entityRepository.findAll().stream()
                .map(data -> partnerCommissionMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, PartnerCommissionPojo.class, partnerCommissionPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<PartnerCommissionPojo> partnerCommissionPojoList = entityRepository.findAll().stream()
                .map(data -> partnerCommissionMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, PartnerCommissionPojo.class, partnerCommissionPojoList, null);
    }


    public void partnerCommissionForPrepaidCustomerCreation(Double grossOfferPrice,Double offerPrice, List<ItemCharge> list,Customers customers,StaffUser staffUser, Long invoiceId,Integer paymentStatusForFranCustomer) {
        Partner partner = customers.getPartner();
        if (partner != null && partner.getId() != CommonConstants.DEFAULT_PARTNER_ID) {
            if (partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN)) {
                List<PriceBookPlanDetail> priceBookPlanDetailList = partner.getPriceBookId().getPriceBookPlanDetailList();
                List<String> planIdList = list.stream().map(x -> x.getPlanid()).distinct().collect(Collectors.toList());
                planIdList.stream().forEach(planId -> {
                    PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                    List<PriceBookPlanDetail> tmpBookList = priceBookPlanDetailList.stream().filter(x -> x.getPostpaidPlan()!=null && x.getPostpaidPlan().getId().equals(Integer.parseInt(planId))).collect(Collectors.toList());
                    if(tmpBookList==null || (tmpBookList!=null && tmpBookList.isEmpty()))
                    {
                        tmpBookList = priceBookPlanDetailList.stream().filter(x ->{
                            if(x.planGroup!=null)
                            {
                                List<Integer> postpaidPlans=new ArrayList<>();
                                Integer planGroupId=x.planGroup.getPlanGroupId();
                                if(planGroupId!=null) {
                                    List<PlanGroupMapping> groupMappings=planGroupMappingRepository.findPlanGroupMappingByPlanGroupId(planGroupId,2);
                                    if(groupMappings!=null && !groupMappings.isEmpty())
                                        postpaidPlans=groupMappings.stream().map(y->y.getPlan().getId()).collect(Collectors.toList());
                                }

                                if(postpaidPlans!=null && !postpaidPlans.isEmpty())
                                {
                                    for(Integer mapping:postpaidPlans)
                                    {
                                        if(mapping.equals(Integer.parseInt(plan.getId().toString())))
                                            return true;
                                    }
                                    return false;
                                }
                                else
                                    return false;
                            }
                            else
                                return false;
                        }).collect(Collectors.toList());
                    }

                    if ((tmpBookList != null && tmpBookList.size() > 0) || partner.getPriceBookId().getIsAllPlanSelected() || partner.getPriceBookId().getIsAllPlanGroupSelected()) {
                        List<ItemCharge> chargesForSelectedPlan = list.stream().filter(data -> data.getPlanid().equalsIgnoreCase(planId)).collect(Collectors.toList());
                        Double totalTax = chargesForSelectedPlan.stream().mapToDouble(d -> d.getTax()).sum();
                        Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> (d.getPrice() - d.getDiscount())).sum();
                        Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                        Double basePriceExcludeAGR = baseOfferPrice - agr_tax;
                        Double partner_commission = null;
                        Double partnerTax=0d;
                        Double tds_tax = null;
                        List<PriceBookSlabDetails> priceBookSlabDetailsList = null;
                        Long customerCount = partner.getTotalCustomerCount();
                        customerCount = customerCount != null ? customerCount : 0;

                        if(partner.getPriceBookId().getIsAllPlanSelected() || partner.getPriceBookId().getIsAllPlanSelected())
                        {
                            if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN))
                                partner_commission = (basePriceExcludeAGR * Double.parseDouble(partner.getPriceBookId().getRevenueSharePercentage().toString())) / 100.00;
                            else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN)) {
                                Long finalCustomerCount = customerCount;
                                priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                                if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                    partner_commission = priceBookSlabDetailsList.get(0).getCommissionAmount();
                            }
                        }
                        if(tmpBookList!=null && tmpBookList.size()>0)
                        {
                            if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN))
                                partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenueSharePercentage())) / 100.00;
                            else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN)) {
                                Long finalCustomerCount = customerCount;
                                priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                                if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                    partner_commission = priceBookSlabDetailsList.get(0).getCommissionAmount();
                            }
                        }

                        Tax tax = dbrService.getTax(Integer.parseInt(partner.getTaxid().toString()));
                        partnerTax = dbrService.getTaxAmount(tax, partner_commission);

                        tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                        partner_commission = partner_commission + partnerTax - tds_tax;
                        addPartnerLedgerAndLedgerDetailAgainstCommission(grossOfferPrice,baseOfferPrice + totalTax, partner_commission, totalTax, agr_tax, tds_tax, null, customers, partner, plan, invoiceId, partnerTax,paymentStatusForFranCustomer,staffUser,0.0);
                    }
                });
            } else {
                List<ServiceCommission> serviceCommissionList = partner.getPriceBookId().getServiceCommissionList();
                List<String> planIdList = list.stream().map(x -> x.getPlanid()).distinct().collect(Collectors.toList());
                planIdList.stream().forEach(planId -> {
                    PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                    List<ServiceCommission> tmpBookList = serviceCommissionList.stream().filter(x -> x.getServiceId().equals(plan.getServiceId().longValue())).collect(Collectors.toList());
                    if (tmpBookList != null && tmpBookList.size() > 0) {
                        List<ItemCharge> chargesForSelectedPlan = list.stream().filter(data -> data.getPlanid().equalsIgnoreCase(planId)).collect(Collectors.toList());
                        Double totalTax = chargesForSelectedPlan.stream().mapToDouble(d -> d.getTax()).sum();
                        Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> (d.getPrice() - d.getDiscount())).sum();
                        Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                        Double basePriceExcludeAGR = baseOfferPrice - agr_tax;

                        Double partner_commission = null;
                        Double royaltyCommission=0d;
                        Double partnerTax=0d;
                        Double tds_tax = null;
                        Double royaltyBasePrice=0.0;

                        partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;


                        Double baseOfferPriceForRoyalty = chargesForSelectedPlan.stream().filter(x->x.getRoyaltyApply()).mapToDouble(d -> (d.getPrice()-d.getDiscount())).sum();
                        if(baseOfferPriceForRoyalty!=null && baseOfferPriceForRoyalty>0) {
                            Double agr_taxForRoyalty = (baseOfferPriceForRoyalty * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                            Double basePriceExcludeAGRForRoyalty = baseOfferPriceForRoyalty - agr_taxForRoyalty;
                            basePriceExcludeAGRForRoyalty=(basePriceExcludeAGRForRoyalty * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                            royaltyBasePrice=basePriceExcludeAGRForRoyalty;
                            if(tmpBookList.get(0).getRoyaltyPercentage()!=0)
                                royaltyCommission = (basePriceExcludeAGRForRoyalty * Double.parseDouble(String.valueOf(tmpBookList.get(0).getRoyaltyPercentage()))) / 100.00;
                        }

                        if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                            partner_commission -= royaltyCommission;
                        else
                            partner_commission -= royaltyCommission;

                        Tax tax = dbrService.getTax(Integer.parseInt(partner.getTaxid().toString()));
                        partnerTax = dbrService.getTaxAmount(tax, partner_commission);

                        tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                        partner_commission = partner_commission + partnerTax - tds_tax;
                        addPartnerLedgerAndLedgerDetailAgainstCommission(grossOfferPrice,baseOfferPrice + totalTax, partner_commission, totalTax, agr_tax, tds_tax, royaltyCommission, customers, partner, plan, invoiceId, partnerTax,paymentStatusForFranCustomer,staffUser,royaltyBasePrice);
                    }
                });
            }
        }
    }

    private void addPartnerLedgerAndLedgerDetailAgainstCommission(Double grossOfferPrice,Double offerPrice, Double partner_commission, Double totalTax, Double agr_tax, Double tds_tax, Double royalty, Customers customers, Partner partner, PostpaidPlan plan, Long invoiceId, Double partnerTax,Integer paymentStatusForFranCustomer,StaffUser staffUser,Double royaltyBasePrice) {

        Optional<DebitDocument> document=debitDocRepository.findById(invoiceId.intValue());
        if(document.isPresent()) {
            DecimalFormat df = new DecimalFormat("0.00");
            DebitDocument debitDocument=document.get();
            Double amount=debitDocument.getTotalamount();
            if(debitDocument.getAdjustedAmount()!=null)
                amount=debitDocument.getTotalamount()-debitDocument.getAdjustedAmount();
            amount=Double.parseDouble(df.format(amount));
            if ((customers.getLcoId() != null && (partner.getBalance() + (partner.getCredit() - partner.getCreditConsume()) - partner.getCommrelvalue()) >=partner_commission) || (paymentStatusForFranCustomer == 1 && customers.getLcoId() == null && customers.getIs_from_pwc() && (amount==0.0d || partner.getBalance() > 0 || (partner.getBalance() == 0 && partner.getCreditConsume().intValue() == 0))) || paymentStatusForFranCustomer.longValue()==1) {
                PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
                partnerLedgerDetails.setCustid(customers.getId());
                partnerLedgerDetails.setPlanid(plan.getId().toString());
                partnerLedgerDetails.setOfferprice(offerPrice);
                partnerLedgerDetails.setTax(totalTax);
                partnerLedgerDetails.setAgr_amount(agr_tax);
                partnerLedgerDetails.setTds_amount(tds_tax);
                partnerLedgerDetails.setCommission(partner_commission);
                partnerLedgerDetails.setAmount(0.00);
                partnerLedgerDetails.setPartner(partner);
                partnerLedgerDetails.setDebitDocId(invoiceId);
                partnerLedgerDetails.setGrossOfferPrice(grossOfferPrice);
                partnerLedgerDetails.setRoyalty(royalty);
                if (customers.getLcoId() == null)
                    partnerLedgerDetails.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                else {
                    partnerLedgerDetails.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
                    partnerLedgerDetails.setAmount(partner_commission);
                    partnerLedgerDetails.setCommission(0.0d);
                }
                partnerLedgerDetails.setTranscategory(CommonConstants.TRANS_CATEGORY_COMMISSION);
                partnerLedgerDetails.setDescription("Commission against creation of customer = " + customers.getFirstname() + " For PlanID = " + plan.getId());
                partnerLedgerDetails.setCreateDate(LocalDateTime.now());
                partnerLedgerDetails.setPartnerTax(partnerTax);
                partnerLedgerDetails.setRoyaltyBasePrice(royaltyBasePrice);
                partnerLedgerDetails = partnerLedgerDetailsRepository.save(partnerLedgerDetails);
                addPartnerLedgerEntryAgainstCommission(partner_commission, partner, customers);
            } else if (paymentStatusForFranCustomer == 4 || paymentStatusForFranCustomer == 3 || paymentStatusForFranCustomer == 2 || (customers.getLcoId() != null && (partner.getBalance() + (partner.getCredit() - partner.getCreditConsume()) - partner.getCommrelvalue()) < partner_commission) || (paymentStatusForFranCustomer == 1 && customers.getLcoId() == null && customers.getIs_from_pwc() && (partner.getBalance() == 0 && partner.getCreditConsume().intValue() > 0))) {
                TempPartnerLedgerDetail partnerLedgerDetails = new TempPartnerLedgerDetail();
                partnerLedgerDetails.setCustid(customers.getId());
                partnerLedgerDetails.setOfferprice(offerPrice);
                partnerLedgerDetails.setPlanid(plan.getId().toString());
                partnerLedgerDetails.setTax(totalTax);
                partnerLedgerDetails.setInvoice_id(invoiceId.toString());
                partnerLedgerDetails.setAgr_amount(agr_tax);
                partnerLedgerDetails.setTds_amount(tds_tax);
                partnerLedgerDetails.setCommission(partner_commission);
                partnerLedgerDetails.setGrossOfferPrice(grossOfferPrice);
                partnerLedgerDetails.setAmount(0.00);
                partnerLedgerDetails.setPartner(partner);
                partnerLedgerDetails.setRoyalty(royalty);
                if (customers.getLcoId() == null)
                    partnerLedgerDetails.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                else {
                    partnerLedgerDetails.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
                    partnerLedgerDetails.setAmount(partner_commission);
                }
                partnerLedgerDetails.setTranscategory(CommonConstants.TRANS_CATEGORY_COMMISSION);
                partnerLedgerDetails.setDebitDocId(invoiceId.longValue());
                partnerLedgerDetails.setDescription("Commission against creation of customer = " + customers.getFirstname() + " For PlanID = " + plan.getId());
                partnerLedgerDetails.setCREATE_DATE(LocalDateTime.now());
                partnerLedgerDetails.setPartnerTax(partnerTax);
                if (paymentStatusForFranCustomer == 2)
                    partnerLedgerDetails.setPaymentStatus(2);
                else if (paymentStatusForFranCustomer == 3)
                    partnerLedgerDetails.setPaymentStatus(3);
                else if (paymentStatusForFranCustomer == 4)
                    partnerLedgerDetails.setPaymentStatus(4);
                else if (paymentStatusForFranCustomer == 1 && customers.getLcoId() == null && customers.getIs_from_pwc() && (partner.getBalance() == 0 && partner.getCreditConsume().intValue() > 0))
                    partnerLedgerDetails.setPaymentStatus(5);
                else
                    partnerLedgerDetails.setPaymentStatus(6);

                partnerLedgerDetails.setRoyaltyBasePrice(royaltyBasePrice);
                partnerLedgerDetails.setStaffUserId(staffUser.getId());
                tempPartnerLedgerDetailsRepository.save(partnerLedgerDetails);
            }
        }
    }

    private void addPartnerLedgerEntryAgainstCommission(Double partner_commission, Partner partner, Customers customer) {
        if (partner.getCommissionShareType().equalsIgnoreCase("Revenue")) {
            PartnerCommission commission = new PartnerCommission();
            commission.setPartnerid(partner.getId());
            commission.setCustomerid(customer.getId());
            commission.setCommtype(partner.getCommissionShareType());
            commission.setCommval(Double.parseDouble(new DecimalFormat("##.##").format(partner_commission)));
            commission.setStatus(SubscriberConstants.STATUS_PENDING);
            commission.setBilldate(LocalDateTime.now());
            commission=partnerCommissionRepository.save(commission);
        }

        PartnerLedger partnerLedger = partnerLedgerRepository.findByPartner_Id(partner.getId());
        if (partnerLedger != null) {
            if (partnerLedger.getTotaldue() == null) {
                partnerLedger.setTotaldue(0.0);
            }

            partnerLedger.setTotaldue(Double.parseDouble(new DecimalFormat("##.####").format(partnerLedger.getTotaldue() + partner_commission)));
            partnerLedger.setUpdatedate(LocalDate.now());
            partnerLedger=partnerLedgerRepository.save(partnerLedger);

            if (partner.getCommissionShareType().equalsIgnoreCase("balance")) {
                Double b = partner.getBalance() + partner_commission;
                partner.setBalance(Double.parseDouble(new DecimalFormat("##.##").format(b)));
                partner=partnerRepository.save(partner);
            }
            if (partner.getCommissionShareType().equalsIgnoreCase("Revenue")) {
                //Double partnerCommRelValue = partner.getCommrelvalue();
                partner.setCommrelvalue(Double.parseDouble(new DecimalFormat("##.##").format(partner.getCommrelvalue()+partner_commission))); //+ partnerCommRelValue);
                partner=partnerRepository.save(partner);
            }
        }
    }

    public Integer checkAndUpdatePaymentAdjustmentAgainstInvoiceAmount(List<ItemCharge> items,Double totalInvoiceAmount, Customers customers,StaffUser staffUser,Long invoiceId) {
        try {
            String createFrom=null;
            if(items!=null && items.size()>0)
            {
                String cprId=items.get(0).getCustpackageid();
                if(cprId!=null)
                {
                    CustPlanMappping mappping=custPlanMappingRepository.findById(Integer.parseInt(cprId));
                    if(mappping!=null)
                    {
                        createFrom=mappping.getPurchaseFrom();
                    }
                }
            }
            Partner partner=customers.getPartner();
            Optional<DebitDocument> debitDocument=debitDocRepository.findById(invoiceId.intValue());
            DebitDocument document=null;
            if(debitDocument.isPresent())
            {
                document=debitDocument.get();
            }

            if(document.getAdjustedAmount()==null)
                document.setAdjustedAmount(0.0);

            totalInvoiceAmount=totalInvoiceAmount-document.getAdjustedAmount();

            if(createFrom!=null && createFrom.equalsIgnoreCase("admin") && document.getTotalamount().doubleValue() == document.getAdjustedAmount().doubleValue())
            {
                return 1;
            }
            else if(createFrom!=null && !createFrom.equalsIgnoreCase("admin") && customers.getLcoId()!=null)
            {
                return 1;
            }
            else if(createFrom!=null && !createFrom.equalsIgnoreCase("admin") && document.getTotalamount().doubleValue() == document.getAdjustedAmount().doubleValue())
            {

                updatePartnerBalanceAgainstInvoiceAmount(customers,document.getAdjustedAmount(),invoiceId);
                return 1;
            }
            else if ((customers != null && customers.getPartner().getId()!=CommonConstants.DEFAULT_PARTNER_ID && customers.getLcoId()==null && (createFrom!=null && !createFrom.equalsIgnoreCase("admin")) && (partner.getBalance()>0 && partner.getBalance() >= totalInvoiceAmount))) {
                if (adjustPaymentAgainstInvoiceAmount(customers, staffUser.getPartnerid(), totalInvoiceAmount, invoiceId, staffUser.getId(), staffUser)) {
                    updatePartnerBalanceAgainstInvoiceAmount(customers, totalInvoiceAmount,invoiceId);
                    return 1;
                }
            }
            else if(customers != null && customers.getPartner().getId()!=CommonConstants.DEFAULT_PARTNER_ID && customers.getLcoId()==null && ((createFrom!=null && !createFrom.equalsIgnoreCase("admin"))) && (partner.getBalance() < totalInvoiceAmount))
            {
                if (adjustPaymentAgainstInvoiceAmount(customers, staffUser.getPartnerid(), totalInvoiceAmount, invoiceId, staffUser.getId(), staffUser)) {
                    updatePartnerBalanceAgainstInvoiceAmount(customers, totalInvoiceAmount,invoiceId);
                    return 2;
                }
            }
            else if (customers != null && customers.getIs_from_pwc() && customers.getLcoId()==null && ((createFrom!=null && !createFrom.equalsIgnoreCase("admin"))) && (partner.getBalance()>0 && partner.getBalance() < totalInvoiceAmount))
                return 2;
            else if(customers != null && customers.getIs_from_pwc() && customers.getLcoId()==null && staffUser.getPartnerid().intValue()==CommonConstants.DEFAULT_PARTNER_ID)
                return 3;
            else if(customers != null && !customers.getIs_from_pwc() && customers.getPartner().getId()!=CommonConstants.DEFAULT_PARTNER_ID && staffUser.getPartnerid().intValue()==CommonConstants.DEFAULT_PARTNER_ID)
                return 4;
            return 5;
        }catch (Exception e){return 5;}
    }

    public void updatePartnerBalanceAgainstInvoiceAmount(Customers customers,Double totalInvoiceAmount,Long invoiceId)
    {
        Partner partner = customers.getPartner();
        if (partner != null && partner.getId() != CommonConstants.DEFAULT_PARTNER_ID) {
            Double amount=totalInvoiceAmount;
            if(customers.getLcoId()==null)
            {
                if (partner.getBalance() >= totalInvoiceAmount) {
                    amount=totalInvoiceAmount;
                    partner.setBalance(partner.getBalance() - totalInvoiceAmount);
                    partner=partnerRepository.save(partner);
                    addPartnerLedgerDetailAgainstInvoiceAmount(totalInvoiceAmount, customers, partner,invoiceId);
                }
                else if ((partner.getBalance() - totalInvoiceAmount) < 0) {
                    if(partner.getBalance() > 0) {
                        amount=partner.getBalance();
                        Double creditConsume = partner.getCreditConsume() + (totalInvoiceAmount - partner.getBalance());
                        partner.setCreditConsume(creditConsume);
                        partner.setBalance(0d);
                        addPartnerLedgerDetailAgainstInvoiceAmount(amount, customers, partner,invoiceId);
                    } else {
                        Double creditConsume = partner.getCreditConsume() + totalInvoiceAmount;
                        partner.setCreditConsume(creditConsume);
                    }
                    partnerRepository.save(partner);
                }
            }
        }
    }

    public boolean adjustPaymentAgainstInvoiceAmount(Customers customers,Integer requestFromPartnerId,Double totalInvoiceAmount,Long invoiceId,Integer loggedInUserId,StaffUser staffUser)
    {
        Optional<DebitDocument> document=debitDocRepository.findById(invoiceId.intValue());
        if(document.isPresent()) {
            CreditDocument creditDocument = new CreditDocument();
            creditDocument.setAdjustedAmount(0.0);
            if(customers.getPartner().getBalance()>totalInvoiceAmount)
                creditDocument.setAmount(totalInvoiceAmount);
            else
                creditDocument.setAmount(customers.getPartner().getBalance());
            creditDocument.setCustomer(customers);
            creditDocument.setStatus(UtilsCommon.PAYMENT_STATUS_APPROVED);
            creditDocument.setLcoid(customers.getLcoId());
            creditDocument.setPaymentdate(LocalDate.now());
            creditDocument.setType(UtilsCommon.PAYMENT_TYPE);
            creditDocument.setCreatedate(LocalDateTime.now());
            creditDocument.setIsDelete(false);
            creditDocument.setTdsflag(false);
            creditDocument.setPaydetails4("Received By Partner : "+customers.getPartner().getName());
            creditDocument.setPaytype(com.adopt.apigw.modules.subscriber.model.Constants.ADVANCE);
            creditDocument.setApproverid(loggedInUserId);
            creditDocument.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
            creditDocument.setPaymode(CommonConstants.PAYMENT_MODE_TYPE_CASH);
            creditDocument.setTds_received(false);
            creditDocument.setCreatedById(staffUser.getId());
            creditDocument.setCreatedByName(staffUser.getFullName());
            creditDocument.setMvnoId(staffUser.getMvnoId());
            creditDocument.setLastModifiedById(staffUser.getId());
            creditDocument.setLastModifiedByName(staffUser.getFullName());
//            creditDocument.setCreditdocumentno(creditDocService.getPaymentInvoiceNo());
            Boolean isLCO = customers.getLcoId() != null ? true :false;
            creditDocument.setCreditdocumentno(numberSequenceUtil.getPaymentNumber(isLCO, customers.getLcoId(), customers.getMvnoId()));
            DebitDocument debitDocument= debitDocRepository.findById(invoiceId.intValue()).get();
            if(creditDocument.getAdjustedAmount()>0.0d)
                creditDocument=creditDocRepository.save(creditDocument);
            creditDocument.setXmldocument(PaymentDetailsXml.getPaymentDetails(creditDocument, UtilsCommon.ADDR_TYPE_PRESENT,null,debitDocument));

            if(customers.getPartner().getBalance()>totalInvoiceAmount)
                creditDocument.setAdjustedAmount(totalInvoiceAmount);
            else
                creditDocument.setAdjustedAmount(customers.getPartner().getBalance());

            if(creditDocument.getAdjustedAmount()>0.0d)
                creditDocument=creditDocRepository.save(creditDocument);

            CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
            if(customers.getPartner().getBalance()>totalInvoiceAmount)
                creditDebitDocMapping.setAdjustedAmount(totalInvoiceAmount);
            else
                creditDebitDocMapping.setAdjustedAmount(customers.getPartner().getBalance());

            creditDebitDocMapping.setIsDeleted(false);
            creditDebitDocMapping.setDebtDocId(invoiceId.intValue());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());

            if(creditDocument.getAdjustedAmount()>0.0d)
                creditDebitDocMapping=creditDebtMappingRepository.save(creditDebitDocMapping);

            creditDocService.addLedgerAndLedgerDetailEntry(creditDocument,customers,false);
            if(customers.getPartner().getBalance()>totalInvoiceAmount) {
                if(debitDocument.getAdjustedAmount()==null)
                    debitDocument.setAdjustedAmount(totalInvoiceAmount);
                else
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + totalInvoiceAmount);
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
            }
            else {
                if(debitDocument.getAdjustedAmount()==null)
                    debitDocument.setAdjustedAmount(customers.getPartner().getBalance());
                else
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount()+customers.getPartner().getBalance());
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIAL_PENDING);
            }
            if(creditDocument.getAdjustedAmount()>0.0d)
                debitDocument=debitDocRepository.save(debitDocument);

            return true;
        }
        return false;
    }

    public void addPartnerLedgerDetailAgainstInvoiceAmount(Double offerPrice, Customers customers, Partner partner,Long invoiceId) {
        DebitDocument debitDocument;
        String planId = "";
        if(invoiceId != null){
            debitDocument = debitDocRepository.findById(invoiceId.intValue()).orElse(null);
            if(debitDocument != null){
                if(debitDocument.getPostpaidPlan()!=null)
                    planId = debitDocument.getPostpaidPlan().getId().toString();
            }
        }
        PartnerLedgerDetails details = new PartnerLedgerDetails();
        details.setCommission(Double.parseDouble(new DecimalFormat("##.##").format(0.0)));
        details.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
        details.setTranscategory(CommonConstants.TRANS_CATEGORY_CUST_CREATE);
        details.setDescription("Debit Against Customer Creation = " + customers.getFirstname());
        details.setPartner(partner);
        details.setCreateDate(LocalDateTime.now());
        details.setTds_amount(0.0);
        details.setAgr_amount(0.0);
        details.setTax(0.0);
        details.setPlanid(planId);
        details.setDebitDocId(invoiceId);
        details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(offerPrice)));
        details.setGrossOfferPrice(offerPrice);
        details.setCustid(customers.getId());
        details.setOfferprice(offerPrice);
        partnerLedgerDetailsRepository.save(details);
    }


    public void addPartnerLedgerDetailAgainstCommissionAmount(List<TempPartnerLedgerDetail> details) {
        if (details != null && details.size() > 0) {
            for (TempPartnerLedgerDetail tempPartnerLedgerDetail : details) {
                Customers customer = customersRepository.findById(tempPartnerLedgerDetail.getCustid()).orElse(null);
                if (customer != null) {
                    PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
                    partnerLedgerDetails.setCustid(tempPartnerLedgerDetail.getCustid());
                    partnerLedgerDetails.setOfferprice(tempPartnerLedgerDetail.getOfferprice());
                    partnerLedgerDetails.setTax(tempPartnerLedgerDetail.getTax());
                    partnerLedgerDetails.setAgr_amount(tempPartnerLedgerDetail.getAgr_amount());
                    partnerLedgerDetails.setTds_amount(tempPartnerLedgerDetail.getTds_amount());
                    partnerLedgerDetails.setCommission(tempPartnerLedgerDetail.getCommission());
                    partnerLedgerDetails.setAmount(tempPartnerLedgerDetail.getAmount());
                    partnerLedgerDetails.setPartner(tempPartnerLedgerDetail.getPartner());
                    partnerLedgerDetails.setRoyalty(tempPartnerLedgerDetail.getRoyalty());
                    partnerLedgerDetails.setPartnerTax(tempPartnerLedgerDetail.getPartnerTax());
                    partnerLedgerDetails.setGrossOfferPrice(tempPartnerLedgerDetail.getGrossOfferPrice());
                    partnerLedgerDetails.setTranstype(tempPartnerLedgerDetail.getTranstype());
                    partnerLedgerDetails.setTranscategory(tempPartnerLedgerDetail.getTranscategory());
                    partnerLedgerDetails.setDescription(tempPartnerLedgerDetail.getDescription());
                    partnerLedgerDetails.setDebitDocId(tempPartnerLedgerDetail.getInvoice_id() != null ? Long.parseLong(tempPartnerLedgerDetail.getInvoice_id()) : null);
                    partnerLedgerDetails.setCreateDate(LocalDateTime.now());
                    partnerLedgerDetails.setPlanid(tempPartnerLedgerDetail.getPlanid());
                    partnerLedgerDetails.setRoyalty(tempPartnerLedgerDetail.getRoyalty());
                    partnerLedgerDetails.setPartnerTax(tempPartnerLedgerDetail.getPartnerTax());
                    partnerLedgerDetails.setGrossOfferPrice(tempPartnerLedgerDetail.getGrossOfferPrice());
                    partnerLedgerDetails.setRoyaltyBasePrice(tempPartnerLedgerDetail.getRoyaltyBasePrice());
                    partnerLedgerDetailsRepository.save(partnerLedgerDetails);
                    addPartnerLedgerEntryAgainstCommission(tempPartnerLedgerDetail.getCommission(), tempPartnerLedgerDetail.getPartner(), customer);
                }
            }
        }
    }

    public void partnerCommissionForPostpaidCustomerCreation(Double grossOfferPrice,Double offerPrice, List<PostpaidItemCharge> list, Integer custId, Long invoiceId)
    {
        Customers customers=customersRepository.findById(custId).get();
        Partner partner=customers.getPartner();
        if(partner!=null && partner.getId()!=CommonConstants.DEFAULT_PARTNER_ID) {
            if (partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN)) {
                List<PriceBookPlanDetail> priceBookPlanDetailList = partner.getPriceBookId().getPriceBookPlanDetailList();
                List<String> planIdList = list.stream().map(x -> x.getPlanid()).distinct().collect(Collectors.toList());
                planIdList.stream().forEach(planId -> {
                    PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                    List<PriceBookPlanDetail> tmpBookList = priceBookPlanDetailList.stream().filter(x -> x.getPostpaidPlan().getId().equals(Integer.parseInt(planId))).collect(Collectors.toList());
                    if (tmpBookList != null && tmpBookList.size() > 0) {
                        List<PostpaidItemCharge> chargesForSelectedPlan = list.stream().filter(data -> data.getPlanid().equalsIgnoreCase(planId)).collect(Collectors.toList());
                        Double totalTax = chargesForSelectedPlan.stream().mapToDouble(d -> d.getTax()).sum();
                        Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> d.getPrice()).sum();
                        Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                        Double basePriceExcludeAGR = baseOfferPrice - agr_tax;
                        Double partner_commission = null;
                        Double tds_tax = null;
                        List<PriceBookSlabDetails> priceBookSlabDetailsList = null;
                        Long customerCount = partner.getTotalCustomerCount();
                        customerCount = customerCount != null ? customerCount : 0;

                        if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN))
                            partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenueSharePercentage())) / 100.00;
                        else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_SERVICE)) {
                            List<ServiceCommission> commissionList = partner.getPriceBookId().getServiceCommissionList().stream().filter(x -> x.getServiceId().equals(tmpBookList.get(0).getPostpaidPlan().getServiceId().longValue())).collect(Collectors.toList());
                            if (commissionList != null && !commissionList.isEmpty())
                                partner_commission = (basePriceExcludeAGR * Double.parseDouble(commissionList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                        } else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN)) {
                            Long finalCustomerCount = customerCount;
                            priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                            if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                partner_commission = priceBookSlabDetailsList.get(0).getCommissionAmount();
                        }

                        tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                        partner_commission = partner_commission - tds_tax;
                        //addPartnerLedgerAndLedgerDetailAgainstCommission(grossOfferPrice,baseOfferPrice + totalTax, partner_commission, totalTax, agr_tax, tds_tax,null, customers, partner, plan, invoiceId, 0d,1);
                    }
                });
            }
            else
            {
                List<ServiceCommission> serviceCommissionList = partner.getPriceBookId().getServiceCommissionList();
                List<String> planIdList = list.stream().map(x -> x.getPlanid()).distinct().collect(Collectors.toList());
                planIdList.stream().forEach(planId -> {
                    PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                    List<ServiceCommission> tmpBookList = serviceCommissionList.stream().filter(x -> x.getServiceId().equals(plan.getServiceId().longValue())).collect(Collectors.toList());
                    if (tmpBookList != null && tmpBookList.size() > 0) {
                        List<PostpaidItemCharge> chargesForSelectedPlan = list.stream().filter(data -> data.getPlanid().equalsIgnoreCase(planId)).collect(Collectors.toList());
                        Double totalTax = chargesForSelectedPlan.stream().mapToDouble(d -> d.getTax()).sum();
                        Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> (d.getPrice() - d.getDiscount())).sum();
                        Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                        Double basePriceExcludeAGR = baseOfferPrice - agr_tax;

                        Double partner_commission = null;
                        Double royaltyCommission=0d;
                        Double partnerTax=0d;
                        Double tds_tax = null;

                        partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;


                        Double baseOfferPriceForRoyalty = chargesForSelectedPlan.stream().filter(x->x.getRoyaltyApply()).mapToDouble(d -> (d.getPrice()-d.getDiscount())).sum();
                        if(baseOfferPriceForRoyalty!=null && baseOfferPriceForRoyalty>0) {
                            Double agr_taxForRoyalty = (baseOfferPriceForRoyalty * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                            Double basePriceExcludeAGRForRoyalty = baseOfferPriceForRoyalty - agr_taxForRoyalty;
                            basePriceExcludeAGRForRoyalty=(basePriceExcludeAGRForRoyalty * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                            if(tmpBookList.get(0).getRoyaltyPercentage()!=0)
                                royaltyCommission = (basePriceExcludeAGRForRoyalty * Double.parseDouble(String.valueOf(tmpBookList.get(0).getRoyaltyPercentage()))) / 100.00;
                        }

                        if(customers.getIs_from_pwc() && customers.getLcoId()!=null)
                            partner_commission+=royaltyCommission;
                        else
                            partner_commission-=royaltyCommission;

                        Tax tax = dbrService.getTax(Integer.parseInt(partner.getTaxid().toString()));
                        partnerTax = dbrService.getTaxAmount(tax, partner_commission);

                        tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                        partner_commission = partner_commission + partnerTax - tds_tax;
                        //addPartnerLedgerAndLedgerDetailAgainstCommission(grossOfferPrice,baseOfferPrice + totalTax, partner_commission, totalTax, agr_tax, tds_tax,royaltyCommission, customers, partner, plan, invoiceId, partnerTax,1);
                    }
                });
            }
        }
    }

    public double getCommissionAmount(CustomersPojo customer,Partner partner) {
        Double partner_commission = 0d;

        if (partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN))
        {
            if (null != customer.getPlanMappingList() && 0 < customer.getPlanMappingList().size())
            {
                List<PriceBookPlanDetail> priceBookPlanDetailList = partner.getPriceBookId().getPriceBookPlanDetailList();
                for (CustPlanMapppingPojo planMappping : customer.getPlanMappingList())
                {
                    if (null != planMappping.getPlanId())
                    {
                        PostpaidPlan plan = postpaidPlanRepo.findById(planMappping.getPlanId()).get();
                        List<PriceBookPlanDetail> tmpBookList = priceBookPlanDetailList.stream().filter(x -> x.getPostpaidPlan()!=null && x.getPostpaidPlan().getId().equals(Integer.parseInt(plan.getId().toString()))).collect(Collectors.toList());
                        if(tmpBookList==null || (tmpBookList!=null && tmpBookList.isEmpty()))
                        {
                            tmpBookList = priceBookPlanDetailList.stream().filter(x ->{
                                if(x.planGroup!=null)
                                {
                                    List<Integer> postpaidPlans=new ArrayList<>();
                                    Integer planGroupId=x.planGroup.getPlanGroupId();
                                    if(planGroupId!=null) {
                                        List<PlanGroupMapping> groupMappings=planGroupMappingRepository.findPlanGroupMappingByPlanGroupId(planGroupId,2);
                                        if(groupMappings!=null && !groupMappings.isEmpty())
                                        {
                                            postpaidPlans=groupMappings.stream().map(y->y.getPlan().getId()).collect(Collectors.toList());
                                        }
                                    }


                                    if(postpaidPlans!=null && !postpaidPlans.isEmpty())
                                    {
                                        for(Integer mapping:postpaidPlans)
                                        {
                                            if(mapping.equals(Integer.parseInt(plan.getId().toString())))
                                                return true;
                                        }
                                        return false;
                                    }
                                    else
                                        return false;
                                }
                                else
                                    return false;
                            }).collect(Collectors.toList());
                        }
                        if (tmpBookList != null && tmpBookList.size() > 0)
                        {
                            List<PostpaidPlanCharge> chargesForSelectedPlan = plan.getChargeList();

                            Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> d.getCharge().getActualprice()).sum();
                            Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                            Double basePriceExcludeAGR = baseOfferPrice - agr_tax;
                            Double tds_tax = null;
                            Double partnerTax=0d;
                            List<PriceBookSlabDetails> priceBookSlabDetailsList = null;
                            Long customerCount = partner.getTotalCustomerCount();
                            customerCount = customerCount != null ? customerCount : 0;

                            if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN))
                                partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenueSharePercentage())) / 100.00;
                            else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_SERVICE)) {
                                List<PriceBookPlanDetail> finalTmpBookList = tmpBookList;
                                List<ServiceCommission> commissionList = partner.getPriceBookId().getServiceCommissionList().stream().filter(x -> x.getServiceId().equals(finalTmpBookList.get(0).getPostpaidPlan().getServiceId().longValue())).collect(Collectors.toList());
                                if (commissionList != null && !commissionList.isEmpty())
                                    partner_commission = (basePriceExcludeAGR * Double.parseDouble(commissionList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                            } else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN)) {
                                Long finalCustomerCount = customerCount;
                                priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                                if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                    partner_commission = priceBookSlabDetailsList.get(0).getCommissionAmount();
                            }

                            Tax tax = dbrService.getTax(Integer.parseInt(partner.getTaxid().toString()));
                            partnerTax = dbrService.getTaxAmount(tax, partner_commission);

                            tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                            partner_commission = partner_commission + partnerTax - tds_tax;
                        }
                    }
                }
            }
        }
        else {
            if (null != customer.getPlanMappingList() && 0 < customer.getPlanMappingList().size()) {
                List<ServiceCommission> serviceCommissionList = partner.getPriceBookId().getServiceCommissionList();
                for (CustPlanMapppingPojo planMappping : customer.getPlanMappingList()) {
                    if (null != planMappping.getPlanId()) {
                        PostpaidPlan plan = postpaidPlanRepo.findById(planMappping.getPlanId()).get();
                        if (plan != null) {
                            List<ServiceCommission> tmpBookList = serviceCommissionList.stream().filter(x -> x.getServiceId().equals(plan.getServiceId().longValue())).collect(Collectors.toList());
                            if (tmpBookList != null && tmpBookList.size() > 0) {
                                List<PostpaidPlanCharge> chargesForSelectedPlan = plan.getChargeList();

                                Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> d.getCharge().getActualprice()).sum();
                                Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                                Double basePriceExcludeAGR = baseOfferPrice - agr_tax;

                                Double royaltyCommission=0d;
                                Double partnerTax=0d;
                                Double tds_tax = null;

                                partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;

                                Double baseOfferPriceForRoyalty = chargesForSelectedPlan.stream().filter(x->x.getCharge().getRoyalty_payable()!=null && x.getCharge().getRoyalty_payable()).mapToDouble(d -> d.getCharge().getActualprice()).sum();
                                if(baseOfferPriceForRoyalty!=null && baseOfferPriceForRoyalty>0) {
                                    Double agr_taxForRoyalty = (baseOfferPriceForRoyalty * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                                    Double basePriceExcludeAGRForRoyalty = baseOfferPriceForRoyalty - agr_taxForRoyalty;
                                    basePriceExcludeAGRForRoyalty=(basePriceExcludeAGRForRoyalty * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                                    royaltyCommission = (basePriceExcludeAGRForRoyalty * Double.parseDouble(String.valueOf(tmpBookList.get(0).getRoyaltyPercentage()))) / 100.00;
                                }

                                partner_commission += royaltyCommission;

                                Tax tax = dbrService.getTax(Integer.parseInt(partner.getTaxid().toString()));
                                partnerTax = dbrService.getTaxAmount(tax, partner_commission);

                                tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                                partner_commission = partner_commission + partnerTax - tds_tax;
                            }
                        }
                    }
                }
            }
        }
        return partner_commission;
    }

    public void adjustInvoiceAmount(Double totalInvoiceAmount, Long invoiceId)
    {
        if(invoiceId!=null)
        {
            Optional<PartnerDebitDocument> partnerDebitDocument=partnerCreditDocRepository.findById(invoiceId.intValue());
            if(partnerDebitDocument.isPresent())
            {
                PartnerDebitDocument document=partnerDebitDocument.get();
                Partner partner=document.getPartner();
                addDbrEntry(partnerDebitDocument.get(),totalInvoiceAmount);
                if(partner.getBalance()>=totalInvoiceAmount)
                {
                    updatePartnerCreditDebitDoc(invoiceId,totalInvoiceAmount,partner,document);
                    updatePartnerLedgerAndDetails(partner,document,invoiceId,totalInvoiceAmount);
                }
            }
        }
    }

    private void addDbrEntry(PartnerDebitDocument partnerDebitDocument, Double totalInvoiceAmount) {
        CustomerDBR dbr = new CustomerDBR();
        dbr.setInvoiceId(partnerDebitDocument.getId().longValue());
        dbr.setCustid(null);
        dbr.setStartdate(LocalDate.now());
        dbr.setEnddate(LocalDate.now());
        dbr.setDbr(totalInvoiceAmount);
        dbr.setPendingamt(0.0);
        dbr.setCustname(null);
        dbr.setOffer_price(totalInvoiceAmount);
        dbr.setPartnerId(partnerDebitDocument.getPartner().getId().longValue());
        dbr.setStatus("Active");
        dbr.setCusttype(null);
        dbr.setIsDirectCharge(false);
        dbr.setCumm_revenue(totalInvoiceAmount);
        customerDBRRepository.save(dbr);
    }

    private void updatePartnerLedgerAndDetails(Partner partner, PartnerDebitDocument document, Long invoiceId, Double totalInvoiceAmount) {

        PartnerLedger partnerLedger=partnerLedgerRepository.findByPartner_Id(partner.getId());
        if(partnerLedger!=null)
        {
            PartnerLedgerDetails details=new PartnerLedgerDetails();
            details.setCommission(Double.parseDouble(new DecimalFormat("##.##").format(0.0)));
            details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(totalInvoiceAmount)));
            details.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
            details.setTranscategory(CommonConstants.TRANS_CATEGORY_CUST_CREATE);
            details.setDescription("Debit Against Partner Invoice Creation for partnerId = " + partner.getId());
            details.setPartner(partner);
            details.setCreateDate(LocalDateTime.now());
            details.setTds_amount(0.0);
            details.setAgr_amount(0.0);
            details.setTax(0.0);
            details.setPartnerTax(0.0);
            details.setGrossOfferPrice(0.0);
            details.setCustid(null);
            details.setOfferprice(0.0);
            details=partnerLedgerDetailsRepository.save(details);

            details.setId(null);
            details.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            details.setTranscategory(CommonConstants.TRANS_CATEGORY_CUST_CREATE);
            details.setDescription("Credit Against Partner Invoice Creation for partnerId = " + partner.getId());
            details=partnerLedgerDetailsRepository.save(details);

            partner.setBalance(partner.getBalance()-totalInvoiceAmount);
            document.setAdjustedamount(document.getAdjustedamount()+totalInvoiceAmount);

            partnerLedger.setTotaldue(partnerLedger.getTotaldue()-totalInvoiceAmount);
            partnerLedger.setTotalpaid(partnerLedger.getTotalpaid()+totalInvoiceAmount);
            partnerLedger=partnerLedgerRepository.save(partnerLedger);

            partner.setCommrelvalue(partner.getCommrelvalue()-totalInvoiceAmount);
            partner=partnerRepository.save(partner);
        }
    }

    private void updatePartnerCreditDebitDoc(Long invoiceId, Double totalInvoiceAmount, Partner partner, PartnerDebitDocument document) {

        PartnerCreditDocument creditDocument=new PartnerCreditDocument();
        creditDocument.setAdjustedAmount(totalInvoiceAmount);
        creditDocument.setPaydetails4("Payment Received By Organization");
        creditDocument.setType("Payment");
        creditDocument.setPaytype("invoice");
        creditDocument.setStatus("Fully Adjusted");
        creditDocument.setCreateDate(LocalDateTime.now());
        creditDocument.setPaymode(CommonConstants.PAYMENT_MODE_TYPE_ONLINE);
        creditDocument.setInvoiceId(invoiceId.intValue());
        creditDocument.setIsDelete(false);
        creditDocument.setLcoid(partner.getId());
        creditDocument.setPaymentdate(LocalDate.now());
        creditDocument.setAmount(totalInvoiceAmount);
        creditDocument=partnerCreditDocumentRepository.save(creditDocument);

        if(document.getAdjustedamount()!=null)
            document.setAdjustedamount(document.getAdjustedamount()+totalInvoiceAmount);
        else
            document.setAdjustedamount(totalInvoiceAmount);
        document=partnerCreditDocRepository.save(document);

        PartnerCreditDebitMapping creditDebitDocMapping=new PartnerCreditDebitMapping();
        creditDebitDocMapping.setAdjustedAmount(totalInvoiceAmount);
        creditDebitDocMapping.setDebtDocId(invoiceId.intValue());
        creditDebitDocMapping.setCreditDocId(creditDocument.getId());
        creditDebitDocMapping.setIsDeleted(false);
        creditDebitDocMapping=partnerCreditDebitMappingRepository.save(creditDebitDocMapping);
    }

    public double getCommissionAmount(ChangePlanRequestDTOList requestDTOs, Partner partner) {
        Double partner_commission = 0d;
        if (null != requestDTOs.getChangePlanRequestDTOList() && 0 < requestDTOs.getChangePlanRequestDTOList().size())
        {
            List<ServiceCommission> serviceCommissionList = partner.getPriceBookId().getServiceCommissionList();
            for(ChangePlanRequestDTO requestDTO:requestDTOs.getChangePlanRequestDTOList())
            {
                if(requestDTO.getPlanId()!=null)
                {
                    PostpaidPlan plan = postpaidPlanRepo.findById(requestDTO.getPlanId()).get();
                    if (plan != null)
                    {
                        List<ServiceCommission> tmpBookList = serviceCommissionList.stream().filter(x -> x.getServiceId().equals(plan.getServiceId().longValue())).collect(Collectors.toList());
                        if (tmpBookList != null && tmpBookList.size() > 0) {
                            List<PostpaidPlanCharge> chargesForSelectedPlan = plan.getChargeList();

                            Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> d.getCharge().getActualprice()).sum();
                            Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                            Double basePriceExcludeAGR = baseOfferPrice - agr_tax;

                            Double royaltyCommission=0d;
                            Double partnerTax=0d;
                            Double tds_tax = null;

                            partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;

                            Double baseOfferPriceForRoyalty = chargesForSelectedPlan.stream().filter(x->x.getCharge().getRoyalty_payable()!=null && x.getCharge().getRoyalty_payable()).mapToDouble(d -> d.getCharge().getActualprice()).sum();
                            if(baseOfferPriceForRoyalty!=null && baseOfferPriceForRoyalty>0) {
                                Double agr_taxForRoyalty = (baseOfferPriceForRoyalty * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                                Double basePriceExcludeAGRForRoyalty = baseOfferPriceForRoyalty - agr_taxForRoyalty;
                                basePriceExcludeAGRForRoyalty=(basePriceExcludeAGRForRoyalty * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                                royaltyCommission = (basePriceExcludeAGRForRoyalty * Double.parseDouble(String.valueOf(tmpBookList.get(0).getRoyaltyPercentage()))) / 100.00;
                            }

                            partner_commission += royaltyCommission;

                            Tax tax = dbrService.getTax(Integer.parseInt(partner.getTaxid().toString()));
                            partnerTax = dbrService.getTaxAmount(tax, partner_commission);

                            tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                            partner_commission = partner_commission + partnerTax - tds_tax;
                        }
                    }
                }

                if(requestDTO.getPlanGroupId()!=null)
                {
                    Integer groupId=requestDTOs.getChangePlanRequestDTOList().get(0).getPlanGroupId();
                    // TODO: pass mvnoID manually 6/5/2025
                    List<PlanGroupMapping> planGroupMappingList = planGroupMappingService.findPlanGroupMappingByPlanGroupId(groupId, getMvnoIdFromCurrentStaff(null));
                    for(int j=0;j<planGroupMappingList.size();j++)
                    {
                        PostpaidPlan plan = planGroupMappingList.get(j).getPlan();
                        if (plan != null)
                        {
                            List<ServiceCommission> tmpBookList = serviceCommissionList.stream().filter(x -> x.getServiceId().equals(plan.getServiceId().longValue())).collect(Collectors.toList());
                            if (tmpBookList != null && tmpBookList.size() > 0) {
                                List<PostpaidPlanCharge> chargesForSelectedPlan = plan.getChargeList();

                                Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> d.getCharge().getActualprice()).sum();
                                Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                                Double basePriceExcludeAGR = baseOfferPrice - agr_tax;

                                Double royaltyCommission=0d;
                                Double partnerTax=0d;
                                Double tds_tax = null;

                                partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;

                                Double baseOfferPriceForRoyalty = chargesForSelectedPlan.stream().filter(x->x.getCharge().getRoyalty_payable()!=null && x.getCharge().getRoyalty_payable()).mapToDouble(d -> d.getCharge().getActualprice()).sum();
                                if(baseOfferPriceForRoyalty!=null && baseOfferPriceForRoyalty>0) {
                                    Double agr_taxForRoyalty = (baseOfferPriceForRoyalty * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                                    Double basePriceExcludeAGRForRoyalty = baseOfferPriceForRoyalty - agr_taxForRoyalty;
                                    basePriceExcludeAGRForRoyalty=(basePriceExcludeAGRForRoyalty * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                                    royaltyCommission = (basePriceExcludeAGRForRoyalty * Double.parseDouble(String.valueOf(tmpBookList.get(0).getRoyaltyPercentage()))) / 100.00;
                                }

                                partner_commission += royaltyCommission;

                                Tax tax = dbrService.getTax(Integer.parseInt(partner.getTaxid().toString()));
                                partnerTax = dbrService.getTaxAmount(tax, partner_commission);

                                tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                                partner_commission = partner_commission + partnerTax - tds_tax;
                            }
                        }
                    }
                }
            }
        }
        return partner_commission;
    }

    public double getCommissionAmount(DeactivatePlanReqDTO requestDTOs, Partner partner) {
        Double partner_commission = 0d;
        if (null != requestDTOs.getDeactivatePlanReqModels() && 0 < requestDTOs.getDeactivatePlanReqModels().size())
        {
            List<ServiceCommission> serviceCommissionList = partner.getPriceBookId().getServiceCommissionList();
            for(DeactivatePlanReqModel requestDTO:requestDTOs.getDeactivatePlanReqModels()) {
                if (requestDTO.getPlanId() != null) {
                    PostpaidPlan plan = postpaidPlanRepo.findById(requestDTO.getNewPlanId()).get();
                    if (plan != null) {
                        List<ServiceCommission> tmpBookList = serviceCommissionList.stream().filter(x -> x.getServiceId().equals(plan.getServiceId().longValue())).collect(Collectors.toList());
                        if (tmpBookList != null && tmpBookList.size() > 0) {
                            List<PostpaidPlanCharge> chargesForSelectedPlan = plan.getChargeList();

                            Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> d.getCharge().getActualprice()).sum();
                            Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                            Double basePriceExcludeAGR = baseOfferPrice - agr_tax;

                            Double royaltyCommission = 0d;
                            Double partnerTax = 0d;
                            Double tds_tax = null;

                            partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;

                            Double baseOfferPriceForRoyalty = chargesForSelectedPlan.stream().filter(x -> x.getCharge().getRoyalty_payable() != null && x.getCharge().getRoyalty_payable()).mapToDouble(d -> d.getCharge().getActualprice()).sum();
                            if (baseOfferPriceForRoyalty != null && baseOfferPriceForRoyalty > 0) {
                                Double agr_taxForRoyalty = (baseOfferPriceForRoyalty * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                                Double basePriceExcludeAGRForRoyalty = baseOfferPriceForRoyalty - agr_taxForRoyalty;
                                basePriceExcludeAGRForRoyalty = (basePriceExcludeAGRForRoyalty * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                                royaltyCommission = (basePriceExcludeAGRForRoyalty * Double.parseDouble(String.valueOf(tmpBookList.get(0).getRoyaltyPercentage()))) / 100.00;
                            }

                            partner_commission += royaltyCommission;

                            Tax tax = dbrService.getTax(Integer.parseInt(partner.getTaxid().toString()));
                            partnerTax = dbrService.getTaxAmount(tax, partner_commission);

                            tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                            partner_commission = partner_commission + partnerTax - tds_tax;
                        }
                    }
                }
            }
        }
        return partner_commission;
    }


    public void revertPartnerCommission(String planId,Double grossOfferPrice,Double baseOfferPrice,Double offerPrice,Double totalTax,Customers customers, Long invoiceId) {
        Partner partner = customers.getPartner();
        if (partner != null)
        {
            if (partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN))
            {
                List<PriceBookPlanDetail> priceBookPlanDetailList = partner.getPriceBookId().getPriceBookPlanDetailList();
                List<PriceBookPlanDetail> tmpBookList = priceBookPlanDetailList.stream().filter(x -> x.getPostpaidPlan().getId().equals(Integer.parseInt(planId))).collect(Collectors.toList());
                if (tmpBookList != null && tmpBookList.size() > 0)
                {
                        Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                        Double basePriceExcludeAGR = baseOfferPrice - agr_tax;
                        Double partner_commission = null;
                        Double partnerTax=0d;
                        Double tds_tax = null;
                        List<PriceBookSlabDetails> priceBookSlabDetailsList = null;
                        Long customerCount = partner.getTotalCustomerCount();
                        customerCount = customerCount != null ? customerCount : 0;

                        if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN))
                            partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenueSharePercentage())) / 100.00;
                        else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_SERVICE)) {
                            List<ServiceCommission> commissionList = partner.getPriceBookId().getServiceCommissionList().stream().filter(x -> x.getServiceId().equals(tmpBookList.get(0).getPostpaidPlan().getServiceId().longValue())).collect(Collectors.toList());
                            if (commissionList != null && !commissionList.isEmpty())
                                partner_commission = (basePriceExcludeAGR * Double.parseDouble(commissionList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                        } else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN)) {
                            Long finalCustomerCount = customerCount;
                            priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                            if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                partner_commission = priceBookSlabDetailsList.get(0).getCommissionAmount();
                        }

                        Tax tax = dbrService.getTax(Integer.parseInt(partner.getTaxid().toString()));
                        partnerTax = dbrService.getTaxAmount(tax, partner_commission);

                        tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                        partner_commission = partner_commission - tds_tax;
                        addRevertPartnerLedgerDetailAgainstCommission(grossOfferPrice,baseOfferPrice + totalTax, partner_commission, totalTax, agr_tax, tds_tax, null, customers, partner, planId, invoiceId, partnerTax);
                }
            }
            else
            {
                List<ServiceCommission> serviceCommissionList = partner.getPriceBookId().getServiceCommissionList();
                PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                List<ServiceCommission> tmpBookList = serviceCommissionList.stream().filter(x -> x.getServiceId().equals(plan.getServiceId().longValue())).collect(Collectors.toList());
                if (tmpBookList != null && tmpBookList.size() > 0) {
                    Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                    Double basePriceExcludeAGR = baseOfferPrice - agr_tax;

                    Double partner_commission = null;
                    Double royaltyCommission=0d;
                    Double partnerTax=0d;
                    Double tds_tax = null;

                    partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;


                    if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                        partner_commission += royaltyCommission;
                    else
                        partner_commission -= royaltyCommission;

                    Tax tax = dbrService.getTax(Integer.parseInt(partner.getTaxid().toString()));
                    partnerTax = dbrService.getTaxAmount(tax, partner_commission);

                    tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                    partner_commission = partner_commission + partnerTax - tds_tax;
                    addRevertPartnerLedgerDetailAgainstCommission(grossOfferPrice,baseOfferPrice + totalTax, partner_commission, totalTax, agr_tax, tds_tax, null, customers, partner, planId, invoiceId, partnerTax);
                }
            }
        }
    }


    public void addRevertPartnerLedgerDetailAgainstCommission(Double grossOfferPrice,Double offerPrice, Double partner_commission, Double totalTax, Double agr_tax, Double tds_tax, Double royalty, Customers customers, Partner partner,String planId, Long invoiceId, Double partnerTax) {

        Optional<DebitDocument> document=debitDocRepository.findById(invoiceId.intValue());
        if(document.isPresent()) {
            DecimalFormat df = new DecimalFormat("0.00");
            PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
            partnerLedgerDetails.setCustid(customers.getId());
            partnerLedgerDetails.setOfferprice(offerPrice);
            partnerLedgerDetails.setTax(totalTax);
            partnerLedgerDetails.setAgr_amount(agr_tax);
            partnerLedgerDetails.setTds_amount(tds_tax);
            partnerLedgerDetails.setCommission(partner_commission);
            partnerLedgerDetails.setAmount(0.00);
            partnerLedgerDetails.setPartner(partner);
            partnerLedgerDetails.setDebitDocId(invoiceId);
            partnerLedgerDetails.setGrossOfferPrice(grossOfferPrice);
            partnerLedgerDetails.setRoyalty(royalty);
            partnerLedgerDetails.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
            partnerLedgerDetails.setTranscategory("Revert Commission");
            partnerLedgerDetails.setDescription("Revert Commission against creation of customer = " + customers.getFirstname() + " For PlanID = " + planId);
            partnerLedgerDetails.setCreateDate(LocalDateTime.now());
            partnerLedgerDetails.setPartnerTax(partnerTax);
            partnerLedgerDetails = partnerLedgerDetailsRepository.save(partnerLedgerDetails);
        }
    }

    public void transferCommissionFromOnePartnerToAnotherPartner(Integer oldPartnerId,Integer newPartnerId,Double transferableCommission,Customers customers)
    {
        Partner oldPartner = partnerRepository.findById(oldPartnerId).get();
        if (oldPartner != null && oldPartner.getId() != CommonConstants.DEFAULT_PARTNER_ID)
        {
            if(oldPartner.getCommtype().equalsIgnoreCase("Balance"))
            {
                if (oldPartner.getBalance() >= transferableCommission)
                {
                    oldPartner.setBalance(oldPartner.getBalance() - transferableCommission);
                    oldPartner=partnerRepository.save(oldPartner);
                    addPartnerLedgerDetailAgainstShiftLocation(transferableCommission, oldPartner,customers,false);
                }
                else if ((oldPartner.getBalance() - transferableCommission) < 0)
                {
                    if(oldPartner.getBalance() > 0) {
                        Double amount=oldPartner.getBalance();
                        Double creditConsume = oldPartner.getCreditConsume() + (transferableCommission - oldPartner.getBalance());
                        oldPartner.setCreditConsume(creditConsume);
                        oldPartner.setBalance(0d);
                        addPartnerLedgerDetailAgainstShiftLocation(amount, oldPartner,customers,false);
                    } else {
                        Double creditConsume = oldPartner.getCreditConsume() + transferableCommission;
                        oldPartner.setCreditConsume(creditConsume);
                    }
                    partnerRepository.save(oldPartner);
                }
            }
            else
            {
                if(oldPartner.getCommrelvalue()!=null)
                    oldPartner.setCommrelvalue(oldPartner.getCommrelvalue() - transferableCommission);
                else
                    oldPartner.setCommrelvalue(-transferableCommission);
                oldPartner=partnerRepository.save(oldPartner);
                addPartnerLedgerDetailAgainstShiftLocation(transferableCommission, oldPartner,customers,false);
            }
        }


        Partner newPartner = partnerRepository.findById(newPartnerId).get();
        if (newPartner != null && newPartner.getId() != CommonConstants.DEFAULT_PARTNER_ID)
        {
            if(newPartner.getCommtype().equalsIgnoreCase("Balance"))
            {
                newPartner.setBalance(newPartner.getBalance() + transferableCommission);
                newPartner=partnerRepository.save(newPartner);
                addPartnerLedgerDetailAgainstShiftLocation(transferableCommission, newPartner,customers,true);
            }
            else
            {
                if(newPartner.getCommrelvalue()!=null)
                    newPartner.setCommrelvalue(newPartner.getCommrelvalue() + transferableCommission);
                else
                    newPartner.setCommrelvalue(transferableCommission);
                newPartner=partnerRepository.save(newPartner);
                addPartnerLedgerDetailAgainstShiftLocation(transferableCommission, newPartner,customers,true);
            }
        }
    }

    public void addPartnerLedgerDetailAgainstShiftLocation(Double transferableCommission, Partner partner,Customers customers,Boolean isNewPartner) {
        PartnerLedgerDetails details = new PartnerLedgerDetails();
        details.setCommission(Double.parseDouble(new DecimalFormat("##.##").format(0.0)));
        details.setTranscategory(CommonConstants.TRANS_CATEGORY_COMMISSION_TRANSFER);
        if(isNewPartner)
        {
            details.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            details.setDescription("Credit Against Customer Location Shift = " + customers.getFirstname());
            details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferableCommission)));
        }
        else
        {
            details.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
            details.setDescription("Debit Against Customer Location Shift = " + customers.getFirstname());
            details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferableCommission)));
        }

        details.setPartner(partner);
        details.setCreateDate(LocalDateTime.now());
        details.setTds_amount(0.0);
        details.setAgr_amount(0.0);
        details.setTax(0.0);
        details.setDebitDocId(null);
        details.setGrossOfferPrice(null);
        details.setCustid(null);
        details.setOfferprice(null);
        partnerLedgerDetailsRepository.save(details);
    }

    public void addPartnerLedgerDetailAgainstBalanceShiftLocation(Double transferableBalance, Partner partner,Customers customers,Boolean isNewPartner) {
        PartnerLedgerDetails details = new PartnerLedgerDetails();
        details.setCommission(Double.parseDouble(new DecimalFormat("##.##").format(0.0)));
        details.setTranscategory(CommonConstants.TRANS_CATEGORY_BALANCE_TRANSFER);
        if(isNewPartner)
        {
            details.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
            details.setDescription("Debit Against Customer Location Shift = " + customers.getFirstname());
            details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferableBalance)));
        }
        else
        {
            details.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            details.setDescription("Credit Against Customer Location Shift = " + customers.getFirstname());
            details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferableBalance)));
        }

        details.setPartner(partner);
        details.setCreateDate(LocalDateTime.now());
        details.setTds_amount(0.0);
        details.setAgr_amount(0.0);
        details.setTax(0.0);
        details.setDebitDocId(null);
        details.setGrossOfferPrice(null);
        details.setCustid(null);
        details.setOfferprice(null);
        partnerLedgerDetailsRepository.save(details);
    }


    public void checkAndUpdatePaymentAdjustmentAgainstInventoryInvoiceAmount(Double totalInvoiceAmount, Customers customers,StaffUser staffUser,Long invoiceId) {
        try {
            Partner partner=customers.getPartner();
            Optional<DebitDocument> debitDocument=debitDocRepository.findById(invoiceId.intValue());
            DebitDocument document=null;
            if(debitDocument.isPresent())
                document=debitDocument.get();

            if(document.getAdjustedAmount()==null)
                document.setAdjustedAmount(0.0);

            totalInvoiceAmount=totalInvoiceAmount-document.getAdjustedAmount();

            if ((customers.getLcoId()==null && (partner.getBalance()>0 && partner.getBalance() >= totalInvoiceAmount))) {
                if (adjustPaymentAgainstInvoiceAmount(customers, staffUser.getPartnerid(), totalInvoiceAmount, invoiceId, staffUser.getId(), staffUser)) {
                    updatePartnerBalanceAgainstInvoiceAmount(customers, totalInvoiceAmount,invoiceId);
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public void transferBalanceFromOnePartnerToAnotherPartner(Integer oldPartnerId,Integer newPartnerId,Double transferableBalance,Customers customers)
    {
        Partner oldPartner = partnerRepository.findById(oldPartnerId).get();
        if (oldPartner != null && oldPartner.getId() != CommonConstants.DEFAULT_PARTNER_ID)
        {
            oldPartner.setBalance(oldPartner.getBalance() + transferableBalance);
            oldPartner=partnerRepository.save(oldPartner);
            addPartnerLedgerDetailAgainstBalanceShiftLocation(transferableBalance, oldPartner,customers,false);
        }

        Partner newPartner = partnerRepository.findById(newPartnerId).get();
        if (newPartner != null && newPartner.getId() != CommonConstants.DEFAULT_PARTNER_ID)
        {
            if (newPartner.getBalance() >= transferableBalance)
            {
                newPartner.setBalance(newPartner.getBalance() - transferableBalance);
                newPartner=partnerRepository.save(newPartner);
                addPartnerLedgerDetailAgainstBalanceShiftLocation(transferableBalance, newPartner,customers,true);
            }
            else if ((newPartner.getBalance() - transferableBalance) < 0)
            {
                if(newPartner.getBalance() > 0) {
                    Double amount=newPartner.getBalance();
                    Double creditConsume = newPartner.getCreditConsume() + (transferableBalance - newPartner.getBalance());
                    newPartner.setCreditConsume(creditConsume);
                    newPartner.setBalance(0d);
                    addPartnerLedgerDetailAgainstBalanceShiftLocation(amount, newPartner,customers,true);
                } else {
                    Double creditConsume = newPartner.getCreditConsume() + transferableBalance;
                    newPartner.setCreditConsume(creditConsume);
                }
                partnerRepository.save(newPartner);
            }
        }
    }
}
