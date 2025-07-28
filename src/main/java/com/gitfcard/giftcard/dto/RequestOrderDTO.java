package com.gitfcard.giftcard.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class RequestOrderDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "At least one gift card request is required")
    private List<GiftCardRequestDTO> giftCards;

    public static class GiftCardRequestDTO {
        @NotNull(message = "Card type ID is required")
        private Long cardTypeId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        public GiftCardRequestDTO() {}

        public GiftCardRequestDTO(Long cardTypeId, Integer quantity) {
            this.cardTypeId = cardTypeId;
            this.quantity = quantity;
        }

        public Long getCardTypeId() {
            return cardTypeId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setCardTypeId(Long cardTypeId) {
            this.cardTypeId = cardTypeId;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    public RequestOrderDTO() {}

    public RequestOrderDTO(Long userId, List<GiftCardRequestDTO> giftCards) {
        this.userId = userId;
        this.giftCards = giftCards;
    }

    public Long getUserId() {
        return userId;
    }

    public List<GiftCardRequestDTO> getGiftCards() {
        return giftCards;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setGiftCards(List<GiftCardRequestDTO> giftCards) {
        this.giftCards = giftCards;
    }
}

