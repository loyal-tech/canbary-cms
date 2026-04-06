package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.mapper.postpaid.CustomerLedgerMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustomerLedger;
import com.adopt.apigw.model.postpaid.CustomerLedgerDtlsPojo;
import com.adopt.apigw.model.postpaid.CustomerLedgerPojo;
import com.adopt.apigw.repository.postpaid.CustomerLedgerRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerLedgerService extends AbstractService<CustomerLedger, CustomerLedgerPojo, Integer> {

    @Autowired
    private CustomerLedgerRepository entityRepository;

    @Autowired
    private CustomerLedgerMapper customerLedgerMapper;

    @Override
    protected JpaRepository<CustomerLedger, Integer> getRepository() {
        return entityRepository;
    }

//    public Page<CustomerLedger> searchEntity(String searchText,Integer pageNumber,int pageSize){
// 	   PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
// 	   return entityRepository.searchEntity(searchText,pageRequest);
// 	}

//    public List<CustomerLedger>getAllActiveEntities(){
//    	return entityRepository.findByStatus("Y");
//    }

    public List<CustomerLedger> getAllEntities(Integer pageNumber, int pageSize) {
//    	PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.findAll();
    }

    public List<CustomerLedger> getCustomerLeger(Customers customer) {
        return entityRepository.findByCustomer(customer);
    }

    public CustomerLedger getCustomerLeger(Integer custId) {
        return entityRepository.findByCustomerId(custId);
    }



    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Customer Ledger");
        List<CustomerLedgerPojo> customerLedgerPojoList = entityRepository.findAll().stream()
                .map(data -> customerLedgerMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, CustomerLedgerPojo.class, customerLedgerPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<CustomerLedgerPojo> customerLedgerPojoList = entityRepository.findAll().stream()
                .map(data -> customerLedgerMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, CustomerLedgerPojo.class, customerLedgerPojoList, null);
    }

}
