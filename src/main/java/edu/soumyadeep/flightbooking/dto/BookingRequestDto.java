package edu.soumyadeep.flightbooking.dto;

import edu.soumyadeep.flightbooking.model.Booking;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookingRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    // For one-way bookings
    private String offerId;

    // For round-trip bookings
    private List<String> offerIds = new ArrayList<>();

    @NotEmpty(message = "At least one passenger is required")
    @Valid
    private List<PassengerDto> passengers;

    @NotNull(message = "Trip type is required")
    private Booking.TripType tripType;

    @NotNull(message = "Departure date is required")
    private LocalDate departDate;

    // Now optional — validation will happen programmatically for ROUNDTRIP only
    private LocalDate returnDate;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    /**
     * Helper method — true if the request represents a round trip.
     */
    public boolean isRoundTrip() {
        return tripType != null && tripType == Booking.TripType.ROUNDTRIP;
    }

    /**
     * Helper method — true if the request represents a one-way trip.
     */
    public boolean isOneWay() {
        return tripType != null && tripType == Booking.TripType.ONEWAY;
    }

    public String getOutboundOfferId() {
        if (isRoundTrip() && offerIds != null && !offerIds.isEmpty()) {
            return offerIds.get(0);
        }
        return offerId;
    }

    public String getReturnOfferId() {
        if (isRoundTrip() && offerIds != null && offerIds.size() > 1) {
            return offerIds.get(1);
        }
        return null;
    }

    @Data
    public static class PassengerDto {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotNull(message = "Date of birth is required")
        private LocalDate dob;

        @Pattern(
                regexp = "ECONOMY|PREMIUM_ECONOMY|BUSINESS|FIRST",
                message = "Traveller class must be ECONOMY, PREMIUM_ECONOMY, BUSINESS, or FIRST"
        )
        private String travellerClass;
    }
}