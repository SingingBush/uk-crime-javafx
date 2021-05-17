package com.singingbush.ukcrime.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PoliceForce {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("telephone")
    private String telephone; // only populated when getting single object

    @JsonProperty("url")
    private String url; // only populated when getting single object

    @JsonProperty("description")
    private String description; // only populated when getting single object

//    @JsonProperty("engagement_methods")
//    private List<EngagementMethod> engagementMethods; // todo: (social media, eg facebook [url, description, title])

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(this.name);
    }

    public String getTelephone() {
        return telephone;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoliceForce that = (PoliceForce) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "PoliceForce{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
