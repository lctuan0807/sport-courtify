package com.sportcourtify.booking;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sportcourtify.booking.dto.BookingRequest;
import com.sportcourtify.booking.dto.BookingResponse;
import com.sportcourtify.common.exception.BookingConflictException;
import com.sportcourtify.common.exception.ResourceNotFoundException;
import com.sportcourtify.court.Court;
import com.sportcourtify.court.CourtRepository;
import com.sportcourtify.user.User;
import com.sportcourtify.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

	private final BookingRepository bookingRepository;
	private final CourtRepository courtRepository;
	private final UserRepository userRepository;

	public BookingResponse createBooking(BookingRequest request) {
		if (!request.startTime().isBefore(request.endTime())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startTime must be before endTime");
		}

		Court court = courtRepository.findById(request.courtId())
				.orElseThrow(() -> new ResourceNotFoundException("Court not found: " + request.courtId()));
		User user = userRepository.findById(request.userId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.userId()));

		if (bookingRepository.existsOverlapping(court.getId(), request.startTime(), request.endTime())) {
			throw new BookingConflictException(
					"Court " + court.getId() + " is already booked for the requested time range");
		}

		Booking booking = Booking.builder()
				.court(court)
				.user(user)
				.startTime(request.startTime())
				.endTime(request.endTime())
				.status(BookingStatus.CONFIRMED)
				.build();
		return BookingResponse.from(bookingRepository.save(booking));
	}

	public List<BookingResponse> list() {
		return bookingRepository.findAll().stream()
				.map(BookingResponse::from)
				.toList();
	}

	public BookingResponse getById(Long id) {
		return BookingResponse.from(findBooking(id));
	}

	public BookingResponse cancelBooking(Long id) {
		Booking booking = findBooking(id);
		if (booking.getStatus() != BookingStatus.CANCELLED) {
			booking.setStatus(BookingStatus.CANCELLED);
			booking.setCancelledAt(Instant.now());
			bookingRepository.save(booking);
		}
		return BookingResponse.from(booking);
	}

	private Booking findBooking(Long id) {
		return bookingRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
	}
}
