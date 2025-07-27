package com.gitfcard.giftcard.dto;

public class GiftCardTypeResponseDTO {

    private Long id;
    private String name;
    private String currency;

    public GiftCardTypeResponseDTO(Long id, String name, String currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }
}

