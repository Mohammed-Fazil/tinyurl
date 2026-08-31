package com.tinyurl.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tinyurl.entity.UrlMapping;

public interface UrlMappingRepository extends MongoRepository<UrlMapping, String> {

	Optional<UrlMapping> findByShortCode(String shortCode);

	boolean existsByShortCode(String shortCode);
}