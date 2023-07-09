package ru.practicum.ewm.main.event.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSearchCriteriaByUser {
    Set<@Positive Long> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    @Size(max = 7000) String text;
    Boolean onlyAvailable;
    EventSort sort;
}