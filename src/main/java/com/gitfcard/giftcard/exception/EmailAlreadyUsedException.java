package com.gitfcard.giftcard.exception;

public class EmailAlreadyUsedException extends Exception {

	public EmailAlreadyUsedException(String email){
		super("User with this email already exists: " + email);
	}
	
}
