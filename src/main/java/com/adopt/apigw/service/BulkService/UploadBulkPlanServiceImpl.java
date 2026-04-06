package com.adopt.apigw.service.BulkService;

import com.adopt.apigw.controller.postpaid.audit.PostPaidPlanAuditService;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.model.postpaid.PostpaidPlanMapper;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.adopt.apigw.modules.qosPolicy.repository.QOSPolicyRepository;
import com.adopt.apigw.pojo.api.PostpaidPlanPojo;
import com.adopt.apigw.rabbitMq.message.PostpaidPlanMessage;
import com.adopt.apigw.repository.common.CustQuotaRepository;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.utils.APIConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UploadBulkPlanServiceImpl implements UploadBulkPlanService{

    @Autowired
    private QOSPolicyRepository qosPolicyRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private PostPaidPlanAuditService planAuditService;

    @Autowired
    private CustPlanMappingRepository custPlanMapppingRepository;

    @Autowired
    private CustQuotaRepository custQuotaRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private PostpaidPlanMapper postpaidPlanMapper;

    @Autowired
    AsyncService asyncService;

    @Transactional
    public String uploadBulkData(MultipartFile file, Integer mvnoId, String userName)throws CustomValidationException{
        try {
            log.info(".....................uploadBulkData Processing started.....................");
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            Map<String, Integer> headerMap = getHeaderMap(workbook.getSheetAt(0));

            //validate Headers and Rows
            log.info(".....................validating master sheet.....................");
            validateMasterSheet(workbook, headerMap);

            //validate Qos-Policies in excel are available in DB or Not
            validateQosPolicies(workbook,mvnoId, headerMap);

            List<PostpaidPlan> postpaidPlans = readExcelToPostpaidPlan(workbook);
            log.info(".....................started saving master sheet.....................");
            return savePostPaidPlan(postpaidPlans, mvnoId,userName);
        } catch (CustomValidationException ex) {
            log.error("Exception Occured::: {}",ex.getMessage());
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (Exception e) {
            HttpStatus status = (e instanceof Exception) ? HttpStatus.NO_CONTENT :
                    (e instanceof Exception) ? HttpStatus.CONFLICT :
                            HttpStatus.NOT_ACCEPTABLE;
            log.error(APIConstants.FETCH_TYPE,
                    APIConstants.FAIL_STATUS,
                    status.value(),
                    e.getMessage()
            );
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateMasterSheet(Workbook workbook, Map<String, Integer> headerMap) throws IllegalArgumentException {
        String SUBMODULE = " [validateMasterSheet()] ";
        try {
            Sheet masterSheet = workbook.getSheetAt(0);
            if (masterSheet == null) {
                log.error(SUBMODULE + "Plan Sheet is not found in the uploaded file.");
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Plan Sheet is not found in the uploaded file.", null);
            }

            validateHeader(headerMap);
            for (int rowIndex = 1; rowIndex <= masterSheet.getLastRowNum(); rowIndex++) {
                Row row = masterSheet.getRow(rowIndex);
                if (row != null) {
                    validateRow(row, rowIndex, headerMap);
                }
            }
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (IllegalArgumentException e) {
            log.error(APIConstants.FETCH_TYPE,
                    APIConstants.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            throw e;
        } catch (Exception e) {
            log.error(APIConstants.FETCH_TYPE,
                    APIConstants.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Integer> getHeaderMap(Sheet masterSheet) {
        Row headerRow = masterSheet.getRow(0);
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            headerMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
        }
        return headerMap;
    }

    public void validateHeader(Map<String, Integer> headerMap) {
        // Define valid headers
        Set<String> validHeaders = new HashSet<>();
        validHeaders.add(BulkManagementConstant.ColumnName.PLAN_NAME);
        validHeaders.add(BulkManagementConstant.ColumnName.END_DATE);
        validHeaders.add(BulkManagementConstant.ColumnName.QUOTA);
        validHeaders.add(BulkManagementConstant.ColumnName.QUOTA_TYPE);
        validHeaders.add(BulkManagementConstant.ColumnName.USAGE_QUOTA_TYPE);
        validHeaders.add(BulkManagementConstant.ColumnName.MAX_CONCURRENTSESSION);
        validHeaders.add(BulkManagementConstant.ColumnName.QUOTA_RESET_INTERVAL);
        validHeaders.add(BulkManagementConstant.ColumnName.QUOTA_UNIT);
        validHeaders.add(BulkManagementConstant.ColumnName.STATUS);
        validHeaders.add(BulkManagementConstant.ColumnName.VALIDITY);
        validHeaders.add(BulkManagementConstant.ColumnName.UNITS_OF_VALIDITY);
        validHeaders.add(BulkManagementConstant.ColumnName.ALLOW_OVER_USAGE);
        validHeaders.add(BulkManagementConstant.ColumnName.QOS_NAME);
        validHeaders.add(BulkManagementConstant.ColumnName.POSTPAID_PLAN_ID);

        // Extract headers from the map
        Set<String> receivedHeaders = headerMap.keySet();

        // Find missing and extra headers
        Set<String> missingHeaders = new HashSet<>(validHeaders);
        missingHeaders.removeAll(receivedHeaders);

        Set<String> extraHeaders = new HashSet<>(receivedHeaders);
        extraHeaders.removeAll(validHeaders);

        // Construct detailed error messages
        StringBuilder errorMessage = new StringBuilder("Header validation failed:");

        if (!missingHeaders.isEmpty()) {
            errorMessage.append(" Missing headers: ").append(String.join(", ", missingHeaders)).append(".");
        }
        if (!extraHeaders.isEmpty()) {
            errorMessage.append(" Extra headers: ").append(String.join(", ", extraHeaders)).append(".");
        }

        // If any headers are invalid (missing or extra), throw an exception
        if (!missingHeaders.isEmpty() || !extraHeaders.isEmpty()) {
            throw new CustomValidationException(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    errorMessage.toString(),
                    null
            );
        }
        // If all headers are valid
        log.info("All headers are valid.");
    }

    private void validateRow(Row row, int rowIndex, Map<String, Integer> headerMap) {
        String SUBMODULE = " [validateRow()] ";
        for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
            String header = entry.getKey();
            int colIndex = entry.getValue();
            Cell cell = row.getCell(colIndex);
            if ( (header.equals(BulkManagementConstant.ColumnName.PLAN_NAME)
                    || header.equals(BulkManagementConstant.ColumnName.POSTPAID_PLAN_ID)
                    || header.equals(BulkManagementConstant.ColumnName.END_DATE)
                    || header.equals(BulkManagementConstant.ColumnName.QUOTA)
                    || header.equals(BulkManagementConstant.ColumnName.QUOTA_TYPE)
                    || header.equals(BulkManagementConstant.ColumnName.USAGE_QUOTA_TYPE)
                    || header.equals(BulkManagementConstant.ColumnName.MAX_CONCURRENTSESSION)
                    || header.equals(BulkManagementConstant.ColumnName.QUOTA_RESET_INTERVAL)
                    || header.equals(BulkManagementConstant.ColumnName.QUOTA_UNIT)
                    || header.equals(BulkManagementConstant.ColumnName.STATUS)
                    || header.equals(BulkManagementConstant.ColumnName.VALIDITY)
                    || header.equals(BulkManagementConstant.ColumnName.UNITS_OF_VALIDITY)
                    || header.equals(BulkManagementConstant.ColumnName.ALLOW_OVER_USAGE)
                    || header.equals(BulkManagementConstant.ColumnName.QOS_NAME)))
            {
                if (isCellEmpty(cell)) {
                    log.error(APIConstants.FETCH_TYPE,
                            APIConstants.FAIL_STATUS,
                            HttpStatus.EXPECTATION_FAILED.value(),
                            "Empty cell found at row " + (rowIndex + 1) +
                                    ", column " + header
                    );
                    log.error(SUBMODULE + "Empty cell found at row " + (rowIndex + 1) + ", column " + header);
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Empty cell found at row " + (rowIndex + 1) + ", column " + header, null);
                }
                if (header.equalsIgnoreCase("QUOTA")) {
                    checkValidNumber(getExcelCellValue(cell), "QUOTA", cell, false, rowIndex);
                } else if (header.equalsIgnoreCase("max_concurrentsession")) {
                    checkValidNumber(getExcelCellValue(cell), "max_concurrentsession", cell, false, rowIndex);
                } else if (header.equalsIgnoreCase("validity")) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        double numericValue = cell.getNumericCellValue();
                        checkValidNumber(numericValue, "validity", cell, false, rowIndex);
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Value must be Numeric at row " + (rowIndex + 1) + ", column " + header, null);
                    }
                } else if (header.equalsIgnoreCase("allow_over_usage")) {
                    getExcelBooleanCellValue(cell, rowIndex, header);
                }
            }
        }
    }

    public boolean isCellEmpty(Cell cell) {
        if (cell == null) {
            return true;
        }
        return cell.getCellType() == CellType.BLANK ||
                (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty());
    }

    public static <T> T checkValidNumber(T value, String header, Cell cell, boolean allowDecimals, int rowIndex) {
        if (value != null) {
            try {
                if (value instanceof Number) {
                    // Check for decimal values if only Long is allowed
                    if (!allowDecimals && value instanceof Double && ((Double) value % 1 != 0)) {
                        throw new CustomValidationException(
                                HttpStatus.EXPECTATION_FAILED.value(),
                                "Invalid value in '" + header + "' column. Expected an integer value but found: " + value + " in COLUMN: " + header + " at ROW: " + (rowIndex + 1),
                                null
                        );
                    }

                    // Perform positive number check
                    double numberValue = ((Number) value).doubleValue();
                    checkNumberPositive(numberValue, header, rowIndex);

                    // Return the value in its original type
                    return value;
                } else {
                    // Attempt to parse non-numeric types
                    String stringValue = value.toString().trim();
                    if (!allowDecimals && stringValue.contains(".")) {
                        throw new CustomValidationException(
                                HttpStatus.EXPECTATION_FAILED.value(),
                                "Invalid value in '" + header + "' column. Expected an integer value but found: " + value + " in COLUMN: " + header + " at ROW: " + (rowIndex + 1),
                                null
                        );
                    }

                    // Parse as Double or Long based on allowance
                    if (allowDecimals) {
                        double parsedDouble = Double.parseDouble(stringValue);
                        checkNumberPositive(parsedDouble, header, rowIndex);
                        return (T) Double.valueOf(parsedDouble);
                    } else {
                        long parsedLong = Long.parseLong(stringValue);
                        checkNumberPositive(parsedLong, header, rowIndex);
                        validateLongNumber(parsedLong, header, rowIndex);
                        return (T) Long.valueOf(parsedLong);
                    }
                }
            } catch (NumberFormatException e) {
                throw new CustomValidationException(
                        HttpStatus.EXPECTATION_FAILED.value(),
                        "Value in '" + header + "' column cannot be converted to a valid number: " + value + " in COLUMN: " + header + " at ROW: " + (rowIndex + 1),
                        e
                );
            }
        } else {
            throw new CustomValidationException(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Invalid value in '" + header + "' column. Expected a numeric value but found: " + cell.toString() + " in COLUMN: " + header + " at ROW: " + (rowIndex + 1),
                    null
            );
        }
    }

    private static void checkNumberPositive(double d, String header, int rowIndex) {
        if (d < 0) {
            throw new CustomValidationException(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Invalid value in '" + header + "' column. Expected a positive numeric value only but found: " + d + " in COLUMN: " + header + " at ROW: " + (rowIndex + 1),
                    null
            );
        }
    }


    private static void validateLongNumber(long d, String header, int rowIndex) {
        if (d < 0 || d > 999_999_999) {
            throw new CustomValidationException(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Invalid value in '" + header + "' column. Expected a numeric value Range Between 0 to 999999999 but found:: " + d + " in COLUMN: " + header + " at ROW: " + (rowIndex + 1),
                    null
            );
        }
    }

    public void validateQosPolicies(Workbook workbook, Integer mvno, Map<String, Integer> headerColumnMap) throws IOException {
        // Fetch all QoS data from the database
        List<String> qosData = getAllQosData(mvno);

        // Get the first sheet (assumed to be "MASTER-SHEET")
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new IllegalArgumentException("Sheet not found in the workbook.");
        }

        // Extract headers and map them to column indices
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Header row is missing in the sheet.");
        }

        // Ensure required headers are present
        if (!headerColumnMap.containsKey("qos_name")) {
            throw new IllegalArgumentException("'qos_name' column is missing in the sheet.");
        }

        // Validate QoS data
        int qosNameColumnIndex = headerColumnMap.get("qos_name");
        boolean qosNameFound = false;

        for (Row row : sheet) {
            // Skip the header row
            if (row.getRowNum() == 0) continue;

            Cell cell = row.getCell(qosNameColumnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null) {
                String qosName = getExcelCellValue(cell).trim();
                if (!qosData.contains(qosName)) {
                    throw new IllegalArgumentException(
                            "QoS name '" + qosName + "' not found in the database for MVNO: " + mvno
                    );
                }
                qosNameFound = true;
            }
        }

        // Throw exception if no 'qos_name' values are found
        if (!qosNameFound) {
            throw new IllegalArgumentException("'qos_name' values not found in the sheet.");
        }
    }

    public List<String> getAllQosData(Integer mvno) {
        try {
            return qosPolicyRepository.findNamesByIsDeletedFalseAndMvnoIdIn(Arrays.asList(1, mvno));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public static List<PostpaidPlan> readExcelToPostpaidPlan(Workbook workbook) throws IOException {
        List<PostpaidPlan> plans = new ArrayList<>();
//        Workbook workbook = planworkbook;

        Sheet sheet = workbook.getSheetAt(0);

        // Read header row
        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue());
        }

        // Iterate over rows (skip header row)
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Skip header row

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            PostpaidPlan plan = new PostpaidPlan();

            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i);
                Cell cell = row.getCell(i);

                // Map cell value to object fields based on header
                if (header.equalsIgnoreCase("POSTPAID_PLAN_ID")) {
                    plan.setId(Integer.valueOf(getExcelCellValue(cell)));
                } else if (header.equalsIgnoreCase("PLAN_NAME")) {
                    plan.setName(getExcelCellValue(cell));
                } else if (header.equalsIgnoreCase("END_DATE")) {
                    plan.setEndDate(getExcelDateCellValue(cell));
                } else if (header.equalsIgnoreCase("QUOTA")) {
                    plan.setQuota(Long.valueOf(getExcelCellValue(cell)));
                } else if (header.equalsIgnoreCase("quota_type")) {
                    plan.setQuotatype(getExcelCellValue(cell));
                } else if (header.equalsIgnoreCase("usage_quota_type")) {
                    plan.setUsageQuotaType(getExcelCellValue(cell));
                } else if (header.equalsIgnoreCase("max_concurrentsession")) {
                    plan.setMaxconcurrentsession(getExcelCellValue(cell));
                } else if (header.equalsIgnoreCase("quota_reset_interval")) {
                    plan.setQuotaResetInterval(getExcelCellValue(cell));
                } else if (header.equalsIgnoreCase("QUOTA_UNIT")) {
                    plan.setQuotaUnit(getExcelCellValue(cell));
                } else if (header.equalsIgnoreCase("STATUS")) {
                    plan.setStatus(getExcelCellValue(cell));
                } else if (header.equalsIgnoreCase("validity")) {
                    plan.setValidity((double) cell.getNumericCellValue());
                } else if (header.equalsIgnoreCase("units_of_validity")) {
                    plan.setUnitsOfValidity(getExcelCellValue(cell));
                } else if (header.equalsIgnoreCase("allow_over_usage")) {
                    //get Safe boolean
                    plan.setAllowOverUsage(getExcelBooleanCellValue(cell, 0, null));
                } else if (header.equalsIgnoreCase("qos_name")) {
                    String qosName = getExcelCellValue(cell);
                    QOSPolicy qospolicy = new QOSPolicy();
                    qospolicy.setName(qosName);
                    plan.setQospolicy(qospolicy);
                }
            }
            plans.add(plan);
        }

        workbook.close();
        return plans;
    }

    private static String getExcelCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return cell.getCellType() == CellType.NUMERIC ?
                String.valueOf((long) cell.getNumericCellValue()).trim() :
                cell.getStringCellValue().trim();
    }

    private static LocalDate getExcelDateCellValue(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Cell can't be Empty...");
        }

        DataFormatter formatter = new DataFormatter();
        String cellValue = formatter.formatCellValue(cell);

        // Define the date formats
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Try parsing with the first format
        try {
            return LocalDate.parse(cellValue, formatter1);
        } catch (DateTimeParseException e1) {
            // Fallback to the second format
            try {
                return LocalDate.parse(cellValue, formatter2);
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Unable to parse date: " + cellValue);
            }
        }
    }

    private static boolean getExcelBooleanCellValue(Cell cell, int rowIndex, String header) {
        switch (cell.getCellType()) {
            case BOOLEAN:
                // Directly return the boolean value for BOOLEAN cell type
                return cell.getBooleanCellValue();

            case STRING:
                // Parse string value to boolean
                String cellValue = getExcelCellValue(cell).toLowerCase();
                if ("true".equalsIgnoreCase(cellValue)) {
                    return true;
                } else if ("false".equalsIgnoreCase(cellValue)) {
                    return false;
                } else {
                    throw new CustomValidationException(
                            HttpStatus.EXPECTATION_FAILED.value(),
                            "Invalid value at row" + (rowIndex + 1) + " column " + header + ". Value must be 'True' or 'False' only.",
                            null
                    );
                }
            default:
                throw new CustomValidationException(
                        HttpStatus.EXPECTATION_FAILED.value(),
                        "Unsupported cell type. Value must be 'True' or 'False' only.",
                        null
                );
        }
    }


    @Transactional
    public String savePostPaidPlan(List<PostpaidPlan> plans, Integer staffId, String userName) {
        log.info("Total PostPaidPlan Will be saved::: {}", plans.size());
        ArrayList<PostpaidPlan> updatedPostpaidPlans = new ArrayList<>();

        List<Integer> planIds = new ArrayList<>();
        List<String> qasNames = new ArrayList<>();

        for (PostpaidPlan plan : plans) {
            planIds.add(plan.getId());
            qasNames.add(plan.getQospolicy().getName());
        }

        List<PostpaidPlan> byId = postpaidPlanRepo.findAllByIdIn(planIds);
        List<QOSPolicy> qosPolicies = qosPolicyRepository.findAllByIsDeletedFalseAndNameInAndMvnoIdIn(qasNames,Arrays.asList(1,staffId));

        Map<String, QOSPolicy> qosPolicyMap = qosPolicies.stream()
                .collect(Collectors.toMap((qas -> qas.getName().trim()), policy -> policy));

        int count = 0;
        for (PostpaidPlan plan : plans) {
            if (null == qosPolicyMap.get(plan.getQospolicy().getName())) {
                log.error("QOS Policy not present in Database with Name:: {}. ", plan.getQospolicy().getName());
                throw new RuntimeException("QOS Policy not present in Database with Name:: " + plan.getQospolicy().getName() + ".");
            }
            if (!byId.isEmpty()) {
                PostpaidPlan updatedPostpaidPlan = byId.get(count);
                PostpaidPlan existingPostpaidPlan = new PostpaidPlan(updatedPostpaidPlan);

                //Fields added manually, coz not populating while creating object with new keyword
                existingPostpaidPlan.setServiceAreaNameList(updatedPostpaidPlan.getServiceAreaNameList());
                existingPostpaidPlan.setBandwidth(updatedPostpaidPlan.getBandwidth());
                existingPostpaidPlan.setMvnoName(updatedPostpaidPlan.getMvnoName());

                updatedPostpaidPlan.setName(plan.getName());
                updatedPostpaidPlan.setEndDate(plan.getEndDate());
                updatedPostpaidPlan.setQuota(plan.getQuota());
                updatedPostpaidPlan.setQuotatype(plan.getQuotatype());
                updatedPostpaidPlan.setUsageQuotaType(plan.getUsageQuotaType());
                updatedPostpaidPlan.setMaxconcurrentsession(plan.getMaxconcurrentsession());
                updatedPostpaidPlan.setQuotaResetInterval(plan.getQuotaResetInterval());
                updatedPostpaidPlan.setQuotaUnit(plan.getQuotaUnit());
                updatedPostpaidPlan.setStatus(plan.getStatus());
                updatedPostpaidPlan.setValidity(plan.getValidity());
                updatedPostpaidPlan.setUnitsOfValidity(plan.getUnitsOfValidity());
                updatedPostpaidPlan.setAllowOverUsage(plan.getAllowOverUsage());
                updatedPostpaidPlan.setQospolicy(qosPolicyMap.get(plan.getQospolicy().getName().trim()));
                boolean updated = planAuditService.updatePostpaidPlan(existingPostpaidPlan, updatedPostpaidPlan, staffId, userName);
                if (updated) {
                    updatedPostpaidPlans.add(updatedPostpaidPlan);
                }
            } else {
                log.error("No Data found in Database with given Id::  {} And Name:: {}.", plan.getId(), plan.getName());
                throw new RuntimeException("No Data found in Database with given Id::  " + plan.getId() + " And Name::" + plan.getName());
            }
            count++;
            log.info(" Processing Counter:: "+ count);
        }
        asyncService.doAsync(updatedPostpaidPlans);
        return updatedPostpaidPlans.size() + " PostPaidPlan Updated Successfully";
    }


    @Transactional(rollbackFor = Exception.class)
     public void updatePlansAndCustmapping(List<PostpaidPlan> updatedPostpaidPlans){
        if (!CollectionUtils.isEmpty(updatedPostpaidPlans)) {
            long startTime = System.currentTimeMillis();
            postpaidPlanRepo.saveAll(updatedPostpaidPlans);
            log.info("Total {} Plans Updated with time ::: {}.", updatedPostpaidPlans.size(), ((System.currentTimeMillis() - startTime) / 1000));
            try {
                long startTime1 = System.currentTimeMillis();
                updateCustPlanOnPlanUpdateUsingJPQL(updatedPostpaidPlans);
                log.info("Total {} CustPlanMappings Updated with time ::: {}.", updatedPostpaidPlans.size(), ((System.currentTimeMillis() - startTime1) / 1000));
            } catch (Exception ex) {
                log.error("Error to update customer plan and quota");
            }
            List<PostpaidPlanPojo> postpaidPlanDTOS = postpaidPlanMapper.domainToDTO(updatedPostpaidPlans, new CycleAvoidingMappingContext());
            PostpaidPlanMessage message = new PostpaidPlanMessage(true, true, postpaidPlanDTOS);
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName(), "BulkUpdate"));
        }
    }


    @Transactional
    public void updateCustPlanOnPlanUpdateUsingJPQL(List<PostpaidPlan> postpaidPlans) {
        try {
            log.info("No Of Plans to Update: " + postpaidPlans.size());
            for (PostpaidPlan plan : postpaidPlans) {
                // Directly update CustPlanMapping
                int updatedPlans = custPlanMapppingRepository.updateQosPolicyIdByPlanId(plan.getQospolicy().getId(), plan.getId());
                if (updatedPlans > 0) {
                    log.info("Updated CustPlanMappings for planId: " + plan.getId());
                } else {
                    log.debug("No Customer Plans found to update for planId: " + plan.getId());
                }
                // Fetch IDs of updated mappings
                List<Long> cprIds = custPlanMapppingRepository.fetchUpdatedCprIds(plan.getId());
                // Directly update CustQuotaDetails if needed
                if (!CollectionUtils.isEmpty(cprIds)) {
                    int updatedQuota = custQuotaRepository.updateQuotaDetailsByCprIds(Double.valueOf(plan.getQuota()), plan.getQuotaUnit(), plan.getQuotatype(), plan.getUsageQuotaType(), cprIds);
                    log.info("Updated CustQuotaDetails for " + updatedQuota + " records.");
                } else {
                    log.debug("No Customer Quota Details found for planId: " + plan.getId());
                }

            }
        } catch (Exception ex) {
            log.error("Error in updating Customer Plan and Quota on plan update", ex);
            throw new RuntimeException(ex);
        }
    }

    public void writeWorkbookToDirectory(Workbook workbook, String filePath) {
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            System.out.println("Workbook written successfully to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing the workbook: " + e.getMessage());
        }
    }

}
