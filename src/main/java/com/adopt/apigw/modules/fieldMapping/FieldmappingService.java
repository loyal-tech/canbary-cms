package com.adopt.apigw.modules.fieldMapping;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.mapper.postpaid.CustomerAddressMapper;
import com.adopt.apigw.model.postpaid.CustomerAddress;
import com.adopt.apigw.modules.BusinessUnit.service.BusinessUnitService;
import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import com.adopt.apigw.modules.CommonList.service.CommonListService;
import com.adopt.apigw.modules.CommonList.utils.TypeConstants;
import com.adopt.apigw.modules.FieldServiceParamMapping.FieldServiceParamMapping;
import com.adopt.apigw.modules.FieldServiceParamMapping.FieldServiceParamMappingRepository;
import com.adopt.apigw.modules.FieldServiceParamMapping.FieldServiceParamMappingService;
import com.adopt.apigw.modules.ServiceParameterMapping.Service.ServiceParamMappingService;
import com.adopt.apigw.modules.ServiceParameterMapping.mapper.ServiceParamMappingMapper;
import com.adopt.apigw.modules.ServiceParameterMapping.model.ServiceParamMappingDTO;
import com.adopt.apigw.modules.ServiceParameterMapping.repository.ServiceParamMappingRepository;
import com.adopt.apigw.pojo.api.CustomerAddressPojo;
import com.adopt.apigw.repository.postpaid.CustomerAddressRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FieldmappingService extends ExBaseAbstractService2 {

    private static final Logger logger = LoggerFactory.getLogger(FieldMappingController.class);
    @Autowired
    ScreenRepository screenRepository;
    @Autowired
    FieldServiceParamMappingService fieldServiceParamMappingService;

    @Autowired
    ScreenFieldMappingMapper screenFieldMappingMapper;

    @Autowired
    FieldServiceParamMappingRepository fieldServiceParamMappingRepository;
    @Autowired
    FieldsBuidMappingRepo fieldsBuidMappingRepo;
    @Autowired
    FieldmappingMapper fieldmappingMapper;
    @Autowired
    private FieldRepo fieldRepo;

    @Autowired
    private BusinessUnitService businessUnitService;

    @Autowired
    private ServiceParamMappingRepository serviceParamMappingRepository;

    @Autowired
    private ServiceParamMappingMapper serviceParamMappingMapper;

    @Autowired
    ServiceParamMappingService serviceParamMappingService;

    @Autowired
    private FieldsMapper fieldsMapper;

    @Autowired
    private ScreenFieldMappingRepository screenFieldMappingRepository;
    @Autowired
    CommonListService commonListService;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private CustomerAddressMapper customerAddressMapper;

    public FieldmappingService(FieldsBuidMappingRepo repository, FieldmappingMapper mapper) {
        super(repository, mapper);
    }

    public List<FielmappingDto> getTemplate(Long screenid) {
        String SUBMODULE = getModuleNameForLog() + " [getTemplate()] ";
        List<ScreenFieldMapping> screenFieldMappings = new ArrayList<>();
        logger.info(getModuleNameForLog() + "--" + "Fetching TEMPLATE .Data[" + SUBMODULE.toString() + "]");
        try {
            List<FielmappingDto> fielmappingDtos = new ArrayList<>();
            screenFieldMappings = screenFieldMappingRepository.getFields(screenid);
            if (screenFieldMappings.get(0).getScreens().getScreenname().equalsIgnoreCase("plan")) {
                List<FieldServiceParamMapping> plan = fieldServiceParamMappingRepository.findAll();
                int temp=0;
                for(ScreenFieldMapping screenFieldMapping : screenFieldMappings) {
                        FielmappingDto fielmappingDto = new FielmappingDto();
                        fielmappingDto.setDataType(screenFieldMapping.getFields().getDataType());
                        fielmappingDto.setFieldName(screenFieldMapping.getFields().getName());
                        fielmappingDto.setScreen(screenFieldMapping.getScreens().getScreenname());
                        fielmappingDto.setFieldId(screenFieldMapping.getFields().getId());
                        fielmappingDto.setId(screenFieldMapping.getId());
                        for (int i=temp;i<plan.size();) {
                            fielmappingDto.setModule(plan.get(i).getModule());
                            temp++;
                            break;
                        }
                        fielmappingDtos.add(fielmappingDto);
                }
            }else {
                List<FieldsBuidMapping> buidMappings = fieldsBuidMappingRepo.findAllByScreen(screenFieldMappings.get(0).getScreens().getScreenname());
                int temp=0;
                for (ScreenFieldMapping screenFieldMapping : screenFieldMappings) {
                        FielmappingDto fielmappingDto = new FielmappingDto();
                        fielmappingDto.setDataType(screenFieldMapping.getFields().getDataType());
                        fielmappingDto.setFieldName(screenFieldMapping.getFields().getName());
                        fielmappingDto.setScreen(screenFieldMapping.getScreens().getScreenname());
                        fielmappingDto.setFieldId(screenFieldMapping.getFields().getId());
                        fielmappingDto.setId(screenFieldMapping.getId());
                        for (int j=temp;j<buidMappings.size();) {
                            fielmappingDto.setModule(buidMappings.get(j).getModule());
                            temp++;
                            break;
                        }
                        fielmappingDtos.add(fielmappingDto);
                }
            }
            return fielmappingDtos;
        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "--" + exception.getMessage() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }
    public List<Fields> getFields() {
        String SUBMODULE = getModuleNameForLog() + " [getFields()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching Fields .Data[" + SUBMODULE.toString() + "]");
        try {
            List<Long> buids = getBUIdsFromCurrentStaff();
            if(buids.size() == 1) {
//                String buType = businessUnitService.getById(buids.get(0)).getPlanBindingType();
                QFields qFields = QFields.fields;
                BooleanExpression booleanExpression = qFields.isNotNull();
//                booleanExpression = booleanExpression.and(qFields.bu_type.equalsIgnoreCase(buType));
                return (List<Fields>) fieldRepo.findAll(booleanExpression);
            } else
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), MessageConstants.SAVE_RESTRICTED_TO_STAFF_WITH_MULTIPLE, null);
        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public String getModuleNameForLog() {
        return "[FieldmappingService]";
    }

    public FielmappingDto saveEntity(FielmappingDto entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        logger.info(getModuleNameForLog() + "--" + " saveEntity .Data[" + entity.toString() + "]");
        try {
            return fieldmappingMapper.domainToDTO(fieldsBuidMappingRepo.save(fieldmappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext())), new CycleAvoidingMappingContext());
        }
        catch (Exception exception){
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }

    public List<FielmappingDto> saveEntityList(List<FielmappingDto> entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [getFields()] ";
        logger.info(getModuleNameForLog() + "--" + "Save Entity List .DataList[" + entity.toString() + "]");
        try {
            for(FielmappingDto checkDto: entity){
                if(checkDto.getModule()==null)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Module Name is Mandatory", null);
            }
            List<FielmappingDto> fielmappingDtos = new ArrayList<>();
            Set<FieldsBuidMapping> fieldsBuidMappings = fieldsBuidMappingRepo.getAll();
            List<Long> buids = getBUIdsFromCurrentStaff();
            if (buids.size() == 1) {
                entity.stream().forEach(fielmappingDto -> {
                    fielmappingDto.setBuid(buids.get(0));
                });
            } else{
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.SAVE_RESTRICTED_TO_STAFF_WITH_MULTIPLE, null);
            }
            List<FieldsBuidMapping> listOne= fieldsBuidMappings.stream().sorted(Comparator.comparing(FieldsBuidMapping::getFieldId).reversed()).collect(Collectors.toList());
            List<FielmappingDto> listTwo = entity.stream().sorted(Comparator.comparing(FielmappingDto::getFieldId).reversed()).collect(Collectors.toList());
            List<FielmappingDto> buidMappingList = fieldmappingMapper.domainToDTO( listOne, new CycleAvoidingMappingContext());
            List<FielmappingDto> result = Stream.concat(
                            buidMappingList.stream().filter(dto1 -> listTwo.stream().noneMatch(dto2 -> dto2.getFieldId().equals(dto1.getFieldId()))),
                            listTwo.stream().filter(dto2 -> buidMappingList.stream().noneMatch(dto1 -> dto1.getFieldId().equals(dto2.getFieldId()))))
                    .collect(Collectors.toList());

            result.stream().forEach(fielmappingDto -> {
                if (fielmappingDto.getId() != null && getBUIdsFromCurrentStaff().get(0) == fielmappingDto.getBuid()) {
                    fieldsBuidMappingRepo.deleteById(fielmappingDto.getId());
                }
            });
            Set<FieldsBuidMapping> oldFieldsBuidMappings = new HashSet<>();
            List<FielmappingDto> listOneList  = new ArrayList<>();
            List<FieldsBuidMapping> checkBuidMapping = fieldsBuidMappingRepo.findAllByBuid(buids.get(0));
            // save Template
            if (checkBuidMapping.size() == 0) {
                for(FieldsBuidMapping fieldsBuidMapping : fieldsBuidMappings) {
                    FieldsBuidMapping mappings = new FieldsBuidMapping(fieldsBuidMapping);
//                    mappings.setBuid(buids.get(0));
                    oldFieldsBuidMappings.add(mappings);
                }
                listOneList = entity.stream().filter(two -> oldFieldsBuidMappings.stream()
                                .anyMatch(one -> (one.getBuid()==(two.getBuid()))
                                        && one.getScreen().equalsIgnoreCase(two.getScreen())
                                        && one.getModule().equalsIgnoreCase(two.getModule())
                                        && one.getIsMandatory() == two.getIsMandatory()))
                        .collect(Collectors.toList());
                entity.removeAll(listOneList);
            }
            //edit / update field Template
            else if (checkBuidMapping.size() != 0) {
                for (FieldsBuidMapping fieldsBuidMapping : fieldsBuidMappings) {
                    FieldsBuidMapping mappings = new FieldsBuidMapping(fieldsBuidMapping);
                    mappings.setBuid(buids.get(0));
                    oldFieldsBuidMappings.add(mappings);
                }
                listOneList = entity.stream().filter(two -> oldFieldsBuidMappings.stream()
                                .anyMatch(one -> (one.getBuid() == (two.getBuid()))
                                        && one.getScreen().equalsIgnoreCase(two.getScreen())
                                        && one.getModule().equalsIgnoreCase(two.getModule())
                                        && one.getIsMandatory() == two.getIsMandatory()
                                        && two.getDefaultMandatory()))
                        .collect(Collectors.toList());
                entity.removeAll(listOneList);
                List<FielmappingDto> finalOne = fieldmappingMapper.domainToDTO(fieldsBuidMappingRepo.findAllByBuid(buids.get(0)), new CycleAvoidingMappingContext());
                List<FielmappingDto> listTwoList =  entity.stream().filter(two -> finalOne.stream()
                                .anyMatch(one -> (one.getBuid() == (two.getBuid()))
                                        && one.getScreen()==(two.getScreen())
                                        && one.getModule()==(two.getModule())
                                        && one.getIsMandatory() == two.getIsMandatory()))
                        .collect(Collectors.toList());
                entity.removeAll(listTwoList);
                //update isMandatory and moduleName
                List<FielmappingDto> DtoList =  entity.stream().filter(two -> checkBuidMapping.stream()
                                .anyMatch(one -> (two.getFieldId().equals(one.getFieldId()))))
                        .collect(Collectors.toList());
                List<FielmappingDto> updateEntity = new ArrayList<>();
                if(DtoList.size()>0){
                    for (FielmappingDto fielmappingDto:DtoList){
                        for (FieldsBuidMapping fieldsBuidMapping:checkBuidMapping){
                            if(fielmappingDto.getFieldId().equals(fieldsBuidMapping.getFieldId())){
                                fielmappingDto.setId(fieldsBuidMapping.getId());
                            }
                        }
                        updateEntity.add(fielmappingDto);

                    }
                    entity = entity.stream()
                            .map(fielmappingDto -> {
                                updateEntity.stream()
                                        .filter(updateDto -> updateDto.getId() != null && updateDto.getFieldId() == fielmappingDto.getFieldId())
                                        .findFirst()
                                        .ifPresent(updateDto -> fielmappingDto.setId(updateDto.getId()));
                                return fielmappingDto;
                            })
                            .collect(Collectors.toList());
                }
            }
            if (entity.size() != 0) {
                Set<FielmappingDto> fielmappingDtoList = new HashSet<>(entity);
                List<FielmappingDto> list = new ArrayList<>(fielmappingDtoList);
                List<FieldsBuidMapping> fieldsBuidMappingList = fieldmappingMapper.dtoToDomain(list, new CycleAvoidingMappingContext());
                fieldsBuidMappingList = fieldsBuidMappingRepo.saveAll(fieldsBuidMappingList);
                fielmappingDtos = fieldmappingMapper.domainToDTO(fieldsBuidMappingList, new CycleAvoidingMappingContext());
            }
            return fielmappingDtos;
        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }

    public List<FieldsBuidMapping> getbutypes(Long id){
        List<FieldsBuidMapping> list = new ArrayList<>();
        String SUBMODULE = getModuleNameForLog() + " [getbutypes()] ";
        logger.info(getModuleNameForLog() + "--" + " Fetching BU Types .DataList[" + SUBMODULE.toString() + "]");
        try {
            list = fieldsBuidMappingRepo.findAllByBuid(id);
            return list;
        }catch (Exception exception){
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public IBaseDto2 getEntityForUpdateAndDelete(Object id, Integer mvnoId) throws Exception {
        return null;
    }

    @Override
    public Object saveEntity(Object entity) throws Exception {
        return null;
    }

    @Override
    public Object updateEntity(Object entity) throws Exception {
        return null;
    }

    @Override
    public void deleteEntity(Object entity) throws Exception {

    }

    public List<FieldsDetailsDTO> getPlanFieldsByServiceId(Long serviceId) {
        String SUBMODULE = getModuleNameForLog() + " [getPlanFieldsByServiceId()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching PlanFieldsByServiceId .Data[" + SUBMODULE.toString() + "]");
        try {
            List<FieldServiceParamMapping> list = new ArrayList<>();
            List<ServiceParamMappingDTO> serviceParamMappingList = serviceParamMappingService.getParamsByServiceId(serviceId);
            List<Long> serviceparamIdList = serviceParamMappingList.stream().map(ServiceParamMappingDTO::getServiceParamId).collect(Collectors.toList());
            List<FieldServiceParamMapping> byServiceParameterIdIn = fieldServiceParamMappingRepository.findAllByServiceParameterIdIn(serviceparamIdList);
            List<Fields> fieldsList = byServiceParameterIdIn.stream().map(FieldServiceParamMapping::getFields).collect(Collectors.toList());
            List<Long> fieldsIdList = fieldsList.stream().map(Fields::getId).collect(Collectors.toList());
            List<ScreenFieldMapping> screenFieldMappingsList = screenFieldMappingRepository.findAllByFieldsIdInAndScreensId(fieldsIdList, 3L);

            byServiceParameterIdIn.sort(Comparator.comparing(mapping -> mapping.getFields().getId()));
            screenFieldMappingsList.sort(Comparator.comparing(mapping -> mapping.getFields().getId()));

            for (int i = 0; i < byServiceParameterIdIn.size(); i++) {
                FieldServiceParamMapping fieldServiceParamMapping = byServiceParameterIdIn.get(i);
                ServiceParamMappingDTO serviceParamMappingDTO = serviceParamMappingMapper.domainToDTO(serviceParamMappingRepository.findByServiceidAndServiceParamId(serviceId, fieldServiceParamMapping.getServiceParameter().getId()), new CycleAvoidingMappingContext());
                fieldServiceParamMapping.getFields().setDefaultValue(serviceParamMappingDTO.getValue());
                fieldServiceParamMapping.getFields().setMandatoryFlag(serviceParamMappingDTO.getIsMandatory());
            }

            list.addAll(byServiceParameterIdIn);

            // Process the first list using Stream API
            List<FieldsDetailsDTO> listFieldsDetailsDTO = list.stream()
                    .map(fieldServiceParamMapping -> {
                        FieldsDetailsDTO fieldsDetailsDTO = new FieldsDetailsDTO();
                        fieldsDetailsDTO.setId(fieldServiceParamMapping.getFields().getId());
                        fieldsDetailsDTO.setFieldname(fieldServiceParamMapping.getFields().getFieldname());
                        fieldsDetailsDTO.setName(fieldServiceParamMapping.getFields().getName());
                        fieldsDetailsDTO.setDataType(fieldServiceParamMapping.getFields().getDataType());
                        fieldsDetailsDTO.setModule(fieldServiceParamMapping.getModule());
                        fieldsDetailsDTO.setIsMandatory(fieldServiceParamMapping.getIs_mandatory());
                        fieldsDetailsDTO.setDefaultValue(fieldServiceParamMapping.getFields().getDefaultValue());
                        fieldsDetailsDTO.setMandatoryFlag(fieldServiceParamMapping.getFields().getMandatoryFlag());
                        return fieldsDetailsDTO;
                    })
                    .collect(Collectors.toList());

            // Process the second list using Stream API
            List<FieldsDetailsDTO> screenFieldsDetailsDTO = screenFieldMappingsList.stream()
                    .map(screenFieldMapping -> {
                        FieldsDetailsDTO fieldsDetailsDTO = new FieldsDetailsDTO();
                        fieldsDetailsDTO.setFieldType(screenFieldMapping.getFieldType());
                        fieldsDetailsDTO.setEndpoint(screenFieldMapping.getEndpoint());
                        fieldsDetailsDTO.setBackendrequired(screenFieldMapping.getBackendrequired());
                        fieldsDetailsDTO.setDependantfieldName(screenFieldMapping.getDependantfieldName());
                        fieldsDetailsDTO.setIsdependant(screenFieldMapping.getIsdependant());
                        fieldsDetailsDTO.setIsdostrequest(screenFieldMapping.getIspostrequest());
                        fieldsDetailsDTO.setRegex(screenFieldMapping.getRegex());
                        return fieldsDetailsDTO;
                    })
                    .collect(Collectors.toList());

            List<FieldsDetailsDTO> finalfieldsDetailsDTOList = new ArrayList<>();


            for (int i = 0; i < listFieldsDetailsDTO.size(); i++) {
                FieldsDetailsDTO combinedList = new FieldsDetailsDTO();

                // 1st list(listFieldsDetailsDTO)
                combinedList.setId(listFieldsDetailsDTO.get(i).getId());
                combinedList.setFieldname(listFieldsDetailsDTO.get(i).getFieldname());
                combinedList.setName(listFieldsDetailsDTO.get(i).getName());
                combinedList.setDataType(listFieldsDetailsDTO.get(i).getDataType());
                combinedList.setModule(listFieldsDetailsDTO.get(i).getModule());
                combinedList.setIsMandatory(listFieldsDetailsDTO.get(i).getIsMandatory());
                combinedList.setDefaultValue(listFieldsDetailsDTO.get(i).getDefaultValue());
                combinedList.setMandatoryFlag(listFieldsDetailsDTO.get(i).getMandatoryFlag());

                // 2nd list(screenFieldsDetailsDTO)
                if (i < screenFieldsDetailsDTO.size()) {
                    combinedList.setFieldType(screenFieldsDetailsDTO.get(i).getFieldType());
                    combinedList.setEndpoint(screenFieldsDetailsDTO.get(i).getEndpoint());
                    combinedList.setBackendrequired(screenFieldsDetailsDTO.get(i).getBackendrequired());
                    combinedList.setDependantfieldName(screenFieldsDetailsDTO.get(i).getDependantfieldName());
                    combinedList.setIsdependant(screenFieldsDetailsDTO.get(i).getIsdependant());
                    combinedList.setIsdostrequest(screenFieldsDetailsDTO.get(i).getIsdostrequest());
                    combinedList.setRegex(screenFieldsDetailsDTO.get(i).getRegex());
                }

                finalfieldsDetailsDTOList.add(combinedList);
            }

            finalfieldsDetailsDTOList.forEach(fieldsDetailsDTO -> {
                if (fieldsDetailsDTO.getMandatoryFlag() != null && fieldsDetailsDTO.getMandatoryFlag())
                    fieldsDetailsDTO.setMandatoryFlag(true);
                else
                    fieldsDetailsDTO.setMandatoryFlag(false);
            });
            return finalfieldsDetailsDTOList;

        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }

    public List<FieldsDetailsDTO> getAvailableAndBoundedFields(String screen) {
        String SUBMODULE = getModuleNameForLog() + " [getAvailableAndBoundedFields()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching AvailableAndBoundedFields .Data[" + SUBMODULE.toString() + "]");
        List<FieldsDetailsDTO> finalFieldsDtoList = new ArrayList<>();
        List<FieldsDetailsDTO> finalDTOlist = new ArrayList<>();
        List<Screens> screensList = screenRepository.findIdByScreenname(screen);
        Long screenid = screensList.get(0).getId();
        try {
            List<Long> buids = getBUIdsFromCurrentStaff();
            if (buids.size() == 1) {
                QScreenFieldMapping qScreenFieldMapping = QScreenFieldMapping.screenFieldMapping;
                BooleanExpression booleanExpression = qScreenFieldMapping.screens.id.eq(screenid);

                List<Fields> fields = new ArrayList<>();
                List<ScreenFieldMapping> fieldsList = (List<ScreenFieldMapping>) screenFieldMappingRepository.findAll(booleanExpression);
                List<Long> screenFieldIdsList = fieldsList.stream().map(ScreenFieldMapping::getFields).collect(Collectors.toList()).stream().map(Fields::getId).collect(Collectors.toList());
                screenFieldIdsList.stream().forEach(aLong -> {
                    Fields lists = fieldRepo.findById(aLong).get();
                    fields.add(lists);
                });
                List<Long> fieldIdsList = fields.stream().map(Fields::getId).collect(Collectors.toList());
                QFieldsBuidMapping qFieldsBuidMapping = QFieldsBuidMapping.fieldsBuidMapping;
                BooleanExpression booleanExpression1 = qFieldsBuidMapping.isNotNull().and(qFieldsBuidMapping.screen.equalsIgnoreCase(String.valueOf(screenid))).and(qFieldsBuidMapping.buid.eq(buids.get(0))).and(qFieldsBuidMapping.fieldId.in(fieldIdsList));

                List<FieldsBuidMapping> fieldsBuidMappingList = (List<FieldsBuidMapping>) fieldsBuidMappingRepo.findAll(booleanExpression1);
                List<Fields> boundedFields = fields.stream().filter(two -> fieldsBuidMappingList.stream()
                                .anyMatch(one -> (one.getFieldId().equals(two.getId()))))
                        .collect(Collectors.toList());

                List<FieldsDTO> boundedFieldsDtos = fieldsMapper.domainToDTO(boundedFields, new CycleAvoidingMappingContext());
                List<ScreenFieldMapping> mappings = screenFieldMappingRepository.findAllByScreen(screenid);
                List<ScreenFieldMappingDto> list2 = mappings.stream().map(screenFieldMapping -> screenFieldMappingMapper.domainToDTO(screenFieldMapping,new
                        CycleAvoidingMappingContext())).collect(Collectors.toList());
                for (int i=0;i<mappings.size();i++){
                    if (mappings.get(i).getId().equals(list2.get(i).getId())){
                        ScreenFieldMappingDto screenFieldMappingDto = new ScreenFieldMappingDto();
                        screenFieldMappingDto.setFieldid(mappings.get(i).getFields().getId());
                        screenFieldMappingDto.setIndexing(mappings.get(i).getIndexing());
                        screenFieldMappingDto.setFieldType(mappings.get(i).getFieldType());
                        screenFieldMappingDto.setBackendrequired(mappings.get(i).getBackendrequired());
                        screenFieldMappingDto.setDependantfieldName(mappings.get(i).getDependantfieldName());
                        screenFieldMappingDto.setEndpoint(mappings.get(i).getEndpoint());
                        screenFieldMappingDto.setIsdependant(mappings.get(i).getIsdependant());
                        screenFieldMappingDto.setIsdostrequest(mappings.get(i).getIspostrequest());
                        //screenFieldMappingDto.setChild(mappings.get(i).getParentfields().getId());
                        list2.set(i,screenFieldMappingDto);
                    }
                }

                // Set child of direct field if any
                List<Fields> fieldsTest = mappings.stream().map(ScreenFieldMapping::getFields).collect(Collectors.toList());
                List<Long> fieldIds = fieldsTest.stream().map(Fields::getId).collect(Collectors.toList());
                List<FieldsDTO> nullparentFields = fieldRepo.findAllById(fieldIds).stream().map(fields1 -> fieldsMapper.domainToDTO(fields1, new CycleAvoidingMappingContext())).collect(Collectors.toList());

                for (int i=0;i<nullparentFields.size();i++){
                    List<ScreenFieldMapping> list = new ArrayList<>();
                    List<ScreenFieldMapping> parentfields = screenFieldMappingRepository.findAllByParentfields(nullparentFields.get(i).getId(),screenid);
                    list.addAll(parentfields);
                    // Get fields from list of screen field mapping
                    List<FieldsDTO> list1 = list.stream().map(ScreenFieldMapping::getFields).collect(Collectors.toList()).stream().map(fields1 -> fieldsMapper.domainToDTO(fields1, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                    if(parentfields.size() > 0) {
                        for (int x = 0; x < parentfields.size(); x++) {
                            FieldsDTO fieldsDTO = new FieldsDTO(parentfields.get(x));
                            list1.set(x,fieldsDTO);
                        }
                    }
                    nullparentFields.get(i).setChild(list1);
                }

                for (FieldsDTO fieldsDTOOne : nullparentFields) {
                    FieldsDetailsDTO fieldsDetailsDTO = new FieldsDetailsDTO(fieldsDTOOne);
                    for (ScreenFieldMappingDto fieldMappingDto : list2) {
                        if (fieldsDTOOne.getId().equals(fieldMappingDto.getFieldid())){
                            fieldsDetailsDTO.setFieldname(fieldsDTOOne.getFieldname());
                            fieldsDetailsDTO.setIndexing(fieldMappingDto.getIndexing());
                            fieldsDetailsDTO.setFieldType(fieldMappingDto.getFieldType());
                            fieldsDetailsDTO.setEndpoint(fieldMappingDto.getEndpoint());
                            fieldsDetailsDTO.setDependantfieldName(fieldMappingDto.getDependantfieldName());
                            fieldsDetailsDTO.setBackendrequired(fieldMappingDto.getBackendrequired());
                            fieldsDetailsDTO.setIsdependant(fieldMappingDto.getIsdependant());
                            fieldsDetailsDTO.setIsdostrequest(fieldMappingDto.getIsdostrequest());
                            fieldsDetailsDTO.setRegex(fieldMappingDto.getRegex());
                        }
                    }
                    for (FieldsDTO fieldsDTOTwo : boundedFieldsDtos) {
                        if (fieldsDTOOne.getFieldname().equals(fieldsDTOTwo.getFieldname())) {
                            fieldsDetailsDTO.setIsBounded(true);
                        }
                    }
                    finalFieldsDtoList.add(fieldsDetailsDTO);
                }
                Collections.sort(finalFieldsDtoList, Comparator.comparing(FieldsDetailsDTO::getIndexing));
                finalFieldsDtoList.forEach(p1 -> {
                    Optional<FieldsBuidMapping> matchingFields = fieldsBuidMappingList.stream()
                            .filter(p2 -> p2.getFieldId().equals(p1.getId()))
                            .findFirst();
                    matchingFields.ifPresent(p2 -> p1.setModule(p2.getModule()));
                    matchingFields.ifPresent(p2 -> p1.setIsMandatory(p2.getIsMandatory()));
                });
                List<FieldsBuidMapping> checkMandatory = fieldsBuidMappingRepo.findAllByNullBuids();
                if(checkMandatory.size()== 17)
                {
                    finalDTOlist.addAll( getCustomerMandatoryFields(finalFieldsDtoList,screenid));

                }
                else {
                    finalDTOlist.addAll(finalFieldsDtoList);
                }
            } else
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), MessageConstants.SAVE_RESTRICTED_TO_STAFF_WITH_MULTIPLE, null);
        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
        return finalDTOlist;
    }
    public List<FieldsDetailsDTO> getCustomerTemplate(String screen) {
        return getAvailableAndBoundedFields("customer").stream().filter(fieldsDetailsDTO -> fieldsDetailsDTO.getIsBounded()).collect(Collectors.toList());
    }

    public List<ModuleWiseFieldsDto> getModuleWiseFields(String screen) {

        String SUBMODULE = getModuleNameForLog() + " [getAvailableAndBoundedFields()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching AvailableAndBoundedFields .Data[" + screen.toString() + "]");

        List<ModuleWiseFieldsDto> finalModuleWiseDto = new ArrayList<>();

        try {

            List<FieldsDetailsDTO> list1 = getAvailableAndBoundedFieldsFoModuleWise(screen);

            List<CommonListDTO> commonListList;
            String screenType = "";
            if (screen.equalsIgnoreCase("customer")) screenType = TypeConstants.CUSTOMER_SCREEN;
            else if (screen.equalsIgnoreCase("plan")) screenType = TypeConstants.PLAN_SCREEN;
            else if (screen.equalsIgnoreCase("lead")) screenType = TypeConstants.LEAD_SCREEN;

            commonListList = commonListService.getCommonListByType(screenType);

            Collections.sort(commonListList, Comparator.comparing(CommonListDTO::getId));
            List<String> module = commonListList.stream().map(CommonListDTO::getText).collect(Collectors.toList());

            List<FieldsDetailsDTO> list2 = list1.stream().filter(fieldsDetailsDTO -> fieldsDetailsDTO.getModule() != null).collect(Collectors.toList());

            module.stream().forEach(m -> {
                ModuleWiseFieldsDto moduleWiseFieldsDto1 = new ModuleWiseFieldsDto();
                List<FieldsDetailsDTO> fieldsDetailsDTOS = new ArrayList<>();
                list2.stream().forEach(fieldsDetailsDTO -> {
                    if(!fieldsDetailsDTO.getModule().equalsIgnoreCase(Constants.PERMANENT_ADDRESS_DETAILS)
                        && !fieldsDetailsDTO.getModule().equalsIgnoreCase(Constants.PAYMENT_ADDRESS_DETAILS)
                        && !fieldsDetailsDTO.getModule().equalsIgnoreCase(Constants.PRESENT_ADDRESS_DETAILS)) {
                        if (fieldsDetailsDTO.getModule().equalsIgnoreCase(m))
                            fieldsDetailsDTOS.add(fieldsDetailsDTO);
                    }
                });
                moduleWiseFieldsDto1.setModuleName(m);
                moduleWiseFieldsDto1.setFields(fieldsDetailsDTOS);
                finalModuleWiseDto.add(moduleWiseFieldsDto1);
            });
        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
        List<ModuleWiseFieldsDto> finalList = new ArrayList<>();

        FieldsDTO fieldsDTO = fieldsMapper.domainToDTO(fieldRepo.findByFieldname("addressList"), new CycleAvoidingMappingContext());
        FieldsDTO parentField = new FieldsDTO(screenFieldMappingRepository.findByFieldsIdAndScreensId(fieldsDTO.getId(), 2L));// fieldsDTO.getScreen()
        List<ScreenFieldMapping> screenFieldMappingList = screenFieldMappingRepository.findAllByParentfields(parentField.getId(), 2L);
        List<Long> fieldIdsList = screenFieldMappingList.stream().map(ScreenFieldMapping::getFields).collect(Collectors.toList()).stream().map(Fields::getId).collect(Collectors.toList());
        List<FieldsDTO> fieldsList = fieldRepo.findAllById(fieldIdsList).stream().map(fields -> fieldsMapper.domainToDTO(fields, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        for (int i=0;i<screenFieldMappingList.size();i++) {
            FieldsDTO fieldsDTO1 = new FieldsDTO(screenFieldMappingList.get(i));
            fieldsList.set(i,fieldsDTO1);
        }
        parentField.setChild(fieldsList);

        for(ModuleWiseFieldsDto moduleWiseFieldsDto : finalModuleWiseDto) {
            if (moduleWiseFieldsDto.getModuleName().equalsIgnoreCase(Constants.PRESENT_ADDRESS_DETAILS)) {
                parentField.setFieldname("presentAddress");
                moduleWiseFieldsDto.getFields().add(new FieldsDetailsDTO(parentField));
            } else if (moduleWiseFieldsDto.getModuleName().equalsIgnoreCase(Constants.PAYMENT_ADDRESS_DETAILS)) {
                parentField.setFieldname("paymentAddress");
                moduleWiseFieldsDto.getFields().add(new FieldsDetailsDTO(parentField));
            } else if (moduleWiseFieldsDto.getModuleName().equalsIgnoreCase(Constants.PERMANENT_ADDRESS_DETAILS)) {
                parentField.setFieldname("permanentAddress");
                moduleWiseFieldsDto.getFields().add(new FieldsDetailsDTO(parentField));
            }
            finalList.add(moduleWiseFieldsDto);
        }
        return finalList;
    }
    public List<FieldsDetailsDTO> getCustomerMandatoryFields(List<FieldsDetailsDTO> fieldsDetailsDTOS, Long screenid){
        String SUBMODULE = getModuleNameForLog() + " [getAvailableAndBoundedFields()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching AvailableAndBoundedFields .Data[" + SUBMODULE.toString() + "]");
        List<FieldsDetailsDTO> finalFieldsDtoList = new ArrayList<>();
        try {
            List<Long> buids = getBUIdsFromCurrentStaff();
            if (buids.size() == 1) {
                QScreenFieldMapping qScreenFieldMapping = QScreenFieldMapping.screenFieldMapping;
                BooleanExpression booleanExpression = qScreenFieldMapping.screens.id.eq(screenid);

                List<Fields> fields = new ArrayList<>();
                List<ScreenFieldMapping> fieldsList = (List<ScreenFieldMapping>) screenFieldMappingRepository.findAll(booleanExpression);
                List<Long> screenFieldIdsList = fieldsList.stream().map(ScreenFieldMapping::getFields).collect(Collectors.toList()).stream().map(Fields::getId).collect(Collectors.toList());
                screenFieldIdsList.stream().forEach(aLong -> {
                    Fields lists = fieldRepo.findById(aLong).get();
                    fields.add(lists);
                });
                List<Long> fieldIdsList = fields.stream().map(Fields::getId).collect(Collectors.toList());
                QFieldsBuidMapping qFieldsBuidMapping = QFieldsBuidMapping.fieldsBuidMapping;
                BooleanExpression booleanExpression1 = qFieldsBuidMapping.isNotNull().and(qFieldsBuidMapping.screen.equalsIgnoreCase(String.valueOf(screenid))).and(qFieldsBuidMapping.buid.isNull()).and(qFieldsBuidMapping.fieldId.in(fieldIdsList));

                List<FieldsBuidMapping> fieldsBuidMappingList = (List<FieldsBuidMapping>) fieldsBuidMappingRepo.findAll(booleanExpression1);
                List<Fields> boundedFields = fields.stream().filter(two -> fieldsBuidMappingList.stream()
                                .anyMatch(one -> (one.getFieldId().equals(two.getId()))))
                        .collect(Collectors.toList());

                List<FieldsDTO> boundedFieldsDtos = fieldsMapper.domainToDTO(boundedFields, new CycleAvoidingMappingContext());
                List<Long> fIdsList = boundedFieldsDtos.stream().map(FieldsDTO::getId).collect(Collectors.toList());
                List<ScreenFieldMapping> mappings = screenFieldMappingRepository.findAllByFieldsIdInAndScreensId(fIdsList,screenid);
                List<ScreenFieldMappingDto> list2 = mappings.stream().map(screenFieldMapping -> screenFieldMappingMapper.domainToDTO(screenFieldMapping,new
                        CycleAvoidingMappingContext())).collect(Collectors.toList());
                for (int i=0;i<mappings.size();i++){
                    if (mappings.get(i).getId().equals(list2.get(i).getId())){
                        ScreenFieldMappingDto screenFieldMappingDto = new ScreenFieldMappingDto();
                        screenFieldMappingDto.setFieldid(mappings.get(i).getFields().getId());
                        screenFieldMappingDto.setIndexing(mappings.get(i).getIndexing());
                        screenFieldMappingDto.setFieldType(mappings.get(i).getFieldType());
                        screenFieldMappingDto.setBackendrequired(mappings.get(i).getBackendrequired());
                        screenFieldMappingDto.setDependantfieldName(mappings.get(i).getDependantfieldName());
                        screenFieldMappingDto.setEndpoint(mappings.get(i).getEndpoint());
                        screenFieldMappingDto.setIsdependant(mappings.get(i).getIsdependant());
                        screenFieldMappingDto.setIsdostrequest(mappings.get(i).getIspostrequest());
                        //screenFieldMappingDto.setChild(mappings.get(i).getParentfields().getId());
                        list2.set(i,screenFieldMappingDto);
                    }
                }

                // Set child of direct field if any
                List<Fields> fieldsTest = mappings.stream().map(ScreenFieldMapping::getFields).collect(Collectors.toList());
                List<Long> fieldIds = fieldsTest.stream().map(Fields::getId).collect(Collectors.toList());
                List<FieldsDTO> nullparentFields = fieldRepo.findAllById(fieldIds).stream().map(fields1 -> fieldsMapper.domainToDTO(fields1, new CycleAvoidingMappingContext())).collect(Collectors.toList());

                for (int i=0;i<nullparentFields.size();i++){
                    List<ScreenFieldMapping> list = new ArrayList<>();
                    List<ScreenFieldMapping> parentfields = screenFieldMappingRepository.findAllByParentfields(nullparentFields.get(i).getId(),screenid);
                    list.addAll(parentfields);
                    // Get fields from list of screen field mapping
                    List<FieldsDTO> list1 = list.stream().map(ScreenFieldMapping::getFields).collect(Collectors.toList()).stream().map(fields1 -> fieldsMapper.domainToDTO(fields1, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                    if(parentfields.size() > 0) {
                        for (int x = 0; x < parentfields.size(); x++) {
                            FieldsDTO fieldsDTO = new FieldsDTO(parentfields.get(x));
                            list1.set(x,fieldsDTO);
                        }
                    }
                    nullparentFields.get(i).setChild(list1);
                }

                for (FieldsDTO fieldsDTOOne : nullparentFields) {
                    FieldsDetailsDTO fieldsDetailsDTO = new FieldsDetailsDTO(fieldsDTOOne);
                    for (ScreenFieldMappingDto fieldMappingDto : list2) {
                        if (fieldsDTOOne.getId().equals(fieldMappingDto.getFieldid())){
                            fieldsDetailsDTO.setFieldname(fieldsDTOOne.getFieldname());
                            fieldsDetailsDTO.setIndexing(fieldMappingDto.getIndexing());
                            fieldsDetailsDTO.setFieldType(fieldMappingDto.getFieldType());
                            fieldsDetailsDTO.setEndpoint(fieldMappingDto.getEndpoint());
                            fieldsDetailsDTO.setDependantfieldName(fieldMappingDto.getDependantfieldName());
                            fieldsDetailsDTO.setBackendrequired(fieldMappingDto.getBackendrequired());
                            fieldsDetailsDTO.setIsdependant(fieldMappingDto.getIsdependant());
                            fieldsDetailsDTO.setIsdostrequest(fieldMappingDto.getIsdostrequest());
                            fieldsDetailsDTO.setRegex(fieldMappingDto.getRegex());
                        }
                    }
                    for (FieldsDTO fieldsDTOTwo : boundedFieldsDtos) {
                        if (fieldsDTOOne.getFieldname().equals(fieldsDTOTwo.getFieldname())) {
                            fieldsDetailsDTO.setIsBounded(true);
                            fieldsDetailsDTO.setDefaultMandatory(true);
                        }
                    }
                    finalFieldsDtoList.add(fieldsDetailsDTO);
                }
                Collections.sort(finalFieldsDtoList, Comparator.comparing(FieldsDetailsDTO::getIndexing));
                finalFieldsDtoList.forEach(p1 -> {
                    Optional<FieldsBuidMapping> matchingFields = fieldsBuidMappingList.stream()
                            .filter(p2 -> p2.getFieldId().equals(p1.getId()))
                            .findFirst();
                    matchingFields.ifPresent(p2 -> p1.setModule(p2.getModule()));
                    matchingFields.ifPresent(p2 -> p1.setIsMandatory(p2.getIsMandatory()));
                });

            } else
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), MessageConstants.SAVE_RESTRICTED_TO_STAFF_WITH_MULTIPLE, null);
        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }

//        fieldsDetailsDTOS.addAll(finalFieldsDtoList);
        fieldsDetailsDTOS.forEach(p1 -> {
            Optional<FieldsDetailsDTO> matchingFields = finalFieldsDtoList.stream()
                    .filter(p2 -> p2.getId().equals(p1.getId()))
                    .findFirst();
            matchingFields.ifPresent(p2 -> p1.setIsBounded(p2.getIsBounded()));
            matchingFields.ifPresent(p2 -> p1.setIsMandatory(p2.getIsMandatory()));
            matchingFields.ifPresent(p2 -> p1.setModule(p2.getModule()));
            matchingFields.ifPresent(p2 -> p1.setScreen(p2.getScreen()));
            matchingFields.ifPresent(p2 -> p1.setDefaultMandatory(p2.getDefaultMandatory()));
            matchingFields.ifPresent(p2 -> p1.setFieldname(p2.getFieldname()));

        });
        return fieldsDetailsDTOS;
    }

    public List<FieldsDetailsDTO> getAvailableAndBoundedFieldsFoModuleWise(String screen) {
        String SUBMODULE = getModuleNameForLog() + " [getAvailableAndBoundedFields()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching AvailableAndBoundedFields .Data[" + SUBMODULE.toString() + "]");
        List<FieldsDetailsDTO> finalFieldsDtoList = new ArrayList<>();
        List<Screens> screensList = screenRepository.findIdByScreenname(screen);
        Long screenid = screensList.get(0).getId();
        try {
            List<Long> buids = getBUIdsFromCurrentStaff();
            if (buids.size() == 1) {
                QScreenFieldMapping qScreenFieldMapping = QScreenFieldMapping.screenFieldMapping;
                BooleanExpression booleanExpression = qScreenFieldMapping.screens.id.eq(screenid);

                List<Fields> fields = new ArrayList<>();
                List<ScreenFieldMapping> fieldsList = (List<ScreenFieldMapping>) screenFieldMappingRepository.findAll(booleanExpression);
                List<Long> screenFieldIdsList = fieldsList.stream().map(ScreenFieldMapping::getFields).collect(Collectors.toList()).stream().map(Fields::getId).collect(Collectors.toList());
                screenFieldIdsList.stream().forEach(aLong -> {
                    Fields lists = fieldRepo.findById(aLong).get();
                    fields.add(lists);
                });
                List<Long> fieldIdsList = fields.stream().map(Fields::getId).collect(Collectors.toList());
                QFieldsBuidMapping qFieldsBuidMapping = QFieldsBuidMapping.fieldsBuidMapping;
                BooleanExpression booleanExpression1 = qFieldsBuidMapping.isNotNull().and(qFieldsBuidMapping.screen.equalsIgnoreCase(String.valueOf(screenid))).and(qFieldsBuidMapping.buid.eq(buids.get(0))).and(qFieldsBuidMapping.fieldId.in(fieldIdsList));

                List<FieldsBuidMapping> fieldsBuidMappingList = (List<FieldsBuidMapping>) fieldsBuidMappingRepo.findAll(booleanExpression1);
                List<Fields> boundedFields = fields.stream().filter(two -> fieldsBuidMappingList.stream()
                                .anyMatch(one -> (one.getFieldId().equals(two.getId()))))
                        .collect(Collectors.toList());

                List<FieldsDTO> boundedFieldsDtos = fieldsMapper.domainToDTO(boundedFields, new CycleAvoidingMappingContext());
                List<ScreenFieldMapping> mappings = screenFieldMappingRepository.findAllByScreen(screenid);
                List<ScreenFieldMappingDto> list2 = mappings.stream().map(screenFieldMapping -> screenFieldMappingMapper.domainToDTO(screenFieldMapping,new
                        CycleAvoidingMappingContext())).collect(Collectors.toList());
                for (int i=0;i<mappings.size();i++){
                    if (mappings.get(i).getId().equals(list2.get(i).getId())){
                        ScreenFieldMappingDto screenFieldMappingDto = new ScreenFieldMappingDto();
                        screenFieldMappingDto.setFieldid(mappings.get(i).getFields().getId());
                        screenFieldMappingDto.setIndexing(mappings.get(i).getIndexing());
                        screenFieldMappingDto.setFieldType(mappings.get(i).getFieldType());
                        screenFieldMappingDto.setBackendrequired(mappings.get(i).getBackendrequired());
                        screenFieldMappingDto.setDependantfieldName(mappings.get(i).getDependantfieldName());
                        screenFieldMappingDto.setEndpoint(mappings.get(i).getEndpoint());
                        screenFieldMappingDto.setIsdependant(mappings.get(i).getIsdependant());
                        screenFieldMappingDto.setIsdostrequest(mappings.get(i).getIspostrequest());
                        //screenFieldMappingDto.setChild(mappings.get(i).getParentfields().getId());
                        list2.set(i,screenFieldMappingDto);
                    }
                }

                // Set child of direct field if any
                List<Fields> fieldsTest = mappings.stream().map(ScreenFieldMapping::getFields).collect(Collectors.toList());
                List<Long> fieldIds = fieldsTest.stream().map(Fields::getId).collect(Collectors.toList());
                List<FieldsDTO> nullparentFields = fieldRepo.findAllById(fieldIds).stream().map(fields1 -> fieldsMapper.domainToDTO(fields1, new CycleAvoidingMappingContext())).collect(Collectors.toList());

                for (int i=0;i<nullparentFields.size();i++){
                    List<ScreenFieldMapping> list = new ArrayList<>();
                    List<ScreenFieldMapping> parentfields = screenFieldMappingRepository.findAllByParentfields(nullparentFields.get(i).getId(),screenid);
                    list.addAll(parentfields);
                    // Get fields from list of screen field mapping
                    List<FieldsDTO> list1 = list.stream().map(ScreenFieldMapping::getFields).collect(Collectors.toList()).stream().map(fields1 -> fieldsMapper.domainToDTO(fields1, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                    if(parentfields.size() > 0) {
                        for (int x = 0; x < parentfields.size(); x++) {
                            FieldsDTO fieldsDTO = new FieldsDTO(parentfields.get(x));
                            list1.set(x,fieldsDTO);
                        }
                    }
                    nullparentFields.get(i).setChild(list1);
                }

                for (FieldsDTO fieldsDTOOne : nullparentFields) {
                    FieldsDetailsDTO fieldsDetailsDTO = new FieldsDetailsDTO(fieldsDTOOne);
                    for (ScreenFieldMappingDto fieldMappingDto : list2) {
                        if (fieldsDTOOne.getId().equals(fieldMappingDto.getFieldid())){
                            fieldsDetailsDTO.setFieldname(fieldsDTOOne.getFieldname());
                            fieldsDetailsDTO.setIndexing(fieldMappingDto.getIndexing());
                            fieldsDetailsDTO.setFieldType(fieldMappingDto.getFieldType());
                            fieldsDetailsDTO.setEndpoint(fieldMappingDto.getEndpoint());
                            fieldsDetailsDTO.setDependantfieldName(fieldMappingDto.getDependantfieldName());
                            fieldsDetailsDTO.setBackendrequired(fieldMappingDto.getBackendrequired());
                            fieldsDetailsDTO.setIsdependant(fieldMappingDto.getIsdependant());
                            fieldsDetailsDTO.setIsdostrequest(fieldMappingDto.getIsdostrequest());
                            fieldsDetailsDTO.setRegex(fieldMappingDto.getRegex());
                        }
                    }
                    for (FieldsDTO fieldsDTOTwo : boundedFieldsDtos) {
                        if (fieldsDTOOne.getFieldname().equals(fieldsDTOTwo.getFieldname())) {
                            fieldsDetailsDTO.setIsBounded(true);
                        }
                    }
                    finalFieldsDtoList.add(fieldsDetailsDTO);
                }
                Collections.sort(finalFieldsDtoList, Comparator.comparing(FieldsDetailsDTO::getIndexing));
                finalFieldsDtoList.forEach(p1 -> {
                    Optional<FieldsBuidMapping> matchingFields = fieldsBuidMappingList.stream()
                            .filter(p2 -> p2.getFieldId().equals(p1.getId()))
                            .findFirst();
                    matchingFields.ifPresent(p2 -> p1.setModule(p2.getModule()));
                    matchingFields.ifPresent(p2 -> p1.setIsMandatory(p2.getIsMandatory()));
                });

            } else
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), MessageConstants.SAVE_RESTRICTED_TO_STAFF_WITH_MULTIPLE, null);
        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
        return finalFieldsDtoList;
    }
    public CustomerAddressPojo getPresentAddressByCustomerId(Integer customerId) {
        String SUBMODULE = getModuleNameForLog() + " [getPresentAddressByCustomerId()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching PresentAddress By CustomerId .Data[" + SUBMODULE.toString() + "]");
        CustomerAddressPojo customerAddressPojo = new CustomerAddressPojo();
        try {
            CustomerAddress presentcustomerAddress = customerAddressRepository.findByAddressTypeAndCustomerId("Present", customerId);
            customerAddressPojo = customerAddressMapper.domainToDTO(presentcustomerAddress, new CycleAvoidingMappingContext());
        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
        return customerAddressPojo;
    }

    public List<FieldsDTO> getFieldDetailsByParam(Long paramId) {
        String SUBMODULE = getModuleNameForLog() + " [getFieldDetailsByParam()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching FieldDetails By Param .Data[" + SUBMODULE.toString() + "]");
        List<FieldsDTO> fieldsDTOList = new ArrayList<>();
        try {
            List<FieldServiceParamMapping> fieldServiceParamMappings = fieldServiceParamMappingRepository.findAllByServiceParameterIdIn(Arrays.asList(paramId));
            List<Fields> fieldsList = fieldServiceParamMappings.stream().map(FieldServiceParamMapping::getFields).collect(Collectors.toList());
            List<Long> fieldsIdList = fieldsList.stream().map(Fields::getId).collect(Collectors.toList());
            List<ScreenFieldMapping> screenFieldMappingsList = screenFieldMappingRepository.findAllByFieldsIdInAndScreensId(fieldsIdList, 3L);

            for(ScreenFieldMapping screenFieldMapping : screenFieldMappingsList)
                fieldsDTOList.add(new FieldsDTO(screenFieldMapping));

        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
        return fieldsDTOList;
    }
}
