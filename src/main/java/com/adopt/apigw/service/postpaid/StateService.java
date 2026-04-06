package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveStateSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateStateSharedDataMessage;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.pojo.api.StatePojo;
import com.adopt.apigw.repository.postpaid.StateRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.CommonConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StateService extends AbstractService<State, StatePojo, Integer> {

//    @Autowired
//    private MessagesPropertyConfig messagesProperty;
//
//    @Autowired
//    private StateRepository entityRepository;
//
//    @Autowired
//    MessageSender messageSender;
//
//    @Autowired
//    CreateDataSharedService createDataSharedService;
//
//    public static final String MODULE = "[StateService]";
//    private static final Logger logger = LoggerFactory.getLogger(APIController.class);
//    public StateService() {
//        sortColMap.put("countryName", "country.name");
//        sortColMap.put("id", "stateid");
//    }
//
//    @Override
//    public JpaRepository<State, Integer> getRepository() {
//        return entityRepository;
//    }
//
//    public Page<State> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//        if(getMvnoIdFromCurrentStaff() == 1)
//            return entityRepository.findAll(pageRequest);
//        if (null == filterList || 0 == filterList.size())
//            return entityRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        else
//            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
//    }
//
//    public Page<State> searchEntity(String searchText, Integer pageNumber, int pageSize) {
//        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
//        return entityRepository.searchEntity(searchText, pageRequest,getMvnoIdFromCurrentStaff());
//    }
//
    public List<State> getAllActiveEntities() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAllByStatusAndIsDeletedIsFalseOrderByIdDesc(CommonConstants.ACTIVE_STATUS)
        		.stream().filter(state -> state.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || state.getMvnoId() == null || state.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1).collect(Collectors.toList());
    }

    public List<State> getAllEntities() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAll()
        		.stream().filter(state -> state.getMvnoId() == getMvnoIdFromCurrentStaff(null) || state.getMvnoId() == null).collect(Collectors.toList());
    }
