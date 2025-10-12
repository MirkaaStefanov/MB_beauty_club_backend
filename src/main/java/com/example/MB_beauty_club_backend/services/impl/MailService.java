package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.models.entity.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.security.mail.admin}")
    private String adminEmail;

    public void sendLowStockReport(List<Product> lowStockProducts) throws MessagingException {
        if (lowStockProducts.isEmpty()) {
            return;
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        helper.setTo(adminEmail);
        helper.setSubject("⚠️ Ниска наличност на продукти");

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<h2 style='color:red;'>⚠️ Внимание: Ниски наличности!</h2>");
        htmlContent.append("<p>Следните продукти са с наличност под 10:</p>");
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

        helper.setText(htmlContent.toString(), true);
        mailSender.send(mimeMessage);
    }

//    public void sendDatabaseBackup(File backupFile) throws MessagingException {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        // Set 'multipart' to true because we are adding an attachment
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//
//        helper.setTo(adminEmail);
//        helper.setSubject("💾 Ежедневен бекъп на база данни: " + backupFile.getName());
//
//        String htmlContent = "<h2>Ежедневен бекъп на база данни</h2>" +
//                "<p>Прикаченият файл съдържа пълния SQL бекъп на базата данни.</p>" +
//                "<p>Дата: " + java.time.LocalDate.now() + "</p>";
//        helper.setText(htmlContent, true);
//
//        // Attach the file
//        FileSystemResource file = new FileSystemResource(backupFile);
//        helper.addAttachment(backupFile.getName(), file);
//
//        mailSender.send(mimeMessage);
//    }

    public void sendDatabaseBackup() throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // Set 'multipart' to true because we are adding an attachment
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(adminEmail);
        helper.setSubject("💾 Ежедневен бекъп на база данни: ");

        String htmlContent = "<h2>Ежедневен бекъп на база данни</h2>" +
                "<p>Прикаченият файл съдържа пълния SQL бекъп на базата данни.</p>" +
                "<p>Дата: " + java.time.LocalDate.now() + "</p>";
        helper.setText(htmlContent, true);


        mailSender.send(mimeMessage);
    }

}