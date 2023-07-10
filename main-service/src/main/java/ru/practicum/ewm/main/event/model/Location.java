package ru.practicum.ewm.main.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    @JsonProperty("lat")
    Float latitude;

    @JsonProperty("lon")
    Float longitude;

    public Location(Event event) {
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
    }
}