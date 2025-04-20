package com.singingbush.ukcrime.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Neighbourhood {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    // the remaining fields are only retrieved when retrieving a single neighbourhood by id

    @Nullable
    @JsonProperty(value = "description", required = false)
    private String description;

    @JsonProperty("population")
    private String population;

    @JsonProperty("url_force")
    private String urlForce;

    @JsonProperty("centre")
    private Location centre;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(this.name);
    }

    public @Nullable String getDescription() {
        return description;
    }

    public Location getCentre() {
        return centre;
    }
}
