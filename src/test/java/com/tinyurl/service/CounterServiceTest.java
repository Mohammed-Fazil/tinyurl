package com.tinyurl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.tinyurl.entity.Counter;

class CounterServiceTest {

	@Mock
	private MongoOperations mongoOperations;

	private CounterService counterService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		counterService = new CounterService(mongoOperations);
	}

	@Test
	void shouldReturnNextSequence() {

		Counter counter = new Counter("url", 123456);

		when(mongoOperations.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
				eq(Counter.class))).thenReturn(counter);

		long sequence = counterService.getNextSequence();

		assertEquals(123456, sequence);
	}
}