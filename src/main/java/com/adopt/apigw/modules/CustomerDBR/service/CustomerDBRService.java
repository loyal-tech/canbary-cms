package com.adopt.apigw.modules.CustomerDBR.service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.CustomerDBR.domain.CustomerDBR;
import com.adopt.apigw.modules.CustomerDBR.mapper.CustomerDBRMapper;
import com.adopt.apigw.modules.CustomerDBR.model.CustomerDBRDTO;
import com.adopt.apigw.modules.CustomerDBR.pojo.CustomerDBRPojo;
import com.adopt.apigw.modules.CustomerDBR.repository.CustomerDBRRepository;
import com.adopt.apigw.pojo.CustomerDBRResponse;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.DbrService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerDBRService extends ExBaseAbstractService2<CustomerDBRDTO, CustomerDBR, Long> {

    public CustomerDBRService(JpaRepository<CustomerDBR, Long> repository, IBaseMapper<CustomerDBRDTO, CustomerDBR> mapper) {
        super(repository, mapper);
    }

    @Autowired
    CustomerDBRRepository customerDBRRepository;

    @Autowired
    CustomersService customersService;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    CustomerDBRMapper customerDBRMapper;

    @Autowired
    DbrService dbrService;

//    @Autowired
//    CustomersPojo customersPojo;
//
//    @Autowired
//    CustPlanMapppingPojo custPlanMapppingPojo;

    public CustomerDBRDTO updateCustoerDBR(CustomerDBRDTO customerDBRDTO, Long custid) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Data Updated Successfully");

        return customerDBRDTO;
    }

    public void removeCustomerDBR(Integer cusid) {
        try {
            Customers customers=customersRepository.findById(cusid).get();
            List<CustomerDBR> list = customerDBRRepository.findAllByCustid(Long.valueOf(cusid));
            if (list.size() > 0) {
                LocalDate startDate = LocalDate.now();
                Long custid = list.get(0).getCustid();
                List<CustomerDBR> list3 = (List<CustomerDBR>) customerDBRRepository.getValuefordelete(startDate, custid);
                customerDBRRepository.deleteAll(list3);

                CustomerDBR dbr=new CustomerDBR();
                dbr.setPendingamt(list3.stream().mapToDouble(x-> x.getDbr()).sum());
                dbr.setDbr(0d);
                dbr.setCustid(custid);
                dbr.setCusttype(customers.getCustomerType());
                dbr.setCustname(customers.getCustname());
                dbr.setStatus("Active");
                dbr.setStartdate(LocalDate.now());
                dbr.setEnddate(LocalDate.now());
                customerDBRRepository.save(dbr);
            }
        } catch (Exception ex) {
            System.out.printf("Error which remove customer dbr.");
        }
    }

    public CustomerDBRDTO saveCustomerDBR(CustomerDBRDTO customerDBRDTO) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Data Submitted Successfully");

        List<CustomerDBR> list = new ArrayList<CustomerDBR>();
        String a = customerDBRDTO.getCusttype().toString();
        int days = 1;
        if (!a.equals("postpaid")) {
            for (int i = 0; i < customerDBRDTO.getValidity_days(); i++) {
                CustomerDBR customerDBR = new CustomerDBR();
                customerDBR.setCustid(customerDBRDTO.getCusid());
                customerDBR.setPlanid(customerDBRDTO.getPlanid());
                customerDBR.setCustname(customerDBRDTO.getCustname());
                customerDBR.setPlanname(customerDBRDTO.getPlanname());
                customerDBR.setCusttype(customerDBRDTO.getCusttype());
                customerDBR.setValidity_days(customerDBRDTO.getValidity_days());
                customerDBR.setOffer_price(customerDBRDTO.getOffer_price());
                if (i != 0) {
                    customerDBR.setStartdate(customerDBRDTO.getStartdate().plusDays(days));
                    days++;
                } else {
                    customerDBR.setStartdate(customerDBRDTO.getStartdate());
                }
                customerDBR.setStatus(customerDBRDTO.getStatus());
                customerDBR.setEnddate(customerDBRDTO.getEnddate());
                double day = customerDBRDTO.getValidity_days();
                double price = customerDBRDTO.getOffer_price();
                double total1 = price / day;
                customerDBR.setDbr(total1);
                double b = customerDBRDTO.getOffer_price();
                if (i != 0) {
                    CustomerDBR customerDBRValue = customerDBRRepository.getCustValue(customerDBR.getCustid());
                    System.out.println(customerDBRValue.getPendingamt());
                    b = customerDBRValue.getPendingamt();
                    double total;
                    total = b - total1;
                    customerDBR.setPendingamt(total);
                } else {
                    double total;
                    total = b - total1;
                    customerDBR.setPendingamt(total);
                }
                customerDBRRepository.save(customerDBR);
            }

        } else {
            CustomerDBR customerDBR = new CustomerDBR();
            customerDBR.setCustid(customerDBRDTO.getCusid());
            customerDBR.setPlanid(customerDBRDTO.getPlanid());
            customerDBR.setCustname(customerDBRDTO.getCustname());
            customerDBR.setPlanname(customerDBRDTO.getPlanname());
            customerDBR.setCusttype(customerDBRDTO.getCusttype());
            customerDBR.setValidity_days(customerDBRDTO.getValidity_days());
            customerDBR.setOffer_price(customerDBRDTO.getOffer_price());
            customerDBR.setStartdate(customerDBRDTO.getStartdate());
            customerDBR.setStatus(customerDBRDTO.getStatus());
            customerDBR.setEnddate(customerDBRDTO.getEnddate());
            double day = customerDBRDTO.getValidity_days();
            double price = customerDBRDTO.getOffer_price();
            double total1 = price / day;
            customerDBR.setDbr(0.0);
            customerDBR.setPendingamt(customerDBRDTO.getOffer_price());
            customerDBRRepository.save(customerDBR);

        }

