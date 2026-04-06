package com.adopt.apigw.service.postpaid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.adopt.apigw.MicroSeviceDataShare.MessageSender.DataSharedMessageSender;
import com.adopt.apigw.MicroSeviceDataShare.SharedDataConstants.SharedDataConstants;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.devCode.TransactionUtil;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.ChangePlanDTOs.ChangePlanMessage;
import com.adopt.apigw.modules.ChangePlanDTOs.CustChargeDetailsRevenue;
import com.adopt.apigw.modules.subscriber.service.ChargeThread;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CafFollowUpMessage;
import com.adopt.apigw.rabbitMq.message.CustServiceChargeIPDtlsMessage;
import com.adopt.apigw.rabbitMq.message.CustomerPackageRelMessage;
import com.adopt.apigw.repository.common.CustServiceChargeIPDetailsRepo;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.service.common.CustServiceChargeIPDetailsService;
import com.adopt.apigw.service.common.CustomersService;
import io.swagger.models.auth.In;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.mapper.postpaid.CustChargeMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.pojo.api.CustChargeDetailsDeleteDTO;
import com.adopt.apigw.pojo.api.CustChargeDetailsPojo;
import com.adopt.apigw.pojo.api.CustChargeOverrideDTO;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.pojo.api.TaxDetailCountReqDTO;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.itextpdf.text.Document;
import org.springframework.util.CollectionUtils;

@Service
public class CustChargeService extends AbstractService<CustChargeDetails, CustChargeDetailsPojo, Integer> {

    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    DataSharedMessageSender messageSenderRevenue;
    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    private MessagesPropertyConfig messagesProperty;
    @Autowired
    private CustChargeRepository entityRepository;
    @Autowired
    private CustChargeMapper custChargeMapper;
    @Autowired
    private CustChargeDetailMapper custChargeDetailMapper;
    @Autowired
    private TaxService taxService;
    @Autowired
    private TaxTypeTierRepository taxTypeTierRepository;
    @Autowired
    private ChargeService chargeService;
    @Autowired
    private PostpaidPlanService planService;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    private CustomersRepository customersRepository;

	@Autowired
	private CustomersService customersService;

	@Autowired
	private DbrService dbrService;

	@Autowired
	private CustServiceChargeIPDetailsRepo custServiceChargeIPDetailsRepo;
	@Autowired
	private CustServiceChargeIPDetailsService custServiceChargeIPDetailsService;

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private KafkaMessageSender kafkaMessageSender;
	@Autowired
	private CustomerChargeHistoryRepo customerChargeHistoryRepo;

	@Autowired
	private TransactionUtil transactionUtil;

    @Autowired
    private CustChargeInstallmentRepository custChargeInstallmentRepository;

    @Override
    protected JpaRepository<CustChargeDetails, Integer> getRepository() {
        return entityRepository;
    }

