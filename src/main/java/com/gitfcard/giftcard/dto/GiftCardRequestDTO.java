package com.gitfcard.giftcard.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GiftCardRequestDTO {

    @NotNull(message = "Card type ID is required")
    private Long cardTypeId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    public GiftCardRequestDTO() {}

    public GiftCardRequestDTO(Long cardTypeId, Integer quantity) {
        this.cardTypeId = cardTypeId;
        this.quantity = quantity;
    }

    public Long getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(Long cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

