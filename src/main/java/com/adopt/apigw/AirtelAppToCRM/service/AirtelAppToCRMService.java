package com.adopt.apigw.AirtelAppToCRM.service;

import com.adopt.apigw.AirtelAppToCRM.dto.AirtelAppToCRMDTO;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustPlanMapppingRepository;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirtelAppToCRMService {

    @Autowired
     private CustomersRepository customersRepository;

    @Autowired
    private ClientServiceRepository clientServiceRepository;

    @Autowired
    private CustPlanMapppingRepository planMapppingRepository;


    public List<AirtelAppToCRMDTO> getCustomersByAccountNumber(AirtelAppToCRMDTO dto, Integer mvnoId) {
        List<Object[]> results = customersRepository.findCustomersByAccountNumber(dto.getAccountNo(), mvnoId);
        return results.stream()
                .map(obj -> new AirtelAppToCRMDTO(
                        (String) obj[0], // customerMsisdn
                        (String) obj[1], // username
                        (String) obj[2], // password
                        (String) obj[3], // accountNo
                         String.valueOf(obj[4]), // walletBal
                        (String) obj[5], // firstName
                        (String) obj[6],  // lastName
                        (String) obj[7],  // status
                        obj[8] != null ? Integer.parseInt(String.valueOf(obj[8])) : 0,  // custId
                        obj[9] != null ? Integer.parseInt(String.valueOf(obj[9])) : 0,  // mvnoId
                        obj[10] != null ? Integer.parseInt(String.valueOf(obj[10])) : 0, // buId
                        (String) obj[11] // custtype (Prepaid/Postpaid)
                ))
                .collect(Collectors.toList());
    }

    public AirtelAppToCRMDTO AirtelAppToCRMServiceBillFetch(AirtelAppToCRMDTO airtelDto) {
        List<Customers> byAcctno = customersRepository.findByAcctno(airtelDto.getAccountNo());
        Customers customers = byAcctno.get(0);
        if(customers!=null && customers.getMvnoId()!=null){
            String currencyForPayment = clientServiceRepository.findValueByNameandMvnoId("CURRENCY_FOR_PAYMENT", customers.getMvnoId());
            LocalDateTime latestExpiryDateByCustId = planMapppingRepository.findLatestExpiryDateByCustId(customers.getId());

            AirtelAppToCRMDTO dto = new AirtelAppToCRMDTO();
            dto.setCurrencyCode(currencyForPayment);
            dto.setFirstName(customers.getFirstname());
            dto.setLastName(customers.getLastname());
            dto.setDueDate(String.valueOf(latestExpiryDateByCustId));
            dto.setWalletBalance(String.valueOf(customers.getWalletbalance()));
            return dto;
        }
        return null;
    }
    public String getmobilenumber(String id){
        if(id!=null) {
            String results = customersRepository.findMobileNmuber(Integer.parseInt(id));
            return results;
        }
        return null;
    }
}
