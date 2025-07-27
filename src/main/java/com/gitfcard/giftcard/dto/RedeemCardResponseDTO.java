package com.gitfcard.giftcard.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class RedeemCardResponseDTO {
    private UUID cardId;
    private String message;
    private boolean redeemed;
    private BigDecimal remainingBalance;

    public RedeemCardResponseDTO(UUID cardId, String message, boolean redeemed, BigDecimal remainingBalance) {
        this.cardId = cardId;
        this.message = message;
        this.redeemed = redeemed;
        this.remainingBalance = remainingBalance;
    }

    public UUID getCardId() {
        return cardId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }
}

