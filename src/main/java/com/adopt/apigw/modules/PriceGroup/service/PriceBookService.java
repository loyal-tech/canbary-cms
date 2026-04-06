package com.adopt.apigw.modules.PriceGroup.service;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.service.ExBaseService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.mapper.postpaid.PlangroupMapper;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import com.adopt.apigw.modules.PriceGroup.domain.QPriceBook;
import com.adopt.apigw.modules.PriceGroup.mapper.PriceBookMapper;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookDTO;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookPlanDetailDTO;
import com.adopt.apigw.modules.PriceGroup.repository.PriceBookPlanDtlRepository;
import com.adopt.apigw.modules.PriceGroup.repository.PriceBookRepository;
import com.adopt.apigw.pojo.api.PlanGroupDTO;
import com.adopt.apigw.pojo.api.PostpaidPlanPojo;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.repository.postpaid.PlanGroupRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.service.postpaid.PlanGroupService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
import com.itextpdf.text.Document;
//import javafx.beans.binding.BooleanExpression;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceBookService extends ExBaseAbstractService2<PriceBookDTO, PriceBook, Long> {

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    PriceBookRepository priceBookRepository;
    @Autowired
    PriceBookMapper priceBookMapper;

    @Autowired
    PostpaidPlanMapper postpaidPlanMapper;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    PlanGroupRepository planGroupRepository;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PriceBookPlanDtlRepository priceBookPlanDtlRepository;

    @Autowired
    private PlanGroupService planGroupService;

   @Autowired
   private PlangroupMapper mapper;

   @Autowired
   private ClientServiceSrv clientServiceSrv;


    private static final Logger logger = LoggerFactory.getLogger(PriceBookService.class);


    public PriceBookService(PriceBookRepository repository, PriceBookMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PriceBook Service]";
    }

    public List<PriceBookDTO> getAllActive() {
//        List<PriceBook> priceBooks = priceBookRepository.getAllByStatus().stream().filter(priceBook -> priceBook.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || priceBook.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1) && (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(priceBook.getBuId()))).collect(Collectors.toList());
// TODO: pass mvnoID manually 6/5/2025
        List<PriceBook> priceBooks = priceBookRepository.getAllByStatus().stream().filter(priceBook -> (priceBook.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || priceBook.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (priceBook.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(priceBook.getBuId()))).collect(Collectors.toList());
        List<PriceBookDTO> priceBookDTOList = priceBooks.stream().map(data -> priceBookMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return priceBookDTOList;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = priceBookRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = priceBookRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = priceBookRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }

            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId== 1) count = priceBookRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = priceBookRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = priceBookRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (mvnoId == 1) countEdit = priceBookRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = priceBookRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(mvnoId, 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = priceBookRepository.duplicateVerifyAtEdit2(name, id, mvnoId, getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            QPriceBook qPriceBook = QPriceBook.priceBook;
            Page<PriceBookDTO> bookDTOPage=null;
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            BooleanExpression booleanExpression = qPriceBook.isNotNull().and(qPriceBook.isDeleted.eq(false));
            GenericDataDTO genericDataDTO = new GenericDataDTO();

            if (filterList.size() > 0) {
                for (GenericSearchModel genericSearchModel : filterList) {
                    booleanExpression = booleanExpression.and(qPriceBook.bookname.containsIgnoreCase(genericSearchModel.getFilterValue()));

                }
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qPriceBook.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qPriceBook.mvnoId.eq(1).or(qPriceBook.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qPriceBook.buId.in(getBUIdsFromCurrentStaff()))));
            }

            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            QueryResults<PriceBookDTO> queryResults = queryFactory
                    .select(Projections.constructor(
                            PriceBookDTO.class,
                            qPriceBook.id,
                            qPriceBook.bookname,
                            qPriceBook.createdate,
                            qPriceBook.status,
                            qPriceBook.isDeleted
                    ))
                    .from(qPriceBook)
                    .where(booleanExpression)
                    .orderBy(qPriceBook.id.desc())
                    .offset((page - 1) * pageSize)
                    .limit(pageSize)
                    .fetchResults();

            List<PriceBookDTO> priceBookDTOS = queryResults.getResults();
            long totalRecords = queryResults.getTotal();
            bookDTOPage=new PageImpl<>(priceBookDTOS, PageRequest.of(page - 1, pageSize), totalRecords);


            if (null != priceBookDTOS && 0 < priceBookDTOS.size()) {
                genericDataDTO.setDataList(bookDTOPage.getContent());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(totalRecords);
                genericDataDTO.setPageRecords(bookDTOPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(bookDTOPage.getNumber() + 1);
                genericDataDTO.setTotalPages(bookDTOPage.getTotalPages());
            }
            return genericDataDTO;

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("PriceBook");
        createExcel(workbook, sheet, PriceBookDTO.class, getFields(),mvnoId);
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                PriceBookDTO.class.getDeclaredField("id"),
                PriceBookDTO.class.getDeclaredField("bookname"),
                PriceBookDTO.class.getDeclaredField("description"),
                PriceBookDTO.class.getDeclaredField("validfrom"),
                PriceBookDTO.class.getDeclaredField("validto"),
                PriceBookDTO.class.getDeclaredField("status"),
                PriceBookDTO.class.getDeclaredField("agrPercentage"),
                PriceBookDTO.class.getDeclaredField("tdsPercentage"),
        };
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, PriceBookDTO.class, getFields(),mvnoId);
    }

    public GenericDataDTO getBookByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getBookByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<PriceBook> priceBookList = null;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1)
                priceBookList = priceBookRepository.findAllByBooknameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);

            else if (getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                priceBookList = priceBookRepository.findAllByBooknameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            else
                // TODO: pass mvnoID manually 6/5/2025
                priceBookList = priceBookRepository.findAllByNameAndIsDeleteIsFalse(name, pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());


            if (null != priceBookList && 0 < priceBookList.getSize()) {
                makeGenericResponse(genericDataDTO, priceBookList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public PriceBookDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<PriceBook> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if (getMvnoIdFromCurrentStaff(null) == 1)       // TODO: pass mvnoID manually 6/5/2025
            paginationList = priceBookRepository.findAll(pageRequest);
        else if (null == filterList || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = priceBookRepository.findAll(pageRequest, Arrays.asList(1, mvnoId));
            else
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = priceBookRepository.findAll(pageRequest, mvnoId, getBUIdsFromCurrentStaff());


        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }



    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy1(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<PriceBookDTO> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        QPriceBook qPriceBook=QPriceBook.priceBook;
        BooleanExpression booleanExpression = qPriceBook.isNotNull();
        booleanExpression=booleanExpression.and(qPriceBook.isDeleted.eq(false));
        if (null == filterList || 0 == filterList.size()) {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qPriceBook.mvnoId.in(Arrays.asList(1, getMvnoIdFromCurrentStaff(null))));
            else
            {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qPriceBook.mvnoId.in(Arrays.asList(getMvnoIdFromCurrentStaff(null))));
                booleanExpression = booleanExpression.and(qPriceBook.buId.in(getBUIdsFromCurrentStaff()));
            }
        }

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QueryResults<PriceBookDTO> queryResults = queryFactory
                .select(Projections.constructor(
                        PriceBookDTO.class,
                        qPriceBook.id,
                        qPriceBook.bookname,
                        qPriceBook.createdate,
                        qPriceBook.status,
                        qPriceBook.isDeleted
                ))
                .from(qPriceBook)
                .where(booleanExpression)
                .orderBy(qPriceBook.id.desc())
                .offset((page - 1) * size)
                .limit(size)
                .fetchResults();

        List<PriceBookDTO> priceBookDTOS = queryResults.getResults();
        long totalRecords = queryResults.getTotal();
        paginationList=new PageImpl<>(priceBookDTOS, PageRequest.of(page - 1, size), totalRecords);


        if (null != priceBookDTOS && 0 < priceBookDTOS.size()) {
            genericDataDTO.setDataList(paginationList.getContent());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(totalRecords);
            genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
            genericDataDTO.setTotalPages(paginationList.getTotalPages());
        }else{
            genericDataDTO.setDataList(paginationList.getContent());
            genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
            genericDataDTO.setResponseMessage("No records found.");
            genericDataDTO.setTotalRecords(totalRecords);
            genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
            genericDataDTO.setTotalPages(paginationList.getTotalPages());
        }
        return genericDataDTO;
    }


    public void validateSaveOrUpdateData(PriceBookDTO entityDTO) {

        if(entityDTO.getRevenueType()!=null && entityDTO.getRevenueType().equalsIgnoreCase("Percentage"))
        {
            if(entityDTO.getCommission_on().equalsIgnoreCase("Plan level"))
            {
                if(entityDTO.getPriceBookPlanDetailList()!=null && !entityDTO.getPriceBookPlanDetailList().isEmpty())
                {
                    for(PriceBookPlanDetailDTO dto:entityDTO.getPriceBookPlanDetailList())
                    {
                        if(dto.getRevenueSharePercentage()==null)
                            throw new RuntimeException("Revenue Share Percentage is mandatory, Please add valid value.");

                    }
                }
            }
        }
        if (entityDTO.getRevenueType() == null || entityDTO.getRevenueType().isEmpty()) {
            throw new RuntimeException("Revenue type is mandatory, Please add valid value.");
        } else if (!(entityDTO.getRevenueType().equalsIgnoreCase("Percentage") || entityDTO.getRevenueType().equalsIgnoreCase("Slab"))) {
            throw new RuntimeException("Please add valid revenue type, Percentage or Slab.");
        } else if (entityDTO.getRevenueType().equalsIgnoreCase("Percentage")) {
            if (entityDTO.getPriceBookSlabDetailsList() != null && !entityDTO.getPriceBookSlabDetailsList().isEmpty()) {
                throw new RuntimeException("Percentage revenue share cannot have Slab Details.");
            }
        } else if (entityDTO.getRevenueType().equalsIgnoreCase("Slab")) {
            if (entityDTO.getPriceBookPlanDetailList() != null || !entityDTO.getPriceBookPlanDetailList().isEmpty()) {
                entityDTO.getPriceBookPlanDetailList().forEach(x -> {
                    if (x.getRevenueSharePercentage() != null && !x.getRevenueSharePercentage().equalsIgnoreCase("0"))
                        throw new RuntimeException("Slab Revenue cannot have Revenue Share Percentage.");
                });
            }
        }
    }

    public PriceBookDTO checkAndUpdateAllPlanSelected(PriceBookDTO entityDTO,Integer mvnoId) throws Exception {
        if(entityDTO!=null && entityDTO.getIsAllPlanSelected()!=null && entityDTO.getIsAllPlanSelected())
        {
            PostpaidPlanService postpaidPlanService = SpringContext.getBean(PostpaidPlanService.class);
            List<PostpaidPlan> postpaidPlanList = postpaidPlanService.getAllActiveEntities("NORMAL", Constants.PLAN_GROUP_ALL,mvnoId);
            List<PriceBookPlanDetailDTO> bookPlanDetailDTOS=entityDTO.getPriceBookPlanDetailList();
            if(bookPlanDetailDTOS==null)
                bookPlanDetailDTOS=new ArrayList<>();

            if(entityDTO.getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_SERVICE))
            {
                List<Long> serviceIdList=entityDTO.getServiceCommissionList().stream().map(x->x.getServiceId()).collect(Collectors.toList());
                if(serviceIdList!=null && serviceIdList.size()>0)
                    postpaidPlanList=postpaidPlanList.stream().filter(x->serviceIdList.contains(x.getServiceId().longValue())).collect(Collectors.toList());
            }

            for(PostpaidPlan plan:postpaidPlanList)
            {
                PriceBookPlanDetailDTO dto=new PriceBookPlanDetailDTO();
                dto.setPriceBook(entityDTO);
                PostpaidPlanPojo pojo=new PostpaidPlanPojo();
                pojo.setId(plan.getId());
                dto.setPostpaidPlan(pojo);
                dto.setPartnerofficeprice(0d);
                if(entityDTO.getRevenueType().equalsIgnoreCase("Percentage")) {
                    if (entityDTO.getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN))
                        dto.setRevenueSharePercentage(entityDTO.getRevenueSharePercentage().toString());
                    else
                        dto.setRevenueSharePercentage(null);
                }
                else
                    dto.setRevenueSharePercentage(null);
                dto.setOfferprice(plan.getOfferprice());
                dto.setRegistration("NO");
                dto.setRenewal("NO");
                dto.setRevsharen("YES");
                bookPlanDetailDTOS.add(dto);
            }
            entityDTO.setPriceBookPlanDetailList(bookPlanDetailDTOS);
        }
        return entityDTO;
    }

