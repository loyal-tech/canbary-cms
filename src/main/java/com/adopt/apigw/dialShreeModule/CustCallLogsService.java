package com.adopt.apigw.dialShreeModule;

import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CustCallLogsService {

    @Autowired
    CustCallLogsRepository custCallLogsRepository;

    @Autowired
    CustCallLogsMapper custCallLogsMapper;

    private final Logger log = LoggerFactory.getLogger(CustCallLogsService.class);



    public void save(CustCallLogsDTO custCallLogsDTO) {
        try{
            log.info(":::::::::::::::::Save Method Perform For CustomerCallLogs Data:::::::::::::::::");
            if(custCallLogsDTO!=null && !Objects.isNull(custCallLogsDTO)){
                CustCallLogs custCallLogs = custCallLogsMapper.dtoToDomain(custCallLogsDTO,new CycleAvoidingMappingContext());
                if(custCallLogs.getDynamicData()!=null){
                    custCallLogs.getDynamicData().setCallLogs(custCallLogs);
                }
                custCallLogsRepository.save(custCallLogs);
                log.info(":::::::::::::::::CustomerCallLogs Data Save Successfully::::::::::::::::::");
            }
        }catch (Exception e){
            log.error("::::::::::::::::Error While perform Save regarding CustomerCallLogs Data::::::::::::::::::"+e.getMessage());
            log.error(e.getStackTrace().toString());
        }
    }

    public Page<CustCallDTO> getAllDetailsByMobileNum(String phoneNum, PaginationRequestDTO paginationRequestDTO){
        PageRequest pageRequest = PageRequest.of(paginationRequestDTO.getPage() - 1, paginationRequestDTO.getPageSize(),Sort.by("id").descending());
        Page<CustCallDTO> custCallLogs = null;
        try {
            custCallLogs  = custCallLogsRepository.findAllByPhoneNum(phoneNum,pageRequest);
            if(Objects.isNull(custCallLogs) || custCallLogs == null){
                log.error("CustCallLog Details fetch failed — reason: No record found for phone number: "+phoneNum);
                throw new Exception("No CustCallLogDetails Found for Provided num: "+phoneNum);
            }
        } catch (Exception e) {
            log.error(":::::::::::::::CustCallLog Details fetch failed.::::::::::::::"+e.getMessage());
            e.getStackTrace();
        }
        return custCallLogs;
    }

    public CustCallLogs getAllDetailsByuniqueId(String uniqueId){
        CustCallLogs custCallLogs = null;
        try {
            custCallLogs  = custCallLogsRepository.findAllByuniqueid(uniqueId);
            if(Objects.isNull(custCallLogs) || custCallLogs == null){
                log.error("CustCallLog Details fetch failed — reason: No record found for uniqueId: "+uniqueId);
                throw new Exception("No CustCallLogDetails Found for Provided num: "+uniqueId);
            }
        } catch (Exception e) {
            log.error(":::::::::::::::CustCallLog Details fetch failed.::::::::::::::"+e.getMessage());
            e.getStackTrace();
        }
        return custCallLogs;
    }

}
