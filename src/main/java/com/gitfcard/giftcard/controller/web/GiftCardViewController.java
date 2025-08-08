package com.gitfcard.giftcard.controller.web;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.gitfcard.giftcard.dto.GiftCardResponseDTO;
import com.gitfcard.giftcard.service.GiftCardService;

@Controller
@RequestMapping("/cards")
public class GiftCardViewController {

    private final GiftCardService giftCardService;

    public GiftCardViewController(GiftCardService giftCardService) {
        this.giftCardService = giftCardService;
    }

    @GetMapping("")
    public String listCards(Model model) {
        model.addAttribute("cards", giftCardService.getAllCards());
        return "cards/list";
    }

    @GetMapping("/{id}")
    public String cardDetails(@PathVariable UUID id, Model model) throws Exception {
        GiftCardResponseDTO card = giftCardService.getCardById(id);
        model.addAttribute("card", card);
        return "cards/detail";
    }

    @PostMapping("/{id}/redeem")
    public String redeemCard(@PathVariable UUID id, Model model) {
        String message;
        try {
            giftCardService.redeem(id);
            message = "Card redeemed successfully.";
        } catch (Exception e) {
            message = "Error redeeming card: " + e.getMessage();
        }
        model.addAttribute("message", message);

        // Reload card details after redeem attempt
        try {
            GiftCardResponseDTO card = giftCardService.getCardById(id);
            model.addAttribute("card", card);
        } catch (Exception e) {
            // ignore or redirect somewhere else
        }

        return "cards/detail";
    }
}

