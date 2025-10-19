package edu.soumyadeep.flightbooking.service;

import edu.soumyadeep.flightbooking.dto.BookingRequestDto;
import edu.soumyadeep.flightbooking.model.Booking;

import java.util.Optional;

public interface BookingService {

    /**
     * Create a one-way booking
     */
    Booking createOneWayBooking(BookingRequestDto req);

    /**
     * Create a round-trip booking
     */
    Booking createRoundTripBooking(BookingRequestDto req);

    /**
     * Retrieve a booking by its ID
     */
    Optional<Booking> getBooking(Long id);
}