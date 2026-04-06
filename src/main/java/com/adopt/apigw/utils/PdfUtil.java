package com.adopt.apigw.utils;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class PdfUtil<DTO> {

    public void generatePdf(Document doc, Class clazz, List<DTO> pojoList, Field[] fields) throws Exception {
        try {

            doc.setPageSize(PageSize.A4.rotate());
            doc.open();

            if (fields == null)
                fields = clazz.getDeclaredFields();

            PdfPTable table = new PdfPTable(fields.length);
            table.setWidthPercentage(100);

            String[] columnNames = new ExcelUtil<>().fields(clazz, fields);
            for (int i = 0; i < columnNames.length; i++) {
                generatePdfCell(table, columnNames[i].toUpperCase(), BaseColor.GRAY);
            }

            for (DTO dto : pojoList) {
                for (int i = 0; i < fields.length; i++) {
                    PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, fields[i].getName());
                    Method getter = pd.getReadMethod();
                    generatePdfCell(table, getter.invoke(dto), BaseColor.WHITE);
                }
            }
            doc.add(table);
            doc.close();

        } catch (Exception ex) {
            ApplicationLogger.logger.error("PDF " + ex.getMessage() + ex);
            throw ex;
        }
    }

    public void generatePdfCell(PdfPTable table, Object text, BaseColor backgroundColor) {
        PdfPCell cell = null;

        if (text instanceof Integer || text instanceof Boolean
                || text instanceof LocalDate
                || text instanceof LocalTime
                || text instanceof LocalDateTime
                || text instanceof String) {
            cell = new PdfPCell(new Phrase(text.toString()));
        } else if (text instanceof Double) {
            cell = new PdfPCell(new Phrase(String.valueOf(text)));
        } else if (text instanceof Float) {
            cell = new PdfPCell(new Phrase((Float) text));
        } else if (text instanceof List) {
            cell = new PdfPCell(new Phrase(text.toString()));
        } else {
            cell = new PdfPCell(new Phrase());
        }

        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
