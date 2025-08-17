package com.gitfcard.giftcard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gitfcard.giftcard.dto.GiftCardResponseDTO;
import com.gitfcard.giftcard.dto.RedeemCardResponseDTO;
import com.gitfcard.giftcard.entity.GiftCard;
import com.gitfcard.giftcard.entity.GiftCardType;
import com.gitfcard.giftcard.entity.Order;
import com.gitfcard.giftcard.repository.GiftCardRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class GiftCardServiceTest {

    @Mock
    private GiftCardRepository giftCardRepository;

    @InjectMocks
    private GiftCardService giftCardService;

    private GiftCard validGiftCard;
    private GiftCard expiredGiftCard;
    private GiftCard redeemedGiftCard;
    private GiftCard zeroBalanceGiftCard;
    private UUID giftCardId;
    private GiftCardType giftCardType;
    private Order order;

    @BeforeEach
    void setUp() {
        giftCardId = UUID.randomUUID();
        giftCardType = new GiftCardType();
        order = new Order();
        
        validGiftCard = new GiftCard("GIFT123", new BigDecimal("100.00"), 
                                   LocalDateTime.now().plusDays(30), giftCardType, order);
        validGiftCard.setId(giftCardId);
        validGiftCard.setCreationDate(LocalDateTime.now().minusDays(1));
        
        expiredGiftCard = new GiftCard("EXPIRED123", new BigDecimal("50.00"), 
                                     LocalDateTime.now().minusDays(1), giftCardType, order);
        expiredGiftCard.setId(UUID.randomUUID());
        expiredGiftCard.setCreationDate(LocalDateTime.now().minusDays(5));
        
        redeemedGiftCard = new GiftCard("REDEEMED123", new BigDecimal("75.00"), 
                                      LocalDateTime.now().plusDays(15), giftCardType, order);
        redeemedGiftCard.setId(UUID.randomUUID());
        redeemedGiftCard.setRedeemed(true);
        redeemedGiftCard.setCreationDate(LocalDateTime.now().minusDays(2));
        
        zeroBalanceGiftCard = new GiftCard("ZERO123", BigDecimal.ZERO, 
                                         LocalDateTime.now().plusDays(20), giftCardType, order);
        zeroBalanceGiftCard.setId(UUID.randomUUID());
        zeroBalanceGiftCard.setCreationDate(LocalDateTime.now().minusDays(3));
    }


    @Test
    void testRedeem_SuccessfulRedemption() {
        when(giftCardRepository.findById(giftCardId)).thenReturn(Optional.of(validGiftCard));
        when(giftCardRepository.save(any(GiftCard.class))).thenReturn(validGiftCard);

        RedeemCardResponseDTO result = giftCardService.redeem(giftCardId);

        assertNotNull(result);
        assertEquals(giftCardId, result.getCardId());
        assertEquals("Card successfully redeemed.", result.getMessage());
        assertTrue(result.isRedeemed());
        assertEquals(new BigDecimal("100.00"), result.getRemainingBalance());
        
        verify(giftCardRepository, times(1)).findById(giftCardId);
        verify(giftCardRepository, times(1)).save(validGiftCard);
        assertTrue(validGiftCard.isRedeemed());
    }

    @Test
    void testRedeem_CardNotFound_ThrowsEntityNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(giftCardRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> giftCardService.redeem(nonExistentId));
        
        assertEquals("GiftCard not found with id: " + nonExistentId, exception.getMessage());
        verify(giftCardRepository, times(1)).findById(nonExistentId);
        verify(giftCardRepository, never()).save(any());
    }

    @Test
    void testRedeem_AlreadyRedeemed_ThrowsIllegalStateException() {
        UUID redeemedCardId = redeemedGiftCard.getId();
        when(giftCardRepository.findById(redeemedCardId)).thenReturn(Optional.of(redeemedGiftCard));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> giftCardService.redeem(redeemedCardId));
        
        assertEquals("GiftCard is already redeemed", exception.getMessage());
        verify(giftCardRepository, times(1)).findById(redeemedCardId);
        verify(giftCardRepository, never()).save(any());
    }

    @Test
    void testRedeem_ExpiredCard_ThrowsIllegalStateException() {
        UUID expiredCardId = expiredGiftCard.getId();
        when(giftCardRepository.findById(expiredCardId)).thenReturn(Optional.of(expiredGiftCard));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> giftCardService.redeem(expiredCardId));
        
        assertEquals("GiftCard has expired", exception.getMessage());
        verify(giftCardRepository, times(1)).findById(expiredCardId);
        verify(giftCardRepository, never()).save(any());
    }

    @Test
    void testRedeem_ZeroBalance_ThrowsIllegalStateException() {
        UUID zeroBalanceCardId = zeroBalanceGiftCard.getId();
        when(giftCardRepository.findById(zeroBalanceCardId)).thenReturn(Optional.of(zeroBalanceGiftCard));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> giftCardService.redeem(zeroBalanceCardId));
        
        assertEquals("GiftCard balance is zero, cannot redeem", exception.getMessage());
        verify(giftCardRepository, times(1)).findById(zeroBalanceCardId);
        verify(giftCardRepository, never()).save(any());
    }


    @Test
    void testGetAllCards_Success() {
        List<GiftCard> giftCards = Arrays.asList(validGiftCard, expiredGiftCard, redeemedGiftCard);
        when(giftCardRepository.findAll()).thenReturn(giftCards);

        List<GiftCardResponseDTO> result = giftCardService.getAllCards();

        assertNotNull(result);
        assertEquals(3, result.size());
        
        GiftCardResponseDTO firstCard = result.get(0);
        assertEquals(validGiftCard.getId(), firstCard.getId());
        assertEquals(validGiftCard.getCode(), firstCard.getCode());
        assertEquals(validGiftCard.getBalance(), firstCard.getBalance());
        assertEquals(validGiftCard.getExpirationDate(), firstCard.getExpirationDate());
        assertEquals(validGiftCard.isRedeemed(), firstCard.isRedeemed());
        assertEquals(validGiftCard.getCreationDate(), firstCard.getCreationDate());
        
        verify(giftCardRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCards_EmptyList() {
        when(giftCardRepository.findAll()).thenReturn(Arrays.asList());

        List<GiftCardResponseDTO> result = giftCardService.getAllCards();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(giftCardRepository, times(1)).findAll();
    }


    @Test
    void testGetCardById_Success() throws Exception {
        when(giftCardRepository.findById(giftCardId)).thenReturn(Optional.of(validGiftCard));

        GiftCardResponseDTO result = giftCardService.getCardById(giftCardId);

        assertNotNull(result);
        assertEquals(validGiftCard.getId(), result.getId());
        assertEquals(validGiftCard.getCode(), result.getCode());
        assertEquals(validGiftCard.getBalance(), result.getBalance());
        assertEquals(validGiftCard.getExpirationDate(), result.getExpirationDate());
        assertEquals(validGiftCard.isRedeemed(), result.isRedeemed());
        assertEquals(validGiftCard.getCreationDate(), result.getCreationDate());
        
        verify(giftCardRepository, times(1)).findById(giftCardId);
    }

    @Test
    void testGetCardById_CardNotFound_ThrowsException() {
        UUID nonExistentId = UUID.randomUUID();
        when(giftCardRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, 
            () -> giftCardService.getCardById(nonExistentId));
        
        assertEquals("no user found with such id: " + nonExistentId, exception.getMessage());
        verify(giftCardRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void testGetCardById_NullId_ThrowsException() {
        when(giftCardRepository.findById(null)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, 
            () -> giftCardService.getCardById(null));
        
        assertEquals("no user found with such id: null", exception.getMessage());
        verify(giftCardRepository, times(1)).findById(null);
    }


    @Test
    void testRedeem_NegativeBalance_ThrowsIllegalStateException() {
        GiftCard negativeBalanceCard = new GiftCard("NEG123", new BigDecimal("-10.00"), 
                                                   LocalDateTime.now().plusDays(10), giftCardType, order);
        negativeBalanceCard.setId(UUID.randomUUID());
        
        when(giftCardRepository.findById(negativeBalanceCard.getId()))
            .thenReturn(Optional.of(negativeBalanceCard));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> giftCardService.redeem(negativeBalanceCard.getId()));
        
        assertEquals("GiftCard balance is zero, cannot redeem", exception.getMessage());
    }

    @Test
    void testRedeem_ExpirationDateExactlyNow() {
        GiftCard justExpiredCard = new GiftCard("JUST_EXPIRED", new BigDecimal("25.00"), 
                                              LocalDateTime.now().minusNanos(1), giftCardType, order);
        justExpiredCard.setId(UUID.randomUUID());
        
        when(giftCardRepository.findById(justExpiredCard.getId()))
            .thenReturn(Optional.of(justExpiredCard));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> giftCardService.redeem(justExpiredCard.getId()));
        
        assertEquals("GiftCard has expired", exception.getMessage());
    }
}
