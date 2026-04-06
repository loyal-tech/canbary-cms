package com.adopt.apigw.service.BulkService;


import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import com.adopt.apigw.modules.CommonList.service.CommonListService;
import com.adopt.apigw.modules.qosPolicy.repository.QOSPolicyRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
@Slf4j
public class DownloadBulkPlanServiceImpl implements DownloadBulkPlanService {

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private QOSPolicyRepository qosPolicyRepository;

    @Autowired
    private CommonListService commonListService;

    private final static String QUOTA_TYPE_DATA = "quotaTypeData";
    private final static String QUOTA_TYPE_TIME = "quotaTypeTime";
    private final static String CHARGE_CATEGORY = "chargeCategory";
    private final static String COMMON_STATUS = "commonStatus";
    private final static String QUOTA_RESET_INTERVAL_DATA = "quotaResetIntervalData";

    private final List<String> staticHeaders1 = Arrays.asList(
            "POSTPAID_PLAN_ID", "PLAN_NAME",
            "END_DATE", "QUOTA", "quota_type", "usage_quota_type", "max_concurrentsession",
            "quota_reset_interval", "QUOTA_UNIT", "STATUS", "validity", "units_of_validity",
            "allow_over_usage", "qos_name"
    );

    public Resource writePostpaidPlansToExcel(Integer mvno) throws Exception {

        List<PostpaidPlan> postPaidPlans = getPostPaidPlans(mvno);
        int lastRow = postPaidPlans.size();
        log.info("postpaidPlanDetails details fetched with size {}", lastRow);

        String masterSheetName = "MASTER-SHEET";
        List<Object> objects = writeExcelSheetWithHeaders(staticHeaders1, masterSheetName);
        Workbook workbook = (Workbook) objects.get(0);
        Sheet masterSheet = (Sheet) objects.get(1);

        writePostPaidPlansToMasterSheet(postPaidPlans, masterSheet);
        log.info("Added Postpaid plan to Excel....");

        List<String> qosPolicyNames = getAllQosData(mvno);
        log.info("Adding Service-AreaNames to Sheet.....");

        addValidationToWorkBook(masterSheet, lastRow, qosPolicyNames,mvno);

        return generateResource(workbook);
    }

    public List<PostpaidPlan> getPostPaidPlans(Integer mvno) {
        List<PostpaidPlan> postpaidPlanDetails;
        if (mvno == 1) {
            postpaidPlanDetails = postpaidPlanRepo.findAllActivePlansWithQosPolicy("Active");
        } else {
            postpaidPlanDetails = postpaidPlanRepo.findAllActivePlansWithQosPolicyByMvnoIds("Active", Collections.singletonList(mvno));
        }
        return postpaidPlanDetails;
    }

