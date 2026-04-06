package com.adopt.apigw.service.common;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaConstant;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.OTPProfileMessage;
import com.adopt.apigw.service.radius.AbstractService;

import com.adopt.apigw.model.common.OTPManagement;
import com.adopt.apigw.model.common.QOTPManagement;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.pojo.api.OTPManagementDto;
import com.adopt.apigw.pojo.api.UpdateOTPManagementDto;
import com.adopt.apigw.repository.common.OTPManagementRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OTPManagmentService extends AbstractService {

    @Autowired
    OTPManagementRepository otpManagementRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;


    public List<OTPManagement> getOtpProfileByProfileName(String name,Integer mvnoId) {
        try {
            QOTPManagement qOTPManagement = QOTPManagement.oTPManagement;
            BooleanExpression boolExp = qOTPManagement.isNotNull();
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId != 1)
                // TODO: pass mvnoID manually 6/5/2025
                boolExp = boolExp.and(qOTPManagement.mvnoId.in(mvnoId, 1));
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                boolExp = boolExp.and(qOTPManagement.profileName.contains(name));
            return (List<OTPManagement>) otpManagementRepository.findAll(boolExp);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public OTPManagement getOtpProfileById(Long id,Integer mvnoId) {
        try {
            return findByProfileId(id, false, mvnoId);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private OTPManagement findByProfileId(Long profileId, Boolean isUpdateOrDelete,Integer mvnoId) {
        try {
            QOTPManagement qotpManagement = QOTPManagement.oTPManagement;
            BooleanExpression exp = qotpManagement.isNotNull();
            exp = exp.and(qotpManagement.profileId.eq(profileId));
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId != 1)
                // TODO: pass mvnoID manually 6/5/2025
                exp = exp.and(qotpManagement.mvnoId.in(mvnoId, 1));

            OTPManagement otpManagement = (OTPManagement) otpManagementRepository.findOne(exp).orElse(null);
            if(isUpdateOrDelete) {
                // TODO: pass mvnoID manually 6/5/2025
                if (otpManagement == null || (!(mvnoId == 1 || mvnoId.intValue() == otpManagement.getMvnoId().intValue())))
                    throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
                else
                    return otpManagement;
            } else
                // TODO: pass mvnoID manually 6/5/2025
                if (mvnoId == 1 || ((otpManagement.getMvnoId() == mvnoId.intValue() || otpManagement.getMvnoId() == 1)))
                    return otpManagement;
            if (Objects.nonNull(otpManagement)) return otpManagement;
            else
                throw new IllegalArgumentException("No record found for otp profile with id : '" + profileId + "'. OR You do not have access to update or delete this record");
        }
        catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public OTPManagement findByProfileName(String profileName, Boolean isUpdateOrDelete,Integer mvnoId) {
        try {
            QOTPManagement qotpManagement = QOTPManagement.oTPManagement;
            BooleanExpression exp = qotpManagement.isNotNull();
            exp = exp.and(qotpManagement.profileName.eq(profileName));
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId!= 1)
                // TODO: pass mvnoID manually 6/5/2025
                exp = exp.and(qotpManagement.mvnoId.in(mvnoId, 1));
            List<OTPManagement> otpManagementList= IterableUtils.toList( otpManagementRepository.findAll(exp));
            if (otpManagementList.size()>0) {
                // TODO: pass mvnoID manually 6/5/2025
                return otpManagementList.stream()
                        .filter(i -> i.getMvnoId() == mvnoId)
                        .findFirst()
                        .orElseGet(() ->
                                otpManagementList.stream()
                                        .filter(i -> i.getMvnoId() == 1)
                                        .findFirst()
                                        .orElse(null)
                        );
            }
            else
                throw new IllegalArgumentException("No record found for otp profile with profile name : '" + profileName + "'. OR You do not have access to update or delete this record.");
        }catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public List<OTPManagement> findAll(Integer mvnoId) {
        try {
            List<OTPManagement> otpManagementList = new ArrayList<>();
            // TODO: pass mvnoID manually 6/5/2025
            otpManagementList = otpManagementRepository.findAll().stream().filter(otpManagement -> (otpManagement.getMvnoId() == mvnoId || otpManagement.getMvnoId() == 1 || mvnoId == 1)).collect(Collectors.toList());

            for (OTPManagement otpManagement : otpManagementList){
                if(otpManagement.getMvnoId()!= null){
                    otpManagement.setMvnoName(mvnoRepository.getOne(otpManagement.getMvnoId().longValue()).getName());
                }
            }
            return otpManagementList;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }





/*
    public PageableResponse getAllOtpProfile(Long mvnoId, PaginationDTO paginationDTO, String profileName) {
        PageableResponse<OTPManagement> pageableResponse = new PageableResponse<>();
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(WifiConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                QOTPManagement qOtpManagement = QOTPManagement.oTPManagement;
                BooleanExpression exp = qOtpManagement.isNotNull();
                // check mvnoid for superadmin
                if (mvnoId != 1) {
                    exp = exp.and(qOtpManagement.mvnoId.in(mvnoId, 1));
                }
                if (paginationDTO.getPage() > 0) {
                    paginationDTO.setPage(paginationDTO.getPage() - 1);
                }
                Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "createDate"));
                //Check search filter
                if (!StringUtils.isBlank(profileName)) {
                    exp = exp.and(qOtpManagement.profileName.like("%" + profileName + "%"));
                }
                Page<OTPManagement> page = otpManagementRepo.findAll(exp, pageable);
                return pageableResponse.convert(new PageImpl<>(page.getContent(), pageable, page.getTotalElements()));
            }

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }*/

    public void deleteOtpProfileById(Long profileId,Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(profileId)) {
                throw new IllegalArgumentException("Please enter valid profile id.");
            } else {
                Optional<OTPManagement> optionalOtpManagement = Optional.of(findByProfileId(profileId, true,mvnoId));
                if (optionalOtpManagement.isPresent()) {
                    otpManagementRepository.deleteById(profileId);
                } else {
                    throw new IllegalArgumentException("Profile not found for Id " + profileId);
                }

            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public OTPManagement saveOtpProfile(OTPManagementDto otpMang,Integer mvnoId, HttpServletRequest request) {
        try {
             OTPManagement otp = new OTPManagement(otpMang);
            checkForUniqueProfileName(otp, null, false,mvnoId);
            validateOTPProfileData(otp, false);
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId!=null){
                // TODO: pass mvnoID manually 6/5/2025
                otp.setMvnoId(mvnoId);
            }
            otp.setCreatedById(getLoggedInUserId());
            otp.setLastModifiedById(getLoggedInUserId());
            OTPManagement otpManagement=otpManagementRepository.save(otp);

            OTPProfileMessage  otpProfileMessage = new OTPProfileMessage(otpManagement);
//            messageSender.send(otpProfileMessage,RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(otpProfileMessage,OTPProfileMessage.class.getSimpleName(), KafkaConstant.OPT_PROFILE_SAVE));
            return otpManagement;

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


//    private void checkForUniqueProfileName(OTPManagement otpMang, Long profileId, boolean isUpdate) {
//        try {
//            QOTPManagement qOTPManagement = QOTPManagement.oTPManagement;
//            BooleanExpression boolExp = qOTPManagement.isNotNull();
//            boolExp = boolExp.and(qOTPManagement.mvnoId.in(getLoggedInMvnoId(),1));
//            if (isUpdate) {
//                boolExp = boolExp.and(qOTPManagement.profileId.ne(profileId));
//            } else {
//                boolExp = boolExp.and(qOTPManagement.profileName.eq(otpMang.getProfileName()));
//                Optional<OTPManagement> optionalOtpMgmt = otpManagementRepository.findOne(boolExp);
//                if (optionalOtpMgmt.isPresent()) {
//                    throw new RuntimeException("Profile exist with the same name : '" + otpMang.getProfileName() + "'");
//                }
//            }
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    private void checkForUniqueProfileName(OTPManagement otpMang, Long profileId, boolean isUpdate,Integer mvnoId) {
        try {
            QOTPManagement qOTPManagement = QOTPManagement.oTPManagement;
            BooleanExpression boolExp = qOTPManagement.isNotNull();
            if(otpMang.getMvnoId() == null){
                // TODO: pass mvnoID manually 6/5/2025
                boolExp = boolExp.and(qOTPManagement.mvnoId.eq(mvnoId));
            }else{
                boolExp = boolExp.and(qOTPManagement.mvnoId.eq(otpMang.getMvnoId()));
            }
            if (isUpdate) {
                boolExp = boolExp.and(qOTPManagement.profileId.ne(profileId));
            } else {
                boolExp = boolExp.and(qOTPManagement.profileName.eq(otpMang.getProfileName()));
                Optional<OTPManagement> optionalOtpMgmt = otpManagementRepository.findOne(boolExp);
                if (optionalOtpMgmt.isPresent()) {
                    throw new RuntimeException("Profile exist with the same name : '" + otpMang.getProfileName() + "'");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Transactional
    public OTPManagement updateOtpProfile(UpdateOTPManagementDto updateOTPManagementDto,HttpServletRequest request,Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(updateOTPManagementDto.getProfileId())) {
                throw new IllegalArgumentException("Please enter valid profile id.");
            } else {
                OTPManagement oldOTPManagement = findByProfileId(updateOTPManagementDto.getProfileId(), true,mvnoId);
                OTPManagement otpM = otpManagementRepository.findById(updateOTPManagementDto.getProfileId()).orElse(null);
                if (Objects.isNull(otpM)) {
                    throw new RuntimeException("Given OTP not available with given Id: " + updateOTPManagementDto.getProfileId());
                }
                OTPManagement updatedOtp = new OTPManagement(updateOTPManagementDto);
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null)!=null){
                    // TODO: pass mvnoID manually 6/5/2025
                    updatedOtp.setMvnoId(getMvnoIdFromCurrentStaff(null));
                }
                updatedOtp.setProfileName(oldOTPManagement.getProfileName());
                updatedOtp.setCreatedById(getLoggedInUserId());
                updatedOtp.setLastModifiedById(getLoggedInUserId());
                updatedOtp = otpManagementRepository.save(updatedOtp);
                OTPProfileMessage  otpProfileMessage = new OTPProfileMessage(updatedOtp);
//                messageSender.send(otpProfileMessage,RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON_UPDATE);
                kafkaMessageSender.send(new KafkaMessageData(otpProfileMessage,OTPProfileMessage.class.getSimpleName(),KafkaConstant.OPT_PROFILE_UPDATE));
                return updatedOtp;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateOTPProfileData(OTPManagement otpManagement, boolean isUpdateOrDelete) {
        try {
            if (!isUpdateOrDelete && !ValidateCrudTransactionData.validateStringTypeFieldValue(otpManagement.getProfileName())) {
                throw new RuntimeException("OTP Profile name is mandatory. Please enter valid profile name");
            } else if (otpManagement.getOtpLength() == null || otpManagement.getOtpLength() == 0) {
                throw new RuntimeException("OTP length is mandatory.Please enter valid OTP length");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(otpManagement.getOtpValidityInMin())) {
                throw new RuntimeException("Validity is mandatory.");
            } else if (APIConstants.OTP_GENERATION_TYPE.stream().noneMatch(otpGenerationType -> otpManagement.getGenerationType().equals(otpGenerationType))) {
                throw new RuntimeException("Please enter valid generation type allowed values are :-" + APIConstants.OTP_GENERATION_TYPE.toString());
            }/* else if (!(otpManagement.getType().size() > 0)) {
                throw new RuntimeException("Please enter allowed value for OTP. Ex:-Upper Case,Lower Case");
            } else if (!isUpdateOrDelete && !ValidateCrudTransactionData.validateStringTypeFieldValue(otpManagement.getCreatedBy())) {
                throw new RuntimeException("Please enter created by value.");
            } else if (isUpdateOrDelete && !ValidateCrudTransactionData.validateStringTypeFieldValue(otpManagement.getLastModifiedBy())) {
                throw new RuntimeException("Please enter last modified value");
            }*/

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    protected JpaRepository getRepository() {
        return null;
    }



    public OTPManagement saveOtpProfileFromRabbitMq(OTPManagement otpMang) {
        try {
            OTPManagement otp = new OTPManagement(otpMang);
            checkForUniqueProfileName(otp, null, false,otpMang.getMvnoId());
            validateOTPProfileData(otp, false);
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null)!=null){
                // TODO: pass mvnoID manually 6/5/2025
                otp.setMvnoId(getMvnoIdFromCurrentStaff(null));
            }
            otp.setCreatedById(getLoggedInUserId());
            otp.setLastModifiedById(getLoggedInUserId());
            OTPManagement otpManagement=otpManagementRepository.save(otp);

            OTPProfileMessage  otpProfileMessage = new OTPProfileMessage(otpManagement);
//            messageSender.send(otpProfileMessage,RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(otpProfileMessage,OTPProfileMessage.class.getSimpleName(),KafkaConstant.OPT_PROFILE_SAVE));
            return otpManagement;

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


//    public OTPManagementDto domainToDTO (OTPManagement otpManagement){
//        OTPManagementDto otpDTO = new OTPManagementDto();
//        otpDTO.setOtpLength(otpManagement.getOtpLength());
//        otpDTO.setStaticOtp(otpManagement.getStaticOtp());
//        otpDTO.setProfileName(otpManagement.getProfileName());
//        otpDTO.setOtpValidityInMin(otpManagement.getOtpValidityInMin());
//        otpDTO.setGenerationType(otpManagement.getGenerationType());
//        otpDTO.
//        return otpDTO;
//    }
}
