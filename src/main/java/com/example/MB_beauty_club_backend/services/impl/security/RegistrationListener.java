package com.example.MB_beauty_club_backend.services.impl.security;

import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.services.TokenService;
import com.example.MB_beauty_club_backend.services.impl.security.events.OnRegistrationCompleteEvent;
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
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final TokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationListener.class);

    // Inject SendGrid properties from your application.yml
    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Override
    @Async
    public void onApplicationEvent(@NotNull OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    protected void confirmRegistration(OnRegistrationCompleteEvent event) {
        // This part stays the same
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        tokenService.createVerificationToken(user, token);

        // --- New SendGrid Logic ---
        Email from = new Email(fromEmail);
        String subject = "The Sample Project Registration Confirmation";
        Email to = new Email(user.getEmail());

        String confirmationUrl = event.getAppUrl() + "/auth/registrationConfirm?token=" + token;
        String messageContent = "Dear, " + user.getName() + "\n\n"
                + "Thank you for registering with The Sample Project!\n\n"
                + "To complete your registration, please click the following link to verify your email:\n"
                + confirmationUrl + "\n\n"
                + "If you did not create an account with us, please ignore this email.\n"
                + "Best regards,\n"
                + "The Sample team!";

        Content content = new Content("text/plain", messageContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            logger.info(">>> ATTEMPTING TO SEND REGISTRATION EMAIL TO: {}", user.getEmail());
            Response response = sg.api(request);
            logger.info(">>> EMAIL SENT SUCCESSFULLY. Status Code: {}", response.getStatusCode());

        } catch (IOException ex) {
            logger.error("!!! ERROR SENDING EMAIL: {}", ex.getMessage());
        }
    }
}