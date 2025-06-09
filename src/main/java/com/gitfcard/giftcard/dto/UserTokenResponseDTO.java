package com.gitfcard.giftcard.dto;


public class UserTokenResponseDTO {
    private String token;
    
    public UserTokenResponseDTO(String token){
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}
