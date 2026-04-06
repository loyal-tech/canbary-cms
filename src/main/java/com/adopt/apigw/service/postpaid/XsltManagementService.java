package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.QXsltManagement;
import com.adopt.apigw.model.postpaid.XsltManagement;
import com.adopt.apigw.pojo.XslManagementPojo;
import com.adopt.apigw.repository.postpaid.XsltManagementRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.itextpdf.text.Document;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class XsltManagementService extends AbstractService<XsltManagement, XslManagementPojo, Integer> {

    public static final String MODULE = "[XsltManagementService]";

    @Autowired
    private XsltManagementRepository xsltManagementRepository;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @PersistenceContext
    EntityManager entityManager;
    public XsltManagementService() {
        sortColMap.put("id", "templateid");
        sortColMap.put("name", "templatename");
        sortColMap.put("type", "templatetype");
    }

    @Override
    protected JpaRepository<XsltManagement, Integer> getRepository() {
        return xsltManagementRepository;
    }

    public List<XsltManagement> getAllActiveEntities() {
// TODO: pass mvnoID manually 6/5/2025
        return xsltManagementRepository.findByStatus("Y")
        		.stream().filter(tax -> tax.getMvnoId() == getMvnoIdFromCurrentStaff(null) || tax.getMvnoId() == null).collect(Collectors.toList());
    }

    public List<XsltManagement> getAllEntities() {
        boolean flag = getLoggedInUser().getLco();
        JPAQuery<CustPlanMappping> query = new JPAQuery<>(entityManager);
        Integer mvnoid = getLoggedInUser().getMvnoId();
        QXsltManagement qXsltManagement=QXsltManagement.xsltManagement;
        BooleanExpression exp = qXsltManagement.isNull();
        List<XsltManagement> xsltManagementList = new ArrayList<>();
        if (flag) {
            xsltManagementList=query.select(QXsltManagement.xsltManagement).from(qXsltManagement).where(qXsltManagement.lcoid.isNotNull().and(qXsltManagement.isDelete.eq(false).and(qXsltManagement.lcoid.eq(getLoggedInUser().getPartnerId())))).fetch();
        } else {
            xsltManagementList=query.select(QXsltManagement.xsltManagement).from(qXsltManagement).where(qXsltManagement.lcoid.isNotNull().and(qXsltManagement.isDelete.eq(false).and(qXsltManagement.lcoid.isNull()))).fetch();

        }
        return xsltManagementList;
    }

    public List<XsltManagement> getListByType(String type, Integer mvnoId) {
        boolean flag = getLoggedInUser().getLco();
        JPAQuery<CustPlanMappping> query = new JPAQuery<>(entityManager);
//        Integer mvnoid = mvnoId;
        QXsltManagement qXsltManagement=QXsltManagement.xsltManagement;
        BooleanExpression exp = qXsltManagement.isNull().and(qXsltManagement.isDelete.eq(false)).and(qXsltManagement.templatetype.equalsIgnoreCase(type));
        List<XsltManagement> xsltManagementList = new ArrayList<>();
        if (flag) {
            exp = exp.and(qXsltManagement.lcoid.eq(getLoggedInUser().getPartnerId()));
        }

        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            exp = exp.and((qXsltManagement.mvnoId.in(mvnoId, 1)));
        }
        if (getBUIdsFromCurrentStaff().size() != 0) {
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId!=1){
                // TODO: pass mvnoID manually 6/5/2025
                exp = exp.and(qXsltManagement.mvnoId.eq(1).or(qXsltManagement.mvnoId.eq(mvnoId).and(qXsltManagement.buId.in(getBUIdsFromCurrentStaff()))));
            }else if(mvnoId == 1){ // TODO: pass mvnoID manually 6/5/2025
                exp = exp.and(qXsltManagement.buId.in(getBUIdsFromCurrentStaff()));
            }
        }

        List<XsltManagement> data = xsltManagementRepository.findAll();
        data = data.stream().filter(xsltManagement -> xsltManagement.getTemplatetype().equalsIgnoreCase(type)).collect(Collectors.toList());
        xsltManagementList = (List<XsltManagement>) xsltManagementRepository.findAll(exp);
        return data;
    }

    public Page<XsltManagement> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if(getLoggedInUser().getLco())
        {
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1)
                return xsltManagementRepository.findAll(pageRequest,getLoggedInUser().getPartnerId());
            if (null == filterList || 0 == filterList.size()) {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    return xsltManagementRepository.findAll(pageRequest, Arrays.asList(1, mvnoId),getLoggedInUser().getPartnerId());
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    return xsltManagementRepository.findAll(pageRequest, mvnoId, getBUIdsFromCurrentStaff(),getLoggedInUser().getPartnerId());
            }
            else
                return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
        }
        else
        {
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1)
                return xsltManagementRepository.findAll(pageRequest);
            if (null == filterList || 0 == filterList.size()) {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    return xsltManagementRepository.findAll(pageRequest, Arrays.asList(1, mvnoId));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    return xsltManagementRepository.findAll(pageRequest,mvnoId, getBUIdsFromCurrentStaff());
            }
            else
                return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
        }
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.XsltManagement', '1')")
    public List<XslManagementPojo> convertResponseModelIntoPojo(List<XsltManagement> xsltManagementList) {
        List<XslManagementPojo> pojoListRes = new ArrayList<>();
        if (xsltManagementList != null && xsltManagementList.size() > 0) {
            for (XsltManagement xsltManagement : xsltManagementList) {
                pojoListRes.add(convertXslManagementModelToXslManagementPojo(xsltManagement));
            }
        }
        return pojoListRes;
    }

    public XslManagementPojo convertXslManagementModelToXslManagementPojo(XsltManagement xsltManagement) {
        XslManagementPojo pojo = null;
        Boolean flag=getLoggedInUser().getLco();

        if (xsltManagement != null) {
            pojo = new XslManagementPojo();
            pojo.setId(xsltManagement.getId());
            pojo.setStatus(xsltManagement.getStatus());
            pojo.setTemplatename(xsltManagement.getTemplatename());
            pojo.setTemplatetype(xsltManagement.getTemplatetype());
            pojo.setCreatedate(xsltManagement.getCreatedate());
            pojo.setCreatedById(xsltManagement.getCreatedById());
            pojo.setCreatedByName(xsltManagement.getCreatedByName());
            pojo.setUpdatedate(xsltManagement.getUpdatedate());
            pojo.setLastModifiedById(xsltManagement.getLastModifiedById());
            pojo.setUpdatedate(xsltManagement.getUpdatedate());
            pojo.setJrxmlfile(xsltManagement.getJrxmlfile());
            pojo.setIsDelete(xsltManagement.getIsDelete());
            pojo.setLcoid(xsltManagement.getLcoid());

            if(xsltManagement.getMvnoId() != null) {
            	pojo.setMvnoId(xsltManagement.getMvnoId());
            }
        }
        return pojo;
    }

