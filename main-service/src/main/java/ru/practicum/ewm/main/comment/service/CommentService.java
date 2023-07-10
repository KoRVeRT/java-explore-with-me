package ru.practicum.ewm.main.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.model.CommentSearchCriteriaByAdmin;
import ru.practicum.ewm.main.comment.model.CommentSort;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByAdmin(Pageable pageable, CommentSearchCriteriaByAdmin criteria);

    void deleteCommentByAdmin(Long commentId);

    CommentDto addCommentByUser(CommentDto commentDto);

    CommentDto updateCommentByUser(CommentDto commentDto);

    void deleteCommentByUser(Long userId, Long commentId);

    List<CommentDto> getUserComments(Pageable pageable, Long userId, CommentSort commentSort);

    CommentDto getUserCommentById(Long userId, Long commentId);

    List<CommentDto> getEventComments(Pageable pageable, Long eventId, CommentSort sort);

    CommentDto getEventCommentById(Long eventId, Long commentId);
}