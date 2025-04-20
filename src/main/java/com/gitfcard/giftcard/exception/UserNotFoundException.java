package com.gitfcard.giftcard.exception;

public class UserNotFoundException extends Exception{

	public UserNotFoundException(Long id, String firstName){
		super("User: " + firstName + "with id: " + id + " not found");
	}

	public UserNotFoundException(Long id){
		super("User with id: " + id + " not found");
	}

	
}
