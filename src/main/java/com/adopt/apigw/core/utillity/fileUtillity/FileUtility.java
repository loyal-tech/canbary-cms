package com.adopt.apigw.core.utillity.fileUtillity;

import com.adopt.apigw.constants.DocumentConstants;
import com.adopt.apigw.core.exceptions.FileNotCreatedException;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.service.common.ClientServiceSrv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class FileUtility {

    private static final String MODULE = " [File Utility ] ";
    @Autowired
    private ClientServiceSrv clientService;

    private static final Logger logger = LoggerFactory.getLogger(FileUtility.class);

    public String saveFileToServer(MultipartFile argFile, String path) throws IOException {
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        String fileName = "Test";

        int allowedFileSize = clientService.getByName(DocumentConstants.ALLOWED_DOCUMENT_SIZE) != null ? Integer.parseInt(clientService.getByName(DocumentConstants.ALLOWED_DOCUMENT_SIZE).getValue()) : 2;
        if (argFile.getSize() > ((long) allowedFileSize * 1024 * 1024))
            throw new RuntimeException("File size limit exceeds. Please provide document within " + allowedFileSize + "MB");

        if (null != argFile) {
            fileName = (null != argFile.getOriginalFilename()) ? argFile.getOriginalFilename().replace("/", "_").trim() : fileName;
            ApplicationLogger.logger.info(SUBMODULE + " :saveFileToServer-FileName :" + fileName);
        }
//        path="D:\\";
        File file = new File(path + System.currentTimeMillis() + "_" + fileName);
        File directory = new File(path);
        try {
            if (!directory.exists()) {
                directory.mkdir();
            }
            boolean isCreated = file.createNewFile();
            if (!isCreated) {
                throw new FileNotCreatedException();
            }
            if (null != argFile) {
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(argFile.getBytes());
                fout.close();
            }
            return file.getName();
        } catch (IOException e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw new FileNotCreatedException();
        }
    }


    public String saveFileToServerForTicket(MultipartFile argFile, String path) throws IOException {
        String SUBMODULE = MODULE + " [saveFileToServerForTicket()] ";
        Assert.notNull(path, "Path should not be empty");
        String fileName = "Test";

        int allowedFileSize = clientService.getByName(DocumentConstants.ALLOWED_TICKET_DOCUMENT_SIZE) != null ? Integer.parseInt(clientService.getByName(DocumentConstants.ALLOWED_TICKET_DOCUMENT_SIZE).getValue()) : 500;
        if (argFile.getSize() > ((long) allowedFileSize * 8 * 1000))
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "File size limit exceeds. Please provide document within " + allowedFileSize + "KB", null);
        if (null != argFile) {
            fileName = (null != argFile.getOriginalFilename()) ? argFile.getOriginalFilename().replace("/", "_").trim() : fileName;
        }
//        path="D:\\";
        File file = new File(path + System.currentTimeMillis() + "_" + fileName);

        logger.info("===================== Absolute Path :-" + file.getAbsolutePath() + " File Details : -  " + file.toString() + "=====================");
        File directory = new File(path);
        try {
            if (!directory.exists()) {
                directory.mkdir();
            }
            logger.info("=====================Directory Path :- " + directory.getPath() + "=====================");
            boolean isCreated = file.createNewFile();
            if (!isCreated) {
                throw new FileNotCreatedException();
            }

            if (null != argFile) {
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(argFile.getBytes());
                fout.close();
            }
            return file.getName();
        } catch (
                IOException e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw new FileNotCreatedException();
        }

    }

    public boolean removeFileAtServer(String argFile, String path) {
        boolean isFileDeleted = false;
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        try {
            File file = new File(path + argFile);
            if (null == file) {
                ApplicationLogger.logger.debug(SUBMODULE + "File not found with name" + file.getName());
            }
            if (null != file && file.delete()) {
                isFileDeleted = true;
            }
            return isFileDeleted;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public boolean removeFileAtServer(String path) {
        boolean isFileDeleted = false;
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        try {
            File file = new File(path);
            if (null == file) {
                ApplicationLogger.logger.debug(SUBMODULE + "File not found with name" + file.getName());
            }
            if (null != file && file.delete()) {
                isFileDeleted = true;
            }
            return isFileDeleted;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public MultipartFile getFileFromArray(String fileName, MultipartFile[] files) {
        boolean isAvailable = false;
        try {
            Integer allowedFileSize = clientService.getByName(DocumentConstants.ALLOWED_DOCUMENT_SIZE) != null ? Integer.parseInt(clientService.getByName(DocumentConstants.ALLOWED_DOCUMENT_SIZE).getValue()) : 2;
            for (MultipartFile file : files) {
                if (file.getSize() > (allowedFileSize * 1024 * 1024))
                    throw new RuntimeException("File size limit exceeds. Please provide document within " + allowedFileSize + "MB");
                if (fileName.equalsIgnoreCase(file.getOriginalFilename())) {
                    isAvailable = true;
                    return file;
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    public MultipartFile getFileFromArrayForTicket(MultipartFile files) {
        int allowedFileSize = clientService.getByName(DocumentConstants.ALLOWED_TICKET_DOCUMENT_SIZE) != null ? Integer.parseInt(clientService.getByName(DocumentConstants.ALLOWED_TICKET_DOCUMENT_SIZE).getValue()) : 500;
        if (files.getSize() > (long) allowedFileSize  * 1000)
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "File size limit exceeds. Please provide document within " + allowedFileSize + "KB",null);
        else {
            return files;
        }

    }

}
