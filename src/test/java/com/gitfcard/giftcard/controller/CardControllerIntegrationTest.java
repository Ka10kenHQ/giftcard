package com.gitfcard.giftcard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gitfcard.giftcard.config.TestSecurityConfig;
import com.gitfcard.giftcard.dto.GiftCardResponseDTO;
import com.gitfcard.giftcard.dto.RedeemCardResponseDTO;
import com.gitfcard.giftcard.service.GiftCardService;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class CardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GiftCardService giftCardService;

    private GiftCardResponseDTO giftCard1;
    private GiftCardResponseDTO giftCard2;
    private RedeemCardResponseDTO redeemResponse;
    private UUID giftCardId;
    private UUID nonExistentId;

    @BeforeEach
    void setUp() {
        giftCardId = UUID.randomUUID();
        nonExistentId = UUID.randomUUID();
        
        giftCard1 = new GiftCardResponseDTO(
            giftCardId,
            "GIFT123",
            new BigDecimal("100.00"),
            LocalDateTime.now().plusDays(30),
            false,
            LocalDateTime.now().minusDays(1)
        );
        
        giftCard2 = new GiftCardResponseDTO(
            UUID.randomUUID(),
            "GIFT456", 
            new BigDecimal("50.00"),
            LocalDateTime.now().plusDays(15),
            true,
            LocalDateTime.now().minusDays(5)
        );
        
        redeemResponse = new RedeemCardResponseDTO(
            giftCardId,
            "Card successfully redeemed.",
            true,
            new BigDecimal("100.00")
        );
    }


    @Test
    void testGetAllCards_Success() throws Exception {
        List<GiftCardResponseDTO> giftCards = Arrays.asList(giftCard1, giftCard2);
        when(giftCardService.getAllCards()).thenReturn(giftCards);

        mockMvc.perform(get("/api/cards")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(giftCardId.toString()))
                .andExpect(jsonPath("$[0].code").value("GIFT123"))
                .andExpect(jsonPath("$[0].balance").value(100.00))
                .andExpect(jsonPath("$[0].redeemed").value(false))
                .andExpect(jsonPath("$[1].code").value("GIFT456"))
                .andExpect(jsonPath("$[1].balance").value(50.00))
                .andExpect(jsonPath("$[1].redeemed").value(true));
    }

    @Test
    void testGetAllCards_EmptyList_ReturnsEmptyArray() throws Exception {
        when(giftCardService.getAllCards()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/cards")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAllCards_ServiceException_ReturnsInternalServerError() throws Exception {
        when(giftCardService.getAllCards()).thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/cards")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void testGetCardById_Success() throws Exception {
        when(giftCardService.getCardById(giftCardId)).thenReturn(giftCard1);

        mockMvc.perform(get("/api/cards/{id}", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(giftCardId.toString()))
                .andExpect(jsonPath("$.code").value("GIFT123"))
                .andExpect(jsonPath("$.balance").value(100.00))
                .andExpect(jsonPath("$.redeemed").value(false))
                .andExpect(jsonPath("$.expirationDate").exists())
                .andExpect(jsonPath("$.creationDate").exists());
    }

    @Test
    void testGetCardById_CardNotFound_ReturnsInternalServerError() throws Exception {
        when(giftCardService.getCardById(nonExistentId))
            .thenThrow(new Exception("no user found with such id: " + nonExistentId));

        mockMvc.perform(get("/api/cards/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetCardById_InvalidUUIDFormat_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/cards/{id}", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCardById_ServiceException_ReturnsInternalServerError() throws Exception {
        when(giftCardService.getCardById(giftCardId))
            .thenThrow(new RuntimeException("Unexpected error occurred"));

        mockMvc.perform(get("/api/cards/{id}", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void testRedeemCard_Success() throws Exception {
        when(giftCardService.redeem(giftCardId)).thenReturn(redeemResponse);

        mockMvc.perform(post("/api/cards/{id}/redeem", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cardId").value(giftCardId.toString()))
                .andExpect(jsonPath("$.message").value("Card successfully redeemed."))
                .andExpect(jsonPath("$.redeemed").value(true))
                .andExpect(jsonPath("$.remainingBalance").value(100.00));
    }

    @Test
    void testRedeemCard_CardNotFound_ReturnsInternalServerError() throws Exception {
        when(giftCardService.redeem(nonExistentId))
            .thenThrow(new EntityNotFoundException("GiftCard not found with id: " + nonExistentId));

        mockMvc.perform(post("/api/cards/{id}/redeem", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRedeemCard_AlreadyRedeemed_ReturnsInternalServerError() throws Exception {
        when(giftCardService.redeem(giftCardId))
            .thenThrow(new IllegalStateException("GiftCard is already redeemed"));

        mockMvc.perform(post("/api/cards/{id}/redeem", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRedeemCard_ExpiredCard_ReturnsInternalServerError() throws Exception {
        when(giftCardService.redeem(giftCardId))
            .thenThrow(new IllegalStateException("GiftCard has expired"));

        mockMvc.perform(post("/api/cards/{id}/redeem", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRedeemCard_ZeroBalance_ReturnsInternalServerError() throws Exception {
        when(giftCardService.redeem(giftCardId))
            .thenThrow(new IllegalStateException("GiftCard balance is zero, cannot redeem"));

        mockMvc.perform(post("/api/cards/{id}/redeem", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRedeemCard_InvalidUUIDFormat_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/cards/{id}/redeem", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRedeemCard_ServiceException_ReturnsInternalServerError() throws Exception {
        when(giftCardService.redeem(giftCardId))
            .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/api/cards/{id}/redeem", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void testGetAllCards_InvalidHttpMethod_ReturnsMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/api/cards")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testGetCardById_InvalidHttpMethod_ReturnsMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/api/cards/{id}", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testRedeemCard_InvalidHttpMethod_ReturnsMethodNotAllowed() throws Exception {
        mockMvc.perform(get("/api/cards/{id}/redeem", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }


    @Test
    void testGetAllCards_WithoutContentType_Success() throws Exception {
        List<GiftCardResponseDTO> giftCards = Arrays.asList(giftCard1);
        when(giftCardService.getAllCards()).thenReturn(giftCards);

        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testRedeemCard_WithAcceptHeader_Success() throws Exception {
        when(giftCardService.redeem(giftCardId)).thenReturn(redeemResponse);

        mockMvc.perform(post("/api/cards/{id}/redeem", giftCardId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cardId").value(giftCardId.toString()));
    }


    @Test
    void testGetCardById_NullReturnFromService_ReturnsOkWithNoBody() throws Exception {
        when(giftCardService.getCardById(giftCardId)).thenReturn(null);

        mockMvc.perform(get("/api/cards/{id}", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testRedeemCard_NullReturnFromService_ReturnsOkWithNoBody() throws Exception {
        when(giftCardService.redeem(giftCardId)).thenReturn(null);

        mockMvc.perform(post("/api/cards/{id}/redeem", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void testInvalidEndpoint_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/cards/invalid/endpoint")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCardEndpointWithTrailingSlash_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/cards/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void testRedeemCard_ConcurrentRequest_FirstSucceedsSecondFails() throws Exception {
        when(giftCardService.redeem(giftCardId))
            .thenReturn(redeemResponse)
            .thenThrow(new IllegalStateException("GiftCard is already redeemed"));

        mockMvc.perform(post("/api/cards/{id}/redeem", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cards/{id}/redeem", giftCardId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
