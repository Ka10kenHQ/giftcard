package com.gitfcard.giftcard.dto;

import com.gitfcard.giftcard.entity.User;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
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
	
}
