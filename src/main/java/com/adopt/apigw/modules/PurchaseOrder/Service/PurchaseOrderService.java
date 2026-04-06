package com.adopt.apigw.modules.PurchaseOrder.Service;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.fileUtillity.FileUtility;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.PurchaseOrder.DTO.PurchaseOrderDTO;
import com.adopt.apigw.modules.PurchaseOrder.Domain.PurchaseOrder;
import com.adopt.apigw.modules.PurchaseOrder.Domain.QPurchaseOrder;
import com.adopt.apigw.modules.PurchaseOrder.Mapper.PurchaseOrderMapper;
import com.adopt.apigw.modules.PurchaseOrder.Repository.PurchaseOrderRepository;
import com.adopt.apigw.modules.purchaseDetails.repository.PurchaseDetailsRepo;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@Service
public class PurchaseOrderService extends ExBaseAbstractService<PurchaseOrderDTO, PurchaseOrder,Long> {

    @Autowired
    ClientServiceSrv clientServiceSrv;
   @Autowired
    PurchaseOrderRepository repository;
    @Autowired
    private PurchaseDetailsRepo purchaseDetailsRepo;

    @Autowired
    CustomersService customersService;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private PurchaseOrderMapper mapper;

    public String PATH;

    public PurchaseOrderService(JpaRepository<PurchaseOrder, Long> repository, IBaseMapper<PurchaseOrderDTO, PurchaseOrder> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PurchaseOrderService]";
    }



    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<PurchaseOrder> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            paginationList = repository.findAll(pageRequest);
        else if (null == filterList || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = repository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff(null)));
            else
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = repository.findAll(pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());


        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getpoNumber(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    private GenericDataDTO getpoNumber(String filterValue, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getpoNumber()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QPurchaseOrder qPurchaseOrder=QPurchaseOrder.purchaseOrder;
            Page<PurchaseOrder> purchaseOrders = null;
            BooleanExpression booleanExpression = qPurchaseOrder.isNotNull()
                    .and(qPurchaseOrder.isDeleted.eq(false))
                    .and(qPurchaseOrder.ponumber.likeIgnoreCase("%" + filterValue + "%").or(qPurchaseOrder.ponumber.containsIgnoreCase(filterValue)));
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) {
                purchaseOrders = repository.findAll(booleanExpression, pageRequest);
            }else {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qPurchaseOrder.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                purchaseOrders = repository.findAll(booleanExpression, pageRequest);
            }
            if (null != purchaseOrders && 0 < purchaseOrders.getSize()) {
                makeGenericResponse(genericDataDTO, purchaseOrders);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public boolean duplicateVerifyAtSave(String ponumber) {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (ponumber != null) {
            ponumber = ponumber.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = repository.duplicateVerifyAtSave(ponumber);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(ponumber, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(ponumber, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = repository.deleteVerify(Long.valueOf(id));
        if (count == 1) {
            flag = true;
        }
        return flag;
    }

    public void uploadDocument(PurchaseOrderDTO entityDTO, MultipartFile file) throws IOException {
        String SUBMODULE = "PO" + " [uploadDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.ENTERPRISE_PO_DOC_PATH).get(0).getValue();
        List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
        try{
            Customers customers = customersService.getById(entityDTO.getCustid().getId());
            String subFolderName = "/" + customers.getUsername().trim() + "/";
            String path = PATH + subFolderName;
            ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + path);
           if(entityDTO.getFilename() != null) {
               System.out.println(file.getSize());
               MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
               if (null != file1) {
                   entityDTO.setUniquename(fileUtility.saveFileToServer(file1, path));
                   entityDTO.setFilename(entityDTO.getFilename());
               }
           }else{
                if (null != entityDTO.getFilename() && null != entityDTO.getFilename() && !entityDTO.getFilename().equalsIgnoreCase(entityDTO.getFilename())) {
                    fileUtility.removeFileAtServer(entityDTO.getUniquename(), path);
                }
                MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
                if (null != file1) {
                    entityDTO.setUniquename(fileUtility.saveFileToServer(file1, path));
                }
                PurchaseOrder obj = mapper.dtoToDomain(entityDTO , new CycleAvoidingMappingContext());
                purchaseOrderList.add(obj);
            }
        }catch (Exception ex){
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        }

    public GenericDataDTO save(PurchaseOrderDTO dto) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        try {
            if (dto != null) {
                purchaseOrder = mapper.dtoToDomain(dto, new CycleAvoidingMappingContext());
                purchaseOrder.setCustomer(dto.getCustid());
                purchaseOrder = repository.save(purchaseOrder);
                purchaseOrderDTO = mapper.domainToDTO(purchaseOrder, new CycleAvoidingMappingContext());
                purchaseOrderDTO.setCustid(purchaseOrder.getCustomer());
                genericDataDTO.setData(purchaseOrderDTO);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return genericDataDTO;
    }
}

