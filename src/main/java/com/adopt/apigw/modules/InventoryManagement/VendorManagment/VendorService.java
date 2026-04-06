package com.adopt.apigw.modules.InventoryManagement.VendorManagment;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.modules.InventoryManagement.item.ItemServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.product.QProduct;
import com.adopt.apigw.rabbitMq.message.SaveUpdateVendorMessage;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorService extends ExBaseAbstractService<VendorDto,Vendor,Long>{

    @Autowired
    ProductRepository productRepository;
    @Autowired
    private VendorRepo vendorRepo;

    @Autowired
    private VendorMapper vendorMapper;

    public VendorService(VendorRepo vendorRepo,VendorMapper vendorMapper) {
        super(vendorRepo, vendorMapper);
    }
    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
    @Override
    public String getModuleNameForLog() {
        return null;
    }



    public void saveVendorEntity(SaveUpdateVendorMessage saveUpdateVendorMessage){
        try{
            Vendor vendor = new Vendor();

            vendor.setId(saveUpdateVendorMessage.getId());
            vendor.setName(saveUpdateVendorMessage.getName());
            vendor.setStatus(saveUpdateVendorMessage.getStatus());
            vendor.setDeleted(saveUpdateVendorMessage.isDeleted());
            vendor.setMvnoId(saveUpdateVendorMessage.getMvnoId());

            vendorRepo.save(vendor);
            logger.debug("Vendor saved successfully!!");
        }catch (Exception e){
            logger.error("Vendor not saved !!"+e.getMessage());
        }


    }


    public void updateVendorEntity(SaveUpdateVendorMessage saveUpdateVendorMessage){
        try{
            Vendor vendor = vendorRepo.findById(saveUpdateVendorMessage.getId()).orElse(null);
            if(vendor!=null){
                vendor.setName(saveUpdateVendorMessage.getName());
                vendor.setStatus(saveUpdateVendorMessage.getStatus());
                vendor.setDeleted(saveUpdateVendorMessage.isDeleted());
                logger.debug("Vendor updated successfully!!");
                vendorRepo.save(vendor);
            }else{
                Vendor savedVendor = new Vendor(saveUpdateVendorMessage.getId(),saveUpdateVendorMessage.getName(),saveUpdateVendorMessage.getStatus(),saveUpdateVendorMessage.isDeleted(), saveUpdateVendorMessage.getMvnoId());
                logger.debug("Vendor updated successfully!!");
                vendorRepo.save(savedVendor);
            }

        }catch (Exception e){
            logger.error("Vendor not updated !!"+e.getMessage());
        }

    }



//
//    public VendorDto getVendor(Long id ) {
//        VendorDto vendorDto;
//        try {
//            vendorDto = new VendorDto();
//            Vendor vendor = vendorRepo.findById(id).orElse(null);
//            if (vendor != null) {
//                vendorDto = vendorMapper.domainToDTO(vendor, new CycleAvoidingMappingContext());
//            }
//        } catch (Exception exception) {
//            throw new RuntimeException(exception.getMessage());
//        }
//        return vendorDto;
//    }

//    public GenericDataDTO getAll(PaginationRequestDTO requestDTO) {
//        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
//        GenericDataDTO genericDataDTO=new GenericDataDTO();
//        try {
//            PageRequest pageRequest1=PageRequest.of(requestDTO.getPage(),requestDTO.getPageSize());
//            QVendor qVendor=QVendor.vendor;
//            BooleanExpression booleanExpression = qVendor.isNotNull()
//                    .and(qVendor.isDeleted.eq(false));
//            Page<Vendor> page=vendorRepo.findAll(booleanExpression, pageRequest1);
//            genericDataDTO.setDataList(page.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(page.getTotalElements());
//            genericDataDTO.setPageRecords(page.getNumberOfElements());
//            genericDataDTO.setCurrentPageNumber(page.getNumber() + 1);
//            genericDataDTO.setTotalPages(page.getTotalPages());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return genericDataDTO;
//    }
//
//    @Override
//    public VendorDto saveEntity(VendorDto entity) throws Exception {
//        return super.saveEntity(entity);
//    }
//
//    public GenericDataDTO findAllVendor(){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try{
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            QVendor qVendor = QVendor.vendor;
//            BooleanExpression booleanExpression = qVendor.isNotNull().and(qVendor.status.eq(CommonConstants.ACTIVE_STATUS)).and(qVendor.isDeleted.eq(false));
//            if (getMvnoIdFromCurrentStaff() != 1)
//                booleanExpression = booleanExpression.and(qVendor.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//            genericDataDTO.setDataList(IterableUtils.toList(this.vendorRepo.findAll(booleanExpression)));
//            logger.info("Fetching All Active Vendors  :  request:; Response : {{}}", genericDataDTO.getResponseCode());
//
//        }catch (Exception ex) {
//            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Fetch All Active Vendors :  Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
//
//
//    @Override
//    public boolean deleteVerification(Integer id) throws Exception {
//        boolean flag = false;
//        Integer count = productRepository.deleteVerifyVendor(id);
//        if (count !=0) {
//            flag = true;
//        }
//        return flag;
//    }
//
//
//
//    @Override
//    public boolean duplicateVerifyAtSave(String name) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count = null;
//            if (getMvnoIdFromCurrentStaff() == 1) {
//                count = vendorRepo.duplicateVerifyAtSave(name);
//            } else {
//                if (getMvnoIdFromCurrentStaff() != 1)
//                    count = vendorRepo.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            }
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//    @Override
//    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count = null;
//            if (getMvnoIdFromCurrentStaff() == 1) count = vendorRepo.duplicateVerifyAtSave(name);
//            else {
//                if (getMvnoIdFromCurrentStaff() != 1)
//                    count = vendorRepo.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            }
//            if (count >= 1) {
//                Integer countEdit = null;
//                if (getMvnoIdFromCurrentStaff() == 1) countEdit = vendorRepo.duplicateVerifyAtEdit(name, id);
//                else {
//                    if (getMvnoIdFromCurrentStaff() != 1)
//                        countEdit = vendorRepo.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                }
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }
    // Get List By Page and Size and Sort By and Order By
