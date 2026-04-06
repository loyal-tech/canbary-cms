package com.adopt.apigw.service.postpaid;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.lead.LeadCustPlanMappping;
import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.pojo.api.StaffUserPojo;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.TatUtils;
import com.adopt.apigw.utils.UpdateDiffFinder;
import org.javers.common.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.pojo.api.CustSpecialPlanMapppingPojo;
import com.adopt.apigw.pojo.api.CustSpecialPlanRelMapppingPojo;
import com.adopt.apigw.repository.postpaid.CustSpecialPlanMapppingRepository;
import com.adopt.apigw.repository.postpaid.CustSpecialPlanRelMapppingRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.APIConstants;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class CustSpecialPlanMapppingService
		extends AbstractService<CustSpecialPlanMappping, CustSpecialPlanMapppingPojo, Integer> {

	@Autowired
	private CustSpecialPlanMapppingRepository custSpecialPlanMapppingRepository;

	@Autowired
	private CustomersService customersService;
	@Autowired
	private CustomersRepository customersRepository;

	@Autowired
	private PostpaidPlanService postpaidPlanService;

	@Autowired
	private PlanGroupService planGroupService;

	@Autowired
	private CustSpecialPlanRelMapppingRepository relMappingRepo;
	@Autowired
	TeamHierarchyMappingRepo teamHierarchyMappingRepo;
	@Autowired
	HierarchyService hierarchyService;

	@Autowired
	private TatUtils tatUtils;

	@Autowired
	WorkflowAuditService workflowAuditService;
	@Autowired
	private ClientServiceSrv clientServiceSrv;

	@Autowired
	StaffUserService staffUserService;
	@Autowired
	private StaffUserRepository staffUserRepository;

	@Autowired
	LeadMasterRepository leadMasterRepository;
//	@Autowired
//	CustSpecialPlanMapper custSpecialPlanMapper;
//	@Autowired
//	CustSpecialPlanRelMapper custSpecialPlanRelMapper;

	@Autowired
	CustSpecialPlanRelMapppingRepository custSpecialPlanRelMapppingRepository;
	@Autowired
	CustSpecialPlanRelMapper custSpecialPlanRelMapper;
	@Autowired
	private PostpaidPlanRepo postpaidPlanRepo;

	@Autowired
	MvnoRepository mvnoRepository;

	private static final Logger log = LoggerFactory.getLogger(APIController.class);

	@Override
	protected JpaRepository<CustSpecialPlanMappping, Integer> getRepository() {
		return custSpecialPlanMapppingRepository;
	}

	public CustSpecialPlanRelMappping findById(Long id) {
		CustSpecialPlanRelMappping custSpecialPlanRelMappping = relMappingRepo.findById(id).get();
		// TODO: pass mvnoID manually 6/5/2025
		if(custSpecialPlanRelMappping != null && ((custSpecialPlanRelMappping.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || custSpecialPlanRelMappping.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()) && (custSpecialPlanRelMappping.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(custSpecialPlanRelMappping.getBuId()))))
			return custSpecialPlanRelMappping;
		return null;
	}

	public CustSpecialPlanRelMappping findByIdForUpdateOrDelete(Long id) {
		CustSpecialPlanRelMappping custSpecialPlanRelMappping = relMappingRepo.findById(id).get();
		// TODO: pass mvnoID manually 6/5/2025
		if(custSpecialPlanRelMappping == null || (!(getMvnoIdFromCurrentStaff(null) == 1 || getMvnoIdFromCurrentStaff(null).intValue() == custSpecialPlanRelMappping.getMvnoId().intValue()) && (custSpecialPlanRelMappping.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(custSpecialPlanRelMappping.getBuId()))))
			throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
		return custSpecialPlanRelMappping;
	}

	public CustSpecialPlanMappping findById(Integer id) {
		CustSpecialPlanMappping custSpecialPlanMappping = custSpecialPlanMapppingRepository.findById(id).get();
		if(custSpecialPlanMappping != null && (custSpecialPlanMappping.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(custSpecialPlanMappping.getCustomer().getId()) == 1 || custSpecialPlanMappping.getMvnoId() == getMvnoIdFromCurrentStaff(custSpecialPlanMappping.getCustomer().getId()).intValue()))
			return custSpecialPlanMappping;
		return null;
	}

	public CustSpecialPlanMappping findByIdForUpdateOrDelete(Integer id) {
		CustSpecialPlanMappping custSpecialPlanMappping = custSpecialPlanMapppingRepository.findById(id).get();
		if(custSpecialPlanMappping == null || !(getMvnoIdFromCurrentStaff(custSpecialPlanMappping.getCustomer().getId()) == 1 || getMvnoIdFromCurrentStaff(custSpecialPlanMappping.getCustomer().getId()).intValue() == custSpecialPlanMappping.getMvnoId().intValue()))
			throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
		return custSpecialPlanMappping;
	}

	public List<CustSpecialPlanMappping> findAll() {
		return custSpecialPlanMapppingRepository.findAll();
//				.stream().filter(custSpecialPlanMappping -> custSpecialPlanMappping.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || custSpecialPlanMappping.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
	}

	public List<CustSpecialPlanMappping> findAllByCustomers(Integer custId) {
		if (custId != null) {
			Customers customer = customersRepository.findById(custId).get();
			if (customer != null) {
				return custSpecialPlanMapppingRepository.findAllByCustomer(customer);
//						.stream().filter(custSpecialPlanMappping -> custSpecialPlanMappping.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || custSpecialPlanMappping.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
			} else {
				throw new CustomValidationException(APIConstants.FAIL, "Customer is not found with id = " + custId,
						null);
			}
		} else {
			throw new CustomValidationException(APIConstants.FAIL, "Customer id is null!", null);
		}
	}

//	public List<CustSpecialPlanMappping> findAllBySpecialPlan(Integer planid) {
//		if (planid != null) {
//			PostpaidPlan postpaidPlan = postpaidPlanService.get(planid);
//			if (postpaidPlan != null) {
//				return custSpecialPlanMapppingRepository.findAllBySpecialPlan(postpaidPlan);
////						.stream().filter(custSpecialPlanMappping -> custSpecialPlanMappping.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || custSpecialPlanMappping.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
//			} else {
//				throw new CustomValidationException(APIConstants.FAIL, "PostpaidPlan is not found with id = " + planid,
//						null);
//			}
//		} else {
//			throw new CustomValidationException(APIConstants.FAIL, "PostpaidPlan id is null!", null);
//		}
//	}

	@Transactional
	public CustSpecialPlanRelMapppingPojo saveCustSpecialPlanMappping(CustSpecialPlanRelMapppingPojo pojo) {

		CustSpecialPlanRelMappping mapping = new CustSpecialPlanRelMappping();
		List<CustSpecialPlanMappping> custMappingList = new ArrayList<>();
		List<CustSpecialPlanMappping> planMappingList = new ArrayList<>();
		List<CustSpecialPlanMappping> planGroupMappingList = new ArrayList<>();
		List<CustSpecialPlanMappping> leadCustMappingList = new ArrayList<>();
		// TODO: pass mvnoID manually 6/5/2025
		pojo.setMvnoId(getMvnoIdFromCurrentStaff(null));
		if (pojo != null) {
			
			if (pojo.getName() != null) {
				mapping.setMappingName(pojo.getName());
			}
			if (pojo.getStatus() != null) {
				mapping.setStatus(pojo.getStatus());
			}
			// TODO: pass mvnoID manually 6/5/2025
			if (getMvnoIdFromCurrentStaff(null) != null) {
				// TODO: pass mvnoID manually 6/5/2025
				mapping.setMvnoId(getMvnoIdFromCurrentStaff(null));
				// TODO: pass mvnoID manually 6/5/2025
				mapping.setMvnoName(mvnoRepository.findMvnoNameById(getMvnoIdFromCurrentStaff(null).longValue()));
            }
			if(!custMappingList.isEmpty()){

			}
			mapping.setStatus("NewActivation");
			pojo.setStatus("NewActivation");
			mapping.setNextStaff(getLoggedInUserId());

			if (getBUIdsFromCurrentStaff().size() == 1)
				mapping.setBuId(getBUIdsFromCurrentStaff().get(0));

			mapping = relMappingRepo.save(mapping);
			if (!CollectionUtils.isEmpty(pojo.getCustMapping())) {
				List<CustSpecialPlanMappping> custMapping = saveOrUpdateCustMappping(pojo.getCustMapping(), mapping);
				custMappingList.addAll(custMapping);
			}
			if (!CollectionUtils.isEmpty(pojo.getLeadCustMapping())) {
				List<CustSpecialPlanMappping> planMapping = saveOrUpdateLeadcustMappping(pojo.getLeadCustMapping(), mapping);
				leadCustMappingList.addAll(planMapping);
			}


			if (!CollectionUtils.isEmpty(pojo.getPlanMapping())) {
				List<CustSpecialPlanMappping> planMapping = saveOrUpdatePlanMappping(pojo.getPlanMapping(), mapping);
				planMappingList.addAll(planMapping);
			}

			if (!CollectionUtils.isEmpty(pojo.getPlanGroupMapping())) {
				List<CustSpecialPlanMappping> planGroupMapping = saveOrUpdatePlanGroupMappping(pojo.getPlanGroupMapping(), mapping);
				planGroupMappingList.addAll(planGroupMapping);
			}
			
			if (!CollectionUtils.isEmpty(planMappingList)) {

				List<CustSpecialPlanMappping> result = custSpecialPlanMapppingRepository.saveAll(planMappingList);
				mapping.setCustSpecialPlanMapppingList(Sets.asSet(result));
			}

			if (!CollectionUtils.isEmpty(planGroupMappingList)) {
				List<CustSpecialPlanMappping> result = custSpecialPlanMapppingRepository.saveAll(planGroupMappingList);
				mapping.setCustSpecialPlanMapppingList(Sets.asSet(result));
			}

			if (!CollectionUtils.isEmpty(custMappingList)) {
				List<CustSpecialPlanMappping> result = custSpecialPlanMapppingRepository.saveAll(custMappingList);
				if(!CollectionUtils.isEmpty(mapping.getCustSpecialPlanMapppingList()))
					result.addAll(mapping.getCustSpecialPlanMapppingList());
				mapping.setCustSpecialPlanMapppingList(Sets.asSet(result));
			}

			if (!CollectionUtils.isEmpty(leadCustMappingList)) {
				List<CustSpecialPlanMappping> result = custSpecialPlanMapppingRepository.saveAll(leadCustMappingList);
				if(!CollectionUtils.isEmpty(mapping.getCustSpecialPlanMapppingList()))
					result.addAll(mapping.getCustSpecialPlanMapppingList());
				mapping.setCustSpecialPlanMapppingList(Sets.asSet(result));
			}

		} else {
			throw new CustomValidationException(APIConstants.FAIL, "Please enter valid data!", null);
		}

		for(CustSpecialPlanMappping custSpecialPlanMappping :planMappingList){
			AssignSpecialPlanMappingWorkflow(custSpecialPlanMappping);
		}
		Set<CustSpecialPlanMapppingPojo> list1 = custMappingList.stream()
				.map(data -> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());
		Set<CustSpecialPlanMapppingPojo> list2 = planMappingList.stream()
				.map(data -> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());
		Set<CustSpecialPlanMapppingPojo> list3 = planGroupMappingList.stream()
				.map(data -> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());
		Set<CustSpecialPlanMapppingPojo> list4 = null;
		leadCustMappingList.stream()
				.map(data->new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toList());

		return new CustSpecialPlanRelMapppingPojo(mapping, list1, list2, list3,list4);

	}

	private List<CustSpecialPlanMappping> saveOrUpdateLeadcustMappping(Set<CustSpecialPlanMapppingPojo> pojoList, CustSpecialPlanRelMappping custSpecialPlanRelMappping) {
		List<CustSpecialPlanMappping> mappingList = new ArrayList<CustSpecialPlanMappping>();
		for (CustSpecialPlanMapppingPojo custSpecialPlanMapppingPojo : pojoList) {
			CustSpecialPlanMappping custSpecialPlanMappping = null;
			if (custSpecialPlanMapppingPojo != null) {
				if (custSpecialPlanMapppingPojo.getId() == null)
					custSpecialPlanMappping = new CustSpecialPlanMappping();
				else
					custSpecialPlanMappping = findById(custSpecialPlanMapppingPojo.getId());

				if (custSpecialPlanMapppingRepository.isDuplicateRecordFound(
						custSpecialPlanMapppingPojo.getSpecialPlanId(), custSpecialPlanMapppingPojo.getNormalPlanId(),
						custSpecialPlanMapppingPojo.getCustomerId(), custSpecialPlanRelMappping.getId()) < 1 ||
						custSpecialPlanMapppingRepository.isDuplicateRecordFoundForPlanGroup(
								custSpecialPlanMapppingPojo.getSpecialPlanGroupId(), custSpecialPlanMapppingPojo.getNormalPlanGroupId(),
								custSpecialPlanMapppingPojo.getCustomerId(), custSpecialPlanRelMappping.getId()) < 1) {

					// check special Plan id available or not
					if (custSpecialPlanMapppingPojo.getSpecialPlanId() != null) {
						PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custSpecialPlanMapppingPojo.getSpecialPlanId()).get();
						if (postpaidPlan != null)
							custSpecialPlanMappping.setSpecialPlan(postpaidPlan);
						else
							throw new CustomValidationException(APIConstants.FAIL, "Special plan not available!", null);
					}else if (custSpecialPlanMapppingPojo.getSpecialPlanGroupId() != null) {
						// TODO: pass mvnoID manually 6/5/2025
						PlanGroup planGroup = planGroupService
								.findPlanGroupById(custSpecialPlanMapppingPojo.getSpecialPlanGroupId(), getMvnoIdFromCurrentStaff(null));
						if (planGroup != null)
							custSpecialPlanMappping.setSpecialPlanGroup(planGroup);
						else
							throw new CustomValidationException(APIConstants.FAIL, "Special plan group not available!", null);
					}

					// check special Plan with lead customer mapping available or not
					if (custSpecialPlanMapppingPojo.getLeadCustId() != null) {
						if (custSpecialPlanMapppingRepository.isDuplicateRecordFoundForeadCustomer(
								custSpecialPlanMapppingPojo.getSpecialPlanId(),
								custSpecialPlanMapppingPojo.getLeadCustId(), custSpecialPlanRelMappping.getId()) < 1
								|| custSpecialPlanMapppingRepository.isDuplicateRecordFoundForLeadCustomerPlanGroup(
								custSpecialPlanMapppingPojo.getSpecialPlanGroupId(),
								custSpecialPlanMapppingPojo.getLeadCustId(), custSpecialPlanRelMappping.getId()) < 1) {
							LeadMaster leadMaster = leadMasterRepository.findById(Long.valueOf(custSpecialPlanMapppingPojo.getLeadCustId())).get();
							if (leadMaster != null)
								custSpecialPlanMappping.setLeadMaster(leadMaster);
						} else {
							throw new CustomValidationException(APIConstants.FAIL,
									"Mapping Alredy Exists for special plan and Leadcustomer", null);
						}
					}
					if(custSpecialPlanMappping.getLeadMaster() != null){
						custSpecialPlanMappping.setService(custSpecialPlanMappping.getService());
						custSpecialPlanMappping.setCustSpecialPlanRelMappping(custSpecialPlanRelMappping);
						custSpecialPlanMappping.setMvnoId(getMvnoIdFromCurrentStaff(custSpecialPlanMappping.getCustomer().getId()));
						mappingList.add(custSpecialPlanMappping);
					}
				}else{
					throw new CustomValidationException(APIConstants.FAIL, "Mapping Alredy Exists for special plan ",
							null);
				}
			}
		}

			return mappingList;
	}

	@Transactional
	public CustSpecialPlanRelMapppingPojo UpdateCustSpecialPlanMappping(CustSpecialPlanRelMapppingPojo pojo) {
		CustSpecialPlanRelMappping mapping = null;
		findByIdForUpdateOrDelete(pojo.getId());
		// TODO: pass mvnoID manually 6/5/2025
		pojo.setMvnoId(getMvnoIdFromCurrentStaff(null));
		pojo.setStatus("NewActivation");
		if (pojo.getId() != null) {
			CustSpecialPlanRelMappping existingMapping = findByIdForUpdateOrDelete(pojo.getId());
			if (existingMapping != null) {
				mapping = existingMapping;

			} else {
				throw new CustomValidationException(APIConstants.FAIL, "Mapping Relation not available!", null);
			}

		}
		List<CustSpecialPlanMappping> custMappingList = new ArrayList<>();
		List<CustSpecialPlanMappping> planMappingList = new ArrayList<>();
		List<CustSpecialPlanMappping> planGroupMappingList = new ArrayList<>();
		List<CustSpecialPlanMappping> leadCustMappingList = new ArrayList<>();

		if (pojo.getName() != null) {
			mapping.setMappingName(pojo.getName());
		}
		if (pojo.getStatus() != null) {
			mapping.setStatus(pojo.getStatus());
		}
		// TODO: pass mvnoID manually 6/5/2025
		if (getMvnoIdFromCurrentStaff(null) != null) {
			// TODO: pass mvnoID manually 6/5/2025
			mapping.setMvnoId(getMvnoIdFromCurrentStaff(null));
        }
		mapping = relMappingRepo.save(mapping);


		List<CustSpecialPlanMappping> existingList = custSpecialPlanMapppingRepository.findAllByCustSpecialPlanRelMappping(mapping);
		if(!CollectionUtils.isEmpty(existingList)) {
			custSpecialPlanMapppingRepository.deleteInBatch(existingList);
		}

		if (!CollectionUtils.isEmpty(pojo.getCustMapping())) {
			List<CustSpecialPlanMappping> custMapping = saveOrUpdateCustMappping(pojo.getCustMapping(), mapping);
			List<CustSpecialPlanMappping> result = custSpecialPlanMapppingRepository.saveAll(custMapping);
//			result.addAll(mapping.getCustSpecialPlanMapppingList());
			custMappingList.addAll(result);
		}

		if (!CollectionUtils.isEmpty(pojo.getPlanMapping())) {
			List<CustSpecialPlanMappping> planMapping = saveOrUpdatePlanMappping(pojo.getPlanMapping(), mapping);
			List<CustSpecialPlanMappping> result = custSpecialPlanMapppingRepository.saveAll(planMapping);
			planMappingList.addAll(result);
		}

		if (!CollectionUtils.isEmpty(pojo.getPlanGroupMapping())) {
			List<CustSpecialPlanMappping> planGroupMapping = saveOrUpdatePlanGroupMappping(pojo.getPlanGroupMapping(), mapping);
			List<CustSpecialPlanMappping> result = custSpecialPlanMapppingRepository.saveAll(planGroupMapping);
			planGroupMappingList.addAll(result);
		}
		if (!CollectionUtils.isEmpty(pojo.getLeadCustMapping())) {
			List<CustSpecialPlanMappping> leadplanMapping = saveOrUpdateLeadcustMappping(pojo.getLeadCustMapping(), mapping);
			List<CustSpecialPlanMappping> result = custSpecialPlanMapppingRepository.saveAll(leadplanMapping);
			leadCustMappingList.addAll(result);
		}
//		if (!CollectionUtils.isEmpty(planMappingList)) {
////			List<CustSpecialPlanMappping> result = custSpecialPlanMapppingRepository.saveAll(planMappingList);
////			mapping.setCustSpecialPlanMapppingList(Sets.asSet(result));
//		}
//
//		if (!CollectionUtils.isEmpty(custMappingList)) {
////			List<CustSpecialPlanMappping> result = custSpecialPlanMapppingRepository.saveAll(custMappingList);
////			result.addAll(mapping.getCustSpecialPlanMapppingList());
////			mapping.setCustSpecialPlanMapppingList(Sets.asSet(result));
//		}
		for(CustSpecialPlanMappping custSpecialPlanMappping :planMappingList){
			AssignSpecialPlanMappingWorkflow(custSpecialPlanMappping);
		}
		for(CustSpecialPlanMappping custSpecialPlanMappping :custMappingList){
			AssignSpecialPlanMappingWorkflow(custSpecialPlanMappping);
		}
		for(CustSpecialPlanMappping custSpecialPlanMappping :planGroupMappingList){
			AssignSpecialPlanMappingWorkflow(custSpecialPlanMappping);
		}
		for(CustSpecialPlanMappping custSpecialPlanMappping :leadCustMappingList){
			AssignSpecialPlanMappingWorkflow(custSpecialPlanMappping);
		}

		Set<CustSpecialPlanMapppingPojo> list1 = custMappingList.stream()
				.map(data -> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());
		Set<CustSpecialPlanMapppingPojo> list2 = planMappingList.stream()
				.map(data -> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());
		Set<CustSpecialPlanMapppingPojo> list3 = planGroupMappingList.stream()
				.map(data -> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());
		Set<CustSpecialPlanMapppingPojo> list4 = leadCustMappingList.stream()
				.map(data-> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());

		return new CustSpecialPlanRelMapppingPojo(mapping, list1, list2, list3,list4);
	}

	public List<CustSpecialPlanMappping> saveOrUpdateCustMappping(Set<CustSpecialPlanMapppingPojo> pojoList,
			CustSpecialPlanRelMappping custSpecialPlanRelMappping) {

		List<CustSpecialPlanMappping> mappingList = new ArrayList<CustSpecialPlanMappping>();
		for (CustSpecialPlanMapppingPojo custSpecialPlanMapppingPojo : pojoList) {
			CustSpecialPlanMappping custSpecialPlanMappping = null;
			if (custSpecialPlanMapppingPojo != null) {
				//custSpecialPlanMapppingPojo.setNextStaff(getLoggedInUserId());
				if (custSpecialPlanMapppingPojo.getId() == null)
					custSpecialPlanMappping = new CustSpecialPlanMappping();
				else
					custSpecialPlanMappping = findById(custSpecialPlanMapppingPojo.getId());

				if (custSpecialPlanMapppingRepository.isDuplicateRecordFound(
						custSpecialPlanMapppingPojo.getSpecialPlanId(), custSpecialPlanMapppingPojo.getNormalPlanId(),
						custSpecialPlanMapppingPojo.getCustomerId(), custSpecialPlanRelMappping.getId()) < 1 ||
						custSpecialPlanMapppingRepository.isDuplicateRecordFoundForPlanGroup(
								custSpecialPlanMapppingPojo.getSpecialPlanGroupId(), custSpecialPlanMapppingPojo.getNormalPlanGroupId(),
								custSpecialPlanMapppingPojo.getCustomerId(), custSpecialPlanRelMappping.getId()) < 1) {

					// check special Plan id available or not
					if (custSpecialPlanMapppingPojo.getSpecialPlanId() != null) {
						PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custSpecialPlanMapppingPojo.getSpecialPlanId()).get();

						if (postpaidPlan != null)
							custSpecialPlanMappping.setSpecialPlan(postpaidPlan);
						else
							throw new CustomValidationException(APIConstants.FAIL, "Special plan not available!", null);
					} else if (custSpecialPlanMapppingPojo.getSpecialPlanGroupId() != null) {
						// TODO: pass mvnoID manually 6/5/2025
							PlanGroup planGroup = planGroupService
									.findPlanGroupById(custSpecialPlanMapppingPojo.getSpecialPlanGroupId(), getMvnoIdFromCurrentStaff(null));
							if (planGroup != null)
								custSpecialPlanMappping.setSpecialPlanGroup(planGroup);
							else
								throw new CustomValidationException(APIConstants.FAIL, "Special plan group not available!", null);
						} else {
						throw new CustomValidationException(APIConstants.FAIL, "Special plan is Mandatory!", null);
					}

					// check special Plan with customer mapping available or not
					if (custSpecialPlanMapppingPojo.getCustomerId() != null) {
						if (custSpecialPlanMapppingRepository.isDuplicateRecordFoundForCustomer(
								custSpecialPlanMapppingPojo.getSpecialPlanId(),
								custSpecialPlanMapppingPojo.getCustomerId(), custSpecialPlanRelMappping.getId()) < 1
						|| custSpecialPlanMapppingRepository.isDuplicateRecordFoundForCustomerPlanGroup(
								custSpecialPlanMapppingPojo.getSpecialPlanGroupId(),
								custSpecialPlanMapppingPojo.getCustomerId(), custSpecialPlanRelMappping.getId()) < 1) {
							Customers customer = customersRepository.findById(custSpecialPlanMapppingPojo.getCustomerId()).get();
							if (customer != null)
								custSpecialPlanMappping.setCustomer(customer);
						} else {
							throw new CustomValidationException(APIConstants.FAIL,
									"Mapping Alredy Exists for special plan and customer", null);
						}
					}
					if (custSpecialPlanMappping.getCustomer() != null) {
						custSpecialPlanMappping.setService(custSpecialPlanMapppingPojo.getService());
						custSpecialPlanMappping.setCustSpecialPlanRelMappping(custSpecialPlanRelMappping);
						// TODO: pass mvnoID manually 6/5/2025
						custSpecialPlanMappping.setMvnoId(getMvnoIdFromCurrentStaff(null));
						mappingList.add(custSpecialPlanMappping);
					}
				} else {
					throw new CustomValidationException(APIConstants.FAIL, "Mapping Alredy Exists for special plan ",
							null);
				}

			}

		}

		return mappingList;
	}

	public List<CustSpecialPlanMappping> saveOrUpdatePlanMappping(Set<CustSpecialPlanMapppingPojo> pojoList,
			CustSpecialPlanRelMappping custSpecialPlanRelMappping) {
		List<CustSpecialPlanMapppingPojo> response = new ArrayList<CustSpecialPlanMapppingPojo>();

		List<CustSpecialPlanMappping> mappingList = new ArrayList<CustSpecialPlanMappping>();
		for (CustSpecialPlanMapppingPojo custSpecialPlanMapppingPojo : pojoList) {

			CustSpecialPlanMappping custSpecialPlanMappping = null;
			if (custSpecialPlanMapppingPojo != null) {
			//	custSpecialPlanMapppingPojo.setNextStaff(getLoggedInUserId());

				if (custSpecialPlanMapppingPojo.getId() == null)
					custSpecialPlanMappping = new CustSpecialPlanMappping();
				else
					custSpecialPlanMappping = findById(custSpecialPlanMapppingPojo.getId());

				if (custSpecialPlanMapppingRepository.isDuplicateRecordFound(
						custSpecialPlanMapppingPojo.getSpecialPlanId(), custSpecialPlanMapppingPojo.getNormalPlanId(),
						custSpecialPlanMapppingPojo.getCustomerId(), custSpecialPlanRelMappping.getId()) < 1) {

					// check special Plan id available or not
					if (custSpecialPlanMapppingPojo.getSpecialPlanId() != null) {
						PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custSpecialPlanMapppingPojo.getSpecialPlanId()).get();

						if (postpaidPlan != null)
							custSpecialPlanMappping.setSpecialPlan(postpaidPlan);
						else
							throw new CustomValidationException(APIConstants.FAIL, "Special plan not available!", null);
					} else {
						throw new CustomValidationException(APIConstants.FAIL, "Special plan is Mandatory!", null);
					}
					// check special Plan with noraml plan mapping available or not
					if (custSpecialPlanMapppingPojo.getNormalPlanId() != null) {
						if (custSpecialPlanMapppingPojo.getNormalPlanId() != null && custSpecialPlanMapppingRepository
								.isDuplicateRecordFoundForPlan(custSpecialPlanMapppingPojo.getSpecialPlanId(),
										custSpecialPlanMapppingPojo.getNormalPlanId(), custSpecialPlanRelMappping.getId()) < 1) {
							PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custSpecialPlanMapppingPojo.getSpecialPlanId()).get();

							if (postpaidPlan != null)
								custSpecialPlanMappping.setNormalPlan(postpaidPlan);
						} else {
							throw new CustomValidationException(APIConstants.FAIL,
									"Mapping Alredy Exists for special plan ", null);
						}
					}
					if (custSpecialPlanMappping.getNormalPlan() != null) {
						custSpecialPlanMappping.setService(custSpecialPlanMapppingPojo.getService());
						custSpecialPlanMappping.setCustSpecialPlanRelMappping(custSpecialPlanRelMappping);
						// TODO: pass mvnoID manually 6/5/2025
						custSpecialPlanMappping.setMvnoId(getMvnoIdFromCurrentStaff(null));
						mappingList.add(custSpecialPlanMappping);
					}
				} else {
					throw new CustomValidationException(APIConstants.FAIL, "Mapping Alredy Exists for special plan ",
							null);
				}
			}

		}

		return mappingList;

	}

	private List<CustSpecialPlanMappping> saveOrUpdatePlanGroupMappping(Set<CustSpecialPlanMapppingPojo> pojoList, CustSpecialPlanRelMappping custSpecialPlanRelMappping) {
		List<CustSpecialPlanMapppingPojo> response = new ArrayList<CustSpecialPlanMapppingPojo>();

		List<CustSpecialPlanMappping> mappingList = new ArrayList<CustSpecialPlanMappping>();
		for (CustSpecialPlanMapppingPojo custSpecialPlanMapppingPojo : pojoList) {
			CustSpecialPlanMappping custSpecialPlanMappping = null;
			if (custSpecialPlanMapppingPojo != null) {
				if (custSpecialPlanMapppingPojo.getId() == null)
					custSpecialPlanMappping = new CustSpecialPlanMappping();
				else
					custSpecialPlanMappping = findById(custSpecialPlanMapppingPojo.getId());

				if (custSpecialPlanMapppingRepository.isDuplicateRecordFoundForPlanGroup(
						custSpecialPlanMapppingPojo.getSpecialPlanGroupId(), custSpecialPlanMapppingPojo.getNormalPlanGroupId(),
						custSpecialPlanMapppingPojo.getCustomerId(), custSpecialPlanRelMappping.getId()) < 1) {

					// check special Plan group id available or not
					if (custSpecialPlanMapppingPojo.getSpecialPlanGroupId() != null) {
						// TODO: pass mvnoID manually 6/5/2025
						PlanGroup planGroup = planGroupService
								.findPlanGroupById(custSpecialPlanMapppingPojo.getSpecialPlanGroupId(), getMvnoIdFromCurrentStaff(null));
						if (planGroup != null)
							custSpecialPlanMappping.setSpecialPlanGroup(planGroup);
						else
							throw new CustomValidationException(APIConstants.FAIL, "Special plan group not available!", null);
					} else {
						throw new CustomValidationException(APIConstants.FAIL, "Special plan group is Mandatory!", null);
					}
					// check special Plan group with noraml plan group mapping available or not
					if (custSpecialPlanMapppingPojo.getNormalPlanGroupId() != null) {
						if (custSpecialPlanMapppingPojo.getNormalPlanGroupId() != null && custSpecialPlanMapppingRepository
								.isDuplicateRecordFoundPlanGroup(custSpecialPlanMapppingPojo.getSpecialPlanGroupId(),
										custSpecialPlanMapppingPojo.getNormalPlanGroupId(), custSpecialPlanRelMappping.getId()) < 1) {
							// TODO: pass mvnoID manually 6/5/2025
							PlanGroup planGroup = planGroupService
									.findPlanGroupById(custSpecialPlanMapppingPojo.getNormalPlanGroupId(), getMvnoIdFromCurrentStaff(null));
							if (planGroup != null)
								custSpecialPlanMappping.setNormalPlanGroup(planGroup);
						} else {
							throw new CustomValidationException(APIConstants.FAIL,
									"Mapping Alredy Exists for special plan group ", null);
						}
					}
					if (custSpecialPlanMappping.getNormalPlanGroup() != null) {
						custSpecialPlanMappping.setService(custSpecialPlanMapppingPojo.getService());
						custSpecialPlanMappping.setCustSpecialPlanRelMappping(custSpecialPlanRelMappping);
						// TODO: pass mvnoID manually 6/5/2025
						custSpecialPlanMappping.setMvnoId(getMvnoIdFromCurrentStaff(null));
						mappingList.add(custSpecialPlanMappping);
					}
				} else {
					throw new CustomValidationException(APIConstants.FAIL, "Mapping Alredy Exists for special plan group",
							null);
				}
			}

		}

		return mappingList;
	}

	public boolean isCustomerPrimeOrNot(Integer custId, Long leadCust) {
		try {
			if (custId != null) {
				Customers customer = customersRepository.findById(custId).get();
				if (customer != null) {
					if (custSpecialPlanMapppingRepository.findAllByCustomer(customer).size() > 0) {
						return true;
					}
					if (customer.getPlangroup() != null) {
						if (custSpecialPlanMapppingRepository.existsByNormalPlanGroup(customer.getPlangroup()))
							return true;
						else
							return false;
					}
					if (!CollectionUtils.isEmpty(customer.getPlanMappingList())) {
						List<Integer> planIds = customer.getPlanMappingList().stream().map(CustPlanMappping::getPlanId)
								.collect(Collectors.toList());
						if (custSpecialPlanMapppingRepository.countByNormalPlan(planIds) > 0)
							return true;
						else
							return false;
					} else
						return false;
				} else {
					throw new CustomValidationException(APIConstants.FAIL, "Customer is not found with id = " + custId,
							null);
				}
			}
			else if(leadCust != null){
				LeadMaster leadMaster = leadMasterRepository.findById(leadCust).get();
				if(leadMaster != null){
					if (custSpecialPlanMapppingRepository.findAllByLeadMaster(leadMaster).size() > 0) {
						return true;
					}
					if (leadMaster.getPlangroupid() != null) {
						if (custSpecialPlanMapppingRepository.existsByNormalPlanGroup(leadMaster.getPlangroupid()))
							return true;
						else
							return false;
					}
					if (!CollectionUtils.isEmpty(leadMaster.getPlanMappingList())) {
						List<Integer> planIds = leadMaster.getPlanMappingList().stream().map(LeadCustPlanMappping::getPlanId)
								.collect(Collectors.toList());
						if (custSpecialPlanMapppingRepository.countByNormalPlan(planIds) > 0)
							return true;
						else
							return false;
					} else
						return false;

				}else {
					throw new CustomValidationException(APIConstants.FAIL, "Customer is not found with id = " + custId,
							null);
				}
			} else {
				throw new CustomValidationException(APIConstants.FAIL, "Customer id is null!", null);
			}
		} catch (Exception ex) {
			return false;
		}
	}

	public List<CustSpecialPlanMappping> findAllByNormalPlanIds(List<PostpaidPlan> planids) {
		if (!CollectionUtils.isEmpty(planids)) {
			QCustSpecialPlanMappping qCustSpecialPlanMappping = QCustSpecialPlanMappping.custSpecialPlanMappping;
			BooleanExpression booleanExpression = qCustSpecialPlanMappping.isNotNull();
			booleanExpression = booleanExpression.and(qCustSpecialPlanMappping.normalPlan.id.in(planids.get(0).getId()));
			List<CustSpecialPlanMappping> list = (List<CustSpecialPlanMappping>) custSpecialPlanMapppingRepository
					.findAll(booleanExpression);
			return list;
		}
		return null;
	}

	public Page<CustSpecialPlanMappping> findAllByNormalPlanWithPagination(Integer pageNumber, Integer customPageSize,
			Integer sortOrder, String listType) {
		if (sortOrder == null)
			sortOrder = 0;
		pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
		QCustSpecialPlanMappping qCustSpecialPlanMappping = QCustSpecialPlanMappping.custSpecialPlanMappping;
		BooleanExpression booleanExpression = qCustSpecialPlanMappping.isNotNull();
		if (listType != null && !"".equals(listType) && "normalmapping".equals(listType))
			booleanExpression = booleanExpression.and(qCustSpecialPlanMappping.normalPlan.id.isNotNull());
		else if (listType != null && !"".equals(listType) && "customermapping".equals(listType))
			booleanExpression = booleanExpression.and(qCustSpecialPlanMappping.customer.id.isNotNull());
		return (Page<CustSpecialPlanMappping>) custSpecialPlanMapppingRepository.findAll(booleanExpression,
				pageRequest);
	}
	
	public Page<CustSpecialPlanRelMappping> findAllByCustPlanRelWithPagination(Integer pageNumber, Integer customPageSize,
			Integer sortOrder) {
		if (sortOrder == null)
			sortOrder = 0;
		pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
		QCustSpecialPlanRelMappping qCustSpecialPlanRelMappping = QCustSpecialPlanRelMappping.custSpecialPlanRelMappping;
		BooleanExpression booleanExpression = qCustSpecialPlanRelMappping.isNotNull();
		// TODO: pass mvnoID manually 6/5/2025
		if(getMvnoIdFromCurrentStaff(null) != 1)
			// TODO: pass mvnoID manually 6/5/2025
			booleanExpression = booleanExpression.and(qCustSpecialPlanRelMappping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
		if(getBUIdsFromCurrentStaff().size() != 0)
			// TODO: pass mvnoID manually 6/5/2025
			booleanExpression = booleanExpression
					.and(qCustSpecialPlanRelMappping.mvnoId.eq(1)
							.or(qCustSpecialPlanRelMappping.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qCustSpecialPlanRelMappping.buId.in(getBUIdsFromCurrentStaff()))));
		return (Page<CustSpecialPlanRelMappping>) relMappingRepo.findAll(booleanExpression, pageRequest);
	}
	
	public CustSpecialPlanRelMapppingPojo findCustPlanRelById(Long id) {
		CustSpecialPlanRelMappping mapping = relMappingRepo.getOne(id);
		if(mapping == null) {
			throw new CustomValidationException(APIConstants.FAIL, "Relation mapping not available!", null);
		}
		Set<CustSpecialPlanMapppingPojo> custMappingPojo = null;
		Set<CustSpecialPlanMapppingPojo> planMappingPojo = null;
		Set<CustSpecialPlanMapppingPojo> planGroupMappingPojo = null;
		Set<CustSpecialPlanMapppingPojo> leadcustMappingPojo =  null;
		
		List<CustSpecialPlanMappping> mappingList = custSpecialPlanMapppingRepository.findAllByCustSpecialPlanRelMappping(mapping);
		if(!CollectionUtils.isEmpty(mappingList)) {
			List<CustSpecialPlanMappping> custList = mappingList.stream().filter(data -> data.getCustomer() != null).collect(Collectors.toList());
			if(!CollectionUtils.isEmpty(custList)) {
				custMappingPojo = custList.stream()
						.map(data -> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());
			}

			List<CustSpecialPlanMappping> leadcustList = mappingList.stream().filter(data -> data.getLeadMaster() != null).collect(Collectors.toList());
			if(!CollectionUtils.isEmpty(leadcustList)) {
				leadcustMappingPojo = leadcustList.stream()
						.map(data -> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());
			}
			List<CustSpecialPlanMappping> planMapping = mappingList.stream().filter(data -> data.getNormalPlan() != null).collect(Collectors.toList());
			if(!CollectionUtils.isEmpty(planMapping)) {
				planMappingPojo = planMapping.stream()
						.map(data -> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());
			}

			List<CustSpecialPlanMappping> planGroupMapping = mappingList.stream().filter(data -> data.getNormalPlanGroup() != null).collect(Collectors.toList());
			if(!CollectionUtils.isEmpty(planGroupMapping)) {
				planGroupMappingPojo = planGroupMapping.stream()
						.map(data -> new CustSpecialPlanMapppingPojo(data)).collect(Collectors.toSet());
			}
			
		}
		return new CustSpecialPlanRelMapppingPojo(mapping, custMappingPojo, planMappingPojo, planGroupMappingPojo,leadcustMappingPojo);
	}
	
	@Transactional
	public void deleteCustPlanRelById(Long id) {
		CustSpecialPlanRelMappping mapping = findByIdForUpdateOrDelete(id);
		if(mapping == null) {
			throw new CustomValidationException(APIConstants.FAIL, "Relation mapping not available!", null);
		}
//		List<CustSpecialPlanMappping> existingList = custSpecialPlanMapppingRepository.findAllByCustSpecialPlanRelMappping(mapping.get());
//		if(!CollectionUtils.isEmpty(existingList)) {
//			custSpecialPlanMapppingRepository.deleteInBatch(existingList);
//		}
				
		relMappingRepo.delete(mapping);
	}

	@Transactional
	public void deleteBulkCustSpecialPlanMappping(Integer[] ids) {
		for(Integer id : ids)
			findByIdForUpdateOrDelete(id);
		if (ids.length > 0 && ids != null)
			custSpecialPlanMapppingRepository.deleteUsersWithIds(Arrays.asList(ids));
		else
			throw new CustomValidationException(APIConstants.FAIL, "Id list can not be empty or null!", null);
	}
	
	@Transactional
	public void deleteCustSpecialPlanMappping(Integer id) {
		findByIdForUpdateOrDelete(id);
		if (id != null)
			custSpecialPlanMapppingRepository.deleteUsersWithIds(Arrays.asList(id));
		else
			throw new CustomValidationException(APIConstants.FAIL, "Id can not be empty or null!", null);
	}
	
	public Page<CustSpecialPlanRelMappping> searchList(Integer pageNumber, Integer customPageSize,Integer sortOrder, String name) {
		if (sortOrder == null)
			sortOrder = 0;
		pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
		
		QCustSpecialPlanRelMappping qCustSpecialPlanMappping = QCustSpecialPlanRelMappping.custSpecialPlanRelMappping;
		
		BooleanExpression booleanExpression = qCustSpecialPlanMappping.isNotNull();
		
		if (name != null) {
			// TODO: pass mvnoID manually 6/5/2025
			if(getMvnoIdFromCurrentStaff(null) != null && getMvnoIdFromCurrentStaff(null) == 1) {
				booleanExpression = booleanExpression.and(qCustSpecialPlanMappping.mappingName.containsIgnoreCase(name));
			}
			// TODO: pass mvnoID manually 6/5/2025
			else if(getMvnoIdFromCurrentStaff(null) != null && getMvnoIdFromCurrentStaff(null) != 1) {
				booleanExpression = booleanExpression.and(qCustSpecialPlanMappping.mappingName.containsIgnoreCase(name));
				//booleanExpression = booleanExpression.and(qCustSpecialPlanMappping.mvnoId.eq(getMvnoIdFromCurrentStaff()).or(qCustSpecialPlanMappping.mvnoId.eq(1)));
			}
			// TODO: pass mvnoID manually 6/5/2025
			if(getMvnoIdFromCurrentStaff(null) != 1)
				// TODO: pass mvnoID manually 6/5/2025
				booleanExpression = booleanExpression.and(qCustSpecialPlanMappping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
			if(getBUIdsFromCurrentStaff().size() != 0)
				// TODO: pass mvnoID manually 6/5/2025
				booleanExpression = booleanExpression
						.and(qCustSpecialPlanMappping.mvnoId.eq(1)
								.or(qCustSpecialPlanMappping.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qCustSpecialPlanMappping.buId.in(getBUIdsFromCurrentStaff()))));
		}
		return (Page<CustSpecialPlanRelMappping>) relMappingRepo.findAll(booleanExpression, pageRequest);
	}

	@Override
	public CustSpecialPlanMappping get(Integer id,Integer mvnoId) {
		CustSpecialPlanMappping custSpecialPlanMappping = custSpecialPlanMapppingRepository.findById(id).get();
		if(custSpecialPlanMappping != null) {
			// TODO: pass mvnoID manually 6/5/2025
			if (getMvnoIdFromCurrentStaff(null) == 1 || (custSpecialPlanMappping.getMvnoId() == getMvnoIdFromCurrentStaff(null) || custSpecialPlanMappping.getMvnoId() == 1))
				return custSpecialPlanMappping;
		}
		return null;
	}

