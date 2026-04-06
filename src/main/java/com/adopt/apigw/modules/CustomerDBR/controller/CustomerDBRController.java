package com.adopt.apigw.modules.CustomerDBR.controller;

import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.CustomerDBR.domain.CustomerDBR;
import com.adopt.apigw.modules.CustomerDBR.mapper.CustomerDBRMapper;
import com.adopt.apigw.modules.CustomerDBR.model.CustomDailyRevenue;
import com.adopt.apigw.modules.CustomerDBR.model.CustomMonthlyRevenue;
import com.adopt.apigw.modules.CustomerDBR.model.CustomerDBRDTO;
import com.adopt.apigw.modules.CustomerDBR.pojo.CustomerDBRPojo;
import com.adopt.apigw.modules.CustomerDBR.service.CustomerDBRService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
import com.adopt.apigw.modules.planUpdate.service.CustomerPackageService;
import com.adopt.apigw.nepaliCalendarUtils.service.DateConverterService;
import com.adopt.apigw.pojo.CustomerDBRResponse;
import com.adopt.apigw.service.postpaid.DbrService;
import com.adopt.apigw.utils.APIConstants;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1")
public class CustomerDBRController extends ApiBaseController {

    @Autowired
    CustomerDBRService customerDBRService;


    @Autowired
    DbrService dbrService;


    private static final Logger logger = LoggerFactory.getLogger(CustomerDBRController.class);

