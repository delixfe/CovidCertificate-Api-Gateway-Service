package ch.admin.bag.covidcertificate.gateway.service;

import ch.admin.bag.covidcertificate.gateway.error.RestError;
import ch.admin.bag.covidcertificate.gateway.service.dto.ReadValueSetsException;
import ch.admin.bag.covidcertificate.gateway.service.dto.incoming.IssuableRapidTestDto;
import ch.admin.bag.covidcertificate.gateway.service.dto.incoming.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.gateway.service.dto.incoming.RapidTestDto;
import ch.admin.bag.covidcertificate.gateway.service.dto.incoming.VaccineDto;
import ch.admin.bag.covidcertificate.gateway.service.util.WebClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValueSetsService {

    @Value("${cc-management-service.uri}")
    private String serviceUri;

    private final WebClient defaultWebClient;

    public List<RapidTestDto> getRapidTests() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUri + "api/v1/rapid-tests");

        String uri = builder.toUriString();
        log.debug("Call the ValueSetsService with url {}", kv("url", uri));
        try {
            List<RapidTestDto> response = defaultWebClient
                    .post()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<RapidTestDto>>() {})
                    .switchIfEmpty(Mono.error(new IllegalStateException("Response Body is null")))
                    .block();

            log.trace("ValueSetsService Response: {}", response);
            return response;

        } catch (WebClientResponseException e) {
            RestError errorResponse = WebClientUtils.handleWebClientResponseError(e);
            throw new ReadValueSetsException(errorResponse);
        }
    }

    public List<IssuableRapidTestDto> getIssuableRapidTests() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUri + "api/v1/issuable-rapid-tests");

        String uri = builder.toUriString();
        log.debug("Call the ValueSetsService with url {}", kv("url", uri));
        try {
            List<IssuableRapidTestDto> response = defaultWebClient
                    .post()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<IssuableRapidTestDto>>() {})
                    .switchIfEmpty(Mono.error(new IllegalStateException("Response Body is null")))
                    .block();

            log.trace("ValueSetsService Response: {}", response);
            return response;

        } catch (WebClientResponseException e) {
            RestError errorResponse = WebClientUtils.handleWebClientResponseError(e);
            throw new ReadValueSetsException(errorResponse);
        }
    }

    public List<VaccineDto> getVaccines() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUri + "api/v1/vaccines");

        String uri = builder.toUriString();
        log.debug("Call the ValueSetsService with url {}", kv("url", uri));
        try {
            List<VaccineDto> response = defaultWebClient
                    .post()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<VaccineDto>>() {})
                    .switchIfEmpty(Mono.error(new IllegalStateException("Response Body is null")))
                    .block();

            log.trace("ValueSetsService Response: {}", response);
            return response;

        } catch (WebClientResponseException e) {
            RestError errorResponse = WebClientUtils.handleWebClientResponseError(e);
            throw new ReadValueSetsException(errorResponse);
        }
    }

    public List<IssuableVaccineDto> getIssuableVaccines() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUri + "api/v1/issuable-vaccines");

        String uri = builder.toUriString();
        log.debug("Call the ValueSetsService with url {}", kv("url", uri));
        try {
            List<IssuableVaccineDto> response = defaultWebClient
                    .post()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<IssuableVaccineDto>>() {})
                    .switchIfEmpty(Mono.error(new IllegalStateException("Response Body is null")))
                    .block();

            log.trace("ValueSetsService Response: {}", response);
            return response;

        } catch (WebClientResponseException e) {
            RestError errorResponse = WebClientUtils.handleWebClientResponseError(e);
            throw new ReadValueSetsException(errorResponse);
        }
    }
}
