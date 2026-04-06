package com.adopt.apigw.modules.NetworkDevices.service;


import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.CommonList.utils.TypeConstants;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.QCustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.inward.QInward;
import com.adopt.apigw.modules.InventoryManagement.item.ItemDto;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.product.ProductServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.product.QProduct;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategory;
import com.adopt.apigw.modules.InventoryManagement.productCategory.QProductCategory;
import com.adopt.apigw.modules.NetworkDevices.domain.*;
//import com.adopt.apigw.modules.NetworkDevices.mapper.NetworkConvertor;
import com.adopt.apigw.modules.NetworkDevices.mapper.NetworkDeviceMapper;
import com.adopt.apigw.modules.NetworkDevices.mapper.SloatMapper.NetworkConvertor;
import com.adopt.apigw.modules.NetworkDevices.model.*;
import com.adopt.apigw.modules.NetworkDevices.repository.NetworkDeviceBindingsRepository;
import com.adopt.apigw.modules.NetworkDevices.repository.NetworkDeviceRepository;
import com.adopt.apigw.modules.NetworkDevices.repository.NetworkdeviceBindRepository;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.utils.CommonConstants;
import com.itextpdf.text.Document;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class NetworkDeviceService extends ExBaseAbstractService<NetworkDeviceDTO, NetworkDevices, Long> {

    public static final String MODULE = " [NetworkDeviceService] ";

    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;
    @Autowired
    private NetworkDeviceMapper networkDeviceMapper;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private NetworkdeviceBindRepository networkdeviceBindRepository;

    @Autowired
    NetworkConvertor networkConvertor;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private NetworkDeviceBindingsRepository networkDeviceBindingsRepository;

    public NetworkDeviceService(NetworkDeviceRepository repository, NetworkDeviceMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "deviceid");
        sortColMap.put("type", "devicetype");
        sortColMap.put("areaName", "name");
    }

    @Override
    public String getModuleNameForLog() {
        return "[NetworkDeviceService]";
    }


    public void UpdateNetworkDevice(NetworkDeviceDTO dto, List<Oltslots> oltSlots) {
        String SUBMODULE = MODULE + "[UpdateNetworkDevice()]";
        NetworkDevices networkDevices = new NetworkDevices();
        try {
            networkDevices.setId(dto.getId());
            networkDevices.setName(dto.getName());
            networkDevices.setDevicetype(dto.getDevicetype());
            ServiceArea serviceArea = serviceAreaMapper.dtoToDomain(dto.getServicearea(), new CycleAvoidingMappingContext());
            networkDevices.setServicearea(serviceArea);
            networkDevices.setOltslotsList(oltSlots);
            networkDevices.setStatus(dto.getStatus());
            networkDeviceRepository.save(networkDevices);

        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }
    }

    public List<NetworkDeviceDTO> getNetworkDevicesByServiceAreaId(Long serviceAreaId) {
        List<NetworkDevices> networkDevicesList = networkDeviceRepository.findByServiceareaIdAndIsDeletedIsFalse(serviceAreaId);
        // TODO: pass mvnoID manually 6/5/2025
        // TODO: pass mvnoID manually 6/5/2025
        List<NetworkDeviceDTO> networkDeviceDTOList = networkDevicesList.stream().filter(data -> data.getDevicetype().equalsIgnoreCase(TypeConstants.OLT) && data.getMvnoId() !=null || data.getMvnoId() == 1).map(data -> networkDeviceMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return networkDeviceDTOList;
    }

    public NetworkDevices findByNameAndDeviceType(String serviceName, String deviceType) {
        NetworkDevices networkDevices = new NetworkDevices();
        // TODO: pass mvnoID manually 6/5/2025
        List<NetworkDevices> networkDevicesList = networkDeviceRepository.findByNameAndDevicetypeAndIsDeletedIsFalse(serviceName, deviceType)
                .stream().filter(data -> data.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || data.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1).collect(Collectors.toList());

        if (null != networkDevicesList && 0 < networkDevicesList.size()) {
            networkDevices = networkDevicesList.get(0);
        }
        return networkDevices;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("NetworkDevice");
        createExcel(workbook, sheet, NetworkDeviceDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, NetworkDeviceDTO.class, null,mvnoId);
    }

    public GenericDataDTO getDeviceByNameOrTypeOrAreaName(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getDeviceByNameOrTypeOrAreaName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<NetworkDevices> networkDevicesList;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1)
                networkDevicesList = networkDeviceRepository.findAllByNameContainingIgnoreCaseOrDevicetypeContainingIgnoreCaseOrServicearea_NameContainingIgnoreCase(s1, s1, s1, pageRequest);
            else
                // TODO: pass mvnoID manually 6/5/2025
                networkDevicesList = networkDeviceRepository.findAllByNameContainingIgnoreCaseOrDevicetypeContainingIgnoreCaseOrServicearea_NameContainingIgnoreCaseAndMvnoIdIn(s1, s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (null != networkDevicesList && 0 < networkDevicesList.getSize()) {
                makeGenericResponse(genericDataDTO, networkDevicesList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        QNetworkDevices qNetworkDevices = QNetworkDevices.networkDevices;
        QProduct qProduct = QProduct.product;
        BooleanExpression booleanExpression = qNetworkDevices.isNotNull().and(qNetworkDevices.isDeleted.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qNetworkDevices.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        }
        try {
            PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (filterList.size() > 0) {
                for (GenericSearchModel genericSearchModel : filterList) {
                    String s1 = genericSearchModel.getFilterValue();
                    if (genericSearchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        if (!genericSearchModel.getFilterValue().isEmpty()) {
                            booleanExpression = booleanExpression.and((qNetworkDevices.name.likeIgnoreCase("%" + s1 + "%"))
                                    .or(qNetworkDevices.servicearea.name.likeIgnoreCase("%" + s1 + "%"))
                                    .or(qNetworkDevices.status.likeIgnoreCase("%" + s1 + "%"))
                                    .or(qNetworkDevices.devicetype.likeIgnoreCase("%" + s1 + "%")));
                        }
                    }
                    if (null != genericSearchModel.getFilterCondition()) {
                        if (genericSearchModel.getFilterColumn().equalsIgnoreCase("status")) {
                            booleanExpression = booleanExpression.and(qNetworkDevices.status.likeIgnoreCase("%" + s1 + "%"));
                        }
                        if (genericSearchModel.getFilterColumn().equalsIgnoreCase("devicetype")) {
                            booleanExpression = booleanExpression.and(qNetworkDevices.devicetype.likeIgnoreCase("%" + s1 + "%"));
                        }
                        if (genericSearchModel.getFilterColumn().equalsIgnoreCase("name")) {
                            booleanExpression = booleanExpression.and(qNetworkDevices.name.likeIgnoreCase("%" + s1 + "%"));
                        }
                        if (genericSearchModel.getFilterColumn().equalsIgnoreCase("ServiceArea")) {
                            booleanExpression = booleanExpression.and(qNetworkDevices.servicearea.name.likeIgnoreCase("%" + s1 + "%"));
                        }
                        if (genericSearchModel.getFilterColumn().equalsIgnoreCase("Product")) {
                            booleanExpression = booleanExpression.and(qNetworkDevices.product.name.likeIgnoreCase("%" + s1 + "%"));
                        }
                    }
                }
                Page<NetworkDevices> networkDevices = networkDeviceRepository.findAll(booleanExpression, pageRequest);
                if (null != networkDevices && 0 < networkDevices.getSize()) {
                    return makeGenericResponse(genericDataDTO, networkDevices);
                }
                if (networkDevices.getTotalElements() == 0) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                    genericDataDTO.setResponseMessage("Data Not Found.");
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return genericDataDTO;
    }

    public GenericDataDTO getByDeviceName(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getByDeviceName()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        QNetworkDevices qNetworkDevices = QNetworkDevices.networkDevices;
        BooleanExpression booleanExpression = qNetworkDevices.isNotNull().and(qNetworkDevices.isDeleted.eq(false))
                .and((qNetworkDevices.name.likeIgnoreCase("%" + s1 + "%")).or(qNetworkDevices.servicearea.name.likeIgnoreCase("%" + s1 + "%")).or(qNetworkDevices.status.likeIgnoreCase("%" + s1 + "%")));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qNetworkDevices.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        }
        Page<NetworkDevices> networkDevices = networkDeviceRepository.findAll(booleanExpression, pageRequest);
        if (null != networkDevices && 0 < networkDevices.getSize()) {
            makeGenericResponse(genericDataDTO, networkDevices);
        }
        if (networkDevices.getTotalElements() == 0) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage("Data Not Found.");
        }

        return genericDataDTO;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = networkDeviceRepository.duplicateVerifyAtSave(name);
            else
                // TODO: pass mvnoID manually 6/5/2025
                count = networkDeviceRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = networkDeviceRepository.duplicateVerifyAtEdit(name, id);
            else
                // TODO: pass mvnoID manually 6/5/2025
                count = networkDeviceRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = true;
        Integer count = networkDeviceRepository.deleteVerify(id);
        if (count == 0) {
            flag = false;
        }
        return flag;
    }

    @Override
    public NetworkDeviceDTO saveEntity(NetworkDeviceDTO entity) throws Exception {
        try {
            if (entity.getServiceAreaIdsList() != null) {
                //NetworkDevices networkDevices = new NetworkDevices();
//                networkDevices.setServiceAreaNameList(serviceAreaRepository.findAllById(entity.getServiceAreaIdsList()));
                List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllById(entity.getServiceAreaIdsList());
                List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                for (ServiceArea serviceArea : serviceAreaList) {
                    ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext());
                    serviceAreaDTOS.add(serviceAreaDTO);
                }
//                ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.domainToDTO((ServiceArea) serviceAreaRepository.findAllById(entity.getServiceAreaIdsList()), new CycleAvoidingMappingContext());
                entity.setServiceAreaNameList(serviceAreaDTOS);
                entity.setAvailablePorts(entity.getTotalPorts());
            }

//            if (entity.getAvailableInPorts() == null || entity.getTotalInPorts() == null || entity.getAvailableOutPorts() == null || entity.getTotalOutPorts() == null)
//                throw new RuntimeException("Please provide proper values ports");
//            if (entity.getAvailableInPorts() > entity.getTotalInPorts() || entity.getAvailableOutPorts() > entity.getTotalOutPorts())
//                throw new RuntimeException("Available ports should be less than total ports.");
            return super.saveEntity(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public NetworkDeviceDTO updateEntity(NetworkDeviceDTO entity) {
        try {
            getEntityForUpdateAndDelete(entity.getId(),entity.getMvnoId());
            if (entity.getServiceAreaIdsList() != null) {
                //NetworkDevices networkDevices = new NetworkDevices();
//                networkDevices.setServiceAreaNameList(serviceAreaRepository.findAllById(entity.getServiceAreaIdsList()));
                List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllById(entity.getServiceAreaIdsList());
                List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                for (ServiceArea serviceArea : serviceAreaList) {
                    ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext());
                    serviceAreaDTOS.add(serviceAreaDTO);
                }
//                ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.domainToDTO((ServiceArea) serviceAreaRepository.findAllById(entity.getServiceAreaIdsList()), new CycleAvoidingMappingContext());
                entity.setServiceAreaNameList(serviceAreaDTOS);
            }
//            if (entity.getAvailableInPorts() == null || entity.getTotalInPorts() == null || entity.getAvailableOutPorts() == null || entity.getTotalOutPorts() == null)
//                throw new RuntimeException("Please provide proper values ports");
//            if (entity.getAvailableInPorts() > entity.getTotalInPorts() || entity.getAvailableOutPorts() > entity.getTotalOutPorts())
//                throw new RuntimeException("Available ports should be less than total ports.");
            NetworkDeviceDTO dbParentNetworkDevice = null;
            NetworkDeviceDTO newParentNetworkDevice = null;

            return super.updateEntity(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    public Map<String, Object> getHierarchy(Long id) {
//        Map<String, Object> map = new HashMap<>();
//        try {
//            map.put("parent", networkDeviceBindingsRepository.findByDeviceId(id).stream().map(this::convertMappingToDTO));
//            map.put("children", networkDeviceBindingsRepository.findByParentDeviceId(id).stream().map(this::convertMappingToDTO));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return map;
//    }
    public Map<String, Object> getHierarchy(Long id) {
        Map<String, Object> map = new HashMap<>();
        try {
            List<NetworkDeviceBindDTO> parentList = networkdeviceBindRepository.findByCurrentDeviceId(id)
                    .stream()
                    .map(this::convertMappingToDTOSS)
                    .filter(dto -> dto.getPortType().equalsIgnoreCase("in")) // Filter by portType "in"
                    .collect(Collectors.toList());

            map.put("parent", parentList);

            List<NetworkDeviceBindDTO> childList = networkdeviceBindRepository.findByCurrentDeviceId(id)
                    .stream()
                    .map(this::convertMappingToDTOSS)
                    .filter(dto -> dto.getPortType().equalsIgnoreCase("OUT")) // Filter by portType "in"
                    .collect(Collectors.toList());
            map.put("children", childList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    @Override
    public List<NetworkDeviceDTO> getAllEntities(Integer mvnoId) {
        try {
//            Integer mvnoId =null;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            QNetworkDevices qNetworkDevices = QNetworkDevices.networkDevices;
            QNetworkDeviceServiceAreaMapping qNetworkDeviceServiceAreaMapping = QNetworkDeviceServiceAreaMapping.networkDeviceServiceAreaMapping;
            BooleanExpression aBoolean = qNetworkDevices.isNotNull().and(qNetworkDevices.isDeleted.eq(false));
            if (getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = super.getServiceAreaIdList(mvnoId).stream().map(Long::intValue).collect(Collectors.toList());
                aBoolean = aBoolean.and(qNetworkDevices.id.in(query.select(qNetworkDeviceServiceAreaMapping.deviceId).from(qNetworkDeviceServiceAreaMapping)
                        .where(qNetworkDeviceServiceAreaMapping.serviceIdList.in(serviceIDs))));
            }
            List<NetworkDevices> networkDevicesList = IterableUtils.toList(networkDeviceRepository.findAll(aBoolean));
            // TODO: pass mvnoID manually 6/5/2025
            return networkDevicesList.stream().map(data -> super.getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(networkDeviceDTO -> networkDeviceDTO.getMvnoId() == mvnoId || networkDeviceDTO.getMvnoId() == 1 || mvnoId == 1).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<NetworkDevices> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId == 1)
            paginationList = networkDeviceRepository.findAll(pageRequest);
        else
            // TODO: pass mvnoID manually 6/5/2025
            paginationList = networkDeviceRepository.findAll(pageRequest, Arrays.asList(mvnoId, 1));
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    public NetworkDevices getEntityToUpdate(Long id) {
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return networkDeviceRepository.getOne(id);
        else
            // TODO: pass mvnoID manually 6/5/2025
            return networkDeviceRepository.findByIdAndMvnoIdIn(id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
    }

    @Transactional
    public List<NetworkDeviceBindingsDTO> saveParentDeviceBindings(DeviceMappingDTO deviceMappingDTO) {
        try {
//      Port type - IN
            Set<Long> inPortParentIds = boundParents(deviceMappingDTO.getDeviceId()).stream().filter(networkDeviceBindingsDTO -> networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.IN)).map(NetworkDeviceBindingsDTO::getParentDeviceId).collect(Collectors.toSet());

            Set<Long> inDBSimilar = new HashSet<>(inPortParentIds);
            Set<Long> inDBDifferent = new HashSet<>(inPortParentIds);
            Set<Long> inNewDifferent = new HashSet<>(deviceMappingDTO.getInPortDevices());

            inDBSimilar.retainAll(deviceMappingDTO.getInPortDevices());
            inDBDifferent.removeAll(inDBSimilar);
            inNewDifferent.removeAll(inDBSimilar);

            // (+1) Release 'out ports' from inDBDifferent, They are the ids of devices which are replaced by others
            for (Long deviceId : inDBDifferent) {
                NetworkDeviceDTO networkDeviceDTO = networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(deviceId).get(),new CycleAvoidingMappingContext());
                networkDeviceDTO.setAvailableOutPorts(networkDeviceDTO.getAvailableOutPorts() + 1);
                saveEntity(networkDeviceDTO);
            }
            // Delete overriden mappings
            networkDeviceBindingsRepository.deleteByDeviceIdAndParentDeviceIdIn(deviceMappingDTO.getDeviceId(), inDBDifferent);

            // (-1) Bind 'out ports' of inNewDifferent, They are the new devices & devices who replaced existing devices
            for (Long deviceId : inNewDifferent) {
                NetworkDeviceDTO networkDeviceDTO = networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(deviceId).get(),new CycleAvoidingMappingContext());
                if (networkDeviceDTO.getAvailableOutPorts() == 0)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), networkDeviceDTO.getId() + "-" + networkDeviceDTO.getName() + MessageConstants.PORTS_UNAVAILABLE, null);
                networkDeviceDTO.setAvailableOutPorts(networkDeviceDTO.getAvailableOutPorts() - 1);
                saveEntity(networkDeviceDTO);

                // Save new mapping
                NetworkDeviceBindings networkDeviceBindings = new NetworkDeviceBindings(deviceMappingDTO.getDeviceId(), CommonConstants.IN, deviceId);
                networkDeviceBindingsRepository.save(networkDeviceBindings);
            }

//      Port type - OUT
            Set<Long> outPortParentIds = boundParents(deviceMappingDTO.getDeviceId()).stream().filter(networkDeviceBindingsDTO -> networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.OUT)).map(NetworkDeviceBindingsDTO::getParentDeviceId).collect(Collectors.toSet());

            Set<Long> outDBSimilar = new HashSet<>(outPortParentIds);
            Set<Long> outDBDifferent = new HashSet<>(outPortParentIds);
            Set<Long> outNewDifferent = new HashSet<>(deviceMappingDTO.getOutPortDevices());

            outDBSimilar.retainAll(deviceMappingDTO.getOutPortDevices());
            outDBDifferent.removeAll(outDBSimilar);
            outNewDifferent.removeAll(outDBSimilar);

            // (+1) Release 'in ports' from outDBDifferent, They are the ids of devices which are replaced by others
            for (Long deviceId : outDBDifferent) {
                NetworkDeviceDTO networkDeviceDTO = networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(deviceId).get(),new CycleAvoidingMappingContext());
                networkDeviceDTO.setAvailableInPorts(networkDeviceDTO.getAvailableInPorts() + 1);
                saveEntity(networkDeviceDTO);
            }
            // Delete overriden mappings
            networkDeviceBindingsRepository.deleteByDeviceIdAndParentDeviceIdIn(deviceMappingDTO.getDeviceId(), outDBDifferent);

            // (-1) Bind 'in ports' of outNewDifferent, They are the new devices & devices who replaced existing devices
            for (Long deviceId : outNewDifferent) {
                NetworkDeviceDTO networkDeviceDTO = networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(deviceId).get(),new CycleAvoidingMappingContext());
                if (networkDeviceDTO.getAvailableInPorts() == 0)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), networkDeviceDTO.getId() + "-" + networkDeviceDTO.getName() + MessageConstants.PORTS_UNAVAILABLE, null);
                networkDeviceDTO.setAvailableInPorts(networkDeviceDTO.getAvailableInPorts() - 1);
                saveEntity(networkDeviceDTO);

                // Save new mapping
                NetworkDeviceBindings networkDeviceBindings = new NetworkDeviceBindings(deviceMappingDTO.getDeviceId(), CommonConstants.OUT, deviceId);
                networkDeviceBindingsRepository.save(networkDeviceBindings);
            }

//  Update IN/OUT ports from current device
            NetworkDeviceDTO currentDevice =networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(deviceMappingDTO.getDeviceId()).get(),new CycleAvoidingMappingContext());
            currentDevice.setAvailableInPorts(currentDevice.getAvailableInPorts() + inDBDifferent.size() - inNewDifferent.size());
            currentDevice.setAvailableOutPorts(currentDevice.getAvailableOutPorts() + outDBDifferent.size() - outNewDifferent.size());
            saveEntity(currentDevice);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return boundParents(deviceMappingDTO.getDeviceId());
    }

    @Transactional
    public List<NetworkDeviceBindingsDTO> deviceChildParentBinding(DevicePortMappingDTO devicePortsBindings) {
        try {
            List<Long> devicesBindedToINPort = devicePortsBindings.getInPortDevices().stream().map(NetworkDevicePortsBinding::getParentDeviceId).collect(Collectors.toList());
            List<Long> devicesBindedToOUTPort = devicePortsBindings.getOutPortDevices().stream().map(NetworkDevicePortsBinding::getParentDeviceId).collect(Collectors.toList());
//      Port type - IN
            Set<Long> inPortParentIds = boundParents(devicePortsBindings.getDeviceId()).stream().filter(networkDeviceBindingsDTO -> networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.IN)).map(NetworkDeviceBindingsDTO::getParentDeviceId).collect(Collectors.toSet());

            Set<Long> inDBSimilar = new HashSet<>(inPortParentIds);
            Set<Long> inDBDifferent = new HashSet<>(inPortParentIds);
            Set<Long> inNewDifferent = new HashSet<>(devicesBindedToINPort);

            inDBSimilar.retainAll(devicesBindedToINPort);
            inDBDifferent.removeAll(inDBSimilar);
            inNewDifferent.removeAll(inDBSimilar);

            // (+1) Release 'out ports' from inDBDifferent, They are the ids of devices which are replaced by others
            for (Long deviceId : inDBDifferent) {
                NetworkDeviceDTO networkDeviceDTO = networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(deviceId).get(),new CycleAvoidingMappingContext());
                networkDeviceDTO.setAvailableOutPorts(networkDeviceDTO.getAvailableOutPorts() + 1);
                saveEntity(networkDeviceDTO);
            }
            // Delete overriden mappings
            networkDeviceBindingsRepository.deleteByDeviceIdAndParentDeviceIdIn(devicePortsBindings.getDeviceId(), inDBDifferent);

            // (-1) Bind 'out ports' of inNewDifferent, They are the new devices & devices who replaced existing devices
            for (Long deviceId : inNewDifferent) {
                NetworkDeviceDTO networkDeviceDTO = networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(deviceId).get(),new CycleAvoidingMappingContext());
                if (networkDeviceDTO.getAvailableOutPorts() == 0)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), networkDeviceDTO.getId() + "-" + networkDeviceDTO.getName() + MessageConstants.PORTS_UNAVAILABLE, null);
                networkDeviceDTO.setAvailableOutPorts(networkDeviceDTO.getAvailableOutPorts() - 1);
                saveEntity(networkDeviceDTO);

                // Here check inbind and outbind duplicates(duplicates should not enter coz 1 port has 1 device attached)
                List<NetworkDevicePortsBinding> networkDevicePortsBindings = devicePortsBindings.getInPortDevices().stream().filter(devicePortsBinding -> devicePortsBinding.getParentDeviceId() == deviceId).collect(Collectors.toList());
                Boolean isDeviceBinded = false;
                isDeviceBinded = isPortAvailable(devicePortsBindings.getDeviceId(), networkDevicePortsBindings.get(0).getInBind(), CommonConstants.IN);
                if (isDeviceBinded) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.PORT_OCCUPIED, null);
                }
                isDeviceBinded = isPortAvailable(deviceId, networkDevicePortsBindings.get(0).getOutBind(), CommonConstants.OUT);
                if (isDeviceBinded) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.PORT_OCCUPIED, null);
                }

                // Save new mapping
                NetworkDeviceBindings networkDeviceBindings = new NetworkDeviceBindings(devicePortsBindings.getDeviceId(), CommonConstants.IN, deviceId, networkDevicePortsBindings.get(0).getInBind(), networkDevicePortsBindings.get(0).getOutBind());
                networkDeviceBindingsRepository.save(networkDeviceBindings);
            }

