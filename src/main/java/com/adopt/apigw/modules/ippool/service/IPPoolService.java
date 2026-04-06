package com.adopt.apigw.modules.ippool.service;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.repository.CustomRepository;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.ippool.domain.IPPool;
import com.adopt.apigw.modules.ippool.mapper.IPPoolMapper;
import com.adopt.apigw.modules.ippool.model.IPPoolDTO;
import com.adopt.apigw.modules.ippool.repository.IPPoolRepository;
import com.adopt.apigw.modules.subscriber.model.CustIPDetailsDTO;
import com.adopt.apigw.modules.subscriber.queryScript.IpExpiryScript;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPPoolService extends ExBaseAbstractService<IPPoolDTO, IPPool, Long> {

    @Autowired
    private IPPoolRepository ipPoolRepository;

    public IPPoolService(IPPoolRepository repository, IPPoolMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "pool_id");
        sortColMap.put("name", "pool_name");
        sortColMap.put("type", "pool_type");
        sortColMap.put("category", "pool_category");
    }

    @Autowired
    private CustomRepository<CustIPDetailsDTO> customRepository;

    @Override
    public String getModuleNameForLog() {
        return "[IPPoolService]";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getPoolByNameOrTypeOrCategory(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("IPPool");
        createExcel(workbook, sheet, IPPoolDTO.class, getFields(),mvnoId);
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                IPPoolDTO.class.getDeclaredField("poolId"),
                IPPoolDTO.class.getDeclaredField("poolName"),
                IPPoolDTO.class.getDeclaredField("poolCategory"),
                IPPoolDTO.class.getDeclaredField("poolType"),
                IPPoolDTO.class.getDeclaredField("status"),
        };
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, IPPoolDTO.class, getFields(),mvnoId);
    }

    public GenericDataDTO getPoolByNameOrTypeOrCategory(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getDeviceByNameOrTypeOrAreaName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<IPPool> ipPoolList;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1)
                ipPoolList = ipPoolRepository.findAllByPoolNameContainingIgnoreCaseOrPoolTypeContainingIgnoreCaseOrPoolCategoryContainingIgnoreCaseAndIsDeleteIsFalse(s1, s1, s1, pageRequest);
            else
                // TODO: pass mvnoID manually 6/5/2025

                ipPoolList = ipPoolRepository.findAllByPoolNameContainingIgnoreCaseOrPoolTypeContainingIgnoreCaseOrPoolCategoryContainingIgnoreCaseAndIsDeleteIsFalse(s1, s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (null != ipPoolList && 0 < ipPoolList.getSize()) {
                makeGenericResponse(genericDataDTO, ipPoolList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<CustIPDetailsDTO> getCustIpDetails(Long custId) {
        List<CustIPDetailsDTO> custIPList = customRepository.getResultOfQuery(IpExpiryScript.getIpDetails(custId), CustIPDetailsDTO.class);
        if (null != custIPList && 0 < custIPList.size()) {
            return custIPList;
        }
        return new ArrayList<>();
    }

    @Override
    public IPPoolDTO saveEntity(IPPoolDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        try {
            // TODO: pass mvnoID manually 6/5/2025

            entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
            if (entity.getDefaultPoolFlag()) {
                List<IPPoolDTO> defaultPoolList = getAllDefaultPool();
                if (null != defaultPoolList && 0 < defaultPoolList.size()) {
                    for (IPPoolDTO ipPoolDTO : defaultPoolList) {
                        ipPoolDTO.setDefaultPoolFlag(false);
                        updateEntity(ipPoolDTO);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(),ex);
            throw ex;
        }
        return super.saveEntity(entity);
    }

    @Override
    public boolean duplicateVerifyAtSave(String name)throws Exception
    {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = ipPoolRepository.duplicateVerifyAtSave(name);
                // TODO: pass mvnoID manually 6/5/2025
            else count = ipPoolRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = ipPoolRepository.duplicateVerifyAtSave(name);
                // TODO: pass mvnoID manually 6/5/2025
            else count = ipPoolRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = ipPoolRepository.duplicateVerifyAtEdit(name, id);
                    // TODO: pass mvnoID manually 6/5/2025
                else countEdit = ipPoolRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
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
    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        Integer count=ipPoolRepository.deleteVerify(id);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    public List<IPPoolDTO> getAllDefaultPool() {
        return ipPoolRepository.findAllByDefaultPoolFlagIsTrueAndIsDeleteIsFalse().stream().map(data -> getMapper()
                .domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<IPPool> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if(getMvnoIdFromCurrentStaff(null) == 1)            // TODO: pass mvnoID manually 6/5/2025
            paginationList = ipPoolRepository.findAll(pageRequest);
        else
            // TODO: pass mvnoID manually 6/5/2025
            paginationList = ipPoolRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }
}
