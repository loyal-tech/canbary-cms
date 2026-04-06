package com.adopt.apigw.modules.InventoryManagement.product;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.QExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.repository.ExternalItemManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.*;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.ProductPlanGroupMapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.ProductPlanGroupMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.QProductPlanGroupMapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.domain.Productplanmapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.domain.QProductplanmapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.dto.Productplanmappingdto;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.mapper.Productplanmappingmapper;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.repository.ProductPlanMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardController;
import com.adopt.apigw.modules.InventoryManagement.item.*;
import com.adopt.apigw.modules.InventoryManagement.outward.Outward;
import com.adopt.apigw.modules.InventoryManagement.outward.OutwardRepository;
import com.adopt.apigw.modules.InventoryManagement.outward.QOutward;
import com.adopt.apigw.modules.InventoryManagement.productCategory.*;
import com.adopt.apigw.modules.InventoryManagement.productOwner.*;
import com.adopt.apigw.modules.InventoryManagement.warehouse.WareHouse;
import com.adopt.apigw.modules.InventoryManagement.warehouse.WareHouseManagmentServiceAreamappingRepo;
import com.adopt.apigw.modules.InventoryManagement.warehouse.WareHouseParentServiceAreaMapRepo;
import com.adopt.apigw.modules.InventoryManagement.warehouse.WareHouseServiceAreaMapping;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.pojo.api.ChargePojo;
import com.adopt.apigw.repository.postpaid.ChargeRepository;
import com.adopt.apigw.repository.postpaid.PlanServiceRepository;
import com.adopt.apigw.repository.postpaid.TaxRepository;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.ChargeService;
import com.adopt.apigw.service.postpaid.TaxService;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ExBaseAbstractService<ProductDto, Product, Long> {


    @Autowired
    private ProductRepository productRepository;
    @Autowired
    ChargeService chargeService;

    @Autowired
    private InOutWardMacRepo inOutWardMacRepo;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductPlanMappingRepository productPlanMappingRepository;
    @Autowired
    private ProductPlanGroupMappingRepository productPlanGroupMappingRepository;
    @Autowired
    private Productplanmappingmapper productplanmappingmapper;

    @Autowired
    private InOutWardMacMapper inOutWardMacMapper;

    @Autowired
    private ProductOwnerRepository productOwnerRepository;

    @Autowired
    private PlanServiceRepository planServiceRepository;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ExternalItemManagementRepository externalItemManagementRepository;
    @Autowired
    private ChargeRepository chargeRepository;
    @Autowired
    private ChargeMapper chargeMapper;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private TaxService taxService;
    @Autowired
    private WareHouseManagmentServiceAreamappingRepo wareHouseManagmentServiceAreamappingRepo;
    @Autowired
    private ProductOwnerMapper productOwnerMapper;

    public ProductServiceImpl(ProductRepository productRepository, IBaseMapper<ProductDto, Product> mapper) {
        super(productRepository, mapper);
    }
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    private OutwardRepository outwardRepository;

    @Override
    public String getModuleNameForLog() {
        return "[ProductServiceImpl]";
    }

    GenericDataDTO getAllActiveProduct() {
        String SUBMODULE = getModuleNameForLog() + " [getAllActiveProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            QProduct qProduct = QProduct.product;
            BooleanExpression booleanExpression = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false));
            if (getMvnoIdFromCurrentStaff(null) != 1)// TODO: pass mvnoID manually 6/5/2025
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            genericDataDTO.setDataList(IterableUtils.toList(this.productRepository.findAll(booleanExpression)));
            logger.info("Fetching all active products :  request: { From : {}}; Response : {{}}", APIConstants.SUCCESS);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (CustomValidationException ex) {
            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all active products :  request: { From : {} }; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, HttpStatus.NOT_FOUND.value(), ex.getMessage());
        } catch (Exception ex) {
          //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all active products :  request: { From : {} }; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND.value(), ex.getMessage());
        }
        return genericDataDTO;

    }

    GenericDataDTO getAllProductByServiceId(Long serviceId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllProductByServiceId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {

            PlanService planService=planServiceRepository.findById(Math.toIntExact(serviceId)).get();
            List<Product> productList=new ArrayList<>();
            if(planService.getIs_dtv()==false) {
                QProductOwner qProductOwner = QProductOwner.productOwner;
                BooleanExpression booleanExpression1 = qProductOwner.isNotNull();
                if(getLoggedInUser().getPartnerId() != 1) {
                    booleanExpression1 = booleanExpression1.and(qProductOwner.ownerType.equalsIgnoreCase("Partner"));
                } else {
                    booleanExpression1 = booleanExpression1.and(qProductOwner.ownerType.equalsIgnoreCase("Staff"));
                }
                List<ProductOwner> productOwnerList = (List<ProductOwner>) productOwnerRepository.findAll(booleanExpression1);
                List<Long> Ids = productOwnerList.stream().map(ProductOwner::getProductId).collect(Collectors.toList());
                QProduct qProduct = QProduct.product;
                BooleanExpression booleanExpression2 = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.id.in(Ids)).and(qProduct.productCategory.type.contains(CommonConstants.CUSTOMER_BIND)).and(qProduct.caseId.isNull()).and(qProduct.productCategory.hasMac.eq(true).or(qProduct.productCategory.hasSerial.eq(true)));
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1)
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression2 = booleanExpression2.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                productList=IterableUtils.toList(this.productRepository.findAll(booleanExpression2));
                productList.stream().forEach(product -> {
                    if(product.getNewProductCharge() != null) {
                        if (product.getNewProductCharge() != 0) {
                            Charge charge = chargeRepository.findById(product.getNewProductCharge()).get();
                            if(charge.getTaxamount() != null) {
                                product.setNewProductAmount(charge.getPrice() + charge.getTaxamount());
                            } else {
                                product.setNewProductAmount(charge.getPrice());
                            }
                        }
                    }
                    if(product.getRefurburshiedProductCharge() != null) {
                        if(product.getRefurburshiedProductCharge() != 0) {
                            Charge charge = chargeRepository.findById(product.getRefurburshiedProductCharge()).get();
                            if(charge.getTaxamount() != null) {
                                product.setRefurburshiedProductAmount(charge.getPrice() + charge.getTaxamount());
                            } else {
                                product.setRefurburshiedProductAmount(charge.getPrice());
                            }
                        }
                    }
                });
            }
            if(planService.getIs_dtv()==true) {
                QProductOwner qProductOwner = QProductOwner.productOwner;
                //BooleanExpression booleanExpression1 = qProductOwner.isNotNull().and(qProductOwner.ownerType.equalsIgnoreCase("Staff"));
                BooleanExpression booleanExpression1 = qProductOwner.isNotNull();
                if(getLoggedInUser().getPartnerId() != 1) {
                    booleanExpression1 = booleanExpression1.and(qProductOwner.ownerType.equalsIgnoreCase("Partner"));
                } else {
                    booleanExpression1 = booleanExpression1.and(qProductOwner.ownerType.equalsIgnoreCase("Staff"));
                }
                List<ProductOwner> productOwnerList = (List<ProductOwner>) productOwnerRepository.findAll(booleanExpression1);
                List<Long> Ids = productOwnerList.stream().map(ProductOwner::getProductId).collect(Collectors.toList());
                QProduct qProduct = QProduct.product;
                BooleanExpression booleanExpression2 = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.id.in(Ids)).and(qProduct.productCategory.type.eq(CommonConstants.CUSTOMER_BIND)).and(qProduct.caseId.isNotNull()).and(qProduct.productCategory.dtvCategory.eq("STB").or(qProduct.productCategory.dtvCategory.eq("Card")));
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1)
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression2 = booleanExpression2.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                productList=IterableUtils.toList(this.productRepository.findAll(booleanExpression2));
                productList.stream().forEach(product -> {
                    if(product.getNewProductCharge() != null) {
                        if (product.getNewProductCharge() != 0) {
                            Charge charge = chargeRepository.findById(product.getNewProductCharge()).get();
                            if(charge.getTaxamount() != null) {
                                product.setNewProductAmount(charge.getPrice() + charge.getTaxamount());
                            } else {
                                product.setNewProductAmount(charge.getPrice());
                            }
                        }
                    }
                    if(product.getRefurburshiedProductCharge() != null) {
                        if(product.getRefurburshiedProductCharge() != 0) {
                            Charge charge = chargeRepository.findById(product.getRefurburshiedProductCharge()).get();
                            if(charge.getTaxamount() != null) {
                                product.setRefurburshiedProductAmount(charge.getPrice() + charge.getTaxamount());
                            } else {
                                product.setRefurburshiedProductAmount(charge.getPrice());
                            }
                        }
                    }
                });
            }
            genericDataDTO.setDataList(productList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successful");
            logger.info("Fetching all active products by service Id " + serviceId +" :  request: { From : {}}; Response : {{}}", APIConstants.SUCCESS);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (CustomValidationException ex) {
            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable fetch all active products by service Id " + serviceId +" :  request: { From : {} }; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, HttpStatus.NOT_ACCEPTABLE.value(),genericDataDTO.getResponseMessage());
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable fetch all active products by service Id " + serviceId +" :  request: { From : {} }; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_ACCEPTABLE.value(), genericDataDTO.getResponseMessage());
        }
        return genericDataDTO;

    }

    GenericDataDTO getAllNetworkandNaBindProduct() {
        String SUBMODULE = getModuleNameForLog() + " [getAllNetworkandNaBindProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            QProduct qProduct = QProduct.product;
            BooleanExpression booleanExpression = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.productCategory.type.contains("CustomerBind, NetworkBind").or(qProduct.productCategory.type.eq(CommonConstants.NETWORK_BIND)).or(qProduct.productCategory.type.eq(CommonConstants.NA))).and(qProduct.productCategory.hasSerial.eq(true)).and(qProduct.productCategory.hasTrackable.eq(true));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            genericDataDTO.setDataList(IterableUtils.toList(this.productRepository.findAll(booleanExpression)));
            logger.info("Fetching all active network bind and na type of products :  request: { From : {}}; Response : {{}}", APIConstants.SUCCESS);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (CustomValidationException ex) {
            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all active network bind and na type of products :  request: { From : {} }; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, HttpStatus.NOT_FOUND.value(), ex.getMessage());
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all active network bind and na type of products :  request: { From : {} }; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND.value(), ex.getMessage());
        }
        return genericDataDTO;

    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        QProduct qProduct = QProduct.product;
        BooleanExpression booleanExpression = qProduct.isNotNull().and(qProduct.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Product> paginationList = null;
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                paginationList = productRepository.findAll(pageRequest);
//            } else {
            paginationList = productRepository.findAll(booleanExpression, pageRequest);
            paginationList.stream().forEach(r->{
                Charge newcharge = chargeRepository.findByName(r.getName() + "-NewCharge-" + r.getId());
                Charge oldCharge = chargeRepository.findByName(r.getName() + "-RefurbishedCharge-" + r.getId());
                if (newcharge != null) {
                    Long newProductPrice= Math.round(newcharge.getActualprice()+taxService.getTaxAmountFromCharge(newcharge,null));
                    r.setNewPrice(newProductPrice);
                    r.setNewProductTax(newcharge.getTax().getId().longValue());
                    r.setNewProductTaxName(newcharge.getTax().getName());
                }
                if (oldCharge != null) {
                    Long refurbishedProductPrice= Math.round((oldCharge.getActualprice()+taxService.getTaxAmountFromCharge(oldCharge,null)));
                    r.setRefurburshiedPrice(refurbishedProductPrice);
                    r.setRefurburshiedProductTax(oldCharge.getTax().getId().longValue());
                    r.setRefurburshiedProductTaxName(oldCharge.getTax().getName());
                }
            });
//            }

            if (paginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getProductList(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Unable to search product:  Response : {{}};Error :{} ;Exception:{}",APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getStackTrace());
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getProductList(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getProductList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Page<Product> productList;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1)
                productList = productRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
            else
                // TODO: pass mvnoID manually 6/5/2025
                productList = productRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));

            productList.stream().forEach(r -> {
                Charge newcharge = chargeRepository.findByName(r.getName() + "-NewCharge-" + r.getId());
                Charge oldCharge = chargeRepository.findByName(r.getName() + "-RefurbishedCharge-" + r.getId());
                if (newcharge != null) {
                    Long newProductPrice = Math.round(newcharge.getActualprice() + taxService.getTaxAmountFromCharge(newcharge, null));
                    r.setNewPrice(newProductPrice);
                    r.setNewProductTax(newcharge.getTax().getId().longValue());
                    r.setNewProductTaxName(newcharge.getTax().getName());
                }
                if (oldCharge != null) {
                    Long refurbishedProductPrice = Math.round((oldCharge.getActualprice() + taxService.getTaxAmountFromCharge(oldCharge, null)));
                    r.setRefurburshiedPrice(refurbishedProductPrice);
                    r.setRefurburshiedProductTax(oldCharge.getTax().getId().longValue());
                    r.setRefurburshiedProductTaxName(oldCharge.getTax().getName());
                }
            });

            if (null != productList && 0 < productList.getSize()) {
                makeGenericResponse(genericDataDTO, productList);
                logger.info("Product search successfull with " + name + "  :  request: { From : {}}; Response : {{}}", APIConstants.SUCCESS);
            }
            if(productList.getTotalElements()==0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Data Not Found.");
                logger.error("Unable to search product by " + name + " :  request: { From : {}}; Response : {{}}; Error :{} ", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to search product by " + name + " :  request: { From : {}}; Response : {{}}; Error :{}; Exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to search product by " + name + " :  request: { From : {}}; Response : {{}}; Error :{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }
        return genericDataDTO;
    }





    GenericDataDTO getAllChargeByType(String chargeType) {
        String SUBMODULE = getModuleNameForLog() + " [getAllChargeByType()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successful");
            genericDataDTO.setDataList(IterableUtils.toList(getAllChargePojoByChargeType(chargeType)));
            logger.info("Fetching all charge by type " + chargeType + " :  request: { From : {}}; Response : {{}}", APIConstants.SUCCESS);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all charge by type " + chargeType + " :  request: { From : {} }; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, HttpStatus.NOT_FOUND.value(), ex.getMessage());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all charge by type " + chargeType + " :  request: { From : {} }; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND.value(), ex.getMessage());
        }
        return genericDataDTO;

    }

    @Override
    public void deleteEntity(ProductDto entity) throws Exception {
        super.deleteEntity(entity);
    }

    @Override
    public ProductDto saveEntity(ProductDto entity) throws Exception {
      //  ProductCategoryDto productCategoryDto = productCategoryService.getEntityForUpdateAndDelete(entity.getProductCategory().getId());
        ProductCategory productCategory=productCategoryRepository.findById(entity.getProductCategory().getId()).orElse(null);
        ProductCategoryDto productCategoryDto=productCategoryMapper.domainToDTO(productCategory,new CycleAvoidingMappingContext());
        if (productCategoryDto.isHasCas()) {
            if (entity.getCaseId() == null) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select CAS", null);
            }
        }
        ProductDto productDto= super.saveEntity(entity);
        productDto.setNewPrice(entity.getActualpricenewProduct());
        productDto.setRefurburshiedPrice(entity.getActualpricerefurbishedProduct());
        productDto.setNewProductTax(entity.getNewProductTax());
        productDto.setRefurburshiedProductTax(entity.getRefurburshiedProductTax());



        //create New Product Charge
        if(entity.getActualpricenewProduct()!=null){
            Double newProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getNewProductTax()),productDto.getNewPrice());
            Charge charge=new Charge();
            charge.setName(productDto.getName()+"-NewCharge-"+productDto.getId());
            charge.setChargecategory("Installation Charge");
            charge.setChargetype("Customer Direct");
            charge.setService(null);
            charge.setDesc("Product Charge");
            charge.setStatus("Active");
            charge.setActualprice(newProductPriceWithoutTax);
            charge.setPrice(newProductPriceWithoutTax);
            charge.setTax(taxRepository.findById(Math.toIntExact(entity.getNewProductTax())).orElse(null));
            charge.setTaxamount(entity.getActualpricenewProduct()-newProductPriceWithoutTax);
            charge.setIsDelete(true);
            chargeRepository.save(charge);
            Product product=productRepository.findById(productDto.getId()).orElse(null);
            product.setNewProductCharge(charge.getId());
            productRepository.save(product);

        }


        //create Refurbished Charges
        if(entity.getActualpricerefurbishedProduct()!=null){
            Double refurbishedProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getRefurburshiedProductTax()),productDto.getRefurburshiedPrice());
            Charge charge=new Charge();
            charge.setName(productDto.getName()+"-RefurbishedCharge-"+productDto.getId());
            charge.setChargecategory("Installation Charge");
            charge.setChargetype("Customer Direct");
            charge.setService(null);
            charge.setDesc("Product Charge");
            charge.setStatus("Active");
            charge.setActualprice(refurbishedProductPriceWithoutTax);
            charge.setPrice(refurbishedProductPriceWithoutTax);
            charge.setTax(taxRepository.findById(Math.toIntExact(entity.getRefurburshiedProductTax())).orElse(null));
            charge.setTaxamount(entity.getActualpricerefurbishedProduct()-refurbishedProductPriceWithoutTax);
            charge.setIsDelete(true);
            chargeRepository.save(charge);
            Product product=productRepository.findById(productDto.getId()).orElse(null);
            product.setRefurburshiedProductCharge(Math.toIntExact(charge.getId()));
            productRepository.save(product);        }
        return productDto;
    }


    public ProductDto saveEntityFromRms(ProductDto entity) throws Exception {

        ProductCategory productCategory=productCategoryRepository.findById(entity.getProductCategory().getId()).orElse(null);
        ProductCategoryDto productCategoryDto=productCategoryMapper.domainToDTO(productCategory,new CycleAvoidingMappingContext());
        if (productCategoryDto.isHasCas()) {
            if (entity.getCaseId() == null) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select CAS", null);
            }
        }
       // ProductDto productDto= super.saveEntity(entity);
        ProductDto productDto = productMapper.domainToDTO(productRepository.save(productMapper.dtoToDomain(entity,new CycleAvoidingMappingContext())),new CycleAvoidingMappingContext()) ;
        productDto.setNewPrice(entity.getActualpricenewProduct());
        productDto.setRefurburshiedPrice(entity.getActualpricerefurbishedProduct());
        productDto.setNewProductTax(entity.getNewProductTax());
        productDto.setRefurburshiedProductTax(entity.getRefurburshiedProductTax());



        //create New Product Charge
        if(entity.getActualpricenewProduct()!=null){
            Double newProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getNewProductTax()),productDto.getNewPrice());
            Charge charge=new Charge();
            charge.setName(productDto.getName()+"-NewCharge-"+productDto.getId());
            charge.setChargecategory("Installation Charge");
            charge.setChargetype("Customer Direct");
            charge.setService(null);
            charge.setDesc("Product Charge");
            charge.setStatus("Active");
            charge.setActualprice(newProductPriceWithoutTax);
            charge.setPrice(newProductPriceWithoutTax);
            charge.setTax(taxRepository.findById(Math.toIntExact(entity.getNewProductTax())).orElse(null));
            charge.setTaxamount(entity.getActualpricenewProduct()-newProductPriceWithoutTax);
            charge.setIsDelete(true);
            chargeRepository.save(charge);
            Product product=productRepository.findById(productDto.getId()).orElse(null);
            product.setNewProductCharge(charge.getId());
            productRepository.save(product);

        }


        //create Refurbished Charges
        if(entity.getActualpricerefurbishedProduct()!=null){
            Double refurbishedProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getRefurburshiedProductTax()),productDto.getRefurburshiedPrice());
            Charge charge=new Charge();
            charge.setName(productDto.getName()+"-RefurbishedCharge-"+productDto.getId());
            charge.setChargecategory("Installation Charge");
            charge.setChargetype("Customer Direct");
            charge.setService(null);
            charge.setDesc("Product Charge");
            charge.setStatus("Active");
            charge.setActualprice(refurbishedProductPriceWithoutTax);
            charge.setPrice(refurbishedProductPriceWithoutTax);
            charge.setTax(taxRepository.findById(Math.toIntExact(entity.getRefurburshiedProductTax())).orElse(null));
            charge.setTaxamount(entity.getActualpricenewProduct()-refurbishedProductPriceWithoutTax);
            charge.setIsDelete(true);
            chargeRepository.save(charge);
            Product product=productRepository.findById(productDto.getId()).orElse(null);
            product.setRefurburshiedProductCharge(Math.toIntExact(charge.getId()));
            productRepository.save(product);        }
        return productDto;
    }

    @Override
    public ProductDto updateEntity(ProductDto entity) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
