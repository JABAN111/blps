package org.example.blps_lab1.common.advicer;

import org.example.blps_lab1.common.exceptions.FieldNotSpecifiedException;
import org.example.blps_lab1.common.exceptions.ObjectAlreadyExistException;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.common.exceptions.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorAdvicer {


    @ExceptionHandler({FieldNotSpecifiedException.class, IllegalArgumentException.class, 
        MailAuthenticationException.class, MailSendException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleFieldNotSpecifiedException(RuntimeException e) {
        return e.getMessage();
    }

    @ExceptionHandler({ObjectAlreadyExistException.class,
        ObjectNotExistException.class,
        ObjectNotFoundException.class
    })
    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
    public String handleObjectException(RuntimeException e) {
        return e.getMessage();
    }
   

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        return e.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException e) {
        return "Произошла ошибка на сервере";
    }    


}
