package com.gitfcard.giftcard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponseDTO {

    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalPrice;
    private List<GiftCardItemDTO> giftCards;

    public OrderResponseDTO(Long orderId, Long userId, LocalDateTime orderDate, String status,
                            BigDecimal totalPrice, List<GiftCardItemDTO> giftCards) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.giftCards = giftCards;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public List<GiftCardItemDTO> getGiftCards() {
        return giftCards;
    }

    public static class GiftCardItemDTO {
        private UUID giftCardId;
        private String code;
        private BigDecimal balance;
        private boolean redeemed;
        private String currency;
        private LocalDateTime expirationDate;

        public GiftCardItemDTO(UUID giftCardId, String code, BigDecimal balance, boolean redeemed, String currency, LocalDateTime expirationDate) {
            this.giftCardId = giftCardId;
            this.code = code;
            this.balance = balance;
            this.redeemed = redeemed;
            this.currency = currency;
            this.expirationDate = expirationDate;
        }

        public UUID getGiftCardId() {
            return giftCardId;
        }

        public String getCode() {
            return code;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public boolean isRedeemed() {
            return redeemed;
        }

        public String getCurrency() {
            return currency;
        }

        public LocalDateTime getExpirationDate() {
            return expirationDate;
        }
    }
}

