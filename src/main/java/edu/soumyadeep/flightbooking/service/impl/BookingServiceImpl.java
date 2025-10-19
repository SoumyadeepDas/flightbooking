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

    @Override
    @Transactional
    public Booking createOneWayBooking(BookingRequestDto req) {
        validator.validateOneWay(req);
        User user = validator.validateUser(req.getUserId());

        var offer = validator.validateOffer(req.getOfferId(), flightService);
        String cabin = (String) offer.get("cabin");

        // Ensure traveller class matches the searched cabin
        validator.validateCabinConsistency(req.getPassengers(), cabin);

        double totalPrice = pricingService.calculateOneWayPrice(offer, req.getPassengers().size());

        Booking booking = mapper.toEntity(req, user, offer, totalPrice, Booking.TripType.ONEWAY);
        bookingRepository.save(booking);

        log.info("One-way booking created: {} for user {}", booking.getBookingReference(), user.getId());
        return booking;
    }

    @Override
    @Transactional
    public Booking createRoundTripBooking(BookingRequestDto req) {
        validator.validateRoundTrip(req);
        User user = validator.validateUser(req.getUserId());

        var outbound = validator.validateOffer(req.getOfferIds().get(0), flightService);
        var returns = validator.validateOffer(req.getOfferIds().get(1), flightService);

        validator.validateRoundTripDirection(outbound, returns);

        String outboundCabin = (String) outbound.get("cabin");

        // Ensure traveller class matches outbound cabin
        validator.validateCabinConsistency(req.getPassengers(), outboundCabin);

        double totalPrice = pricingService.calculateRoundTripPrice(outbound, returns, req.getPassengers().size());

        Booking booking = mapper.toEntity(req, user, outbound, totalPrice, Booking.TripType.ROUNDTRIP);
        bookingRepository.save(booking);

        log.info("Round-trip booking created: {} for user {}", booking.getBookingReference(), user.getId());
        return booking;
    }

    @Override
    public Optional<Booking> getBooking(Long id) {
        return bookingRepository.findById(id);
    }
}