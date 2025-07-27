package com.gitfcard.giftcard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class GiftCardResponseDTO {
    private UUID id;
    private String code;
    private BigDecimal balance;
    private LocalDateTime expirationDate;
    private boolean redeemed;
    private LocalDateTime creationDate;

    public GiftCardResponseDTO(UUID id, String code, BigDecimal balance, LocalDateTime expirationDate, boolean redeemed, LocalDateTime creationDate) {
        this.id = id;
        this.code = code;
        this.balance = balance;
        this.expirationDate = expirationDate;
        this.redeemed = redeemed;
        this.creationDate = creationDate;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public boolean isRedeemed() { return redeemed; }
    public LocalDateTime getCreationDate() { return creationDate; }
}

