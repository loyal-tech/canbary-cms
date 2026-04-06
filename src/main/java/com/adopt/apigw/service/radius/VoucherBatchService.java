package com.adopt.apigw.service.radius;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.mapper.postpaid.VoucherBatchMapper;
import com.adopt.apigw.model.radius.VoucherBatch;
import com.adopt.apigw.pojo.api.VoucherBatchPojo;
import com.adopt.apigw.repository.radius.VoucherBatchRepository;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherBatchService extends AbstractService<VoucherBatch, VoucherBatchPojo, Integer> {

    @Autowired
    VoucherBatchRepository voucherBatchRepository;
    @Autowired
    private VoucherBatchMapper voucherBatchMapper;

    @Override
    protected JpaRepository<VoucherBatch, Integer> getRepository() {
        return voucherBatchRepository;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherBatch', '1')")
    public Page<VoucherBatch> findVoucherBatch(String trim, Integer pageNumber, int dbPageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, dbPageSize);
        return voucherBatchRepository.findVoucherBatch(trim, pageRequest);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherBatch', '1')")
    public Page<VoucherBatch> getVoucherBatchListByMasterId(Integer id, Integer pageNumber, int dbPageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, dbPageSize);
        return voucherBatchRepository.getListById(String.valueOf(id), pageRequest);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherBatch', '1')")
    public List<VoucherBatch> getVoucherBatchList(Integer id) {
        return voucherBatchRepository.findByvcId(id);
    }

    public VoucherBatch getVoucherBatchByVoucherCode(String voucherCode) {
        return voucherBatchRepository.findByvoucherCode(voucherCode);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherBatch', '2')")
    public VoucherBatch getVoucherBatchForAdd() {
        return new VoucherBatch();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherBatch', '2')")
    public VoucherBatch getVoucherBatchForEdit(Integer id) {
        return voucherBatchRepository.getOne(id);
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Voucher Batch");
        List<VoucherBatchPojo> voucherBatchPojos = getRepository().findAll().stream()
                .map(data -> voucherBatchMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, VoucherBatchPojo.class, voucherBatchPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<VoucherBatchPojo> voucherBatchPojos = getRepository().findAll().stream()
                .map(data -> voucherBatchMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, VoucherBatchPojo.class, voucherBatchPojos, null);
    }
}
