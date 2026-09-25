package com.mycompany.fichajefx.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mycompany.fichajefx.models.Fichaje;

import java.io.FileOutputStream;
import java.util.List;

public class PdfService {
    public static void generateAuditReport(String filePath, List<Fichaje> records) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
        Paragraph title = new Paragraph("Registro de Auditoría de Fichajes", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(25);
        document.add(title);

        // Metadata
        Paragraph meta = new Paragraph("Fecha de generación: " + new java.util.Date().toString() + "\n\n");
        document.add(meta);

        // Table
        PdfPTable table = new PdfPTable(new float[] { 3.5f, 5, 2.5f, 6 }); // Width ratios
        table.setWidthPercentage(100);

        // Headers with contrast
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        addHeaderCell(table, "DNI", headerFont);
        addHeaderCell(table, "Nombre Empleado", headerFont);
        addHeaderCell(table, "Acción", headerFont);
        addHeaderCell(table, "Fecha/Hora", headerFont);

        for (Fichaje record : records) {
            table.addCell(new Phrase(record.getDni(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
            table.addCell(new Phrase(record.getNombre_empleado(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
            table.addCell(new Phrase(record.getTipo(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            table.addCell(new Phrase(record.getTimestamp(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        }

        document.add(table);
        document.close();
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBackgroundColor(new java.awt.Color(226, 232, 240));
        table.addCell(cell);
    }
}
