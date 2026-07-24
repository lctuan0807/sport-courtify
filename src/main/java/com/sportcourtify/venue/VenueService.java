package com.sportcourtify.venue;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sportcourtify.common.exception.ResourceNotFoundException;
import com.sportcourtify.venue.dto.VenueRequest;
import com.sportcourtify.venue.dto.VenueResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VenueService {

	private final VenueRepository venueRepository;

	public VenueResponse create(VenueRequest request) {
		Venue venue = Venue.builder()
				.name(request.name())
				.address(request.address())
				.city(request.city())
				.openTime(request.openTime())
				.closeTime(request.closeTime())
				.build();
		return VenueResponse.from(venueRepository.save(venue));
	}

	public List<VenueResponse> list() {
		return venueRepository.findAll().stream()
				.map(VenueResponse::from)
				.toList();
	}

	public VenueResponse getById(Long id) {
		return VenueResponse.from(findVenue(id));
	}

	public VenueResponse update(Long id, VenueRequest request) {
		Venue venue = findVenue(id);
		venue.setName(request.name());
		venue.setAddress(request.address());
		venue.setCity(request.city());
		venue.setOpenTime(request.openTime());
		venue.setCloseTime(request.closeTime());
		return VenueResponse.from(venueRepository.save(venue));
	}

	public void delete(Long id) {
		venueRepository.delete(findVenue(id));
	}

	private Venue findVenue(Long id) {
		return venueRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + id));
	}
}