//        CustomerPackageDTO customerPackageDTO = new CustomerPackageDTO();
//        Customers customers = new Customers();
//        CustomerDBR customerDBR = new CustomerDBR();
//        customerDBR.setStatus(customerPackageDTO.getStatus());
//        customerDBR.setCusttype(customers.getCusttype());
//        customerDBR.setCustname(customers.getCustname());
//        customerDBR.setPlanname(customers.getFullName());
//        customerDBR.setValidity_days();
//        customerDBR.setPlanid(customerPackageDTO.getPlanId();
//        customerDBR.setCustid(Long.valueOf(customerPackageDTO.getCustomersId());
//        customerDBR.setOffer_price(custPlanMapppingPojo.getOfferPrice());
//        customerDBR.setStartdate(customerPackageDTO.getStartDate());
//        customerDBR.setEnddate(customerPackageDTO.getEndDate());
//        double day = custPlanMapppingPojo.getPlanValidityDays();
//        double price = custPlanMapppingPojo.getOfferPrice();
//        double total = price/day;
//        customerDBR.setDbr(total);
//        customerDBR.setPendingamt(custPlanMapppingPojo.getOfferPrice());
//        customerDBRRepository.save(customerDBR);
        return customerDBRDTO;
    }

    public List<CustomerDBR> getdbrdeatils(LocalDate startdate, LocalDate endate) {
        return customerDBRRepository.getdbrdeatails(startdate, endate);
    }

