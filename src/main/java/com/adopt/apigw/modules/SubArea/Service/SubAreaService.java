package com.adopt.apigw.modules.SubArea.Service;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SubAreaMessage;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.modules.Area.repository.AreaRepository;
import com.adopt.apigw.modules.SubArea.DTO.SubAreaDTO;
import com.adopt.apigw.modules.SubArea.Domain.QSubArea;
import com.adopt.apigw.modules.SubArea.Domain.SubArea;
import com.adopt.apigw.modules.SubArea.Mapper.SubAreaMapper;
import com.adopt.apigw.modules.SubArea.Repository.SubAreaRepository;
import com.adopt.apigw.service.postpaid.CityService;
import com.adopt.apigw.service.postpaid.CountryService;
import com.adopt.apigw.service.postpaid.StateService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubAreaService extends ExBaseAbstractService<SubAreaDTO, SubArea, Long> {



    @Autowired
    CountryService countryService;

    @Autowired
    StateService stateService;

    @Autowired
    CityService cityService;

    @Autowired
    SubAreaRepository subAreaRepository;
    @Autowired
    SubAreaMapper subAreaMapper;
    @Autowired
    AreaRepository areaRepository;





    public SubAreaService(JpaRepository<SubArea, Long> repository, IBaseMapper<SubAreaDTO, SubArea> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[SubAreaService]";
    }


    public GenericDataDTO getSubAreaByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<SubArea> subAreaList = null;
            QSubArea qSubArea = QSubArea.subArea;
            Country country = countryService.getByName(name);
            List<State> state = stateService.getByName(name);
            List<City> city = cityService.getCityByName(name);
            boolean flag = false;
            BooleanExpression booleanExpression = qSubArea.isNotNull()
                    .and(qSubArea.name.likeIgnoreCase("%" + name.trim() + "%"))
                    .or(qSubArea.status.equalsIgnoreCase(name.trim()));
            if(country != null){
                booleanExpression = booleanExpression.or(qSubArea.countryId.eq(country.getId()));
            }
            if(state != null && state.size() > 0){
                booleanExpression = booleanExpression.or(qSubArea.stateId.in(state.stream().map(st->st.getId()).collect(Collectors.toList())));
            }
            if(city != null && city.size() > 0){
                booleanExpression = booleanExpression.or(qSubArea.cityId.in(city.stream().map(st->st.getId()).collect(Collectors.toList())));
            }
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) {
                subAreaList = subAreaRepository.findAll(booleanExpression, pageRequest);
            }else {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qSubArea.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                booleanExpression = booleanExpression.and(qSubArea.isDeleted.eq(false));
                subAreaList = subAreaRepository.findAll(booleanExpression, pageRequest);
            }
            if (null != subAreaList && 0 < subAreaList.getSize()) {
                makeGenericResponse(genericDataDTO, subAreaList);
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
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getSubAreaByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    @GetMapping(path = "/all")
    public List<SubAreaDTO> getAllEntities(Integer mvnoId) {
        List<Integer>mvnoIds=new ArrayList<>();
        // TODO: pass mvnoID manually 6/5/2025
        if(mvnoId!=1){
            mvnoIds.add(mvnoId);
        }
        mvnoIds.add(1);
        QSubArea qSubArea =QSubArea.subArea;
        BooleanExpression booleanExpression=qSubArea.isNotNull().and(qSubArea.isDeleted.eq(false)).and(qSubArea.mvnoId.in(mvnoIds));
        if (Objects.nonNull(getBUIdsFromCurrentStaff()) && !getBUIdsFromCurrentStaff().isEmpty()) {
            booleanExpression = booleanExpression.and(qSubArea.buId.eq(getBUIdsFromCurrentStaff().get(0)));
        }
        List<SubArea> subAreas= IterableUtils.toList(subAreaRepository.findAll(booleanExpression));
        List<SubAreaDTO> subAreaDTOList=subAreaMapper.domainToDTO(subAreas,new CycleAvoidingMappingContext());
        return subAreaDTOList;
    }

    public void saveData(SubAreaMessage dataMessage) throws Exception {
        Optional<SubArea> subAreas = subAreaRepository.findById(dataMessage.getId());

        SubArea subArea = subAreas.orElseGet(SubArea::new);
        subArea.setId(dataMessage.getId());
        subArea.setArea(areaRepository.findById(dataMessage.getAreaId())
                .orElseThrow(() -> new Exception("Area not found with id: " + dataMessage.getAreaId())));

        subArea.setMvnoId(dataMessage.getMvnoId());
        subArea.setStatus(dataMessage.getStatus());
        subArea.setName(dataMessage.getName());
        subArea.setCountryId(dataMessage.getCountryId());
        subArea.setIsDeleted(dataMessage.getIsDeleted());
        subArea.setStateId(dataMessage.getStateId());
        subArea.setCityId(dataMessage.getCityId());

        subAreaRepository.save(subArea);
    }

}
