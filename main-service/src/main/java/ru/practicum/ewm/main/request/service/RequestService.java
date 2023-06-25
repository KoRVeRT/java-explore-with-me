package ru.practicum.ewm.main.request.service;

import ru.practicum.ewm.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUser(Long userId);

    List<ParticipationRequestDto> getRequestByEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateParticipationRequest(EventRequestStatusUpdateRequest updateRequest,
                                                              Long userId, Long eventId);

    ParticipationRequestDto cancelRequestById(Long userId, Long requestId);
}