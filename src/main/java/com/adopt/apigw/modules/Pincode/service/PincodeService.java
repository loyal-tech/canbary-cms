package com.adopt.apigw.modules.Pincode.service;


import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SavePincodeSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdatePincodeSharedDataMessage;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericRequestDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Area.domain.QArea;
import com.adopt.apigw.modules.Area.model.AreaDTO;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.Pincode.domain.QPincode;
import com.adopt.apigw.modules.Pincode.mapper.PincodeMapper;
import com.adopt.apigw.modules.Pincode.model.PincodeDTO;
import com.adopt.apigw.modules.Pincode.model.PincodeDetailDTO;
import com.adopt.apigw.modules.Pincode.repository.PincodeRepository;
import com.adopt.apigw.modules.ServiceArea.domain.QServiceAreaPincodeRel;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceAreaPincodeRel;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaPincodeRelRepository;
import com.adopt.apigw.service.postpaid.CityService;
import com.adopt.apigw.service.postpaid.CountryService;
import com.adopt.apigw.service.postpaid.StateService;
import com.itextpdf.text.Document;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PincodeService extends ExBaseAbstractService<PincodeDTO, Pincode, Long> {
//
//    public PincodeService(PincodeRepository repository, PincodeMapper mapper) {
//        super(repository, mapper);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[PincodeService]";
//    }
//
    @Autowired
    private PincodeRepository pincodeRepository;
//    @Autowired
//    private CityService cityService;
//    @Autowired
//    private StateService stateService;
//    @Autowired
//    private CountryService countryService;
//
//    @Autowired
//    private ServiceAreaPincodeRelRepository serviceAreaPincodeRelRepository;
//
//    public PincodeDetailDTO getDetailsByPin(String pincode) throws Exception {
//        String SUBMODULE = " [getDetailsByPin()] ";
//        PincodeDetailDTO detailsModel = new PincodeDetailDTO();
//        try {
//            if (pincode.length() == SubscriberConstants.PINCODE_LENGTH) {
//                Pincode entity = pincodeRepository.findByPincodeAndIsDeletedIsFalse(pincode);
//                if (getMvnoIdFromCurrentStaff() == 1 || entity.getMvnoId() == 1 || entity.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()) {
//                    if (entity != null) {
//                        //Set City
//                        City city = cityService.get(entity.getCityId());
//                        if (city != null) {
//                            GenericRequestDTO cityModel = new GenericRequestDTO();
//                            cityModel.setId(city.getId().longValue());
//                            cityModel.setName(city.getName());
//                            detailsModel.setCity(cityModel);
//                        }
//
//                        //Set Country
//                        Country country = countryService.get(entity.getCountryId());
//                        if (country != null) {
//                            GenericRequestDTO countryModel = new GenericRequestDTO();
//                            countryModel.setId(country.getId().longValue());
//                            countryModel.setName(country.getName());
//                            detailsModel.setCountry(countryModel);
//                        }
//
//                        //Set State
//                        State state = stateService.get(entity.getStateId());
//                        if (state != null) {
//                            GenericRequestDTO stateModel = new GenericRequestDTO();
//                            stateModel.setId(state.getId().longValue());
//                            stateModel.setName(state.getName());
//                            detailsModel.setState(stateModel);
//                        }
//
//                        //Set Area
//                        if (entity.getAreaList().size() > 0) {
//                            List<GenericRequestDTO> areaList = new ArrayList<>();
//                            entity.getAreaList().forEach(data -> {
//                                GenericRequestDTO area = new GenericRequestDTO();
//                                area.setName(data.getName());
//                                area.setId(data.getId());
//                                areaList.add(area);
//                            });
//                            detailsModel.setAreaList(areaList);
//                        }
//
//                        //Set pincode
//                        GenericRequestDTO pinCodeModel = new GenericRequestDTO();
//                        pinCodeModel.setId(entity.getId());
//                        pinCodeModel.setName(entity.getPincode());
//                        detailsModel.setPincode(pinCodeModel);
//                    } else {
//                        throw new RuntimeException("Pincode not found!!");
//                    }
//                } else {
//                    throw new RuntimeException("Pincode not found!!");
//                }
//            } else {
//                throw new RuntimeException("Please provide valid pin code");
//            }
//        } catch (RuntimeException ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return detailsModel;
//    }
//
//    public GenericDataDTO getPincode(String pincode, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getPincode()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            Page<Pincode> qosPolicyList = null;
//            if(getMvnoIdFromCurrentStaff() == 1)
//                qosPolicyList = pincodeRepository.findAllByPincodeContainingIgnoreCaseAndIsDeletedIsFalse(pincode, pageRequest);
//            else
//                qosPolicyList = pincodeRepository.findAllByPincodeContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(pincode, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (null != qosPolicyList && 0 < qosPolicyList.getSize()) {
//                makeGenericResponse(genericDataDTO, qosPolicyList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    public List<PincodeDTO> getAllPincodeBySearch(String s1) {
//        String SUBMODULE = getModuleNameForLog() + " [getAllPincodeBySearch()] ";
//        List<Pincode> pincodeList = null;
//        Page<Pincode> qosPolicyList = null;
//        try {
//            QPincode qPincode = QPincode.pincode1;
//            //List<Pincode> entity = pincodeRepository.findByPincode(s1.trim());
//            List<Country> country = countryService.getName(s1);
//            List<State> state = stateService.getName(s1);
//            List<City> city = cityService.getName(s1);
//            BooleanExpression booleanExpression = qPincode.isNotNull()
//                   // .and(qPincode.isDeleted.eq(false))
//                    .and(qPincode.pincode.containsIgnoreCase(s1))
//                    .or(qPincode.status.containsIgnoreCase(s1));
//            if(country != null){
//                booleanExpression = booleanExpression.or(qPincode.countryId.in(country.stream().map(st->st.getId()).collect(Collectors.toList())));
//            }
//            if(state != null && state.size() > 0){
//                booleanExpression = booleanExpression.or(qPincode.stateId.in(state.stream().map(st->st.getId()).collect(Collectors.toList())));
//            }
//            if(city != null  && city.size() > 0){
//                //booleanExpression = booleanExpression.or(qPincode.cityId.eq(city.getId()));
//                booleanExpression = booleanExpression.or(qPincode.cityId.in(city.stream().map(st->st.getId()).collect(Collectors.toList())));
//            }
//            //pincodeList = pincodeRepository.findAllByPincodeStartingWithAndIsDeletedIsFalse(s1);
//            booleanExpression =booleanExpression.and(qPincode.isDeleted.eq(false));
//            pincodeList = (List<Pincode>) pincodeRepository.findAll(booleanExpression);
//
//            if (null != pincodeList && 0 < pincodeList.size()) {
//                pincodeList.sort(Comparator.comparing(pincode -> pincode.getCreatedate()));
//                Collections.reverse(pincodeList);
//                return pincodeList.stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext()))
//                        .collect(Collectors.toList())
//                        .stream().filter(pincodeDTO -> pincodeDTO.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || pincodeDTO.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
//            }
//            return new ArrayList<>();
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//
//
//    @Override
//    public boolean deleteVerification(Integer id)throws Exception
//    {
//        boolean flag=false;
//        Integer count=pincodeRepository.deleteVerify(id);
//        if(count==0){
//            flag=true;
//        }
//        return flag;
//    }
//
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//                        return getPincode(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
//        Sheet sheet = workbook.createSheet("Pincode");
//        createExcel(workbook, sheet, AreaDTO.class, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{
//                AreaDTO.class.getDeclaredField("id"),
//                AreaDTO.class.getDeclaredField("pincode"),
//        };
//    }
//
//    @Override
//    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
//        createPDF(doc, AreaDTO.class, getFields());
//    }
//
//    @Override
//    public boolean duplicateVerifyAtSave(String pincode) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (pincode != null) {
//        	pincode = pincode.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = pincodeRepository.duplicateVerifyAtSave(pincode);
//            else count = pincodeRepository.duplicateVerifyAtSave(pincode, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//
//    public boolean duplicateVerifyAtSaveWithPincodeAndCityID(String pincode, Integer cityId) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (pincode != null&& cityId!=null) {
//            pincode = pincode.trim();
//            cityId = cityId.intValue();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = pincodeRepository.duplicateVerifyAtSaveWithPincodeAndCityID(pincode, cityId);
//            else count = pincodeRepository.duplicateVerifyAtSaveWithPincodeAndCityID(pincode, cityId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//
//
//    public boolean duplicateVerifyAtEdit(String pincode, Long id,Integer cityId) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (pincode != null) {
//        	pincode = pincode.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = pincodeRepository.duplicateVerifyAtSaveWithPincodeAndCityID(pincode,cityId);
//            else count = pincodeRepository.duplicateVerifyAtSaveWithPincodeAndCityID(pincode,cityId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count >= 1) {
//                Integer countEdit;
//                if(getMvnoIdFromCurrentStaff() == 1) countEdit = pincodeRepository.duplicateVerifyAtEdit(pincode,id,cityId);
//                else countEdit = pincodeRepository.duplicateVerifyAtEdit(pincode, id,cityId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<Pincode> paginationList = null;
//        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
//        if(getMvnoIdFromCurrentStaff() == 1)
//            paginationList = pincodeRepository.findAll(pageRequest);
//        else
//            paginationList = pincodeRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
    public String getPincode(Long id){
       return pincodeRepository.getPincode(id);
    }
//
//    public List<Pincode> getPincodeListByServiceId(List<Long> serviceAreaIds) throws Exception{
//        try {
//            QPincode qPincode = QPincode.pincode1;
//            QServiceAreaPincodeRel qServiceAreaPincodeRel = QServiceAreaPincodeRel.serviceAreaPincodeRel;
////            List<Pincode> pincodeList = new ArrayList<>();
//            BooleanExpression booleanExpression = qServiceAreaPincodeRel.isNotNull().and(qServiceAreaPincodeRel.isDeleted.eq(false));
//            booleanExpression = booleanExpression.and(qServiceAreaPincodeRel.serviceArea.id.in(serviceAreaIds));
//            List<ServiceAreaPincodeRel> serviceAreaPincodeRelList = (List<ServiceAreaPincodeRel>) serviceAreaPincodeRelRepository.findAll(booleanExpression);
//            List<Long> pincodes = serviceAreaPincodeRelList.stream().map(ServiceAreaPincodeRel::getPincodeData).collect(Collectors.toList()).stream().map(Pincode::getId).collect(Collectors.toList());
//            BooleanExpression booleanExpPincode = qPincode.isNotNull().and(qPincode.isDeleted.eq(false)).and(qPincode.id.in(pincodes)).and(qPincode.status.equalsIgnoreCase("Active"));
//            if (getLoggedInUserId() != 1) {
//                booleanExpPincode = booleanExpPincode.and(qPincode.mvnoId.eq(getMvnoIdFromCurrentStaff()));
//            }
//            List<Pincode> finalPincodeList = (List<Pincode>) pincodeRepository.findAll(booleanExpPincode);
//            return finalPincodeList;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    public List<Pincode> findByName(String pincode){
//        return pincodeRepository.findByPincode(pincode);
//    }

/* getting data from the common apigw service */

    public PincodeService(PincodeRepository repository, PincodeMapper mapper) {
        super(repository, mapper);
    }


    @Autowired
    PincodeRepository entityRepository;
    //
    @Override
    public String getModuleNameForLog() {
        return "[PincodeService]";
    }

    private static Log log = LogFactory.getLog(PincodeService.class);
    @Transactional
    public void savePincode(SavePincodeSharedDataMessage message){
        try {
            Pincode pincode = new Pincode();

            pincode.setId(message.getId());
            pincode.setStatus(message.getStatus());
            pincode.setCountryId(message.getCountryId());
            pincode.setStateId(message.getStateId());
            pincode.setCityId(message.getCityId());
            pincode.setMvnoId(message.getMvnoId());
            pincode.setIsDeleted(message.getIsDeleted());
            pincode.setPincode(message.getPincode());
            pincode.setCreatedById(message.getCreatedById());
            pincode.setLastModifiedById(message.getLastModifiedById());
            pincode.setCreatedByName(message.getCreatedByName());
            pincode.setLastModifiedByName(message.getLastModifiedByName());
            entityRepository.save(pincode);
        }catch (Exception e){
            log.info("Unable to Create Pincode with Pincode "+message.getPincode()+" :"+e.getMessage());
        }

    }

    @Transactional
    public void updatePincode(UpdatePincodeSharedDataMessage message){
        try {
            if(message.getId()!=null) {
                Pincode pincode = new Pincode();
                pincode = entityRepository.findById(message.getId()).orElse(null);
                if(pincode!=null) {
                    pincode.setStatus(message.getStatus());
                    pincode.setCountryId(message.getCountryId());
                    pincode.setStateId(message.getStateId());
                    pincode.setCityId(message.getCityId());
                    pincode.setMvnoId(message.getMvnoId());
                    pincode.setIsDeleted(message.getIsDeleted());
                    pincode.setPincode(message.getPincode());
                    pincode.setCreatedById(message.getCreatedById());
                    pincode.setLastModifiedById(message.getLastModifiedById());
                    pincode.setCreatedByName(message.getCreatedByName());
                    pincode.setLastModifiedByName(message.getLastModifiedByName());
                    entityRepository.save(pincode);
                }else{
//                    log.info("No Data Foundd");
                    Pincode pincode1 = new Pincode();

                    pincode1.setId(message.getId());
                    pincode1.setStatus(message.getStatus());
                    pincode1.setCountryId(message.getCountryId());
                    pincode1.setStateId(message.getStateId());
                    pincode1.setCityId(message.getCityId());
                    pincode1.setMvnoId(message.getMvnoId());
                    pincode1.setIsDeleted(message.getIsDeleted());
                    pincode1.setPincode(message.getPincode());
                    pincode1.setCreatedById(message.getCreatedById());
                    pincode1.setLastModifiedById(message.getLastModifiedById());
                    pincode.setCreatedByName(message.getCreatedByName());
                    pincode.setLastModifiedByName(message.getLastModifiedByName());
                    entityRepository.save(pincode1);
                }
            }
        }catch (Exception e){
            log.info("Unable to Update Pincode "+e.getMessage());
        }

    }



}

