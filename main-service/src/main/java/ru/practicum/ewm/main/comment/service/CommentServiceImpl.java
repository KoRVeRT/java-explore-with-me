package ru.practicum.ewm.main.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.dto.CommentMapper;
import ru.practicum.ewm.main.comment.model.Comment;
import ru.practicum.ewm.main.comment.model.CommentSortByAdmin;
import ru.practicum.ewm.main.comment.model.CommentSort;
import ru.practicum.ewm.main.comment.repository.CommentRepository;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private static final String MESSAGE_NOT_FOUND_COMMENT = "CommentId=%d was not found";

    @Override
    public List<CommentDto> getCommentsByAdmin(Integer from, Integer size, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, Set<Long> userIds, Set<Long> eventIds,
                                               String text, CommentSortByAdmin sort) {
        Sort commentSort = sort != null ? sort.getSort() : Sort.unsorted();
        Pageable pageable = getPageable(from, size, commentSort);
        Specification<Comment> spec = Specification
                .where(CommentSpecification.hasEventIdIn(eventIds))
                .and(CommentSpecification.hasUserIdIn(userIds))
                .and(CommentSpecification.hasTextLike(text))
                .and(CommentSpecification.isCreatedAfter(rangeStart))
                .and(CommentSpecification.isCreatedBefore(rangeEnd));
        return commentRepository.findAll(spec, pageable).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        try {
            commentRepository.deleteById(commentId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format(MESSAGE_NOT_FOUND_COMMENT, commentId));
        }
    }

    @Override
    @Transactional
    public CommentDto addCommentByUser(CommentDto commentDto) {
        Long eventId = commentDto.getEventId();
        Event event = findEventById(eventId);
        checkEventPublished(event);
        User user = findUserById(commentDto.getCommentatorId());
        Comment comment = commentMapper.toComment(commentDto, event, user);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto updateCommentByUser(CommentDto commentDto) {
        findEventById(commentDto.getEventId());
        findUserById(commentDto.getCommentatorId());
        Comment comment = findCommentById(commentDto.getId());
        checkValidUserComment(commentDto.getId(), comment);
        comment.setText(commentDto.getText());
        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        findUserById(userId);
        Comment comment = findCommentById(commentId);
        checkValidUserComment(userId, comment);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getUserComments(Integer from, Integer size, Long userId, CommentSort sort) {
        findUserById(userId);
        Sort commentSort = sort != null ? sort.getSort() : Sort.unsorted();
        Pageable pageable = getPageable(from, size, commentSort);
        Specification<Comment> spec = Specification.where(CommentSpecification.hasUserId(userId));
        return commentRepository.findAll(spec, pageable).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getUserCommentById(Long userId, Long commentId) {
        findUserById(userId);
        Comment comment = findCommentById(commentId);
        if (!comment.getCommentator().getId().equals(userId)) {
            throw new EntityNotFoundException(String.format(MESSAGE_NOT_FOUND_COMMENT, commentId));
        }
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getEventComments(Integer from, Integer size, Long eventId, CommentSort sort) {
        findEventById(eventId);
        Sort commentSort = sort != null ? sort.getSort() : Sort.unsorted();
        Pageable pageable = getPageable(from, size, commentSort);
        Specification<Comment> spec = Specification.where(CommentSpecification.hasEventId(eventId));
        return commentRepository.findAll(spec, pageable).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getEventCommentById(Long eventId, Long commentId) {
        findEventById(eventId);
        Comment comment = findCommentById(commentId);
        if (!comment.getEvent().getId().equals(eventId)) {
            throw new EntityNotFoundException(String.format(MESSAGE_NOT_FOUND_COMMENT, commentId));
        }
        return commentMapper.toCommentDto(comment);
    }

    private Pageable getPageable(Integer from, Integer size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }

    private void checkEventPublished(Event event) {
        if (event.getEventState() != EventState.PUBLISHED) {
            throw new ConflictException(String
                    .format("Cannot add comment, because event is not published: %s", event.getEventState()));
        }
    }

    private void checkValidUserComment(Long userId, Comment comment) {
        if (!Objects.equals(userId, comment.getCommentator().getId())) {
            throw new ConflictException(String.format("UserId=%d has no access to change comment, comment owner id=%d",
                    userId, comment.getCommentator().getId()));
        }
    }

    private Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("EventId=%d was not found", id)));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("UserId=%d was not found", id)));
    }

    private Comment findCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MESSAGE_NOT_FOUND_COMMENT, id)));
    }
}