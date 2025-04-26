package org.example.blps_lab1.core.exception.common;

//NOTE: Используется, если обязательное поле в запросе не было указаноы
public class FieldNotSpecifiedException extends RuntimeException{
    public FieldNotSpecifiedException(String message) {
        super(message);
    }
}
