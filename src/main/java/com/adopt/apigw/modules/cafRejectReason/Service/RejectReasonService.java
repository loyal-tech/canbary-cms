package com.adopt.apigw.modules.cafRejectReason.Service;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.cafRejectReason.DTO.*;
import com.adopt.apigw.modules.cafRejectReason.Entity.QRejectReason;
import com.adopt.apigw.modules.cafRejectReason.Entity.RejectReason;
import com.adopt.apigw.modules.cafRejectReason.Entity.RejectSubReason;
import com.adopt.apigw.modules.cafRejectReason.Repo.RejectReasonRepository;
import com.adopt.apigw.modules.cafRejectReason.Repo.RejectSubReasonRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class RejectReasonService extends ExBaseAbstractService2<RejectReasonDto, RejectReason, Long> {

    public static final String MODULE = "[RejectReasonService]";

    private final Logger logger = LoggerFactory.getLogger(CustomersService.class);

    @Autowired
    private RejectReasonRepository rejectReasonRepository;

    @Autowired
    private RejectSubReasonRepository rejectSubReasonRepository;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private CustomersRepository customersRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private MvnoRepository mvnoRepository;

    public RejectReasonService(JpaRepository<RejectReason, Long> repository, RejectReasonMapper mapper) {
        super(repository, mapper);
    }


    @Transactional
    public RejectReasonDto saveRejectReason(RejectReasonDto rejectReasonDto) {
        String SUBMODULE = MODULE + "saveRejectReason()";
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != null) {
                // TODO: pass mvnoID manually 6/5/2025
                rejectReasonDto.setMvnoId(getMvnoIdFromCurrentStaff(null));
            }
            if (!getBUIdsFromCurrentStaff().isEmpty()) {
                rejectReasonDto.setBuId(getBUIdsFromCurrentStaff().get(0));
            }
            RejectReason rejectReason = new RejectReason(rejectReasonDto);
            RejectReason savedRejectReason = this.rejectReasonRepository.save(rejectReason);
            if (savedRejectReason.getRejectSubReasonList() != null
                    && !savedRejectReason.getRejectSubReasonList().isEmpty()) {
                savedRejectReason.getRejectSubReasonList()
                        .forEach(rejectSubReason -> rejectSubReason.setRejectReason(savedRejectReason));
                this.rejectSubReasonRepository.saveAll(savedRejectReason.getRejectSubReasonList());
            }
            logger.info("RejectReason has been created successfully");
            return new RejectReasonDto(savedRejectReason);
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public RejectReasonDto updateRejectReason(RejectReasonDto rejectReasonDto, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        RejectReason exstingRejectReason = this.rejectReasonRepository.findById(rejectReasonDto.getId()).get();
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != null) {
                // TODO: pass mvnoID manually 6/5/2025
                rejectReasonDto.setMvnoId(getMvnoIdFromCurrentStaff(null));
            }
            if (!getBUIdsFromCurrentStaff().isEmpty()) {
                rejectReasonDto.setBuId(getBUIdsFromCurrentStaff().get(0));
            }
            RejectReason rejectReason = new RejectReason(rejectReasonDto);
            if (rejectReasonDto.getRejectSubReasonDeletedIds() != null
                    && !rejectReasonDto.getRejectSubReasonDeletedIds().isEmpty()) {
                for (Long rejectSubReasonId : rejectReasonDto.getRejectSubReasonDeletedIds()) {
                    Optional<RejectSubReason> optionalRejectSubReason = this.rejectSubReasonRepository
                            .findById(rejectSubReasonId);
                    if (optionalRejectSubReason.isPresent()) {
                        RejectSubReason rejectSubReason = optionalRejectSubReason.get();
                        List<Customers> customersList = this.customersRepository
                                .findByRejectSubReasonId(rejectSubReasonId);
                        if (customersList != null && !customersList.isEmpty()) {
                            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR,
                                    "This operation will not allow as this " + rejectSubReason.getName()
                                            + " RejectSubReason is used for caf customer creation.",
                                    null);
                        }
                        this.rejectSubReasonRepository.deleteById(rejectSubReasonId);
                    }
                }
            }
            if (rejectReason.getRejectSubReasonList() != null && !rejectReason.getRejectSubReasonList().isEmpty()) {
                rejectReason.getRejectSubReasonList()
                        .forEach(rejectSubReason -> rejectSubReason.setRejectReason(rejectReason));
                this.rejectSubReasonRepository.saveAll(rejectReason.getRejectSubReasonList());
            }
            RejectReason updatedRejectReason = this.rejectReasonRepository.save(rejectReason);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(
                    "RejectReason with old name : " + exstingRejectReason.getName() + " is updated to : "
                            + rejectReasonDto.getName()
                            + " updated Successfully; request: { From : {}, Request Url : {}}; Response : {{}}",
                    req.getHeader("requestFrom"), req.getRequestURL(), RESP_CODE);
            return new RejectReasonDto(updatedRejectReason);
        } catch (Exception ex) {
            logger.info(
                    "Unable to Update RejectReason with old name : " + exstingRejectReason.getName()
                            + " is updated to : " + rejectReasonDto.getName()
                            + " ; request: { From : {}, Request Url : {}}; Response : {{}};Exception:{}",
                    req.getHeader("requestFrom"), req.getRequestURL(), RESP_CODE, ex.getMessage());
            throw ex;
        }
    }


    public RejectReasonDto findById(Long id) {
        Optional<RejectReason> rejectReason = this.rejectReasonRepository.findById(id);
        if (rejectReason.isPresent()) {
            return new RejectReasonDto(rejectReason.get());
        } else {
            return null;
        }
    }


    public void validateRequest(RejectReasonDto dto, Integer mvnoId, Integer operation) {
        if (dto == null) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, "Required object is not set",
                    null);
        }
        if (dto != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (dto.getId() != null)
                throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR,
                        "Id should not be present in the JSON body.", null);
            if (!dto.getName().equalsIgnoreCase("")) {
                List<RejectReason> rejectReasonList = this.rejectReasonRepository
                        .findByNameAndMvnoIdAndIsDelete(dto.getName(), mvnoId, false);
                if (rejectReasonList != null && rejectReasonList.size() > 0)
                    throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR,
                            "RejectReason already exit.", null);
            }
        }
        if (!(dto.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)
                || dto.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, "Inproper value for status.",
                    null);
        }
        if (dto != null && dto.getName().equalsIgnoreCase("")) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, "Please enter name.", null);
        }
        if (dto != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
                || operation.equals(CommonConstants.OPERATION_DELETE)) && dto.getId() == null) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, "Id can not be set to null.",
                    null);
        }
    }


    public void deleteRejectReason(Long rejectReasonId) {
        String SUBMODULE = MODULE + "deleteRejectReason()";
        try {
            RejectReason rejectReasonEntity = this.rejectReasonRepository.findById(rejectReasonId).get();

            if (Objects.nonNull(rejectReasonEntity)) {
                List<Customers> findByCustomerList = this.customersRepository
                        .findByRejectReasonId(rejectReasonEntity.getId());
                if (findByCustomerList != null && findByCustomerList.size() > 0) {
                    throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR,
                            "This operation will not allow as this RejectReason is used for Caf Customer creation.",
                            null);
                }
                if (rejectReasonEntity.getRejectSubReasonList() != null
                        && rejectReasonEntity.getRejectSubReasonList().size() > 0) {
                    this.rejectSubReasonRepository.deleteAll(rejectReasonEntity.getRejectSubReasonList());
                }
                rejectReasonEntity.setIsDelete(true);
                rejectReasonEntity.setRejectSubReasonList(null);
                this.rejectReasonRepository.save(rejectReasonEntity);
                logger.info("deleted successfully: " + rejectReasonEntity.getName());
            }
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Page<RejectReason> search1(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("name")) {
                        return getRejectReasonByName(searchModel.getFilterValue(), pageRequest);
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

    public Page<RejectReason> getRejectReasonByName(String s1, PageRequest pageRequest) {
        Page<RejectReason> rejectReasons = null;
        QRejectReason qRejectReason = QRejectReason.rejectReason;
        BooleanExpression booleanExpression = qRejectReason.isNotNull()
                .and(qRejectReason.isDelete.eq(false))
                .and(qRejectReason.name.likeIgnoreCase("%" + s1 + "%"))
                .or(qRejectReason.status.equalsIgnoreCase(s1));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1) {
            return rejectReasonRepository.findAll(booleanExpression, pageRequest);
        } else {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qRejectReason.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            if (getBUIdsFromCurrentStaff().size() > 0) {
                booleanExpression = booleanExpression.and(qRejectReason.buId.in(getBUIdsFromCurrentStaff()));
            }
            return rejectReasonRepository.findAll(booleanExpression, pageRequest);
        }
    }


    public Page<RejectReasonDto> search(Integer mvnoId, List<Long> buId, PaginationRequestDTO paginationRequestDTO) {
        String SUBMODULE = MODULE + "search()";
        PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
                paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
                paginationRequestDTO.getSortOrder());
        try {
            String queryForRejectReason = "SELECT rr FROM RejectReason rr WHERE rr.isDelete = false";
            String countQueryForRejectReason = "SELECT count(rr.id) FROM RejectReason rr WHERE rr.isDelete = false";

            if (mvnoId != null) {
                queryForRejectReason += " AND (rr.mvnoId IS NULL OR rr.mvnoId=" + mvnoId + ")";
                countQueryForRejectReason += " AND (rr.mvnoId IS NULL OR rr.mvnoId=" + mvnoId + ")";
            } else {
                queryForRejectReason += " AND (rr.mvnoId IS NULL)";
                countQueryForRejectReason += " AND (rr.mvnoId IS NULL)";
            }

            if (buId != null) {
                queryForRejectReason += " AND (rr.buId IS NULL OR rr.buId=" + buId + ")";
                countQueryForRejectReason += " AND (rr.buId IS NULL OR rr.buId=" + buId + ")";
            } else {
                queryForRejectReason += " AND (rr.buId IS NULL)";
                countQueryForRejectReason += " AND (rr.buId IS NULL)";
            }

            if (paginationRequestDTO.getFilters() != null && paginationRequestDTO.getFilters().size() > 0)
                if (paginationRequestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("name")) {
                    queryForRejectReason += " AND lower(rr.name) LIKE '%"
                            + paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%' ";
                    countQueryForRejectReason += " AND lower(rr.name) LIKE '%"
                            + paginationRequestDTO.getFilters().get(0).getFilterValue().toLowerCase() + "%' ";
                }
            Query q = entityManager.createQuery(queryForRejectReason, RejectReason.class);
            List<RejectReason> rejectReasonList = q.getResultList();
            List<RejectReasonDto> rejectReasonDtos = new ArrayList<RejectReasonDto>();
            for (RejectReason rejectReason : rejectReasonList) {
                rejectReasonDtos.add(new RejectReasonDto(rejectReason));
            }
            Query queryTotal = entityManager.createQuery(countQueryForRejectReason);
            long countResult = (long) queryTotal.getSingleResult();
            return new PageImpl<RejectReasonDto>(rejectReasonDtos, PageRequest.of(0, pageRequest.getPageSize()),
                    countResult);
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public RejectReasonDto convertToDto(RejectReason rejectReason) {
        RejectReasonDto rejectReasonDto = new RejectReasonDto();
        rejectReasonDto.setId(rejectReason.getId());
        rejectReasonDto.setName(rejectReason.getName());
        rejectReasonDto.setStatus(rejectReason.getStatus());
        if (rejectReason.getRejectSubReasonList() != null && rejectReason.getRejectSubReasonList().size() > 0) {
            List<RejectSubReasonDto> rejectSubReasonDtoList = new ArrayList<RejectSubReasonDto>();
            for (RejectSubReason rejectSubReason : rejectReason.getRejectSubReasonList()) {
                rejectSubReasonDtoList.add(new RejectSubReasonDto(rejectSubReason));
            }
            rejectReasonDto.setRejectSubReasonDtoList(rejectSubReasonDtoList);
        }
        return rejectReasonDto;
    }

    public Page<RejectReasonDto> findAll(PaginationRequestDTO paginationRequestDTO) {
        PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
                paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
                paginationRequestDTO.getSortOrder());
        QRejectReason qRejectReason = QRejectReason.rejectReason;
        BooleanExpression booleanExpression = qRejectReason.isNotNull().and(qRejectReason.isDelete.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qRejectReason.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        }
        if (!getBUIdsFromCurrentStaff().isEmpty()) {
            booleanExpression = booleanExpression.and(qRejectReason.buId.in(getBUIdsFromCurrentStaff()));
        }
        Page<RejectReason> page = null;
        page = this.rejectReasonRepository.findAll(booleanExpression, pageRequest);
        return page.map(this::convertToDto);
    }

    public Page<RejectReasonDto> findAll(Integer mvnoId, Long buId, PaginationRequestDTO paginationRequestDTO) {
        String SUBMODULE = MODULE + "findAll()";
        PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
                paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
                paginationRequestDTO.getSortOrder());
        try {
            String queryForRejectReason = "SELECT rr FROM RejectReason rr WHERE rr.isDelete = false";
            String countQueryForRejectReason = "SELECT count(rr.id) FROM RejectReason rr WHERE rr.isDelete = false";

            if (mvnoId != null) {
                queryForRejectReason += " AND (rr.mvnoId IS NULL OR rr.mvnoId=" + mvnoId + ")";
                countQueryForRejectReason += " AND (rr.mvnoId IS NULL OR rr.mvnoId=" + mvnoId + ")";
            } else {
                queryForRejectReason += " AND (rr.mvnoId IS NULL)";
                countQueryForRejectReason += " AND (rr.mvnoId IS NULL)";
            }

            if (buId != null) {
                queryForRejectReason += " AND (rr.buId IS NULL OR rr.buId=" + buId + ")";
                countQueryForRejectReason += " AND (rr.buId IS NULL OR rr.buId=" + buId + ")";
            } else {
                queryForRejectReason += " AND (rr.buId IS NULL)";
                countQueryForRejectReason += " AND (rr.buId IS NULL)";
            }
            queryForRejectReason += " order by rr.id DESC";
            Query q = entityManager.createQuery(queryForRejectReason, RejectReason.class);
            List<RejectReason> rejectReasonList = q.getResultList();
            List<RejectReasonDto> rejectReasonDtos = new ArrayList<RejectReasonDto>();
            for (RejectReason rejectReason : rejectReasonList) {
                rejectReasonDtos.add(new RejectReasonDto(rejectReason));
            }
            Query queryTotal = entityManager.createQuery(countQueryForRejectReason);
            long countResult = (long) queryTotal.getSingleResult();
            return new PageImpl<RejectReasonDto>(rejectReasonDtos, PageRequest.of(0, pageRequest.getPageSize()),
                    countResult);
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<RejectReason> findAllRejectedReasonsList() {
        String SUBMODULE = MODULE + "findAllRejectedReasonsList()";
        List<RejectReason> rejectReasonList = new ArrayList<>();
        rejectReasonList = rejectReasonRepository.findAllRejectedReasonsList();
        return rejectReasonList;
    }


    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Override
    public RejectReasonDto getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }
}
