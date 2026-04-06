package com.adopt.apigw.service.common;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveClientServMessge;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SyncClientServiceMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateClientServMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.ClientServiceMapper;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.pojo.ClientServicePojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.ClientServiceMessage;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;

import io.swagger.models.auth.In;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.adopt.apigw.core.utillity.log.ApplicationLogger.logger;

@Service
public class ClientServiceSrv extends AbstractService<ClientService, ClientServicePojo, Integer> {

    @Autowired
    private ClientServiceRepository entityRepository;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private ClientServiceMapper clientServiceMapper;
    
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    CreateDataSharedService createDataSharedService;
    @Autowired
    CacheService cacheService;

    @Autowired
    private MvnoRepository mvnoRepository;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<ClientService, Integer> getRepository() {
        return entityRepository;
    }

    public ClientService searchByName(String name) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findByNameAndMvnoId(name,getLoggedInMvnoId());
    }

    public List<ClientService> getAllEntity() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAll().stream().filter(clientService -> clientService.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || clientService.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
    }

    public List<ClientService> saveAllEntity(List<ClientService> list) {
        return entityRepository.saveAll(list);
    }

    public ClientService getByName(String name) {
// TODO: pass mvnoID manually 6/5/2025
        return entityRepository.getByNameAndMvnoId(name, getMvnoIdFromCurrentStaff(null));
    }
    public ClientService getByNameAndMvnoId(String name,Integer mvnoId) {

        return entityRepository.getByNameAndMvnoId(name, mvnoId);
    }
    public ClientService getCurrencyByName(String name) {
// TODO: pass mvnoID manually 6/5/2025
        return entityRepository.getByNameAndMvnoIdEquals(name, getMvnoIdFromCurrentStaff(null));
    }
    public ClientService getCurrencyByNameAadMvnoId(String name,Integer mvnoId) {
// TODO: pass mvnoID manually 6/5/2025
        return entityRepository.getByNameAndMvnoIdEquals(name, mvnoId);
    }

    public ClientServicePojo getByClientServicePojoName(String name,Integer mvnoId) {
        // TODO: pass mvnoID manually 6/5/2025
        return clientServiceMapper.domainToDTO(entityRepository.getByNameAndMvnoId(name, mvnoId), new
                CycleAvoidingMappingContext());
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.ClientService', '1')")
    public List<ClientServicePojo> convertResponseModelIntoPojo(List<ClientService> clinetServiceList) throws Exception {
        List<ClientServicePojo> pojoListRes = new ArrayList<ClientServicePojo>();
        if (clinetServiceList != null && clinetServiceList.size() > 0) {
            for (ClientService clientServ : clinetServiceList) {
                pojoListRes.add(convertConfigurationModelToConfigurationPojo(clientServ));
            }
        }
        return pojoListRes;
    }

    public ClientServicePojo convertConfigurationModelToConfigurationPojo(ClientService clientService) throws Exception {
        ClientServicePojo pojo = null;
        if (clientService != null) {
            pojo = new ClientServicePojo();
            pojo.setId(clientService.getId());
            pojo.setName(clientService.getName());
            pojo.setValue(clientService.getValue());
            if(clientService.getMvnoId() != null) {
            	pojo.setMvnoId(clientService.getMvnoId());
            }
        }
        return pojo;
    }


    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.ClientService', '2')")
    public ClientServicePojo save(ClientServicePojo pojo, Integer mvnoId) throws Exception {
        ClientService oldObj = null;
        if (pojo.getId() != null) {
            oldObj = get(pojo.getId(),mvnoId);
        }
        // TODO: pass mvnoID manually 6/5/2025
        pojo.setMvnoId(mvnoId);
        ClientService obj = convertClientServicePojoToClientServiceModel(pojo);
        log.info("ClientService update details " + UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
        obj = saveClientService(obj);
        pojo = convertConfigurationModelToConfigurationPojo(obj);
        createDataSharedService.sendEntitySaveDataForAllMicroService(obj);
        return pojo;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.ClientService', '2')")
    public ClientServicePojo update(ClientServicePojo pojo,Integer mvnoId) throws Exception {
//        ClientService oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
        // TODO: pass mvnoID manually 6/5/2025
        pojo.setMvnoId(mvnoId);
        ClientService obj = convertClientServicePojoToClientServiceModel(pojo);
        getEntityForUpdateAndDelete(pojo.getId(),mvnoId);
//        if(oldObj!=null) {
//            log.info("ClientService update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
//        }
        obj = saveClientService(obj);
        pojo = convertConfigurationModelToConfigurationPojo(obj);
        ClientService save = entityRepository.save(obj);
        String cacheKey = cacheKeys.CLIENTSERVICE + save.getName() ; // Create a unique cache key
        cacheService.saveOrUpdateInCacheAsync(save,cacheKey);

        String valueToCache = save.getValue();
        String cacheKey_name = cacheKeys.CLIENTSERVICE_NAME_MVNO + save.getName() + "_" + save.getMvnoId();
        cacheService.saveOrUpdateInCacheAsync(obj.getName(),cacheKey_name);
        createDataSharedService.updateEntityDataForAllMicroService(obj);
        return pojo;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.ClientService', '2')")
    public ClientService saveClientService(ClientService clientService) throws Exception {
        // String operation="edit";
        // if(clientService !=null && clientService.getId()==null){
        //  operation = "add";
        //}
//    	if(getMvnoIdFromCurrentStaff() != null) {
//    		 clientService.setMvnoId(getMvnoIdFromCurrentStaff());
//     	}
        ClientService save = entityRepository.save(clientService);
        String cacheKey = cacheKeys.CLIENTSERVICE + save.getName() ; // Create a unique cache key
        cacheService.saveOrUpdateInCacheAsync(save,cacheKey);

        String valueToCache = save.getValue();
        String cacheKey_name = cacheKeys.CLIENTSERVICE_NAME_MVNO + save.getName() + "_" + save.getMvnoId();
        cacheService.saveOrUpdateInCacheAsync(valueToCache,cacheKey_name);
        //send message
        ClientServiceMessage clientServiceMessage = new ClientServiceMessage(save.getId(),save.getName(),save.getValue(),save.getMvnoId());
      kafkaMessageSender.send(new KafkaMessageData(clientServiceMessage, ClientServiceMessage.class.getSimpleName()));
//        messageSender.send(clientServiceMessage, RabbitMqConstants.QUEUE_CLIENT_SERVICE_UPDATE);

return save;
    }

    public ClientService convertClientServicePojoToClientServiceModel(ClientServicePojo clientServicePojo) throws Exception {
        ClientService clientService = null;
        if (clientServicePojo != null) {
            clientService = new ClientService();
            if (clientServicePojo.getId() != null) {
                clientService.setId(clientServicePojo.getId());
            }
            clientService.setName(clientServicePojo.getName());
            clientService.setValue(clientServicePojo.getValue());
            if(clientServicePojo.getMvnoId() != null) {
            	clientService.setMvnoId(clientServicePojo.getMvnoId());
            }
        }
        return clientService;
    }

   // @Cacheable(cacheNames = "clientSrv", key = "#name")
    public List<ClientServicePojo> getClientSrvByNameLight(String name) {
        List<Integer> mvnoIds = new ArrayList<>();
        mvnoIds.add(1);
        // TODO: pass mvnoID manually 6/5/2025
        mvnoIds.add(getMvnoIdFromCurrentStaff(null));
        return entityRepository.findAll()
                .stream()
                .filter(data -> data.getName().
                equalsIgnoreCase(name))
                .map(data -> clientServiceMapper.domainToDTO
                (data, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList())
                .stream()
                .filter(clientServicePojo -> clientServicePojo.getMvnoId() == 1|| clientServicePojo.getMvnoId().equals(mvnoIds.get(1)) || mvnoIds.get(0) == 1 ).collect(Collectors.toList());
    }


    public List<ClientServicePojo> getClientSrvByName(String name) {
        List<Integer> mvnoIds = new ArrayList<>();
        List<ClientServicePojo> servicePojoList = new ArrayList<>();
        mvnoIds.add(1);
        // TODO: pass mvnoID manually 6/5/2025
        mvnoIds.add(getMvnoIdFromCurrentStaff(null));
        List<ClientService> clientServicePojoList = entityRepository.findAllByNameAndMvnoIdIn(name,mvnoIds);
        for(ClientService clientService : clientServicePojoList){
            ClientServicePojo pojo = new ClientServicePojo();
            pojo = convertToClientServicePojo(clientService);
            servicePojoList.add(pojo);
        }

        return servicePojoList;
    }

    @Cacheable(cacheNames = "clientSrv")
    public List<ClientServicePojo> getAllClientSrv() {
        return entityRepository.findAll().stream().map(data -> clientServiceMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public void validateRequest(ClientServicePojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getId() != null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
        }

        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Client Service");
        List<ClientServicePojo> clientServicePojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, ClientServicePojo.class, clientServicePojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<ClientServicePojo> clientServicePojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, ClientServicePojo.class, clientServicePojos, null);
    }

    public String getValueByName(String name) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findValueByNameandMvnoId(name,getLoggedInMvnoId(null));
    }
    public String getValueByName(String name,Integer mvnoId) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findValueByNameandMvnoId(name,mvnoId);
    }
    public String getValueByNameAndmvnoId(String name,Integer mvnoId) {
        String cacheKey = cacheKeys.CLIENTSERVICE_NAME_MVNO + name + "_" + mvnoId;
        String value = null;

        try {
            value = (String) cacheService.getFromCache(cacheKey, String.class);

            if (value != null) {
                return value;
            }
            value = entityRepository.findValueByNameandMvnoId(name, mvnoId);

            if (value != null) {
                cacheService.putInCache(cacheKey, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return value;
    }


    @Override
    public ClientService get(Integer id,Integer mvnoId) {
        ClientService clientService = super.get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId == null)
            return clientService;
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId.intValue() == 1 || (clientService.getMvnoId().intValue() == mvnoId.intValue() || clientService.getMvnoId().intValue() == 1))
            return clientService;
        return null;
    }

    public ClientService getEntityForUpdateAndDelete(Integer id,Integer mvnoId) {
        ClientService clientService = get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if(clientService == null || !(mvnoId == 1 || mvnoId.intValue() == clientService.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return clientService;
    }

    // Shared Data From Common APIGW to CMS
    public void saveSharedClientService(SaveClientServMessge message) throws Exception{
        try {
            ClientService clientService = new ClientService();
            clientService.setId(message.getId());
            clientService.setName(message.getName());
            clientService.setValue(message.getValue());
            clientService.setMvnoId(message.getMvnoId());
            clientService.setCreatedById(message.getCreatedById());
            clientService.setCreatedByName(message.getCreatedByName());
            clientService.setLastModifiedById(message.getLastModifiedById());
            clientService.setLastModifiedByName(message.getLastModifiedByName());
            entityRepository.save(clientService);
            String cacheKey = cacheKeys.CLIENTSERVICE + clientService.getName() + ":" + clientService.getMvnoId(); // Create a unique cache key
            cacheService.putInCache(cacheKey, clientService);
            logger.info("Client Service created successfully with name " + message.getName());
        }catch (CustomValidationException e) {
            logger.error("Unable to create client service with name " + message.getName(), e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public void updateSharedClientService(UpdateClientServMessage message) throws Exception {
        try {
            ClientService clientService = entityRepository.getByNameAndMvnoId(message.getName(),message.getMvnoId());
            if (clientService != null) {
                clientService.setName(message.getName());
                clientService.setValue(message.getValue());
                clientService.setMvnoId(message.getMvnoId());
                clientService.setCreatedById(message.getCreatedById());
                clientService.setCreatedByName(message.getCreatedByName());
                clientService.setLastModifiedById(message.getLastModifiedById());
                clientService.setLastModifiedByName(message.getLastModifiedByName());
                entityRepository.save(clientService);
                String cacheKey = cacheKeys.CLIENTSERVICE + clientService.getName() + ":" + clientService.getMvnoId(); // Create a unique cache key
                cacheService.putInCache(cacheKey, clientService);
                logger.info("Client service updated successfully with name " + message.getName());
            } else {
                ClientService clientService1 = new ClientService();
                clientService1.setId(entityRepository.findlast()+1);
                clientService1.setName(message.getName());
                clientService1.setValue(message.getValue());
                clientService1.setMvnoId(message.getMvnoId());
                clientService1.setCreatedById(message.getCreatedById());
                clientService1.setCreatedByName(message.getCreatedByName());
                clientService1.setLastModifiedById(message.getLastModifiedById());
                clientService1.setLastModifiedByName(message.getLastModifiedByName());
                entityRepository.save(clientService1);
                String cacheKey = cacheKeys.CLIENTSERVICE + clientService1.getName() + ":" + clientService1.getMvnoId(); // Create a unique cache key
                cacheService.putInCache(cacheKey, clientService);
                logger.info("Client service updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update client service with name " + message.getName(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to update client service with name " + message.getName(), e.getMessage());
        }
    }
    public ClientService getCurrencyByNameAndMvnoId(String name,Integer mvnoId) {
        return entityRepository.getByNameAndMvnoIdEquals(name, mvnoId);
    }

    public ClientService getByNameAndMvnoIdEquals(String name, Integer mvnoId) {
        String cacheKey = cacheKeys.CLIENTSERVICE + name + ":" + mvnoId; // Create a unique cache key
        ClientService clientService = null;

        try {
            clientService = (ClientService) cacheService.getFromCache(cacheKey, ClientService.class);
            if (clientService != null) {
                return clientService;
            }
            clientService = entityRepository.getByNameAndMvnoIdEquals(name, mvnoId);

            if (clientService != null) {
                cacheService.putInCache(cacheKey, clientService);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return clientService;
    }

    public void addDefaultPathWhenMvnoCreated(Mvno mvno) {
        List<ClientService> clientServices = new ArrayList<>();
        Integer clientService =entityRepository.findlast();
        if(!entityRepository.existsByNameAndMvnoId("mvnodocpathread", mvno.getId().intValue())) {
            ClientService service = new ClientService("mvnodocpathread", "//var/document/mvnodocpath/",mvno.getId().intValue());
          service.setId(clientService+1);
            clientServices.add(service);
        }
        if(!entityRepository.existsByNameAndMvnoId("mvnodocpath", mvno.getId().intValue())) {
            ClientService service = new ClientService("mvnodocpath", "//var/document/mvnodocpath/",mvno.getId().intValue());
            service.setId(clientService+2);
            clientServices.add(service);
        }
        if(!CollectionUtils.isEmpty(clientServices)) {
            entityRepository.saveAll(clientServices);
        }
    }

    private ClientServicePojo convertToClientServicePojo(ClientService clientService) {
        ClientServicePojo pojo = new ClientServicePojo();
        pojo.setId(clientService.getId());
        pojo.setName((clientService.getName()));
        pojo.setMvnoId(clientService.getMvnoId());
        pojo.setValue(clientService.getValue());
        pojo.setDisplayName(clientService.getName());
        pojo.setDisplayId(clientService.getId());

        return pojo;
    }


    public ClientService getClientSrvByNameAndCustID(String name,Integer custId) {
        // TODO: pass mvnoID manually 6/5/2025
        ClientService clientService = entityRepository.findByNameAndMvnoId(name,getMvnoIdFromCurrentStaff(custId));
        return clientService;
    }


    public void syncSharedClientService(SyncClientServiceMessage message) throws Exception{
        try {

            List<ClientService> clientServiceList = new ArrayList<ClientService>();

            for (SaveClientServMessge saveClientServMessge : message.getClientServiceList()) {
                ClientService clientService = new ClientService();
                clientService.setId(saveClientServMessge.getId());
                clientService.setName(saveClientServMessge.getName());
                clientService.setValue(saveClientServMessge.getValue());
                clientService.setMvnoId(saveClientServMessge.getMvnoId());
                clientService.setCreatedById(saveClientServMessge.getCreatedById());
                clientService.setCreatedByName(saveClientServMessge.getCreatedByName());
                clientService.setLastModifiedById(saveClientServMessge.getLastModifiedById());
                clientService.setLastModifiedByName(saveClientServMessge.getLastModifiedByName());

                clientServiceList.add(clientService);
            }

            Integer mvnoId = message.getMvnoId();

            if (mvnoId == 1) {
                // SuperAdmin: update only itself
                syncForSingleMvno(clientServiceList, 1, true, true);

                // SuperAdmin: insert for all MVNOs id service not present
                List<Long> allMvnoIds = mvnoRepository.findAllMvnoId();
                for (Long innerMvnoId : allMvnoIds) {
                    if (innerMvnoId != 1) {
                        syncForSingleMvno(clientServiceList, innerMvnoId.intValue(), false, true);
                    }
                }
            } else {
                // Normal MVNO: update/insert only for itself
                syncForSingleMvno(clientServiceList, mvnoId, true, true);
            }

            log.info("Client Service Sync successfully");
        } catch (CustomValidationException e) {
            log.error("Unable to create client service with name");
        }
    }

    private void syncForSingleMvno(List<ClientService> clientServiceList, int mvnoId, boolean allowUpdate, boolean allowInsert) {

        final int BATCH_SIZE = 500;

        List<ClientService> saveBatch = new ArrayList<>(BATCH_SIZE);
        List<ClientService> deleteBatch = new ArrayList<>(BATCH_SIZE);

        for (ClientService service : clientServiceList) {

            List<ClientService> existingList =
                    entityRepository.findAllByNameAndMvnoId(service.getName(), mvnoId);

            // CASE 1: Existing records (handle duplicates)
            if (!existingList.isEmpty()) {

                ClientService primary = existingList.stream()
                        .max(Comparator.comparing(ClientService::getId))
                        .get();

                // collect duplicates
                for (ClientService cs : existingList) {
                    if (!cs.getId().equals(primary.getId())) {
                        deleteBatch.add(cs);
                    }
                }

                if (allowUpdate) {
                    primary.setValue(service.getValue());
                    primary.setLastModifiedById(service.getLastModifiedById());
                    primary.setLastModifiedByName(service.getLastModifiedByName());
                    saveBatch.add(primary);
                }
            }

            // CASE 2: No record → Insert
            else if (allowInsert) {

                ClientService newService = new ClientService();
                Integer newId = entityRepository.findlast()+1;
                newService.setId(newId);
                newService.setName(service.getName());
                newService.setValue(service.getValue());
                newService.setMvnoId(mvnoId);
                newService.setCreatedById(service.getCreatedById());
                newService.setCreatedByName(service.getCreatedByName());
                newService.setLastModifiedById(service.getLastModifiedById());
                newService.setLastModifiedByName(service.getLastModifiedByName());


                entityRepository.save(newService);

                log.info("Inserted ClientService " + service.getName() + " for mvnoId " + mvnoId);
            }

            //  Save/Delete when batch size reached
            if (saveBatch.size() == BATCH_SIZE) {
                entityRepository.saveAll(saveBatch);
                saveBatch.clear();
            }

            if (deleteBatch.size() == BATCH_SIZE) {
                entityRepository.deleteAll(deleteBatch);
                deleteBatch.clear();
            }
        }

        //  Save/Delete remaining records
        if (!saveBatch.isEmpty()) {
            entityRepository.saveAll(saveBatch);
        }

        if (!deleteBatch.isEmpty()) {
            entityRepository.deleteAll(deleteBatch);
        }
    }
}
