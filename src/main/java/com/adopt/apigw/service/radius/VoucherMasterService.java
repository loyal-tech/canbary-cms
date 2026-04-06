package com.adopt.apigw.service.radius;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.mapper.postpaid.VoucherMasterMapper;
import com.adopt.apigw.model.radius.VoucherMaster;
import com.adopt.apigw.pojo.api.VoucherMasterPojo;
import com.adopt.apigw.repository.radius.VoucherMasterRepository;
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
public class VoucherMasterService extends AbstractService<VoucherMaster, VoucherMasterPojo, Integer> {

    @Autowired
    private VoucherMasterRepository voucherMasterRepository;
    @Autowired
    private VoucherMasterMapper voucherMasterMapper;

    @Override
    protected JpaRepository<VoucherMaster, Integer> getRepository() {
        return voucherMasterRepository;
    }

    public Page<VoucherMaster> findVoucherMaster(String trim, Integer pageNumber, int dbPageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, dbPageSize);
        return voucherMasterRepository.findVoucherMaster(trim, pageRequest);
    }

    public VoucherMaster findById(Integer id) {
        return voucherMasterRepository.findById(id).orElse(null);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherMaster', '1')")
    public Page<VoucherMaster> getList(Integer pageNumber, int customPageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, customPageSize);
        return voucherMasterRepository.findAll(pageRequest);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherMaster', '2')")
    public VoucherMaster getVoucherMasterForAdd() {
        return new VoucherMaster();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherMaster', '2')")
    public VoucherMaster getVoucherMasterForEdit(Integer id) {
        return voucherMasterRepository.getOne(id);
    }


    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherMaster', '2')")
    public VoucherMaster saveVoucherMaster(VoucherMaster voucherMaster) {
        VoucherMaster save = voucherMasterRepository.save(voucherMaster);
        return save;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Voucher Master");
        List<VoucherMasterPojo> voucherMasterPojos = getRepository().findAll().stream()
                .map(data -> voucherMasterMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, VoucherMasterPojo.class, voucherMasterPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<VoucherMasterPojo> voucherMasterPojos = getRepository().findAll().stream()
                .map(data -> voucherMasterMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, VoucherMasterPojo.class, voucherMasterPojos, null);
    }
}
