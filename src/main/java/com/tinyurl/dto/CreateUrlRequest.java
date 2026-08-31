package com.tinyurl.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUrlRequest(

		@NotBlank(message = "URL cannot be empty")
		@Pattern(regexp = "^(https?://).+", message = "URL must start with http:// or https://")
		String url

) {
}