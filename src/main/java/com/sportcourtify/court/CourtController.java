package com.sportcourtify.court;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sportcourtify.court.dto.CourtRequest;
import com.sportcourtify.court.dto.CourtResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/venues/{venueId}/courts")
@RequiredArgsConstructor
public class CourtController {

	private final CourtService courtService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CourtResponse create(@PathVariable Long venueId, @Valid @RequestBody CourtRequest request) {
		return courtService.create(venueId, request);
	}

	@GetMapping
	public List<CourtResponse> list(@PathVariable Long venueId) {
		return courtService.list(venueId);
	}

	@GetMapping("/{courtId}")
	public CourtResponse getById(@PathVariable Long venueId, @PathVariable Long courtId) {
		return courtService.getById(venueId, courtId);
	}

	@PutMapping("/{courtId}")
	public CourtResponse update(@PathVariable Long venueId, @PathVariable Long courtId,
			@Valid @RequestBody CourtRequest request) {
		return courtService.update(venueId, courtId, request);
	}

	@DeleteMapping("/{courtId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long venueId, @PathVariable Long courtId) {
		courtService.delete(venueId, courtId);
	}
}
