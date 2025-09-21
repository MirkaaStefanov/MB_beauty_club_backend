package com.example.MB_beauty_club_backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendLowStockAlert(String to, String productName, int availableQuantity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("⚠️ Ниско количество: " + productName);
        message.setText("Продуктът '" + productName + "' има ниско количество.\n" +
                "Налично: " + availableQuantity + "\n\n" +
                "Моля подобрете наличността.");

        mailSender.send(message);
    }
}