package com.adopt.apigw.controller.api;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class CustomerRequestExcelGenerator {



    public static CustomerRequest createDummyCustomer(int index) {
        CustomerRequest c = new CustomerRequest();

        c.setCusttype("Type" + index);
        c.setTitle("Mr");
        c.setFirstname("First" + index);
        c.setLastname("Last" + index);
        c.setUsername("user" + index);
        c.setPassword("pass" + index);
        c.setCountryCode("+91");
        c.setPrimaryMobile("999000" + index);
        c.setSecondaryMobile("888000" + index);
        c.setTelephone("012345678" + index);
        c.setFax("01234567" + index);
        c.setEmail("user" + index + "@example.com");
        c.setPan("ABCDE1234" + index);
        c.setContactperson("Contact" + index);
        c.setCalendarType("AD");
        c.setCustomerCategory("Category" + index);
        c.setCDCustomerType("CDType" + index);
        c.setCDCustomerSubType("SubType" + index);
        c.setCustomerSector("Sector" + index);
        c.setCustomerSectorType("SectorType" + index);
        c.setCAFNumber("CAF" + index);
        c.setDOB("1990-01-0" + ((index % 9) + 1));
        c.setBillday(15);
        c.setStatus("ACTIVE");
        c.setDedicatedStaffUserName("staff" + index);
        c.setParentCustomer("parent" + index);
        c.setCustomerType("Retail");
        c.setSalesMark("Mark" + index);
        c.setParentExperience("5 Years");
        c.setServiceArea("Area-" + index);
        c.setBranchName("Branch-" + index);
        c.setPartner("Partner-" + index);
        c.setAddress("Address Line " + index);
        c.setMunicipality("Municipality-" + index);
        c.setWard("Ward-" + index);
        c.setLandmark("Landmark-" + index);
        c.setValleyType("Valley");
        c.setLatitude("27.12345");
        c.setLongitude("85.12345");
        c.setPOP("POP-" + index);
        c.setOLT("OLT-" + index);
        c.setMasterDB("MasterDB-" + index);
        c.setSplitterDB("SplitterDB-" + index);
        c.setStaticIP("192.168.1." + index);
        c.setNASIP("10.10.10." + index);
        c.setNASPortValidate("Yes");
        c.setPlanCategory("Category-" + index);
        c.setPlanGroupName("Group-" + index);
        c.setInvoiceType("POSTPAID");
        c.setBillTo("Self");
        c.setInvoiceToOrganization("Org-" + index);
        c.setBillableTo("Billing-" + index);
        c.setDiscountType("Percentage");
        c.setDiscountPercentage("10");
        c.setDExpiryDate("2025-12-31");
        c.setNewPriceWithDiscount("999");
        c.setServiceName("Service-" + index);
        c.setPlannameList(Arrays.asList("Plan1", "Plan2", "Plan3"));
        c.setAreaName("Area-" + index);
        c.setEarlybillday(3);

        return c;
    }
//    public static Workbook generateExcelToStream(CustomerRequest customers) {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Customer Data");
//
//        CellStyle headerStyle = workbook.createCellStyle();
//        Font headerFont = workbook.createFont();
//        headerFont.setBold(true);
//        headerFont.setFontHeightInPoints((short) 12); // optional
//        headerStyle.setFont(headerFont);
//        Field[] fields = CustomerRequest.class.getDeclaredFields();
//
//        Row headerRow = sheet.createRow(0);
//        for (int i = 0; i < fields.length; i++) {
//            Cell cell = headerRow.createCell(i);
//            cell.setCellValue(fields[i].getName());
//        }
//
//        Row row = sheet.createRow(1);
//        for (int i = 0; i < fields.length; i++) {
//            fields[i].setAccessible(true);
//            Object value = null;
//            try { value = fields[i].get(customers); } catch (Exception ignored) {}
//            row.createCell(i).setCellValue(value == null ? "" : value.toString());
//        }
//
//        return workbook;
//    }

    public static Workbook generateExcelToStream(CustomerRequest customers) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Customer Data");

        // ===== HEADER STYLE (BOLD) =====
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12); // optional
        headerStyle.setFont(headerFont);

        // ===== HEADER ROW =====
        Field[] fields = CustomerRequest.class.getDeclaredFields();
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < fields.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields[i].getName());
            cell.setCellStyle(headerStyle);   // << APPLY BOLD STYLE
        }

        // ===== DATA ROW =====
        Row row = sheet.createRow(1);
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object value = null;
            try { value = fields[i].get(customers); } catch (Exception ignored) {}

            row.createCell(i).setCellValue(value == null ? "" : value.toString());
        }

        return workbook;
    }

}