//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
//        QVendor qVendor = QVendor.vendor;
//        BooleanExpression booleanExpression = qVendor.isNotNull().and(qVendor.isDeleted.eq(false));
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        PageRequest pageRequest;
//        Page<Vendor> paginationList = null;
//        try {
//            if (getMvnoIdFromCurrentStaff() != 1)
//                booleanExpression = booleanExpression.and(qVendor.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            paginationList = vendorRepo.findAll(booleanExpression, pageRequest);
//
//            if (paginationList.getSize() > 0) {
//                makeGenericResponse(genericDataDTO, paginationList);
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return genericDataDTO;
//    }
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getVendorList(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            logger.error("Unable to Search :  Response : {{}};Error :{} ;Exception:{}", APIConstants.FAIL, HttpStatus.NOT_ACCEPTABLE, ex.getStackTrace());
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//    public GenericDataDTO getVendorList(String name, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getVendorList()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            Page<Vendor> vendorList;
//            if (getMvnoIdFromCurrentStaff() == 1)
//                vendorList = vendorRepo.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
//            else
//                vendorList = vendorRepo.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (null != vendorList && 0 < vendorList.getSize()) {
//                makeGenericResponse(genericDataDTO, vendorList);
//            }
//            if (vendorList.getTotalElements() == 0) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage("Data Not Found.");
//            }
//        } catch (Exception ex) {
//            logger.error("Unable to Fetch all vendor by Type :  Response : {{}};Error :{} ;Exception:{}", APIConstants.FAIL, HttpStatus.NOT_ACCEPTABLE, ex.getStackTrace());
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return genericDataDTO;
//    }
}
