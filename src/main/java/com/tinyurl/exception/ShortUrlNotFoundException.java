package com.tinyurl.exception;

public class ShortUrlNotFoundException extends RuntimeException {

	public ShortUrlNotFoundException(String message) {
		super(message);
	}
}