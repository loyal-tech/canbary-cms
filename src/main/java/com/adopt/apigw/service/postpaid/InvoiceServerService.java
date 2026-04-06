package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.postpaid.InvoiceServer;
import com.adopt.apigw.pojo.api.InvoiceServerPojo;
import com.adopt.apigw.repository.postpaid.InvoiceServerRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceServerService extends AbstractService<InvoiceServer, InvoiceServerPojo, Integer> {

    @Autowired
    private InvoiceServerRepository invoiceServerRepository;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<InvoiceServer, Integer> getRepository() {
        return invoiceServerRepository;
    }

    public InvoiceServer findActiveServerDetail(String type) {
        List<InvoiceServer> list = invoiceServerRepository.findByStatusAndServertype("1",type);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.InvoiceServer', '1')")
    public List<InvoiceServer> getAllActiveEntities() {
        return invoiceServerRepository.findAllByStatus("1");
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.InvoiceServer', '4')")
    public void deleteInvoiceServer(Integer id) throws Exception {
        invoiceServerRepository.deleteById(id);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.InvoiceServer', '2')")
    public InvoiceServer getInvoiceServerForAdd() {
        return new InvoiceServer();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.InvoiceServer', '2')")
    public InvoiceServer getInvoiceServerForEdit(Integer id) throws Exception {
        return invoiceServerRepository.getOne(id);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.InvoiceServer', '2')")
    public InvoiceServer saveInvoiceServer(InvoiceServer invoiceServer) throws Exception {
        InvoiceServer save = invoiceServerRepository.save(invoiceServer);
        return save;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.InvoiceServer', '2')")
    public InvoiceServerPojo save(InvoiceServerPojo pojo) throws Exception {
        InvoiceServer oldObj = null;
        if (pojo.getId() != null) {
            oldObj = invoiceServerRepository.findById(pojo.getId()).get();
        }
        InvoiceServer obj = convertInvoiceServerPojoToInvoiceServerModel(pojo);
        if(oldObj!=null) {
            log.info("InvoiceServer update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
        }
        obj = saveInvoiceServer(obj);
        pojo = convertInvoiceServerModelToInvoiceServerPojo(obj);
        return pojo;
    }

    public InvoiceServer convertInvoiceServerPojoToInvoiceServerModel(InvoiceServerPojo invoiceServerPojo) throws Exception {
        InvoiceServer invoiceServer = null;
        if (invoiceServerPojo != null) {
            invoiceServer = new InvoiceServer();
            if (invoiceServerPojo.getId() != null) {
                invoiceServer.setId(invoiceServerPojo.getId());
            }
            invoiceServer.setServerip(invoiceServerPojo.getServerip());
            invoiceServer.setStatus(invoiceServerPojo.getStatus());
            invoiceServer.setWebport(invoiceServerPojo.getWebport());
            invoiceServer.setServertype(invoiceServerPojo.getServertype());
            invoiceServer.setCreatedate(invoiceServerPojo.getCreatedate());
            invoiceServer.setUpdatedate(invoiceServerPojo.getUpdatedate());
        }
        return invoiceServer;
    }

    public InvoiceServerPojo convertInvoiceServerModelToInvoiceServerPojo(InvoiceServer invoiceServer) throws Exception {
        InvoiceServerPojo pojo = null;
        if (invoiceServer != null) {
            pojo = new InvoiceServerPojo();
            pojo.setId(invoiceServer.getId());
            pojo.setServerip(invoiceServer.getServerip());
            pojo.setStatus(invoiceServer.getStatus());
            pojo.setWebport(invoiceServer.getWebport());
            pojo.setServertype(invoiceServer.getServertype());
            pojo.setCreatedate(invoiceServer.getCreatedate());
            pojo.setUpdatedate(invoiceServer.getUpdatedate());
        }
        return pojo;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.InvoiceServer', '1')")
    public List<InvoiceServerPojo> convertResponseModelIntoPojo(List<InvoiceServer> invoiceServerList) throws Exception {
        List<InvoiceServerPojo> pojoListRes = new ArrayList<InvoiceServerPojo>();
        if (invoiceServerList != null && invoiceServerList.size() > 0) {
            for (InvoiceServer invoiceServer : invoiceServerList) {
                pojoListRes.add(convertInvoiceServerModelToInvoiceServerPojo(invoiceServer));
            }
        }
        return pojoListRes;
    }

    public void validateRequest(InvoiceServerPojo pojo, Integer operation) {
        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getId() != null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
        }
        if (!(pojo.getStatus().equalsIgnoreCase("0") || pojo.getStatus().equalsIgnoreCase("1"))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Invoice Service");
        List<InvoiceServerPojo> invoiceServerPojoList =  convertResponseModelIntoPojo(invoiceServerRepository.findAll());
        createExcel(workbook, sheet, InvoiceServerPojo.class, invoiceServerPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<InvoiceServerPojo> invoiceServerPojoList =  convertResponseModelIntoPojo(invoiceServerRepository.findAll());
        createPDF(doc, InvoiceServerPojo.class, invoiceServerPojoList, null);
    }
}
