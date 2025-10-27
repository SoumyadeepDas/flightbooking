package edu.soumyadeep.flightbooking.service.impl.helper;

import edu.soumyadeep.flightbooking.dto.BookingRequestDto;
import edu.soumyadeep.flightbooking.model.User;
import edu.soumyadeep.flightbooking.repository.UserRepository;
import edu.soumyadeep.flightbooking.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final UserRepository userRepository;

    // Validate One-Way Booking Request
    public void validateOneWay(BookingRequestDto req) {
        if (req == null)
            throw new RuntimeException("Booking request cannot be null.");
        if (req.getOfferId() == null)
            throw new RuntimeException("Offer ID is required for one-way booking.");
        if (req.getPassengers() == null || req.getPassengers().isEmpty())
            throw new RuntimeException("At least one passenger is required.");
    }

    // Validate Round Trip Booking Request
    public void validateRoundTrip(BookingRequestDto req) {
        if (req == null)
            throw new RuntimeException("Booking request cannot be null.");
        if (req.getOfferIds() == null || req.getOfferIds().size() != 2)
            throw new RuntimeException("Two offers (outbound and return) are required for roundtrip booking.");
        if (req.getReturnDate() == null)
            throw new RuntimeException("Return date is required for round-trip booking.");
        if (req.getPassengers() == null || req.getPassengers().isEmpty())
            throw new RuntimeException("At least one passenger is required.");
    }

    // Validate User Existence
    public User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    // Validate Offer Existence
    public Map<String, Object> validateOffer(String offerId, FlightService flightService) {
        return flightService.getOfferById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found. Please search flights first."));
    }

    // Validate Outbound/Return Direction Logic
    public void validateRoundTripDirection(Map<String, Object> outbound, Map<String, Object> returns) {
        String outboundOrigin = (String) outbound.get("origin");
        String outboundDestination = (String) outbound.get("destination");
        String returnOrigin = (String) returns.get("origin");
        String returnDestination = (String) returns.get("destination");

        if (!outboundOrigin.equalsIgnoreCase(returnDestination)
                || !outboundDestination.equalsIgnoreCase(returnOrigin)) {
            throw new RuntimeException("Invalid roundtrip: return flight must match outbound route.");
        }
    }

    // Validate Traveller Class Matches Flight Cabin
    public void validateCabinConsistency(List<BookingRequestDto.PassengerDto> passengers, String cabin) {
        if (cabin == null || cabin.isBlank()) {
            throw new RuntimeException("Flight cabin information is missing. Please re-fetch flight offers.");
        }

        for (BookingRequestDto.PassengerDto passenger : passengers) {
            if (passenger.getTravellerClass() == null || passenger.getTravellerClass().isBlank()) {
                throw new RuntimeException("Traveller class is required for each passenger.");
            }

            validatePassengerDob(passenger);

            if (!passenger.getTravellerClass().equalsIgnoreCase(cabin)) {
                throw new RuntimeException(String.format(
                        "Traveller class '%s' does not match selected flight cabin '%s' for passenger %s %s.",
                        passenger.getTravellerClass(), cabin,
                        passenger.getFirstName(), passenger.getLastName()
                ));
            }
        }
    }

    // Validate Passenger Date of Birth
    private void validatePassengerDob(BookingRequestDto.PassengerDto passenger) {
        if (passenger.getDob() == null)
            throw new RuntimeException(String.format("Date of birth is required for passenger %s %s.",
                    passenger.getFirstName(), passenger.getLastName()));

        LocalDate dob = passenger.getDob();
        LocalDate today = LocalDate.now();

        if (dob.isAfter(today)) {
            throw new RuntimeException(String.format(
                    "Invalid date of birth for passenger %s %s — DOB cannot be in the future (%s).",
                    passenger.getFirstName(), passenger.getLastName(), dob));
        }

        if (dob.isBefore(LocalDate.of(1900, 1, 1))) {
            throw new RuntimeException(String.format(
                    "Invalid date of birth for passenger %s %s — DOB too old (%s).",
                    passenger.getFirstName(), passenger.getLastName(), dob));
        }
    }

    // Validate Outbound or Return Flight Date Consistency
    public void validateDateConsistency(BookingRequestDto req, Map<String, Object> offer, boolean isReturn) {
        if (offer == null || !offer.containsKey("departDate")) {
            throw new RuntimeException("Offer date information is missing. Please re-fetch flight data.");
        }

        Object offerDepartDate = offer.get("departDate");

        // Choose the right date to compare (depart vs return)
        String bookingDate = isReturn
                ? (req.getReturnDate() != null ? req.getReturnDate().toString() : null)
                : (req.getDepartDate() != null ? req.getDepartDate().toString() : null);

        if (offerDepartDate == null || bookingDate == null) {
            throw new RuntimeException("Booking or offer date is missing.");
        }

        if (!offerDepartDate.toString().equals(bookingDate)) {
            throw new RuntimeException(String.format(
                    "%s flight date '%s' does not match the offer date '%s'. Please reselect flight.",
                    isReturn ? "Return" : "Departure",
                    bookingDate, offerDepartDate
            ));
        }
    }

    // Overload for One-Way Flights (defaults to outbound check)
    public void validateDateConsistency(BookingRequestDto req, Map<String, Object> offer) {
        validateDateConsistency(req, offer, false);
    }
}