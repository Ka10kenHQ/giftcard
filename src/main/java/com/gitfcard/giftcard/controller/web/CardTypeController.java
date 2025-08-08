package com.gitfcard.giftcard.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gitfcard.giftcard.service.CardTypeService;

import jakarta.validation.Valid;

@Controller("cardTypeWebController")
@RequestMapping("/admin/card-types")
public class CardTypeController {

    private final CardTypeService cardTypeService;

    public CardTypeController(CardTypeService cardTypeService) {
        this.cardTypeService = cardTypeService;
    }

    @GetMapping("")
    public String listCardTypes(Model model) {
        var cardTypes = cardTypeService.getAll();
        model.addAttribute("cardTypes", cardTypes);
        return "admin/card-type"; 
    }

    @PostMapping("/create")
    public String createCardType(@ModelAttribute("newCardType") @Valid GiftCardTypeForm form,
        BindingResult result,
        Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cardTypes", cardTypeService.getAll());
            return "admin/card-types";
        }
        cardTypeService.create(form.getName(), form.getCurrency());
        return "redirect:/admin/card-types";
    }

    @PostMapping("/delete/{id}")
    public String deleteCardType(@PathVariable Long id) {
        cardTypeService.delete(id);
        return "redirect:/admin/card-types";
    }

    public static class GiftCardTypeForm {
        @jakarta.validation.constraints.NotBlank
        private String name;

        @jakarta.validation.constraints.NotBlank
        private String currency;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

}

