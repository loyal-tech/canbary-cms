package com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.domain.ReportProblem;
import com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.mapper.ReportProblemMapper;
import com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.model.ReportProblemDTO;
import com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.repository.ReportProblemRepository;
import com.adopt.apigw.utils.CommonConstants;

@Service
@EnableCaching
public class ReportProblemService extends ExBaseAbstractService2<ReportProblemDTO, ReportProblem, Long> {


    @Autowired
    private ReportProblemRepository reportProblemRepository;

    @Autowired
    private ReportProblemMapper reportProblemMapper;

    public ReportProblemService(ReportProblemRepository repository, ReportProblemMapper mapper) {
        super(repository, mapper);
        this.reportProblemRepository = repository;
        this.reportProblemMapper = mapper;
    }

    public GenericDataDTO saveReport(ReportProblemDTO reportProblem) throws Exception {

            GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Problem Submitted Successfully");

        List<ReportProblem> list = new ArrayList<>();
        for (int i = 0; i < reportProblem.getIssue_list().size(); i++) {
            ReportProblem problem = new ReportProblem();
            problem.setPhno(reportProblem.getPhno());
            problem.setDesc(reportProblem.getDesc());
            problem.setIssue(String.valueOf(reportProblem.getIssue_list().get(i)));
            System.out.println("Problem list :" + problem);
            list.add(problem);
        }
        list = reportProblemRepository.saveAll(list);
        return genericDataDTO;
    }

    public GenericDataDTO searchreport(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [searchreport()] ";
        Long aLong = null;
        try {
            PageRequest pageRequest = generatepagerequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    return getreportproblembyPhno(Long.valueOf(searchModel.getFilterValue()),pageRequest);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return GenericDataDTO.getGenericDataDTO(filterList);
    }
    public PageRequest generatepagerequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        this.MAX_PAGE_SIZE = pageSize;
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }
        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
        return pageRequest;
    }

    public GenericDataDTO getreportproblembyPhno(Long phno,PageRequest pageRequest){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<ReportProblem> reportProblems = null;
        reportProblems = reportProblemRepository.findAllReportByPhno(phno,pageRequest);
        makeGenericResponse(genericDataDTO, reportProblems);
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    //@Cacheable(cacheNames = "ReportProblem", key = "#type")
    public List<ReportProblemDTO> getReportProblemByno(Long phno) {
        System.out.println(reportProblemRepository.findAllReportProblemByPhno(phno));
        List<ReportProblem> problemlist = reportProblemRepository.findAllReportProblemByPhno(phno);
        System.out.println(problemlist);
        return problemlist.stream().map(domain -> reportProblemMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());

    }

    @Override
    public ReportProblemDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }
}



