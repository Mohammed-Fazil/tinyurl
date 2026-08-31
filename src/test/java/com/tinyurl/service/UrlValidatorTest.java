package com.tinyurl.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tinyurl.exception.InvalidUrlException;

class UrlValidatorTest {

	private UrlValidator urlValidator;

	@BeforeEach
	void setUp() {
		urlValidator = new UrlValidator();
	}

	@Test
	void shouldAcceptValidHttpsUrl() {

		assertDoesNotThrow(() -> urlValidator.validate("https://www.google.com"));
	}

	@Test
	void shouldAcceptValidHttpUrl() {

		assertDoesNotThrow(() -> urlValidator.validate("http://example.com"));
	}

	@Test
	void shouldRejectUrlWithoutScheme() {

		assertThrows(InvalidUrlException.class, () -> urlValidator.validate("google.com"));
	}

	@Test
	void shouldRejectEmptyUrl() {

		assertThrows(InvalidUrlException.class, () -> urlValidator.validate(""));
	}

	@Test
	void shouldRejectInvalidUrl() {

		assertThrows(InvalidUrlException.class, () -> urlValidator.validate("hello"));
	}

	@Test
	void shouldRejectUnsupportedScheme() {

		assertThrows(InvalidUrlException.class, () -> urlValidator.validate("ftp://example.com"));
	}
}