package ru.practicum.ewm.main.comment.dto;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.comment.model.Comment;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public Comment toComment(CommentDto commentDto, Event event, User user) {
        return Comment.builder()
                .text(commentDto.getText())
                .commentator(user)
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .commentatorId(comment.getCommentator().getId())
                .createdOn(comment.getCreatedOn())
                .build();
    }
}