//    @Override
//    public boolean deleteVerification(Integer id) throws Exception {
//        boolean flag = false;
//        Integer count = priceBookRepository.deleteVerify(id)
//                ;
//        if (count == 0) {
//            flag = true;
//        }
//        return flag;
//    }

    public PriceBookDTO checkAndUpdateAllPlangroupSelected(PriceBookDTO entityDTO) {
        if(entityDTO!=null && entityDTO.getIsAllPlanGroupSelected()!=null && entityDTO.getIsAllPlanGroupSelected()) {

            // TODO: pass mvnoID manually 6/5/2025
            List<PlanGroup> planGroupList = planGroupService.findAllPlanGroupList(getMvnoIdFromCurrentStaff(null), "NORMAL", null, null, null,null);
            planGroupList.stream().map(x -> mapper.domainToDTO(x, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            List<PriceBookPlanDetailDTO> bookPlanDetailDTOS = entityDTO.getPriceBookPlanDetailList();
            if (bookPlanDetailDTOS == null)
                bookPlanDetailDTOS = new ArrayList<>();

            for (PlanGroup plan : planGroupList) {
                PriceBookPlanDetailDTO dto = new PriceBookPlanDetailDTO();
                dto.setPriceBook(entityDTO);
                PlanGroupDTO pojo = new PlanGroupDTO();
                pojo.setPlanGroupId(plan.getPlanGroupId());
                dto.setPlanGroup(pojo);
                dto.setPartnerofficeprice(0d);
                if (entityDTO.getRevenueType().equalsIgnoreCase("Percentage")) {
                    if (entityDTO.getCommission_on() != null && entityDTO.getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN) && entityDTO.getRevenueSharePercentage() != null)
                        dto.setRevenueSharePercentage(entityDTO.getRevenueSharePercentage().toString());
                    else
                        dto.setRevenueSharePercentage(null);
                } else
                    dto.setRevenueSharePercentage(null);
                dto.setRegistration("NO");
                dto.setRenewal("NO");
                dto.setRevsharen("YES");
                bookPlanDetailDTOS.add(dto);
            }
            //bookPlanDetailDTOS = bookPlanDetailDTOS.stream().filter(x -> x.getOfferprice() != null).collect(Collectors.toList());
            entityDTO.setPriceBookPlanDetailList(bookPlanDetailDTOS);
        }

        return entityDTO;
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;

        QPartner qPartner = QPartner.partner;
        QPriceBook qPriceBook = QPriceBook.priceBook;
        BooleanExpression booleanExpression1 = qPriceBook.isDeleted.isNotNull().and(qPriceBook.isDeleted.eq(false));
        booleanExpression1=booleanExpression1.and(qPriceBook.id.eq(id.longValue()));
        List<PriceBook> priceBook = IterableUtils.toList(priceBookRepository.findAll(booleanExpression1));
        BooleanExpression expression = qPartner.isNotNull().and(qPartner.priceBookId.eq(priceBook.get(0)));
        expression= expression.and(qPartner.isDelete.eq(false));
        if(getMvnoIdFromCurrentStaff(null)!=1)      // TODO: pass mvnoID manually 6/5/2025
            expression=expression.and(qPartner.mvnoId.in(Arrays.asList(getMvnoIdFromCurrentStaff(null),1)));  // TODO: pass mvnoID manually 6/5/2025

        if(getBUIdsFromCurrentStaff()!=null && !getBUIdsFromCurrentStaff().isEmpty())
            expression=expression.and(qPartner.buId.in(getBUIdsFromCurrentStaff()));

        List<Partner> partners = IterableUtils.toList(partnerRepository.findAll(expression));
        if (partners.size() == 0) {
            flag = true;
        }
        return flag;
    }


    public GenericDataDTO getAll1(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);

            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
                genericDataDTO = getListByPageAndSizeAndSortByAndOrderBy1(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder()
                        , requestDTO.getFilters());
            else
                genericDataDTO = search(requestDTO.getFilters()
                        , requestDTO.getPage(), requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder(),mvnoId);


            if (null != genericDataDTO) {
                logger.info("Fetching All Entities records:  request: { Module : {}}; Response : {Code :{}; Message : {}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                logger.error("Unable to fetch all Entities No records found:  request: { Module : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to fetch all Entities:  request: { module : {}}; Response : {Code :{}; Message : {};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
        }
        return genericDataDTO;
    }




    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        this.PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        this.PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        this.SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        this.SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        if (null == requestDTO.getPage())
            requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize())
            requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy())
            requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder())
            requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }
}
