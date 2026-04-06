package com.adopt.apigw.modules.TimeBasePolicy.service;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.TimeBasePolicy.domain.QTimeBasePolicy;
import com.adopt.apigw.modules.TimeBasePolicy.domain.TimeBasePolicy;
import com.adopt.apigw.modules.TimeBasePolicy.mapper.TimeBasePolicyMapper;
import com.adopt.apigw.modules.TimeBasePolicy.module.TimeBasePolicyDTO;
import com.adopt.apigw.modules.TimeBasePolicy.module.TimeBasePolicyDetailsDTO;
import com.adopt.apigw.modules.TimeBasePolicy.repository.TimeBasePolicyRepository;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.adopt.apigw.modules.qosPolicy.repository.QOSPolicyRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TimeBasePolicyService extends ExBaseAbstractService2<TimeBasePolicyDTO, TimeBasePolicy, Long> {

    public TimeBasePolicyService(TimeBasePolicyRepository repository, TimeBasePolicyMapper mapper)
    {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[TimeBasePolicyService]";
    }

    @Autowired
    private QOSPolicyRepository qosPolicyRepository;
    @Autowired
    private TimeBasePolicyRepository timeBasePolicyRepository;

    @Override
    public TimeBasePolicyDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }

    //Get All Time Base Policy with Pagination
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<TimeBasePolicy> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        // TODO: pass mvnoID manually 6/5/2025
        if(mvnoId == 1)
            paginationList = timeBasePolicyRepository.findAll(pageRequest);
        else {
            if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = timeBasePolicyRepository.findAll(pageRequest, Arrays.asList(mvnoId, 1));
            else
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = timeBasePolicyRepository.findAll(pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        }
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    // Duplicate Time Base Policy
    @Override
    public boolean duplicateVerifyAtSave(String policyname,Integer mvnoId) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (policyname != null) {
            policyname = policyname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

//    @Override
//    public TimeBasePolicyDTO saveEntity(TimeBasePolicyDTO timeBasePolicyDTO) throws Exception {
//        //timeBasePolicyDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        timeBasePolicyDTO.setTimeBasePolicyDetailsList(timeBasePolicyDTO.getTimeBasePolicyDetailsList());
//        TimeBasePolicyDTO save = super.saveEntity(timeBasePolicyDTO);
//        QosPolicyMessage message = new QosPolicyMessage(save);
//        messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_QOS_POLICY);
//        return save;
//    }

    //Search Time Base Policy
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            QTimeBasePolicy qTimeBasePolicy = QTimeBasePolicy.timeBasePolicy;
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            BooleanExpression booleanExpression = qTimeBasePolicy.isNotNull().and(qTimeBasePolicy.isDeleted.eq(false));
            GenericDataDTO genericDataDTO = new GenericDataDTO();

            if (filterList.size() > 0) {
                for (GenericSearchModel genericSearchModel : filterList) {
                    booleanExpression = booleanExpression.and(qTimeBasePolicy.name.containsIgnoreCase(genericSearchModel.getFilterValue()));

                }
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId!= 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qTimeBasePolicy.mvnoId.in(1, mvnoId));
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qTimeBasePolicy.mvnoId.eq(1).or(qTimeBasePolicy.mvnoId.eq(mvnoId).and(qTimeBasePolicy.buId.in(getBUIdsFromCurrentStaff()))));
            }
            return makeGenericResponse(genericDataDTO, timeBasePolicyRepository.findAll(booleanExpression,pageRequest));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getTimeBasePolicyByName(String name, PageRequest pageRequest,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<TimeBasePolicy> timeBasePolicyList = null;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1)
                timeBasePolicyList = timeBasePolicyRepository.findAllBynameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
            else
                // TODO: pass mvnoID manually 6/5/2025
                timeBasePolicyList = timeBasePolicyRepository.findAllBynameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(mvnoId, 1));
            if (null != timeBasePolicyList && 0 < timeBasePolicyList.getSize()) {
                makeGenericResponse(genericDataDTO, timeBasePolicyList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    // Duplicate Verify At Time Base Policy
    @Override
    public boolean duplicateVerifyAtEdit(String policyname, Integer policyid,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (policyname != null) {
            policyname = policyname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(mvnoId == 1) countEdit = timeBasePolicyRepository.duplicateVerifyAtEdit(policyname, policyid);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = timeBasePolicyRepository.duplicateVerifyAtEdit(policyname, policyid, Arrays.asList(mvnoId, 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = timeBasePolicyRepository.duplicateVerifyAtEdit(policyname, policyid, mvnoId, getBUIdsFromCurrentStaff());
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

    // Get Time Base Policy By Id
    public TimeBasePolicy getById(Long policyid) {
        return timeBasePolicyRepository.findById(policyid).get();
    }

    //Delete Verification
    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        Integer count=timeBasePolicyRepository.deleteVerify(id);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    public String getid(Long id){
        Optional<QOSPolicy> qosPolicy = qosPolicyRepository.findById(id);
        String name = qosPolicy.get().getName();
        return name;
    }


    public GenericDataDTO timeBaseSearch(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            QTimeBasePolicy qTimeBasePolicy = QTimeBasePolicy.timeBasePolicy;
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            BooleanExpression booleanExpression = qTimeBasePolicy.isNotNull().and(qTimeBasePolicy.isDeleted.eq(false));
            GenericDataDTO genericDataDTO = new GenericDataDTO();

            if (filterList.size() > 0) {
                for (GenericSearchModel genericSearchModel : filterList) {
                    booleanExpression = booleanExpression.and(qTimeBasePolicy.name.containsIgnoreCase(genericSearchModel.getFilterValue()));

                }
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId!= 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qTimeBasePolicy.mvnoId.in(1, mvnoId));
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qTimeBasePolicy.mvnoId.eq(1).or(qTimeBasePolicy.mvnoId.eq(mvnoId).and(qTimeBasePolicy.buId.in(getBUIdsFromCurrentStaff()))));
            }
            return makeGenericResponse(genericDataDTO, timeBasePolicyRepository.findAll(booleanExpression,pageRequest));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
}
