package com.singingbush.ukcrime.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.singingbush.ukcrime.model.Crime;
import com.singingbush.ukcrime.model.Neighbourhood;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singingbush.ukcrime.model.PoliceForce;
import com.singingbush.ukcrime.model.SeniorOfficer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class UkPoliceApi {

    private static final Logger log = LoggerFactory.getLogger(UkPoliceApi.class);
    private static final String API_BASE = "https://data.police.uk/api";

    private final String _baseApiUrl; // only needed for wiremock tests
    private final ObjectMapper _mapper;
    private final HttpClient _httpClient;
    private LocalDate lastUpdatedDate;

    public UkPoliceApi() {
        this(API_BASE);
    }

    // only used for testing with Wiremock
    UkPoliceApi(@NotNull final String baseUrl) {
        _baseApiUrl = !baseUrl.isBlank() ? baseUrl : API_BASE;
        _mapper = new ObjectMapper();

        _httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4L))
            //.version(HttpClient.Version.HTTP_1_1)  // HttpClient.Version.HTTP_2 is the default
            .build();
    }

    public List<PoliceForce> getAllPoliceForces() {
        return getMany(URI.create(String.format("%s/forces", _baseApiUrl)), PoliceForce.class);
    }

    public PoliceForce getPoliceForceById(final String policeForceId) {
        return getOne(URI.create(String.format("%s/forces/%s", _baseApiUrl, policeForceId)), PoliceForce.class);
    }

    /**
     * @see <a href="https://data.police.uk/docs/method/crime-last-updated/">API doc: Last updated</a>
     * @return Month of the latest crime data in ISO date format. (The day is irrelevant and is only there to keep a standard formatted date)
     */
    public LocalDate getLastUpdatedDate() {
        if(lastUpdatedDate == null) {
            final Map<String, String> body = getOne(URI.create(String.format("%s/crime-last-updated", _baseApiUrl)), Map.class);

            final String lastUpdatedString = body.get("date");
            this.lastUpdatedDate = LocalDate.parse(lastUpdatedString, DateTimeFormatter.ISO_DATE);
        }
        return lastUpdatedDate;
    }

    /**
     * @see <a href="https://data.police.uk/docs/method/senior-officers/">API doc: Senior officers</a>
     *
     * @param policeForce the {@link PoliceForce}
     * @return a list of senior officers for the given police force
     */
    public List<SeniorOfficer> getPoliceForceSeniorOfficers(final @NotNull PoliceForce policeForce) {
        return getMany(URI.create(String.format("%s/forces/%s/people", _baseApiUrl, policeForce.getId())), SeniorOfficer.class);
    }

    /**
     * @see <a href="https://data.police.uk/docs/method/neighbourhoods/">API doc: List of neighbourhoods for a force</a>
     *
     * @param policeForce the {@link PoliceForce}
     * @return a list of neighbourhoods
     */
    public List<Neighbourhood> getPoliceForceNeighbourhoods(final @NotNull PoliceForce policeForce) {
        return getMany(URI.create(String.format("%s/%s/neighbourhoods", _baseApiUrl, policeForce.getId())), Neighbourhood.class);
    }

    /**
     * @see <a href="https://data.police.uk/docs/method/neighbourhood/">API doc: Specific neighbourhood</a>
     *
     * @param policeForce the {@link PoliceForce}
     * @param id the neighbourhood id
     * @return a neighbourhood by id
     */
    public Neighbourhood getPoliceForceNeighbourhood(final @NotNull PoliceForce policeForce, final @NotNull String id) {
        return getOne(URI.create(String.format("%s/%s/%s", _baseApiUrl, policeForce.getId(), id)), Neighbourhood.class);
    }

    public List<Crime> streetCrimeByLocation(final String latitude, final String longitude) {
        return getMany(URI.create(String.format("%s/crimes-street/all-crime?lat=%s&lng=%s", _baseApiUrl, latitude, longitude)), Crime.class);
    }

    public List<Crime> streetCrimeByPoliceForce(final @NotNull PoliceForce policeForce) {
        final String lastMonth = LocalDate.ofInstant(Instant.now(), ZoneId.of("Europe/London"))
            .minusMonths(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM"));

        final URI uri = URI.create(String.format("%s/crimes-no-location?category=all-crime&force=%s&date=%s", _baseApiUrl, policeForce.getId(), lastMonth));
        return getMany(uri, Crime.class);
    }

    private HttpRequest get(final URI uri) {
        return HttpRequest.newBuilder()
            .uri(uri)
            .header("Accept-Language", "en-GB")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .timeout(Duration.ofSeconds(3L))
            .GET()   // this is the default
            .build();
    }

    private <T> @Nullable T getOne(URI uri, final Class<T> type) {
        final HttpRequest request = get(uri);

        try {
            //HttpResponse.BodyHandlers.ofInputStream() // todo: compare String with InputStream for performance
            final HttpResponse<String> response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response status code: " + response.statusCode());
            log.debug("Response headers: " + response.headers());
            log.trace("Response body: " + response.body());

            if(response.statusCode() == 200) {
                final T thing = _mapper.readValue(response.body(), type);
                log.debug("found a {} object", type.getSimpleName());
                return thing;
            }
        } catch(final IOException | InterruptedException e) {
            log.error(String.format("Error while making HTTP request to %s", uri), e);
        }
        return null;
    }

    private <T> @Nullable List<T> getMany(final URI uri, final Class<T> type) {
        final HttpRequest request = get(uri);

        try {
            //HttpResponse.BodyHandlers.ofInputStream() // todo: compare String with InputStream for performance
            final HttpResponse<String> response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response status code: " + response.statusCode());
            log.debug("Response headers: " + response.headers());
            log.trace("Response body: " + response.body());

            if(response.statusCode() == 200) {
                final CollectionType listType = _mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
                final List<T> things = _mapper.readValue(response.body(), listType);

//            final List<T> things = _mapper.readValue(response.body(), new TypeReference<>(){}); // not working
                log.info("found {} {} objects", things.size(), type.getSimpleName());
                return things;
            }
        } catch(final IOException | InterruptedException e) {
            log.error(String.format("Error while making HTTP request to %s", uri), e);
        }


//        _httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//            .thenAccept(response -> {
//                log.info("Response status code: " + response.statusCode());
//                log.info("Response headers: " + response.headers());
//                log.info("Response body: " + response.body());
//            });

        return null;
    }

    private <T> CompletableFuture<List<T>> getManyAsync(final URI uri, final Class<T> type) {
        final HttpRequest request = get(uri);
        final CollectionType listType = _mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);

        return _httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                log.info("Response status code: " + response.statusCode());
                log.debug("Response headers: " + response.headers());
                log.debug("Response body: " + response.body());
                return response;
            })
            .thenApply(HttpResponse::body)
            .thenApply(body -> {
                try {
                    return _mapper.readValue(body, listType);
                } catch (final JsonProcessingException e) {
                    log.error("", e);
                    throw new CompletionException(e);
                }
            });
    }
}
