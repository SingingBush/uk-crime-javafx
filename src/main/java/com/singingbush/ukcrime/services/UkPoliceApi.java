package com.singingbush.ukcrime.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.singingbush.ukcrime.model.Crime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singingbush.ukcrime.model.PoliceForce;
import com.singingbush.ukcrime.model.SeniorOfficer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class UkPoliceApi {

    private static final Logger log = LoggerFactory.getLogger(UkPoliceApi.class);
    private static final String API_BASE = "https://data.police.uk/api";

    private final ObjectMapper _mapper;
    private HttpClient _httpClient;

    public UkPoliceApi() {
        _mapper = new ObjectMapper();

        _httpClient = HttpClient.newBuilder()
            //.version(HttpClient.Version.HTTP_1_1)  // HttpClient.Version.HTTP_2 is the default
            .build();
    }

    public List<PoliceForce> getAllPoliceForces() {
        return getMany(URI.create(String.format("%s/forces", API_BASE)), PoliceForce.class);
    }

    public PoliceForce getPoliceForceById(final String policeForceId) {
        return getOne(URI.create(String.format("%s/forces/%s", API_BASE, policeForceId)), PoliceForce.class);
    }

    /**
     * @see <a href="https://data.police.uk/docs/method/senior-officers/">API doc: Senior officers</a>
     *
     * @param policeForce the {@link PoliceForce}
     * @return a list of senior officers for the given police force
     */
    public List<SeniorOfficer> getPoliceForceSeniorOfficers(final PoliceForce policeForce) {
        return getMany(URI.create(String.format("%s/forces/%s/people", API_BASE, policeForce.getId())), SeniorOfficer.class);
    }

    public List<Crime> streetCrimeByLocation(final String latitude, final String longitude) {
        final URI uri = URI.create("$API_BASE/crimes-street/all-crime?lat=$latitude&lng=$longitude&date=2019-03");
        return getMany(uri, Crime.class);
    }

    public List<Crime> streetCrimeByPoliceForce(final PoliceForce policeForce) {
        final URI uri = URI.create(String.format("%s/crimes-no-location?category=all-crime&force=%s&date=2019-03", API_BASE, policeForce.getId()));
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

    private <T> T getOne(URI uri, final Class<T> type) {
        final HttpRequest request = get(uri);

        try {
            //HttpResponse.BodyHandlers.ofInputStream() // todo: compare String with InputStream for performance
            final HttpResponse<String> response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response status code: " + response.statusCode());
            log.debug("Response headers: " + response.headers());
            log.debug("Response body: " + response.body());

            final T thing = _mapper.readValue(response.body(), type);
            log.info("found a {} object", type.getSimpleName());
            return thing;
        } catch(final IOException | InterruptedException e) {
            log.error("", e);
        }
        return null;
    }

    private <T> List<T> getMany(final URI uri, final Class<T> type) {
        final HttpRequest request = get(uri);

        try {
            //HttpResponse.BodyHandlers.ofInputStream() // todo: compare String with InputStream for performance
            final HttpResponse<String> response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response status code: " + response.statusCode());
            log.debug("Response headers: " + response.headers());
            log.debug("Response body: " + response.body());

            final CollectionType listType = _mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            final List<T> things = _mapper.readValue(response.body(), listType);

//            final List<T> things = _mapper.readValue(response.body(), new TypeReference<>(){}); // not working
            log.info("found {} {} objects", things.size(), type.getSimpleName());
            return things;
        } catch(final IOException | InterruptedException e) {
            log.error("", e);
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
