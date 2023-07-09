package ru.practicum.ewm.main.comment.model;

import org.springframework.data.domain.Sort;

public enum CommentSort {
    SORT_BY_TIME_ASC(Sort.by("createdOn").ascending()),
    SORT_BY_TIME_DESC(Sort.by("createdOn").descending());

    private final Sort sort;

    CommentSort(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() {
        return sort;
    }
}