//    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.XsltManagement', '2')")
//    public XslManagementPojo save(XslManagementPojo pojo) throws Exception {
//        XsltManagement oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
//        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//        if(getLoggedInUser().getLco())
//            pojo.setLcoid(getLoggedInUser().getPartnerId());
//        else
//            pojo.setLcoid(null);
//
//        XsltManagement obj = convertXsltManagementPojoToXsltManagementModel(pojo);
//        if(getBUIdsFromCurrentStaff().size() == 1)
//                obj.setBuId(getBUIdsFromCurrentStaff().get(0));
//        log.info("XsltManagement update details: " + UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
//        obj = saveXslManagement(obj);
//        pojo = convertXslManagementModelToXslManagementPojo(obj);
//        return pojo;
//    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.XsltManagement', '2')")
    public XsltManagement saveXslManagement(XsltManagement xsltManagement) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) != null) {
            // TODO: pass mvnoID manually 6/5/2025
    		xsltManagement.setMvnoId(getMvnoIdFromCurrentStaff(null));
    	}
        return xsltManagementRepository.save(xsltManagement);
    }

//    public boolean checkDuplicateByBuIdAndMvnoIdAndByTemplateType(XslManagementPojo pojo) throws Exception {
//        XsltManagement obj = convertXsltManagementPojoToXsltManagementModel(pojo);
//
//        if(getBUIdsFromCurrentStaff().size() == 1)
//            obj.setBuId(getBUIdsFromCurrentStaff().get(0));
//        obj.setMvnoId(getMvnoIdFromCurrentStaff());
//
//        if(getLoggedInUser().getLco())
//            obj.setLcoid(getLoggedInUser().getPartnerId());
//        else
//            obj.setLcoid(null);
//
//
//        QXsltManagement management=QXsltManagement.xsltManagement;
//        BooleanExpression expression=management.isNotNull();
//        if(obj.getBuId()!=null)
//            expression=expression.and(management.buId.eq(obj.getBuId())).and(management.isDelete.eq(false));
//        else
//            expression=expression.and(management.buId.isNull()).and(management.isDelete.eq(false));
//
//        if(obj.getLcoid()!=null)
//            expression=expression.and(management.lcoid.eq(obj.getLcoid()));
//        else
//            expression=expression.and(management.lcoid.isNull());
//
//
//        expression=expression.and(management.mvnoId.eq(obj.getMvnoId()));
//        expression=expression.and(management.templatetype.eq(obj.getTemplatetype()));
//        Long count=xsltManagementRepository.count(expression);
//        if(count>0)
//            return true;
//        else
//            return false;
//    }

    public XsltManagement convertXsltManagementPojoToXsltManagementModel(XslManagementPojo pojo) throws Exception {
        XsltManagement xsltManagement = null;
        if (pojo != null) {
            xsltManagement = new XsltManagement();
            if (pojo.getId() != null) {
                xsltManagement.setId(pojo.getId());
            }
            xsltManagement.setStatus(pojo.getStatus());
            xsltManagement.setTemplatename(pojo.getTemplatename());
            xsltManagement.setTemplatetype(pojo.getTemplatetype());
            xsltManagement.setCreatedate(pojo.getCreatedate());
            xsltManagement.setIsDelete(pojo.getIsDelete());
            xsltManagement.setCreatedById(pojo.getCreatedById());
            xsltManagement.setCreatedByName(pojo.getCreatedByName());
            xsltManagement.setLastModifiedById(pojo.getLastModifiedById());
            xsltManagement.setUpdatedate(pojo.getUpdatedate());
            xsltManagement.setLastModifiedByName(pojo.getLastModifiedByName());
            xsltManagement.setJrxmlfile(pojo.getJrxmlfile());
            if(pojo.getMvnoId() != null) {
            	xsltManagement.setMvnoId(pojo.getMvnoId());
            }
            if(pojo.getLcoid() != null){
                xsltManagement.setLcoid(pojo.getLcoid());
            }
        }
        return xsltManagement;
    }

