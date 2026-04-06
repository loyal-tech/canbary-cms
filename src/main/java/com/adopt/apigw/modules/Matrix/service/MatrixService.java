package com.adopt.apigw.modules.Matrix.service;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Matrix.domain.Matrix;
import com.adopt.apigw.modules.Matrix.domain.QMatrix;
import com.adopt.apigw.modules.Matrix.mapper.MatrixMapper;
import com.adopt.apigw.modules.Matrix.model.MatrixDTO;
import com.adopt.apigw.modules.Matrix.repository.MatrixRepository;
import com.adopt.apigw.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.adopt.apigw.modules.tickets.domain.QTicketReasonCategory;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MatrixService  extends ExBaseAbstractService2< MatrixDTO, Matrix,Long> {


    public MatrixService(MatrixRepository repository, MatrixMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[MatrixService]";
    }
    @Autowired
    MatrixRepository matrixRepository;

    @Override
    public boolean duplicateVerifyAtSave(String matrixname,Integer mvnoId) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (matrixname != null) {
            matrixname = matrixname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = matrixRepository.duplicateVerifyAtSave(matrixname);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = matrixRepository.duplicateVerifyAtSave(matrixname, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = matrixRepository.duplicateVerifyAtSave(matrixname, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String matrixname, Integer matrixid,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (matrixname != null) {
            matrixname = matrixname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(mvnoId == 1) count = matrixRepository.duplicateVerifyAtSave(matrixname);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = matrixRepository.duplicateVerifyAtSave(matrixname, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = matrixRepository.duplicateVerifyAtSave(matrixname, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = matrixRepository.duplicateVerifyAtEdit(matrixname, matrixid);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = matrixRepository.duplicateVerifyAtEdit(matrixname, matrixid, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = matrixRepository.duplicateVerifyAtEdit(matrixname, matrixid, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
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

//    public GenericDataDTO search1(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
//        try {
//
//        } catch (Exception ex) {
//
//        }
//        return null;
//    }
    //Delete Verification
    public boolean deleteVerification(Integer id)throws Exception
        {
            String SUBMODULE = getModuleNameForLog() + " [deleteVerification()] ";

            boolean flag=false;
            try{

                Integer count=matrixRepository.deleteVerify(id);
                if(count==0){ // Count == 1 due to tatmatrix is not bind with any services
                    flag=true;
                }else {
                    throw new RuntimeException(DeleteContant.MATRIX_EXIST);
                }

            }catch (Exception ex){
                ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
                throw ex;
            }
            return flag;
        }

//    public String getid(Long id){
//        Optional<Matrix> matrix = matrixRepository.findById(id);
//        String name = matrix.get().getName();
//        return name;
//    }

    @Override
    public MatrixDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }

    //Get All with Pagination
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Matrix> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if(getLoggedInUser().getLco())
        {
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1)
                paginationList = matrixRepository.findAll(pageRequest,getLoggedInUser().getPartnerId());
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    paginationList = matrixRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1),getLoggedInUser().getPartnerId());
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    paginationList = matrixRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff(),getLoggedInUser().getPartnerId());
            }
        }
        else
        {
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1)
                paginationList = matrixRepository.findAll(pageRequest);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    paginationList = matrixRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    paginationList = matrixRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
        }


        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }




    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QMatrix qMatrix = QMatrix.matrix;
        BooleanExpression booleanExpression = qMatrix.isNotNull().and(qMatrix.isDeleted.eq(false));
        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qMatrix.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qMatrix.lcoId.isNull());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                        booleanExpression = booleanExpression.and(qMatrix.name.containsIgnoreCase(genericSearchModel.getFilterValue()));

                }
            }

        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qMatrix.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qMatrix.mvnoId.eq(1).or(qMatrix.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qMatrix.buId.in(getBUIdsFromCurrentStaff()))));
        }
        return makeGenericResponse(genericDataDTO, matrixRepository.findAll(booleanExpression, pageRequest));
    }

    public GenericDataDTO getTatmatrixByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            Page<Matrix> tatmatrixList = null;
//            if(getMvnoIdFromCurrentStaff() == 1)
//                tatmatrixList = matrixRepository.findAllBynameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
//            else
//                tatmatrixList = matrixRepository.findAllBynameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (null != tatmatrixList && 0 < tatmatrixList.getSize()) {
//                makeGenericResponse(genericDataDTO, tatmatrixList);
//            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public List<Matrix> matrixList()
    {
        List<Matrix> list =  new ArrayList<>();
        if(getLoggedInUser().getLco())
        {
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1)
                list   = matrixRepository.findbystatus(getLoggedInUser().getPartnerId());
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    list   = matrixRepository.findAllBystatus(Arrays.asList(getMvnoIdFromCurrentStaff(null), 1),getLoggedInUser().getPartnerId());
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    list   = matrixRepository.findAllBystatus(getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff(),getLoggedInUser().getPartnerId());
            }
        }
        else
        {
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1)
                list   = matrixRepository.findbystatus();
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    // TODO: pass mvnoID manually 6/5/2025
                    list   = matrixRepository.findAllBystatus(Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    list   = matrixRepository.findAllBystatus(getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
        }

        return list;
    }
}
