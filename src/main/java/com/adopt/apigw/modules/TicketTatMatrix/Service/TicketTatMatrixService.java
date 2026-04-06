package com.adopt.apigw.modules.TicketTatMatrix.Service;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.TicketTatMatrix.Domain.QTicketTatMatrix;
import com.adopt.apigw.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.adopt.apigw.modules.TicketTatMatrix.Mapper.TicketTatMatrixMapper;
import com.adopt.apigw.modules.TicketTatMatrix.Model.TicketTatMatrixDTO;
import com.adopt.apigw.modules.TicketTatMatrix.Repository.TicketTatMatrixRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketTatMatrixService extends ExBaseAbstractService2<TicketTatMatrixDTO, TicketTatMatrix, Long> {
    public TicketTatMatrixService(TicketTatMatrixRepository repository, TicketTatMatrixMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    TicketTatMatrixRepository repository;

    @Autowired
    TicketTatMatrixMapper mapper;

    @Override
    public String getModuleNameForLog() {
        return "[TicketTatMatrixService]";
    }

    @Override
    public boolean duplicateVerifyAtSave(String matrixname,Integer mvnoId) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (matrixname != null) {
            matrixname = matrixname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = repository.duplicateVerifyAtSave(matrixname);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(matrixname, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(matrixname, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String tatmatrixname, Integer tatmatrixid,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (tatmatrixname != null) {
            tatmatrixname = tatmatrixname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = repository.duplicateVerifyAtSave(tatmatrixname);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(tatmatrixname, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(tatmatrixname, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (mvnoId == 1)
                    countEdit = repository.duplicateVerifyAtEdit(tatmatrixname, tatmatrixid);
                else {
                    if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = repository.duplicateVerifyAtEdit(tatmatrixname, tatmatrixid, Arrays.asList(mvnoId, 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = repository.duplicateVerifyAtEdit(tatmatrixname, tatmatrixid, mvnoId, getBUIdsFromCurrentStaff());
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


    public boolean deleteVerification(Integer id) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [deleteVerification()] ";

        boolean flag = false;
        try {

            Integer count = repository.deleteVerify(id);
            if (count == 0) { // Count == 1 due to tatmatrix is not bind with any services
                flag = true;
            } else {
                throw new RuntimeException(DeleteContant.MATRIX_EXIST);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return flag;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QTicketTatMatrix qTicketTatMatrix = QTicketTatMatrix.ticketTatMatrix;
        BooleanExpression booleanExpression = qTicketTatMatrix.isNotNull().and(qTicketTatMatrix.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                booleanExpression = booleanExpression.and(qTicketTatMatrix.name.containsIgnoreCase(genericSearchModel.getFilterValue()));

            }
        }
// TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketTatMatrix.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketTatMatrix.mvnoId.eq(1).or(qTicketTatMatrix.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qTicketTatMatrix.buId.in(getBUIdsFromCurrentStaff()))));
        }
        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
    }

    public List<TicketTatMatrixDTO> getAllTicketTatMatrix() {
        // TODO: pass mvnoID manually 6/5/2025
        List<TicketTatMatrix> ticketTatMatrixList = repository.getAllByStatus().stream().filter(x -> (x.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || x.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (x.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(x.getBuId()))).collect(Collectors.toList());
        List<TicketTatMatrixDTO> ticketTatMatrixDTOList = ticketTatMatrixList.stream().map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return ticketTatMatrixDTOList;
    }

    @Override
    public TicketTatMatrixDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<TicketTatMatrix> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1) paginationList = repository.findAll(pageRequest);
        else if (null == filterList || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = repository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff(null)));
            else
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = repository.findAll(pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());


        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    public List<TicketTatMatrix> findAllByStatus() {
        QTicketTatMatrix ticketTatMatrix = QTicketTatMatrix.ticketTatMatrix;
        BooleanExpression booleanExpression = ticketTatMatrix.status.equalsIgnoreCase("Active").and(ticketTatMatrix.isDeleted.eq(false));
        if (getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(ticketTatMatrix.buId.in(getBUIdsFromCurrentStaff()));
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and((ticketTatMatrix.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1)));
        }
        return IterableUtils.toList(repository.findAll(booleanExpression));
    }
}
