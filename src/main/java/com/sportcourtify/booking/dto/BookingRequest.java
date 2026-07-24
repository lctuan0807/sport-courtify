package com.sportcourtify.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(
		@NotNull Long courtId,
		@NotNull Long userId,
		@NotNull LocalDateTime startTime,
		@NotNull LocalDateTime endTime) {
}
