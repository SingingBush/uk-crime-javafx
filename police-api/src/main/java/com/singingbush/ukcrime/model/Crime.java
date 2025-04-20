package com.singingbush.ukcrime.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Crime {

    @JsonProperty(value = "id", required = true)
    private Long id;

    @JsonProperty(value = "category", required = true)
    private String category;

    @JsonProperty("location_type")
    private String locationType;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("location_subtype")
    private String locationSubtype;

    @JsonProperty("context")
    private String context;

    @Nullable
    @JsonProperty("outcome_status")
    private OutcomeStatus outcomeStatus;

    @JsonProperty("persistent_id")
    private String persistentId;

    @JsonProperty("month")
    private String month;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationSubtype() {
        return locationSubtype;
    }

    public void setLocationSubtype(String locationSubtype) {
        this.locationSubtype = locationSubtype;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public OutcomeStatus getOutcomeStatus() {
        return outcomeStatus;
    }

    public void setOutcomeStatus(OutcomeStatus outcomeStatus) {
        this.outcomeStatus = outcomeStatus;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crime crime = (Crime) o;
        return Objects.equals(id, crime.id) &&
            Objects.equals(category, crime.category) &&
            Objects.equals(locationType, crime.locationType) &&
            Objects.equals(location, crime.location) &&
            Objects.equals(locationSubtype, crime.locationSubtype) &&
            Objects.equals(context, crime.context) &&
            Objects.equals(outcomeStatus, crime.outcomeStatus) &&
            Objects.equals(persistentId, crime.persistentId) &&
            Objects.equals(month, crime.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, locationType, location, locationSubtype, context, outcomeStatus, persistentId, month);
    }

    @Override
    public String toString() {
        return "Crime{" +
            "id=" + id +
            ", category='" + category + '\'' +
            ", locationType='" + locationType + '\'' +
            ", location='" + location + '\'' +
            ", locationSubtype='" + locationSubtype + '\'' +
            ", context='" + context + '\'' +
            ", outcomeStatus='" + outcomeStatus + '\'' +
            ", persistentId='" + persistentId + '\'' +
            ", month='" + month + '\'' +
            '}';
    }
}
