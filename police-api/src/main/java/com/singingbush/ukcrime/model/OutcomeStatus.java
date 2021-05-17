package com.singingbush.ukcrime.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutcomeStatus {

    @JsonProperty("category")
    private String category;

    @JsonProperty("date")
    private String month;

    public String getCategory() {
        return category;
    }

    public String getMonth() {
        return month;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutcomeStatus that = (OutcomeStatus) o;
        return Objects.equals(category, that.category) &&
            Objects.equals(month, that.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, month);
    }

    @Override
    public String toString() {
        return "OutcomeStatus{" +
            "category='" + category + '\'' +
            ", month='" + month + '\'' +
            '}';
    }
}
