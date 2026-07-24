package com.sportcourtify.user.dto;

import java.time.Instant;

import com.sportcourtify.user.User;

public record UserResponse(
		Long id,
		String fullName,
		String email,
		String phone,
		Instant createdAt,
		Instant updatedAt) {

	public static UserResponse from(User user) {
		return new UserResponse(
				user.getId(),
				user.getFullName(),
				user.getEmail(),
				user.getPhone(),
				user.getCreatedAt(),
				user.getUpdatedAt());
	}
}
