package com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnController;

import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnDomain.ReturnDto;
import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnModel.Return;
import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnService.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1")
public class RetrunController extends ApiBaseController {

    @Autowired
    private ReturnService returnService;

    @PostMapping("/savereturn" )
    public ReturnDto saveReturn(@RequestBody ReturnDto returnDto) throws Exception {
        return returnService.saveReturn(returnDto);
    }

    @GetMapping("/getReturnforCustomer")
    public List<Return> getforCustomer(@RequestParam("id") Long id) throws Exception {
        List<Return> list = new ArrayList<>();
        list = returnService.getreturnforcustomer(id);
        return list;
    }
}
