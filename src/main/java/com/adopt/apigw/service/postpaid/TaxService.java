package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.VasPlanCharge;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.TaxMessage;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaxService extends AbstractService<Tax, TaxPojo, Integer> {

    private static final Logger log = LoggerFactory.getLogger(CustomersService.class);
    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private TaxTypeTierRepository taxTypeTierRepository;

    @Autowired
    private TaxTypeSlabRepository taxTypeSlabRepository;

    @Autowired
    private PostpaidPlanChargeRepo planChargeRepo;
    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    private PostpaidPlanChargeService postpaidPlanChargeService;

    public TaxService() {
        sortColMap.put("id", "taxid");
        sortColMap.put("type", "taxtype");

    }

    public static final String MODULE = "[TaxService]";

    @Autowired
    private TaxRepository entityRepository;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustomersRepository customersRepository;

    @Override
    protected JpaRepository<Tax, Integer> getRepository() {
        return entityRepository;
    }

    public Page<Tax> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder,
                             List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return entityRepository.findAll(pageRequest);
        if (null == filterList || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff(null)));
            else
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,getMvnoIdFromCurrentStaff(null));
    }
    public Page<Tax> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder,
                             List<GenericSearchModel> filterList,Integer mvnoId) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId == 1) {
            if (filterList != null && !filterList.isEmpty()) {
                return search(filterList, pageNumber, customPageSize, sortBy, sortOrder, mvnoId);
            } else {
                return entityRepository.findAll(pageRequest);
            }
        }
        if (null == filterList || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAll(pageRequest, Arrays.asList(1, mvnoId));
            else
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAll(pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
    }

    public Page<Tax> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return entityRepository.searchEntity(searchText, pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.searchEntity(searchText, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.searchEntity(searchText, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1), getBUIdsFromCurrentStaff());
    }
    public Page<Tax> searchEntity(String searchText, Integer pageNumber, int pageSize,Integer mvnoId) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId == 1)
            return entityRepository.searchEntity(searchText, pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.searchEntity(searchText, pageRequest, Arrays.asList(mvnoId, 1));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.searchEntity(searchText, pageRequest, Arrays.asList(mvnoId, 1), getBUIdsFromCurrentStaff());
    }

    public List<Tax> getAllActiveEntities() {
//        System.out.println(getMvnoIdFromCurrentStaff());
        return entityRepository.findByStatusAndIsDeleteIsFalse(CommonConstants.YES_STATUS).stream()
                // TODO: pass mvnoID manually 6/5/2025
                .filter(tax -> (tax.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || tax.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(tax.getBuId())))
                .collect(Collectors.toList());
    }
    public List<Tax> getAllActiveEntities(Integer mvnoId) {
//        System.out.println(getMvnoIdFromCurrentStaff());
        return entityRepository.findByStatusAndIsDeleteIsFalse(CommonConstants.YES_STATUS).stream()
                // TODO: pass mvnoID manually 6/5/2025
                .filter(tax -> (tax.getMvnoId() == mvnoId || tax.getMvnoId() == 1 || mvnoId == 1) && (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(tax.getBuId())))
                .collect(Collectors.toList());
    }

    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = entityRepository.deleteVerify(id);
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    public void deleteTax(Integer id) throws Exception {
        String SUBMODULE = MODULE + " [deleteTax()] ";
        try {
            Tax tax = entityRepository.getOne(id);
            boolean flag = this.deleteVerification(tax.getId());
            if (flag) {
                tax.setIsDelete(true);
                entityRepository.save(tax);
            } else {
                throw new RuntimeException(DeleteContant.TAX_DELETE_EXIST);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Tax getTaxForAdd() {
        return new Tax();
    }

    public Tax getTaxForEdit(Integer id) {
        return entityRepository.getOne(id);
    }

    public TaxTypeSlab getTaxTypeSlab() {
        return new TaxTypeSlab();
    }

    public List<TaxTypeSlab> getTaxTypeSlabList() {
        return new ArrayList<>();
    }

    public Tax deleteSlab(Tax tax, int index) {
        tax.getSlabList().remove(index);
        return tax;
    }

    public TaxTypeTier getTaxTypeTier() {
        return new TaxTypeTier();
    }

    public List<TaxTypeTier> getTaxTypeTierList() {
        return new ArrayList<>();
    }

    public Tax deleteTier(Tax tax, int index) {
        tax.getTieredList().remove(index);
        return tax;
    }

    public Tax saveTax(Tax tax) {
        String SUBMODULE = MODULE + " [saveTax()] ";
        try {
            if (tax.getTaxtype().equals(CommonConstants.TAX_TYPE_SLAB)) {
                for (TaxTypeSlab item : tax.getSlabList()) {
                    item.setTax(tax);
                }
            } else if (tax.getTaxtype().equals(CommonConstants.TAX_TYPE_TIER) || tax.getTaxtype().equals(CommonConstants.TAX_TYPE_COMPOUND)) {
                for (TaxTypeTier item : tax.getTieredList()) {
                    if(item.getBeforeDiscount()==null)
                        item.setBeforeDiscount(false);
                    item.setTax(tax);
                }
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (tax.getMvnoId() != null) {
                // TODO: pass mvnoID manually 6/5/2025
                tax.setMvnoId(tax.getMvnoId());
                // TODO: pass mvnoID manually 6/5/2025
                tax.setMvnoName(mvnoRepository.findMvnoNameById(tax.getMvnoId().longValue()));
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return entityRepository.save(tax);
    }

    public TaxPojo save(TaxPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [save()] ";
//        Tax oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff(null));
            Tax obj = convertTaxPojoToTaxModel(pojo);
            if (getBUIdsFromCurrentStaff().size() == 1)
                obj.setBuId(getBUIdsFromCurrentStaff().get(0));
//            if(oldObj!=null) {
//                log.info("Tax update details:" + UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
//            }
            obj = saveTax(obj);
            pojo = convertTaxModelToTaxPojo(obj);
//            TaxMessage customMessage = new TaxMessage(obj);
//            kafkaMessageSender.send(new KafkaMessageData(customMessage,TaxMessage.class.getSimpleName()));
//            messageSender.send(customMessage, RabbitMqConstants.QUEUE_TAX_MGMTN_SUCCESS);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }

       return pojo;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name,Integer mvnoId) {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public Double taxCalculationByPlan(TaxDetailCountReqDTO pojo, List<PostpaidPlanCharge> chargeList) throws Exception {
//        String customerUsername = customersRepository.findCustomerName(pojo.getCustId());
        log.debug("Initializing tax calculation process for customer : "+pojo.getCustId());
        Double totalAmount = 0.0;
        if (pojo.getPlanId() != null) {
            if (pojo.getCustId() != null) {
                log.debug("Fetching charge list to calculate tax for customer : "+pojo.getCustId());
//                List<Integer> chargeIdlist = chargeService.getchargelistByPlan(pojo.getPlanId());
                if (chargeList != null) {
                    log.debug("Calculating tax amount based on charge list for customer : "+pojo.getCustId());
                    for (PostpaidPlanCharge postpaidPlanCharge : chargeList) {
//                        Charge charge = chargeService.get(chrgId);
                        if (postpaidPlanCharge.getCharge() != null) {
                            if (postpaidPlanCharge.getCharge().getTax() != null) {
                                log.debug("Fetching total tax amount from charge for customer : "+pojo.getCustId());
                                totalAmount = getTaxAmountFromCharge(postpaidPlanCharge.getCharge(), pojo.getPlanId());
                                log.debug("Fetching total tax amount from charge for customer : "+pojo.getCustId()+" completed");
                                log.debug("Calculated totalAmount :"+totalAmount+" for customer :"+customersService);
                            }
                        }
                    }
                }
            } else {
                log.debug("Fetching charge list by plan for customer : "+pojo.getCustId());
//                List<Integer> chargeIdlist = chargeService.getchargelistByPlan(pojo.getPlanId());
                log.debug("Fetching charge list by plan for customer : "+pojo.getCustId()+" completed");
                if (chargeList != null) {
                    for (PostpaidPlanCharge postpaidPlanCharge : chargeList) {
//                        Charge charge = chargeService.get(chrgId);
                        if (postpaidPlanCharge.getCharge() != null) {
                            if (postpaidPlanCharge.getCharge().getTax() != null) {
                                totalAmount = Double.sum(totalAmount, getTaxAmountFromCharge(postpaidPlanCharge.getCharge(), pojo.getPlanId()));
//                                totalAmount = total;
                                log.debug("Calculated totalAmount :"+totalAmount+" for customer :"+customersService);
                            }
                        }
                    }
                }

            }
        } else {
            log.warn("Plan details are missing, please provide plan details.");
            throw new Exception("Please Provide Plan");
        }
        return Double.parseDouble(new DecimalFormat("##.##").format(totalAmount));
    }

    public Double taxCalculationByVasPlan(TaxDetailCountReqDTO pojo, List<VasPlanCharge> chargeList) throws Exception {
//        String customerUsername = customersRepository.findCustomerName(pojo.getCustId());
        log.debug("Initializing tax calculation process for customer : "+pojo.getCustId());
        Double totalAmount = 0.0;
        if (pojo.getPlanId() != -1) {
            if (pojo.getCustId() != null) {
                log.debug("Fetching charge list to calculate tax for customer : "+pojo.getCustId());
//                List<Integer> chargeIdlist = chargeService.getchargelistByPlan(pojo.getPlanId());
                if (chargeList != null) {
                    log.debug("Calculating tax amount based on charge list for customer : "+pojo.getCustId());
                    for (VasPlanCharge postpaidPlanCharge : chargeList) {

//                        Charge charge = chargeService.get(chrgId);
                        if (postpaidPlanCharge.getCharge() != null) {
                            if (postpaidPlanCharge.getCharge().getTax() != null) {
                                log.debug("Fetching total tax amount from charge for customer : "+pojo.getCustId());
                                totalAmount = getTaxAmountFromCharge(postpaidPlanCharge.getCharge(), pojo.getPlanId());
                                log.debug("Fetching total tax amount from charge for customer : "+pojo.getCustId()+" completed");
                                log.debug("Calculated totalAmount :"+totalAmount+" for customer :"+customersService);
                            }
                        }
                    }
                }
            } else {
                log.debug("Fetching charge list by plan for customer : "+pojo.getCustId());
//                List<Integer> chargeIdlist = chargeService.getchargelistByPlan(pojo.getPlanId());
                log.debug("Fetching charge list by plan for customer : "+pojo.getCustId()+" completed");
                if (chargeList != null) {
                    for (VasPlanCharge postpaidPlanCharge : chargeList) {
//                        Charge charge = chargeService.get(chrgId);
                        if (postpaidPlanCharge.getCharge() != null) {
                            if (postpaidPlanCharge.getCharge().getTax() != null) {
                                totalAmount = Double.sum(totalAmount, getTaxAmountFromCharge(postpaidPlanCharge.getCharge(), pojo.getPlanId()));
//                                totalAmount = total;
                                log.debug("Calculated totalAmount :"+totalAmount+" for customer :"+customersService);
                            }
                        }
                    }
                }

            }
        } else {
            log.warn("Plan details are missing, please provide plan details.");
            throw new Exception("Please Provide Plan");
        }
        return Double.parseDouble(new DecimalFormat("##.##").format(totalAmount));
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
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
    public boolean duplicateVerifyAtEdit(String name, Integer id,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    count = entityRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (mvnoId == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(mvnoId, 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, mvnoId, getBUIdsFromCurrentStaff());
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

    public Tax convertTaxPojoToTaxModel(TaxPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [convertTaxPojoToTaxModel()] ";
        Tax taxObj = new Tax();
        try {
            if (pojo.getId() != null) {
                taxObj.setId(pojo.getId());
                Tax tempTax = entityRepository.findById(pojo.getId()).get();
                if (tempTax != null && !pojo.getTaxtype().equals(tempTax.getTaxtype())) {
                    if (chargeService.findAllByTaxId(pojo.getId()).size() > 0) {
                        throw new CustomValidationException(APIConstants.FAIL,
                                messagesProperty.get("api.taxtype.cannot.change"), null);
                    }
                }
            }
            taxObj.setName(pojo.getName());
            taxObj.setDesc(pojo.getDesc());
            taxObj.setTaxtype(pojo.getTaxtype());
            taxObj.setMvnoId(CommonConstants.DEFAULT_MVNO_ID);
            taxObj.setStatus(pojo.getStatus());
      //      taxObj.setLedgerId(pojo.getLedgerId());
            if (pojo.getMvnoId() != null) {
                taxObj.setMvnoId(pojo.getMvnoId());
            }
            if (taxObj.getTaxtype().equals(CommonConstants.TAX_TYPE_SLAB)) {
                if (pojo.getSlabList() != null && pojo.getSlabList().size() > 0) {
                    TaxTypeSlab slab = null;
                    for (TaxTypeSlabPojo element : pojo.getSlabList()) {
                        slab = new TaxTypeSlab(element, taxObj);
                        taxObj.getSlabList().add(slab);
                    }
                } else {
                    throw new CustomValidationException(APIConstants.FAIL,
                            messagesProperty.get("api.slablist.cannot.empty"), null);
                }
            } else if (taxObj.getTaxtype().equals(CommonConstants.TAX_TYPE_TIER)) {
                if (pojo.getTieredList() != null && pojo.getTieredList().size() > 0) {
                    TaxTypeTier tier = null;
                    for (TaxTypeTierPojo element : pojo.getTieredList()) {
                        tier = new TaxTypeTier(element, taxObj);
                        taxObj.getTieredList().add(tier);
                    }
                } else {
                    throw new CustomValidationException(APIConstants.FAIL,
                            messagesProperty.get("api.tierlist.cannot.empty"), null);
                }
            }
            else if (taxObj.getTaxtype().equals(CommonConstants.TAX_TYPE_COMPOUND)) {
                if (pojo.getTieredList() != null && pojo.getTieredList().size() > 0) {
                    TaxTypeTier tier = null;
                    for (TaxTypeTierPojo element : pojo.getTieredList()) {
                        tier = new TaxTypeTier(element, taxObj);
                        taxObj.getTieredList().add(tier);
                    }
                } else {
                    throw new CustomValidationException(APIConstants.FAIL,
                            messagesProperty.get("api.tierlist.cannot.empty"), null);
                }
            }
            else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.invalid.taxType"),
                        null);
            }


        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return taxObj;
    }

    public Double taxCalculationByCharge(TaxDetailCountReqDTO pojo) throws Exception {
        Double totalAmount = 0.0;

        if (pojo.getChargeId() != null) {
            ChargeService chargeService = SpringContext.getBean(ChargeService.class);
            Charge charge = chargeRepository.findById(pojo.getChargeId()).get();
            if (charge != null) {
                if (charge.getTax() != null) {
                    totalAmount = getTaxAmountFromCharge(charge, pojo.getPlanId());
                }
            }

        } else {
            throw new Exception("Please Provide ChargeId !");
        }
        return Double.parseDouble(new DecimalFormat("##.##").format(totalAmount));
    }

    public Double taxCalculationByCharge(TaxDetailCountReqDTO pojo,Charge charge) throws Exception {
        Double totalAmount = 0.0;

        if (pojo.getChargeId() != null) {
//            ChargeService chargeService = SpringContext.getBean(ChargeService.class);
//            Charge charge = chargeService.get(pojo.getChargeId());
            if (charge != null) {
                if (charge.getTax() != null) {
                    totalAmount = getTaxAmountFromCharge(charge, pojo.getPlanId());
                }
            }

        } else {
            throw new Exception("Please Provide ChargeId !");
        }
        return Double.parseDouble(new DecimalFormat("##.##").format(totalAmount));
    }

    public Double getTaxAmountFromCharge(Charge charge,Integer planId) {
        log.debug("Calculating tax amount from charge");
        Double totalAmount = 0.0;
        Double price = 0.0;
        if (charge.getTax().getTaxtype().equalsIgnoreCase("Compound")) {
            if(charge.getTax()!=null)
            {
                if(planId == null){
                    price = charge.getActualprice();
                }else{
                    price = getChargeAmount(charge.getId(),planId,charge.getActualprice());
                }
                for (TaxTypeTier tax : charge.getTax().getTieredList()) {
                    Double taxAmount = (price  * (tax.getRate() / 100.0f));
                    price=price+taxAmount;
                    totalAmount=totalAmount+taxAmount;
                }
            }
        }
        else if (charge.getTax().getTaxtype().equalsIgnoreCase("TIER")) {
            if (charge.getTax() != null) {
                if(planId == null){
                    price = charge.getActualprice();
                }else {
                    price = getChargeAmount(charge.getId(), planId, charge.getActualprice());
                }
                Double tier1 = 0.0;
                Double tier2 = 0.0;
                Double tier3 = 0.0;
                for (TaxTypeTier tax : charge.getTax().getTieredList()) {
//                    Double calPrice =
                    if (tax.getTaxGroup().equalsIgnoreCase("TIER1")) {
                        tier1 = tier1 + (price  * (tax.getRate() / 100.0f));
                        log.debug("tier1 value : "+tier1);
                    }
                    if (tax.getTaxGroup().equalsIgnoreCase("TIER2") && tier1 != 0) {
                        tier2 = tier2 + ((tier1) * (tax.getRate() / 100.0f));
                        log.debug("tier2 value : "+tier2);
                    }
                    if (tax.getTaxGroup().equalsIgnoreCase("TIER3") && tier2 != 0) {
                        tier3 = tier3 + ((tier2) * (tax.getRate() / 100.0f));
                        log.debug("tier3 value : "+tier3);
                    }
                }
                totalAmount = tier1 + tier2 + tier3;
                log.debug("totalAmount = (tier1 + tier2 + tier3) : "+totalAmount);
            }
        } else if (charge.getTax().getTaxtype().equalsIgnoreCase("SLAB")) {
            //TODO: Update slab tax once done on billing engine
            log.debug("Calculating tax amount from charge if tax type is SLAB");
            if (charge.getTax() != null) {
                if(planId == null){
                    price = charge.getActualprice();
                }else{
                    price = getChargeAmount(charge.getId(),planId,charge.getActualprice());
                }
                log.debug("charge amount : "+price);
                for (TaxTypeSlab tax : charge.getTax().getSlabList()) {
                    price = Double.valueOf((price * (tax.getRate() / 100.0f)));
                    totalAmount += price;
                }
            }
            log.debug("Calculation for tax amount from charge if tax type is SLAB is completed");
        }
        log.debug("Calculation completed for tax amount from charge  with totalAmount : "+totalAmount);
        return totalAmount;
    }

    public Double getTaxAmountFromChargeAndPrice(Charge charge,Double price) {
        log.debug("Calculating tax amount from charge");
        Double totalAmount = 0.0;

        if (charge.getTax().getTaxtype().equalsIgnoreCase("Compound")) {
            if(charge.getTax()!=null)
            {
                for (TaxTypeTier tax : charge.getTax().getTieredList()) {
                    Double taxAmount = (price  * (tax.getRate() / 100.0f));
                    price=price+taxAmount;
                    totalAmount=totalAmount+taxAmount;
                }
            }
        }
        else if (charge.getTax().getTaxtype().equalsIgnoreCase("TIER")) {
            if (charge.getTax() != null) {
//                Double price = charge.getActualprice();
                Double tier1 = 0.0;
                Double tier2 = 0.0;
                Double tier3 = 0.0;
                for (TaxTypeTier tax : charge.getTax().getTieredList()) {
//                    Double calPrice =
                    if (tax.getTaxGroup().equalsIgnoreCase("TIER1")) {
                        tier1 = tier1 + ((price + tier1) * (tax.getRate() / 100.0f));
                        log.debug("tier1 value : "+tier1);
                    }
                    if (tax.getTaxGroup().equalsIgnoreCase("TIER2") && tier1 != 0) {
                        tier2 = tier2 + ((tier1) * (tax.getRate() / 100.0f));
                        log.debug("tier2 value : "+tier2);
                    }
                    if (tax.getTaxGroup().equalsIgnoreCase("TIER1") && tier2 != 0) {
                        tier3 = tier3 + ((tier2) * (tax.getRate() / 100.0f));
                        log.debug("tier3 value : "+tier3);
                    }
                }
                totalAmount = tier1 + tier2 + tier3;
                log.debug("totalAmount = (tier1 + tier2 + tier3) : "+totalAmount);
            }
            log.debug("Calculation for tax amount from charge if tax type is TIER is completed");
        } else if (charge.getTax().getTaxtype().equalsIgnoreCase("SLAB")) {
            //TODO: Update slab tax once done on billing engine
            log.debug("Calculating tax amount from charge if tax type is SLAB");
            if (charge.getTax() != null) {
//                Double price = charge.getActualprice();
                for (TaxTypeSlab tax : charge.getTax().getSlabList()) {
                    price = Double.valueOf((price * (tax.getRate() / 100.0f)));
                    totalAmount += price;
                    log.debug("totalAmount = (tier1 + tier2 + tier3) : "+totalAmount);
                }
            }
            log.debug("Calculation for tax amount from charge if tax type is SLAB is completed");
        }

        return totalAmount;
    }

    public Double getTaxAmountFromOfferPrice(Double offerPrice, List<TaxTypeTier> taxList) {
        Double totalAmount = 0.0;

        Double price = offerPrice;
        //this.price = price;
        Double tier1 = 0.0;
        Double tier2 = 0.0;
        Double tier3 = 0.0;
        for (TaxTypeTier tax : taxList) {
//                    Double calPrice =
            if (tax.getTaxGroup().equalsIgnoreCase("TIER1")) {
                tier1 = tier1 + ((price + tier1) * (tax.getRate() / 100.0f));
            }
            if (tax.getTaxGroup().equalsIgnoreCase("TIER2") && tier1 != 0) {
                tier2 = tier2 + ((tier1) * (tax.getRate() / 100.0f));
            }
            if (tax.getTaxGroup().equalsIgnoreCase("TIER1") && tier2 != 0) {
                tier3 = tier3 + ((tier2) * (tax.getRate() / 100.0f));
            }
        }
        totalAmount = tier1 + tier2 + tier3;


        return totalAmount;
    }

//    public Double taxCalculation(Integer id, Integer location) throws Exception {
//        String taxCalURL = null;
//        Double amount = 0.0;
//        ArrayList<TaxDetailCountResDTO> list = null;
//        taxCalURL = CommonConstants.TAX_CALCULATION_URL.replace("{taxCalServer}", CommonConstants.TAX_CAL_SERVER)
//                .replace("{taxCalPort}", CommonConstants.TAX_CAL_PORT).replace("{id}", String.valueOf(id))
//                .replace("{location}", String.valueOf(location));
//
//        ApplicationLogger.logger.info("perform Calculate Tax:" + taxCalURL);
//
//        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS)
//                .writeTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS).build();
//        Request httpRequest = new Request.Builder().url(taxCalURL).build();
//        try {
//            Response response = client.newCall(httpRequest).execute();
//            if (response != null) {
//                String responseBody = response.body().string();
//                ApplicationLogger.logger.info("Tax Calculation () resposne:" + responseBody);
//
//                HashMap<String, Object> responseMap = CommonUtils.convertJsonToHashMap(responseBody);
//                if (responseMap.get("responseCode").toString().equalsIgnoreCase("200")) {
//                    Object object1 = responseMap.get("responseObject");
//                    list = (ArrayList<TaxDetailCountResDTO>) object1;
//                    amount = getTaxCalTotal(list);
//                }
//            } else {
//                ApplicationLogger.logger.info("Tax Calculation() Response is null");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return Double.parseDouble(new DecimalFormat("##.##").format(amount));
//    }
//
//    public Double getTaxCalTotal(ArrayList<TaxDetailCountResDTO> list) throws Exception {
//        Double amount = 0.0;
//        if (list != null && list.size() > 0) {
//            for (int i = 0; i < list.size(); i++) {
//                if (list.get(i).getName().equalsIgnoreCase(CommonConstants.TAX_NAME_TOTAL)) {
//                    amount = list.get(i).getTaxAmount();
//                }
//            }
//        }
//        Double totalAmount = (Double) (amount * (18.0f / 100.0f));
//        return Double.parseDouble(new DecimalFormat("##.##").format(totalAmount));
//    }

    public TaxPojo convertTaxModelToTaxPojo(Tax tax) throws Exception {
        String SUBMODULE = MODULE + " [convertTaxModelToTaxPojo()] ";
        TaxPojo pojo = null;
        try {
            if (tax != null) {
                pojo = new TaxPojo();
                pojo.setId(tax.getId());
                pojo.setName(tax.getName());
                pojo.setDesc(tax.getDesc());
                pojo.setTaxtype(tax.getTaxtype());
                pojo.setStatus(tax.getStatus());
                pojo.setCreatedById(tax.getCreatedById());
                pojo.setCreatedate(tax.getCreatedate());
                pojo.setCreatedByName(tax.getCreatedByName());
                pojo.setUpdatedate(tax.getUpdatedate());
                pojo.setLastModifiedById(tax.getLastModifiedById());
                pojo.setLastModifiedByName(tax.getLastModifiedByName());
                pojo.setMvnoName(tax.getMvnoName());
                if (tax.getMvnoId() != null) {
                    pojo.setMvnoId(tax.getMvnoId());
                }
                if (pojo.getTaxtype().equals(CommonConstants.TAX_TYPE_SLAB)) {
                    TaxTypeSlabPojo slab = null;
                    for (TaxTypeSlab element : tax.getSlabList()) {
                        slab = new TaxTypeSlabPojo(element);
                        pojo.getSlabList().add(slab);
                    }
                } else if (pojo.getTaxtype().equals(CommonConstants.TAX_TYPE_TIER)) {
                    TaxTypeTierPojo tier = null;
                    for (TaxTypeTier element : tax.getTieredList()) {
                        tier = new TaxTypeTierPojo(element);
                        pojo.getTieredList().add(tier);
                    }
                }
                else if (pojo.getTaxtype().equals(CommonConstants.TAX_TYPE_COMPOUND)) {
                    TaxTypeTierPojo tier = null;
                    for (TaxTypeTier element : tax.getTieredList()) {
                        tier = new TaxTypeTierPojo(element);
                        pojo.getTieredList().add(tier);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public List<TaxPojo> convertResponseModelIntoPojo(List<Tax> taxlist) throws Exception {
        List<TaxPojo> pojoListRes = new ArrayList<TaxPojo>();
        if (taxlist != null && taxlist.size() > 0) {
            for (Tax tax : taxlist) {
                pojoListRes.add(convertTaxModelToTaxPojo(tax));
            }
        }
        return pojoListRes;
    }

    public void validateRequest(TaxPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"),
                    null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.YES_STATUS)
                || pojo.getStatus().equalsIgnoreCase(CommonConstants.NO_STATUS))) {
            throw new CustomValidationException(APIConstants.FAIL,
                    messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
                || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"),
                    null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Tax");
        List<TaxPojo> taxPojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, TaxPojo.class, taxPojos, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{TaxPojo.class.getDeclaredField("id"), TaxPojo.class.getDeclaredField("name"),
                TaxPojo.class.getDeclaredField("desc"),};
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<TaxPojo> taxPojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, TaxPojo.class, taxPojos, getFields());
    }

    @Override
    public Page<Tax> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getTaxByNameOrType(searchModel.getFilterValue(), searchModel.getFilterDataType(), pageRequest,mvnoId);
                    }
                } else
                    throw new RuntimeException("Please Provide Search Column!");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public Page<Tax> getTaxByNameOrType(String s1, String dataType, PageRequest pageRequest,Integer mvnoId) {
        if (!dataType.equalsIgnoreCase("") && dataType != null) {
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1)
                return entityRepository.findAllByNameContainingIgnoreCaseAndTaxtypeContainingIgnoreCaseAndIsDeleteIsFalse(s1 != null ? s1 : "", dataType, pageRequest);
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAllByNameContainingIgnoreCaseAndTaxtypeContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(s1 != null ? s1 : "", dataType, pageRequest, Arrays.asList(mvnoId, 1));
            else
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAllByNameContainingIgnoreCaseAndTaxtypeContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(s1 != null ? s1 : "", dataType, pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        } else {
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1)
                return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(s1 != null ? s1 : "", pageRequest);
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(mvnoId, 1));
            else
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(s1 != null ? s1 : "", pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        }
    }

    @Override
    public Tax get(Integer id,Integer mvnoid) {
        Tax tax = super.get(id,mvnoid);
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoid == 1 || ((tax.getMvnoId() == mvnoid || tax.getMvnoId() == 1) && (tax.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(tax.getBuId()))))
            return tax;
        return null;
    }

    public Tax getEntityForUpdateAndDelete(Integer id,Integer mvnoId) {
        Tax tax = get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (tax == null || (!(mvnoId == 1 || mvnoId.intValue() == tax.getMvnoId().intValue()) && (tax.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(tax.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return tax;
    }

    public Double getChargeAmount(Integer chargeId,Integer planId,Double actualPrice)
    {
        if(chargeId!=null && planId!=null)
        {
            //QPostpaidPlanCharge  qPostpaidPlanCharge=QPostpaidPlanCharge.postpaidPlanCharge;
            //BooleanExpression expression=qPostpaidPlanCharge.isNotNull();
            //expression=expression.and(qPostpaidPlanCharge.charge.id.eq(chargeId)).and(qPostpaidPlanCharge.plan.id.eq(planId));
//            List<Double> list=  planChargeRepo.getChargeListByChargeIdAndPlanId(planId,chargeId);
            List<Double> list = postpaidPlanChargeService.getChargeListByChargeIdAndPlanId(planId,chargeId);
            if(list!=null && !list.isEmpty())
            {
                if(list.get(0)!=null && list.get(0)>=0)
                    return list.get(0);
            }
        }
        return actualPrice;
    }


    public Double getPriceWithoutTax(int taxId,Long priceWithTax){
        Optional<Tax> newProducttaxO= entityRepository.findById(taxId);
        Double newPriceWithoutTax= Double.valueOf(priceWithTax);
        if(newProducttaxO.isPresent())
        {
            Tax newProducttax=newProducttaxO.get();
            List<TaxTypeTier> taxTypeTiers=  newProducttax.getTieredList();
            //taxTypeTiers.forEach(taxTypeTier->taxTypeTier.getTax());
            Double newProducttaxRate=taxTypeTiers.get(0).getRate();
            newPriceWithoutTax= priceWithTax*100/(100+newProducttaxRate);
        }
        return newPriceWithoutTax;
    }

    public Double getTaxPer(Charge charge) {
        Optional<Tax>  primaryTax = entityRepository.findById(charge.getTax().getId());
        if(primaryTax.isPresent())
        {
            Tax tierTax=primaryTax.get();
            List<TaxTypeTier> taxTypeTiers=  tierTax.getTieredList();
            //taxTypeTiers.forEach(taxTypeTier->taxTypeTier.getTax());
            return taxTypeTiers.get(0).getRate();
        }
        return 0d;
    }

    public Double getPriceWithoutTax(int taxId,Double priceWithTax){
        Optional<Tax> newProducttaxO= entityRepository.findById(taxId);
        Double newPriceWithoutTax= priceWithTax;
        if(newProducttaxO.isPresent())
        {
            Tax newProducttax=newProducttaxO.get();
            List<TaxTypeTier> taxTypeTiers=  newProducttax.getTieredList();
            //taxTypeTiers.forEach(taxTypeTier->taxTypeTier.getTax());
            Double newProducttaxRate=taxTypeTiers.get(0).getRate();
            newPriceWithoutTax= priceWithTax*100/(100+newProducttaxRate);
        }
        return newPriceWithoutTax;
    }

    public void sendCreateDataShared(TaxPojo pojo, Integer operation) throws Exception {
        try {
            Tax taxEntiry = convertTaxPojoToTaxModel(pojo);
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                createDataSharedService.sendEntitySaveDataForAllMicroService(taxEntiry);
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                createDataSharedService.updateEntityDataForAllMicroService(taxEntiry);
            } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
                createDataSharedService.deleteEntityDataForAllMicroService(taxEntiry);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    public Page<Tax> setMvnoName (Page<Tax> taxPage){

        for (Tax tax: taxPage) {
            String mvnoName = mvnoRepository.findMvnoNameById(tax.getMvnoId().longValue());
            tax.setMvnoName(mvnoName);
        }
        return taxPage;
    }
}