//    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.XsltManagement', '4')")
//    public void deleteXsltManagement(Integer id) throws Exception {
//        XsltManagement xsltManagement = get(id);
//        xsltManagement.setIsDelete(true);
//        xsltManagementRepository.save(xsltManagement);
//    }

//    public void validateRequest(XslManagementPojo pojo, Integer operation) {
//
//        if (pojo == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
//        }
//        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
//            if (pojo.getId() != null) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
//            }
//        }
//        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
//                || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
//        }
//    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Template Management");
        List<XslManagementPojo> xslManagementPojos = convertResponseModelIntoPojo(xsltManagementRepository.findAll());
        createExcel(workbook, sheet, XslManagementPojo.class, xslManagementPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<XslManagementPojo> xslManagementPojos = convertResponseModelIntoPojo(xsltManagementRepository.findAll());
        createPDF(doc, XslManagementPojo.class, xslManagementPojos, null);
    }


    @Override
    public Page<XsltManagement> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getTemplateByNameOrTypeOrStatus(searchModel.getFilterValue(), pageRequest);
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

    public Page<XsltManagement> getTemplateByNameOrTypeOrStatus(String s1, PageRequest pageRequest) {
        // TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) == 1)
            return xsltManagementRepository.findAllByCustom(pageRequest, s1, s1, s1);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
            // TODO: pass mvnoID manually 6/5/2025
            return xsltManagementRepository.findAllByCustom(pageRequest, s1, s1, s1, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        } else {
            // TODO: pass mvnoID manually 6/5/2025
            return xsltManagementRepository.findAllByCustom(pageRequest, s1, s1, s1, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
        }
    }

 //   @Override
//    public boolean duplicateVerifyAtSave(String name) throws Exception {
//        boolean flag = false;
//        Integer mvnoId = getMvnoIdFromCurrentStaff();
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = xsltManagementRepository.duplicateVerifyAtSave(name);
//            else{
//                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
//                    count = xsltManagementRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                } else {
//                    count = xsltManagementRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//                }
//            }
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }


//    @Override
//    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
//        boolean flag = false;
//        Integer mvnoId = getMvnoIdFromCurrentStaff();
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = xsltManagementRepository.duplicateVerifyAtSave(name);
//            else {
//                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
//                    count = xsltManagementRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                } else {
//                    count = xsltManagementRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//                }
//            }
//            if (count >= 1) {
//                Integer countEdit;
//                if(getMvnoIdFromCurrentStaff() == 1) countEdit = xsltManagementRepository.duplicateVerifyAtEdit(name, id);
//                else {
//                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
//                        countEdit = xsltManagementRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                    } else {
//                        countEdit = xsltManagementRepository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//                    }
//                }
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }

    @Override
    public XsltManagement get(Integer id,Integer mvnoId) {
        XsltManagement xsltManagement = super.get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (xsltManagement != null && (getMvnoIdFromCurrentStaff(null) == 1 || ((xsltManagement.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || xsltManagement.getMvnoId() == 1) && (xsltManagement.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(xsltManagement.getBuId())))))
            return xsltManagement;
        return null;
    }

//    public XsltManagement getEntityForUpdateAndDelete(Integer id) {
//        XsltManagement xsltManagement = get(id);
//        if(xsltManagement == null || (!(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == xsltManagement.getMvnoId().intValue()) && (xsltManagement.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(xsltManagement.getBuId()))))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        return xsltManagement;
//    }

}
