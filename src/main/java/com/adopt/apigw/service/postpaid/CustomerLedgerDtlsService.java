package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.CafCustomers.Domain.TrialCustomerLedgerDtls;
import com.adopt.apigw.modules.CafCustomers.Repo.TrialCustomerLedgerDtlRepo;
import com.adopt.apigw.repository.postpaid.CustomerLedgerDtlsRepository;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.CommonConstants;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerLedgerDtlsService extends AbstractService<CustomerLedgerDtls, CustomerLedgerDtlsPojo, Integer> {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private CustomerLedgerDtlsRepository entityRepository;

    @Autowired
    private CustomerLedgerDtlsMapper customerLedgerDtlsMapper;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
//
//    @Autowired
//    private DebitDocument debitDocument;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private TrialCustomerLedgerDtlRepo trialCustomerLedgerDtlRepo;

    @Override
    protected JpaRepository<CustomerLedgerDtls, Integer> getRepository() {
        return entityRepository;
    }

//    public Page<CustomerLedgerDtls> searchEntity(String searchText,Integer pageNumber,int pageSize){
// 	   PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
// 	   return entityRepository.searchEntity(searchText,pageRequest);
// 	}

//    public List<CustomerLedgerDtls>getAllActiveEntities(){
//    	return entityRepository.findByStatus("Y");
//    }

    public List<CustomerLedgerDtls> getAllEntities(Integer pageNumber, int pageSize) {
//    	PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.findAll();
    }

    public List<CustomerLedgerDtlsPojo> convertResponseModelIntoPojo(List<CustomerLedgerDtls> customerLedgerDtls) {
        return customerLedgerDtls.stream().map(data -> customerLedgerDtlsMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<CustomerLedgerDtls> getCustomerLeger(Customers customer) {
        return entityRepository.findByCustomer(customer);
    }

    public CustomerLedgerInfoPojo getByTime(CustomerLedgerDtlsPojo pojo) {
        DecimalFormat df = new DecimalFormat("#.##");
        Optional<Customers> customers= customersRepository.findById(pojo.getCustId());
        Boolean isCaf=false;
        if (customers.isPresent()){
            isCaf = customers.get().getStatus().equalsIgnoreCase("NewActivation");
        }
        CustomerLedgerInfoPojo infoPojo = new CustomerLedgerInfoPojo();
        Double OpeningAmount=null;
        if (isCaf){
            OpeningAmount=trialCustomerLedgerDtlRepo.findOpeningAmount(pojo.getCREATE_DATE(), pojo.getCustId());
        }else {
            OpeningAmount = entityRepository.findOpeningAmount(pojo.getCREATE_DATE(), pojo.getCustId());
        }
        if (OpeningAmount == null) {
            OpeningAmount = 0.0;
        }
        infoPojo.setOpeningAmount(OpeningAmount);
        Double bal = 0.0;
        List<CustomerLedgerDtls> customerLedgerDtlsList = null;
        List<TrialCustomerLedgerDtls> trialCustomerLedgerDtls=null;
        if (pojo.getCREATE_DATE() != null && pojo.getEND_DATE() != null) {
            if (isCaf){
                trialCustomerLedgerDtls= trialCustomerLedgerDtlRepo.findAllByCREATE_DATEAndEndDateAndCustomerIdAndIsDelete(pojo.getCREATE_DATE(), pojo.getEND_DATE(), pojo.getCustId(), false);
                customerLedgerDtlsList=trialToCust(trialCustomerLedgerDtls);
            }else {
                customerLedgerDtlsList = entityRepository.findAllByCREATE_DATEAndEndDateAndCustomerIdAndIsDelete(pojo.getCREATE_DATE(), pojo.getEND_DATE(), pojo.getCustId(), false);
            }
        }
        if (pojo.getCREATE_DATE() == null && pojo.getEND_DATE() == null) {
            if (isCaf){
                trialCustomerLedgerDtls = trialCustomerLedgerDtlRepo.findByCustomerIdAndIsDelete(pojo.getCustId(), false);
                customerLedgerDtlsList=trialToCust(trialCustomerLedgerDtls);
            }else {
                customerLedgerDtlsList = entityRepository.findByCustomerIdAndIsDelete(pojo.getCustId(), false);
            }
        }
        if (!customerLedgerDtlsList.isEmpty()) {
            List<Integer> ids = customerLedgerDtlsList.stream().map(CustomerLedgerDtls::getDebitdocid).filter(Objects::nonNull).collect(Collectors.toList());
            List<DebitDocument> invoiceList = findDebitDocByStatus("VOID", ids);
            List<Integer> ids1 = new ArrayList<>();
            for (DebitDocument debitDocument1 : invoiceList) {
                ids1.add(debitDocument1.getId());
            }

            customerLedgerDtlsList.removeIf(l -> !ids1.contains(l.getDebitdocid()) && l.getTranstype().equalsIgnoreCase("DR") && l.getDebitdocid() != null);

        }
        if (customerLedgerDtlsList != null) {
            for (int i = 0; i < customerLedgerDtlsList.size(); i++) {
                if (customerLedgerDtlsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT)) {
                    bal += OpeningAmount - customerLedgerDtlsList.get(i).getAmount();
                }
                if (customerLedgerDtlsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)) {
                    bal += OpeningAmount + customerLedgerDtlsList.get(i).getAmount();
                }
                customerLedgerDtlsList.get(i).setBalAmount(Double.parseDouble(df.format(bal)));
                customerLedgerDtlsList.get(i).setAmount(Double.parseDouble(df.format(customerLedgerDtlsList.get(i).getAmount())));
            }
        }
//        List<CustomerLedgerDtls> sortedList = customerLedgerDtlsList.stream().sorted(Comparator.comparing(CustomerLedgerDtls::getId).reversed()).collect(Collectors.toList());
        infoPojo.setDebitCreditDetail(convertResponseModelIntoPojo(customerLedgerDtlsList));
        Double ClosingAmount = 0.0;
        if (pojo.getCREATE_DATE() != null && pojo.getEND_DATE() != null) {
            if (isCaf){
                ClosingAmount = trialCustomerLedgerDtlRepo.findClsoingAmount(pojo.getCREATE_DATE(), pojo.getEND_DATE(), pojo.getCustId());
            }else {
                ClosingAmount = entityRepository.findClsoingAmount(pojo.getCREATE_DATE(), pojo.getEND_DATE(), pojo.getCustId());
                }
            }
        if (pojo.getCREATE_DATE() == null || pojo.getEND_DATE() == null) {
            if (isCaf){
                ClosingAmount = trialCustomerLedgerDtlRepo.findClsoingAmountById(pojo.getCustId());
            }else {
                ClosingAmount = entityRepository.findClsoingAmountById(pojo.getCustId());
            }
        }

        if (ClosingAmount == null) {
            ClosingAmount = 0.0;
        }
        Double Balance = OpeningAmount + ClosingAmount;
        infoPojo.setClosingBalance(Double.parseDouble(df.format(Balance)));
        return infoPojo;
    }
//
//    public CustomerLedgerInfoPojo getWalletAmt(CustomerLedgerDtlsPojo pojo){
//        DecimalFormat df = new DecimalFormat("#.##");
//        CustomerLedgerInfoPojo infoPojo = new CustomerLedgerInfoPojo();
//
////        Double OpeningAmount = entityRepository.findOpeningAmount(pojo.getCREATE_DATE(), pojo.getCustId());
//        Double OpeningAmount = entityRepository.getWalletBalance(pojo.getCustId());
//        if (OpeningAmount == null) {
//            OpeningAmount = 0.0;
//        }infoPojo.setOpeningAmount(OpeningAmount);
//        //infoPojo.setOpeningAmount(OpeningAmount);
//        Double bal = 0.0;
//        List<CustomerLedgerDtls> customerLedgerDtlsList = null;
//        if (pojo.getCREATE_DATE() != null && pojo.getEND_DATE() != null) {
//            customerLedgerDtlsList = entityRepository.findAllByCREATE_DATEAndEndDateAndCustomerId(pojo.getCREATE_DATE(), pojo.getEND_DATE(), pojo.getCustId());
//        }
//        if (pojo.getCREATE_DATE() == null && pojo.getEND_DATE() == null) {
//            customerLedgerDtlsList = entityRepository.findByCustomerId(pojo.getCustId());
//        }
//
//        if (customerLedgerDtlsList != null) {
//            for (int i = 0; i < customerLedgerDtlsList.size(); i++) {
//                if (customerLedgerDtlsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT)) {
//                    bal += OpeningAmount + customerLedgerDtlsList.get(i).getAmount();
//                }
//                if (customerLedgerDtlsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)) {
//                    bal += OpeningAmount - customerLedgerDtlsList.get(i).getAmount();
//                }
//                customerLedgerDtlsList.get(i).setBalAmount(Double.parseDouble(df.format(bal)));
//                customerLedgerDtlsList.get(i).setAmount(Double.parseDouble(df.format(customerLedgerDtlsList.get(i).getAmount())));
//            }
//        }
//        //infoPojo.setDebitCreditDetail(convertResponseModelIntoPojo(customerLedgerDtlsList));
//        Double ClosingAmount=0.0;
//        if(pojo.getCREATE_DATE() != null && pojo.getEND_DATE() != null)
//        {
//            ClosingAmount = entityRepository.findClsoingAmount(pojo.getCREATE_DATE(), pojo.getEND_DATE(), pojo.getCustId());
//        }
//        if(pojo.getCREATE_DATE()==null || pojo.getEND_DATE()==null)
//        {
//            ClosingAmount = entityRepository.findClsoingAmountById(pojo.getCustId());
//        }
//
//        if (ClosingAmount == null) {
//            ClosingAmount = 0.0;
//        }
//        Double Balance = OpeningAmount + ClosingAmount;
//
//        if(Balance <= 0){
//            Balance = 0.0;
//        }
//        infoPojo.setClosingBalance(Double.parseDouble(df.format(Balance)));
//        return infoPojo;
//    }


    public CustomerLedgerInfoPojo getWalletAmt(CustomerLedgerDtlsPojo pojo) {
//        JPAQuery<CustomerLedgerDtls> query = new JPAQuery<>(entityManager);
//        QCreditDocument qCreditDocument=QCreditDocument.creditDocument;
//        JPAQuery<Double> queryResult;
        DecimalFormat df = new DecimalFormat("#.##");
        CustomerLedgerInfoPojo infoPojo = new CustomerLedgerInfoPojo();

        List<CustomerLedgerDtls> customerLedgerDtlsList = null;
        Double OpeningAmount = entityRepository.findOpeningAmount(pojo.getCREATE_DATE() , pojo.getCustId());
        if(OpeningAmount == null){
            OpeningAmount = 0.0;
        }

//        Double bal = 0.0;
//        if (pojo.getCREATE_DATE() == null && pojo.getEND_DATE() == null) {
//            customerLedgerDtlsList = entityRepository.findByCustomerIdAndIsDelete(pojo.getCustId(), false);
//        }
//        if (!customerLedgerDtlsList.isEmpty()) {
//            List<Integer> ids = customerLedgerDtlsList.stream().map(CustomerLedgerDtls::getDebitdocid).filter(Objects::nonNull).collect(Collectors.toList());
//            List<DebitDocument> invoiceList = findDebitDocByStatus("VOID", ids);
//            List<Integer> ids1 = new ArrayList<>();
//            for (DebitDocument debitDocument1 : invoiceList) {
//                ids1.add(debitDocument1.getId());
//            }
//
//            customerLedgerDtlsList.removeIf(l -> !ids1.contains(l.getDebitdocid()) && l.getTranstype().equalsIgnoreCase("DR") && l.getDebitdocid() != null);
//
//        }
//        if (customerLedgerDtlsList != null) {
//            for (int i = 0; i < customerLedgerDtlsList.size(); i++) {
//                if (customerLedgerDtlsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT)) {
//                    bal += OpeningAmount - customerLedgerDtlsList.get(i).getAmount();
//                }
//                if (customerLedgerDtlsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)) {
//                    bal += OpeningAmount + customerLedgerDtlsList.get(i).getAmount();
//                }
//                customerLedgerDtlsList.get(i).setBalAmount(Double.parseDouble(df.format(bal)));
//                customerLedgerDtlsList.get(i).setAmount(Double.parseDouble(df.format(customerLedgerDtlsList.get(i).getAmount())));
//            }
//        }
        Double ClosingAmount = 0.0;
        if (pojo.getCREATE_DATE() == null || pojo.getEND_DATE() == null) {
            ClosingAmount = entityRepository.findClsoingAmountById(pojo.getCustId());
        }

        if (ClosingAmount == null) {
            ClosingAmount = 0.0;
        }
        Double Balance = OpeningAmount + ClosingAmount;
        infoPojo.setClosingBalance(Double.parseDouble(df.format(Balance)));
//          queryResult= query.select(qCreditDocument.amount.subtract(qCreditDocument.adjustedAmount).sum() ).where(qCreditDocument.customer.id.eq(pojo.getCustId()).and(qCreditDocument.paytype.eq("advance")));

        infoPojo.setOpeningAmount(OpeningAmount);
        return infoPojo;
    }

    public CustomerLedgerAllInfoPojo custInfoBytime(Integer custId, CustomerLedgerInfoPojo pojo) {
        CustomerLedgerAllInfoPojo custPojo = new CustomerLedgerAllInfoPojo();
        Customers customers = customersRepository.findById(custId).orElse(null);
        custPojo.setCustId(customers.getId());
        custPojo.setCustname(customers.getCustname());
        custPojo.setUsername(customers.getUsername());
        String address = null;
        for (int i = 0; i < customers.getAddressList().size(); i++) {
            if (customers.getAddressList().get(i).getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)) {
                address = customers.getAddressList().get(i).getFullAddress();
            }
        }
        custPojo.setAddress(address);
        String plan = null;
        for (int i = 0; i < customers.getPlanMappingList().size(); i++) {
            if (customers.getPlanMappingList().get(i).getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA)) {
                Integer planId = customers.getPlanMappingList().get(i).getPlanId();
                plan = postpaidPlanRepo.getOne(planId).getDisplayName();
            }
        }
        custPojo.setPlan(plan);
        custPojo.setStatus(customers.getStatus());

        custPojo.setCustomerLedgerInfoPojo(pojo);
        return custPojo;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Customer Ledger Details");
        List<CustomerLedgerDtlsPojo> customerLedgerDtlsPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, CustomerLedgerDtlsPojo.class, customerLedgerDtlsPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<CustomerLedgerDtlsPojo> customerLedgerDtlsPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, CustomerLedgerDtlsPojo.class, customerLedgerDtlsPojoList, null);
    }

    public String getCustName(Integer custId) {
        Customers customers = customersRepository.findById(custId).orElse(null);
        return customers.getUsername();
    }


    public List<DebitDocument> findDebitDocByStatus(String status, List<Integer> ids) {
        QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
        BooleanExpression exp = qDebitDocument.isNotNull();
        exp = exp.and(qDebitDocument.billrunstatus.notEqualsIgnoreCase(status));
        if (!CollectionUtils.isEmpty(ids)) exp = exp.and(qDebitDocument.id.in(ids));
        return (List<DebitDocument>) debitDocRepository.findAll(exp);
    }

    public List<CustomerLedgerDtls> trialToCust(List <TrialCustomerLedgerDtls> trialCustomerLedgerDtls){
        List<CustomerLedgerDtls> customerLedgerDtlsList = new ArrayList<>();
        for (TrialCustomerLedgerDtls trialCustomerLedgerDtls1:trialCustomerLedgerDtls){
            CustomerLedgerDtls customerLedgerDtls=new CustomerLedgerDtls();
            customerLedgerDtls.setCustomer(trialCustomerLedgerDtls1.getCustomer());
            customerLedgerDtls.setBank(trialCustomerLedgerDtls1.getBank());
            customerLedgerDtls.setAmount(trialCustomerLedgerDtls1.getAmount());
            customerLedgerDtls.setId(trialCustomerLedgerDtls1.getId());
            customerLedgerDtls.setBalAmount(trialCustomerLedgerDtls1.getBalAmount());
            customerLedgerDtls.setDebitdocid(trialCustomerLedgerDtls1.getDebitdocid());
            customerLedgerDtls.setCREATE_DATE(trialCustomerLedgerDtls1.getCREATE_DATE());
            customerLedgerDtls.setEND_DATE(trialCustomerLedgerDtls1.getEND_DATE());
            customerLedgerDtls.setCreditdocid(trialCustomerLedgerDtls1.getCreditdocid());
            customerLedgerDtls.setPaymentMode(trialCustomerLedgerDtls1.getPaymentMode());
            customerLedgerDtls.setPaymentRefNo(trialCustomerLedgerDtls1.getPaymentRefNo());
            customerLedgerDtls.setTranscategory(trialCustomerLedgerDtls1.getTranscategory());
            customerLedgerDtls.setTranstype(trialCustomerLedgerDtls1.getTranstype());
            customerLedgerDtls.setRefNo(trialCustomerLedgerDtls1.getRefNo());
            customerLedgerDtlsList.add(customerLedgerDtls);
        }
        return customerLedgerDtlsList;
    }
}
