package com.adopt.apigw.modules.reports.recentrenewal.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.repository.CustomRepository;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.reports.recentrenewal.model.ChargeReportResponseModel;
import com.adopt.apigw.modules.reports.recentrenewal.model.RecentRenewalReportResponseModel;
import com.adopt.apigw.modules.reports.recentrenewal.model.ReportRequestModel;
import com.adopt.apigw.modules.reports.recentrenewal.queryscript.ChargeReportQueryScript;
import com.adopt.apigw.modules.reports.recentrenewal.queryscript.RecentRenewalReportQueryScript;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService<T> {

    private String MODULE = " [ReportService] ";

    @Autowired
    private CustomRepository<RecentRenewalReportResponseModel> recentRenewalReportRepository;

    @Autowired
    private CustomRepository<ChargeReportResponseModel> chargeReportRepository;

    public GenericDataDTO getRecentRenewal(ReportRequestModel requestModel) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<RecentRenewalReportResponseModel> totalRecords = new ArrayList<>();
        List<RecentRenewalReportResponseModel> paginationRecords = new ArrayList<>();
        String SUBMODULE = MODULE + " [getRecentRenewal()] ";
        StringBuilder commonQueryP1 = new StringBuilder(RecentRenewalReportQueryScript.COMMON_P1);
        StringBuilder commonQueryP2 = new StringBuilder(RecentRenewalReportQueryScript.COMMON_P3);
        StringBuilder whereCondition = new StringBuilder("");
        StringBuilder finalQuery = new StringBuilder("");
        try {
            boolean flag = false;

            if (null != requestModel.getCustid()) {
                whereCondition.append(RecentRenewalReportQueryScript.CUST_ID + requestModel.getCustid());
                flag = true;
            }

            if (null != requestModel.getPurchaseType() && requestModel.getPurchaseType().equalsIgnoreCase(RecentRenewalReportQueryScript.PURCHASE_TYPE_ONLINE)) {
                commonQueryP1.append(RecentRenewalReportQueryScript.ONLINE_PARAM);
                commonQueryP2.append(RecentRenewalReportQueryScript.COMMON_P4);
            }

            commonQueryP1.append(RecentRenewalReportQueryScript.COMMON_P2);

            if (null != requestModel.getStartDate() && null != requestModel.getEndDate()) {
                commonQueryP1.append(RecentRenewalReportQueryScript.CONCAT + " ('" + requestModel.getStartDate() + "', ' 00:00:00 ' )" + " and "
                        + RecentRenewalReportQueryScript.CONCAT + "('" + requestModel.getEndDate() + "', ' 23:59:59 ' )");
            }

            if (null != requestModel.getPrice1()) {
                if (null != requestModel.getPriceCondition()) {
                    if (requestModel.getPriceCondition().equalsIgnoreCase(RecentRenewalReportQueryScript.EXACTLY)) {
                        commonQueryP1.append("\n" + RecentRenewalReportQueryScript.PLAN_PRICE + RecentRenewalReportQueryScript.EXACTLY_OPERATOR + requestModel.getPrice1());
                    }
                    if (requestModel.getPriceCondition().equalsIgnoreCase(RecentRenewalReportQueryScript.GREATER_THAN)) {
                        commonQueryP1.append("\n" + RecentRenewalReportQueryScript.PLAN_PRICE + RecentRenewalReportQueryScript.GREATER_THAN_OPERATOR + requestModel.getPrice1());
                    }
                    if (requestModel.getPriceCondition().equalsIgnoreCase(RecentRenewalReportQueryScript.LESS_THAN)) {
                        commonQueryP1.append("\n" + RecentRenewalReportQueryScript.PLAN_PRICE + RecentRenewalReportQueryScript.LESS_THAN_OPERATOR + requestModel.getPrice1());
                    }
                    if (requestModel.getPriceCondition().equalsIgnoreCase(RecentRenewalReportQueryScript.BETWEEN) && null != requestModel.getPrice2()) {
                        commonQueryP1.append("\n" + RecentRenewalReportQueryScript.PLAN_PRICE + RecentRenewalReportQueryScript.BETWEEN + " " + requestModel.getPrice1() + RecentRenewalReportQueryScript.AND
                                + requestModel.getPrice2());
                    }
                }
            }

            if (null != requestModel.getPaymentStatus()) {
                if (flag)
                    whereCondition.append(RecentRenewalReportQueryScript.AND);
                whereCondition.append(RecentRenewalReportQueryScript.PAYMENT_STATUS + "'" + requestModel.getPaymentStatus() + "'");
                flag = true;
            }

            if (null != requestModel.getPurchaseStatus()) {
                if (flag)
                    whereCondition.append(RecentRenewalReportQueryScript.AND);
                whereCondition.append(RecentRenewalReportQueryScript.PURCHASE_STATUS + "'" + requestModel.getPurchaseStatus() + "'");
                flag = true;
            }

            finalQuery.append(commonQueryP1 + "\n").append(commonQueryP2);

            if (null != whereCondition && whereCondition.length() > 0) {
                finalQuery.append(RecentRenewalReportQueryScript.WHERE + whereCondition);
            }

            finalQuery.append(RecentRenewalReportQueryScript.ORDER_BY);
            totalRecords = recentRenewalReportRepository.getResultOfQuery(finalQuery.toString(), RecentRenewalReportResponseModel.class);

            if (null != totalRecords && 0 < totalRecords.size()) {
                genericDataDTO = setTotalParam(totalRecords, genericDataDTO, requestModel.getPageSize());
                genericDataDTO.setExcelDataList(totalRecords);
            }

            finalQuery.append(RecentRenewalReportQueryScript.LIMIT + requestModel.getPageSize()
                    + RecentRenewalReportQueryScript.OFFSET + ((requestModel.getPage() - 1) * requestModel.getPageSize()));
            paginationRecords = recentRenewalReportRepository.getResultOfQuery(finalQuery.toString(), RecentRenewalReportResponseModel.class);

            if (null != paginationRecords && 0 < paginationRecords.size()) {
                genericDataDTO = setPaginationParam(paginationRecords, genericDataDTO);
            } else {
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setDataList(null);
                genericDataDTO.setTotalPages(0);
                genericDataDTO.setTotalRecords(0);
            }

            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setCurrentPageNumber(requestModel.getPage());

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public GenericDataDTO getChargeReport(ReportRequestModel requestModel) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ChargeReportResponseModel> totalRecords = new ArrayList<>();
        List<ChargeReportResponseModel> paginationRecords = new ArrayList<>();
        String SUBMODULE = MODULE + " [getChargeReport()] ";
        StringBuilder commonQueryP1 = new StringBuilder(ChargeReportQueryScript.COMMON_P1);
        StringBuilder whereCondition = new StringBuilder("");
        StringBuilder finalQuery = new StringBuilder("");
        try {
            boolean flag = false;

            if (null != requestModel.getCustid()) {
                whereCondition.append(ChargeReportQueryScript.CUST_ID_QUERY + requestModel.getCustid());
                flag = true;
            }

            if (null != requestModel.getChargecategory()) {
                if (flag)
                    whereCondition.append(ChargeReportQueryScript.AND);
                whereCondition.append(ChargeReportQueryScript.CHARGE_CATEGORY_QUERY + "'" + requestModel.getChargecategory() + "'");
                flag = true;
            }

            if (null != requestModel.getChargetype()) {
                if (flag)
                    whereCondition.append(ChargeReportQueryScript.AND);
                whereCondition.append(ChargeReportQueryScript.CHARGE_TYPE_QUERY + "'" + requestModel.getChargetype() + "'");
                flag = true;
            }

            if (null != requestModel.getChargeid()) {
                if (flag)
                    whereCondition.append(ChargeReportQueryScript.AND);
                whereCondition.append(ChargeReportQueryScript.CHARGE_ID_QUERY + requestModel.getChargeid());
                flag = true;
            }

            if (null != requestModel.getPrice1()) {
                if (null != requestModel.getPriceCondition()) {
                    if (flag)
                        whereCondition.append(ChargeReportQueryScript.AND);
                    if (requestModel.getPriceCondition().equalsIgnoreCase(RecentRenewalReportQueryScript.EXACTLY)) {
                        whereCondition.append("\n" + ChargeReportQueryScript.CHARGE_PRICE + ChargeReportQueryScript.EXACTLY_OPERATOR + requestModel.getPrice1());
                    }
                    if (requestModel.getPriceCondition().equalsIgnoreCase(RecentRenewalReportQueryScript.GREATER_THAN)) {
                        whereCondition.append("\n" + ChargeReportQueryScript.CHARGE_PRICE + ChargeReportQueryScript.GREATER_THAN_OPERATOR + requestModel.getPrice1());
                    }
                    if (requestModel.getPriceCondition().equalsIgnoreCase(RecentRenewalReportQueryScript.LESS_THAN)) {
                        whereCondition.append("\n" + ChargeReportQueryScript.CHARGE_PRICE + ChargeReportQueryScript.LESS_THAN_OPERATOR + requestModel.getPrice1());
                    }
                    if (requestModel.getPriceCondition().equalsIgnoreCase(RecentRenewalReportQueryScript.BETWEEN) && null != requestModel.getPrice2()) {
                        whereCondition.append("\n" + ChargeReportQueryScript.CHARGE_PRICE + ChargeReportQueryScript.BETWEEN + " " + requestModel.getPrice1() + RecentRenewalReportQueryScript.AND
                                + requestModel.getPrice2());
                    }
                    flag = true;
                }
            }

            if (null != requestModel.getChargereversal()) {
                if (requestModel.getChargereversal().equalsIgnoreCase(ChargeReportQueryScript.REVERSED_NO)) {
                    if (flag)
                        whereCondition.append(ChargeReportQueryScript.AND);
                    whereCondition.append(ChargeReportQueryScript.IS_REVERSED_QUERY + "0");
                    flag = true;
                }
                if (requestModel.getChargereversal().equalsIgnoreCase(ChargeReportQueryScript.REVERSED_YES)) {
                    if (flag)
                        whereCondition.append(ChargeReportQueryScript.AND);
                    whereCondition.append(ChargeReportQueryScript.IS_REVERSED_QUERY + "1");
                    flag = true;
                }
            }

            if (null != requestModel.getStartDate() && null != requestModel.getEndDate()) {
                if (flag)
                    whereCondition.append(ChargeReportQueryScript.AND);

                whereCondition.append(ChargeReportQueryScript.CHARGE_DATE + ChargeReportQueryScript.CONCAT + " ('" + requestModel.getStartDate() + "', ' 00:00:00 ' )" + " and "
                        + ChargeReportQueryScript.CONCAT + "('" + requestModel.getEndDate() + "', ' 23:59:59 ' )");

            }

            finalQuery.append(commonQueryP1);
            if (null != whereCondition && whereCondition.length() > 0) {
                finalQuery.append(ChargeReportQueryScript.WHERE + whereCondition);
            }

            finalQuery.append(ChargeReportQueryScript.ORDER_BY);
            totalRecords = chargeReportRepository.getResultOfQuery(finalQuery.toString(), ChargeReportResponseModel.class);

            if (null != totalRecords && 0 < totalRecords.size()) {
                genericDataDTO = setTotalParam(totalRecords, genericDataDTO, requestModel.getPageSize());
                genericDataDTO.setExcelDataList(totalRecords);
            }

            finalQuery.append(RecentRenewalReportQueryScript.LIMIT + requestModel.getPageSize()
                    + RecentRenewalReportQueryScript.OFFSET + ((requestModel.getPage() - 1) * requestModel.getPageSize()));
            paginationRecords = chargeReportRepository.getResultOfQuery(finalQuery.toString(), ChargeReportResponseModel.class);

            if (null != paginationRecords && 0 < paginationRecords.size()) {
                genericDataDTO = setPaginationParam(paginationRecords, genericDataDTO);
            } else {
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setDataList(null);
                genericDataDTO.setTotalPages(0);
                genericDataDTO.setTotalRecords(0);
            }

            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setCurrentPageNumber(requestModel.getPage());

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public void excelGenerateForRecentRenewalReport(Workbook workbook, ReportRequestModel requestModel) throws Exception {
        Sheet sheet = workbook.createSheet("RecentRenewalReport");
        GenericDataDTO genericDataDTO = getRecentRenewal(requestModel);
        if (null != genericDataDTO && null != genericDataDTO.getExcelDataList() && 0 < genericDataDTO.getExcelDataList().size()) {
            generateExcel(workbook, sheet, RecentRenewalReportResponseModel.class, genericDataDTO.getExcelDataList(), getFieldsForRecentRenewals());
        }
    }

    public void excelGenerateForChargeReport(Workbook workbook, ReportRequestModel requestModel) throws Exception {
        Sheet sheet = workbook.createSheet("ChargeReport");
        GenericDataDTO genericDataDTO = getChargeReport(requestModel);
        if (null != genericDataDTO && null != genericDataDTO.getExcelDataList() && 0 < genericDataDTO.getExcelDataList().size()) {
            generateExcel(workbook, sheet, ChargeReportResponseModel.class, genericDataDTO.getExcelDataList(), getFieldsForChargeReport());
        }
    }

    public void generateExcel(Workbook workbook, Sheet sheet, Class clazz, List<T> pojoList, Field[] fields) throws InvocationTargetException, IllegalAccessException {
        try {
            Row row = sheet.createRow(0);
            CellStyle style = workbook.createCellStyle();
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setBold(true);
            font.setFontHeight(16);
            style.setFont(font);

            String[] columnNames = fields(clazz, fields);
            for (int i = 0; i < columnNames.length; i++) {
                generateCell(row, i, columnNames[i].toUpperCase(), style, sheet);
            }
            int rowCount = 1;
            style = workbook.createCellStyle();
            font = (XSSFFont) workbook.createFont();
            font.setFontHeight(14);
            style.setFont(font);
            if (fields == null)
                fields = clazz.getDeclaredFields();
            for (T dto : pojoList) {
                Row row1 = sheet.createRow(rowCount++);
                int columnCount = 0;
                for (int i = 0; i < fields.length; i++) {
                    PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, fields[i].getName());
                    if (null != pd) {
                        Method getter = pd.getReadMethod();
                        generateCell(row1, columnCount++, getter.invoke(dto), style, sheet);
                    }
                }
            }
        } catch (Exception exception) {
            ApplicationLogger.logger.error("EXCEL " + exception.getMessage(), exception);
            exception.printStackTrace();
            throw exception;
        }
    }

    public String[] fields(Class clazz, Field[] fields) {
        String[] memberVariables;
        if (fields == null) {
            memberVariables = new String[clazz.getDeclaredFields().length];
            fields = clazz.getDeclaredFields();
        } else
            memberVariables = new String[fields.length];
        Integer i = 0;
        for (Field field : fields) {
            memberVariables[i] = field.getName();
            i++;
        }
        return memberVariables;
    }

    public void generateCell(Row row, int columnCount, Object value, CellStyle style, Sheet sheet) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof java.lang.Long) {
            cell.setCellValue((java.lang.Long) value);
        } else {
            cell.setCellValue(value + "");
        }
        cell.setCellStyle(style);
    }

    private Field[] getFieldsForRecentRenewals() throws NoSuchFieldException {
        return new Field[]{
                RecentRenewalReportResponseModel.class.getDeclaredField("username"),
                RecentRenewalReportResponseModel.class.getDeclaredField("name"),
                RecentRenewalReportResponseModel.class.getDeclaredField("status"),
                RecentRenewalReportResponseModel.class.getDeclaredField("acctnumber"),
                RecentRenewalReportResponseModel.class.getDeclaredField("gst"),
                RecentRenewalReportResponseModel.class.getDeclaredField("pan"),
                RecentRenewalReportResponseModel.class.getDeclaredField("address"),
                RecentRenewalReportResponseModel.class.getDeclaredField("area"),
                RecentRenewalReportResponseModel.class.getDeclaredField("city"),
                RecentRenewalReportResponseModel.class.getDeclaredField("state"),
                RecentRenewalReportResponseModel.class.getDeclaredField("email"),
                RecentRenewalReportResponseModel.class.getDeclaredField("zip"),
                RecentRenewalReportResponseModel.class.getDeclaredField("mobile"),
                RecentRenewalReportResponseModel.class.getDeclaredField("planname"),
                RecentRenewalReportResponseModel.class.getDeclaredField("planprice"),
                RecentRenewalReportResponseModel.class.getDeclaredField("paymentdate"),
                RecentRenewalReportResponseModel.class.getDeclaredField("activationdate"),
                RecentRenewalReportResponseModel.class.getDeclaredField("creationdate"),
                RecentRenewalReportResponseModel.class.getDeclaredField("allotedtime"),
                RecentRenewalReportResponseModel.class.getDeclaredField("allotedtotaldatatransfer"),
                RecentRenewalReportResponseModel.class.getDeclaredField("usedtime"),
                RecentRenewalReportResponseModel.class.getDeclaredField("useddatatransfer"),
                RecentRenewalReportResponseModel.class.getDeclaredField("partner"),
                RecentRenewalReportResponseModel.class.getDeclaredField("renewaltype"),
                RecentRenewalReportResponseModel.class.getDeclaredField("createdfrom"),
                RecentRenewalReportResponseModel.class.getDeclaredField("rechargeby"),
                RecentRenewalReportResponseModel.class.getDeclaredField("paidamount"),
                RecentRenewalReportResponseModel.class.getDeclaredField("purchasefrom"),
        };
    }

    private Field[] getFieldsForChargeReport() throws NoSuchFieldException {
        return new Field[]{
                ChargeReportResponseModel.class.getDeclaredField("username"),
                ChargeReportResponseModel.class.getDeclaredField("name"),
                ChargeReportResponseModel.class.getDeclaredField("status"),
                ChargeReportResponseModel.class.getDeclaredField("email"),
                ChargeReportResponseModel.class.getDeclaredField("mobile"),
                ChargeReportResponseModel.class.getDeclaredField("acctnumber"),
                ChargeReportResponseModel.class.getDeclaredField("gst"),
                ChargeReportResponseModel.class.getDeclaredField("pan"),
                ChargeReportResponseModel.class.getDeclaredField("chargename"),
                ChargeReportResponseModel.class.getDeclaredField("category"),
                ChargeReportResponseModel.class.getDeclaredField("chargetype"),
                ChargeReportResponseModel.class.getDeclaredField("chargeprice"),
                ChargeReportResponseModel.class.getDeclaredField("chargedate"),
                ChargeReportResponseModel.class.getDeclaredField("poolname"),
                ChargeReportResponseModel.class.getDeclaredField("ip"),
                ChargeReportResponseModel.class.getDeclaredField("startdate"),
                ChargeReportResponseModel.class.getDeclaredField("enddate"),
                ChargeReportResponseModel.class.getDeclaredField("reversed"),
                ChargeReportResponseModel.class.getDeclaredField("revdate"),
                ChargeReportResponseModel.class.getDeclaredField("reversedamount"),
                ChargeReportResponseModel.class.getDeclaredField("validity"),
                ChargeReportResponseModel.class.getDeclaredField("createbyname"),
        };
    }

    private GenericDataDTO setTotalParam(List totalRecords, GenericDataDTO genericDataDTO, int pagesize) {
        genericDataDTO.setTotalRecords(totalRecords.size());
        Integer totalPages = totalRecords.size() / pagesize;
        genericDataDTO.setTotalPages(totalPages > 0 ? totalPages : 1);
        return genericDataDTO;
    }

    private GenericDataDTO setPaginationParam(List pageRecords, GenericDataDTO genericDataDTO) {
        genericDataDTO.setPageRecords(pageRecords.size());
        genericDataDTO.setDataList(pageRecords);
        return genericDataDTO;
    }

}