//
//    public List<State> findByCountry(Country country) {
//        return entityRepository.findAllByCountryAndIsDeletedIsFalseOrderByIdDesc(country)
//        		.stream().filter(state -> state.getMvnoId() == getMvnoIdFromCurrentStaff() || state.getMvnoId() == null).collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean deleteVerification(Integer id)throws Exception
//    {
//        boolean flag=false;
//        Integer count=entityRepository.deleteVerify(id);
//        if(count==0){
//            flag=true;
//        }
//        return flag;
//    }
//
//    public void deleteState(Integer id) throws Exception {
//        String SUBMODULE = MODULE + " [deleteState()] ";
//        try {
//            State state = entityRepository.getOne(id);
//            boolean flag=this.deleteVerification(state.getId());
//            if(flag){
//                state.setIsDeleted(true);
//                entityRepository.save(state);
//                StateMessage stateMessage = new StateMessage(state);
//                this.messageSender.send(stateMessage, RabbitMqConstants.QUEUE_STATE);
//                createDataSharedService.deleteEntityDataForAllMicroService(state);
//            }else{
//                throw new RuntimeException(DeleteContant.STATE_DELETE_EXIST);
//            }
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//
//    }
//
//    public State getStateForAdd() {
//        return new State();
//    }
//
//    public State getStateForEdit(Integer id) throws Exception {
//        return entityRepository.getOne(id);
//    }
//
//    public State saveState(State state) throws Exception {
//    	if(getMvnoIdFromCurrentStaff() != null) {
//    		state.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        State save = entityRepository.save(state);
//        return save;
//    }
//
//    public List<State> getStateListByCountry(Country country) throws Exception {
//        return entityRepository.findAllByCountryAndIsDeletedIsFalseOrderByIdDesc(country)
//        		.stream().filter(state -> state.getMvnoId() == getMvnoIdFromCurrentStaff() || state.getMvnoId() == null).collect(Collectors.toList());
//    }
//
//    public StatePojo save(StatePojo pojo) throws Exception {
//        String SUBMODULE = MODULE + " [save()] ";
//        try {
//            State obj = convertStatePojoToStateModel(pojo);
//            obj = saveState(obj);
//            pojo = convertStateModelToStatePojo(obj);
//            createDataSharedService.sendEntitySaveDataForAllMicroService(obj);
//            StateMessage stateMessage = new StateMessage(obj);
//            this.messageSender.send(stateMessage, RabbitMqConstants.QUEUE_STATE);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojo;
//    }
//
//    @Override
//    public boolean duplicateVerifyAtSave(String name){
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count = entityRepository.duplicateVerifyAtSave(name);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//    @Override
//    public boolean duplicateVerifyCountryAtSave(String name, Integer countryId){
//        boolean flag = false;
//        if (name != null && countryId != null) {
//            name = name.trim();
//            Integer count = entityRepository.duplicateVerifyCountryAtSave(name , countryId);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//    public StatePojo update(StatePojo pojo, HttpServletRequest req) throws Exception {
//        String SUBMODULE = MODULE + " [update()] ";
//        Integer RESP_CODE = APIConstants.FAIL;
//
//        try {
//            State oldObj1=getStateForEdit(pojo.getId());
//
//            State obj = convertStatePojoToStateModel(pojo);
//            StatePojo pojold=  convertStateModelToStatePojo(oldObj1);
//            State oldstate=getStateForUpdateAndDelete(pojo.getId());
//            State newvalues=new State(pojold,pojo.getId());
//            getStateForUpdateAndDelete(obj.getId());
//            State newobj=getStateForEdit(pojo.getId());
//            String updatedValues = CommonUtils.getUpdatedDiff(pojold,pojo);
//            obj = saveState(obj);
//            pojo = convertStateModelToStatePojo(obj);
//            StateMessage stateMessage = new StateMessage(obj);
//            this.messageSender.send(stateMessage, RabbitMqConstants.QUEUE_STATE);
//            createDataSharedService.updateEntityDataForAllMicroService(obj);
//            RESP_CODE=APIConstants.SUCCESS;
//
//            logger.info("State with : " +updatedValues + "is  updated Successfully "+"; request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),RESP_CODE);
//
//        } catch (Exception ex) {
//            RESP_CODE=APIConstants.FAIL;
//            logger.error("Unable to Update State with name "+pojo.getName()+" : "+" ; request: { From : {}}; Response : {{}};Exception:{}", req.getHeader("requestFrom"),RESP_CODE,ex.getMessage());
//            throw ex;
//        }
//        return pojo;
//    }
//
//    @Override
//    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count = entityRepository.duplicateVerifyAtSave(name);
//            if (count >= 1) {
//                Integer countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    @Override
//    public boolean duplicateVerifyCountryAtEdit(String name,Integer countryId , Integer id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count = entityRepository.duplicateVerifyCountryAtSave(name , countryId);
//            if (count >= 1) {
//                Integer countEdit = entityRepository.duplicateVerifyCountryAtEdit(name,countryId,id);
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//    public State convertStatePojoToStateModel(StatePojo pojo) throws Exception {
//        String SUBMODULE = MODULE + " [convertStatePojoToStateModel()] ";
//        State state = null;
//        try {
//            if (pojo != null) {
//                state = new State();
//                if (pojo.getId() != null) {
//                    state.setId(pojo.getId());
//                }
//                state.setName(pojo.getName());
//                state.setStatus(pojo.getStatus());
//                if(pojo.getMvnoId() != null) {
//                	state.setMvnoId(pojo.getMvnoId());
//                }
//                CountryService countryService = SpringContext.getBean(CountryService.class);
//                if (countryService.get(pojo.getCountryPojo().getId()) != null) {
//                    state.setCountry(countryService.convertCountryPojoToCountryModel(pojo.getCountryPojo()));
//                } else {
//                    throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.country.not.available"), null);
//                }
//                return state;
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//
//        return null;
//    }
//
//    public StatePojo convertStateModelToStatePojo(State state) throws Exception {
//        String SUBMODULE = MODULE + " [convertStateModelToStatePojo()] ";
//        StatePojo pojo = null;
//        try {
//            if (state != null) {
//                pojo = new StatePojo();
//                pojo.setId(state.getId());
//                pojo.setName(state.getName());
//                pojo.setStatus(state.getStatus());
//                pojo.setCreatedate(state.getCreatedate());
//                pojo.setUpdatedate(state.getUpdatedate());
//                pojo.setCreatedById(state.getCreatedById());
//                pojo.setCreatedByName(state.getCreatedByName());
//                pojo.setLastModifiedById(state.getLastModifiedById());
//                pojo.setLastModifiedByName(state.getLastModifiedByName());
//                pojo.setDisplayId(state.getId());
//                pojo.setDisplayName(state.getName());
//                CountryService countryService = SpringContext.getBean(CountryService.class);
//                pojo.setCountryName(countryService.get(state.getCountry().getId()) != null ? countryService.get(state.getCountry().getId()).getName() : null);
//                pojo.setCountryPojo(countryService.convertCountryModelToCountryPojo(state.getCountry()));
//                if(state.getMvnoId() != null) {
//                	pojo.setMvnoId(state.getMvnoId());
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojo;
//    }
//
//    public List<StatePojo> convertResponseModelIntoPojo(List<State> stateList) throws Exception {
//        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
//        List<StatePojo> pojoListRes = new ArrayList<StatePojo>();
//        try {
//            if (stateList != null && stateList.size() > 0) {
//                for (State state : stateList) {
//                    pojoListRes.add(convertStateModelToStatePojo(state));
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojoListRes;
//    }
//
//
//    public void validateRequest(StatePojo pojo, Integer operation) {
//
//        if (pojo == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
//        }
//        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
//            if (pojo.getId() != null)
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
//        }
//        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)
//                || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
//        }
//        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
//                || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
//        }
//        if (pojo != null && operation.equals(CommonConstants.OPERATION_UPDATE)
//                || operation.equals(CommonConstants.OPERATION_DELETE)) {
//            if (entityRepository.findById(pojo.getId()) == null) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.state.not.found"), null);
//            }
//        }
//    }
//    public List<State> getName(String n){
//        QState qState = QState.state;
//        BooleanExpression booleanExpression = qState.isNotNull()
//                .and(qState.name.containsIgnoreCase(n));
//        return (List<State>) entityRepository.findAll(booleanExpression);
//    }
//    public List<State> getByName(String stateName) {
//        return entityRepository.findByName(stateName);
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
//        Sheet sheet = workbook.createSheet("State");
//        List<StatePojo> statePojos = convertResponseModelIntoPojo(entityRepository.findAll());
//        createExcel(workbook, sheet, StatePojo.class, statePojos, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{
//                StatePojo.class.getDeclaredField("id"),
//                StatePojo.class.getDeclaredField("name"),
//                StatePojo.class.getDeclaredField("status"),
//                StatePojo.class.getDeclaredField("countryName"),
//        };
//    }
//
//    @Override
//    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
//        List<StatePojo> statePojos = convertResponseModelIntoPojo(entityRepository.findAll());
//        createPDF(doc, StatePojo.class, statePojos, getFields());
//    }
//
//    public Page<State> getStateByNameOrCountryName(String s1, PageRequest pageRequest) {
//        if(getMvnoIdFromCurrentStaff() == 1)
//            return entityRepository.findAllByNameContainingIgnoreCaseOrCountry_NameContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, s1, pageRequest);
//        return entityRepository.findAllByNameContainingIgnoreCaseOrCountry_NameContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//    }
//
//    @Override
//    public Page<State> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = MODULE + " [search()] ";
//        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//        try {
//            for (GenericSearchModel searchModel : filterList) {
//                if (null != searchModel.getFilterColumn()) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getStateByNameOrCountryName(searchModel.getFilterValue(), pageRequest);
//                    }
//                } else
//                    throw new RuntimeException("Please Provide Search Column!");
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return null;
//    }
//
//    @Override
//    public State get(Integer id) {
//        State state = super.get(id);
//        if (getMvnoIdFromCurrentStaff() == 1 || (state.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || state.getMvnoId() == 1))
//            return state;
//        return null;
//    }
//
//    public State getStateForUpdateAndDelete(Integer id) {
//        State state = get(id);
//        if(state == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == state.getMvnoId().intValue()))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        return state;
//    }


    /* Data get from common microservice */


    @Autowired
    private StateRepository entityRepository;
    @Override
    protected JpaRepository<State, Integer> getRepository() {
        return entityRepository;
    }
    private static Log log = LogFactory.getLog(StateService.class);


    public void saveStateEntity(SaveStateSharedDataMessage stateSharedDataMessage){
        try {
            State state = new State();
            state.setId(stateSharedDataMessage.getId());
            state.setName(stateSharedDataMessage.getName());
            state.setStatus(stateSharedDataMessage.getStatus());
            state.setCountry(stateSharedDataMessage.getCountry());
            state.setMvnoId(stateSharedDataMessage.getMvnoId());
            state.setIsDeleted(stateSharedDataMessage.getIsDeleted());
            state.setMvnoId(stateSharedDataMessage.getMvnoId());
            state.setCreatedById(stateSharedDataMessage.getCreatedById());
            state.setLastModifiedById(stateSharedDataMessage.getLastModifiedById());
            state.setCreatedByName(stateSharedDataMessage.getCreatedByName());
            state.setLastModifiedByName(stateSharedDataMessage.getLastModifiedByName());
            entityRepository.save(state);
        }catch (Exception e){
            log.info("Unable to Create State with name "+stateSharedDataMessage.getName()+" :"+e.getMessage());
        }


    }

    public void updateStateEntity(UpdateStateSharedDataMessage message){
        try {
            if(message.getId()!=null) {
                State state = entityRepository.findById(message.getId()).orElse(null);
                if(state!=null) {
                    state.setName(message.getName());
                    state.setStatus(message.getStatus());
                    state.setCountry(message.getCountry());
                    state.setMvnoId(message.getMvnoId());
                    state.setIsDeleted(message.getIsDeleted());
                    state.setMvnoId(message.getMvnoId());
                    state.setCreatedById(message.getCreatedById());
                    state.setLastModifiedById(message.getLastModifiedById());
//                    state.setCreatedByName(message.getCreatedByName());
                    state.setLastModifiedByName(message.getLastModifiedByName());
                    entityRepository.save(state);
                }else {
//                    log.info("No Data Found   ");
                    State state1 = new State();
                    state1.setId(message.getId());
                    state1.setName(message.getName());
                    state1.setStatus(message.getStatus());
                    state1.setCountry(message.getCountry());
                    state1.setMvnoId(message.getMvnoId());
                    state1.setIsDeleted(message.getIsDeleted());
                    state1.setCreatedById(message.getCreatedById());
                    state1.setLastModifiedById(message.getLastModifiedById());
//                    state.setCreatedByName(message.getCreatedByName());
                    state.setLastModifiedByName(message.getLastModifiedByName());
                    entityRepository.save(state1);
                }
            }
        }catch (Exception e){
            log.info("Unable to Create State with name "+message.getName()+" :"+e.getMessage());
        }


    }

    public List<State> getByName(String name) {
      return  entityRepository.findByName(name);
    }
}
