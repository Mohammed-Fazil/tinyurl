package com.tinyurl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Base62EncoderTest {

	private final Base62Encoder encoder = new Base62Encoder();

	@Test
	void shouldEncodeNumbersToBase62() {

		assertEquals("0", encoder.encode(0));
		assertEquals("1", encoder.encode(1));
		assertEquals("9", encoder.encode(9));
		assertEquals("a", encoder.encode(10));
		assertEquals("z", encoder.encode(35));
		assertEquals("A", encoder.encode(36));
		assertEquals("Z", encoder.encode(61));
		assertEquals("10", encoder.encode(62));
		assertEquals("w7e", encoder.encode(123456));
	}
}