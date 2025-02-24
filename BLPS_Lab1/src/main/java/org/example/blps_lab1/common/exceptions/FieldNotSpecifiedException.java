package org.example.blps_lab1.common.exceptions;

//NOTE: Используется, если обязательное поле в запросе не было указаноы
public class FieldNotSpecifiedException extends RuntimeException{
    public FieldNotSpecifiedException(String message) {
        super(message);
    }
}
