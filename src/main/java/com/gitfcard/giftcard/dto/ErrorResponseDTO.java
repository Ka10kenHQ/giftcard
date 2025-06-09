package com.gitfcard.giftcard.dto;

public class ErrorResponseDTO {
    private String message;
    private String error;
    private int status;
    
    public ErrorResponseDTO(String message, String error, int status) {
        this.message = message;
        this.error = error;
        this.status = status;
    }
    
    public ErrorResponseDTO(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
} 