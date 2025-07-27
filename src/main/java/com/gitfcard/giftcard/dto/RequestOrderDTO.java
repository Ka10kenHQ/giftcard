package com.gitfcard.giftcard.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class RequestOrderDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "At least one order item is required")
    private List<OrderItemDTO> orderItems;

    public static class OrderItemDTO {
        @NotNull(message = "Card type ID is required")
        private Long cardTypeId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        public OrderItemDTO() {}

        public OrderItemDTO(Long cardTypeId, Integer quantity) {
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

    public RequestOrderDTO(Long userId, List<OrderItemDTO> orderItems) {
        this.userId = userId;
        this.orderItems = orderItems;
    }

    public Long getUserId() {
        return userId;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}

