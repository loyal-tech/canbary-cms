package com.adopt.apigw.modules.Cas.Service;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.Cas.Domain.*;
import com.adopt.apigw.modules.Cas.Mapper.CasMapper;
import com.adopt.apigw.modules.Cas.Model.CasMasterDTO;
import com.adopt.apigw.modules.Cas.Repository.CasParameterMappingRepocitory;
import com.adopt.apigw.modules.Cas.Repository.CasePackageRepository;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.service.common.EzBillServiceUtility;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.ezbill.entity.AllPackagesList;
import com.adopt.ezbill.entity.GetAllPackagesRequest;
import com.adopt.ezbill.entity.GetAllPackagesResponse;
import com.adopt.ezbill.service.EZBillService;
import com.adopt.ezbill.service.EZBillServiceImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CasMasterService extends ExBaseAbstractService2<CasMasterDTO, CasMaster, Long> {

    @Autowired
    CasePackageRepository repository;
    private final Logger logger= LoggerFactory.getLogger(CasMasterService.class);
    @Autowired
    private Tracer tracer;

    @Autowired
    private CasMasterRepository casMasterRepository;
    @Autowired
    private CasPackageMappingRepository casPackageMappingRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    CasParameterMappingRepocitory casParameterMappingRepocitory;

    @Autowired
    CreateDataSharedService createDataSharedService;

    public CasMasterService(CasePackageRepository repository, CasMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CasePackageService]";
    }

    @Override
    public boolean duplicateVerifyAtSave(String casname,Integer mvnoId) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (casname != null) {
            casname = casname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = repository.duplicateVerifyAtSave(casname);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(casname, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(casname, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;

    }

    @Override
    public boolean duplicateVerifyAtEdit(String casname, Integer casid,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (casname != null) {
            casname = casname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = repository.duplicateVerifyAtSave(casname);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(casname, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(casname, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) == 1) countEdit = repository.duplicateVerifyAtEdit(casname, casid);
                else {
                    if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = repository.duplicateVerifyAtEdit(casname, casid, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = repository.duplicateVerifyAtEdit(casname, casid, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
                }
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
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QCasMaster qCasMaster = QCasMaster.casMaster;
        BooleanExpression booleanExpression = qCasMaster.isNotNull().and(qCasMaster.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());

        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                booleanExpression = booleanExpression.and(qCasMaster.casname.containsIgnoreCase(genericSearchModel.getFilterValue()));

            }
        }

        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qCasMaster.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qCasMaster.mvnoId.eq(1).or(qCasMaster.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qCasMaster.buId.in(getBUIdsFromCurrentStaff()))));
        }
        logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Search CasMaster using keyword : "+filterList.get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");

        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
    }


    //Delete Verification
    public boolean deleteVerification(Integer id) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [deleteVerification()] ";

        boolean flag = false;
        try {

            Integer count = repository.deleteVerify(Long.valueOf(id));
            if (count == 1) { // Count == 1 due to cas is not bind with any services
                flag = true;
            } else {
                throw new RuntimeException(DeleteContant.CAS_EXIST);
            }

            if(productRepository.countAllByByCasId(id) > 0){
                throw new RuntimeException(DeleteContant.CAS_EXIST_IN_PRODUCT);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return flag;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<CasMaster> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if (getMvnoIdFromCurrentStaff(null) == 1)       // TODO: pass mvnoID manually 6/5/2025
            paginationList = repository.findAllByIsDeletedIsFalse(pageRequest);
        else if (null == filterList || filterList.isEmpty())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().isEmpty())
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = repository.findAllByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(null), 1), pageRequest);
            else
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = repository.findAllByIsDeletedIsFalseAndMvnoIdInAndAndBuIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(null), 1), getBUIdsFromCurrentStaff(), pageRequest);
        if (null != paginationList && !paginationList.getContent().isEmpty()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }


    @Transactional
    public GenericDataDTO refreshCasPackage(Long casId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        CasMaster casMaster = casMasterRepository.findById(casId).orElse(null);
        GetAllPackagesRequest getAllPackagesRequest = new GetAllPackagesRequest();
        GetAllPackagesResponse getAllPackagesResponse = new GetAllPackagesResponse();
        EZBillService ezBillService = new EZBillServiceImpl();
        List<AllPackagesList> allPackagesLists = new ArrayList<>();
        if (casMaster != null) {
            String authToken = "5a4c89b6321cd3.84561630";
            authToken = EzBillServiceUtility.getAuthTokenFromCAS(casMaster, authToken);
            getAllPackagesRequest.setAuthToken(authToken);
            getAllPackagesResponse = ezBillService.getAllCASPackage(getAllPackagesRequest, casMaster.getEndpoint());

            switch (casMaster.getCasname()) {
                case CommonConstants.EZ_BILL_CAS_NAMES.SAFEVIEW:
                    allPackagesLists = getAllPackagesResponse.getAllPackagesList().stream()
                            .filter(allPackagesList -> allPackagesList.getDisplayName().equalsIgnoreCase(CommonConstants.EZ_BILL_CAS_NAMES.SAFEVIEW))
                            .collect(Collectors.toList());
                    break;
                case CommonConstants.EZ_BILL_CAS_NAMES.SAFEVIEW1:
                    allPackagesLists = getAllPackagesResponse.getAllPackagesList().stream().filter(allPackagesList -> allPackagesList.getDisplayName().equalsIgnoreCase(CommonConstants.EZ_BILL_CAS_NAMES.SAFEVIEW1)).collect(Collectors.toList());
                    break;
                case CommonConstants.EZ_BILL_CAS_NAMES.VERIMATRIX:
                    allPackagesLists = getAllPackagesResponse.getAllPackagesList().stream().filter(allPackagesList -> allPackagesList.getDisplayName().equalsIgnoreCase(CommonConstants.EZ_BILL_CAS_NAMES.VERIMATRIX)).collect(Collectors.toList());
                    break;
            }
            if (allPackagesLists.size() > 0) {
                casPackageMappingRepository.deleteByCasMasterId(casMaster.getId());
                for (AllPackagesList allPackagesList : allPackagesLists) {
                    CasPackageMapping casPackageMapping = new CasPackageMapping();
                    casPackageMapping.setPackageName(allPackagesList.getPackageName());
                    casPackageMapping.setCasMasterId(casMaster.getId());
                    casPackageMapping.setPackageId((long) allPackagesList.getPackageId());
                    casPackageMapping.setIsDeleted(false);
                    casPackageMappingRepository.save(casPackageMapping);
                    genericDataDTO.setResponseMessage("Records fetched successfully.");
                }
            }else{
                genericDataDTO.setResponseMessage("No records found.");
            }
        }
        return genericDataDTO;
    }


    public List<CasMaster> getAllActiveEntities(Integer mvnoId) {
        QCasMaster qCasMaster=QCasMaster.casMaster;
        BooleanExpression booleanExpression=QCasMaster.casMaster.isNotNull().and(QCasMaster.casMaster.isDeleted.eq(false));
        booleanExpression=booleanExpression.and(qCasMaster.status.equalsIgnoreCase("Active"));
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qCasMaster.mvnoId.in(mvnoId, 1));
        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qCasMaster.mvnoId.eq(1).or(qCasMaster.mvnoId.eq(mvnoId).and(qCasMaster.buId.in(getBUIdsFromCurrentStaff()))));

      List<CasMaster> casMasters= IterableUtils.toList(casMasterRepository.findAll(booleanExpression));
       return casMasters;
    }


    @Override
    public CasMasterDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }

    @Override
    public CasMasterDTO updateEntity(CasMasterDTO entityDTO) throws Exception {
        List<CasParameterMapping> casParameterMappingList=new ArrayList<>();
                entityDTO.getCasParameterMappings().stream().forEach(r->{
                        if(r.getId()!=null){
                               r.setCasId(entityDTO.getId());
                                casParameterMappingList.add(r);
                          }

                });
         super.updateEntity(entityDTO);
        if(casParameterMappingList.size()>0) {
                         casParameterMappingRepocitory.saveAll(casParameterMappingList);
        }
        return entityDTO;
    }

    public void sendCreatedDataShared(CasMasterDTO casMasterDTO, Integer operation) throws Exception{
        try {
            CasMaster casMaster = casMasterRepository.findById(casMasterDTO.getId()).orElse(null);
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                createDataSharedService.sendEntitySaveDataForAllMicroService(casMaster);
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                createDataSharedService.updateEntityDataForAllMicroService(casMaster);
            } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
                createDataSharedService.deleteEntityDataForAllMicroService(casMaster);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }
}
