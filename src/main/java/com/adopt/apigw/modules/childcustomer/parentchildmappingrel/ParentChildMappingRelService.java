package com.adopt.apigw.modules.childcustomer.parentchildmappingrel;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.devCode.ToolService;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.childcustomer.dto.ChildCustPojo;
import com.adopt.apigw.modules.childcustomer.entity.ChildCustomer;
import com.adopt.apigw.modules.childcustomer.implemetation.ChildCustomerImpl;
import com.adopt.apigw.modules.childcustomer.repository.ChildCustomerRepo;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.utils.APIConstants;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParentChildMappingRelService {
    @Autowired
    private ParentChildMappingRepo parentChildMappingRepo;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private ChildCustomerImpl childCustomerservice;
    @Autowired
    private ChildCustomerRepo childCustomerRepo;
    private final Logger logger = LoggerFactory.getLogger(ParentChildMappingRelService.class);
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private AuditLogService auditLogService;


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ToolService toolService;

    @Autowired
    private CustomersService customersService;
    public void saveParentChildRel(ChildCustPojo childCustomer, Object[] customers) {
        logger.info("Starting to save Parent-Child mapping for child user: {}", childCustomer.getUserName());

        try {
            ParentChildMappingRel parentChildMappingRel = new ParentChildMappingRel();

//            Customers customers = customersRepository.findById(childCustomer.getParentCustId().intValue()).orElse(null);

            parentChildMappingRel.setParentCustomer(childCustomer.getParentCustId());
            if(Objects.nonNull(customers)) {
                Object[] innerArray = (Object[]) customers[0];
                Integer id = (Integer) innerArray[0];
                String username = (String) innerArray[1];
                String firstname = (String) innerArray[2];
                String lastname = (String) innerArray[3];
                Integer partnerId = (Integer) innerArray[4];
                String acctNo = innerArray[5].toString();

                parentChildMappingRel.setParentUsername(username);
                parentChildMappingRel.setParentFirstName(firstname);
                parentChildMappingRel.setParentLastName(lastname);
                parentChildMappingRel.setPartnerId(partnerId != null ? partnerId.longValue() : null);
                parentChildMappingRel.setParentAccountNumber(acctNo);

                logger.debug("Mapped parent customer ID: {} to child user: {}", id, childCustomer.getUserName());
            }
            Long mvnoId = childCustomer.getMvnoId().longValue();

            // Set child customer
//            Optional<ChildCustomer> optionalchildCustomer = childCustomerRepo.findByUserNameAndMvnoId(childCustomer.getUserName(),mvnoId);
            Object[] optionalchildCustomer = childCustomerRepo.findBasicChildCustomerInfoByUserNameAndMvnoId(childCustomer.getUserName(), mvnoId);
            if(optionalchildCustomer!=null){
                Object[] childData = (Object[]) optionalchildCustomer[0];
//                ChildCustomer customer = optionalchildCustomer.get();
                parentChildMappingRel.setChildCustomer((Long) childData[0]);
                parentChildMappingRel.setChildUsername((String) childData[1]);
                parentChildMappingRel.setChildFirstName((String) childData[2]);
                parentChildMappingRel.setChildLastName((String) childData[3]);
                parentChildMappingRel.setStatus((String) childData[4]);
                parentChildMappingRel.setChildEmail((String) childData[5]);
                parentChildMappingRel.setChildMobile((String) childData[6]);
                parentChildMappingRel.setIsDelete((Boolean) childData[7]);
                parentChildMappingRel.setChildPassword((String) childData[8]);
//                parentChildMappingRel.setParentAccountNumber((String) childData[9]);

            }else {
                Object[] childData = (Object[]) optionalchildCustomer[0];
                logger.warn("No parent customer found for ID: {}", (Long) childData[0]);
            }
            if(childCustomer.getIsParent()==null){
                parentChildMappingRel.setIsparent(false);
            }else {
                parentChildMappingRel.setIsparent(childCustomer.getIsParent());
            }
            // Set additional metadata
            parentChildMappingRel.setMvno(mvnoId);
            if(childCustomer.getIsParentWalletUsable() == null){
                childCustomer.setIsParentWalletUsable(false);
            }
            parentChildMappingRel.setIsParentWalletUsable(childCustomer.getIsParentWalletUsable());
//            parentChildMappingRel.setCreatedByStaff(childCustomerservice.getLoggedInUser().getStaffId().longValue());

            // Save entity
            parentChildMappingRel.setIsDelete(false);
            childCustomerRepo.updateIsDeleteFalseByUsernameAndMvnoId(childCustomer.getUserName(), mvnoId);
            parentChildMappingRepo.saveAndFlush(parentChildMappingRel);
            dataShareInRevenue(parentChildMappingRel);
            logger.info("Successfully saved Parent-Child mapping for child user: {}", childCustomer.getUserName());

        } catch (Exception e) {
            logger.error("Error saving Parent-Child mapping for child user: {}", childCustomer.getUserName(), e);
        }
    }

    private void dataShareInRevenue(ParentChildMappingRel parentChildMappingRel) {
        try {
            KafkaMessageData messageData = new KafkaMessageData(parentChildMappingRel, parentChildMappingRel.getClass().getSimpleName());
            logger.info("Sending data to Kafka for revenue share: {}", messageData);
            kafkaMessageSender.send(messageData);
            logger.info("Kafka message sent successfully for ParentChildMappingRel with ID: {}", parentChildMappingRel.getId());
        } catch (Exception e) {
            logger.error("Failed to send ParentChildMappingRel to Kafka. Data: {}", parentChildMappingRel, e);
            // Optional: retry logic, custom exception or alert
        }
    }


    public ResponseEntity<?> getParentChildRel(String username, Integer mvnoId) {
        HashMap<String, Object> responseMap = new HashMap<>();
        int responseCode = APIConstants.INTERNAL_SERVER_ERROR;
        try {
            logger.info("Fetching Parent-Child mapping for username: {}", username);
            // TODO: pass mvnoID manually 6/5/2025
            Optional<ChildCustomer> childCustomer = childCustomerRepo.findByUserNameAndMvnoIdInAndIsdeleted(
                    username,
                    Arrays.asList(mvnoId.longValue(),1L),false
);
            if (childCustomer.isPresent()) {
                List<ParentChildMappingRel> mappings = parentChildMappingRepo.findAllByChildCustomerAndIsDelete(childCustomer.get().getId(),false);
                if (!mappings.isEmpty()) {
                    responseMap.put("responseMessage", APIConstants.SUCCESS_STATUS);
                    responseMap.put("parentChildMappingRel", mappings);
                    responseCode = APIConstants.SUCCESS;
                    logger.info("Successfully fetched {} records for username {}", mappings.size(), username);
                } else {
                    responseMap.put("responseMessage", "No child found.");
                    responseCode = APIConstants.NO_CONTENT_FOUND;
                    logger.warn("No Parent-Child mappings found for username: {}", username);
                }
            } else {
                responseMap.put("responseMessage", "No child found.");
                responseCode = APIConstants.NO_CONTENT_FOUND;
                logger.warn("Child customer not found for username: {}", username);
            }
        } catch (Exception e) {
            responseMap.put("responseMessage", APIConstants.FAIL);
            responseCode = APIConstants.FAIL;
            logger.error("Error occurred while fetching Parent-Child mappings for username: {}", username, e);
        }

        return childCustomerservice.apiResponse(responseCode, responseMap);
    }

    public void updateParentChildRel(ChildCustPojo childCustomer) {
        Long mvnoId = null;
        String username = childCustomer.getUserName();

        try {
            // TODO: pass mvnoID manually 6/5/2025
            mvnoId = childCustomer.getMvnoId().longValue();
            logger.info("Attempting to update ParentChildMappingRel for username: {}, mvnoId: {}", username, mvnoId);

            Optional<ParentChildMappingRel> childMappingRel = parentChildMappingRepo.findByChildUsernameAndMvnoAndIsparent(username, mvnoId, true);

            if (childMappingRel.isPresent()) {
                Optional<Customers> parentCustomers = customersRepository.findById(childCustomer.getParentCustId().intValue());
                ParentChildMappingRel customer = childMappingRel.get();

                if (parentCustomers.isPresent()){
                    Customers parentcust = parentCustomers.get();
                    customer.setPartnerId((long) childCustomerservice.getLoggedInUser().getPartnerId());
                    customer.setParentFirstName(parentcust.getFirstname());
                    customer.setParentLastName(parentcust.getLastname());
                }
                customer.setChildEmail(childCustomer.getEmail());
                customer.setChildMobile(childCustomer.getMobileNumber());
                customer.setChildFirstName(childCustomer.getFirstName());
                customer.setChildLastName(childCustomer.getLastName());
                customer.setChildPassword(childCustomer.getPassword());
                customer.setStatus(childCustomer.getStatus());

                customer.setCreatedByStaff(childCustomerservice.getLoggedInUser().getStaffId().longValue());

                parentChildMappingRepo.saveAndFlush(customer);
                logger.info("Successfully updated ParentChildMappingRel for username: {}", username);
            } else {
                logger.warn("No ParentChildMappingRel found for username: {} with mvnoId: {} and isParent = true", username, mvnoId);
            }

        } catch (Exception e) {
            logger.error("Exception occurred while updating ParentChildMappingRel for username: {} and mvnoId: {}", username, mvnoId, e);
        }
    }

    public Page<ParentChildMappingRel> getParentChildByParentCustId(Long id, Integer page , Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        logger.info("Fetching child customers with filters: {}, page: {}, pageSize: {}, sortBy: {}, sortOrder: {}, status: {}", page, pageSize);

        try {
            QParentChildMappingRel qParentChildMappingRel = QParentChildMappingRel.parentChildMappingRel;
            BooleanExpression booleanExpression = qParentChildMappingRel.isNotNull().and(qParentChildMappingRel.isDelete.eq(false));

            booleanExpression = booleanExpression.and(qParentChildMappingRel.parentCustomer.eq(id).and(qParentChildMappingRel.isparent.isFalse()));
            // Build the query
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            QueryResults<ParentChildMappingRel> queryResults = queryFactory.select(
                            Projections.constructor(
                                    ParentChildMappingRel.class,
                                    qParentChildMappingRel.id,
                                    qParentChildMappingRel.parentUsername,
                                    qParentChildMappingRel.childUsername,
                                    qParentChildMappingRel.createdByStaff,
                                    qParentChildMappingRel.mvno,
                                    qParentChildMappingRel.parentCustomer,
                                    qParentChildMappingRel.childCustomer,
                                    qParentChildMappingRel.isparent,
                                    qParentChildMappingRel.partnerId,
                                    qParentChildMappingRel.parentFirstName,
                                    qParentChildMappingRel.parentLastName,
                                    qParentChildMappingRel.childFirstName,
                                    qParentChildMappingRel.childLastName,
                                    qParentChildMappingRel.childEmail,
                                    qParentChildMappingRel.childMobile,
                                    qParentChildMappingRel.status,
                                    qParentChildMappingRel.isDelete,
                                    qParentChildMappingRel.parentAccountNumber)
                    )
                    .from(qParentChildMappingRel)
                    .where(booleanExpression)
                    .orderBy(qParentChildMappingRel.id.desc())
                    .offset(pageRequest.getOffset())
                    .limit(pageRequest.getPageSize())
                    .fetchResults();

            logger.info("Query executed successfully. Total records: {}", queryResults.getTotal());

            return new PageImpl<>(queryResults.getResults(), pageRequest, queryResults.getTotal());

        } catch (Exception e) {
            logger.error("Error occurred while fetching child customers", e);
            return Page.empty(); // Safer than returning null
        }
    }

    public ResponseEntity<?> getParentChildRelBYParentId(Long parentId) {
        HashMap<String, Object> responseMap = new HashMap<>();
        int responseCode = APIConstants.INTERNAL_SERVER_ERROR;
        try {
            logger.info("Fetching Parent-Child mapping for username: {}", parentId);

            List<ParentChildMappingRel> childCustomer = parentChildMappingRepo.findByparentCustomer(parentId);
            childCustomer = childCustomer.stream().filter(toolService.distinctByKey(ParentChildMappingRel::getChildUsername)).collect(Collectors.toList());
            if (!childCustomer.isEmpty()) {
                if (!childCustomer.isEmpty() && childCustomer.size()>0) {
                    responseMap.put("responseMessage", APIConstants.SUCCESS_STATUS);
                    responseMap.put("parentChildMappingRel", childCustomer);
                    responseCode = APIConstants.SUCCESS;
                    logger.info("Successfully fetched {} records for username {}", childCustomer.size(), parentId);
                } else {
                    responseMap.put("responseMessage", "No child found.");
                    responseCode = APIConstants.NO_CONTENT_FOUND;
                    logger.warn("No Parent-Child mappings found for username: {}", parentId);
                }
            } else {
                responseMap.put("responseMessage", "No child found.");
                responseCode = APIConstants.NO_CONTENT_FOUND;
                logger.warn("Child customer not found for username: {}", parentId);
            }
        } catch (Exception e) {
            responseMap.put("responseMessage", APIConstants.FAIL);
            responseCode =
                    APIConstants.FAIL;
            logger.error("Error occurred while fetching Parent-Child mappings for username: {}", parentId, e);
        }

        return childCustomerservice.apiResponse(responseCode, responseMap);
    }


    public ResponseEntity<?> getChildUpdate(UpdateChildDTO updateChildDTO) {
        HashMap<String, Object> responseMap = new HashMap<>();
        int responseCode = APIConstants.INTERNAL_SERVER_ERROR;
        try {
            logger.info("Fetching Parent-Child mapping for username: {}", updateChildDTO.getFirstName());
//            Optional<ChildCustomer> childCustomerOptional = childCustomerRepo.findById(updateChildDTO.getChildId());
            ParentChildMappingRel parentChildMappingRel = parentChildMappingRepo.findById(updateChildDTO.getChildId()).orElse(null);
            if (parentChildMappingRel !=null){
                Optional<ChildCustomer> childCustomerOptional = childCustomerRepo.findById(parentChildMappingRel.getChildCustomer());

                ChildCustomer childCustomer = childCustomerOptional.get();
                childCustomer.setStatus(updateChildDTO.getStatus());
                childCustomer.setFirstName(updateChildDTO.getFirstName());
                childCustomer.setEmail(updateChildDTO.getEmail());
                childCustomer.setLastName(updateChildDTO.getLastName());
                childCustomerRepo.save(childCustomer);
            }
            if(parentChildMappingRel != null){
                parentChildMappingRel.setChildFirstName(updateChildDTO.getFirstName());
                parentChildMappingRel.setChildLastName(updateChildDTO.getLastName());
                parentChildMappingRel.setStatus(updateChildDTO.getStatus());
                parentChildMappingRel.setChildEmail(updateChildDTO.getEmail());
//                parentChildMappingRel.setIsParentWalletUsable(updateChildDTO.getIsParentWalletUsable());
                parentChildMappingRepo.save(parentChildMappingRel);
                List<ParentChildMappingRel> parentChildMappingRelList = parentChildMappingRepo.findAllByChildCustomerAndIsDelete(parentChildMappingRel.getChildCustomer(),false);
                parentChildMappingRelList.stream().forEach(parentChildMappingRel1 -> {
                    parentChildMappingRel1.setChildFirstName(updateChildDTO.getFirstName());
                    parentChildMappingRel1.setChildLastName(updateChildDTO.getLastName());
                    parentChildMappingRel1.setStatus(updateChildDTO.getStatus());
                    parentChildMappingRel1.setChildEmail(updateChildDTO.getEmail());
//                    parentChildMappingRel1.setIsParentWalletUsable(updateChildDTO.getIsParentWalletUsable());
                });
                parentChildMappingRepo.saveAll(parentChildMappingRelList);
                responseMap.put("responseMessage","Child customer update successfully.");
                responseCode = APIConstants.SUCCESS;
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHILD_CUSTOMER, AclConstants.OPERATION_CHILDCUSTOMER_EDIT, null, null, parentChildMappingRel.getParentCustomer(), parentChildMappingRel.getChildFirstName());
            }
             else {
                responseMap.put("responseMessage", "No child found.");
                responseCode = APIConstants.NO_CONTENT_FOUND;
                logger.warn("Child customer not found for username: {}", updateChildDTO);
            }
        } catch (Exception e) {
            responseMap.put("responseMessage", APIConstants.FAIL);
            responseCode =
                    APIConstants.FAIL;
            logger.error("Error occurred while fetching Parent-Child mappings for username: {}", updateChildDTO, e);
        }

        return childCustomerservice.apiResponse(responseCode, responseMap);
    }


    public ResponseEntity<?> getChildDelete(Long childId ) {
        HashMap<String, Object> responseMap = new HashMap<>();
        int responseCode = APIConstants.INTERNAL_SERVER_ERROR;
        try {
            logger.info("Fetching Parent-Child mapping for username: {}", childId);
            ParentChildMappingRel parentChildMappingRel = parentChildMappingRepo.findById(childId).orElse(null);
            if(parentChildMappingRel != null){
                Optional<ChildCustomer> childCustomerOptional = childCustomerRepo.findById(parentChildMappingRel.getChildCustomer());
                if (childCustomerOptional.isPresent()){
                    ChildCustomer childCustomer = childCustomerOptional.get();
                    childCustomer.setIsdeleted(true);
                    childCustomerRepo.save(childCustomer);
                }
                parentChildMappingRel.setIsDelete(true);
                parentChildMappingRepo.save(parentChildMappingRel);
                responseMap.put("responseMessage","Child customer delete successfully.");
                responseCode =
                        APIConstants.SUCCESS;
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHILD_CUSTOMER, AclConstants.OPERATION_CHILDCUSTOMER_DELETE, null, null, parentChildMappingRel.getParentCustomer(), parentChildMappingRel.getChildFirstName());
            }
            else {
                responseMap.put("responseMessage", "No child found.");
                responseCode = APIConstants.NO_CONTENT_FOUND;
                logger.warn("Child customer not found for username: {}", childId);
            }
        } catch (Exception e) {
            responseMap.put("responseMessage", APIConstants.FAIL);
            responseCode =
                    APIConstants.FAIL;
            logger.error("Error occurred while fetching Parent-Child mappings for username: {}", childId, e);
        }

        return childCustomerservice.apiResponse(responseCode, responseMap);
    }

    public ResponseEntity<?> getChildById(Long childId) {
        HashMap<String, Object> responseMap = new HashMap<>();
        int responseCode = APIConstants.INTERNAL_SERVER_ERROR;
        try {
            logger.info("Fetching Parent-Child mapping for username: {}", childId);
            ParentChildMappingRel parentChildMappingRel = parentChildMappingRepo.findById(childId).orElse(null);
            if(parentChildMappingRel != null){
                responseMap.put("responseMessage","Child customer found successfully.");
                responseMap.put("data",parentChildMappingRel);
                responseCode =
                        APIConstants.SUCCESS;
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHILD_CUSTOMER, AclConstants.OPERATION_CHILDCUSTOMER_FETCH, null, null, parentChildMappingRel.getParentCustomer(), "Fetched " +parentChildMappingRel.getId() + " child customers");

            }
            else {
                responseMap.put("responseMessage", "No child found.");
                responseCode = APIConstants.NO_CONTENT_FOUND;
                logger.warn("Child customer not found for username: {}", childId);
            }
        } catch (Exception e) {
            responseMap.put("responseMessage", APIConstants.FAIL);
            responseCode =
                    APIConstants.FAIL;
            logger.error("Error occurred while fetching Parent-Child mappings for username: {}", childId, e);
        }

        return childCustomerservice.apiResponse(responseCode, responseMap);
    }

    public Page<ParentChildMappingRel> getchildSearch(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder, String status,Long parentId) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        logger.info("Fetching child customers with filters: {}, page: {}, pageSize: {}, sortBy: {}, sortOrder: {}, status: {}", filters, page, pageSize, sortBy, sortOrder, status);

        try {
            QParentChildMappingRel qParentChildMappingRel = QParentChildMappingRel.parentChildMappingRel;
            BooleanExpression booleanExpression = qParentChildMappingRel.isNotNull().and(qParentChildMappingRel.isDelete.eq(false));
            booleanExpression = booleanExpression.and(qParentChildMappingRel.parentCustomer.eq(parentId));
            booleanExpression= booleanExpression.and(qParentChildMappingRel.isparent.isFalse());

            for (GenericSearchModel searchModel : filters) {
                String column = searchModel.getFilterColumn();
                String value = searchModel.getFilterValue();

                if (SearchConstants.CUST_USERNAME.equalsIgnoreCase(column) && value != null) {
                    booleanExpression = booleanExpression.and(qParentChildMappingRel.childUsername.containsIgnoreCase(value));
                    logger.debug("Filtering by userName contains: {}", value);
                } else if (SearchConstants.ANY.equalsIgnoreCase(column) && value != null && !value.isEmpty()) {
                    booleanExpression = booleanExpression.and(
                            qParentChildMappingRel.childUsername.likeIgnoreCase("%" + value + "%")
                                    .or(qParentChildMappingRel.childFirstName.likeIgnoreCase("%" + value + "%"))
                                    .or(qParentChildMappingRel.childMobile.contains(value))
                    );
                    logger.debug("Filtering by ANY field (username/firstName/mobileNumber) contains: {}", value);
                } else {
                    logger.warn("Unsupported filter column: {}", column);
                }
            }

            // Build the query
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            QueryResults<ParentChildMappingRel> queryResults = queryFactory.select(
                            Projections.constructor(
                                    ParentChildMappingRel.class,
                                    qParentChildMappingRel.id,
                                    qParentChildMappingRel.parentUsername,
                                    qParentChildMappingRel.childUsername,
                                    qParentChildMappingRel.createdByStaff,
                                    qParentChildMappingRel.mvno,
                                    qParentChildMappingRel.parentCustomer,
                                    qParentChildMappingRel.childCustomer,
                                    qParentChildMappingRel.isparent,
                                    qParentChildMappingRel.partnerId,
                                    qParentChildMappingRel.parentFirstName,
                                    qParentChildMappingRel.parentLastName,
                                    qParentChildMappingRel.childFirstName,
                                    qParentChildMappingRel.childLastName,
                                    qParentChildMappingRel.childEmail,
                                    qParentChildMappingRel.childMobile,
                                    qParentChildMappingRel.status,
                                    qParentChildMappingRel.isDelete,
                                    qParentChildMappingRel.parentAccountNumber)
                    )
                    .from(qParentChildMappingRel)
                    .where(booleanExpression)
                    .orderBy(qParentChildMappingRel.id.desc())
                    .offset(pageRequest.getOffset())
                    .limit(pageRequest.getPageSize())
                    .fetchResults();

            logger.info("Query executed successfully. Total records: {}", queryResults.getTotal());
            if (!queryResults.getResults().isEmpty()) {
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHILD_CUSTOMER, AclConstants.OPERATION_CHILDCUSTOMER_SEARCH, null, null, parentId, filters.get(0).getFilterValue());
            }
            return new PageImpl<>(queryResults.getResults(), pageRequest, queryResults.getTotal());

        } catch (Exception e) {
            logger.error("Error occurred while fetching child customers", e);
            return Page.empty(); // Safer than returning null
        }
    }

    public ResponseEntity<?> verifyChildCustomer(VerifyChildDTO verifyChildDTO) {
        HashMap<String, Object> responseMap = new HashMap<>();
        int responseCode = APIConstants.INTERNAL_SERVER_ERROR;
        try {
            logger.info("Fetching Parent-Child mapping for username: {}", verifyChildDTO.getUserName());
            String username = verifyChildDTO.getUserName();
            String mobileNumber = verifyChildDTO.getMobileNumber();
            Long parentId = verifyChildDTO.getParentId();
            // TODO: pass mvnoID manually 6/5/2025
            Long mvnoId = customersService.getMvnoIdFromCurrentStaff(verifyChildDTO.getParentId().intValue()).longValue();
            List<ParentChildMappingRel> exactMatch = parentChildMappingRepo.findByparentCustomerAndMobileNumberAndUsername(parentId, mobileNumber, username);
            if (!exactMatch.isEmpty()) {
                responseMap.put("responseMessage", "Child Customer already exists under this parent.");
                responseCode = HttpStatus.CONFLICT.value();
                return childCustomerservice.apiResponse(responseCode, responseMap);
            }
            List<ParentChildMappingRel> sameUserSameMobile = parentChildMappingRepo.findByMobileNumberAndUsername(mobileNumber, username);
            if (!sameUserSameMobile.isEmpty()) {
                boolean differentParentExists = sameUserSameMobile.stream()
                        .anyMatch(rel -> !rel.getParentCustomer().equals(parentId));
                if (differentParentExists) {
                    responseMap.put("responseMessage", "Child verified under a different parent. Proceed to create.");
                    responseCode = HttpStatus.OK.value();
                    return childCustomerservice.apiResponse(responseCode, responseMap);
                } else {
                    responseMap.put("responseMessage", "Child Customer already exists under this parent.");
                    responseCode = HttpStatus.CONFLICT.value();
                    return childCustomerservice.apiResponse(responseCode, responseMap);
                }
            }
            List<ParentChildMappingRel> sameMobileList = parentChildMappingRepo.findByChildMobile(mobileNumber);
            if (!sameMobileList.isEmpty()) {
                responseMap.put("responseMessage", "Mobile number is already in use with a different username.");
                responseCode = HttpStatus.CONFLICT.value();
                return childCustomerservice.apiResponse(responseCode, responseMap);
            }

            List<ParentChildMappingRel> sameUsernameList = parentChildMappingRepo.findByChildUsernameAndMvno(username, mvnoId);
            if (!sameUsernameList.isEmpty()) {
                boolean differentMobile = sameUsernameList.stream()
                        .anyMatch(rel -> !mobileNumber.equals(rel.getChildMobile()));
                if (differentMobile) {
                    responseMap.put("responseMessage", "Username already exists with a different mobile number.");
                    responseCode = HttpStatus.CONFLICT.value();
                    return childCustomerservice.apiResponse(responseCode, responseMap);
                }
            }

            responseMap.put("responseMessage", "Child is valid for creation.");
            responseCode = HttpStatus.OK.value();

        } catch (Exception e) {
            responseMap.put("responseMessage", APIConstants.FAIL);
            responseCode =
                    APIConstants.FAIL;
            logger.error("Error occurred while fetching Parent-Child mappings for username: {}", verifyChildDTO, e);
        }

        return childCustomerservice.apiResponse(responseCode, responseMap);
    }
    public List<ParentChildMappingRel> getParentChildMappingList(String mobileNumber, Integer mvnoId){
        List<ParentChildMappingRel> parentChildMappingRelList = new ArrayList<>();
        // TODO: pass mvnoID manually 6/5/2025
//        Long mvnoId = customersService.getMvnoIdFromCurrentStaff(null).longValue();
        parentChildMappingRelList = parentChildMappingRepo.findByChildMobileAndMvnoIn(mobileNumber , Arrays.asList(mvnoId.longValue(),1L));
        return parentChildMappingRelList;
    }

    public boolean isChildUserExist(String username, Long mvnoId) {
        return parentChildMappingRepo.countByUsernameAndMvnoNative(username, Arrays.asList(mvnoId, 1L)) > 0;
    }
    public ResponseEntity<?> reactivateChildCustomer(Long childId ) {
        HashMap<String, Object> responseMap = new HashMap<>();
        int responseCode = APIConstants.INTERNAL_SERVER_ERROR;
        try {
            logger.info("Fetching Parent-Child mapping for username: {}", childId);
            ParentChildMappingRel parentChildMappingRel = parentChildMappingRepo.findById(childId).orElse(null);
            if(parentChildMappingRel != null){
                Optional<ChildCustomer> childCustomerOptional = childCustomerRepo.findById(parentChildMappingRel.getChildCustomer());
                if (childCustomerOptional.isPresent()){
                    ChildCustomer childCustomer = childCustomerOptional.get();
                    childCustomer.setIsdeleted(false);
                    childCustomerRepo.save(childCustomer);
                }
                parentChildMappingRel.setIsDelete(false);
                parentChildMappingRepo.save(parentChildMappingRel);
                responseMap.put("responseMessage","Child customer ReActivated successfully.");
                responseCode =
                        APIConstants.SUCCESS;
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHILD_CUSTOMER, AclConstants.OPERATION_CHILDCUSTOMER_DELETE, null, null, parentChildMappingRel.getParentCustomer(), parentChildMappingRel.getChildFirstName());
            }
            else {
                responseMap.put("responseMessage", "No child found.");
                responseCode = APIConstants.NO_CONTENT_FOUND;
                logger.warn("Child customer not found for username: {}", childId);
            }
        } catch (Exception e) {
            responseMap.put("responseMessage", APIConstants.FAIL);
            responseCode =
                    APIConstants.FAIL;
            logger.error("Error occurred while fetching Parent-Child mappings for username: {}", childId, e);
        }

        return childCustomerservice.apiResponse(responseCode, responseMap);
    }

}
