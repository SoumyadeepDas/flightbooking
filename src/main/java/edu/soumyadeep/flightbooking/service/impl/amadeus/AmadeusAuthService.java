package edu.soumyadeep.flightbooking.service.impl.amadeus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:application-secret.properties")
public class AmadeusAuthService {

    @Value("${amadeus.api.base-url}")
    private String baseUrl;

    @Value("${amadeus.api.key}")
    private String clientId;

    @Value("${amadeus.api.secret}")
    private String clientSecret;

    public String getAccessToken() {
        WebClient client = WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                        .build())
                .build();

        Map<String, Object> response = client.post()
                .uri("/v1/security/oauth2/token")
                .body(BodyInserters
                        .fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> {
                    log.error("Error fetching Amadeus token: {}", e.getMessage());
                    return Mono.empty();
                })
                .block();

        if (response == null || !response.containsKey("access_token")) {
            log.error("Failed to retrieve Amadeus access token. Response: {}", response);
            return null;
        }

        return response.get("access_token").toString();
    }
}