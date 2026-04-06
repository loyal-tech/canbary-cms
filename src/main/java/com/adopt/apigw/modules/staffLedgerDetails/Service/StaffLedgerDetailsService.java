package com.adopt.apigw.modules.staffLedgerDetails.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.RvenueClient.RevenueClient;
import com.adopt.apigw.modules.StaffLedgerTransaction.StaffLedgerTransactionMapping;
import com.adopt.apigw.modules.StaffLedgerTransaction.StaffLedgerTransactionMappingDTO;
import com.adopt.apigw.modules.StaffLedgerTransaction.StaffLedgerTransactionMappingMapper;
import com.adopt.apigw.modules.StaffLedgerTransaction.StaffLedgerTransactionRepository;
import com.adopt.apigw.repository.postpaid.CreditDocRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.staffLedgerDetails.dto.StaffLedgerDetailsDto;
import com.adopt.apigw.modules.staffLedgerDetails.entity.QStaffLedgerDetails;
import com.adopt.apigw.modules.staffLedgerDetails.entity.StaffLedgerDetails;
import com.adopt.apigw.modules.staffLedgerDetails.mapper.StaffLedgerDetailsMapper;
import com.adopt.apigw.modules.staffLedgerDetails.repository.StaffLedgerDetailsRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.querydsl.core.types.dsl.BooleanExpression;

import static com.adopt.apigw.core.utillity.log.ApplicationLogger.logger;

@Service
public class StaffLedgerDetailsService extends ExBaseAbstractService2<StaffLedgerDetailsDto, StaffLedgerDetails, Long> {

    @Autowired
    StaffLedgerDetailsRepository repository;

    @Autowired
    StaffLedgerDetailsMapper mapper;

    @Autowired
    private StaffLedgerTransactionMappingMapper staffLedgerTransactionMappingMapper;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    private StaffLedgerTransactionRepository staffLedgerTransactionRepository;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private CreditDocRepository creditDocRepository;


    public StaffLedgerDetailsService(StaffLedgerDetailsRepository repository, StaffLedgerDetailsMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "{StaffLedgerDetailsService}";
    }

    public StaffLedgerDetailsDto save(StaffLedgerDetailsDto entityDTO) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [save()] ";

        try {

            StaffLedgerDetails dataDTO = new StaffLedgerDetails();
            dataDTO.setAction(entityDTO.getAction());
            StaffUser staffUser = staffUserRepository.findById(entityDTO.getId()).orElse(null);
            if (entityDTO.getAmount() > staffUser.getAvailableAmount()) {
                throw new CustomValidationException(400, "Transfer amount can't be more than your wallet amount", null);
            }
            dataDTO.setStaff(staffUser);
            dataDTO.setDate(entityDTO.getDate());
            dataDTO.setAmount(entityDTO.getAmount());
            dataDTO.setBankId(entityDTO.getBankId());
            dataDTO.setBankName(entityDTO.getBankName());
            dataDTO.setPaymentMode(entityDTO.getPaymentMode());
            dataDTO.setRemarks(entityDTO.getRemarks());
            dataDTO.setTransactionType("DR");
            dataDTO.setAction("Withdraw");
            dataDTO.setChequeno(entityDTO.getChequeno());
            dataDTO.setChequedate(entityDTO.getChequedate());
            dataDTO.setStatus(CommonConstants.STAFF_WALLET_STATUS.CLEAR);
            dataDTO = repository.save(dataDTO);

            Double drAmount = 0.00;
            Double transferred = staffUser.getTotalTransferred();
            if (dataDTO.getTransactionType().equalsIgnoreCase("Dr")) {
                if (transferred == null) {
                    transferred = 0.000;
                }
                drAmount += transferred + dataDTO.getAmount();
            }
            staffUser.setTotalTransferred(drAmount);
            Double collectedAmt = staffUser.getTotalCollected();
            if (collectedAmt == null) {
                collectedAmt = 0.000;
                staffUser.setTotalCollected(collectedAmt);
            }
            Double availableAmt = collectedAmt - staffUser.getTotalTransferred();
            staffUser.setAvailableAmount(availableAmt);
            staffUserRepository.save(staffUser);

            return mapper.domainToDTO(dataDTO, new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }

    }

