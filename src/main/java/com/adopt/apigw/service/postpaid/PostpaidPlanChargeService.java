package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.mapper.postpaid.PostpaidPlanChargeMapper;
import com.adopt.apigw.model.postpaid.PostpaidPlanCharge;
import com.adopt.apigw.pojo.api.PostpaidPlanChargePojo;
import com.adopt.apigw.repository.postpaid.PostpaidPlanChargeRepo;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.radius.AbstractService;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostpaidPlanChargeService extends AbstractService<PostpaidPlanCharge, PostpaidPlanChargePojo, Integer> {

    @Autowired
    private PostpaidPlanChargeRepo entityRepository;
    @Autowired
    private PostpaidPlanChargeMapper postpaidPlanChargeMapper;
    @Autowired
    private CacheService cacheService;

    @Override
    protected JpaRepository<PostpaidPlanCharge, Integer> getRepository() {
        return entityRepository;
    }

    public Page<PostpaidPlanCharge> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.searchEntity(searchText, pageRequest);
    }

    public List<PostpaidPlanCharge> getPostpaidPlanChargesByPlanId(Integer planId) {
        return entityRepository.findAllByPlan(planId);
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Charges");
        List<PostpaidPlanChargePojo> postpaidPlanChargePojos = entityRepository.findAll().stream()
                .map(data -> postpaidPlanChargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, PostpaidPlanChargePojo.class, postpaidPlanChargePojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<PostpaidPlanChargePojo> postpaidPlanChargePojos = entityRepository.findAll().stream()
                .map(data -> postpaidPlanChargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, PostpaidPlanChargePojo.class, postpaidPlanChargePojos, null);
    }


    public List<Double> getChargeListByChargeIdAndPlanId(Integer planId, Integer chargeId) {
        String cacheKey = cacheKeys.CHARGE_LIST + planId + "_" + chargeId;

        try {
            List<Double> cachedCharges = (List<Double>) cacheService.getFromCache(cacheKey, List.class);
            if (cachedCharges != null) {
                return cachedCharges;
            }

            List<Double> chargeList = entityRepository.getChargeListByChargeIdAndPlanId(planId, chargeId);
            if (!chargeList.isEmpty()) {
                cacheService.putInCache(cacheKey, chargeList);
            }
            return chargeList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
