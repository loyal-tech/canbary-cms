package com.adopt.apigw.modules.CustomerMacMgmt.Controller;


import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.modules.CustomerMacMgmt.Service.CustMacMgmtService;
import com.adopt.apigw.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.CUST_MAC_MGMT)
public class CustMacMgmtController {



    @Autowired
    CustMacMgmtService custMacMgmtService;
    @PostMapping("save")
    public GenericDataDTO saveCustomeMac(@RequestBody List<CustMacMappping> custMacMappping, HttpServletRequest request) throws  Exception{
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
           genericDataDTO = custMacMgmtService.saveMacMapping(custMacMappping);

        }catch (CustomValidationException customValidationException){
            genericDataDTO.setResponseMessage(customValidationException.getMessage());
            genericDataDTO.setResponseCode(customValidationException.getErrCode());
        }catch (Exception e){
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }


    @PostMapping("update")
    public GenericDataDTO updateCustomeMac(@RequestBody CustMacMappping custMacMappping, HttpServletRequest request) throws  Exception{
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO = custMacMgmtService.updateMacMapping(custMacMappping);
        }catch (CustomValidationException customValidationException){
            genericDataDTO.setResponseMessage(customValidationException.getMessage());
            genericDataDTO.setResponseCode(customValidationException.getErrCode());
        }catch (Exception e){
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }



    @DeleteMapping("delete")
    public GenericDataDTO deleteCustomeMac(@RequestParam Integer custMacMapppingId, HttpServletRequest request) throws  Exception{
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO =custMacMgmtService.deleteMacMapping(custMacMapppingId);
        }catch (CustomValidationException customValidationException){
            genericDataDTO.setResponseMessage(customValidationException.getMessage());
            genericDataDTO.setResponseCode(customValidationException.getErrCode());
        }catch (Exception e){
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }



    @GetMapping("findByCustId")
    public GenericDataDTO getCustomeMac(@RequestParam Integer custId, HttpServletRequest request) throws  Exception{
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO = custMacMgmtService.findAllByCustId(custId);
        }catch (CustomValidationException customValidationException){
            genericDataDTO.setResponseMessage(customValidationException.getMessage());
            genericDataDTO.setResponseCode(customValidationException.getErrCode());
        }catch (Exception e){
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }

    @GetMapping("getMacCount")
    public GenericDataDTO getCustomeMacCount(@RequestParam Integer custId, HttpServletRequest request) throws  Exception{
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO = custMacMgmtService.getMacCountForCustomer(custId);
        }catch (CustomValidationException customValidationException){
            genericDataDTO.setResponseMessage(customValidationException.getMessage());
            genericDataDTO.setResponseCode(customValidationException.getErrCode());
        }catch (Exception e){
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }
}
