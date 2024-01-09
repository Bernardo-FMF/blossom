package org.blossom.auth.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.blossom.auth.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class EmailService {
    @Autowired
    JavaMailSender mailSender;

    @Autowired
    ThymeleafService thymeleafService;

    @Value("${spring.mail.username}")
    private String email;

    @Value("${vendor.frontend.callback-url}")
    private String callbackUrl;

    public void sendPasswordRecoveryEmail(UserDto userDto) {
        try {
            sendEmail(userDto, "password-recovery.html", "Blossom: Password Recovery");
        } catch (Exception ex) {
            log.error("Error sending recovery email to user " + userDto.getId(), ex);
        }
    }

    public void sendVerificationEmail(UserDto userDto) {
        try {
            sendEmail(userDto, "email-verification.html", "Blossom: Email Verification");
        } catch (Exception ex) {
            log.error("Error sending verification email to user " + userDto.getId(), ex);
        }
    }

    private void sendEmail(UserDto userDto, String template, String subject) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(userDto.getEmail());
        helper.setFrom(email);

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", userDto.getUsername());
        variables.put("expiration-date", userDto.getExpirationDate());
        variables.put("callbackUrl", buildCallbackUrl(userDto.getToken(), userDto.getId()));

        helper.setText(thymeleafService.createContent(template, variables), true);
        helper.setSubject(subject);
        mailSender.send(message);
    }

    private String buildCallbackUrl(String token, int id) {
        return String.format(callbackUrl + "?token=%s&userId=%s", token, id);
    }
}
