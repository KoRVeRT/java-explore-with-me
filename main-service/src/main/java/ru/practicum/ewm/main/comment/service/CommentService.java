package ru.practicum.ewm.main.comment.service;

import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.model.CommentSortByAdmin;
import ru.practicum.ewm.main.comment.model.CommentSort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface CommentService {
    List<CommentDto> getCommentsByAdmin(Integer from, Integer size, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    Set<Long> userIds, Set<Long> eventIds, String text, CommentSortByAdmin sort);

    void deleteCommentByAdmin(Long commentId);

    CommentDto addCommentByUser(CommentDto commentDto);

    CommentDto updateCommentByUser(CommentDto commentDto);

    void deleteCommentByUser(Long userId, Long commentId);

    List<CommentDto> getUserComments(Integer from, Integer size, Long userId, CommentSort commentSort);

    CommentDto getUserCommentById(Long userId, Long commentId);

    List<CommentDto> getEventComments(Integer from, Integer size, Long eventId, CommentSort sort);

    CommentDto getEventCommentById(Long eventId, Long commentId);
}