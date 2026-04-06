package com.adopt.apigw.service.radius;

import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.radius.CheckItemReplyItem;
import com.adopt.apigw.model.radius.RadiusProfileCheckItem;
import com.adopt.apigw.pojo.api.CheckItemReplyItemPojo;
import com.adopt.apigw.pojo.api.RadiusProfileCheckItemPojo;
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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RadProfileCheckItemService extends AbstractService<RadiusProfileCheckItem, RadiusProfileCheckItemPojo, Integer> {

    public static final String MODULE = " [RadProfileCheckItemService] ";

    @Autowired
    private RadProfCheckItemRepository entityRepository;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private RadiusProfileRepository radiusProfileRepository;
    @Autowired
    private RadProfCheckItemRepository radProfCheckItemRepository;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<RadiusProfileCheckItem, Integer> getRepository() {
        return entityRepository;
    }

    public void deleteReplteItems(Integer checkItemId) {
        entityRepository.deleteReplteItems(checkItemId);
    }

    public List<RadiusProfileCheckItem> findAllByRadiusProfile(Integer radiusProfileId) {
        return entityRepository.findAllByRadiusProfileAndIsDeletedFalse(radiusProfileRepository.getOne(radiusProfileId));
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.RadiusProfile', '2')")
    public RadiusProfileCheckItem saveRadiusProfileCheckItem(RadiusProfileCheckItem radiusProfileCheckItem) {
        String SUBMODULE = MODULE + "[saveRadiusProfileCheckItem()]";
        try {
            for (CheckItemReplyItem item : radiusProfileCheckItem.getReplyItems()) {
                item.setRadiusprofileid(radiusProfileCheckItem.getRadiusProfile().getId());
                item.setRadiusProfileCheckItem(radiusProfileCheckItem);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return entityRepository.save(radiusProfileCheckItem);
    }

    public void delete(RadiusProfileCheckItem radiusProfileCheckItem)throws Exception
    {
        String SUBMODULE = MODULE + " [deleteRadiusProfile()] ";
        try{
            radiusProfileCheckItem.setIsDeleted(true);
            entityRepository.save(radiusProfileCheckItem);
        }catch(Exception ex)
        {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public RadiusProfileCheckItemPojo save(RadiusProfileCheckItemPojo pojo) {
        String SUBMODULE = MODULE + "[save()]";
        RadiusProfileCheckItem oldObj = null;
        if (pojo.getId() != null) {
            oldObj = radProfCheckItemRepository.findById(pojo.getId()).get();
        }
        RadiusProfileCheckItem obj = convertRadiusProfileCheckItemPojoToRadiusProfileCheckItemModel(pojo);
        try {
            for (CheckItemReplyItem item : obj.getReplyItems()) {
                item.setRadiusprofileid(obj.getRadiusProfile().getId());
                item.setRadiusProfileCheckItem(obj);
            }
            if(oldObj!=null) {
                log.info("RadiusProfileCheckItem update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
            }
            obj = entityRepository.save(obj);
            pojo = convertRadiusProfileCheckItemModelToRadiusProfileCheckItemPojo(obj);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public RadiusProfileCheckItem convertRadiusProfileCheckItemPojoToRadiusProfileCheckItemModel(RadiusProfileCheckItemPojo radiusProfileCheckItemPojo) {
        String SUBMODULE = MODULE + "[convertRadiusProfileCheckItemPojoToRadiusProfileCheckItemModel()]";
        RadiusProfileCheckItem radiusProfileCheckItem = null;
        try {
            if (radiusProfileCheckItemPojo != null) {
                radiusProfileCheckItem = new RadiusProfileCheckItem();
                if (radiusProfileCheckItemPojo.getId() != null) {
                    radiusProfileCheckItem.setId(radiusProfileCheckItemPojo.getId());
                }
                radiusProfileCheckItem.setCheckitem(radiusProfileCheckItemPojo.getCheckitem());
                if (radiusProfileRepository.findById(radiusProfileCheckItemPojo.getRadiusProfileId()) != null)
                    radiusProfileCheckItem.setRadiusProfile(radiusProfileRepository.findById(radiusProfileCheckItemPojo.getRadiusProfileId()).get());

                if (radiusProfileCheckItemPojo.getReplyItems() != null && radiusProfileCheckItemPojo.getReplyItems().size() > 0) {
                    List<CheckItemReplyItem> checkItemReplyItemList = new ArrayList<CheckItemReplyItem>();
                    CheckItemReplyItem checkItemReplyItem = null;
                    for (CheckItemReplyItemPojo checkItemReplyItemPojo : radiusProfileCheckItemPojo.getReplyItems()) {
                        checkItemReplyItem = new CheckItemReplyItem();
                        if (checkItemReplyItemPojo.getId() != null) {
                            checkItemReplyItem.setId(checkItemReplyItemPojo.getId());
                        }
                        checkItemReplyItem.setAttribute(checkItemReplyItemPojo.getAttribute());
                        checkItemReplyItem.setAttributevalue(checkItemReplyItemPojo.getAttributevalue());
                        checkItemReplyItem.setRadiusprofileid(checkItemReplyItemPojo.getRadiusprofileid());
                        checkItemReplyItemList.add(checkItemReplyItem);
                    }
                    radiusProfileCheckItem.setReplyItems(checkItemReplyItemList);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return radiusProfileCheckItem;
    }

    public RadiusProfileCheckItemPojo convertRadiusProfileCheckItemModelToRadiusProfileCheckItemPojo(RadiusProfileCheckItem radiusProfileCheckItem) {
        String SUBMODULE = MODULE + "[convertRadiusProfileCheckItemModelToRadiusProfileCheckItemPojo()]";
        RadiusProfileCheckItemPojo pojo = null;
        try {
            if (radiusProfileCheckItem != null) {
                pojo = new RadiusProfileCheckItemPojo();
                pojo.setId(radiusProfileCheckItem.getId());
                pojo.setCheckitem(radiusProfileCheckItem.getCheckitem());
                pojo.setRadiusProfileId(radiusProfileCheckItem.getRadiusProfile().getId());
                pojo.setCreatedate(radiusProfileCheckItem.getCreatedate());
                pojo.setCreatedById(radiusProfileCheckItem.getCreatedById());
                pojo.setCreatedByName(radiusProfileCheckItem.getCreatedByName());
                pojo.setUpdatedate(radiusProfileCheckItem.getUpdatedate());
                pojo.setLastModifiedById(radiusProfileCheckItem.getLastModifiedById());
                pojo.setLastModifiedByName(radiusProfileCheckItem.getLastModifiedByName());

                if (radiusProfileCheckItem.getReplyItems() != null && radiusProfileCheckItem.getReplyItems().size() > 0) {
                    List<CheckItemReplyItemPojo> checkItemReplyItemPojoList = new ArrayList<>();
                    CheckItemReplyItemPojo checkItemReplyItemPojo = null;
                    for (CheckItemReplyItem checkItemReplyItem : radiusProfileCheckItem.getReplyItems()) {
                        checkItemReplyItemPojo = new CheckItemReplyItemPojo();
                        if (checkItemReplyItem.getId() != null) {
                            checkItemReplyItemPojo.setId(checkItemReplyItem.getId());
                        }
                        checkItemReplyItemPojo.setAttribute(checkItemReplyItem.getAttribute());
                        checkItemReplyItemPojo.setAttributevalue(checkItemReplyItem.getAttributevalue());
                        checkItemReplyItemPojo.setRadiusprofileid(checkItemReplyItem.getRadiusprofileid());
                        checkItemReplyItemPojo.setRadiusProfileCheckItemId(checkItemReplyItem.getRadiusProfileCheckItem().getId());
                        checkItemReplyItemPojo.setCreatedate(checkItemReplyItem.getCreatedate());
                        checkItemReplyItemPojo.setCreatedById(checkItemReplyItem.getCreatedById());
                        checkItemReplyItemPojo.setCreatedByName(checkItemReplyItem.getCreatedByName());
                        checkItemReplyItemPojo.setUpdatedate(checkItemReplyItem.getUpdatedate());
                        checkItemReplyItemPojo.setLastModifiedById(checkItemReplyItem.getLastModifiedById());
                        checkItemReplyItemPojo.setLastModifiedByName(checkItemReplyItem.getLastModifiedByName());
                        checkItemReplyItemPojoList.add(checkItemReplyItemPojo);
                    }
                    pojo.setReplyItems(checkItemReplyItemPojoList);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public List<RadiusProfileCheckItemPojo> convertResponseModelIntoPojo(List<RadiusProfileCheckItem> radiusProfileCheckItemList) {
        String SUBMODULE = MODULE + "[convertResponseModelIntoPojo()]";
        List<RadiusProfileCheckItemPojo> pojoListRes = new ArrayList<>();
        try {
            if (radiusProfileCheckItemList != null && radiusProfileCheckItemList.size() > 0) {
                for (RadiusProfileCheckItem radiusProfileCheckItem : radiusProfileCheckItemList) {
                    pojoListRes.add(convertRadiusProfileCheckItemModelToRadiusProfileCheckItemPojo(radiusProfileCheckItem));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    public void validateRequest(RadiusProfileCheckItemPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (pojo != null && !operation.equals(CommonConstants.OPERATION_DELETE)) {
            if (radiusProfileRepository.getOne(pojo.getRadiusProfileId()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.radiusprofile.not.found"), null);
            }
            if (pojo.getReplyItems() == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.checkitem.replyitem.list.required"), null);
            }
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Radius Profile Check Item");
        List<RadiusProfileCheckItemPojo> radiusProfileCheckItemPojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, RadiusProfileCheckItemPojo.class, radiusProfileCheckItemPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<RadiusProfileCheckItemPojo> radiusProfileCheckItemPojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, RadiusProfileCheckItemPojo.class, radiusProfileCheckItemPojos, null);
    }
}
