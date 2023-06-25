package ru.practicum.ewm.main.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.dto.RequestMapper;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;
import ru.practicum.ewm.main.request.repository.RequestRepository;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User requester = getUser(userId);
        Event event = getEvent(eventId);
        checkParticipationRequest(requester, event);
        Request request = createParticipationRequest(requester, event);
        Request savedRequest = requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        getUser(userId);
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getRequestByEvent(Long userId, Long eventId) {
        getUser(userId);
        getEvent(eventId);
        return requestRepository.findAllByEventInitiatorIdAndEventId(userId, eventId)
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateParticipationRequest(EventRequestStatusUpdateRequest updateRequest,
                                                                     Long userId, Long eventId) {
        getUser(userId);
        Event event = getEvent(eventId);
        List<Request> requests = updateParticipationRequest(event, updateRequest);
        requestRepository.saveAll(requests);
        return requestMapper.toEventRequestStatusUpdateResult(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequestById(Long userId, Long requestId) {
        getUser(userId);
        Request request = getParticipationRequestById(requestId);
        request.setRequestStatus(RequestStatus.CANCELED);
        Request canceledRequest = requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(canceledRequest);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(()
                        -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private Request getParticipationRequestById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(()
                        -> new EntityNotFoundException(String.format("Request with id=%d was not found", requestId)));
    }

    private List<Request> updateParticipationRequest(Event event,
                                                     EventRequestStatusUpdateRequest updateRequest) {
        RequestStatus status = updateRequest.getStatus();
        int limit = event.getParticipantLimit();
        long confirmedRequests = requestRepository.getEventConfirmedRequestsCount(event.getId(), RequestStatus.CONFIRMED);
        checkParticipationLimit(confirmedRequests, event.getParticipantLimit());
        List<Request> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        setParticipationStatusToConfirmed(event, status, limit, confirmedRequests, requests);
        setParticipationRequestStatusToRejected(status, requests);
        return requests;
    }

    private void setParticipationStatusToConfirmed(Event event, RequestStatus status, int limit, long confirmedRequests,
                                                   List<Request> requests) {
        if (status == RequestStatus.CONFIRMED) {
            AtomicLong confirmedCounter = new AtomicLong(confirmedRequests);
            requests.forEach(request -> processRequest(event, limit, confirmedCounter, request));
        }
    }

    private void processRequest(Event event, int limit, AtomicLong confirmedCounter, Request request) {
        checkParticipationRequestStatusIsPending(request);
        if (event.getParticipantLimit() == 0 || limit - confirmedCounter.get() > 0) {
            request.setRequestStatus(RequestStatus.CONFIRMED);
            confirmedCounter.incrementAndGet();
        } else {
            request.setRequestStatus(RequestStatus.REJECTED);
        }
    }

    private void setParticipationRequestStatusToRejected(RequestStatus status, List<Request> requests) {
        if (status == RequestStatus.REJECTED) {
            for (Request request : requests) {
                checkParticipationRequestStatusIsPending(request);
                request.setRequestStatus(RequestStatus.REJECTED);
            }
        }
    }

    private void checkParticipationRequestStatusIsPending(Request request) {
        if (request.getRequestStatus() != RequestStatus.PENDING) {
            throw new ConflictException("Request must have PENDING status");
        }
    }

    private Request createParticipationRequest(User requester, Event event) {
        Long eventId = event.getId();
        Long userId = requester.getId();
        checkParticipationRequestExists(eventId, userId);
        Request request = Request.builder()
                .requester(requester)
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();
        if (Boolean.TRUE.equals(!event.getRequestModeration()) || event.getParticipantLimit() == 0) {
            request.setRequestStatus(RequestStatus.CONFIRMED);
        } else {
            request.setRequestStatus(RequestStatus.PENDING);
        }

        return request;
    }

    private void checkParticipationRequest(User requester, Event event) {
        Long eventId = event.getId();
        Long confirmedRequests = requestRepository.getEventConfirmedRequestsCount(eventId, RequestStatus.CONFIRMED);
        Integer participationLimit = event.getParticipantLimit();
        checkEventToPublished(event.getEventState());
        checkRequesterToParticipation(requester, event);
        checkParticipationLimit(confirmedRequests, participationLimit);
    }

    private void checkParticipationLimit(Long confirmedRequests, Integer participationLimit) {
        if (participationLimit == 0) {
            return;
        }
        int newParticipationRequest = 1;
        long requests = confirmedRequests + newParticipationRequest;
        if (requests > participationLimit) {
            throw new ConflictException("Event has reached limit of applications for participation");
        }
    }

    private void checkRequesterToParticipation(User requester, Event event) {
        Long userId = requester.getId();
        Long initiatorId = event.getInitiator().getId();
        if (userId.equals(initiatorId)) {
            throw new ConflictException("Initiator of event cannot add request to its event");
        }
    }

    private void checkParticipationRequestExists(Long eventId, Long userId) {
        if (Boolean.TRUE.equals(requestRepository.existsByEventIdAndRequesterId(eventId, userId))) {
            throw new ConflictException("Request already exists");
        }
    }

    private void checkEventToPublished(EventState eventState) {
        if (!eventState.equals(EventState.PUBLISHED)) {
            throw new ConflictException("Participation in unpublished event is prohibited");
        }
    }
}