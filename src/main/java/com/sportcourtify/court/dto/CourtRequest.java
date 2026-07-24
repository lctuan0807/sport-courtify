package com.sportcourtify.court.dto;

import java.math.BigDecimal;

import com.sportcourtify.court.SportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CourtRequest(
		@NotBlank String name,
		@NotNull SportType sportType,
		@NotNull @Positive BigDecimal pricePerHour,
		Boolean active) {
}
