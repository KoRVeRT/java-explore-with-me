package ru.practicum.ewm.main.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.model.CommentSort;
import ru.practicum.ewm.main.comment.service.CommentService;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.model.EventSort;
import ru.practicum.ewm.main.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByPublic(@RequestParam(required = false) Set<@Positive Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) LocalDateTime rangeStart,
                                                 @RequestParam(required = false) LocalDateTime rangeEnd,
                                                 @RequestParam(required = false) @Size(max = 7000) String text,
                                                 @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(required = false) EventSort sort,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size,
                                                 HttpServletRequest request) {
        List<EventShortDto> events = eventService.searchEventsByUser(from, size, categories, paid, rangeStart, rangeEnd,
                text, sort, onlyAvailable, request);
        log.info("Events list with size={} has been got", events.size());
        return events;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable Long id,
                                     HttpServletRequest httpServletRequest) {
        EventFullDto event = eventService.getEventById(id, httpServletRequest);
        log.info("Event with id={} has been got", id);
        return event;
    }

    @GetMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getEventComments(@PathVariable Long id,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size,
                                             @RequestParam(required = false) CommentSort sort) {
        List<CommentDto> comments = commentService.getEventComments(from, size, id, sort);
        log.info("Comments with size={} has been got", comments.size());
        return comments;
    }

    @GetMapping("/{id}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getEventCommentById(@PathVariable Long id,
                                          @PathVariable Long commentId) {
        CommentDto comment = commentService.getEventCommentById(id, commentId);
        log.info("Comment with fields { " +
                        "id={}, " +
                        "text={}, " +
                        "eventId={}, " +
                        "commentatorId={}," +
                        "created={}" +
                        "} has been got", comment.getId(), comment.getText(), comment.getEventId(),
                comment.getCommentatorId(), comment.getCreatedOn());
        return comment;
    }
}