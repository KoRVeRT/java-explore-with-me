package ru.practicum.ewm.main.event.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSearchCriteriaByUserWithRequestInfo extends EventSearchCriteriaByUser {
    String uri;
    String ip;
}