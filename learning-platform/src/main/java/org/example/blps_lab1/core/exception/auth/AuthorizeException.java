package org.example.blps_lab1.core.exception.auth;

public class AuthorizeException extends RuntimeException{

    public AuthorizeException(String messString){
        super(messString);
    }
    
}
