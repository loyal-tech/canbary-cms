package com.adopt.apigw.modules.qosPolicy.service;


import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.adopt.apigw.modules.TimeBasePolicy.domain.QTimeBasePolicy;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.adopt.apigw.modules.qosPolicy.domain.QQOSPolicy;
import com.adopt.apigw.modules.qosPolicy.mapper.QOSPolicyMapper;
import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;
import com.adopt.apigw.modules.qosPolicy.repository.QOSPolicyRepository;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.QosPolicyMessage;
import com.adopt.apigw.utils.APIConstants;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.adopt.apigw.core.utillity.log.ApplicationLogger.logger;

@Service
public class QOSPolicyService extends ExBaseAbstractService2<QOSPolicyDTO, QOSPolicy, Long> {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    public QOSPolicyService(QOSPolicyRepository repository, QOSPolicyMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[QOSPolicyService]";
    }

    @Autowired
    private QOSPolicyRepository qosPolicyRepository;

    @Autowired
    private QOSPolicyMapper qosPolicyMapper;

    public GenericDataDTO getPolicyByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            // TODO: pass mvnoID manually 6/5/2025
            Page<QOSPolicy> qosPolicyList = qosPolicyRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoId(name, pageRequest,getMvnoIdFromCurrentStaff(null));
            if (null != qosPolicyList && 0 < qosPolicyList.getSize()) {
                makeGenericResponse(genericDataDTO, qosPolicyList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            QQOSPolicy qOSPolicy = QQOSPolicy.qOSPolicy;
            Page<CustomerInventoryMapping> customerInventoryMappingPage = null;
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            BooleanExpression booleanExpression = qOSPolicy.isNotNull().and(qOSPolicy.isDeleted.eq(false));
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (filterList.size() > 0) {
                for (GenericSearchModel genericSearchModel : filterList) {
                    booleanExpression = booleanExpression.and(qOSPolicy.name.containsIgnoreCase(genericSearchModel.getFilterValue()));
                }
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qOSPolicy.mvnoId.in(1, mvnoId));
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qOSPolicy.mvnoId.eq(1).or(qOSPolicy.mvnoId.eq(mvnoId).and(qOSPolicy.buId.in(getBUIdsFromCurrentStaff()))));
            }
            return makeGenericResponse(genericDataDTO, qosPolicyRepository.findAll(booleanExpression , pageRequest));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name,Integer mvnoId)throws Exception
    {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = qosPolicyRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = qosPolicyRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = qosPolicyRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
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
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = qosPolicyRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = qosPolicyRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = qosPolicyRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(mvnoId == 1) countEdit = qosPolicyRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = qosPolicyRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(mvnoId, 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = qosPolicyRepository.duplicateVerifyAtEdit(name, id, mvnoId, getBUIdsFromCurrentStaff());
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
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("QOSPolicy");
        createExcel(workbook, sheet, QOSPolicyDTO.class, getFields(),mvnoId);
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                QOSPolicyDTO.class.getDeclaredField("id"),
                QOSPolicyDTO.class.getDeclaredField("name"),
                QOSPolicyDTO.class.getDeclaredField("description"),
        };
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, QOSPolicyDTO.class, getFields(),mvnoId);
    }

    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        Integer planCount=qosPolicyRepository.deleteVerify(id);
        Integer qosPolicyCount = qosPolicyRepository.findCountForTimeBasePolicy(id.longValue());
        if(planCount==0 && qosPolicyCount ==0){
            flag=true;
        }
        return flag;
    }


    @Override
    public QOSPolicyDTO saveEntity(QOSPolicyDTO qosPolicyDTO) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
//        qosPolicyDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
        QOSPolicyDTO save = super.saveEntity(qosPolicyDTO);
        QosPolicyMessage message = new QosPolicyMessage(save);
        //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_QOS_POLICY);
        kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        return save;
    }



    @Override
    public QOSPolicyDTO updateEntity(QOSPolicyDTO qosPolicyDTO) throws Exception {
        QOSPolicyDTO update = super.updateEntity(qosPolicyDTO);
        QosPolicyMessage message = new QosPolicyMessage(update);
        //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_QOS_POLICY);
        kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
        return update;
    }

    @Override
    public void deleteEntity(QOSPolicyDTO qosPolicyDTO) throws Exception {
        super.deleteEntity(qosPolicyDTO);
        qosPolicyDTO.setIsDeleted(true);
        QosPolicyMessage message = new QosPolicyMessage(qosPolicyDTO);
        //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_QOS_POLICY);
        kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName(),"QOS_POLICY"));

    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<QOSPolicy> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        // TODO: pass mvnoID manually 6/5/2025
        if(mvnoId == 1)
            paginationList = qosPolicyRepository.findAll(pageRequest);
        else {
            if(getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = qosPolicyRepository.findAll(pageRequest, Arrays.asList(mvnoId, 1));
            else
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = qosPolicyRepository.findAll(pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        }
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    public QOSPolicyDTO getById(Long id, boolean considerDeleteFlag) throws Exception {
        try {
            QOSPolicy domain = (null == qosPolicyRepository.findById(id)) ? null : qosPolicyRepository.findById(id).get();
            if (null == domain || (domain.getDeleteFlag() && considerDeleteFlag)) {
                throw new DataNotFoundException(getModuleNameForLog() + "--" + "Data not found for id " + id);
            }
            QOSPolicyDTO dto = qosPolicyMapper.domainToDTO(qosPolicyRepository.findById(id).get(), new CycleAvoidingMappingContext());
            if(dto == null )
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            return dto;
            /*if(null == dto){

            }*/
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                throw new DataNotFoundException();
            }
            logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }
@Override
    public QOSPolicyDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        Optional<QOSPolicy> policy = qosPolicyRepository.findById(id);
        if(policy.isPresent()){
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId==1){
                return qosPolicyMapper.domainToDTO(policy.get(),new CycleAvoidingMappingContext());
            }

            // TODO: pass mvnoID manually 6/5/2025
            else if(mvnoId == policy.get().getMvnoId().intValue() || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(policy.get().getBuId())){
                return qosPolicyMapper.domainToDTO(policy.get(),new CycleAvoidingMappingContext());
            }else{
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            }
        }
        return null;
    }

    public GenericDataDTO qosSearch(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [qosSearch()] ";
        try {
            QQOSPolicy qOSPolicy = QQOSPolicy.qOSPolicy;
            Page<CustomerInventoryMapping> customerInventoryMappingPage = null;
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            BooleanExpression booleanExpression = qOSPolicy.isNotNull().and(qOSPolicy.isDeleted.eq(false));
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (filterList.size() > 0) {
                for (GenericSearchModel genericSearchModel : filterList) {
                    booleanExpression = booleanExpression.and(qOSPolicy.name.containsIgnoreCase(genericSearchModel.getFilterValue()));
                }
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qOSPolicy.mvnoId.in(1, mvnoId));
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qOSPolicy.mvnoId.eq(1).or(qOSPolicy.mvnoId.eq(mvnoId).and(qOSPolicy.buId.in(getBUIdsFromCurrentStaff()))));
            }
            return makeGenericResponse(genericDataDTO, qosPolicyRepository.findAll(booleanExpression , pageRequest));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

}
