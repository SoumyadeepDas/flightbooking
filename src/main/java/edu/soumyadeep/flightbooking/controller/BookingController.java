package edu.soumyadeep.flightbooking.controller;

import edu.soumyadeep.flightbooking.dto.BookingRequestDto;
import edu.soumyadeep.flightbooking.exception.BookingNotFoundException;
import edu.soumyadeep.flightbooking.exception.UserHasNoBookingsException;
import edu.soumyadeep.flightbooking.exception.UserNotFoundException;
import edu.soumyadeep.flightbooking.model.Booking;
import edu.soumyadeep.flightbooking.repository.BookingRepository;
import edu.soumyadeep.flightbooking.repository.UserRepository;
import edu.soumyadeep.flightbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.soumyadeep.flightbooking.constant.EndpointConstant.*;

@RestController
@RequestMapping(BOOKING_PATH)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    /**
     * Create a one-way flight booking
     */
    @PostMapping(BOOKING_ONEWAY)
    public ResponseEntity<Booking> createOneWayBooking(@Valid @RequestBody final BookingRequestDto req) {
        Booking booking = bookingService.createOneWayBooking(req);
        return ResponseEntity.ok(booking);
    }

    /**
     * Create a roundtrip flight booking
     */
    @PostMapping(BOOKING_ROUNDTRIP)
    public ResponseEntity<Booking> createRoundTripBooking(@Valid @RequestBody final BookingRequestDto req) {
        Booking booking = bookingService.createRoundTripBooking(req);
        return ResponseEntity.ok(booking);
    }

    /**
     * Get booking details by booking ID
     */
    @GetMapping(FIND_BOOKING_BY_BOOKING_ID)
    public ResponseEntity<Booking> getBooking(@PathVariable final Long id) {
        return bookingService.getBooking(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID " + id));
    }

    /**
     * Get all bookings for a user
     */
    @GetMapping(FIND_ALL_BOOKINGS_BY_USER_ID)
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable final Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        var bookings = bookingRepository.findByUserId(userId);
        if (bookings.isEmpty()) {
            throw new UserHasNoBookingsException(userId);
        }

        return ResponseEntity.ok(bookings);
    }

    /**
     * Get booking by reference number
     */
    @GetMapping(FIND_BOOKING_BY_BOOKING_REF)
    public ResponseEntity<Booking> getBookingByReference(@PathVariable final String ref) {
        var booking = bookingRepository.findByBookingReference(ref)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for reference: " + ref));
        return ResponseEntity.ok(booking);
    }
}