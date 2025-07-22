package com.gitfcard.giftcard.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

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

	public Long redeem(Long id){
		Optional<GiftCard> giftCardOpt = giftCardRepository.findById(id);

		if(giftCardOpt.isEmpty()){
			throw new EntityNotFoundException("There is no GiftCard with such id:" + id);
		}

		GiftCard giftCardRow = giftCardOpt.get();

		if(giftCardRow.isRedeemed()){
			throw new Error("GiftCard is already redeem");
		}

		giftCardRow.setRedeemed(true);

		giftCardRepository.save(giftCardRow);

		return giftCardRow.getId();

	}

	public 
}
