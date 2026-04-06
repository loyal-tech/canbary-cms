package com.adopt.apigw.service.BulkService;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface DownloadBulkPlanService {
    public Resource writePostpaidPlansToExcel(Integer mvno) throws Exception;
}
