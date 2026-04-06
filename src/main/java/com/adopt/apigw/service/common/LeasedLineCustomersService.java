package com.adopt.apigw.service.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.utils.UpdateDiffFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.model.common.LeasedLineCircuitDetails;
import com.adopt.apigw.model.common.LeasedLineCustomers;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.pojo.api.LeasedLineCircuitDetailsPojo;
import com.adopt.apigw.pojo.api.LeasedLineCustomersPojo;
import com.adopt.apigw.repository.common.LeasedLineCircuitDetailsRepository;
import com.adopt.apigw.repository.common.LeasedLineCustomersRepository;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;

@Service
public class LeasedLineCustomersService extends AbstractService<LeasedLineCustomers, LeasedLineCustomersPojo, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(LeasedLineCustomersService.class);

    @Autowired
    private MessagesPropertyConfig messagesProperty;
    
    public static final String MODULE = "[LeasedLineCustomersService]";

    @Autowired
    private LeasedLineCustomersRepository leasedLineCustomersRepository;
    @Autowired
    private LeasedLineCustomersRepository entityRepository;
    
    @Autowired
    private LeasedLineCircuitDetailsRepository leasedLineCircuitDetailsRepository;
    
    @Autowired 
    private ClientServiceSrv clientServiceSrv;
    
    @Autowired 
    private PostpaidPlanService postpaidPlanService;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);


    @Override
	protected JpaRepository<LeasedLineCustomers, Integer> getRepository() {
		return entityRepository;
	}
	
	public Page<LeasedLineCustomers> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, "llcustid", sortOrder);
        if (null == filterList || 0 == filterList.size())
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1)
                return entityRepository.findAll(pageRequest);
            else
                if(getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    return entityRepository.findAll(pageRequest, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    return entityRepository.findAll(pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
    }

    public Page<LeasedLineCustomers> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.searchEntity(searchText, pageRequest,getMvnoIdFromCurrentStaff(null));
    }
    
    @Override
    public Page<LeasedLineCustomers> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, "llcustid", sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getLeasedLineCustomersByName(searchModel.getFilterValue(), pageRequest);
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
    
    public Page<LeasedLineCustomers> getLeasedLineCustomersByName(String s1, PageRequest pageRequest) {
        // TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) == 1)
            return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(s1!=null?s1:"", pageRequest);
        else
            if(getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(s1!=null?s1:"", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            else
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(s1!=null?s1:"", pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
    }
    
    public List<LeasedLineCustomers> getAllEntities() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAll().stream().filter(leasedLineCustomers -> (leasedLineCustomers.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || leasedLineCustomers.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (leasedLineCustomers.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(leasedLineCustomers.getBuId()))).collect(Collectors.toList());
    }

    public void deleteLeasedLineCustomers(Integer id) throws Exception {
        String SUBMODULE = MODULE + " [deleteLeasedLineCustomers()] ";
        try {
        	LeasedLineCustomers leasedLineCustomers = entityRepository.getOne(id);
//            boolean flag=this.deleteVerification(leasedLineCustomers.getId());
//            if(flag){
            	leasedLineCustomers.setIsDelete(true);
                entityRepository.save(leasedLineCustomers);
//            }else{
//                throw new RuntimeException(DeleteContant.LEASED_LINE_CUST_DELETE_EXIST);
//            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }
    
    @Override
    public boolean duplicateVerifyAtSave(String name) {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
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
            if(getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
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


    public LeasedLineCustomers saveLeasedLineCustomers(LeasedLineCustomers leasedLineCustomers) {
        String SUBMODULE = MODULE + " [saveLeasedLineCustomers()] ";
        try {
        	ClientService clientService = clientServiceSrv.searchByName("LLCIDENTIFIRE");
            for (LeasedLineCircuitDetails item : leasedLineCustomers.getLlcDetailsList()) {
            	if(item != null && item.getId() == null) {
            		if(clientService != null) {
                        List<LeasedLineCircuitDetails> dbLlcDetails = leasedLineCircuitDetailsRepository.findByLlcIdentifier(clientService.getValue());
                    	if(dbLlcDetails != null) {
                           String prevLlcIdentifier = item.getLlcIdentifier();
                    		if(prevLlcIdentifier != null && !"".equals(prevLlcIdentifier)) {
                    			if(prevLlcIdentifier.contains("-")) {
                    				String identArr[] = prevLlcIdentifier.split("-", 0);
                    				if(identArr != null && identArr.length > 0) {
                    					Integer newLlcIdentifier = Integer.valueOf(identArr[1]);
                    					if(newLlcIdentifier != null) {
                    						String newVal = String.valueOf(newLlcIdentifier+1);
                    						item.setLlcIdentifier("CIRCUIT-"+newVal);
                    						clientService.setValue("CIRCUIT-"+newVal);
                    						clientServiceSrv.save(clientService);
                    					}
                    				}
                    			}
                    		}
                    	}else {
                    		item.setLlcIdentifier(clientService.getValue());
                    	}
                	}else {
                		item.setLlcIdentifier("");
                	}
            	}           	
            	item.setLeasedLineCustomers(leasedLineCustomers);
            }
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) != null) {
                // TODO: pass mvnoID manually 6/5/2025
            	leasedLineCustomers.setMvnoId(getMvnoIdFromCurrentStaff(null));
        	}
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return entityRepository.save(leasedLineCustomers);
    }
    
    public LeasedLineCustomersPojo save(LeasedLineCustomersPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [save()] ";
        LeasedLineCustomers oldObj = null;
        if (pojo.getId() != null) {
            oldObj = leasedLineCustomersRepository.findById(pojo.getId()).get();
        }
        try {
            // TODO: pass mvnoID manually 6/5/2025
            pojo.setMvnoId(pojo.getMvnoId());
        	LeasedLineCustomers obj = convertLeasedLineCustomersPojoToLeasedLineCustomersModel(pojo);
            if(getBUIdsFromCurrentStaff().size() == 1)
                obj.setBuId(getBUIdsFromCurrentStaff().get(0));
            if(oldObj!=null) {
                log.info("LeasedLineCustomers update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
            }
            obj = saveLeasedLineCustomers(obj);
            pojo = convertLeasedLineCustomersModelToLeasedLineCustomersPojo(obj);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }
    
    public LeasedLineCustomers convertLeasedLineCustomersPojoToLeasedLineCustomersModel(LeasedLineCustomersPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [convertLeasedLineCustomersPojoToLeasedLineCustomersModel()] ";
        LeasedLineCustomers leasedLineCustomersObj = new LeasedLineCustomers();
        try {
            if (pojo.getId() != null) {
            	leasedLineCustomersObj.setId(pojo.getId());       	
            }
            leasedLineCustomersObj.setName(pojo.getName());
            leasedLineCustomersObj.setBillingAddress(pojo.getBillingAddress());
            leasedLineCustomersObj.setBusinessName(pojo.getBusinessName());
            leasedLineCustomersObj.setEmail(pojo.getEmail());
            leasedLineCustomersObj.setTechnicalPersonName(pojo.getTechnicalPersonName());
            leasedLineCustomersObj.setTechnicalPersonContactNo(pojo.getTechnicalPersonContactNo());
            if(pojo.getMvnoId() != null) {
            	leasedLineCustomersObj.setMvnoId(pojo.getMvnoId());
            }
            if (pojo.getLlcDetailsList() != null && pojo.getLlcDetailsList().size() > 0) {
            	LeasedLineCircuitDetails leasedLineCircuitDetails = null;
                for (LeasedLineCircuitDetailsPojo element : pojo.getLlcDetailsList()) {
                	//PostpaidPlan postpaidPlan = postpaidPlanService.get(element.getPackageId());
                	leasedLineCircuitDetails = new LeasedLineCircuitDetails(element, leasedLineCustomersObj);
                    leasedLineCustomersObj.getLlcDetailsList().add(leasedLineCircuitDetails);
                	
                }
            } else {
                throw new CustomValidationException(APIConstants.FAIL, "Leased line circuit details list is required", null);
            }
            
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return leasedLineCustomersObj;
    }
    
    public LeasedLineCustomersPojo convertLeasedLineCustomersModelToLeasedLineCustomersPojo(LeasedLineCustomers leasedLineCustomers) throws Exception {
        String SUBMODULE = MODULE + " [convertLeasedLineCustomersModelToLeasedLineCustomersPojo()] ";
        LeasedLineCustomersPojo pojo = null;
        try {
            if (leasedLineCustomers != null) {
                pojo = new LeasedLineCustomersPojo();
                pojo.setId(leasedLineCustomers.getId());
                pojo.setName(leasedLineCustomers.getName());
                pojo.setBillingAddress(leasedLineCustomers.getBillingAddress());
                pojo.setBusinessName(leasedLineCustomers.getBusinessName());
                pojo.setEmail(leasedLineCustomers.getEmail());
                pojo.setTechnicalPersonName(leasedLineCustomers.getTechnicalPersonName());
                pojo.setTechnicalPersonContactNo(leasedLineCustomers.getTechnicalPersonContactNo());
                pojo.setCreatedById(leasedLineCustomers.getCreatedById());
                pojo.setCreatedate(leasedLineCustomers.getCreatedate());
                pojo.setCreatedByName(leasedLineCustomers.getCreatedByName());
                pojo.setUpdatedate(leasedLineCustomers.getUpdatedate());
                pojo.setLastModifiedById(leasedLineCustomers.getLastModifiedById());
                pojo.setLastModifiedByName(leasedLineCustomers.getLastModifiedByName());
                if(leasedLineCustomers.getMvnoId() != null) {
                	pojo.setMvnoId(leasedLineCustomers.getMvnoId());
                }
                LeasedLineCircuitDetailsPojo leasedLineCircuitDetailsPojo = null;
                for (LeasedLineCircuitDetails element : leasedLineCustomers.getLlcDetailsList()) {
                	if(element.getPackageId() != null) {
                		PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(element.getPackageId()).get();
                    	leasedLineCircuitDetailsPojo = new LeasedLineCircuitDetailsPojo(element,postpaidPlan == null ? "" : postpaidPlan.getName());
                	}else {
                    	leasedLineCircuitDetailsPojo = new LeasedLineCircuitDetailsPojo(element,"");
                	}
                	
                    pojo.getLlcDetailsList().add(leasedLineCircuitDetailsPojo);
                }
               
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }
    
    public List<LeasedLineCustomersPojo> convertResponseModelIntoPojo(List<LeasedLineCustomers> list) throws Exception {
        List<LeasedLineCustomersPojo> pojoListRes = new ArrayList<LeasedLineCustomersPojo>();
        if (list != null && list.size() > 0) {
            for (LeasedLineCustomers leasedLineCustomers : list) {
                pojoListRes.add(convertLeasedLineCustomersModelToLeasedLineCustomersPojo(leasedLineCustomers));
            }
        }
        // TODO: pass mvnoID manually 6/5/2025
        return pojoListRes.stream().filter(leasedLineCustomers -> leasedLineCustomers.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || leasedLineCustomers.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1).collect(Collectors.toList());
    }
    
    public void validateRequest(LeasedLineCustomersPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
                || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }
    @Override
    public LeasedLineCustomers get(Integer id,Integer mvnoId) {
        LeasedLineCustomers customers = super.get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (customers != null && ((getMvnoIdFromCurrentStaff(null) == 1 || (customers.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || customers.getMvnoId() == 1)) && (customers.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(customers.getBuId()))))
            return customers;
        return null;
    }

    public LeasedLineCustomers getEntityForUpdateAndDelete(Integer id) {
        LeasedLineCustomers customers = leasedLineCustomersRepository.findById(id).get();
        // TODO: pass mvnoID manually 6/5/2025
        if(customers == null || (!(getMvnoIdFromCurrentStaff(id) == 1 || getMvnoIdFromCurrentStaff(id).intValue() == customers.getMvnoId().intValue())  && (customers.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(customers.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return customers;
    }
}
