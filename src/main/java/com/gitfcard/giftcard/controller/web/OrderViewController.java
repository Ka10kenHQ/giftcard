package com.gitfcard.giftcard.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.gitfcard.giftcard.dto.OrderResponseDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.service.JWTUtil;
import com.gitfcard.giftcard.service.OrderService;
import com.gitfcard.giftcard.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/orders")
public class OrderViewController {

    private final OrderService orderService;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public OrderViewController(OrderService orderService, UserService userService, JWTUtil jwtUtil) {
        this.orderService = orderService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{id}")
    public String viewOrderDetail(
            @PathVariable Long id,
            Model model,
            HttpServletRequest request
    ) throws Exception {
        // Extract user email from JWT token for authorization
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token == null || jwtUtil.extractUsernameFromToken(token) == null) {
            return "redirect:/login";
        }
        String email = jwtUtil.extractUsernameFromToken(token);
        UserResponceDTO user = userService.getUserByEmail(email);
        model.addAttribute("user", user);

        OrderResponseDTO order = orderService.getOrderById(id);

        if (!order.getUserId().equals(user.getId())) {
            return "error/403";
        }

        model.addAttribute("order", order);
        return "user/order-detail";
    }
}