    public List<StaffLedgerDetailsDto> getStaffLedgerDetailsbyStaffId(Integer id) {
        QStaffLedgerDetails qStaffLedgerDetails = QStaffLedgerDetails.staffLedgerDetails;
        BooleanExpression booleanExpression = qStaffLedgerDetails.isNotNull().and(qStaffLedgerDetails.staff.id.eq(id));
        List<StaffLedgerDetails> staffLedgerDetails = (List<StaffLedgerDetails>) repository.findAll(booleanExpression);

        List<Long> creditDocIds = staffLedgerDetails.stream().map(StaffLedgerDetails::getCreditDocId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        List<Object[]> invoiceResults = creditDocRepository.getInvoiceIdsByCreditDocIds(creditDocIds);

        Map<Long, Integer> creditDocIdToInvoiceIdMap = new HashMap<>();
        for (Object[] row : invoiceResults) {
            Long creditDocId = ((Number) row[0]).longValue();
            Integer invoiceId = row[1] != null ? ((Number) row[1]).intValue() : null;
            creditDocIdToInvoiceIdMap.put(creditDocId, invoiceId);
        }

        List<Integer> invoiceIds = creditDocIdToInvoiceIdMap.values().stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());

        Map<Integer, List<String>> revenueMap = new HashMap<>();
        if (!invoiceIds.isEmpty()) {
            try {
                String token=creditDocService.getToken();
                ResponseEntity<Map<Integer, List<String>>> response = revenueClient.getInvoiceNumber("Bearer " + token, new ArrayList<>(invoiceIds));
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    revenueMap = response.getBody();
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Error calling RevenueClient: {}", e.getMessage(), e);
            }
        }

        Map<Integer, List<String>> finalRevenueMap = revenueMap;
        Map<Long, Integer> finalCreditMap = creditDocIdToInvoiceIdMap;

        List<StaffLedgerDetailsDto> staffLedgerDetailsDtos = mapper.domainToDTO(
                staffLedgerDetails.stream()
                        .sorted(Comparator.comparing(StaffLedgerDetails::getCreatedate))
                        .peek(detail -> {
                            if (detail.getCustId() != null) {
                                Optional<String> currencyOpt = customersRepository.findCurrencyByCustomerId(Math.toIntExact(detail.getCustId()));
                                currencyOpt.ifPresent(currency -> detail.setCurrency(currency));
                                String custName = customersRepository.findUsernameById(Math.toIntExact(detail.getCustId()));
                                if (custName != null) detail.setCustName(custName);
                            }
                            if (detail.getCreditDocId() != null) {
                                String result = "Advance Payment";
                                Integer invoiceId = finalCreditMap.get(detail.getCreditDocId());
                                if (invoiceId != null && finalRevenueMap.containsKey(invoiceId)) {
                                    List<String> debitNumbers = finalRevenueMap.get(invoiceId);
                                    if (debitNumbers != null && !debitNumbers.isEmpty()) {
                                        result = debitNumbers.get(0);
                                    }
                                }
                                detail.setDebitDocumentNumber(result);
                            }

                        })
                        .collect(Collectors.toList()),
                new CycleAvoidingMappingContext()
        );
        finalRevenueMap.clear();
        finalCreditMap.clear();
        return  staffLedgerDetailsDtos;
    }

