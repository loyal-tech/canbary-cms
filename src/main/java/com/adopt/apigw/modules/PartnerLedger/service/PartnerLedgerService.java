package com.adopt.apigw.modules.PartnerLedger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedger;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.mapper.PartnerLedgerMapper;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerLedgerBalanceDTO;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerLedgerDTO;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerLedgerRepository;
import com.adopt.apigw.utils.CommonConstants;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartnerLedgerService extends ExBaseAbstractService<PartnerLedgerDTO, PartnerLedger, Long> {
    public PartnerLedgerService(PartnerLedgerRepository repository, PartnerLedgerMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    private PartnerLedgerMapper partnerLedgerMapper;
    @Autowired
    private PartnerLedgerRepository partnerLedgerRepository;

    public void setPartnerLedger(Integer partnerId) throws Exception {
        PartnerLedgerDTO partnerLedgerDTO = new PartnerLedgerDTO();
        partnerLedgerDTO.setPartnerId(partnerId);
        partnerLedgerDTO.setTotalpaid(0.0);
        partnerLedgerDTO.setTotaldue(0.0);
        partnerLedgerRepository.save(partnerLedgerMapper.dtoToDomain(partnerLedgerDTO, new CycleAvoidingMappingContext()));
    }

    public void addBalance(PartnerLedgerBalanceDTO dto) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [addBalance()] ";
        try {
            PartnerLedger partnerLedger = getByPartnerId(dto.getPartner_id());
            if (null != partnerLedger) {
                if (partnerLedger.getTotaldue() == null) {
                    partnerLedger.setTotaldue(0.0);
                }
                if (dto.getAmount() == null) {
                    dto.setAmount(0.0);
                }
                if (dto.getCredit() == null) {
                    dto.setCredit(0);
                }
                partnerLedger.setTotaldue(Double.parseDouble(new DecimalFormat("##.####").format(partnerLedger.getTotaldue() + dto.getAmount())));
                if (dto.getPaymentdate() != null) {
                    partnerLedger.setUpdatedate(dto.getPaymentdate());
                }
                //partnerLedgerRepository.save(partnerLedger);
            } else
                throw new RuntimeException("Ledger Not Found For Partner!");

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public void reverseBalance(PartnerLedgerBalanceDTO dto) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [reverseBalance()] ";
        try{
            PartnerLedger partnerLedger = getByPartnerId(dto.getPartner_id());
            if (null != partnerLedger) {
                if (partnerLedger.getTotaldue() == null) {
                    partnerLedger.setTotaldue(0.0);
                }
                if (dto.getAmount() == null) {
                    dto.setAmount(0.0);
                }
                partnerLedger.setTotaldue(Double.parseDouble(new DecimalFormat("##.##").format(partnerLedger.getTotaldue() + dto.getAmount())));
                if (dto.getPaymentdate() != null) {
                    partnerLedger.setUpdatedate(dto.getPaymentdate());
                }
                partnerLedgerRepository.save(partnerLedger);
            } else
                throw new RuntimeException("Ledger Not Found For Partner!");
        }catch (Exception ex){
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public PartnerLedger getByPartnerId(Integer id) throws Exception {
        return partnerLedgerRepository.findByPartner_Id(id);
    }

    public void setLedgerFromDetails(List<PartnerLedgerDetails> ledgerDetails, Integer partnerId) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [setLedgerFromDetails()] ";
        try {
            PartnerLedger partnerLedger = getByPartnerId(partnerId);
            if (null != partnerLedger) {
                if (null != ledgerDetails && 0 < ledgerDetails.size()) {

                    List<PartnerLedgerDetails> creditDetails = ledgerDetails.stream().filter(data -> null != data.getTranstype()
                            && data.getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT)).collect(Collectors.toList());

                    if (null != creditDetails && 0 < creditDetails.size()) {
                        partnerLedger.setTotalpaid(partnerLedger.getTotalpaid() + creditDetails.stream().mapToDouble(PartnerLedgerDetails::getAmount).sum());
                    }

                    List<PartnerLedgerDetails> debitDetails = ledgerDetails.stream().filter(data -> null != data.getTranstype()
                            && data.getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)).collect(Collectors.toList());

                    if (null != debitDetails && 0 < debitDetails.size()) {
                        partnerLedger.setTotaldue(partnerLedger.getTotaldue() + debitDetails.stream().mapToDouble(PartnerLedgerDetails::getAmount).sum());
                    }

                    updateEntity(partnerLedgerMapper.domainToDTO(partnerLedger, new CycleAvoidingMappingContext()));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public String getModuleNameForLog() {
        return "[PartnerLedgerService]";
    }
}
