package com.sportcourtify.booking;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Query("""
			SELECT COUNT(b) > 0 FROM Booking b
			WHERE b.court.id = :courtId
			  AND b.status <> com.sportcourtify.booking.BookingStatus.CANCELLED
			  AND b.startTime < :endTime
			  AND b.endTime > :startTime
			""")
	boolean existsOverlapping(@Param("courtId") Long courtId, @Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime);
}