    public void insertTransfferedAmountinLedger(List<Integer> ledgerIds, List<Double> amountList) {
        Integer transferredid = repository.findAllByTransactionType("DR").stream().sorted((o1, o2) -> o2.getId() - o1.getId()).findFirst().get().getId();
        int i = 0;
        for (i = 0; i <= amountList.size() - 1; i++) {
            Long id = ledgerIds.get(i).longValue();
            QStaffLedgerDetails qStaffLedgerDetails = QStaffLedgerDetails.staffLedgerDetails;
            BooleanExpression booleanExpression = qStaffLedgerDetails.id.eq(ledgerIds.get(i));
            Optional<StaffLedgerDetails> staffLedgerDetails = repository.findOne(booleanExpression);
            BooleanExpression booleanExpression1 = qStaffLedgerDetails.transactionType.eq("DR");
            if (staffLedgerDetails.isPresent()) {
                staffLedgerDetails.get().setTransferredamount(amountList.get(i));
                repository.save(staffLedgerDetails.get());
                StaffLedgerTransactionMapping staffLedgerTransactionMapping = new StaffLedgerTransactionMapping();
                staffLedgerTransactionMapping.setTransfferedamount(amountList.get(i));
                staffLedgerTransactionMapping.setPaymentid(ledgerIds.get(i));
                staffLedgerTransactionMapping.setTransfferedid(transferredid);
                staffLedgerTransactionMapping.setDate(LocalDate.now());
                staffLedgerTransactionRepository.save(staffLedgerTransactionMapping);
            }
        }

    }

    public void setStatusForStaffWalletAdjustedAmount(StaffLedgerDetailsDto entityDTO) {
        if (entityDTO.getPaymentMode().equalsIgnoreCase(CommonConstants.STAFF_PAYMENT_MODE_TYPE_CHEQUE)){
            adjustStaffLedgerForCheque(entityDTO);
        } else {
            adjustStaffLedgerForCash(entityDTO);
        }
    }

    public void adjustStaffLedgerForCheque(StaffLedgerDetailsDto entityDTO) {
        QStaffLedgerDetails qStaffLedgerDetails = QStaffLedgerDetails.staffLedgerDetails;
        BooleanExpression booleanExpression = qStaffLedgerDetails.staff.id.eq(entityDTO.getId()).and(qStaffLedgerDetails.chequeno.eq(entityDTO.getChequeno())).and(qStaffLedgerDetails.transactionType.eq("CR")).and(qStaffLedgerDetails.paymentMode.equalsIgnoreCase(CommonConstants.STAFF_PAYMENT_MODE_TYPE_CHEQUE)).and(qStaffLedgerDetails.status.notEqualsIgnoreCase(CommonConstants.STAFF_WALLET_STATUS.SETTELED));
        List<StaffLedgerDetails> staffLedgerDetails = (List<StaffLedgerDetails>) repository.findAll(booleanExpression);
        if (staffLedgerDetails != null && !staffLedgerDetails.isEmpty()) {
            for (StaffLedgerDetails ledgerDetail : staffLedgerDetails) {
                ledgerDetail.setStatus(CommonConstants.STAFF_WALLET_STATUS.SETTELED);
                ledgerDetail.setTransferredamount(entityDTO.getAmount());
                repository.saveAndFlush(ledgerDetail);
            }
        }

    }

