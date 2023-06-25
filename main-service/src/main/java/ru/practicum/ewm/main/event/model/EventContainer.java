package ru.practicum.ewm.main.event.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventContainer {
    List<Event> events;
    Set<Long> eventsIds;
    Map<Long, Long> eventsViews;
    Map<Long, Long> confirmedRequests;
}