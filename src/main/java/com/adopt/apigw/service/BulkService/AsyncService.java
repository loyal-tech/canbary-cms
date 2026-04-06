package com.adopt.apigw.service.BulkService;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsyncService {


    @Autowired
    private UploadBulkPlanService uploadBulkManagementService;


    @Async
    public void doAsync(List<PostpaidPlan> list){
        uploadBulkManagementService.updatePlansAndCustmapping(list);
    }
}