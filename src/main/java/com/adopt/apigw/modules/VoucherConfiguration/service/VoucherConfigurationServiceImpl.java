package com.adopt.apigw.modules.VoucherConfiguration.service;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.model.postpaid.QPostpaidPlan;
import com.adopt.apigw.modules.LocationMaster.service.LocationMasterService;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Reseller.mapper.WifiUtils;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.Voucher.module.ValidateCrudTransactionData;
import com.adopt.apigw.modules.Voucher.repository.VoucherRepository;
import com.adopt.apigw.modules.VoucherBatch.domain.BSSVoucherBatch;
import com.adopt.apigw.modules.VoucherBatch.domain.QBSSVoucherBatch;
import com.adopt.apigw.modules.VoucherBatch.repository.BSSVoucherBatchRepository;
import com.adopt.apigw.modules.VoucherConfiguration.domain.QVoucherConfiguration;
import com.adopt.apigw.modules.VoucherConfiguration.domain.UpdateVoucherConfigDto;
import com.adopt.apigw.modules.VoucherConfiguration.domain.VoucherConfiguration;
import com.adopt.apigw.modules.VoucherConfiguration.module.VoucherConfigDto;
import com.adopt.apigw.modules.VoucherConfiguration.repository.VoucherConfigurationRepository;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class VoucherConfigurationServiceImpl implements VoucherConfigurationService {

//	private static Log log = LogFactory.getLog(VoucherConfigurationServiceImpl.class);

    private final Logger log = LoggerFactory.getLogger(VoucherConfigurationServiceImpl.class);

    @Autowired
    private VoucherConfigurationRepository voucherConfigurationRepository;

    @Autowired
    private PostpaidPlanRepo planRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private LocationMasterService locationMasterService;

    @Autowired
    private PostpaidPlanService planService;

    @Autowired
    private BSSVoucherBatchRepository voucherBatchRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private MvnoRepository mvnoRepository;
    
    Javers javers = JaversBuilder.javers().build();

    @Override
    public VoucherConfiguration save(VoucherConfigDto voucherConfigDto, Long mvnoId) {
        try {
//			log.info("Persisting Voucher Configuration for name " + voucherConfigDto.getName());
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                    throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
                }else if (getBUIdsFromCurrentStaff().size() == 1){
                    voucherConfigDto.setBuId(getBUIdsFromCurrentStaff().get(0));
                }
                VoucherConfiguration voucherConfigVo=new VoucherConfiguration();
                if(voucherConfigDto.getPlanId()!=null) {
                    PostpaidPlan plan = validatePlan(voucherConfigDto.getPlanId(), mvnoId);
                    voucherConfigVo = new VoucherConfiguration(voucherConfigDto, plan, mvnoId);
                }else{
                    voucherConfigVo = new VoucherConfiguration(voucherConfigDto, null, mvnoId);
                }
                voucherConfigVo.setVoucherAmount(voucherConfigDto.getVoucherAmount());
                validateVoucherData(voucherConfigVo, false, mvnoId);
                voucherConfigVo.setCreatedOn(LocalDateTime.now());
                voucherConfigVo.setCreatedBy("admin admin");
                voucherConfigVo.setCreatedByStaffId(planService.getLoggedInUser().getStaffId());
                voucherConfigVo.setMvnoName(mvnoRepository.findMvnoNameById(voucherConfigVo.getMvnoId()));
                return voucherConfigurationRepository.save(voucherConfigVo);
            }
            /*
             * boolean bVoucherExist =
             * voucherConfigurationRepository.findByName(voucherConfigDto.getName()).
             * isPresent(); if (bVoucherExist) { throw new
             * RuntimeException("Voucher exist with name " + voucherConfigDto.getName()); }
             */

            /*
             * return planRepository.findByPlanName(voucherConfigDto.getPlanName())
             * .map(plan -> new VoucherConfiguration(voucherConfigDto, plan))
             * .map(voucherConfiguration ->
             * voucherConfigurationRepository.save(voucherConfiguration)) .orElseThrow(() ->
             * new RuntimeException("Plan not found with name " +
             * voucherConfigDto.getPlanName()));
             */
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public VoucherConfiguration update(UpdateVoucherConfigDto voucherConfigDto, Long mvnoId) {
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_UPDATE);
        try {
//            log.info("Updating Voucher Configuration for Id " + voucherConfigDto.getId());
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                PostpaidPlan plan=null;
                if(Objects.nonNull(voucherConfigDto.getPlanId())) {
                     plan = validatePlan(voucherConfigDto.getPlanId(), mvnoId);
                }
                Optional<VoucherConfiguration> vConfiguration = voucherConfigurationRepository.findById(voucherConfigDto.getId());
                if (!vConfiguration.isPresent()) {
//                    log.error("Given voucher profile is not available: " + voucherConfigDto.getName());
                    throw new RuntimeException("Given voucher profile is not available");
                }
                VoucherConfiguration voucherConfigVo = new VoucherConfiguration(voucherConfigDto, plan, mvnoId);
                String updates = WifiUtils.getUpdatedDiff(vConfiguration.get(), voucherConfigVo);
                validateVoucherData(voucherConfigVo, true, mvnoId);
                voucherConfigVo.setLastModifiedBy("admin admin");
                voucherConfigVo.setLastModifiedOn(LocalDateTime.now());
                voucherConfigVo.setBuId(vConfiguration.get().getBuId());
                if(vConfiguration.isPresent()) {
                    voucherConfigVo.setCreatedByStaffId(vConfiguration.get().getCreatedByStaffId());
                    voucherConfigVo.setMvnoName(vConfiguration.get().getMvnoName());
                }

//                log.info("Voucher Configuration has been updated successfully, updated values: " + updates);

                return voucherConfigurationRepository.save(voucherConfigVo);
            }
        } catch (Throwable e) {
//            log.error("Error while update voucher: " + voucherConfigDto.getName() + " " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private PostpaidPlan validatePlan(Integer planId, Long mvnoId) {
        try {


            QPostpaidPlan qPlan = QPostpaidPlan.postpaidPlan;
            BooleanExpression boolExp = qPlan.isNotNull();
            boolExp = boolExp.and(qPlan.id.eq(planId));
            if(mvnoId != 1)
                boolExp = boolExp.and(qPlan.mvnoId.in(mvnoId, 1));
            Optional<PostpaidPlan> optionalPlan = planRepository.findOne(boolExp);
            if (!optionalPlan.isPresent()) {
                throw new RuntimeException("Plan not found with plan ID " + planId);
            } else {
                return optionalPlan.get();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateVoucherData(VoucherConfiguration voucherConfigVo, boolean isUpdate, Long mvnoId) {
        try {
//			String uniqueNameMsg = "Voucher configuration with name '" + voucherConfigVo.getName()
//					+ "' is already exist. Please enter unique voucher name.";

            if (isUpdate && !ValidateCrudTransactionData.validateLongTypeFieldValue(voucherConfigVo.getId())) {
                throw new RuntimeException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid voucher config id");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(voucherConfigVo.getName())) {
                throw new RuntimeException(APIConstants.BASIC_STRING_MSG + "Please enter valid voucher name");
            }
//			else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(voucherConfigVo.getPostValue())) {
//				throw new RuntimeException(APIConstants.BASIC_STRING_MSG + "Please enter valid post value");
//			} else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(voucherConfigVo.getPreValue())) {
//				throw new RuntimeException(APIConstants.BASIC_STRING_MSG + "Please enter valid pre value");
//			} 
            else if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(voucherConfigVo.getVoucherCodeLength())) {
                throw new RuntimeException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid length. It must be greater than or equal to 1");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(voucherConfigVo.getNoOfVoucher())) {
                throw new RuntimeException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid no. of voucher. It must be greater than or equal to 1");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(voucherConfigVo.getStatus()) || (!voucherConfigVo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) && !voucherConfigVo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
                throw new RuntimeException("Please enter valid status. It should be '" + CommonConstants.ACTIVE_STATUS + "' or '" + CommonConstants.INACTIVE_STATUS + "'");
            } else if (voucherConfigVo.getVoucherCodeFormat().isEmpty()) {
                throw new RuntimeException("Voucher type is mandatory. Please select voucher type.");
            }
//			else if (isUpdate) {
//				if (voucherConfigurationRepository
//						.findByNameToUpdateRecord(voucherConfigVo.getName(), voucherConfigVo.getId()).isPresent()) {
//					throw new RuntimeException(uniqueNameMsg);
//				}
//			}
//			else if (!isUpdate)
//			{
//				if (voucherConfigurationRepository.findByNameAndMvnoId(voucherConfigVo.getName(),mvnoId).isPresent())
//				{
//					throw new RuntimeException(uniqueNameMsg);
//				}
//			}
            checkForUniqueVoucherProfile(voucherConfigVo.getName(), mvnoId, voucherConfigVo.getId(), isUpdate);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void checkForUniqueVoucherProfile(String profileName, Long mvnoId, Long profileId, boolean isUpdate) {
        try {
            QVoucherConfiguration qVoucherConfig = QVoucherConfiguration.voucherConfiguration;
            BooleanExpression boolExp = qVoucherConfig.isNotNull();
            if (isUpdate) {
                boolExp = boolExp.and(qVoucherConfig.id.ne(profileId));
            }
            if (mvnoId == 1) {
                boolExp = boolExp.and(qVoucherConfig.name.eq(profileName));
                List<VoucherConfiguration> voucherConfigList = (List<VoucherConfiguration>) voucherConfigurationRepository.findAll(boolExp);
                if (!voucherConfigList.isEmpty()) {
                    throw new RuntimeException("Profile exist with the same name : '" + profileName + "'");
                }
            } else {
                boolExp = boolExp.and(qVoucherConfig.name.eq(profileName)).and((qVoucherConfig.mvnoId.eq(mvnoId)).or(qVoucherConfig.mvnoId.eq(1L)));
                if(getBUIdsFromCurrentStaff().size()!=0){
                    boolExp = boolExp.and(qVoucherConfig.buId.in(getBUIdsFromCurrentStaff()));
                }
                Optional<VoucherConfiguration> optionalVouchConfig = voucherConfigurationRepository.findOne(boolExp);
                if (optionalVouchConfig.isPresent()) {
                    throw new RuntimeException("Profile exist with the same name : '" + profileName + "'");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public VoucherConfiguration findById(Long id, Long mvnoId) {
        try {
//            log.info("getting voucher configuration for id " + id);
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                QVoucherConfiguration qVoucherConfiguration = QVoucherConfiguration.voucherConfiguration;
                BooleanExpression boolExp = qVoucherConfiguration.isNotNull();
                boolExp = boolExp.and(qVoucherConfiguration.id.eq(id));
                if (mvnoId != 1) boolExp = boolExp.and(qVoucherConfiguration.mvnoId.in(mvnoId, 1));
                if(getBUIdsFromCurrentStaff().size()!=0){
                    boolExp = boolExp.and(qVoucherConfiguration.buId.in(getBUIdsFromCurrentStaff()));
                }
                return voucherConfigurationRepository.findOne(boolExp).orElseThrow(() -> new EntityNotFoundException("Voucher Configuration not found for id " + id));
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public VoucherConfiguration getByID(Long id) {
        Optional<VoucherConfiguration> voucherConfigurationOptional = voucherConfigurationRepository.findById(id);
        if (voucherConfigurationOptional.isPresent())
            return voucherConfigurationRepository.findById(id).get();
        return null;
    }

    @Override
    public Page<VoucherConfiguration> findByName(String name, Long mvnoId, PaginationDTO paginationDTO) {
        try {
//            log.info("getting voucher configuration for name " + name);
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                QVoucherConfiguration qVoucherConfiguration = QVoucherConfiguration.voucherConfiguration;
                BooleanExpression boolExp = qVoucherConfiguration.isNotNull();
                if (name == null && paginationDTO.getFromDate() == null && paginationDTO.getToDate() == null && paginationDTO.getPage() == 0 && paginationDTO.getSize() == 0) {
                    Page<VoucherConfiguration> page = new PageImpl<VoucherConfiguration>(voucherConfigurationRepository.findAll());
                    return page;
                }
                if (!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null"))) {
                    boolExp = boolExp.and(qVoucherConfiguration.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00")).or(qVoucherConfiguration.lastModifiedOn.after((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
                }
                if (!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null"))) {
                    boolExp = boolExp.and(qVoucherConfiguration.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59")).or(qVoucherConfiguration.lastModifiedOn.before((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
                }
                if (mvnoId != 1) boolExp = boolExp.or(qVoucherConfiguration.mvnoId.in(mvnoId, 1));

                if(getBUIdsFromCurrentStaff().size()!=0){
                    boolExp = boolExp.and(qVoucherConfiguration.buId.in(getBUIdsFromCurrentStaff()));
                }
                if (StringUtils.isBlank(name) || name.equalsIgnoreCase("null")) {
                    name = "";
                } else {
                    boolExp = boolExp.and(qVoucherConfiguration.name.contains(name));
                }
                Predicate builder = boolExp;
                if (paginationDTO.getSize() < 1) {
                    Page<VoucherConfiguration> page = new PageImpl<VoucherConfiguration>((List<VoucherConfiguration>) voucherConfigurationRepository.findAll(builder));
                    return page;
                }
                if (paginationDTO.getPage() > 0) {
                    paginationDTO.setPage(paginationDTO.getPage() - 1);
                }
                Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastModifiedOn"));
                return voucherConfigurationRepository.findAll(builder, pageable);
//				return voucherConfigurationRepository.findByNameAndMvnoId(name,mvnoId)
//						.orElseThrow(() -> new EntityNotFoundException("Voucher Configuration not found for name " + name));
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PageableResponse<VoucherConfiguration> getAll(Long mvnoId, String name, Long locationId, PaginationDTO paginationDTO) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            }
            QVoucherConfiguration qVoucherConfiguration = QVoucherConfiguration.voucherConfiguration;
            BooleanExpression boolExp = qVoucherConfiguration.isNotNull();
            PageableResponse<VoucherConfiguration> pageableResponse = new PageableResponse<>();

            List<Long> planIdList = new ArrayList<>();


//            Set<Plan> plans = new HashSet<>(planService.getPlans(mvnoId, locationId));
//            boolExp = boolExp.and(qVoucherConfiguration.mvnoId.in(mvnoId, 1));
//            for (Plan plan : plans) {
//                boolExp = boolExp.or(qVoucherConfiguration.plan.planId.eq(plan.getPlanId()));
//            }

            if(mvnoId!=1){
                boolExp = boolExp.and(qVoucherConfiguration.mvnoId.in(mvnoId, 1));
            }
            if(getBUIdsFromCurrentStaff().size()!=0){
                boolExp = boolExp.and(qVoucherConfiguration.buId.in(getBUIdsFromCurrentStaff()));
            }
            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }

            if(getLoggedInUserPartnerId()!=1)
            {
                boolExp = boolExp.and(qVoucherConfiguration.createdByStaffId.eq(getLoggedInUser().getStaffId()));
            }

            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastModifiedOn"));
            if (!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null"))) {
                boolExp = boolExp.and(qVoucherConfiguration.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00")).or(qVoucherConfiguration.lastModifiedOn.after((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
            }
            if (!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null"))) {
                boolExp = boolExp.and(qVoucherConfiguration.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59")).or(qVoucherConfiguration.lastModifiedOn.before((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
            }
            if (!StringUtils.isBlank(name))
                boolExp = boolExp.and(qVoucherConfiguration.name.like("%" + name + "%"));
            Page<VoucherConfiguration> page = voucherConfigurationRepository.findAll(boolExp, pageable);
           //log.debug("Request to fetch voucher profile with the name :"+name);
            return pageableResponse.convert(new PageImpl<>(page.getContent(), pageable, page.getTotalElements()));

//			Set<Plan> plans = new HashSet<>(planService.getPlans(mvnoId, locationId));
//			Set<VoucherConfiguration> voucherConfigurations = new HashSet<>();
//			for(Plan plan : plans){
//				voucherConfigurations.addAll(findVoucherConfigurations(plan.getPlanId(), mvnoId));
//			}
//			return new ArrayList<>(voucherConfigurations);
        } catch (Throwable e) {
//            log.error("Error while fetching records  with name :" +name);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id, Long mvnoId) {
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_DELETE);
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
                QVoucherConfiguration qVoucherConfiguration = QVoucherConfiguration.voucherConfiguration;
                BooleanExpression booleanExpressionForVoucherConfig = qVoucherConfiguration.isNotNull().and(qVoucherConfiguration.id.eq(id));
                if (mvnoId != 1) {
                    booleanExpressionForVoucherConfig = booleanExpressionForVoucherConfig.and(qVoucherConfiguration.mvnoId.eq(mvnoId));
                }
                BooleanExpression exp = qVoucherBatch.isNotNull();
                exp = exp.and(qVoucherBatch.voucherConfiguration.id.eq(id));
                List<BSSVoucherBatch> voucherBatches = (List<BSSVoucherBatch>) voucherBatchRepository.findAll(exp);
                if (!voucherBatches.isEmpty()) {
                    throw new IllegalArgumentException("This operation will not allow as there are some voucher batches available related to this profile .");
                }

                if (!voucherConfigurationRepository.findOne(booleanExpressionForVoucherConfig).isPresent()) {
                    throw new IllegalArgumentException("No record foud for voucher profile to delete");
                } else {
                    voucherConfigurationRepository.deleteById(id);
//                    log.info("Voucher Configuration has been deleted successfully by id:  " + id);
                }
            }
//			List<Voucher> voucherVo = voucherRepository.findAll();
//			for(int i=0;i<voucherVo.size();i++)
//			{
//				Voucher voucher = voucherVo.get(i);
//				if(voucher.getConfiguration().getId().equals(id))
//				{
//					throw new IllegalArgumentException("This operation will not allow as this Voucher Ptofile is used for Voucher.");
//				}
//			}
        }catch (IllegalArgumentException ie){
            throw new IllegalArgumentException();
        }catch (Throwable e) {
//            log.error("Error while delete voucher by id: " + id + " " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String updateVoucherConfigStatus(Long id, String status, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                QVoucherConfiguration qVoucherConfiguration = QVoucherConfiguration.voucherConfiguration;
                BooleanExpression booleanExpressionForVoucherConfig = qVoucherConfiguration.isNotNull().and(qVoucherConfiguration.id.eq(id));
                if (mvnoId != 1) {
                    booleanExpressionForVoucherConfig = booleanExpressionForVoucherConfig.and(qVoucherConfiguration.mvnoId.eq(mvnoId));
                }
                VoucherConfiguration optionalVoucherConfig = voucherConfigurationRepository.findOne(booleanExpressionForVoucherConfig).orElse(null);
                if (Objects.nonNull(optionalVoucherConfig)) {
                    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(status)) {
                        throw new RuntimeException(APIConstants.BASIC_STRING_MSG + "Voucher congifuration status is mandatory. Please enter valid status.");
                    } else if (!status.equals(CommonConstants.ACTIVE_STATUS) && !status.equals(CommonConstants.INACTIVE_STATUS)) {
                        throw new IllegalArgumentException("Please enter valid voucher congifuration status. It should be '" + CommonConstants.ACTIVE_STATUS + "' or '" + CommonConstants.INACTIVE_STATUS + "'");
                    }
                    optionalVoucherConfig.setStatus(status);
                    voucherConfigurationRepository.save(optionalVoucherConfig);
                    String msg = "";
                    if (status.equals(CommonConstants.ACTIVE_STATUS)) {
                        msg = "Voucher '" + optionalVoucherConfig.getName() + "' has been activated successfully.";
                    } else {
                        msg = "Voucher '" + optionalVoucherConfig.getName() + "' has been inactivated successfully.";
                    }
                    return msg;
                } else {
                    throw new IllegalArgumentException("No record found with id : '" + id + "'");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<VoucherConfiguration> findVoucher(String voucherName, String voucherCodeFormat, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                QVoucherConfiguration qVoucherConfiguration = QVoucherConfiguration.voucherConfiguration;
                BooleanExpression boolExp = qVoucherConfiguration.isNotNull();
                if ((!ValidateCrudTransactionData.validateStringTypeFieldValue(voucherName) || voucherName.equalsIgnoreCase("null")) && (!ValidateCrudTransactionData.validateStringTypeFieldValue(voucherCodeFormat) || voucherCodeFormat.equalsIgnoreCase("null"))) {
                    if (mvnoId == 1) {
                        return voucherConfigurationRepository.findAll();
                    } else {
                        boolExp = boolExp.and(qVoucherConfiguration.mvnoId.eq(mvnoId)).or(qVoucherConfiguration.mvnoId.eq(1L));
                        return (List<VoucherConfiguration>) voucherConfigurationRepository.findAll(boolExp);
                    }
                } else {
                    if (mvnoId == 1) {
                        if (ValidateCrudTransactionData.validateStringTypeFieldValue(voucherName) && voucherName != "null") {
                            boolExp = boolExp.and(qVoucherConfiguration.name.contains(voucherName));
                        }

//						if(ValidateCrudTransactionData.validateStringTypeFieldValue(voucherCodeFormat) && voucherCodeFormat != "null")
//						{
//							boolExp = boolExp.and(qVoucherConfiguration.voucherCodeFormat.contains(FieldType.UPPER_CASE));
//						}
                        return (List<VoucherConfiguration>) voucherConfigurationRepository.findAll(boolExp);
                    } else {
                        if (ValidateCrudTransactionData.validateStringTypeFieldValue(voucherName) && voucherName != "null") {
                            boolExp = boolExp.and(qVoucherConfiguration.name.contains(voucherName));
                        }
//						if(ValidateCrudTransactionData.validateStringTypeFieldValue(voucherCodeFormat) && voucherCodeFormat != "null")
//						{
//							boolExp = boolExp.and(qVoucherConfiguration.voucherCodeFormat.contains(FieldType.valueOf(voucherCodeFormat)));
//						}
                        boolExp = boolExp.and(qVoucherConfiguration.mvnoId.eq(mvnoId).or(qVoucherConfiguration.mvnoId.eq(1L)));
                        if(getBUIdsFromCurrentStaff().size()!=0){
                            boolExp = boolExp.and(qVoucherConfiguration.buId.in(getBUIdsFromCurrentStaff()));
                        }
                        return (List<VoucherConfiguration>) voucherConfigurationRepository.findAll(boolExp);
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<VoucherConfiguration> findVoucherConfigurations(Long planId, Long mvnoId) {
        QVoucherConfiguration qVoucherConfiguration = QVoucherConfiguration.voucherConfiguration;
        BooleanExpression boolExp = qVoucherConfiguration.isNotNull();

        boolExp = boolExp.and(qVoucherConfiguration.plan.id.eq(planId.intValue()));
        if (mvnoId != 1) boolExp = boolExp.and(qVoucherConfiguration.mvnoId.in(mvnoId, 1));
        if(getBUIdsFromCurrentStaff().size()!=0){
            boolExp = boolExp.and(qVoucherConfiguration.buId.in(getBUIdsFromCurrentStaff()));
        }

        return (List<VoucherConfiguration>) voucherConfigurationRepository.findAll(boolExp);
    }

    @Override
    public Integer countByPlanIdAndMvnoId(Long planId, Long mvnoId) {
        QVoucherConfiguration qVoucherConfiguration = QVoucherConfiguration.voucherConfiguration;
        BooleanExpression boolExp = qVoucherConfiguration.isNotNull();
        boolExp = boolExp.and(qVoucherConfiguration.plan.id.eq(planId.intValue()));
        if(mvnoId != 1)
            boolExp = boolExp.and(qVoucherConfiguration.mvnoId.in(mvnoId, 1));
        if(getBUIdsFromCurrentStaff().size()!=0){
            boolExp = boolExp.and(qVoucherConfiguration.buId.in(getBUIdsFromCurrentStaff()));
        }
        List<VoucherConfiguration> voucherConfigurations = (List<VoucherConfiguration>) voucherConfigurationRepository.findAll(boolExp);
        return voucherConfigurations.size();
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
            ApplicationLogger.logger.error("" + e.getStackTrace(), e);
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
            ApplicationLogger.logger.error("" + e.getStackTrace(), e);
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
            ApplicationLogger.logger.error("" + e.getStackTrace(), e);
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }
}
