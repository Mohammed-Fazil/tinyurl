package com.tinyurl.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "url_mappings")
public class UrlMapping {

	@Id
	private String id;

	private Long sequenceId;

	@Indexed(unique = true)
	private String shortCode;

	private String originalUrl;

	private LocalDateTime createdAt;

	private String userId;
}