package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveCitySharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateCitySharedDataMessage;
import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.pojo.api.CityPojo;
import com.adopt.apigw.repository.postpaid.CityRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.CommonConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService extends AbstractService<City, CityPojo, Integer> {
    public static final String MODULE = "[CityService]";
    private static Log log = LogFactory.getLog(CityService.class);

    @Autowired
    CityRepository cityRepository;
    @Override
    public JpaRepository<City, Integer> getRepository() {
        return cityRepository;
    }
//
//    public static final String MODULE = "[CityService]";
//
//    public CityService() {
//        sortColMap.put("stateName", "state.name");
//        sortColMap.put("countryName", "country.name");
//        sortColMap.put("id", "cityid");
//    }
//
//    @Autowired
//    private MessagesPropertyConfig messagesProperty;
//
//    @Autowired
//    private CityRepository entityRepository;
//
//    @Autowired
//    private CountryService countryService;
//
//    @Autowired
//    private StateService stateService;
//
//    @Autowired
//    private MessageSender messageSender;
//
//    @Autowired
//    private CreateDataSharedService createDataSharedService;
//private static  final Logger logger= LoggerFactory.getLogger(CityService.class);
//    @Override
//    public JpaRepository<City, Integer> getRepository() {
//        return entityRepository;
//    }
//
//    public Page<City> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//        if(getMvnoIdFromCurrentStaff() == 1)
//            return entityRepository.findAll(pageRequest);
//        if (null == filterList || 0 == filterList.size())
//            return entityRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        else
//            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
//    }
//
//    public Page<City> searchEntity(String searchText, Integer pageNumber, int pageSize) {
//        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
//        return entityRepository.searchEntity(searchText, pageRequest, getMvnoIdFromCurrentStaff());
//    }
//
    public List<City> getAllActiveEntities() {
        return cityRepository.findByStatusAndIsDeleteIsFalseOrderByIdDesc(CommonConstants.ACTIVE_STATUS)
                // TODO: pass mvnoID manually 6/5/2025
        		.stream().filter(city -> city.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || city.getMvnoId() == null || city.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1).collect(Collectors.toList());
    }

    public List<City> getAllEntities() {
        return cityRepository.findAll()
                // TODO: pass mvnoID manually 6/5/2025
        		.stream().filter(city -> city.getMvnoId() == getMvnoIdFromCurrentStaff(null) || city.getMvnoId() == null).collect(Collectors.toList());
    }
//
//    public List<City> findByState(State state) {
//        return entityRepository.findByStateAndIsDeleteIsFalseOrderByIdDesc(state)
//        		.stream().filter(city -> city.getMvnoId() == getMvnoIdFromCurrentStaff() || city.getMvnoId() == null).collect(Collectors.toList());
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
//    public void deleteCity(Integer id) throws Exception {
//        String SUBMODULE = MODULE + "deleteCity()";
//        try {
//            City city = entityRepository.getOne(id);
//            boolean flag=this.deleteVerification(city.getId());
//            if(flag){
//                city.setIsDelete(true);
//                entityRepository.save(city);
//                CityMessage cityMessage = new CityMessage(city);
//                this.messageSender.send(cityMessage, RabbitMqConstants.QUEUE_CITY);
//                createDataSharedService.deleteEntityDataForAllMicroService(city);
//            }else{
//                throw new RuntimeException(DeleteContant.CITY_DELETE_EXIST);
//            }
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    public City getCityForAdd() {
//        return new City();
//    }
//
//    public City getCityForEdit(Integer id) throws Exception {
//        return entityRepository.getOne(id);
//    }
//
//    public City saveCity(City city) throws Exception {
//    	if(getMvnoIdFromCurrentStaff() != null) {
//    		city.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        return entityRepository.save(city);
//
//    }
//
//    public CityPojo save(CityPojo pojo) throws Exception {
//        String SUBMODULE = MODULE + "save()";
//        try {
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//            City obj = convertCityPojoToCityModel(pojo);
//            obj = saveCity(obj);
//            pojo = convertCityModelToCityPojo(obj);
//            CityMessage cityMessage = new CityMessage(obj);
//            this.messageSender.send(cityMessage, RabbitMqConstants.QUEUE_CITY);
//            createDataSharedService.sendEntitySaveDataForAllMicroService(obj);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojo;
//    }
//
//    @Override
//    public boolean duplicateVerifyAtSave(String name) {
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
//    public boolean duplicateVerifyStateAtSave(String name , Integer countryId,Integer STATEID) {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyStateAtSave(name, countryId, STATEID);
//            else count = entityRepository.duplicateVerifyStateAtSave(name,countryId,STATEID, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    public CityPojo update(CityPojo pojo, HttpServletRequest req) throws Exception {
//        String SUBMODULE = MODULE + "update()";
//        Integer RESP_CODE = APIConstants.FAIL;
//
//        City old1=getCityForUpdateAndDelete(pojo.getId());
//        try {
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//            City obj = convertCityPojoToCityModel(pojo);
//            getCityForUpdateAndDelete(obj.getId());
//            City newcity=new City(obj,pojo.getId());
//            String updatedValues = CommonUtils.getUpdatedDiff(convertCityModelToCityPojo(obj),convertCityModelToCityPojo(old1));
//            obj = saveCity(obj);
//            pojo = convertCityModelToCityPojo(obj);
//            CityMessage cityMessage = new CityMessage(obj);
//            this.messageSender.send(cityMessage, RabbitMqConstants.QUEUE_CITY);
//            createDataSharedService.updateEntityDataForAllMicroService(obj);
//            RESP_CODE=APIConstants.SUCCESS;
//
//            logger.info("Country with old values : " + updatedValues +  " updated Successfully; request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),RESP_CODE);
//
//        } catch (Exception ex) {
//            logger.info("Unable to Update Country "+pojo.getName()+"; request: { From : {}}; Response : {{}};Exception:{}", req.getHeader("requestFrom"),RESP_CODE,ex.getMessage());
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
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
//            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count >= 1) {
//                Integer countEdit;
//                if(getMvnoIdFromCurrentStaff() == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
//                else countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//    @Override
//    public boolean duplicateVerifyStateAtEdit(String name ,Integer countryId , Integer STATEID, Integer id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count = entityRepository.duplicateVerifyStateAtSave(name,countryId,STATEID);
//            if (count >= 1) {
//                Integer countEdit = entityRepository.duplicateVerifyStateAtEdit(name,countryId,STATEID, id);
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
//    public City convertCityPojoToCityModel(CityPojo pojo) throws Exception {
//        String SUBMODULE = MODULE + " [convertCityPojoToCityModel()] ";
//        City city = null;
//        try {
//            if (pojo != null) {
//                city = new City();
//                if (pojo.getId() != null) {
//                    city.setId(pojo.getId());
//                }
//                city.setName(pojo.getName());
//                city.setStatus(pojo.getStatus());
//                if(pojo.getMvnoId() != null) {
//                	city.setMvnoId(pojo.getMvnoId());
//                }
//                CountryService countryService = SpringContext.getBean(CountryService.class);
//                if (countryService.get(pojo.getCountryId()) != null) {
//                    city.setCountryId(pojo.getCountryId());
//                } else {
//                    throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.country.not.available"), null);
//                }
//                StateService stateService = SpringContext.getBean(StateService.class);
//                if (stateService.get(pojo.getStatePojo().getId()) != null) {
//                    city.setState(stateService.get(pojo.getStatePojo().getId()));
//                } else {
//                    throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.state.not.available"), null);
//                }
//                return city;
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return city;
//    }
//
//    public CityPojo convertCityModelToCityPojo(City city) throws Exception {
//        String SUBMODULE = MODULE + " [convertCityModelToCityPojo()] ";
//        CityPojo pojo = null;
//        try {
//            if (city != null) {
//                pojo = new CityPojo();
//                pojo.setId(city.getId());
//                pojo.setName(city.getName());
//                pojo.setStatus(city.getStatus());
//                pojo.setCreatedate(city.getCreatedate());
//                pojo.setUpdatedate(city.getUpdatedate());
//                pojo.setCreatedById(city.getCreatedById());
//                pojo.setLastModifiedById(city.getLastModifiedById());
//                pojo.setCreatedByName(city.getCreatedByName());
//                pojo.setLastModifiedByName(city.getLastModifiedByName());
//                pojo.setCountryId(city.getCountryId());
//                pojo.setStateName(city.getState().getName());
//                if(city.getMvnoId() != null) {
//                	pojo.setMvnoId(city.getMvnoId());
//                }
//                pojo.setDisplayId(city.getId());
//                pojo.setDisplayName(city.getName());
//                CountryService countryService = SpringContext.getBean(CountryService.class);
//                pojo.setCountryName(countryService.get(city.getCountryId()) != null ? countryService.get(city.getCountryId()).getName() : null);
//                StateService stateService = SpringContext.getBean(StateService.class);
//                pojo.setStatePojo(stateService.convertStateModelToStatePojo(city.getState()));
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojo;
//    }
//
//    public List<CityPojo> convertResponseModelIntoPojo(List<City> cityList) throws Exception {
//        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
//        List<CityPojo> pojoListRes = new ArrayList<>();
//        try {
//            if (cityList != null && cityList.size() > 0) {
//                for (City city : cityList) {
//                    pojoListRes.add(convertCityModelToCityPojo(city));
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojoListRes;
//
//    }
//
//    public void validateRequest(CityPojo pojo, Integer operation) {
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
//        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
//        }
//        if (pojo != null && operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) {
//            if (entityRepository.findById(pojo.getId()) == null) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.city.not.found"), null);
//            }
//        }
//        if (!operation.equals(CommonConstants.OPERATION_DELETE) && pojo != null && pojo.getCountryId() != null) {
//            if (countryService.get(pojo.getCountryId()) == null) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.city.not.found"), null);
//            }
//        }
//        if (!operation.equals(CommonConstants.OPERATION_DELETE) && pojo != null && pojo.getStatePojo().getId() != null) {
//            if (stateService.get(pojo.getStatePojo().getId()) == null) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.state.not.found"), null);
//            }
//        }
//    }
//
//    public List<City> getName(String n){
//        QCity qCity = QCity.city;
//        BooleanExpression booleanExpression = qCity.isNotNull()
//                .and(qCity.name.containsIgnoreCase(n));
//        return (List<City>) entityRepository.findAll(booleanExpression);
//    }
//    public List<City> getCityByName(String name) {
//        return entityRepository.findByName(name);
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
//        Sheet sheet = workbook.createSheet("City");
//        List<CityPojo> cityPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
//        createExcel(workbook, sheet, CityPojo.class, cityPojoList, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{
//                CityPojo.class.getDeclaredField("id"),
//                CityPojo.class.getDeclaredField("name"),
//                CityPojo.class.getDeclaredField("status"),
//                CityPojo.class.getDeclaredField("stateName"),
//                CityPojo.class.getDeclaredField("countryName"),
//        };
//    }
//
//    @Override
//    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
//        List<CityPojo> cityPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
//        createPDF(doc, CityPojo.class, cityPojoList, getFields());
//    }
//
//    @Override
//    public Page<City> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = MODULE + " [search()] ";
//        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//        try {
//            for (GenericSearchModel searchModel : filterList) {
//                if (null != searchModel.getFilterColumn()) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getCityByNameOrStateNameOrCountryName(searchModel.getFilterValue(), pageRequest);
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
//    public Page<City> getCityByNameOrStateNameOrCountryName(String s1, PageRequest pageRequest) {
//        if(getMvnoIdFromCurrentStaff() == 1)
//            return entityRepository.findAllByNameContainingIgnoreCaseOrState_NameAndIsDeleteIsFalse(s1, s1, s1, s1, pageRequest);
//        return entityRepository.findAllByNameContainingIgnoreCaseOrState_NameAndIsDeleteIsFalse(s1, s1, s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//    }
//
//    @Override
//    public City get(Integer id) {
//        City city = super.get(id);
//        if (getMvnoIdFromCurrentStaff() == 1 || (city.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || city.getMvnoId() == 1))
//            return city;
//        return null;
//    }
//
//    public City getCityForUpdateAndDelete(Integer id) {
//        City city = get(id);
//        if(city == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == city.getMvnoId().intValue()))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        return city;
//    }


    /* Getting Data from  common MicroSerivce*/

    @Transactional
    public void saveCityEntity(SaveCitySharedDataMessage message){
        try {
            City city = new City();
            city.setId(message.getId());
            city.setCountryId(message.getCountryId());
            city.setState(message.getState());
            city.setName(message.getName());
            city.setStatus(message.getStatus());
            city.setMvnoId(message.getMvnoId());
            city.setIsDelete(message.getIsDelete());
            city.setCreatedById(message.getCreatedById());
            city.setLastModifiedById(message.getLastModifiedById());
            city.setCreatedByName(message.getCreatedByName());
            city.setLastModifiedByName(message.getLastModifiedByName());
            cityRepository.save(city);
        }catch (Exception e){
            log.info("Unable to Create City with name "+message.getName()+" :"+e.getMessage());
        }

    }
    @Transactional
    public void updateCityEntity (UpdateCitySharedDataMessage message){
        try {
            if(message.getId()!=null) {
                City city = cityRepository.findById(message.getId()).orElse(null);
                if(city!=null){
                    city.setId(message.getId());
                    city.setCountryId(message.getCountryId());
                    city.setState(message.getState());
                    city.setName(message.getName());
                    city.setStatus(message.getStatus());
                    city.setMvnoId(message.getMvnoId());
                    city.setIsDelete(message.getIsDelete());
                    city.setCreatedById(message.getCreatedById());
                    city.setLastModifiedById(message.getLastModifiedById());
                    city.setCreatedByName(message.getCreatedByName());
                    city.setLastModifiedByName(message.getLastModifiedByName());
                    cityRepository.save(city);
                }else{
//                    log.info("No data Found");
                    City city1 = new City();
                    city1.setCountryId(message.getCountryId());
                    city1.setState(message.getState());
                    city1.setName(message.getName());
                    city1.setStatus(message.getStatus());
                    city1.setMvnoId(message.getMvnoId());
                    city1.setIsDelete(message.getIsDelete());
                    city1.setCreatedById(message.getCreatedById());
                    city1.setLastModifiedById(message.getLastModifiedById());
                    city.setCreatedByName(message.getCreatedByName());
                    city.setLastModifiedByName(message.getLastModifiedByName());
                    cityRepository.save(city1);
                }
            }
        }catch (Exception e){
            log.info("Unable to Update City ");
        }


    }

    public List<City> getCityByName(String name) {
       return cityRepository.findByName(name);
    }
}
