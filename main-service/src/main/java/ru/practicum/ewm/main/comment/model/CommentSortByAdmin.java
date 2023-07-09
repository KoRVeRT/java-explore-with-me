package ru.practicum.ewm.main.comment.model;

import org.springframework.data.domain.Sort;

public enum CommentSortByAdmin {
    SORT_BY_TIME_ASC(Sort.by("createdOn").ascending()),
    SORT_BY_TIME_DESC(Sort.by("createdOn").descending()),
    SORT_BY_USER(Sort.by("commentator.userId")),
    SORT_BY_EVENTS(Sort.by("event.id"));

    private final Sort sort;

    CommentSortByAdmin(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() {
        return sort;
    }
}
