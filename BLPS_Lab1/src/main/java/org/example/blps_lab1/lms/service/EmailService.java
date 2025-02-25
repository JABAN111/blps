package org.example.blps_lab1.lms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendTermsOfStudy(String toEmail, String courseName, BigDecimal price){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper= new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("Поздравление с успешным формированием заявки");
            String htmlContent = "<html>" +
                    "<body>" +
                    "<h2>Добро пожаловать!</h2>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent,true);
            mailSender.send(message);
            log.info("Email send successfully on {}", toEmail);
        }catch (MessagingException e){
            log.error("Error while sending an email on {} {}", toEmail, e.getMessage());
            throw new RuntimeException("Ошибка при отправке email: " + e.getMessage(), e);
        }

    }
}
