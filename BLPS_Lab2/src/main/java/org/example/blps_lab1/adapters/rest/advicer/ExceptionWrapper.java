package org.example.blps_lab1.adapters.rest.advicer;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/*
 Класс обертка, чтобы пользовательские ошибки заворачивались в json файл с указанием времени ошибки и текста ошибки
 */ 
@Data
@Slf4j
public class ExceptionWrapper {

    private String message;
    private LocalDateTime time;

    public ExceptionWrapper(Exception e){
        log.debug("Exception wrapper got error: ", e);
        message = e.getMessage();
        time = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "{\"message\": \"" + message + "\", \"time\": " + time + "}";
    }
    
}
