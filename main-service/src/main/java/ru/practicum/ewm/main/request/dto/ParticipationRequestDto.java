package ru.practicum.ewm.main.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.request.model.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    Long id;
    Long event;
    Long count;
    Long requester;
    RequestStatus status;
    LocalDateTime created;

    public ParticipationRequestDto(Long event, Long count, RequestStatus status) {
        this.event = event;
        this.count = count;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ParticipationRequestDto {" +
                "id=" + id +
                ", event=" + event +
                ", count=" + count +
                ", requester=" + requester +
                ", status=" + status +
                ", created=" + created +
                '}';
    }
}