    private static String MODULE = " [APIController] ";
    private static final String CUSTOMER_PAYMENT = "CustomerPayment";
    private static final String OTP = "otp";

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_ADD + "\")")
    @PostMapping("/savecustdbr" )
    public CustomerDBRDTO saveCustDBR(@RequestBody CustomerDBRDTO customerDBRDTO) throws Exception {
        return customerDBRService.saveCustomerDBR(customerDBRDTO);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_VIEW + "\")")
    @GetMapping("/monthlywisedbr")
    public List<CustomerDBRPojo> getmonthlyDBR(@RequestParam("startdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
                                               @RequestParam("endate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endate) throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getmonthlyDBR]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        List<CustomerDBRPojo> response = new ArrayList<>();
        List<CustomerDBR> customerDBRS = new ArrayList<>();
        customerDBRS = customerDBRService.getdbrdeatils(startdate,endate);
        int a = startdate.getMonthValue();//10
        int b = endate.getMonthValue();//12
        int startyear = startdate.getYear();
        int endyear = endate.getYear();
        int total = (b - a)+1;
        int total1 = (((12-a)+b)+1);
        if (endyear > startyear)
        {
            LocalDate y = startdate;
            for(int i=0;i<total1;i++)
            {
                CustomerDBRPojo customerDBRPojo = new CustomerDBRPojo();
                LocalDate finalY = y;
                Double totDBR = customerDBRS.stream().filter(value -> value.getStartdate().getMonthValue() == finalY.getMonthValue())
                        .mapToDouble(CustomerDBR::getDbr).sum();
                Double totPending = customerDBRS.stream().filter(value -> value.getStartdate().getMonthValue() == finalY.getMonthValue())
                        .mapToDouble(CustomerDBR::getPendingamt).sum();
                customerDBRPojo.setMonth(y.getMonth().toString() +"-"+ y.getYear());
                customerDBRPojo.setDate(y);
                customerDBRPojo.setDbr(totDBR);
                customerDBRPojo.setPendingamt(totPending);
                response.add(customerDBRPojo);
                y = y.plusMonths(1);
            }
        }
        else
        {
            LocalDate y = startdate;
            for (int i = 0; i < total; i++) {
                CustomerDBRPojo customerDBRPojo = new CustomerDBRPojo();
                LocalDate finalY = y;
                Double totDBR = customerDBRS.stream().filter(value -> value.getStartdate().getMonthValue() == finalY.getMonthValue())
                        .mapToDouble(CustomerDBR::getDbr).sum();
                Double totPending = customerDBRS.stream().filter(value -> value.getStartdate().getMonthValue() == finalY.getMonthValue())
                        .mapToDouble(CustomerDBR::getPendingamt).sum();
                customerDBRPojo.setMonth(y.getMonth().toString() +"-"+ y.getYear());
                customerDBRPojo.setDate(y);
                customerDBRPojo.setDbr(totDBR);
                customerDBRPojo.setPendingamt(totPending);
                response.add(customerDBRPojo);
                y = y.plusMonths(1);
            }
        }
        return response;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_VIEW + "\")")
//    @GetMapping("/getCustomer")
//    public CustomerDBRResponse getbycustid(@RequestParam("startdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
//                                           @RequestParam("endate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endate,
//                                           @RequestParam("custid") Long custid) throws Exception {
//        CustomerDBRResponse list = customerDBRService.getbycustid(startdate,endate.plusDays(1),custid);
//        return list;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_VIEW + "\")")
//    @GetMapping("/getDbrByCustomerIdAndDate")
//    public List<CustomerDBRPojo> getDbrByCustomerIdAndDate(@RequestParam("startdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
//                                             @RequestParam("custid") Long custid) throws Exception {
//        List<CustomerDBRPojo> list = customerDBRService.getbycustid(startdate,custid);
//        return list;
//    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_VIEW + "\")")
    @GetMapping("/monthwisedbr")
    public  List<CustomerDBRPojo> getmonthwiseprepaiddbr(@RequestParam("startdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
                                                         @RequestParam("endate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endate) throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getmonthwiseprepaiddbr]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        CustomerDBRDTO customerDBRDTO = new CustomerDBRDTO();
        genericDataDTO.setResponseMessage("Success");
        List<CustomerDBRPojo> response = new ArrayList<>();
        List<CustomerDBR> dbedetails = customerDBRService.getdbrdeatils(startdate,endate);
        System.out.println(dbedetails);
        Set<LocalDate> startDateList = dbedetails.stream().map(CustomerDBR::getStartdate).collect(Collectors.toSet());
        for (LocalDate startDate: startDateList) {
            CustomerDBRPojo customerDBRPojo = new CustomerDBRPojo();
            Double dbr = dbedetails.stream().filter(customerDBRDTO1 -> customerDBRDTO1.getStartdate().isEqual(startDate)).mapToDouble(CustomerDBR::getDbr).sum();
            Double pendingamt = dbedetails.stream().filter(customerDBRDTO1 -> customerDBRDTO1.getStartdate().isEqual(startDate)).mapToDouble(CustomerDBR::getPendingamt).sum();
            customerDBRPojo.setPendingamt(pendingamt);
            customerDBRPojo.setDbr(dbr);
            customerDBRPojo.setStartdate(startDate);
            response.add(customerDBRPojo);
        }
        return response;
    }

    private String getModuleNameForLog() {
        return null;
    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_VIEW + "\")")
//    @GetMapping("/monthlywisedbr1")
//    public List<CustomerDBRPojo> getMonthWiseDBR(@RequestParam("startdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
//                                                 @RequestParam("endate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endate) throws Exception {
//        String SUB_MODULE = getModuleNameForLog() + "[getMonthWiseDBR]";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        List<CustomerDBRPojo> response = new ArrayList<>();
//        List<CustomMonthlyRevenue> customDailyRevenues = dbrService.getMonthWiseDbrDeatils();
//        while (startdate.compareTo(endate)<0)
//        {
//            LocalDate tmpStartDate = startdate;
//            Integer month=tmpStartDate.getMonthValue();
//            Integer year=tmpStartDate.getYear();
//
//            CustomerDBRPojo customerDBRPojo = new CustomerDBRPojo();
//            Double totDBR = customDailyRevenues.stream().filter(value -> (value.getMonth() == month && value.getYear().equalsIgnoreCase(String.valueOf(year)))).mapToDouble(CustomMonthlyRevenue::getRevenue).sum();
//            Double totPending = customDailyRevenues.stream().filter(value -> (value.getMonth() == month && value.getYear().equalsIgnoreCase(String.valueOf(year)))).mapToDouble(CustomMonthlyRevenue::getOutstanding).sum();
//            customerDBRPojo.setMonth(tmpStartDate.getMonth().toString() +"-"+ tmpStartDate.getYear());
//            customerDBRPojo.setDate(tmpStartDate);
//            customerDBRPojo.setDbr(totDBR);
//            customerDBRPojo.setPendingamt(totPending);
//            response.add(customerDBRPojo);
//            startdate=startdate.plusMonths(1);
//        }
//        return response;
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_VIEW + "\")")
//    @GetMapping("/daywisedbr")
//    public List<CustomerDBRPojo> getDayWiseDBR(@RequestParam("startdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
//                                               @RequestParam("endate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endate) throws Exception {
//        String SUB_MODULE = getModuleNameForLog() + "[getDayWiseDBR]";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        List<CustomerDBRPojo> response = new ArrayList<>();
//        List<CustomDailyRevenue> customDailyRevenues = dbrService.getDailyDbrDeatils(startdate,endate);
//        List<LocalDate> localDates=customDailyRevenues.stream().map(x->x.getDate()).distinct().collect(Collectors.toList());
//        if(localDates!=null && !localDates.isEmpty())
//        {
//            localDates.stream().forEach(data->{
//                List<CustomDailyRevenue> tmp=customDailyRevenues.stream().filter(x->x.getDate().equals(data)).collect(Collectors.toList());
//                CustomerDBRPojo customerDBRPojo = new CustomerDBRPojo();
//                customerDBRPojo.setDbr(tmp.stream().mapToDouble(t->t.getRevenue()).sum());
//                customerDBRPojo.setStartdate(data);
//                customerDBRPojo.setPendingamt(tmp.stream().mapToDouble(t->t.getOutstanding()).sum());
//                response.add(customerDBRPojo);
//            });
//
//        }
//        return response;
//    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_VIEW + "\")")
    @GetMapping("/postpaidbilldbr")
    public void getPostPaidDbr(@RequestParam("billDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate billDate) throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getPostPaidDbr]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        List<CustomerDBRPojo> response = new ArrayList<>();
        dbrService.addDbrForPostpaidCustomerForGivenDate(billDate);
    }
}
