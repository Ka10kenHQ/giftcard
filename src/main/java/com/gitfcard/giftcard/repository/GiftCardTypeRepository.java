package com.gitfcard.giftcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gitfcard.giftcard.entity.GiftCardType;

@Repository
public interface GiftCardTypeRepository extends JpaRepository<GiftCardType,Long>  {

	
}
