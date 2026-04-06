package com.adopt.apigw.modules.planUpdate.service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.modules.planUpdate.domain.CustomerPackage;
import com.adopt.apigw.modules.planUpdate.mapper.CustomerPackageMapper;
import com.adopt.apigw.modules.planUpdate.model.CustomerPackageDTO;
import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.spring.SpringContext;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerPackageService extends ExBaseAbstractService<CustomerPackageDTO, CustomerPackage, Long> {

    @Autowired
    private CustomerPackageRepository customerPackageRepository;
    @Autowired
    private CustomerPackageMapper customerPackageMapper;

    public CustomerPackageService(CustomerPackageRepository repository, CustomerPackageMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "CustomerPackageService";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        return null;
    }

    public List<CustomerPackageDTO> findAllByCustomersId(Integer id){
        return customerPackageRepository.findAllByCustomersId(id)
                .stream().map(domain -> customerPackageMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("CustomerPackage");
        createExcel(workbook, sheet, CustomerPackageDTO.class, null,mvnoId);
    }
    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, CustomerPackageDTO.class, null,mvnoId);
    }

    public CustomerPackageDTO findParentCustPackageDetailByExpiryDate(Integer id){
        List<CustomerPackageDTO> list=customerPackageRepository.findParentCustPackageDetailByExpiryDate(id, LocalDateTime.now())
                .stream().map(domain -> customerPackageMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
        return list.get(0);
    }

    public List<CustomerPackage> getAllByCustomer(Integer customerid) {
        return customerPackageRepository.findAllByCustomersId(customerid);
    }
}
