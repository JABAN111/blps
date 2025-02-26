package org.example.blps_lab1.lms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public MimeMessageHelper createMimeMessageHelper(String toEmail, String subject){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            return helper;
        }catch (MessagingException e){
            log.error("Error while creating email:  {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при создании сообщения: " + e.getMessage());
        }
    }

    /*public void sendMail(MimeMessageHelper helper){
        try{
            mailSender.send(helper.getMimeMessage());
            log.info("Email отправлен успешно");
        }catch (MessagingException e){
            log.error("Ошибка при отправке email: {}", e.getMessage());
            throw new RuntimeException("Ошибка приотпраке email: " + e.getMessage(), e);
        }
    }*/

    public void sendTermsOfStudy(String toEmail, String courseName, BigDecimal price){
        try {
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Поздравление с успешным формированием заявки");

            String htmlContent = "<html>" +
                    "<body>" +
                    "<h2>Добро пожаловать!</h2>" +
                    "<p>Вам необходимо оплатить данный курс" + courseName+ " </p>" +
                    "<p>Стоимость данного курса составляет: " + price + "руб </p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent,true);
            mailSender.send(helper.getMimeMessage());
            log.info("Email send successfully on {}", toEmail);
        }catch (MessagingException e){
            log.error("Error while sending an email on {} {}", toEmail, e.getMessage());
            throw new RuntimeException("Ошибка при отправке email: " + e.getMessage(), e);
        }

    }

    public void informAboutNewCourses(String toEmail, List<Course> additionalCourses){
        try{
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Запись на курсы");
            StringBuilder courseList = new StringBuilder();
            for(Course course : additionalCourses){
                courseList.append("<li>").append(course.getCourseName()).append("</li>");
            }
            String htmlContent = "<html>" +
                    "<body>" +
                    "<h2>Вы были записаны на дополнительные курсы, связанные с курсом </h2>"+
                    "<p>Вот представленный список курсов:</p>"+
                    "<ul>" + courseList + "</ul>" +
                    "<p>Данные курсы вы можете увидеть на вашей основной странице с курсами</p>"+
                    "</body>"+
                    "</html>";

            helper.setText(htmlContent);
            mailSender.send(helper.getMimeMessage());
        }catch (MessagingException e){
            log.error("Error while sending an email on {} {}", toEmail, e.getMessage());
            throw new RuntimeException("Ошибка при отправке email: " + e.getMessage(), e);
        }
    }

    public void informAboutModuleCompletion(String toEmail, String courseName, String moduleName){
        try{
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Успешное прохождение модуля");
            String htmlContent = "<html>" +
                    "<body>" +
                    "<h2>Поздравляем с успешным прохождение модуля " + moduleName + "</h2>" +
                    "<p>Вы полность прошли модкль" + moduleName + "из курса " + courseName+ " </p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent);
            mailSender.send(helper.getMimeMessage());
        }catch (MessagingException e){
            log.error("Error while sending an email on {} {}", toEmail, e.getMessage());
            throw new RuntimeException("Ошибка при отправке email: " + e.getMessage(), e);
        }
    }

    public void informAboutCompanyProblem(String toEmail, String companyName){
        try{
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Проблема при регистрации");
            String htmlContent = "<html>" +
                    "<body>" +
                    "<h2> Компания с указанным вами именем "+companyName+" не зарегестрирована</h2>" +
                    "<p>Пожалуйста проверьте, что вы правильно указали название компании</p>" +
                    "<p>Если ошибка не будет решена, обратитесь в техподержку</p>"+
                    "</body>" +
                    "</html>";
            helper.setText(htmlContent);
            mailSender.send(helper.getMimeMessage());
        }catch (MessagingException e){
            log.error("Error while sending an email on {} {}", toEmail, e.getMessage());
            throw new RuntimeException("Ошибка при отправке email: " + e.getMessage(), e);
        }
    }

    public void informAboutCourseCompletion(String toEmail, String courseName){
        try{
            MimeMessageHelper helper = createMimeMessageHelper(toEmail, "Успешное прохождение курса");
            String htmlContent = "<html>" +
                    "<body>" +
                    "<h2>Поздравляем с успешным прохождение курса " + courseName + "</h2>" +
                    "<p>Надеямся вам понравился созданный нашей командой курс и ждём вас на новых занятиях</p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent);
            mailSender.send(helper.getMimeMessage());
        }catch (MessagingException e){
            log.error("Error while sending an email on {} {}", toEmail, e.getMessage());
            throw new RuntimeException("Ошибка при отправке email: " + e.getMessage(), e);
        }
    }
}
