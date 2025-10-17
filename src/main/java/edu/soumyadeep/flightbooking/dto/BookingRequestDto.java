package edu.soumyadeep.flightbooking.dto;

import edu.soumyadeep.flightbooking.model.Booking;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;


import jakarta.validation.Valid;

import lombok.Data;

@Data
public class BookingRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Offer ID is required")
    private String offerId;

    @NotEmpty(message = "At least one passenger is required")
    @Valid
    private List<PassengerDto> passengers;


    @NotNull(message = "Trip type is required")
    private Booking.TripType tripType;

    @NotNull(message = "Departure date is required")
    private LocalDate departDate;

    private LocalDate returnDate;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

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