package edu.soumyadeep.flightbooking.service.impl;

import edu.soumyadeep.flightbooking.dto.BookingRequestDto;
import edu.soumyadeep.flightbooking.model.Booking;
import edu.soumyadeep.flightbooking.model.Passenger;
import edu.soumyadeep.flightbooking.model.User;
import edu.soumyadeep.flightbooking.repository.BookingRepository;
import edu.soumyadeep.flightbooking.repository.UserRepository;
import edu.soumyadeep.flightbooking.service.BookingService;
import edu.soumyadeep.flightbooking.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FlightService flightService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              FlightService flightService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.flightService = flightService;

        log.info("BookingServiceImpl initialized with FlightService instance: {}", flightService);
    }

    @Override
    @Transactional
    public Booking createBooking(BookingRequestDto req) {
        // 1) Validate user
        if (req == null) throw new RuntimeException("Booking request cannot be null");

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2) Get the selected offer by ID from FlightService
        var offer = flightService.getOfferById(req.getOfferId())
                .orElseThrow(() -> new RuntimeException("Offer not found. Please search flights first."));

        // Extract values from offer
        double price = ((Number) offer.get("price")).doubleValue();
        String origin = (String) offer.get("origin");
        String destination = (String) offer.get("destination");

        // Validate passengers
        if (req.getPassengers() == null || req.getPassengers().isEmpty()) {
            throw new RuntimeException("At least one passenger must be provided.");
        }

        // Compute passenger count and total price
        int passengerCount = req.getPassengers().size();
        double totalPrice = price * passengerCount;

        Booking.TripType tripType = req.getTripType() != null ? req.getTripType() : Booking.TripType.ONEWAY;
        if (tripType == Booking.TripType.ROUNDTRIP) {
            // if roundtrip required to be priced differently (e.g., double) adjust here
            totalPrice *= 2;
        }

        // Build a booking object with all fields mapped
        Booking booking = Booking.builder()
                .bookingReference("MMT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .tripType(tripType)
                .origin(origin)
                .destination(destination)
                .departDate(req.getDepartDate())
                .returnDate(tripType == Booking.TripType.ROUNDTRIP ? req.getReturnDate() : null)
                .totalPrice(totalPrice)
                .build();

        // Add passengers
        List<Passenger> passengerEntities = new ArrayList<>();
        for (BookingRequestDto.PassengerDto dto : req.getPassengers()) {
            if (dto == null) continue;

            Passenger.TravellerClass travellerClass = Passenger.TravellerClass.ECONOMY; // default
            try {
                String tc = dto.getTravellerClass();
                if (tc != null) {
                    travellerClass = Passenger.TravellerClass.valueOf(tc);
                }
            } catch (IllegalArgumentException iae) {
                // fallback to ECONOMY if invalid value provided
                log.warn("Invalid travellerClass '{}' provided for passenger {} {}, defaulting to ECONOMY",
                        dto.getTravellerClass(), dto.getFirstName(), dto.getLastName());
            }

            Passenger p = Passenger.builder()
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .dob(dto.getDob())
                    .travellerClass(travellerClass)
                    .booking(booking)
                    .build();
            passengerEntities.add(p);
        }
        booking.setPassengers(passengerEntities);

        // Save and return
        bookingRepository.save(booking);
        log.info("Booking created: {} for user {} with {} passengers (totalPrice={})",
                booking.getBookingReference(), user.getId(), passengerEntities.size(), booking.getTotalPrice());
        return booking;
    }

    @Override
    public Optional<Booking> getBooking(Long id) {
        return bookingRepository.findById(id);
    }
}