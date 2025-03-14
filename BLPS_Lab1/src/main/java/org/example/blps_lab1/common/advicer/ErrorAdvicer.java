package org.example.blps_lab1.common.advicer;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.exception.AuthorizeException;
import org.example.blps_lab1.common.exceptions.ExceptionWrapper;
import org.example.blps_lab1.common.exceptions.FieldNotSpecifiedException;
import org.example.blps_lab1.common.exceptions.ObjectAlreadyExistException;
import org.example.blps_lab1.common.exceptions.ObjectNotExistException;
import org.example.blps_lab1.common.exceptions.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorAdvicer {
    @ExceptionHandler({FieldNotSpecifiedException.class, IllegalArgumentException.class, 
        MailAuthenticationException.class, MailSendException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionWrapper handleFieldNotSpecifiedException(RuntimeException e) {
        return new ExceptionWrapper(e);
    }

    @ExceptionHandler({ObjectAlreadyExistException.class,
        ObjectNotExistException.class,
        ObjectNotFoundException.class
    })
    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
    public ExceptionWrapper handleObjectException(RuntimeException e) {
        return new ExceptionWrapper(e);
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler({AuthorizeException.class})
    public ExceptionWrapper AuthorizeException(RuntimeException e){
        return new ExceptionWrapper(e);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionWrapper handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ExceptionWrapper(new Exception("Произошла внутренняя ошибка сервера"));
    }   
    
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionWrapper UsernameNotFoundException(UsernameNotFoundException e){
        return new ExceptionWrapper(e);
    }


}
