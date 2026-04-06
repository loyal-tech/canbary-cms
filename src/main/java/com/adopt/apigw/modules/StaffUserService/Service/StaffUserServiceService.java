package com.adopt.apigw.modules.StaffUserService.Service;

import com.adopt.apigw.constants.DocumentConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.mapper.postpaid.StaffUserMapper;
import com.adopt.apigw.model.common.QStaffUser;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.StaffUserService.Mapper.StaffUserServiceMapper;
import com.adopt.apigw.modules.StaffUserService.Repository.StaffUserServiceServiceRepository;
import com.adopt.apigw.modules.StaffUserService.domain.QStaffUserServiceMapping1;
import com.adopt.apigw.modules.StaffUserService.domain.StaffUserServiceMapping1;
import com.adopt.apigw.modules.StaffUserService.model.StaffUserServiceDTO;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.common.StaffUserServiceRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class StaffUserServiceService extends ExBaseAbstractService<StaffUserServiceDTO, StaffUserServiceMapping1, Long> {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    StaffUserServiceMapper staffUserServiceMapper;
    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    StaffUserMapper staffUserMapper;
    @Autowired
    private StaffUserServiceRepository staffUserServiceRepository;
    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private ClientServiceSrv clientService;


    public StaffUserServiceService(StaffUserServiceServiceRepository repository, StaffUserServiceMapper mapper) {
        super(repository, mapper);
    }
//    @Override
//    public boolean duplicateVerifyAtSave(String name) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = staffUserServiceRepository.duplicateVerifyAtSave(name);
//            else count = staffUserServiceRepository.duplicateVerifyAtSave(name, mvnoIds);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }


    @Override
    public String getModuleNameForLog() {
        return "[StaffUserServiceService]";
    }

    public GenericDataDTO getStaffbyRecieptNumber(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filters, String prefix, Integer recieptNo) {
        QStaffUserServiceMapping1 qStaffUserServiceMapping = QStaffUserServiceMapping1.staffUserServiceMapping1;
        BooleanExpression expression = qStaffUserServiceMapping.isNotNull().and(qStaffUserServiceMapping.isActive.eq(true));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        JPAQuery<StaffUser> query = new JPAQuery<>(entityManager);
        QStaffUser qStaffUser = QStaffUser.staffUser;
        List<Tuple> queryResult;
        List<StaffUser> staffUserList = new ArrayList<>();
        try {
            if (!prefix.trim().isEmpty()) {
                expression = expression.and(qStaffUserServiceMapping.prefix.equalsIgnoreCase(prefix));
            }
            queryResult = query.select(qStaffUser.id, qStaffUser.firstname, qStaffUser.lastname, qStaffUser.username, qStaffUser.email, qStaffUser.phone,qStaffUser.status).from(qStaffUser, qStaffUserServiceMapping)
                    .where(qStaffUserServiceMapping.fromreceiptnumber.loe(recieptNo)
                            .and(qStaffUserServiceMapping.toreceiptnumber.goe(recieptNo))
                            .and(qStaffUserServiceMapping.stfmappingId.eq(qStaffUser.id)).and(expression)).distinct()
                    .fetch();

            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    StaffUser user = new StaffUser();
                    user.setUsername(result.get(qStaffUser.username));
                    user.setEmail(result.get(qStaffUser.email));
                    user.setStatus(result.get(qStaffUser.status));
                    user.setId(result.get(qStaffUser.id));
                    user.setEmail(result.get(qStaffUser.email));
                    user.setStatus(result.get(qStaffUser.status));
                    user.setFirstname(result.get(qStaffUser.firstname));
                    user.setLastname(result.get(qStaffUser.lastname));
                    staffUserList.add(user);

                });
            }
            genericDataDTO.setDataList(staffUserList);
        } catch (Exception ex) {
            throw ex;
        }
        return genericDataDTO;

    }

    public GenericDataDTO save(StaffUserServiceDTO staffUserServiceDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        StaffUserServiceMapping1 staffUserServiceMapping = staffUserServiceMapper.dtoToDomain(staffUserServiceDTO, new CycleAvoidingMappingContext());

        StaffUserServiceMapping1 save = staffUserServiceRepository.save(staffUserServiceMapping);

        if (save != null) {
            genericDataDTO.setData(save);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        } else {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    public GenericDataDTO uploadProfileImage(MultipartFile file, Integer staffId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseMessage("Uploaded successfully");
        try {
            int allowedFileSize = clientService.getByName(DocumentConstants.ALLOWED_PROFILE_IMAGE__SIZE) != null ? Integer.parseInt(clientService.getByName(DocumentConstants.ALLOWED_PROFILE_IMAGE__SIZE).getValue()) : 500;
            if (file.getSize() > (long) allowedFileSize * 1000)
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "File size limit exceeds. Please provide document within " + allowedFileSize + "KB", null);
            else {
                StaffUser user = staffUserRepository.findById(staffId).orElse(null);
                if (user != null) {
//                    Blob blob = Hibernate.createBlob(file.getInputStream());
                    user.setProfileImage(file.getBytes());
                    staffUserRepository.save(user);
                }

            }
        } catch (CustomValidationException exception) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), exception.getMessage(), null);
        } catch (IOException exception) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), exception.getMessage(), null);
        }
        return genericDataDTO;

    }

    public GenericDataDTO getProfileImage(Integer staffId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage("Uploaded successfully");
            StaffUser staffUser = staffUserRepository.findById(staffId).orElse(null);
            if (staffUser != null && staffUser.getProfileImage() != null) {
                genericDataDTO.setData(Arrays.copyOf(staffUser.getProfileImage(), staffUser.getProfileImage().length));
            }
        } catch (CustomValidationException exception) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), exception.getMessage(), null);
        }
        return genericDataDTO;
    }

    public List getStaffRecieptDataByStaffId(Integer id) {
        List<StaffUserServiceMapping1> staffUserServiceMappingList = staffUserServiceRepository.findByStaffId(id);
        List<StaffUserServiceDTO> staffUserServiceDTOList = staffUserServiceMapper.domainToDTO(staffUserServiceMappingList,new CycleAvoidingMappingContext());
        return staffUserServiceDTOList;
    }
}