    public void adjustStaffLedgerForCash(StaffLedgerDetailsDto entityDTO) {
        QStaffLedgerDetails qStaffLedgerDetails = QStaffLedgerDetails.staffLedgerDetails;
        BooleanExpression booleanExpression = qStaffLedgerDetails.staff.id.eq(entityDTO.getId()).and(qStaffLedgerDetails.transactionType.eq("CR")).and(qStaffLedgerDetails.paymentMode.equalsIgnoreCase(CommonConstants.PAYMENT_MODE_TYPE_CASH_CAPS)).and(qStaffLedgerDetails.status.notEqualsIgnoreCase(CommonConstants.STAFF_WALLET_STATUS.SETTELED));
        List<StaffLedgerDetails> staffLedgerDetails = (List<StaffLedgerDetails>) repository.findAll(booleanExpression);
        if (staffLedgerDetails != null && !staffLedgerDetails.isEmpty()) {
            Collections.sort(staffLedgerDetails, Comparator.comparing(StaffLedgerDetails::getDate));
            Double debitAmount = entityDTO.getAmount();
            for (StaffLedgerDetails ledgerDetail : staffLedgerDetails) {
                if (debitAmount != 0) {
                    if (ledgerDetail.getStatus().equals(CommonConstants.STAFF_WALLET_STATUS.PENDING)) {
                        if (Objects.equals(debitAmount, ledgerDetail.getAmount())) {
                            ledgerDetail.setStatus(CommonConstants.STAFF_WALLET_STATUS.SETTELED);
                            ledgerDetail.setTransferredamount(debitAmount);
                            debitAmount = 0D;
                        } else if (debitAmount < ledgerDetail.getAmount()) {
                            ledgerDetail.setStatus(CommonConstants.STAFF_WALLET_STATUS.PARTIALY_PENDING);
                            ledgerDetail.setTransferredamount(debitAmount);
                            debitAmount = 0D;
                        } else if (debitAmount > ledgerDetail.getAmount()) {
                            ledgerDetail.setStatus(CommonConstants.STAFF_WALLET_STATUS.SETTELED);
                            ledgerDetail.setTransferredamount(ledgerDetail.getAmount());
                            debitAmount = debitAmount - ledgerDetail.getAmount();
                        }
                    } else if (ledgerDetail.getStatus().equals(CommonConstants.STAFF_WALLET_STATUS.PARTIALY_PENDING)) {
                        Double remainAmount = ledgerDetail.getAmount() - ledgerDetail.getTransferredamount();
                        if (Objects.equals(debitAmount, remainAmount)) {
                            ledgerDetail.setStatus(CommonConstants.STAFF_WALLET_STATUS.SETTELED);
                            ledgerDetail.setTransferredamount(Double.sum(ledgerDetail.getTransferredamount(), debitAmount));
                            debitAmount = 0D;
                        } else if (debitAmount < remainAmount) {
                            ledgerDetail.setStatus(CommonConstants.STAFF_WALLET_STATUS.PARTIALY_PENDING);
                            ledgerDetail.setTransferredamount(Double.sum(ledgerDetail.getTransferredamount(), debitAmount));
                            debitAmount = 0D;
                        } else if (debitAmount > remainAmount) {
                            ledgerDetail.setStatus(CommonConstants.STAFF_WALLET_STATUS.SETTELED);
                            ledgerDetail.setTransferredamount(ledgerDetail.getAmount());
                            debitAmount = debitAmount - remainAmount;
                        }
                    }
                    repository.saveAndFlush(ledgerDetail);
                }

            }
        }
    }

    public List<StaffLedgerTransactionMappingDTO> getStaffLedgerDetailsbyTransfered(Integer id) {
        List<StaffLedgerTransactionMapping> staffLedgerTransactionMappingList = staffLedgerTransactionRepository.findAllByTransfferedid(id);
        return staffLedgerTransactionMappingMapper.domainToDTO(staffLedgerTransactionMappingList.stream().sorted(Comparator.comparing(StaffLedgerTransactionMapping::getDate)).collect(Collectors.toList()), new CycleAvoidingMappingContext());
    }

    public Double getTotalAmountFromID(Integer id) {
        StaffLedgerDetails staffLedgerDetails = repository.findById(id);
        return staffLedgerDetails.getAmount();
    }

    public List<StaffLedgerDetailsDto> getStaffLedgerDetailsbyStaffIdandPaymentMode(Integer id, String payment) {
        QStaffLedgerDetails qStaffLedgerDetails = QStaffLedgerDetails.staffLedgerDetails;
        BooleanExpression booleanExpression = qStaffLedgerDetails.isNotNull().and(qStaffLedgerDetails.staff.id.eq(id));
        booleanExpression = booleanExpression.and(qStaffLedgerDetails.paymentMode.equalsIgnoreCase(payment));
        List<StaffLedgerDetails> staffLedgerDetails = (List<StaffLedgerDetails>) repository.findAll(booleanExpression);
//        staffLedgerDetails.stream().sorted(Comparator.comparing(StaffLedgerDetails::getCreatedate)).collect(Collectors.toList());
        return mapper.domainToDTO(staffLedgerDetails.stream().sorted(Comparator.comparing(StaffLedgerDetails::getCreatedate)).collect(Collectors.toList()), new CycleAvoidingMappingContext());
    }

    @Override
    public StaffLedgerDetailsDto getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }
}