//      Port type - OUT
            Set<Long> outPortParentIds = boundParents(devicePortsBindings.getDeviceId()).stream().filter(networkDeviceBindingsDTO -> networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.OUT)).map(NetworkDeviceBindingsDTO::getParentDeviceId).collect(Collectors.toSet());

            Set<Long> outDBSimilar = new HashSet<>(outPortParentIds);
            Set<Long> outDBDifferent = new HashSet<>(outPortParentIds);
            Set<Long> outNewDifferent = new HashSet<>(devicesBindedToOUTPort);

            outDBSimilar.retainAll(devicesBindedToOUTPort);
            outDBDifferent.removeAll(outDBSimilar);
            outNewDifferent.removeAll(outDBSimilar);

            // (+1) Release 'in ports' from outDBDifferent, They are the ids of devices which are replaced by others
            for (Long deviceId : outDBDifferent) {
                NetworkDeviceDTO networkDeviceDTO = networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(deviceId).get(),new CycleAvoidingMappingContext());
                networkDeviceDTO.setAvailableInPorts(networkDeviceDTO.getAvailableInPorts() + 1);
                saveEntity(networkDeviceDTO);
            }
            // Delete overriden mappings
            networkDeviceBindingsRepository.deleteByDeviceIdAndParentDeviceIdIn(devicePortsBindings.getDeviceId(), outDBDifferent);

            // (-1) Bind 'in ports' of outNewDifferent, They are the new devices & devices who replaced existing devices
            for (Long deviceId : outNewDifferent) {
                NetworkDeviceDTO networkDeviceDTO = networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(deviceId).get(),new CycleAvoidingMappingContext());
                if (networkDeviceDTO.getAvailableInPorts() == 0)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), networkDeviceDTO.getId() + "-" + networkDeviceDTO.getName() + MessageConstants.PORTS_UNAVAILABLE, null);
                networkDeviceDTO.setAvailableInPorts(networkDeviceDTO.getAvailableInPorts() - 1);
                saveEntity(networkDeviceDTO);

                // Here check inbind and outbind duplicates(duplicates should not enter coz 1 port has 1 device attached)
                List<NetworkDevicePortsBinding> networkDevicePortsBindings = devicePortsBindings.getOutPortDevices().stream().filter(devicePortsBinding -> devicePortsBinding.getParentDeviceId() == deviceId).collect(Collectors.toList());
                Boolean isDeviceBinded = false;
