package com.event_management.services;

import com.event_management.entities.Event;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfGenerationService {

    private final EventService eventService;

    @Autowired
    public PdfGenerationService(EventService eventService){
        this.eventService = eventService;
    }

    public byte[] generatePdf() throws DocumentException, IOException {

        List<Event> events = eventService.getAllEvents();

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();
        document.add(new Paragraph("Event Report"));

        PdfPTable table = new PdfPTable(5); // 5 columns
        table.setWidthPercentage(100);

        // Column headers
        table.addCell(new PdfPCell(new Paragraph("ID")));
        table.addCell(new PdfPCell(new Paragraph("Title")));
        table.addCell(new PdfPCell(new Paragraph("Host")));
        table.addCell(new PdfPCell(new Paragraph("Date")));
        table.addCell(new PdfPCell(new Paragraph("Location")));

        // Data rows
        for (Event event : events) {
            table.addCell(new PdfPCell(new Paragraph(event.getId().toString())));
            table.addCell(new PdfPCell(new Paragraph(event.getTitle())));
            table.addCell(new PdfPCell(new Paragraph(event.getHost().getFirstName()))); // Assuming User entity has a username field
            table.addCell(new PdfPCell(new Paragraph(event.getDate().toString())));
            table.addCell(new PdfPCell(new Paragraph(event.getLocation())));
        }

        document.add(table);

        document.close();
        return baos.toByteArray();
    }


}
