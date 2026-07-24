package com.sportcourtify.court;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sportcourtify.common.exception.ResourceNotFoundException;
import com.sportcourtify.court.dto.CourtRequest;
import com.sportcourtify.court.dto.CourtResponse;
import com.sportcourtify.venue.Venue;
import com.sportcourtify.venue.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourtService {

	private final CourtRepository courtRepository;
	private final VenueRepository venueRepository;

	public CourtResponse create(Long venueId, CourtRequest request) {
		Venue venue = findVenue(venueId);
		Court court = Court.builder()
				.venue(venue)
				.name(request.name())
				.sportType(request.sportType())
				.pricePerHour(request.pricePerHour())
				.active(request.active() == null || request.active())
				.build();
		return CourtResponse.from(courtRepository.save(court));
	}

	public List<CourtResponse> list(Long venueId) {
		findVenue(venueId);
		return courtRepository.findByVenueId(venueId).stream()
				.map(CourtResponse::from)
				.toList();
	}

	public CourtResponse getById(Long venueId, Long courtId) {
		return CourtResponse.from(findCourt(venueId, courtId));
	}

	public CourtResponse update(Long venueId, Long courtId, CourtRequest request) {
		Court court = findCourt(venueId, courtId);
		court.setName(request.name());
		court.setSportType(request.sportType());
		court.setPricePerHour(request.pricePerHour());
		court.setActive(request.active() == null || request.active());
		return CourtResponse.from(courtRepository.save(court));
	}

	public void delete(Long venueId, Long courtId) {
		courtRepository.delete(findCourt(venueId, courtId));
	}

	private Venue findVenue(Long venueId) {
		return venueRepository.findById(venueId)
				.orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + venueId));
	}

	private Court findCourt(Long venueId, Long courtId) {
		Court court = courtRepository.findById(courtId)
				.orElseThrow(() -> new ResourceNotFoundException("Court not found: " + courtId));
		if (!court.getVenue().getId().equals(venueId)) {
			throw new ResourceNotFoundException("Court not found: " + courtId);
		}
		return court;
	}
}
