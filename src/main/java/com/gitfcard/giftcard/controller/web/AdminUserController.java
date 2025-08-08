package com.gitfcard.giftcard.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.gitfcard.giftcard.dto.UserRequestDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.dto.UserUpdateDTO;
import com.gitfcard.giftcard.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String listUsers(Model model) {
        List<UserResponceDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/list";
    }

    @GetMapping("/{id}")
    public String getUserDetail(@PathVariable Long id, Model model) throws Exception {
        UserResponceDTO user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/detail";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("userRequestDTO", new UserRequestDTO());
        return "admin/create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("userRequestDTO") @Valid UserRequestDTO userRequestDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/create";
        }
        userService.save(userRequestDTO);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) throws Exception {
        UserResponceDTO user = userService.getUserById(id);
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();

        userUpdateDTO.setFirstName(user.getFirstName());
        userUpdateDTO.setLastName(user.getLastName());
        userUpdateDTO.setEmail(user.getEmail());

        model.addAttribute("userUpdateDTO", userUpdateDTO);
        return "admin/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(
        @PathVariable Long id,
        @ModelAttribute("userUpdateDTO") @Valid UserUpdateDTO userUpdateDTO,
        BindingResult result,
        Model model
    ) throws Exception {
        if (result.hasErrors()) {
            return "admin/edit";
        }

        userService.update(userUpdateDTO, id);
        return "redirect:/admin/users";
    }
}

