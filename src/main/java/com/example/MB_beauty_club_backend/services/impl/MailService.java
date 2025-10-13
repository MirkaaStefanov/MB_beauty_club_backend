package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.models.entity.Product;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    // Inject SendGrid properties from your application.yml
    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    // We'll update this property name in application.yml
    @Value("${sendgrid.admin-email}")
    private String adminEmail;

    public void sendLowStockReport(List<Product> lowStockProducts) {
        if (lowStockProducts == null || lowStockProducts.isEmpty()) {
            return;
        }

        // The HTML building logic remains exactly the same
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<h2 style='color:red;'>⚠️ Внимание: Ниски наличности!</h2>");
        // ... (rest of your HTML string building)
        htmlContent.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse: collapse; width:100%; font-family: sans-serif;'>");
        htmlContent.append("<tr style='background:#f2f2f2;'>")
                .append("<th style='padding: 8px;'>Снимка</th>")
                .append("<th style='padding: 8px;'>Име</th>")
                .append("<th style='padding: 8px;'>Баркод</th>")
                .append("<th style='padding: 8px;'>Наличност</th>")
                .append("</tr>");

        for (Product product : lowStockProducts) {
            htmlContent.append("<tr>")
                    .append("<td style='text-align:center; padding: 8px;'>");

            if (product.getImageData() != null && product.getImageData().length > 0) {
                String base64Image = Base64.getEncoder().encodeToString(product.getImageData());
                htmlContent.append("<img src='data:image/jpeg;base64,")
                        .append(base64Image)
                        .append("' width='80' style='max-width:80px; max-height:80px;'/>");
            } else {
                htmlContent.append("—");
            }

            htmlContent.append("</td>")
                    .append("<td style='padding: 8px;'>").append(product.getName()).append("</td>")
                    .append("<td style='padding: 8px;'>").append(product.getBarcode() != null ? product.getBarcode() : "—").append("</td>")
                    .append("<td style='color:red; font-weight:bold; text-align:center; padding: 8px;'>")
                    .append(product.getAvailableQuantity())
                    .append("</td>")
                    .append("</tr>");
        }
        htmlContent.append("</table>");

        // --- New SendGrid Logic ---
        Email from = new Email(fromEmail);
        Email to = new Email(adminEmail);
        String subject = "⚠️ Ниска наличност на продукти";
        // Use "text/html" for the content type
        Content content = new Content("text/html", htmlContent.toString());
        Mail mail = new Mail(from, subject, to, content);

        sendEmailWithSendGrid(mail);
    }

    public void sendDatabaseBackup(File backupFile) {
        if (backupFile == null || !backupFile.exists()) {
            logger.error("Database backup file does not exist. Cannot send email.");
            return;
        }

        Email from = new Email(fromEmail);
        Email to = new Email(adminEmail);
        String subject = "💾 Ежедневен бекъп на база данни: " + backupFile.getName();
        String htmlContent = "<h2>Ежедневен бекъп на база данни</h2>" +
                "<p>Прикаченият файл съдържа пълния SQL бекъп на базата данни.</p>" +
                "<p>Дата: " + java.time.LocalDate.now() + "</p>";
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        // --- Logic for adding the file attachment ---
        try {
            byte[] fileData = Files.readAllBytes(backupFile.toPath());
            String base64Content = Base64.getEncoder().encodeToString(fileData);

            Attachments attachments = new Attachments();
            attachments.setContent(base64Content);
            attachments.setFilename(backupFile.getName());
            attachments.setType("application/octet-stream"); // A generic type for any file
            attachments.setDisposition("attachment");
            mail.addAttachments(attachments);

        } catch (IOException e) {
            logger.error("!!! ERROR reading backup file for attachment: {}", e.getMessage());
            return; // Don't send the email if the file can't be attached
        }

        sendEmailWithSendGrid(mail);
    }

    // Helper method to avoid repeating the send logic
    private void sendEmailWithSendGrid(Mail mail) {
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            logger.info(">>> ATTEMPTING TO SEND EMAIL to: {}", mail.getPersonalization().get(0).getTos().get(0).getEmail());
            Response response = sg.api(request);
            logger.info(">>> EMAIL SENT SUCCESSFULLY. Status Code: {}", response.getStatusCode());

        } catch (IOException ex) {
            logger.error("!!! ERROR SENDING EMAIL: {}", ex.getMessage());
        }
    }
}