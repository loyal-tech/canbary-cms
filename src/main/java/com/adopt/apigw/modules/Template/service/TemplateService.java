package com.adopt.apigw.modules.Template.service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Alert.communication.service.CommunicationService;
import com.adopt.apigw.modules.Template.domain.Template;
import com.adopt.apigw.modules.Template.mapper.TemplateMapper;
import com.adopt.apigw.modules.Template.model.TemplateDTO;
import com.adopt.apigw.modules.Template.repository.TemplateRepository;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class TemplateService extends ExBaseAbstractService<TemplateDTO, Template, Long> {
    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    TemplateMapper tamplateMapper;

    @Autowired
    CommunicationService communicationService;

    @Autowired
    private MessagesPropertyConfig messagesProperty;


    private static final String MODULE = " [Template File] ";

    public TemplateService(TemplateRepository repository, TemplateMapper mapper) {
        super(repository, mapper);
        this.tamplateMapper = mapper;
        this.templateRepository = repository;
    }

    public Template save(HttpServletRequest request) {
        String SUBMODULE = MODULE + " [saveTemplate] ";
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile templateFile = multipartRequest.getFile("templateFile");
        String file = multipartRequest.getParameter("file");
        String id = multipartRequest.getParameter("id");
        String name = multipartRequest.getParameter("name");
        String type = multipartRequest.getParameter("type");
        String status = multipartRequest.getParameter("status");

        Template template = new Template();
        try {
            if (id != null) {
                template.setId(Long.parseLong(id));
            }
            if (templateFile.isEmpty() == false && templateFile != null) {
                String fileContent = new String(templateFile.getBytes());
                template.setFile(fileContent);
            } else {
                template.setFile(file);
            }
            template.setName(name);
            template.setStatus(status);
            template.setType(type);
            templateRepository.save(template);
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
        }
        return template;
    }

    @Override
    public String getModuleNameForLog() {
        return "[TemplateService]";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        return null;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Template");
        createExcel(workbook, sheet, TemplateDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, TemplateDTO.class, null,mvnoId);
    }
}
