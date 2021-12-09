package com.demo.micro.exception;

public class CustomException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CustomException(String exceptionId) {
		super(exceptionId);
	}

}
