package com.adopt.apigw.modules.Reseller.service;

import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import com.adopt.apigw.modules.LocationMaster.repository.LocationMasterRepository;
import com.adopt.apigw.modules.LocationMaster.service.LocationMasterService;
import com.adopt.apigw.modules.Reseller.domain.QReseller;
import com.adopt.apigw.modules.Reseller.domain.Reseller;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Reseller.mapper.PasswordGenerator;
import com.adopt.apigw.modules.Reseller.mapper.WifiUtils;
import com.adopt.apigw.modules.Reseller.module.ResellerChangePasswordDto;
import com.adopt.apigw.modules.Reseller.module.ResellerDto;
import com.adopt.apigw.modules.Reseller.repository.ResellerRepository;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.Voucher.module.ValidateCrudTransactionData;
import com.adopt.apigw.modules.VoucherBatch.service.VoucherBatchService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class ResellerServiceImpl extends PasswordGenerator implements ResellerService {
    private final Logger log = LoggerFactory.getLogger(ResellerServiceImpl.class);
    private static final String PERCENTAGE = "Percentage";
    private static final String FLAT = "Flat";
    private static final String PREPAID = "Prepaid";
    private static final String COMMISSION = "Commission";
    private static final String BAD_CREDENTIALS = "Bad Credentials";


    @Autowired
    private ResellerRepository resellerRepository;

    @Autowired
    private VoucherBatchService voucherBatchService;

    @Autowired
    WifiUtils wifiUtils;

    @Autowired
    private LocationMasterService locationMasterService;

    @Autowired
    private LocationMasterRepository locationMasterRepository;


//    @Value("" +
//            "${groovy.file.path}")
//    private String groovyFilPath;

    public Page<Reseller> getResellerPagebleList(Integer pageNumber, int customPageSize, String sortBy, Integer sortOrder, String searchText, Long mvnoId) {
        PageRequest pageRequest = null;
        if (sortOrder.equals(CommonConstants.SORT_ORDER_ASC))
            pageRequest = PageRequest.of(pageNumber - 1, customPageSize, Sort.by(sortBy));
        else pageRequest = PageRequest.of(pageNumber - 1, customPageSize, Sort.by(sortBy).descending());

        QReseller qReseller = QReseller.reseller;
        BooleanExpression exp = qReseller.isNotNull();
        if (mvnoId == null || mvnoId != 1) exp = exp.and(qReseller.mvnoId.in(mvnoId, 1));

        if (!"".equals(searchText) && searchText != null) {
            exp = exp.and(qReseller.resellerName.startsWithIgnoreCase(searchText).or(qReseller.address.startsWithIgnoreCase(searchText)).or(qReseller.phone.startsWithIgnoreCase(searchText)).or(qReseller.email.startsWithIgnoreCase(searchText)).or(qReseller.gstNo.startsWithIgnoreCase(searchText)).or(qReseller.commissionType.startsWithIgnoreCase(searchText)).or(qReseller.resellerType.startsWithIgnoreCase(searchText)));
        }
        Predicate builder = exp;
        return (Page<Reseller>) resellerRepository.findAll(builder, pageRequest);
    }

    @Override
    public Reseller findResellerById(Long id, Long mvnoId, Boolean isUpdate) {
        try {
            return findById(id, mvnoId, isUpdate);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Reseller findById(Long id, Long mvnoId, Boolean isUpdate) {
        try {
            if (Objects.isNull(mvnoId) || mvnoId == 0) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else if (Objects.isNull(id)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid Reseller id.");
            }
            QReseller qReseller = QReseller.reseller;
            BooleanExpression boolExp = qReseller.isNotNull();
            if (mvnoId != 1) {
                if (isUpdate) boolExp = boolExp.and(qReseller.mvnoId.eq(mvnoId));
                else boolExp = boolExp.and(qReseller.mvnoId.in(mvnoId, 1));
            }
            boolExp = boolExp.and(qReseller.resellerId.eq(id));
            Optional<Reseller> optionalReseller = resellerRepository.findOne(boolExp);
            if (optionalReseller.isPresent()) {
                return optionalReseller.get();
            } else {
                if (isUpdate)
                    throw new RuntimeException("You do not have access to update/delete record with given reseller id :' " + id + "'");
                else
                    throw new RuntimeException("No record found for reseller with the given reseller id :'" + id + "', Please enter valid reseller id.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Reseller> findAllResellers(Long mvnoId, Long locationId) {
        try {
            QReseller qReseller = QReseller.reseller;
            BooleanExpression boolExp = qReseller.isNotNull();

            if (mvnoId == 1) {
                if (locationId != null)
                    boolExp = boolExp.and(qReseller.locationMaster.locationMasterId.eq(locationId).or(qReseller.locationMaster.locationMasterId.isNull()));
            } else {
                if (locationId == null) boolExp = boolExp.and(qReseller.mvnoId.in(mvnoId, 1));
                else
                    boolExp = boolExp.and(qReseller.locationMaster.locationMasterId.eq(locationId).or(qReseller.locationMaster.locationMasterId.isNull()).and(qReseller.mvnoId.in(mvnoId, 1)));
            }
            return (List<Reseller>) resellerRepository.findAll(boolExp);
        } catch (Throwable e) {
            log.error("Error while fetching reseller's " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Reseller> searchResellers(String name, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + " Please enter valid mvno id.");
            } else {
                QReseller qReseller = QReseller.reseller;
                BooleanExpression boolExp = qReseller.isNotNull();
                if (StringUtils.isBlank(name) || name.equalsIgnoreCase("null")) {
                    if (mvnoId == 1) {
                        return resellerRepository.findAll();
                    } else {
                        boolExp = boolExp.and(qReseller.mvnoId.eq(mvnoId)).or(qReseller.mvnoId.eq(1L));
                        return (List<Reseller>) resellerRepository.findAll(boolExp);
                    }
                } else {
                    if (mvnoId == 1) {
                        boolExp = boolExp.and(qReseller.resellerName.contains(name));
                        return (List<Reseller>) resellerRepository.findAll(boolExp);
                    } else {
                        boolExp = boolExp.and(qReseller.resellerName.contains(name)).and(qReseller.mvnoId.in(mvnoId, 1));
                        return (List<Reseller>) resellerRepository.findAll(boolExp);
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteResellerById(Long id, Long mvnoId) {
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_DELETE);
        try {
            Reseller reseller = findById(id, mvnoId, true);
            if (voucherBatchService.countByResellerIdAndMvnoId(id, mvnoId) > 0)
                throw new RuntimeException("This operation will not allow as this reseller is used for VoucherBatch .");
            resellerRepository.delete(reseller);
            log.info("Reseller has been deleted successfully: " + reseller.getResellerName() + " by " + MDC.get("username"));
        } catch (Throwable e) {
            log.error("Error while delete reseller by id: " + id);
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }



    @Transactional
    @Override
    public Reseller saveReseller(ResellerDto resellerDto, Long mvnoId) {
        LocationMaster locationMaster = new LocationMaster();
        if (Objects.nonNull(mvnoId)) {
            try {
                locationMaster = locationMasterService.findlocationMasterById(resellerDto.getLocationMaster().getLocationMasterId(), resellerDto.getMvnoId());
                Reseller reseller = new Reseller(resellerDto, locationMaster);
                String encryptPassword = encryptPassword(resellerDto.getPassword());
                reseller.setPassword(encryptPassword);
                reseller.setMvnoId(resellerDto.getMvnoId());
                validateResellerDetail(reseller, false);
//			adjustSomeFieldsValueBasedOnResellerType(reseller);
                reseller.setCreatedOn(LocalDateTime.now());
                reseller.setCreatedBy("admin admin");
                return resellerRepository.save(reseller);
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Please enter MVNOId");
        }
    }
//    @Transactional
//    @Override
//    public void saveManageBalance(Reseller manageBalance, String remark, Long mvnoId) {
//        LocationMaster locationMaster = new LocationMaster();
//        if (Objects.nonNull(mvnoId)) {
//            try {
//            	if (!ValidateCrudTransactionData.validateLongTypeFieldValue(manageBalance.getCreditLimit())) {
//                    throw new RuntimeException("Please enter valid credit limit");
//                } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(manageBalance.getBalance())) {
//                    throw new RuntimeException("Please enter valid balance");
//                }else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(remark)) {
//                    throw new RuntimeException("Please enter valid Remark");
//                }
//            	Long oldBalance;
//                Long newCreditLimit = manageBalance.getCreditLimit();;
//                Optional<Reseller> reseller = resellerRepository.findById(manageBalance.getResellerId());
//                if(reseller.isPresent()) {
//                	Reseller resellerVo = reseller.get();
//                	oldBalance = resellerVo.getBalance();
//                	Long creditLimit = resellerVo.getCreditLimit();
//                	if(newCreditLimit>0) {
//                		newCreditLimit = newCreditLimit + creditLimit;
//                	}else {
//                		newCreditLimit = creditLimit + newCreditLimit;
//                	}
//                	Long newCreditLimit2 = newCreditLimit.longValue();
//                	resellerVo.setBalance(manageBalance.getBalance() + oldBalance);
//                	resellerVo.setCreditLimit(newCreditLimit2);
//                	Reseller saveReseller = resellerRepository.save(resellerVo);
//
//                	ManageBalance manageBalanceVo = new ManageBalance();
//                     manageBalanceVo.setOldBalance(oldBalance);
//                     manageBalanceVo.setNewBalance(manageBalance.getBalance() + oldBalance);
//                     manageBalanceVo.setRemarks(remark);
//                     manageBalanceVo.setUpdatedDate(new Timestamp(new Date().getTime()));
//                     manageBalanceVo.setUpdatedBy("admin admin");
//                     manageBalanceVo.setMvnoId(mvnoId);
//                     manageBalanceVo.setResellerId(saveReseller.getResellerId());
//                     //ManageBalance saveManageBalance =
//                    		 manageBalanceRepository.save(manageBalanceVo);
//                    // return manageB;
//                }
//            } catch (Throwable e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        } else {
//            throw new IllegalArgumentException("Please enter MVNOId");
//        }
//    }
//
//    @Transactional
//    @Override
//    public void saveAddBalance(AddBalance manageBalance, Long mvnoId) {
//        LocationMaster locationMaster = new LocationMaster();
//        if (Objects.nonNull(mvnoId)) {
//            try {
//            	Long oldBalance;
//                //Double newCreditLimit = Double.parseDouble(manageBalance.getCreditLimit().toString());;
//                Optional<Reseller> reseller = resellerRepository.findById(manageBalance.getResellerId());
//                if(reseller.isPresent()) {
//                	Reseller resellerVo = reseller.get();
//                	oldBalance = resellerVo.getBalance();
//                	resellerVo.setBalance(manageBalance.getNewBalance() + oldBalance);
////                	resellerVo.setCreditLimit(String.valueOf(newCreditLimit2));
//                	Reseller saveReseller = resellerRepository.save(resellerVo);
//
//                	AddBalance manageBalanceVo = new AddBalance();
//                     manageBalanceVo.setOldBalance(oldBalance);
//                     manageBalanceVo.setNewBalance(manageBalance.getNewBalance() + oldBalance);
//                     manageBalanceVo.setPaymentType(manageBalance.getPaymentType());
//                     if((manageBalance.getPaymentType()).equals("Cash"))
//                    		 manageBalanceVo.setComment(manageBalance.getComment());
//                     if((manageBalance.getPaymentType()).equals("Cheque")) {
//                    	 manageBalanceVo.setChequeNumber(manageBalance.getChequeNumber());
//                    	 manageBalanceVo.setBankName(manageBalance.getBankName());
//                     }
//                     if((manageBalance.getPaymentType()).equals("Online"))
//                    	 	manageBalanceVo.setTransactionId(manageBalance.getTransactionId());
//                    // manageBalanceVo.setRemarks(remark);
//                     manageBalanceVo.setUpdatedDate(new Timestamp(new Date().getTime()));
//                     manageBalanceVo.setUpdatedBy("admin admin");
//                     manageBalanceVo.setMvnoId(mvnoId);
//                     manageBalanceVo.setResellerId(saveReseller.getResellerId());
//                     //ManageBalance saveManageBalance =
//                     addBalanceRepository.save(manageBalanceVo);
//                    // return manageB;
//                }
//            } catch (Throwable e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        } else {
//            throw new IllegalArgumentException("Please enter MVNOId");
//        }
//    }
    @Transactional
    @Override
    public Reseller updateReseller(Reseller reseller, Long mvnoId) {
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_UPDATE);
        try {
            reseller.setMvnoId(mvnoId);
            validateResellerDetail(reseller, true);
            Reseller resellerEntity = findById(reseller.getResellerId(), mvnoId, true);
            String updated = WifiUtils.getUpdatedDiff(resellerEntity, reseller);
//			adjustSomeFieldsValueBasedOnResellerType(reseller);
            reseller.setLastModifiedBy("admin admin");
            reseller.setLastModifiedOn(LocalDateTime.now());
            reseller.setMvnoId(resellerEntity.getMvnoId());
            reseller.setPassword(resellerEntity.getPassword());
            Reseller update = resellerRepository.save(reseller);
            log.info("Reseller has been updated successfully by " + MDC.get("username") + " the difference is " + updated);
            return update;
        } catch (Throwable e) {
            log.error("Error while update reseller" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

//	private void adjustSomeFieldsValueBasedOnResellerType(Reseller reseller) {
//		try {
//			if (reseller.getResellerType().equals(PREPAID)) {
//				reseller.setCommissionType(null);
//				reseller.setCommissionValue(null);
//			} else {
//				reseller.setCreditLimit(null);
//				reseller.setBalance(null);
//			}
//		} catch (Throwable e) {
//			throw new RuntimeException(e.getMessage());
//		}
//	}

    private void validateResellerDetail(Reseller reseller, boolean isUpdate) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getResellerName())) {
                throw new RuntimeException("Please enter valid reseller name");
            }
//			else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getAddress())) {
//				throw new RuntimeException("Please enter valid reseller address");
//			}
            else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getPhone())) {
                throw new RuntimeException("Please enter valid reseller phone");
            }
//			else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getGstNo())) {
//				throw new RuntimeException("Please enter valid gst number");
//			}
            else if (reseller.getLocationMaster() != null && !ValidateCrudTransactionData.validateLongTypeFieldValue(reseller.getLocationMaster().getLocationMasterId())) {
                throw new RuntimeException("Please enter valid location");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getUsername())) {
                throw new RuntimeException("Please enter valid username");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getPassword())) {
                throw new RuntimeException("Please enter valid password");
            }
            else if (reseller.getCreditLimit() == null)
            {
                throw new RuntimeException("Please enter valid credit limit");
            }
            else if (reseller.getBalance() == null) 
            {
                throw new RuntimeException("Please enter valid balance");
            }
            else if (isUpdate) 
            {
                if (reseller.getResellerId() == null || reseller.getResellerId() == 0)
                    throw new RuntimeException("Please enter valid reseller id");
                checkDuplicateEntity(reseller, reseller.getMvnoId(), true);
            }
            if(reseller.getCreditLimit() < 0)
            {
            	throw new RuntimeException("Please enter valid numeric value for credit limit");
            }
            if(reseller.getBalance() < 0)
            {
            	throw new RuntimeException("Please enter valid numeric value for balance");
            }
            if (!isUpdate) checkDuplicateEntity(reseller, reseller.getMvnoId(), false);
//			validateResellerType(reseller);
//			validateDistributerId(reseller);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //	private void validateDistributerId(Reseller reseller) {
//		try {
//			if (reseller.getDistributerId() == 0) {
//				throw new RuntimeException("Please enter valid distributer id.");
//			} else if (reseller.getDistributerId() != 0) {
//				Optional<Distributer> optionalDistributer = distributerRepository.findById(reseller.getDistributerId());
//				if (!optionalDistributer.isPresent()) {
//					throw new RuntimeException("No record found with distributer id : '" + reseller.getDistributerId()
//							+ "', Please enter valid distributer id.");
//				}
//			}
//		} catch (Throwable e) {
//			throw new RuntimeException(e.getMessage());
//		}
//	}
//
//	private void validateResellerType(Reseller reseller) {
//		try {
//			if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getResellerType())
//					|| (!reseller.getResellerType().equals(PREPAID)
//							&& !reseller.getResellerType().equals(COMMISSION))) {
//				throw new RuntimeException("Please enter valid reseller type. Reseller type should be '" + PREPAID
//						+ "' or '" + COMMISSION + "'");
//			} else if (reseller.getResellerType().equalsIgnoreCase(PREPAID)) {
//				if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getCreditLimit())) {
//					throw new RuntimeException("Please enter valid credit limit");
//				} else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getBalance())) {
//					throw new RuntimeException("Please enter valid balance value");
//				} else if (reseller.getResellerType().equalsIgnoreCase(PREPAID)
//						&& (!reseller.getCommissionType().equalsIgnoreCase(APIConstants.BLANK_STRING)
//								&& reseller.getCommissionType() != null && !reseller.getCommissionType().isEmpty())
//						|| (!reseller.getCommissionValue().equalsIgnoreCase(APIConstants.BLANK_STRING)
//								&& reseller.getCommissionValue() != null && !reseller.getCommissionValue().isEmpty())) {
//					throw new RuntimeException("You have selected reseller type '" + PREPAID
//							+ "', So you don't need to enter commission value & commission type fields value.");
//				}
//			} else {
//				if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getCommissionType())
//						|| (!reseller.getCommissionType().equalsIgnoreCase(FLAT)
//								&& !reseller.getCommissionType().equalsIgnoreCase(PERCENTAGE))) {
//					throw new RuntimeException("Please enter valid commission type. Commission type should be '" + FLAT
//							+ "' or '" + PERCENTAGE + "'");
//				} else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reseller.getCommissionValue())) {
//					throw new RuntimeException("Please enter valid commission value");
//				} else if (reseller.getResellerType().equalsIgnoreCase(COMMISSION)
//						&& (!reseller.getCreditLimit().equalsIgnoreCase(APIConstants.BLANK_STRING)
//								&& reseller.getCreditLimit() != null && !reseller.getCreditLimit().isEmpty())
//						|| (!reseller.getBalance().equalsIgnoreCase(APIConstants.BLANK_STRING)
//								&& reseller.getBalance() != null && !reseller.getBalance().isEmpty())) {
//					throw new RuntimeException("You have selected reseller type '" + COMMISSION
//							+ "', So you don't need to enter credit limit & balance fields value.");
//				}
//			}
//		} catch (Throwable e) {
//			throw new RuntimeException(e.getMessage());
//		}
//	}
    private boolean validateNumericValue(String value, String fieldName) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Please enter valid numeric value for : " + fieldName);
        }
    }

    private void checkDuplicateEntity(Reseller reseller, Long mvnoId, Boolean isUpdate) {
        try {

            QReseller qReseller = QReseller.reseller;
            BooleanExpression boolExp = qReseller.isNotNull();
            String msg = "Reseller with name '" + reseller.getResellerName() + "' is already exist. Please enter unique reseller name.";

            if (isUpdate) {
                boolExp = boolExp.and(qReseller.resellerId.ne(reseller.getResellerId()));
            }

            if (mvnoId == 1) {
                boolExp = boolExp.and(qReseller.resellerName.eq(reseller.getResellerName()));
                List<Reseller> resellerList = (List<Reseller>) resellerRepository.findAll(boolExp);
                if (!resellerList.isEmpty()) {
                    throw new IllegalArgumentException(msg);
                }
            } else {
                boolExp = boolExp.and(qReseller.resellerName.eq(reseller.getResellerName())).and((qReseller.mvnoId.in(mvnoId, 1)));
                Optional<Reseller> optionalCoaDMProfile = resellerRepository.findOne(boolExp);
                if (optionalCoaDMProfile.isPresent()) {
                    throw new IllegalArgumentException(msg);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String encryptPassword(String password) {
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.encode(password);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    @Override
//    public Reseller validateLoginUser(LoginDto loginDto, Long mvnoId) {
//        try {
//            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(loginDto.getUserName())) {
//                throw new IllegalArgumentException(APIConstants.BASIC_STRING_MSG + "User name is mandatory. Please enter valid user name.");
//            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(loginDto.getPassword())) {
//                throw new IllegalArgumentException(APIConstants.BASIC_STRING_MSG + "Password is mandatory. Please enter valid password");
//            } else {
//                Optional<Reseller> optionalReseller = resellerRepository.findByUsernameAndMvnoId(loginDto.getUserName(), mvnoId);
//                if (!optionalReseller.isPresent()) {
//                    //throw new IllegalArgumentException(APIConstants.NOT_FOUND + ", No record found with user : '" + loginDto.getUserName() + "'. Please enter valid user name.");
//                    throw new IllegalArgumentException(BAD_CREDENTIALS);
//                }
//                if (!isPasswordMatched(loginDto.getPassword(), optionalReseller.get().getPassword())) {
//                    throw new IllegalArgumentException("Please enter valid reseller Password.");
//                }
//                return optionalReseller.get();
//            }
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

//    @Override
//    public Reseller validateLoginUser(LoginDto loginDto, Long mvnoId, String cid, String mac) {
//        try {
//            File tempFile = new File(groovyFilPath + "/wificustomerlogin.groovy");
//            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(loginDto.getUserName())) {
//                throw new IllegalArgumentException(APIConstants.BASIC_STRING_MSG + "User name is mandatory. Please enter valid user name.");
//            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(loginDto.getPassword())) {
//                throw new IllegalArgumentException(APIConstants.BASIC_STRING_MSG + "Password is mandatory. Please enter valid password");
//            } else {
//                if (tempFile.exists()) {
//                    JSONObject json = wifiUtils.runWithGroovyClassLoader("preprocess", groovyFilPath + "/wificustomerlogin.groovy", loginDto.getUserName(), loginDto.getPassword(), null, mvnoId, cid, mac);
//                    if (json.has("status")) {
//                        if (json.get("status").toString().equals("1")) {
//                            wifiUtils.saveCustomer(json, loginDto.getUserName(), loginDto.getPassword(), "", mvnoId, mac);
//                        } else {
//                            throw new IllegalArgumentException("Invalid login credentials.");
//                        }
//                    } else if (json.has("error")) {
//                        JSONArray errorArray = json.getJSONArray("data");
//                        String message = "";
//                        for(Object error:errorArray) {
//                            message = message + error.toString();
//                        }
//                        throw new RuntimeException(message);
//                    }
//                }
//                Optional<Reseller> optionalReseller = resellerRepository.findByUsernameAndMvnoId(loginDto.getUserName(), mvnoId);
//                try {
//                    if (tempFile.exists()) {
//                        wifiUtils.runWithGroovyClassLoader("postprocess", groovyFilPath + "/wificustomerlogin.groovy", loginDto.getUserName(), loginDto.getPassword(), null, mvnoId, cid, mac);
//                    }
//                } catch (Exception e1) {
//                }
//                if (!optionalReseller.isPresent()) {
//                    //throw new IllegalArgumentException(APIConstants.NOT_FOUND + BAD_CREDENTIALS);
//                    throw new IllegalArgumentException(BAD_CREDENTIALS);
//                }
//                if (!isPasswordMatched(loginDto.getPassword(), optionalReseller.get().getPassword())) {
//                    throw new IllegalArgumentException("Please enter valid reseller Password.");
//                }
//                return optionalReseller.get();
//            }
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    @Override
    public List<Reseller> searchResellersByLocationId(Long locationId, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId))
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            QReseller qReseller = QReseller.reseller;
            BooleanExpression exp = qReseller.isNotNull();


            if (mvnoId == 1) {
                if (locationId == null) return resellerRepository.findAll();

                else exp = exp.and(qReseller.locationMaster.locationMasterId.eq(locationId));
            } else {
                if (locationId == null) exp = exp.and(qReseller.mvnoId.in(mvnoId, 1));
                else
                    exp = exp.and(qReseller.locationMaster.locationMasterId.eq(locationId).and(qReseller.mvnoId.in(mvnoId, 1)));
            }

            return (List<Reseller>) resellerRepository.findAll(exp);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }



	/*@Override
	public void validateLogoutUser(String userName, Long mvnoId) {
		try {
			if (!ValidateCrudTransactionData.validateStringTypeFieldValue(userName)) {
				throw new IllegalArgumentException(
						APIConstants.BASIC_STRING_MSG + "User name is mandatory. Please enter valid user name.");
			} else {
				Optional<Reseller> optionalReseller = resellerRepository.findByResellerNameAndMvnoId(userName, mvnoId);
				if (!optionalReseller.isPresent()) {
					throw new IllegalArgumentException(
							"No record found with user : '" + userName + "'. Please enter valid user name.");
				}
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
*/

    @Transactional
    @Override
    public String changeStatus(Long resellerId, String status, Long mvnoId) {
        String msg;
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_UPDATE);
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(resellerId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid policy id to change status of  record.");
            } else {
                QReseller qReseller = QReseller.reseller;
                BooleanExpression exp = qReseller.isNotNull();
                exp = exp.and(qReseller.mvnoId.eq(mvnoId).and(qReseller.resellerId.eq(resellerId)));
                Optional<Reseller> optionalReseller = resellerRepository.findOne(exp);
                if (optionalReseller.isPresent()) {
                    log.info("Reseller status has been updated successfully: " + optionalReseller.get().getResellerName() + " by " + MDC.get("username"));
                    optionalReseller.get().setStatus(status);
                    resellerRepository.save(optionalReseller.get());
                    if (status.equals("Active")) {
                        msg = "Policy '" + optionalReseller.get().getResellerName() + "' has been activated successfully.";
                    } else {
                        msg = "Policy '" + optionalReseller.get().getResellerName() + "' has been inactivated successfully.";
                    }
                    return msg;
                } else {
                    throw new RuntimeException("No record found for reseller with the given reseller id :'" + resellerId + "' and mvno id '" + mvnoId + "', Please enter valid reseller id and mvno id to change the status");
                }
            }
        } catch (Throwable e) {
            log.error("Error while delete plan: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(APIConstants.TYPE);
        }

    }


    public void changePassword(ResellerChangePasswordDto passwordDto, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(passwordDto.getUsername())) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid user name");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(passwordDto.getNewPassword())) {
                throw new IllegalArgumentException(APIConstants.BASIC_STRING_MSG + "Please enter valid password");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(passwordDto.getConfirmNewPassword())) {
                throw new IllegalArgumentException(APIConstants.BASIC_STRING_MSG + "Please enter valid confirm password");
            } else if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
                throw new IllegalArgumentException("Please enter valid password. New password and confirm password value must be same.");
            } else if (passwordDto.getUsername() != null) {
                Optional<Reseller> resellerOptional = resellerRepository.findByUsernameAndMvnoId(passwordDto.getUsername(), mvnoId);
                if (!resellerOptional.isPresent()) {
                    throw new IllegalArgumentException("No record found with user : '" + passwordDto.getUsername() + "'. Please enter valid user name to change the password.");
                } else {
                    resellerOptional.get().setPassword(encryptPassword(passwordDto.getNewPassword()));
                    resellerRepository.save(resellerOptional.get());


                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public PageableResponse<Reseller> getAllReseller(Long mvnoId, PaginationDTO paginationDTO, String resellerName) {
        try {
            PageableResponse<Reseller> pageableResponse = new PageableResponse<>();
            QReseller qReseller = QReseller.reseller;
            BooleanExpression exp = qReseller.isNotNull();
            // check mvnoid for superadmin
            if (mvnoId != 1) {
                exp = exp.and(qReseller.mvnoId.in(mvnoId, 1));
            }
            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "resellerName"));
            
            //Check search filter
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(resellerName) && !resellerName.equals("null"))
            {
            	exp = exp.and(qReseller.resellerName.containsIgnoreCase(resellerName));
            }
            //Check date filter
            if (!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null"))) {
                exp = exp.and(qReseller.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00")).or(qReseller.lastModifiedOn.after((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
            }
            if (!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null"))) {
                exp = exp.and(qReseller.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59")).or(qReseller.lastModifiedOn.before((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
            }
            Page<Reseller> page = resellerRepository.findAll(exp, pageable);
            return pageableResponse.convert(new PageImpl<>(page.getContent(), pageable, page.getTotalElements()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer countByLocationId(Long locationId) {
        return resellerRepository.countByLocationMasterLocationMasterId(locationId);
    }
}
