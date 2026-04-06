package com.adopt.apigw.service.radius;


import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.ExcelUtil;
import com.adopt.apigw.utils.PdfUtil;
import com.itextpdf.text.Document;
import io.swagger.models.auth.In;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.javers.spring.annotation.JaversAuditable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractService<T, DTO, Long> {

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    StaffUserService staffUserService;

    public Integer MAX_PAGE_SIZE;

    public Map<String, String> sortColMap = new HashMap<>();

    public PageRequest pageRequest = null;

    //  public static final int PAGE_SIZE = CommonConstants.DB_PAGE_SIZE;
    protected abstract JpaRepository<T, Long> getRepository();
    @Autowired
    private CustomersRepository customersRepository;
    
    public Integer getMvnoIdFromCurrentStaff() {
        //TODO: Change once API work on live BSS server
    	Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if(securityContext.getAuthentication().getPrincipal() != null)
            	    mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }
    public Integer getMvnoIdFromCurrentStaff(Integer custId) {
        //TODO: Change once API work on live BSS server
        Integer mvnoId = null;
        try {
            if(custId!=null){
                mvnoId = customersRepository.getCustomerMvnoIdByCustId(custId);

            }
            else {
                SecurityContext securityContext = SecurityContextHolder.getContext();
                if (null != securityContext.getAuthentication()) {
                    if(securityContext.getAuthentication().getPrincipal() != null)
                        mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }


    public Page<T> getList(Integer pageNumber) {
//        PageRequest pageRequest = PageRequest.of(pageNumber - 1, PAGE_SIZE);
//                //new PageRequest(pageNumber - 1, PAGE_SIZE, Sort.Direction.DESC, "firstname");
//
//        return getRepository().findAll(pageRequest);
        return getList(pageNumber, CommonConstants.DB_PAGE_SIZE);
    }

    public Page<T> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if (null == filterList || 0 == filterList.size())
            return getRepository().findAll(pageRequest);
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,getMvnoIdFromCurrentStaff());
    }
    public Page<T> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if (null == filterList || 0 == filterList.size())
            return getRepository().findAll(pageRequest);
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
    }

    public Page<T> getList(Integer pageNumber, Integer customPageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, customPageSize);
        //new PageRequest(pageNumber - 1, PAGE_SIZE, Sort.Direction.DESC, "firstname");

        return getRepository().findAll(pageRequest);
    }

  //  @JaversAuditable
    public T save(T entity) {
        return getRepository().save(entity);
    }

    public T get(Long id, Integer mvnoId) {
        return getRepository().findById(id).orElse(null);
    }

    public void delete(Long id) {
        try {
            getRepository().deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            ApplicationLogger.logger.error("Abstract Service delete() " + e.getMessage(), e);
        }
    }

    public boolean deleteVerification(Integer id) throws Exception{
    	return false;
    }

    public boolean duplicateVerifyAtSave(String name) throws Exception{
    	return false;
    }
    public boolean duplicateVerifyStateAtSave(String name , Integer countryId , Integer STATEID) throws Exception{
        return false;
    }
    public boolean duplicateVerifyStateAtEdit(String name , Integer countryId , Integer STATEID, Integer id) throws Exception{
        return false;
    }

    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception{
    	return false;
    }
    
    public boolean duplicateVerifyAtSave(String name, Integer mvnoId) throws Exception{
    	return false;
    }
    public boolean duplicateVerifyCountryAtSave(String name , Integer countryId) throws Exception{
        return false;
    }
    public boolean duplicateVerifyCountryAtEdit(String name , Integer countryId, Integer id) throws Exception{
        return false;
    }

    public boolean duplicateVerifyAtEdit(String name, Integer id, Integer mvnoId) throws Exception{
    	return false;
    }
  //  @JaversAuditable
    public T update(T entity) {
        return getRepository().save(entity);
    }
 //   @JaversAuditable
    public T updateById(Long Id, T entity) {

        if (Id != null) {
            T checkEntity = getRepository().findById(Id).orElse(null);
            if (checkEntity == null) {
                return null;
            }
        }
        return getRepository().save(entity);
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            partnerId = -1;
        }
        return partnerId;
    }
    
    public int getLoggedInMvnoId() {
        int loggedInMvnoId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
            	loggedInMvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
        	loggedInMvnoId = -1;
        }
        return loggedInMvnoId;
    }

    public int getLoggedInStaffId() {
        int loggedInStaffId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInStaffId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getStaffId();
            }
        } catch (Exception e) {
            loggedInStaffId = -1;
        }
        return loggedInStaffId;
    }

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        if (pageSize > MAX_PAGE_SIZE)
            pageSize = MAX_PAGE_SIZE;

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        return pageRequest;
    }

    public void createExcel(Workbook workbook, Sheet sheet, Class clazz, List<DTO> pojoList, Field[] fields) throws Exception {
        new ExcelUtil<DTO>().generateExcel(workbook, sheet, clazz, pojoList, fields);
    }

    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
    }

    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
    }

    public void createPDF(Document doc, Class clazz, List<DTO> pojoList, Field[] fields) throws Exception {
        new PdfUtil<DTO>().generatePdf(doc, clazz, pojoList, fields);
    }

//    public Page<T> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        return null;
//    }
    public Page<T> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        return null;
    }
    public List<java.lang.Long> getServiceAreaIdList() {
        List<java.lang.Long> idList = new ArrayList<>();

        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                idList = staffUserService.get(getLoggedInUserId(),null).getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList());
//                if(idList==null || idList.isEmpty()){
//                    idList = staffUserService.get(1).getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList());
//                }
//                idList.addAll(staffUserService.get(1).getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }

        return idList;
    }

    public List<java.lang.Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = new ArrayList<java.lang.Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }
    public String getJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        }
        return null;
    }
    public Integer getLoggedInMvnoId(Integer custId) {
        Integer mvnoId = null;
        try {
            if(custId != null){
                return customersRepository.getCustomerMvnoIdByCustId(custId);
           }
//            else {
//                SecurityContext securityContext = SecurityContextHolder.getContext();
//                if (null != securityContext.getAuthentication()) {
//                    if(securityContext.getAuthentication().getPrincipal() != null)
//                        return ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
//                }
//            }
        } catch (Exception e) {
            //        ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return null;
    }
}
