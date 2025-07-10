package com.gitfcard.giftcard.dto;

import java.math.BigDecimal;

public class GiftRedeemRequestDTO {

    private String code;

    private String userId;

    private BigDecimal amount;

    public GiftRedeemRequestDTO(String code, String userId, BigDecimal amount){
        this.code = code;
        this.userId = userId;
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


}