//    public CustomerDBRResponse getbycustid(LocalDate startdate, LocalDate endate, Long custid) {
//        List<CustomerDBR> customerDBRS = customerDBRRepository.getbyCustid(startdate, endate, custid);
//        List<CustomerDBR> outstandingCustomerDBRS = customerDBRRepository.getbyCustid(startdate.minusDays(1), custid);
//        CustomerDBRResponse responseData=new CustomerDBRResponse();
//
//        if(outstandingCustomerDBRS!=null && !outstandingCustomerDBRS.isEmpty())
//        {
//            DecimalFormat df = new DecimalFormat("0.00");
//            Double outstandingPending=outstandingCustomerDBRS.stream().mapToDouble(x->x.getPendingamt()).sum();
//            Double outstandingDBR=outstandingCustomerDBRS.stream().mapToDouble(x->x.getDbr()).sum();
//            Double outstandingRevenue=outstandingCustomerDBRS.stream().mapToDouble(x->x.getCumm_revenue()).sum();
//
//            responseData.setOutstandingPending(Double.parseDouble(df.format(outstandingPending)));
//            responseData.setOutstandingDbr(Double.parseDouble(df.format(outstandingDBR)));
//            responseData.setOutstandingRevenue(Double.parseDouble(df.format(outstandingRevenue)));
//        }
//        else
//        {
//            responseData.setOutstandingPending(0.0d);
//            responseData.setOutstandingDbr(0.0d);
//            responseData.setOutstandingRevenue(0.0d);
//        }
//
//        List<CustomerDBRPojo> response = new ArrayList<>();
//        if(customerDBRS!=null && !customerDBRS.isEmpty()) {
//            LocalDate dbStartDate = customerDBRS.get(0).getStartdate();
//            Double offerPrice = customerDBRS.get(0).getOffer_price();
//            LocalDate dbEndDate = customerDBRS.get(customerDBRS.size() - 1).getEnddate().plusMonths(1);
//            if (dbEndDate.isAfter(endate))
//                endate = dbEndDate;
//            LocalDate y = startdate;
//            if (dbStartDate.isAfter(startdate))
//                y = dbStartDate;
//            int pointer = customerDBRS.size();
//            Double cummrevenue=0d;
//            while (pointer > 0 && (y.isBefore(endate))) {
//                CustomerDBRPojo customerDBRPojo = new CustomerDBRPojo();
//                LocalDate finalY = y;
//                Double totDBR = customerDBRS.stream().filter(value -> value.getStartdate().equals(finalY)).mapToDouble(CustomerDBR::getDbr).sum();
//                Double totPending = customerDBRS.stream().filter(value -> value.getStartdate().equals(finalY)).mapToDouble(CustomerDBR::getPendingamt).sum();
//
//                String remarksList = customerDBRS.stream().filter(value -> value.getStartdate().equals(finalY))
//                        .map(x->x.getRemark()).collect(Collectors.joining(" ")).trim();
//
//
//                Boolean isContainsMultipleService=customerDBRS.stream().filter(value -> value.getStartdate().equals(finalY)).filter(x->x.getServiceId() != null).mapToInt(x->x.getServiceId().intValue()).distinct().count()>1;
//
//                cummrevenue +=totDBR;
//
////                DecimalFormat decimalFormat = new DecimalFormat("#.##");
////                String convertedNumber = decimalFormat.format(cummrevenue);
////                Double diff =offerPrice- Double.parseDouble(convertedNumber);
////                String converteddiff = decimalFormat.format(diff);
////                if (endate.minusDays(1).equals(y)){
////                    cummrevenue =  Double.parseDouble(convertedNumber) + Double.parseDouble(converteddiff);
////                    customerDBRPojo.setCumm_revenue(cummrevenue);
////                }
//
//                customerDBRPojo.setMonth(y.getDayOfMonth() + "-" + y.getMonthValue() + "-" + y.getYear());
//                customerDBRPojo.setDate(y);
//                customerDBRPojo.setDbr(totDBR);
//                customerDBRPojo.setPendingamt(totPending);
//                customerDBRPojo.setRemark(remarksList);
//                customerDBRPojo.setIsContainsMultipleService(isContainsMultipleService);
//
//                customerDBRPojo.setCumm_revenue(cummrevenue);
//                if(customerDBRPojo.getPendingamt()==0 && customerDBRPojo.getDbr()==0) {}
//                else
//                    response.add(customerDBRPojo);
//
//                y = y.plusDays(Long.parseLong("1"));
//            };
//        }
//
//        responseData.setCustomerDBRPojos(response);
//        return responseData;
//    }


//    public List<CustomerDBRPojo> getbycustid(LocalDate startdate, Long custid) {
//        List<CustomerDBR> customerDBRS = customerDBRRepository.getbyCustid(startdate, custid);
//        List<CustomerDBRPojo> response = new ArrayList<>();
//        if(customerDBRS!=null && !customerDBRS.isEmpty())
//        {
//            customerDBRS.stream().forEach(x->{
//                CustomerDBRPojo pojo=new CustomerDBRPojo();
//                pojo.setDbr(x.getDbr());
//                pojo.setMonth(startdate.getDayOfMonth()+"");
//                pojo.setDate(startdate);
//                pojo.setPendingamt(x.getPendingamt());
//                pojo.setCumm_revenue(x.getCumm_revenue());
//                pojo.setIsContainsMultipleService(false);
//                pojo.setStartdate(startdate);
//                pojo.setServiceName(dbrService.getServiceNameById(x.getServiceId()));
//                response.add(pojo);
//            });
//        }
//        return response;
//    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Override
    public CustomerDBRDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }
}
