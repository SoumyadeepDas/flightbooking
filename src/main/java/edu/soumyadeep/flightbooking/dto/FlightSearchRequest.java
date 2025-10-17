package edu.soumyadeep.flightbooking.dto;



import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FlightSearchRequest {

    @NotBlank(message = "Origin airport code is required")
    @Size(min = 3, max = 3, message = "Origin must be a 3-letter IATA code (e.g. DEL)")
    private String origin;

    @NotBlank(message = "Destination airport code is required")
    @Size(min = 3, max = 3, message = "Destination must be a 3-letter IATA code (e.g. BLR)")
    private String destination;

    @NotNull(message = "Departure date is required")
    @FutureOrPresent(message = "Departure date cannot be in the past")
    private LocalDate departDate;

    @Future(message = "Return date must be in the future")
    private LocalDate returnDate;

    @NotNull(message = "Trip type is required (ONEWAY or ROUNDTRIP)")
    private TripType tripType;

    @NotNull(message = "Number of adults is required")
    @Min(value = 1, message = "At least 1 adult required")
    @Max(value = 9, message = "Max 9 passengers allowed")
    private Integer adults;

    @Pattern(regexp = "ECONOMY|PREMIUM_ECONOMY|BUSINESS|FIRST",
            message = "Cabin must be ECONOMY, PREMIUM_ECONOMY, BUSINESS, or FIRST")
    private String cabin;

    public enum TripType { ONEWAY, ROUNDTRIP }
}