//        entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
        ProductCategoryDto productCategoryDto = productCategoryService.getEntityForUpdateAndDelete(entity.getProductCategory().getId(),entity.getMvnoId());
        if (productCategoryDto.isHasCas()) {
            if (entity.getCaseId() == null) {
                throw new Exception("Please select CAS");
            }
        }
        Product product=productRepository.findById(entity.getId()).orElse(null);
        ProductDto productDto = super.saveEntity(entity);
        productDto.setNewPrice(entity.getActualpricenewProduct());
        productDto.setRefurburshiedPrice(entity.getActualpricerefurbishedProduct());

        //update new Charge
        Charge newcharge = chargeRepository.findByName(product.getName() + "-NewCharge-" + productDto.getId());
        if (newcharge != null && entity.getActualpricenewProduct()!=null && entity.getNewProductTax() != null) {
            Double newProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getNewProductTax()),productDto.getNewPrice());
            newcharge.setName(productDto.getName() + "-NewCharge-" + productDto.getId());
            newcharge.setChargecategory("Installation Charge");
            newcharge.setChargetype("Customer Direct");
            newcharge.setService(null);
            newcharge.setDesc("Product Charge");
            newcharge.setStatus("Active");
            newcharge.setActualprice(newProductPriceWithoutTax);
            newcharge.setPrice(newProductPriceWithoutTax);
            newcharge.setTax(taxRepository.findById(Math.toIntExact(entity.getNewProductTax())).orElse(null));
            newcharge.setTaxamount(entity.getActualpricenewProduct()-newProductPriceWithoutTax);
            newcharge.setIsDelete(true);
            chargeRepository.save(newcharge);
        } else {
            //create New Product Charge
            if(entity.getActualpricenewProduct()!=null){
                Double newProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getNewProductTax()),productDto.getNewPrice());
                Charge charge=new Charge();
                charge.setName(productDto.getName()+"-NewCharge-"+productDto.getId());
                charge.setChargecategory("Installation Charge");
                charge.setChargetype("Customer Direct");
                charge.setService(null);
                charge.setDesc("Product Charge");
                charge.setStatus("Active");
                charge.setActualprice(newProductPriceWithoutTax);
                charge.setPrice(newProductPriceWithoutTax);
                charge.setTax(taxRepository.findById(Math.toIntExact(entity.getNewProductTax())).orElse(null));
                charge.setTaxamount(entity.getActualpricenewProduct()-newProductPriceWithoutTax);
                charge.setIsDelete(true);
                chargeRepository.save(charge);
                product=productRepository.findById(productDto.getId()).orElse(null);
                product.setNewProductCharge(charge.getId());
                productRepository.save(product);

            }
        }

        Charge oldCharge = chargeRepository.findByName(product.getName() + "-RefurbishedCharge-" + productDto.getId());
        if (oldCharge != null && entity.getActualpricerefurbishedProduct()!=null && entity.getRefurburshiedProductTax() != null) {
            Double refurbishedProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getRefurburshiedProductTax()),productDto.getRefurburshiedPrice());
            oldCharge.setName(productDto.getName() + "-RefurbishedCharge-" + productDto.getId());
            oldCharge.setChargecategory("Installation Charge");
            oldCharge.setChargetype("Customer Direct");
            oldCharge.setService(null);
            oldCharge.setDesc("Product Charge");
            oldCharge.setStatus("Active");
            oldCharge.setActualprice(entity.getActualpricerefurbishedProduct());
            oldCharge.setActualprice(refurbishedProductPriceWithoutTax);
            oldCharge.setPrice(refurbishedProductPriceWithoutTax);
            oldCharge.setTax(taxRepository.findById(Math.toIntExact(entity.getRefurburshiedProductTax())).orElse(null));
            oldCharge.setTaxamount(entity.getActualpricerefurbishedProduct()-refurbishedProductPriceWithoutTax);
            oldCharge.setIsDelete(true);
            chargeRepository.save(oldCharge);
        } else {
            //create Refurbished Charges
            if(entity.getActualpricerefurbishedProduct()!=null){
                Double refurbishedProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getRefurburshiedProductTax()),productDto.getRefurburshiedPrice());
                Charge charge=new Charge();
                charge.setName(productDto.getName()+"-RefurbishedCharge-"+productDto.getId());
                charge.setChargecategory("Installation Charge");
                charge.setChargetype("Customer Direct");
                charge.setService(null);
                charge.setDesc("Product Charge");
                charge.setStatus("Active");
                charge.setActualprice(refurbishedProductPriceWithoutTax);
                charge.setPrice(refurbishedProductPriceWithoutTax);
                charge.setTax(taxRepository.findById(Math.toIntExact(entity.getRefurburshiedProductTax())).orElse(null));
                charge.setTaxamount(entity.getActualpricerefurbishedProduct()-refurbishedProductPriceWithoutTax);
                charge.setIsDelete(true);
                chargeRepository.save(charge);
                product=productRepository.findById(productDto.getId()).orElse(null);
                product.setRefurburshiedProductCharge(Math.toIntExact(charge.getId()));
                productRepository.save(product);        }
        }
        return productDto;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = productRepository.duplicateVerifyAtSave(name);
            else count = productRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }
    public boolean duplicateProductIdVerifyAtSave(String productId) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (productId != null) {
            productId = productId.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = productRepository.duplicateProductIdVerifyAtSave(productId);
            else count = productRepository.duplicateProductIdVerifyAtSave(productId, mvnoIds);
            if (count == 0) {
                flag = true;
            }
            if (flag == false) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Product Id Already Exists", null);
            }
        }
        return flag;
    }

    @Override
    public boolean deleteVerification(Integer id)throws Exception{
        boolean flag = false;
        Integer count = productRepository.deleteVerify(id);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = productRepository.duplicateVerifyAtSave(name);
            else count = productRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = productRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id));
                else countEdit = productRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id), mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }
    public boolean duplicateProductIdVerifyAtEdit(String productId, Long id) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (productId != null) {
            productId = productId.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = productRepository.duplicateVerifyAtSave(productId);
            else count = productRepository.duplicateVerifyAtSave(productId, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = productRepository.duplicateProductIdVerifyAtEdit(productId, Math.toIntExact(id));
                else countEdit = productRepository.duplicateProductIdVerifyAtEdit(productId, Math.toIntExact(id), mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
            if (flag == false) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Product Id Already Exists", null);
            }
        }
        return flag;
    }

    public GenericDataDTO getAllProductsByMacSerial(Long macMappingId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllProductsByMacSerial()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(macMappingId).get();
            Item item = itemRepository.getOne(inOutWardMACMapping.getItemId());
            Product product = productRepository.getOne(item.getProductId());
            ProductCategory productCategory = productCategoryRepository.getOne(product.getProductCategory().getId());
            if (productCategory.isHasMac() && productCategory.isHasSerial()) {
                genericDataDTO.setDataList(getAllProductsByMacAndSerial(product.getProductCategory().getId()));
            } else if (!productCategory.isHasMac()) {
                genericDataDTO.setDataList(getAllProductsByOnlySerial(product.getProductCategory().getId()));
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("SUccessful");
            logger.info("Fetching all products by inoutward mac mappingid " + macMappingId + " :  request: { From : {}}; Response : {{}}", APIConstants.SUCCESS);
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all products by inoutward mac mappingid " + macMappingId + " :  request: { From : {} }; Response : {{}};Error :{} ;Exception :{}", SUBMODULE, HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all products by inoutward mac mappingid " + macMappingId + " :  request: { From : {} }; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
        }
        return genericDataDTO;
    }

    public List<ProductDto> getAllProductsByMacAndSerial(Long pcId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllProductsByMacAndSerial()] ";
        QProduct qProduct = QProduct.product;
        BooleanExpression booleanExpression = qProduct.isNotNull();
        try {
            booleanExpression = booleanExpression.and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.productCategory.type.contains(CommonConstants.CUSTOMER_BIND)).and(qProduct.productCategory.hasSerial.eq(true)).and(qProduct.productCategory.hasMac.eq(true)).and(qProduct.productCategory.id.eq(pcId));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        } catch (CustomValidationException ex) {
            logger.error("Unable to fetching all product by product category id " + pcId + " :  request: { From : {} }; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, HttpStatus.NOT_FOUND.value(), ex.getMessage());
//            ex.printStackTrace();
        }
        List<Product> products = (List<Product>) productRepository.findAll(booleanExpression);
        return products.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<ProductDto> getAllProductsByOnlySerial(Long pcId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllProductsByMacAndSerial()] ";
        QProduct qProduct = QProduct.product;
        BooleanExpression booleanExpression = qProduct.isNotNull();
        try {
            booleanExpression = booleanExpression.and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.productCategory.type.contains(CommonConstants.CUSTOMER_BIND)).and(qProduct.productCategory.hasMac.eq(false)).and(qProduct.productCategory.hasSerial.eq(true)).and(qProduct.productCategory.id.eq(pcId));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        } catch (CustomValidationException ex) {
            logger.error("Unable to fetching all product by product category id " + pcId + " :  request: { From : {} }; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, HttpStatus.NOT_FOUND.value(), ex.getMessage());
//            ex.printStackTrace();
        }
        List<Product> products = (List<Product>) productRepository.findAll(booleanExpression);
        return products.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<ProductDto> getAllProductsByProductCategoryId(Long id){
        QProduct qProduct = QProduct.product;
        BooleanExpression booleanExpression = qProduct.isNotNull().and(qProduct.status.equalsIgnoreCase("ACTIVE")).and(qProduct.isDeleted.eq(false)).and((qProduct.productCategory.type.contains("CustomerBind, NetworkBind")).or(qProduct.productCategory.type.eq(CommonConstants.CUSTOMER_BIND))).and(qProduct.productCategory.id.in(id));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        List<Product> products = IterableUtils.toList(productRepository.findAll(booleanExpression));
        products.stream().forEach(product -> {
            if(product.getNewProductCharge() != null) {
                if (product.getNewProductCharge() != 0) {
                    Charge charge = chargeRepository.findById(product.getNewProductCharge()).get();
                    if(charge.getTaxamount() != null) {
                        product.setNewProductAmount(charge.getPrice() + charge.getTaxamount());
                    } else {
                        product.setNewProductAmount(charge.getPrice());
                    }
                }
            }
            if(product.getRefurburshiedProductCharge() != null) {
                if(product.getRefurburshiedProductCharge() != 0) {
                    Charge charge = chargeRepository.findById(product.getRefurburshiedProductCharge()).get();
                    if (charge.getTaxamount() != null) {
                        product.setRefurburshiedProductAmount(charge.getPrice() + charge.getTaxamount());
                    } else {
                        product.setRefurburshiedProductAmount(charge.getPrice());
                    }
                }
            }
        });
        return products.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());

    }
    // Get Plan Inventory Id if Customer Plan Category is Individual
    public List<Productplanmappingdto> getAllPlanInventorysIdOnPlanId(Long planId) {
        try {
            QProductplanmapping qProductplanmapping = QProductplanmapping.productplanmapping;
            BooleanExpression booleanExpression = qProductplanmapping.isNotNull().and(qProductplanmapping.planId.eq(planId));
            List<Productplanmapping> productplanmappingsList = (List<Productplanmapping>) productPlanMappingRepository.findAll(booleanExpression);
            List<Productplanmapping> finalProductPlanMapping = new ArrayList<>();
            productplanmappingsList.stream().forEach(productplanmapping -> {
                Product product = productRepository.findById(productplanmapping.getProductId()).get();
                if (!product.getProductCategory().getDtvCategory().equalsIgnoreCase("Card") || product.getProductCategory().getDtvCategory() == null) {
                    finalProductPlanMapping.add(productplanmapping);
                }
            });
            List<Productplanmappingdto> productplanmappingdtos = finalProductPlanMapping.stream().map(p -> productplanmappingmapper.domainToDTO(p, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            return productplanmappingdtos;
        } catch (CustomValidationException exception) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), exception.getMessage(), null);
        }
    }
    // Get Plan Inventory Id if Customer Plan Category is PlanGroup
    public List<ProductPlanGroupMapping> getAllInventoryIdOnPlanIdAndPlanGroupId(Long planId, Long planGroupId) {
        try {
            QProductPlanGroupMapping qProductPlanGroupMapping = QProductPlanGroupMapping.productPlanGroupMapping;
            BooleanExpression booleanExpression = qProductPlanGroupMapping.isNotNull().and(qProductPlanGroupMapping.planId.eq(planId)).and(qProductPlanGroupMapping.planGroupId.eq(planGroupId));
            List<ProductPlanGroupMapping> productPlanGroupMappingList = IterableUtils.toList(productPlanGroupMappingRepository.findAll(booleanExpression));
            productPlanGroupMappingList.stream().forEach(productPlanGroupMapping -> {
                if(productPlanGroupMapping.getName() == null) {
                    if (productPlanGroupMapping.getPlanId() != null) {
                        QProductplanmapping qProductplanmapping = QProductplanmapping.productplanmapping;
                        BooleanExpression booleanExpression1 = qProductplanmapping.planId.eq(productPlanGroupMapping.getPlanId()).and(qProductplanmapping.productId.eq(productPlanGroupMapping.getProductId())).and(qProductplanmapping.productCategoryId.eq(productPlanGroupMapping.getProductCategoryId()));
                        List<Productplanmapping> productplanmappings = IterableUtils.toList(productPlanMappingRepository.findAll(booleanExpression1));
                        if(productplanmappings != null || productplanmappings.size() != 0) {
                            productPlanGroupMapping.setName(productplanmappings.get(0).getName());
                        }
                    }
                }
            });
            List<ProductPlanGroupMapping> finalProductPlanGroupMappingList = new ArrayList<>();
            productPlanGroupMappingList.stream().forEach(productPlanGroupMapping -> {
                Product product = productRepository.findById(productPlanGroupMapping.getProductId()).get();
                if (!product.getProductCategory().getDtvCategory().equalsIgnoreCase("Card") || product.getProductCategory().getDtvCategory() == null) {
                    finalProductPlanGroupMappingList.add(productPlanGroupMapping);
                }
            });
            return finalProductPlanGroupMappingList;
        } catch (CustomValidationException exception) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),exception.getMessage(),null);
        }
    }
    public List<ProductCategory> getProductCategoryByProductPlanGroupMappingId(Long mappingId) {
        QProductPlanGroupMapping qProductPlanGroupMapping = QProductPlanGroupMapping.productPlanGroupMapping;
        BooleanExpression booleanExpression = qProductPlanGroupMapping.id.eq(mappingId);
        List<ProductPlanGroupMapping> productPlanGroupMappingList = IterableUtils.toList(productPlanGroupMappingRepository.findAll(booleanExpression));
        List<ProductCategory> productCategory = new ArrayList<>();
        for (int i=0; i<productPlanGroupMappingList.size(); i++) {
            if (productPlanGroupMappingList.get(i).getProductCategoryId() != null) {
                productCategory.add(productCategoryRepository.findById(productPlanGroupMappingList.get(i).getProductCategoryId()).get());
            }
        }
        return productCategory;
    }

    public List<Product> getProductByProductPlanGroupMappingId(Integer mappingId) {
        QProductPlanGroupMapping qProductPlanGroupMapping = QProductPlanGroupMapping.productPlanGroupMapping;
        BooleanExpression booleanExpression = qProductPlanGroupMapping.id.eq(Long.valueOf(mappingId));
        List<ProductPlanGroupMapping> productPlanGroupMappingList = IterableUtils.toList(productPlanGroupMappingRepository.findAll(booleanExpression));
        if (productPlanGroupMappingList.get(0).getProductId() != null) {
            Product products = productRepository.findById(productPlanGroupMappingList.get(0).getProductId()).get();
            List<Product> productList = new ArrayList<>();
            productList.add(products);
            return productList;
        } else {
            QProduct qProduct = QProduct.product;
            BooleanExpression aBoolean = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.productCategory.type.eq(CommonConstants.CUSTOMER_BIND));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                aBoolean = aBoolean.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            return (List<Product>) this.productRepository.findAll(aBoolean);
        }
    }
    public List<InOutWardMACMapingDTO> getAllItemBasedOnProduct(List<Long> productId) {
        try {
            List<Item> itemListByProductId=itemRepository.findAllByProductIdIn(productId);
            List<Item> itemList=itemListByProductId.stream().filter(r->r.getItemStatus().equalsIgnoreCase(CommonConstants.UNALLOCATED) && r.getOwnershipType().equalsIgnoreCase("Subisu Owned")).collect(Collectors.toList());
            List<InOutWardMACMapping> finalInOutWardMacMappingList=new ArrayList<>();
            itemList.stream().forEach(r->{
                List<InOutWardMACMapping> inOutWardMACMappingList=inOutWardMacRepo.findAllByItemId(r.getId());
                List<InOutWardMACMapping> inOutWardMACMapping=inOutWardMACMappingList.stream().filter(p->p.getCustInventoryMappingId()==null && p.getInventoryMappingId()==null && p.getBulkConsumptionId()==null && p.getIsForwarded()==0).collect(Collectors.toList());
               if(!(inOutWardMACMapping.isEmpty())) {
                    finalInOutWardMacMappingList.add(inOutWardMACMapping.get(0));
                }

            });
            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS=inOutWardMacMapper.domainToDTO(finalInOutWardMacMappingList,new CycleAvoidingMappingContext());
            inOutWardMACMapingDTOS.stream().forEach(r->{
                r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
            });
            return  inOutWardMACMapingDTOS;
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    GenericDataDTO getAllProductForNonTrackableProductCategory() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QProductOwner qProductOwner = QProductOwner.productOwner;
            //BooleanExpression booleanExpression1 = qProductOwner.isNotNull().and(qProductOwner.ownerType.equalsIgnoreCase("Staff"));
            BooleanExpression booleanExpression1 = qProductOwner.isNotNull();
            if(getLoggedInUser().getPartnerId() != 1) {
                booleanExpression1 = booleanExpression1.and(qProductOwner.ownerType.equalsIgnoreCase("Partner"));
            } else {
                booleanExpression1 = booleanExpression1.and(qProductOwner.ownerType.equalsIgnoreCase("Staff"));
            }
            List<ProductOwner> productOwnerList = (List<ProductOwner>) productOwnerRepository.findAll(booleanExpression1);
            List<Long> Ids = productOwnerList.stream().map(ProductOwner::getProductId).collect(Collectors.toList());
            QProduct qProduct = QProduct.product;
            BooleanExpression booleanExpression2 = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.id.in(Ids)).and(qProduct.productCategory.type.contains(CommonConstants.CUSTOMER_BIND)).and(qProduct.productCategory.hasTrackable.eq(false)).and(qProduct.productCategory.hasSerial.eq(false)).and(qProduct.productCategory.hasMac.eq(false));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression2 = booleanExpression2.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            List<Product>products = IterableUtils.toList(productRepository.findAll(booleanExpression2));
            products.stream().forEach(product -> {
                if(product.getNewProductCharge() != null) {
                    Charge charge = chargeRepository.findById(product.getNewProductCharge()).get();
                    if(charge.getTaxamount() != null) {
                        product.setNewProductAmount(charge.getPrice() + charge.getTaxamount());
                    } else {
                        product.setNewProductAmount(charge.getPrice());
                    }
                }
            });
            genericDataDTO.setDataList(products);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetching All Active Products  :  request:; Response : {{}}", genericDataDTO.getResponseCode());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Fetch All active Products :  Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        return genericDataDTO;

    }

    GenericDataDTO getAllCBProducts() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QProduct qProduct = QProduct.product;
            BooleanExpression booleanExpression2 = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.productCategory.type.contains(CommonConstants.CUSTOMER_BIND)).and(qProduct.productCategory.hasSerial.eq(true));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression2 = booleanExpression2.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            genericDataDTO.setDataList(IterableUtils.toList(this.productRepository.findAll(booleanExpression2)));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetching All Active CB Products  :  request:; Response : {{}}", genericDataDTO.getResponseCode());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Fetch All Active CB Products :  Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        return genericDataDTO;

    }
    GenericDataDTO getAllProductbasedOnItemType(String itemType) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if(itemType.equalsIgnoreCase("Serialized Item")) {
                QProduct qProduct = QProduct.product;
                BooleanExpression booleanExpression = qProduct.isDeleted.eq(false).and(qProduct.productCategory.hasSerial.eq(true).or(qProduct.productCategory.hasMac.eq(true))).and(qProduct.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS));
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1)
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                genericDataDTO.setDataList(IterableUtils.toList(this.productRepository.findAll(booleanExpression)));
                logger.info("Fetching All Active Serialized Item Products  :  request:; Response : {{}}", genericDataDTO.getResponseCode());
            }
            if(itemType.equalsIgnoreCase("Non Serialized Item")){
                QProduct qProduct = QProduct.product;
                BooleanExpression booleanExpression = qProduct.isDeleted.eq(false).and(qProduct.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qProduct.productCategory.hasMac.eq(false)).and(qProduct.productCategory.hasSerial.eq(false)).and(qProduct.productCategory.hasTrackable.eq(false)).and(qProduct.productCategory.hasPort.eq(false)).and(qProduct.productCategory.hasCas.eq(false));
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1)
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                genericDataDTO.setDataList(IterableUtils.toList(this.productRepository.findAll(booleanExpression)));
                logger.info("Fetching All Active NonSerialized Item Products  :  request:; Response : {{}}", genericDataDTO.getResponseCode());
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Fetch All Active Products :  Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        return genericDataDTO;
    }


    public List<InOutWardMACMapingDTO> getAllSerializedItemBaseOnProduct(Long productId,String itemType, Long ownerId, String ownerType) {
        try {
            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = null;
            if (itemType.equalsIgnoreCase("Serialized Item")) {
                QItem qItem = QItem.item;
                BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(productId)).and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED).or(qItem.itemStatus.ne(CommonConstants.DEFECTIVE))).and(qItem.ownerType.equalsIgnoreCase(ownerType)).and(qItem.ownerId.eq(ownerId));
                List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.inventoryMappingId.isNull()).and(qInOutWardMACMapping.bulkConsumptionId.isNull());
                List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                inOutWardMACMapingDTOS.stream().forEach(r -> {
                    r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
                    r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
                    r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
                    r.setCondition(itemRepository.findById(r.getItemId()).get().getCondition());
                    r.setOwnerShip(itemRepository.findById(r.getItemId()).get().getOwnershipType());
                });
            }
            return inOutWardMACMapingDTOS;
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
//        else{
//            QProductOwner qProductOwner = QProductOwner.productOwner;
//            BooleanExpression booleanExpression = qProductOwner.productId.eq(productId);
//            List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
//            List<ProductOwnerDto> productOwnerDtos = productOwnerMapper.domainToDTO(productOwnerList, new CycleAvoidingMappingContext());
//            productOwnerDtos.stream().forEach(r -> {
//                r.setProductName(productRepository.findById(r.getProductId()).get().getName());
//            });
//            genericDataDTO.setDataList(productOwnerDtos);
//            return genericDataDTO;
//        }

    }
    public GenericDataDTO getAllProductsByCustomerOwned(Long custId,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            StaffUser loggedInUser = staffUserService.get(getLoggedInUserId(),mvnoId);
            if(loggedInUser.getPartnerid()==1) {
                QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
                BooleanExpression exp1 = qExternalItemManagement.isNotNull().and(qExternalItemManagement.isDeleted.eq(false)).and(qExternalItemManagement.ownerId.eq(custId).and(qExternalItemManagement.ownershipType.equalsIgnoreCase("Customer Owned")));
                List<ExternalItemManagement> externalItemManagementList = (List<ExternalItemManagement>) externalItemManagementRepository.findAll(exp1);
                List<Product> productLists = new ArrayList<>();
                externalItemManagementList.stream().forEach(r -> {
                    Product product = productRepository.getOne(r.getProductId().getId());
                    productLists.add(product);
                });
                List<Long> prodIds = productLists.stream().map(Product::getId).collect(Collectors.toList());
                QProduct qProduct = QProduct.product;
                BooleanExpression booleanExpression2 = qProduct.isNotNull().and(qProduct.id.in(prodIds)).and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.productCategory.type.contains(CommonConstants.CUSTOMER_BIND)).and(qProduct.productCategory.hasSerial.eq(true));
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1)
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression2 = booleanExpression2.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                List<Product> listOfProducts = IterableUtils.toList(this.productRepository.findAll(booleanExpression2));
                if (listOfProducts != null) {
                    genericDataDTO.setDataList(listOfProducts);
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                } else if (listOfProducts == null) {
                    throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "ExternalItem Products with this Customer are not binded.", null);
                }
            }else{

                QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;

                BooleanExpression exp1 = qExternalItemManagement.isNotNull()
                        .and(qExternalItemManagement.isDeleted.eq(false))
                        .and(qExternalItemManagement.isDeleted.eq(false))
                        .and(qExternalItemManagement.ownerId.eq(custId).and(qExternalItemManagement.ownershipType.equalsIgnoreCase("Customer Owned"))
                                        .or(qExternalItemManagement.ownerId.eq(loggedInUser.getPartnerid().longValue())
                                                .and(qExternalItemManagement.ownershipType.equalsIgnoreCase("Partner Owned"))));

                List<ExternalItemManagement> externalItemManagementList = (List<ExternalItemManagement>) externalItemManagementRepository.findAll(exp1);
                List<Product> productLists = new ArrayList<>();
                externalItemManagementList.stream().forEach(r -> {
                    Product product = productRepository.getOne(r.getProductId().getId());
                    productLists.add(product);
                });
                List<Long> prodIds = productLists.stream().map(Product::getId).collect(Collectors.toList());
                QProduct qProduct = QProduct.product;
                BooleanExpression booleanExpression2 = qProduct.isNotNull().and(qProduct.id.in(prodIds)).and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.productCategory.type.contains(CommonConstants.CUSTOMER_BIND)).and(qProduct.productCategory.hasSerial.eq(true));
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1)
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression2 = booleanExpression2.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                List<Product> listOfProducts = IterableUtils.toList(this.productRepository.findAll(booleanExpression2));
                if (listOfProducts != null) {
                    genericDataDTO.setDataList(listOfProducts);
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                } else if (listOfProducts == null) {
                    throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "ExternalItem Products with this Customer are not binded.", null);
                }

            }
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }
    GenericDataDTO getAllNetworkAndNABindNonSerializedProduct() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QProductOwner qProductOwner = QProductOwner.productOwner;
            //BooleanExpression booleanExpression1 = qProductOwner.isNotNull().and(qProductOwner.ownerType.equalsIgnoreCase("Staff"));
            BooleanExpression booleanExpression1 = qProductOwner.isNotNull();
            if(getLoggedInUser().getPartnerId() != 1) {
                booleanExpression1 = booleanExpression1.and(qProductOwner.ownerType.equalsIgnoreCase("Partner"));
            } else {
                booleanExpression1 = booleanExpression1.and(qProductOwner.ownerType.equalsIgnoreCase("Staff"));
            }
            List<ProductOwner> productOwnerList = (List<ProductOwner>) productOwnerRepository.findAll(booleanExpression1);
            List<Long> Ids = productOwnerList.stream().map(ProductOwner::getProductId).collect(Collectors.toList());
            QProduct qProduct = QProduct.product;
            BooleanExpression booleanExpression2 = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.id.in(Ids)).and(qProduct.productCategory.type.eq(CommonConstants.NETWORK_BIND).or(qProduct.productCategory.type.contains("CustomerBind, NetworkBind")).or(qProduct.productCategory.type.eq(CommonConstants.NA))).and(qProduct.productCategory.hasTrackable.eq(false)).and(qProduct.productCategory.hasSerial.eq(false)).and(qProduct.productCategory.hasMac.eq(false));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression2 = booleanExpression2.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            genericDataDTO.setDataList(IterableUtils.toList(productRepository.findAll(booleanExpression2)));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            logger.info("Fetching All Active Products  :  request:; Response : {{}}", genericDataDTO.getResponseCode());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Fetch All active Products :  Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        return genericDataDTO;

    }

    public List<ChargePojo> getAllChargePojoByChargeType(String chargeType) {
        return chargeRepository.findAllByChargetypeAndIsDeleteIsFalse(chargeType).stream().map(data ->
                        chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                // TODO: pass mvnoID manually 6/5/2025
                .stream().filter(charge -> (charge.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)) && (charge.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || charge.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId()))).collect(Collectors.toList());
    }

    public List<Product> getByName(String productName) {
        return productRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(productName);
    }

    public List<ProductPlanGroupMapping> getProductPlanGroupMappingDetails(Long planGroupId, Long planId, Long productCategoryId, Long productId) {
        QProductPlanGroupMapping qProductPlanGroupMapping = QProductPlanGroupMapping.productPlanGroupMapping;
        BooleanExpression booleanExpression = qProductPlanGroupMapping.planGroupId.eq(planGroupId).and(qProductPlanGroupMapping.planId.eq(planId)).and(qProductPlanGroupMapping.productCategoryId.eq(productCategoryId)).and(qProductPlanGroupMapping.productId.eq(productId));
        return IterableUtils.toList(productPlanGroupMappingRepository.findAll(booleanExpression));
    }
    public List<Productplanmapping> getProductPlanMappingDetails(Long planId, Long productCategoryId, Long productId) {
        QProductplanmapping qProductplanmapping = QProductplanmapping.productplanmapping;
        BooleanExpression booleanExpression = qProductplanmapping.planId.eq(planId).and(qProductplanmapping.productCategoryId.eq(productCategoryId)).and(qProductplanmapping.productId.eq(productId));
        return IterableUtils.toList(productPlanMappingRepository.findAll(booleanExpression));
    }

    public void validateProduct(ProductDto entityDto) {
        if (entityDto.getActualpricenewProduct() != null) {
            if (entityDto.getNewProductTax() == null) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select new product tax", null);
            }
        }
        if (entityDto.getActualpricerefurbishedProduct() != null) {
            if (entityDto.getRefurburshiedProductTax() == null) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select refurburshied product tax", null);
            }
        }
        if (entityDto.getNewProductTax() != null) {
            if (entityDto.getActualpricenewProduct() == null) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter Actual product price", null);
            }
        }
        if (entityDto.getRefurburshiedProductTax() != null) {
            if (entityDto.getActualpricerefurbishedProduct() == null) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter refurburshied Actual product price", null);
            }
        }

    }

    public GenericDataDTO getAllActiveProductsByProductCategoryId(Long productCategoryId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QProduct qProduct = QProduct.product;
            BooleanExpression booleanExpression = qProduct.isDeleted.eq(false).and(qProduct.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qProduct.productCategory.id.eq(productCategoryId));
            List<Product> productList = IterableUtils.toList(productRepository.findAll(booleanExpression));
            genericDataDTO.setDataList(productList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Fetching All Active Products By Product Category Id " + productCategoryId + " :  request:; Response : {{}}", genericDataDTO.getResponseCode());
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            logger.error("Unable to Fetch All Active Products By Product Category Id " + productCategoryId + " :  Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),e.getStackTrace());
        }
        return genericDataDTO;
    }
}
