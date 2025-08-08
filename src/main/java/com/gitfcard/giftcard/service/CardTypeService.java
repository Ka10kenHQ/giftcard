package com.gitfcard.giftcard.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitfcard.giftcard.dto.GiftCardTypeResponseDTO;
import com.gitfcard.giftcard.entity.GiftCardType;
import com.gitfcard.giftcard.repository.GiftCardTypeRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CardTypeService {

	private final GiftCardTypeRepository giftCardTypeRepository;

	@Autowired
	public CardTypeService(GiftCardTypeRepository giftCardTypeRepository){
		this.giftCardTypeRepository = giftCardTypeRepository;
	}

	public List<GiftCardTypeResponseDTO> getAll(){
		return giftCardTypeRepository.findAll()
			   .stream().map(cardType ->
			   new GiftCardTypeResponseDTO(cardType.getId(), cardType.getName(), cardType.getCurrency())
			   )
			.collect(Collectors.toList());
	}

    public void create(String name, String currency) {
        GiftCardType newType = new GiftCardType(name, currency);
        giftCardTypeRepository.save(newType);
    }

    public void delete(Long id) {
        giftCardTypeRepository.deleteById(id);
    }
}

