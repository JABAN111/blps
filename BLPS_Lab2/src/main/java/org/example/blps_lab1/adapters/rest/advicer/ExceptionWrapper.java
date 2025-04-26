package org.example.blps_lab1.adapters.rest.advicer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/*
 Класс обертка, чтобы пользовательские ошибки заворачивались в json файл с указанием времени ошибки и текста ошибки
 */
@Data
@Slf4j
public class ExceptionWrapper {

    private String message;
    private String time;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ExceptionWrapper(Exception e) {
        log.debug("Exception wrapper got error: ", e);
        message = e.getMessage();
        this.time = LocalDateTime.now().format(FORMATTER);
    }

    @Override
    public String toString() {
        return "{\"message\": \"" + message + "\", \"time\": \"" + time + "\"}";
    }

}
