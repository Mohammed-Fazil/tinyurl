package com.tinyurl.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.tinyurl.dto.CreateUrlRequest;
import com.tinyurl.dto.CreateUrlResponse;
import com.tinyurl.entity.UrlMapping;
import com.tinyurl.exception.ShortUrlNotFoundException;
import com.tinyurl.repository.UrlMappingRepository;

@Service
public class UrlService {

	private final UrlMappingRepository urlMappingRepository;
	private final CounterService counterService;
	private final Base62Encoder base62Encoder;
	private final UrlValidator urlValidator;

	public UrlService(UrlMappingRepository urlMappingRepository, CounterService counterService,
			Base62Encoder base62Encoder, UrlValidator urlValidator) {

		this.urlMappingRepository = urlMappingRepository;
		this.counterService = counterService;
		this.base62Encoder = base62Encoder;
		this.urlValidator = urlValidator;
	}

	public CreateUrlResponse createShortUrl(CreateUrlRequest request) {
		
		 urlValidator.validate(request.url());

		long sequenceId = counterService.getNextSequence();

		String shortCode = base62Encoder.encode(sequenceId);

		UrlMapping urlMapping = UrlMapping.builder().sequenceId(sequenceId).shortCode(shortCode)
				.originalUrl(request.url()).createdAt(LocalDateTime.now()).build();

		urlMappingRepository.save(urlMapping);

		String shortUrl = "http://localhost:8080/" + shortCode;

		return new CreateUrlResponse(shortUrl);
	}

	public String getOriginalUrl(String shortCode) {

		UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
				.orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found"));

		return urlMapping.getOriginalUrl();
	}

}