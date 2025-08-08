package com.gitfcard.giftcard.dto;

public class UserMapper {

    public static UserUpdateDTO toUserUpdateDTO(UserResponceDTO user) {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}

