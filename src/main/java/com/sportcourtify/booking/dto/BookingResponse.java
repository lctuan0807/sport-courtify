package com.sportcourtify.booking.dto;

import java.time.Instant;
import java.time.LocalDateTime;

import com.sportcourtify.booking.Booking;
import com.sportcourtify.booking.BookingStatus;

public record BookingResponse(
		Long id,
		Long courtId,
		Long userId,
		LocalDateTime startTime,
		LocalDateTime endTime,
		BookingStatus status,
		Instant cancelledAt,
		Instant createdAt,
		Instant updatedAt) {

	public static BookingResponse from(Booking booking) {
		return new BookingResponse(
				booking.getId(),
				booking.getCourt().getId(),
				booking.getUser().getId(),
				booking.getStartTime(),
				booking.getEndTime(),
				booking.getStatus(),
				booking.getCancelledAt(),
				booking.getCreatedAt(),
				booking.getUpdatedAt());
	}
}
