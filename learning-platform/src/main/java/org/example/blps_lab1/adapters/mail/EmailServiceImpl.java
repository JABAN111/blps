package org.example.blps_lab1.adapters.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.domain.course.nw.NewCourse;
import org.example.blps_lab1.core.exception.mail.MailCreationException;
import org.example.blps_lab1.core.exception.mail.MailSendingException;
import org.example.blps_lab1.core.ports.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@Profile("stage")
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String senderEmail;

    public MimeMessageHelper createMimeMessageHelper(String toEmail, String subject) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            return helper;
        } catch (MessagingException e) {
            log.error("Error while creating email:  {}", e.getMessage(), e);
            throw new MailCreationException("Ошибка при создании сообщения: " + e.getMessage());
        }
    }

    public void informAboutNewCourses(String toEmail,
                                      String courseName,
                                      BigDecimal price,
                                      List<NewCourse> additionalCourses) {
        try {
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Запись на курсы");

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                    "<div style='max-width: 600px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #1FAEE9; text-align: center;'>Вы были записаны на курс " + courseName + "</h2>" +
                    "<p style='color: #555; font-size: 16px;'>Поздравляем вас с записью на курс " + courseName + ", вам необходимо оплатить данный курс.</p>" +
                    "<p style='color: #555; font-size: 16px;'>Стоимость данного курса составляет: " + price + "</p>";

            if (additionalCourses != null && !additionalCourses.isEmpty()) {
                StringBuilder courseList = new StringBuilder();
                for (var course : additionalCourses) {
                    courseList.append("<li>").append(course.getName()).append("</li>");
                }
                htmlContent += "<p style='color: #555; font-size: 16px;'>Также вы были записаны на дополнительные курсы:</p>" +
                        "<ul>" + courseList + "</ul>" +
                        "<p style='color: #555; font-size: 16px;'>Данные курсы вы можете увидеть на вашей основной странице с курсами.</p>";
            }

            htmlContent += "</div></body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("Error while sending an email on {} {}", toEmail, e.getMessage());
            throw new MailSendingException("Ошибка при отправке email о записи на курсы: " + e.getMessage());
        }
    }


    public void informAboutModuleCompletion(String toEmail, String courseName, String moduleName) {
        try {
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Успешное прохождение модуля");
            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                    "<div style='max-width: 600px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #1FAEE9; text-align: center;'>Поздравляем с успешным прохождение модуля " + moduleName + "</h2>" +
                    "<p style='color: #555; font-size: 16px;'>Вы полность прошли модуль " + moduleName + " из курса: " + courseName + " </p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("Error while sending an email on {} {}", toEmail, e.getMessage());
            throw new MailSendingException("Ошибка при отправке email о завершении модуля: " + e.getMessage());
        }
    }

    public void informAboutCompanyProblem(String toEmail, String companyName) {
        try {
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Проблема при регистрации");
            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" + "<div style='max-width: 600px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1);'>" +
                    "<div style='max-width: 600px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #1FAEE9; text-align: center;'> Компания с указанным вами именем " + companyName + " не зарегестрирована</h2>" +
                    "<p style='color: #555; font-size: 16px;'>Пожалуйста проверьте, что вы правильно указали название компании</p>" +
                    "<p style='color: #555; font-size: 16px;'>Если ошибка не будет решена, обратитесь в техподержку</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            helper.setText(htmlContent, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("Error while sending an email on {} {}", toEmail, e.getMessage());
            throw new MailSendingException("Ошибка при отправке email о проблемах при регистрации компании: " + e.getMessage());
        }
    }

    public void informAboutCourseCompletion(String toEmail, String courseName) {
        try {
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Успешное прохождение курса");
            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                    "<div style='max-width: 600px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #1FAEE9; text-align: center;'>Поздравляем с успешным прохождение курса " + courseName + "</h2>" +
                    "<p style='color: #555; font-size: 16px;'>Надеямся вам понравился созданный нашей командой курс и ждём вас на новых занятиях</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("Error while sending an email on {} {}", toEmail, e.getMessage());
            throw new MailSendingException("Ошибка при отправке email о успешном прохождении курса: " + e.getMessage());
        }
    }


    public void rejectionMail(String toEmail, String courseName) {
        try {
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Отказ от курса");
            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                    "<div style='max-width: 600px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #1FAEE9; text-align: center;'>Вы отказались от записи на курс " + courseName + "</h2>" +
                    "<p style='color: #555; font-size: 16px;'>Если передумаете вы знаете, где нас найти</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            helper.setText(htmlContent, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("Error while sending email on {} {}", toEmail, e.getMessage());
            throw new MailSendingException("Ошибка при отправке email о успешном отказе");
        }
    }

    public void informMinioFailure(String toEmail) {
        try {
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Проблема при отправлении сертификата");
            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                    "<div style='max-width: 600px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #1FAEE9; text-align: center;'>Не получилось отправить сертификат вам на почту</h2>" +
                    "<p style='color: #555; font-size: 16px;'>К сожалению при формировании сертификата о прохождении курса возникла ошибка</p>" +
                    "<p style='color: #555; font-size: 16px;'>Мы уже занимаемся решением этой проблемы, а вас просим подождать</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            helper.setText(htmlContent, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("Error while sending email on {} {}", toEmail, e.getMessage());
            throw new MailSendingException("Ошибка при отправке email о успешном отказе");
        }
    }

    public void sendCertificateToUser(String toEmail, File file) {
        try {
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Ваш сертификат о прохождении курса");
            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                    "<div style='max-width: 600px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #1FAEE9; text-align: center;'>Поздравляем с успешным прохождением курса</h2>" +
                    "<p style='color: #555; font-size: 16px;'>Сертификат о прохождении курса прикреплен в этом письме</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            helper.setText(htmlContent, true);

            FileSystemResource fileResource = new FileSystemResource(file);
            helper.addAttachment("Сертификат.pdf", fileResource);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("Error while sending email on {} {}", toEmail, e.getMessage());
//            throw new MailSendingException("Ошибка при отправке email о успешном отказе");
        }
    }
}
