package com.adopt.apigw.controller.radius.vouchermaster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.model.radius.Plan;
import com.adopt.apigw.model.radius.ValidateResponse;
import com.adopt.apigw.model.radius.VoucherBatch;
import com.adopt.apigw.service.radius.PlanService;
import com.adopt.apigw.service.radius.VoucherBatchService;
import com.adopt.apigw.service.radius.VoucherMasterService;

@RestController
@RequestMapping(path = "/Voucher")
public class VoucherRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(VoucherRestController.class);

    VoucherMasterService voucherMasterService;

    VoucherBatchService voucherBatchService;

    PlanService planService;

    public VoucherRestController(VoucherMasterService voucherMasterService, VoucherBatchService voucherBatchService, PlanService planService) {
        this.voucherMasterService = voucherMasterService;
        this.voucherBatchService = voucherBatchService;
        this.planService = planService;
    }

    @GetMapping(path="/validateVoucherCode/{voucherCode}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ValidateResponse> getUsersById(@PathVariable(value = "voucherCode") String  voucherCode)
    {
        logger.info("method call");
        ValidateResponse user =null;
        VoucherBatch vb=voucherBatchService.getVoucherBatchByVoucherCode(voucherCode);
        if(vb!=null)
        {
            Plan plan=planService.findById(vb.getPlanId());
            logger.info("method call"+vb.getPlanId());
            if(plan!=null)
            {
                if(plan.getStml().equalsIgnoreCase("ACTIVE"))
                {
                    logger.info("method call"+plan.getStml());
                    user=new ValidateResponse(200,"The Status Is ACTIVE");
                }else
                {
                    user=new ValidateResponse(401,"The Status Is INACTIVE");
                }

            }

        }else {
            user=new ValidateResponse(404,"We Don't find VocherBatch With "+voucherCode);
        }

        return ResponseEntity.ok().body(user);

    }
}
