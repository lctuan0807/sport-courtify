package com.sportcourtify.venue.dto;

import java.time.Instant;
import java.time.LocalTime;

import com.sportcourtify.venue.Venue;

public record VenueResponse(
		Long id,
		String name,
		String address,
		String city,
		LocalTime openTime,
		LocalTime closeTime,
		Instant createdAt,
		Instant updatedAt) {

	public static VenueResponse from(Venue venue) {
		return new VenueResponse(
				venue.getId(),
				venue.getName(),
				venue.getAddress(),
				venue.getCity(),
				venue.getOpenTime(),
				venue.getCloseTime(),
				venue.getCreatedAt(),
				venue.getUpdatedAt());
	}
}
