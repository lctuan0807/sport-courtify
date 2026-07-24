package com.sportcourtify.venue.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VenueRequest(
		@NotBlank String name,
		String address,
		String city,
		@NotNull LocalTime openTime,
		@NotNull LocalTime closeTime) {
}
