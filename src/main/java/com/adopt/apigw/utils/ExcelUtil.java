package com.adopt.apigw.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Component
public class ExcelUtil<DTO> {

    public void generateExcel(Workbook workbook, Sheet sheet, Class clazz, List<DTO> pojoList, Field[] fields) throws InvocationTargetException, IllegalAccessException {
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
            for (DTO dto : pojoList) {
                Row row1 = sheet.createRow(rowCount++);
                int columnCount = 0;
                for (int i = 0; i < fields.length; i++) {
                    PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, fields[i].getName());
                    if(null != pd){
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

}
