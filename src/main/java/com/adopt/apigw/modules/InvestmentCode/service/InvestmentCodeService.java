package com.adopt.apigw.modules.InvestmentCode.service;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveInvestmentCodeSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateInvestmentCodeSharedDataMessage;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.model.postpaid.QPlanService;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.adopt.apigw.modules.BusinessVerticals.domain.BusinessVerticals;
import com.adopt.apigw.modules.BusinessVerticals.domain.QBusinessVerticals;
import com.adopt.apigw.modules.InvestmentCode.DTO.InvestmentCodeDto;
import com.adopt.apigw.modules.InvestmentCode.Domain.InvestmentCode;
import com.adopt.apigw.modules.InvestmentCode.Domain.QInvestmentCode;
import com.adopt.apigw.modules.InvestmentCode.repository.InvestmentCodeRepository;
import com.adopt.apigw.modules.InvestmentCodeBUmapping.IcNameBuMapping;
import com.adopt.apigw.modules.InvestmentCodeBUmapping.IcNameBuMappingRepo;
import com.adopt.apigw.modules.InvestmentCodeBUmapping.QIcNameBuMapping;
import com.adopt.apigw.repository.postpaid.PlanServiceRepository;
import com.adopt.apigw.spring.LoggedInUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvestmentCodeService extends ExBaseAbstractService<InvestmentCodeDto, InvestmentCode,Long> {

    @Autowired
    InvestmentCodeRepository investmentCodeRepository;


    @Autowired
    IcNameBuMappingRepo repo;

    @Autowired
    BusinessUnitRepository businessUnitRepository;

    @Autowired
    PlanServiceRepository planServiceRepository;


    public InvestmentCodeService(JpaRepository<InvestmentCode, Long> repository, IBaseMapper<InvestmentCodeDto, InvestmentCode> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[InvestmentCodeService]";
    }

    @Override
    public boolean duplicateVerifyAtSave(String icName) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (icName != null) {
            icName = icName.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = investmentCodeRepository.duplicateVerifyAtSave(icName);
            else count = investmentCodeRepository.duplicateVerifyAtSave(icName, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateVerifyAtSaveForCode(String icCode) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (icCode != null) {
            icCode = icCode.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = investmentCodeRepository.duplicateVerifyAtSave(icCode);
            else count = investmentCodeRepository.duplicateVerifyAtSaveForCode(icCode, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public InvestmentCode getById(Long id) {
        return investmentCodeRepository.findById(id).get();
    }

    public boolean duplicateVerifyAtEdit(String icname, Long id) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (icname != null) {
            icname = icname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = investmentCodeRepository.duplicateVerifyAtSave(icname);
            else count = investmentCodeRepository.duplicateVerifyAtSave(icname, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) == 1)
                    countEdit = investmentCodeRepository.duplicateVerifyAtEdit(icname, id);
                else countEdit = investmentCodeRepository.duplicateVerifyAtEdit(icname, id, mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getInvestmentCodeByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getInvestmentCodeByName(String icname, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QInvestmentCode qInvestmentCode=QInvestmentCode.investmentCode;
            Page<InvestmentCode> investmentCodesList = null;
            BooleanExpression booleanExpression = qInvestmentCode.isNotNull()
                    .and(qInvestmentCode.isDeleted.eq(false))
                    .and(qInvestmentCode.icname.likeIgnoreCase("%" + icname + "%").or(qInvestmentCode.iccode.containsIgnoreCase(icname).or(qInvestmentCode.status.equalsIgnoreCase(icname))));
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) {
                investmentCodesList = investmentCodeRepository.findAll(booleanExpression, pageRequest);
            }else {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qInvestmentCode.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                investmentCodesList = investmentCodeRepository.findAll(booleanExpression, pageRequest);
            }
            if (null != investmentCodesList && 0 < investmentCodesList.getSize()) {
                makeGenericResponse(genericDataDTO, investmentCodesList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public List<Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = new ArrayList<Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }

    public List<InvestmentCode> getIcname(List<Long> buIds) {
        try {
            List<BusinessUnit> businnesUnit =businessUnitRepository.findAllByIdIn(buIds);
            List<IcNameBuMapping> mapping = repo.findAllByBusinessUnitidIn(businnesUnit);
            List<Long> IcCodes = mapping.stream().map(i -> i.getInvestmentCodeid().getId()).distinct().collect(Collectors.toList());

            List<InvestmentCode> investmentCodes = investmentCodeRepository.findAllByIdIn(IcCodes);
            List<InvestmentCode> investmentCodeList = investmentCodes.stream().filter(i ->i.getStatus().equalsIgnoreCase("Active") && !i.getIsDeleted() && i.getStatus().equalsIgnoreCase("Active")).collect(Collectors.toList());
            return investmentCodeList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = investmentCodeRepository.deleteVerifyForInvestmentCode(Long.valueOf(id));
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    public List<String> getIcnameListByBuId(Long id) {
        try{
            QIcNameBuMapping qIcNameBuMapping=QIcNameBuMapping.icNameBuMapping;
            BooleanExpression exp=qIcNameBuMapping.isDeleted.eq(false).and(qIcNameBuMapping.businessUnitid.id.eq(id));
            List<IcNameBuMapping> icNameBuMappingList= (List<IcNameBuMapping>) repo.findAll(exp);
            List<String> icnames=new ArrayList<>();
            if (icNameBuMappingList.size()>0){
                for (IcNameBuMapping icname:icNameBuMappingList){
                    String names = icname.getInvestmentCodeid().getIcname();
                    icnames.add(names);
                }
            }
            return icnames;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<InvestmentCode> removebindedInvestmet(List<InvestmentCode> investmentCodeList) {

        QPlanService qPlanService = QPlanService.planService;
        List<Long> ids = investmentCodeList.stream()
                .map(InvestmentCode::getId)
                .collect(Collectors.toList());
        BooleanExpression booleanExpression = qPlanService.isNotNull().and(qPlanService.investmentid.in(ids));
        List<PlanService> planServiceList = (List<PlanService>) planServiceRepository.findAll(booleanExpression);
        List<Long> iccodeids = planServiceList.stream().map(planService -> planService.getInvestmentid()).collect(Collectors.toList());
        planServiceList.stream()
                .map(PlanService::getInvestmentid)
                .forEach(ids::remove);
        investmentCodeList.removeIf(ic -> iccodeids.contains(ic.getId()));
        return investmentCodeList;

    }



    //save recieved data from new api

    public void saveInvestMentCode(SaveInvestmentCodeSharedDataMessage message){
        InvestmentCode invc = new InvestmentCode();

        invc.setId(message.getId());
        invc.setStatus(message.getStatus());
        invc.setMvnoId(message.getMvnoId());
        invc.setIcname(message.getIcname());
        invc.setIccode(message.getIccode());
        invc.setIsDeleted(message.getIsDeleted());
        invc.setCreatedById(message.getCreatedById());
        invc.setLastModifiedById(message.getLastModifiedById());
        invc.setCreatedByName(message.getCreatedByName());
        invc.setLastModifiedByName(message.getLastModifiedByName());

        investmentCodeRepository.save(invc);
    }


    public void updateInvestMentCode(UpdateInvestmentCodeSharedDataMessage message){
        InvestmentCode invc = investmentCodeRepository.findById(message.getId()).orElse(null);
        if(invc!=null){
            invc.setId(message.getId());
            invc.setStatus(message.getStatus());
            invc.setMvnoId(message.getMvnoId());
            invc.setIcname(message.getIcname());
            invc.setIccode(message.getIccode());
            invc.setIsDeleted(message.getIsDeleted());
            invc.setCreatedById(message.getCreatedById());
            invc.setLastModifiedById(message.getLastModifiedById());
            invc.setCreatedByName(message.getCreatedByName());
            invc.setLastModifiedByName(message.getLastModifiedByName());
            investmentCodeRepository.save(invc);
        } else {
            InvestmentCode invc1 = new InvestmentCode();
            invc1.setId(message.getId());
            invc1.setStatus(message.getStatus());
            invc1.setMvnoId(message.getMvnoId());
            invc1.setIcname(message.getIcname());
            invc1.setIccode(message.getIccode());
            invc1.setIsDeleted(message.getIsDeleted());
            invc1.setCreatedById(message.getCreatedById());
            invc1.setLastModifiedById(message.getLastModifiedById());
            invc1.setCreatedByName(message.getCreatedByName());
            invc1.setLastModifiedByName(message.getLastModifiedByName());
            investmentCodeRepository.save(invc1);
        }

    }

}