    public List<Object> writeExcelSheetWithHeaders(List<String> header, String masterSheetName) {

        List<Object> objects = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(masterSheetName);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header.get(i));
                CellStyle headerCellStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerCellStyle.setFont(font);
                cell.setCellStyle(headerCellStyle);
            }
            objects.add(workbook);
            objects.add(sheet);
            log.info("Excel sheet with headers created successfully");
        } catch (Exception e) {

            throw new RuntimeException(e.getMessage());
        }
        return objects;
    }

    public void writePostPaidPlansToMasterSheet(List<PostpaidPlan> postpaidPlanDetails, Sheet sheet) {
        int rowNum = 1;
        for (PostpaidPlan postpaidPlan : postpaidPlanDetails) {
            Row row = sheet.createRow(rowNum++);
            int cellNum = 0;
            // Write data in the same order as the headers
            row.createCell(cellNum++).setCellValue(postpaidPlan.getId());
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getName()));
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getEndDate()));
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getQuota()));
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getQuotatype()));
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getUsageQuotaType()));
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getMaxconcurrentsession()));
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getQuotaResetInterval()));
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getQuotaUnit()));
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getStatus()));
            row.createCell(cellNum++).setCellValue(getIntegerNonNullValue(postpaidPlan.getValidity()));
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getUnitsOfValidity()));
            row.createCell(cellNum++).setCellValue(getNonNullValue(postpaidPlan.getAllowOverUsage()));
            row.createCell(cellNum++).setCellValue(postpaidPlan.getQospolicy() != null ? postpaidPlan.getQospolicy().getName() : "");
        }
    }

    public static <T> String getNonNullValue(T value) {
        return value != null
                ? value.toString().trim()
                : "";
    }

    public static <T> Integer getIntegerNonNullValue(T value) {
        if (value != null) {
            try {
                if (value instanceof Number) {
                    // Direct conversion for numeric types
                    return ((Number) value).intValue();
                } else {
                    // Fallback for non-numeric types
                    return Integer.parseInt(value.toString().trim());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Value cannot be converted to Integer: " + value, e);
            }
        }
        return 0;
    }

    public List<String> getAllQosData(Integer mvno) {
        try {
            return qosPolicyRepository.findNamesByIsDeletedFalseAndMvnoIdIn(Arrays.asList(1, mvno));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void addValidationToWorkBook(Sheet masterSheet, int lastRow, List<String> qosPolicyNames, Integer mvno) throws Exception {
        Map<String, List<CommonListDTO>> commonList = getCommonList(mvno);

        String[] array = {"Hours", "Days", "Months", "Years"};
        addValidityDropdown(masterSheet, array, 11, 1, lastRow);

        String[] array1 = commonList.get(QUOTA_TYPE_DATA).stream().map(CommonListDTO::getValue).toArray(String[]::new);
        addValidityDropdown(masterSheet, array1, 8, 1, lastRow);

        String[] array2 = commonList.get(COMMON_STATUS).stream().map(CommonListDTO::getValue).toArray(String[]::new);
        addValidityDropdown(masterSheet, array2, 9, 1, lastRow);


        String[] array3 = commonList.get(QUOTA_RESET_INTERVAL_DATA).stream().map(CommonListDTO::getValue).toArray(String[]::new);
        addValidityDropdown(masterSheet, array3, 7, 1, lastRow);

        String[] array5 = {"TOTAL", "DOWNLOAD", "UPLOAD"};
        addValidityDropdown(masterSheet, array5, 5, 1, lastRow);

        String[] array4 = qosPolicyNames.stream().filter(Objects::nonNull).toArray(String[]::new);
        addValidityDropdown(masterSheet, array4, 13, 1, lastRow);


        addDateValidationRules(masterSheet, new int[]{2}, 1, lastRow);

        int startColumn =0;
        int endColumn   =13;
        setCellWidth(masterSheet, startColumn,endColumn);

//        disableColumnEditing(masterSheet, new int[]{0,4}, lastIndex);
    }

    public Map<String, List<CommonListDTO>> getCommonList(Integer mvno) throws Exception {
        Map<String, List<CommonListDTO>> allData = new HashMap<>();
        commonListService.getAllEntities(mvno);
        List<CommonListDTO> quotaTypeData = commonListService.getCommonListByType(QUOTA_TYPE_DATA);
        List<CommonListDTO> quotaTypeTime = commonListService.getCommonListByType(QUOTA_TYPE_TIME);
        List<CommonListDTO> chargeCategory = commonListService.getCommonListByType(CHARGE_CATEGORY);
        List<CommonListDTO> commonStatus = commonListService.getCommonListByType(COMMON_STATUS);
        List<CommonListDTO> quotaResetIntervalData = commonListService.getCommonListByType(QUOTA_RESET_INTERVAL_DATA);
        allData.put(QUOTA_TYPE_DATA, quotaTypeData);
        allData.put(QUOTA_TYPE_TIME, quotaTypeTime);
        allData.put(CHARGE_CATEGORY, chargeCategory);
        allData.put(COMMON_STATUS, commonStatus);
        allData.put(QUOTA_RESET_INTERVAL_DATA, quotaResetIntervalData);
        return allData;
    }


    public void addValidityDropdown(Sheet sheet, String[] array, int columnIndex, int startRow, int lastRow) {
        try {
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(array);
            CellRangeAddressList addressList = new CellRangeAddressList(startRow, lastRow, columnIndex, columnIndex);
            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
            dataValidation.setShowErrorBox(true);
            dataValidation.createErrorBox("Invalid Values", "Values must select from Dropdown");
            sheet.addValidationData(dataValidation);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void addDateValidationRules(Sheet sheet, int[] columIndex, int startRow, int endRow) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

        // Column I (Date Validation)
        CellRangeAddressList dateRangeI = new CellRangeAddressList(startRow, endRow, columIndex[0], columIndex[0]); // Column I (Index 8)
//        CellRangeAddressList dateRangeH = new CellRangeAddressList(startRow, endRow, columIndex[1],columIndex[1]); // Column I (Index 8)

        // Create date constraint using Excel's internal date serial numbers
        // Excel dates are stored as numbers where 1 = January 1, 1900
        DataValidationConstraint dateConstraint = validationHelper.createDateConstraint(
                DataValidationConstraint.OperatorType.BETWEEN,
                "DATE(1900,1,1)", // Start Date
                "DATE(2099,12,31)", // End Date
                null  // Remove the format string
        );

        DataValidation dateValidation = validationHelper.createValidation(dateConstraint, dateRangeI);
        dateValidation.setSuppressDropDownArrow(true);
        dateValidation.setShowErrorBox(true);
        dateValidation.createErrorBox("Invalid Input", "Please enter a valid date.");
        sheet.addValidationData(dateValidation);
    }

    void setCellWidth(Sheet masterSheet, int startColumn, int endColumn) {
        if (masterSheet == null || startColumn < 0 || endColumn < startColumn) {
            throw new IllegalArgumentException("Invalid sheet or column range.");
        }

        for (int columnIndex = startColumn; columnIndex <= endColumn; columnIndex++) {
            int maxLength = 0;

            // Iterate through all rows to find the longest word in the column
            for (int rowIndex = 0; rowIndex < masterSheet.getPhysicalNumberOfRows(); rowIndex++) {
                Row row = masterSheet.getRow(rowIndex);
                if (row != null) {
                    Cell cell = row.getCell(columnIndex);
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue();
                        maxLength = Math.max(maxLength, cellValue.length());
                    }
                    if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                        Double cellValue = cell.getNumericCellValue();
                        maxLength = Math.max(maxLength, String.valueOf(cellValue).length());
                    }
                }
            }

            // Set the column width based on the longest word (characters wide)
            masterSheet.setColumnWidth(columnIndex, (maxLength+2) * 256);
        }
    }


    private Resource generateResource(Workbook workbook){
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void disableColumnEditing(Sheet sheet, int[] columnIndexes, int totalRows) {
        Workbook workbook = sheet.getWorkbook();

        // Create styles
        CellStyle lockedStyle = workbook.createCellStyle();
        lockedStyle.setLocked(true);

        CellStyle unlockedStyle = workbook.createCellStyle();
        unlockedStyle.setLocked(false);

        // Loop through all rows and columns
        for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            for (int colIndex = 0; colIndex < sheet.getRow(0).getPhysicalNumberOfCells(); colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) {
                    cell = row.createCell(colIndex);
                }

                // Check if current column should be locked
                boolean shouldLock = false;
                for (int lockColumn : columnIndexes) {
                    if (colIndex == lockColumn) {
                        shouldLock = true;
                        break;
                    }
                }

                cell.setCellStyle(shouldLock ? lockedStyle : unlockedStyle);
            }
        }

        // Enable sheet protection
//        sheet.protectSheet("your_password");
    }
}
