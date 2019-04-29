package com.singingbush.ukcrime.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeniorOfficer {

    @JsonProperty("name")
    private String name;

    @JsonProperty("rank")
    private String rank;

    @JsonProperty("contact_details")
    private Map<String,String> contactDetails;

    @JsonProperty("bio")
    private String biography; // (if available)

    public String getName() {
        return name;
    }

    public String getRank() {
        return rank;
    }

    public Map<String, String> getContactDetails() {
        return contactDetails;
    }

    public String getBiography() {
        return biography;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeniorOfficer that = (SeniorOfficer) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(rank, that.rank) &&
            Objects.equals(contactDetails, that.contactDetails) &&
            Objects.equals(biography, that.biography);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rank, contactDetails, biography);
    }

    @Override
    public String toString() {
        return "SeniorOfficer{" +
            "name='" + name + '\'' +
            ", rank='" + rank + '\'' +
            ", contactDetails=" + contactDetails +
            ", biography='" + biography + '\'' +
            '}';
    }
}
