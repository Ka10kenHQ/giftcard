package com.gitfcard.giftcard.controller.web;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.gitfcard.giftcard.dto.OrderResponseDTO;
import com.gitfcard.giftcard.dto.UserUpdateDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.service.JWTUtil;
import com.gitfcard.giftcard.service.OrderService;
import com.gitfcard.giftcard.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/users/me")
public class UserViewController {

    private final UserService userService;
    private final OrderService orderService;
    private final JWTUtil jwtUtil;

    public UserViewController(UserService userService, OrderService orderService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

	@GetMapping("")
	public String userProfile(Model model, HttpServletRequest request) throws Exception {
		String token = jwtUtil.extractTokenFromRequest(request);
		if (token == null || jwtUtil.extractUsernameFromToken(token) == null) {
			return "redirect:/login";
		}

		String email = jwtUtil.extractUsernameFromToken(token);
		UserResponceDTO user = userService.getUserByEmail(email);

		if (jwtUtil.isAdmin(token)) {
			model.addAttribute("user", user);
			return "admin/profile"; 
		}

		List<OrderResponseDTO> orders = orderService.getOrderByUserEmail(email);
		model.addAttribute("user", user);
		model.addAttribute("orders", orders);
		return "user/profile";
	}

    @GetMapping("/edit")
    public String showEditForm(Model model, HttpServletRequest request) throws Exception {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token == null || jwtUtil.extractUsernameFromToken(token) == null) {
            return "redirect:/login";
        }
        String email = jwtUtil.extractUsernameFromToken(token);
        UserUpdateDTO dto = userService.getUserUpdateDTOByEmail(email);
        model.addAttribute("userUpdateDTO", dto);
        return "user/edit";
    }

    @PostMapping("/edit")
    public String updateCurrentUser(
        @ModelAttribute("userUpdateDTO") @Valid UserUpdateDTO userUpdateDTO,
        BindingResult result,
        HttpServletRequest request
    ) throws Exception {
        if (result.hasErrors()) {
            return "user/edit";
        }

        String token = jwtUtil.extractTokenFromRequest(request);
        String email = jwtUtil.extractUsernameFromToken(token);
        UserResponceDTO user = userService.getUserByEmail(email);

        userService.update(userUpdateDTO, user.getId());

        return "redirect:/users/me";
    }

	@GetMapping("/orders/view")
	public String viewCurrentUserOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		String email = userDetails.getUsername();
		List<OrderResponseDTO> orders = orderService.getOrderByUserEmail(email);
		model.addAttribute("orders", orders);
		return "user/orders"; // Thymeleaf template name: orders.html
	}

}

