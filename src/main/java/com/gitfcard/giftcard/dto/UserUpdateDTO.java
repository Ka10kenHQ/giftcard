package com.gitfcard.giftcard.dto;

import com.gitfcard.giftcard.entity.User;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public UserUpdateDTO() {}

    public UserUpdateDTO(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

}

