package com.gitfcard.giftcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gitfcard.giftcard.entity.GiftCard;

@Repository
public interface GiftCardRepository  extends JpaRepository<GiftCard, Long>{

}
