package com.event_management.services;

import com.event_management.entities.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfUsingFlyingSaucer {

    private final EventService eventService;

    @Autowired
    public PdfUsingFlyingSaucer(EventService eventService) {
        this.eventService = eventService;
    }

    public byte[] generatePdf() throws Exception {
        List<Event> events = eventService.getAllEvents();

        // Create HTML content
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html lang=\"en\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\"/>");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
        htmlContent.append("<style>");
        htmlContent.append("body { font-family: Arial, sans-serif; }");
        htmlContent.append("h1 { color: #336699; }");
        htmlContent.append(".color { color: red; }");
        htmlContent.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        htmlContent.append("th, td { border: 1px solid #dddddd; padding: 8px; text-align: center; }");
        htmlContent.append("form { margin-top: 20px; }");
        htmlContent.append("input[type='text'], input[type='number'] { width: 200px; padding: 5px; margin-bottom: 10px; border: 1px solid #dddddd; }");
        htmlContent.append("input[type='submit'] { padding: 5px 20px; background-color: #336699; color: white; border: none; cursor: pointer; }");
        htmlContent.append("</style>");
        htmlContent.append("<link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css' integrity='sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ7755fHbC+MlbvqWfBff1UXYZd0OfiIcR1' crossorigin='anonymous' />");
        htmlContent.append("</head>");
        htmlContent.append("<body>");
        htmlContent.append("<div class='container'>");
        htmlContent.append("<h1 class='text-primary'>Event Report</h1>");
        htmlContent.append("<table class='table table-bordered'>");
        htmlContent.append("<thead class='thead-light'>");
        htmlContent.append("<tr><th>ID</th><th>Title</th><th>Host</th><th>Date</th><th>Location</th></tr>");
        htmlContent.append("</thead><tbody>");

        for (Event event : events) {
            htmlContent.append("<tr>");
            htmlContent.append("<td class=\"color\">").append(event.getId()).append("</td>");
            htmlContent.append("<td>").append(event.getTitle()).append("</td>");
            htmlContent.append("<td>").append(event.getHost().getFirstName()).append("</td>");
            htmlContent.append("<td>").append(event.getDate()).append("</td>");
            htmlContent.append("<td>").append(event.getLocation()).append("</td>");
            htmlContent.append("</tr>");
        }

        htmlContent.append("</tbody></table>");
        htmlContent.append("<h1 class='text-primary'>Registration Form</h1>");
        htmlContent.append("<form>");
        htmlContent.append("<div class='form-group'>");
        htmlContent.append("<label for='eventName'>Event Name:</label>");
        htmlContent.append("<input type='text' class='form-control' id='eventName' name='eventName' />");
        htmlContent.append("</div>");
        htmlContent.append("<div class='form-group'>");
        htmlContent.append("<label for='eventDate'>Event Date:</label>");
        htmlContent.append("<input type='text' class='form-control' id='eventDate' name='eventDate' />");
        htmlContent.append("</div>");
        htmlContent.append("<div class='form-group'>");
        htmlContent.append("<label for='eventLocation'>Event Location:</label>");
        htmlContent.append("<input type='text' class='form-control' id='eventLocation' name='eventLocation' />");
        htmlContent.append("</div>");
        htmlContent.append("<div class='form-group'>");
        htmlContent.append("<label for='eventCapacity'>Event Capacity:</label>");
        htmlContent.append("<input type='number' class='form-control' id='eventCapacity' name='eventCapacity' />");
        htmlContent.append("</div>");
        htmlContent.append("<button type='submit' class='btn btn-primary'>Submit</button>");
        htmlContent.append("</form>");
        htmlContent.append("</div></body></html>");

        // Generate PDF from HTML using Flying Saucer and OpenPDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent.toString());
        renderer.layout();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }
}
