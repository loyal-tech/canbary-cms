package com.adopt.apigw.service.radius;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.radius.CheckItemReplyItem;
import com.adopt.apigw.model.radius.RadiusProfile;
import com.adopt.apigw.model.radius.RadiusProfileCheckItem;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.pojo.api.RadiusProfilePojo;
import com.adopt.apigw.repository.radius.RadProfCheckItemRepository;
import com.adopt.apigw.repository.radius.RadiusProfileRepository;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RadiusProfileService extends AbstractService<RadiusProfile, RadiusProfilePojo, Integer> {

    public static final String MODULE = "[RadiusProfileService]";

    public RadiusProfileService() {
        sortColMap.put("id", "radiusprofileid");
    }

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private RadiusProfileRepository entityRepository;

    @Autowired
    private RadProfileCheckItemService checkItemService;
    @Autowired
    private RadProfCheckItemRepository radProfCheckItemRepository;
    @Autowired
    RadiusProfileRepository radiusProfileRepository;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<RadiusProfile, Integer> getRepository() {
        return entityRepository;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.RadiusProfile', '1')")
    public Page<RadiusProfile> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.searchEntity(searchText, pageRequest);
    }

    public List<RadiusProfile> getAllActiveEntities() {
        return entityRepository.findByStatusAndIsDeleteIsFalse("1");
    }

    public List<RadiusProfile> getAllEntities(List<Integer> id) {
        return entityRepository.findAllById(id);
    }

    public RadiusProfile getActiveEntity(Integer id) {
        return entityRepository.findById(id).orElse(null);
    }

    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        Integer count= entityRepository.deleteVerify(id);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    public void deleteRadiusProfile(Integer id) throws Exception {
        boolean flag=this.deleteVerification(id);
        if(flag){
            entityRepository.deleteById(id);
        }else{
            throw new RuntimeException(DeleteContant.RADIUS_PROFILE_DELETE_EXIST);
        }
    }

    public RadiusProfile getRadiusProfileForAdd() {
        return new RadiusProfile();
    }

    public RadiusProfile getRadiusProfileForEdit(Integer id) throws Exception {
        return entityRepository.getOne(id);
    }

    public RadiusProfileCheckItem addCondition(Integer id) throws Exception {
        String SUBMODULE = MODULE + " [addCondition()] ";
        RadiusProfile prof = entityRepository.getOne(id);
        RadiusProfileCheckItem chkItem = new RadiusProfileCheckItem();
        try {
            chkItem.setReplyItems(new ArrayList<>());
            chkItem.getReplyItems().add(new CheckItemReplyItem());
            chkItem.setRadiusProfile(prof);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return chkItem;
    }

    public RadiusProfileCheckItem editCondition(Integer id) throws Exception {
        RadiusProfileCheckItem chkItem = radProfCheckItemRepository.findById(id).get();
        return chkItem;
    }

    public void deleteCondition(Integer id) throws Exception {
        checkItemService.delete(id);
    }

    public CheckItemReplyItem getCheckItemReplyItem() {
        return new CheckItemReplyItem();
    }

    public List<CheckItemReplyItem> getCheckItemReplyItemList() {
        return new ArrayList<>();
    }

    public RadiusProfileCheckItem removeReplyItem(RadiusProfileCheckItem checkItem, int index) {
        checkItem.getReplyItems().remove(index);
        return checkItem;
    }

    public RadiusProfile saveRadiusProfile(RadiusProfile radiusProfile) throws Exception {
        String SUBMODULE = MODULE + "[saveRadiusProfile()]";
        try {
            return entityRepository.save(radiusProfile);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    public RadiusProfilePojo save(RadiusProfilePojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [save()] ";
        RadiusProfile oldObj = null;
        if (pojo.getId() != null) {
            oldObj = radiusProfileRepository.findById(pojo.getId()).get();
        }
        try {
            RadiusProfile obj = convertRadiusProfilePojoToRadiusProfileModel(pojo);
            if(oldObj!=null) {
                log.info("RadiusProfile update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
            }
            obj = saveRadiusProfile(obj);
            pojo = convertRadiusProfileModelToRadiusProfilePojo(obj);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
                // TODO: pass mvnoID manually 6/5/2025
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
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
            if(getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
                // TODO: pass mvnoID manually 6/5/2025
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                    // TODO: pass mvnoID manually 6/5/2025
                else countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }


    public RadiusProfile convertRadiusProfilePojoToRadiusProfileModel(RadiusProfilePojo radiusProfilePojo) throws Exception {
        String SUBMODULE = MODULE + " [convertRadiusProfilePojoToRadiusProfileModel()] ";
        RadiusProfile radiusProfile = null;
        try {
            if (radiusProfilePojo != null) {
                radiusProfile = new RadiusProfile();
                if (radiusProfilePojo.getId() != null) {
                    radiusProfile.setId(radiusProfilePojo.getId());
                    RadiusProfile radProfileOld = entityRepository.getOne(radiusProfilePojo.getId());
                    radiusProfile.setCheckItems(radProfileOld.getCheckItems());
                }
                radiusProfile.setName(radiusProfilePojo.getName());
                radiusProfile.setStatus(radiusProfilePojo.getStatus());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return radiusProfile;
    }

    public RadiusProfilePojo convertRadiusProfileModelToRadiusProfilePojo(RadiusProfile radiusProfile) throws Exception {
        String SUBMODULE = MODULE + " [convertRadiusProfileModelToRadiusProfilePojo()] ";
        RadiusProfilePojo pojo = null;
        try {
            if (radiusProfile != null) {
                pojo = new RadiusProfilePojo();
                pojo.setId(radiusProfile.getId());
                pojo.setName(radiusProfile.getName());
                pojo.setStatus(radiusProfile.getStatus());
                pojo.setCreatedate(radiusProfile.getCreatedate());
                pojo.setCreatedById(radiusProfile.getCreatedById());
                pojo.setCreatedByName(radiusProfile.getCreatedByName());
                pojo.setUpdatedate(radiusProfile.getUpdatedate());
                pojo.setLastModifiedById(radiusProfile.getLastModifiedById());
                pojo.setLastModifiedByName(radiusProfile.getLastModifiedByName());
                pojo.setCheckItems(checkItemService.convertResponseModelIntoPojo(radiusProfile.getCheckItems()));
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public List<RadiusProfilePojo> convertResponseModelIntoPojo(List<RadiusProfile> radiusProfileList) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
        List<RadiusProfilePojo> pojoListRes = new ArrayList<>();
        try {
            if (radiusProfileList != null && radiusProfileList.size() > 0) {
                for (RadiusProfile radiusProfile : radiusProfileList) {
                    pojoListRes.add(convertRadiusProfileModelToRadiusProfilePojo(radiusProfile));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    public List<RadiusProfile> convertPojoIntoModel(List<RadiusProfilePojo> radiusProfileList) throws Exception {
        String SUBMODULE = MODULE + " [convertPojoIntoModel()] ";
        List<RadiusProfile> pojoListRes = new ArrayList<>();
        try {
            if (radiusProfileList != null && radiusProfileList.size() > 0) {
                for (RadiusProfilePojo radiusProfile : radiusProfileList) {
                    pojoListRes.add(convertRadiusProfilePojoToRadiusProfileModel(radiusProfile));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    public void validateRequest(RadiusProfilePojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_DELETE) && !(pojo.getStatus().equalsIgnoreCase("1") || pojo.getStatus().equalsIgnoreCase("0"))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Radius Profile");
        List<RadiusProfilePojo> radiusProfilePojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, RadiusProfilePojo.class, radiusProfilePojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<RadiusProfilePojo> radiusProfilePojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, RadiusProfilePojo.class, radiusProfilePojos, null);
    }

    @Override
    public Page<RadiusProfile> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getRadiusProfileByName(searchModel.getFilterValue(), pageRequest);
                    }
                } else
                    throw new RuntimeException("Please Provide Search Column!");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public Page<RadiusProfile> getRadiusProfileByName(String s1, PageRequest pageRequest) {
        return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(pageRequest, s1);
    }
}
