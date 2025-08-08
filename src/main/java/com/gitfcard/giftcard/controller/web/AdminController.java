package com.gitfcard.giftcard.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard"; 
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        return "admin/orders"; 
    }
}

