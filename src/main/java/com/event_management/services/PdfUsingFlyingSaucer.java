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
        String htmlContent = """
                <html>
                <head>
                <style>
                body { font-family: Arial, sans-serif; }
                h1 { color: #336699; }
                table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                th, td { border: 1px solid #dddddd; padding: 8px; text-align: center; word-wrap: break-word; word-break: break-all; white-space: pre-wrap; }
                form { margin-top: 20px; }
                input[type='text'], input[type='number'] { width: 200px; padding: 5px; margin-bottom: 10px; border: 1px solid #dddddd; }
                input[type='submit'] { padding: 5px 20px; background-color: #336699; color: white; border: none; cursor: pointer; }
                </style>
                <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ7755fHbC+MlbvqWfBff1UXYZd0OfiIcR1" crossorigin="anonymous" />
                </head>
                <body>
                <div class="container">
                <h1 class="text-primary">Event Report</h1>
                <table class="table table-bordered">
                <thead class="thead-light">
                <tr><th>ID</th><th>Title</th><th>Host</th><th>Date</th><th>Location</th></tr>
                </thead><tbody>
                """;

        for (Event event : events) {
            htmlContent += """
                    <tr>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    </tr>
                    """.formatted(event.getId(), event.getTitle(), event.getHost().getFirstName(), event.getDate(), event.getLocation());
        }

        htmlContent += """
                </tbody></table>
                <h1 class="text-primary">Registration Form</h1>
                <form>
                <div class="form-group">
                <label for='eventName'>Event Name:</label>
                <input type='text' class='form-control' id='eventName' name='eventName' />
                </div>
                <div class="form-group">
                <label for='eventDate'>Event Date:</label>
                <input type='text' class='form-control' id='eventDate' name='eventDate' />
                </div>
                <div class="form-group">
                <label for='eventLocation'>Event Location:</label>
                <input type='text' class='form-control' id='eventLocation' name='eventLocation' />
                </div>
                <div class="form-group">
                <label for='eventCapacity'>Event Capacity:</label>
                <input type='number' class='form-control' id='eventCapacity' name='eventCapacity' />
                </div>
                <button type='submit' class='btn btn-primary'>Submit</button>
                </form>
                </div></body></html>
                """;

        // Generate PDF from HTML using Flying Saucer and OpenPDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }
}
