package org.blossom.auth.email;

import jakarta.mail.internet.MimeMessage;
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

            helper.setText(thymeleafService.createContent("password-recovery.html", variables), true);
            helper.setSubject("Blossom: Password Recovery");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendVerificationEmail(UserDto dto) {

    }

    private Object buildCallbackUrl(String token, int id) {
        return String.format(callbackUrl + "?token=%s&userId=%s", token, id);
    }
}
