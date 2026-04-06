package com.adopt.apigw.modules.VoucherBatch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.LocationMaster.service.LocationMasterService;
import com.adopt.apigw.modules.Reseller.domain.Reseller;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Reseller.mapper.WifiUtils;
import com.adopt.apigw.modules.Reseller.service.ResellerService;
import com.adopt.apigw.modules.Voucher.domain.QVoucher;
import com.adopt.apigw.modules.Voucher.domain.Voucher;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.Voucher.module.ValidateCrudTransactionData;
import com.adopt.apigw.modules.Voucher.repository.VoucherRepository;
import com.adopt.apigw.modules.Voucher.service.VoucherService;
import com.adopt.apigw.modules.VoucherBatch.domain.BSSVoucherBatch;
import com.adopt.apigw.modules.VoucherBatch.domain.QBSSVoucherBatch;
import com.adopt.apigw.modules.VoucherBatch.module.UpdateVoucherBatchDto;
import com.adopt.apigw.modules.VoucherBatch.module.VoucherBatchDto;
import com.adopt.apigw.modules.VoucherBatch.module.VoucherBatchInfoDto;
import com.adopt.apigw.modules.VoucherBatch.repository.BSSVoucherBatchRepository;
import com.adopt.apigw.modules.VoucherConfiguration.domain.VoucherConfiguration;
import com.adopt.apigw.modules.VoucherConfiguration.repository.VoucherConfigurationRepository;
import com.adopt.apigw.modules.VoucherConfiguration.service.VoucherConfigurationService;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class VoucherBatchServiceImpl implements VoucherBatchService {
	final Logger log = LoggerFactory.getLogger(VoucherBatchServiceImpl.class);
	@Autowired
	private BSSVoucherBatchRepository voucherBatchRepository;

	@Autowired
	private ResellerService resellerService;

	@Autowired
	private PostpaidPlanService planService;

	@Autowired
	private VoucherBatchService voucherBatchService;

	@Autowired
	private LocationMasterService locationMasterService;

	@Autowired
	private VoucherConfigurationService voucherConfigurationService;

	@Autowired
	private VoucherService voucherService;

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private VoucherConfigurationRepository voucherConfigurationRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	CreateDataSharedService createDataSharedService;

	@Override
	public VoucherBatchDto saveVoucherBatch(VoucherBatchDto voucherBatchDto, Long mvnoId) {
		try {
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
				throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
			}
			PostpaidPlan plan=null;
			if(Objects.nonNull(voucherBatchDto.getPlanId())) {
				plan= planService.findById(voucherBatchDto.getPlanId());
				if (plan == null)
					throw new RuntimeException("Plan is mandatory. Please add correct plan");

				voucherBatchDto.setPrice(plan.getOfferprice());
			}

			voucherBatchDto.setCreatedByStaffId(getLoggedInUser().getStaffId());
			if (voucherBatchDto.getCreateDate() == null)
				voucherBatchDto.setCreateDate(LocalDateTime.now());
			Reseller reseller = null;
			VoucherConfiguration voucherConfiguration = null;
			if (voucherBatchDto.getVoucherProfileId() != null)
				if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
					throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
				}else if (getBUIdsFromCurrentStaff().size() == 1){
					voucherBatchDto.setBuId(getBUIdsFromCurrentStaff().get(0));
				}
				voucherConfiguration = voucherConfigurationService.findById(voucherBatchDto.getVoucherProfileId(), mvnoId);
			if(!voucherConfiguration.getStatus().equalsIgnoreCase("ACTIVE")){
				throw new CustomValidationException(APIConstants.FAIL, "Voucher Profile Is In Active", null);
			}
			BSSVoucherBatch voucherBatch = new BSSVoucherBatch();
            if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
				 voucherBatch = new BSSVoucherBatch(voucherBatchDto, plan, reseller, voucherConfiguration, mvnoId, getBUIdsFromCurrentStaff().get(0));
			}
			else{
				voucherBatch = new BSSVoucherBatch(voucherBatchDto, plan, reseller, voucherConfiguration, mvnoId, null);
			}
			if( Objects.nonNull(voucherConfiguration) && Objects.isNull(voucherBatch.getPrice())){
				voucherBatch.setPrice(voucherBatch.getVoucherConfiguration().getVoucherAmount());
			}
			validateVoucherBatchDetail(voucherBatch, false);
			if (Objects.nonNull(voucherConfiguration)){
			voucherBatch.setExpirydate(LocalDateTime.now().plusDays(voucherConfiguration.getValidity()));

			}
			if (voucherBatchDto.getResellerId() != null){
				reseller = resellerService.findResellerById(voucherBatchDto.getResellerId(), mvnoId, false);
				validateReseller(reseller.getResellerId(), mvnoId, (voucherBatch.getPrice() * voucherBatch.getVoucherQuantity()), false);
				voucherBatch.setReseller(reseller);
			}
			voucherBatch.setCreatedByStaffId(getLoggedInUser().getStaffId());
			BSSVoucherBatch saveVoucherBatchDto = voucherBatchRepository.save(voucherBatch);
			return new VoucherBatchDto(saveVoucherBatchDto);
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void generateBatchAndVouchers(VoucherBatchDto voucherBatchDto, Long mvnoId) {
		try{
			VoucherBatchDto voucherBatch = saveVoucherBatch(voucherBatchDto, mvnoId);
			VoucherConfiguration voucherConfiguration = voucherConfigurationRepository.findById(voucherBatchDto.getVoucherProfileId()).get();
			if(!voucherConfiguration.getStatus().equalsIgnoreCase("ACTIVE")){
				throw  new RuntimeException("Voucher Configuration is InActive.Please Active Voucher Configuration");
			}
			if(!voucherBatch.equals(null))
			{
				voucherService.generateBatch(voucherBatch.getVoucherBatchId(), voucherBatch.getVoucherProfileId(), voucherConfiguration.getMvnoId());
			}} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private void validateVoucherBatchDetail(BSSVoucherBatch voucherBatch, boolean isUpdate)
	{
		try {
			if (!ValidateCrudTransactionData.validateStringTypeFieldValue(voucherBatch.getBatchName()))
				throw new RuntimeException("VoucherBatch name is mandatory. Please enter valid VoucherBatch name");
			else if (voucherBatch.getPrice() == null) {
				throw new RuntimeException("Price is mandatory. Please enter valid voucherBatch Price");
			} else if (voucherBatch.getCreateDate() == null) {
				throw new RuntimeException("Start Date is mandatory. Please enter valid Start Date(YYYY-MM-DD)");
			} else if (voucherBatch.getVoucherQuantity() == null || voucherBatch.getVoucherQuantity() <= 0) {
				throw new RuntimeException("Voucher Quantity is mandatory. Please enter valid quantity");
			} else if (isUpdate) {
				if (voucherBatch.getVoucherBatchId() == null || voucherBatch.getVoucherBatchId() == 0) {
					throw new RuntimeException("Please enter valid VoucherBatch id");
				} else {
					Optional<BSSVoucherBatch> optionalVoucherBatch = voucherBatchRepository
							.findById(voucherBatch.getVoucherBatchId());
					if (!optionalVoucherBatch.isPresent()) {
						throw new RuntimeException("No record found with voucherBatch id : '"
								+ voucherBatch.getVoucherBatchId() + "',Please enter valid id to update record.");
					}
//					else {
//						voucherBatch.setCreatedOn(optionalVoucherBatch.get().getCreatedOn());
//					}
				}
			}
			checkForUniqueVoucherBatch(voucherBatch.getBatchName(), voucherBatch.getMvnoId(), voucherBatch.getVoucherBatchId(), isUpdate);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	private void checkForUniqueVoucherBatch(String batchName, Long mvnoId, Long voucherBatchId, boolean isUpdate) {
		try {
			String message = "Voucher batch exist with the same name : '" + batchName + "'";
			QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
			BooleanExpression boolExp = qVoucherBatch.isNotNull();
			if (isUpdate) {
				boolExp = boolExp.and(qVoucherBatch.voucherBatchId.ne(voucherBatchId));
			}

			if (mvnoId == 1) {
				boolExp = boolExp.and(qVoucherBatch.batchName.eq(batchName));
				List<BSSVoucherBatch> voucherBatchList = (List<BSSVoucherBatch>) voucherBatchRepository.findAll(boolExp);
				if (!voucherBatchList.isEmpty()) {
					throw new IllegalArgumentException(message);
				}
			} else {
				boolExp = boolExp.and(qVoucherBatch.batchName.eq(batchName))
						.and((qVoucherBatch.mvnoId.eq(mvnoId)).or(qVoucherBatch.mvnoId.eq(1L)));
				Optional<BSSVoucherBatch> optionalVoucherBatch = voucherBatchRepository.findOne(boolExp);
				if (optionalVoucherBatch.isPresent()) {
					throw new IllegalArgumentException(message);
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public UpdateVoucherBatchDto updateVoucherBatch(UpdateVoucherBatchDto voucherBatchDto, Long mvnoId) {
		MDC.put(APIConstants.TYPE, APIConstants.TYPE_UPDATE);
		try {
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
				throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
			}
			PostpaidPlan plan = planService.findById(Math.toIntExact(voucherBatchDto.getPlanId()));
			Reseller reseller = null;
			voucherBatchDto.setPrice(plan.getOfferprice());
			if(voucherBatchDto.getResellerId() != null)
				reseller = resellerService.findResellerById(voucherBatchDto.getResellerId(), mvnoId, false);
			BSSVoucherBatch voucherBatchVo = findVoucherBatchById(voucherBatchDto.getVoucherBatchId(), mvnoId);
			QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
			BooleanExpression exp = qVoucherBatch.isNotNull();
			if(mvnoId != 1)
				exp = exp.and(qVoucherBatch.mvnoId.eq(mvnoId));
			exp = exp.and(qVoucherBatch.voucherBatchId.eq(voucherBatchDto.getVoucherBatchId()));
			Optional<BSSVoucherBatch> voucherBatch = voucherBatchRepository.findOne(exp);

			if (!voucherBatch.isPresent()) {
				throw new RuntimeException("No record found or You do not have access to update this record.");
			}
			voucherBatchVo.setMvnoId(voucherBatch.get().getMvnoId());
			String updatedValues = WifiUtils.getUpdatedDiff(voucherBatch, voucherBatchVo);
			validateVoucherBatchDetail(voucherBatchVo, true);
			if(voucherBatchDto.getResellerId() != null && voucherBatchVo.getReseller() != null) {
				if(voucherBatchVo.getReseller().getResellerId() != voucherBatchDto.getResellerId()) {
					validateReseller(voucherBatchDto.getResellerId(), mvnoId, (voucherBatchVo.getPrice() * voucherBatchVo.getVoucherQuantity()), true);
					voucherBatchVo.setReseller(reseller);
				}
			}
			if(voucherBatchVo.getReseller() == null)
				voucherBatchVo.setReseller(reseller);
//			voucherBatchVo.setLastModifiedBy(CommonConstants.USER_ADMIN);
//			voucherBatchVo.setLastModifiedOn(LocalDateTime.now());
			log.info("VoucherBatch has been update successfully by " + MDC.get("userName") + " the difference is "
					+ updatedValues);
			BSSVoucherBatch saveVoucherBatch = voucherBatchRepository.save(voucherBatchVo);

			return new UpdateVoucherBatchDto(saveVoucherBatch, voucherBatch.get().getMvnoId());
		} catch (Throwable e) {
			log.error("Error while update VoucherBatch: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(APIConstants.TYPE);
		}
	}

	@Override
	public void deleteVoucherBatchById(Long voucherBatchId, Long mvnoId) {
		MDC.put(APIConstants.TYPE, APIConstants.TYPE_DELETE);
		try {
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
				throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
			} else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(voucherBatchId)) {
				throw new IllegalArgumentException(
						APIConstants.BASIC_NUMERIC_MSG + "Please enter valid voucherBatch id to delete record.");
			} else {
				if(voucherService.countByBatchId(voucherBatchId) > 0)
					throw new IllegalArgumentException("This operation will not allow as there are some voucher batches available related to this profile .");
				QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
				BooleanExpression exp = qVoucherBatch.isNotNull();
				if(mvnoId != 1)
					exp = exp.and(qVoucherBatch.mvnoId.eq(mvnoId));
				exp = exp.and(qVoucherBatch.voucherBatchId.eq(voucherBatchId));
				Optional<BSSVoucherBatch> optionalVoucherBatch = voucherBatchRepository.findOne(exp);

				if (!optionalVoucherBatch.isPresent()) {
					throw new RuntimeException("No record found for VoucherBatch with the given VoucherBatch id :'"
							+ voucherBatchId + "', Please enter valid VoucherBatch id to delete the record.");

				} else {
					log.info("VoucherBatch has been deleted successfully: " + optionalVoucherBatch.get().getVoucherBatchId()
							+ " by " + MDC.get("username"));
					voucherBatchRepository.deleteById(optionalVoucherBatch.get().getVoucherBatchId());
				}
			}
		} catch (Throwable e) {
			log.error("Error while delete VoucherBatch: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(APIConstants.TYPE);
		}

	}



	@Override
	public List<VoucherBatchInfoDto> findAllVoucherBatch(Long mvnoId) {
		try {
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
				throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
			} else {
				PostpaidPlan plan = planService.findById(Math.toIntExact(mvnoId));
				Set<BSSVoucherBatch> voucherBatches = new HashSet<>();
				if(Objects.nonNull(plan)){
					voucherBatches.addAll(findAllVoucherBatchByPlanId(plan.getId().longValue(), mvnoId));
				}
				List<BSSVoucherBatch> listVoucherBatch = new ArrayList<>(voucherBatches);
				List<VoucherBatchInfoDto> voucherBatchInfoDtoList = new ArrayList<>();
				for (BSSVoucherBatch voucherBatch : listVoucherBatch) {
					VoucherBatchInfoDto dto = new VoucherBatchInfoDto(voucherBatch);
					voucherBatchInfoDtoList.add(dto);
				}
				return voucherBatchInfoDtoList;
			}
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}


	@Override
	public PageableResponse<VoucherBatchInfoDto> getAllVoucherBatch(Long mvnoId, Long resellerId, String batchName,
																	PaginationDTO paginationDto) {
		try {
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
				throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
			}
			PageableResponse<BSSVoucherBatch> pageableResponse = new PageableResponse<>();
			QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
			BooleanExpression exp = qVoucherBatch.isNotNull();
//			Set<Plan> plans = new HashSet<>(planService.getPlans(mvnoId, locationId));
//			Set<VoucherBatch> voucherBatches = new HashSet<>();
//			for(Plan plan : plans){
//				voucherBatches.addAll(findAllVoucherBatchByPlanId(plan.getPlanId(), mvnoId));
//			}
			// check mvnoid for superadmin
			if (mvnoId != 1) {
				exp = exp.and(qVoucherBatch.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			}
			if(getBUIdsFromCurrentStaff().size()!=0){
				exp = exp.and(qVoucherBatch.buId.in(getBUIdsFromCurrentStaff()));
			}
			if(resellerId != null)
			{
				exp = exp.and(qVoucherBatch.reseller.resellerId.eq(resellerId));
			}


			if(getLoggedInUserPartnerId()!=1)
			{
				exp = exp.and(qVoucherBatch.createdByStaffId.eq(getLoggedInUser().getStaffId()));
			}

			if(paginationDto.getPage() > 0) {
				paginationDto.setPage(paginationDto.getPage() - 1);
			}
			Pageable pageable = PageRequest.of(paginationDto.getPage(), paginationDto.getSize(), Sort.by(Sort.Direction.DESC, "voucherBatchId"));


			//Check date filter
//			if(!(StringUtils.isBlank(paginationDto.getFromDate()) || paginationDto.getFromDate().equalsIgnoreCase("null")))
//			{
//				exp=exp.and(qVoucherBatch.eventTime.eq(Timestamp.valueOf(paginationDto.getFromDate() + " 00:00:00"))
//						.or(qVoucherBatch.eventTime.after(Timestamp.valueOf(paginationDto.getFromDate() + " 00:00:00"))));
//			}
//			if(!(StringUtils.isBlank(paginationDto.getToDate()) || paginationDto.getToDate().equalsIgnoreCase("null")))
//			{
//				exp=exp.and(qVoucherBatch.eventTime.eq(Timestamp.valueOf(paginationDto.getToDate() + " 23:59:59"))
//						.or(qVoucherBatch.eventTime.before(Timestamp.valueOf(paginationDto.getToDate() + " 23:59:59"))));
//			}
			//Check search filter
			if(!StringUtils.isBlank(batchName)) {
				exp=exp.and(qVoucherBatch.batchName.containsIgnoreCase(batchName));
			}
			Page<BSSVoucherBatch> page = voucherBatchRepository.findAll(exp, pageable);
			return pageableResponse.convert(new PageImpl<>(page.getContent(), pageable, page.getTotalElements()));
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<BSSVoucherBatch> searchVoucherBatch(String batchName, Long mvnoId) {
		try {
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
				throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + " Please enter valid mvno id.");
			} else {
				QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
				BooleanExpression boolExp = qVoucherBatch.isNotNull();
				if (StringUtils.isBlank(batchName) || batchName.equalsIgnoreCase("null")) {
					if (mvnoId != 1)
						boolExp = boolExp.and(qVoucherBatch.mvnoId.eq(mvnoId)).or(qVoucherBatch.mvnoId.eq(1L));
					if(getBUIdsFromCurrentStaff().size()!=0){
						boolExp = boolExp.and(qVoucherBatch.buId.in(getBUIdsFromCurrentStaff()));
					}
				} else {
					if (mvnoId == 1)
						boolExp = boolExp.and(qVoucherBatch.batchName.contains(batchName));
					else
						boolExp = boolExp.and(qVoucherBatch.batchName.contains(batchName)).and(qVoucherBatch.mvnoId.in(mvnoId, 1));
				}
				List<BSSVoucherBatch> listVoucherBatch = (List<BSSVoucherBatch>) voucherBatchRepository.findAll(boolExp);
				return listVoucherBatch;
			}
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public BSSVoucherBatch findVoucherBatchById(Long voucherBatchId, Long mvnoId) {
		try {
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
				throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
			} else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(voucherBatchId)) {
				throw new IllegalArgumentException(
						APIConstants.BASIC_NUMERIC_MSG + "Please enter valid voucherBatch id.");
			} else {
				QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
				BooleanExpression boolExp = qVoucherBatch.isNotNull();
				if(mvnoId == null || mvnoId != 1)
					boolExp = boolExp.and(qVoucherBatch.mvnoId.in(mvnoId, 1));
//				if(getBUIdsFromCurrentStaff().size()!=0){
//					boolExp = boolExp.and(qVoucherBatch.buId.in(getBUIdsFromCurrentStaff()));
//				}
				boolExp = boolExp.and(qVoucherBatch.voucherBatchId.eq(voucherBatchId));
				Optional<BSSVoucherBatch> optionalVoucherBatch = voucherBatchRepository.findOne(boolExp);
				if (optionalVoucherBatch.isPresent()) {
					return optionalVoucherBatch.get();
				} else {
					throw new RuntimeException("No record found for voucherBatch with the given voucherBatch id :'"
							+ voucherBatchId + "', Please enter valid voucherBatch id");
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}



	@Override
	public List<BSSVoucherBatch> findVoucherBatcheWithoutReseller( Long mvnoId) {
		try {
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
				throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
			} else {
				QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
				BooleanExpression exp = qVoucherBatch.isNotNull();
				if (mvnoId == 1) {
					List<BSSVoucherBatch> listVoucherBatch = voucherBatchRepository.findAll();
					List<BSSVoucherBatch> voucherBatchDtoList = new ArrayList<BSSVoucherBatch>();
					for (BSSVoucherBatch voucherBatch : listVoucherBatch) {
						//VoucherBatchDto dto = new VoucherBatchDto(voucherBatch, mvnoId);
						if(voucherBatch.getReseller()==null) {
							voucherBatchDtoList.add(voucherBatch);
						}
					}
					return voucherBatchDtoList;
				} else {
					exp = exp.and(qVoucherBatch.mvnoId.eq(mvnoId).or(qVoucherBatch.mvnoId.eq(1L)));
					if(getBUIdsFromCurrentStaff().size()!=0){
						exp = exp.and(qVoucherBatch.buId.in(getBUIdsFromCurrentStaff()));
					}
					List<BSSVoucherBatch> listVoucherBatch = (List<BSSVoucherBatch>) voucherBatchRepository.findAll(exp);
					List<BSSVoucherBatch> voucherBatchDtoList = new ArrayList<BSSVoucherBatch>();
					for (BSSVoucherBatch voucherBatch : listVoucherBatch) {
						//VoucherBatchDto dto = new VoucherBatchDto(voucherBatch, mvnoId);
						if(voucherBatch.getReseller()==null) {
							voucherBatchDtoList.add(voucherBatch);
						}
					}
					return voucherBatchDtoList;
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<VoucherBatchDto> searchByDate(Long mvnoId, LocalDateTime createDate) {
		QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
		BooleanExpression boolExp = qVoucherBatch.isNotNull();
		boolExp = boolExp.and(qVoucherBatch.createDate.between(createDate, LocalDateTime.now()));
		if(mvnoId == null || mvnoId != 1)
			boolExp = boolExp.and(qVoucherBatch	.mvnoId.in(mvnoId, 1));
		if(getBUIdsFromCurrentStaff().size()!=0){
			boolExp = boolExp.and(qVoucherBatch.buId.in(getBUIdsFromCurrentStaff()));
		}
		List<BSSVoucherBatch> voucherBatchList = (List<BSSVoucherBatch>) voucherBatchRepository.findAll(boolExp);
		List<VoucherBatchDto> voucherBatchDtoList = new ArrayList<>();
		for(BSSVoucherBatch voucherBatch : voucherBatchList){
			VoucherBatchDto voucherBatchDto = new VoucherBatchDto(voucherBatch);
			voucherBatchDtoList.add(voucherBatchDto);
		}
		return voucherBatchDtoList;
	}

	@Override
	public void assignResellerToVoucherBatch(Long voucherBatchId, Long resellerId, Long mvnoId) {
		if (voucherBatchId == null || voucherBatchId == 0)
			throw new RuntimeException("Please enter valid voucher batch id.");
		BSSVoucherBatch voucherBatch = findVoucherBatchById(voucherBatchId, mvnoId);
		if(voucherBatch.getReseller() == null || voucherBatch.getReseller() != null && voucherBatch.getReseller().getResellerId() != resellerId) {
			validateReseller(resellerId, mvnoId, (voucherBatch.getPrice() * voucherBatch.getVoucherQuantity()), true);
			UpdateVoucherBatchDto updateVoucherBatchDto = new UpdateVoucherBatchDto(voucherBatch, mvnoId);
			updateVoucherBatchDto.setResellerId(resellerId);
			updateVoucherBatch(updateVoucherBatchDto, mvnoId);
		}
	}

	@Override
	public Integer countByPlanIdAndMvnoId(Long planId, Long mvnoId) {
		QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
		BooleanExpression boolExp = qVoucherBatch.isNotNull();
		boolExp = boolExp.and(qVoucherBatch.plan.id.eq(Math.toIntExact(planId)));
		if(mvnoId != 1)
			boolExp = boolExp.and(qVoucherBatch.mvnoId.in(mvnoId, 1));
		List<BSSVoucherBatch> voucherBatches = (List<BSSVoucherBatch>) voucherBatchRepository.findAll(boolExp);
		return voucherBatches.size();
	}
	@Override
	public Integer countByResellerIdAndMvnoId(Long resellerId, Long mvnoId) {
		QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
		BooleanExpression boolExp = qVoucherBatch.isNotNull();
		boolExp = boolExp.and(qVoucherBatch.reseller.resellerId.eq(resellerId));
		if(mvnoId != 1)
			boolExp = boolExp.and(qVoucherBatch.mvnoId.in(mvnoId, 1));
		List<BSSVoucherBatch> voucherBatches = (List<BSSVoucherBatch>) voucherBatchRepository.findAll(boolExp);
		return voucherBatches.size();
	}

	private void validateReseller(Long resellerId, Long mvnoId, Double price, Boolean isUpdate)
	{
		try
		{
			Reseller reseller = resellerService.findResellerById(resellerId, mvnoId, isUpdate);
			if((reseller.getBalance() != null &&  reseller.getCreditLimit() != null) && ((Long.valueOf(reseller.getBalance()) + Long.valueOf(reseller.getCreditLimit())) < price))
			{
				throw new RuntimeException("Unable to Assign batch, Reseller has insufficient credit limit."+APIConstants.INVALID_RESELLER_MSG);
			}
			else
			{
				Long remainingBalance = 0L;
				if(reseller.getBalance() != null)
				{
					if(Long.valueOf(reseller.getBalance()) > 0)
					{
						remainingBalance  = (long) (Long.valueOf(reseller.getBalance()) - price);
					}
					else if(Long.valueOf(reseller.getBalance()) == 0)
					{
						remainingBalance = 0L;
					}
				}

				if(remainingBalance < 0)
				{
					reseller.setBalance(0L);
					if(reseller.getCreditLimit() != null)
					{
						reseller.setCreditLimit(Long.valueOf(reseller.getCreditLimit()) + remainingBalance);
					}
				}
				else if(remainingBalance == 0)
				{
					if(reseller.getCreditLimit() != null)
					{
						reseller.setCreditLimit((long) (Long.valueOf(reseller.getCreditLimit()) - price));
					}
				}
				else
				{
					reseller.setBalance(remainingBalance);
				}
				resellerService.updateReseller(reseller, reseller.getMvnoId());
			}
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<BSSVoucherBatch> findAllVoucherBatchByPlanId(Long planId, Long mvnoId) {
		QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
		BooleanExpression boolExp = qVoucherBatch.isNotNull();

		boolExp = boolExp.and(qVoucherBatch.plan.id.eq(Math.toIntExact(planId)));
		if(mvnoId != 1)
			boolExp = boolExp.and(qVoucherBatch.mvnoId.in(mvnoId, 1));
		if(getBUIdsFromCurrentStaff().size()!=0){
			boolExp = boolExp.and(qVoucherBatch.buId.in(getBUIdsFromCurrentStaff()));
		}

		return  (List<BSSVoucherBatch>) voucherBatchRepository.findAll(boolExp);
	}
	@Override
	public void updateExpiryDate(Long voucherBatchId, String expiryDate, Long mvnoId, String lastModifiedBy) {

		if (voucherBatchId == null || voucherBatchId == 0)
			throw new RuntimeException("Please enter valid voucher batch id.");
		String DateFormat = null;
		BSSVoucherBatch voucherBatch = findVoucherBatchById(voucherBatchId, mvnoId);
		if(voucherBatch.getExpirydate()!=null && voucherBatch.getExpirydate().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Voucher batch is already expired");
		}
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		List<Voucher> voucherList = null;
		Boolean validVoucherPresent = false;
		QVoucher qVoucher = QVoucher.voucher;
		BooleanExpression boolExp = qVoucher.isNotNull();
		boolExp = boolExp.and(qVoucher.voucherBatch.voucherBatchId.eq(voucherBatchId));
		if (mvnoId != 1) boolExp = boolExp.and(qVoucher.mvnoId.in(mvnoId, 1));
		voucherList = (List<Voucher>) voucherRepository.findAll(boolExp);
		for(Voucher voucher: voucherList) {
			if (!voucher.getStatus().equals("SCRAPPED") || !voucher.equals("USED")) {
				validVoucherPresent =true;
			}
		}
        /*if(!expiryDate.equals(DateFormat))
        {
            throw new RuntimeException("ExpDate Date is mandatory. Please enter valid Expiry Date in formate (YYYY-MM-DD)");
        }*/

		voucherBatch.setExpirydate(LocalDateTime.parse(expiryDate , format));

		if(validVoucherPresent) {
			voucherBatch.setExpirydate(LocalDateTime.parse(expiryDate,format));
			voucherBatchRepository.save(voucherBatch);
		}else {
			throw new RuntimeException("voucher is used or scrapped OR no voucher Created with this batch");
		}
		UpdateVoucherBatchDto updateVoucherBatchDto = new UpdateVoucherBatchDto(voucherBatch, mvnoId);
		boolean overriteExpiry = false;

		log.info("VoucherBatch date updated successfully, for voucherBatch name :  "+voucherBatch.getBatchName()+" updated date : " +expiryDate);
	}

	@Override
	public boolean isPartnerBalanceInsufficient(VoucherBatchDto voucherBatchDto, Integer partnerId) {
		if(partnerId!=null)
		{
			Partner partner=partnerRepository.findById(partnerId).orElse(null);
			if(partner!=null && voucherBatchDto.getPlanId()!=null)
			{
				PostpaidPlan plan = planService.findById(voucherBatchDto.getPlanId());
				if (plan == null)
					throw new RuntimeException("Plan is mandatory. Please add correct plan");
				voucherBatchDto.setPrice(plan.getOfferprice());
				Double totalBatchPrice=voucherBatchDto.getPrice()*voucherBatchDto.getVoucherQuantity();
				if(partner.getBalance()>=totalBatchPrice)
					return false;
			}
		}
		return true;
	}


	@Override
	public void shareVoucherBatchData(VoucherBatchDto voucherBatchDto,Integer partnerId) {

		createDataSharedService.sendVoucherBatchForAllMicroService(voucherBatchDto,partnerId);
	}

	public List<java.lang.Long> getBUIdsFromCurrentStaff() {
		List<java.lang.Long> mvnoIds = new ArrayList<java.lang.Long>();
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


	public int getLoggedInUserPartnerId() {
		int partnerId = -1;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error("[VoucherBatchService]" + e.getStackTrace(), e);
			partnerId = -1;
		}
		return partnerId;
	}


	public LoggedInUser getLoggedInUser() {
		LoggedInUser loggedInUser = null;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error("[VoucherBatchService]" + e.getStackTrace(), e);
		}
		return loggedInUser;
	}

	public int getLoggedInUserId() {
		int loggedInUserId = -1;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error("[VoucherBatchService]" + e.getStackTrace(), e);
			loggedInUserId = -1;
		}
		return loggedInUserId;
	}


}
