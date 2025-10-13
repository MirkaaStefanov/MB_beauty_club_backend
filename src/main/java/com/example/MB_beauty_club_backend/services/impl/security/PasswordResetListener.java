package com.example.MB_beauty_club_backend.services.impl.security;

import com.example.MB_beauty_club_backend.config.FrontendConfig;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.services.TokenService;
import com.example.MB_beauty_club_backend.services.impl.security.events.OnPasswordResetRequestEvent;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PasswordResetListener implements ApplicationListener<OnPasswordResetRequestEvent> {

    // Dependencies that are still needed
    private final FrontendConfig frontendConfig;
    private final TokenService tokenService;

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetListener.class);

    // Inject SendGrid properties from your application.yml
    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Override
    @Async
    public void onApplicationEvent(@NotNull OnPasswordResetRequestEvent event) {
        sendPasswordResetEmail(event);
    }

    protected void sendPasswordResetEmail(OnPasswordResetRequestEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        tokenService.createVerificationToken(user, token);

        // --- New SendGrid Logic ---
        Email from = new Email(fromEmail);
        Email to = new Email(user.getEmail());
        String subject = "Password Reset Request";

        String confirmationUrl = frontendConfig.getForgottenPasswordUrl() + "?token=" + token;
        String messageContent = "Dear " + user.getName() + ",\n\n"
                + "A password reset request has been initiated for your account.\n"
                + "Please click the following link to reset your password:\n"
                + confirmationUrl + "\n\n"
                + "If you did not request this change, please ignore this email.\n\n"
                + "Best regards,\n"
                + "The Sample Team!";

        Content content = new Content("text/plain", messageContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            logger.info(">>> ATTEMPTING TO SEND PASSWORD RESET EMAIL TO: {}", user.getEmail());
            Response response = sg.api(request);
            logger.info(">>> PASSWORD RESET EMAIL SENT SUCCESSFULLY. Status Code: {}", response.getStatusCode());

        } catch (IOException ex) {
            logger.error("!!! ERROR SENDING PASSWORD RESET EMAIL: {}", ex.getMessage());
        }
    }
}