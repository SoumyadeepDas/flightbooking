package edu.soumyadeep.flightbooking.service.impl;

import edu.soumyadeep.flightbooking.dto.BookingRequestDto;
import edu.soumyadeep.flightbooking.model.Booking;
import edu.soumyadeep.flightbooking.model.User;
import edu.soumyadeep.flightbooking.repository.BookingRepository;
import edu.soumyadeep.flightbooking.repository.UserRepository;
import edu.soumyadeep.flightbooking.service.BookingService;
import edu.soumyadeep.flightbooking.service.FlightService;
import edu.soumyadeep.flightbooking.service.impl.helper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FlightService flightService;
    private final BookingValidator validator;
    private final BookingMapper mapper;
    private final BookingPricingService pricingService;

    /**
     * Handles creation of a one-way flight booking.
     */
    @Override
    @Transactional
    public Booking createOneWayBooking(BookingRequestDto req) {
        // Validate booking details
        validator.validateOneWay(req);
        User user = validator.validateUser(req.getUserId());

        // Fetch the selected flight offer
        var offer = validator.validateOffer(req.getOfferId(), flightService);

        // Validate cabin and date consistency
        String cabin = (String) offer.get("cabin");
        validator.validateCabinConsistency(req.getPassengers(), cabin);
        validator.validateDateConsistency(req, offer); // outbound only (default false)

        // Calculate total price
        double totalPrice = pricingService.calculateOneWayPrice(offer, req.getPassengers().size());

        // Map DTO → Entity
        Booking booking = mapper.toEntity(req, user, offer, totalPrice, Booking.TripType.ONEWAY);
        bookingRepository.save(booking);

        log.info("One-way booking created successfully: {} (User ID: {})",
                booking.getBookingReference(), user.getId());
        return booking;
    }

    /**
     * Handles creation of a round-trip flight booking.
     */
    @Override
    @Transactional
    public Booking createRoundTripBooking(BookingRequestDto req) {
        // Step 1: Validate request data and user
        validator.validateRoundTrip(req);
        User user = validator.validateUser(req.getUserId());

        // Step 2: Fetch both outbound and return offers
        var outbound = validator.validateOffer(req.getOfferIds().get(0), flightService);
        var returns = validator.validateOffer(req.getOfferIds().get(1), flightService);

        // Step 3: Validate flight direction consistency
        validator.validateRoundTripDirection(outbound, returns);

        // Step 4: Validate booking dates match searched offer dates
        validator.validateDateConsistency(req, outbound, false); // checks departDate
        validator.validateDateConsistency(req, returns, true);   // checks returnDate

        // Step 5: Validate traveller cabin consistency
        String outboundCabin = (String) outbound.get("cabin");
        validator.validateCabinConsistency(req.getPassengers(), outboundCabin);

        // Step 6: Calculate total price (outbound + return)
        double totalPrice = pricingService.calculateRoundTripPrice(outbound, returns, req.getPassengers().size());

        // Step 7: Map DTO → Entity and save booking
        Booking booking = mapper.toEntity(req, user, outbound, totalPrice, Booking.TripType.ROUNDTRIP);
        bookingRepository.save(booking);

        log.info("Round-trip booking created successfully: {} (User ID: {})",
                booking.getBookingReference(), user.getId());
        return booking;
    }

    /**
     * Retrieves a booking by its database ID.
     */
    @Override
    public Optional<Booking> getBooking(Long id) {
        return bookingRepository.findById(id);
    }
}