package com.singingbush.ukcrime.services;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.singingbush.ukcrime.model.Crime;
import com.singingbush.ukcrime.model.Neighbourhood;
import com.singingbush.ukcrime.model.PoliceForce;
import com.singingbush.ukcrime.model.SeniorOfficer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UkPoliceApiTest {

    private static final Logger log = LoggerFactory.getLogger(UkPoliceApiTest.class);

    private static final String APPLICATION_JSON_VALUE = "application/json";

    private UkPoliceApi api;

    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
        .options(wireMockConfig()
            .dynamicPort()
        )
        .build();

    @BeforeEach
    void setUp() {
        final String baseUri = wiremock.baseUrl();
        log.info("Wiremock configured with base url: {}", baseUri);
        this.api = new UkPoliceApi(baseUri);
    }

    @AfterEach
    void tearDown() {
        wiremock.resetAll();
    }

    @Test
    void getAllPoliceForces() {
        wiremock.stubFor(get(urlPathEqualTo("/forces"))
            .withHeader("Accept", equalTo(APPLICATION_JSON_VALUE))
            .willReturn(
                ok("[]")
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
            ));

        final List<PoliceForce> policeForces = this.api.getAllPoliceForces();

        assertEquals(0, policeForces.size());
    }

    @Test
    void getPoliceForceById() {
        wiremock.stubFor(get(urlPathEqualTo("/forces/FORCE-ID"))
            .withHeader("Accept", equalTo(APPLICATION_JSON_VALUE))
            .willReturn(
                ok("""
                    {
                      "id": "FORCE-ID",
                      "name": "Test Force Name"
                    }
                    """)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
            ));

        final PoliceForce force = this.api.getPoliceForceById("FORCE-ID");

        assertNotNull(force);
        assertEquals("FORCE-ID", force.getId());
        assertEquals("Test Force Name", force.getName());
    }

    @Test
    void getLastUpdatedDate() {
        wiremock.stubFor(get(urlPathEqualTo("/crime-last-updated"))
            .withHeader("Accept", equalTo(APPLICATION_JSON_VALUE))
            .willReturn(
                ok("""
                    {
                      "date": "2025-12-01"
                    }
                    """)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
            ));

        final LocalDate lastUpdatedDate = this.api.getLastUpdatedDate();

        assertEquals(LocalDate.of(2025, 12, 1), lastUpdatedDate);
    }

    @Test
    void getPoliceForceSeniorOfficers() {
        wiremock.stubFor(get(urlPathEqualTo("/forces/FORCE-ID/people"))
            .withHeader("Accept", equalTo(APPLICATION_JSON_VALUE))
            .willReturn(
                ok("""
                    [
                        {
                          "name": "Bobby",
                          "rank": "Flat Foot"
                        }
                    ]
                    """)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
            ));

        final PoliceForce mockForce = mock(PoliceForce.class);
        when(mockForce.getId()).thenReturn("FORCE-ID");
        final List<SeniorOfficer> seniorOfficers = this.api.getPoliceForceSeniorOfficers(mockForce);

        assertEquals(1, seniorOfficers.size());
    }

    @Test
    void getPoliceForceNeighbourhoods() {
        wiremock.stubFor(get(urlPathEqualTo("/FORCE-ID/neighbourhoods"))
            .withHeader("Accept", equalTo(APPLICATION_JSON_VALUE))
            .willReturn(
                ok("""
                    [
                      {
                        "id": "FORCE-ID",
                        "name": "Test Force Name",
                        "description": "blah blah blah",
                        "centre": {
                          "latitude": "12345",
                          "longitude": "12345"
                        }
                      }
                    ]
                    """)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
            ));

        final PoliceForce mockForce = mock(PoliceForce.class);
        when(mockForce.getId()).thenReturn("FORCE-ID");

        final List<Neighbourhood> policeForceNeighbourhoods = this.api.getPoliceForceNeighbourhoods(mockForce);

        assertNotNull(policeForceNeighbourhoods);
    }

    @Test
    void getPoliceForceNeighbourhood() {
        wiremock.stubFor(get(urlPathEqualTo("/FORCE-ID/suburb"))
            .withHeader("Accept", equalTo(APPLICATION_JSON_VALUE))
            .willReturn(
                ok("""
                    {
                      "id": "FORCE-ID",
                      "name": "Test Force Name",
                      "description": "blah blah blah",
                      "centre": {
                        "latitude": "12345",
                        "longitude": "12345"
                      }
                    }
                    """)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
            ));

        final PoliceForce mockForce = mock(PoliceForce.class);
        when(mockForce.getId()).thenReturn("FORCE-ID");

        final Neighbourhood neighbourhood = this.api.getPoliceForceNeighbourhood(mockForce, "suburb");

        assertNotNull(neighbourhood);
    }

    @Test
    void streetCrimeByLocation() {
        wiremock.stubFor(get(urlPathEqualTo("/crimes-street/all-crime"))
            .withQueryParam("lat", new EqualToPattern("111"))
            .withQueryParam("lng", new EqualToPattern("222"))
            .withHeader("Accept", equalTo(APPLICATION_JSON_VALUE))
            .willReturn(
                ok("""
                    [
                      {
                        "id": 100000,
                        "category": "Something Bad",
                        "location": {
                          "latitude": "12345",
                          "longitude": "12345"
                        }
                      }
                    ]
                    """)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
            ));

        final List<Crime> crimes = this.api.streetCrimeByLocation("111", "222");

        assertNotNull(crimes);
        assertEquals(1, crimes.size());
    }

    @Test
    void streetCrimeByPoliceForce() {
        wiremock.stubFor(get(urlPathEqualTo("/crimes-no-location"))
            .withQueryParam("category", new EqualToPattern("all-crime"))
            .withQueryParam("force", new EqualToPattern("FORCE-ID"))
            .withQueryParam("date", new RegexPattern("[0-9]{4}-[0-9]{2}"))
            .withHeader("Accept", equalTo(APPLICATION_JSON_VALUE))
            .willReturn(
                ok("""
                    [
                      {
                        "id": 100000,
                        "category": "Something Bad",
                        "location": {
                          "latitude": "12345",
                          "longitude": "12345"
                        }
                      }
                    ]
                    """)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
            ));

        final PoliceForce mockForce = mock(PoliceForce.class);
        when(mockForce.getId()).thenReturn("FORCE-ID");

        final List<Crime> crimes = this.api.streetCrimeByPoliceForce(mockForce);

        assertNotNull(crimes);
        assertEquals(1, crimes.size());
    }
}