    public List<CustChargeDetails> findAllByCustomer(Integer custId) {
        if (custId != null) {
            Optional<Customers> customerOptional = customersRepository.findById(custId);
            if (customerOptional.isPresent()) {
                List<CustChargeDetails> custChargeList = entityRepository.findAllByCustomer(customerOptional.get());
                List<Integer> chargeDetailsIds = custChargeList.stream().map(CustChargeDetails::getId).collect(Collectors.toList());
                List<CustChargeInstallment> custChargeInstallments =
                        custChargeInstallmentRepository.findByCustChargeDetailsIdsFetch(chargeDetailsIds);
                Map<Integer, CustChargeInstallment> installmentMap = custChargeInstallments.stream()
                        .collect(Collectors.toMap(
                                ci -> ci.getCustChargeDetails().getId(),
                                ci -> ci,
                                (existing, replacement) -> replacement
                        ));

                if (custChargeList != null && custChargeList.size() > 0) {
                    custChargeList.forEach(charge -> {
                        String chargeName = "";
                        if (charge.getChargeid() != null) {
                            chargeName = chargeRepository.findNameById(charge.getChargeid());
                            charge.setCharge_name(chargeName);
                        }
                        CustChargeInstallment ci = installmentMap.get(charge.getId());
                        if (ci != null) {
                            charge.setInstallmentStartDate(ci.getInstallmentStartDate());
                            charge.setNextInstallmentDate(ci.getNextInstallmentDate());
                            charge.setLastInstallmentDate(ci.getLastInstallmentDate());
                            charge.setInstallmentNo(ci.getInstallmentNo());
                            charge.setTotalInstallments(ci.getTotalInstallments());
                            charge.setInstallmentFrequency(ci.getInstallmentFrequency());
                            charge.setAmountPerInstallment(ci.getAmountPerInstallment());
                        }
                        if(ci != null && ci.getAmountPerInstallment() != null && charge.getDiscount() != null){
                            Double price = charge.getAmountPerInstallment().doubleValue();
                            Double discount = charge.getDiscount();
                            Double discountAmount = price * (discount / 100);
                            charge.setFinalPrice(price - discountAmount);
                        } else if(ci != null && ci.getAmountPerInstallment() != null) {
                            charge.setFinalPrice(ci.getAmountPerInstallment().doubleValue());
                        } else if(charge.getDiscount() != null){
                            Double price = charge.getPrice();
                            Double discount = charge.getDiscount();
                            Double discountAmount = price * (discount / 100);
                            charge.setFinalPrice(price - discountAmount);
                        } else {
                            charge.setFinalPrice(charge.getPrice());
                        }

                        if(charge.getDiscount() != null){
                            Double price = charge.getPrice();
                            Double discount = charge.getDiscount();
                            Double discountAmount = price * (discount / 100);
                            charge.setDiscountPrice(price - discountAmount);
                        }
                        if(charge.getDiscount() != null){
                            Double price = charge.getPrice();
                            Double discount = charge.getDiscount();
                            Double discountAmount = price * (discount / 100);
                            charge.setDiscountValue(discountAmount);
                        }
                    });
                }
                return custChargeList;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, "Customer not found by given id! " + custId, null);
            }
        } else {
            throw new CustomValidationException(APIConstants.FAIL, "Customer id can not be null!", null);
        }
    }

    public void mapCustomerPlanPackIdToDirectCharge(Integer custId) {
        DecimalFormat df = new DecimalFormat("0.00");
//    	Optional<Customers> customerOptional = customersRepository.findById(custId);
//    	if(customerOptional.isPresent()) {
        List<CustChargeDetails> list = findAllByCustomer(custId);
        if (list != null && !list.isEmpty()) {
            for (CustChargeDetails custChargeDetails : list) {
                if (custChargeDetails.getCustomer() != null && custChargeDetails.getPlanid() != null) {
                    List<CustPlanMappping> packageRelList = custPlanMappingRepository.findAllByCustomerIdAndPlanId(custChargeDetails.getCustomer().getId(), custChargeDetails.getPlanid());
                    if (packageRelList != null && !packageRelList.isEmpty()) {
                        CustPlanMappping packageRel = packageRelList.get(0);
                        custChargeDetails.setPlanValidity(packageRel.getPlanValidityDays());
                        custChargeDetails.setCustPlanMapppingId(packageRel.getId());
                        if (packageRel.getStartDate() != null)
                            custChargeDetails.setStartdate(packageRel.getStartDate());
                        if (packageRel.getEndDate() != null) custChargeDetails.setEnddate(packageRel.getEndDate());
                    }
                }
                Double chargePriceIncludingTax = custChargeDetails.getPrice();
                Double tax = 0.0;
				List<TaxTypeTier> taxTypeTiers = taxService.get(custChargeDetails.getTaxId(),getMvnoIdFromCurrentStaff(custId)).getTieredList();
                for (TaxTypeTier taxTypeTier : taxTypeTiers) {
                    tax = tax + chargePriceIncludingTax * taxTypeTier.getRate() / 100.0;
                    chargePriceIncludingTax += chargePriceIncludingTax * taxTypeTier.getRate() / 100.0;
                }

                Double price = custChargeDetails.getPrice();
                Double dbr = price + tax;
                custChargeDetails.setPrice(price);
                custChargeDetails.setTaxamount(tax);
                if (!custChargeDetails.getType().equalsIgnoreCase("One-time"))
                    dbr = dbr / custChargeDetails.getPlanValidity();
                custChargeDetails.setDbr(Double.parseDouble(df.format(dbr)));
            }
            entityRepository.saveAll(list);
        }
//    	}else {
//            throw new CustomValidationException(APIConstants.FAIL, "Customer not found by given id! " + custId, null);
//    	}
    }

    public CustChargeDetails deleteCustomerDirectCharge(CustChargeDetailsDeleteDTO dto) {
        if (dto.getId() != null) {
            Optional<CustChargeDetails> optional = entityRepository.findById(dto.getId());
            if (optional != null && optional.isPresent()) {
                CustChargeDetails custChargeDetailsDb = optional.get();
                if (dto.isSoftDelete()) {
                    custChargeDetailsDb.setIsDeleted(true);
                } else {
                    if (dto.getEndDate() == null)
                        throw new CustomValidationException(APIConstants.FAIL, "EndDate can not be null!", null);
                    custChargeDetailsDb.setEnddate(dto.getEndDate());
                }
                return entityRepository.save(custChargeDetailsDb);

            } else {
                throw new CustomValidationException(APIConstants.FAIL, "CustChargeDetails not found by given id! " + dto.getId(), null);
            }
        } else {
            throw new CustomValidationException(APIConstants.FAIL, "custChargeDetailsId id can not be null!", null);
        }
    }


    public List<CustChargeDetailsPojo> calDirectCharge(CustomersPojo customer) {
        Integer stateId = customer.getAddressList().stream().filter(data -> data.getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)).findAny().orElse(null).getStateId();
        List<CustChargeDetailsPojo> custChargeDetailsPojoList = new ArrayList<>();
        if (customer.getOverChargeList() != null && customer.getOverChargeList().size() > 0) {
            for (CustChargeDetailsPojo custChargeDetailsPojo : customer.getOverChargeList()) {
                custChargeDetailsPojo.setStartdate(new Date());
                custChargeDetailsPojo.setCharge_date(new Date());
                if (customer.getCusttype() != null && customer.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID) && custChargeDetailsPojo.getType() != null && custChargeDetailsPojo.getType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_RECURRING)) {
                    LocalDate nextBillDate = calculateNextBillDate(custChargeDetailsPojo.getBillingCycle(), customer.getBillday());
                    custChargeDetailsPojo.setNextBillDate(nextBillDate);
                }
                try {
                    if (custChargeDetailsPojo.getPlanid() != null) {
                        PostpaidPlan postpaidPlan = planService.get(custChargeDetailsPojo.getPlanid(),customer.getMvnoId());
                        if (customer.getId() != null && postpaidPlan.getId() != null) {
                            List<CustPlanMappping> packageRelList = custPlanMappingRepository.findAllByCustomerIdAndPlanId(customer.getId(), postpaidPlan.getId());
                            if (packageRelList != null && packageRelList.size() > 0) {
                                CustPlanMappping packageRel = packageRelList.get(0);
                                custChargeDetailsPojo.setPlanValidity(packageRel.getPlanValidityDays());
                                custChargeDetailsPojo.setCustPlanMapppingId(packageRel.getId());
                                if (packageRel.getStartDate() != null)
                                    custChargeDetailsPojo.setStartdate(Date.from(packageRel.getStartDate().atZone(ZoneId.systemDefault()).toInstant()));
                                if (packageRel.getEndDate() != null)
                                    custChargeDetailsPojo.setEnddate(Date.from(packageRel.getEndDate().atZone(ZoneId.systemDefault()).toInstant()));
                            }
                        }
                        custChargeDetailsPojo.setUnitsOfValidity(postpaidPlan.getUnitsOfValidity());
                    }
                    Charge charge = chargeService.get(custChargeDetailsPojo.getChargeid(),customer.getMvnoId());
                    custChargeDetailsPojo.setChargetype(charge.getChargetype());
                    if (null != charge) {
                        custChargeDetailsPojo.setChargeid(charge.getId());
                        custChargeDetailsPojo.setIsUsed(false);
                        if (charge.getTax() != null) custChargeDetailsPojo.setTaxId(charge.getTax().getId());
                        TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(null, stateId, null, charge.getId());
                        Double taxAmount = taxService.taxCalculationByCharge(taxDetailCountReqDTO);
                        custChargeDetailsPojo.setTaxamount(taxAmount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return custChargeDetailsPojoList;
    }

    public CustChargeOverrideDTO createCustChargeOverride(CustChargeOverrideDTO custChargeOverrideDTO) throws Exception {
        List<Integer> chargeIdList = new ArrayList<>();
        List<CustChargeDetails> custChargeDetailsForRevenue = new ArrayList<>();
        Customers customer = customersRepository.findById(custChargeOverrideDTO.getCustid()).get();
        if (customer != null) {
            Integer stateId = null;
            if (customer.getAddressList() != null && !CollectionUtils.isEmpty(customer.getAddressList())) {
                stateId = customer.getAddressList().stream().filter(data -> data.getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)).findAny().orElse(null).getStateId();
            }
            if (custChargeOverrideDTO.getCustChargeDetailsPojoList() != null && custChargeOverrideDTO.getCustChargeDetailsPojoList().size() > 0) {
                List<CustChargeInstallment> custChargeInstallments = new ArrayList<>();
                for (CustChargeDetailsPojo custChargeDetailsPojo : custChargeOverrideDTO.getCustChargeDetailsPojoList()) {
                    CustServiceChargeIPDetails custServiceChargeIPDetails = new CustServiceChargeIPDetails();
                    CustChargeDetails custChargeDetails = new CustChargeDetails();
                    custChargeDetails = custChargeMapper.dtoToDomain(custChargeDetailsPojo, new CycleAvoidingMappingContext());
                    if (custChargeDetailsPojo.getId() != null) {
                        Optional<CustChargeDetails> optional = entityRepository.findById(custChargeDetailsPojo.getId());
                        if (optional != null && optional.isPresent()) custChargeDetails = optional.get();
                    }

                    custChargeDetails.setBillableCustomerId(custChargeOverrideDTO.getBillableCustomerId());

                    custChargeDetails.setPrice(custChargeDetailsPojo.getPrice());
                    custChargeDetails.setActualprice(custChargeDetailsPojo.getActualprice());
                    custChargeDetails.setCharge_date(LocalDateTime.now());
                    custChargeDetails.setCustomer(customer);
                    custChargeDetails.setRemarks(custChargeDetailsPojo.getRemarks());
                    custChargeDetails.setType(custChargeDetailsPojo.getType());
                    custChargeDetails.setIsUsed(false);
                    if (customer.getCusttype() != null && customer.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID) && custChargeDetailsPojo.getType() != null && custChargeDetailsPojo.getType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_RECURRING)) {
                        custChargeDetails.setBillingCycle(custChargeDetailsPojo.getBillingCycle());
                        LocalDate nextBillDate = calculateNextBillDate(custChargeDetailsPojo.getBillingCycle(), customer.getBillday());
                        custChargeDetails.setNextBillDate(nextBillDate);
                    }
                    CustPlanMappping packageRel = new CustPlanMappping();
                    if (custChargeDetailsPojo.getPlanid() != null) {
                        PostpaidPlan postpaidPlan = planService.get(custChargeDetailsPojo.getPlanid(),customer.getMvnoId());
                        custChargeDetails.setPlanid(postpaidPlan.getId());
                        if (customer.getId() != null && postpaidPlan.getId() != null) {
                            List<CustPlanMappping> packageRelList = null;
                            if (custChargeDetailsPojo.getCustServiceMappingId() != null)
                                packageRelList = custPlanMappingRepository.findAllByCustomerIdAndPlanIdAndAndCustServiceMappingId(customer.getId(), postpaidPlan.getId(), custChargeDetailsPojo.getCustServiceMappingId());
                            else
                                packageRelList = custPlanMappingRepository.findAllByCustomerIdAndPlanId(customer.getId(), postpaidPlan.getId());

                            if (packageRelList != null && packageRelList.size() > 0) {
                                packageRel = packageRelList.get(0);
                                custChargeDetails.setPlanValidity(packageRel.getPlanValidityDays());
                                custChargeDetails.setCustPlanMapppingId(packageRel.getId());
                                if (packageRel.getStartDate() != null)
                                    custChargeDetails.setStartdate(packageRel.getStartDate());
                                if (packageRel.getEndDate() != null)
                                    custChargeDetails.setEnddate(packageRel.getEndDate());

                            }
                        }
                        custChargeDetails.setUnitsOfValidity(postpaidPlan.getUnitsOfValidity());
                    }
                    Charge charge = chargeService.get(custChargeDetailsPojo.getChargeid(),customer.getMvnoId());
                    custChargeDetails.setChargetype(charge.getChargetype());
                    if (null != charge) {
                        custChargeDetails.setChargeid(charge.getId());
//							custChargeDetails.setRemarks();

                        if (charge.getTax() != null) custChargeDetails.setTaxId(charge.getTax().getId());

                        TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(null, stateId, customer.getId(), charge.getId());
                        Double taxAmount = taxService.taxCalculationByCharge(taxDetailCountReqDTO);
                        custChargeDetails.setTaxamount(taxAmount);
                        if (customer.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_PREPAID)) {
                            if (custChargeDetails.getType().equalsIgnoreCase("One-time")) {
                                Double newPrice = custChargeDetails.getPrice();
                                Double chargePriceIncludingTax = custChargeDetails.getPrice();
                                Double tax = 0.0;
                                if (custChargeOverrideDTO.getTaxInPer() != null && custChargeOverrideDTO.getIsMvnoCharge()) {
                                    tax = tax + chargePriceIncludingTax * custChargeOverrideDTO.getTaxInPer() / 100.0;
                                } else {
                                    for (int k = 0; k < taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().size(); k++) {
                                        tax = tax + chargePriceIncludingTax * taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                                        chargePriceIncludingTax += chargePriceIncludingTax * taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                                    }
                                }
                                newPrice = newPrice + tax;
                                custChargeDetails.setTaxamount(tax);
                                custChargeDetails.setDbr(newPrice);
                                custChargeDetails.setStartdate(LocalDateTime.now());
                                if (packageRel.getEndDate() != null) {
                                    custChargeDetails.setEnddate(packageRel.getEndDate());
                                }
                            } else {
                                if (!LocalDate.now().equals(custChargeDetails.getStartdate())) {
                                    if (LocalDate.now().compareTo(ChronoLocalDate.from(custChargeDetails.getStartdate().toLocalDate())) > 0) {
                                        Double dbr = custChargeDetails.getPrice() / custChargeDetails.getPlanValidity();
                                        Long daysDiff = ChronoUnit.DAYS.between(LocalDate.now(), custChargeDetails.getEnddate().toLocalDate());
                                        Double newPrice = dbr * daysDiff;
                                        custChargeDetails.setPrice(newPrice);
                                        Double chargePriceIncludingTax = custChargeDetails.getPrice();
                                        Double tax = 0.0;
                                        for (int k = 0; k < taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().size(); k++) {
                                            tax = tax + chargePriceIncludingTax * taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                                            chargePriceIncludingTax += chargePriceIncludingTax * taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                                        }
                                        custChargeDetails.setTaxamount(tax);
                                        custChargeDetails.setStartdate(LocalDateTime.now());
                                        if (packageRel.getEndDate() != null) {
                                            custChargeDetails.setEnddate((Instant.ofEpochMilli(custChargeDetailsPojo.getExpiry().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59)));

//												custChargeDetails.setEnddate(packageRel.getEndDate());
                                            Charge chargeEntity = chargeService.get(custChargeDetailsPojo.getChargeid(),customer.getMvnoId());
                                            Integer count = 0;
                                            if (chargeEntity.getChargecategory().equalsIgnoreCase("IP")) {
                                                count = custServiceChargeIPDetailsRepo.duplicateIPCheckAtSave(custChargeDetailsPojo.getStaticIPAdrress());
                                            }
                                            if (count > 0) {
                                                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Static IP Already in Use", null);
                                            } else {
                                                if ((null != custChargeDetailsPojo.getStaticIPAdrress()) && !(custChargeDetailsPojo.getStaticIPAdrress().isEmpty())) {
                                                    custChargeDetails.setConnection_no(custChargeDetailsPojo.getConnection_no());
                                                    custChargeDetails.setStaticIPAdrress(custChargeDetailsPojo.getStaticIPAdrress());
                                                    custServiceChargeIPDetails.setStaticIPAdrress(custChargeDetailsPojo.getStaticIPAdrress());
                                                    List<CustServiceChargeIPDetails> custChargeDetailsList = custServiceChargeIPDetailsRepo.findAllByCustid(custChargeOverrideDTO.getCustid());
                                                    if (custChargeDetailsList.size() > 0) {
                                                        custServiceChargeIPDetails.setStaticIPStartDate(custChargeDetailsList.get(custChargeDetailsList.size() - 1).getStaticIPEndDate().plusMinutes(1));
                                                        custChargeDetails.setStartdate(custChargeDetailsList.get(custChargeDetailsList.size() - 1).getStaticIPEndDate().plusMinutes(1));
                                                        LocalDateTime localDateTime = Instant.ofEpochMilli(custChargeDetailsPojo.getExpiry().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                                                        if ((localDateTime.isBefore(custChargeDetailsList.get(custChargeDetailsList.size() - 1).getStaticIPEndDate())) || ((localDateTime.isEqual(custChargeDetailsList.get(custChargeDetailsList.size() - 1).getStaticIPEndDate())))) {
                                                            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Given Expiry Date " + localDateTime.toLocalDate() + " is overlapping for selected Charge, Please select another expiry date greater than " + custChargeDetailsList.get(custChargeDetailsList.size() - 1).getStaticIPEndDate().toLocalDate(), null);
                                                        }
                                                    } else {
                                                        custChargeDetails.setStartdate(LocalDateTime.now());
                                                        custServiceChargeIPDetails.setStaticIPStartDate(LocalDateTime.now());
                                                    }
                                                    custServiceChargeIPDetails.setStaticIPEndDate((Instant.ofEpochMilli(custChargeDetailsPojo.getExpiry().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59)));
//												custServiceChargeIPDetails.setStaticIPStartDate(LocalDateTime.now());
                                                    custServiceChargeIPDetails.setChargeId(custChargeDetailsPojo.getChargeid());
                                                    custServiceChargeIPDetails.setCustServiceMappingId(packageRel.getCustServiceMappingId());
                                                    custServiceChargeIPDetails.setCustId(custChargeOverrideDTO.getCustid());
                                                    custServiceChargeIPDetailsRepo.save(custServiceChargeIPDetails);
                                                    CustServiceChargeIPDtlsMessage custServiceChargeIPDtlsMessage = new CustServiceChargeIPDtlsMessage(custServiceChargeIPDetails);
                                                    //messageSender.send(custServiceChargeIPDtlsMessage, RabbitMqConstants.QUEUE_APIGW_CREATE_CUST_SERVICE_CHARGE_IP_DTLS);
                                                    kafkaMessageSender.send(new KafkaMessageData(custServiceChargeIPDtlsMessage, custServiceChargeIPDtlsMessage.getClass().getSimpleName(), "CREATE_CUST_SERVICE_CHARGE_IP_DTLS"));
                                                }
                                            }
                                        }
                                        custChargeDetails.setDbr((newPrice + tax) / daysDiff);
                                    } else {
                                        Double newPrice = custChargeDetails.getPrice();
                                        Double chargePriceIncludingTax = custChargeDetails.getPrice();
                                        Double tax = 0.0;
                                        for (int k = 0; k < taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().size(); k++) {
                                            tax = tax + chargePriceIncludingTax * taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                                            chargePriceIncludingTax += chargePriceIncludingTax * taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                                        }
                                        newPrice = newPrice + tax;
                                        Double dbr = newPrice / custChargeDetails.getPlanValidity();
                                        custChargeDetails.setDbr(dbr);
                                        custChargeDetails.setTaxamount(tax);
                                        custChargeDetails.setStartdate(LocalDateTime.now());
                                        if (packageRel.getEndDate() != null) {
                                            custChargeDetails.setEnddate((Instant.ofEpochMilli(custChargeDetailsPojo.getExpiry().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59)));
//												custChargeDetails.setEnddate(packageRel.getEndDate());
                                            Charge chargeEntity = chargeService.get(custChargeDetailsPojo.getChargeid(),customer.getMvnoId());
                                            Integer count = 0;
                                            if (chargeEntity.getChargecategory().equalsIgnoreCase("IP")) {
                                                count = custServiceChargeIPDetailsRepo.duplicateIPCheckAtSave(custChargeDetailsPojo.getStaticIPAdrress());
                                            }
                                            if (count > 0) {
                                                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Static IP Already in Use", null);
                                            } else {
                                                if ((null != custChargeDetailsPojo.getStaticIPAdrress()) && !(custChargeDetailsPojo.getStaticIPAdrress().isEmpty())) {
                                                    custChargeDetails.setConnection_no(custChargeDetailsPojo.getConnection_no());
                                                    custChargeDetails.setStaticIPAdrress(custChargeDetailsPojo.getStaticIPAdrress());
                                                    custServiceChargeIPDetails.setStaticIPAdrress(custChargeDetailsPojo.getStaticIPAdrress());
                                                    List<CustServiceChargeIPDetails> custChargeDetailsList = custServiceChargeIPDetailsRepo.findAllByCustid(custChargeOverrideDTO.getCustid());
                                                    if (custChargeDetailsList.size() > 0) {
                                                        custChargeDetails.setStartdate(custChargeDetailsList.get(custChargeDetailsList.size() - 1).getStaticIPEndDate().plusMinutes(1));
                                                        custServiceChargeIPDetails.setStaticIPStartDate(custChargeDetailsList.get(custChargeDetailsList.size() - 1).getStaticIPEndDate().plusMinutes(1));
                                                        LocalDateTime localDateTime = Instant.ofEpochMilli(custChargeDetailsPojo.getExpiry().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                                                        if ((localDateTime.isBefore(custChargeDetailsList.get(custChargeDetailsList.size() - 1).getStaticIPEndDate())) || ((localDateTime.isEqual(custChargeDetailsList.get(custChargeDetailsList.size() - 1).getStaticIPEndDate())))) {
                                                            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Given Expiry Date " + localDateTime.toLocalDate() + " is overlapping for selected Charge, Please select another expiry date greater than " + custChargeDetailsList.get(custChargeDetailsList.size() - 1).getStaticIPEndDate().toLocalDate(), null);
                                                        }
                                                    } else {
                                                        custChargeDetails.setStartdate(LocalDateTime.now());
                                                        custServiceChargeIPDetails.setStaticIPStartDate(LocalDateTime.now());
                                                    }
                                                    custServiceChargeIPDetails.setStaticIPEndDate((Instant.ofEpochMilli(custChargeDetailsPojo.getExpiry().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59)));
                                                    custServiceChargeIPDetails.setChargeId(custChargeDetailsPojo.getChargeid());
                                                    custServiceChargeIPDetails.setCustServiceMappingId(packageRel.getCustServiceMappingId());
                                                    custServiceChargeIPDetails.setCustId(custChargeOverrideDTO.getCustid());
                                                    custServiceChargeIPDetailsRepo.save(custServiceChargeIPDetails);
                                                    CustServiceChargeIPDtlsMessage custServiceChargeIPDtlsMessage = new CustServiceChargeIPDtlsMessage(custServiceChargeIPDetails);
                                                    //messageSender.send(custServiceChargeIPDtlsMessage, RabbitMqConstants.QUEUE_APIGW_CREATE_CUST_SERVICE_CHARGE_IP_DTLS);
                                                    kafkaMessageSender.send(new KafkaMessageData(custServiceChargeIPDtlsMessage, custServiceChargeIPDtlsMessage.getClass().getSimpleName(), "CREATE_CUST_SERVICE_CHARGE_IP_DTLS"));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //dbrService.addDbrForPrepaidCustomerForDirectCharges(custChargeDetails,customer);
                    }
                    custChargeDetails.setIsUsed(false);
                    CustChargeDetails details = entityRepository.save(custChargeDetails);
                    // direct charge installment
                    if(custChargeDetailsPojo.getInstallmentFrequency() != null && !custChargeDetailsPojo.getInstallmentFrequency().isEmpty()){
                        CustChargeInstallment custChargeInstallment = new CustChargeInstallment();
                        custChargeInstallment.setCustomerId(custChargeDetails.getCustomer().getId());
                        custChargeInstallment.setCustChargeDetails(details);

                        custChargeInstallment.setInstallmentFrequency(custChargeDetailsPojo.getInstallmentFrequency());
                        custChargeInstallment.setInstallmentNo(-1);
                        custChargeInstallment.setTotalInstallments(custChargeDetailsPojo.getTotalInstallments());

                        BigDecimal amountPerInstallment = calculateAmountPerInstallment(custChargeDetailsPojo.getPrice(), custChargeDetailsPojo.getTotalInstallments());
                        custChargeInstallment.setAmountPerInstallment(amountPerInstallment);

//                        String frequency = custChargeDetailsPojo.getInstallmentFrequency().toUpperCase();
//                        LocalDate startDate = LocalDate.now();
//                        custChargeInstallment.setInstallmentStartDate(startDate);
//
//                        Period periodBetweenInstallments;
//                        switch (frequency) {
//                            case CommonConstants.INSTALLMENT_FREQUENCY.MONTHLY:
//                                periodBetweenInstallments = Period.ofMonths(1);
//                                break;
//                            case CommonConstants.INSTALLMENT_FREQUENCY.QUARTERLY:
//                                periodBetweenInstallments = Period.ofMonths(3);
//                                break;
//                            case CommonConstants.INSTALLMENT_FREQUENCY.ANNUALLY:
//                                periodBetweenInstallments = Period.ofYears(1);
//                                break;
//                            default:
//                                throw new IllegalArgumentException("Unsupported installment frequency: " + frequency);
//                        }
//
//                        LocalDate nextInstallmentDate = startDate.plus(periodBetweenInstallments);
//
//                        custChargeInstallment.setNextInstallmentDate(nextInstallmentDate);
//                        custChargeInstallment.setLastInstallmentDate(startDate);
                        custChargeInstallment.setInstallmentEnabled(true);
                        custChargeInstallmentRepository.save(custChargeInstallment);
                        custChargeInstallments.add(custChargeInstallment);
                    }

                    chargeIdList.add(details.getId());
                    custChargeDetailsForRevenue.add(details);
                    custChargeDetailsPojo.setId(details.getId());
                }

                ChangePlanMessage changePlanMessage = new ChangePlanMessage();
                if (customer.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)) {
                    List<CustChargeDetailsRevenue> custChargeDetailsRevenues = new ArrayList<>();
                    for (CustChargeDetails data : custChargeDetailsForRevenue) {
                        CustChargeDetailsRevenue custChargeDetailsRevenue = new CustChargeDetailsRevenue(data);
                        custChargeDetailsRevenue.setIsRenew(false);
                        if(custChargeInstallments != null  && data.getInstallmentFrequency() != null && !data.getInstallmentFrequency().isEmpty()){
                            custChargeDetailsRevenue.setCustChargeInstallmentList(custChargeInstallments);
                        }
                        if (custChargeOverrideDTO.getIsRenew() != null)
                            custChargeDetailsRevenue.setIsRenew(custChargeOverrideDTO.getIsRenew());
                        custChargeDetailsRevenues.add(custChargeDetailsRevenue);
                    }
                    changePlanMessage.setCustChargeDetailsRevenues(custChargeDetailsRevenues);
                    changePlanMessage.setCustChargeIds(chargeIdList);
                    changePlanMessage.setType(CommonConstants.INVOICE_TYPE.CUSTOMER_CHARGE);
                    changePlanMessage.setCreatedById(getLoggedInUser().getStaffId());
                    if (custChargeOverrideDTO.getIsMvnoCharge() != null) {
                        changePlanMessage.setIsMvnoCustomer(custChargeOverrideDTO.getIsMvnoCharge());
                        changePlanMessage.setIspFromDate(custChargeOverrideDTO.getIspFromDate().toString());
                        changePlanMessage.setIspToDate(custChargeOverrideDTO.getIspToDate().toString());
                    }
                    if (!CollectionUtils.isEmpty(custChargeOverrideDTO.getDebitDocDetailIds())) {
                        changePlanMessage.setDebitDocDetailIds(custChargeOverrideDTO.getDebitDocDetailIds());
                    }
//					messageSenderRevenue.send(changePlanMessage, SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_SHARE_REVENUE);
                    kafkaMessageSender.send(new KafkaMessageData(changePlanMessage, ChangePlanMessage.class.getSimpleName(), "DIRECT_CHARG"));

                } else if (customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION)) {
                    List<CustChargeDetailsRevenue> custChargeDetailsRevenues = new ArrayList<>();
                    for (CustChargeDetails data : custChargeDetailsForRevenue) {
                        CustChargeDetailsRevenue custChargeDetailsRevenue = new CustChargeDetailsRevenue(data);
                        custChargeDetailsRevenue.setIsRenew(false);
                        if(custChargeInstallments != null){
                            custChargeDetailsRevenue.setCustChargeInstallmentList(custChargeInstallments);
                        }
						if(custChargeOverrideDTO.getIsRenew() != null)
							custChargeDetailsRevenue.setIsRenew(custChargeOverrideDTO.getIsRenew());custChargeDetailsRevenues.add(custChargeDetailsRevenue);
                    }
                    changePlanMessage.setCustChargeDetailsRevenues(custChargeDetailsRevenues);
                    changePlanMessage.setCustChargeIds(chargeIdList);
                    changePlanMessage.setType(CommonConstants.INVOICE_TYPE.CUSTOMER_CHARGE);
                    changePlanMessage.setCreatedById(getLoggedInUserId());
                    if (custChargeOverrideDTO.getIsMvnoCharge() != null) {
                        changePlanMessage.setIsMvnoCustomer(custChargeOverrideDTO.getIsMvnoCharge());
                        changePlanMessage.setIspFromDate(custChargeOverrideDTO.getIspFromDate().toString());
                        changePlanMessage.setIspToDate(custChargeOverrideDTO.getIspToDate().toString());
                    }
                    if (!CollectionUtils.isEmpty(custChargeOverrideDTO.getDebitDocDetailIds())) {
                        changePlanMessage.setDebitDocDetailIds(custChargeOverrideDTO.getDebitDocDetailIds());
                    }
                    kafkaMessageSender.send(new KafkaMessageData(changePlanMessage, ChangePlanMessage.class.getSimpleName(), "DIRECT_CHARG"));
//					messageSenderRevenue.send(changePlanMessage, SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_SHARE_REVENUE);
                }
            }
        }
        return custChargeOverrideDTO;
    }

    public LocalDate calculateNextBillDate(Integer billingCycle, Integer customerBillday) {
        LocalDate nextBillDate = null;
        if (billingCycle != null && customerBillday != null && billingCycle != 13) {
            nextBillDate = LocalDate.now().plusMonths(billingCycle);
            if (nextBillDate.getDayOfMonth() != customerBillday) {
                nextBillDate = nextBillDate.withDayOfMonth(customerBillday);
            }
        }
        return nextBillDate;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Tax', '1')")
    public List<CustChargeDetails> searchByChargeTypeAndCustomer(String chargeType, Customers cust) {
        return entityRepository.findByChargetypeAndCustomer(chargeType, cust);
    }

    public List<CustChargeDetailsPojo> findCustChargeByChargeCategory(Customers customers, String chargeCategory) {
        return entityRepository.getIpPurchasedCharge(customers.getId().longValue(), chargeCategory, false, false).stream().map(data -> custChargeDetailMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<CustChargeDetailsPojo> findCustChargeForRollback(Customers customers, String chargeCategory) {
        return entityRepository.getIpPurchasedChargeForRollback(customers.getId().longValue(), chargeCategory, false).stream().map(data -> custChargeDetailMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<CustChargeDetailsPojo> findCustChargeByChargeCategoryUsed(Customers customers, String chargeCategory) {
        return entityRepository.getIpPurchasedCharge(customers.getId().longValue(), chargeCategory, true, false).stream().map(data -> custChargeDetailMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public void saveAll(List<CustChargeDetails> chargeList) {
        entityRepository.saveAll(chargeList);
    }

    public List<CustChargeDetails> findByCustomer(Customers cust) {
        return entityRepository.findByCustomer(cust);
    }

    public List<CustChargeDetails> findByCustomerId(Integer cust) {
        return entityRepository.findByCustomer_Id(cust);
    }

    public CustChargeDetails findByChargeByid(Integer id) {
        return entityRepository.findById(id).orElse(null);
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Cust Reply Item");
        List<CustChargeDetailsPojo> custChargeDetailsPojos = getRepository().findAll().stream().map(data -> custChargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, CustChargeDetailsPojo.class, custChargeDetailsPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<CustChargeDetailsPojo> custChargeDetailsPojos = getRepository().findAll().stream().map(data -> custChargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, CustChargeDetailsPojo.class, custChargeDetailsPojos, null);
    }


    public void createCustomerChargeOverrideForShiftLocation(Integer custId, Integer chargeId, Double price, Integer billableCustomerId, String paymentOwner, Integer paymentOwnerId, Double discount) {
        List<Integer> chargeIdList = new ArrayList<>();
        Customers customer = customersRepository.findById(custId).get();
        if (customer != null) {
            Integer stateId = customer.getAddressList().stream().filter(data -> data.getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)).findAny().orElse(null).getStateId();
            if (chargeId != null) {
                CustChargeDetails custChargeDetails = new CustChargeDetails();
                custChargeDetails.setBillableCustomerId(billableCustomerId);
                custChargeDetails.setPrice(price);
                custChargeDetails.setActualprice(price);
                custChargeDetails.setCharge_date(LocalDateTime.now());
                custChargeDetails.setCustomer(customer);
                custChargeDetails.setType("One-time");
                custChargeDetails.setIsUsed(false);
                custChargeDetails.setIs_reversed(false);
                custChargeDetails.setBillTo("CUSTOMER");
                custChargeDetails.setNewAmount(0.0);
                custChargeDetails.setDbr(price);
                custChargeDetails.setPlanValidity(1);
                custChargeDetails.setValidity(1.0);
                custChargeDetails.setDiscount(discount);
                custChargeDetails.setUnitsOfValidity(CommonConstants.VALIDIDY_UNIT_DAYS);
                try {
                    Charge charge = chargeService.get(chargeId,customer.getMvnoId());
                    custChargeDetails.setChargetype(charge.getChargetype());
                    if (null != charge) {
                        custChargeDetails.setChargeid(charge.getId());

                        if (charge.getTax() != null) custChargeDetails.setTaxId(charge.getTax().getId());

                        TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(null, stateId, null, charge.getId());
                        Double taxAmount = taxService.taxCalculationByCharge(taxDetailCountReqDTO);
                        custChargeDetails.setTaxamount(taxAmount);
                        if (customer.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_PREPAID)) {
                            if (custChargeDetails.getType().equalsIgnoreCase("One-time")) {
                                Double newPrice = custChargeDetails.getPrice();
                                Double chargePriceIncludingTax = custChargeDetails.getPrice();
                                Double tax = 0.0;
                                for (int k = 0; k < taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().size(); k++) {
                                    tax = tax + chargePriceIncludingTax * taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                                    chargePriceIncludingTax += chargePriceIncludingTax * taxService.get(custChargeDetails.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                                }
                                newPrice = newPrice + tax;
                                custChargeDetails.setTaxamount(tax);
                                custChargeDetails.setDbr(newPrice);
                                custChargeDetails.setStartdate(LocalDate.now().atStartOfDay());
                                custChargeDetails.setEnddate(LocalDate.now().atStartOfDay());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                custChargeDetails.setIsUsed(false);
                List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerId(customer.getId());
                custChargeDetails.setCustPlanMapppingId(custPlanMappping.get(0).getId());
                custChargeDetails.setPlanid(custPlanMappping.get(0).getPlanId());
                CustChargeDetails details = entityRepository.save(custChargeDetails);
                chargeIdList.add(details.getId());

                ChangePlanMessage changePlanMessage = new ChangePlanMessage();
                if (customer.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)) {
//					Runnable chargeRunnable = new ChargeThread(custId, chargeIdList, customersService, 0L,paymentOwner,paymentOwnerId);
//					Thread invoiceThread = new Thread(chargeRunnable);
//					invoiceThread.start();


                    List<CustChargeDetailsRevenue> custChargeDetailsRevenues = new ArrayList<>();
//					for (CustChargeDetails data : details) {
                    CustChargeDetailsRevenue custChargeDetailsRevenue = new CustChargeDetailsRevenue(details);
                    custChargeDetailsRevenue.setIsRenew(false);
                    custChargeDetailsRevenues.add(custChargeDetailsRevenue);
//					}
                    changePlanMessage.setCustChargeDetailsRevenues(custChargeDetailsRevenues);
                    changePlanMessage.setCustChargeIds(chargeIdList);
                    changePlanMessage.setType(CommonConstants.INVOICE_TYPE.CUSTOMER_CHARGE);
                    changePlanMessage.setCreatedById(getLoggedInUser().getStaffId());
                    changePlanMessage.setIsMvnoCustomer(false);

//					messageSenderRevenue.send(changePlanMessage, SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_SHARE_REVENUE);
                    kafkaMessageSender.send(new KafkaMessageData(changePlanMessage, ChangePlanMessage.class.getSimpleName(), "DIRECT_CHARG"));
                }

                if (customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION)) {
//					Runnable chargeRunnable = new ChargeThread(true,custId, chargeIdList, customersService, 0L,paymentOwner,paymentOwnerId,null);
//					Thread invoiceThread = new Thread(chargeRunnable);
//					invoiceThread.start();
                    List<CustChargeDetailsRevenue> custChargeDetailsRevenues = new ArrayList<>();
//					for (CustChargeDetails data : custChargeDetailsForRevenue) {
                    CustChargeDetailsRevenue custChargeDetailsRevenue = new CustChargeDetailsRevenue(details);
                    custChargeDetailsRevenues.add(custChargeDetailsRevenue);
//					}
                    changePlanMessage.setCustChargeDetailsRevenues(custChargeDetailsRevenues);
                    changePlanMessage.setCustChargeIds(chargeIdList);
                    changePlanMessage.setType(CommonConstants.INVOICE_TYPE.CUSTOMER_CHARGE);
                    changePlanMessage.setIsMvnoCustomer(false);
//					messageSenderRevenue.send(changePlanMessage, SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_SHARE_REVENUE);,
                    kafkaMessageSender.send(new KafkaMessageData(changePlanMessage, ChangePlanMessage.class.getSimpleName(), "DIRECT_CHARG"));
                }
            }
        }
    }

    public CustChargeDetails updateStaticIpAddress(Integer custChargeId, String staticIPAddress, Date staticIPExpiryDate) {
        CustChargeDetails custChargeDetails = entityRepository.findById(custChargeId).get();
        CustChargeDetails details = new CustChargeDetails();
        CustServiceChargeIPDetails custServiceChargeIPDetails = new CustServiceChargeIPDetails();
        Integer count = 0;
        if (null != staticIPAddress) {
            count = custServiceChargeIPDetailsRepo.duplicateIPCheckAtSave(staticIPAddress);
        }
        if (count > 0) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Static IP Already in Use", null);
        } else {
            custServiceChargeIPDetails = custServiceChargeIPDetailsRepo.findByStaticIPAdrress(custChargeDetails.getStaticIPAdrress());
            if ((null != staticIPAddress) && (null == staticIPExpiryDate)) {
                custServiceChargeIPDetails.setStaticIPAdrress(staticIPAddress);
                custChargeDetails.setStaticIPAdrress(staticIPAddress);
            } else if (null != staticIPExpiryDate && (null == staticIPAddress)) {
                custChargeDetails.setEnddate((Instant.ofEpochMilli(staticIPExpiryDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.now())));
                custServiceChargeIPDetails.setStaticIPEndDate((Instant.ofEpochMilli(staticIPExpiryDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.now())));
            } else {
                custChargeDetails.setStaticIPAdrress(staticIPAddress);
                custServiceChargeIPDetails.setStaticIPAdrress(staticIPAddress);
                custChargeDetails.setEnddate((Instant.ofEpochMilli(staticIPExpiryDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.now())));
                custServiceChargeIPDetails.setStaticIPEndDate((Instant.ofEpochMilli(staticIPExpiryDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.now())));

            }
            CustServiceChargeIPDetails entity = custServiceChargeIPDetailsService.update(custServiceChargeIPDetails);
            details = this.update(custChargeDetails);
            CustServiceChargeIPDtlsMessage custServiceChargeIPDtlsMessage = new CustServiceChargeIPDtlsMessage(entity);
//			messageSender.send(custServiceChargeIPDtlsMessage, RabbitMqConstants.QUEUE_APIGW_UPDATE_CUST_SERVICE_CHARGE_IP_DTLS);
            kafkaMessageSender.send(new KafkaMessageData(custServiceChargeIPDtlsMessage, CustServiceChargeIPDtlsMessage.class.getSimpleName(), "UPDATE_CUST_SERVICE_CHARGE_IP_DTLS"));
        }
        return details;
    }

	public void UpdateCustomerChargeHistoryByUpdatePlan(String planType , Charge charge , Double newAmount,Integer planId){
       if(planType.equalsIgnoreCase(CommonConstants.PLAN_TYPE_POSTPAID)){
		   List<Integer> custCharhistoryIds = customerChargeHistoryRepo.getChargeHistoryIdsByPlanIdandchargeId(planId, charge.getId());
		   if(!custCharhistoryIds.isEmpty()){
			   Double taxAmount = taxService.getTaxAmountFromChargeAndPrice(charge , newAmount);
               transactionUtil.updateCustomerChargeHistory(custCharhistoryIds , newAmount,taxAmount);
			   UpdateChargeHistoryMessage updateChargeHistoryMessage = new UpdateChargeHistoryMessage();
			   updateChargeHistoryMessage.setNewAmount(newAmount);
			   updateChargeHistoryMessage.setTaxAmount(taxAmount);
			   updateChargeHistoryMessage.setCustCharhistoryIds(custCharhistoryIds);
			   kafkaMessageSender.send(new KafkaMessageData(updateChargeHistoryMessage, UpdateChargeHistoryMessage.class.getSimpleName()));
		   }
		   else{
			   System.out.println("custCharhistoryIds is empty code will not be executed.");
		   }
	   }
	   else{
		   System.out.println("Plan is prepaid cannot update existing charge.");
	   }
	}

    public BigDecimal calculateAmountPerInstallment(double actualPrice, Integer totalInstallments) {
        if (totalInstallments == null || totalInstallments == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal price = BigDecimal.valueOf(actualPrice);
        return price.divide(new BigDecimal(totalInstallments), 4, RoundingMode.HALF_UP);
    }

}
