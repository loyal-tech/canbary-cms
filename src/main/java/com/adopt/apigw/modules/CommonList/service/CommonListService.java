package com.adopt.apigw.modules.CommonList.service;

import com.adopt.apigw.constants.AuditLogConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.CommonList.domain.CommonList;
import com.adopt.apigw.modules.CommonList.mapper.CommonListMapper;
import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import com.adopt.apigw.modules.CommonList.repository.CommonListRepository;
import com.adopt.apigw.modules.CommonList.utils.TypeConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommonListService extends ExBaseAbstractService<CommonListDTO, CommonList, Long> {

    @Autowired
    private CommonListRepository commonListRepository;

    @Autowired
    private CommonListMapper commonListMapper;

    public CommonListService(CommonListRepository repository, CommonListMapper mapper) {
        super(repository, mapper);
        this.commonListMapper = mapper;
        this.commonListRepository = repository;
    }

    @Override
    public String getModuleNameForLog() {
        return "[CommonListService]";
    }

    public List<CommonListDTO> getCommonListByTypeWithoutCaching(String type) {
        return commonListRepository.findAllByTypeAndStatusOrderByValueAsc(type, CommonConstants.ACTIVE_STATUS)
                .stream().map(domain -> commonListMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        return null;
    }

    @Cacheable(cacheNames = "commonTypes", key = "#type")
    public List<CommonListDTO> getCommonListByType(String type) {
        CycleAvoidingMappingContext context = new CycleAvoidingMappingContext();
        return commonListRepository.findAllByTypeAndStatusOrderByValueAsc(type, CommonConstants.ACTIVE_STATUS)
                .stream()
                .map(domain -> commonListMapper.domainToDTO(domain, context))
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "commonTypes", key = "#type")
    public List<CommonListDTO> getCommonListForAudit(String type) {
        String SUBMODULE = getModuleNameForLog() + " [getCommonListForAudit()] ";
        try {
            List<CommonListDTO> auditForList = getCommonListByType(type);
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                return auditForList.stream().filter(dto -> !dto.getValue().equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_PARTNER))
                        .collect(Collectors.toList());
            }
            return auditForList;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Cacheable(cacheNames = "allCommonTypes")
    public List<CommonListDTO> getAllEntities(Integer mvnoId) throws Exception {
        return commonListRepository.findAllByStatus(CommonConstants.ACTIVE_STATUS)
                .stream().map(domain -> commonListMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @CacheEvict(cacheNames = "commonTypes", key = "#commonListDTO.type")
    public CommonListDTO saveEntity(CommonListDTO commonListDTO) throws Exception {
        return super.saveEntity(commonListDTO);
    }

    @CacheEvict(cacheNames = "commonTypes", key = "#commonListDTO.type")
    public CommonListDTO updateEntity(CommonListDTO commonListDTO) throws Exception {
        return super.updateEntity(commonListDTO);
    }

    @CacheEvict(cacheNames = "commonTypes", key = "#commonListDTO.type")
    public void deleteEntity(CommonListDTO commonListDTO) throws Exception {
        super.deleteEntity(commonListDTO);
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("CommonList");
        createExcel(workbook, sheet, CommonListDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, CommonListDTO.class, null,mvnoId);
    }

    public String concatMethod(String mode) {

        String newMode = mode.toLowerCase();
        if (newMode.equalsIgnoreCase("online")){
            String online = TypeConstants.CUSTDOCVERIFICATIONMODE_ONLINE;
            return online;
        }else {
            String offline = TypeConstants.CUSTDOCVERIFICATIONMODE_OFFLINE;
            return offline;
        }
    }

    public String concatMethod(String mode, String custdocsubtype) {
        try {
            String newMode = mode.toLowerCase();
            String newcustdocsubtype = custdocsubtype.toLowerCase();

            if (newMode.equals("online")) {
                switch (newcustdocsubtype) {
                    case "proofofidentity": return TypeConstants.CUSTDOCSUBTYPE_PROOFOFIDENTITY_ONLINE;
                    case "proofofaddress": return TypeConstants.CUSTDOCSUBTYPE_PROOFOFADDRESS_ONLINE;
                    case "onsitephoto": return TypeConstants.CUSTDOCSUBTYPE_ONSITEPHOTO_ONLINE; // Add this constant if needed
                }
            } else if (newMode.equals("offline")) {
                switch (newcustdocsubtype) {
                    case "proofofidentity": return TypeConstants.CUSTDOCSUBTYPE_PROOFOFIDENTITY_OFFLINE;
                    case "proofofaddress": return TypeConstants.CUSTDOCSUBTYPE_PROOFOFADDRESS_OFFLINE;
                    case "contract": return TypeConstants.CUSTDOCSUBTYPE_CONTRACT_OFFLINE;
                    case "migration": return TypeConstants.CUSTDOCSUBTYPE_MIGRATION_OFFLINE;
                    case "onsitephoto": return TypeConstants.CUSTDOCSUBTYPE_ONSITEPHOTO_OFFLINE; // Add this constant if needed
                }
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