//	public CustSpecialPlanMappping getCityForUpdateAndDelete(Integer id) {
//		CustSpecialPlanMappping custSpecialPlanMappping = get(id);
//		// TODO: pass mvnoID manually 6/5/2025
//		if(custSpecialPlanMappping == null || !(getMvnoIdFromCurrentStaff(null) == 1 || getMvnoIdFromCurrentStaff(null).intValue() == custSpecialPlanMappping.getMvnoId().intValue()))
//			throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//		return custSpecialPlanMappping;
//	}


	@Override
	public boolean duplicateVerifyAtSave(String name) {
		boolean flag = false;
		if (name != null) {
			name = name.trim();
			Integer count;
			// TODO: pass mvnoID manually 6/5/2025
			if(getMvnoIdFromCurrentStaff(null) == 1) count = relMappingRepo.duplicateVerifyAtSave(name);
			else {
				// TODO: pass mvnoID manually 6/5/2025
				if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) count = relMappingRepo.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
				else count = relMappingRepo.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff()); // TODO: pass mvnoID manually 6/5/2025
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
			if(getMvnoIdFromCurrentStaff(null) == 1) count = relMappingRepo.duplicateVerifyAtSave(name);
			else {
				if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
					// TODO: pass mvnoID manually 6/5/2025
					count = relMappingRepo.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
				else
					// TODO: pass mvnoID manually 6/5/2025
					count = relMappingRepo.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());            }
			if (count >= 1) {
				Integer countEdit;
				// TODO: pass mvnoID manually 6/5/2025
				if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = relMappingRepo.duplicateVerifyAtEdit(name, id);
				else {
					if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
						// TODO: pass mvnoID manually 6/5/2025
						countEdit = relMappingRepo.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
					else
						// TODO: pass mvnoID manually 6/5/2025
						countEdit = relMappingRepo.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
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


public void AssignSpecialPlanMappingWorkflow( CustSpecialPlanMappping custSpecialPlanMappping){

CustSpecialPlanRelMappping obj =relMappingRepo.findById(custSpecialPlanMappping.getCustSpecialPlanRelMappping().getId()).orElse(null);
	CustSpecialPlanRelMapppingPojo pojo = custSpecialPlanRelMapper.domainToDTO(obj, new CycleAvoidingMappingContext());

	if (obj.getNextTeamHierarchyMapping() == null && obj.getNextStaff() == null) {
		if (obj.getStatus() != null && !"".equals(obj.getStatus())) {
			if (obj.getStatus().equalsIgnoreCase("NewActivation")) {
				pojo.setId(obj.getId().longValue());
				StaffUser assignedStaff = null;
				if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
					Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(obj.getMvnoId(), obj.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, CommonConstants.HIERARCHY_TYPE, false, true, pojo);
					int staffId = 0;
					if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
						staffId = Integer.parseInt(map.get("staffId"));
						assignedStaff = staffUserRepository.findById(staffId).get();
					//	custSpecialPlanMappping.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
						//custSpecialPlanMappping.setNextStaff(staffId);
						if (obj != null) {
							String action = CommonConstants.WORKFLOW_MSG_ACTION.SPECIAL_PLAN_MAPPING + " with Name : " + " ' " + pojo.getName() + " ' ";
							hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), action,assignedStaff.getId().longValue());

						}
						workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, obj.getId().intValue(), obj.getMappingName(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
						if (assignedStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
							if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
								map.put("tat_id", map.get("current_tat_id"));
							tatUtils.saveOrUpdateDataForTatMatrix(map, assignedStaff, obj.getId().intValue(), null);
						}
					} else {
						StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
						assignedStaff = currentStaff;
						obj.setNextTeamHierarchyMapping(null);
						obj.setNextStaff(currentStaff.getId());
						workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, obj.getId().intValue(), obj.getMappingName(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
					}
				}
				else {
					StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
					assignedStaff = currentStaff;
					obj.setNextTeamHierarchyMapping(null);
					obj.setNextStaff(currentStaff.getId());
					workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, obj.getId().intValue(), obj.getMappingName(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
				}
			}
		}
	}
}


	public GenericDataDTO updateSpecialPlanAssignment(CustSpecialPlanRelMapppingPojo pojo) {
		CustSpecialPlanMappping oldObj = null;
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		if (pojo.getId() != null && pojo.getNextStaff() != null) {
			CustSpecialPlanRelMappping plan = findById(pojo.getId());
			StaffUser staffUser = staffUserRepository.findById(pojo.getNextStaff()).get();
			StaffUser loggedInUser =  staffUserRepository.findById(getLoggedInUserId()).get();
			StringBuilder approvedByName = new StringBuilder();
			if (!staffUser.getUsername().equalsIgnoreCase("admin")) {
				if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
					Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(plan.getMvnoId(), plan.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, CommonConstants.HIERARCHY_TYPE, pojo.getFlag().equalsIgnoreCase("approved") ? true : false, plan.getNextTeamHierarchyMapping() == null ? true : false, pojo);
					if (!map.containsKey("staffId") && !map.containsKey("nextTatMappingId")) {
						plan.setNextStaff(null);
						genericDataDTO.setDataList(null);
						plan.setNextTeamHierarchyMapping(null);
						if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
							plan.setStatus(SubscriberConstants.ACTIVE);
						} else if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected")) {
							plan.setStatus(SubscriberConstants.REJECT);
						}
						workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, plan.getId().intValue(), plan.getMappingName(), staffUser.getId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " +pojo.getRemarks() + "\n" + pojo.getFlag() + " by :- " + staffUser.getUsername());
					} else {
						plan.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
						plan.setNextStaff(Integer.valueOf(map.get("staffId")));
						StaffUser assigned = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
						workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, plan.getId().intValue(), plan.getMappingName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "Remarks  : " + pojo.getRemarks() + "\n" + pojo.getFlag() + " by :- " + staffUser.getUsername());
						workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, plan.getId().intValue(), plan.getMappingName(), staffUser.getId(), assigned.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + pojo.getRemarks() + "\n" + "Assigned to :- " + assigned.getUsername());
						if (assigned.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
							tatUtils.saveOrUpdateDataForTatMatrix(map, assigned, plan.getId().intValue(), null);
						}
					}
					if (plan != null) {
						if (plan.getStatus().equalsIgnoreCase("Approved") || plan.getStatus().equalsIgnoreCase("Rejected")) {
							map.put("entityId", plan.getId().toString());
							tatUtils.inActivateTatWorkflowMapping(map);
						}
					}
				} else {
					if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected") && plan.getNextTeamHierarchyMapping() == null) {
						hierarchyService.rejectDirectFromCreatedStaff(CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, plan.getId().intValue());
						plan.setNextStaff(null);
						workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, plan.getId().intValue(), plan.getMappingName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemarks() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
					} else {


						Map<String, Object> mapForManual = hierarchyService.getTeamForNextApprove(plan.getMvnoId(), plan.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, CommonConstants.HIERARCHY_TYPE, pojo.getFlag().equalsIgnoreCase("approved") ? true : false, plan.getNextTeamHierarchyMapping() == null, pojo);

						if (mapForManual.containsKey("assignableStaff")) {
							//PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(pojo.getPlanId()).orElse(null);
							CustSpecialPlanRelMappping postpaidPlan = custSpecialPlanRelMapppingRepository.findById(pojo.getId()).orElse(null);
							if (postpaidPlan != null) {
								if (postpaidPlan.getStatus().equalsIgnoreCase("Active")) {
									genericDataDTO.setDataList(null);
									plan.setNextStaff(null);
									plan.setNextTeamHierarchyMapping(null);
									workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, plan.getId().intValue(), plan.getMappingName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemarks() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
								} else {
									//plan.setNextTeamHierarchyMapping((Integer) mapForManual.get("nextTeamHierarchyMappingId"));
										genericDataDTO.setDataList((List<StaffUserPojo>) mapForManual.get("assignableStaff"));
									workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, plan.getId().intValue(), plan.getMappingName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemarks() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
								}
							}


						} else {

							plan.setNextStaff(null);
							plan.setNextTeamHierarchyMapping(null);
							if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
								//	plan.setStatus("Approved");
								plan.setStatus(SubscriberConstants.ACTIVE);
							} else if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected")) {
								plan.setStatus("Rejected");
								plan.setStatus(SubscriberConstants.REJECT);
							}


							workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, plan.getId().intValue(), plan.getMappingName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemarks() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
						}
					}
				}

			} else {
				approvedByName.append("Administrator");
				if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
					plan.setStatus(SubscriberConstants.ACTIVE);
					plan.setNextStaff(null);
					plan.setNextTeamHierarchyMapping(null);
				} else {
					plan.setStatus(SubscriberConstants.REJECT);
					plan.setNextStaff(null);
					plan.setNextTeamHierarchyMapping(null);
				}
				workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, plan.getId().intValue(), plan.getMappingName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("rejected") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " +plan.getRemarks() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
			}

			custSpecialPlanRelMapppingRepository.save(plan);
			//saveCustSpecialPlanMappping(pojo);
		}
		return genericDataDTO;
	}
	public CustSpecialPlanMappping getEntityForUpdateAndDelete(Integer id) {
		CustSpecialPlanMappping custSpecialPlanMappping = custSpecialPlanMapppingRepository.findById(id).get();
		// TODO: pass mvnoID manually 6/5/2025
		if (custSpecialPlanMappping == null || (!(getMvnoIdFromCurrentStaff(null) == 1 || getMvnoIdFromCurrentStaff(null).intValue() == custSpecialPlanMappping.getMvnoId().intValue()) && (custSpecialPlanMappping.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(custSpecialPlanMappping.getCustSpecialPlanRelMappping().getBuId()))))
			throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
		return custSpecialPlanMappping;
	}

    public GenericDataDTO getSpecialPlanMappingApprovals(PaginationRequestDTO paginationRequestDTO) {
		PageRequest pageRequest = staffUserService.generatePageRequest(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
		QCustSpecialPlanRelMappping qCustSpecialPlanRelMappping =QCustSpecialPlanRelMappping.custSpecialPlanRelMappping;
		GenericDataDTO genericDataDTO = new GenericDataDTO();

		BooleanExpression booleanExpression = qCustSpecialPlanRelMappping.isNotNull().and(qCustSpecialPlanRelMappping.nextStaff.eq(getLoggedInUserId()));
		// TODO: pass mvnoID manually 6/5/2025
		if (getMvnoIdFromCurrentStaff(null) != 1)
			// TODO: pass mvnoID manually 6/5/2025
			booleanExpression = booleanExpression.and(qCustSpecialPlanRelMappping.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
		if (getBUIdsFromCurrentStaff() != null && staffUserService.getBUIdsFromCurrentStaff().size() > 0) {
			// TODO: pass mvnoID manually 6/5/2025
			booleanExpression = booleanExpression.and(qCustSpecialPlanRelMappping.mvnoId.eq(1).or(qCustSpecialPlanRelMappping.mvnoId.eq(getMvnoIdFromCurrentStaff(null))
					.and(qCustSpecialPlanRelMappping.buId.in(getBUIdsFromCurrentStaff()))));
		}

		Page<CustSpecialPlanRelMappping> paginationList = custSpecialPlanRelMapppingRepository.findAll(booleanExpression, pageRequest);
		genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> custSpecialPlanRelMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
		genericDataDTO.setResponseCode(HttpStatus.OK.value());
		genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
		genericDataDTO.setTotalRecords(paginationList.getTotalElements());
		genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
		genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
		genericDataDTO.setTotalPages(paginationList.getTotalPages());
		return genericDataDTO;
    }

	public List<CustSpecialPlanMappping> findAllByLeadCustomers(Integer leadCustId) {
			if (leadCustId != null) {
				LeadMaster leadMaster = leadMasterRepository.findById(Long.valueOf(leadCustId)).get();
				if (leadMaster != null) {
					return custSpecialPlanMapppingRepository.findAllByLeadMaster(leadMaster);
//						.stream().filter(custSpecialPlanMappping -> custSpecialPlanMappping.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || custSpecialPlanMappping.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
				} else {
					throw new CustomValidationException(APIConstants.FAIL, "Lead is not found with id = " + leadCustId,
							null);
				}
			} else {
				throw new CustomValidationException(APIConstants.FAIL, "Lead id is null!", null);
			}


	}
}
