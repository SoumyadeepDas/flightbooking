package edu.soumyadeep.flightbooking.service;

import edu.soumyadeep.flightbooking.dto.BookingRequestDto;
import edu.soumyadeep.flightbooking.model.Booking;

import java.util.Optional;

public interface BookingService {
    Booking createBooking(BookingRequestDto req);
    Optional<Booking> getBooking(Long id);
}
