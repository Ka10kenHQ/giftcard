package com.gitfcard.giftcard.dto;

import com.gitfcard.giftcard.entity.User;

public class UserResponceDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public UserResponceDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }
    public UserResponceDTO() {
        // Default constructor
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
       return this.lastName;
    }

    public String getEmail(){
        return this.email;
    }
    public Long getId(){
        return this.id;
    }
}
