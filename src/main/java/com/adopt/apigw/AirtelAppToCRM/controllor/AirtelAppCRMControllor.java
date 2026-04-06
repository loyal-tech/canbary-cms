package com.adopt.apigw.AirtelAppToCRM.controllor;

import com.adopt.apigw.AirtelAppToCRM.dto.AirtelAppToCRMDTO;
import com.adopt.apigw.AirtelAppToCRM.dto.TransactionStatusDTO;
import com.adopt.apigw.AirtelAppToCRM.service.AirtelAppToCRMService;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.spring.LoggedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class AirtelAppCRMControllor {

    private final String MODULES = "[AirtelAppCRMControllor ]";
    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    AirtelAppToCRMService customersService;
    @Autowired
    private CustomersRepository customersRepository;


    @PostMapping("/getcustomersByAccNumber")
    public List<AirtelAppToCRMDTO> getCustomersByAcc(@RequestBody AirtelAppToCRMDTO airtelAppToCRMDTO) {
//        LoggedInUser loggedInUser = getLoggedInUser();
        return customersService.getCustomersByAccountNumber(airtelAppToCRMDTO, getLoggedInUser().getMvnoId());
    }

    @PostMapping("/getCustDetailsByAcctNum")
    public List<AirtelAppToCRMDTO> getCustDetailsByAcctNum(@RequestBody AirtelAppToCRMDTO airtelAppToCRMDTO) {
//        LoggedInUser loggedInUser = getLoggedInUser();
        return customersService.getCustomersByAccountNumber(airtelAppToCRMDTO, airtelAppToCRMDTO.getMvnoId());
    }

    @PostMapping("/getcustomersbillFetch")
    public AirtelAppToCRMDTO getcustomersbillFetch(@RequestBody AirtelAppToCRMDTO airtelAppToCRMDTO) {
        return customersService.AirtelAppToCRMServiceBillFetch(airtelAppToCRMDTO);
    }

    @GetMapping("/getmobilenumber/{custid}")
    public String getMobilenumber(@PathVariable("custid") String id) {
        return customersService.getmobilenumber(id);
    }


    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULES + e.getStackTrace(), e);
        }
        return loggedInUser;
    }
    @GetMapping("/getcustomersByAccountNo/{accountNo}")
    public ResponseEntity<List<AirtelAppToCRMDTO>> getCustomersByAccount(@PathVariable("accountNo") String accountNo, HttpServletRequest request) {
        List<Object[]> results = customersRepository.findCustomersByAccountNumber(accountNo, getLoggedInUser().getMvnoId());
        ClientService clientService = clientServiceRepository.findByNameAndMvnoId("MOBILE_NUMBER",  getLoggedInUser().getMvnoId());
        String mobileNumber = clientService.getValue();
        List<AirtelAppToCRMDTO> airtelAppToCRMDTOS = results.stream()
                .map(obj -> {
                    return new AirtelAppToCRMDTO(
                            (String) obj[0], // customerMsisdn
                            (String) obj[1], // username
                            (String) obj[2], // password
                            (String) obj[3], // accountNo
                            String.valueOf(obj[4]), // walletBal
                            (String) obj[5], // firstName
                            (String) obj[6], // lastName
                            (String) obj[7], // status
                            obj[8] != null ? Integer.parseInt(String.valueOf(obj[8])) : 0,  // custId
                            obj[9] != null ? Integer.parseInt(String.valueOf(obj[9])) : 0,  // mvnoId
                            obj[10] != null ? Integer.parseInt(String.valueOf(obj[10])) : 0, // buId
                            mobileNumber , // Now setting mobile number dynamically
                            (String) obj[11] //custtype
                    );
                })
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(airtelAppToCRMDTOS);
    }

@GetMapping("/CustomerByID/{custId}")
public ResponseEntity<List<TransactionStatusDTO>> getCustomersByID(@PathVariable("custId") String custId, HttpServletRequest request) {
    List<Object[]> results = customersRepository.findCustomersById(Integer.valueOf(custId));
    List<TransactionStatusDTO> transactionstatusDTOList = results.stream()
            .map(obj -> new TransactionStatusDTO(
                    (String) obj[0], // accountNo
                    (String) obj[1], // mobile
                    (String) obj[2], // Name
                    (String) obj[3] // email
            ))
            .collect(Collectors.toList());
          return ResponseEntity.status(HttpStatus.OK).body(transactionstatusDTOList);
    }
}
