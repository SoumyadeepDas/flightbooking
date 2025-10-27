package edu.soumyadeep.flightbooking.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "flight_offers_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightOfferCache {

    @Id
    @Column(name = "offer_id", nullable = false, unique = true)
    private String offerId;

    @Column(nullable = false, length = 10)
    private String origin;

    @Column(nullable = false, length = 10)
    private String destination;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, length = 5)
    private String currency;

    @Column(nullable = false, length = 20)
    private String cabin;

    @Column(name = "trip_direction", nullable = false, length = 10)
    private String tripDirection; // OUTBOUND or RETURN

    @Column(name = "depart_date", nullable = false)
    private String departDate;


    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }
}