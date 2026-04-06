package com.adopt.apigw.modules.mvnoDocDetails.service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.mvnoDocDetails.domain.MvnoDocDetails;
import com.adopt.apigw.modules.mvnoDocDetails.model.MvnoDocDetailsDTO;
import com.itextpdf.text.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface DocDetailsService {

    List<MvnoDocDetailsDTO> uploadDocument(List<MvnoDocDetailsDTO> mvnoDocDetailsList, Long mvnoId, MultipartFile[] files);

    List<MvnoDocDetailsDTO> findDocsByEntityId(Long mvnoId);

    boolean isDocPending(Long mvnoId);

    String deleteDocument(List<Long> docIdList, Long mvnoId);

    void pdfGenerate(Document doc, Integer mvnoId);

    GenericDataDTO getDocApprovals(Long docId, Boolean isApproveRequest, String remarks);

    MvnoDocDetailsDTO updateEntity(MvnoDocDetailsDTO mvnoDocDetailsDTO);
    MvnoDocDetailsDTO saveEntity(MvnoDocDetailsDTO mvnoDocDetailsDTO);

    MvnoDocDetailsDTO getEntityById(Long docId);

    List<MvnoDocDetails> getMvnoDocumentforDunning(Integer dateDiff , Integer mvnoId);
}
