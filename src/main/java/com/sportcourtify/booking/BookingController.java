package com.sportcourtify.booking;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sportcourtify.booking.dto.BookingRequest;
import com.sportcourtify.booking.dto.BookingResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookingResponse create(@Valid @RequestBody BookingRequest request) {
		return bookingService.createBooking(request);
	}

	@GetMapping
	public List<BookingResponse> list() {
		return bookingService.list();
	}

	@GetMapping("/{id}")
	public BookingResponse getById(@PathVariable Long id) {
		return bookingService.getById(id);
	}

	@PostMapping("/{id}/cancel")
	public BookingResponse cancel(@PathVariable Long id) {
		return bookingService.cancelBooking(id);
	}
}
