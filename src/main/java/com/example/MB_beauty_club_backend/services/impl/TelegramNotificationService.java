package com.example.MB_beauty_club_backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    private String escapeMarkdownV2(String text) {
        // List of all 19 reserved characters in MarkdownV2
        return text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }

    @Async
    public void sendNewOrderNotification(String messageText) {
        try {
            // 1. (NEW) Escape the raw message for MarkdownV2
            String escapedMessage = escapeMarkdownV2(messageText);

            // 2. Build the URL template
            String urlTemplate = "https://api.telegram.org/bot" + botToken + "/sendMessage"
                    + "?chat_id=" + chatId
                    + "&text={text}"
                    + "&parse_mode={parse_mode}";

            // 3. Define the values for the placeholders
            String parseMode = "MarkdownV2";

            // 4. Send the HTTP GET request
            // We pass the new 'escapedMessage' as the variable for {text}
            restTemplate.getForObject(urlTemplate, String.class, escapedMessage, parseMode);

        } catch (Exception e) {
            // Log the error if the message fails to send.
            System.err.println("Error sending Telegram message: " + e.getMessage());
        }
    }


}