//                isDeviceBinded = isPortAvailable(devicePortsBindings.getDeviceId(), networkDevicePortsBindings.get(0).getOutBind(), CommonConstants.OUT);
                isDeviceBinded = isPortAvailable(devicePortsBindings.getDeviceId(), networkDevicePortsBindings.get(0).getInBind(), CommonConstants.IN);
                if (isDeviceBinded)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.PORT_OCCUPIED, null);
                isDeviceBinded = isPortAvailable(deviceId, networkDevicePortsBindings.get(0).getOutBind(), CommonConstants.OUT);
                if (isDeviceBinded)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.PORT_OCCUPIED, null);

                // Save new mapping
                NetworkDeviceBindings networkDeviceBindings = new NetworkDeviceBindings(devicePortsBindings.getDeviceId(), CommonConstants.OUT, deviceId, networkDevicePortsBindings.get(0).getInBind(), networkDevicePortsBindings.get(0).getOutBind());
                networkDeviceBindingsRepository.save(networkDeviceBindings);
            }

//  Update IN/OUT ports from current device
            NetworkDeviceDTO currentDevice = networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(devicePortsBindings.getDeviceId()).get(),new CycleAvoidingMappingContext());
            currentDevice.setAvailableInPorts(currentDevice.getAvailableInPorts() + inDBDifferent.size() - inNewDifferent.size());
            currentDevice.setAvailableOutPorts(currentDevice.getAvailableOutPorts() + outDBDifferent.size() - outNewDifferent.size());
            saveEntity(currentDevice);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return boundParents(devicePortsBindings.getDeviceId());
    }

    private boolean isPortAvailable(Long deviceId, String portName, String portType) {
        NetworkDeviceBindings networkDeviceBindings = null;
        if (portType.equalsIgnoreCase(CommonConstants.IN))
            networkDeviceBindings = networkDeviceBindingsRepository.findByDeviceIdAndInBind(deviceId, portName);
        else
            networkDeviceBindings = networkDeviceBindingsRepository.findByParentDeviceIdAndOutBind(deviceId, portName);
        if (networkDeviceBindings != null)
            return true;
        return false;
    }

    public List<NetworkDeviceBindingsDTO> boundParents(Long id) {
        List<NetworkDeviceBindingsDTO> networkDeviceBindingsList = new ArrayList<>();
        networkDeviceBindingsList.addAll(networkDeviceBindingsRepository.findByDeviceId(id).stream().map(this::convertMappingToDTO).collect(Collectors.toList()));
        List<NetworkDeviceBindingsDTO> parents = networkDeviceBindingsRepository.findByParentDeviceId(id).stream().map(this::convertMappingToDTO).collect(Collectors.toList());
        List<NetworkDeviceBindingsDTO> reversedParents = new ArrayList<>();
        for (NetworkDeviceBindingsDTO networkDeviceBindingsDTO : parents) {
            NetworkDeviceBindingsDTO reversedParent = new NetworkDeviceBindingsDTO();
            reversedParent.setId(networkDeviceBindingsDTO.getId());
            reversedParent.setPortType(networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.IN) ? CommonConstants.OUT : CommonConstants.IN);
            reversedParent.setOutBind(networkDeviceBindingsDTO.getInBind());
            reversedParent.setInBind(networkDeviceBindingsDTO.getOutBind());
            reversedParent.setDeviceId(networkDeviceBindingsDTO.getParentDeviceId());
            reversedParent.setParentDeviceId(networkDeviceBindingsDTO.getDeviceId());
            reversedParent.setDeviceName(networkDeviceBindingsDTO.getParentDeviceName());
            reversedParent.setParentDeviceName(networkDeviceBindingsDTO.getDeviceName());
            reversedParents.add(reversedParent);
        }
        networkDeviceBindingsList.addAll(reversedParents);
