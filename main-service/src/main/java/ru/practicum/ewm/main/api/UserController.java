package ru.practicum.ewm.main.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.model.CommentSort;
import ru.practicum.ewm.main.comment.service.CommentService;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.dto.EventUpdateDto;
import ru.practicum.ewm.main.event.dto.NewEventDto;
import ru.practicum.ewm.main.event.service.EventService;
import ru.practicum.ewm.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final EventService eventService;
    private final RequestService requestService;
    private final CommentService commentService;

    @GetMapping("/{userId}/events")
    @ResponseStatus(OK)
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size) {
        List<EventShortDto> events = eventService.getUserEvents(from, size, userId);
        log.info("User events list with size={} has been got", events.size());
        return events;
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(CREATED)
    public EventFullDto addEventByUser(@PathVariable Long userId,
                                       @Valid @RequestBody NewEventDto newEventDto) {
        EventFullDto eventFullDto = eventService.addEvent(newEventDto, userId);
        log.info("User with id={} added new event", userId);
        log.info(eventFullDto.getAnnotation(), eventFullDto.getTitle(), eventFullDto.getCategory());
        return eventFullDto;
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(OK)
    public EventFullDto getEventByUser(@PathVariable Long userId,
                                       @PathVariable Long eventId) {
        log.info("User event with userId={}, eventId={} has been got", userId, eventId);
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(OK)
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @Valid @RequestBody EventUpdateDto eventUpdateDto) {
        EventFullDto eventFullDto = eventService.updateEventByUser(eventUpdateDto, userId, eventId);
        log.info("User event with userId={}, eventId={} has been update", userId, eventId);
        return eventFullDto;
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUser(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        List<ParticipationRequestDto> requestDto = requestService.getRequestByEvent(userId, eventId);
        log.info("Participation list of size={} has been got", requestDto.size());
        return requestDto;
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(OK)
    public EventRequestStatusUpdateResult updateRequestStatusByUser(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = requestService
                .updateParticipationRequest(eventRequestStatusUpdateRequest, userId, eventId);
        log.info("Participation statuses has been updated");
        return eventRequestStatusUpdateResult;
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(OK)
    public List<ParticipationRequestDto> getParticipationRequestsByUser(@PathVariable Long userId) {
        List<ParticipationRequestDto> participation = requestService.getRequestsByUser(userId);
        log.info("Participation list of size={} has been got", participation.size());
        return participation;
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(CREATED)
    public ParticipationRequestDto addRequestByUser(@PathVariable Long userId,
                                                    @RequestParam Long eventId) {
        ParticipationRequestDto addedParticipation = requestService.addRequest(userId, eventId);
        log.info("Participation has been added {}", addedParticipation);
        return addedParticipation;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(OK)
    public ParticipationRequestDto cancelRequestByUser(@PathVariable Long userId,
                                                       @PathVariable Long requestId) {
        ParticipationRequestDto canceledParticipation = requestService.cancelRequestById(userId, requestId);
        log.info("Participation has been canceled {}", canceledParticipation);
        return canceledParticipation;
    }

    @PostMapping("/{userId}/comments")
    @ResponseStatus(CREATED)
    public CommentDto addCommentEvent(@PathVariable Long userId,
                                      @RequestBody @Valid CommentDto commentDto) {
        commentDto.setCommentatorId(userId);
        CommentDto savedComment = commentService.addCommentByUser(commentDto);
        log.info("Comment has been added: {}", savedComment.toString());
        return savedComment;
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(OK)
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody @Valid CommentDto commentDto) {
        commentDto.setCommentatorId(userId);
        commentDto.setId(commentId);
        CommentDto updatedComment = commentService.updateCommentByUser(commentDto);
        log.info("Comment has been updated: {}", updatedComment.toString());
        return updatedComment;
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
        log.info("User with id={} deleted comment with id={}", userId, commentId);
    }

    @GetMapping("/{userId}/comments")
    @ResponseStatus(OK)
    public List<CommentDto> getUserComments(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) CommentSort sort) {
        Pageable pageable = PageRequest.of(from, size);
        List<CommentDto> comments = commentService.getUserComments(pageable, userId, sort);
        log.info("Comments with size={} has been got", comments.size());
        return comments;
    }

    @GetMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(OK)
    public CommentDto getUserCommentById(@PathVariable Long userId,
                                         @PathVariable Long commentId) {
        CommentDto comment = commentService.getUserCommentById(userId, commentId);
        log.info("Comment has been got: {}", comment.toString());
        return comment;
    }
}