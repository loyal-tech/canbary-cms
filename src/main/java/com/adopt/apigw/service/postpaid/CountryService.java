package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveCountrySharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateCountrySharedDataMessage;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.pojo.api.CountryPojo;
import com.adopt.apigw.repository.postpaid.CountryRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.CommonConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class
CountryService extends AbstractService<Country, CountryPojo, Integer> {

//    @Autowired
//    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private CountryRepository entityRepository;

//    @Autowired
//    MessageSender messageSender;

////    @Autowired
////    CreateDataSharedService createDataSharedService;
//
//    @Override
//    protected JpaRepository<Country, Integer> getRepository() {
//        return entityRepository;
//    }
//
//    public static final String MODULE = "[CountryService]";
//    private static final Logger logger = LoggerFactory.getLogger(APIController.class);
//    public Page<Country> searchEntity(String searchText, Integer pageNumber, int pageSize) {
//        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
//        return entityRepository.searchEntity(searchText, pageRequest,getMvnoIdFromCurrentStaff());
//    }
//
    public List<Country> getAllActiveEntities() {
        return entityRepository.findByStatusAndIsDeleteIsFalseOrderByIdDesc(CommonConstants.ACTIVE_STATUS)
                // TODO: pass mvnoID manually 6/5/2025
               .stream().filter(country -> country.getMvnoId().intValue() == getMvnoIdFromCurrentStaff(null).intValue() || country.getMvnoId() == null || country.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1).collect(Collectors.toList());

    }
//
    public List<Country> getAllEntities() {
        return entityRepository.findAll()
                // TODO: pass mvnoID manually 6/5/2025
                .stream().filter(country -> country.getMvnoId() == getMvnoIdFromCurrentStaff(null) || country.getMvnoId() == null).collect(Collectors.toList());
    }
//
//    public Page<Country> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//        Page<Country> page = null;
//        if(getMvnoIdFromCurrentStaff() == 1)
//            return entityRepository.findAll(pageRequest);
//        if (null == filterList || 0 == filterList.size())
//            return entityRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()));
//        else
//            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
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
//    public void deleteCountry(Integer id) throws Exception {
//        String SUBMODULE = MODULE + "[deleteCountry()]";
//        try {
//            Country country = entityRepository.getOne(id);
//            boolean flag=this.deleteVerification(country.getId());
//            if(flag){
//                country.setIsDelete(true);
//                entityRepository.save(country);
//                CountryMessage countryMessage = new CountryMessage(country.getId(), country.getName(), country.getStatus(), country.getIsDelete());
//                this.messageSender.send(countryMessage, RabbitMqConstants.QUEUE_COUNTRY);
//                createDataSharedService.deleteEntityDataForAllMicroService(country);
//            }else{
//                throw new RuntimeException(DeleteContant.COUNTRY_DELETE_EXIST);
//            }
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    public Country getCountryForAdd() {
//        return new Country();
//    }
//
//    public Country getCountryForEdit(Integer id) throws Exception {
//        return entityRepository.getOne(id);
//    }
//
//
//    public Country saveCountry(Country country) throws Exception {
//    	if(getMvnoIdFromCurrentStaff() != null) {
//        	country.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        Country save = entityRepository.save(country);
//        return save;
//    }
//
//    public CountryPojo save(CountryPojo pojo) throws Exception {
//        String SUBMODULE = MODULE + "save()";
//        try {
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//            Country obj = convertCountryPojoToCountryModel(pojo);
//            obj = saveCountry(obj);
//            pojo = convertCountryModelToCountryPojo(obj);
//            CountryMessage countryMessage = new CountryMessage(pojo.getId(), pojo.getName(), pojo.getStatus(), pojo.getIsDelete());
//            this.messageSender.send(countryMessage, RabbitMqConstants.QUEUE_COUNTRY);
//            createDataSharedService.sendEntitySaveDataForAllMicroService(obj);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojo;
//    }
//
//    @Override
//    public boolean duplicateVerifyAtSave(String name) throws Exception {
//
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
//            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    public CountryPojo update(CountryPojo pojo,HttpServletRequest req) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        String SUBMODULE = MODULE + "update()";
//        Country old1=get(pojo.getId());
//        try {
//
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//            Country dbvalue=getCountryForEdit(pojo.getId());
//            Country obj = convertCountryPojoToCountryModel(pojo);
//        //    Country oldcountry=new Country(pojo,pojo.getId());
//            Country updatedvalue=new Country(pojo,pojo.getId());
//
//            getCountryForUpdateAndDelete(obj.getId());
//            String updatedValues = CommonUtils.getUpdatedDiff(dbvalue,updatedvalue);
//            obj = saveCountry(obj);
//
//            pojo = convertCountryModelToCountryPojo(obj);
//
//
//            CountryMessage countryMessage = new CountryMessage(pojo.getId(), pojo.getName(), pojo.getStatus(), pojo.getIsDelete());
//            this.messageSender.send(countryMessage, RabbitMqConstants.QUEUE_COUNTRY);
//            createDataSharedService.updateEntityDataForAllMicroService(obj);
//
//            RESP_CODE = APIConstants.SUCCESS;
//            logger.info("Country with old  : " + updatedValues +  " updated Successfully; "+" request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),RESP_CODE);
//
//        } catch (Exception ex) {
//            logger.info("Unable to Update Country with old  : " + old1 +  " is updated to : "+pojo.getName() +" "+"; request: { From : {}}; Response : {{}};Exception:{}", req.getHeader("requestFrom"),RESP_CODE,ex.getMessage());
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
//
    public Country convertCountryPojoToCountryModel(CountryPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [convertCountryPojoToCountryModel()] ";
        Country country = null;
        try {
            if (pojo != null) {
                country = new Country();
                if (pojo.getId() != null) {
                    country.setId(pojo.getId());
                }
                country.setName(pojo.getName());
                country.setStatus(pojo.getStatus());
                if(pojo.getMvnoId() != null) {
                	country.setMvnoId(pojo.getMvnoId());
                }
                return country;
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }
//
    public CountryPojo convertCountryModelToCountryPojo(Country country) throws Exception {
        String SUBMODULE = MODULE + " [convertCountryModelToCountryPojo()] ";
        CountryPojo pojo = null;
        try {
            if (country != null) {
                pojo = new CountryPojo();
                pojo.setId(country.getId());
                pojo.setName(country.getName());
                pojo.setStatus(country.getStatus());
                pojo.setCreatedById(country.getCreatedById());
                pojo.setLastModifiedById(country.getLastModifiedById());
                pojo.setCreatedate(country.getCreatedate());
                pojo.setUpdatedate(country.getUpdatedate());
                pojo.setDisplayId(country.getId());
                pojo.setDisplayName(country.getName());
                if(country.getMvnoId() != null) {
                	pojo.setMvnoId(country.getMvnoId());
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public List<CountryPojo> convertResponseModelIntoPojo(List<Country> countryList) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
        List<CountryPojo> pojoListRes = new ArrayList<CountryPojo>();
        try {
            if (countryList != null && countryList.size() > 0) {
                for (Country country : countryList) {
                    pojoListRes.add(convertCountryModelToCountryPojo(country));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }
//
//    public void validateRequest(CountryPojo pojo, Integer operation) {
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
//    }
//    public List<Country> getName(String n){
//        QCountry qCountry = QCountry.country;
//        BooleanExpression booleanExpression = qCountry.isNotNull()
//                .and(qCountry.name.containsIgnoreCase(n));
//        return (List<Country>) entityRepository.findAll(booleanExpression);
//    }
//
//    public Country getByName(String countryName) {
//        return entityRepository.findByNameAndIsDeleteIsFalse(countryName);
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
//        Sheet sheet = workbook.createSheet("Country");
//        List<CountryPojo> countryPojos = convertResponseModelIntoPojo(entityRepository.findAll());
//        createExcel(workbook, sheet, CountryPojo.class, countryPojos, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{
//                CountryPojo.class.getDeclaredField("id"),
//                CountryPojo.class.getDeclaredField("name"),
//                CountryPojo.class.getDeclaredField("status"),
//        };
//    }
//
//    @Override
//    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
//        List<CountryPojo> countryPojos = convertResponseModelIntoPojo(entityRepository.findAll());
//        createPDF(doc, CountryPojo.class, countryPojos, getFields());
//    }
//
//    public Page<Country> getCountryByName(String s1, PageRequest pageRequest) {
//        Page<Country> countryList = null;
//        QCountry qCountry = QCountry.country;
//        BooleanExpression booleanExpression = qCountry.isNotNull()
//                .and(qCountry.isDelete.eq(false))
//                .and(qCountry.name.likeIgnoreCase("%" + s1 + "%"))
//                .or(qCountry.status.equalsIgnoreCase(s1));
//        if(getMvnoIdFromCurrentStaff() == 1) {
//            //return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(s1, pageRequest);
//            return entityRepository.findAll(booleanExpression, pageRequest);
//        }else {
//            //return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            booleanExpression = booleanExpression.and(qCountry.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//            return entityRepository.findAll(booleanExpression, pageRequest);
//        }
//    }
//
//    @Override
//    public Page<Country> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = MODULE + " [search()] ";
//        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//        try {
//            for (GenericSearchModel searchModel : filterList) {
//                if (null != searchModel.getFilterColumn()) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getCountryByName(searchModel.getFilterValue(), pageRequest);
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
//    public Country get(Integer id) {
//        Country country = super.get(id);
//        if (getMvnoIdFromCurrentStaff().intValue() == 1 || (country.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || country.getMvnoId().intValue() == 1))
//            return country;
//        return null;
//    }
//
//    public Country getCountryForUpdateAndDelete(Integer id) {
//        Country country = get(id);
//        if(country == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == country.getMvnoId().intValue()))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        return country;
//    }


    /* getting data from the common micro service   */


    public static final String MODULE = "[CountryService]";


    @Override
    protected JpaRepository<Country, Integer> getRepository() {
        return entityRepository;
    }

    private static Log log = LogFactory.getLog(CountryService.class);
    @Transactional
    public void saveCountry (SaveCountrySharedDataMessage message){
        try {
            Country country = new Country();
            country.setId(message.getId());
            country.setName(message.getName());
            country.setStatus(message.getStatus());
            country.setMvnoId(message.getMvnoId());
            country.setIsDelete(message.getIsDelete());
            country.setCreatedById(message.getCreatedById());
            country.setLastModifiedById(message.getLastModifiedById());
            country.setCreatedByName(message.getCreatedByName());
            country.setLastModifiedByName(message.getLastModifiedByName());
            entityRepository.save(country);
        }catch (Exception e){
            log.info("Unable to Create Country with name "+message.getName()+" :"+e.getMessage());
        }


    }
    @Transactional
    public void updateCountry(UpdateCountrySharedDataMessage message) {
        try {
            if(message.getId()!=null) {
                Country country = entityRepository.findById(message.getId()).orElse(null);
                if(country!=null) {
                    country.setName(message.getName());
                    country.setStatus(message.getStatus());
                    country.setMvnoId(message.getMvnoId());
                    country.setIsDelete(message.getIsDelete());
                    country.setCreatedById(message.getCreatedById());
                    country.setLastModifiedById(message.getLastModifiedById());
                    country.setCreatedByName(message.getCreatedByName());
                    country.setLastModifiedByName(message.getLastModifiedByName());
                    entityRepository.save(country);
                }else{
//                    log.info("No Data found");
                    Country country1 = new Country();
                    country1.setId(message.getId());
                    country1.setName(message.getName());
                    country1.setStatus(message.getStatus());
                    country1.setMvnoId(message.getMvnoId());
                    country1.setIsDelete(message.getIsDelete());
                    country1.setCreatedById(message.getCreatedById());
                    country1.setLastModifiedById(message.getLastModifiedById());
                    country.setCreatedByName(message.getCreatedByName());
                    country.setLastModifiedByName(message.getLastModifiedByName());
                    entityRepository.save(country1);
                }
            }
        }
        catch (Exception e){
            log.info("Unable to Update Country with name "+message.getName()+" :"+e.getMessage());
        }
    }

    public Country getByName(String name) {
        return entityRepository.findByName(name);
    }
}
