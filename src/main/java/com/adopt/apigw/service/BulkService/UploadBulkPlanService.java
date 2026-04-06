package com.adopt.apigw.service.BulkService;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UploadBulkPlanService {
    public String uploadBulkData(MultipartFile file, Integer mvnoId, String username);

    void updatePlansAndCustmapping(List<PostpaidPlan> updatedPostpaidPlans);
    }
