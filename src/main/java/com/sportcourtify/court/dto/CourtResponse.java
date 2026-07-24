package com.sportcourtify.court.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.sportcourtify.court.Court;
import com.sportcourtify.court.SportType;

public record CourtResponse(
		Long id,
		Long venueId,
		String name,
		SportType sportType,
		BigDecimal pricePerHour,
		boolean active,
		Instant createdAt,
		Instant updatedAt) {

	public static CourtResponse from(Court court) {
		return new CourtResponse(
				court.getId(),
				court.getVenue().getId(),
				court.getName(),
				court.getSportType(),
				court.getPricePerHour(),
				court.isActive(),
				court.getCreatedAt(),
				court.getUpdatedAt());
	}
}
