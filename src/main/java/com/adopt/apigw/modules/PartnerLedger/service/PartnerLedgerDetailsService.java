package com.adopt.apigw.modules.PartnerLedger.service;

import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.mapper.PartnerLedgerDetailMapper;
import com.adopt.apigw.modules.PartnerLedger.model.*;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerLedgerDetailsRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartnerLedgerDetailsService extends ExBaseAbstractService<PartnerLedgerDetailsDTO, PartnerLedgerDetails, Long> {
    public PartnerLedgerDetailsService(PartnerLedgerDetailsRepository repository, PartnerLedgerDetailMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    private PartnerLedgerDetailMapper partnerLedgerDetailMapper;
    @Autowired
    private PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private PostpaidPlanService planService;

    @Autowired
    CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    private PartnerLedgerService partnerLedgerService;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private CustomersRepository customersRepository;

    public void setCreditBalance(Integer partnerId, Double balance) throws Exception {
        //setCreditBalance(partnerId, balance, null, null);
    }

    public PartnerLedgerDetails setCreditBalance(Integer custId, Double offerprice, Double agr, Double tds, Integer partnerId, Double commission, String category, String desc, Double tax) throws Exception {
        try {
            PartnerLedgerDetailsDTO partnerLedgerDetailsDTO = new PartnerLedgerDetailsDTO();
            PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
            partnerLedgerDetailsDTO.setCustid(custId);
            partnerLedgerDetailsDTO.setOfferprice(offerprice);
            partnerLedgerDetailsDTO.setTax(tax);
            partnerLedgerDetailsDTO.setAgr_amount(agr);
            partnerLedgerDetailsDTO.setTds_amount(tds);
            partnerLedgerDetailsDTO.setCommission(commission);
            partnerLedgerDetailsDTO.setAmount(0.00);
            partnerLedgerDetailsDTO.setPartnerId(partnerId);
            partnerLedgerDetailsDTO.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            if (category == null)
                partnerLedgerDetailsDTO.setTranscategory(CommonConstants.TRANS_CATEGORY_ADD_BALANCE);
            else
                partnerLedgerDetailsDTO.setTranscategory(category);
            if (desc == null)
                partnerLedgerDetailsDTO.setDescription("Partner Create");
            else
                partnerLedgerDetailsDTO.setDescription(desc);
            partnerLedgerDetailsDTO.setCreateDate(LocalDateTime.now());
            partnerLedgerDetails = partnerLedgerDetailMapper.dtoToDomain(partnerLedgerDetailsDTO, new CycleAvoidingMappingContext());
            //return partnerLedgerDetailsRepository.save(partnerLedgerDetails);
            return null;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(" SetCreditBalance " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public void addBalance(PartnerLedgerBalanceDTO dto) throws Exception {
        try {
            Integer id = dto.getPartner_id();
            if (partnerRepository.getOne(id) != null) {
                PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
                PartnerLedgerDetailsDTO partnerLedgerDetailsDTO = new PartnerLedgerDetailsDTO();
                partnerLedgerDetailsDTO.setAmount(Double.parseDouble(new DecimalFormat("##.####").format(dto.getAmount())));
                partnerLedgerDetailsDTO.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                partnerLedgerDetailsDTO.setTranscategory(CommonConstants.TRANS_CATEGORY_ADD_BALANCE);
                partnerLedgerDetailsDTO.setDescription(dto.getDescription());
                partnerLedgerDetailsDTO.setPartnerId(dto.getPartner_id());
                if (dto.getPaymentdate() != null) {
                    partnerLedgerDetailsDTO.setCreateDate(dto.getPaymentdate().atStartOfDay());
                }
                partnerLedgerDetails = partnerLedgerDetailMapper.dtoToDomain(partnerLedgerDetailsDTO, new CycleAvoidingMappingContext());
                partnerLedgerDetailsRepository.save(partnerLedgerDetails);
            } else {
                throw new DataNotFoundException("Partner Not Found");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(" addBalance() " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public void reverseBalance(PartnerLedgerBalanceDTO dto) throws Exception {
        //reverseBalance(dto, null, null);
    }

    public void reverseBalance(Integer custId,Double offerPrice,Double balanceAmount,Integer partnerId, String category, String desc) throws Exception {
        try {
            if (partnerRepository.getOne(partnerId) != null) {
                PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
                PartnerLedgerDetailsDTO partnerLedgerDetailsDTO = new PartnerLedgerDetailsDTO();
                partnerLedgerDetailsDTO.setCommission(Double.parseDouble(new DecimalFormat("##.##").format(0.0)));
                if(balanceAmount>0)
                    partnerLedgerDetailsDTO.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                else
                    partnerLedgerDetailsDTO.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
                if(balanceAmount<0)
                    balanceAmount=-(balanceAmount);
                if(offerPrice>0)
                    partnerLedgerDetailsDTO.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(offerPrice)));
                else
                    partnerLedgerDetailsDTO.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(balanceAmount)));
                partnerLedgerDetailsDTO.setTranscategory(category);
                partnerLedgerDetailsDTO.setDescription(desc);
                partnerLedgerDetailsDTO.setPartnerId(partnerId);
                partnerLedgerDetailsDTO.setCreateDate(LocalDateTime.now());
                partnerLedgerDetailsDTO.setTds_amount(0.0);
                partnerLedgerDetailsDTO.setAgr_amount(0.0);
                partnerLedgerDetailsDTO.setTax(0.0);
                partnerLedgerDetailsDTO.setCustid(custId);
                partnerLedgerDetailsDTO.setOfferprice(offerPrice);
                partnerLedgerDetails = partnerLedgerDetailMapper.dtoToDomain(partnerLedgerDetailsDTO, new CycleAvoidingMappingContext());
                partnerLedgerDetailsRepository.save(partnerLedgerDetails);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(" reverseBalance() " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<PartnerLedgerDetailsDTO> convertResponseModelIntoPojo(List<PartnerLedgerDetails> partnerLedgerDetails) {
        return partnerLedgerDetails.stream().map(data -> partnerLedgerDetailMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public PartnerLedgerInfoPojo getByTime(PartnerLedgerGetDTO pojo) throws Exception
    {
        PartnerLedgerInfoPojo infoPojo = new PartnerLedgerInfoPojo();
        Double OpeningAmount = 0.0;
        if (pojo.getSTART_DATE() != null) {
            OpeningAmount = partnerLedgerDetailsRepository.findOpeningAmount(pojo.getPartner_id(), pojo.getSTART_DATE());
        }
        if (OpeningAmount != null) {
            infoPojo.setOpeningAmount(Double.parseDouble(new DecimalFormat("##.####").format(OpeningAmount)));
        }else{
            infoPojo.setOpeningAmount(0.0);
            OpeningAmount = 0.0;
        }
        Double bal = 0.0;
        List<PartnerLedgerDetails> partnerLedgerDetailsList = new ArrayList<>();
        if (pojo.getSTART_DATE() != null && pojo.getEND_DATE() != null)
            partnerLedgerDetailsList = partnerLedgerDetailsRepository.findAllByStartDateAndEndDateAndPartnerId(pojo.getSTART_DATE(), pojo.getEND_DATE(), pojo.getPartner_id());

        if (pojo.getSTART_DATE() == null && pojo.getEND_DATE() == null)
            partnerLedgerDetailsList = partnerLedgerDetailsRepository.findAllByPartner_IdOrderByCreateDateAsc(pojo.getPartner_id());

        if (partnerLedgerDetailsList != null && 0 < partnerLedgerDetailsList.size())
        {
            String plan = "";
            for (int i = 0; i < partnerLedgerDetailsList.size(); i++)
            {
                List<String> creditNoteList = new ArrayList<>();
                if(partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase("Withdraw")){
                    bal=bal-partnerLedgerDetailsList.get(i).getAmount();
                }else
                {
                    if (partnerLedgerDetailsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT)) {
                        if (partnerLedgerDetailsList.get(i).getAmount() > 0.0d)
                            bal += OpeningAmount + partnerLedgerDetailsList.get(i).getAmount();
                        if (partnerLedgerDetailsList.get(i).getCommission() != null)
                            if (partnerLedgerDetailsList.get(i).getCommission() > 0.0d)
                                bal += OpeningAmount + partnerLedgerDetailsList.get(i).getCommission();
                    }
                    if (partnerLedgerDetailsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)) {
                        if (partnerLedgerDetailsList.get(i).getAmount() > 0.0d)
                            bal += OpeningAmount - partnerLedgerDetailsList.get(i).getAmount();
                        if (partnerLedgerDetailsList.get(i).getCommission() != null)
                            if (partnerLedgerDetailsList.get(i).getCommission() > 0.0d)

                                bal += OpeningAmount - partnerLedgerDetailsList.get(i).getCommission();
                    }
                }
                partnerLedgerDetailsList.get(i).setBalAmount(Double.parseDouble(new DecimalFormat("##.####").format(bal)));

                if(partnerLedgerDetailsList.get(i).getPlanid() != null){
                    String[] parts = partnerLedgerDetailsList.get(i).getDescription().split(" PlanID");
                    String before = parts[0];
                    //String after = parts[1];
                    String planName="";
                    if(partnerLedgerDetailsList.get(i).getPlanid()!=null && !partnerLedgerDetailsList.get(i).getPlanid().equalsIgnoreCase(""))
                        planName = postpaidPlanRepo.findNameById(Integer.parseInt(partnerLedgerDetailsList.get(i).getPlanid()));
                    String desc = before + " PlanName = " + planName;
                    partnerLedgerDetailsList.get(i).setDescription(desc);
                    partnerLedgerDetailsList.get(i).setPlanname(planName);
                } else if (partnerLedgerDetailsList.get(i).getDebitDocId() != null) {
                    DebitDocument debitDec = debitDocRepository.findById(partnerLedgerDetailsList.get(i).getDebitDocId().intValue()).orElse(null);
                    if(debitDec != null && debitDec.getPostpaidPlan() != null){
                       plan = postpaidPlanRepo.findNameById(debitDec.getPostpaidPlan().getId());
                    }
                }

                if (partnerLedgerDetailsList.get(i).getDebitDocId() != null) {
                    DebitDocument debitDoc = debitDocRepository.findById(partnerLedgerDetailsList.get(i).getDebitDocId().intValue()).orElse(null);
                    if (debitDoc != null) {
                        partnerLedgerDetailsList.get(i).setInvoiceNo(debitDoc.getDocnumber());
                    }
                    List<CreditDebitDocMapping> creditDebitMapping = creditDebtMappingRepository.findBydebtDocId(partnerLedgerDetailsList.get(i).getDebitDocId().intValue());

                    List<Integer> creditDocIdList = new ArrayList<>();
                    if (creditDebitMapping != null && creditDebitMapping.size() > 0) {
                        creditDocIdList = creditDebitMapping.stream().map(CreditDebitDocMapping::getCreditDocId).collect(Collectors.toList());
                    }

//                    List<CreditDocument> creditDocumentList = new ArrayList<>();
//                    List<String> creditNoteList = new ArrayList<>();
                    if (creditDocIdList != null && creditDocIdList.size() > 0) {
//                        creditDocumentList = creditDocRepository.findAllByIdIn(creditDocIdList);
                        creditNoteList = creditDocRepository.findAllByIdInAndTypeCreditNote(creditDocIdList);
                    }

//                    if (creditDocumentList != null && creditDocIdList.size() > 0) {
//                        List<String> creditNoteList = new ArrayList<>();
//                        if (creditDocumentList != null && creditDocumentList.size() > 0) {
//                            creditNoteList = creditDocumentList.stream().map(CreditDocument::getCreditdocumentno).collect(Collectors.toList());
//                        }
//                        partnerLedgerDetailsList.get(i).setCreditDocNo(creditNoteList);
//                    }
                }

                if(partnerLedgerDetailsList.get(i).getCustid()!=null) {
                    partnerLedgerDetailsList.get(i).setCustomer_name(customersRepository.findCustomerName(partnerLedgerDetailsList.get(i).getCustid()));
                    partnerLedgerDetailsList.get(i).setCustomer_username(customersRepository.findUsernameById(partnerLedgerDetailsList.get(i).getCustid()));
                }
                if(partnerLedgerDetailsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT) &&
                        partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_ADD_BALANCE)){
                    partnerLedgerDetailsList.get(i).setTranscategory(CommonConstants.WALLET_BALANCE_TOPUP);
                }
                if(partnerLedgerDetailsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT) &&
                        partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_CUST_CREATE)){
                    partnerLedgerDetailsList.get(i).setTranscategory(CommonConstants.TRANS_CATEGORY_INVOICE1);
                    partnerLedgerDetailsList.get(i).setPlanname(plan);
                    partnerLedgerDetailsList.get(i).setOfferprice(partnerLedgerDetailsList.get(i).getGrossOfferPrice());
                }
                if(partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase("Revert Balance")){
                    partnerLedgerDetailsList.get(i).setTranscategory(CommonConstants.TRANS_CREDIT_NOTE1);
                    partnerLedgerDetailsList.get(i).setCreditDocNo(creditNoteList);
                    partnerLedgerDetailsList.get(i).setOfferprice(partnerLedgerDetailsList.get(i).getGrossOfferPrice());
                }
                if(partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase("Revert Commission")){
                    partnerLedgerDetailsList.get(i).setTranscategory(CommonConstants.CREDIT_NOTE_COMMISSION);
                    partnerLedgerDetailsList.get(i).setCreditDocNo(creditNoteList);
                    partnerLedgerDetailsList.get(i).setOfferprice(partnerLedgerDetailsList.get(i).getGrossOfferPrice());

                }
                if (partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase("Withdraw")) {
                    partnerLedgerDetailsList.get(i).setTranscategory(CommonConstants.WALLET_BALANCE_PAYOUT);
                }
                if (partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_COMMISSION)) {
                    partnerLedgerDetailsList.get(i).setOfferprice(partnerLedgerDetailsList.get(i).getGrossOfferPrice());
                }
            }
        }
        infoPojo.setDebitCreditDetail(convertResponseModelIntoPojo(partnerLedgerDetailsList));
        infoPojo.setClosingBalance(Double.parseDouble(new DecimalFormat("##.####").format(bal)));
        return infoPojo;
    }

    public PartnerLedgerAllInfoPojo partInfoByTime(Integer partnerId, PartnerLedgerInfoPojo pojo) {
        PartnerLedgerAllInfoPojo partpojo = new PartnerLedgerAllInfoPojo();
        Partner partner = partnerRepository.getOne(partnerId);
        partpojo.setPartnerId(partner.getId());
        partpojo.setPartnername(partner.getName());
        partpojo.setAddress(partner.getAddress1());
        partpojo.setStatus(partner.getStatus());
        partpojo.setPartnerLedgerInfoPojo(pojo);
        return partpojo;
    }

    public void setLedgerDetailsForCustomerCreation(Integer custId,Double offerPrice,Double balanceAmount,Customers customers, Integer partner) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [setLedgerDetailsForCustomerCreation()] ";
        try {
            Partner franchise = partnerRepository.findById(partner).orElse(null);
            if (null != franchise)
            {
                reverseBalance(custId,offerPrice,balanceAmount, partner, CommonConstants.TRANS_CATEGORY_CUST_CREATE, "Debit Against Customer Creation = " + customers.getFirstname());
                franchise.setBalance(franchise.getBalance()-offerPrice);
                //partnerRepository.save(franchise);
            }
            else
                throw new DataNotFoundException("Partner Not Found");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public String getModuleNameForLog() {
        return "[PartnerLedgerDetailsService]";
    }
}
