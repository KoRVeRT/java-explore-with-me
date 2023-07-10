package ru.practicum.ewm.main.comment.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentSearchCriteriaByAdmin  {
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Set<Long> userIds;
    Set<Long> eventIds;
    String text;
    CommentSortByAdmin sort;
}
