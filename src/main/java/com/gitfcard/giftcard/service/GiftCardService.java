package com.gitfcard.giftcard.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gitfcard.giftcard.dto.GiftCardResponseDTO;
import com.gitfcard.giftcard.dto.RedeemCardResponseDTO;
import com.gitfcard.giftcard.entity.GiftCard;
import com.gitfcard.giftcard.repository.GiftCardRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class GiftCardService {
	private GiftCardRepository giftCardRepository;

	public GiftCardService(GiftCardRepository giftCardRepository){
		this.giftCardRepository = giftCardRepository;
	}

	public RedeemCardResponseDTO redeem(UUID id) {
		GiftCard giftCard = giftCardRepository.findById(id)
		.orElseThrow(() -> new EntityNotFoundException("GiftCard not found with id: " + id));

		if (giftCard.isRedeemed()) {
			throw new IllegalStateException("GiftCard is already redeemed");
		}

		if (giftCard.getExpirationDate().isBefore(LocalDateTime.now())) {
			throw new IllegalStateException("GiftCard has expired");
		}

		if (giftCard.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalStateException("GiftCard balance is zero, cannot redeem");
		}


		giftCard.setRedeemed(true);
		giftCardRepository.save(giftCard);

		return new RedeemCardResponseDTO(
			giftCard.getId(),
			"Card successfully redeemed.",
			giftCard.isRedeemed(),
			giftCard.getBalance()
		);

	}



	public List<GiftCardResponseDTO> getAllCards() {
		List<GiftCard> giftCards =  giftCardRepository.findAll();
		return giftCards.stream().map(card -> 
				new GiftCardResponseDTO(card.getId(), card.getCode(), card.getBalance(),
				card.getExpirationDate(), card.isRedeemed(), card.getCreationDate())).collect(Collectors.toList());
	}

	public GiftCardResponseDTO getCardById(UUID id) throws Exception {
		Optional<GiftCard> giftCardEntity = giftCardRepository.findById(id);

		if(giftCardEntity.isEmpty()){
			throw new Exception("no user found with such id: " + id);
		}
		
		GiftCard giftCard = giftCardEntity.get();

		return new GiftCardResponseDTO(giftCard.getId(), giftCard.getCode(), giftCard.getBalance(),
					giftCard.getExpirationDate(), giftCard.isRedeemed(), giftCard.getCreationDate());
	}
}