//        networkDeviceBindingsList.addAll(networkDeviceBindingsRepository.findByParentDeviceId(id).stream(c).map(this::convertMappingToDTO).collect(Collectors.toList()));
        return networkDeviceBindingsList;
    }

    public List<NetworkDeviceDTO> availableParents(Long id,Integer mvnoId) {
        List<NetworkDeviceDTO> networkDevices = getAllEntities(mvnoId).stream().filter(networkDeviceDTO -> networkDeviceDTO.getId() != id).collect(Collectors.toList());
        List<Long> parentIds = boundParents(id).stream().map(NetworkDeviceBindingsDTO::getParentDeviceId).collect(Collectors.toList());
        List<Long> childIds = boundParents(id).stream().map(NetworkDeviceBindingsDTO::getDeviceId).collect(Collectors.toList());
        // Ignores already mapped entries
        networkDevices = networkDevices.stream().filter(networkDevice -> !parentIds.contains(networkDevice.getId()))
                .filter(networkDevice -> !childIds.contains(networkDevice.getId())).collect(Collectors.toList());
        return networkDevices;
    }

    public String deleteDeviceMapping(Long id) {
        try {
            networkDeviceBindingsRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Device mapping deleted successfully.";
    }

    NetworkDeviceBindingsDTO convertMappingToDTO(NetworkDeviceBindings networkDeviceBindings) {
        NetworkDeviceBindingsDTO networkDeviceBindingsDTO = new NetworkDeviceBindingsDTO();
        networkDeviceBindingsDTO.setId(networkDeviceBindings.getId());
        networkDeviceBindingsDTO.setDeviceId(networkDeviceBindings.getDeviceId());
        networkDeviceBindingsDTO.setDeviceName(networkDeviceRepository.findById(networkDeviceBindings.getDeviceId()).get().getName());
        networkDeviceBindingsDTO.setParentDeviceId(networkDeviceBindings.getParentDeviceId());
        networkDeviceBindingsDTO.setParentDeviceName(networkDeviceRepository.findById(networkDeviceBindings.getParentDeviceId()).get().getName());
        networkDeviceBindingsDTO.setPortType(networkDeviceBindings.getPortType());
        networkDeviceBindingsDTO.setInBind(networkDeviceBindings.getInBind());
        networkDeviceBindingsDTO.setOutBind(networkDeviceBindings.getOutBind());
        return networkDeviceBindingsDTO;
    }

    public Set<String> getPortsAvailability(Long parentDeviceId) {
        try {
            NetworkDeviceDTO networkDeviceDTO = networkDeviceMapper.domainToDTO(networkDeviceRepository.findById(parentDeviceId).get(),new CycleAvoidingMappingContext());
            Set<String> ports = new HashSet<>();
            Integer totalPorts;

            if (networkDeviceDTO != null) {
                totalPorts = networkDeviceDTO.getAvailablePorts();
                for (int i = 1; i <= totalPorts; i++)
                    ports.add(networkDeviceDTO.getName()+"-Port-" + i);
                Set<String> deviceIdIN = networkDeviceBindingsRepository.findByDeviceId(parentDeviceId).stream().filter(networkDeviceDTO2 -> networkDeviceDTO2.getPortType().equalsIgnoreCase(CommonConstants.IN)).map(NetworkDeviceBindings::getInBind).collect(Collectors.toSet());
                Set<String> parentDeviceIdOUT = networkDeviceBindingsRepository.findByParentDeviceId(parentDeviceId).stream().filter(networkDeviceDTO2 -> networkDeviceDTO2.getPortType().equalsIgnoreCase(CommonConstants.OUT)).map(NetworkDeviceBindings::getInBind).collect(Collectors.toSet());
                ports.removeAll(deviceIdIN);
                ports.removeAll(parentDeviceIdOUT);
                return ports;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Inward> getAllInwardByProduct(Long productId) {
        QInward qInward = QInward.inward;
        JPAQuery<Inward> query = new JPAQuery<>(entityManager);
        List<Inward> inwardList = new ArrayList<>();
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.productId.id.eq(productId)).and(qInward.sourceType.equalsIgnoreCase(CommonConstants.STAFF).or(qInward.sourceType.equalsIgnoreCase(CommonConstants.PARTNER)))
                .and(qInward.isDeleted.eq(false)).and((qInward.productId.productCategory.type.eq(CommonConstants.CUSTOMER_BIND)
                        .or(qInward.productId.productCategory.type.eq(CommonConstants.NETWORK_BIND))));
        List<Tuple> result = query.select(qInward.id, qInward.inwardNumber, qInward.unusedQty, qInward.mvnoId).from(qInward).where(booleanExpression).fetch();
        if (!result.isEmpty()) {
            result.forEach(tuple -> {
                Inward inward = new Inward();
                inward.setId(tuple.get(qInward.id));
                inward.setInwardNumber(tuple.get(qInward.inwardNumber));
                inward.setUnusedQty(tuple.get(qInward.unusedQty));
                inward.setMvnoId(tuple.get(qInward.mvnoId));
                inwardList.add(inward);
            });
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return inwardList;
        else
            // TODO: pass mvnoID manually 6/5/2025
            return inwardList.stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
    }

    public GenericDataDTO getAllProduct() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            QProduct qProduct = QProduct.product;
            BooleanExpression booleanExpression = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false).and((qProduct.productCategory.type.eq(CommonConstants.CUSTOMER_BIND)
                    .or(qProduct.productCategory.type.eq(CommonConstants.NETWORK_BIND)))));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            genericDataDTO.setDataList(IterableUtils.toList(this.productRepository.findAll(booleanExpression)));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }

    public GenericDataDTO searchNetworkDevices(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchNetworkDevicesPojo searchNetworkDevicesPojo) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (searchNetworkDevicesPojo != null) {
                genericDataDTO = findNetworkDevices(pageNumber, customPageSize, sortBy, sortOrder, searchNetworkDevicesPojo);
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    public GenericDataDTO findNetworkDevicesByType(String deviceType) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        PageRequest pageRequest;
//        Page<NetworkDevices> paginationList = null;
//        QNetworkDevices qNetworkDevices = QNetworkDevices.networkDevices;
        List<NetworkDeviceDTO> networkDeviceDTOList = new ArrayList<>();
//        BooleanExpression booleanExpression = qNetworkDevices.isNotNull().and(qNetworkDevices.isDeleted.eq(false));
        try {
//            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            if (getMvnoIdFromCurrentStaff() != 1)
//                booleanExpression = booleanExpression.and(qNetworkDevices.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//            if (deviceType != null && !"null".equals(deviceType) && !"".equals(deviceType)) {
//                booleanExpression = booleanExpression.and(qNetworkDevices.devicetype.equalsIgnoreCase(deviceType));
//            }
//            booleanExpression = booleanExpression.and(qNetworkDevices.status.startsWithIgnoreCase(search.getStatus()));
//            if (search.getServiceName() != null && !"null".equals(search.getServiceName()) && !"".equals(search.getServiceName())) {
//                booleanExpression = booleanExpression.and(qNetworkDevices.servicearea.name.startsWithIgnoreCase(search.getServiceName()));
//            }
            List<NetworkDevices> networkDevices = networkDeviceRepository.findAllByIsDeletedFalseAndDevicetypeAndStatus(deviceType, "Active");
            List<NetworkDeviceDTO> dto = networkDevices.stream().map(networkDevice -> networkDeviceMapper.domainToDTO(networkDevice, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            for (NetworkDeviceDTO networkDeviceDTO : dto) {
                if (networkDeviceDTO.getProductId() != null) {
                    networkDeviceDTO.setProductName(productRepository.findById(networkDeviceDTO.getProductId()).get().getName());
                    networkDeviceDTOList.add(networkDeviceDTO);
                }
            }
            if (networkDeviceDTOList.size() > 0) {
                genericDataDTO.setDataList(networkDeviceDTOList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Data Not Found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    public GenericDataDTO findNetworkDevices(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchNetworkDevicesPojo search) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<NetworkDevices> paginationList = null;
        QNetworkDevices qNetworkDevices = QNetworkDevices.networkDevices;
        List<NetworkDeviceDTO> networkDeviceDTOList = new ArrayList<>();
        BooleanExpression booleanExpression = qNetworkDevices.isNotNull().and(qNetworkDevices.isDeleted.eq(false));
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qNetworkDevices.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            if (search.getDevicetype() != null && !"null".equals(search.getDevicetype()) && !"".equals(search.getDevicetype())) {
                booleanExpression = booleanExpression.and(qNetworkDevices.devicetype.startsWithIgnoreCase(search.getDevicetype()));
            }
            if (search.getName() != null && !"null".equals(search.getName()) && !"".equals(search.getName())) {
                booleanExpression = booleanExpression.and(qNetworkDevices.name.startsWithIgnoreCase(search.getName()));
            }
            if (search.getProductName() != null && !"null".equals(search.getProductName()) && !"".equals(search.getProductName())) {
                booleanExpression = booleanExpression.and(qNetworkDevices.product.name.startsWithIgnoreCase(search.getProductName()));
            }
            if (search.getStatus() != null && !"null".equals(search.getStatus()) && !"".equals(search.getStatus())) {
                booleanExpression = booleanExpression.and(qNetworkDevices.status.startsWithIgnoreCase(search.getStatus()));
            }
            if (search.getServiceName() != null && !"null".equals(search.getServiceName()) && !"".equals(search.getServiceName())) {
                booleanExpression = booleanExpression.and(qNetworkDevices.servicearea.name.startsWithIgnoreCase(search.getServiceName()));
            }
            paginationList = networkDeviceRepository.findAll(booleanExpression, pageRequest);
            List<NetworkDeviceDTO> dto = paginationList.get().map(networkDevices -> networkDeviceMapper.domainToDTO(networkDevices, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            for (NetworkDeviceDTO networkDeviceDTO : dto) {
                if (networkDeviceDTO.getProductId() != null) {
                    networkDeviceDTO.setProductName(productRepository.findById(networkDeviceDTO.getProductId()).get().getName());
                    networkDeviceDTOList.add(networkDeviceDTO);
                }
            }
            if (null != paginationList && 0 < paginationList.getSize()) {
                genericDataDTO.setDataList(networkDeviceDTOList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(paginationList.getTotalElements());
                genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
                genericDataDTO.setTotalPages(paginationList.getTotalPages());
            } else if (paginationList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Data Not Found.");
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    public NetworkDeviceBind saveNetworks(NetworkDeviceBindDTO dataStoreMappingDto) {
        NetworkDeviceBind currentDeviceMapping = networkConvertor.convertDtoToEntity(dataStoreMappingDto);
        currentDeviceMapping.setCurrentDevicePort(dataStoreMappingDto.getCurrentDevicePort());
        currentDeviceMapping.setPortType(dataStoreMappingDto.getPortType());

        NetworkDeviceBind otherDeviceMapping = networkConvertor.convertDtoToEntity(dataStoreMappingDto);
        otherDeviceMapping.setCurrentDeviceId(dataStoreMappingDto.getOtherDeviceId());
        otherDeviceMapping.setOtherDevicePort(dataStoreMappingDto.getCurrentDevicePort());
        otherDeviceMapping.setCurrentDevicePort(dataStoreMappingDto.getOtherDevicePort());
        otherDeviceMapping.setOtherDeviceId(dataStoreMappingDto.getCurrentDeviceId());
        otherDeviceMapping.setMappingId(currentDeviceMapping.getMappingId());

        if(otherDeviceMapping.getPortType().equalsIgnoreCase("in")) {
            otherDeviceMapping.setPortType("out");
        }
        else
        {
            otherDeviceMapping.setPortType("in");
        }
        otherDeviceMapping.setMappingId(dataStoreMappingDto.getMappingId());

        Optional<NetworkDevices> currentDevice = networkDeviceRepository.findById(currentDeviceMapping.getCurrentDeviceId());
        currentDevice.ifPresent(device -> {
            int availablePorts = device.getAvailablePorts();
            // int currentDevicePort = currentDeviceMapping.getCurrentDevicePort();
            int newAvailablePorts = Math.max(availablePorts - 1, 1);
            device.setAvailablePorts(newAvailablePorts);
            // device.setAvailablePorts(currentDeviceMapping.getCurrentDevicePort() - 1);
            NetworkDeviceDTO currentDeviceDTO = networkDeviceMapper.domainToDTO(device, new CycleAvoidingMappingContext());
            try {
                saveEntity(currentDeviceDTO);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        Optional<NetworkDevices> otherDevice = networkDeviceRepository.findById(currentDeviceMapping.getOtherDeviceId());
        otherDevice.ifPresent(device -> {
            int availablePorts = device.getAvailablePorts();
            int newAvailablePorts = Math.max(availablePorts - 1, 1);
            device.setAvailablePorts(newAvailablePorts);
            NetworkDeviceDTO otherDeviceDTO = networkDeviceMapper.domainToDTO(device, new CycleAvoidingMappingContext());
            try {
                saveEntity(otherDeviceDTO);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });

        NetworkDeviceBind networkDeviceBind = networkdeviceBindRepository.findTopByOrderByIdDesc();
        if(networkDeviceBind==null){
            currentDeviceMapping.setMappingId(1);
            otherDeviceMapping.setMappingId(1);
            networkdeviceBindRepository.save(currentDeviceMapping);
            networkdeviceBindRepository.save(otherDeviceMapping);
        }
        else {
            currentDeviceMapping.setMappingId(networkDeviceBind.getMappingId()+1);
            otherDeviceMapping.setMappingId(networkDeviceBind.getMappingId()+1);
            networkdeviceBindRepository.save(currentDeviceMapping);
            networkdeviceBindRepository.save(otherDeviceMapping);

        }

        return currentDeviceMapping;
    }
    NetworkDeviceBindDTO convertMappingToDTOS(NetworkDeviceBind networkDeviceBindings){
        NetworkDeviceBindDTO networkDeviceBindingsDTO = new NetworkDeviceBindDTO();
        networkDeviceBindingsDTO.setId(networkDeviceBindings.getId());
        networkDeviceBindingsDTO.setCurrentDeviceId(networkDeviceBindings.getCurrentDeviceId());
        networkDeviceBindingsDTO.setPortType(networkDeviceBindings.getPortType());
        networkDeviceBindingsDTO.setOtherDeviceId(networkDeviceBindings.getOtherDeviceId());
        networkDeviceBindingsDTO.setMappingId(networkDeviceBindings.getMappingId());
        networkDeviceBindingsDTO.setOtherDevicePort(networkDeviceBindings.getOtherDevicePort());
        networkDeviceBindingsDTO.setCurrentDevicePort(networkDeviceBindings.getCurrentDevicePort());
        return networkDeviceBindingsDTO;
    }
    NetworkDeviceBindDTO convertMappingToDTOSS(NetworkDeviceBind networkDeviceBindings){
        NetworkDeviceBindDTO networkDeviceBindingsDTO = new NetworkDeviceBindDTO();
        networkDeviceBindingsDTO.setId(networkDeviceBindings.getId());
        networkDeviceBindingsDTO.setDeviceName(networkDeviceRepository.findById(networkDeviceBindings.getCurrentDeviceId()).get().getName());
        networkDeviceBindingsDTO.setParentDeviceName(networkDeviceRepository.findById(networkDeviceBindings.getOtherDeviceId()).get().getName());
        networkDeviceBindingsDTO.setCurrentDeviceId(networkDeviceBindings.getCurrentDeviceId());
        networkDeviceBindingsDTO.setPortType(networkDeviceBindings.getPortType());
        networkDeviceBindingsDTO.setOtherDeviceId(networkDeviceBindings.getOtherDeviceId());
        networkDeviceBindingsDTO.setMappingId(networkDeviceBindings.getMappingId());
        networkDeviceBindingsDTO.setOtherDevicePort(networkDeviceBindings.getOtherDevicePort());
        networkDeviceBindingsDTO.setCurrentDevicePort(networkDeviceBindings.getCurrentDevicePort());
        return networkDeviceBindingsDTO;
    }
    public List<NetworkDeviceBindDTO> getAllMappingData(Long id){
        List<NetworkDeviceBindDTO> networkDeviceBindingsList = new ArrayList<>();
        networkDeviceBindingsList.addAll(networkdeviceBindRepository.findByCurrentDeviceId(id).stream().map(this::convertMappingToDTOS).collect(Collectors.toList()));
        List<NetworkDeviceBindDTO> parents = networkdeviceBindRepository.findByCurrentDeviceId(id).stream().map(this::convertMappingToDTOS).collect(Collectors.toList());
        List<NetworkDeviceBindDTO> reversedParents = new ArrayList<>();
        for(NetworkDeviceBindDTO networkDeviceBindingsDTO : parents){
            NetworkDeviceBindDTO reversedParent = new NetworkDeviceBindDTO();
            reversedParent.setId(networkDeviceBindingsDTO.getId());
            reversedParent.setPortType(networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.IN) ? CommonConstants.OUT : CommonConstants.IN);
            reversedParent.setCurrentDevicePort(networkDeviceBindingsDTO.getCurrentDevicePort());
            reversedParent.setCurrentDeviceId(networkDeviceBindingsDTO.getCurrentDeviceId());
            reversedParent.setOtherDeviceId(networkDeviceBindingsDTO.getOtherDeviceId());
            reversedParent.setOtherDevicePort(networkDeviceBindingsDTO.getOtherDevicePort());
            reversedParent.setMappingId(networkDeviceBindingsDTO.getMappingId());
            reversedParents.add(reversedParent);
        }
        networkDeviceBindingsList.addAll(reversedParents);
//        networkDeviceBindingsList.addAll(networkDeviceBindingsRepository.findByParentDeviceId(id).stream(c).map(this::convertMappingToDTO).collect(Collectors.toList()));
        return networkDeviceBindingsList;
    }

}
