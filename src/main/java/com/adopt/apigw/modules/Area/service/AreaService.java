package com.adopt.apigw.modules.Area.service;


import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveAreaSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateAreaSharedDataMessage;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Area.domain.QArea;
import com.adopt.apigw.modules.Area.mapper.AreaMapper;
import com.adopt.apigw.modules.Area.model.AreaDTO;
import com.adopt.apigw.modules.Area.repository.AreaRepository;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.Pincode.model.PincodeDetailDTO;
import com.adopt.apigw.modules.Pincode.service.PincodeService;
import com.adopt.apigw.modules.ServiceArea.domain.QServiceArea;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AreaService extends ExBaseAbstractService<AreaDTO, Area, Long> {
//
//    public AreaService(AreaRepository repository, AreaMapper mapper) {
//        super(repository, mapper);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[AreaService]";
//    }
//
    @Autowired
    private AreaRepository areaRepository;
//
//    @Autowired
//    private CountryService countryService;
//
//    @Autowired
//    private StateService stateService;
//
//    @Autowired
//    private CityService cityService;
//
//    @Autowired
//    private PincodeService pincodeService;
//
//    public GenericDataDTO getAreaByName(String name, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            Page<Area> qosPolicyList = null;
//            QArea qArea = QArea.area;
//            Country country = countryService.getByName(name);
//            List<State> state = stateService.getByName(name);
//            List<City> city = cityService.getCityByName(name);
//            List<Pincode> pincode = pincodeService.findByName(name);
//            boolean flag = false;
//            BooleanExpression booleanExpression = qArea.isNotNull()
//                  //  .and(qArea.isDeleted.eq(false))
//                    .and(qArea.name.likeIgnoreCase("%" + name.trim() + "%"))
//                    .or(qArea.status.equalsIgnoreCase(name.trim()));
//            if(country != null){
//                booleanExpression = booleanExpression.or(qArea.countryId.eq(country.getId()));
//                //flag = true;
//            }
//            if(state != null && state.size() > 0){
//                //booleanExpression = booleanExpression.or(qArea.stateId.eq(state.stream().map(st->st.getId()).findAny().get()));
//                booleanExpression = booleanExpression.or(qArea.stateId.in(state.stream().map(st->st.getId()).collect(Collectors.toList())));
//            }
//            if(city != null && city.size() > 0){
//                //booleanExpression = booleanExpression.or(qArea.cityId.eq(city.getId()));
//                booleanExpression = booleanExpression.or(qArea.cityId.in(city.stream().map(st->st.getId()).collect(Collectors.toList())));
//            }
//            if(pincode != null && pincode.size() > 0){
//                //booleanExpression = booleanExpression.or(qArea.pincode.id.eq(pincode.getId()));
//                booleanExpression = booleanExpression.or(qArea.pincode.id.in(pincode.stream().map(pc->pc.getId()).collect(Collectors.toList())));
//            }
//            if(getMvnoIdFromCurrentStaff() == 1) {
//                //qosPolicyList = areaRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
//                qosPolicyList = areaRepository.findAll(booleanExpression, pageRequest);
//            }else {
//                //qosPolicyList = areaRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                booleanExpression = booleanExpression.and(qArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                booleanExpression = booleanExpression.and(qArea.isDeleted.eq(false));
//                qosPolicyList = areaRepository.findAll(booleanExpression, pageRequest);
//            }
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
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getAreaByName(searchModel.getFilterValue(), pageRequest);
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
//        Sheet sheet = workbook.createSheet("Area");
//        createExcel(workbook, sheet, AreaDTO.class, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{
//                AreaDTO.class.getDeclaredField("id"),
//                AreaDTO.class.getDeclaredField("name"),
//        };
//    }
//
//    @Override
//    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
//        createPDF(doc, AreaDTO.class, getFields());
//    }
//
    public Area getById(Long id) {
    	return areaRepository.findById(id).get();
    }
//
//    @Override
//    public boolean deleteVerification(Integer id)throws Exception{
//        boolean flag = false;
//        Integer count = areaRepository.deleteVerify(id);
//        if(count==0){
//            flag=true;
//        }
//        return flag;
//    }
//
//
//    public boolean duplicateVerifyAtSave(String name,Integer countryId,Integer stateId,Integer cityId,Integer pincodeId ) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (name != null) {
//        	name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count =areaRepository.duplicateVerifyAtSave(name,countryId,stateId,cityId,pincodeId);
//            else count = areaRepository.duplicateVerifyAtSave(name,countryId,stateId,cityId,pincodeId, mvnoIds);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//
//    public boolean duplicateVerifyAtEdit(String name, Long id,Integer countryId,Integer stateId,Integer cityId,Integer pincodeId) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (name != null) {
//        	name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = areaRepository.duplicateVerifyAtSave(name,countryId,stateId,cityId,pincodeId);
//            else count = areaRepository.duplicateVerifyAtSave(name,countryId,stateId,cityId,pincodeId, mvnoIds);
//            if (count >= 1) {
//                Integer countEdit;
//                if(getMvnoIdFromCurrentStaff() == 1) countEdit = areaRepository.duplicateVerifyAtEdit(name,id,countryId,stateId,cityId,pincodeId);
//                else countEdit = areaRepository.duplicateVerifyAtEdit(name, id,countryId,stateId,cityId,pincodeId, mvnoIds);
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
//        Page<Area> paginationList = null;
//        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
//        if(getMvnoIdFromCurrentStaff() == 1)
//            paginationList = areaRepository.findAll(pageRequest);
//        else
//            paginationList = areaRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }

    //

    @Autowired
    AreaRepository entityRepository;
    public AreaService(AreaRepository repository, AreaMapper mapper) {
        super(repository, mapper);
    }
    //
    private static Log log = LogFactory.getLog(AreaService.class);
    @Override
    public String getModuleNameForLog() {
        return "[AreaService]";
    }

    @Transactional
    public void saveAreaEntity(SaveAreaSharedDataMessage message){
        try {
            Area area = new Area();
            area.setId(message.getId());
            area.setName(message.getName());
            area.setStatus(message.getStatus());
            area.setMvnoId(message.getMvnoId());
            area.setCountryId(message.getCountryId());
            area.setStateId(message.getStateId());
            area.setCityId(message.getCityId());
            area.setPincode(message.getPincode());
            area.setIsDeleted(message.getIsDeleted());
            area.setCreatedById(message.getCreatedById());
            area.setLastModifiedById(message.getLastModifiedById());
            area.setCreatedByName(message.getCreatedByName());
            area.setLastModifiedByName(message.getLastModifiedByName());
            entityRepository.save(area);
        }catch (Exception e){
            log.info("Unable to Create Area with name "+message.getName()+" :"+e.getMessage());
        }


    }
    @Transactional
    public void updateAreaEntity(UpdateAreaSharedDataMessage message){
        try {
            if(message.getId()!=null) {
                Area area = entityRepository.findById(message.getId()).orElse(null);
                if(area!=null) {
                    area.setName(message.getName());
                    area.setStatus(message.getStatus());
                    area.setMvnoId(message.getMvnoId());
                    area.setCountryId(message.getCountryId());
                    area.setStateId(message.getStateId());
                    area.setCityId(message.getCityId());
                    area.setPincode(message.getPincode());
                    area.setIsDeleted(message.getIsDeleted());
                    area.setCreatedById(message.getCreatedById());
                    area.setLastModifiedById(message.getLastModifiedById());
                    area.setCreatedByName(message.getCreatedByName());
                    area.setLastModifiedByName(message.getLastModifiedByName());
                    entityRepository.save(area);
                }else{
//                    log.info("No Data found:");
                    Area area1 = new Area();
                    area1.setId(message.getId());
                    area1.setName(message.getName());
                    area1.setStatus(message.getStatus());
                    area1.setMvnoId(message.getMvnoId());
                    area1.setCountryId(message.getCountryId());
                    area1.setStateId(message.getStateId());
                    area1.setCityId(message.getCityId());
                    area1.setPincode(message.getPincode());
                    area1.setIsDeleted(message.getIsDeleted());
                    area1.setCreatedById(message.getCreatedById());
                    area1.setLastModifiedById(message.getLastModifiedById());
                    area.setCreatedByName(message.getCreatedByName());
                    area.setLastModifiedByName(message.getLastModifiedByName());
                    entityRepository.save(area1);
                }
            }
        }catch (Exception e){
            log.info("Unable to Create Area with name "+message.getName()+" :"+e.getMessage());
        }



    }


}
