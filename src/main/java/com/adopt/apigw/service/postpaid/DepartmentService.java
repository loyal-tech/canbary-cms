package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveDepartmentSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateDepartmentSharedDataMessage;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.model.postpaid.Department;
import com.adopt.apigw.model.postpaid.DepartmentPlanMapping;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.pojo.api.DepartmentPojo;
import com.adopt.apigw.repository.postpaid.DepartmentRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService extends AbstractService<Department, DepartmentPojo, Integer> {

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private DepartmentRepository entityRepository;

    @Override
    protected JpaRepository<Department, Integer> getRepository() {
        return entityRepository;
    }

    public static final String MODULE = "[DepartmentService]";
    private static final Logger logger = LoggerFactory.getLogger(APIController.class);
//
//    public Page<Department> searchEntity(String searchText, Integer pageNumber, int pageSize) {
//        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
//        return entityRepository.searchEntity(searchText, pageRequest, getMvnoIdFromCurrentStaff());
//    }
//
//    public List<Department> getAllActiveEntities() {
//        return entityRepository.findByStatusAndIsDeleteIsFalseOrderByIdDesc(CommonConstants.ACTIVE_STATUS)
//                .stream().filter(department -> department.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || department.getMvnoId() == null || department.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
//
//    }
//
//    public List<Department> getAllEntities() {
//        return entityRepository.findAll()
//                .stream().filter(department -> department.getMvnoId() == getMvnoIdFromCurrentStaff() || department.getMvnoId() == null).collect(Collectors.toList());
//    }
//
//    public Page<Department> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//        Page<Department> page = null;
//        if (getMvnoIdFromCurrentStaff() == 1)
//            return entityRepository.findAll(pageRequest);
//        if (null == filterList || 0 == filterList.size())
//            return entityRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()));
//        else
//            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
//    }
//
//    public void deleteDepartment(Integer id) throws Exception {
//        String SUBMODULE = MODULE + "[deleteDepartment()]";
//        try {
//            Department department = entityRepository.getOne(id);
//            department.setIsDelete(true);
//            entityRepository.save(department);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    public Department getDepartmentForAdd() {
//        return new Department();
//    }
//
//    public Department getDepartmentForEdit(Integer id) throws Exception {
//        return entityRepository.getOne(id);
//    }
//
//
//    public Department saveDepartment(Department department) throws Exception {
//        if (getMvnoIdFromCurrentStaff() != null) {
//            department.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        Department save = entityRepository.save(department);
//        return save;
//    }
//
//    public DepartmentPojo save(DepartmentPojo pojo) throws Exception {
//        String SUBMODULE = MODULE + "save()";
//        try {
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//            Department obj = convertDepartmentPojoToDepartmentModel(pojo);
//            obj = saveDepartment(obj);
//            pojo = convertDepartmentModelToDepartmentPojo(obj);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojo;
//    }
//
//    public DepartmentPojo update(DepartmentPojo pojo, HttpServletRequest req) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        String SUBMODULE = MODULE + "update()";
//        Department old1 = get(pojo.getId());
//        try {
//
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//            Department dbvalue = getDepartmentForEdit(pojo.getId());
//            Department obj = convertDepartmentPojoToDepartmentModel(pojo);
//            //    Department olddepartment=new Department(pojo,pojo.getId());
//            Department updatedvalue = new Department(pojo, pojo.getId());
//
//            getDepartmentForUpdateAndDelete(obj.getId());
//            String updatedValues = CommonUtils.getUpdatedDiff(dbvalue, updatedvalue);
//            obj = saveDepartment(obj);
//
//            pojo = convertDepartmentModelToDepartmentPojo(obj);
//            RESP_CODE = APIConstants.SUCCESS;
//            logger.info("Department with old  : " + updatedValues + " updated Successfully; " + " request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), RESP_CODE);
//
//        } catch (Exception ex) {
//            logger.info("Unable to Update Department with old  : " + old1 + " is updated to : " + pojo.getName() + " " + "; request: { From : {}}; Response : {{}};Exception:{}", req.getHeader("requestFrom"), RESP_CODE, ex.getMessage());
//            throw ex;
//        }
//        return pojo;
//    }
//
//    @Override
//    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
//            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count >= 1) {
//                Integer countEdit;
//                if (getMvnoIdFromCurrentStaff() == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
//                else
//                    countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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
//    public Department convertDepartmentPojoToDepartmentModel(DepartmentPojo pojo) throws Exception {
//        String SUBMODULE = MODULE + " [convertDepartmentPojoToDepartmentModel()] ";
//        Department department = null;
//        try {
//            if (pojo != null) {
//                department = new Department();
//                if (pojo.getId() != null) {
//                    department.setId(pojo.getId());
//                }
//                department.setName(pojo.getName());
//                department.setStatus(pojo.getStatus());
//                if (pojo.getMvnoId() != null) {
//                    department.setMvnoId(pojo.getMvnoId());
//                }
//                return department;
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return null;
//    }
//
//    public DepartmentPojo convertDepartmentModelToDepartmentPojo(Department department) throws Exception {
//        String SUBMODULE = MODULE + " [convertDepartmentModelToDepartmentPojo()] ";
//        DepartmentPojo pojo = null;
//        try {
//            if (department != null) {
//                pojo = new DepartmentPojo();
//                pojo.setId(department.getId());
//                pojo.setName(department.getName());
//                pojo.setStatus(department.getStatus());
//                pojo.setCreatedById(department.getCreatedById());
//                pojo.setLastModifiedById(department.getLastModifiedById());
//                pojo.setCreatedByName(department.getCreatedByName());
//                pojo.setLastModifiedByName(department.getLastModifiedByName());
//                pojo.setCreatedate(department.getCreatedate());
//                pojo.setUpdatedate(department.getUpdatedate());
//                pojo.setDisplayId(department.getId());
//                pojo.setDisplayName(department.getName());
//                if (department.getMvnoId() != null) {
//                    pojo.setMvnoId(department.getMvnoId());
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojo;
//    }
//
//    public List<DepartmentPojo> convertResponseModelIntoPojo(List<Department> departmentList) throws Exception {
//        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
//        List<DepartmentPojo> pojoListRes = new ArrayList<DepartmentPojo>();
//        try {
//            if (departmentList != null && departmentList.size() > 0) {
//                for (Department department : departmentList) {
//                    pojoListRes.add(convertDepartmentModelToDepartmentPojo(department));
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojoListRes;
//    }
//
//    public void validateRequest(DepartmentPojo pojo, Integer operation) {
//
//        if (pojo == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
//        }
//        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
//            if (pojo.getId() != null)
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
//        }
//        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)
//                || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
//        }
//        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
//                || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
//        }
//    }
//
//    public List<Department> getName(String n) {
//        QDepartment qDepartment = QDepartment.department;
//        BooleanExpression booleanExpression = qDepartment.isNotNull()
//                .and(qDepartment.name.containsIgnoreCase(n));
//        return (List<Department>) entityRepository.findAll(booleanExpression);
//    }
//
//
//    public Department getByName(String departmentName) {
//        return entityRepository.findByNameAndIsDeleteIsFalse(departmentName);
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
//        Sheet sheet = workbook.createSheet("Department");
//        List<DepartmentPojo> departmentPojos = convertResponseModelIntoPojo(entityRepository.findAll());
//        createExcel(workbook, sheet, DepartmentPojo.class, departmentPojos, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{
//                DepartmentPojo.class.getDeclaredField("id"),
//                DepartmentPojo.class.getDeclaredField("name"),
//                DepartmentPojo.class.getDeclaredField("status"),
//        };
//    }
//
//    @Override
//    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
//        List<DepartmentPojo> departmentPojos = convertResponseModelIntoPojo(entityRepository.findAll());
//        createPDF(doc, DepartmentPojo.class, departmentPojos, getFields());
//    }
//
//    public Page<Department> getDepartmentByName(String s1, PageRequest pageRequest) {
//        Page<Department> departmentList = null;
//        QDepartment qDepartment = QDepartment.department;
//        BooleanExpression booleanExpression = qDepartment.isNotNull()
//                .and(qDepartment.isDelete.eq(false))
//                .and(qDepartment.name.likeIgnoreCase("%" + s1 + "%"))
//                .or(qDepartment.status.equalsIgnoreCase(s1));
//        if (getMvnoIdFromCurrentStaff() == 1) {
//            //return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(s1, pageRequest);
//            return entityRepository.findAll(booleanExpression, pageRequest);
//        } else {
//            //return entityRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            booleanExpression = booleanExpression.and(qDepartment.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//            return entityRepository.findAll(booleanExpression, pageRequest);
//        }
//    }
//
//    @Override
//    public Page<Department> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = MODULE + " [search()] ";
//        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//        try {
//            for (GenericSearchModel searchModel : filterList) {
//                if (null != searchModel.getFilterColumn()) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getDepartmentByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                } else
//                    throw new RuntimeException("Please Provide Search Column!");
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return null;
//    }
//
//    @Override
//    public Department get(Integer id) {
//        Department department = super.get(id);
//        if (getMvnoIdFromCurrentStaff().intValue() == 1 || (department.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || department.getMvnoId().intValue() == 1))
//            return department;
//        return null;
//    }
//
//    public Department getDepartmentForUpdateAndDelete(Integer id) {
//        Department department = get(id);
//        if (department == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == department.getMvnoId().intValue()))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        return department;
//    }


    public void saveDepartment(SaveDepartmentSharedDataMessage message){
        Department department = new Department();
        department.setId(message.getId());
        department.setStatus(message.getStatus());
        department.setMvnoId(message.getMvnoId());
        department.setName(message.getName());
        department.setIsDelete(message.getIsDelete());
        List<DepartmentPlanMapping> departmentPlanMappingList=new ArrayList<>();
        if(message.getPlanIds().size()>0) {
            for (Integer planid : message.getPlanIds()) {
                DepartmentPlanMapping departmentPlanMapping = new DepartmentPlanMapping();
                departmentPlanMapping.setPlanId(new PostpaidPlan(planid));
                departmentPlanMapping.setDepartment(department);
                departmentPlanMappingList.add(departmentPlanMapping);
            }
            department.setDepartmentPlanMappings(departmentPlanMappingList);
        }
        entityRepository.save(department);
    }

    public void updateDepartment(UpdateDepartmentSharedDataMessage message){
        Department department = entityRepository.findById(message.getId()).orElse(null);
        if(department!=null){
            department.setStatus(message.getStatus());
            department.setMvnoId(message.getMvnoId());
            department.setName(message.getName());
            department.setIsDelete(message.getIsDelete());
            List<DepartmentPlanMapping> departmentPlanMappingList=new ArrayList<>();
            if(message.getPlanIds().size()>0) {
                for (Integer planid : message.getPlanIds()) {
                    DepartmentPlanMapping departmentPlanMapping = new DepartmentPlanMapping();
                    departmentPlanMapping.setPlanId(new PostpaidPlan(planid));
                    departmentPlanMapping.setDepartment(department);
                    departmentPlanMappingList.add(departmentPlanMapping);
                }
                department.setDepartmentPlanMappings(departmentPlanMappingList);
            }
            entityRepository.save(department);
        } else {
            Department department1 = new Department();
            department1.setId(message.getId());
            department1.setStatus(message.getStatus());
            department1.setMvnoId(message.getMvnoId());
            department1.setName(message.getName());
            department1.setIsDelete(message.getIsDelete());
            List<DepartmentPlanMapping> departmentPlanMappingList=new ArrayList<>();
            if(message.getPlanIds().size()>0) {
                for (Integer planid : message.getPlanIds()) {
                    DepartmentPlanMapping departmentPlanMapping = new DepartmentPlanMapping();
                    departmentPlanMapping.setPlanId(new PostpaidPlan(planid));
                    departmentPlanMapping.setDepartment(department);
                    departmentPlanMappingList.add(departmentPlanMapping);
                }
                department.setDepartmentPlanMappings(departmentPlanMappingList);
            }
            entityRepository.save(department1);
        }
    }
}
