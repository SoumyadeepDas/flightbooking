package edu.soumyadeep.flightbooking.service.impl.helper;

import edu.soumyadeep.flightbooking.dto.BookingRequestDto;
import edu.soumyadeep.flightbooking.model.User;
import edu.soumyadeep.flightbooking.repository.UserRepository;
import edu.soumyadeep.flightbooking.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

            if (!passenger.getTravellerClass().equalsIgnoreCase(cabin)) {
                throw new RuntimeException(String.format(
                        "Traveller class '%s' does not match selected flight cabin '%s' for passenger %s %s.",
                        passenger.getTravellerClass(), cabin,
                        passenger.getFirstName(), passenger.getLastName()
                ));
            }
        }
    }
}