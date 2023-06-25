package ru.practicum.ewm.main.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
     Long id;
     String title;
     Boolean pinned;
     List<EventShortDto> events;
}