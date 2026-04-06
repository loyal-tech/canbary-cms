package com.adopt.apigw.controller.postpaid.audit;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;

public interface PostPaidPlanAuditService {
    PageableResponse getPlanAudit(Integer loggedInMvno, PaginationDTO dto);

    public boolean updatePostpaidPlan(PostpaidPlan existingPlan, PostpaidPlan updatedPlan, Integer staffId, String username);
}
