package ru.practicum.ewm.main.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getRequestStatus())
                .created(request.getCreatedOn())
                .build();
    }

    public EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(List<Request> requests) {
        Map<Boolean, List<ParticipationRequestDto>> requestStatusMap = requests.stream()
                .map(this::toParticipationRequestDto)
                .collect(Collectors.partitioningBy(dto -> dto.getStatus() == RequestStatus.CONFIRMED));
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requestStatusMap.get(true))
                .rejectedRequests(requestStatusMap.get(false))
                .build();
    }
}